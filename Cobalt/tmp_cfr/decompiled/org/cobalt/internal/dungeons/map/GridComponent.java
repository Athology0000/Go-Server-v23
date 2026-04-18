/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_2338
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.dungeons.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_2338;
import org.cobalt.internal.dungeons.map.RoomNeighbor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0086\b\u0018\u0000 &2\u00020\u0001:\u0001&B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J\r\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\r\u0010\n\u001a\u00020\u0007\u00a2\u0006\u0004\b\n\u0010\tJ\r\u0010\u000b\u001a\u00020\u0007\u00a2\u0006\u0004\b\u000b\u0010\tJ\r\u0010\f\u001a\u00020\u0002\u00a2\u0006\u0004\b\f\u0010\rJ\r\u0010\u000e\u001a\u00020\u0002\u00a2\u0006\u0004\b\u000e\u0010\rJ\r\u0010\u0010\u001a\u00020\u000f\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0013\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00000\u0012\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0013\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00000\u0012\u00a2\u0006\u0004\b\u0015\u0010\u0014J\u0013\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00160\u0012\u00a2\u0006\u0004\b\u0017\u0010\u0014J\u0010\u0010\u0018\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0018\u0010\rJ\u0010\u0010\u0019\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0019\u0010\rJ$\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u001b\u0010\u001d\u001a\u00020\u00072\b\u0010\u001c\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u001d\u0010\u001eJ\u0011\u0010\u001f\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001f\u0010\rJ\u0011\u0010!\u001a\u00020 H\u00d6\u0081\u0004\u00a2\u0006\u0004\b!\u0010\"R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010#\u001a\u0004\b$\u0010\rR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010#\u001a\u0004\b%\u0010\r\u00a8\u0006'"}, d2={"Lorg/cobalt/internal/dungeons/map/GridComponent;", "", "", "x", "z", "<init>", "(II)V", "", "isValid", "()Z", "isRoom", "isDoor", "roomIndex", "()I", "doorIndex", "Lnet/minecraft/class_2338;", "toWorldCenter", "()Lnet/minecraft/class_2338;", "", "neighboringRooms", "()Ljava/util/List;", "neighboringDoors", "Lorg/cobalt/internal/dungeons/map/RoomNeighbor;", "roomNeighbors", "component1", "component2", "copy", "(II)Lorg/cobalt/internal/dungeons/map/GridComponent;", "other", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getX", "getZ", "Companion", "cobalt"})
@SourceDebugExtension(value={"SMAP\nDungeonTypes.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DungeonTypes.kt\norg/cobalt/internal/dungeons/map/GridComponent\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,352:1\n777#2:353\n873#2,2:354\n777#2:356\n873#2,2:357\n777#2:359\n873#2,2:360\n777#2:362\n873#2,2:363\n*S KotlinDebug\n*F\n+ 1 DungeonTypes.kt\norg/cobalt/internal/dungeons/map/GridComponent\n*L\n50#1:353\n50#1:354,2\n52#1:356\n52#1:357,2\n63#1:359\n63#1:360,2\n73#1:362\n73#1:363,2\n*E\n"})
public final class GridComponent {
    @NotNull
    public static final Companion Companion = new Companion(null);
    private final int x;
    private final int z;

    public GridComponent(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public final int getX() {
        return this.x;
    }

    public final int getZ() {
        return this.z;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final boolean isValid() {
        int n = this.x;
        if (0 > n) return false;
        if (n >= 11) return false;
        boolean bl = true;
        if (!bl) return false;
        n = this.z;
        if (0 > n) return false;
        if (n >= 11) return false;
        return true;
    }

    public final boolean isRoom() {
        return (this.x & 1) == 0 && (this.z & 1) == 0;
    }

    public final boolean isDoor() {
        return (this.x & 1 ^ this.z & 1) == 1;
    }

    public final int roomIndex() {
        return this.z / 2 * 6 + this.x / 2;
    }

    public final int doorIndex() {
        int index = (this.x - 1 >> 1) + 6 * this.z;
        return index - index / 12;
    }

    @NotNull
    public final class_2338 toWorldCenter() {
        return new class_2338(-185 + 16 * this.x, 0, -185 + 16 * this.z);
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final List<GridComponent> neighboringRooms() {
        List list;
        if (!this.isDoor()) {
            return CollectionsKt.emptyList();
        }
        if ((this.x & 1) == 1) {
            void $this$filterTo$iv$iv;
            Object[] objectArray = new GridComponent[]{new GridComponent(this.x - 1, this.z), new GridComponent(this.x + 1, this.z)};
            Iterable $this$filter$iv = CollectionsKt.listOf((Object[])objectArray);
            boolean $i$f$filter = false;
            Iterable iterable = $this$filter$iv;
            Collection destination$iv$iv = new ArrayList();
            boolean $i$f$filterTo = false;
            for (Object element$iv$iv : $this$filterTo$iv$iv) {
                GridComponent it = (GridComponent)element$iv$iv;
                boolean bl = false;
                if (!it.isValid()) continue;
                destination$iv$iv.add(element$iv$iv);
            }
            list = (List)destination$iv$iv;
        } else {
            Object $this$filter$iv = new GridComponent[]{new GridComponent(this.x, this.z - 1), new GridComponent(this.x, this.z + 1)};
            $this$filter$iv = CollectionsKt.listOf((Object[])$this$filter$iv);
            boolean $i$f$filter = false;
            Object $this$filterTo$iv$iv = $this$filter$iv;
            Collection destination$iv$iv = new ArrayList();
            boolean $i$f$filterTo = false;
            Iterator iterator = $this$filterTo$iv$iv.iterator();
            while (iterator.hasNext()) {
                Object element$iv$iv = iterator.next();
                GridComponent it = (GridComponent)element$iv$iv;
                boolean bl = false;
                if (!it.isValid()) continue;
                destination$iv$iv.add(element$iv$iv);
            }
            list = (List)destination$iv$iv;
        }
        return list;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final List<GridComponent> neighboringDoors() {
        void $this$filterTo$iv$iv;
        if (!this.isRoom()) {
            return CollectionsKt.emptyList();
        }
        Object[] objectArray = new GridComponent[]{new GridComponent(this.x, this.z - 1), new GridComponent(this.x, this.z + 1), new GridComponent(this.x - 1, this.z), new GridComponent(this.x + 1, this.z)};
        Iterable $this$filter$iv = CollectionsKt.listOf((Object[])objectArray);
        boolean $i$f$filter = false;
        Iterable iterable = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            GridComponent it = (GridComponent)element$iv$iv;
            boolean bl = false;
            if (!it.isValid()) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        return (List)destination$iv$iv;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final List<RoomNeighbor> roomNeighbors() {
        void $this$filterTo$iv$iv;
        if (!this.isRoom()) {
            return CollectionsKt.emptyList();
        }
        Object[] objectArray = new RoomNeighbor[]{new RoomNeighbor(new GridComponent(this.x, this.z - 2), new GridComponent(this.x, this.z - 1)), new RoomNeighbor(new GridComponent(this.x, this.z + 2), new GridComponent(this.x, this.z + 1)), new RoomNeighbor(new GridComponent(this.x - 2, this.z), new GridComponent(this.x - 1, this.z)), new RoomNeighbor(new GridComponent(this.x + 2, this.z), new GridComponent(this.x + 1, this.z))};
        Iterable $this$filter$iv = CollectionsKt.listOf((Object[])objectArray);
        boolean $i$f$filter = false;
        Iterable iterable = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            RoomNeighbor it = (RoomNeighbor)element$iv$iv;
            boolean bl = false;
            if (!it.getRoom().isValid()) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        return (List)destination$iv$iv;
    }

    public final int component1() {
        return this.x;
    }

    public final int component2() {
        return this.z;
    }

    @NotNull
    public final GridComponent copy(int x, int z) {
        return new GridComponent(x, z);
    }

    public static /* synthetic */ GridComponent copy$default(GridComponent gridComponent, int n, int n2, int n3, Object object) {
        if ((n3 & 1) != 0) {
            n = gridComponent.x;
        }
        if ((n3 & 2) != 0) {
            n2 = gridComponent.z;
        }
        return gridComponent.copy(n, n2);
    }

    @NotNull
    public String toString() {
        return "GridComponent(x=" + this.x + ", z=" + this.z + ")";
    }

    public int hashCode() {
        int result = Integer.hashCode(this.x);
        result = result * 31 + Integer.hashCode(this.z);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof GridComponent)) {
            return false;
        }
        GridComponent gridComponent = (GridComponent)other;
        if (this.x != gridComponent.x) {
            return false;
        }
        return this.z == gridComponent.z;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001d\u0010\b\u001a\u00020\u00072\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0004\b\b\u0010\t\u00a8\u0006\n"}, d2={"Lorg/cobalt/internal/dungeons/map/GridComponent$Companion;", "", "<init>", "()V", "", "worldX", "worldZ", "Lorg/cobalt/internal/dungeons/map/GridComponent;", "fromWorld", "(DD)Lorg/cobalt/internal/dungeons/map/GridComponent;", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final GridComponent fromWorld(double worldX, double worldZ) {
            return new GridComponent((int)Math.floor((worldX - (double)-200) / 16.0), (int)Math.floor((worldZ - (double)-200) / 16.0));
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

