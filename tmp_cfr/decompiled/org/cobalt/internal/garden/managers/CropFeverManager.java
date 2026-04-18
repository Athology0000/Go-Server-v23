/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden.managers;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\t\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u0015\u0010\b\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\b\u0010\tJ\r\u0010\n\u001a\u00020\u0004\u00a2\u0006\u0004\b\n\u0010\u0003J\r\u0010\f\u001a\u00020\u000b\u00a2\u0006\u0004\b\f\u0010\rR\"\u0010\u000e\u001a\u00020\u000b8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u000e\u0010\u000f\u001a\u0004\b\u0010\u0010\r\"\u0004\b\u0011\u0010\u0012R\"\u0010\u0014\u001a\u00020\u00138\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0014\u0010\u0015\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u0014\u0010\u001a\u001a\u00020\u00138\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001a\u0010\u0015\u00a8\u0006\u001b"}, d2={"Lorg/cobalt/internal/garden/managers/CropFeverManager;", "", "<init>", "()V", "", "reset", "", "message", "onChatMessage", "(Ljava/lang/String;)V", "update", "", "shouldDelay", "()Z", "feverActive", "Z", "getFeverActive", "setFeverActive", "(Z)V", "", "feverDetectedAt", "J", "getFeverDetectedAt", "()J", "setFeverDetectedAt", "(J)V", "FEVER_TIMEOUT_MS", "cobalt"})
public final class CropFeverManager {
    @NotNull
    public static final CropFeverManager INSTANCE = new CropFeverManager();
    private static volatile boolean feverActive;
    private static volatile long feverDetectedAt;
    private static final long FEVER_TIMEOUT_MS = 65000L;

    private CropFeverManager() {
    }

    public final boolean getFeverActive() {
        return feverActive;
    }

    public final void setFeverActive(boolean bl) {
        feverActive = bl;
    }

    public final long getFeverDetectedAt() {
        return feverDetectedAt;
    }

    public final void setFeverDetectedAt(long l) {
        feverDetectedAt = l;
    }

    public final void reset() {
        feverActive = false;
        feverDetectedAt = 0L;
    }

    public final void onChatMessage(@NotNull String message) {
        Intrinsics.checkNotNullParameter((Object)message, (String)"message");
        if (StringsKt.contains((CharSequence)message, (CharSequence)"CROP FEVER", (boolean)true)) {
            feverActive = true;
            feverDetectedAt = System.currentTimeMillis();
        }
    }

    public final void update() {
        if (feverActive && System.currentTimeMillis() - feverDetectedAt > 65000L) {
            feverActive = false;
        }
    }

    public final boolean shouldDelay() {
        return feverActive;
    }
}

