package com.obf.passes;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class StringEncryptionPassTest {
    @Test
    void stringLiteralsAreReplaced() {
        ClassNode cn = new ClassNode();
        cn.name = "com/example/Test";
        cn.superName = "java/lang/Object";
        cn.interfaces = new ArrayList<>();
        cn.fields = new ArrayList<>();
        cn.access = Opcodes.ACC_PUBLIC;

        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
            "hello", "()Ljava/lang/String;", null, null);
        InsnList insns = new InsnList();
        insns.add(new LdcInsnNode("Hello World"));
        insns.add(new InsnNode(Opcodes.ARETURN));
        mn.instructions = insns;
        mn.tryCatchBlocks = new ArrayList<>();
        cn.methods = new ArrayList<>(List.of(mn));

        Map<String, ClassNode> classes = new HashMap<>();
        classes.put(cn.name, cn);

        new StringEncryptionPass().apply(classes);

        ClassNode result = classes.get(cn.name);
        MethodNode resultMethod = result.methods.stream()
            .filter(m -> m.name.equals("hello")).findFirst().orElseThrow();

        boolean hasOriginalPlaintext = false;
        boolean callsDecryptBase64 = false;
        for (AbstractInsnNode insn : resultMethod.instructions) {
            if (insn instanceof LdcInsnNode ldc
                && ldc.cst instanceof String s
                && s.equals("Hello World")) {
                hasOriginalPlaintext = true;
            }
            if (insn instanceof MethodInsnNode mi
                && mi.owner.equals("com/obf/runtime/StringDecryptor")
                && mi.name.equals("decryptBase64")) {
                callsDecryptBase64 = true;
            }
        }
        assertFalse(hasOriginalPlaintext, "Original plaintext should not remain in bytecode");
        assertTrue(callsDecryptBase64, "Encrypted strings should be decrypted via StringDecryptor");
    }
}
