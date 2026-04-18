/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden.managers;

import kotlin.Metadata;
import org.cobalt.internal.garden.managers.PestTabListParser;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b\u0007\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\r\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0003R\"\u0010\b\u001a\u00020\u00078\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\b\u0010\t\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\"\u0010\u000f\u001a\u00020\u000e8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u000f\u0010\u0010\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014\u00a8\u0006\u0015"}, d2={"Lorg/cobalt/internal/garden/managers/PestBonusManager;", "", "<init>", "()V", "", "reset", "update", "", "bonusActive", "Z", "getBonusActive", "()Z", "setBonusActive", "(Z)V", "", "lastChecked", "J", "getLastChecked", "()J", "setLastChecked", "(J)V", "cobalt"})
public final class PestBonusManager {
    @NotNull
    public static final PestBonusManager INSTANCE = new PestBonusManager();
    private static volatile boolean bonusActive;
    private static volatile long lastChecked;

    private PestBonusManager() {
    }

    public final boolean getBonusActive() {
        return bonusActive;
    }

    public final void setBonusActive(boolean bl) {
        bonusActive = bl;
    }

    public final long getLastChecked() {
        return lastChecked;
    }

    public final void setLastChecked(long l) {
        lastChecked = l;
    }

    public final void reset() {
        bonusActive = false;
        lastChecked = 0L;
    }

    public final void update() {
        long now = System.currentTimeMillis();
        if (now - lastChecked < 5000L) {
            return;
        }
        lastChecked = now;
        bonusActive = PestTabListParser.INSTANCE.parse().getBonusActive();
    }
}

