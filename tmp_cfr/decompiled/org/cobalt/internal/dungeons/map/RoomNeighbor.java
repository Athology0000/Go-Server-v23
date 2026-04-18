/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.dungeons.map;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.internal.dungeons.map.GridComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\bJ$\u0010\n\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\n\u0010\u000bJ\u001b\u0010\u000e\u001a\u00020\r2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0011\u0010\u0011\u001a\u00020\u0010H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0011\u0010\u0014\u001a\u00020\u0013H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0014\u0010\u0015R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0016\u001a\u0004\b\u0017\u0010\bR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0016\u001a\u0004\b\u0018\u0010\b\u00a8\u0006\u0019"}, d2={"Lorg/cobalt/internal/dungeons/map/RoomNeighbor;", "", "Lorg/cobalt/internal/dungeons/map/GridComponent;", "room", "door", "<init>", "(Lorg/cobalt/internal/dungeons/map/GridComponent;Lorg/cobalt/internal/dungeons/map/GridComponent;)V", "component1", "()Lorg/cobalt/internal/dungeons/map/GridComponent;", "component2", "copy", "(Lorg/cobalt/internal/dungeons/map/GridComponent;Lorg/cobalt/internal/dungeons/map/GridComponent;)Lorg/cobalt/internal/dungeons/map/RoomNeighbor;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "Lorg/cobalt/internal/dungeons/map/GridComponent;", "getRoom", "getDoor", "cobalt"})
public final class RoomNeighbor {
    @NotNull
    private final GridComponent room;
    @NotNull
    private final GridComponent door;

    public RoomNeighbor(@NotNull GridComponent room, @NotNull GridComponent door) {
        Intrinsics.checkNotNullParameter((Object)room, (String)"room");
        Intrinsics.checkNotNullParameter((Object)door, (String)"door");
        this.room = room;
        this.door = door;
    }

    @NotNull
    public final GridComponent getRoom() {
        return this.room;
    }

    @NotNull
    public final GridComponent getDoor() {
        return this.door;
    }

    @NotNull
    public final GridComponent component1() {
        return this.room;
    }

    @NotNull
    public final GridComponent component2() {
        return this.door;
    }

    @NotNull
    public final RoomNeighbor copy(@NotNull GridComponent room, @NotNull GridComponent door) {
        Intrinsics.checkNotNullParameter((Object)room, (String)"room");
        Intrinsics.checkNotNullParameter((Object)door, (String)"door");
        return new RoomNeighbor(room, door);
    }

    public static /* synthetic */ RoomNeighbor copy$default(RoomNeighbor roomNeighbor, GridComponent gridComponent, GridComponent gridComponent2, int n, Object object) {
        if ((n & 1) != 0) {
            gridComponent = roomNeighbor.room;
        }
        if ((n & 2) != 0) {
            gridComponent2 = roomNeighbor.door;
        }
        return roomNeighbor.copy(gridComponent, gridComponent2);
    }

    @NotNull
    public String toString() {
        return "RoomNeighbor(room=" + this.room + ", door=" + this.door + ")";
    }

    public int hashCode() {
        int result = this.room.hashCode();
        result = result * 31 + this.door.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RoomNeighbor)) {
            return false;
        }
        RoomNeighbor roomNeighbor = (RoomNeighbor)other;
        if (!Intrinsics.areEqual((Object)this.room, (Object)roomNeighbor.room)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.door, (Object)roomNeighbor.door);
    }
}

