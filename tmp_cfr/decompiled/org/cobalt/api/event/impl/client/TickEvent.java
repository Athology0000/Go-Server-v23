/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 */
package org.cobalt.api.event.impl.client;

import kotlin.Metadata;
import org.cobalt.api.event.Event;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b&\u0018\u00002\u00020\u0001:\u0002\u0004\u0005B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0006"}, d2={"Lorg/cobalt/api/event/impl/client/TickEvent;", "Lorg/cobalt/api/event/Event;", "<init>", "()V", "Start", "End", "cobalt"})
public abstract class TickEvent
extends Event {
    public TickEvent() {
        super(false);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2={"Lorg/cobalt/api/event/impl/client/TickEvent$End;", "Lorg/cobalt/api/event/impl/client/TickEvent;", "<init>", "()V", "cobalt"})
    public static final class End
    extends TickEvent {
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2={"Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "Lorg/cobalt/api/event/impl/client/TickEvent;", "<init>", "()V", "cobalt"})
    public static final class Start
    extends TickEvent {
    }
}

