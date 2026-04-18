/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.pathfinder.jni;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000b\n\u0002\b\u0012\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\t\b\u0082\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u0012\u0006\u0010\u0007\u001a\u00020\u0002\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\u000bJ\u0010\u0010\r\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\u000bJ\u0010\u0010\u000e\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\u000bJ\u0010\u0010\u000f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\u000bJB\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u00022\b\b\u0002\u0010\u0007\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u001b\u0010\u0013\u001a\u00020\u00022\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0011\u0010\u0016\u001a\u00020\u0015H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0019\u001a\u00020\u0018H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0019\u0010\u001aR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001b\u001a\u0004\b\u001c\u0010\u000bR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u001b\u001a\u0004\b\u001d\u0010\u000bR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001b\u001a\u0004\b\u001e\u0010\u000bR\u0017\u0010\u0006\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001b\u001a\u0004\b\u001f\u0010\u000bR\u0017\u0010\u0007\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u001b\u001a\u0004\b \u0010\u000b\u00a8\u0006!"}, d2={"Lorg/cobalt/api/pathfinder/jni/MovementInputs;", "", "", "forward", "backward", "left", "right", "sprint", "<init>", "(ZZZZZ)V", "component1", "()Z", "component2", "component3", "component4", "component5", "copy", "(ZZZZZ)Lorg/cobalt/api/pathfinder/jni/MovementInputs;", "other", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "Z", "getForward", "getBackward", "getLeft", "getRight", "getSprint", "cobalt"})
final class MovementInputs {
    private final boolean forward;
    private final boolean backward;
    private final boolean left;
    private final boolean right;
    private final boolean sprint;

    public MovementInputs(boolean forward, boolean backward, boolean left, boolean right, boolean sprint) {
        this.forward = forward;
        this.backward = backward;
        this.left = left;
        this.right = right;
        this.sprint = sprint;
    }

    public final boolean getForward() {
        return this.forward;
    }

    public final boolean getBackward() {
        return this.backward;
    }

    public final boolean getLeft() {
        return this.left;
    }

    public final boolean getRight() {
        return this.right;
    }

    public final boolean getSprint() {
        return this.sprint;
    }

    public final boolean component1() {
        return this.forward;
    }

    public final boolean component2() {
        return this.backward;
    }

    public final boolean component3() {
        return this.left;
    }

    public final boolean component4() {
        return this.right;
    }

    public final boolean component5() {
        return this.sprint;
    }

    @NotNull
    public final MovementInputs copy(boolean forward, boolean backward, boolean left, boolean right, boolean sprint) {
        return new MovementInputs(forward, backward, left, right, sprint);
    }

    public static /* synthetic */ MovementInputs copy$default(MovementInputs movementInputs, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, int n, Object object) {
        if ((n & 1) != 0) {
            bl = movementInputs.forward;
        }
        if ((n & 2) != 0) {
            bl2 = movementInputs.backward;
        }
        if ((n & 4) != 0) {
            bl3 = movementInputs.left;
        }
        if ((n & 8) != 0) {
            bl4 = movementInputs.right;
        }
        if ((n & 0x10) != 0) {
            bl5 = movementInputs.sprint;
        }
        return movementInputs.copy(bl, bl2, bl3, bl4, bl5);
    }

    @NotNull
    public String toString() {
        return "MovementInputs(forward=" + this.forward + ", backward=" + this.backward + ", left=" + this.left + ", right=" + this.right + ", sprint=" + this.sprint + ")";
    }

    public int hashCode() {
        int result = Boolean.hashCode(this.forward);
        result = result * 31 + Boolean.hashCode(this.backward);
        result = result * 31 + Boolean.hashCode(this.left);
        result = result * 31 + Boolean.hashCode(this.right);
        result = result * 31 + Boolean.hashCode(this.sprint);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MovementInputs)) {
            return false;
        }
        MovementInputs movementInputs = (MovementInputs)other;
        if (this.forward != movementInputs.forward) {
            return false;
        }
        if (this.backward != movementInputs.backward) {
            return false;
        }
        if (this.left != movementInputs.left) {
            return false;
        }
        if (this.right != movementInputs.right) {
            return false;
        }
        return this.sprint == movementInputs.sprint;
    }
}

