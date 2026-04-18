/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.JvmField
 *  kotlin.jvm.JvmStatic
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.util.player;

import kotlin.Metadata;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmStatic;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u001c\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0019\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0019\u0010\t\u001a\u00020\u00062\b\b\u0002\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\t\u0010\bJG\u0010\u0011\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u0010\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u000f\u0010\u0013\u001a\u00020\u0006H\u0007\u00a2\u0006\u0004\b\u0013\u0010\u0003R\u0016\u0010\u0014\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0006\n\u0004\b\u0014\u0010\u0015R\u0016\u0010\u0016\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0006\n\u0004\b\u0016\u0010\u0015R\u0016\u0010\u0017\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0006\n\u0004\b\u0017\u0010\u0015R\u0016\u0010\u0018\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0015R\u0016\u0010\u0019\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u0015R\u0016\u0010\u001a\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0006\n\u0004\b\u001a\u0010\u0015R\u0016\u0010\u001b\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0006\n\u0004\b\u001b\u0010\u0015R\u0016\u0010\u001c\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u0015R\u0016\u0010\u001d\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0006\n\u0004\b\u001d\u0010\u0015R\u0016\u0010\u001e\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u0015R\u0016\u0010\u001f\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0006\n\u0004\b\u001f\u0010\u0015R\u0016\u0010 \u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0006\n\u0004\b \u0010\u0015R\u0016\u0010!\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0006\n\u0004\b!\u0010\u0015\u00a8\u0006\""}, d2={"Lorg/cobalt/api/util/player/MovementManager;", "", "<init>", "()V", "", "state", "", "setLookLock", "(Z)V", "setMovementLock", "forward", "backward", "left", "right", "jump", "shift", "sprint", "setForcedMovement", "(ZZZZZZZ)V", "clearForcedMovement", "isLookLocked", "Z", "isMovementLocked", "hasForcedMovement", "forcedForward", "forcedBackward", "forcedLeft", "forcedRight", "forcedJump", "forcedShift", "forcedSprint", "forcedAttack", "forcedUse", "forcedActionsEnabled", "cobalt"})
public final class MovementManager {
    @NotNull
    public static final MovementManager INSTANCE = new MovementManager();
    @JvmField
    public static volatile boolean isLookLocked;
    @JvmField
    public static volatile boolean isMovementLocked;
    @JvmField
    public static volatile boolean hasForcedMovement;
    @JvmField
    public static volatile boolean forcedForward;
    @JvmField
    public static volatile boolean forcedBackward;
    @JvmField
    public static volatile boolean forcedLeft;
    @JvmField
    public static volatile boolean forcedRight;
    @JvmField
    public static volatile boolean forcedJump;
    @JvmField
    public static volatile boolean forcedShift;
    @JvmField
    public static volatile boolean forcedSprint;
    @JvmField
    public static volatile boolean forcedAttack;
    @JvmField
    public static volatile boolean forcedUse;
    @JvmField
    public static volatile boolean forcedActionsEnabled;

    private MovementManager() {
    }

    @JvmStatic
    public static final void setLookLock(boolean state) {
        isLookLocked = state;
    }

    public static /* synthetic */ void setLookLock$default(boolean bl, int n, Object object) {
        if ((n & 1) != 0) {
            bl = true;
        }
        MovementManager.setLookLock(bl);
    }

    @JvmStatic
    public static final void setMovementLock(boolean state) {
        isMovementLocked = state;
        if (!state) {
            MovementManager.clearForcedMovement();
        }
    }

    public static /* synthetic */ void setMovementLock$default(boolean bl, int n, Object object) {
        if ((n & 1) != 0) {
            bl = true;
        }
        MovementManager.setMovementLock(bl);
    }

    @JvmStatic
    public static final void setForcedMovement(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean shift, boolean sprint) {
        forcedForward = forward;
        forcedBackward = backward;
        forcedLeft = left;
        forcedRight = right;
        forcedJump = jump;
        forcedShift = shift;
        forcedSprint = sprint;
        hasForcedMovement = true;
    }

    @JvmStatic
    public static final void clearForcedMovement() {
        hasForcedMovement = false;
        forcedForward = false;
        forcedBackward = false;
        forcedLeft = false;
        forcedRight = false;
        forcedJump = false;
        forcedShift = false;
        forcedSprint = false;
        forcedAttack = false;
        forcedUse = false;
        forcedActionsEnabled = false;
    }
}

