package com.obf.pipeline;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.*;

public class JarReader {
    private final Path jarPath;

    public JarReader(Path jarPath) {
        this.jarPath = jarPath;
    }

    public Map<String, ClassNode> readClasses() throws IOException {
        Map<String, ClassNode> classes = new LinkedHashMap<>();
        try (JarFile jar = new JarFile(jarPath.toFile())) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    try (InputStream is = jar.getInputStream(entry)) {
                        ClassReader cr = new ClassReader(is.readAllBytes());
                        ClassNode cn = new ClassNode();
                        cr.accept(cn, 0);
                        classes.put(cn.name, cn);
                    }
                }
            }
        }
        return classes;
    }

    public Map<String, byte[]> readResources() throws IOException {
        Map<String, byte[]> resources = new LinkedHashMap<>();
        try (JarFile jar = new JarFile(jarPath.toFile())) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.getName().endsWith(".class") && !entry.isDirectory()) {
                    try (InputStream is = jar.getInputStream(entry)) {
                        resources.put(entry.getName(), is.readAllBytes());
                    }
                }
            }
        }
        return resources;
    }
}
