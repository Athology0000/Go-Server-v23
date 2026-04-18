/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_124
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_5250
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.grotto;

import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_5250;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u001f\u0010\n\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\fH\u0007\u00a2\u0006\u0004\b\u0007\u0010\rJ\u0017\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\fH\u0007\u00a2\u0006\u0004\b\u000e\u0010\rJ\u0017\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\fH\u0007\u00a2\u0006\u0004\b\u000f\u0010\rJ\u0017\u0010\u0010\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\fH\u0007\u00a2\u0006\u0004\b\u0010\u0010\rJ\u0017\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u0011\u001a\u00020\fH\u0007\u00a2\u0006\u0004\b\u0012\u0010\rJ\u0017\u0010\u0011\u001a\u00020\u00062\u0006\u0010\u0011\u001a\u00020\fH\u0007\u00a2\u0006\u0004\b\u0011\u0010\rR\u0014\u0010\u0014\u001a\u00020\u00138\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0014\u0010\u0015R\u0014\u0010\t\u001a\u00020\u00168\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\t\u0010\u0017R\u0014\u0010\u0018\u001a\u00020\u00168\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0017R\u0014\u0010\u0019\u001a\u00020\u00168\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u0017R\u0014\u0010\u001a\u001a\u00020\u00168\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001a\u0010\u0017\u00a8\u0006\u001b"}, d2={"Lorg/cobalt/internal/grotto/GrottoChat;", "", "<init>", "()V", "Lnet/minecraft/class_2561;", "message", "", "send", "(Lnet/minecraft/class_2561;)V", "prefix", "sendWithPrefix", "(Lnet/minecraft/class_2561;Lnet/minecraft/class_2561;)V", "", "(Ljava/lang/String;)V", "grotto", "autoRoutes", "mh", "usage", "mhUsage", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lnet/minecraft/class_5250;", "Lnet/minecraft/class_5250;", "grottoPrefix", "autoRoutesPrefix", "mhPrefix", "cobalt"})
public final class GrottoChat {
    @NotNull
    public static final GrottoChat INSTANCE = new GrottoChat();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final class_5250 prefix;
    @NotNull
    private static final class_5250 grottoPrefix;
    @NotNull
    private static final class_5250 autoRoutesPrefix;
    @NotNull
    private static final class_5250 mhPrefix;

    private GrottoChat() {
    }

    @JvmStatic
    public static final void send(@NotNull class_2561 message) {
        Intrinsics.checkNotNullParameter((Object)message, (String)"message");
        if (GrottoChat.mc.field_1724 == null || GrottoChat.mc.field_1687 == null) {
            return;
        }
        GrottoChat.mc.field_1705.method_1743().method_1812((class_2561)class_2561.method_43473().method_10852((class_2561)prefix).method_10852(message));
    }

    private final void sendWithPrefix(class_2561 prefix, class_2561 message) {
        if (GrottoChat.mc.field_1724 == null || GrottoChat.mc.field_1687 == null) {
            return;
        }
        GrottoChat.mc.field_1705.method_1743().method_1812((class_2561)class_2561.method_43473().method_10852(prefix).method_10852(message));
    }

    @JvmStatic
    public static final void send(@NotNull String message) {
        Intrinsics.checkNotNullParameter((Object)message, (String)"message");
        class_5250 class_52502 = class_2561.method_43470((String)message);
        Intrinsics.checkNotNullExpressionValue((Object)class_52502, (String)"literal(...)");
        GrottoChat.send((class_2561)class_52502);
    }

    @JvmStatic
    public static final void grotto(@NotNull String message) {
        Intrinsics.checkNotNullParameter((Object)message, (String)"message");
        class_2561 class_25612 = (class_2561)grottoPrefix;
        class_5250 class_52502 = class_2561.method_43470((String)message);
        Intrinsics.checkNotNullExpressionValue((Object)class_52502, (String)"literal(...)");
        INSTANCE.sendWithPrefix(class_25612, (class_2561)class_52502);
    }

    @JvmStatic
    public static final void autoRoutes(@NotNull String message) {
        Intrinsics.checkNotNullParameter((Object)message, (String)"message");
        class_2561 class_25612 = (class_2561)autoRoutesPrefix;
        class_5250 class_52502 = class_2561.method_43470((String)message);
        Intrinsics.checkNotNullExpressionValue((Object)class_52502, (String)"literal(...)");
        INSTANCE.sendWithPrefix(class_25612, (class_2561)class_52502);
    }

    @JvmStatic
    public static final void mh(@NotNull String message) {
        Intrinsics.checkNotNullParameter((Object)message, (String)"message");
        class_2561 class_25612 = (class_2561)mhPrefix;
        class_5250 class_52502 = class_2561.method_43470((String)message);
        Intrinsics.checkNotNullExpressionValue((Object)class_52502, (String)"literal(...)");
        INSTANCE.sendWithPrefix(class_25612, (class_2561)class_52502);
    }

    @JvmStatic
    public static final void mhUsage(@NotNull String usage) {
        Intrinsics.checkNotNullParameter((Object)usage, (String)"usage");
        class_2561 class_25612 = (class_2561)mhPrefix;
        class_5250 class_52502 = class_2561.method_43470((String)"Usage: ").method_27692(class_124.field_1061).method_10852((class_2561)class_2561.method_43470((String)usage).method_27692(class_124.field_1080));
        Intrinsics.checkNotNullExpressionValue((Object)class_52502, (String)"append(...)");
        INSTANCE.sendWithPrefix(class_25612, (class_2561)class_52502);
    }

    @JvmStatic
    public static final void usage(@NotNull String usage) {
        Intrinsics.checkNotNullParameter((Object)usage, (String)"usage");
        class_5250 class_52502 = class_2561.method_43470((String)"Usage: ").method_27692(class_124.field_1061).method_10852((class_2561)class_2561.method_43470((String)usage).method_27692(class_124.field_1080));
        Intrinsics.checkNotNullExpressionValue((Object)class_52502, (String)"append(...)");
        GrottoChat.send((class_2561)class_52502);
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        class_5250 class_52502 = class_2561.method_43470((String)"[Grotto] ").method_27692(class_124.field_1075);
        Intrinsics.checkNotNullExpressionValue((Object)class_52502, (String)"withStyle(...)");
        prefix = class_52502;
        class_5250 class_52503 = class_2561.method_43470((String)"[GrottoScanner] ").method_27692(class_124.field_1076);
        Intrinsics.checkNotNullExpressionValue((Object)class_52503, (String)"withStyle(...)");
        grottoPrefix = class_52503;
        class_5250 class_52504 = class_2561.method_43470((String)"[AutoRoutes] ").method_27692(class_124.field_1062);
        Intrinsics.checkNotNullExpressionValue((Object)class_52504, (String)"withStyle(...)");
        autoRoutesPrefix = class_52504;
        class_5250 class_52505 = class_2561.method_43470((String)"[MiningHelper] ").method_27692(class_124.field_1075);
        Intrinsics.checkNotNullExpressionValue((Object)class_52505, (String)"withStyle(...)");
        mhPrefix = class_52505;
    }
}

