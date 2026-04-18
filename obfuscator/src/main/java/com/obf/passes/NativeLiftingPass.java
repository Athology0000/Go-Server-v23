package com.obf.passes;

import com.obf.codegen.ClangCompiler;
import com.obf.codegen.JniCGenerator;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class NativeLiftingPass implements Pass {
    private static final Set<Integer> SUPPORTED_OPCODES = Set.of(
        Opcodes.NOP,
        Opcodes.ACONST_NULL,
        Opcodes.ICONST_M1, Opcodes.ICONST_0, Opcodes.ICONST_1, Opcodes.ICONST_2,
        Opcodes.ICONST_3, Opcodes.ICONST_4, Opcodes.ICONST_5,
        Opcodes.LCONST_0, Opcodes.LCONST_1,
        Opcodes.FCONST_0, Opcodes.FCONST_1, Opcodes.FCONST_2,
        Opcodes.DCONST_0, Opcodes.DCONST_1,
        Opcodes.BIPUSH, Opcodes.SIPUSH, Opcodes.LDC,
        Opcodes.ILOAD, Opcodes.LLOAD, Opcodes.FLOAD, Opcodes.DLOAD, Opcodes.ALOAD,
        Opcodes.ISTORE, Opcodes.LSTORE, Opcodes.FSTORE, Opcodes.DSTORE, Opcodes.ASTORE,
        Opcodes.POP, Opcodes.DUP,
        Opcodes.IADD, Opcodes.ISUB, Opcodes.IMUL, Opcodes.IDIV, Opcodes.IREM, Opcodes.INEG,
        Opcodes.ISHL, Opcodes.ISHR, Opcodes.IUSHR, Opcodes.IAND, Opcodes.IOR, Opcodes.IXOR,
        Opcodes.LADD, Opcodes.LSUB, Opcodes.LMUL, Opcodes.LDIV, Opcodes.LREM, Opcodes.LNEG,
        Opcodes.LAND, Opcodes.LOR, Opcodes.LXOR,
        Opcodes.FADD, Opcodes.FSUB, Opcodes.FMUL, Opcodes.FDIV,
        Opcodes.DADD, Opcodes.DSUB, Opcodes.DMUL, Opcodes.DDIV,
        Opcodes.I2L, Opcodes.I2F, Opcodes.I2D,
        Opcodes.L2I, Opcodes.L2F, Opcodes.L2D,
        Opcodes.F2I, Opcodes.F2D,
        Opcodes.D2I, Opcodes.D2L, Opcodes.D2F,
        Opcodes.I2B, Opcodes.I2C, Opcodes.I2S,
        Opcodes.GETSTATIC, Opcodes.GETFIELD, Opcodes.PUTSTATIC, Opcodes.PUTFIELD,
        Opcodes.INVOKESTATIC, Opcodes.INVOKEVIRTUAL, Opcodes.INVOKESPECIAL,
        Opcodes.NEW, Opcodes.ARRAYLENGTH, Opcodes.CHECKCAST, Opcodes.INSTANCEOF,
        Opcodes.IINC, Opcodes.ATHROW,
        Opcodes.RETURN, Opcodes.IRETURN, Opcodes.LRETURN, Opcodes.FRETURN, Opcodes.DRETURN, Opcodes.ARETURN
    );

    private final Path outputDll;
    private final Path originalDll;
    private final Path workDir;
    private final boolean skipCompile; // for testing without Clang
    public final Map<String, Set<String>> liftedMethods = new LinkedHashMap<>();

    public NativeLiftingPass(Path outputDll, Path originalDll) throws IOException {
        this(outputDll, originalDll, Files.createTempDirectory("obf-native"), false);
    }

    public NativeLiftingPass(Path outputDll, Path originalDll, Path workDir, boolean skipCompile) {
        this.outputDll = outputDll;
        this.originalDll = originalDll;
        this.workDir = workDir;
        this.skipCompile = skipCompile;
    }

    @Override
    public void apply(Map<String, ClassNode> classes) {
        String jniInclude = findJniInclude();
        ClangCompiler compiler = skipCompile ? null :
            new ClangCompiler(workDir, Path.of(jniInclude));

        for (ClassNode cn : classes.values()) {
            String jniClass = cn.name.replace('/', '_').replace('$', '_');
            Set<String> lifted = new HashSet<>();

            for (MethodNode mn : cn.methods) {
                if (!shouldLift(mn)) {
                    continue;
                }

                try {
                    String cSource = new JniCGenerator(jniClass, mn).generate();
                    String safeName = sanitize(mn.name) + "_" + Math.abs(mn.desc.hashCode());
                    Path cFile = workDir.resolve(jniClass + "_" + safeName + ".c");
                    Files.writeString(cFile, cSource);

                    if (!skipCompile) {
                        compiler.compile(cFile, jniClass + "_" + safeName);
                    }

                    lifted.add(mn.name + mn.desc);
                } catch (Exception e) {
                    System.err.println("  [warn] Could not lift " + cn.name + "." + mn.name + ": " + e.getMessage());
                }
            }

            if (!lifted.isEmpty()) {
                liftedMethods.put(cn.name, lifted);
            }
        }

        if (!skipCompile && !liftedMethods.isEmpty()) {
            try {
                compiler.linkDll(outputDll, originalDll);
            } catch (Exception e) {
                throw new RuntimeException("DLL linking failed", e);
            }
        }
    }

    private boolean shouldLift(MethodNode mn) {
        if ((mn.access & (Opcodes.ACC_NATIVE | Opcodes.ACC_ABSTRACT | Opcodes.ACC_BRIDGE | Opcodes.ACC_SYNTHETIC)) != 0) return false;
        if (mn.name.equals("<init>") || mn.name.equals("<clinit>")) return false;
        if (mn.name.startsWith("lambda$")) return false;
        if (mn.instructions == null) return false;
        if (mn.tryCatchBlocks != null && !mn.tryCatchBlocks.isEmpty()) return false;

        long realInsns = 0;
        for (AbstractInsnNode insn : mn.instructions) {
            int op = insn.getOpcode();
            if (op == -1) {
                continue;
            }
            if (insn instanceof JumpInsnNode
                || insn instanceof LookupSwitchInsnNode
                || insn instanceof TableSwitchInsnNode) {
                return false;
            }
            if (!SUPPORTED_OPCODES.contains(op)) {
                return false;
            }
            realInsns++;
        }
        return realInsns > 3;
    }

    private String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9]", "_");
    }

    private String findJniInclude() {
        String javaHome = System.getProperty("java.home");
        return javaHome + "/include";
    }
}
