package com.obf;

import com.obf.passes.*;
import com.obf.pipeline.Pipeline;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        ObfuscationProfile profile = ObfuscationProfile.DEFAULT;
        List<String> positional = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if ("--profile".equals(args[i])) {
                if (i + 1 >= args.length) {
                    System.err.println("Missing value for --profile");
                    System.exit(1);
                }
                try {
                    profile = ObfuscationProfile.parse(args[++i]);
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                    System.err.println("Valid profiles: default, aggressive");
                    System.exit(1);
                }
                continue;
            }
            positional.add(args[i]);
        }

        if (positional.size() < 4) {
            System.err.println("Usage: java -jar obfuscator.jar [--profile default|aggressive] <input.jar> <input.dll> <output.jar> <output.dll>");
            System.err.println("  OBFUSCATOR_CLANG env var: path to Clang with OLLVM passes (default: clang)");
            System.exit(1);
        }

        Path inputJar  = Path.of(positional.get(0));
        Path inputDll  = Path.of(positional.get(1));
        Path outputJar = Path.of(positional.get(2));
        Path outputDll = Path.of(positional.get(3));

        Pipeline pipeline = new Pipeline(inputJar, inputDll, outputJar, outputDll)
            .addPass(new NameObfuscationPass(
                profile == ObfuscationProfile.AGGRESSIVE
                    ? NameObfuscationPass.Mode.AGGRESSIVE
                    : NameObfuscationPass.Mode.DEFAULT))
            .addPass(new StringEncryptionPass())
            .addPass(new ControlFlowPass())
            .addPass(new AntiDecompilerPass());

        if (profile == ObfuscationProfile.AGGRESSIVE) {
            NativeLiftingPass liftingPass = new NativeLiftingPass(outputDll, inputDll);
            pipeline
                .addPass(liftingPass)
                .addPass(new StubReplacementPass(liftingPass, outputDll.getFileName().toString()));
        }

        pipeline.run(null);
    }
}
