/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.dungeons.map;

import java.util.LinkedHashSet;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import org.cobalt.internal.dungeons.map.DoorKind;
import org.cobalt.internal.dungeons.map.DungeonRoom;
import org.cobalt.internal.dungeons.map.GridComponent;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0015\u0010\t\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\t\u0010\nJ\u0017\u0010\r\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\r\u0010\u000eR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u000f\u001a\u0004\b\u0010\u0010\u0011R'\u0010\u0015\u001a\u0012\u0012\u0004\u0012\u00020\u00130\u0012j\b\u0012\u0004\u0012\u00020\u0013`\u00148\u0006\u00a2\u0006\f\n\u0004\b\u0015\u0010\u0016\u001a\u0004\b\u0017\u0010\u0018R\"\u0010\u001a\u001a\u00020\u00198\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u001a\u0010\u001b\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001fR\"\u0010 \u001a\u00020\f8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b \u0010!\u001a\u0004\b\"\u0010#\"\u0004\b$\u0010%R\"\u0010'\u001a\u00020&8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b'\u0010(\u001a\u0004\b)\u0010*\"\u0004\b+\u0010,\u00a8\u0006-"}, d2={"Lorg/cobalt/internal/dungeons/map/DungeonDoor;", "", "Lorg/cobalt/internal/dungeons/map/GridComponent;", "component", "<init>", "(Lorg/cobalt/internal/dungeons/map/GridComponent;)V", "Lnet/minecraft/class_2248;", "blockAt69", "", "updateFromBlock", "(Lnet/minecraft/class_2248;)V", "block", "", "isInfested", "(Lnet/minecraft/class_2248;)Z", "Lorg/cobalt/internal/dungeons/map/GridComponent;", "getComponent", "()Lorg/cobalt/internal/dungeons/map/GridComponent;", "Ljava/util/LinkedHashSet;", "Lorg/cobalt/internal/dungeons/map/DungeonRoom;", "Lkotlin/collections/LinkedHashSet;", "rooms", "Ljava/util/LinkedHashSet;", "getRooms", "()Ljava/util/LinkedHashSet;", "", "rotation", "I", "getRotation", "()I", "setRotation", "(I)V", "opened", "Z", "getOpened", "()Z", "setOpened", "(Z)V", "Lorg/cobalt/internal/dungeons/map/DoorKind;", "type", "Lorg/cobalt/internal/dungeons/map/DoorKind;", "getType", "()Lorg/cobalt/internal/dungeons/map/DoorKind;", "setType", "(Lorg/cobalt/internal/dungeons/map/DoorKind;)V", "cobalt"})
public final class DungeonDoor {
    @NotNull
    private final GridComponent component;
    @NotNull
    private final LinkedHashSet<DungeonRoom> rooms;
    private int rotation;
    private boolean opened;
    @NotNull
    private DoorKind type;

    public DungeonDoor(@NotNull GridComponent component) {
        Intrinsics.checkNotNullParameter((Object)component, (String)"component");
        this.component = component;
        this.rooms = new LinkedHashSet();
        this.rotation = -1;
        this.type = DoorKind.NORMAL;
    }

    @NotNull
    public final GridComponent getComponent() {
        return this.component;
    }

    @NotNull
    public final LinkedHashSet<DungeonRoom> getRooms() {
        return this.rooms;
    }

    public final int getRotation() {
        return this.rotation;
    }

    public final void setRotation(int n) {
        this.rotation = n;
    }

    public final boolean getOpened() {
        return this.opened;
    }

    public final void setOpened(boolean bl) {
        this.opened = bl;
    }

    @NotNull
    public final DoorKind getType() {
        return this.type;
    }

    public final void setType(@NotNull DoorKind doorKind) {
        Intrinsics.checkNotNullParameter((Object)((Object)doorKind), (String)"<set-?>");
        this.type = doorKind;
    }

    public final void updateFromBlock(@NotNull class_2248 blockAt69) {
        Intrinsics.checkNotNullParameter((Object)blockAt69, (String)"blockAt69");
        this.opened = Intrinsics.areEqual((Object)blockAt69, (Object)class_2246.field_10124) || Intrinsics.areEqual((Object)blockAt69, (Object)class_2246.field_10499);
        this.type = this.isInfested(blockAt69) ? DoorKind.ENTRANCE : (Intrinsics.areEqual((Object)blockAt69, (Object)class_2246.field_10381) ? DoorKind.WITHER : (Intrinsics.areEqual((Object)blockAt69, (Object)class_2246.field_10328) ? DoorKind.BLOOD : DoorKind.NORMAL));
    }

    private final boolean isInfested(class_2248 block) {
        return Intrinsics.areEqual((Object)block, (Object)class_2246.field_10492) || Intrinsics.areEqual((Object)block, (Object)class_2246.field_10176) || Intrinsics.areEqual((Object)block, (Object)class_2246.field_10100) || Intrinsics.areEqual((Object)block, (Object)class_2246.field_29224) || Intrinsics.areEqual((Object)block, (Object)class_2246.field_10480) || Intrinsics.areEqual((Object)block, (Object)class_2246.field_10277) || Intrinsics.areEqual((Object)block, (Object)class_2246.field_10387);
    }
}

