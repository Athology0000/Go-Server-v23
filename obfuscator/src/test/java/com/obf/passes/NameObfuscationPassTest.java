package com.obf.passes;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class NameObfuscationPassTest {
    @Test
    void classNameIsMangled() {
        ClassNode cn = new ClassNode();
        cn.name = "com/example/MyClass";
        cn.superName = "java/lang/Object";
        cn.interfaces = new ArrayList<>();
        cn.methods = new ArrayList<>();
        cn.fields = new ArrayList<>();
        cn.access = Opcodes.ACC_PUBLIC;

        Map<String, ClassNode> classes = new HashMap<>();
        classes.put(cn.name, cn);

        new NameObfuscationPass().apply(classes);

        // The original name key is gone
        assertFalse(classes.containsKey("com/example/MyClass"));
        // Only one class remains
        assertEquals(1, classes.size());
        ClassNode result = classes.values().iterator().next();
        // Name is not the original
        assertNotEquals("com/example/MyClass", result.name);
    }

    @Test
    void methodNameIsPreservedForCompatibility() {
        ClassNode cn = new ClassNode();
        cn.name = "com/example/Foo";
        cn.superName = "java/lang/Object";
        cn.interfaces = new ArrayList<>();
        cn.access = Opcodes.ACC_PUBLIC;
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC, "myMethod", "()V", null, null);
        mn.instructions = new InsnList();
        mn.instructions.add(new InsnNode(Opcodes.RETURN));
        cn.methods = List.of(mn);
        cn.fields = new ArrayList<>();

        Map<String, ClassNode> classes = new HashMap<>();
        classes.put(cn.name, cn);

        new NameObfuscationPass().apply(classes);

        ClassNode result = classes.values().iterator().next();
        boolean methodPreserved = result.methods.stream()
            .filter(m -> !m.name.equals("<init>") && !m.name.equals("<clinit>") && !m.name.equals("main"))
            .anyMatch(m -> m.name.equals("myMethod"));
        assertTrue(methodPreserved);
    }

    @Test
    void fabricEntrypointClassIsRewrittenInMetadata() {
        ClassNode cn = new ClassNode();
        cn.name = "com/example/ModInit";
        cn.superName = "java/lang/Object";
        cn.interfaces = new ArrayList<>();
        cn.access = Opcodes.ACC_PUBLIC;
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC, "onInitialize", "()V", null, null);
        mn.instructions = new InsnList();
        mn.instructions.add(new InsnNode(Opcodes.RETURN));
        cn.methods = List.of(mn);
        cn.fields = new ArrayList<>();

        Map<String, ClassNode> classes = new HashMap<>();
        classes.put(cn.name, cn);

        Map<String, byte[]> resources = new HashMap<>();
        resources.put(
            "fabric.mod.json",
            "{\"entrypoints\":{\"main\":[\"com.example.ModInit\"]}}".getBytes(StandardCharsets.UTF_8)
        );

        NameObfuscationPass pass = new NameObfuscationPass();
        pass.preserveMetadataClasses(resources, classes.keySet());
        pass.apply(classes);
        pass.rewriteMetadataResources(resources);

        assertFalse(classes.containsKey("com/example/ModInit"));
        assertEquals(1, classes.size());
        ClassNode result = classes.values().iterator().next();
        assertNotEquals("com/example/ModInit", result.name);
        assertTrue(result.methods.stream().anyMatch(m -> m.name.equals("onInitialize")));
        String metadata = new String(resources.get("fabric.mod.json"), StandardCharsets.UTF_8);
        assertFalse(metadata.contains("com.example.ModInit"));
        assertTrue(metadata.contains(result.name.replace('/', '.')));
    }

    @Test
    void nativeClassIsPreserved() {
        ClassNode cn = new ClassNode();
        cn.name = "org/example/NativeBridge";
        cn.superName = "java/lang/Object";
        cn.interfaces = new ArrayList<>();
        cn.access = Opcodes.ACC_PUBLIC;
        MethodNode nativeMethod = new MethodNode(
            Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_NATIVE,
            "createEngine",
            "()J",
            null,
            null
        );
        cn.methods = List.of(nativeMethod);
        cn.fields = new ArrayList<>();

        Map<String, ClassNode> classes = new HashMap<>();
        classes.put(cn.name, cn);

        new NameObfuscationPass().apply(classes);

        assertTrue(classes.containsKey("org/example/NativeBridge"));
        ClassNode result = classes.get("org/example/NativeBridge");
        assertNotNull(result);
        assertTrue(result.methods.stream().anyMatch(m -> m.name.equals("createEngine")));
    }

    @Test
    void kotlinMetadataReferencedClassIsPreserved() {
        ClassNode base = new ClassNode();
        base.name = "org/example/Command";
        base.superName = "java/lang/Object";
        base.interfaces = new ArrayList<>();
        base.methods = new ArrayList<>();
        base.fields = new ArrayList<>();
        base.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT;

        ClassNode derived = new ClassNode();
        derived.name = "org/example/MainCommand";
        derived.superName = "org/example/Command";
        derived.interfaces = new ArrayList<>();
        derived.methods = new ArrayList<>();
        derived.fields = new ArrayList<>();
        derived.access = Opcodes.ACC_PUBLIC;

        AnnotationNode metadata = new AnnotationNode("Lkotlin/Metadata;");
        metadata.values = new ArrayList<>();
        metadata.values.add("d2");
        metadata.values.add(new ArrayList<>(List.of(
            "Lorg/example/MainCommand;",
            "Lorg/example/Command;"
        )));
        derived.visibleAnnotations = new ArrayList<>(List.of(metadata));

        Map<String, ClassNode> classes = new HashMap<>();
        classes.put(base.name, base);
        classes.put(derived.name, derived);

        new NameObfuscationPass().apply(classes);

        assertTrue(classes.containsKey("org/example/Command"));
    }

    @Test
    void privateMembersAreMangledForInternalJavaClass() {
        ClassNode cn = new ClassNode();
        cn.name = "com/example/InternalWorker";
        cn.superName = "java/lang/Object";
        cn.interfaces = new ArrayList<>();
        cn.access = Opcodes.ACC_FINAL;

        FieldNode field = new FieldNode(Opcodes.ACC_PRIVATE, "secretValue", "I", null, null);
        cn.fields = new ArrayList<>(List.of(field));

        MethodNode method = new MethodNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
            "computeSecret", "()V", null, null);
        method.instructions = new InsnList();
        method.instructions.add(new InsnNode(Opcodes.RETURN));
        cn.methods = new ArrayList<>(List.of(method));

        Map<String, ClassNode> classes = new HashMap<>();
        classes.put(cn.name, cn);

        new NameObfuscationPass().apply(classes);

        ClassNode result = classes.values().iterator().next();
        assertTrue(result.fields.stream().noneMatch(f -> f.name.equals("secretValue")));
        assertTrue(result.methods.stream().noneMatch(m -> m.name.equals("computeSecret")));
    }

    @Test
    void aggressiveModeRenamesPublicFieldsForInternalJavaClass() {
        ClassNode cn = new ClassNode();
        cn.name = "com/example/PublicFieldHolder";
        cn.superName = "java/lang/Object";
        cn.interfaces = new ArrayList<>();
        cn.access = Opcodes.ACC_FINAL;

        FieldNode field = new FieldNode(Opcodes.ACC_PUBLIC, "visibleValue", "I", null, null);
        cn.fields = new ArrayList<>(List.of(field));
        cn.methods = new ArrayList<>();

        Map<String, ClassNode> classes = new HashMap<>();
        classes.put(cn.name, cn);

        new NameObfuscationPass(NameObfuscationPass.Mode.AGGRESSIVE).apply(classes);

        ClassNode result = classes.values().iterator().next();
        assertTrue(result.fields.stream().noneMatch(f -> f.name.equals("visibleValue")));
    }
}
