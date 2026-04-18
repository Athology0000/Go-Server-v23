/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_1041
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_1041;
import net.minecraft.class_310;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.render.NvgEvent;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.ui.notification.UINotification;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J'\u0010\n\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u00042\b\b\u0002\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0017\u0010\u000e\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\fH\u0007\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0017\u0010\u0012\u001a\u00020\t2\u0006\u0010\u0011\u001a\u00020\u0010H\u0002\u00a2\u0006\u0004\b\u0012\u0010\u0013J\r\u0010\u0014\u001a\u00020\t\u00a2\u0006\u0004\b\u0014\u0010\u0003R\u0014\u0010\u0016\u001a\u00020\u00158\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0016\u0010\u0017R\u001a\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00190\u00188\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001a\u0010\u001bR\u001a\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00190\u00188\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u001b\u00a8\u0006\u001d"}, d2={"Lorg/cobalt/api/notification/NotificationManager;", "", "<init>", "()V", "", "title", "description", "", "duration", "", "queue", "(Ljava/lang/String;Ljava/lang/String;J)V", "Lorg/cobalt/api/event/impl/render/NvgEvent;", "event", "onRender", "(Lorg/cobalt/api/event/impl/render/NvgEvent;)V", "", "screenHeight", "updateNotifications", "(F)V", "clear", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "", "Lorg/cobalt/internal/ui/notification/UINotification;", "notifQueue", "Ljava/util/List;", "activeNotifications", "cobalt"})
@SourceDebugExtension(value={"SMAP\nNotificationManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NotificationManager.kt\norg/cobalt/api/notification/NotificationManager\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,73:1\n1915#2,2:74\n1915#2,2:76\n1924#2,3:78\n*S KotlinDebug\n*F\n+ 1 NotificationManager.kt\norg/cobalt/api/notification/NotificationManager\n*L\n31#1:74,2\n47#1:76,2\n61#1:78,3\n*E\n"})
public final class NotificationManager {
    @NotNull
    public static final NotificationManager INSTANCE = new NotificationManager();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final List<UINotification> notifQueue;
    @NotNull
    private static final List<UINotification> activeNotifications;

    private NotificationManager() {
    }

    public final void queue(@NotNull String title, @NotNull String description, long duration) {
        Intrinsics.checkNotNullParameter((Object)title, (String)"title");
        Intrinsics.checkNotNullParameter((Object)description, (String)"description");
        notifQueue.add(new UINotification(title, description, duration));
    }

    public static /* synthetic */ void queue$default(NotificationManager notificationManager, String string, String string2, long l, int n, Object object) {
        if ((n & 4) != 0) {
            l = 2000L;
        }
        notificationManager.queue(string, string2, l);
    }

    @SubscribeEvent
    public final void onRender(@NotNull NvgEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_1041 class_10412 = mc.method_22683();
        Intrinsics.checkNotNullExpressionValue((Object)class_10412, (String)"getWindow(...)");
        class_1041 window = class_10412;
        float screenWidth = window.method_4480();
        float screenHeight = window.method_4507();
        this.updateNotifications(screenHeight);
        NVGRenderer.INSTANCE.beginFrame(screenWidth, screenHeight);
        Iterable $this$forEach$iv = activeNotifications;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            UINotification notif = (UINotification)element$iv;
            boolean bl = false;
            float xOffset = notif.xOffset(screenWidth);
            float yOffset = notif.getYOffset();
            NVGRenderer.push();
            NVGRenderer.translate(xOffset, yOffset);
            notif.render();
            NVGRenderer.pop();
        }
        NVGRenderer.INSTANCE.endFrame();
    }

    /*
     * WARNING - void declaration
     */
    private final void updateNotifications(float screenHeight) {
        long currentTime = System.currentTimeMillis();
        Iterable $this$forEach$iv = activeNotifications;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            UINotification it = (UINotification)element$iv;
            boolean bl = false;
            it.checkExpiry(currentTime);
        }
        activeNotifications.removeIf(arg_0 -> NotificationManager.updateNotifications$lambda$2(NotificationManager::updateNotifications$lambda$1, arg_0));
        while (activeNotifications.size() < 3 && !((Collection)notifQueue).isEmpty()) {
            UINotification notif = notifQueue.remove(0);
            float targetY = screenHeight - (float)(activeNotifications.size() + 1) * (notif.getHeight() + 10.0f) - 10.0f;
            notif.setTargetY(targetY);
            notif.setPreviousY(targetY);
            notif.start(currentTime);
            activeNotifications.add(notif);
        }
        Iterable $this$forEachIndexed$iv = activeNotifications;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            void notif;
            int n;
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            UINotification uINotification = (UINotification)item$iv;
            int index = n;
            boolean bl = false;
            float newTargetY = screenHeight - (float)(index + 1) * (notif.getHeight() + 10.0f) - 10.0f;
            notif.moveTo(newTargetY);
        }
    }

    public final void clear() {
        notifQueue.clear();
        activeNotifications.clear();
    }

    private static final boolean updateNotifications$lambda$1(UINotification it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.isDone();
    }

    private static final boolean updateNotifications$lambda$2(Function1 $tmp0, Object p0) {
        return (Boolean)$tmp0.invoke(p0);
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        notifQueue = new ArrayList();
        activeNotifications = new ArrayList();
    }
}

