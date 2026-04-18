/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_2596
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.event.impl.client;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_2596;
import org.cobalt.api.event.Event;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\t\b&\u0018\u00002\u00020\u0001:\u0002\t\nB\u0013\u0012\n\u0010\u0003\u001a\u0006\u0012\u0002\b\u00030\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u001b\u0010\u0003\u001a\u0006\u0012\u0002\b\u00030\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0006\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\u000b"}, d2={"Lorg/cobalt/api/event/impl/client/PacketEvent;", "Lorg/cobalt/api/event/Event;", "Lnet/minecraft/class_2596;", "packet", "<init>", "(Lnet/minecraft/class_2596;)V", "Lnet/minecraft/class_2596;", "getPacket", "()Lnet/minecraft/class_2596;", "Incoming", "Outgoing", "cobalt"})
public abstract class PacketEvent
extends Event {
    @NotNull
    private final class_2596<?> packet;

    public PacketEvent(@NotNull class_2596<?> packet) {
        Intrinsics.checkNotNullParameter(packet, (String)"packet");
        super(true);
        this.packet = packet;
    }

    @NotNull
    public final class_2596<?> getPacket() {
        return this.packet;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0013\u0012\n\u0010\u0003\u001a\u0006\u0012\u0002\b\u00030\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005\u00a8\u0006\u0006"}, d2={"Lorg/cobalt/api/event/impl/client/PacketEvent$Incoming;", "Lorg/cobalt/api/event/impl/client/PacketEvent;", "Lnet/minecraft/class_2596;", "packet", "<init>", "(Lnet/minecraft/class_2596;)V", "cobalt"})
    public static final class Incoming
    extends PacketEvent {
        public Incoming(@NotNull class_2596<?> packet) {
            Intrinsics.checkNotNullParameter(packet, (String)"packet");
            super(packet);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0013\u0012\n\u0010\u0003\u001a\u0006\u0012\u0002\b\u00030\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005\u00a8\u0006\u0006"}, d2={"Lorg/cobalt/api/event/impl/client/PacketEvent$Outgoing;", "Lorg/cobalt/api/event/impl/client/PacketEvent;", "Lnet/minecraft/class_2596;", "packet", "<init>", "(Lnet/minecraft/class_2596;)V", "cobalt"})
    public static final class Outgoing
    extends PacketEvent {
        public Outgoing(@NotNull class_2596<?> packet) {
            Intrinsics.checkNotNullParameter(packet, (String)"packet");
            super(packet);
        }
    }
}

