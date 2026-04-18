/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.panel.panels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_310;
import org.cobalt.api.notification.NotificationManager;
import org.cobalt.api.ui.theme.Theme;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.ui.theme.impl.CustomTheme;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Image;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.animation.ColorAnimation;
import org.cobalt.internal.ui.components.UITopbar;
import org.cobalt.internal.ui.components.tooltips.TooltipPosition;
import org.cobalt.internal.ui.components.tooltips.UITooltip;
import org.cobalt.internal.ui.components.tooltips.impl.UITextTooltip;
import org.cobalt.internal.ui.panel.UIPanel;
import org.cobalt.internal.ui.panel.panels.UIThemeEditor;
import org.cobalt.internal.ui.screen.UIConfig;
import org.cobalt.internal.ui.theme.ThemeSerializer;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.cobalt.internal.ui.util.GridLayout;
import org.cobalt.internal.ui.util.ScrollHandler;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0000\u0018\u00002\u00020\u0001:\u0003 !\"B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u001f\u0010\n\u001a\u00020\t2\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\n\u0010\u000bR\u0014\u0010\r\u001a\u00020\f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\r\u0010\u000eR\u0014\u0010\u0010\u001a\u00020\u000f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0010\u0010\u0011R\u0014\u0010\u0013\u001a\u00020\u00128\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0013\u0010\u0014R\u001a\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00160\u00158\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0017\u0010\u0018R\u001c\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00160\u00158\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u0018R\u0014\u0010\u001b\u001a\u00020\u001a8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001b\u0010\u001cR\u0014\u0010\u001e\u001a\u00020\u001d8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u001f\u00a8\u0006#"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIThemeSelector;", "Lorg/cobalt/internal/ui/panel/UIPanel;", "<init>", "()V", "", "render", "", "horizontalAmount", "verticalAmount", "", "mouseScrolled", "(DD)Z", "Lorg/cobalt/internal/ui/components/UITopbar;", "topBar", "Lorg/cobalt/internal/ui/components/UITopbar;", "Lorg/cobalt/internal/ui/panel/panels/UIThemeSelector$UICreateThemeButton;", "createButton", "Lorg/cobalt/internal/ui/panel/panels/UIThemeSelector$UICreateThemeButton;", "Lorg/cobalt/internal/ui/panel/panels/UIThemeSelector$UIImportThemeButton;", "importButton", "Lorg/cobalt/internal/ui/panel/panels/UIThemeSelector$UIImportThemeButton;", "", "Lorg/cobalt/internal/ui/panel/panels/UIThemeSelector$UIThemeEntry;", "allEntries", "Ljava/util/List;", "entries", "Lorg/cobalt/internal/ui/util/GridLayout;", "gridLayout", "Lorg/cobalt/internal/ui/util/GridLayout;", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "scrollHandler", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "UICreateThemeButton", "UIImportThemeButton", "UIThemeEntry", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUIThemeSelector.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UIThemeSelector.kt\norg/cobalt/internal/ui/panel/panels/UIThemeSelector\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,327:1\n1586#2:328\n1661#2,3:329\n1915#2,2:332\n*S KotlinDebug\n*F\n+ 1 UIThemeSelector.kt\norg/cobalt/internal/ui/panel/panels/UIThemeSelector\n*L\n32#1:328\n32#1:329,3\n77#1:332,2\n*E\n"})
public final class UIThemeSelector
extends UIPanel {
    @NotNull
    private final UITopbar topBar = new UITopbar("Themes");
    @NotNull
    private final UICreateThemeButton createButton = new UICreateThemeButton();
    @NotNull
    private final UIImportThemeButton importButton = new UIImportThemeButton();
    @NotNull
    private final List<UIThemeEntry> allEntries;
    @NotNull
    private List<? extends UIThemeEntry> entries;
    @NotNull
    private final GridLayout gridLayout;
    @NotNull
    private final ScrollHandler scrollHandler;

    /*
     * WARNING - void declaration
     */
    public UIThemeSelector() {
        super(0.0f, 0.0f, 890.0f, 600.0f);
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        Iterable iterable = ThemeManager.INSTANCE.getThemes();
        UIThemeSelector uIThemeSelector = this;
        boolean $i$f$map = false;
        void var3_4 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            Theme theme = (Theme)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new UIThemeEntry((Theme)it));
        }
        uIThemeSelector.allEntries = (List)destination$iv$iv;
        this.entries = this.allEntries;
        this.gridLayout = new GridLayout(3, 270.0f, 100.0f, 20.0f);
        this.scrollHandler = new ScrollHandler(0.0f, 1, null);
        this.getComponents().addAll((Collection<UIComponent>)this.allEntries);
        this.getComponents().add(this.topBar);
        this.getComponents().add(this.createButton);
        this.getComponents().add(this.importButton);
    }

    @Override
    public void render() {
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ThemeManager.INSTANCE.getCurrentTheme().getBackground(), 10.0f);
        this.topBar.updateBounds(this.getX(), this.getY()).render();
        float buttonY = this.getY() + this.topBar.getHeight() / (float)2 - this.createButton.getHeight() / (float)2;
        float searchBarX = this.getX() + this.getWidth() - 320.0f;
        this.importButton.updateBounds(searchBarX - this.importButton.getWidth() - 10.0f, buttonY).render();
        this.createButton.updateBounds(searchBarX - this.importButton.getWidth() - 10.0f - this.createButton.getWidth() - 8.0f, buttonY).render();
        float startY = this.getY() + this.topBar.getHeight();
        float visibleHeight = this.getHeight() - this.topBar.getHeight();
        this.scrollHandler.setMaxScroll(this.gridLayout.contentHeight(this.entries.size()) + 20.0f, visibleHeight);
        NVGRenderer.pushScissor(this.getX(), startY, this.getWidth(), visibleHeight);
        float scrollOffset = this.scrollHandler.getOffset();
        this.gridLayout.layout(this.getX() + 20.0f, startY + 20.0f - scrollOffset, this.entries);
        Iterable $this$forEach$iv = this.entries;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            UIComponent p0 = (UIComponent)element$iv;
            boolean bl = false;
            p0.render();
        }
        NVGRenderer.popScissor();
    }

    @Override
    public boolean mouseScrolled(double horizontalAmount, double verticalAmount) {
        if (ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
            this.scrollHandler.handleScroll(verticalAmount);
            return true;
        }
        return false;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0002\u0018\u0000 \u00132\u00020\u0001:\u0001\u0013B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u0017\u0010\t\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\t\u0010\nR\u0014\u0010\f\u001a\u00020\u000b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\f\u0010\rR\u0016\u0010\u000e\u001a\u00020\b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u000e\u0010\u000fR\u0014\u0010\u0011\u001a\u00020\u00108\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0011\u0010\u0012\u00a8\u0006\u0014"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIThemeSelector$UICreateThemeButton;", "Lorg/cobalt/internal/ui/UIComponent;", "<init>", "()V", "", "render", "", "button", "", "mouseClicked", "(I)Z", "Lorg/cobalt/internal/ui/animation/ColorAnimation;", "colorAnim", "Lorg/cobalt/internal/ui/animation/ColorAnimation;", "wasHovering", "Z", "Lorg/cobalt/internal/ui/components/tooltips/UITooltip;", "tooltip", "Lorg/cobalt/internal/ui/components/tooltips/UITooltip;", "Companion", "cobalt"})
    private static final class UICreateThemeButton
    extends UIComponent {
        @NotNull
        public static final Companion Companion = new Companion(null);
        @NotNull
        private final ColorAnimation colorAnim = new ColorAnimation(150L);
        private boolean wasHovering;
        @NotNull
        private final UITooltip tooltip = new UITooltip(UICreateThemeButton::tooltip$lambda$0, TooltipPosition.BELOW, 0.0f, 4, null);
        @NotNull
        private static final Image plusIcon = NVGRenderer.createImage("/assets/cobalt/textures/ui/plus.svg");

        public UICreateThemeButton() {
            super(0.0f, 0.0f, 36.0f, 36.0f);
        }

        @Override
        public void render() {
            boolean hovering = ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight());
            if (hovering != this.wasHovering) {
                this.colorAnim.start();
                this.wasHovering = hovering;
            }
            int bgColor = this.colorAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), ThemeManager.INSTANCE.getCurrentTheme().getAccent(), !hovering);
            int borderColor = this.colorAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), ThemeManager.INSTANCE.getCurrentTheme().getAccent(), !hovering);
            int iconColor = this.colorAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getText(), ThemeManager.INSTANCE.getCurrentTheme().getTextOnAccent(), !hovering);
            NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), bgColor, 5.0f);
            NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.5f, borderColor, 5.0f);
            float iconSize = 18.0f;
            NVGRenderer.image(plusIcon, this.getX() + this.getWidth() / 2.0f - iconSize / 2.0f, this.getY() + this.getHeight() / 2.0f - iconSize / 2.0f, iconSize, iconSize, 0.0f, iconColor);
            this.tooltip.updateBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }

        @Override
        public boolean mouseClicked(int button) {
            if (button == 0 && ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
                CustomTheme theme = new CustomTheme("Custom " + (ThemeManager.INSTANCE.getThemes().size() + 1), false, 0.0f, 0.0f, 0.0f, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, 0x3FFFFFF, null);
                ThemeManager.INSTANCE.registerTheme(theme);
                ThemeManager.INSTANCE.setTheme(theme);
                UIConfig.INSTANCE.swapBodyPanel(new UIThemeEditor(theme));
                return true;
            }
            return false;
        }

        private static final UIComponent tooltip$lambda$0() {
            return new UITextTooltip("Create Theme", 0.0f, 2, null);
        }

        @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIThemeSelector$UICreateThemeButton$Companion;", "", "<init>", "()V", "Lorg/cobalt/api/util/ui/helper/Image;", "plusIcon", "Lorg/cobalt/api/util/ui/helper/Image;", "cobalt"})
        public static final class Companion {
            private Companion() {
            }

            public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
                this();
            }
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0002\u0018\u0000 \u00132\u00020\u0001:\u0001\u0013B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u0017\u0010\t\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\t\u0010\nR\u0014\u0010\f\u001a\u00020\u000b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\f\u0010\rR\u0016\u0010\u000e\u001a\u00020\b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u000e\u0010\u000fR\u0014\u0010\u0011\u001a\u00020\u00108\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0011\u0010\u0012\u00a8\u0006\u0014"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIThemeSelector$UIImportThemeButton;", "Lorg/cobalt/internal/ui/UIComponent;", "<init>", "()V", "", "render", "", "button", "", "mouseClicked", "(I)Z", "Lorg/cobalt/internal/ui/animation/ColorAnimation;", "colorAnim", "Lorg/cobalt/internal/ui/animation/ColorAnimation;", "wasHovering", "Z", "Lorg/cobalt/internal/ui/components/tooltips/UITooltip;", "tooltip", "Lorg/cobalt/internal/ui/components/tooltips/UITooltip;", "Companion", "cobalt"})
    @SourceDebugExtension(value={"SMAP\nUIThemeSelector.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UIThemeSelector.kt\norg/cobalt/internal/ui/panel/panels/UIThemeSelector$UIImportThemeButton\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,327:1\n1807#2,3:328\n*S KotlinDebug\n*F\n+ 1 UIThemeSelector.kt\norg/cobalt/internal/ui/panel/panels/UIThemeSelector$UIImportThemeButton\n*L\n231#1:328,3\n*E\n"})
    private static final class UIImportThemeButton
    extends UIComponent {
        @NotNull
        public static final Companion Companion = new Companion(null);
        @NotNull
        private final ColorAnimation colorAnim = new ColorAnimation(150L);
        private boolean wasHovering;
        @NotNull
        private final UITooltip tooltip = new UITooltip(UIImportThemeButton::tooltip$lambda$0, TooltipPosition.BELOW, 0.0f, 4, null);
        @NotNull
        private static final Image importIcon = NVGRenderer.createImage("/assets/cobalt/textures/ui/import.svg");

        public UIImportThemeButton() {
            super(0.0f, 0.0f, 36.0f, 36.0f);
        }

        @Override
        public void render() {
            boolean hovering = ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight());
            if (hovering != this.wasHovering) {
                this.colorAnim.start();
                this.wasHovering = hovering;
            }
            int bgColor = this.colorAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), ThemeManager.INSTANCE.getCurrentTheme().getAccentSecondary(), !hovering);
            int borderColor = this.colorAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), ThemeManager.INSTANCE.getCurrentTheme().getAccentSecondary(), !hovering);
            int iconColor = this.colorAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getText(), ThemeManager.INSTANCE.getCurrentTheme().getTextOnAccent(), !hovering);
            NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), bgColor, 5.0f);
            NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.5f, borderColor, 5.0f);
            float iconSize = 18.0f;
            NVGRenderer.image(importIcon, this.getX() + this.getWidth() / 2.0f - iconSize / 2.0f, this.getY() + this.getHeight() / 2.0f - iconSize / 2.0f, iconSize, iconSize, 0.0f, iconColor);
            this.tooltip.updateBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }

        @Override
        public boolean mouseClicked(int button) {
            if (button == 0 && ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
                String string = class_310.method_1551().field_1774.method_1460();
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getClipboard(...)");
                String clipboard = string;
                CustomTheme theme = ThemeSerializer.INSTANCE.fromBase64(clipboard);
                if (theme == null) {
                    NotificationManager.queue$default(NotificationManager.INSTANCE, "Import Failed", "Invalid theme data in clipboard", 0L, 4, null);
                    return true;
                }
                Object finalName = null;
                finalName = theme.getName();
                int counter = 2;
                while (true) {
                    boolean bl;
                    block6: {
                        Iterable $this$any$iv = ThemeManager.INSTANCE.getThemes();
                        boolean $i$f$any = false;
                        if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                            bl = false;
                        } else {
                            for (Object element$iv : $this$any$iv) {
                                Theme it = (Theme)element$iv;
                                boolean bl2 = false;
                                if (!Intrinsics.areEqual((Object)it.getName(), (Object)finalName)) continue;
                                bl = true;
                                break block6;
                            }
                            bl = false;
                        }
                    }
                    if (!bl) break;
                    finalName = theme.getName() + " (" + counter + ")";
                    ++counter;
                }
                theme.setName((String)finalName);
                ThemeManager.INSTANCE.registerTheme(theme);
                ThemeManager.INSTANCE.setTheme(theme);
                NotificationManager.queue$default(NotificationManager.INSTANCE, "Theme Imported", "'" + finalName + "' imported successfully", 0L, 4, null);
                UIConfig.INSTANCE.swapBodyPanel(new UIThemeEditor(theme));
                return true;
            }
            return false;
        }

        private static final UIComponent tooltip$lambda$0() {
            return new UITextTooltip("Import Theme", 0.0f, 2, null);
        }

        @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIThemeSelector$UIImportThemeButton$Companion;", "", "<init>", "()V", "Lorg/cobalt/api/util/ui/helper/Image;", "importIcon", "Lorg/cobalt/api/util/ui/helper/Image;", "cobalt"})
        public static final class Companion {
            private Companion() {
            }

            public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
                this();
            }
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\b\u0012\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000f\u0010\u0007\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\f\u0010\rJ\u000f\u0010\u000e\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\u000e\u0010\bR\u001a\u0010\u0003\u001a\u00020\u00028\u0004X\u0084\u0004\u00a2\u0006\f\n\u0004\b\u0003\u0010\u000f\u001a\u0004\b\u0010\u0010\u0011\u00a8\u0006\u0012"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIThemeSelector$UIThemeEntry;", "Lorg/cobalt/internal/ui/UIComponent;", "Lorg/cobalt/api/ui/theme/Theme;", "theme", "<init>", "(Lorg/cobalt/api/ui/theme/Theme;)V", "", "render", "()V", "", "button", "", "mouseClicked", "(I)Z", "handleSelection", "Lorg/cobalt/api/ui/theme/Theme;", "getTheme", "()Lorg/cobalt/api/ui/theme/Theme;", "cobalt"})
    private static class UIThemeEntry
    extends UIComponent {
        @NotNull
        private final Theme theme;

        public UIThemeEntry(@NotNull Theme theme) {
            Intrinsics.checkNotNullParameter((Object)theme, (String)"theme");
            super(0.0f, 0.0f, 270.0f, 100.0f);
            this.theme = theme;
        }

        @NotNull
        protected final Theme getTheme() {
            return this.theme;
        }

        @Override
        public void render() {
            boolean hovering = ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight());
            boolean isSelected = Intrinsics.areEqual((Object)ThemeManager.INSTANCE.getCurrentTheme().getName(), (Object)this.theme.getName());
            NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.theme.getPanel(), 10.0f);
            if (isSelected) {
                NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 2.0f, this.theme.getAccent(), 10.0f);
            } else if (hovering) {
                NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.0f, this.theme.getAccent(), 10.0f);
            } else {
                NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 10.0f);
            }
            NVGRenderer.text$default(this.theme.getName(), this.getX() + 20.0f, this.getY() + 20.0f, 16.0f, this.theme.getText(), null, 32, null);
            float swatchY = this.getY() + 50.0f;
            float swatchSize = 30.0f;
            float swatchGap = 10.0f;
            NVGRenderer.rect(this.getX() + 20.0f, swatchY, swatchSize, swatchSize, this.theme.getBackground(), 5.0f);
            NVGRenderer.hollowRect(this.getX() + 20.0f, swatchY, swatchSize, swatchSize, 1.0f, this.theme.getTextSecondary(), 5.0f);
            NVGRenderer.rect(this.getX() + 20.0f + swatchSize + swatchGap, swatchY, swatchSize, swatchSize, this.theme.getAccent(), 5.0f);
            NVGRenderer.rect(this.getX() + 20.0f + (swatchSize + swatchGap) * (float)2, swatchY, swatchSize, swatchSize, this.theme.getText(), 5.0f);
        }

        @Override
        public boolean mouseClicked(int button) {
            if (ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
                if (button == 0) {
                    this.handleSelection();
                    return true;
                }
                if (button == 1 && this.theme instanceof CustomTheme) {
                    UIConfig.INSTANCE.swapBodyPanel(new UIThemeEditor((CustomTheme)this.theme));
                    return true;
                }
            }
            return false;
        }

        public void handleSelection() {
            ThemeManager.INSTANCE.setTheme(this.theme);
        }
    }
}

