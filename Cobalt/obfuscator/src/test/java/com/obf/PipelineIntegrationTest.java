package com.obf;

import com.obf.passes.*;
import com.obf.pipeline.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import static org.junit.jupiter.api.Assertions.*;

class PipelineIntegrationTest {

    @TempDir Path tempDir;

    /** Build a minimal Hello.jar in memory */
    private Path buildHelloJar() throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V17, Opcodes.ACC_PUBLIC, "Hello", null, "java/lang/Object", null);
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
            "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("Hello");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println",
            "(Ljava/lang/String;)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
        cw.visitEnd();

        Path jar = tempDir.resolve("Hello.jar");
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jar.toFile()))) {
            jos.putNextEntry(new JarEntry("Hello.class"));
            jos.write(cw.toByteArray());
            jos.closeEntry();
            // MANIFEST
            jos.putNextEntry(new JarEntry("META-INF/MANIFEST.MF"));
            jos.write("Manifest-Version: 1.0\nMain-Class: Hello\n".getBytes());
            jos.closeEntry();
        }
        return jar;
    }

    private Path buildFabricEntrypointJar() throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V17, Opcodes.ACC_PUBLIC, "com/example/ModInit", null, "java/lang/Object", null);

        MethodVisitor ctor = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        ctor.visitCode();
        ctor.visitVarInsn(Opcodes.ALOAD, 0);
        ctor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        ctor.visitInsn(Opcodes.RETURN);
        ctor.visitMaxs(1, 1);
        ctor.visitEnd();

        MethodVisitor entrypoint = cw.visitMethod(Opcodes.ACC_PUBLIC, "onInitialize", "()V", null, null);
        entrypoint.visitCode();
        entrypoint.visitInsn(Opcodes.RETURN);
        entrypoint.visitMaxs(0, 1);
        entrypoint.visitEnd();
        cw.visitEnd();

        Path jar = tempDir.resolve("FabricEntrypoint.jar");
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jar.toFile()))) {
            jos.putNextEntry(new JarEntry("com/example/ModInit.class"));
            jos.write(cw.toByteArray());
            jos.closeEntry();

            jos.putNextEntry(new JarEntry("fabric.mod.json"));
            jos.write(("{" +
                "\"schemaVersion\":1," +
                "\"id\":\"testmod\"," +
                "\"version\":\"1.0.0\"," +
                "\"entrypoints\":{\"main\":[\"com.example.ModInit\"]}" +
                "}").getBytes(StandardCharsets.UTF_8));
            jos.closeEntry();
        }
        return jar;
    }

    @Test
    void pipelineProducesNonEmptyOutputJar() throws Exception {
        Path inputJar  = buildHelloJar();
        Path dummyDll  = tempDir.resolve("input.dll");
        Files.writeString(dummyDll, "");
        Path outputJar = tempDir.resolve("output.jar");
        Path outputDll = tempDir.resolve("output.dll");

        Path workDir = tempDir.resolve("work");
        Files.createDirectories(workDir);

        NativeLiftingPass liftingPass = new NativeLiftingPass(outputDll, dummyDll,
            workDir, true /* skipCompile */);
        StubReplacementPass stubPass = new StubReplacementPass(liftingPass, "output");
        ClassEncryptionPass encPass  = new ClassEncryptionPass();

        Pipeline pipeline = new Pipeline(inputJar, dummyDll, outputJar, outputDll)
            .addPass(new NameObfuscationPass())
            .addPass(new StringEncryptionPass())
            .addPass(new ControlFlowPass())
            .addPass(new AntiDecompilerPass())
            .addPass(liftingPass)
            .addPass(stubPass)
            .addPass(encPass);
        pipeline.run(encPass);

        assertTrue(Files.exists(outputJar), "Output JAR should exist");
        assertTrue(Files.size(outputJar) > 0, "Output JAR should be non-empty");

        // Verify metadata is still valid even after the main class name is obfuscated.
        try (JarFile jar = new JarFile(outputJar.toFile())) {
            List<String> classEntries = Collections.list(jar.entries()).stream()
                .map(JarEntry::getName)
                .filter(name -> name.endsWith(".class"))
                .toList();
            assertFalse(classEntries.isEmpty(), "Obfuscated output should still contain class entries");
            assertTrue(classEntries.stream().noneMatch("Hello.class"::equals),
                "Main class should be obfuscated instead of left readable");

            JarEntry manifest = jar.getJarEntry("META-INF/MANIFEST.MF");
            assertNotNull(manifest, "Manifest should still be present");
            String manifestText = new String(jar.getInputStream(manifest).readAllBytes(), StandardCharsets.UTF_8);
            assertFalse(manifestText.contains("Main-Class: Hello"),
                "Manifest should be rewritten to the obfuscated main class name");
        }
    }

    @Test
    void pipelineRewritesFabricEntrypointClassesReferencedInMetadata() throws Exception {
        Path inputJar = buildFabricEntrypointJar();
        Path dummyDll = tempDir.resolve("fabric-input.dll");
        Files.writeString(dummyDll, "");
        Path outputJar = tempDir.resolve("fabric-output.jar");
        Path outputDll = tempDir.resolve("fabric-output.dll");

        Pipeline pipeline = new Pipeline(inputJar, dummyDll, outputJar, outputDll)
            .addPass(new NameObfuscationPass());
        pipeline.run();

        try (JarFile jar = new JarFile(outputJar.toFile())) {
            JarEntry metadata = jar.getJarEntry("fabric.mod.json");
            assertNotNull(metadata, "fabric.mod.json should still be present");
            String json = new String(jar.getInputStream(metadata).readAllBytes(), StandardCharsets.UTF_8);
            assertFalse(json.contains("com.example.ModInit"),
                "Fabric metadata should be rewritten to the obfuscated entrypoint class");

            List<String> classEntries = Collections.list(jar.entries()).stream()
                .map(JarEntry::getName)
                .filter(name -> name.endsWith(".class"))
                .toList();
            assertEquals(1, classEntries.size(), "Test jar should still contain exactly one class");
            assertNotEquals("com/example/ModInit.class", classEntries.getFirst(),
                "Fabric entrypoint class should now be obfuscated");
            assertTrue(json.contains(classEntries.getFirst().replace('/', '.').replace(".class", "")),
                "Fabric metadata should reference the obfuscated entrypoint class");
        }
    }
}
