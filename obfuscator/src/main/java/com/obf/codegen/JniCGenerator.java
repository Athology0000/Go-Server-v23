package com.obf.codegen;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class JniCGenerator {
    private final String jniClassName; // e.g. "com_example_Foo"
    private final MethodNode method;
    private final Deque<String> stack = new ArrayDeque<>();
    private final Set<String> emittedLabels = new HashSet<>();
    private final Map<Integer, String> localTypes = new LinkedHashMap<>();
    private final Map<String, String> valueTypes = new HashMap<>();
    private final Set<String> declaredTemps = new LinkedHashSet<>();
    private final StringBuilder body = new StringBuilder();
    private int tempCounter = 0;

    public JniCGenerator(String jniClassName, MethodNode method) {
        this.jniClassName = jniClassName;
        this.method = method;
    }

    public String generate() {
        Type[] argTypes = Type.getArgumentTypes(method.desc);
        Type returnType = Type.getReturnType(method.desc);
        boolean isStatic = (method.access & Opcodes.ACC_STATIC) != 0;

        StringBuilder params = new StringBuilder("JNIEnv *_env, ");
        params.append(isStatic ? "jclass _cls" : "jobject _this");
        for (int i = 0; i < argTypes.length; i++) {
            params.append(", ").append(jniType(argTypes[i])).append(" _p").append(i);
        }

        int slot = isStatic ? 0 : 1;
        if (!isStatic) {
            initializeLocal(0, "jobject", "_this");
        }
        for (int i = 0; i < argTypes.length; i++) {
            initializeLocal(slot, jniType(argTypes[i]), "_p" + i);
            slot += argTypes[i].getSize();
        }

        for (AbstractInsnNode insn : method.instructions) {
            translateInsn(insn);
        }

        StringBuilder decls = new StringBuilder();
        for (String decl : declaredTemps) {
            decls.append("  ").append(decl).append(";\n");
        }

        return "#include <jni.h>\n#include <stdint.h>\n\n" +
            "JNIEXPORT " + jniType(returnType) + " JNICALL\n" +
            "Java_" + jniClassName + "_" + method.name + "(\n" +
            "    " + params + ")\n{\n" +
            decls +
            body +
            "}\n";
    }

    private void translateInsn(AbstractInsnNode insn) {
        if (insn instanceof LabelNode lbl) {
            emitLabel(lbl);
            return;
        }
        if (insn instanceof LineNumberNode || insn instanceof FrameNode) {
            return;
        }

        int op = insn.getOpcode();
        if (op == -1) {
            return;
        }

        switch (op) {
            case Opcodes.ICONST_M1 -> push("jint", "-1");
            case Opcodes.ICONST_0  -> push("jint", "0");
            case Opcodes.ICONST_1  -> push("jint", "1");
            case Opcodes.ICONST_2  -> push("jint", "2");
            case Opcodes.ICONST_3  -> push("jint", "3");
            case Opcodes.ICONST_4  -> push("jint", "4");
            case Opcodes.ICONST_5  -> push("jint", "5");
            case Opcodes.LCONST_0  -> push("jlong", "0LL");
            case Opcodes.LCONST_1  -> push("jlong", "1LL");
            case Opcodes.FCONST_0  -> push("jfloat", "0.0f");
            case Opcodes.FCONST_1  -> push("jfloat", "1.0f");
            case Opcodes.FCONST_2  -> push("jfloat", "2.0f");
            case Opcodes.DCONST_0  -> push("jdouble", "0.0");
            case Opcodes.DCONST_1  -> push("jdouble", "1.0");
            case Opcodes.ACONST_NULL -> push("jobject", "NULL");

            case Opcodes.BIPUSH, Opcodes.SIPUSH -> {
                IntInsnNode iin = (IntInsnNode) insn;
                push("jint", String.valueOf(iin.operand));
            }

            case Opcodes.LDC -> {
                LdcInsnNode ldc = (LdcInsnNode) insn;
                if (ldc.cst instanceof Integer i) {
                    push("jint", String.valueOf(i));
                } else if (ldc.cst instanceof Long l) {
                    push("jlong", l + "LL");
                } else if (ldc.cst instanceof Float f) {
                    push("jfloat", f + "f");
                } else if (ldc.cst instanceof Double d) {
                    push("jdouble", String.valueOf(d));
                } else if (ldc.cst instanceof String s) {
                    push("jobject", "(*_env)->NewStringUTF(_env, \"" + escapeCString(s) + "\")");
                } else {
                    throw unsupported("Unsupported LDC constant: " + ldc.cst);
                }
            }

            case Opcodes.ILOAD -> push("jint", "_local" + ((VarInsnNode) insn).var);
            case Opcodes.LLOAD -> push("jlong", "_local" + ((VarInsnNode) insn).var);
            case Opcodes.FLOAD -> push("jfloat", "_local" + ((VarInsnNode) insn).var);
            case Opcodes.DLOAD -> push("jdouble", "_local" + ((VarInsnNode) insn).var);
            case Opcodes.ALOAD -> push("jobject", "_local" + ((VarInsnNode) insn).var);

            case Opcodes.ISTORE -> storeLocal(((VarInsnNode) insn).var, "jint", pop());
            case Opcodes.LSTORE -> storeLocal(((VarInsnNode) insn).var, "jlong", pop());
            case Opcodes.FSTORE -> storeLocal(((VarInsnNode) insn).var, "jfloat", pop());
            case Opcodes.DSTORE -> storeLocal(((VarInsnNode) insn).var, "jdouble", pop());
            case Opcodes.ASTORE -> storeLocal(((VarInsnNode) insn).var, "jobject", pop());

            case Opcodes.IADD -> { String b = pop(), a = pop(); push("jint", "(" + a + "+" + b + ")"); }
            case Opcodes.ISUB -> { String b = pop(), a = pop(); push("jint", "(" + a + "-" + b + ")"); }
            case Opcodes.IMUL -> { String b = pop(), a = pop(); push("jint", "(" + a + "*" + b + ")"); }
            case Opcodes.IDIV -> { String b = pop(), a = pop(); push("jint", "(" + a + "/" + b + ")"); }
            case Opcodes.IREM -> { String b = pop(), a = pop(); push("jint", "(" + a + "%" + b + ")"); }
            case Opcodes.INEG -> { String a = pop(); push("jint", "(-" + a + ")"); }
            case Opcodes.ISHL -> { String b = pop(), a = pop(); push("jint", "(" + a + "<<" + b + ")"); }
            case Opcodes.ISHR -> { String b = pop(), a = pop(); push("jint", "(" + a + ">>" + b + ")"); }
            case Opcodes.IUSHR -> {
                String b = pop(), a = pop();
                push("jint", "((jint)(((uint32_t)(" + a + ")) >> (" + b + ")))");
            }
            case Opcodes.IAND -> { String b = pop(), a = pop(); push("jint", "(" + a + "&" + b + ")"); }
            case Opcodes.IOR  -> { String b = pop(), a = pop(); push("jint", "(" + a + "|" + b + ")"); }
            case Opcodes.IXOR -> { String b = pop(), a = pop(); push("jint", "(" + a + "^" + b + ")"); }

            case Opcodes.LADD -> { String b = pop(), a = pop(); push("jlong", "(" + a + "+" + b + ")"); }
            case Opcodes.LSUB -> { String b = pop(), a = pop(); push("jlong", "(" + a + "-" + b + ")"); }
            case Opcodes.LMUL -> { String b = pop(), a = pop(); push("jlong", "(" + a + "*" + b + ")"); }
            case Opcodes.LDIV -> { String b = pop(), a = pop(); push("jlong", "(" + a + "/" + b + ")"); }
            case Opcodes.LREM -> { String b = pop(), a = pop(); push("jlong", "(" + a + "%" + b + ")"); }
            case Opcodes.LNEG -> { String a = pop(); push("jlong", "(-" + a + ")"); }
            case Opcodes.LAND -> { String b = pop(), a = pop(); push("jlong", "(" + a + "&" + b + ")"); }
            case Opcodes.LOR  -> { String b = pop(), a = pop(); push("jlong", "(" + a + "|" + b + ")"); }
            case Opcodes.LXOR -> { String b = pop(), a = pop(); push("jlong", "(" + a + "^" + b + ")"); }

            case Opcodes.FADD -> { String b = pop(), a = pop(); push("jfloat", "(" + a + "+" + b + ")"); }
            case Opcodes.FSUB -> { String b = pop(), a = pop(); push("jfloat", "(" + a + "-" + b + ")"); }
            case Opcodes.FMUL -> { String b = pop(), a = pop(); push("jfloat", "(" + a + "*" + b + ")"); }
            case Opcodes.FDIV -> { String b = pop(), a = pop(); push("jfloat", "(" + a + "/" + b + ")"); }
            case Opcodes.DADD -> { String b = pop(), a = pop(); push("jdouble", "(" + a + "+" + b + ")"); }
            case Opcodes.DSUB -> { String b = pop(), a = pop(); push("jdouble", "(" + a + "-" + b + ")"); }
            case Opcodes.DMUL -> { String b = pop(), a = pop(); push("jdouble", "(" + a + "*" + b + ")"); }
            case Opcodes.DDIV -> { String b = pop(), a = pop(); push("jdouble", "(" + a + "/" + b + ")"); }

            case Opcodes.I2L -> { String a = pop(); push("jlong", "((jlong)" + a + ")"); }
            case Opcodes.I2F -> { String a = pop(); push("jfloat", "((jfloat)" + a + ")"); }
            case Opcodes.I2D -> { String a = pop(); push("jdouble", "((jdouble)" + a + ")"); }
            case Opcodes.L2I -> { String a = pop(); push("jint", "((jint)" + a + ")"); }
            case Opcodes.L2F -> { String a = pop(); push("jfloat", "((jfloat)" + a + ")"); }
            case Opcodes.L2D -> { String a = pop(); push("jdouble", "((jdouble)" + a + ")"); }
            case Opcodes.F2I -> { String a = pop(); push("jint", "((jint)" + a + ")"); }
            case Opcodes.F2D -> { String a = pop(); push("jdouble", "((jdouble)" + a + ")"); }
            case Opcodes.D2I -> { String a = pop(); push("jint", "((jint)" + a + ")"); }
            case Opcodes.D2L -> { String a = pop(); push("jlong", "((jlong)" + a + ")"); }
            case Opcodes.D2F -> { String a = pop(); push("jfloat", "((jfloat)" + a + ")"); }
            case Opcodes.I2B -> { String a = pop(); push("jbyte", "((jbyte)" + a + ")"); }
            case Opcodes.I2C -> { String a = pop(); push("jchar", "((jchar)" + a + ")"); }
            case Opcodes.I2S -> { String a = pop(); push("jshort", "((jshort)" + a + ")"); }

            case Opcodes.POP  -> body.append("  (void)(").append(pop()).append(");\n");
            case Opcodes.POP2 -> throw unsupported("POP2 is not supported");
            case Opcodes.DUP  -> { String a = peek(); push(inferType(a), a); }
            case Opcodes.NOP  -> body.append("  /* nop */\n");

            case Opcodes.GOTO -> {
                LabelNode lbl = ((JumpInsnNode) insn).label;
                body.append("  goto _L").append(System.identityHashCode(lbl)).append(";\n");
            }
            case Opcodes.IFEQ -> emitConditionalJump((JumpInsnNode) insn, pop() + " == 0");
            case Opcodes.IFNE -> emitConditionalJump((JumpInsnNode) insn, pop() + " != 0");
            case Opcodes.IFLT -> emitConditionalJump((JumpInsnNode) insn, pop() + " < 0");
            case Opcodes.IFGE -> emitConditionalJump((JumpInsnNode) insn, pop() + " >= 0");
            case Opcodes.IFGT -> emitConditionalJump((JumpInsnNode) insn, pop() + " > 0");
            case Opcodes.IFLE -> emitConditionalJump((JumpInsnNode) insn, pop() + " <= 0");
            case Opcodes.IF_ICMPEQ -> { String b = pop(), a = pop(); emitConditionalJump((JumpInsnNode) insn, a + " == " + b); }
            case Opcodes.IF_ICMPNE -> { String b = pop(), a = pop(); emitConditionalJump((JumpInsnNode) insn, a + " != " + b); }
            case Opcodes.IF_ICMPLT -> { String b = pop(), a = pop(); emitConditionalJump((JumpInsnNode) insn, a + " < " + b); }
            case Opcodes.IF_ICMPGE -> { String b = pop(), a = pop(); emitConditionalJump((JumpInsnNode) insn, a + " >= " + b); }
            case Opcodes.IF_ICMPGT -> { String b = pop(), a = pop(); emitConditionalJump((JumpInsnNode) insn, a + " > " + b); }
            case Opcodes.IF_ICMPLE -> { String b = pop(), a = pop(); emitConditionalJump((JumpInsnNode) insn, a + " <= " + b); }
            case Opcodes.IF_ACMPEQ -> { String b = pop(), a = pop(); emitConditionalJump((JumpInsnNode) insn, a + " == " + b); }
            case Opcodes.IF_ACMPNE -> { String b = pop(), a = pop(); emitConditionalJump((JumpInsnNode) insn, a + " != " + b); }
            case Opcodes.IFNULL    -> emitConditionalJump((JumpInsnNode) insn, pop() + " == NULL");
            case Opcodes.IFNONNULL -> emitConditionalJump((JumpInsnNode) insn, pop() + " != NULL");

            case Opcodes.RETURN -> body.append("  return;\n");
            case Opcodes.IRETURN, Opcodes.LRETURN,
                 Opcodes.FRETURN, Opcodes.DRETURN,
                 Opcodes.ARETURN -> body.append("  return ").append(pop()).append(";\n");

            case Opcodes.GETSTATIC -> {
                FieldInsnNode fi = (FieldInsnNode) insn;
                String t = jniTypeFromDesc(fi.desc);
                String tmp = newTemp(t);
                body.append("  jfieldID _fid").append(tmp).append(" = (*_env)->GetStaticFieldID(_env, ")
                    .append("(*_env)->FindClass(_env, \"").append(fi.owner).append("\"), \"")
                    .append(fi.name).append("\", \"").append(fi.desc).append("\");\n");
                body.append("  ").append(tmp).append(" = (*_env)->GetStatic")
                    .append(jniFieldAccessor(fi.desc)).append("Field(_env, ")
                    .append("(*_env)->FindClass(_env, \"").append(fi.owner).append("\"), _fid").append(tmp).append(");\n");
                stack.push(tmp);
            }
            case Opcodes.GETFIELD -> {
                FieldInsnNode fi = (FieldInsnNode) insn;
                String obj = pop();
                String t = jniTypeFromDesc(fi.desc);
                String tmp = newTemp(t);
                body.append("  jfieldID _fid").append(tmp).append(" = (*_env)->GetFieldID(_env, ")
                    .append("(*_env)->GetObjectClass(_env, ").append(obj).append("), \"")
                    .append(fi.name).append("\", \"").append(fi.desc).append("\");\n");
                body.append("  ").append(tmp).append(" = (*_env)->Get")
                    .append(jniFieldAccessor(fi.desc)).append("Field(_env, ").append(obj)
                    .append(", _fid").append(tmp).append(");\n");
                stack.push(tmp);
            }
            case Opcodes.PUTSTATIC -> {
                FieldInsnNode fi = (FieldInsnNode) insn;
                String val = pop();
                body.append("  { jfieldID _fid = (*_env)->GetStaticFieldID(_env, ")
                    .append("(*_env)->FindClass(_env, \"").append(fi.owner).append("\"), \"")
                    .append(fi.name).append("\", \"").append(fi.desc).append("\"); ")
                    .append("(*_env)->SetStatic").append(jniFieldAccessor(fi.desc)).append("Field(_env, ")
                    .append("(*_env)->FindClass(_env, \"").append(fi.owner).append("\"), _fid, ").append(val).append("); }\n");
            }
            case Opcodes.PUTFIELD -> {
                FieldInsnNode fi = (FieldInsnNode) insn;
                String val = pop();
                String obj = pop();
                body.append("  { jfieldID _fid = (*_env)->GetFieldID(_env, ")
                    .append("(*_env)->GetObjectClass(_env, ").append(obj).append("), \"")
                    .append(fi.name).append("\", \"").append(fi.desc).append("\"); ")
                    .append("(*_env)->Set").append(jniFieldAccessor(fi.desc)).append("Field(_env, ")
                    .append(obj).append(", _fid, ").append(val).append("); }\n");
            }

            case Opcodes.INVOKESTATIC, Opcodes.INVOKEVIRTUAL,
                 Opcodes.INVOKESPECIAL, Opcodes.INVOKEINTERFACE -> {
                MethodInsnNode mi = (MethodInsnNode) insn;
                Type[] args = Type.getArgumentTypes(mi.desc);
                Type ret = Type.getReturnType(mi.desc);
                String[] argVars = new String[args.length];
                for (int i = args.length - 1; i >= 0; i--) {
                    argVars[i] = pop();
                }

                String objVar = (op == Opcodes.INVOKESTATIC) ? null : pop();
                StringBuilder call = new StringBuilder();
                boolean isStaticCall = (op == Opcodes.INVOKESTATIC);
                String accessor = (op == Opcodes.INVOKEINTERFACE) ? "Interface" : "";

                if (isStaticCall) {
                    call.append("(*_env)->CallStatic").append(jniReturnAccessor(ret)).append("Method(_env, ")
                        .append("(*_env)->FindClass(_env, \"").append(mi.owner).append("\"), ")
                        .append("(*_env)->GetStaticMethodID(_env, (*_env)->FindClass(_env, \"").append(mi.owner)
                        .append("\"), \"").append(mi.name).append("\", \"").append(mi.desc).append("\")");
                } else {
                    call.append("(*_env)->Call").append(accessor).append(jniReturnAccessor(ret)).append("Method(_env, ")
                        .append(objVar).append(", ")
                        .append("(*_env)->GetMethodID(_env, (*_env)->GetObjectClass(_env, ").append(objVar)
                        .append("), \"").append(mi.name).append("\", \"").append(mi.desc).append("\")");
                }
                for (String av : argVars) {
                    call.append(", ").append(av);
                }
                call.append(")");

                if (ret.getSort() == Type.VOID) {
                    body.append("  ").append(call).append(";\n");
                } else {
                    String t = jniType(ret);
                    String tmp = newTemp(t);
                    body.append("  ").append(tmp).append(" = ").append(call).append(";\n");
                    stack.push(tmp);
                }
            }

            case Opcodes.NEW -> {
                TypeInsnNode ti = (TypeInsnNode) insn;
                String tmp = newTemp("jobject");
                body.append("  jclass _cls").append(tmp).append(" = (*_env)->FindClass(_env, \"")
                    .append(ti.desc).append("\");\n");
                body.append("  ").append(tmp).append(" = (*_env)->AllocObject(_env, _cls").append(tmp).append(");\n");
                stack.push(tmp);
            }

            case Opcodes.ARRAYLENGTH -> {
                String arr = pop();
                push("jint", "(*_env)->GetArrayLength(_env, " + arr + ")");
            }

            case Opcodes.ATHROW -> {
                String ex = pop();
                emitThrowAndReturn(ex);
            }

            case Opcodes.CHECKCAST -> {
                // Value remains on the stack.
            }
            case Opcodes.INSTANCEOF -> {
                TypeInsnNode ti = (TypeInsnNode) insn;
                String obj = pop();
                push("jboolean", "(*_env)->IsInstanceOf(_env, " + obj +
                    ", (*_env)->FindClass(_env, \"" + ti.desc + "\"))");
            }

            case Opcodes.IINC -> {
                IincInsnNode ii = (IincInsnNode) insn;
                body.append("  _local").append(ii.var).append(" += ").append(ii.incr).append(";\n");
            }

            default -> throw unsupported("Unsupported opcode " + op);
        }
    }

    private void initializeLocal(int slot, String type, String value) {
        localTypes.put(slot, type);
        valueTypes.put("_local" + slot, type);
        body.append("  ").append(type).append(" _local").append(slot)
            .append(" = ").append(value).append(";\n");
    }

    private void storeLocal(int slot, String type, String value) {
        String existingType = localTypes.get(slot);
        if (existingType == null) {
            initializeLocal(slot, type, value);
            return;
        }
        if (!existingType.equals(type)) {
            throw unsupported("Local slot " + slot + " changes type from " + existingType + " to " + type);
        }
        body.append("  _local").append(slot).append(" = ").append(value).append(";\n");
    }

    private void emitLabel(LabelNode label) {
        String name = "_L" + System.identityHashCode(label);
        if (emittedLabels.add(name)) {
            body.append(name).append(":;\n");
        }
    }

    private String newTemp(String type) {
        String name = "_t" + (tempCounter++);
        declaredTemps.add(type + " " + name);
        valueTypes.put(name, type);
        return name;
    }

    private void push(String type, String expr) {
        String tmp = newTemp(type);
        body.append("  ").append(tmp).append(" = ").append(expr).append(";\n");
        stack.push(tmp);
    }

    private String pop() {
        return stack.isEmpty() ? "0" : stack.pop();
    }

    private String peek() {
        return stack.isEmpty() ? "0" : stack.peek();
    }

    private String inferType(String varName) {
        return valueTypes.getOrDefault(varName, "jobject");
    }

    private void emitConditionalJump(JumpInsnNode insn, String cond) {
        body.append("  if (").append(cond).append(") goto _L")
            .append(System.identityHashCode(insn.label)).append(";\n");
    }

    private void emitThrowAndReturn(String throwableExpr) {
        Type returnType = Type.getReturnType(method.desc);
        body.append("  (*_env)->Throw(_env, (jthrowable)").append(throwableExpr).append(");\n");
        if (returnType.getSort() == Type.VOID) {
            body.append("  return;\n");
            return;
        }
        body.append("  return ").append(defaultReturnValue(returnType)).append(";\n");
    }

    private String defaultReturnValue(Type type) {
        return switch (type.getSort()) {
            case Type.BOOLEAN -> "JNI_FALSE";
            case Type.BYTE, Type.CHAR, Type.SHORT, Type.INT -> "0";
            case Type.LONG -> "0LL";
            case Type.FLOAT -> "0.0f";
            case Type.DOUBLE -> "0.0";
            default -> "NULL";
        };
    }

    private UnsupportedOperationException unsupported(String detail) {
        return new UnsupportedOperationException(method.name + method.desc + ": " + detail);
    }

    private String escapeCString(String value) {
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '\\' -> escaped.append("\\\\");
                case '"' -> escaped.append("\\\"");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (ch < 0x20 || ch > 0x7E) {
                        escaped.append(String.format("\\u%04x", (int) ch));
                    } else {
                        escaped.append(ch);
                    }
                }
            }
        }
        return escaped.toString();
    }

    private String jniType(Type t) {
        return switch (t.getSort()) {
            case Type.VOID    -> "void";
            case Type.BOOLEAN -> "jboolean";
            case Type.BYTE    -> "jbyte";
            case Type.CHAR    -> "jchar";
            case Type.SHORT   -> "jshort";
            case Type.INT     -> "jint";
            case Type.LONG    -> "jlong";
            case Type.FLOAT   -> "jfloat";
            case Type.DOUBLE  -> "jdouble";
            default           -> "jobject";
        };
    }

    private String jniTypeFromDesc(String desc) {
        return jniType(Type.getType(desc));
    }

    private String jniFieldAccessor(String desc) {
        return switch (desc) {
            case "Z" -> "Boolean";
            case "B" -> "Byte";
            case "C" -> "Char";
            case "S" -> "Short";
            case "I" -> "Int";
            case "J" -> "Long";
            case "F" -> "Float";
            case "D" -> "Double";
            default  -> "Object";
        };
    }

    private String jniReturnAccessor(Type t) {
        return switch (t.getSort()) {
            case Type.VOID    -> "Void";
            case Type.BOOLEAN -> "Boolean";
            case Type.BYTE    -> "Byte";
            case Type.CHAR    -> "Char";
            case Type.SHORT   -> "Short";
            case Type.INT     -> "Int";
            case Type.LONG    -> "Long";
            case Type.FLOAT   -> "Float";
            case Type.DOUBLE  -> "Double";
            default           -> "Object";
        };
    }
}
