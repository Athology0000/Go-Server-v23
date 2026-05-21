package com.obf.passes;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AntiDecompilerPass implements Pass {

    @Override
    public void apply(Map<String, ClassNode> classes) {
        for (ClassNode cn : classes.values()) {
            Set<String> bootstrapTargets = collectBootstrapImplementationTargets(cn);
            for (MethodNode mn : cn.methods) {
                if (mn.instructions.size() < 2) continue;
                if ((mn.access & Opcodes.ACC_NATIVE) != 0) continue;
                if ((mn.access & (Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE)) != 0) continue;
                if (mn.name.equals("<init>") || mn.name.equals("<clinit>")) continue;
                if (mn.name.startsWith("lambda$")) continue;
                if (mn.name.contains("$lambda$")) continue;
                if (bootstrapTargets.contains(mn.name + mn.desc)) continue;
                if (containsInvokeDynamic(mn)) continue;
                injectMisleadingLineNumbers(mn);
                injectOverlappingHandlers(mn);
            }
        }
    }

    private Set<String> collectBootstrapImplementationTargets(ClassNode cn) {
        Set<String> targets = new HashSet<>();
        for (MethodNode mn : cn.methods) {
            if (mn.instructions == null) {
                continue;
            }
            for (AbstractInsnNode insn : mn.instructions.toArray()) {
                if (!(insn instanceof InvokeDynamicInsnNode indy)) {
                    continue;
                }
                for (Object arg : indy.bsmArgs) {
                    if (arg instanceof Handle handle && cn.name.equals(handle.getOwner())) {
                        targets.add(handle.getName() + handle.getDesc());
                    }
                }
            }
        }
        return targets;
    }

    private boolean containsInvokeDynamic(MethodNode mn) {
        for (AbstractInsnNode insn : mn.instructions.toArray()) {
            if (insn instanceof InvokeDynamicInsnNode) {
                return true;
            }
        }
        return false;
    }

    /** Scrambles line number table so decompilers emit wrong source mappings */
    private void injectMisleadingLineNumbers(MethodNode mn) {
        int fakeLine = 0xFFFF;
        for (AbstractInsnNode insn : mn.instructions.toArray()) {
            if (insn instanceof LineNumberNode lnn) {
                lnn.line = fakeLine--;
            }
        }
        // Inject additional fake line numbers at random points
        int count = 0;
        for (AbstractInsnNode insn : mn.instructions.toArray()) {
            if (count++ % 3 == 0) {
                LabelNode lbl = new LabelNode();
                mn.instructions.insertBefore(insn, lbl);
                mn.instructions.insertBefore(insn, new LineNumberNode(fakeLine--, lbl));
            }
        }
    }

    /**
     * Inserts an overlapping exception handler covering the whole method body
     * that catches Throwable but immediately re-throws. Decompilers that try to
     * reconstruct try-catch regions from overlapping tables fail or emit garbage.
     */
    private void injectOverlappingHandlers(MethodNode mn) {
        if (mn.tryCatchBlocks == null) mn.tryCatchBlocks = new java.util.ArrayList<>();

        // Find first and last real instruction
        AbstractInsnNode first = mn.instructions.getFirst();
        AbstractInsnNode last  = mn.instructions.getLast();
        if (first == null || last == null || first == last) return;

        LabelNode start   = new LabelNode();
        LabelNode end     = new LabelNode();
        LabelNode handler = new LabelNode();

        // Handler: pop Throwable and re-throw (ATHROW)
        InsnList handlerBlock = new InsnList();
        handlerBlock.add(handler);
        handlerBlock.add(new InsnNode(Opcodes.ATHROW));

        mn.instructions.insertBefore(first, start);
        mn.instructions.add(end);
        mn.instructions.add(handlerBlock);

        // Add catch-all handler entry
        mn.tryCatchBlocks.add(0, new TryCatchBlockNode(start, end, handler, null));
    }
}
