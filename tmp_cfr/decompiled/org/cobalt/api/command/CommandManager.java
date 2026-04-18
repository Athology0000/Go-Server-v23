/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Reflection
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.jvm.internal.SpreadBuilder
 *  kotlin.reflect.KAnnotatedElement
 *  kotlin.reflect.KCallable
 *  kotlin.reflect.KClass
 *  kotlin.reflect.KClassifier
 *  kotlin.reflect.KFunction
 *  kotlin.reflect.KParameter
 *  kotlin.reflect.full.KClasses
 *  kotlin.reflect.jvm.KCallablesJvm
 *  net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
 *  net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
 *  net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
 *  net.minecraft.class_7157
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.jvm.internal.SpreadBuilder;
import kotlin.reflect.KAnnotatedElement;
import kotlin.reflect.KCallable;
import kotlin.reflect.KClass;
import kotlin.reflect.KClassifier;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;
import kotlin.reflect.full.KClasses;
import kotlin.reflect.jvm.KCallablesJvm;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.class_7157;
import org.cobalt.api.command.Command;
import org.cobalt.api.command.annotation.DefaultHandler;
import org.cobalt.api.command.annotation.SubCommand;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\n\u001a\u00020\u0006H\u0000\u00a2\u0006\u0004\b\t\u0010\u0003J\u001d\u0010\n\u001a\u00020\u00062\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0002\u00a2\u0006\u0004\b\n\u0010\u000eJ7\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\f0\u000f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\f0\u000f2\n\u0010\u0012\u001a\u0006\u0012\u0002\b\u00030\u00112\u0006\u0010\u0005\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u0013\u0010\u0014JC\u0010\u001b\u001a\f\u0012\u0004\u0012\u00020\f\u0012\u0002\b\u00030\u001a2\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00160\u00152\u0006\u0010\u0019\u001a\u00020\u00182\n\u0010\u0012\u001a\u0006\u0012\u0002\b\u00030\u00112\u0006\u0010\u0005\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u001b\u0010\u001cR\u001a\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00040\u001d8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u001f\u00a8\u0006 "}, d2={"Lorg/cobalt/api/command/CommandManager;", "", "<init>", "()V", "Lorg/cobalt/api/command/Command;", "command", "", "register", "(Lorg/cobalt/api/command/Command;)V", "dispatchAll$cobalt", "dispatchAll", "Lcom/mojang/brigadier/CommandDispatcher;", "Lnet/fabricmc/fabric/api/client/command/v2/FabricClientCommandSource;", "dispatcher", "(Lcom/mojang/brigadier/CommandDispatcher;)V", "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", "builder", "Lkotlin/reflect/KFunction;", "method", "attachExecution", "(Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;Lkotlin/reflect/KFunction;Lorg/cobalt/api/command/Command;)Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", "", "Lkotlin/reflect/KParameter;", "params", "", "index", "Lcom/mojang/brigadier/builder/RequiredArgumentBuilder;", "buildArguments", "(Ljava/util/List;ILkotlin/reflect/KFunction;Lorg/cobalt/api/command/Command;)Lcom/mojang/brigadier/builder/RequiredArgumentBuilder;", "", "commands", "Ljava/util/List;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nCommandManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CommandManager.kt\norg/cobalt/api/command/CommandManager\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 KAnnotatedElements.kt\nkotlin/reflect/full/KAnnotatedElements\n+ 5 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,107:1\n1915#2:108\n1915#2:109\n296#2,2:112\n1915#2:114\n296#2,2:116\n1916#2:118\n1916#2:119\n1916#2:120\n1596#2:121\n1629#2,4:122\n1#3:110\n20#4:111\n20#4:115\n37#5,2:126\n*S KotlinDebug\n*F\n+ 1 CommandManager.kt\norg/cobalt/api/command/CommandManager\n*L\n38#1:108\n41#1:109\n44#1:112,2\n50#1:114\n51#1:116,2\n50#1:118\n41#1:119\n38#1:120\n89#1:121\n89#1:122,4\n44#1:111\n51#1:115\n98#1:126,2\n*E\n"})
public final class CommandManager {
    @NotNull
    public static final CommandManager INSTANCE = new CommandManager();
    @NotNull
    private static final List<Command> commands = new ArrayList();

    private CommandManager() {
    }

    @JvmStatic
    public static final void register(@NotNull Command command) {
        Intrinsics.checkNotNullParameter((Object)command, (String)"command");
        commands.add(command);
    }

    public final void dispatchAll$cobalt() {
        ClientCommandRegistrationCallback.EVENT.register(CommandManager::dispatchAll$lambda$0);
    }

    private final void dispatchAll(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        Iterable $this$forEach$iv = commands;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            Command command = (Command)element$iv;
            boolean bl = false;
            List rootNames = CollectionsKt.plus((Collection)CollectionsKt.listOf((Object)command.getName()), (Object[])command.getAliases());
            Iterable $this$forEach$iv2 = rootNames;
            boolean $i$f$forEach2 = false;
            for (Object element$iv2 : $this$forEach$iv2) {
                Object v2;
                ArgumentBuilder root;
                block8: {
                    String rootName = (String)element$iv2;
                    boolean bl2 = false;
                    root = null;
                    ArgumentBuilder argumentBuilder = ClientCommandManager.literal((String)rootName);
                    Intrinsics.checkNotNullExpressionValue((Object)argumentBuilder, (String)"literal(...)");
                    root = argumentBuilder;
                    Iterable iterable = KClasses.getDeclaredMemberFunctions((KClass)Reflection.getOrCreateKotlinClass(command.getClass()));
                    Iterator iterator = iterable.iterator();
                    while (iterator.hasNext()) {
                        Object v1;
                        Object t;
                        block7: {
                            t = iterator.next();
                            KFunction it = (KFunction)t;
                            boolean bl3 = false;
                            KAnnotatedElement $this$findAnnotation$iv = (KAnnotatedElement)it;
                            boolean $i$f$findAnnotation = false;
                            Iterable $this$firstOrNull$iv$iv = $this$findAnnotation$iv.getAnnotations();
                            boolean $i$f$firstOrNull = false;
                            for (Object element$iv$iv : $this$firstOrNull$iv$iv) {
                                Annotation it$iv = (Annotation)element$iv$iv;
                                boolean bl4 = false;
                                if (!(it$iv instanceof DefaultHandler)) continue;
                                v1 = element$iv$iv;
                                break block7;
                            }
                            v1 = null;
                        }
                        if (!((Annotation)v1 != null)) continue;
                        v2 = t;
                        break block8;
                    }
                    v2 = null;
                }
                KFunction kFunction = v2;
                if (kFunction != null) {
                    KFunction method = kFunction;
                    boolean bl5 = false;
                    KCallablesJvm.setAccessible((KCallable)((KCallable)method), (boolean)true);
                    root = INSTANCE.attachExecution((LiteralArgumentBuilder<FabricClientCommandSource>)root, method, command);
                }
                Iterable $this$forEach$iv3 = KClasses.getDeclaredMemberFunctions((KClass)Reflection.getOrCreateKotlinClass(command.getClass()));
                boolean $i$f$forEach3 = false;
                for (Object element$iv3 : $this$forEach$iv3) {
                    Object v3;
                    KFunction method;
                    block9: {
                        method = (KFunction)element$iv3;
                        boolean bl6 = false;
                        KAnnotatedElement $this$findAnnotation$iv = (KAnnotatedElement)method;
                        boolean $i$f$findAnnotation = false;
                        Iterable $this$firstOrNull$iv$iv = $this$findAnnotation$iv.getAnnotations();
                        boolean $i$f$firstOrNull = false;
                        for (Object element$iv$iv : $this$firstOrNull$iv$iv) {
                            Annotation it$iv = (Annotation)element$iv$iv;
                            boolean bl7 = false;
                            if (!(it$iv instanceof SubCommand)) continue;
                            v3 = element$iv$iv;
                            break block9;
                        }
                        v3 = null;
                    }
                    if ((SubCommand)((Annotation)v3) == null) continue;
                    boolean bl8 = false;
                    KCallablesJvm.setAccessible((KCallable)((KCallable)method), (boolean)true);
                    LiteralArgumentBuilder literalArgumentBuilder = ClientCommandManager.literal((String)method.getName());
                    Intrinsics.checkNotNullExpressionValue((Object)literalArgumentBuilder, (String)"literal(...)");
                    Intrinsics.checkNotNullExpressionValue((Object)root.then((ArgumentBuilder)INSTANCE.attachExecution((LiteralArgumentBuilder<FabricClientCommandSource>)literalArgumentBuilder, method, command)), (String)"then(...)");
                }
                dispatcher.register(root);
            }
        }
    }

    private final LiteralArgumentBuilder<FabricClientCommandSource> attachExecution(LiteralArgumentBuilder<FabricClientCommandSource> builder, KFunction<?> method, Command command) {
        List params = CollectionsKt.drop((Iterable)method.getParameters(), (int)1);
        if (params.isEmpty()) {
            ArgumentBuilder argumentBuilder = builder.executes(arg_0 -> CommandManager.attachExecution$lambda$0(method, command, arg_0));
            Intrinsics.checkNotNullExpressionValue((Object)argumentBuilder, (String)"executes(...)");
            return (LiteralArgumentBuilder)argumentBuilder;
        }
        ArgumentBuilder argumentBuilder = builder.then((ArgumentBuilder)this.buildArguments(params, 0, method, command));
        Intrinsics.checkNotNullExpressionValue((Object)argumentBuilder, (String)"then(...)");
        return (LiteralArgumentBuilder)argumentBuilder;
    }

    private final RequiredArgumentBuilder<FabricClientCommandSource, ?> buildArguments(List<? extends KParameter> params, int index, KFunction<?> method, Command command) {
        RequiredArgumentBuilder requiredArgumentBuilder;
        RequiredArgumentBuilder requiredArgumentBuilder2;
        KParameter param = params.get(index);
        KClassifier kClassifier = param.getType().getClassifier();
        if (Intrinsics.areEqual((Object)kClassifier, (Object)Reflection.getOrCreateKotlinClass(Integer.TYPE))) {
            Object object = param.getName();
            if (object == null) {
                object = "arg" + index;
            }
            RequiredArgumentBuilder requiredArgumentBuilder3 = ClientCommandManager.argument((String)object, (ArgumentType)((ArgumentType)IntegerArgumentType.integer()));
            requiredArgumentBuilder2 = requiredArgumentBuilder3;
            Intrinsics.checkNotNullExpressionValue((Object)requiredArgumentBuilder3, (String)"argument(...)");
        } else if (Intrinsics.areEqual((Object)kClassifier, (Object)Reflection.getOrCreateKotlinClass(String.class))) {
            Object object = param.getName();
            if (object == null) {
                object = "arg" + index;
            }
            RequiredArgumentBuilder requiredArgumentBuilder4 = ClientCommandManager.argument((String)object, (ArgumentType)((ArgumentType)StringArgumentType.string()));
            requiredArgumentBuilder2 = requiredArgumentBuilder4;
            Intrinsics.checkNotNullExpressionValue((Object)requiredArgumentBuilder4, (String)"argument(...)");
        } else if (Intrinsics.areEqual((Object)kClassifier, (Object)Reflection.getOrCreateKotlinClass(Double.TYPE))) {
            Object object = param.getName();
            if (object == null) {
                object = "arg" + index;
            }
            RequiredArgumentBuilder requiredArgumentBuilder5 = ClientCommandManager.argument((String)object, (ArgumentType)((ArgumentType)DoubleArgumentType.doubleArg()));
            requiredArgumentBuilder2 = requiredArgumentBuilder5;
            Intrinsics.checkNotNullExpressionValue((Object)requiredArgumentBuilder5, (String)"argument(...)");
        } else if (Intrinsics.areEqual((Object)kClassifier, (Object)Reflection.getOrCreateKotlinClass(Boolean.TYPE))) {
            Object object = param.getName();
            if (object == null) {
                object = "arg" + index;
            }
            RequiredArgumentBuilder requiredArgumentBuilder6 = ClientCommandManager.argument((String)object, (ArgumentType)((ArgumentType)BoolArgumentType.bool()));
            requiredArgumentBuilder2 = requiredArgumentBuilder6;
            Intrinsics.checkNotNullExpressionValue((Object)requiredArgumentBuilder6, (String)"argument(...)");
        } else {
            throw new IllegalArgumentException("Unsupported parameter type: " + param.getType());
        }
        RequiredArgumentBuilder argBuilder = requiredArgumentBuilder2;
        if (index == CollectionsKt.getLastIndex(params)) {
            kClassifier = argBuilder.executes(arg_0 -> CommandManager.buildArguments$lambda$0(params, method, command, arg_0));
            Intrinsics.checkNotNull((Object)kClassifier);
            requiredArgumentBuilder = (RequiredArgumentBuilder)kClassifier;
        } else {
            kClassifier = argBuilder.then((ArgumentBuilder)this.buildArguments(params, index + 1, method, command));
            Intrinsics.checkNotNull((Object)kClassifier);
            requiredArgumentBuilder = (RequiredArgumentBuilder)kClassifier;
        }
        return requiredArgumentBuilder;
    }

    private static final void dispatchAll$lambda$0(CommandDispatcher dispatcher, class_7157 class_71572) {
        Intrinsics.checkNotNullParameter((Object)dispatcher, (String)"dispatcher");
        Intrinsics.checkNotNullParameter((Object)class_71572, (String)"<unused var>");
        INSTANCE.dispatchAll((CommandDispatcher<FabricClientCommandSource>)dispatcher);
    }

    private static final int attachExecution$lambda$0(KFunction $method, Command $command, CommandContext it) {
        Object[] objectArray = new Object[]{$command};
        $method.call(objectArray);
        return 1;
    }

    /*
     * WARNING - void declaration
     */
    private static final int buildArguments$lambda$0(List $params, KFunction $method, Command $command, CommandContext ctx) {
        void $this$mapIndexedTo$iv$iv;
        Iterable $this$mapIndexed$iv = $params;
        boolean $i$f$mapIndexed = false;
        Iterable iterable = $this$mapIndexed$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$mapIndexed$iv, (int)10));
        boolean $i$f$mapIndexedTo = false;
        int index$iv$iv = 0;
        for (Object item$iv$iv : $this$mapIndexedTo$iv$iv) {
            Object object;
            void p;
            int n;
            if ((n = index$iv$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            KParameter kParameter = (KParameter)item$iv$iv;
            int n2 = n;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            KClassifier kClassifier = p.getType().getClassifier();
            if (Intrinsics.areEqual((Object)kClassifier, (Object)Reflection.getOrCreateKotlinClass(Integer.TYPE))) {
                Object object2 = p.getName();
                if (object2 == null) {
                    object2 = "arg" + (int)i;
                }
                object = IntegerArgumentType.getInteger((CommandContext)ctx, (String)object2);
            } else if (Intrinsics.areEqual((Object)kClassifier, (Object)Reflection.getOrCreateKotlinClass(String.class))) {
                Object object3 = p.getName();
                if (object3 == null) {
                    object3 = "arg" + (int)i;
                }
                object = StringArgumentType.getString((CommandContext)ctx, (String)object3);
            } else if (Intrinsics.areEqual((Object)kClassifier, (Object)Reflection.getOrCreateKotlinClass(Double.TYPE))) {
                Object object4 = p.getName();
                if (object4 == null) {
                    object4 = "arg" + (int)i;
                }
                object = DoubleArgumentType.getDouble((CommandContext)ctx, (String)object4);
            } else if (Intrinsics.areEqual((Object)kClassifier, (Object)Reflection.getOrCreateKotlinClass(Boolean.TYPE))) {
                Object object5 = p.getName();
                if (object5 == null) {
                    object5 = "arg" + (int)i;
                }
                object = BoolArgumentType.getBool((CommandContext)ctx, (String)object5);
            } else {
                throw new IllegalArgumentException("Unsupported parameter type: " + p.getType());
            }
            collection.add(object);
        }
        List args = (List)destination$iv$iv;
        SpreadBuilder spreadBuilder = new SpreadBuilder(2);
        spreadBuilder.add((Object)$command);
        Collection $this$toTypedArray$iv = args;
        boolean $i$f$toTypedArray = false;
        Collection thisCollection$iv = $this$toTypedArray$iv;
        spreadBuilder.addSpread((Object)thisCollection$iv.toArray(new Object[0]));
        $method.call(spreadBuilder.toArray(new Object[spreadBuilder.size()]));
        return 1;
    }
}

