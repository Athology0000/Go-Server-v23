/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.garden;

import java.util.concurrent.LinkedBlockingDeque;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J#\u0010\t\u001a\u00020\u00072\u0006\u0010\u0005\u001a\u00020\u00042\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\u0004\b\t\u0010\nJ\r\u0010\u000b\u001a\u00020\u0007\u00a2\u0006\u0004\b\u000b\u0010\u0003J\u000f\u0010\f\u001a\u00020\u0007H\u0002\u00a2\u0006\u0004\b\f\u0010\u0003R,\u0010\u000f\u001a\u001a\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020\u0004\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00060\u000e0\r8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u000f\u0010\u0010R\u0018\u0010\u0012\u001a\u0004\u0018\u00010\u00118\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0013\u00a8\u0006\u0014"}, d2={"Lorg/cobalt/internal/garden/GardenWorkerThread;", "", "<init>", "()V", "", "name", "Lkotlin/Function0;", "", "block", "submit", "(Ljava/lang/String;Lkotlin/jvm/functions/Function0;)V", "shutdown", "ensureRunning", "Ljava/util/concurrent/LinkedBlockingDeque;", "Lkotlin/Pair;", "queue", "Ljava/util/concurrent/LinkedBlockingDeque;", "Ljava/lang/Thread;", "thread", "Ljava/lang/Thread;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nGardenWorkerThread.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GardenWorkerThread.kt\norg/cobalt/internal/garden/GardenWorkerThread\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,40:1\n1#2:41\n*E\n"})
public final class GardenWorkerThread {
    @NotNull
    public static final GardenWorkerThread INSTANCE = new GardenWorkerThread();
    @NotNull
    private static final LinkedBlockingDeque<Pair<String, Function0<Unit>>> queue = new LinkedBlockingDeque();
    @Nullable
    private static volatile Thread thread;

    private GardenWorkerThread() {
    }

    public final void submit(@NotNull String name, @NotNull Function0<Unit> block) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter(block, (String)"block");
        this.ensureRunning();
        queue.offer((Pair<String, Function0<Unit>>)TuplesKt.to((Object)name, block));
    }

    public final void shutdown() {
        Thread thread = GardenWorkerThread.thread;
        if (thread != null) {
            thread.interrupt();
        }
        queue.clear();
        try {
            Thread thread2 = GardenWorkerThread.thread;
            if (thread2 != null) {
                thread2.join(2000L);
            }
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        GardenWorkerThread.thread = null;
    }

    private final void ensureRunning() {
        Thread thread;
        Thread t = GardenWorkerThread.thread;
        if (t != null && t.isAlive()) {
            return;
        }
        Thread it = thread = new Thread(GardenWorkerThread::ensureRunning$lambda$0, "GardenWorkerThread");
        boolean bl = false;
        it.setDaemon(true);
        it.start();
        GardenWorkerThread.thread = thread;
    }

    private static final void ensureRunning$lambda$0() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Function0 task = (Function0)queue.take().component2();
                task.invoke();
            }
            catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                break;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

