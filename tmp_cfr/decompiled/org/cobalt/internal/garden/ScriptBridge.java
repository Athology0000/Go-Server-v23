/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.garden;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0015\u0010\t\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\t\u0010\bJ\u0015\u0010\n\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\n\u0010\bJ\u0015\u0010\u000b\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u000b\u0010\bJ\r\u0010\r\u001a\u00020\f\u00a2\u0006\u0004\b\r\u0010\u000eJ\r\u0010\u000f\u001a\u00020\u0006\u00a2\u0006\u0004\b\u000f\u0010\u0003J\u0017\u0010\u0012\u001a\u00020\u00062\b\b\u0002\u0010\u0011\u001a\u00020\u0010\u00a2\u0006\u0004\b\u0012\u0010\u0013J\r\u0010\u0014\u001a\u00020\f\u00a2\u0006\u0004\b\u0014\u0010\u000eJ\r\u0010\u0015\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0015\u0010\u0003J\r\u0010\u0016\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0016\u0010\u0003J\r\u0010\u0017\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0017\u0010\u0003J\u0017\u0010\u0018\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u0018\u0010\bJ\u0017\u0010\u001a\u001a\u00020\u00062\u0006\u0010\u0019\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u001a\u0010\bJ\u0017\u0010\u001c\u001a\u00020\u00062\u0006\u0010\u001b\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u001c\u0010\bR\u0014\u0010 \u001a\u00020\u001d8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u001e\u0010\u001fR\u0016\u0010!\u001a\u00020\u00108\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b!\u0010\"R\u0018\u0010#\u001a\u0004\u0018\u00010\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b#\u0010$\u00a8\u0006%"}, d2={"Lorg/cobalt/internal/garden/ScriptBridge;", "", "<init>", "()V", "", "script", "", "startFarming", "(Ljava/lang/String;)V", "startPestScript", "startVisitorScript", "startReturnScript", "", "hasLastStartedScript", "()Z", "restartLastScript", "", "graceMs", "markIntentionalStop", "(J)V", "wasIntentionalStopRecently", "stopScript", "setSpawn", "warpGarden", "startScript", "msg", "sendChat", "cmd", "sendCommand", "Lnet/minecraft/class_310;", "getMc", "()Lnet/minecraft/class_310;", "mc", "intentionalStopUntilMs", "J", "lastStartedScriptCommand", "Ljava/lang/String;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nScriptBridge.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ScriptBridge.kt\norg/cobalt/internal/garden/ScriptBridge\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,66:1\n1#2:67\n*E\n"})
public final class ScriptBridge {
    @NotNull
    public static final ScriptBridge INSTANCE = new ScriptBridge();
    private static volatile long intentionalStopUntilMs;
    @Nullable
    private static volatile String lastStartedScriptCommand;

    private ScriptBridge() {
    }

    private final class_310 getMc() {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        return class_3102;
    }

    public final void startFarming(@NotNull String script) {
        Intrinsics.checkNotNullParameter((Object)script, (String)"script");
        this.startScript(script);
    }

    public final void startPestScript(@NotNull String script) {
        Intrinsics.checkNotNullParameter((Object)script, (String)"script");
        this.startScript(script);
    }

    public final void startVisitorScript(@NotNull String script) {
        Intrinsics.checkNotNullParameter((Object)script, (String)"script");
        this.startScript(script);
    }

    public final void startReturnScript(@NotNull String script) {
        Intrinsics.checkNotNullParameter((Object)script, (String)"script");
        this.startScript(script);
    }

    public final boolean hasLastStartedScript() {
        CharSequence charSequence = lastStartedScriptCommand;
        return !(charSequence == null || StringsKt.isBlank((CharSequence)charSequence));
    }

    public final void restartLastScript() {
        block1: {
            String string;
            String string2 = lastStartedScriptCommand;
            if (string2 == null) break block1;
            String it = string = string2;
            boolean bl = false;
            string2 = !StringsKt.isBlank((CharSequence)it) ? string : null;
            if (string2 != null) {
                String p0 = string2;
                boolean bl2 = false;
                this.sendChat(p0);
            }
        }
    }

    public final void markIntentionalStop(long graceMs) {
        intentionalStopUntilMs = System.currentTimeMillis() + RangesKt.coerceAtLeast((long)graceMs, (long)0L);
    }

    public static /* synthetic */ void markIntentionalStop$default(ScriptBridge scriptBridge, long l, int n, Object object) {
        if ((n & 1) != 0) {
            l = 8000L;
        }
        scriptBridge.markIntentionalStop(l);
    }

    public final boolean wasIntentionalStopRecently() {
        return System.currentTimeMillis() < intentionalStopUntilMs;
    }

    public final void stopScript() {
        ScriptBridge.markIntentionalStop$default(this, 0L, 1, null);
        this.getMc().execute(ScriptBridge::stopScript$lambda$0);
        this.sendChat(".ez-stopscript");
    }

    public final void setSpawn() {
        this.sendCommand("setspawn");
    }

    public final void warpGarden() {
        this.sendCommand("warp garden");
    }

    private final void startScript(String script) {
        if (StringsKt.isBlank((CharSequence)script)) {
            return;
        }
        String command = ".ez-startscript " + script;
        lastStartedScriptCommand = command;
        this.sendChat(command);
    }

    private final void sendChat(String msg) {
        this.getMc().execute(() -> ScriptBridge.sendChat$lambda$0(msg));
    }

    private final void sendCommand(String cmd) {
        this.getMc().execute(() -> ScriptBridge.sendCommand$lambda$0(cmd));
    }

    private static final void stopScript$lambda$0() {
        ScriptBridge.INSTANCE.getMc().field_1690.field_1894.method_23481(false);
        ScriptBridge.INSTANCE.getMc().field_1690.field_1881.method_23481(false);
        ScriptBridge.INSTANCE.getMc().field_1690.field_1913.method_23481(false);
        ScriptBridge.INSTANCE.getMc().field_1690.field_1849.method_23481(false);
        ScriptBridge.INSTANCE.getMc().field_1690.field_1903.method_23481(false);
        ScriptBridge.INSTANCE.getMc().field_1690.field_1832.method_23481(false);
    }

    private static final void sendChat$lambda$0(String $msg) {
        block0: {
            class_746 class_7462 = ScriptBridge.INSTANCE.getMc().field_1724;
            if (class_7462 == null || (class_7462 = class_7462.field_3944) == null) break block0;
            class_7462.method_45729($msg);
        }
    }

    private static final void sendCommand$lambda$0(String $cmd) {
        block0: {
            class_746 class_7462 = ScriptBridge.INSTANCE.getMc().field_1724;
            if (class_7462 == null || (class_7462 = class_7462.field_3944) == null) break block0;
            class_7462.method_45730($cmd);
        }
    }
}

