package com.obf.passes;

import com.obf.runtime.NativeLibraryLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import java.util.*;

/**
 * Must run after NativeLiftingPass. Replaces lifted method bodies with
 * a native declaration and injects System.loadLibrary into <clinit>.
 */
public class StubReplacementPass implements Pass {
    private static final String LOADER_INTERNAL_NAME = "com/obf/runtime/NativeLibraryLoader";
    private final NativeLiftingPass liftingPass;
    private final String libraryFileName;

    public StubReplacementPass(NativeLiftingPass liftingPass, String libraryFileName) {
        this.liftingPass = liftingPass;
        this.libraryFileName = libraryFileName;
    }

    @Override
    public void apply(Map<String, ClassNode> classes) {
        ensureLoaderClassPresent(classes);

        for (ClassNode cn : classes.values()) {
            Set<String> lifted = liftingPass.liftedMethods.getOrDefault(cn.name, Set.of());
            if (lifted.isEmpty()) continue;

            boolean needsLoader = false;
            for (MethodNode mn : cn.methods) {
                if (lifted.contains(mn.name + mn.desc)) {
                    // Clear method body, mark as native
                    mn.instructions.clear();
                    mn.tryCatchBlocks = new ArrayList<>();
                    mn.localVariables = new ArrayList<>();
                    mn.access |= Opcodes.ACC_NATIVE;
                    mn.access &= ~Opcodes.ACC_SYNCHRONIZED;
                    needsLoader = true;
                }
            }

            if (needsLoader) injectLoadLibrary(cn);
        }
    }

    private void injectLoadLibrary(ClassNode cn) {
        MethodNode clinit = cn.methods.stream()
            .filter(m -> m.name.equals("<clinit>")).findFirst().orElseGet(() -> {
                MethodNode m = new MethodNode(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
                m.instructions = new InsnList();
                m.instructions.add(new InsnNode(Opcodes.RETURN));
                m.tryCatchBlocks = new ArrayList<>();
                cn.methods.add(m);
                return m;
            });

        InsnList load = new InsnList();
        load.add(new LdcInsnNode(libraryFileName));
        load.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
            LOADER_INTERNAL_NAME, "loadBundled", "(Ljava/lang/String;)V", false));
        clinit.instructions.insert(load);
    }

    private void ensureLoaderClassPresent(Map<String, ClassNode> classes) {
        if (classes.containsKey(LOADER_INTERNAL_NAME)) {
            return;
        }

        try (var in = NativeLibraryLoader.class.getResourceAsStream("/" + LOADER_INTERNAL_NAME + ".class")) {
            if (in == null) {
                throw new IllegalStateException("Missing bundled runtime class: " + LOADER_INTERNAL_NAME);
            }

            ClassReader reader = new ClassReader(in.readAllBytes());
            ClassNode node = new ClassNode();
            reader.accept(node, 0);
            classes.put(node.name, node);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject native loader runtime class", e);
        }
    }
}
