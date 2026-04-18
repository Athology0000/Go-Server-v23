/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_442
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL30
 */
package org.cobalt.internal.visual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_442;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.render.NvgEvent;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Gradient;
import org.cobalt.internal.ui.animation.RiseAnimation;
import org.cobalt.internal.ui.animation.RiseEasing;
import org.cobalt.render.TitleBackgroundRenderer;
import org.cobalt.render.rise.GlowButton;
import org.cobalt.render.rise.RenderTarget;
import org.cobalt.render.rise.ShaderRegistry;
import org.cobalt.render.rise.UiShaderDrawHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000|\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\u0006\n\u0002\b\b\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001RB\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001d\u0010\b\u001a\u00020\u00072\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0004\b\b\u0010\tJ-\u0010\u0010\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u000e\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0017\u0010\u0014\u001a\u00020\u00072\u0006\u0010\u0013\u001a\u00020\u0012H\u0007\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u000f\u0010\u0016\u001a\u00020\u0007H\u0002\u00a2\u0006\u0004\b\u0016\u0010\u0003J5\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001c0\u001b2\u0006\u0010\u0017\u001a\u00020\u000e2\u0006\u0010\u0018\u001a\u00020\u000e2\u0006\u0010\u0019\u001a\u00020\u000e2\u0006\u0010\u001a\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u001d\u0010\u001eJ-\u0010\"\u001a\u00020\u00072\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u001c0\u001b2\u0006\u0010 \u001a\u00020\u00042\u0006\u0010!\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\"\u0010#J7\u0010%\u001a\u00020\u00072\u0006\u0010 \u001a\u00020\u000e2\u0006\u0010!\u001a\u00020\u000e2\u0006\u0010\u0019\u001a\u00020\u000e2\u0006\u0010$\u001a\u00020\u000e2\u0006\u0010\u001a\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b%\u0010&J=\u0010'\u001a\u00020\u00072\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u001c0\u001b2\u0006\u0010!\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u000e2\u0006\u0010$\u001a\u00020\u000e2\u0006\u0010\u001a\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b'\u0010(J7\u0010)\u001a\u00020\u00072\u0006\u0010 \u001a\u00020\u000e2\u0006\u0010!\u001a\u00020\u000e2\u0006\u0010\u0019\u001a\u00020\u000e2\u0006\u0010$\u001a\u00020\u000e2\u0006\u0010\u001a\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b)\u0010&J5\u0010*\u001a\u00020\u00072\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u001c0\u001b2\u0006\u0010\u0019\u001a\u00020\u000e2\u0006\u0010$\u001a\u00020\u000e2\u0006\u0010\u001a\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b*\u0010+J7\u00102\u001a\u00020\u00072\u0006\u0010,\u001a\u00020\u000e2\u0006\u0010-\u001a\u00020\u000e2\u0006\u0010.\u001a\u00020\u000e2\u0006\u00100\u001a\u00020/2\u0006\u00101\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b2\u00103J\u001f\u00106\u001a\u00020\u00042\u0006\u00104\u001a\u00020\u00042\u0006\u00105\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b6\u00107R\u0014\u00109\u001a\u0002088\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b9\u0010:R\u0014\u0010;\u001a\u0002088\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b;\u0010:R\u0014\u0010=\u001a\u00020<8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b=\u0010>R\u0016\u0010?\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b?\u0010@R\u0016\u0010A\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bA\u0010@R\u0016\u0010C\u001a\u00020B8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bC\u0010DR\u0014\u0010F\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bF\u0010GR0\u0010J\u001a\u001e\u0012\u0004\u0012\u000208\u0012\u0004\u0012\u00020E0Hj\u000e\u0012\u0004\u0012\u000208\u0012\u0004\u0012\u00020E`I8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bJ\u0010KR\u0014\u0010M\u001a\u00020L8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bM\u0010NR\u0014\u0010O\u001a\u00020L8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bO\u0010NR\u0014\u0010P\u001a\u00020L8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bP\u0010NR\u0014\u0010Q\u001a\u00020L8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bQ\u0010N\u00a8\u0006S"}, d2={"Lorg/cobalt/internal/visual/TitleScreenRenderer;", "", "<init>", "()V", "", "x", "y", "", "setMousePos", "(II)V", "Lnet/minecraft/class_332;", "guiGraphics", "mouseX", "mouseY", "", "partialTick", "render", "(Lnet/minecraft/class_332;IIF)V", "Lorg/cobalt/api/event/impl/render/NvgEvent;", "event", "onNvg", "(Lorg/cobalt/api/event/impl/render/NvgEvent;)V", "resetState", "guiWidth", "guiHeight", "scale", "intro", "", "Lorg/cobalt/render/rise/GlowButton;", "buildButtons", "(FFFF)Ljava/util/List;", "buttons", "screenWidth", "screenHeight", "renderHoverBloom", "(Ljava/util/List;II)V", "timeSeconds", "renderTitlePlate", "(FFFFF)V", "renderButtons", "(Ljava/util/List;IFFF)V", "drawTitleText", "drawButtonLabels", "(Ljava/util/List;FFF)V", "cx", "cy", "outerRadius", "", "time", "hover", "drawGearIcon", "(FFFDF)V", "rgb", "alpha", "withAlpha", "(II)I", "", "TITLE_TEXT", "Ljava/lang/String;", "SUBTITLE_TEXT", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "mouseGuiX", "I", "mouseGuiY", "", "wasTitleScreen", "Z", "Lorg/cobalt/internal/ui/animation/RiseAnimation;", "introAnimation", "Lorg/cobalt/internal/ui/animation/RiseAnimation;", "Ljava/util/HashMap;", "Lkotlin/collections/HashMap;", "hoverAnimations", "Ljava/util/HashMap;", "Lorg/cobalt/render/rise/RenderTarget;", "glowMaskTarget", "Lorg/cobalt/render/rise/RenderTarget;", "glowOutlineTarget", "glowBlurTargetA", "glowBlurTargetB", "ButtonSpec", "cobalt"})
@SourceDebugExtension(value={"SMAP\nTitleScreenRenderer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TitleScreenRenderer.kt\norg/cobalt/internal/visual/TitleScreenRenderer\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,387:1\n1586#2:388\n1661#2,2:389\n1663#2:398\n777#2:399\n873#2,2:400\n1915#2,2:402\n1915#2,2:404\n1915#2,2:406\n383#3,7:391\n*S KotlinDebug\n*F\n+ 1 TitleScreenRenderer.kt\norg/cobalt/internal/visual/TitleScreenRenderer\n*L\n113#1:388\n113#1:389,2\n113#1:398\n135#1:399\n135#1:400,2\n154#1:402,2\n235#1:404,2\n318#1:406,2\n117#1:391,7\n*E\n"})
public final class TitleScreenRenderer {
    @NotNull
    public static final TitleScreenRenderer INSTANCE = new TitleScreenRenderer();
    @NotNull
    private static final String TITLE_TEXT = "Dutt Client";
    @NotNull
    private static final String SUBTITLE_TEXT = "Shader stack online";
    @NotNull
    private static final class_310 mc;
    private static int mouseGuiX;
    private static int mouseGuiY;
    private static boolean wasTitleScreen;
    @NotNull
    private static final RiseAnimation introAnimation;
    @NotNull
    private static final HashMap<String, RiseAnimation> hoverAnimations;
    @NotNull
    private static final RenderTarget glowMaskTarget;
    @NotNull
    private static final RenderTarget glowOutlineTarget;
    @NotNull
    private static final RenderTarget glowBlurTargetA;
    @NotNull
    private static final RenderTarget glowBlurTargetB;

    private TitleScreenRenderer() {
    }

    public final void setMousePos(int x, int y) {
        mouseGuiX = x;
        mouseGuiY = y;
    }

    public final void render(@NotNull class_332 guiGraphics, int mouseX, int mouseY, float partialTick) {
        Intrinsics.checkNotNullParameter((Object)guiGraphics, (String)"guiGraphics");
        this.setMousePos(mouseX, mouseY);
        if (!(TitleScreenRenderer.mc.field_1755 instanceof class_442)) {
            this.resetState();
            return;
        }
        if (!wasTitleScreen) {
            wasTitleScreen = true;
            introAnimation.snap(0.0);
            introAnimation.run(1.0, 520L, RiseEasing.EASE_OUT_EXPO);
        }
        float screenWidth = mc.method_22683().method_4480();
        float screenHeight = mc.method_22683().method_4507();
        float guiScale = mc.method_22683().method_4495();
        float guiWidth = mc.method_22683().method_4486();
        float guiHeight = mc.method_22683().method_4502();
        float timeSeconds = (float)(System.currentTimeMillis() % 1000000L) / 1000.0f;
        float intro = RangesKt.coerceIn((float)((float)introAnimation.getValue()), (float)0.0f, (float)1.0f);
        NVGRenderer.INSTANCE.beginFrame(screenWidth, screenHeight);
        TitleBackgroundRenderer.renderToScreen((int)screenWidth, (int)screenHeight, timeSeconds);
        List<GlowButton> buttons = this.buildButtons(guiWidth, guiHeight, guiScale, intro);
        this.renderHoverBloom(buttons, (int)screenWidth, (int)screenHeight);
        this.renderTitlePlate(screenWidth, screenHeight, guiScale, timeSeconds, intro);
        this.renderButtons(buttons, (int)screenHeight, guiScale, timeSeconds, intro);
        this.drawTitleText(screenWidth, screenHeight, guiScale, timeSeconds, intro);
        this.drawButtonLabels(buttons, guiScale, timeSeconds, intro);
        NVGRenderer.INSTANCE.endFrame();
    }

    @SubscribeEvent
    public final void onNvg(@NotNull NvgEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!(TitleScreenRenderer.mc.field_1755 instanceof class_442)) {
            this.resetState();
        }
    }

    private final void resetState() {
        if (!wasTitleScreen) {
            return;
        }
        wasTitleScreen = false;
        hoverAnimations.clear();
        introAnimation.snap(0.0);
    }

    /*
     * WARNING - void declaration
     */
    private final List<GlowButton> buildButtons(float guiWidth, float guiHeight, float scale, float intro) {
        void $this$mapTo$iv$iv;
        float centerX = guiWidth / 2.0f;
        Object[] objectArray = new ButtonSpec[]{new ButtonSpec("Singleplayer", centerX - 100.0f, guiHeight / 4.0f + 48.0f, 200.0f, 0.0f, 16, null), new ButtonSpec("Multiplayer", centerX - 100.0f, guiHeight / 4.0f + 72.0f, 200.0f, 0.0f, 16, null), new ButtonSpec("Options", centerX - 100.0f, guiHeight / 4.0f + 132.0f, 98.0f, 0.0f, 16, null), new ButtonSpec("Quit", centerX + 2.0f, guiHeight / 4.0f + 132.0f, 98.0f, 0.0f, 16, null)};
        List specs = CollectionsKt.listOf((Object[])objectArray);
        Iterable $this$map$iv = specs;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            Object object;
            void $this$getOrPut$iv;
            void spec;
            ButtonSpec buttonSpec = (ButtonSpec)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            boolean hovered = (float)mouseGuiX >= spec.getX() && (float)mouseGuiX <= spec.getX() + spec.getWidth() && (float)mouseGuiY >= spec.getY() && (float)mouseGuiY <= spec.getY() + spec.getHeight();
            Map map = hoverAnimations;
            String key$iv = spec.getLabel();
            boolean $i$f$getOrPut = false;
            Object value$iv = $this$getOrPut$iv.get(key$iv);
            if (value$iv == null) {
                boolean bl2 = false;
                RiseAnimation answer$iv = new RiseAnimation(0.0);
                $this$getOrPut$iv.put(key$iv, answer$iv);
                object = answer$iv;
            } else {
                object = value$iv;
            }
            RiseAnimation hoverAnimation = (RiseAnimation)object;
            hoverAnimation.run(hovered ? 1.0 : 0.0, 160L, RiseEasing.EASE_OUT_CUBIC);
            float hover = RangesKt.coerceIn((float)((float)hoverAnimation.getValue()), (float)0.0f, (float)1.0f);
            float introOffset = (1.0f - intro) * 18.0f;
            collection.add(new GlowButton(spec.getLabel(), spec.getX() * scale, (spec.getY() + introOffset) * scale, spec.getWidth() * scale, spec.getHeight() * scale, 7.0f * scale, hover));
        }
        return (List)destination$iv$iv;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    private final void renderHoverBloom(List<GlowButton> buttons, int screenWidth, int screenHeight) {
        void $this$filterTo$iv$iv;
        Iterable $this$filter$iv = buttons;
        boolean $i$f$filter = false;
        Iterable iterable = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            GlowButton it = (GlowButton)element$iv$iv;
            boolean bl = false;
            if (!(it.getHoverProgress() > 0.03f)) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        List hoveredButtons = (List)destination$iv$iv;
        if (hoveredButtons.isEmpty()) {
            return;
        }
        int previousFramebuffer = GL11.glGetInteger((int)36006);
        int[] previousViewport = new int[4];
        GL11.glGetIntegerv((int)2978, (int[])previousViewport);
        glowMaskTarget.ensureSize(screenWidth, screenHeight);
        glowOutlineTarget.ensureSize(screenWidth, screenHeight);
        glowBlurTargetA.ensureSize(screenWidth, screenHeight);
        glowBlurTargetB.ensureSize(screenWidth, screenHeight);
        GL30.glBindFramebuffer((int)36160, (int)previousFramebuffer);
        GL11.glViewport((int)previousViewport[0], (int)previousViewport[1], (int)previousViewport[2], (int)previousViewport[3]);
        try {
            glowMaskTarget.clear(0.0f, 0.0f, 0.0f, 0.0f);
            Iterable $this$forEach$iv = hoveredButtons;
            boolean $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                GlowButton button = (GlowButton)element$iv;
                boolean bl = false;
                float hover = button.getHoverProgress();
                int bloomAlpha = (int)(88.0f + 132.0f * hover);
                UiShaderDrawHelper.drawRoundedRect(button.getX() - 3.0f, button.getY() - 3.0f, button.getWidth() + 6.0f, button.getHeight() + 6.0f, button.getRadius() + 3.0f, INSTANCE.withAlpha(10476543, bloomAlpha), screenHeight);
            }
        }
        finally {
            GL30.glBindFramebuffer((int)36160, (int)previousFramebuffer);
            GL11.glViewport((int)previousViewport[0], (int)previousViewport[1], (int)previousViewport[2], (int)previousViewport[3]);
        }
        ShaderRegistry.OUTLINE.render(glowMaskTarget, glowOutlineTarget, 2.0f, 1874395135);
        ShaderRegistry.BLOOM.render(glowOutlineTarget, glowBlurTargetA, glowBlurTargetB, 8);
        ShaderRegistry.SCREEN_SPACE.renderTextureAdditive(glowBlurTargetB.getTextureId(), 1.0f);
        ShaderRegistry.SCREEN_SPACE.renderTextureAdditive(glowOutlineTarget.getTextureId(), 0.72f);
    }

    private final void renderTitlePlate(float screenWidth, float screenHeight, float scale, float timeSeconds, float intro) {
        float cardWidth = 272.0f * scale;
        float cardHeight = 58.0f * scale;
        float cardX = screenWidth / 2.0f - cardWidth / 2.0f;
        float cardY = screenHeight * 0.18f - (1.0f - intro) * 24.0f * scale;
        float shadowOffset = 4.0f * scale;
        int cardAlpha = (int)(90.0f + 88.0f * intro);
        UiShaderDrawHelper.drawRoundedRect(cardX + shadowOffset, cardY + shadowOffset, cardWidth, cardHeight, 18.0f * scale, this.withAlpha(132363, (int)(40.0f + 36.0f * intro)), (int)screenHeight);
        UiShaderDrawHelper.drawTriGradientRoundedRect(cardX, cardY, cardWidth, cardHeight, 18.0f * scale, this.withAlpha(1057851, cardAlpha), this.withAlpha(1515063, cardAlpha + 10), this.withAlpha(0x21122C, cardAlpha), Gradient.LeftToRight, timeSeconds * 0.35f, (int)screenHeight);
        UiShaderDrawHelper.drawAnimatedGradientRoundedRect(cardX + 1.5f * scale, cardY + 1.5f * scale, cardWidth - 3.0f * scale, cardHeight * 0.58f, 16.0f * scale, this.withAlpha(6412543, (int)(24.0f + 36.0f * intro)), this.withAlpha(13342207, (int)(14.0f + 28.0f * intro)), Gradient.LeftToRight, timeSeconds, (int)screenHeight);
        UiShaderDrawHelper.drawGradientOutline(cardX, cardY, cardWidth, cardHeight, 18.0f * scale, Math.max(1.0f, 1.3f * scale), this.withAlpha(7917567, (int)(92.0f + 72.0f * intro)), this.withAlpha(12750079, (int)(72.0f + 64.0f * intro)), Gradient.LeftToRight, (int)screenHeight);
    }

    private final void renderButtons(List<GlowButton> buttons, int screenHeight, float scale, float timeSeconds, float intro) {
        Iterable $this$forEach$iv = buttons;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            GlowButton button = (GlowButton)element$iv;
            boolean bl = false;
            float hover = button.getHoverProgress();
            int baseAlpha = (int)(92.0f + 38.0f * intro);
            int fillAlpha = (int)(74.0f + 52.0f * hover + 24.0f * intro);
            int sheenAlpha = (int)(12.0f + 38.0f * hover + 18.0f * intro);
            int borderAlpha = (int)(72.0f + 88.0f * hover + 28.0f * intro);
            UiShaderDrawHelper.drawRoundedRect(button.getX() + 2.0f * scale, button.getY() + 3.0f * scale, button.getWidth(), button.getHeight(), button.getRadius(), INSTANCE.withAlpha(132620, (int)(26.0f + 22.0f * intro)), screenHeight);
            UiShaderDrawHelper.drawGradientRoundedRect(button.getX(), button.getY(), button.getWidth(), button.getHeight(), button.getRadius(), INSTANCE.withAlpha(1120809, baseAlpha), INSTANCE.withAlpha(659739, fillAlpha), Gradient.TopToBottom, screenHeight);
            UiShaderDrawHelper.drawAnimatedGradientRoundedRect(button.getX() + 1.0f * scale, button.getY() + 1.0f * scale, button.getWidth() - 2.0f * scale, button.getHeight() * (0.56f + hover * 0.08f), Math.max(0.0f, button.getRadius() - 1.0f * scale), INSTANCE.withAlpha(6608127, sheenAlpha), INSTANCE.withAlpha(12488447, (int)((float)sheenAlpha * 0.85f)), Gradient.LeftToRight, timeSeconds, screenHeight);
            UiShaderDrawHelper.drawGradientOutline(button.getX(), button.getY(), button.getWidth(), button.getHeight(), button.getRadius(), Math.max(1.0f, 1.05f * scale), INSTANCE.withAlpha(7587071, borderAlpha), INSTANCE.withAlpha(12029439, (int)((float)borderAlpha * 0.92f)), Gradient.LeftToRight, screenHeight);
            if (!(hover > 0.02f)) continue;
            UiShaderDrawHelper.drawRoundedOutline(button.getX() + 1.5f * scale, button.getY() + 1.5f * scale, button.getWidth() - 3.0f * scale, button.getHeight() - 3.0f * scale, Math.max(0.0f, button.getRadius() - 1.5f * scale), Math.max(1.0f, 1.0f * scale), INSTANCE.withAlpha(14874367, (int)(18.0f + 46.0f * hover)), screenHeight);
        }
    }

    private final void drawTitleText(float screenWidth, float screenHeight, float scale, float timeSeconds, float intro) {
        float titleSize = 31.0f * scale;
        float subtitleSize = 9.5f * scale;
        float titleWidth = NVGRenderer.textWidth$default(TITLE_TEXT, titleSize, null, 4, null);
        float subtitleWidth = NVGRenderer.textWidth$default(SUBTITLE_TEXT, subtitleSize, null, 4, null);
        float titleX = screenWidth / 2.0f - titleWidth / 2.0f;
        float subtitleX = screenWidth / 2.0f - subtitleWidth / 2.0f;
        float baseY = screenHeight * 0.197f - (1.0f - intro) * 18.0f * scale;
        float pulse = 0.92f + (float)Math.sin(timeSeconds * 1.15f) * 0.08f;
        int titleAlpha = RangesKt.coerceIn((int)((int)(170.0f + pulse * 84.0f * intro)), (int)0, (int)255);
        int subtitleAlpha = RangesKt.coerceIn((int)((int)(108.0f + 90.0f * intro)), (int)0, (int)255);
        NVGRenderer.textShadow$default(TITLE_TEXT, titleX, baseY, titleSize, this.withAlpha(0xEEF4FF, titleAlpha), null, 32, null);
        NVGRenderer.text$default(SUBTITLE_TEXT, subtitleX, baseY + 26.0f * scale, subtitleSize, this.withAlpha(11782119, subtitleAlpha), null, 32, null);
    }

    private final void drawButtonLabels(List<GlowButton> buttons, float scale, float timeSeconds, float intro) {
        Iterable $this$forEach$iv = buttons;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            GlowButton button = (GlowButton)element$iv;
            boolean bl = false;
            float textSize = 9.2f * scale;
            float hover = button.getHoverProgress();
            int textAlpha = RangesKt.coerceIn((int)((int)(176.0f + 62.0f * intro + 18.0f * hover)), (int)0, (int)255);
            int textColor = INSTANCE.withAlpha(15398143, textAlpha);
            if (Intrinsics.areEqual((Object)button.getLabel(), (Object)"Options")) {
                float gearRadius = button.getHeight() * 0.28f;
                String string = button.getLabel();
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getLabel(...)");
                float labelWidth = NVGRenderer.textWidth$default(string, textSize, null, 4, null);
                float gap = 4.0f * scale;
                float totalWidth = gearRadius * 2.0f + gap + labelWidth;
                float startX = button.getX() + (button.getWidth() - totalWidth) / 2.0f;
                float gearX = startX + gearRadius;
                float gearY = button.getY() + button.getHeight() / 2.0f;
                INSTANCE.drawGearIcon(gearX, gearY, gearRadius, timeSeconds, hover);
                String string2 = button.getLabel();
                Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"getLabel(...)");
                NVGRenderer.text$default(string2, startX + gearRadius * 2.0f + gap, button.getY() + (button.getHeight() - textSize) / 2.0f + 0.5f * scale, textSize, textColor, null, 32, null);
                continue;
            }
            String string = button.getLabel();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getLabel(...)");
            float labelWidth = NVGRenderer.textWidth$default(string, textSize, null, 4, null);
            String string3 = button.getLabel();
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"getLabel(...)");
            NVGRenderer.text$default(string3, button.getX() + (button.getWidth() - labelWidth) / 2.0f, button.getY() + (button.getHeight() - textSize) / 2.0f + 0.5f * scale, textSize, textColor, null, 32, null);
        }
    }

    private final void drawGearIcon(float cx, float cy, float outerRadius, double time, float hover) {
        int teeth = 8;
        float rotation = (float)(time * (0.42 + (double)hover * 0.18));
        float discRadius = outerRadius * 0.66f;
        float toothWidth = outerRadius * 0.32f;
        float toothStart = discRadius - outerRadius * 0.08f;
        float toothLength = outerRadius - toothStart;
        int color = this.withAlpha(15594751, (int)(194.0f + 42.0f * hover));
        int n = 0;
        while (n < teeth) {
            int index = n++;
            boolean bl = false;
            float angle = rotation + (float)index * ((float)Math.PI * 2 / (float)teeth);
            NVGRenderer.push();
            NVGRenderer.translate(cx, cy);
            NVGRenderer.rotate(angle);
            NVGRenderer.rect(-toothWidth / 2.0f, toothStart, toothWidth, toothLength, color, toothWidth * 0.24f);
            NVGRenderer.pop();
        }
        NVGRenderer.circle(cx, cy, discRadius, color);
        NVGRenderer.circle(cx, cy, discRadius * 0.42f, -368702958);
    }

    private final int withAlpha(int rgb, int alpha) {
        return RangesKt.coerceIn((int)alpha, (int)0, (int)255) << 24 | rgb & 0xFFFFFF;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        introAnimation = new RiseAnimation(0.0);
        hoverAnimations = new HashMap();
        glowMaskTarget = new RenderTarget();
        glowOutlineTarget = new RenderTarget();
        glowBlurTargetA = new RenderTarget();
        glowBlurTargetB = new RenderTarget();
        EventBus.register(INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000b\b\u0082\b\u0018\u00002\u00020\u0001B1\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0004\u0012\b\b\u0002\u0010\b\u001a\u00020\u0004\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\u000eJ\u0010\u0010\u0010\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u000eJ\u0010\u0010\u0011\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u000eJB\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u00042\b\b\u0002\u0010\b\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u001b\u0010\u0016\u001a\u00020\u00152\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0019\u001a\u00020\u0018H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\u0011\u0010\u001b\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001b\u0010\fR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001c\u001a\u0004\b\u001d\u0010\fR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001e\u001a\u0004\b\u001f\u0010\u000eR\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001e\u001a\u0004\b \u0010\u000eR\u0017\u0010\u0007\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u001e\u001a\u0004\b!\u0010\u000eR\u0017\u0010\b\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\b\u0010\u001e\u001a\u0004\b\"\u0010\u000e\u00a8\u0006#"}, d2={"Lorg/cobalt/internal/visual/TitleScreenRenderer$ButtonSpec;", "", "", "label", "", "x", "y", "width", "height", "<init>", "(Ljava/lang/String;FFFF)V", "component1", "()Ljava/lang/String;", "component2", "()F", "component3", "component4", "component5", "copy", "(Ljava/lang/String;FFFF)Lorg/cobalt/internal/visual/TitleScreenRenderer$ButtonSpec;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getLabel", "F", "getX", "getY", "getWidth", "getHeight", "cobalt"})
    private static final class ButtonSpec {
        @NotNull
        private final String label;
        private final float x;
        private final float y;
        private final float width;
        private final float height;

        public ButtonSpec(@NotNull String label, float x, float y, float width, float height) {
            Intrinsics.checkNotNullParameter((Object)label, (String)"label");
            this.label = label;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public /* synthetic */ ButtonSpec(String string, float f, float f2, float f3, float f4, int n, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n & 0x10) != 0) {
                f4 = 20.0f;
            }
            this(string, f, f2, f3, f4);
        }

        @NotNull
        public final String getLabel() {
            return this.label;
        }

        public final float getX() {
            return this.x;
        }

        public final float getY() {
            return this.y;
        }

        public final float getWidth() {
            return this.width;
        }

        public final float getHeight() {
            return this.height;
        }

        @NotNull
        public final String component1() {
            return this.label;
        }

        public final float component2() {
            return this.x;
        }

        public final float component3() {
            return this.y;
        }

        public final float component4() {
            return this.width;
        }

        public final float component5() {
            return this.height;
        }

        @NotNull
        public final ButtonSpec copy(@NotNull String label, float x, float y, float width, float height) {
            Intrinsics.checkNotNullParameter((Object)label, (String)"label");
            return new ButtonSpec(label, x, y, width, height);
        }

        public static /* synthetic */ ButtonSpec copy$default(ButtonSpec buttonSpec, String string, float f, float f2, float f3, float f4, int n, Object object) {
            if ((n & 1) != 0) {
                string = buttonSpec.label;
            }
            if ((n & 2) != 0) {
                f = buttonSpec.x;
            }
            if ((n & 4) != 0) {
                f2 = buttonSpec.y;
            }
            if ((n & 8) != 0) {
                f3 = buttonSpec.width;
            }
            if ((n & 0x10) != 0) {
                f4 = buttonSpec.height;
            }
            return buttonSpec.copy(string, f, f2, f3, f4);
        }

        @NotNull
        public String toString() {
            return "ButtonSpec(label=" + this.label + ", x=" + this.x + ", y=" + this.y + ", width=" + this.width + ", height=" + this.height + ")";
        }

        public int hashCode() {
            int result = this.label.hashCode();
            result = result * 31 + Float.hashCode(this.x);
            result = result * 31 + Float.hashCode(this.y);
            result = result * 31 + Float.hashCode(this.width);
            result = result * 31 + Float.hashCode(this.height);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ButtonSpec)) {
                return false;
            }
            ButtonSpec buttonSpec = (ButtonSpec)other;
            if (!Intrinsics.areEqual((Object)this.label, (Object)buttonSpec.label)) {
                return false;
            }
            if (Float.compare(this.x, buttonSpec.x) != 0) {
                return false;
            }
            if (Float.compare(this.y, buttonSpec.y) != 0) {
                return false;
            }
            if (Float.compare(this.width, buttonSpec.width) != 0) {
                return false;
            }
            return Float.compare(this.height, buttonSpec.height) == 0;
        }
    }
}
