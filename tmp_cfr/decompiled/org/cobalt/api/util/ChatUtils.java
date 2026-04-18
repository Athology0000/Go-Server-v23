/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.math.MathKt
 *  net.minecraft.class_124
 *  net.minecraft.class_2561
 *  net.minecraft.class_2583
 *  net.minecraft.class_310
 *  net.minecraft.class_5250
 *  net.minecraft.class_5251
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.util;

import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_310;
import net.minecraft.class_5250;
import net.minecraft.class_5251;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\t\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\t\u0010\bJ'\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\n\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000bH\u0007\u00a2\u0006\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0012\u001a\u00020\u00118\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0013R\u0016\u0010\u0014\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0014\u0010\u0015R\u0014\u0010\u0016\u001a\u00020\u000e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0016\u0010\u0017R\u0014\u0010\u0018\u001a\u00020\u000e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0017\u00a8\u0006\u0019"}, d2={"Lorg/cobalt/api/util/ChatUtils;", "", "<init>", "()V", "", "message", "", "sendDebug", "(Ljava/lang/String;)V", "sendMessage", "text", "", "startRgb", "endRgb", "Lnet/minecraft/class_5250;", "buildGradient", "(Ljava/lang/String;II)Lnet/minecraft/class_5250;", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "lastMessage", "Ljava/lang/String;", "prefix", "Lnet/minecraft/class_5250;", "debugPrefix", "cobalt"})
public final class ChatUtils {
    @NotNull
    public static final ChatUtils INSTANCE = new ChatUtils();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static String lastMessage;
    @NotNull
    private static final class_5250 prefix;
    @NotNull
    private static final class_5250 debugPrefix;

    private ChatUtils() {
    }

    @JvmStatic
    public static final void sendDebug(@NotNull String message) {
        Intrinsics.checkNotNullParameter((Object)message, (String)"message");
        if (ChatUtils.mc.field_1724 == null || ChatUtils.mc.field_1687 == null) {
            return;
        }
        if (Intrinsics.areEqual((Object)message, (Object)lastMessage)) {
            return;
        }
        ChatUtils.mc.field_1705.method_1743().method_1812((class_2561)class_2561.method_43473().method_10852((class_2561)debugPrefix).method_10852((class_2561)class_2561.method_43470((String)(class_124.field_1070 + message))));
        lastMessage = message;
    }

    @JvmStatic
    public static final void sendMessage(@NotNull String message) {
        Intrinsics.checkNotNullParameter((Object)message, (String)"message");
        if (ChatUtils.mc.field_1724 == null || ChatUtils.mc.field_1687 == null) {
            return;
        }
        ChatUtils.mc.field_1705.method_1743().method_1812((class_2561)class_2561.method_43473().method_10852((class_2561)prefix).method_10852((class_2561)class_2561.method_43470((String)(class_124.field_1070 + message))));
    }

    @JvmStatic
    @NotNull
    public static final class_5250 buildGradient(@NotNull String text, int startRgb, int endRgb) {
        Intrinsics.checkNotNullParameter((Object)text, (String)"text");
        class_5250 class_52502 = class_2561.method_43473();
        Intrinsics.checkNotNullExpressionValue((Object)class_52502, (String)"empty(...)");
        class_5250 result = class_52502;
        int length = text.length();
        if (length <= 1) {
            class_5250 class_52503 = class_2561.method_43470((String)text).method_10862(class_2583.field_24360.method_27703(class_5251.method_27717((int)startRgb)));
            Intrinsics.checkNotNullExpressionValue((Object)class_52503, (String)"setStyle(...)");
            return class_52503;
        }
        int sr = startRgb >> 16 & 0xFF;
        int sg = startRgb >> 8 & 0xFF;
        int sb = startRgb & 0xFF;
        int er = endRgb >> 16 & 0xFF;
        int eg = endRgb >> 8 & 0xFF;
        int eb = endRgb & 0xFF;
        int n = ((CharSequence)text).length();
        for (int i = 0; i < n; ++i) {
            class_5250 charText;
            double ratio = (double)i / (double)(length - 1);
            int r = MathKt.roundToInt((double)((double)sr + ratio * (double)(er - sr)));
            int g = MathKt.roundToInt((double)((double)sg + ratio * (double)(eg - sg)));
            int b = MathKt.roundToInt((double)((double)sb + ratio * (double)(eb - sb)));
            int rgb = r << 16 | g << 8 | b;
            Intrinsics.checkNotNullExpressionValue((Object)class_2561.method_43470((String)String.valueOf(text.charAt(i))).method_10862(class_2583.field_24360.method_27703(class_5251.method_27717((int)rgb))), (String)"setStyle(...)");
            result.method_10852((class_2561)charText);
        }
        return result;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        lastMessage = "";
        class_5250 class_52502 = class_2561.method_43470((String)(class_124.field_1063 + "[")).method_10852((class_2561)ChatUtils.buildGradient("Cobalt", 5025232, 11729407)).method_10852((class_2561)class_2561.method_43470((String)(class_124.field_1063 + "] ")));
        Intrinsics.checkNotNullExpressionValue((Object)class_52502, (String)"append(...)");
        prefix = class_52502;
        class_5250 class_52503 = class_2561.method_43470((String)(class_124.field_1063 + "[")).method_10852((class_2561)ChatUtils.buildGradient("Cobalt Debug", 3577974, 7471006)).method_10852((class_2561)class_2561.method_43470((String)(class_124.field_1063 + "] ")));
        Intrinsics.checkNotNullExpressionValue((Object)class_52503, (String)"append(...)");
        debugPrefix = class_52503;
    }
}

