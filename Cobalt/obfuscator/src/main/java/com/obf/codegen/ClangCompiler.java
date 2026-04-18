package com.obf.codegen;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ClangCompiler {
    private final Path workDir;   // temp dir for .c and .obj files
    private final Path jniHeader; // path to jni.h include dir
    private final List<Path> objFiles = new ArrayList<>();

    public ClangCompiler(Path workDir, Path jniIncludeDir) {
        this.workDir = workDir;
        this.jniHeader = jniIncludeDir;
    }

    /**
     * Compile a single JNI C source file to an object file.
     * @param cSource path to the .c file
     * @param objName name for the .obj output (without extension)
     * @return path to the produced .obj file
     */
    public Path compile(Path cSource, String objName) throws IOException, InterruptedException {
        Path obj = workDir.resolve(objName + ".obj");
        String clang = System.getenv().getOrDefault("OBFUSCATOR_CLANG", "clang");

        List<String> cmd = new ArrayList<>();
        cmd.add(clang);
        cmd.add("-c");
        cmd.add("-O2");
        // OLLVM/Hikari flags are only added when OBFUSCATOR_OLLVM=1.
        if ("1".equals(System.getenv("OBFUSCATOR_OLLVM"))) {
            cmd.addAll(List.of(
                "-mllvm", "-bcf",
                "-mllvm", "-bcf_loop=3",
                "-mllvm", "-fla",
                "-mllvm", "-sub",
                "-mllvm", "-sub_loop=3"
            ));
        }
        cmd.addAll(List.of(
            "-I", jniHeader.toString(),
            "-I", jniHeader.resolve("win32").toString(),
            cSource.toAbsolutePath().toString(),
            "-o", obj.toAbsolutePath().toString()
        ));

        runCommand(cmd, "Clang compile: " + cSource.getFileName());
        objFiles.add(obj);
        return obj;
    }

    /**
     * Link all compiled object files into a single DLL.
     * A response file keeps the Windows command line under the CreateProcess limit.
     */
    public void linkDll(Path outputDll, Path originalDll) throws IOException, InterruptedException {
        String clang = System.getenv().getOrDefault("OBFUSCATOR_CLANG", "clang");

        List<String> linkerArgs = new ArrayList<>();
        linkerArgs.add("-shared");
        linkerArgs.add("-o");
        linkerArgs.add(outputDll.toAbsolutePath().toString());

        for (Path obj : objFiles) {
            linkerArgs.add(obj.toAbsolutePath().toString());
        }

        Path responseFile = workDir.resolve("linker.rsp");
        Files.writeString(responseFile, formatResponseFile(linkerArgs), StandardCharsets.UTF_8);

        List<String> cmd = new ArrayList<>();
        cmd.add(clang);
        cmd.add("@" + responseFile.toAbsolutePath());
        runCommand(cmd, "Clang link -> " + outputDll.getFileName());
    }

    public void compileAntiDebug(Path resourceDir) throws IOException, InterruptedException {
        Path antiDebugC = resourceDir.resolve("anti_debug.c");
        Path sha256C = resourceDir.resolve("sha256.c");
        Files.copy(antiDebugC, workDir.resolve("anti_debug.c"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(sha256C, workDir.resolve("sha256.c"), StandardCopyOption.REPLACE_EXISTING);
        compile(workDir.resolve("anti_debug.c"), "anti_debug");
    }

    private void runCommand(List<String> cmd, String label) throws IOException, InterruptedException {
        System.out.println("  [clang] " + label);
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        pb.directory(workDir.toFile());
        Process proc = pb.start();
        String output = new String(proc.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int exit = proc.waitFor();
        if (exit != 0) {
            throw new IOException("Clang failed (exit " + exit + "):\n" + output);
        }
    }

    private String formatResponseFile(List<String> args) {
        StringBuilder response = new StringBuilder();
        for (String arg : args) {
            response.append(quoteForResponseFile(arg)).append(System.lineSeparator());
        }
        return response.toString();
    }

    private String quoteForResponseFile(String arg) {
        String normalized = arg.replace('\\', '/');
        if (normalized.indexOf(' ') >= 0 || normalized.indexOf('\t') >= 0) {
            return "\"" + normalized.replace("\"", "\\\"") + "\"";
        }
        return normalized;
    }
}
