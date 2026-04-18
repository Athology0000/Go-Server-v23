/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.IndexedValue
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.ui.panel.panels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IndexedValue;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.cobalt.api.addon.Addon;
import org.cobalt.api.addon.AddonMetadata;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.ActionSetting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.ColorSetting;
import org.cobalt.api.module.setting.impl.CommandHotkeySetting;
import org.cobalt.api.module.setting.impl.InfoSetting;
import org.cobalt.api.module.setting.impl.KeyBindSetting;
import org.cobalt.api.module.setting.impl.ModeSetting;
import org.cobalt.api.module.setting.impl.RangeSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.module.setting.impl.TextSetting;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.mining.MiningMacroModule;
import org.cobalt.internal.mining.MiningModule;
import org.cobalt.internal.routes.RoutePickerSetting;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.components.UIBackButton;
import org.cobalt.internal.ui.components.UIModule;
import org.cobalt.internal.ui.components.UITopbar;
import org.cobalt.internal.ui.components.settings.UIActionSetting;
import org.cobalt.internal.ui.components.settings.UICheckboxSetting;
import org.cobalt.internal.ui.components.settings.UIColorSetting;
import org.cobalt.internal.ui.components.settings.UICommandHotkeySetting;
import org.cobalt.internal.ui.components.settings.UIInfoSetting;
import org.cobalt.internal.ui.components.settings.UIKeyBindSetting;
import org.cobalt.internal.ui.components.settings.UIModeSetting;
import org.cobalt.internal.ui.components.settings.UIRangeSetting;
import org.cobalt.internal.ui.components.settings.UIRoutePickerSetting;
import org.cobalt.internal.ui.components.settings.UISliderSetting;
import org.cobalt.internal.ui.components.settings.UITextSetting;
import org.cobalt.internal.ui.panel.UIPanel;
import org.cobalt.internal.ui.panel.panels.UIMiningStatsPanel;
import org.cobalt.internal.ui.screen.UIConfig;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.cobalt.internal.ui.util.GridLayout;
import org.cobalt.internal.ui.util.ScrollHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0090\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0000\u0018\u0000 K2\u00020\u0001:\u0002LKB\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u000f\u0010\t\u001a\u00020\bH\u0016\u00a2\u0006\u0004\b\t\u0010\nJ\u0017\u0010\u000e\u001a\u00020\r2\u0006\u0010\f\u001a\u00020\u000bH\u0016\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u001f\u0010\u0013\u001a\u00020\r2\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\u0010H\u0016\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0015\u0010\u0017\u001a\u00020\b2\u0006\u0010\u0016\u001a\u00020\u0015\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u0017\u0010\u001a\u001a\u00020\b2\u0006\u0010\u0016\u001a\u00020\u0019H\u0002\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u0017\u0010\u001d\u001a\u00020\b2\u0006\u0010\u001c\u001a\u00020\rH\u0002\u00a2\u0006\u0004\b\u001d\u0010\u001eJ\u000f\u0010\u001f\u001a\u00020\rH\u0002\u00a2\u0006\u0004\b\u001f\u0010 J\u0019\u0010\"\u001a\u00020\b2\b\b\u0002\u0010!\u001a\u00020\rH\u0002\u00a2\u0006\u0004\b\"\u0010\u001eJ\u0019\u0010%\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030$0#H\u0002\u00a2\u0006\u0004\b%\u0010&J\u001b\u0010)\u001a\u00020(2\n\u0010'\u001a\u0006\u0012\u0002\b\u00030$H\u0002\u00a2\u0006\u0004\b)\u0010*J'\u0010/\u001a\u00020\b2\u0006\u0010,\u001a\u00020+2\u0006\u0010-\u001a\u00020+2\u0006\u0010.\u001a\u00020+H\u0002\u00a2\u0006\u0004\b/\u00100R\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u00101R\u0014\u00103\u001a\u0002028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b3\u00104R\u0014\u00106\u001a\u0002058\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b6\u00107R\u001a\u00108\u001a\b\u0012\u0004\u0012\u00020\u00150#8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b8\u00109R\u001c\u0010:\u001a\b\u0012\u0004\u0012\u00020\u00150#8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b:\u00109R\u0014\u0010<\u001a\u00020;8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b<\u0010=R\u0014\u0010?\u001a\u00020>8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b?\u0010@R\u0016\u0010\u0016\u001a\u00020\u00158\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0016\u0010AR\u001c\u0010C\u001a\b\u0012\u0004\u0012\u00020B0#8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bC\u00109R\u0016\u0010D\u001a\u00020B8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bD\u0010ER\u001c\u0010G\u001a\b\u0012\u0004\u0012\u00020F0#8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bG\u00109R\u001c\u0010H\u001a\b\u0012\u0004\u0012\u00020(0#8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bH\u00109R\u0014\u0010I\u001a\u00020;8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bI\u0010=R\u0014\u0010J\u001a\u00020>8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bJ\u0010@\u00a8\u0006M"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIModuleList;", "Lorg/cobalt/internal/ui/panel/UIPanel;", "Lorg/cobalt/api/addon/AddonMetadata;", "metadata", "Lorg/cobalt/api/addon/Addon;", "addon", "<init>", "(Lorg/cobalt/api/addon/AddonMetadata;Lorg/cobalt/api/addon/Addon;)V", "", "render", "()V", "", "button", "", "mouseClicked", "(I)Z", "", "horizontalAmount", "verticalAmount", "mouseScrolled", "(DD)Z", "Lorg/cobalt/internal/ui/components/UIModule;", "module", "setModule", "(Lorg/cobalt/internal/ui/components/UIModule;)V", "Lorg/cobalt/api/module/Module;", "updateAuxPanel", "(Lorg/cobalt/api/module/Module;)V", "resetSelection", "refreshTabs", "(Z)V", "shouldShowTabs", "()Z", "resetScroll", "rebuildSettings", "", "Lorg/cobalt/api/module/setting/Setting;", "filteredSettings", "()Ljava/util/List;", "setting", "Lorg/cobalt/internal/ui/UIComponent;", "toComponent", "(Lorg/cobalt/api/module/setting/Setting;)Lorg/cobalt/internal/ui/UIComponent;", "", "startX", "y", "maxWidth", "renderSettingsTabs", "(FFF)V", "Lorg/cobalt/api/addon/AddonMetadata;", "Lorg/cobalt/internal/ui/components/UITopbar;", "topBar", "Lorg/cobalt/internal/ui/components/UITopbar;", "Lorg/cobalt/internal/ui/components/UIBackButton;", "backButton", "Lorg/cobalt/internal/ui/components/UIBackButton;", "allModules", "Ljava/util/List;", "modules", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "modulesScroll", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "Lorg/cobalt/internal/ui/util/GridLayout;", "modulesLayout", "Lorg/cobalt/internal/ui/util/GridLayout;", "Lorg/cobalt/internal/ui/components/UIModule;", "", "settingsTabs", "selectedSettingsTab", "Ljava/lang/String;", "Lorg/cobalt/internal/ui/panel/panels/UIModuleList$SettingsTabHitbox;", "tabHitboxes", "settings", "settingsScroll", "settingsLayout", "Companion", "SettingsTabHitbox", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUIModuleList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UIModuleList.kt\norg/cobalt/internal/ui/panel/panels/UIModuleList\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,342:1\n1586#2:343\n1661#2,3:344\n1915#2,2:347\n1915#2,2:349\n1915#2,2:351\n296#2,2:353\n1915#2,2:355\n1915#2,2:357\n777#2:359\n873#2,2:360\n1586#2:362\n1661#2,2:363\n1663#2:366\n1586#2:367\n1661#2,3:368\n777#2:371\n873#2,2:372\n777#2:374\n873#2:375\n1807#2,3:376\n874#2:379\n1#3:365\n*S KotlinDebug\n*F\n+ 1 UIModuleList.kt\norg/cobalt/internal/ui/panel/panels/UIModuleList\n*L\n46#1:343\n46#1:344,3\n145#1:347,2\n167#1:349,2\n171#1:351,2\n182#1:353,2\n196#1:355,2\n217#1:357,2\n240#1:359\n240#1:360,2\n241#1:362\n241#1:363,2\n241#1:366\n257#1:367\n257#1:368,3\n266#1:371\n266#1:372,2\n91#1:374\n91#1:375\n93#1:376,3\n91#1:379\n*E\n"})
public final class UIModuleList
extends UIPanel {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final AddonMetadata metadata;
    @NotNull
    private final UITopbar topBar;
    @NotNull
    private final UIBackButton backButton;
    @NotNull
    private final List<UIModule> allModules;
    @NotNull
    private List<UIModule> modules;
    @NotNull
    private final ScrollHandler modulesScroll;
    @NotNull
    private final GridLayout modulesLayout;
    @NotNull
    private UIModule module;
    @NotNull
    private List<String> settingsTabs;
    @NotNull
    private String selectedSettingsTab;
    @NotNull
    private List<SettingsTabHitbox> tabHitboxes;
    @NotNull
    private List<? extends UIComponent> settings;
    @NotNull
    private final ScrollHandler settingsScroll;
    @NotNull
    private final GridLayout settingsLayout;
    @NotNull
    public static final String SIDE_GROUP = "__side__";
    private static final float TAB_BAR_HEIGHT = 24.0f;
    private static final float TAB_HORIZONTAL_PADDING = 10.0f;
    private static final float TAB_GAP = 8.0f;
    private static final float TAB_TEXT_SIZE = 12.0f;
    private static final float TAB_TEXT_Y = 5.0f;
    private static final float TAB_BAR_GAP = 8.0f;

    /*
     * WARNING - void declaration
     */
    public UIModuleList(@NotNull AddonMetadata metadata, @NotNull Addon addon) {
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        Intrinsics.checkNotNullParameter((Object)metadata, (String)"metadata");
        Intrinsics.checkNotNullParameter((Object)addon, (String)"addon");
        super(0.0f, 0.0f, 890.0f, 600.0f);
        this.metadata = metadata;
        this.topBar = new UITopbar("Modules");
        this.backButton = new UIBackButton(null, 1, null);
        Iterable iterable = CollectionsKt.withIndex((Iterable)addon.getModules());
        UIModuleList uIModuleList = this;
        boolean $i$f$map = false;
        void var5_6 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            IndexedValue indexedValue = (IndexedValue)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            int index = indexedValue.component1();
            Module module = (Module)indexedValue.component2();
            collection.add(new UIModule(module, this, index == 0));
        }
        uIModuleList.allModules = (List)destination$iv$iv;
        this.modules = this.allModules;
        this.modulesScroll = new ScrollHandler(0.0f, 1, null);
        this.modulesLayout = new GridLayout(1, 182.5f, 40.0f, 5.0f);
        this.module = (UIModule)CollectionsKt.first(this.modules);
        this.settingsTabs = CollectionsKt.emptyList();
        this.selectedSettingsTab = "General";
        this.tabHitboxes = CollectionsKt.emptyList();
        this.settings = CollectionsKt.emptyList();
        this.settingsScroll = new ScrollHandler(0.0f, 1, null);
        this.settingsLayout = new GridLayout(1, 627.5f, 60.0f, 10.0f);
        this.getComponents().addAll((Collection<UIComponent>)this.modules);
        this.getComponents().add(this.backButton);
        this.getComponents().add(this.topBar);
        this.refreshTabs(true);
        this.rebuildSettings(false);
        this.updateAuxPanel(((UIModule)CollectionsKt.first(this.modules)).getModule());
        this.topBar.searchChanged((Function1<? super String, Unit>)((Function1)arg_0 -> UIModuleList._init_$lambda$0(this, arg_0)));
    }

    @Override
    public void render() {
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ThemeManager.INSTANCE.getCurrentTheme().getBackground(), 10.0f);
        NVGRenderer.line(this.getX() + this.getWidth() / 4.0f, this.getY() + this.topBar.getHeight() / (float)2 + this.getHeight() * 1.0f / 8.0f, this.getX() + this.getWidth() / 4.0f, this.getY() + this.topBar.getHeight() / (float)2 + this.getHeight() * 7.0f / 8.0f, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getModuleDivider());
        this.topBar.updateBounds(this.getX(), this.getY()).render();
        this.backButton.updateBounds(this.getX() + 20.0f, this.getY() + this.topBar.getHeight() + 20.0f).render();
        NVGRenderer.text$default(this.metadata.getName(), this.getX() + this.backButton.getWidth() + 35.0f, this.getY() + this.topBar.getHeight() + 27.5f, 15.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
        float startY = this.getY() + this.topBar.getHeight() + this.backButton.getHeight() + 40.0f;
        float visibleHeight = this.getHeight() - (this.topBar.getHeight() + this.backButton.getHeight() + 40.0f);
        this.modulesScroll.setMaxScroll(this.modulesLayout.contentHeight(this.modules.size()) + 20.0f, visibleHeight);
        NVGRenderer.pushScissor(this.getX(), startY, this.getWidth() / 4.0f, visibleHeight);
        float modulesScrollOffset = this.modulesScroll.getOffset();
        this.modulesLayout.layout(this.getX() + 20.0f, startY - modulesScrollOffset, this.modules);
        Iterable $this$forEach$iv = this.modules;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            UIComponent p0 = (UIComponent)element$iv;
            boolean bl = false;
            p0.render();
        }
        NVGRenderer.popScissor();
        float settingsStartX = this.getX() + this.getWidth() / 4.0f + 20.0f;
        float settingsRegionWidth = this.getWidth() * 3.0f / 4.0f - 40.0f;
        boolean showTabs = this.shouldShowTabs();
        if (showTabs) {
            this.renderSettingsTabs(settingsStartX, startY - 24.0f - 8.0f, settingsRegionWidth);
        } else {
            this.tabHitboxes = CollectionsKt.emptyList();
        }
        float settingsViewY = showTabs ? startY : startY - 35.0f;
        float settingsViewHeight = showTabs ? visibleHeight : visibleHeight + 35.0f;
        this.settingsScroll.setMaxScroll(this.settingsLayout.contentHeight(this.settings.size()) - 15.0f, settingsViewHeight);
        NVGRenderer.pushScissor(settingsStartX, settingsViewY, settingsRegionWidth, settingsViewHeight);
        float settingsScrollOffset = this.settingsScroll.getOffset();
        this.settingsLayout.layout(settingsStartX, settingsViewY - settingsScrollOffset, this.settings);
        Iterable $this$forEach$iv2 = this.settings;
        boolean $i$f$forEach2 = false;
        for (Object element$iv : $this$forEach$iv2) {
            UIComponent p0 = (UIComponent)element$iv;
            boolean bl = false;
            p0.render();
        }
        NVGRenderer.popScissor();
        $this$forEach$iv2 = this.settings;
        $i$f$forEach2 = false;
        for (Object element$iv : $this$forEach$iv2) {
            UIComponent setting = (UIComponent)element$iv;
            boolean bl = false;
            UIComponent uIComponent = setting;
            if (uIComponent instanceof UIModeSetting) {
                ((UIModeSetting)setting).renderDropdown();
                continue;
            }
            if (uIComponent instanceof UIColorSetting) {
                ((UIColorSetting)setting).drawColorPicker();
                continue;
            }
            if (!(uIComponent instanceof UIRoutePickerSetting)) continue;
            ((UIRoutePickerSetting)setting).renderDropdown();
        }
    }

    @Override
    public boolean mouseClicked(int button) {
        if (button == 0 && this.shouldShowTabs()) {
            Object v0;
            block3: {
                Iterable $this$firstOrNull$iv = this.tabHitboxes;
                boolean $i$f$firstOrNull = false;
                for (Object element$iv : $this$firstOrNull$iv) {
                    SettingsTabHitbox tab = (SettingsTabHitbox)element$iv;
                    boolean bl = false;
                    if (!ExtensionsKt.isHoveringOver(tab.getX(), tab.getY(), tab.getWidth(), tab.getHeight())) continue;
                    v0 = element$iv;
                    break block3;
                }
                v0 = null;
            }
            SettingsTabHitbox hit = v0;
            if (hit != null && !Intrinsics.areEqual((Object)hit.getName(), (Object)this.selectedSettingsTab)) {
                this.selectedSettingsTab = hit.getName();
                UIModuleList.rebuildSettings$default(this, false, 1, null);
                return true;
            }
        }
        return super.mouseClicked(button);
    }

    @Override
    public boolean mouseScrolled(double horizontalAmount, double verticalAmount) {
        Iterable $this$forEach$iv = this.settings;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            UIComponent it = (UIComponent)element$iv;
            boolean bl = false;
            if (!it.mouseScrolled(horizontalAmount, verticalAmount)) continue;
            return true;
        }
        if (ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth() / 4.0f, this.getHeight())) {
            this.modulesScroll.handleScroll(verticalAmount);
            return true;
        }
        if (ExtensionsKt.isHoveringOver(this.getX() + this.getWidth() / 4.0f, this.getY(), this.getWidth() * 3.0f / 4.0f, this.getHeight())) {
            this.settingsScroll.handleScroll(verticalAmount);
            return true;
        }
        return false;
    }

    public final void setModule(@NotNull UIModule module) {
        Intrinsics.checkNotNullParameter((Object)module, (String)"module");
        boolean moduleChanged = !Intrinsics.areEqual((Object)this.module, (Object)module);
        Iterable $this$forEach$iv = this.modules;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            UIModule it = (UIModule)element$iv;
            boolean bl = false;
            if (Intrinsics.areEqual((Object)it, (Object)module)) {
                UIModule.setSelected$default(module, false, 1, null);
                continue;
            }
            it.setSelected(false);
        }
        this.module = module;
        this.refreshTabs(moduleChanged);
        UIModuleList.rebuildSettings$default(this, false, 1, null);
        this.updateAuxPanel(module.getModule());
    }

    private final void updateAuxPanel(Module module) {
        if (module == MiningModule.INSTANCE || module == MiningMacroModule.INSTANCE) {
            UIConfig.INSTANCE.setAuxPanel(new UIMiningStatsPanel());
        } else {
            UIConfig.INSTANCE.clearAuxPanel();
        }
    }

    /*
     * WARNING - void declaration
     */
    private final void refreshTabs(boolean resetSelection) {
        void $this$mapTo$iv$iv;
        Setting it;
        Iterable $this$filterTo$iv$iv;
        Iterable $this$filter$iv = this.module.getSettings();
        boolean $i$f$filter = false;
        Iterable iterable = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            it = (Setting)element$iv$iv;
            boolean bl = false;
            if (!(!Intrinsics.areEqual((Object)it.getUiGroup(), (Object)SIDE_GROUP))) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        Iterable $this$map$iv = (List)destination$iv$iv;
        boolean $i$f$map = false;
        $this$filterTo$iv$iv = $this$map$iv;
        destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            CharSequence charSequence;
            void setting;
            it = (Setting)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            CharSequence charSequence2 = setting.getUiGroup();
            if (StringsKt.isBlank((CharSequence)charSequence2)) {
                boolean bl2 = false;
                charSequence = "General";
            } else {
                charSequence = charSequence2;
            }
            collection.add((String)charSequence);
        }
        List groups = CollectionsKt.distinct((Iterable)((List)destination$iv$iv));
        List list = this.settingsTabs = groups.isEmpty() ? CollectionsKt.listOf((Object)"General") : groups;
        if (resetSelection || !this.settingsTabs.contains(this.selectedSettingsTab)) {
            this.selectedSettingsTab = (String)CollectionsKt.first(this.settingsTabs);
        }
        this.tabHitboxes = CollectionsKt.emptyList();
    }

    private final boolean shouldShowTabs() {
        return this.settingsTabs.size() > 1 && ((CharSequence)this.topBar.getSearchText()).length() == 0;
    }

    /*
     * WARNING - void declaration
     */
    private final void rebuildSettings(boolean resetScroll) {
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        this.getComponents().removeAll((Collection)this.settings);
        Iterable iterable = this.filteredSettings();
        UIModuleList uIModuleList = this;
        boolean $i$f$map = false;
        void var4_5 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void p0;
            Setting setting = (Setting)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(this.toComponent((Setting<?>)p0));
        }
        uIModuleList.settings = (List)destination$iv$iv;
        this.getComponents().addAll((Collection<UIComponent>)this.settings);
        if (resetScroll) {
            this.settingsScroll.reset();
        }
    }

    static /* synthetic */ void rebuildSettings$default(UIModuleList uIModuleList, boolean bl, int n, Object object) {
        if ((n & 1) != 0) {
            bl = true;
        }
        uIModuleList.rebuildSettings(bl);
    }

    /*
     * Unable to fully structure code
     */
    private final List<Setting<?>> filteredSettings() {
        v0 = StringsKt.trim((CharSequence)this.topBar.getSearchText()).toString().toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue((Object)v0, (String)"toLowerCase(...)");
        searchLower = v0;
        $this$filter$iv = this.module.getSettings();
        $i$f$filter = false;
        var4_4 = $this$filter$iv;
        destination$iv$iv = new ArrayList<E>();
        $i$f$filterTo = false;
        for (T element$iv$iv : $this$filterTo$iv$iv) {
            block10: {
                block9: {
                    setting = (Setting)element$iv$iv;
                    $i$a$-filter-UIModuleList$filteredSettings$1 = false;
                    if (!Intrinsics.areEqual((Object)setting.getUiGroup(), (Object)"__side__")) break block9;
                    v1 = false;
                    break block10;
                }
                if (((CharSequence)searchLower).length() == 0) ** GOTO lbl-1000
                v2 = setting.getName().toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)v2, (String)"toLowerCase(...)");
                if (StringsKt.contains$default((CharSequence)v2, (CharSequence)searchLower, (boolean)false, (int)2, null)) ** GOTO lbl-1000
                v3 = setting.getDescription().toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)v3, (String)"toLowerCase(...)");
                if (StringsKt.contains$default((CharSequence)v3, (CharSequence)searchLower, (boolean)false, (int)2, null)) lbl-1000:
                // 3 sources

                {
                    v4 = true;
                } else {
                    v4 = matchesSearch = false;
                }
                if (!matchesSearch) {
                    v1 = false;
                } else if (((CharSequence)searchLower).length() > 0 || this.settingsTabs.size() <= 1) {
                    v1 = true;
                } else {
                    var12_12 = setting.getUiGroup();
                    if (StringsKt.isBlank((CharSequence)var12_12)) {
                        $i$a$-ifBlank-UIModuleList$filteredSettings$1$group$1 = false;
                        v5 = "General";
                    } else {
                        v5 = var12_12;
                    }
                    group = (String)v5;
                    v1 = Intrinsics.areEqual((Object)group, (Object)this.selectedSettingsTab);
                }
            }
            if (!v1) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        return (List)destination$iv$iv;
    }

    private final UIComponent toComponent(Setting<?> setting) {
        UIComponent uIComponent;
        Setting<?> setting2 = setting;
        if (setting2 instanceof ActionSetting) {
            uIComponent = new UIActionSetting((ActionSetting)setting);
        } else if (setting2 instanceof CommandHotkeySetting) {
            uIComponent = new UICommandHotkeySetting((CommandHotkeySetting)setting);
        } else if (setting2 instanceof CheckboxSetting) {
            uIComponent = new UICheckboxSetting((CheckboxSetting)setting);
        } else if (setting2 instanceof ColorSetting) {
            uIComponent = new UIColorSetting((ColorSetting)setting);
        } else if (setting2 instanceof InfoSetting) {
            uIComponent = new UIInfoSetting((InfoSetting)setting);
        } else if (setting2 instanceof KeyBindSetting) {
            uIComponent = new UIKeyBindSetting((KeyBindSetting)setting);
        } else if (setting2 instanceof ModeSetting) {
            uIComponent = new UIModeSetting((ModeSetting)setting);
        } else if (setting2 instanceof RangeSetting) {
            uIComponent = new UIRangeSetting((RangeSetting)setting);
        } else if (setting2 instanceof RoutePickerSetting) {
            uIComponent = new UIRoutePickerSetting((RoutePickerSetting)setting);
        } else if (setting2 instanceof SliderSetting) {
            uIComponent = new UISliderSetting((SliderSetting)setting);
        } else {
            Intrinsics.checkNotNull(setting, (String)"null cannot be cast to non-null type org.cobalt.api.module.setting.impl.TextSetting");
            uIComponent = new UITextSetting((TextSetting)setting);
        }
        return uIComponent;
    }

    private final void renderSettingsTabs(float startX, float y, float maxWidth) {
        String tab;
        float tabWidth;
        ArrayList<SettingsTabHitbox> hitboxes = new ArrayList<SettingsTabHitbox>(this.settingsTabs.size());
        float cursorX = startX;
        float maxX = startX + maxWidth;
        Iterator<String> iterator = this.settingsTabs.iterator();
        while (iterator.hasNext() && !(cursorX + (tabWidth = NVGRenderer.textWidth$default(tab = iterator.next(), 12.0f, null, 4, null) + 20.0f) > maxX)) {
            boolean selected = Intrinsics.areEqual((Object)tab, (Object)this.selectedSettingsTab);
            boolean hovering = ExtensionsKt.isHoveringOver(cursorX, y, tabWidth, 24.0f);
            int bgColor = selected ? ThemeManager.INSTANCE.getCurrentTheme().getSelectedOverlay() : (hovering ? ThemeManager.INSTANCE.getCurrentTheme().getOverlay() : ThemeManager.INSTANCE.getCurrentTheme().getControlBg());
            int borderColor = selected ? ThemeManager.INSTANCE.getCurrentTheme().getAccent() : ThemeManager.INSTANCE.getCurrentTheme().getControlBorder();
            int textColor = selected ? ThemeManager.INSTANCE.getCurrentTheme().getAccent() : ThemeManager.INSTANCE.getCurrentTheme().getText();
            NVGRenderer.rect(cursorX, y, tabWidth, 24.0f, bgColor, 5.0f);
            NVGRenderer.hollowRect(cursorX, y, tabWidth, 24.0f, 1.0f, borderColor, 5.0f);
            NVGRenderer.text$default(tab, cursorX + 10.0f, y + 5.0f, 12.0f, textColor, null, 32, null);
            hitboxes.add(new SettingsTabHitbox(tab, cursorX, y, tabWidth, 24.0f));
            cursorX += tabWidth + 8.0f;
        }
        this.tabHitboxes = hitboxes;
    }

    /*
     * Unable to fully structure code
     */
    private static final Unit _init_$lambda$0(UIModuleList this$0, String searchText) {
        block10: {
            block9: {
                Intrinsics.checkNotNullParameter((Object)searchText, (String)"searchText");
                v0 = this$0;
                if (!(((CharSequence)searchText).length() == 0)) break block9;
                v1 = this$0.allModules;
                break block10;
            }
            v2 = searchText.toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue((Object)v2, (String)"toLowerCase(...)");
            searchLower = v2;
            var3_3 = this$0.allModules;
            var18_4 = v0;
            $i$f$filter = false;
            var5_6 = $this$filter$iv;
            destination$iv$iv = new ArrayList<E>();
            $i$f$filterTo = false;
            for (T element$iv$iv : $this$filterTo$iv$iv) {
                block8: {
                    block11: {
                        uiModule = (UIModule)element$iv$iv;
                        $i$a$-filter-UIModuleList$1$1 = false;
                        v3 = uiModule.getModule().getName().toLowerCase(Locale.ROOT);
                        Intrinsics.checkNotNullExpressionValue((Object)v3, (String)"toLowerCase(...)");
                        if (StringsKt.contains$default((CharSequence)v3, (CharSequence)searchLower, (boolean)false, (int)2, null)) ** GOTO lbl-1000
                        $this$any$iv = uiModule.getModule().getSettings();
                        $i$f$any = false;
                        if (!($this$any$iv instanceof Collection) || !((Collection)$this$any$iv).isEmpty()) break block11;
                        v4 = false;
                        break block8;
                    }
                    for (T element$iv : $this$any$iv) {
                        setting = (Setting)element$iv;
                        $i$a$-any-UIModuleList$1$1$1 = false;
                        v5 = setting.getName().toLowerCase(Locale.ROOT);
                        Intrinsics.checkNotNullExpressionValue((Object)v5, (String)"toLowerCase(...)");
                        if (StringsKt.contains$default((CharSequence)v5, (CharSequence)searchLower, (boolean)false, (int)2, null)) ** GOTO lbl-1000
                        v6 = setting.getDescription().toLowerCase(Locale.ROOT);
                        Intrinsics.checkNotNullExpressionValue((Object)v6, (String)"toLowerCase(...)");
                        if (StringsKt.contains$default((CharSequence)v6, (CharSequence)searchLower, (boolean)false, (int)2, null)) lbl-1000:
                        // 2 sources

                        {
                            v7 = true;
                        } else {
                            v7 = false;
                        }
                        if (!v7) continue;
                        v4 = true;
                        break block8;
                    }
                    v4 = false;
                }
                if (v4) lbl-1000:
                // 2 sources

                {
                    v8 = true;
                } else {
                    v8 = false;
                }
                if (!v8) continue;
                destination$iv$iv.add(element$iv$iv);
            }
            v1 = (List)destination$iv$iv;
            v0 = var18_4;
        }
        v0.modules = v1;
        if (((Collection)this$0.modules).isEmpty() == false) {
            this$0.setModule((UIModule)CollectionsKt.first(this$0.modules));
        } else {
            this$0.getComponents().removeAll((Collection)this$0.settings);
            this$0.settings = CollectionsKt.emptyList();
            this$0.settingsTabs = CollectionsKt.emptyList();
            this$0.tabHitboxes = CollectionsKt.emptyList();
            UIConfig.INSTANCE.clearAuxPanel();
        }
        return Unit.INSTANCE;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\b\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006R\u0014\u0010\b\u001a\u00020\u00078\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\b\u0010\tR\u0014\u0010\n\u001a\u00020\u00078\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\n\u0010\tR\u0014\u0010\u000b\u001a\u00020\u00078\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u000b\u0010\tR\u0014\u0010\f\u001a\u00020\u00078\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\f\u0010\tR\u0014\u0010\r\u001a\u00020\u00078\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\r\u0010\tR\u0014\u0010\u000e\u001a\u00020\u00078\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u000e\u0010\t\u00a8\u0006\u000f"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIModuleList$Companion;", "", "<init>", "()V", "", "SIDE_GROUP", "Ljava/lang/String;", "", "TAB_BAR_HEIGHT", "F", "TAB_HORIZONTAL_PADDING", "TAB_GAP", "TAB_TEXT_SIZE", "TAB_TEXT_Y", "TAB_BAR_GAP", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000b\b\u0082\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0004\u0012\u0006\u0010\b\u001a\u00020\u0004\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\u000eJ\u0010\u0010\u0010\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u000eJ\u0010\u0010\u0011\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u000eJB\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u00042\b\b\u0002\u0010\b\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u001b\u0010\u0016\u001a\u00020\u00152\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0019\u001a\u00020\u0018H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\u0011\u0010\u001b\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001b\u0010\fR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001c\u001a\u0004\b\u001d\u0010\fR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001e\u001a\u0004\b\u001f\u0010\u000eR\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001e\u001a\u0004\b \u0010\u000eR\u0017\u0010\u0007\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u001e\u001a\u0004\b!\u0010\u000eR\u0017\u0010\b\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\b\u0010\u001e\u001a\u0004\b\"\u0010\u000e\u00a8\u0006#"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIModuleList$SettingsTabHitbox;", "", "", "name", "", "x", "y", "width", "height", "<init>", "(Ljava/lang/String;FFFF)V", "component1", "()Ljava/lang/String;", "component2", "()F", "component3", "component4", "component5", "copy", "(Ljava/lang/String;FFFF)Lorg/cobalt/internal/ui/panel/panels/UIModuleList$SettingsTabHitbox;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getName", "F", "getX", "getY", "getWidth", "getHeight", "cobalt"})
    private static final class SettingsTabHitbox {
        @NotNull
        private final String name;
        private final float x;
        private final float y;
        private final float width;
        private final float height;

        public SettingsTabHitbox(@NotNull String name, float x, float y, float width, float height) {
            Intrinsics.checkNotNullParameter((Object)name, (String)"name");
            this.name = name;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @NotNull
        public final String getName() {
            return this.name;
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
            return this.name;
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
        public final SettingsTabHitbox copy(@NotNull String name, float x, float y, float width, float height) {
            Intrinsics.checkNotNullParameter((Object)name, (String)"name");
            return new SettingsTabHitbox(name, x, y, width, height);
        }

        public static /* synthetic */ SettingsTabHitbox copy$default(SettingsTabHitbox settingsTabHitbox, String string, float f, float f2, float f3, float f4, int n, Object object) {
            if ((n & 1) != 0) {
                string = settingsTabHitbox.name;
            }
            if ((n & 2) != 0) {
                f = settingsTabHitbox.x;
            }
            if ((n & 4) != 0) {
                f2 = settingsTabHitbox.y;
            }
            if ((n & 8) != 0) {
                f3 = settingsTabHitbox.width;
            }
            if ((n & 0x10) != 0) {
                f4 = settingsTabHitbox.height;
            }
            return settingsTabHitbox.copy(string, f, f2, f3, f4);
        }

        @NotNull
        public String toString() {
            return "SettingsTabHitbox(name=" + this.name + ", x=" + this.x + ", y=" + this.y + ", width=" + this.width + ", height=" + this.height + ")";
        }

        public int hashCode() {
            int result = this.name.hashCode();
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
            if (!(other instanceof SettingsTabHitbox)) {
                return false;
            }
            SettingsTabHitbox settingsTabHitbox = (SettingsTabHitbox)other;
            if (!Intrinsics.areEqual((Object)this.name, (Object)settingsTabHitbox.name)) {
                return false;
            }
            if (Float.compare(this.x, settingsTabHitbox.x) != 0) {
                return false;
            }
            if (Float.compare(this.y, settingsTabHitbox.y) != 0) {
                return false;
            }
            if (Float.compare(this.width, settingsTabHitbox.width) != 0) {
                return false;
            }
            return Float.compare(this.height, settingsTabHitbox.height) == 0;
        }
    }
}

