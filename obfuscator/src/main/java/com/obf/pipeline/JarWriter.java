package com.obf.pipeline;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import java.io.*;
import java.nio.file.Path;
import java.util.Map;
import java.util.jar.*;

public class JarWriter {
    private final Path outputJar;

    public JarWriter(Path outputJar) {
        this.outputJar = outputJar;
    }

    /**
     * @param classes   transformed ClassNodes
     * @param resources non-class entries (MANIFEST, etc.) — pass-through unchanged
     * @param rawEncryptedClasses if non-null, these encrypted bytes override the ClassNode bytes
     */
    public void write(Map<String, ClassNode> classes,
                      Map<String, byte[]> resources,
                      Map<String, byte[]> rawEncryptedClasses) throws IOException {
        try (JarOutputStream jos = new JarOutputStream(
                new BufferedOutputStream(new FileOutputStream(outputJar.toFile())))) {

            // Write resources first (MANIFEST must be first entry)
            for (Map.Entry<String, byte[]> e : resources.entrySet()) {
                jos.putNextEntry(new JarEntry(e.getKey()));
                jos.write(e.getValue());
                jos.closeEntry();
            }

            // Write classes
            for (Map.Entry<String, ClassNode> e : classes.entrySet()) {
                String entryName = e.getKey() + ".class";
                byte[] bytes;
                if (rawEncryptedClasses != null && rawEncryptedClasses.containsKey(entryName)) {
                    bytes = rawEncryptedClasses.get(entryName);
                } else {
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
                    e.getValue().accept(cw);
                    bytes = cw.toByteArray();
                }
                jos.putNextEntry(new JarEntry(entryName));
                jos.write(bytes);
                jos.closeEntry();
            }
        }
    }
}
