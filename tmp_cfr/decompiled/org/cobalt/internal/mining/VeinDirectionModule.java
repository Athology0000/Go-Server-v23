/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.TuplesKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.io.FilesKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_239
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_3965
 *  net.minecraft.class_638
 *  net.minecraft.class_7923
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.mining;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_638;
import net.minecraft.class_7923;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.MouseEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.WorldRenderContext;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.ActionSetting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.InfoSetting;
import org.cobalt.api.module.setting.impl.InfoType;
import org.cobalt.api.module.setting.impl.TextSetting;
import org.cobalt.api.util.ChatUtils;
import org.cobalt.api.util.render.Render3D;
import org.cobalt.internal.mining.MiningBlockRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00c8\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0006\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0007\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0002deB\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0013\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u001b\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\t\u001a\u00020\b\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0017\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\fH\u0007\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0017\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\u0011H\u0007\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0017\u0010\u0015\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\u0014H\u0007\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u000f\u0010\u0017\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u0017\u0010\u0003J\u000f\u0010\u0018\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u0018\u0010\u0003J\u000f\u0010\u0019\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u0019\u0010\u0003J\u000f\u0010\u001a\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u001a\u0010\u0003J#\u0010\u001e\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u001b\u001a\u00020\b2\b\u0010\u001d\u001a\u0004\u0018\u00010\u001cH\u0002\u00a2\u0006\u0004\b\u001e\u0010\u001fJ\u001f\u0010$\u001a\u00020#2\u0006\u0010!\u001a\u00020 2\u0006\u0010\"\u001a\u00020 H\u0002\u00a2\u0006\u0004\b$\u0010%J\u0017\u0010(\u001a\u00020&2\u0006\u0010'\u001a\u00020&H\u0002\u00a2\u0006\u0004\b(\u0010)J7\u00100\u001a\u00020\u000e2\u0006\u0010+\u001a\u00020*2\u0006\u0010!\u001a\u00020,2\u0006\u0010\"\u001a\u00020,2\u0006\u0010.\u001a\u00020-2\u0006\u0010/\u001a\u00020-H\u0002\u00a2\u0006\u0004\b0\u00101J'\u00106\u001a\u00020,2\u0006\u00102\u001a\u00020,2\u0006\u00103\u001a\u00020,2\u0006\u00105\u001a\u000204H\u0002\u00a2\u0006\u0004\b6\u00107J'\u00108\u001a\u00020-2\u0006\u00102\u001a\u00020-2\u0006\u00103\u001a\u00020-2\u0006\u00105\u001a\u000204H\u0002\u00a2\u0006\u0004\b8\u00109J\u0017\u0010:\u001a\u00020-2\u0006\u00105\u001a\u000204H\u0002\u00a2\u0006\u0004\b:\u0010;J\u0017\u0010=\u001a\u00020\b2\u0006\u0010<\u001a\u00020 H\u0002\u00a2\u0006\u0004\b=\u0010>R\u0014\u0010@\u001a\u00020?8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b@\u0010AR\u0014\u0010C\u001a\u00020B8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bC\u0010DR\u0014\u0010E\u001a\u00020B8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bE\u0010DR<\u0010I\u001a*\u0012\u0004\u0012\u00020\b\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050G0Fj\u0014\u0012\u0004\u0012\u00020\b\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050G`H8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bI\u0010JR\u0018\u0010K\u001a\u0004\u0018\u00010 8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bK\u0010LR\u0014\u0010N\u001a\u00020M8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bN\u0010OR\u0014\u0010Q\u001a\u00020P8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bQ\u0010RR\u0014\u0010S\u001a\u00020P8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bS\u0010RR\u0014\u0010U\u001a\u00020T8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bU\u0010VR&\u0010X\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020T0W0\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bX\u0010YR\u0014\u0010[\u001a\u00020Z8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b[\u0010\\R\u0014\u0010]\u001a\u00020Z8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b]\u0010\\R\u0014\u0010^\u001a\u00020&8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b^\u0010_R\u0014\u0010`\u001a\u00020&8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b`\u0010_R\u0014\u0010b\u001a\u00020a8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bb\u0010c\u00a8\u0006f"}, d2={"Lorg/cobalt/internal/mining/VeinDirectionModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "", "Lorg/cobalt/internal/mining/VeinDirectionModule$VeinFlow;", "getFlows", "()Ljava/util/List;", "", "blockId", "getFlowsForVein", "(Ljava/lang/String;)Ljava/util/List;", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "Lorg/cobalt/api/event/impl/client/MouseEvent$RightClick;", "onRightClick", "(Lorg/cobalt/api/event/impl/client/MouseEvent$RightClick;)V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "onRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "updateTexts", "loadFlows", "saveFlows", "saveFlowsText", "type", "Lcom/google/gson/JsonObject;", "obj", "parseFlowEntry", "(Ljava/lang/String;Lcom/google/gson/JsonObject;)Lorg/cobalt/internal/mining/VeinDirectionModule$VeinFlow;", "Lnet/minecraft/class_2338;", "start", "end", "Lorg/cobalt/internal/mining/VeinDirectionModule$DetectedVein;", "detectVein", "(Lnet/minecraft/class_2338;Lnet/minecraft/class_2338;)Lorg/cobalt/internal/mining/VeinDirectionModule$DetectedVein;", "", "volume", "computeStride", "(I)I", "Lorg/cobalt/api/event/impl/render/WorldRenderContext;", "context", "Lnet/minecraft/class_243;", "Ljava/awt/Color;", "startColor", "endColor", "drawGradientLine", "(Lorg/cobalt/api/event/impl/render/WorldRenderContext;Lnet/minecraft/class_243;Lnet/minecraft/class_243;Ljava/awt/Color;Ljava/awt/Color;)V", "a", "b", "", "t", "lerpVec", "(Lnet/minecraft/class_243;Lnet/minecraft/class_243;D)Lnet/minecraft/class_243;", "lerpColor", "(Ljava/awt/Color;Ljava/awt/Color;D)Ljava/awt/Color;", "gradientColor", "(D)Ljava/awt/Color;", "pos", "coord", "(Lnet/minecraft/class_2338;)Ljava/lang/String;", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Ljava/io/File;", "configFile", "Ljava/io/File;", "textFile", "Ljava/util/LinkedHashMap;", "", "Lkotlin/collections/LinkedHashMap;", "flowsByType", "Ljava/util/LinkedHashMap;", "pendingStart", "Lnet/minecraft/class_2338;", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "info", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "recordOnRightClick", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "renderFlows", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "pendingText", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "Lkotlin/Pair;", "veinTypeSlots", "Ljava/util/List;", "Lorg/cobalt/api/module/setting/impl/ActionSetting;", "removeAllAction", "Lorg/cobalt/api/module/setting/impl/ActionSetting;", "cancelPendingAction", "MAX_DETECT_SAMPLES", "I", "GRADIENT_LINE_SEGMENTS", "", "FLOW_LINE_THICKNESS", "F", "VeinFlow", "DetectedVein", "cobalt"})
@SourceDebugExtension(value={"SMAP\nVeinDirectionModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VeinDirectionModule.kt\norg/cobalt/internal/mining/VeinDirectionModule\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 5 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 6 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,408:1\n383#2,7:409\n363#3,7:416\n1924#3,3:423\n1915#3,2:427\n1642#3,10:429\n1915#3:439\n1916#3:441\n1652#3:442\n1915#3,2:444\n1924#3,3:448\n1586#3:455\n1661#3,3:456\n1915#3,2:459\n1#4:426\n1#4:440\n221#5:443\n222#5:446\n221#5:447\n222#5:451\n3938#6:452\n4474#6,2:453\n*S KotlinDebug\n*F\n+ 1 VeinDirectionModule.kt\norg/cobalt/internal/mining/VeinDirectionModule\n*L\n161#1:409,7\n162#1:416,7\n187#1:423,3\n213#1:427,2\n241#1:429,10\n241#1:439\n241#1:441\n241#1:442\n258#1:444,2\n285#1:448,3\n76#1:455\n76#1:456,3\n107#1:459,2\n241#1:440\n256#1:443\n256#1:446\n283#1:447\n283#1:451\n75#1:452\n75#1:453,2\n*E\n"})
public final class VeinDirectionModule
extends Module {
    @NotNull
    public static final VeinDirectionModule INSTANCE;
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final File configFile;
    @NotNull
    private static final File textFile;
    @NotNull
    private static final LinkedHashMap<String, List<VeinFlow>> flowsByType;
    @Nullable
    private static class_2338 pendingStart;
    @NotNull
    private static final InfoSetting info;
    @NotNull
    private static final CheckboxSetting recordOnRightClick;
    @NotNull
    private static final CheckboxSetting renderFlows;
    @NotNull
    private static final TextSetting pendingText;
    @NotNull
    private static final List<Pair<String, TextSetting>> veinTypeSlots;
    @NotNull
    private static final ActionSetting removeAllAction;
    @NotNull
    private static final ActionSetting cancelPendingAction;
    private static final int MAX_DETECT_SAMPLES = 4096;
    private static final int GRADIENT_LINE_SEGMENTS = 12;
    private static final float FLOW_LINE_THICKNESS = 2.4f;

    private VeinDirectionModule() {
        super("Vein Direction Setter");
    }

    @NotNull
    public final List<VeinFlow> getFlows() {
        Collection<List<VeinFlow>> collection = flowsByType.values();
        Intrinsics.checkNotNullExpressionValue(collection, (String)"<get-values>(...)");
        return CollectionsKt.flatten((Iterable)collection);
    }

    @NotNull
    public final List<VeinFlow> getFlowsForVein(@NotNull String blockId) {
        List<VeinFlow> flows;
        Collection collection;
        Intrinsics.checkNotNullParameter((Object)blockId, (String)"blockId");
        String type = MiningBlockRegistry.INSTANCE.getBLOCK_ID_TO_TYPE().get(blockId);
        CharSequence charSequence = type;
        if (!(charSequence == null || StringsKt.isBlank((CharSequence)charSequence)) && !((collection = (Collection)(flows = flowsByType.get(type))) == null || collection.isEmpty())) {
            return CollectionsKt.toList((Iterable)flows);
        }
        return this.getFlows();
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)recordOnRightClick.getValue()).booleanValue() && pendingStart != null) {
            pendingStart = null;
            this.updateTexts();
        }
    }

    /*
     * WARNING - void declaration
     */
    @SubscribeEvent
    public final void onRightClick(@NotNull MouseEvent.RightClick event) {
        String string;
        int existingIdx;
        List flows;
        VeinFlow flow;
        DetectedVein detected;
        class_2338 start;
        class_2338 clicked;
        block10: {
            int n;
            Object object;
            void $this$getOrPut$iv;
            Intrinsics.checkNotNullParameter((Object)event, (String)"event");
            if (!((Boolean)recordOnRightClick.getValue()).booleanValue()) {
                return;
            }
            class_239 hit = VeinDirectionModule.mc.field_1765;
            if (!(hit instanceof class_3965) || ((class_3965)hit).method_17783() != class_239.class_240.field_1332) {
                return;
            }
            class_2338 class_23382 = ((class_3965)hit).method_17777();
            Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"getBlockPos(...)");
            clicked = class_23382;
            start = pendingStart;
            if (start == null) {
                pendingStart = clicked;
                this.updateTexts();
                ChatUtils.sendMessage("Vein flow start set at " + this.coord(clicked) + ". Right-click the end block.");
                return;
            }
            if (Intrinsics.areEqual((Object)start, (Object)clicked)) {
                ChatUtils.sendMessage("Vein flow end cannot be the same as start.");
                return;
            }
            detected = this.detectVein(start, clicked);
            flow = new VeinFlow(start, clicked, detected.getType(), detected.getBlockId());
            Map map = flowsByType;
            String key$iv = detected.getType();
            boolean $i$f$getOrPut = false;
            Object value$iv = $this$getOrPut$iv.get(key$iv);
            if (value$iv == null) {
                boolean bl = false;
                List answer$iv = new ArrayList();
                $this$getOrPut$iv.put(key$iv, answer$iv);
                object = answer$iv;
            } else {
                object = value$iv;
            }
            List $this$indexOfFirst$iv = flows = (List)object;
            boolean $i$f$indexOfFirst = false;
            int index$iv = 0;
            for (Object item$iv : $this$indexOfFirst$iv) {
                VeinFlow it = (VeinFlow)item$iv;
                boolean bl = false;
                if (Intrinsics.areEqual((Object)it.getStart(), (Object)flow.getStart()) && Intrinsics.areEqual((Object)it.getEnd(), (Object)flow.getEnd()) && Intrinsics.areEqual((Object)it.getBlockId(), (Object)flow.getBlockId())) {
                    n = index$iv;
                    break block10;
                }
                ++index$iv;
            }
            n = existingIdx = -1;
        }
        if (existingIdx >= 0) {
            flows.set(existingIdx, flow);
            string = "updated";
        } else {
            ((Collection)flows).add(flow);
            string = "added";
        }
        String action = string;
        pendingStart = null;
        this.saveFlows();
        this.updateTexts();
        ChatUtils.sendMessage("Vein flow " + action + " for " + detected.getType() + " (#" + flows.size() + "): " + this.coord(start) + " -> " + this.coord(clicked) + ".");
    }

    /*
     * WARNING - void declaration
     */
    @SubscribeEvent
    public final void onRender(@NotNull WorldRenderEvent.Last event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)renderFlows.getValue()).booleanValue()) {
            return;
        }
        if (flowsByType.isEmpty()) {
            return;
        }
        List<VeinFlow> typeList = this.getFlows();
        int segmentCount = RangesKt.coerceAtLeast((int)(typeList.size() - 1), (int)1);
        Iterable $this$forEachIndexed$iv = typeList;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            void flow;
            int n;
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            VeinFlow veinFlow = (VeinFlow)item$iv;
            int index = n;
            boolean bl = false;
            double t = (double)index / (double)segmentCount;
            Color startColor = INSTANCE.gradientColor(t);
            Color endColor = INSTANCE.gradientColor(RangesKt.coerceAtMost((double)(t + 0.22), (double)1.0));
            class_243 startVec = new class_243((double)flow.getStart().method_10263() + 0.5, (double)flow.getStart().method_10264() + 0.5, (double)flow.getStart().method_10260() + 0.5);
            class_243 endVec = new class_243((double)flow.getEnd().method_10263() + 0.5, (double)flow.getEnd().method_10264() + 0.5, (double)flow.getEnd().method_10260() + 0.5);
            INSTANCE.drawGradientLine(event.getContext(), startVec, endVec, startColor, endColor);
            class_238 startBox = new class_238((double)flow.getStart().method_10263(), (double)flow.getStart().method_10264(), (double)flow.getStart().method_10260(), (double)flow.getStart().method_10263() + 1.0, (double)flow.getStart().method_10264() + 1.0, (double)flow.getStart().method_10260() + 1.0);
            class_238 endBox = new class_238((double)flow.getEnd().method_10263(), (double)flow.getEnd().method_10264(), (double)flow.getEnd().method_10260(), (double)flow.getEnd().method_10263() + 1.0, (double)flow.getEnd().method_10264() + 1.0, (double)flow.getEnd().method_10260() + 1.0);
            Render3D.drawBox(event.getContext(), startBox, startColor, true);
            Render3D.drawBox(event.getContext(), endBox, endColor, true);
        }
    }

    /*
     * WARNING - void declaration
     */
    private final void updateTexts() {
        Object object;
        TextSetting textSetting;
        block10: {
            block9: {
                void it;
                textSetting = pendingText;
                object = pendingStart;
                if (object == null) break block9;
                class_2338 class_23382 = object;
                TextSetting textSetting2 = textSetting;
                boolean bl = false;
                String string = INSTANCE.coord((class_2338)it);
                textSetting = textSetting2;
                object = string;
                if (string != null) break block10;
            }
            object = "-";
        }
        textSetting.setValue(object);
        Iterable $this$forEach$iv = veinTypeSlots;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            Object object2;
            Pair pair = (Pair)element$iv;
            boolean bl = false;
            String type = (String)pair.component1();
            TextSetting setting = (TextSetting)pair.component2();
            List list = flowsByType.get(type);
            if (list == null) {
                list = CollectionsKt.emptyList();
            }
            List flows = list;
            if (flows.isEmpty()) {
                object2 = "Not set";
            } else if (flows.size() == 1) {
                object2 = "1 saved | " + INSTANCE.coord(((VeinFlow)flows.get(0)).getStart()) + " -> " + INSTANCE.coord(((VeinFlow)flows.get(0)).getEnd());
            } else {
                VeinFlow last = (VeinFlow)CollectionsKt.last((List)flows);
                object2 = flows.size() + " saved | last " + INSTANCE.coord(last.getStart()) + " -> " + INSTANCE.coord(last.getEnd());
            }
            setting.setValue(object2);
        }
        this.saveFlowsText();
    }

    /*
     * WARNING - void declaration
     */
    private final void loadFlows() {
        Object object3;
        String text;
        Iterator iterator;
        if (!configFile.exists()) {
            return;
        }
        Object object2 = this;
        try {
            VeinDirectionModule $this$loadFlows_u24lambda_u240 = object2;
            boolean bl = false;
            iterator = Result.constructor-impl((Object)FilesKt.readText$default((File)configFile, null, (int)1, null));
        }
        catch (Throwable bl) {
            iterator = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
        }
        object2 = iterator;
        String string = (String)(Result.isFailure-impl((Object)object2) ? null : object2);
        String string2 = string != null ? ((Object)StringsKt.trim((CharSequence)string)).toString() : null;
        if (string2 == null) {
            string2 = "";
        }
        if (((CharSequence)(text = string2)).length() == 0) {
            return;
        }
        iterator = this;
        try {
            VeinDirectionModule $this$loadFlows_u24lambda_u241 = (VeinDirectionModule)((Object)iterator);
            boolean bl = false;
            object3 = Result.constructor-impl((Object)JsonParser.parseString((String)text).getAsJsonObject());
        }
        catch (Throwable bl) {
            object3 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
        }
        iterator = object3;
        JsonObject jsonObject = (JsonObject)(Result.isFailure-impl((Object)iterator) ? null : iterator);
        if (jsonObject == null) {
            return;
        }
        JsonObject root = jsonObject;
        JsonObject jsonObject2 = root.getAsJsonObject("flowsByType");
        if (jsonObject2 == null) {
            return;
        }
        JsonObject flowsObj = jsonObject2;
        flowsByType.clear();
        for (Object object3 : flowsObj.entrySet()) {
            List loaded;
            List list;
            Intrinsics.checkNotNull((Object)object3);
            String type = (String)object3.getKey();
            JsonElement element = (JsonElement)object3.getValue();
            if (element.isJsonArray()) {
                void $this$mapNotNullTo$iv$iv;
                void $this$mapNotNull$iv;
                JsonArray jsonArray = element.getAsJsonArray();
                Intrinsics.checkNotNullExpressionValue((Object)jsonArray, (String)"getAsJsonArray(...)");
                var9_13 = (Iterable)jsonArray;
                boolean $i$f$mapNotNull = false;
                void var11_16 = $this$mapNotNull$iv;
                Collection destination$iv$iv = new ArrayList();
                boolean $i$f$mapNotNullTo = false;
                void $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
                boolean $i$f$forEach = false;
                Iterator iterator2 = $this$forEach$iv$iv$iv.iterator();
                while (iterator2.hasNext()) {
                    VeinFlow it$iv$iv;
                    Object object4;
                    Object object5;
                    Object element$iv$iv$iv;
                    Object element$iv$iv = element$iv$iv$iv = iterator2.next();
                    boolean bl = false;
                    JsonElement it = (JsonElement)element$iv$iv;
                    boolean bl2 = false;
                    Intrinsics.checkNotNull((Object)type);
                    Object object6 = INSTANCE;
                    String string3 = type;
                    VeinDirectionModule veinDirectionModule = INSTANCE;
                    try {
                        VeinDirectionModule $this$loadFlows_u24lambda_u242_u240 = object6;
                        boolean bl3 = false;
                        object5 = Result.constructor-impl((Object)it.getAsJsonObject());
                    }
                    catch (Throwable throwable) {
                        object5 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
                    }
                    object6 = object4 = object5;
                    if (veinDirectionModule.parseFlowEntry(string3, (JsonObject)(Result.isFailure-impl((Object)object6) ? null : object6)) == null) continue;
                    boolean bl4 = false;
                    destination$iv$iv.add(it$iv$iv);
                }
                list = (List)destination$iv$iv;
            } else if (element.isJsonObject()) {
                Object object7;
                Object object8;
                Intrinsics.checkNotNull((Object)type);
                var9_13 = this;
                String string4 = type;
                VeinDirectionModule veinDirectionModule = this;
                try {
                    VeinDirectionModule $this$loadFlows_u24lambda_u243 = (VeinDirectionModule)var9_13;
                    boolean bl = false;
                    object8 = Result.constructor-impl((Object)element.getAsJsonObject());
                }
                catch (Throwable throwable) {
                    object8 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
                }
                var9_13 = object7 = object8;
                list = CollectionsKt.listOfNotNull((Object)veinDirectionModule.parseFlowEntry(string4, (JsonObject)(Result.isFailure-impl((Object)var9_13) ? null : var9_13)));
            } else {
                list = CollectionsKt.emptyList();
            }
            if (!(!((Collection)(loaded = list)).isEmpty())) continue;
            ((Map)flowsByType).put(type, CollectionsKt.toMutableList((Collection)loaded));
        }
    }

    private final void saveFlows() {
        if (!configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdirs();
        }
        JsonObject root = new JsonObject();
        JsonObject flowsObj = new JsonObject();
        Map $this$forEach$iv = flowsByType;
        boolean $i$f$forEach = false;
        Iterator iterator = $this$forEach$iv.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry element$iv;
            Map.Entry entry = element$iv = iterator.next();
            boolean bl = false;
            String type = (String)entry.getKey();
            List flows = (List)entry.getValue();
            JsonArray array = new JsonArray();
            Iterable $this$forEach$iv2 = flows;
            boolean $i$f$forEach2 = false;
            for (Object element$iv2 : $this$forEach$iv2) {
                VeinFlow flow = (VeinFlow)element$iv2;
                boolean bl2 = false;
                JsonObject obj = new JsonObject();
                obj.addProperty("sx", (Number)flow.getStart().method_10263());
                obj.addProperty("sy", (Number)flow.getStart().method_10264());
                obj.addProperty("sz", (Number)flow.getStart().method_10260());
                obj.addProperty("ex", (Number)flow.getEnd().method_10263());
                obj.addProperty("ey", (Number)flow.getEnd().method_10264());
                obj.addProperty("ez", (Number)flow.getEnd().method_10260());
                obj.addProperty("blockId", flow.getBlockId());
                array.add((JsonElement)obj);
            }
            flowsObj.add(type, (JsonElement)array);
        }
        root.add("flowsByType", (JsonElement)flowsObj);
        VeinDirectionModule veinDirectionModule = this;
        try {
            VeinDirectionModule $this$saveFlows_u24lambda_u241 = veinDirectionModule;
            boolean bl = false;
            String string = root.toString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
            FilesKt.writeText$default((File)configFile, (String)string, null, (int)2, null);
            Object object = Result.constructor-impl((Object)Unit.INSTANCE);
        }
        catch (Throwable throwable) {
            Object object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        this.saveFlowsText();
    }

    /*
     * WARNING - void declaration
     */
    private final void saveFlowsText() {
        Object object;
        if (!textFile.getParentFile().exists()) {
            textFile.getParentFile().mkdirs();
        }
        Object object2 = new StringBuilder();
        StringBuilder $this$saveFlowsText_u24lambda_u240 = object2;
        boolean bl = false;
        $this$saveFlowsText_u24lambda_u240.append("# Vein Direction Setter - one slot per vein type").append('\n');
        if (flowsByType.isEmpty()) {
            $this$saveFlowsText_u24lambda_u240.append("EMPTY").append('\n');
        } else {
            Map $this$forEach$iv = flowsByType;
            boolean $i$f$forEach = false;
            Iterator iterator = $this$forEach$iv.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry element$iv;
                Map.Entry entry = element$iv = iterator.next();
                boolean bl2 = false;
                String type = (String)entry.getKey();
                List flows = (List)entry.getValue();
                $this$saveFlowsText_u24lambda_u240.append(type + " (" + flows.size() + ")").append('\n');
                Iterable $this$forEachIndexed$iv = flows;
                boolean $i$f$forEachIndexed = false;
                int index$iv = 0;
                for (Object item$iv : $this$forEachIndexed$iv) {
                    void flow;
                    int n;
                    if ((n = index$iv++) < 0) {
                        CollectionsKt.throwIndexOverflow();
                    }
                    VeinFlow veinFlow = (VeinFlow)item$iv;
                    int index = n;
                    boolean bl3 = false;
                    $this$saveFlowsText_u24lambda_u240.append("  " + (index + 1) + ". " + INSTANCE.coord(flow.getStart()) + " -> " + INSTANCE.coord(flow.getEnd()) + " [" + flow.getBlockId() + "]").append('\n');
                }
            }
        }
        String content = ((StringBuilder)object2).toString();
        object2 = this;
        try {
            VeinDirectionModule $this$saveFlowsText_u24lambda_u241 = (VeinDirectionModule)object2;
            boolean bl4 = false;
            FilesKt.writeText$default((File)textFile, (String)content, null, (int)2, null);
            object = Result.constructor-impl((Object)Unit.INSTANCE);
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
    }

    private final VeinFlow parseFlowEntry(String type, JsonObject obj) {
        Object object;
        int ez;
        int ey;
        int ex;
        int sz;
        int sy;
        int sx;
        block12: {
            block11: {
                JsonElement jsonElement;
                if (obj == null) {
                    return null;
                }
                JsonElement jsonElement2 = obj.get("sx");
                if (jsonElement2 == null) {
                    return null;
                }
                sx = jsonElement2.getAsInt();
                JsonElement jsonElement3 = obj.get("sy");
                if (jsonElement3 == null) {
                    return null;
                }
                sy = jsonElement3.getAsInt();
                JsonElement jsonElement4 = obj.get("sz");
                if (jsonElement4 == null) {
                    return null;
                }
                sz = jsonElement4.getAsInt();
                JsonElement jsonElement5 = obj.get("ex");
                if (jsonElement5 == null) {
                    return null;
                }
                ex = jsonElement5.getAsInt();
                JsonElement jsonElement6 = obj.get("ey");
                if (jsonElement6 == null) {
                    return null;
                }
                ey = jsonElement6.getAsInt();
                JsonElement jsonElement7 = obj.get("ez");
                if (jsonElement7 == null) {
                    return null;
                }
                ez = jsonElement7.getAsInt();
                object = obj.get("blockId");
                if (object == null) break block11;
                JsonElement it = jsonElement = object;
                boolean bl = false;
                object = it.isJsonPrimitive() ? jsonElement : null;
                if (object != null && (object = object.getAsString()) != null) break block12;
            }
            Set<String> set = MiningBlockRegistry.INSTANCE.getTYPE_TO_BLOCK_IDS().get(type);
            object = set != null ? (String)CollectionsKt.firstOrNull((Iterable)set) : null;
            if (object == null) {
                object = "";
            }
        }
        Object blockId = object;
        return new VeinFlow(new class_2338(sx, sy, sz), new class_2338(ex, ey, ez), type, (String)blockId);
    }

    private final DetectedVein detectVein(class_2338 start, class_2338 end) {
        String bestType;
        Object v5;
        Object object;
        Iterator iterator;
        String type;
        String id;
        class_638 class_6382 = VeinDirectionModule.mc.field_1687;
        if (class_6382 == null) {
            return new DetectedVein("Unknown", "");
        }
        class_638 level2 = class_6382;
        int minX = Math.min(start.method_10263(), end.method_10263());
        int maxX = Math.max(start.method_10263(), end.method_10263());
        int minY = Math.min(start.method_10264(), end.method_10264());
        int maxY = Math.max(start.method_10264(), end.method_10264());
        int minZ = Math.min(start.method_10260(), end.method_10260());
        int maxZ = Math.max(start.method_10260(), end.method_10260());
        int volume = (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
        int stride = this.computeStride(volume);
        HashMap typeCounts = new HashMap();
        HashMap idCounts = new HashMap();
        for (int y = minY; y <= maxY; y += stride) {
            for (int x = minX; x <= maxX; x += stride) {
                for (int z = minZ; z <= maxZ; z += stride) {
                    Intrinsics.checkNotNullExpressionValue((Object)class_7923.field_41175.method_10221((Object)level2.method_8320(new class_2338(x, y, z)).method_26204()).toString(), (String)"toString(...)");
                    type = MiningBlockRegistry.INSTANCE.getBLOCK_ID_TO_TYPE().get(id);
                    if (type == null) continue;
                    iterator = typeCounts;
                    object = type;
                    Integer n = (Integer)typeCounts.get(type);
                    Integer n2 = (n != null ? n : 0) + 1;
                    iterator.put(object, n2);
                    iterator = idCounts;
                    Integer n3 = (Integer)idCounts.get(id);
                    object = (n3 != null ? n3 : 0) + 1;
                    iterator.put(id, object);
                }
            }
        }
        Object[] z = new class_2338[]{start, end};
        for (class_2338 pos : CollectionsKt.listOf((Object[])z)) {
            Intrinsics.checkNotNullExpressionValue((Object)class_7923.field_41175.method_10221((Object)level2.method_8320(pos).method_26204()).toString(), (String)"toString(...)");
            if (MiningBlockRegistry.INSTANCE.getBLOCK_ID_TO_TYPE().get(id) == null) continue;
            iterator = typeCounts;
            Integer n = (Integer)typeCounts.get(type);
            object = (n != null ? n : 0) + 1;
            iterator.put(type, object);
            iterator = idCounts;
            Integer n4 = (Integer)idCounts.get(id);
            object = (n4 != null ? n4 : 0) + 1;
            iterator.put(id, object);
        }
        Iterable iterable = ((Map)typeCounts).entrySet();
        iterator = iterable.iterator();
        if (!iterator.hasNext()) {
            v5 = null;
        } else {
            object = iterator.next();
            if (!iterator.hasNext()) {
                v5 = object;
            } else {
                Map.Entry it22 = (Map.Entry)object;
                boolean bl2 = false;
                int it22 = ((Number)it22.getValue()).intValue();
                do {
                    Object bl2 = iterator.next();
                    Map.Entry it32 = (Map.Entry)bl2;
                    boolean $i$a$-maxByOrNull-VeinDirectionModule$detectVein$bestType$2 = false;
                    int it32 = ((Number)it32.getValue()).intValue();
                    if (it22 >= it32) continue;
                    object = bl2;
                    it22 = it32;
                } while (iterator.hasNext());
                v5 = object;
            }
        }
        Map.Entry entry = v5;
        String string = bestType = entry != null ? (String)entry.getKey() : null;
        if (bestType != null) {
            Object v8;
            Map map = idCounts;
            String string2 = bestType;
            iterable = map.entrySet();
            iterator = iterable.iterator();
            if (!iterator.hasNext()) {
                v8 = null;
            } else {
                object = iterator.next();
                if (!iterator.hasNext()) {
                    v8 = object;
                } else {
                    Map.Entry it = (Map.Entry)object;
                    boolean bl = false;
                    int n = ((Number)it.getValue()).intValue();
                    do {
                        Object e = iterator.next();
                        Map.Entry it2 = (Map.Entry)e;
                        $i$a$-maxByOrNull-VeinDirectionModule$detectVein$1 = false;
                        int n5 = ((Number)it2.getValue()).intValue();
                        if (n >= n5) continue;
                        object = e;
                        n = n5;
                    } while (iterator.hasNext());
                    v8 = object;
                }
            }
            Map.Entry entry2 = v8;
            String string3 = entry2 != null ? (String)entry2.getKey() : null;
            if (string3 == null) {
                string3 = "";
            }
            String string4 = string3;
            String string5 = string2;
            return new DetectedVein(string5, string4);
        }
        String string6 = class_7923.field_41175.method_10221((Object)level2.method_8320(start).method_26204()).toString();
        Intrinsics.checkNotNullExpressionValue((Object)string6, (String)"toString(...)");
        String startId = string6;
        String string7 = MiningBlockRegistry.INSTANCE.getBLOCK_ID_TO_TYPE().get(startId);
        if (string7 == null) {
            string7 = "Unknown";
        }
        return new DetectedVein(string7, startId);
    }

    private final int computeStride(int volume) {
        int stride = 1;
        while (volume / (stride * stride * stride) > 4096) {
            ++stride;
        }
        return RangesKt.coerceAtLeast((int)stride, (int)1);
    }

    private final void drawGradientLine(WorldRenderContext context, class_243 start, class_243 end, Color startColor, Color endColor) {
        for (int i = 0; i < 12; ++i) {
            double t0 = (double)i / (double)12;
            double t1 = (double)(i + 1) / (double)12;
            Render3D.drawLine(context, this.lerpVec(start, end, t0), this.lerpVec(start, end, t1), this.lerpColor(startColor, endColor, t0), true, 2.4f);
        }
    }

    private final class_243 lerpVec(class_243 a, class_243 b, double t) {
        return new class_243(a.field_1352 + (b.field_1352 - a.field_1352) * t, a.field_1351 + (b.field_1351 - a.field_1351) * t, a.field_1350 + (b.field_1350 - a.field_1350) * t);
    }

    private final Color lerpColor(Color a, Color b, double t) {
        double tt = RangesKt.coerceIn((double)t, (double)0.0, (double)1.0);
        return new Color(RangesKt.coerceIn((int)((int)((double)a.getRed() + (double)(b.getRed() - a.getRed()) * tt)), (int)0, (int)255), RangesKt.coerceIn((int)((int)((double)a.getGreen() + (double)(b.getGreen() - a.getGreen()) * tt)), (int)0, (int)255), RangesKt.coerceIn((int)((int)((double)a.getBlue() + (double)(b.getBlue() - a.getBlue()) * tt)), (int)0, (int)255), RangesKt.coerceIn((int)((int)((double)a.getAlpha() + (double)(b.getAlpha() - a.getAlpha()) * tt)), (int)0, (int)255));
    }

    private final Color gradientColor(double t) {
        double c = RangesKt.coerceIn((double)t, (double)0.0, (double)1.0);
        return new Color(RangesKt.coerceIn((int)((int)((double)30 + (double)225 * c)), (int)0, (int)255), RangesKt.coerceIn((int)((int)((double)220 + (double)-100 * c)), (int)0, (int)255), RangesKt.coerceIn((int)((int)((double)255 + (double)-215 * c)), (int)0, (int)255), 255);
    }

    private final String coord(class_2338 pos) {
        return pos.method_10263() + "," + pos.method_10264() + "," + pos.method_10260();
    }

    private static final Unit removeAllAction$lambda$0() {
        flowsByType.clear();
        INSTANCE.saveFlows();
        INSTANCE.updateTexts();
        ChatUtils.sendMessage("All vein flows cleared.");
        return Unit.INSTANCE;
    }

    private static final Unit cancelPendingAction$lambda$0() {
        pendingStart = null;
        INSTANCE.updateTexts();
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    static {
        void $this$mapTo$iv$iv;
        Setting[] $this$filterTo$iv$iv;
        INSTANCE = new VeinDirectionModule();
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        configFile = new File(VeinDirectionModule.mc.field_1697, "config/cobalt/vein_directions.json");
        textFile = new File(VeinDirectionModule.mc.field_1697, "config/cobalt/vein_directions.txt");
        flowsByType = new LinkedHashMap();
        info = new InfoSetting("Vein Flow", "Record many mining directions per vein type: right-click start block, then end block.", InfoType.INFO);
        recordOnRightClick = new CheckboxSetting("Right Click Vein Directions", "When enabled, right-click two blocks to add a saved direction for that vein type.", false);
        renderFlows = new CheckboxSetting("Render Vein Flows", "Render saved vein flow lines in-world.", true);
        pendingText = new TextSetting("Pending Start", "Current pending start coordinate.", "-");
        String[] $this$filter$iv = MiningBlockRegistry.INSTANCE.getBLOCK_TYPES();
        boolean $i$f$filter = false;
        String[] stringArray = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        int n = $this$filterTo$iv$iv.length;
        for (int i = 0; i < n; ++i) {
            void settingArray;
            void it = settingArray = $this$filterTo$iv$iv[i];
            boolean bl = false;
            if (!(!Intrinsics.areEqual((Object)it, (Object)"Custom"))) continue;
            destination$iv$iv.add(settingArray);
        }
        Setting[] $this$map$iv = (Setting[])((List)destination$iv$iv);
        boolean $i$f$map = false;
        $this$filterTo$iv$iv = $this$map$iv;
        destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            String string = (String)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(TuplesKt.to((Object)string, (Object)new TextSetting(string, "Saved flow directions for " + string + ".", "Not set")));
        }
        veinTypeSlots = (List)destination$iv$iv;
        removeAllAction = new ActionSetting("Clear All", "Clear all saved vein flows.", "Clear", null, VeinDirectionModule::removeAllAction$lambda$0, 8, null);
        cancelPendingAction = new ActionSetting("Cancel Pending", "Cancel the current pending start point.", "Cancel", null, VeinDirectionModule::cancelPendingAction$lambda$0, 8, null);
        $this$map$iv = new Setting[]{info, recordOnRightClick, renderFlows, pendingText};
        INSTANCE.addSetting($this$map$iv);
        Iterable $this$forEach$iv = veinTypeSlots;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            Pair pair = (Pair)element$iv;
            boolean bl = false;
            TextSetting setting = (TextSetting)pair.component2();
            Setting[] settingArray = new Setting[]{setting};
            INSTANCE.addSetting(settingArray);
        }
        Setting[] settingArray = new Setting[]{removeAllAction, cancelPendingAction};
        INSTANCE.addSetting(settingArray);
        INSTANCE.loadFlows();
        INSTANCE.updateTexts();
        EventBus.register(INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0007\b\u0082\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\bJ$\u0010\n\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\n\u0010\u000bJ\u001b\u0010\u000e\u001a\u00020\r2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0011\u0010\u0011\u001a\u00020\u0010H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0011\u0010\u0013\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0013\u0010\bR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0014\u001a\u0004\b\u0015\u0010\bR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0014\u001a\u0004\b\u0016\u0010\b\u00a8\u0006\u0017"}, d2={"Lorg/cobalt/internal/mining/VeinDirectionModule$DetectedVein;", "", "", "type", "blockId", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", "component1", "()Ljava/lang/String;", "component2", "copy", "(Ljava/lang/String;Ljava/lang/String;)Lorg/cobalt/internal/mining/VeinDirectionModule$DetectedVein;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getType", "getBlockId", "cobalt"})
    private static final class DetectedVein {
        @NotNull
        private final String type;
        @NotNull
        private final String blockId;

        public DetectedVein(@NotNull String type, @NotNull String blockId) {
            Intrinsics.checkNotNullParameter((Object)type, (String)"type");
            Intrinsics.checkNotNullParameter((Object)blockId, (String)"blockId");
            this.type = type;
            this.blockId = blockId;
        }

        @NotNull
        public final String getType() {
            return this.type;
        }

        @NotNull
        public final String getBlockId() {
            return this.blockId;
        }

        @NotNull
        public final String component1() {
            return this.type;
        }

        @NotNull
        public final String component2() {
            return this.blockId;
        }

        @NotNull
        public final DetectedVein copy(@NotNull String type, @NotNull String blockId) {
            Intrinsics.checkNotNullParameter((Object)type, (String)"type");
            Intrinsics.checkNotNullParameter((Object)blockId, (String)"blockId");
            return new DetectedVein(type, blockId);
        }

        public static /* synthetic */ DetectedVein copy$default(DetectedVein detectedVein, String string, String string2, int n, Object object) {
            if ((n & 1) != 0) {
                string = detectedVein.type;
            }
            if ((n & 2) != 0) {
                string2 = detectedVein.blockId;
            }
            return detectedVein.copy(string, string2);
        }

        @NotNull
        public String toString() {
            return "DetectedVein(type=" + this.type + ", blockId=" + this.blockId + ")";
        }

        public int hashCode() {
            int result = this.type.hashCode();
            result = result * 31 + this.blockId.hashCode();
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof DetectedVein)) {
                return false;
            }
            DetectedVein detectedVein = (DetectedVein)other;
            if (!Intrinsics.areEqual((Object)this.type, (Object)detectedVein.type)) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.blockId, (Object)detectedVein.blockId);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\n\b\u0086\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\u000bJ\u0010\u0010\r\u001a\u00020\u0005H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\u0005H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\u000eJ8\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u0005H\u00c6\u0001\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u001b\u0010\u0014\u001a\u00020\u00132\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u0011\u0010\u0017\u001a\u00020\u0016H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u0011\u0010\u0019\u001a\u00020\u0005H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0019\u0010\u000eR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001a\u001a\u0004\b\u001b\u0010\u000bR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u001a\u001a\u0004\b\u001c\u0010\u000bR\u0017\u0010\u0006\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001d\u001a\u0004\b\u001e\u0010\u000eR\u0017\u0010\u0007\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u001d\u001a\u0004\b\u001f\u0010\u000e\u00a8\u0006 "}, d2={"Lorg/cobalt/internal/mining/VeinDirectionModule$VeinFlow;", "", "Lnet/minecraft/class_2338;", "start", "end", "", "veinType", "blockId", "<init>", "(Lnet/minecraft/class_2338;Lnet/minecraft/class_2338;Ljava/lang/String;Ljava/lang/String;)V", "component1", "()Lnet/minecraft/class_2338;", "component2", "component3", "()Ljava/lang/String;", "component4", "copy", "(Lnet/minecraft/class_2338;Lnet/minecraft/class_2338;Ljava/lang/String;Ljava/lang/String;)Lorg/cobalt/internal/mining/VeinDirectionModule$VeinFlow;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Lnet/minecraft/class_2338;", "getStart", "getEnd", "Ljava/lang/String;", "getVeinType", "getBlockId", "cobalt"})
    public static final class VeinFlow {
        @NotNull
        private final class_2338 start;
        @NotNull
        private final class_2338 end;
        @NotNull
        private final String veinType;
        @NotNull
        private final String blockId;

        public VeinFlow(@NotNull class_2338 start, @NotNull class_2338 end, @NotNull String veinType, @NotNull String blockId) {
            Intrinsics.checkNotNullParameter((Object)start, (String)"start");
            Intrinsics.checkNotNullParameter((Object)end, (String)"end");
            Intrinsics.checkNotNullParameter((Object)veinType, (String)"veinType");
            Intrinsics.checkNotNullParameter((Object)blockId, (String)"blockId");
            this.start = start;
            this.end = end;
            this.veinType = veinType;
            this.blockId = blockId;
        }

        @NotNull
        public final class_2338 getStart() {
            return this.start;
        }

        @NotNull
        public final class_2338 getEnd() {
            return this.end;
        }

        @NotNull
        public final String getVeinType() {
            return this.veinType;
        }

        @NotNull
        public final String getBlockId() {
            return this.blockId;
        }

        @NotNull
        public final class_2338 component1() {
            return this.start;
        }

        @NotNull
        public final class_2338 component2() {
            return this.end;
        }

        @NotNull
        public final String component3() {
            return this.veinType;
        }

        @NotNull
        public final String component4() {
            return this.blockId;
        }

        @NotNull
        public final VeinFlow copy(@NotNull class_2338 start, @NotNull class_2338 end, @NotNull String veinType, @NotNull String blockId) {
            Intrinsics.checkNotNullParameter((Object)start, (String)"start");
            Intrinsics.checkNotNullParameter((Object)end, (String)"end");
            Intrinsics.checkNotNullParameter((Object)veinType, (String)"veinType");
            Intrinsics.checkNotNullParameter((Object)blockId, (String)"blockId");
            return new VeinFlow(start, end, veinType, blockId);
        }

        public static /* synthetic */ VeinFlow copy$default(VeinFlow veinFlow, class_2338 class_23382, class_2338 class_23383, String string, String string2, int n, Object object) {
            if ((n & 1) != 0) {
                class_23382 = veinFlow.start;
            }
            if ((n & 2) != 0) {
                class_23383 = veinFlow.end;
            }
            if ((n & 4) != 0) {
                string = veinFlow.veinType;
            }
            if ((n & 8) != 0) {
                string2 = veinFlow.blockId;
            }
            return veinFlow.copy(class_23382, class_23383, string, string2);
        }

        @NotNull
        public String toString() {
            return "VeinFlow(start=" + this.start + ", end=" + this.end + ", veinType=" + this.veinType + ", blockId=" + this.blockId + ")";
        }

        public int hashCode() {
            int result = this.start.hashCode();
            result = result * 31 + this.end.hashCode();
            result = result * 31 + this.veinType.hashCode();
            result = result * 31 + this.blockId.hashCode();
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof VeinFlow)) {
                return false;
            }
            VeinFlow veinFlow = (VeinFlow)other;
            if (!Intrinsics.areEqual((Object)this.start, (Object)veinFlow.start)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.end, (Object)veinFlow.end)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.veinType, (Object)veinFlow.veinType)) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.blockId, (Object)veinFlow.blockId);
        }
    }
}

