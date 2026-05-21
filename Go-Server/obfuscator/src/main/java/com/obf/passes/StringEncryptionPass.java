package com.obf.passes;

import com.obf.runtime.StringDecryptor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

public class StringEncryptionPass implements Pass {
    private static final String DECRYPTOR_INTERNAL_NAME = "com/obf/runtime/StringDecryptor";
    private final SecureRandom rng = new SecureRandom();
    private final byte[] key = new byte[16];
    private final byte[] iv = new byte[16];

    public StringEncryptionPass() {
        rng.nextBytes(key);
        rng.nextBytes(iv);
    }

    @Override
    public void apply(Map<String, ClassNode> classes) {
        ensureDecryptorClassPresent(classes);

        for (ClassNode cn : classes.values()) {
            if (DECRYPTOR_INTERNAL_NAME.equals(cn.name)) {
                continue;
            }

            boolean modified = false;
            for (MethodNode mn : cn.methods) {
                if ((mn.access & Opcodes.ACC_NATIVE) != 0 || mn.instructions == null) {
                    continue;
                }
                for (AbstractInsnNode insn : mn.instructions.toArray()) {
                    if (insn instanceof LdcInsnNode ldc && ldc.cst instanceof String s) {
                        byte[] encrypted = StringDecryptor.encrypt(s, key, iv);
                        String encoded = Base64.getEncoder().encodeToString(encrypted);
                        InsnList replacement = new InsnList();
                        replacement.add(new LdcInsnNode(encoded));
                        replacement.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                            DECRYPTOR_INTERNAL_NAME,
                            "decryptBase64", "(Ljava/lang/String;)Ljava/lang/String;", false));
                        mn.instructions.insert(insn, replacement);
                        mn.instructions.remove(insn);
                        modified = true;
                    }
                }
            }

            if (modified) {
                injectKeyInit(cn, key, iv);
            }
        }
    }

    private void injectKeyInit(ClassNode cn, byte[] key, byte[] iv) {
        MethodNode clinit = cn.methods.stream()
            .filter(m -> m.name.equals("<clinit>")).findFirst().orElseGet(() -> {
                MethodNode m = new MethodNode(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
                m.instructions = new InsnList();
                m.instructions.add(new InsnNode(Opcodes.RETURN));
                m.tryCatchBlocks = new java.util.ArrayList<>();
                cn.methods.add(m);
                return m;
            });

        InsnList init = new InsnList();
        init.add(buildByteArray(key));
        init.add(new FieldInsnNode(Opcodes.PUTSTATIC,
            DECRYPTOR_INTERNAL_NAME, "KEY", "[B"));
        init.add(buildByteArray(iv));
        init.add(new FieldInsnNode(Opcodes.PUTSTATIC,
            DECRYPTOR_INTERNAL_NAME, "IV", "[B"));
        clinit.instructions.insert(init);
    }

    private void ensureDecryptorClassPresent(Map<String, ClassNode> classes) {
        if (classes.containsKey(DECRYPTOR_INTERNAL_NAME)) {
            return;
        }

        try (var in = StringDecryptor.class.getResourceAsStream("/" + DECRYPTOR_INTERNAL_NAME + ".class")) {
            if (in == null) {
                throw new IllegalStateException("Missing bundled runtime class: " + DECRYPTOR_INTERNAL_NAME);
            }

            ClassReader reader = new ClassReader(in.readAllBytes());
            ClassNode node = new ClassNode();
            reader.accept(node, 0);
            classes.put(node.name, node);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject runtime decryptor class", e);
        }
    }

    private InsnList buildByteArray(byte[] data) {
        InsnList l = new InsnList();
        l.add(new LdcInsnNode(data.length));
        l.add(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_BYTE));
        for (int i = 0; i < data.length; i++) {
            l.add(new InsnNode(Opcodes.DUP));
            l.add(new LdcInsnNode(i));
            l.add(new LdcInsnNode((int) data[i]));
            l.add(new InsnNode(Opcodes.BASTORE));
        }
        return l;
    }
}
