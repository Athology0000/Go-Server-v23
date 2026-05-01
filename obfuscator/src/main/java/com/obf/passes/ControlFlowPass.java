package com.obf.passes;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ControlFlowPass implements Pass {

    @Override
    public void apply(Map<String, ClassNode> classes) {
        for (ClassNode cn : classes.values()) {
            Set<String> bootstrapTargets = collectBootstrapImplementationTargets(cn);
            for (MethodNode mn : cn.methods) {
                if (mn.instructions.size() < 4) continue;
                if ((mn.access & Opcodes.ACC_NATIVE) != 0) continue;
                if ((mn.access & (Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE)) != 0) continue;
                if (mn.name.equals("<init>") || mn.name.equals("<clinit>")) continue;
                if (mn.name.startsWith("lambda$")) continue;
                if (mn.name.contains("$lambda$")) continue;
                if (bootstrapTargets.contains(mn.name + mn.desc)) continue;
                if (containsInvokeDynamic(mn)) continue;
                injectOpaquePredicates(mn);
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

    /**
     * Inserts at the start of each method:
     *   int x = System.nanoTime() & 0xFF;
     *   if ((x * (x+1)) % 2 != 0) { // never true
     *       [dead code block: push/pop garbage]
     *   }
     */
    private void injectOpaquePredicates(MethodNode mn) {
        InsnList inject = new InsnList();
        LabelNode skip = new LabelNode();

        // x = (int)(System.nanoTime() & 0xFFL)
        inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
            "java/lang/System", "nanoTime", "()J", false));
        inject.add(new LdcInsnNode(0xFFL));
        inject.add(new InsnNode(Opcodes.LAND));
        inject.add(new InsnNode(Opcodes.L2I));
        inject.add(new VarInsnNode(Opcodes.ISTORE, mn.maxLocals));

        // x + 1
        inject.add(new VarInsnNode(Opcodes.ILOAD, mn.maxLocals));
        inject.add(new VarInsnNode(Opcodes.ILOAD, mn.maxLocals));
        inject.add(new InsnNode(Opcodes.ICONST_1));
        inject.add(new InsnNode(Opcodes.IADD));
        inject.add(new InsnNode(Opcodes.IMUL));
        inject.add(new InsnNode(Opcodes.ICONST_2));
        inject.add(new InsnNode(Opcodes.IREM));
        // if == 0, skip dead block (always taken)
        inject.add(new JumpInsnNode(Opcodes.IFEQ, skip));

        // Dead code block — push/pop NOP equivalent
        inject.add(new LdcInsnNode("DEADCODE"));
        inject.add(new InsnNode(Opcodes.POP));
        inject.add(new LdcInsnNode(0xDEAD));
        inject.add(new InsnNode(Opcodes.POP));

        inject.add(skip);

        mn.instructions.insert(inject);
        mn.maxLocals += 1;
        mn.maxStack += 4;
    }
}
