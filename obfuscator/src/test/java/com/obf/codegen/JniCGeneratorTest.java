package com.obf.codegen;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class JniCGeneratorTest {
    @Test
    void generatesValidCForSimpleAddMethod() {
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

        String c = new JniCGenerator("com_example_Foo", mn).generate();

        assertTrue(c.contains("JNIEXPORT"), "Should have JNI export macro");
        assertTrue(c.contains("jint"), "Should use jint for int");
        assertTrue(c.contains("return"), "Should have return statement");
    }

    @Test
    void generatesValidCForVoidMethod() {
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC,
            "doNothing", "()V", null, null);
        InsnList insns = new InsnList();
        insns.add(new InsnNode(Opcodes.RETURN));
        mn.instructions = insns;
        mn.tryCatchBlocks = new ArrayList<>();
        mn.maxLocals = 1;
        mn.maxStack = 0;

        String c = new JniCGenerator("com_example_Foo", mn).generate();
        assertTrue(c.contains("void"));
        assertTrue(c.contains("return;"));
    }
}
