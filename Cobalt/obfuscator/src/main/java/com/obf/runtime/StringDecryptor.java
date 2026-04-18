package com.obf.runtime;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Injected into target JARs. Decrypts strings at runtime.
 * Key and IV are stored as static fields, set by StringEncryptionPass.
 */
public class StringDecryptor {
    // These fields are set per-class during injection
    public static byte[] KEY;
    public static byte[] IV;

    public static String decrypt(byte[] encrypted) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE,
                new SecretKeySpec(KEY, "AES"),
                new IvParameterSpec(IV));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    public static String decryptBase64(String encoded) {
        try {
            return decrypt(Base64.getDecoder().decode(encoded));
        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    public static byte[] encrypt(String plain, byte[] key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE,
                new SecretKeySpec(key, "AES"),
                new IvParameterSpec(iv));
            return cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return new byte[0];
        }
    }
}
