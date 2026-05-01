package com.obf.passes;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameObfuscationPass implements Pass {
    public enum Mode {
        DEFAULT,
        AGGRESSIVE
    }

    private static final Set<String> TEXT_METADATA_FILES = Set.of(
        "fabric.mod.json",
        "quilt.mod.json",
        "META-INF/MANIFEST.MF"
    );
    private static final Pattern MIXIN_PACKAGE_PATTERN =
        Pattern.compile("\"package\"\\s*:\\s*\"([^\"]+)\"");

    // ASCII-only confusables that still make decompiled output painful to read.
    private static final char[] CHARS = {
        'I', 'l', 'i', 'L'
    };
    private static final int MIN_NAME_LENGTH = 24;
    private final Mode mode;

    private int classCounter = 0;
    private int methodCounter = 0;
    private int fieldCounter = 0;
    private final Set<String> preservedClasses = new LinkedHashSet<>();
    private final Map<String, String> classMappings = new LinkedHashMap<>();
    private final Map<String, String> methodMappings = new LinkedHashMap<>();
    private final Map<String, String> fieldMappings = new LinkedHashMap<>();

    public NameObfuscationPass() {
        this(Mode.DEFAULT);
    }

    public NameObfuscationPass(Mode mode) {
        this.mode = mode;
    }

    public void preserveMetadataClasses(Map<String, byte[]> resources, Collection<String> classNames) {
        Map<String, String> dottedToInternal = new LinkedHashMap<>();
        for (String className : classNames) {
            dottedToInternal.put(className.replace('/', '.'), className);
        }

        for (Map.Entry<String, byte[]> entry : resources.entrySet()) {
            if (!isMetadataResource(entry.getKey())) {
                continue;
            }

            String resourceName = entry.getKey();
            String text = new String(entry.getValue(), StandardCharsets.UTF_8);
            if (resourceName.endsWith(".mixins.json")) {
                preserveDirectMentions(text, dottedToInternal);
                preserveInternalMentions(text, classNames);
                preserveMixinPackageClasses(text, classNames);
            }
        }
    }

    public Map<String, String> getClassMappings() {
        return Collections.unmodifiableMap(classMappings);
    }

    public void rewriteMetadataResources(Map<String, byte[]> resources) {
        if (classMappings.isEmpty()) {
            return;
        }

        List<Map.Entry<String, String>> mappings = classMappings.entrySet().stream()
            .filter(entry -> !entry.getKey().equals(entry.getValue()))
            .sorted((left, right) -> Integer.compare(right.getKey().length(), left.getKey().length()))
            .toList();

        for (Map.Entry<String, byte[]> entry : new ArrayList<>(resources.entrySet())) {
            if (!isMetadataResource(entry.getKey())) {
                continue;
            }

            String text = new String(entry.getValue(), StandardCharsets.UTF_8);
            for (Map.Entry<String, String> mapping : mappings) {
                String oldInternal = mapping.getKey();
                String newInternal = mapping.getValue();
                text = text.replace(oldInternal, newInternal);
                text = text.replace(oldInternal.replace('/', '.'), newInternal.replace('/', '.'));
            }
            resources.put(entry.getKey(), text.getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public void apply(Map<String, ClassNode> classes) {
        Map<String, String> mapping = new HashMap<>();
        classMappings.clear();
        methodMappings.clear();
        fieldMappings.clear();
        preserveNativeClasses(classes.values());
        preserveKotlinMetadataClasses(classes.values(), classes.keySet());

        // Build mappings. Kotlin-heavy / reflective / JNI-sensitive classes keep
        // their members, while plain Java implementation details get stronger
        // member renaming.

        for (ClassNode cn : classes.values()) {
            if (preservedClasses.contains(cn.name)) {
                classMappings.put(cn.name, cn.name);
            } else {
                String newClass = remapClassName(cn.name);
                mapping.put(cn.name, newClass);
                classMappings.put(cn.name, newClass);
            }

            buildMemberMappings(cn);
        }

        mapping.putAll(methodMappings);
        mapping.putAll(fieldMappings);

        SimpleRemapper remapper = new SimpleRemapper(mapping);
        Map<String, ClassNode> remapped = new LinkedHashMap<>();

        for (ClassNode cn : classes.values()) {
            ClassNode dest = new ClassNode();
            cn.accept(new ClassRemapper(dest, remapper));
            remapped.put(dest.name, dest);
        }

        classes.clear();
        classes.putAll(remapped);
    }

    private String nextClassName() {
        return nextName(classCounter++);
    }

    private String nextMethodName() {
        return nextName(methodCounter++);
    }

    private String nextFieldName() {
        return nextName(fieldCounter++);
    }

    private String nextName(int value) {
        int n = value;
        StringBuilder sb = new StringBuilder();
        do {
            sb.insert(0, CHARS[n % CHARS.length]);
            n = n / CHARS.length - 1;
        } while (n >= 0);
        while (sb.length() < MIN_NAME_LENGTH) {
            sb.insert(0, CHARS[(value + sb.length()) % CHARS.length]);
        }
        return sb.toString();
    }

    private String remapClassName(String originalName) {
        int slash = originalName.lastIndexOf('/');
        String packagePrefix = slash >= 0 ? originalName.substring(0, slash + 1) : "";
        return packagePrefix + nextClassName();
    }

    private boolean isMetadataResource(String resourceName) {
        return TEXT_METADATA_FILES.contains(resourceName)
            || resourceName.endsWith(".mixins.json")
            || resourceName.endsWith(".accesswidener");
    }

    private void preserveNativeClasses(Collection<ClassNode> classes) {
        for (ClassNode cn : classes) {
            for (MethodNode mn : cn.methods) {
                if ((mn.access & Opcodes.ACC_NATIVE) != 0) {
                    preservedClasses.add(cn.name);
                    break;
                }
            }
        }
    }

    private void buildMemberMappings(ClassNode cn) {
        boolean preserveMembers = preservedClasses.contains(cn.name) || hasKotlinMetadata(cn) || isAnnotationClass(cn);

        for (FieldNode fn : cn.fields) {
            if (!shouldRenameField(cn, fn, preserveMembers)) {
                continue;
            }
            fieldMappings.put(cn.name + '.' + fn.name, nextFieldName());
        }

        for (MethodNode mn : cn.methods) {
            if (!shouldRenameMethod(cn, mn, preserveMembers)) {
                continue;
            }
            methodMappings.put(cn.name + '.' + mn.name + mn.desc, nextMethodName());
        }
    }

    private void preserveKotlinMetadataClasses(Collection<ClassNode> classes, Collection<String> classNames) {
        Map<String, String> dottedToInternal = new LinkedHashMap<>();
        for (String className : classNames) {
            dottedToInternal.put(className.replace('/', '.'), className);
        }

        for (ClassNode cn : classes) {
            inspectMetadataAnnotations(cn.visibleAnnotations, dottedToInternal, classNames);
            inspectMetadataAnnotations(cn.invisibleAnnotations, dottedToInternal, classNames);
        }
    }

    private boolean hasKotlinMetadata(ClassNode cn) {
        return hasMetadataAnnotation(cn.visibleAnnotations) || hasMetadataAnnotation(cn.invisibleAnnotations);
    }

    private boolean hasMetadataAnnotation(List<AnnotationNode> annotations) {
        if (annotations == null) {
            return false;
        }
        for (AnnotationNode annotation : annotations) {
            if ("Lkotlin/Metadata;".equals(annotation.desc)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAnnotationClass(ClassNode cn) {
        return (cn.access & Opcodes.ACC_ANNOTATION) != 0;
    }

    private boolean shouldRenameField(ClassNode cn, FieldNode fn, boolean preserveMembers) {
        if (preserveMembers) {
            return false;
        }
        if ((cn.access & Opcodes.ACC_ENUM) != 0) {
            return false;
        }
        if ("serialVersionUID".equals(fn.name)) {
            return false;
        }
        if (mode == Mode.AGGRESSIVE) {
            return (fn.access & Opcodes.ACC_ENUM) == 0;
        }
        return (fn.access & Opcodes.ACC_PUBLIC) == 0;
    }

    private boolean shouldRenameMethod(ClassNode cn, MethodNode mn, boolean preserveMembers) {
        if (preserveMembers) {
            return false;
        }
        if ((mn.access & Opcodes.ACC_NATIVE) != 0) {
            return false;
        }
        if ((mn.access & (Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE)) != 0) {
            return false;
        }
        if ("<init>".equals(mn.name) || "<clinit>".equals(mn.name) || "main".equals(mn.name)) {
            return false;
        }
        if ((cn.access & Opcodes.ACC_ANNOTATION) != 0) {
            return false;
        }
        if ((cn.access & Opcodes.ACC_ENUM) != 0
            && ("values".equals(mn.name) || "valueOf".equals(mn.name))) {
            return false;
        }
        if (mode == Mode.AGGRESSIVE) {
            return (mn.access & (Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC)) != 0;
        }
        return (mn.access & (Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC)) != 0;
    }

    private void inspectMetadataAnnotations(
        List<AnnotationNode> annotations,
        Map<String, String> dottedToInternal,
        Collection<String> classNames
    ) {
        if (annotations == null) {
            return;
        }

        for (AnnotationNode annotation : annotations) {
            if (!"Lkotlin/Metadata;".equals(annotation.desc) || annotation.values == null) {
                continue;
            }
            for (Object value : annotation.values) {
                inspectMetadataValue(value, dottedToInternal, classNames);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void inspectMetadataValue(
        Object value,
        Map<String, String> dottedToInternal,
        Collection<String> classNames
    ) {
        if (value instanceof String text) {
            preserveDirectMentions(text, dottedToInternal);
            preserveInternalMentions(text, classNames);
            return;
        }

        if (value instanceof AnnotationNode annotation) {
            if (annotation.values == null) {
                return;
            }
            for (Object nested : annotation.values) {
                inspectMetadataValue(nested, dottedToInternal, classNames);
            }
            return;
        }

        if (value instanceof List<?> list) {
            for (Object nested : new ArrayList<>(list)) {
                inspectMetadataValue(nested, dottedToInternal, classNames);
            }
        }
    }

    private void preserveDirectMentions(String text, Map<String, String> dottedToInternal) {
        for (Map.Entry<String, String> entry : dottedToInternal.entrySet()) {
            if (text.contains(entry.getKey())) {
                preservedClasses.add(entry.getValue());
            }
        }
    }

    private void preserveInternalMentions(String text, Collection<String> classNames) {
        for (String className : classNames) {
            if (text.contains(className)) {
                preservedClasses.add(className);
            }
        }
    }

    private void preserveMixinPackageClasses(String text, Collection<String> classNames) {
        Matcher matcher = MIXIN_PACKAGE_PATTERN.matcher(text);
        while (matcher.find()) {
            String packagePrefix = matcher.group(1).replace('.', '/') + "/";
            for (String className : classNames) {
                if (className.startsWith(packagePrefix)) {
                    preservedClasses.add(className);
                }
            }
        }
    }
}
