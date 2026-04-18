/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  kotlin.sequences.Sequence
 *  kotlin.sequences.SequencesKt
 *  net.minecraft.class_1297
 *  net.minecraft.class_1299
 *  net.minecraft.class_1308
 *  net.minecraft.class_1309
 *  net.minecraft.class_1311
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.visual;

import java.awt.Color;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1311;
import net.minecraft.class_310;
import net.minecraft.class_638;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.ColorSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.internal.helper.ClientGlowEspManager;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ#\u0010\r\u001a\u00020\u000b2\n\u0010\n\u001a\u0006\u0012\u0002\b\u00030\t2\u0006\u0010\f\u001a\u00020\u000bH\u0007\u00a2\u0006\u0004\b\r\u0010\u000eJ\u000f\u0010\u0010\u001a\u00020\u000fH\u0007\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u000f\u0010\u0012\u001a\u00020\u000fH\u0007\u00a2\u0006\u0004\b\u0012\u0010\u0011J\u0017\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0014\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u001b\u0010\u0017\u001a\u00020\u00062\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u0013H\u0002\u00a2\u0006\u0004\b\u0017\u0010\u0016J\u001b\u0010\u0018\u001a\u00020\u000b2\n\u0010\n\u001a\u0006\u0012\u0002\b\u00030\tH\u0002\u00a2\u0006\u0004\b\u0018\u0010\u0019R\u0014\u0010\u001b\u001a\u00020\u001a8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001b\u0010\u001cR\u0014\u0010\u001e\u001a\u00020\u001d8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u001fR\u0014\u0010 \u001a\u00020\u001d8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b \u0010\u001fR\u0014\u0010\"\u001a\u00020!8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\"\u0010#R\u0014\u0010$\u001a\u00020\u001a8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b$\u0010\u001cR\u0014\u0010&\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b&\u0010'R\u0014\u0010(\u001a\u00020\u000f8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b(\u0010)\u00a8\u0006*"}, d2={"Lorg/cobalt/internal/visual/MobEspModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "Lnet/minecraft/class_1299;", "entityType", "", "invisible", "shouldRenderFill", "(Lnet/minecraft/class_1299;Z)Z", "", "fillTintArgb", "()I", "outlineArgb", "Lnet/minecraft/class_638;", "level", "syncGlow", "(Lnet/minecraft/class_638;)V", "clearGlow", "shouldHighlightType", "(Lnet/minecraft/class_1299;)Z", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "outlineColor", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "fillColor", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "fillOpacity", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "showInvisible", "", "GLOBAL_MOB_ESP_SCOPE", "Ljava/lang/String;", "GLOBAL_MOB_ESP_PRIORITY", "I", "cobalt"})
public final class MobEspModule
extends Module {
    @NotNull
    public static final MobEspModule INSTANCE = new MobEspModule();
    @NotNull
    private static final CheckboxSetting enabled = new CheckboxSetting("Enabled", "Render mobs with a white outline through walls and a translucent model fill.", true);
    @NotNull
    private static final ColorSetting outlineColor = new ColorSetting("Outline Color", "Color used for the silhouette outline.", -1);
    @NotNull
    private static final ColorSetting fillColor = new ColorSetting("Fill Color", "Tint used for the visible model fill.", -1);
    @NotNull
    private static final SliderSetting fillOpacity = new SliderSetting("Fill Opacity", "Opacity of the visible model fill.", 0.24, 0.0, 1.0, 0.0, 32, null);
    @NotNull
    private static final CheckboxSetting showInvisible = new CheckboxSetting("Show Invisible", "Also highlight invisible mobs.", true);
    @NotNull
    private static final String GLOBAL_MOB_ESP_SCOPE = "global_mob_esp";
    private static final int GLOBAL_MOB_ESP_PRIORITY = 1;

    private MobEspModule() {
        super("Mob ESP");
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_638 class_6382 = class_310.method_1551().field_1687;
        if (class_6382 == null) {
            MobEspModule $this$onTick_u24lambda_u240 = this;
            boolean bl = false;
            MobEspModule.clearGlow$default($this$onTick_u24lambda_u240, null, 1, null);
            return;
        }
        class_638 level2 = class_6382;
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            this.clearGlow(level2);
            return;
        }
        this.syncGlow(level2);
    }

    @JvmStatic
    public static final boolean shouldRenderFill(@NotNull class_1299<?> entityType, boolean invisible) {
        Intrinsics.checkNotNullParameter(entityType, (String)"entityType");
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            return false;
        }
        if (!((Boolean)showInvisible.getValue()).booleanValue() && invisible) {
            return false;
        }
        return INSTANCE.shouldHighlightType(entityType);
    }

    @JvmStatic
    public static final int fillTintArgb() {
        Color base = new Color(fillColor.getValue(), true);
        int alpha = RangesKt.coerceIn((int)((int)((double)base.getAlpha() * ((Number)fillOpacity.getValue()).doubleValue())), (int)0, (int)255);
        return alpha << 24 | base.getRed() << 16 | base.getGreen() << 8 | base.getBlue();
    }

    @JvmStatic
    public static final int outlineArgb() {
        return outlineColor.getValue();
    }

    private final void syncGlow(class_638 level2) {
        Iterable iterable = level2.method_18112();
        Intrinsics.checkNotNullExpressionValue((Object)iterable, (String)"entitiesForRendering(...)");
        List targets = SequencesKt.toList((Sequence)SequencesKt.map((Sequence)SequencesKt.filter((Sequence)SequencesKt.filter((Sequence)SequencesKt.mapNotNull((Sequence)CollectionsKt.asSequence((Iterable)iterable), MobEspModule::syncGlow$lambda$0), MobEspModule::syncGlow$lambda$1), MobEspModule::syncGlow$lambda$2), MobEspModule::syncGlow$lambda$3));
        ClientGlowEspManager.INSTANCE.sync(GLOBAL_MOB_ESP_SCOPE, level2, targets);
    }

    private final void clearGlow(class_638 level2) {
        ClientGlowEspManager.INSTANCE.clear(GLOBAL_MOB_ESP_SCOPE, level2);
    }

    static /* synthetic */ void clearGlow$default(MobEspModule mobEspModule, class_638 class_6382, int n, Object object) {
        if ((n & 1) != 0) {
            class_6382 = class_310.method_1551().field_1687;
        }
        mobEspModule.clearGlow(class_6382);
    }

    private final boolean shouldHighlightType(class_1299<?> entityType) {
        return entityType.method_5891() != class_1311.field_17715;
    }

    private static final class_1308 syncGlow$lambda$0(class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it instanceof class_1308 ? (class_1308)it : null;
    }

    private static final boolean syncGlow$lambda$1(class_1308 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.method_5805() && ((Boolean)showInvisible.getValue() != false || !it.method_5767());
    }

    private static final boolean syncGlow$lambda$2(class_1308 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        class_1299 class_12992 = it.method_5864();
        Intrinsics.checkNotNullExpressionValue((Object)class_12992, (String)"getType(...)");
        return INSTANCE.shouldHighlightType(class_12992);
    }

    private static final ClientGlowEspManager.GlowTarget syncGlow$lambda$3(class_1308 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return new ClientGlowEspManager.GlowTarget((class_1309)it, outlineColor.getValue(), 1);
    }

    static {
        Setting[] settingArray = new Setting[]{enabled, outlineColor, fillColor, fillOpacity, showInvisible};
        INSTANCE.addSetting(settingArray);
        EventBus.register(INSTANCE);
    }
}

