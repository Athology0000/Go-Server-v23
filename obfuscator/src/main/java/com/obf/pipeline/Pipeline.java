package com.obf.pipeline;

import com.obf.passes.ClassEncryptionPass;
import com.obf.passes.Pass;
import org.objectweb.asm.tree.ClassNode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Pipeline {
    private final List<Pass> passes = new ArrayList<>();
    private final Path inputJar;
    private final Path inputDll;
    private final Path outputJar;
    private final Path outputDll;

    public Pipeline(Path inputJar, Path inputDll, Path outputJar, Path outputDll) {
        this.inputJar = inputJar;
        this.inputDll = inputDll;
        this.outputJar = outputJar;
        this.outputDll = outputDll;
    }

    public Pipeline addPass(Pass pass) {
        passes.add(pass);
        return this;
    }

    public void run() throws Exception {
        run(null);
    }

    public void run(ClassEncryptionPass encPass) throws Exception {
        JarReader reader = new JarReader(inputJar);
        Map<String, ClassNode> classes = reader.readClasses();
        Map<String, byte[]> resources = reader.readResources();

        System.out.println("[*] Loaded " + classes.size() + " classes");

        for (Pass pass : passes) {
            String name = pass.getClass().getSimpleName();
            System.out.println("[*] Running " + name + "...");
            if (pass instanceof com.obf.passes.NameObfuscationPass namePass) {
                namePass.preserveMetadataClasses(resources, classes.keySet());
                namePass.apply(classes);
                namePass.rewriteMetadataResources(resources);
            } else {
                pass.apply(classes);
            }
            System.out.println("[+] " + name + " complete");
        }

        if (!Files.exists(outputDll) && Files.exists(inputDll)) {
            Files.copy(inputDll, outputDll);
        }

        if (Files.exists(outputDll)) {
            resources.put(outputDll.getFileName().toString(), Files.readAllBytes(outputDll));
        }

        Map<String, byte[]> encrypted = (encPass != null) ? encPass.encryptedBytes : null;
        new JarWriter(outputJar).write(classes, resources, encrypted);
        System.out.println("[+] Done -> " + outputJar);
    }
}
