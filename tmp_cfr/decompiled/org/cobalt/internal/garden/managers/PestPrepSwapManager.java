/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden.managers;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\t\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\r\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0007\u0010\bJ\r\u0010\t\u001a\u00020\u0004\u00a2\u0006\u0004\b\t\u0010\u0003J\u0015\u0010\f\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\n\u00a2\u0006\u0004\b\f\u0010\rJ\r\u0010\u000e\u001a\u00020\u0006\u00a2\u0006\u0004\b\u000e\u0010\bJ\r\u0010\u000f\u001a\u00020\u0004\u00a2\u0006\u0004\b\u000f\u0010\u0003J\r\u0010\u0010\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0003R\"\u0010\u0011\u001a\u00020\u00068\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0011\u0010\u0012\u001a\u0004\b\u0013\u0010\b\"\u0004\b\u0014\u0010\u0015R\u0016\u0010\u0016\u001a\u00020\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0016\u0010\u0012R\u0016\u0010\u0018\u001a\u00020\u00178\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0019\u00a8\u0006\u001a"}, d2={"Lorg/cobalt/internal/garden/managers/PestPrepSwapManager;", "", "<init>", "()V", "", "reset", "", "isRunning", "()Z", "markActive", "", "cooldownSeconds", "shouldPrepSwap", "(I)Z", "markStarted", "markDone", "markFailed", "swapDone", "Z", "getSwapDone", "setSwapDone", "(Z)V", "swapRunning", "", "activeSince", "J", "cobalt"})
public final class PestPrepSwapManager {
    @NotNull
    public static final PestPrepSwapManager INSTANCE = new PestPrepSwapManager();
    private static volatile boolean swapDone;
    private static volatile boolean swapRunning;
    private static volatile long activeSince;

    private PestPrepSwapManager() {
    }

    public final boolean getSwapDone() {
        return swapDone;
    }

    public final void setSwapDone(boolean bl) {
        swapDone = bl;
    }

    public final void reset() {
        swapDone = false;
        swapRunning = false;
        activeSince = 0L;
    }

    public final boolean isRunning() {
        return swapRunning;
    }

    public final void markActive() {
        if (activeSince == 0L) {
            activeSince = System.currentTimeMillis();
        }
    }

    public final boolean shouldPrepSwap(int cooldownSeconds) {
        if (swapDone || swapRunning) {
            return false;
        }
        boolean bl = 1 <= cooldownSeconds ? cooldownSeconds < 141 : false;
        if (bl) {
            return true;
        }
        return cooldownSeconds == 0 && activeSince > 0L && System.currentTimeMillis() - activeSince >= 140000L;
    }

    public final boolean markStarted() {
        if (swapDone || swapRunning) {
            return false;
        }
        swapRunning = true;
        return true;
    }

    public final void markDone() {
        swapDone = true;
        swapRunning = false;
    }

    public final void markFailed() {
        swapRunning = false;
    }
}

