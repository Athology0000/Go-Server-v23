/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.combat;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.internal.combat.CombatPatrolPointType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\t\b\u0086\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\u000bJ\u0010\u0010\r\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\u000bJ\u0010\u0010\u000e\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\u000fJ8\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0007\u001a\u00020\u0006H\u00c6\u0001\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u001b\u0010\u0014\u001a\u00020\u00132\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u0011\u0010\u0016\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0016\u0010\u000bJ\u0011\u0010\u0018\u001a\u00020\u0017H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0018\u0010\u0019R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001a\u001a\u0004\b\u001b\u0010\u000bR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u001a\u001a\u0004\b\u001c\u0010\u000bR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001a\u001a\u0004\b\u001d\u0010\u000bR\u0017\u0010\u0007\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u001e\u001a\u0004\b\u001f\u0010\u000f\u00a8\u0006 "}, d2={"Lorg/cobalt/internal/combat/CombatPatrolPoint;", "", "", "x", "y", "z", "Lorg/cobalt/internal/combat/CombatPatrolPointType;", "type", "<init>", "(IIILorg/cobalt/internal/combat/CombatPatrolPointType;)V", "component1", "()I", "component2", "component3", "component4", "()Lorg/cobalt/internal/combat/CombatPatrolPointType;", "copy", "(IIILorg/cobalt/internal/combat/CombatPatrolPointType;)Lorg/cobalt/internal/combat/CombatPatrolPoint;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getX", "getY", "getZ", "Lorg/cobalt/internal/combat/CombatPatrolPointType;", "getType", "cobalt"})
public final class CombatPatrolPoint {
    private final int x;
    private final int y;
    private final int z;
    @NotNull
    private final CombatPatrolPointType type;

    public CombatPatrolPoint(int x, int y, int z, @NotNull CombatPatrolPointType type) {
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
    }

    public final int getX() {
        return this.x;
    }

    public final int getY() {
        return this.y;
    }

    public final int getZ() {
        return this.z;
    }

    @NotNull
    public final CombatPatrolPointType getType() {
        return this.type;
    }

    public final int component1() {
        return this.x;
    }

    public final int component2() {
        return this.y;
    }

    public final int component3() {
        return this.z;
    }

    @NotNull
    public final CombatPatrolPointType component4() {
        return this.type;
    }

    @NotNull
    public final CombatPatrolPoint copy(int x, int y, int z, @NotNull CombatPatrolPointType type) {
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        return new CombatPatrolPoint(x, y, z, type);
    }

    public static /* synthetic */ CombatPatrolPoint copy$default(CombatPatrolPoint combatPatrolPoint, int n, int n2, int n3, CombatPatrolPointType combatPatrolPointType, int n4, Object object) {
        if ((n4 & 1) != 0) {
            n = combatPatrolPoint.x;
        }
        if ((n4 & 2) != 0) {
            n2 = combatPatrolPoint.y;
        }
        if ((n4 & 4) != 0) {
            n3 = combatPatrolPoint.z;
        }
        if ((n4 & 8) != 0) {
            combatPatrolPointType = combatPatrolPoint.type;
        }
        return combatPatrolPoint.copy(n, n2, n3, combatPatrolPointType);
    }

    @NotNull
    public String toString() {
        return "CombatPatrolPoint(x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", type=" + this.type + ")";
    }

    public int hashCode() {
        int result = Integer.hashCode(this.x);
        result = result * 31 + Integer.hashCode(this.y);
        result = result * 31 + Integer.hashCode(this.z);
        result = result * 31 + this.type.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CombatPatrolPoint)) {
            return false;
        }
        CombatPatrolPoint combatPatrolPoint = (CombatPatrolPoint)other;
        if (this.x != combatPatrolPoint.x) {
            return false;
        }
        if (this.y != combatPatrolPoint.y) {
            return false;
        }
        if (this.z != combatPatrolPoint.z) {
            return false;
        }
        return this.type == combatPatrolPoint.type;
    }
}

