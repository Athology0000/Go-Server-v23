package com.obf.pipeline;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.ClassNode;
import java.nio.file.Path;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class JarReaderTest {
    @Test
    void readsClassesFromJar() throws Exception {
        // Use the obfuscator's own JAR as test input after first build
        Path jar = Path.of("build/libs/obfuscator.jar");
        JarReader reader = new JarReader(jar);
        Map<String, ClassNode> classes = reader.readClasses();
        assertFalse(classes.isEmpty());
        assertTrue(classes.containsKey("com/obf/Main") ||
                   classes.values().stream().anyMatch(c -> c.name.startsWith("com/obf")));
    }

    @Test
    void readsNonClassEntriesAsRawBytes() throws Exception {
        Path jar = Path.of("build/libs/obfuscator.jar");
        JarReader reader = new JarReader(jar);
        Map<String, byte[]> resources = reader.readResources();
        assertTrue(resources.containsKey("META-INF/MANIFEST.MF"));
    }
}
