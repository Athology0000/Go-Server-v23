package com.obf.passes;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ControlFlowPassTest {
    @Test
    void methodInstructionCountIncreases() {
        ClassNode cn = new ClassNode();
        cn.name = "com/example/Foo";
        cn.superName = "java/lang/Object";
        cn.interfaces = new ArrayList<>();
        cn.fields = new ArrayList<>();
        cn.access = Opcodes.ACC_PUBLIC;

        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
            "compute", "(I)I", null, null);
        InsnList insns = new InsnList();
        insns.add(new VarInsnNode(Opcodes.ILOAD, 0));
        insns.add(new IntInsnNode(Opcodes.BIPUSH, 2));
        insns.add(new InsnNode(Opcodes.IMUL));
        insns.add(new InsnNode(Opcodes.IRETURN));
        mn.instructions = insns;
        mn.tryCatchBlocks = new ArrayList<>();
        cn.methods = new ArrayList<>(List.of(mn));

        int before = mn.instructions.size();
        Map<String, ClassNode> classes = new HashMap<>();
        classes.put(cn.name, cn);

        new ControlFlowPass().apply(classes);

        ClassNode result = classes.get(cn.name);
        MethodNode resultMn = result.methods.stream()
            .filter(m -> m.name.equals("compute")).findFirst().orElseThrow();
        assertTrue(resultMn.instructions.size() > before,
            "Control flow pass should add instructions");
    }
}
