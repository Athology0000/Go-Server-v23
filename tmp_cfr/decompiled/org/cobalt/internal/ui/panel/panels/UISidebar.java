/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.StringsKt
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.panel.panels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import net.minecraft.class_310;
import org.cobalt.api.addon.Addon;
import org.cobalt.api.addon.AddonMetadata;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.ModuleManager;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Image;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.components.tooltips.TooltipPosition;
import org.cobalt.internal.ui.components.tooltips.UITooltip;
import org.cobalt.internal.ui.components.tooltips.impl.UITextTooltip;
import org.cobalt.internal.ui.panel.UIPanel;
import org.cobalt.internal.ui.panel.panels.UIAccountManagerPanel;
import org.cobalt.internal.ui.panel.panels.UIAddonList;
import org.cobalt.internal.ui.panel.panels.UIModuleList;
import org.cobalt.internal.ui.panel.panels.UIRoutesPanel;
import org.cobalt.internal.ui.panel.panels.UIThemeSelector;
import org.cobalt.internal.ui.screen.UIConfig;
import org.cobalt.internal.ui.screen.UIHudEditor;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0000\u0018\u0000 )2\u00020\u0001:\u0002*)B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u0017\u0010\t\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\t\u0010\nJ\u000f\u0010\u000b\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u000b\u0010\u0003J\u0017\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b\u000f\u0010\u0010J-\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\f2\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00140\u0013H\u0002\u00a2\u0006\u0004\b\u0016\u0010\u0017R\u0014\u0010\u0019\u001a\u00020\u00188\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u001aR\u0014\u0010\u001b\u001a\u00020\u00188\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001b\u0010\u001aR\u0014\u0010\u001c\u001a\u00020\u00188\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u001aR\u0014\u0010\u001d\u001a\u00020\u00188\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001d\u0010\u001aR\u0014\u0010\u001e\u001a\u00020\u00188\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u001aR\u001a\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00180\u00138\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001f\u0010 R\u0014\u0010!\u001a\u00020\u000e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b!\u0010\"R\u0016\u0010#\u001a\u00020\f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b#\u0010$R\u0016\u0010%\u001a\u00020\u000e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b%\u0010\"R\u0014\u0010'\u001a\u00020&8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b'\u0010(\u00a8\u0006+"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UISidebar;", "Lorg/cobalt/internal/ui/panel/UIPanel;", "<init>", "()V", "", "render", "", "button", "", "mouseClicked", "(I)Z", "refreshUserIcon", "", "profileId", "Lorg/cobalt/api/util/ui/helper/Image;", "loadUserIcon", "(Ljava/lang/String;)Lorg/cobalt/api/util/ui/helper/Image;", "id", "name", "", "Lorg/cobalt/api/module/Module;", "modules", "openQuickModules", "(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)V", "Lorg/cobalt/internal/ui/panel/panels/UISidebar$UIButton;", "moduleButton", "Lorg/cobalt/internal/ui/panel/panels/UISidebar$UIButton;", "hudButton", "designButton", "routesButton", "macrosButton", "navButtons", "Ljava/util/List;", "steveIcon", "Lorg/cobalt/api/util/ui/helper/Image;", "cachedProfileId", "Ljava/lang/String;", "userIcon", "Lorg/cobalt/internal/ui/components/tooltips/UITooltip;", "userIconTooltip", "Lorg/cobalt/internal/ui/components/tooltips/UITooltip;", "Companion", "UIButton", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUISidebar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UISidebar.kt\norg/cobalt/internal/ui/panel/panels/UISidebar\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,197:1\n1924#2,3:198\n777#2:201\n873#2,2:202\n*S KotlinDebug\n*F\n+ 1 UISidebar.kt\norg/cobalt/internal/ui/panel/panels/UISidebar\n*L\n77#1:198,3\n47#1:201\n47#1:202,2\n*E\n"})
public final class UISidebar
extends UIPanel {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final UIButton moduleButton = new UIButton("/assets/cobalt/textures/ui/box.svg", "Modules", (Function0<Unit>)((Function0)UISidebar::moduleButton$lambda$0));
    @NotNull
    private final UIButton hudButton = new UIButton("/assets/cobalt/textures/ui/palette.svg", "HUD Editor", (Function0<Unit>)((Function0)UISidebar::hudButton$lambda$0));
    @NotNull
    private final UIButton designButton = new UIButton("/assets/cobalt/textures/ui/interface.svg", "Design", (Function0<Unit>)((Function0)UISidebar::designButton$lambda$0));
    @NotNull
    private final UIButton routesButton = new UIButton("/assets/cobalt/textures/ui/routes.svg", "Routes", (Function0<Unit>)((Function0)UISidebar::routesButton$lambda$0));
    @NotNull
    private final UIButton macrosButton = new UIButton("/assets/cobalt/textures/ui/macros.svg", "Macros", (Function0<Unit>)((Function0)() -> UISidebar.macrosButton$lambda$0(this)));
    @NotNull
    private final List<UIButton> navButtons;
    @NotNull
    private final Image steveIcon;
    @NotNull
    private String cachedProfileId;
    @NotNull
    private Image userIcon;
    @NotNull
    private final UITooltip userIconTooltip;
    private static final float NAV_BUTTON_GAP = 35.0f;

    public UISidebar() {
        super(0.0f, 0.0f, 70.0f, 600.0f);
        Object[] objectArray = new UIButton[]{this.moduleButton, this.hudButton, this.designButton, this.routesButton, this.macrosButton};
        this.navButtons = CollectionsKt.listOf((Object[])objectArray);
        this.steveIcon = NVGRenderer.createImage("/assets/cobalt/textures/steve.png");
        String string = class_310.method_1551().method_1548().method_44717().toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        this.cachedProfileId = string;
        this.userIcon = this.loadUserIcon(this.cachedProfileId);
        this.userIconTooltip = new UITooltip(UISidebar::userIconTooltip$lambda$0, TooltipPosition.BELOW, 0.0f, 4, null);
        this.getComponents().addAll((Collection<UIComponent>)this.navButtons);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void render() {
        this.refreshUserIcon();
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ThemeManager.INSTANCE.getCurrentTheme().getBackground(), 10.0f);
        NVGRenderer.text$default("cb", this.getX() + this.getWidth() / 2.0f - 15.0f, this.getY() + 25.0f, 25.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
        Iterable $this$forEachIndexed$iv = this.navButtons;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            void button;
            int n;
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            UIButton uIButton = (UIButton)item$iv;
            int index = n;
            boolean bl = false;
            float buttonX = this.getX() + this.getWidth() / 2.0f - button.getWidth() / 2.0f;
            float buttonY = this.getY() + 75.0f + (float)index * 35.0f;
            button.setSelected(index == 0 || ExtensionsKt.isHoveringOver(buttonX, buttonY, button.getWidth(), button.getHeight())).updateBounds(buttonX, buttonY).render();
        }
        float userIconX = this.getX() + this.getWidth() / 2.0f - 16.0f;
        float userIconY = this.getY() + this.getHeight() - 32.0f - 20.0f;
        NVGRenderer.image$default(this.userIcon, userIconX, userIconY, 32.0f, 32.0f, 10.0f, 0, 64, null);
        this.userIconTooltip.updateBounds(userIconX, userIconY, 32.0f, 32.0f);
    }

    @Override
    public boolean mouseClicked(int button) {
        float userIconY;
        float userIconX = this.getX() + this.getWidth() / 2.0f - 16.0f;
        if (ExtensionsKt.isHoveringOver(userIconX, userIconY = this.getY() + this.getHeight() - 32.0f - 20.0f, 32.0f, 32.0f) && button == 0) {
            UIConfig.INSTANCE.swapBodyPanel(new UIAccountManagerPanel());
            return true;
        }
        return super.mouseClicked(button);
    }

    private final void refreshUserIcon() {
        String string = class_310.method_1551().method_1548().method_44717().toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        String profileId = string;
        if (Intrinsics.areEqual((Object)profileId, (Object)this.cachedProfileId)) {
            return;
        }
        this.cachedProfileId = profileId;
        this.userIcon = this.loadUserIcon(profileId);
    }

    private final Image loadUserIcon(String profileId) {
        Image image;
        try {
            image = NVGRenderer.createImage("https://mc-heads.net/avatar/" + profileId + "/100/face.png");
        }
        catch (Exception exception) {
            image = this.steveIcon;
        }
        return image;
    }

    private final void openQuickModules(String id, String name, List<? extends Module> modules) {
        if (modules.isEmpty()) {
            return;
        }
        UIConfig.INSTANCE.swapBodyPanel(new UIModuleList(new AddonMetadata(id, name, "builtin", CollectionsKt.emptyList(), CollectionsKt.emptyList(), null, 32, null), new Addon(modules){
            final /* synthetic */ List<Module> $modules;
            {
                this.$modules = $modules;
            }

            public void onLoad() {
            }

            public void onUnload() {
            }

            public List<Module> getModules() {
                return this.$modules;
            }
        }));
    }

    private static final Unit moduleButton$lambda$0() {
        UIConfig.INSTANCE.swapBodyPanel(new UIAddonList());
        return Unit.INSTANCE;
    }

    private static final Unit hudButton$lambda$0() {
        new UIHudEditor().openUI();
        return Unit.INSTANCE;
    }

    private static final Unit designButton$lambda$0() {
        UIConfig.INSTANCE.swapBodyPanel(new UIThemeSelector());
        return Unit.INSTANCE;
    }

    private static final Unit routesButton$lambda$0() {
        UIConfig.INSTANCE.swapBodyPanel(new UIRoutesPanel());
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    private static final Unit macrosButton$lambda$0(UISidebar this$0) {
        void $this$filterTo$iv$iv;
        void $this$filter$iv;
        Iterable iterable = ModuleManager.getModules();
        String string = "Macros";
        String string2 = "cobalt-quick-macros";
        UISidebar uISidebar = this$0;
        boolean $i$f$filter = false;
        void var3_6 = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            Module it = (Module)element$iv$iv;
            boolean bl = false;
            if (!StringsKt.contains((CharSequence)it.getName(), (CharSequence)"macro", (boolean)true)) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        List list = (List)destination$iv$iv;
        uISidebar.openQuickModules(string2, string, list);
        return Unit.INSTANCE;
    }

    private static final UIComponent userIconTooltip$lambda$0() {
        return new UITextTooltip("Manage Accounts (" + class_310.method_1551().method_1548().method_1676() + ")", 0.0f, 2, null);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UISidebar$Companion;", "", "<init>", "()V", "", "NAV_BUTTON_GAP", "F", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0002\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\u0004\b\b\u0010\tJ\u0015\u0010\f\u001a\u00020\u00012\u0006\u0010\u000b\u001a\u00020\n\u00a2\u0006\u0004\b\f\u0010\rJ\u000f\u0010\u000e\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0017\u0010\u0012\u001a\u00020\n2\u0006\u0010\u0011\u001a\u00020\u0010H\u0016\u00a2\u0006\u0004\b\u0012\u0010\u0013R\u001a\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u00058\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0007\u0010\u0014R\u0017\u0010\u0016\u001a\u00020\u00158\u0006\u00a2\u0006\f\n\u0004\b\u0016\u0010\u0017\u001a\u0004\b\u0018\u0010\u0019R\u0016\u0010\u000b\u001a\u00020\n8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u000b\u0010\u001aR\u0014\u0010\u001c\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u001d\u00a8\u0006\u001e"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UISidebar$UIButton;", "Lorg/cobalt/internal/ui/UIComponent;", "", "iconPath", "label", "Lkotlin/Function0;", "", "onClick", "<init>", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/jvm/functions/Function0;)V", "", "selected", "setSelected", "(Z)Lorg/cobalt/internal/ui/UIComponent;", "render", "()V", "", "button", "mouseClicked", "(I)Z", "Lkotlin/jvm/functions/Function0;", "Lorg/cobalt/api/util/ui/helper/Image;", "image", "Lorg/cobalt/api/util/ui/helper/Image;", "getImage", "()Lorg/cobalt/api/util/ui/helper/Image;", "Z", "Lorg/cobalt/internal/ui/components/tooltips/UITooltip;", "tooltip", "Lorg/cobalt/internal/ui/components/tooltips/UITooltip;", "cobalt"})
    private static final class UIButton
    extends UIComponent {
        @NotNull
        private final Function0<Unit> onClick;
        @NotNull
        private final Image image;
        private boolean selected;
        @NotNull
        private final UITooltip tooltip;

        public UIButton(@NotNull String iconPath, @NotNull String label, @NotNull Function0<Unit> onClick) {
            Intrinsics.checkNotNullParameter((Object)iconPath, (String)"iconPath");
            Intrinsics.checkNotNullParameter((Object)label, (String)"label");
            Intrinsics.checkNotNullParameter(onClick, (String)"onClick");
            super(0.0f, 0.0f, 22.0f, 22.0f);
            this.onClick = onClick;
            this.image = NVGRenderer.createImage(iconPath);
            this.tooltip = new UITooltip(() -> UIButton.tooltip$lambda$0(label), TooltipPosition.RIGHT, 0.0f, 4, null);
        }

        @NotNull
        public final Image getImage() {
            return this.image;
        }

        @NotNull
        public final UIComponent setSelected(boolean selected) {
            this.selected = selected;
            return this;
        }

        @Override
        public void render() {
            boolean hovering = ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight());
            NVGRenderer.image$default(this.image, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0.0f, hovering || this.selected ? ThemeManager.INSTANCE.getCurrentTheme().getAccent() : ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), 32, null);
            this.tooltip.updateBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }

        @Override
        public boolean mouseClicked(int button) {
            if (ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight()) && button == 0) {
                this.onClick.invoke();
                return true;
            }
            return false;
        }

        private static final UIComponent tooltip$lambda$0(String $label) {
            return new UITextTooltip($label, 0.0f, 2, null);
        }
    }
}

