/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_1041
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.hud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_1041;
import net.minecraft.class_310;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.render.NvgEvent;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.ModuleManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0013\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\r\u0010\t\u001a\u00020\b\u00a2\u0006\u0004\b\t\u0010\u0003J\u0017\u0010\f\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\nH\u0007\u00a2\u0006\u0004\b\f\u0010\rR\u0014\u0010\u000f\u001a\u00020\u000e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u000f\u0010\u0010R\"\u0010\u0012\u001a\u00020\u00118\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0012\u0010\u0013\u001a\u0004\b\u0012\u0010\u0014\"\u0004\b\u0015\u0010\u0016\u00a8\u0006\u0017"}, d2={"Lorg/cobalt/api/hud/HudModuleManager;", "", "<init>", "()V", "", "Lorg/cobalt/api/hud/HudElement;", "getElements", "()Ljava/util/List;", "", "resetAllPositions", "Lorg/cobalt/api/event/impl/render/NvgEvent;", "event", "onRender", "(Lorg/cobalt/api/event/impl/render/NvgEvent;)V", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "", "isEditorOpen", "Z", "()Z", "setEditorOpen", "(Z)V", "cobalt"})
@SourceDebugExtension(value={"SMAP\nHudModuleManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HudModuleManager.kt\norg/cobalt/api/hud/HudModuleManager\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,57:1\n1391#2:58\n1480#2,5:59\n1915#2,2:64\n777#2:66\n873#2,2:67\n1915#2,2:69\n1915#2,2:71\n1915#2,2:73\n*S KotlinDebug\n*F\n+ 1 HudModuleManager.kt\norg/cobalt/api/hud/HudModuleManager\n*L\n17#1:58\n17#1:59,5\n20#1:64,2\n31#1:66\n31#1:67,2\n33#1:69,2\n40#1:71,2\n51#1:73,2\n*E\n"})
public final class HudModuleManager {
    @NotNull
    public static final HudModuleManager INSTANCE = new HudModuleManager();
    @NotNull
    private static final class_310 mc;
    private static volatile boolean isEditorOpen;

    private HudModuleManager() {
    }

    public final boolean isEditorOpen() {
        return isEditorOpen;
    }

    public final void setEditorOpen(boolean bl) {
        isEditorOpen = bl;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final List<HudElement> getElements() {
        void $this$flatMapTo$iv$iv;
        Iterable $this$flatMap$iv = ModuleManager.getModules();
        boolean $i$f$flatMap = false;
        Iterable iterable = $this$flatMap$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$flatMapTo = false;
        for (Object element$iv$iv : $this$flatMapTo$iv$iv) {
            Module it = (Module)element$iv$iv;
            boolean bl = false;
            Iterable list$iv$iv = it.getHudElements();
            CollectionsKt.addAll((Collection)destination$iv$iv, (Iterable)list$iv$iv);
        }
        return (List)destination$iv$iv;
    }

    public final void resetAllPositions() {
        Iterable $this$forEach$iv = this.getElements();
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            HudElement it = (HudElement)element$iv;
            boolean bl = false;
            it.resetPosition();
        }
    }

    /*
     * WARNING - void declaration
     */
    @SubscribeEvent
    public final void onRender(@NotNull NvgEvent event) {
        Pair<Float, Float> pair;
        void $this$filterTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (HudModuleManager.mc.field_1755 != null && !isEditorOpen) {
            return;
        }
        class_1041 class_10412 = mc.method_22683();
        Intrinsics.checkNotNullExpressionValue((Object)class_10412, (String)"getWindow(...)");
        class_1041 window = class_10412;
        float screenWidth = window.method_4480();
        float screenHeight = window.method_4507();
        Iterable $this$filter$iv = this.getElements();
        boolean $i$f$filter = false;
        Iterator iterator = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            HudElement it = (HudElement)element$iv$iv;
            boolean bl = false;
            if (!it.getEnabled()) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        List enabledElements = (List)destination$iv$iv;
        Iterable $this$forEach$iv = enabledElements;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            HudElement element = (HudElement)element$iv;
            boolean bl = false;
            pair = element.getScreenPosition(screenWidth, screenHeight);
            float screenX = ((Number)pair.component1()).floatValue();
            float screenY = ((Number)pair.component2()).floatValue();
            element.renderPre(screenX, screenY, element.getScale());
        }
        NVGRenderer.INSTANCE.beginFrame(screenWidth, screenHeight);
        $this$forEach$iv = enabledElements;
        $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            HudElement element = (HudElement)element$iv;
            boolean bl = false;
            pair = element.getScreenPosition(screenWidth, screenHeight);
            float screenX = ((Number)pair.component1()).floatValue();
            float screenY = ((Number)pair.component2()).floatValue();
            NVGRenderer.push();
            NVGRenderer.translate(screenX, screenY);
            NVGRenderer.scale(element.getScale(), element.getScale());
            element.render(0.0f, 0.0f, element.getScale());
            NVGRenderer.pop();
        }
        NVGRenderer.INSTANCE.endFrame();
        $this$forEach$iv = enabledElements;
        $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            HudElement element = (HudElement)element$iv;
            boolean bl = false;
            pair = element.getScreenPosition(screenWidth, screenHeight);
            float screenX = ((Number)pair.component1()).floatValue();
            float screenY = ((Number)pair.component2()).floatValue();
            element.renderPost(screenX, screenY, element.getScale());
        }
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
    }
}

