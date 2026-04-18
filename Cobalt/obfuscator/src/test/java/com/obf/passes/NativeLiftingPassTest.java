package com.obf.passes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class NativeLiftingPassTest {
    @TempDir
    Path tempDir;

    @Test
    void cSourceIsGeneratedForNonTrivialMethod() throws Exception {
        ClassNode cn = new ClassNode();
        cn.name = "com/example/Compute";
        cn.superName = "java/lang/Object";
        cn.interfaces = new ArrayList<>();
        cn.fields = new ArrayList<>();
        cn.access = Opcodes.ACC_PUBLIC;

        // Non-trivial method (> 3 instructions)
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
            "add", "(II)I", null, null);
        InsnList insns = new InsnList();
        insns.add(new VarInsnNode(Opcodes.ILOAD, 0));
        insns.add(new VarInsnNode(Opcodes.ILOAD, 1));
        insns.add(new InsnNode(Opcodes.IADD));
        insns.add(new InsnNode(Opcodes.IRETURN));
        mn.instructions = insns;
        mn.tryCatchBlocks = new ArrayList<>();
        mn.maxLocals = 2;
        mn.maxStack = 2;
        cn.methods = new ArrayList<>(List.of(mn));

        Path outputDll = tempDir.resolve("output.dll");
        // Pass skipCompile=true so we don't need Clang in unit tests
        NativeLiftingPass pass = new NativeLiftingPass(outputDll, null, tempDir, true);

        Map<String, ClassNode> classes = new HashMap<>();
        classes.put(cn.name, cn);
        pass.apply(classes);

        // C file should be generated
        boolean cFileExists = Files.list(tempDir)
            .anyMatch(p -> p.toString().endsWith(".c"));
        assertTrue(cFileExists, "C source file should be generated");
    }
}
