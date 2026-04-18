/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 */
package org.cobalt.api.event;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.cobalt.api.event.EventBus;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0006\b&\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0015\u0010\b\u001a\u00020\u00072\u0006\u0010\u0006\u001a\u00020\u0002\u00a2\u0006\u0004\b\b\u0010\u0005J\r\u0010\t\u001a\u00020\u0002\u00a2\u0006\u0004\b\t\u0010\nJ\r\u0010\u000b\u001a\u00020\u0002\u00a2\u0006\u0004\b\u000b\u0010\nR\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\fR\u0016\u0010\u0006\u001a\u00020\u00028\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0006\u0010\f\u00a8\u0006\r"}, d2={"Lorg/cobalt/api/event/Event;", "", "", "cancellable", "<init>", "(Z)V", "value", "", "setCancelled", "isCancelled", "()Z", "post", "Z", "cobalt"})
public abstract class Event {
    private final boolean cancellable;
    private boolean value;

    public Event(boolean cancellable) {
        this.cancellable = cancellable;
    }

    public /* synthetic */ Event(boolean bl, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 1) != 0) {
            bl = false;
        }
        this(bl);
    }

    public final void setCancelled(boolean value) {
        if (this.cancellable) {
            this.value = value;
        }
    }

    public final boolean isCancelled() {
        if (!this.cancellable) {
            return false;
        }
        return this.value;
    }

    public final boolean post() {
        EventBus.post(this);
        return this.isCancelled();
    }

    public Event() {
        this(false, 1, null);
    }
}

