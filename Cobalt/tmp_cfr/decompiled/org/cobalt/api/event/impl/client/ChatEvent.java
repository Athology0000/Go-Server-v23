/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_2596
 *  net.minecraft.class_2797
 *  net.minecraft.class_7439
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.event.impl.client;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_2596;
import net.minecraft.class_2797;
import net.minecraft.class_7439;
import org.cobalt.api.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\t\b&\u0018\u00002\u00020\u0001:\u0002\t\nB\u0013\u0012\n\u0010\u0003\u001a\u0006\u0012\u0002\b\u00030\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u001b\u0010\u0003\u001a\u0006\u0012\u0002\b\u00030\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0006\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\u000b"}, d2={"Lorg/cobalt/api/event/impl/client/ChatEvent;", "Lorg/cobalt/api/event/Event;", "Lnet/minecraft/class_2596;", "packet", "<init>", "(Lnet/minecraft/class_2596;)V", "Lnet/minecraft/class_2596;", "getPacket", "()Lnet/minecraft/class_2596;", "Receive", "Send", "cobalt"})
public abstract class ChatEvent
extends Event {
    @NotNull
    private final class_2596<?> packet;

    public ChatEvent(@NotNull class_2596<?> packet) {
        Intrinsics.checkNotNullParameter(packet, (String)"packet");
        super(true);
        this.packet = packet;
    }

    @NotNull
    public final class_2596<?> getPacket() {
        return this.packet;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0013\u0012\n\u0010\u0003\u001a\u0006\u0012\u0002\b\u00030\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0019\u0010\u0007\u001a\u0004\u0018\u00010\u00068\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\t\u0010\n\u00a8\u0006\u000b"}, d2={"Lorg/cobalt/api/event/impl/client/ChatEvent$Receive;", "Lorg/cobalt/api/event/impl/client/ChatEvent;", "Lnet/minecraft/class_2596;", "packet", "<init>", "(Lnet/minecraft/class_2596;)V", "", "message", "Ljava/lang/String;", "getMessage", "()Ljava/lang/String;", "cobalt"})
    public static final class Receive
    extends ChatEvent {
        @Nullable
        private final String message;

        public Receive(@NotNull class_2596<?> packet) {
            Intrinsics.checkNotNullParameter(packet, (String)"packet");
            super(packet);
            this.message = packet instanceof class_7439 ? ((class_7439)packet).comp_763().getString() : null;
        }

        @Nullable
        public final String getMessage() {
            return this.message;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0013\u0012\n\u0010\u0003\u001a\u0006\u0012\u0002\b\u00030\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0019\u0010\u0007\u001a\u0004\u0018\u00010\u00068\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\t\u0010\n\u00a8\u0006\u000b"}, d2={"Lorg/cobalt/api/event/impl/client/ChatEvent$Send;", "Lorg/cobalt/api/event/impl/client/ChatEvent;", "Lnet/minecraft/class_2596;", "packet", "<init>", "(Lnet/minecraft/class_2596;)V", "", "message", "Ljava/lang/String;", "getMessage", "()Ljava/lang/String;", "cobalt"})
    public static final class Send
    extends ChatEvent {
        @Nullable
        private final String message;

        public Send(@NotNull class_2596<?> packet) {
            Intrinsics.checkNotNullParameter(packet, (String)"packet");
            super(packet);
            this.message = packet instanceof class_2797 ? ((class_2797)packet).comp_945() : null;
        }

        @Nullable
        public final String getMessage() {
            return this.message;
        }
    }
}

