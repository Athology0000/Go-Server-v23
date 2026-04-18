/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden.managers;

import kotlin.Metadata;
import org.cobalt.internal.garden.managers.PestPrepSwapManager;
import org.cobalt.internal.garden.managers.PestTabListParser;
import org.cobalt.internal.garden.managers.TabListData;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0012\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u0015\u0010\t\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\t\u0010\nJ\u0015\u0010\r\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\u000b\u00a2\u0006\u0004\b\r\u0010\u000eR\"\u0010\u000f\u001a\u00020\u00068\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u000f\u0010\u0010\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\"\u0010\u0015\u001a\u00020\u00068\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0015\u0010\u0010\u001a\u0004\b\u0016\u0010\u0012\"\u0004\b\u0017\u0010\u0014R\"\u0010\u0018\u001a\u00020\u000b8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0018\u0010\u0019\u001a\u0004\b\u001a\u0010\u001b\"\u0004\b\u001c\u0010\u000e\u00a8\u0006\u001d"}, d2={"Lorg/cobalt/internal/garden/managers/PestManager;", "", "<init>", "()V", "", "reset", "", "threshold", "", "update", "(I)Z", "", "durationMs", "startCooldown", "(J)V", "lastAliveCount", "I", "getLastAliveCount", "()I", "setLastAliveCount", "(I)V", "lastCooldownSeconds", "getLastCooldownSeconds", "setLastCooldownSeconds", "cleaningCooldownUntil", "J", "getCleaningCooldownUntil", "()J", "setCleaningCooldownUntil", "cobalt"})
public final class PestManager {
    @NotNull
    public static final PestManager INSTANCE = new PestManager();
    private static volatile int lastAliveCount;
    private static volatile int lastCooldownSeconds;
    private static volatile long cleaningCooldownUntil;

    private PestManager() {
    }

    public final int getLastAliveCount() {
        return lastAliveCount;
    }

    public final void setLastAliveCount(int n) {
        lastAliveCount = n;
    }

    public final int getLastCooldownSeconds() {
        return lastCooldownSeconds;
    }

    public final void setLastCooldownSeconds(int n) {
        lastCooldownSeconds = n;
    }

    public final long getCleaningCooldownUntil() {
        return cleaningCooldownUntil;
    }

    public final void setCleaningCooldownUntil(long l) {
        cleaningCooldownUntil = l;
    }

    public final void reset() {
        lastAliveCount = 0;
        lastCooldownSeconds = 0;
        cleaningCooldownUntil = 0L;
    }

    public final boolean update(int threshold) {
        TabListData data = PestTabListParser.INSTANCE.parse();
        lastAliveCount = data.getAlivePests();
        lastCooldownSeconds = data.getCooldownSeconds();
        if (System.currentTimeMillis() < cleaningCooldownUntil) {
            return false;
        }
        return data.getAlivePests() >= threshold;
    }

    public final void startCooldown(long durationMs) {
        cleaningCooldownUntil = System.currentTimeMillis() + durationMs;
        PestPrepSwapManager.INSTANCE.reset();
    }
}

