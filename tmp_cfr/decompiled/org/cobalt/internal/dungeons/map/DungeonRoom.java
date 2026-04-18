/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_2338
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.dungeons.map;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_2338;
import org.cobalt.internal.dungeons.map.DungeonDoor;
import org.cobalt.internal.dungeons.map.GridComponent;
import org.cobalt.internal.dungeons.map.RoomCheckmark;
import org.cobalt.internal.dungeons.map.RoomDefinition;
import org.cobalt.internal.dungeons.map.RoomDirection;
import org.cobalt.internal.dungeons.map.RoomKind;
import org.cobalt.internal.dungeons.map.RoomShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000r\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u001d\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0007\u0010\bJ\r\u0010\n\u001a\u00020\t\u00a2\u0006\u0004\b\n\u0010\u000bJ\u001f\u0010\u000f\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\u00032\b\b\u0002\u0010\u000e\u001a\u00020\r\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0015\u0010\u0013\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u0011\u00a2\u0006\u0004\b\u0013\u0010\u0014R\"\u0010\u0006\u001a\u00020\u00058\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0006\u0010\u0015\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u001d\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00030\u00028\u0006\u00a2\u0006\f\n\u0004\b\u001a\u0010\u001b\u001a\u0004\b\u001c\u0010\u001dR'\u0010!\u001a\u0012\u0012\u0004\u0012\u00020\u001f0\u001ej\b\u0012\u0004\u0012\u00020\u001f` 8\u0006\u00a2\u0006\f\n\u0004\b!\u0010\"\u001a\u0004\b#\u0010$R\"\u0010%\u001a\u00020\r8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b%\u0010&\u001a\u0004\b'\u0010(\"\u0004\b)\u0010*R$\u0010,\u001a\u0004\u0018\u00010+8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b,\u0010-\u001a\u0004\b.\u0010/\"\u0004\b0\u00101R\"\u00103\u001a\u0002028\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b3\u00104\u001a\u0004\b5\u00106\"\u0004\b7\u00108R\"\u0010:\u001a\u0002098\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b:\u0010;\u001a\u0004\b<\u0010=\"\u0004\b>\u0010?R\"\u0010A\u001a\u00020@8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bA\u0010B\u001a\u0004\bC\u0010D\"\u0004\bE\u0010FR\"\u0010G\u001a\u00020\u00058\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bG\u0010\u0015\u001a\u0004\bH\u0010\u0017\"\u0004\bI\u0010\u0019R$\u0010K\u001a\u0004\u0018\u00010J8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bK\u0010L\u001a\u0004\bM\u0010N\"\u0004\bO\u0010PR\"\u0010Q\u001a\u00020\u00058\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bQ\u0010\u0015\u001a\u0004\bR\u0010\u0017\"\u0004\bS\u0010\u0019R\u0013\u0010W\u001a\u0004\u0018\u00010T8F\u00a2\u0006\u0006\u001a\u0004\bU\u0010V\u00a8\u0006X"}, d2={"Lorg/cobalt/internal/dungeons/map/DungeonRoom;", "", "", "Lorg/cobalt/internal/dungeons/map/GridComponent;", "initialComponents", "", "height", "<init>", "(Ljava/util/List;I)V", "", "updateShape", "()V", "component", "", "shouldUpdate", "addComponent", "(Lorg/cobalt/internal/dungeons/map/GridComponent;Z)V", "Lorg/cobalt/internal/dungeons/map/RoomDefinition;", "definition", "applyDefinition", "(Lorg/cobalt/internal/dungeons/map/RoomDefinition;)V", "I", "getHeight", "()I", "setHeight", "(I)V", "components", "Ljava/util/List;", "getComponents", "()Ljava/util/List;", "Ljava/util/LinkedHashSet;", "Lorg/cobalt/internal/dungeons/map/DungeonDoor;", "Lkotlin/collections/LinkedHashSet;", "doors", "Ljava/util/LinkedHashSet;", "getDoors", "()Ljava/util/LinkedHashSet;", "explored", "Z", "getExplored", "()Z", "setExplored", "(Z)V", "", "name", "Ljava/lang/String;", "getName", "()Ljava/lang/String;", "setName", "(Ljava/lang/String;)V", "Lorg/cobalt/internal/dungeons/map/RoomKind;", "type", "Lorg/cobalt/internal/dungeons/map/RoomKind;", "getType", "()Lorg/cobalt/internal/dungeons/map/RoomKind;", "setType", "(Lorg/cobalt/internal/dungeons/map/RoomKind;)V", "Lorg/cobalt/internal/dungeons/map/RoomCheckmark;", "checkmark", "Lorg/cobalt/internal/dungeons/map/RoomCheckmark;", "getCheckmark", "()Lorg/cobalt/internal/dungeons/map/RoomCheckmark;", "setCheckmark", "(Lorg/cobalt/internal/dungeons/map/RoomCheckmark;)V", "Lorg/cobalt/internal/dungeons/map/RoomShape;", "shape", "Lorg/cobalt/internal/dungeons/map/RoomShape;", "getShape", "()Lorg/cobalt/internal/dungeons/map/RoomShape;", "setShape", "(Lorg/cobalt/internal/dungeons/map/RoomShape;)V", "totalSecrets", "getTotalSecrets", "setTotalSecrets", "Lnet/minecraft/class_2338;", "corner", "Lnet/minecraft/class_2338;", "getCorner", "()Lnet/minecraft/class_2338;", "setCorner", "(Lnet/minecraft/class_2338;)V", "rotation", "getRotation", "setRotation", "Lorg/cobalt/internal/dungeons/map/RoomDirection;", "getDirection", "()Lorg/cobalt/internal/dungeons/map/RoomDirection;", "direction", "cobalt"})
@SourceDebugExtension(value={"SMAP\nDungeonTypes.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DungeonTypes.kt\norg/cobalt/internal/dungeons/map/DungeonRoom\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,352:1\n1661#2,3:353\n1661#2,3:356\n*S KotlinDebug\n*F\n+ 1 DungeonTypes.kt\norg/cobalt/internal/dungeons/map/DungeonRoom\n*L\n321#1:353,3\n322#1:356,3\n*E\n"})
public final class DungeonRoom {
    private int height;
    @NotNull
    private final List<GridComponent> components;
    @NotNull
    private final LinkedHashSet<DungeonDoor> doors;
    private boolean explored;
    @Nullable
    private String name;
    @NotNull
    private RoomKind type;
    @NotNull
    private RoomCheckmark checkmark;
    @NotNull
    private RoomShape shape;
    private int totalSecrets;
    @Nullable
    private class_2338 corner;
    private int rotation;

    public DungeonRoom(@NotNull List<GridComponent> initialComponents, int height) {
        Intrinsics.checkNotNullParameter(initialComponents, (String)"initialComponents");
        this.height = height;
        this.components = initialComponents;
        this.doors = new LinkedHashSet();
        this.type = RoomKind.UNKNOWN;
        this.checkmark = RoomCheckmark.UNEXPLORED;
        this.shape = RoomShape.ONE_BY_ONE;
        this.rotation = -1;
    }

    public final int getHeight() {
        return this.height;
    }

    public final void setHeight(int n) {
        this.height = n;
    }

    @NotNull
    public final List<GridComponent> getComponents() {
        return this.components;
    }

    @NotNull
    public final LinkedHashSet<DungeonDoor> getDoors() {
        return this.doors;
    }

    public final boolean getExplored() {
        return this.explored;
    }

    public final void setExplored(boolean bl) {
        this.explored = bl;
    }

    @Nullable
    public final String getName() {
        return this.name;
    }

    public final void setName(@Nullable String string) {
        this.name = string;
    }

    @NotNull
    public final RoomKind getType() {
        return this.type;
    }

    public final void setType(@NotNull RoomKind roomKind) {
        Intrinsics.checkNotNullParameter((Object)((Object)roomKind), (String)"<set-?>");
        this.type = roomKind;
    }

    @NotNull
    public final RoomCheckmark getCheckmark() {
        return this.checkmark;
    }

    public final void setCheckmark(@NotNull RoomCheckmark roomCheckmark) {
        Intrinsics.checkNotNullParameter((Object)((Object)roomCheckmark), (String)"<set-?>");
        this.checkmark = roomCheckmark;
    }

    @NotNull
    public final RoomShape getShape() {
        return this.shape;
    }

    public final void setShape(@NotNull RoomShape roomShape) {
        Intrinsics.checkNotNullParameter((Object)((Object)roomShape), (String)"<set-?>");
        this.shape = roomShape;
    }

    public final int getTotalSecrets() {
        return this.totalSecrets;
    }

    public final void setTotalSecrets(int n) {
        this.totalSecrets = n;
    }

    @Nullable
    public final class_2338 getCorner() {
        return this.corner;
    }

    public final void setCorner(@Nullable class_2338 class_23382) {
        this.corner = class_23382;
    }

    public final int getRotation() {
        return this.rotation;
    }

    public final void setRotation(int n) {
        this.rotation = n;
    }

    @Nullable
    public final RoomDirection getDirection() {
        return switch (this.rotation) {
            case 0 -> RoomDirection.NW;
            case 90 -> RoomDirection.NE;
            case 180 -> RoomDirection.SE;
            case 270 -> RoomDirection.SW;
            default -> null;
        };
    }

    /*
     * WARNING - void declaration
     */
    public final void updateShape() {
        void destination$iv;
        void $this$mapTo$iv;
        void var11_12;
        Iterable destination$iv2;
        void $this$mapTo$iv2;
        int size = this.components.size();
        if (size <= 0 || size > 4) {
            this.shape = RoomShape.UNKNOWN;
            return;
        }
        Iterable iterable = this.components;
        Collection collection = new LinkedHashSet();
        boolean $i$f$mapTo22 = false;
        for (Object item$iv : $this$mapTo$iv2) {
            void it;
            GridComponent gridComponent = (GridComponent)item$iv;
            var11_12 = destination$iv2;
            boolean bl = false;
            var11_12.add(it.getX());
        }
        LinkedHashSet distinctX = (LinkedHashSet)destination$iv2;
        destination$iv2 = this.components;
        Collection $i$f$mapTo22 = new LinkedHashSet();
        boolean $i$f$mapTo = false;
        for (Object item$iv : $this$mapTo$iv) {
            void it;
            GridComponent bl = (GridComponent)item$iv;
            var11_12 = destination$iv;
            boolean bl2 = false;
            var11_12.add(it.getZ());
        }
        LinkedHashSet distinctZ = (LinkedHashSet)destination$iv;
        this.shape = size == 1 ? RoomShape.ONE_BY_ONE : (size == 2 ? RoomShape.ONE_BY_TWO : (size == 4 && (distinctX.size() == 1 || distinctZ.size() == 1) ? RoomShape.ONE_BY_FOUR : (size == 4 ? RoomShape.TWO_BY_TWO : (size == 3 && (distinctX.size() == size || distinctZ.size() == size) ? RoomShape.ONE_BY_THREE : (size == 3 ? RoomShape.L_SHAPE : RoomShape.UNKNOWN)))));
    }

    public final void addComponent(@NotNull GridComponent component, boolean shouldUpdate) {
        Intrinsics.checkNotNullParameter((Object)component, (String)"component");
        if (this.components.contains(component)) {
            return;
        }
        this.components.add(component);
        if (shouldUpdate) {
            this.updateShape();
        }
    }

    public static /* synthetic */ void addComponent$default(DungeonRoom dungeonRoom, GridComponent gridComponent, boolean bl, int n, Object object) {
        if ((n & 2) != 0) {
            bl = true;
        }
        dungeonRoom.addComponent(gridComponent, bl);
    }

    public final void applyDefinition(@NotNull RoomDefinition definition) {
        Intrinsics.checkNotNullParameter((Object)definition, (String)"definition");
        this.name = definition.getName();
        if (definition.getType() != RoomKind.UNKNOWN) {
            this.type = definition.getType();
        }
        this.totalSecrets = definition.getSecrets();
    }
}

