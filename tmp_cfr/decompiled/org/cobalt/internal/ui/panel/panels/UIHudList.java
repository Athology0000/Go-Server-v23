/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.panel.panels;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.hud.HudModuleManager;
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
import org.cobalt.api.ui.theme.Theme;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.components.UITopbar;
import org.cobalt.internal.ui.components.settings.UIActionSetting;
import org.cobalt.internal.ui.components.settings.UICheckboxSetting;
import org.cobalt.internal.ui.components.settings.UIColorSetting;
import org.cobalt.internal.ui.components.settings.UIInfoSetting;
import org.cobalt.internal.ui.components.settings.UIKeyBindSetting;
import org.cobalt.internal.ui.components.settings.UIModeSetting;
import org.cobalt.internal.ui.components.settings.UIRangeSetting;
import org.cobalt.internal.ui.components.settings.UISliderSetting;
import org.cobalt.internal.ui.components.settings.UITextSetting;
import org.cobalt.internal.ui.panel.UIPanel;
import org.cobalt.internal.ui.screen.UIHudEditor;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.cobalt.internal.ui.util.ScrollHandler;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0000\u0018\u00002\u00020\u0001:\u0002\u001c\u001dB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u000f\u0010\u0006\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\u0006\u0010\u0003J\u001f\u0010\u000b\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u0007H\u0016\u00a2\u0006\u0004\b\u000b\u0010\fR\u0014\u0010\u000e\u001a\u00020\r8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u000e\u0010\u000fR\u001c\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00110\u00108\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0013R\u001c\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00110\u00108\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0014\u0010\u0013R\u0014\u0010\u0016\u001a\u00020\u00158\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0016\u0010\u0017R\u0014\u0010\u0019\u001a\u00020\u00188\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u001aR\u0014\u0010\u001b\u001a\u00020\u00188\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001b\u0010\u001a\u00a8\u0006\u001e"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIHudList;", "Lorg/cobalt/internal/ui/panel/UIPanel;", "<init>", "()V", "", "refreshEntries", "render", "", "horizontalAmount", "verticalAmount", "", "mouseScrolled", "(DD)Z", "Lorg/cobalt/internal/ui/components/UITopbar;", "topBar", "Lorg/cobalt/internal/ui/components/UITopbar;", "", "Lorg/cobalt/internal/ui/panel/panels/UIHudList$HudElementEntry;", "allEntries", "Ljava/util/List;", "entries", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "scrollHandler", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "Lorg/cobalt/internal/ui/panel/panels/UIHudList$ActionButton;", "editButton", "Lorg/cobalt/internal/ui/panel/panels/UIHudList$ActionButton;", "resetButton", "ActionButton", "HudElementEntry", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUIHudList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UIHudList.kt\norg/cobalt/internal/ui/panel/panels/UIHudList\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,268:1\n1586#2:269\n1661#2,3:270\n1586#2:273\n1661#2,3:274\n1924#2,3:278\n777#2:281\n873#2,2:282\n1#3:277\n*S KotlinDebug\n*F\n+ 1 UIHudList.kt\norg/cobalt/internal/ui/panel/panels/UIHudList\n*L\n25#1:269\n25#1:270,3\n51#1:273\n51#1:274,3\n107#1:278,3\n68#1:281\n68#1:282,2\n*E\n"})
public final class UIHudList
extends UIPanel {
    @NotNull
    private final UITopbar topBar = new UITopbar("HUD Modules");
    @NotNull
    private List<HudElementEntry> allEntries;
    @NotNull
    private List<HudElementEntry> entries;
    @NotNull
    private final ScrollHandler scrollHandler;
    @NotNull
    private final ActionButton editButton;
    @NotNull
    private final ActionButton resetButton;

    /*
     * WARNING - void declaration
     */
    public UIHudList() {
        super(0.0f, 0.0f, 890.0f, 600.0f);
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        Iterable iterable = HudModuleManager.INSTANCE.getElements();
        UIHudList uIHudList = this;
        boolean $i$f$map = false;
        void var3_4 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            HudElement hudElement = (HudElement)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new HudElementEntry((HudElement)it));
        }
        uIHudList.allEntries = (List)destination$iv$iv;
        this.entries = this.allEntries;
        this.scrollHandler = new ScrollHandler(0.0f, 1, null);
        this.editButton = new ActionButton("Edit HUD", 170.0f, 40.0f, (Function0<Integer>)((Function0)UIHudList::editButton$lambda$0), (Function0<Integer>)((Function0)UIHudList::editButton$lambda$1), (Function0<Unit>)((Function0)UIHudList::editButton$lambda$2));
        this.resetButton = new ActionButton("Reset All", 140.0f, 40.0f, (Function0<Integer>)((Function0)UIHudList::resetButton$lambda$0), (Function0<Integer>)((Function0)UIHudList::resetButton$lambda$1), (Function0<Unit>)((Function0)UIHudList::resetButton$lambda$2));
        this.getComponents().addAll((Collection<UIComponent>)this.allEntries);
        this.getComponents().add(this.editButton);
        this.getComponents().add(this.resetButton);
        this.getComponents().add(this.topBar);
        this.topBar.searchChanged((Function1<? super String, Unit>)((Function1)arg_0 -> UIHudList._init_$lambda$0(this, arg_0)));
    }

    /*
     * WARNING - void declaration
     */
    public final void refreshEntries() {
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        Iterable iterable = HudModuleManager.INSTANCE.getElements();
        UIHudList uIHudList = this;
        boolean $i$f$map = false;
        void var3_4 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            HudElement hudElement = (HudElement)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new HudElementEntry((HudElement)it));
        }
        uIHudList.allEntries = (List)destination$iv$iv;
        this.entries = this.allEntries;
        CollectionsKt.removeAll(this.getComponents(), UIHudList::refreshEntries$lambda$1);
        this.getComponents().addAll(0, (Collection<UIComponent>)this.allEntries);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void render() {
        float f;
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ThemeManager.INSTANCE.getCurrentTheme().getBackground(), 10.0f);
        this.topBar.updateBounds(this.getX(), this.getY()).render();
        float buttonsY = this.getY() + this.topBar.getHeight() + 15.0f;
        float buttonGap = 12.0f;
        float totalButtonsWidth = this.editButton.getWidth() + this.resetButton.getWidth() + buttonGap;
        float buttonsStartX = this.getX() + this.getWidth() / 2.0f - totalButtonsWidth / 2.0f;
        this.editButton.updateBounds(buttonsStartX, buttonsY).render();
        this.resetButton.updateBounds(buttonsStartX + this.editButton.getWidth() + buttonGap, buttonsY).render();
        float listStartY = buttonsY + this.editButton.getHeight() + 20.0f;
        float visibleHeight = this.getHeight() - (listStartY - this.getY());
        float entryGap = 10.0f;
        if (this.entries.isEmpty()) {
            f = 0.0f;
        } else {
            Iterable iterable = this.entries;
            double d = 0.0;
            for (Object object : iterable) {
                void it;
                HudElementEntry hudElementEntry = (HudElementEntry)object;
                double d2 = d;
                boolean bl = false;
                double d3 = it.getHeight();
                d = d2 + d3;
            }
            f = (float)d + (float)(this.entries.size() - 1) * entryGap;
        }
        float contentHeight = f;
        this.scrollHandler.setMaxScroll(contentHeight + 20.0f, visibleHeight);
        NVGRenderer.pushScissor(this.getX(), listStartY, this.getWidth(), visibleHeight);
        float scrollOffset = this.scrollHandler.getOffset();
        Iterable $this$forEachIndexed$iv = this.entries;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            void entry;
            int n;
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            HudElementEntry hudElementEntry = (HudElementEntry)item$iv;
            int index = n;
            boolean bl = false;
            float entryY = listStartY + 10.0f + (float)index * (entry.getHeight() + entryGap) - scrollOffset;
            entry.updateBounds(this.getX(), entryY);
            entry.render();
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

    private static final int editButton$lambda$0() {
        return ThemeManager.INSTANCE.getCurrentTheme().getAccent();
    }

    private static final int editButton$lambda$1() {
        return ThemeManager.INSTANCE.getCurrentTheme().getTextOnAccent();
    }

    private static final Unit editButton$lambda$2() {
        new UIHudEditor().openUI();
        return Unit.INSTANCE;
    }

    private static final int resetButton$lambda$0() {
        return ThemeManager.INSTANCE.getCurrentTheme().getControlBg();
    }

    private static final int resetButton$lambda$1() {
        return ThemeManager.INSTANCE.getCurrentTheme().getText();
    }

    private static final Unit resetButton$lambda$2() {
        HudModuleManager.INSTANCE.resetAllPositions();
        return Unit.INSTANCE;
    }

    private static final boolean refreshEntries$lambda$1(UIComponent it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it instanceof HudElementEntry;
    }

    /*
     * Unable to fully structure code
     */
    private static final Unit _init_$lambda$0(UIHudList this$0, String searchText) {
        block4: {
            block3: {
                Intrinsics.checkNotNullParameter((Object)searchText, (String)"searchText");
                v0 = this$0;
                if (!(((CharSequence)searchText).length() == 0)) break block3;
                v1 = this$0.allEntries;
                break block4;
            }
            v2 = searchText.toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue((Object)v2, (String)"toLowerCase(...)");
            searchLower = v2;
            var3_3 = this$0.allEntries;
            var12_4 = v0;
            $i$f$filter = false;
            var5_6 = $this$filter$iv;
            destination$iv$iv = new ArrayList<E>();
            $i$f$filterTo = false;
            for (T element$iv$iv : $this$filterTo$iv$iv) {
                it = (HudElementEntry)element$iv$iv;
                $i$a$-filter-UIHudList$1$1 = false;
                v3 = it.getModule().getName().toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)v3, (String)"toLowerCase(...)");
                if (StringsKt.contains$default((CharSequence)v3, (CharSequence)searchLower, (boolean)false, (int)2, null)) ** GOTO lbl-1000
                v4 = it.getModule().getDescription().toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)v4, (String)"toLowerCase(...)");
                if (StringsKt.contains$default((CharSequence)v4, (CharSequence)searchLower, (boolean)false, (int)2, null)) lbl-1000:
                // 2 sources

                {
                    v5 = true;
                } else {
                    v5 = false;
                }
                if (!v5) continue;
                destination$iv$iv.add(element$iv$iv);
            }
            v1 = (List)destination$iv$iv;
            v0 = var12_4;
        }
        v0.entries = v1;
        return Unit.INSTANCE;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0002\u0018\u00002\u00020\u0001BI\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0007\u00a2\u0006\u0004\b\r\u0010\u000eJ\u000f\u0010\u000f\u001a\u00020\u000bH\u0016\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0017\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0011\u001a\u00020\bH\u0016\u00a2\u0006\u0004\b\u0013\u0010\u0014R\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u0015R\u001a\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\t\u0010\u0016R\u001a\u0010\n\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\n\u0010\u0016R\u001a\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\f\u0010\u0016\u00a8\u0006\u0017"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIHudList$ActionButton;", "Lorg/cobalt/internal/ui/UIComponent;", "", "label", "", "width", "height", "Lkotlin/Function0;", "", "background", "textColor", "", "onClick", "<init>", "(Ljava/lang/String;FFLkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;)V", "render", "()V", "button", "", "mouseClicked", "(I)Z", "Ljava/lang/String;", "Lkotlin/jvm/functions/Function0;", "cobalt"})
    private static final class ActionButton
    extends UIComponent {
        @NotNull
        private final String label;
        @NotNull
        private final Function0<Integer> background;
        @NotNull
        private final Function0<Integer> textColor;
        @NotNull
        private final Function0<Unit> onClick;

        public ActionButton(@NotNull String label, float width, float height, @NotNull Function0<Integer> background, @NotNull Function0<Integer> textColor, @NotNull Function0<Unit> onClick) {
            Intrinsics.checkNotNullParameter((Object)label, (String)"label");
            Intrinsics.checkNotNullParameter(background, (String)"background");
            Intrinsics.checkNotNullParameter(textColor, (String)"textColor");
            Intrinsics.checkNotNullParameter(onClick, (String)"onClick");
            super(0.0f, 0.0f, width, height);
            this.label = label;
            this.background = background;
            this.textColor = textColor;
            this.onClick = onClick;
        }

        @Override
        public void render() {
            boolean hovering = ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight());
            int baseColor = ((Number)this.background.invoke()).intValue();
            int color = hovering ? new Color(baseColor).darker().getRGB() : baseColor;
            NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), color, 8.0f);
            NVGRenderer.text$default(this.label, this.getX() + this.getWidth() / 2.0f - NVGRenderer.textWidth$default(this.label, 14.0f, null, 4, null) / 2.0f, this.getY() + this.getHeight() / 2.0f - 7.0f, 14.0f, ((Number)this.textColor.invoke()).intValue(), null, 32, null);
        }

        @Override
        public boolean mouseClicked(int button) {
            if (button == 0 && ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
                this.onClick.invoke();
                return true;
            }
            return false;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0010 \n\u0002\b\u0003\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000f\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0017\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\fH\u0016\u00a2\u0006\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0011\u001a\u0004\b\u0012\u0010\u0013R\u0016\u0010\u0014\u001a\u00020\u000e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0014\u0010\u0015R\u0014\u0010\u0016\u001a\u00020\u00068\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\u0016\u0010\u0017R\u001a\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00010\u00188\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u001a\u00a8\u0006\u001b"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIHudList$HudElementEntry;", "Lorg/cobalt/internal/ui/UIComponent;", "Lorg/cobalt/api/hud/HudElement;", "module", "<init>", "(Lorg/cobalt/api/hud/HudElement;)V", "", "computeHeight", "()F", "", "render", "()V", "", "button", "", "mouseClicked", "(I)Z", "Lorg/cobalt/api/hud/HudElement;", "getModule", "()Lorg/cobalt/api/hud/HudElement;", "expanded", "Z", "baseHeight", "F", "", "settings", "Ljava/util/List;", "cobalt"})
    @SourceDebugExtension(value={"SMAP\nUIHudList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UIHudList.kt\norg/cobalt/internal/ui/panel/panels/UIHudList$HudElementEntry\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,268:1\n1586#2:269\n1661#2,3:270\n1915#2,2:273\n*S KotlinDebug\n*F\n+ 1 UIHudList.kt\norg/cobalt/internal/ui/panel/panels/UIHudList$HudElementEntry\n*L\n164#1:269\n164#1:270,3\n228#1:273,2\n*E\n"})
    private static final class HudElementEntry
    extends UIComponent {
        @NotNull
        private final HudElement module;
        private boolean expanded;
        private final float baseHeight;
        @NotNull
        private final List<UIComponent> settings;

        /*
         * WARNING - void declaration
         */
        public HudElementEntry(@NotNull HudElement module) {
            void $this$mapTo$iv$iv;
            void $this$map$iv;
            Intrinsics.checkNotNullParameter((Object)module, (String)"module");
            super(0.0f, 0.0f, 890.0f, 60.0f);
            this.module = module;
            this.baseHeight = 60.0f;
            Iterable iterable = this.module.getSettings();
            HudElementEntry hudElementEntry = this;
            boolean $i$f$map = false;
            void var4_5 = $this$map$iv;
            Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
            boolean $i$f$mapTo = false;
            for (Object item$iv$iv : $this$mapTo$iv$iv) {
                UIComponent uIComponent;
                void it;
                Setting setting = (Setting)item$iv$iv;
                Collection collection = destination$iv$iv;
                boolean bl = false;
                void var11_12 = it;
                if (var11_12 instanceof ActionSetting) {
                    uIComponent = new UIActionSetting((ActionSetting)it);
                } else if (var11_12 instanceof CheckboxSetting) {
                    uIComponent = new UICheckboxSetting((CheckboxSetting)it);
                } else if (var11_12 instanceof ColorSetting) {
                    uIComponent = new UIColorSetting((ColorSetting)it);
                } else if (var11_12 instanceof InfoSetting) {
                    uIComponent = new UIInfoSetting((InfoSetting)it);
                } else if (var11_12 instanceof KeyBindSetting) {
                    uIComponent = new UIKeyBindSetting((KeyBindSetting)it);
                } else if (var11_12 instanceof ModeSetting) {
                    uIComponent = new UIModeSetting((ModeSetting)it);
                } else if (var11_12 instanceof RangeSetting) {
                    uIComponent = new UIRangeSetting((RangeSetting)it);
                } else if (var11_12 instanceof SliderSetting) {
                    uIComponent = new UISliderSetting((SliderSetting)it);
                } else {
                    Intrinsics.checkNotNull((Object)it, (String)"null cannot be cast to non-null type org.cobalt.api.module.setting.impl.TextSetting");
                    uIComponent = new UITextSetting((TextSetting)it);
                }
                collection.add(uIComponent);
            }
            hudElementEntry.settings = (List)destination$iv$iv;
        }

        @NotNull
        public final HudElement getModule() {
            return this.module;
        }

        private final float computeHeight() {
            return this.expanded && !((Collection)this.settings).isEmpty() ? this.baseHeight + (float)this.settings.size() * 70.0f : this.baseHeight;
        }

        @Override
        public void render() {
            this.setHeight(this.computeHeight());
            Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
            NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), theme.getControlBg(), 8.0f);
            NVGRenderer.text$default(this.module.getName(), this.getX() + 20.0f, this.getY() + 18.0f, 14.0f, theme.getText(), null, 32, null);
            NVGRenderer.text$default(this.module.getDescription(), this.getX() + 20.0f, this.getY() + 36.0f, 12.0f, theme.getTextSecondary(), null, 32, null);
            float toggleWidth = 40.0f;
            float toggleHeight = 22.0f;
            float toggleX = this.getX() + this.getWidth() - 20.0f - toggleWidth;
            float toggleY = this.getY() + this.getHeight() / 2.0f - toggleHeight / 2.0f;
            int toggleColor = this.module.getEnabled() ? theme.getAccent() : theme.getControlBg();
            NVGRenderer.rect(toggleX, toggleY, toggleWidth, toggleHeight, toggleColor, toggleHeight / 2.0f);
            float knobRadius = 9.0f;
            float knobX = this.module.getEnabled() ? toggleX + toggleWidth - 11.0f : toggleX + 11.0f;
            float knobY = toggleY + toggleHeight / 2.0f;
            NVGRenderer.circle(knobX, knobY, knobRadius, theme.getTextOnAccent());
            NVGRenderer.line(this.getX(), this.getY() + this.getHeight(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 1.0f, theme.getModuleDivider());
            if (this.expanded && !((Collection)this.settings).isEmpty()) {
                float settingY = 0.0f;
                settingY = this.getY() + this.baseHeight + 10.0f;
                Iterable $this$forEach$iv = this.settings;
                boolean $i$f$forEach = false;
                for (Object element$iv : $this$forEach$iv) {
                    UIComponent setting = (UIComponent)element$iv;
                    boolean bl = false;
                    setting.updateBounds(this.getX() + 20.0f, settingY);
                    setting.setWidth(this.getWidth() - 40.0f);
                    setting.setHeight(60.0f);
                    setting.render();
                    settingY += 70.0f;
                }
            }
        }

        @Override
        public boolean mouseClicked(int button) {
            if (button == 0) {
                float toggleY;
                float toggleWidth = 40.0f;
                float toggleHeight = 22.0f;
                float toggleX = this.getX() + this.getWidth() - 20.0f - toggleWidth;
                if (ExtensionsKt.isHoveringOver(toggleX, toggleY = this.getY() + this.baseHeight / 2.0f - toggleHeight / 2.0f, toggleWidth, toggleHeight)) {
                    this.module.setEnabled(!this.module.getEnabled());
                    return true;
                }
                if (ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.baseHeight)) {
                    this.expanded = !this.expanded;
                    return true;
                }
            }
            if (this.expanded && button == 0) {
                for (UIComponent setting : this.settings) {
                    if (!setting.mouseClicked(button)) continue;
                    return true;
                }
            }
            return false;
        }
    }
}

