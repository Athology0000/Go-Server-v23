package com.obf.runtime;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.lang.reflect.*;

/**
 * Injected into the output JAR as the Main-Class.
 * Decrypts all class files on demand and delegates to the real main.
 */
public class EncryptedClassLoader extends ClassLoader {
    // These are set by ClassEncryptionPass when it injects this class
    private static final byte[] KEY = { /* PLACEHOLDER_KEY */ };
    private static final byte[] IV  = { /* PLACEHOLDER_IV  */ };
    private static final String REAL_MAIN = "PLACEHOLDER_MAIN";
    private final Map<String, byte[]> encryptedClasses = new HashMap<>();

    public EncryptedClassLoader() throws Exception {
        String jarPath = EncryptedClassLoader.class.getProtectionDomain()
            .getCodeSource().getLocation().toURI().getPath();
        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry e = entries.nextElement();
                if (e.getName().endsWith(".class")) {
                    encryptedClasses.put(
                        e.getName().replace('/', '.').replace(".class", ""),
                        jar.getInputStream(e).readAllBytes()
                    );
                }
            }
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] encrypted = encryptedClasses.get(name);
        if (encrypted == null) throw new ClassNotFoundException(name);
        try {
            byte[] decrypted = decrypt(encrypted);
            return defineClass(name, decrypted, 0, decrypted.length);
        } catch (Exception e) {
            throw new ClassNotFoundException(name, e);
        }
    }

    private byte[] decrypt(byte[] data) throws Exception {
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(KEY, "AES"), new IvParameterSpec(IV));
        return c.doFinal(data);
    }

    public static void main(String[] args) throws Exception {
        EncryptedClassLoader loader = new EncryptedClassLoader();
        Class<?> main = loader.loadClass(REAL_MAIN);
        Method m = main.getMethod("main", String[].class);
        m.invoke(null, (Object) args);
    }
}
