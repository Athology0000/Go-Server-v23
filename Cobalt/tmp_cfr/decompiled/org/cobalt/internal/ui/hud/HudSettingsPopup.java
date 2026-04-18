/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_11905
 *  net.minecraft.class_11908
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.ui.hud;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.ActionSetting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.ColorSetting;
import org.cobalt.api.module.setting.impl.InfoSetting;
import org.cobalt.api.module.setting.impl.KeyBindSetting;
import org.cobalt.api.module.setting.impl.ModeSetting;
import org.cobalt.api.module.setting.impl.RangeSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.module.setting.impl.TextSetting;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.animation.ColorAnimation;
import org.cobalt.internal.ui.components.settings.UIActionSetting;
import org.cobalt.internal.ui.components.settings.UICheckboxSetting;
import org.cobalt.internal.ui.components.settings.UIColorSetting;
import org.cobalt.internal.ui.components.settings.UIHudScaleSetting;
import org.cobalt.internal.ui.components.settings.UIInfoSetting;
import org.cobalt.internal.ui.components.settings.UIKeyBindSetting;
import org.cobalt.internal.ui.components.settings.UIModeSetting;
import org.cobalt.internal.ui.components.settings.UIRangeSetting;
import org.cobalt.internal.ui.components.settings.UISliderSetting;
import org.cobalt.internal.ui.components.settings.UITextSetting;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.cobalt.internal.ui.util.GridLayout;
import org.cobalt.internal.ui.util.ScrollHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000l\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\r\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000e\n\u0002\u0010\u0006\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0018\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0000\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J%\u0010\n\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u0006\u00a2\u0006\u0004\b\n\u0010\u000bJ\r\u0010\f\u001a\u00020\t\u00a2\u0006\u0004\b\f\u0010\u0003J\r\u0010\r\u001a\u00020\t\u00a2\u0006\u0004\b\r\u0010\u0003J\u0017\u0010\u000f\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0017\u0010\u0011\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u0011\u0010\u0010J\u0017\u0010\u0013\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u0013\u0010\u0014J%\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0016\u001a\u00020\u00062\u0006\u0010\u0018\u001a\u00020\u0017\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u001f\u0010\u001c\u001a\u00020\u00192\u0006\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0016\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u001c\u0010\u001dJ'\u0010\u001e\u001a\u00020\u00192\u0006\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0016\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u001e\u0010\u001fJ7\u0010\"\u001a\u00020\u00192\u0006\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0016\u001a\u00020\u00062\u0006\u0010 \u001a\u00020\u00062\u0006\u0010!\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\"\u0010#J7\u0010$\u001a\u00020\u00192\u0006\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0016\u001a\u00020\u00062\u0006\u0010 \u001a\u00020\u00062\u0006\u0010!\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b$\u0010#J7\u0010%\u001a\u00020\u00192\u0006\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0016\u001a\u00020\u00062\u0006\u0010 \u001a\u00020\u00062\u0006\u0010!\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b%\u0010#J\u0015\u0010&\u001a\u00020\u00192\u0006\u0010\u0018\u001a\u00020\u0017\u00a2\u0006\u0004\b&\u0010'J%\u0010+\u001a\u00020\u00192\u0006\u0010\u0018\u001a\u00020\u00172\u0006\u0010)\u001a\u00020(2\u0006\u0010*\u001a\u00020(\u00a2\u0006\u0004\b+\u0010,J\u001d\u0010/\u001a\u00020\u00192\u0006\u0010-\u001a\u00020(2\u0006\u0010.\u001a\u00020(\u00a2\u0006\u0004\b/\u00100J\u0015\u00103\u001a\u00020\u00192\u0006\u00102\u001a\u000201\u00a2\u0006\u0004\b3\u00104J\u0015\u00106\u001a\u00020\u00192\u0006\u00102\u001a\u000205\u00a2\u0006\u0004\b6\u00107J\u001d\u0010:\u001a\u00020\u00192\u0006\u00108\u001a\u00020\u00062\u0006\u00109\u001a\u00020\u0006\u00a2\u0006\u0004\b:\u0010\u001dR\"\u0010;\u001a\u00020\u00198\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b;\u0010<\u001a\u0004\b=\u0010>\"\u0004\b?\u0010@R$\u0010\u0005\u001a\u0004\u0018\u00010\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0005\u0010A\u001a\u0004\bB\u0010C\"\u0004\bD\u0010\u0010R\u0016\u0010E\u001a\u00020\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bE\u0010FR\u0016\u0010G\u001a\u00020\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bG\u0010FR\u0014\u0010H\u001a\u00020\u00068\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\bH\u0010FR\u0014\u0010I\u001a\u00020\u00068\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\bI\u0010FR\u0014\u0010J\u001a\u00020\u00068\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\bJ\u0010FR\u0014\u0010K\u001a\u00020\u00068\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\bK\u0010FR\u0014\u0010L\u001a\u00020\u00068\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\bL\u0010FR\u0014\u0010M\u001a\u00020\u00068\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\bM\u0010FR\u0014\u0010O\u001a\u00020N8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bO\u0010PR\u0014\u0010Q\u001a\u00020N8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bQ\u0010PR\u0014\u0010R\u001a\u00020N8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bR\u0010PR\u001c\u0010U\u001a\b\u0012\u0004\u0012\u00020T0S8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bU\u0010VR\u0014\u0010X\u001a\u00020W8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bX\u0010YR\u0014\u0010[\u001a\u00020Z8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b[\u0010\\\u00a8\u0006]"}, d2={"Lorg/cobalt/internal/ui/hud/HudSettingsPopup;", "", "<init>", "()V", "Lorg/cobalt/api/hud/HudElement;", "module", "", "screenWidth", "screenHeight", "", "show", "(Lorg/cobalt/api/hud/HudElement;FF)V", "hide", "render", "target", "renderHeader", "(Lorg/cobalt/api/hud/HudElement;)V", "renderControls", "startY", "renderSettings", "(F)V", "mouseX", "mouseY", "", "button", "", "mouseClicked", "(FFI)Z", "handleCloseButtonClick", "(FF)Z", "handleControlButtonClicks", "(FFLorg/cobalt/api/hud/HudElement;)Z", "controlsY", "buttonHeight", "handleToggleButtonClick", "(FFFFLorg/cobalt/api/hud/HudElement;)Z", "handleResetSettingsClick", "handleResetPositionClick", "mouseReleased", "(I)Z", "", "offsetX", "offsetY", "mouseDragged", "(IDD)Z", "horizontalAmount", "verticalAmount", "mouseScrolled", "(DD)Z", "Lnet/minecraft/class_11908;", "input", "keyPressed", "(Lnet/minecraft/class_11908;)Z", "Lnet/minecraft/class_11905;", "charTyped", "(Lnet/minecraft/class_11905;)Z", "px", "py", "containsPoint", "visible", "Z", "getVisible", "()Z", "setVisible", "(Z)V", "Lorg/cobalt/api/hud/HudElement;", "getModule", "()Lorg/cobalt/api/hud/HudElement;", "setModule", "panelX", "F", "panelY", "panelWidth", "panelHeight", "headerHeight", "controlsHeight", "padding", "cornerRadius", "Lorg/cobalt/internal/ui/animation/ColorAnimation;", "toggleAnim", "Lorg/cobalt/internal/ui/animation/ColorAnimation;", "buttonAnim", "closeAnim", "", "Lorg/cobalt/internal/ui/UIComponent;", "settingComponents", "Ljava/util/List;", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "settingsScroll", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "Lorg/cobalt/internal/ui/util/GridLayout;", "settingsLayout", "Lorg/cobalt/internal/ui/util/GridLayout;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nHudSettingsPopup.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HudSettingsPopup.kt\norg/cobalt/internal/ui/hud/HudSettingsPopup\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,394:1\n1586#2:395\n1661#2,3:396\n1915#2,2:399\n1915#2,2:401\n1915#2,2:403\n*S KotlinDebug\n*F\n+ 1 HudSettingsPopup.kt\norg/cobalt/internal/ui/hud/HudSettingsPopup\n*L\n53#1:395\n53#1:396,3\n68#1:399,2\n239#1:401,2\n246#1:403,2\n*E\n"})
public final class HudSettingsPopup {
    private boolean visible;
    @Nullable
    private HudElement module;
    private float panelX;
    private float panelY;
    private final float panelWidth;
    private final float panelHeight;
    private final float headerHeight;
    private final float controlsHeight;
    private final float padding;
    private final float cornerRadius;
    @NotNull
    private final ColorAnimation toggleAnim = new ColorAnimation(150L);
    @NotNull
    private final ColorAnimation buttonAnim = new ColorAnimation(150L);
    @NotNull
    private final ColorAnimation closeAnim = new ColorAnimation(150L);
    @NotNull
    private List<? extends UIComponent> settingComponents = CollectionsKt.emptyList();
    @NotNull
    private final ScrollHandler settingsScroll = new ScrollHandler(0.0f, 1, null);
    @NotNull
    private final GridLayout settingsLayout = new GridLayout(1, this.panelWidth - this.padding * 2.0f, 60.0f, 10.0f);

    public HudSettingsPopup() {
        this.panelWidth = 520.0f;
        this.panelHeight = 420.0f;
        this.headerHeight = 50.0f;
        this.controlsHeight = 50.0f;
        this.padding = 20.0f;
        this.cornerRadius = 10.0f;
    }

    public final boolean getVisible() {
        return this.visible;
    }

    public final void setVisible(boolean bl) {
        this.visible = bl;
    }

    @Nullable
    public final HudElement getModule() {
        return this.module;
    }

    public final void setModule(@Nullable HudElement hudElement) {
        this.module = hudElement;
    }

    /*
     * WARNING - void declaration
     */
    public final void show(@NotNull HudElement module, float screenWidth, float screenHeight) {
        Collection<UIComponent> collection;
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        Intrinsics.checkNotNullParameter((Object)module, (String)"module");
        this.module = module;
        this.panelX = (screenWidth - this.panelWidth) / 2.0f;
        this.panelY = (screenHeight - this.panelHeight) / 2.0f;
        this.visible = true;
        this.settingsScroll.reset();
        UIHudScaleSetting scaleComponent = new UIHudScaleSetting(module);
        Iterable iterable = module.getSettings();
        Collection collection2 = CollectionsKt.listOf((Object)scaleComponent);
        HudSettingsPopup hudSettingsPopup = this;
        boolean $i$f$map = false;
        void var7_11 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            UIComponent uIComponent;
            void it;
            Setting setting = (Setting)item$iv$iv;
            collection = destination$iv$iv;
            boolean bl = false;
            void var14_21 = it;
            if (var14_21 instanceof ActionSetting) {
                uIComponent = new UIActionSetting((ActionSetting)it);
            } else if (var14_21 instanceof CheckboxSetting) {
                uIComponent = new UICheckboxSetting((CheckboxSetting)it);
            } else if (var14_21 instanceof ColorSetting) {
                uIComponent = new UIColorSetting((ColorSetting)it);
            } else if (var14_21 instanceof InfoSetting) {
                uIComponent = new UIInfoSetting((InfoSetting)it);
            } else if (var14_21 instanceof KeyBindSetting) {
                uIComponent = new UIKeyBindSetting((KeyBindSetting)it);
            } else if (var14_21 instanceof ModeSetting) {
                uIComponent = new UIModeSetting((ModeSetting)it);
            } else if (var14_21 instanceof RangeSetting) {
                uIComponent = new UIRangeSetting((RangeSetting)it);
            } else if (var14_21 instanceof SliderSetting) {
                uIComponent = new UISliderSetting((SliderSetting)it);
            } else {
                Intrinsics.checkNotNull((Object)it, (String)"null cannot be cast to non-null type org.cobalt.api.module.setting.impl.TextSetting");
                uIComponent = new UITextSetting((TextSetting)it);
            }
            collection.add(uIComponent);
        }
        collection = (List)destination$iv$iv;
        hudSettingsPopup.settingComponents = CollectionsKt.plus((Collection)collection2, (Iterable)collection);
        float settingWidth = this.panelWidth - this.padding * 2.0f;
        Iterable $this$forEach$iv = this.settingComponents;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            UIComponent component = (UIComponent)element$iv;
            boolean bl = false;
            component.setWidth(settingWidth);
            component.setHeight(60.0f);
        }
    }

    public final void hide() {
        this.visible = false;
        this.module = null;
        this.settingComponents = CollectionsKt.emptyList();
        this.settingsScroll.reset();
    }

    public final void render() {
        if (!this.visible) {
            return;
        }
        HudElement hudElement = this.module;
        if (hudElement == null) {
            return;
        }
        HudElement target = hudElement;
        NVGRenderer.rect(0.0f, 0.0f, 10000.0f, 10000.0f, new Color(0, 0, 0, 100).getRGB());
        NVGRenderer.rect(this.panelX, this.panelY, this.panelWidth, this.panelHeight, ThemeManager.INSTANCE.getCurrentTheme().getBackground(), this.cornerRadius);
        NVGRenderer.hollowRect(this.panelX, this.panelY, this.panelWidth, this.panelHeight, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), this.cornerRadius);
        this.renderHeader(target);
        float dividerY = this.panelY + this.headerHeight;
        NVGRenderer.line(this.panelX + this.padding, dividerY, this.panelX + this.panelWidth - this.padding, dividerY, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getModuleDivider());
        this.renderControls(target);
        float controlsDividerY = dividerY + this.controlsHeight;
        NVGRenderer.line(this.panelX + this.padding, controlsDividerY, this.panelX + this.panelWidth - this.padding, controlsDividerY, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getModuleDivider());
        this.renderSettings(controlsDividerY);
    }

    private final void renderHeader(HudElement target) {
        NVGRenderer.text$default(target.getName(), this.panelX + this.padding, this.panelY + 17.0f, 16.0f, ThemeManager.INSTANCE.getCurrentTheme().getAccent(), null, 32, null);
        float closeX = this.panelX + this.panelWidth - this.padding - 26.0f;
        float closeY = this.panelY + 12.0f;
        float closeSize = 26.0f;
        boolean closeHover = ExtensionsKt.isHoveringOver(closeX, closeY, closeSize, closeSize);
        int closeBg = this.closeAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), ThemeManager.INSTANCE.getCurrentTheme().getSelectedOverlay(), !closeHover);
        int closeBorder = this.closeAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), ThemeManager.INSTANCE.getCurrentTheme().getAccent(), !closeHover);
        NVGRenderer.rect(closeX, closeY, closeSize, closeSize, closeBg, 6.0f);
        NVGRenderer.hollowRect(closeX, closeY, closeSize, closeSize, 1.0f, closeBorder, 6.0f);
        float cx = closeX + closeSize / 2.0f;
        float cy = closeY + closeSize / 2.0f;
        float half = 5.0f;
        NVGRenderer.line(cx - half, cy - half, cx + half, cy + half, 1.5f, ThemeManager.INSTANCE.getCurrentTheme().getTextPrimary());
        NVGRenderer.line(cx + half, cy - half, cx - half, cy + half, 1.5f, ThemeManager.INSTANCE.getCurrentTheme().getTextPrimary());
    }

    private final void renderControls(HudElement target) {
        float controlsY = this.panelY + this.headerHeight + 10.0f;
        float buttonHeight = 30.0f;
        String toggleText = target.getEnabled() ? "Disable" : "Enable";
        float toggleWidth = NVGRenderer.textWidth$default(toggleText, 13.0f, null, 4, null) + 30.0f;
        float toggleX = this.panelX + this.padding;
        boolean isToggleHover = ExtensionsKt.isHoveringOver(toggleX, controlsY, toggleWidth, buttonHeight);
        int toggleBg = this.toggleAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), ThemeManager.INSTANCE.getCurrentTheme().getSelectedOverlay(), !isToggleHover);
        int toggleBorder = this.toggleAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), ThemeManager.INSTANCE.getCurrentTheme().getAccent(), !isToggleHover);
        NVGRenderer.rect(toggleX, controlsY, toggleWidth, buttonHeight, toggleBg, 8.0f);
        NVGRenderer.hollowRect(toggleX, controlsY, toggleWidth, buttonHeight, 1.5f, toggleBorder, 8.0f);
        NVGRenderer.text$default(toggleText, toggleX + 15.0f, controlsY + 8.0f, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextPrimary(), null, 32, null);
        String statusText = target.getEnabled() ? "Enabled" : "Disabled";
        int statusColor = target.getEnabled() ? ThemeManager.INSTANCE.getCurrentTheme().getAccent() : ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary();
        NVGRenderer.text$default(statusText, toggleX + toggleWidth + 12.0f, controlsY + 8.0f, 12.0f, statusColor, null, 32, null);
        String resetSettingsText = "Reset Settings";
        float resetSettingsWidth = NVGRenderer.textWidth$default(resetSettingsText, 13.0f, null, 4, null) + 30.0f;
        float resetSettingsX = this.panelX + this.panelWidth - this.padding - resetSettingsWidth;
        boolean resetSettingsHover = ExtensionsKt.isHoveringOver(resetSettingsX, controlsY, resetSettingsWidth, buttonHeight);
        int resetSettingsBg = this.buttonAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), ThemeManager.INSTANCE.getCurrentTheme().getSelectedOverlay(), !resetSettingsHover);
        int resetSettingsBorder = this.buttonAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), ThemeManager.INSTANCE.getCurrentTheme().getAccent(), !resetSettingsHover);
        NVGRenderer.rect(resetSettingsX, controlsY, resetSettingsWidth, buttonHeight, resetSettingsBg, 8.0f);
        NVGRenderer.hollowRect(resetSettingsX, controlsY, resetSettingsWidth, buttonHeight, 1.5f, resetSettingsBorder, 8.0f);
        NVGRenderer.text$default(resetSettingsText, resetSettingsX + 15.0f, controlsY + 8.0f, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextPrimary(), null, 32, null);
        String resetText = "Reset Position";
        float resetWidth = NVGRenderer.textWidth$default(resetText, 13.0f, null, 4, null) + 30.0f;
        float resetX = resetSettingsX - resetWidth - 10.0f;
        boolean resetHover = ExtensionsKt.isHoveringOver(resetX, controlsY, resetWidth, buttonHeight);
        int resetBg = this.buttonAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), ThemeManager.INSTANCE.getCurrentTheme().getSelectedOverlay(), !resetHover);
        int resetBorder = this.buttonAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), ThemeManager.INSTANCE.getCurrentTheme().getAccent(), !resetHover);
        NVGRenderer.rect(resetX, controlsY, resetWidth, buttonHeight, resetBg, 8.0f);
        NVGRenderer.hollowRect(resetX, controlsY, resetWidth, buttonHeight, 1.5f, resetBorder, 8.0f);
        NVGRenderer.text$default(resetText, resetX + 15.0f, controlsY + 8.0f, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextPrimary(), null, 32, null);
    }

    private final void renderSettings(float startY) {
        if (this.settingComponents.isEmpty()) {
            String noText = "No settings available";
            NVGRenderer.text$default(noText, this.panelX + this.panelWidth / 2.0f - NVGRenderer.textWidth$default(noText, 13.0f, null, 4, null) / 2.0f, startY + 30.0f, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
            return;
        }
        float settingsAreaY = startY + 10.0f;
        float settingsAreaHeight = this.panelY + this.panelHeight - settingsAreaY - 10.0f;
        float settingWidth = this.panelWidth - this.padding * 2.0f;
        this.settingsScroll.setMaxScroll(this.settingsLayout.contentHeight(this.settingComponents.size()) + 10.0f, settingsAreaHeight);
        NVGRenderer.pushScissor(this.panelX + this.padding, settingsAreaY, settingWidth, settingsAreaHeight);
        float scrollOffset = this.settingsScroll.getOffset();
        this.settingsLayout.layout(this.panelX + this.padding, settingsAreaY - scrollOffset, this.settingComponents);
        Iterable $this$forEach$iv = this.settingComponents;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            UIComponent component = (UIComponent)element$iv;
            boolean bl = false;
            component.setWidth(settingWidth);
            component.render();
        }
        NVGRenderer.popScissor();
        $this$forEach$iv = this.settingComponents;
        $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            UIComponent setting = (UIComponent)element$iv;
            boolean bl = false;
            UIComponent uIComponent = setting;
            if (uIComponent instanceof UIModeSetting) {
                ((UIModeSetting)setting).renderDropdown();
                continue;
            }
            if (!(uIComponent instanceof UIColorSetting)) continue;
            ((UIColorSetting)setting).drawColorPicker();
        }
    }

    public final boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (!this.visible) {
            return false;
        }
        HudElement hudElement = this.module;
        if (hudElement == null) {
            return false;
        }
        HudElement target = hudElement;
        if (button == 0 && this.handleCloseButtonClick(mouseX, mouseY)) {
            return true;
        }
        if (button != 0) {
            return this.containsPoint(mouseX, mouseY);
        }
        if (this.handleControlButtonClicks(mouseX, mouseY, target)) {
            return true;
        }
        for (UIComponent uIComponent : this.settingComponents) {
            if (!uIComponent.mouseClicked(button)) continue;
            return true;
        }
        return this.containsPoint(mouseX, mouseY);
    }

    private final boolean handleCloseButtonClick(float mouseX, float mouseY) {
        float closeX = this.panelX + this.panelWidth - this.padding - 26.0f;
        float closeY = this.panelY + 12.0f;
        if (mouseX >= closeX && mouseX <= closeX + 26.0f && mouseY >= closeY && mouseY <= closeY + 26.0f) {
            this.hide();
            return true;
        }
        return false;
    }

    private final boolean handleControlButtonClicks(float mouseX, float mouseY, HudElement target) {
        float controlsY = this.panelY + this.headerHeight + 10.0f;
        float buttonHeight = 30.0f;
        if (this.handleToggleButtonClick(mouseX, mouseY, controlsY, buttonHeight, target)) {
            return true;
        }
        if (this.handleResetSettingsClick(mouseX, mouseY, controlsY, buttonHeight, target)) {
            return true;
        }
        return this.handleResetPositionClick(mouseX, mouseY, controlsY, buttonHeight, target);
    }

    private final boolean handleToggleButtonClick(float mouseX, float mouseY, float controlsY, float buttonHeight, HudElement target) {
        String toggleText = target.getEnabled() ? "Disable" : "Enable";
        float toggleWidth = NVGRenderer.textWidth$default(toggleText, 13.0f, null, 4, null) + 30.0f;
        float toggleX = this.panelX + this.padding;
        if (mouseX >= toggleX && mouseX <= toggleX + toggleWidth && mouseY >= controlsY && mouseY <= controlsY + buttonHeight) {
            target.setEnabled(!target.getEnabled());
            this.toggleAnim.start();
            return true;
        }
        return false;
    }

    private final boolean handleResetSettingsClick(float mouseX, float mouseY, float controlsY, float buttonHeight, HudElement target) {
        String resetSettingsText = "Reset Settings";
        float resetSettingsWidth = NVGRenderer.textWidth$default(resetSettingsText, 13.0f, null, 4, null) + 30.0f;
        float resetSettingsX = this.panelX + this.panelWidth - this.padding - resetSettingsWidth;
        if (mouseX >= resetSettingsX && mouseX <= resetSettingsX + resetSettingsWidth && mouseY >= controlsY && mouseY <= controlsY + buttonHeight) {
            target.resetSettings();
            this.buttonAnim.start();
            return true;
        }
        return false;
    }

    private final boolean handleResetPositionClick(float mouseX, float mouseY, float controlsY, float buttonHeight, HudElement target) {
        String resetText;
        float resetWidth;
        String resetSettingsText = "Reset Settings";
        float resetSettingsWidth = NVGRenderer.textWidth$default(resetSettingsText, 13.0f, null, 4, null) + 30.0f;
        float resetSettingsX = this.panelX + this.panelWidth - this.padding - resetSettingsWidth;
        float resetX = resetSettingsX - (resetWidth = NVGRenderer.textWidth$default(resetText = "Reset Position", 13.0f, null, 4, null) + 30.0f) - 10.0f;
        if (mouseX >= resetX && mouseX <= resetX + resetWidth && mouseY >= controlsY && mouseY <= controlsY + buttonHeight) {
            target.resetPosition();
            this.buttonAnim.start();
            return true;
        }
        return false;
    }

    public final boolean mouseReleased(int button) {
        if (!this.visible) {
            return false;
        }
        for (UIComponent uIComponent : this.settingComponents) {
            if (!uIComponent.mouseReleased(button)) continue;
            return true;
        }
        return false;
    }

    public final boolean mouseDragged(int button, double offsetX, double offsetY) {
        if (!this.visible) {
            return false;
        }
        for (UIComponent uIComponent : this.settingComponents) {
            if (!uIComponent.mouseDragged(button, offsetX, offsetY)) continue;
            return true;
        }
        return false;
    }

    public final boolean mouseScrolled(double horizontalAmount, double verticalAmount) {
        if (!this.visible) {
            return false;
        }
        for (UIComponent uIComponent : this.settingComponents) {
            if (!uIComponent.mouseScrolled(horizontalAmount, verticalAmount)) continue;
            return true;
        }
        if (ExtensionsKt.isHoveringOver(this.panelX, this.panelY, this.panelWidth, this.panelHeight)) {
            this.settingsScroll.handleScroll(verticalAmount);
            return true;
        }
        return false;
    }

    public final boolean keyPressed(@NotNull class_11908 input) {
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        if (!this.visible) {
            return false;
        }
        for (UIComponent uIComponent : this.settingComponents) {
            if (!uIComponent.keyPressed(input)) continue;
            return true;
        }
        return false;
    }

    public final boolean charTyped(@NotNull class_11905 input) {
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        if (!this.visible) {
            return false;
        }
        for (UIComponent uIComponent : this.settingComponents) {
            if (!uIComponent.charTyped(input)) continue;
            return true;
        }
        return false;
    }

    public final boolean containsPoint(float px, float py) {
        if (!this.visible) {
            return false;
        }
        return px >= this.panelX && px <= this.panelX + this.panelWidth && py >= this.panelY && py <= this.panelY + this.panelHeight;
    }
}

