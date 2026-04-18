/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.event.impl.render;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.event.Event;
import org.cobalt.api.event.impl.render.WorldRenderContext;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\t\b&\u0018\u00002\u00020\u0001:\u0002\t\nB\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0006\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\u000b"}, d2={"Lorg/cobalt/api/event/impl/render/WorldRenderEvent;", "Lorg/cobalt/api/event/Event;", "Lorg/cobalt/api/event/impl/render/WorldRenderContext;", "context", "<init>", "(Lorg/cobalt/api/event/impl/render/WorldRenderContext;)V", "Lorg/cobalt/api/event/impl/render/WorldRenderContext;", "getContext", "()Lorg/cobalt/api/event/impl/render/WorldRenderContext;", "Start", "Last", "cobalt"})
public abstract class WorldRenderEvent
extends Event {
    @NotNull
    private final WorldRenderContext context;

    public WorldRenderEvent(@NotNull WorldRenderContext context) {
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        super(false, 1, null);
        this.context = context;
    }

    @NotNull
    public final WorldRenderContext getContext() {
        return this.context;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005\u00a8\u0006\u0006"}, d2={"Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent;", "Lorg/cobalt/api/event/impl/render/WorldRenderContext;", "context", "<init>", "(Lorg/cobalt/api/event/impl/render/WorldRenderContext;)V", "cobalt"})
    public static final class Last
    extends WorldRenderEvent {
        public Last(@NotNull WorldRenderContext context) {
            Intrinsics.checkNotNullParameter((Object)context, (String)"context");
            super(context);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005\u00a8\u0006\u0006"}, d2={"Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Start;", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent;", "Lorg/cobalt/api/event/impl/render/WorldRenderContext;", "context", "<init>", "(Lorg/cobalt/api/event/impl/render/WorldRenderContext;)V", "cobalt"})
    public static final class Start
    extends WorldRenderEvent {
        public Start(@NotNull WorldRenderContext context) {
            Intrinsics.checkNotNullParameter((Object)context, (String)"context");
            super(context);
        }
    }
}

