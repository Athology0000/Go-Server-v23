/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.comparisons.ComparisonsKt
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
 *  net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
 *  net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
 *  net.minecraft.class_124
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_5250
 *  net.minecraft.class_638
 *  net.minecraft.class_7157
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.grotto;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.class_124;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_5250;
import net.minecraft.class_638;
import net.minecraft.class_7157;
import net.minecraft.class_746;
import org.cobalt.internal.grotto.BlockScanUtils;
import org.cobalt.internal.grotto.GrottoChat;
import org.cobalt.internal.grotto.GrottoScanner;
import org.cobalt.internal.grotto.LookedAtBlockUtils;
import org.cobalt.internal.grotto.RouteOffsets;
import org.cobalt.internal.grotto.RouteUtils;
import org.cobalt.internal.mining.FairyModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u0019\u0010\b\u001a\u00020\u00042\b\u0010\u0007\u001a\u0004\u0018\u00010\u0006H\u0007\u00a2\u0006\u0004\b\b\u0010\tJ3\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\n2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rH\u0002\u00a2\u0006\u0004\b\u0012\u0010\u0013JE\u0010\u0019\u001a\u00020\u00182\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00110\u00142\u0006\u0010\f\u001a\u00020\n2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\u0006\u0010\u0016\u001a\u00020\n2\b\u0010\u0017\u001a\u0004\u0018\u00010\nH\u0002\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\u001d\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\u0006\u0010\u000b\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u001b\u0010\u001cJ\u0017\u0010\u001e\u001a\u00020\u00182\u0006\u0010\u001d\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u001e\u0010\u001fJ\u0017\u0010 \u001a\u00020\n2\u0006\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b \u0010!R\u0016\u0010#\u001a\u00020\"8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b#\u0010$R\u0018\u0010%\u001a\u0004\u0018\u00010\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b%\u0010&\u00a8\u0006'"}, d2={"Lorg/cobalt/internal/grotto/GrottoCommands;", "", "<init>", "()V", "", "register", "Lnet/minecraft/class_2338;", "pos", "setDetectedMansionCore", "(Lnet/minecraft/class_2338;)V", "", "name", "routeName", "", "Lorg/cobalt/internal/grotto/RouteOffsets$Offset;", "offsets", "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", "Lnet/fabricmc/fabric/api/client/command/v2/FabricClientCommandSource;", "routeCmd", "(Ljava/lang/String;Ljava/lang/String;[Lorg/cobalt/internal/grotto/RouteOffsets$Offset;)Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", "Lcom/mojang/brigadier/context/CommandContext;", "ctx", "preset", "mode", "", "runRoute", "(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;[Lorg/cobalt/internal/grotto/RouteOffsets$Offset;Ljava/lang/String;Ljava/lang/String;)I", "buildBlacklistCommand", "(Ljava/lang/String;)Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", "argString", "handleBlacklistArgs", "(Ljava/lang/String;)I", "fmtPos", "(Lnet/minecraft/class_2338;)Ljava/lang/String;", "", "registered", "Z", "detectedMansionCoreOffset", "Lnet/minecraft/class_2338;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nGrottoCommands.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GrottoCommands.kt\norg/cobalt/internal/grotto/GrottoCommands\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,325:1\n777#2:326\n873#2,2:327\n1080#2:329\n1915#2,2:330\n*S KotlinDebug\n*F\n+ 1 GrottoCommands.kt\norg/cobalt/internal/grotto/GrottoCommands\n*L\n239#1:326\n239#1:327,2\n118#1:329\n122#1:330,2\n*E\n"})
public final class GrottoCommands {
    @NotNull
    public static final GrottoCommands INSTANCE = new GrottoCommands();
    private static boolean registered;
    @Nullable
    private static class_2338 detectedMansionCoreOffset;

    private GrottoCommands() {
    }

    @JvmStatic
    public static final void register() {
        if (registered) {
            return;
        }
        registered = true;
        ClientCommandRegistrationCallback.EVENT.register(GrottoCommands::register$lambda$0);
    }

    @JvmStatic
    public static final void setDetectedMansionCore(@Nullable class_2338 pos) {
        detectedMansionCoreOffset = pos;
    }

    private final LiteralArgumentBuilder<FabricClientCommandSource> routeCmd(String name, String routeName, RouteOffsets.Offset[] offsets) {
        ArgumentBuilder argumentBuilder = ((LiteralArgumentBuilder)ClientCommandManager.literal((String)name).then(((RequiredArgumentBuilder)ClientCommandManager.argument((String)"preset", (ArgumentType)((ArgumentType)StringArgumentType.word())).executes(arg_0 -> GrottoCommands.routeCmd$lambda$0(routeName, offsets, arg_0))).then(ClientCommandManager.argument((String)"mode", (ArgumentType)((ArgumentType)StringArgumentType.word())).executes(arg_0 -> GrottoCommands.routeCmd$lambda$1(routeName, offsets, arg_0))))).executes(arg_0 -> GrottoCommands.routeCmd$lambda$2(name, arg_0));
        Intrinsics.checkNotNullExpressionValue((Object)argumentBuilder, (String)"executes(...)");
        return (LiteralArgumentBuilder)argumentBuilder;
    }

    private final int runRoute(CommandContext<FabricClientCommandSource> ctx, String routeName, RouteOffsets.Offset[] offsets, String preset, String mode) {
        if (!((Boolean)FairyModule.INSTANCE.getEnabled().getValue()).booleanValue()) {
            class_5250 class_52502 = class_2561.method_43470((String)"Fairy Grotto module is disabled.").method_27692(class_124.field_1061);
            Intrinsics.checkNotNullExpressionValue((Object)class_52502, (String)"withStyle(...)");
            GrottoChat.send((class_2561)class_52502);
            return 0;
        }
        class_2338 baseOverride = detectedMansionCoreOffset;
        if (baseOverride != null && Intrinsics.areEqual((Object)routeName, (Object)"OptimisedMansion")) {
            class_5250 class_52503 = class_2561.method_43470((String)"Using detected mansion core (base) at ").method_27692(class_124.field_1060).method_10852((class_2561)class_2561.method_43470((String)(baseOverride.method_10263() + "," + baseOverride.method_10264() + "," + baseOverride.method_10260())).method_27692(class_124.field_1068));
            Intrinsics.checkNotNullExpressionValue((Object)class_52503, (String)"append(...)");
            GrottoChat.send((class_2561)class_52503);
            RouteUtils.generateRouteForClientAt(routeName, offsets, preset, mode, baseOverride);
            return 1;
        }
        if (Intrinsics.areEqual((Object)routeName, (Object)"OptimisedMansion") && baseOverride == null) {
            class_5250 class_52504 = class_2561.method_43470((String)"No mansion core detected yet; using your current position as base.").method_27692(class_124.field_1054);
            Intrinsics.checkNotNullExpressionValue((Object)class_52504, (String)"withStyle(...)");
            GrottoChat.send((class_2561)class_52504);
        }
        RouteUtils.generateRouteForClient(routeName, offsets, preset, mode);
        return 1;
    }

    private final LiteralArgumentBuilder<FabricClientCommandSource> buildBlacklistCommand(String name) {
        ArgumentBuilder argumentBuilder = ((LiteralArgumentBuilder)ClientCommandManager.literal((String)name).then(ClientCommandManager.argument((String)"args", (ArgumentType)((ArgumentType)StringArgumentType.greedyString())).executes(GrottoCommands::buildBlacklistCommand$lambda$0))).executes(GrottoCommands::buildBlacklistCommand$lambda$1);
        Intrinsics.checkNotNullExpressionValue((Object)argumentBuilder, (String)"executes(...)");
        return (LiteralArgumentBuilder)argumentBuilder;
    }

    /*
     * WARNING - void declaration
     */
    private final int handleBlacklistArgs(String argString) {
        void $this$filterTo$iv$iv;
        void $this$filter$iv;
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return 0;
        }
        GrottoScanner.initBlacklistFile();
        Object object = ((Object)StringsKt.trim((CharSequence)argString)).toString();
        Regex regex = new Regex("\\s+");
        int n = 0;
        object = regex.split((CharSequence)object, n);
        boolean $i$f$filter = false;
        void var6_9 = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            String it = (String)element$iv$iv;
            boolean bl = false;
            boolean bl2 = !StringsKt.isBlank((CharSequence)it);
            if (!bl2) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        List args = (List)destination$iv$iv;
        if (args.isEmpty()) {
            GrottoChat.grotto(class_124.field_1061 + "/mhbl add <x> <y> <z> | remove <x> <y> <z> | clear | addlookingat | removelookingat");
            return 0;
        }
        String string = ((String)args.get(0)).toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
        switch (string) {
            case "clear": {
                GrottoScanner.INSTANCE.clearPermanentBlacklist();
                GrottoChat.grotto(class_124.field_1080 + "Cleared permanent blacklist (built-in stays).");
                return 1;
            }
            case "removelookingat": 
            case "addlookingat": {
                LookedAtBlockUtils.LookedAtBlockInfo info = LookedAtBlockUtils.getLookedAtBlockInfo();
                if (info == null) {
                    GrottoChat.grotto(class_124.field_1061 + "Look at a block first.");
                    return 0;
                }
                class_2338 pos = new class_2338(info.getX(), info.getY(), info.getZ());
                String string2 = ((String)args.get(0)).toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
                boolean added = Intrinsics.areEqual((Object)string2, (Object)"addlookingat") ? GrottoScanner.INSTANCE.addPermanentBlacklist(pos) : GrottoScanner.INSTANCE.removePermanentBlacklist(pos);
                String string3 = ((String)args.get(0)).toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
                if (Intrinsics.areEqual((Object)string3, (Object)"addlookingat")) {
                    if (added) {
                        GrottoChat.grotto(class_124.field_1080 + "Added to permanent blacklist: " + this.fmtPos(pos));
                    } else {
                        GrottoChat.grotto(class_124.field_1054 + "Already blacklisted (or built-in): " + this.fmtPos(pos));
                    }
                } else if (added) {
                    GrottoChat.grotto(class_124.field_1080 + "Removed from permanent blacklist: " + this.fmtPos(pos));
                } else {
                    GrottoChat.grotto(class_124.field_1054 + "That block wasn't in the permanent blacklist: " + this.fmtPos(pos));
                }
                return 1;
            }
            case "add": 
            case "remove": {
                if (args.size() < 4) {
                    GrottoChat.grotto(class_124.field_1061 + "/mhbl add <x> <y> <z> | remove <x> <y> <z>");
                    return 0;
                }
                Integer x = StringsKt.toIntOrNull((String)((String)args.get(1)));
                Integer y = StringsKt.toIntOrNull((String)((String)args.get(2)));
                Integer z = StringsKt.toIntOrNull((String)((String)args.get(3)));
                if (x == null || y == null || z == null) {
                    GrottoChat.grotto(class_124.field_1061 + "Coords must be numbers.");
                    return 0;
                }
                class_2338 pos = new class_2338(x.intValue(), y.intValue(), z.intValue());
                String string4 = ((String)args.get(0)).toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)string4, (String)"toLowerCase(...)");
                boolean added = Intrinsics.areEqual((Object)string4, (Object)"add") ? GrottoScanner.INSTANCE.addPermanentBlacklist(pos) : GrottoScanner.INSTANCE.removePermanentBlacklist(pos);
                String string5 = ((String)args.get(0)).toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)string5, (String)"toLowerCase(...)");
                if (Intrinsics.areEqual((Object)string5, (Object)"add")) {
                    if (added) {
                        GrottoChat.grotto(class_124.field_1080 + "Added to permanent blacklist: " + this.fmtPos(pos));
                    } else {
                        GrottoChat.grotto(class_124.field_1054 + "Already blacklisted (or built-in): " + this.fmtPos(pos));
                    }
                } else if (added) {
                    GrottoChat.grotto(class_124.field_1080 + "Removed from permanent blacklist: " + this.fmtPos(pos));
                } else {
                    GrottoChat.grotto(class_124.field_1054 + "That block wasn't in the permanent blacklist: " + this.fmtPos(pos));
                }
                return 1;
            }
        }
        GrottoChat.grotto(class_124.field_1061 + "/mhbl add <x> <y> <z> | remove <x> <y> <z> | clear | addlookingat | removelookingat");
        return 0;
    }

    private final String fmtPos(class_2338 pos) {
        return class_124.field_1068 + pos.method_10263() + "," + pos.method_10264() + "," + pos.method_10260() + class_124.field_1080;
    }

    private static final int register$lambda$0$0(CommandContext ctx) {
        String string = StringArgumentType.getString((CommandContext)ctx, (String)"x");
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        Integer x = StringsKt.toIntOrNull((String)string);
        String string2 = StringArgumentType.getString((CommandContext)ctx, (String)"y");
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"getString(...)");
        Integer y = StringsKt.toIntOrNull((String)string2);
        String string3 = StringArgumentType.getString((CommandContext)ctx, (String)"z");
        Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"getString(...)");
        Integer z = StringsKt.toIntOrNull((String)string3);
        if (x == null || y == null || z == null) {
            GrottoChat.usage("/setmansioncore <x> <y> <z>");
            return 0;
        }
        detectedMansionCoreOffset = new class_2338(x.intValue(), y.intValue(), z.intValue());
        class_5250 class_52502 = class_2561.method_43470((String)"Mansion core base set to ").method_27692(class_124.field_1060).method_10852((class_2561)class_2561.method_43470((String)(x + "," + y + "," + z)).method_27692(class_124.field_1068));
        Intrinsics.checkNotNullExpressionValue((Object)class_52502, (String)"append(...)");
        GrottoChat.send((class_2561)class_52502);
        return 1;
    }

    private static final int register$lambda$0$1(CommandContext it) {
        GrottoScanner.INSTANCE.toggle();
        return 1;
    }

    private static final int register$lambda$0$2(CommandContext it) {
        LookedAtBlockUtils.LookedAtBlockInfo info = LookedAtBlockUtils.getLookedAtBlockInfo();
        if (info == null) {
            GrottoChat.mh(class_124.field_1061 + "No block info.");
            return 0;
        }
        GrottoChat.mh("Block: " + class_124.field_1068 + info.getName() + class_124.field_1080 + " (" + info.getId() + ":" + info.getMeta() + ") @ " + class_124.field_1068 + info.getX() + "," + info.getY() + "," + info.getZ());
        return 1;
    }

    private static final int register$lambda$0$3(CommandContext ctx) {
        int radius = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"radius");
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        class_746 player = mc.field_1724;
        class_638 level2 = mc.field_1687;
        if (player == null || level2 == null) {
            return 0;
        }
        int r = radius;
        if (r <= 0) {
            GrottoChat.mh(class_124.field_1061 + "Radius must be > 0.");
            return 0;
        }
        if (r > 20) {
            r = 20;
            GrottoChat.mh("Radius clamped to " + class_124.field_1068 + "20" + class_124.field_1080 + " to avoid insane scans.");
        }
        class_2338 class_23382 = player.method_24515();
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"blockPosition(...)");
        class_2338 pos = class_23382;
        GrottoChat.mh("Scanning blocks in radius " + class_124.field_1068 + r + class_124.field_1080 + " around " + class_124.field_1068 + pos.method_10263() + "," + pos.method_10264() + "," + pos.method_10260() + class_124.field_1080 + "...");
        Map<String, Integer> results = BlockScanUtils.scanAround((class_1937)level2, pos, r);
        if (results.isEmpty()) {
            GrottoChat.mh(class_124.field_1061 + "No blocks found.");
            return 0;
        }
        Iterable $this$sortedByDescending$iv = results.entrySet();
        boolean $i$f$sortedByDescending = false;
        List sorted = CollectionsKt.sortedWith((Iterable)$this$sortedByDescending$iv, (Comparator)new Comparator(){

            public final int compare(T a, T b) {
                Map.Entry it = (Map.Entry)b;
                boolean bl = false;
                Comparable comparable = (Integer)it.getValue();
                it = (Map.Entry)a;
                Comparable comparable2 = comparable;
                bl = false;
                return ComparisonsKt.compareValues((Comparable)comparable2, (Comparable)((Integer)it.getValue()));
            }
        });
        GrottoChat.mh("Top blocks in area:");
        int maxEntries = 12;
        Iterable $this$forEach$iv = CollectionsKt.take((Iterable)sorted, (int)maxEntries);
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            Map.Entry entry = (Map.Entry)element$iv;
            boolean bl = false;
            GrottoChat.mh(class_124.field_1080 + "- " + class_124.field_1068 + entry.getKey() + class_124.field_1080 + " x" + class_124.field_1068 + entry.getValue());
        }
        if (sorted.size() > maxEntries) {
            GrottoChat.mh(class_124.field_1080 + "(" + class_124.field_1068 + (sorted.size() - maxEntries) + class_124.field_1080 + " more types omitted...)");
        }
        return 1;
    }

    private static final int register$lambda$0$4(CommandContext it) {
        GrottoChat.mhUsage("/scanblocks <radius>");
        return 0;
    }

    private static final void register$lambda$0(CommandDispatcher dispatcher, class_7157 class_71572) {
        Intrinsics.checkNotNullParameter((Object)dispatcher, (String)"dispatcher");
        Intrinsics.checkNotNullParameter((Object)class_71572, (String)"<unused var>");
        LiteralArgumentBuilder setMansionCore = (LiteralArgumentBuilder)ClientCommandManager.literal((String)"setmansioncore").then(ClientCommandManager.argument((String)"x", (ArgumentType)((ArgumentType)StringArgumentType.word())).then(ClientCommandManager.argument((String)"y", (ArgumentType)((ArgumentType)StringArgumentType.word())).then(ClientCommandManager.argument((String)"z", (ArgumentType)((ArgumentType)StringArgumentType.word())).executes(GrottoCommands::register$lambda$0$0))));
        dispatcher.register(setMansionCore);
        dispatcher.register(INSTANCE.routeCmd("setupmansion", "Mansion", RouteOffsets.INSTANCE.getMANSION()));
        dispatcher.register(INSTANCE.routeCmd("setupoptimisedmansion", "OptimisedMansion", RouteOffsets.INSTANCE.getOPTIMISED_MANSION()));
        dispatcher.register(INSTANCE.routeCmd("setuppalace", "Palace", RouteOffsets.INSTANCE.getPALACE()));
        dispatcher.register(INSTANCE.routeCmd("setupovergrown", "Overgrown", RouteOffsets.INSTANCE.getOVERGROWN()));
        dispatcher.register(INSTANCE.routeCmd("setupshrine", "Shrine", RouteOffsets.INSTANCE.getSHRINE()));
        dispatcher.register(INSTANCE.routeCmd("setupwaterfall", "Waterfall", RouteOffsets.INSTANCE.getWATERFALL()));
        dispatcher.register((LiteralArgumentBuilder)ClientCommandManager.literal((String)"grotto").executes(GrottoCommands::register$lambda$0$1));
        dispatcher.register((LiteralArgumentBuilder)ClientCommandManager.literal((String)"blocklookingat").executes(GrottoCommands::register$lambda$0$2));
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)ClientCommandManager.literal((String)"scanblocks").then(ClientCommandManager.argument((String)"radius", (ArgumentType)((ArgumentType)IntegerArgumentType.integer((int)1))).executes(GrottoCommands::register$lambda$0$3))).executes(GrottoCommands::register$lambda$0$4));
        dispatcher.register(INSTANCE.buildBlacklistCommand("mhbl"));
        dispatcher.register(INSTANCE.buildBlacklistCommand("mhbbl"));
    }

    private static final int routeCmd$lambda$0(String $routeName, RouteOffsets.Offset[] $offsets, CommandContext ctx) {
        Intrinsics.checkNotNull((Object)ctx);
        String string = StringArgumentType.getString((CommandContext)ctx, (String)"preset");
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        return INSTANCE.runRoute((CommandContext<FabricClientCommandSource>)ctx, $routeName, $offsets, string, null);
    }

    private static final int routeCmd$lambda$1(String $routeName, RouteOffsets.Offset[] $offsets, CommandContext ctx) {
        Intrinsics.checkNotNull((Object)ctx);
        String string = StringArgumentType.getString((CommandContext)ctx, (String)"preset");
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        return INSTANCE.runRoute((CommandContext<FabricClientCommandSource>)ctx, $routeName, $offsets, string, StringArgumentType.getString((CommandContext)ctx, (String)"mode"));
    }

    private static final int routeCmd$lambda$2(String $name, CommandContext it) {
        GrottoChat.usage("/" + $name + " <polar|nebula|polinex|melody> [json]");
        return 0;
    }

    private static final int buildBlacklistCommand$lambda$0(CommandContext ctx) {
        String string = StringArgumentType.getString((CommandContext)ctx, (String)"args");
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        return INSTANCE.handleBlacklistArgs(string);
    }

    private static final int buildBlacklistCommand$lambda$1(CommandContext it) {
        GrottoChat.grotto(class_124.field_1061 + "/mhbl add <x> <y> <z> | remove <x> <y> <z> | clear | addlookingat | removelookingat");
        return 0;
    }
}

