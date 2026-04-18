package com.obf.passes;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class ClassEncryptionPass implements Pass {
    private final byte[] key = new byte[16];
    private final byte[] iv  = new byte[16];
    // Output: encrypted class bytes keyed by "pkg/Name.class"
    public final Map<String, byte[]> encryptedBytes = new HashMap<>();

    public ClassEncryptionPass() {
        new SecureRandom().nextBytes(key);
        new SecureRandom().nextBytes(iv);
    }

    public byte[] getKey() { return key; }
    public byte[] getIv()  { return iv; }

    @Override
    public void apply(Map<String, ClassNode> classes) {
        for (Map.Entry<String, ClassNode> entry : classes.entrySet()) {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES) {
                @Override
                protected String getCommonSuperClass(String type1, String type2) {
                    try {
                        return super.getCommonSuperClass(type1, type2);
                    } catch (Throwable t) {
                        return "java/lang/Object";
                    }
                }
            };
            entry.getValue().accept(cw);
            byte[] classBytes = cw.toByteArray();
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(key, "AES"),
                    new IvParameterSpec(iv));
                encryptedBytes.put(entry.getKey() + ".class", cipher.doFinal(classBytes));
            } catch (Exception e) {
                throw new RuntimeException("Encryption failed for " + entry.getKey(), e);
            }
        }
    }
}
