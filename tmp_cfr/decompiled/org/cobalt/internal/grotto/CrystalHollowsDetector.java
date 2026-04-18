/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  kotlin.Metadata
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.StringsKt
 *  net.minecraft.class_124
 *  net.minecraft.class_310
 *  net.minecraft.class_634
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.grotto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Locale;
import java.util.regex.Pattern;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import net.minecraft.class_124;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.ChatEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.internal.grotto.GrottoChat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u0017\u0010\n\u001a\u00020\t2\u0006\u0010\b\u001a\u00020\u0007H\u0007\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0017\u0010\r\u001a\u00020\t2\u0006\u0010\b\u001a\u00020\fH\u0007\u00a2\u0006\u0004\b\r\u0010\u000eJ\u000f\u0010\u000f\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\u000f\u0010\u0003J\u000f\u0010\u0010\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\u0010\u0010\u0003J\u0019\u0010\u0013\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0011H\u0002\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0017\u0010\u0016\u001a\u00020\t2\u0006\u0010\u0015\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u0016\u0010\u0017R\u0014\u0010\u0019\u001a\u00020\u00188\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u001aR\u001c\u0010\u001d\u001a\n \u001c*\u0004\u0018\u00010\u001b0\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001d\u0010\u001eR\u0014\u0010 \u001a\u00020\u001f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b \u0010!R\u0016\u0010\"\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\"\u0010#R\u0016\u0010$\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b$\u0010#R\u0016\u0010&\u001a\u00020%8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b&\u0010'R\u0016\u0010(\u001a\u00020%8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b(\u0010'R\u0016\u0010)\u001a\u00020%8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b)\u0010'R\u0014\u0010*\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b*\u0010'R\u0014\u0010+\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b+\u0010'R\u0014\u0010,\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b,\u0010'R\u0018\u0010.\u001a\u0004\u0018\u00010-8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b.\u0010/\u00a8\u00060"}, d2={"Lorg/cobalt/internal/grotto/CrystalHollowsDetector;", "", "<init>", "()V", "", "isInCrystalHollows", "()Z", "Lorg/cobalt/api/event/impl/client/TickEvent$End;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$End;)V", "Lorg/cobalt/api/event/impl/client/ChatEvent$Receive;", "onChat", "(Lorg/cobalt/api/event/impl/client/ChatEvent$Receive;)V", "trySendLocraw", "resetState", "", "text", "safeLower", "(Ljava/lang/String;)Ljava/lang/String;", "inHollows", "announceState", "(Z)V", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Ljava/util/regex/Pattern;", "kotlin.jvm.PlatformType", "jsonPattern", "Ljava/util/regex/Pattern;", "Lcom/google/gson/JsonParser;", "parser", "Lcom/google/gson/JsonParser;", "inCrystalHollows", "Z", "locrawPending", "", "locrawPendingSinceMs", "J", "lastLocrawSentMs", "nextLocrawAllowedMs", "LOCRAW_JOIN_DELAY_MS", "LOCRAW_MIN_INTERVAL_MS", "LOCRAW_TIMEOUT_MS", "Lnet/minecraft/class_638;", "lastLevel", "Lnet/minecraft/class_638;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nCrystalHollowsDetector.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CrystalHollowsDetector.kt\norg/cobalt/internal/grotto/CrystalHollowsDetector\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,120:1\n1#2:121\n*E\n"})
public final class CrystalHollowsDetector {
    @NotNull
    public static final CrystalHollowsDetector INSTANCE = new CrystalHollowsDetector();
    @NotNull
    private static final class_310 mc;
    private static final Pattern jsonPattern;
    @NotNull
    private static final JsonParser parser;
    private static boolean inCrystalHollows;
    private static boolean locrawPending;
    private static long locrawPendingSinceMs;
    private static long lastLocrawSentMs;
    private static long nextLocrawAllowedMs;
    private static final long LOCRAW_JOIN_DELAY_MS = 2500L;
    private static final long LOCRAW_MIN_INTERVAL_MS = 12000L;
    private static final long LOCRAW_TIMEOUT_MS = 4000L;
    @Nullable
    private static class_638 lastLevel;

    private CrystalHollowsDetector() {
    }

    @JvmStatic
    public static final boolean isInCrystalHollows() {
        return inCrystalHollows;
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.End event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_638 level2 = CrystalHollowsDetector.mc.field_1687;
        if (level2 == null || CrystalHollowsDetector.mc.field_1724 == null) {
            this.resetState();
            return;
        }
        if (lastLevel != level2) {
            lastLevel = level2;
            this.resetState();
        }
        this.trySendLocraw();
    }

    @SubscribeEvent
    public final void onChat(@NotNull ChatEvent.Receive event) {
        boolean nowInHollows;
        Object $this$onChat_u24lambda_u240;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        String string = event.getMessage();
        if (string == null) {
            return;
        }
        String message = string;
        String raw = null;
        String string2 = class_124.method_539((String)message);
        if (string2 == null) {
            return;
        }
        raw = string2;
        if (!jsonPattern.matcher(raw = ((Object)StringsKt.trim((CharSequence)raw)).toString()).matches()) {
            return;
        }
        Object object = this;
        try {
            $this$onChat_u24lambda_u240 = object;
            boolean bl = false;
            $this$onChat_u24lambda_u240 = Result.constructor-impl((Object)parser.parse(raw).getAsJsonObject());
        }
        catch (Throwable throwable) {
            $this$onChat_u24lambda_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        object = $this$onChat_u24lambda_u240;
        JsonObject jsonObject = (JsonObject)(Result.isFailure-impl((Object)object) ? null : object);
        if (jsonObject == null) {
            return;
        }
        JsonObject obj = jsonObject;
        if (!(obj.has("server") || obj.has("mode") || obj.has("map") || obj.has("gametype"))) {
            return;
        }
        event.setCancelled(true);
        locrawPending = false;
        nextLocrawAllowedMs = System.currentTimeMillis() + 12000L;
        String mode = obj.has("mode") ? this.safeLower(obj.get("mode").getAsString()) : "";
        String map = obj.has("map") ? this.safeLower(obj.get("map").getAsString()) : "";
        boolean bl = nowInHollows = Intrinsics.areEqual((Object)mode, (Object)"crystal_hollows") || StringsKt.contains$default((CharSequence)map, (CharSequence)"crystal hollows", (boolean)false, (int)2, null);
        if (nowInHollows != inCrystalHollows) {
            inCrystalHollows = nowInHollows;
            this.announceState(nowInHollows);
        }
    }

    private final void trySendLocraw() {
        class_746 class_7462 = CrystalHollowsDetector.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        class_638 class_6382 = CrystalHollowsDetector.mc.field_1687;
        if (class_6382 == null) {
            return;
        }
        class_638 level2 = class_6382;
        if (mc.method_47392()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (inCrystalHollows) {
            return;
        }
        if (now < nextLocrawAllowedMs) {
            return;
        }
        if (locrawPending) {
            if (now - locrawPendingSinceMs > 4000L) {
                locrawPending = false;
                nextLocrawAllowedMs = now + 12000L;
            }
            return;
        }
        if (now - lastLocrawSentMs < 12000L) {
            return;
        }
        class_634 class_6342 = player.field_3944;
        if (class_6342 != null) {
            class_6342.method_45730("locraw");
        }
        lastLocrawSentMs = now;
        locrawPending = true;
        locrawPendingSinceMs = now;
    }

    private final void resetState() {
        long now = System.currentTimeMillis();
        inCrystalHollows = false;
        locrawPending = false;
        locrawPendingSinceMs = 0L;
        nextLocrawAllowedMs = now + 2500L;
    }

    private final String safeLower(String text) {
        String string;
        block3: {
            block2: {
                string = text;
                if (string == null || (string = ((Object)StringsKt.trim((CharSequence)string)).toString()) == null) break block2;
                String string2 = string.toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
                string = string2;
                if (string2 != null) break block3;
            }
            string = "";
        }
        return string;
    }

    private final void announceState(boolean inHollows) {
        if (CrystalHollowsDetector.mc.field_1724 == null) {
            return;
        }
        if (inHollows) {
            GrottoChat.grotto("Crystal Hollows detected - scanner enabled.");
        } else {
            GrottoChat.grotto("Left Crystal Hollows - scanner disabled.");
        }
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        jsonPattern = Pattern.compile("^\\{.+}$");
        parser = new JsonParser();
    }
}

