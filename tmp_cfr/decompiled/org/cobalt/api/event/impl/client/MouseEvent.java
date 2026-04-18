/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.event.impl.client;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.cobalt.api.event.Event;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0010\b&\u0018\u0000 \u000b2\u00020\u0001:\u0007\f\r\u000e\u000f\u0010\u0011\u000bB\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0007\u001a\u0004\b\b\u0010\tR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0007\u001a\u0004\b\n\u0010\t\u00a8\u0006\u0012"}, d2={"Lorg/cobalt/api/event/impl/client/MouseEvent;", "Lorg/cobalt/api/event/Event;", "", "button", "action", "<init>", "(II)V", "I", "getButton", "()I", "getAction", "Companion", "LeftClick", "RightClick", "MiddleClick", "LeftRelease", "RightRelease", "MiddleRelease", "cobalt"})
public abstract class MouseEvent
extends Event {
    @NotNull
    public static final Companion Companion = new Companion(null);
    private final int button;
    private final int action;
    public static final int PRESS = 1;
    public static final int RELEASE = 0;

    public MouseEvent(int button, int action) {
        super(true);
        this.button = button;
        this.action = action;
    }

    public final int getButton() {
        return this.button;
    }

    public final int getAction() {
        return this.action;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006R\u0014\u0010\u0007\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\u0007\u0010\u0006\u00a8\u0006\b"}, d2={"Lorg/cobalt/api/event/impl/client/MouseEvent$Companion;", "", "<init>", "()V", "", "PRESS", "I", "RELEASE", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005\u00a8\u0006\u0006"}, d2={"Lorg/cobalt/api/event/impl/client/MouseEvent$LeftClick;", "Lorg/cobalt/api/event/impl/client/MouseEvent;", "", "action", "<init>", "(I)V", "cobalt"})
    public static final class LeftClick
    extends MouseEvent {
        public LeftClick(int action) {
            super(0, action);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005\u00a8\u0006\u0006"}, d2={"Lorg/cobalt/api/event/impl/client/MouseEvent$LeftRelease;", "Lorg/cobalt/api/event/impl/client/MouseEvent;", "", "action", "<init>", "(I)V", "cobalt"})
    public static final class LeftRelease
    extends MouseEvent {
        public LeftRelease(int action) {
            super(0, action);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005\u00a8\u0006\u0006"}, d2={"Lorg/cobalt/api/event/impl/client/MouseEvent$MiddleClick;", "Lorg/cobalt/api/event/impl/client/MouseEvent;", "", "action", "<init>", "(I)V", "cobalt"})
    public static final class MiddleClick
    extends MouseEvent {
        public MiddleClick(int action) {
            super(2, action);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005\u00a8\u0006\u0006"}, d2={"Lorg/cobalt/api/event/impl/client/MouseEvent$MiddleRelease;", "Lorg/cobalt/api/event/impl/client/MouseEvent;", "", "action", "<init>", "(I)V", "cobalt"})
    public static final class MiddleRelease
    extends MouseEvent {
        public MiddleRelease(int action) {
            super(2, action);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005\u00a8\u0006\u0006"}, d2={"Lorg/cobalt/api/event/impl/client/MouseEvent$RightClick;", "Lorg/cobalt/api/event/impl/client/MouseEvent;", "", "action", "<init>", "(I)V", "cobalt"})
    public static final class RightClick
    extends MouseEvent {
        public RightClick(int action) {
            super(1, action);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005\u00a8\u0006\u0006"}, d2={"Lorg/cobalt/api/event/impl/client/MouseEvent$RightRelease;", "Lorg/cobalt/api/event/impl/client/MouseEvent;", "", "action", "<init>", "(I)V", "cobalt"})
    public static final class RightRelease
    extends MouseEvent {
        public RightRelease(int action) {
            super(1, action);
        }
    }
}

