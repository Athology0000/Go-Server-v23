/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelFutureListener
 *  net.minecraft.class_2535
 *  net.minecraft.class_2547
 *  net.minecraft.class_2596
 *  net.minecraft.class_2797
 *  net.minecraft.class_7439
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.cobalt.mixin.network;

import io.netty.channel.ChannelFutureListener;
import net.minecraft.class_2535;
import net.minecraft.class_2547;
import net.minecraft.class_2596;
import net.minecraft.class_2797;
import net.minecraft.class_7439;
import org.cobalt.api.event.impl.client.ChatEvent;
import org.cobalt.api.event.impl.client.PacketEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_2535.class})
public class ConnectionMixin {
    @Inject(method={"method_10759"}, at={@At(value="HEAD")}, cancellable=true)
    private static void onPacketReceived(class_2596<?> packet, class_2547 listener, CallbackInfo callbackInfo) {
        PacketEvent.Incoming incomingPacketEvent = new PacketEvent.Incoming(packet);
        if (incomingPacketEvent.post()) {
            callbackInfo.cancel();
            return;
        }
        if (packet instanceof class_7439) {
            new ChatEvent.Receive(packet).post();
        }
    }

    @Inject(method={"method_10764"}, at={@At(value="HEAD")}, cancellable=true)
    private void onPacketSent(class_2596<?> packet, ChannelFutureListener listener, boolean flush, CallbackInfo callbackInfo) {
        PacketEvent.Outgoing outgoingPacketEvent = new PacketEvent.Outgoing(packet);
        if (outgoingPacketEvent.post()) {
            callbackInfo.cancel();
            return;
        }
        if (packet instanceof class_2797) {
            new ChatEvent.Send(packet).post();
        }
    }
}

