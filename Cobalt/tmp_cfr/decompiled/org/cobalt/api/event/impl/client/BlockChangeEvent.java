/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_2338
 *  net.minecraft.class_2680
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.event.impl.client;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import org.cobalt.api.event.Event;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\t\u001a\u0004\b\n\u0010\u000bR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\f\u001a\u0004\b\r\u0010\u000eR\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\f\u001a\u0004\b\u000f\u0010\u000e\u00a8\u0006\u0010"}, d2={"Lorg/cobalt/api/event/impl/client/BlockChangeEvent;", "Lorg/cobalt/api/event/Event;", "Lnet/minecraft/class_2338;", "pos", "Lnet/minecraft/class_2680;", "oldBlock", "newBlock", "<init>", "(Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_2680;)V", "Lnet/minecraft/class_2338;", "getPos", "()Lnet/minecraft/class_2338;", "Lnet/minecraft/class_2680;", "getOldBlock", "()Lnet/minecraft/class_2680;", "getNewBlock", "cobalt"})
public final class BlockChangeEvent
extends Event {
    @NotNull
    private final class_2338 pos;
    @NotNull
    private final class_2680 oldBlock;
    @NotNull
    private final class_2680 newBlock;

    public BlockChangeEvent(@NotNull class_2338 pos, @NotNull class_2680 oldBlock, @NotNull class_2680 newBlock) {
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        Intrinsics.checkNotNullParameter((Object)oldBlock, (String)"oldBlock");
        Intrinsics.checkNotNullParameter((Object)newBlock, (String)"newBlock");
        super(false);
        this.pos = pos;
        this.oldBlock = oldBlock;
        this.newBlock = newBlock;
    }

    @NotNull
    public final class_2338 getPos() {
        return this.pos;
    }

    @NotNull
    public final class_2680 getOldBlock() {
        return this.oldBlock;
    }

    @NotNull
    public final class_2680 getNewBlock() {
        return this.newBlock;
    }
}

