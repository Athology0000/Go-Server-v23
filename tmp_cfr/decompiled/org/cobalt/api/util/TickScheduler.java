/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.util;

import java.util.Comparator;
import java.util.PriorityQueue;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.util.TickScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u0015B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001f\u0010\t\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006H\u0007\u00a2\u0006\u0004\b\t\u0010\nJ\u0017\u0010\r\u001a\u00020\b2\u0006\u0010\f\u001a\u00020\u000bH\u0007\u00a2\u0006\u0004\b\r\u0010\u000eR\u001a\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0011\u0010\u0012R\u0016\u0010\u0013\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0013\u0010\u0014\u00a8\u0006\u0016"}, d2={"Lorg/cobalt/api/util/TickScheduler;", "", "<init>", "()V", "", "delayTicks", "Ljava/lang/Runnable;", "action", "", "schedule", "(JLjava/lang/Runnable;)V", "Lorg/cobalt/api/event/impl/client/TickEvent$End;", "event", "onClientTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$End;)V", "Ljava/util/PriorityQueue;", "Lorg/cobalt/api/util/TickScheduler$ScheduledTask;", "taskQueue", "Ljava/util/PriorityQueue;", "currentTick", "J", "ScheduledTask", "cobalt"})
@SourceDebugExtension(value={"SMAP\nTickScheduler.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TickScheduler.kt\norg/cobalt/api/util/TickScheduler\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,35:1\n1#2:36\n*E\n"})
public final class TickScheduler {
    @NotNull
    public static final TickScheduler INSTANCE = new TickScheduler();
    @NotNull
    private static final PriorityQueue<ScheduledTask> taskQueue = new PriorityQueue<Object>(Comparator.comparingLong(arg_0 -> TickScheduler.taskQueue$lambda$0((Function1)taskQueue.1.INSTANCE, arg_0)));
    private static long currentTick;

    private TickScheduler() {
    }

    @JvmStatic
    public static final void schedule(long delayTicks, @NotNull Runnable action) {
        Intrinsics.checkNotNullParameter((Object)action, (String)"action");
        taskQueue.offer(new ScheduledTask(currentTick + delayTicks, action));
    }

    @SubscribeEvent
    public final void onClientTick(@NotNull TickEvent.End event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        long l = currentTick;
        currentTick = l + 1L;
        ScheduledTask task = null;
        while (true) {
            ScheduledTask scheduledTask;
            ScheduledTask it = scheduledTask = taskQueue.peek();
            boolean bl = false;
            task = it;
            if (scheduledTask == null) break;
            ScheduledTask scheduledTask2 = task;
            Intrinsics.checkNotNull((Object)scheduledTask2);
            if (currentTick < scheduledTask2.getExecuteTick()) break;
            taskQueue.poll().getAction().run();
        }
    }

    private static final long taskQueue$lambda$0(Function1 $tmp0, Object p0) {
        return ((Number)$tmp0.invoke(p0)).longValue();
    }

    static {
        EventBus.register(INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0082\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ$\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u001b\u0010\u0010\u001a\u00020\u000f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0011\u0010\u0013\u001a\u00020\u0012H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0011\u0010\u0016\u001a\u00020\u0015H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0018\u001a\u0004\b\u0019\u0010\tR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001a\u001a\u0004\b\u001b\u0010\u000b\u00a8\u0006\u001c"}, d2={"Lorg/cobalt/api/util/TickScheduler$ScheduledTask;", "", "", "executeTick", "Ljava/lang/Runnable;", "action", "<init>", "(JLjava/lang/Runnable;)V", "component1", "()J", "component2", "()Ljava/lang/Runnable;", "copy", "(JLjava/lang/Runnable;)Lorg/cobalt/api/util/TickScheduler$ScheduledTask;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "J", "getExecuteTick", "Ljava/lang/Runnable;", "getAction", "cobalt"})
    private static final class ScheduledTask {
        private final long executeTick;
        @NotNull
        private final Runnable action;

        public ScheduledTask(long executeTick, @NotNull Runnable action) {
            Intrinsics.checkNotNullParameter((Object)action, (String)"action");
            this.executeTick = executeTick;
            this.action = action;
        }

        public final long getExecuteTick() {
            return this.executeTick;
        }

        @NotNull
        public final Runnable getAction() {
            return this.action;
        }

        public final long component1() {
            return this.executeTick;
        }

        @NotNull
        public final Runnable component2() {
            return this.action;
        }

        @NotNull
        public final ScheduledTask copy(long executeTick, @NotNull Runnable action) {
            Intrinsics.checkNotNullParameter((Object)action, (String)"action");
            return new ScheduledTask(executeTick, action);
        }

        public static /* synthetic */ ScheduledTask copy$default(ScheduledTask scheduledTask, long l, Runnable runnable, int n, Object object) {
            if ((n & 1) != 0) {
                l = scheduledTask.executeTick;
            }
            if ((n & 2) != 0) {
                runnable = scheduledTask.action;
            }
            return scheduledTask.copy(l, runnable);
        }

        @NotNull
        public String toString() {
            return "ScheduledTask(executeTick=" + this.executeTick + ", action=" + this.action + ")";
        }

        public int hashCode() {
            int result = Long.hashCode(this.executeTick);
            result = result * 31 + this.action.hashCode();
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ScheduledTask)) {
                return false;
            }
            ScheduledTask scheduledTask = (ScheduledTask)other;
            if (this.executeTick != scheduledTask.executeTick) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.action, (Object)scheduledTask.action);
        }
    }
}

