/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.ui.panel.panels;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_310;
import org.cobalt.api.module.setting.impl.ColorSetting;
import org.cobalt.api.module.setting.impl.InfoSetting;
import org.cobalt.api.module.setting.impl.TextSetting;
import org.cobalt.api.notification.NotificationManager;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.ui.theme.ThemePalette;
import org.cobalt.api.ui.theme.impl.CustomTheme;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.components.UIBackButton;
import org.cobalt.internal.ui.components.UITopbar;
import org.cobalt.internal.ui.components.settings.UIColorSetting;
import org.cobalt.internal.ui.components.settings.UIInfoSetting;
import org.cobalt.internal.ui.components.settings.UITextSetting;
import org.cobalt.internal.ui.panel.UIPanel;
import org.cobalt.internal.ui.panel.panels.UIThemeSelector;
import org.cobalt.internal.ui.screen.UIConfig;
import org.cobalt.internal.ui.theme.ThemeSerializer;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.cobalt.internal.ui.util.GridLayout;
import org.cobalt.internal.ui.util.ScrollHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00a0\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0000\u0018\u00002\u00020\u0001:\u0004>?@AB\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000f\u0010\u0007\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\u0007\u0010\bJ\u001f\u0010\r\u001a\u00020\f2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\r\u0010\u000eJ\u000f\u0010\u000f\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u000f\u0010\bJ\u000f\u0010\u0010\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u0010\u0010\bR\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u0011R\u0014\u0010\u0013\u001a\u00020\u00128\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0013\u0010\u0014R\u0014\u0010\u0016\u001a\u00020\u00158\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0016\u0010\u0017R\u0014\u0010\u0019\u001a\u00020\u00188\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u001aR\u0014\u0010\u001c\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u001dR\u0014\u0010\u001f\u001a\u00020\u001e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001f\u0010 R\u001a\u0010#\u001a\b\u0012\u0004\u0012\u00020\"0!8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b#\u0010$R\u001a\u0010&\u001a\b\u0012\u0004\u0012\u00020%0!8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b&\u0010$R\u0014\u0010(\u001a\u00020'8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b(\u0010)R\u0014\u0010+\u001a\u00020*8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b+\u0010,R\u0014\u0010.\u001a\u00020-8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b.\u0010/R&\u00103\u001a\u0014\u0012\u0004\u0012\u000201\u0012\n\u0012\b\u0012\u0004\u0012\u0002020!008\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b3\u00104R\u001a\u00105\u001a\b\u0012\u0004\u0012\u0002020!8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b5\u0010$R\u001a\u00107\u001a\b\u0012\u0004\u0012\u0002060!8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b7\u0010$R\u0014\u00109\u001a\u0002088\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b9\u0010:R\u0014\u0010<\u001a\u00020;8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b<\u0010=\u00a8\u0006B"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIThemeEditor;", "Lorg/cobalt/internal/ui/panel/UIPanel;", "Lorg/cobalt/api/ui/theme/impl/CustomTheme;", "theme", "<init>", "(Lorg/cobalt/api/ui/theme/impl/CustomTheme;)V", "", "render", "()V", "", "horizontalAmount", "verticalAmount", "", "mouseScrolled", "(DD)Z", "applyPalette", "syncTheme", "Lorg/cobalt/api/ui/theme/impl/CustomTheme;", "Lorg/cobalt/internal/ui/components/UITopbar;", "topBar", "Lorg/cobalt/internal/ui/components/UITopbar;", "Lorg/cobalt/internal/ui/components/UIBackButton;", "backButton", "Lorg/cobalt/internal/ui/components/UIBackButton;", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "nameSetting", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "Lorg/cobalt/internal/ui/components/settings/UITextSetting;", "nameEditor", "Lorg/cobalt/internal/ui/components/settings/UITextSetting;", "Lorg/cobalt/api/ui/theme/ThemePalette;", "palette", "Lorg/cobalt/api/ui/theme/ThemePalette;", "", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "paletteSettings", "Ljava/util/List;", "Lorg/cobalt/internal/ui/components/settings/UIColorSetting;", "paletteEditors", "Lorg/cobalt/internal/ui/panel/panels/UIThemeEditor$UIGenerateButton;", "generateButton", "Lorg/cobalt/internal/ui/panel/panels/UIThemeEditor$UIGenerateButton;", "Lorg/cobalt/internal/ui/panel/panels/UIThemeEditor$UICopyButton;", "copyButton", "Lorg/cobalt/internal/ui/panel/panels/UIThemeEditor$UICopyButton;", "Lorg/cobalt/internal/ui/panel/panels/UIThemeEditor$UIDeleteButton;", "deleteButton", "Lorg/cobalt/internal/ui/panel/panels/UIThemeEditor$UIDeleteButton;", "", "", "Lorg/cobalt/internal/ui/panel/panels/UIThemeEditor$ThemeColorField;", "colorGroups", "Ljava/util/Map;", "allColorFields", "Lorg/cobalt/internal/ui/UIComponent;", "colorEditors", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "scrollHandler", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "Lorg/cobalt/internal/ui/util/GridLayout;", "layout", "Lorg/cobalt/internal/ui/util/GridLayout;", "ThemeColorField", "UIGenerateButton", "UICopyButton", "UIDeleteButton", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUIThemeEditor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UIThemeEditor.kt\norg/cobalt/internal/ui/panel/panels/UIThemeEditor\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,418:1\n1586#2:419\n1661#2,3:420\n1586#2:426\n1661#2,3:427\n1915#2,2:433\n1915#2,2:435\n812#2,12:437\n1915#2,2:449\n1915#2,2:451\n1915#2,2:453\n78#3:423\n99#3,2:424\n101#3,3:430\n*S KotlinDebug\n*F\n+ 1 UIThemeEditor.kt\norg/cobalt/internal/ui/panel/panels/UIThemeEditor\n*L\n52#1:419\n52#1:420,3\n143#1:426\n143#1:427,3\n194#1:433,2\n198#1:435,2\n199#1:437,12\n199#1:449,2\n223#1:451,2\n284#1:453,2\n142#1:423\n142#1:424,2\n142#1:430,3\n*E\n"})
public final class UIThemeEditor
extends UIPanel {
    @NotNull
    private final CustomTheme theme;
    @NotNull
    private final UITopbar topBar;
    @NotNull
    private final UIBackButton backButton;
    @NotNull
    private final TextSetting nameSetting;
    @NotNull
    private final UITextSetting nameEditor;
    @NotNull
    private final ThemePalette palette;
    @NotNull
    private final List<ColorSetting> paletteSettings;
    @NotNull
    private final List<UIColorSetting> paletteEditors;
    @NotNull
    private final UIGenerateButton generateButton;
    @NotNull
    private final UICopyButton copyButton;
    @NotNull
    private final UIDeleteButton deleteButton;
    @NotNull
    private final Map<String, List<ThemeColorField>> colorGroups;
    @NotNull
    private final List<ThemeColorField> allColorFields;
    @NotNull
    private final List<UIComponent> colorEditors;
    @NotNull
    private final ScrollHandler scrollHandler;
    @NotNull
    private final GridLayout layout;

    /*
     * WARNING - void declaration
     */
    public UIThemeEditor(@NotNull CustomTheme theme) {
        void $this$flatMapTo$iv$iv;
        void $this$flatMap$iv;
        Map.Entry it;
        void $this$mapTo$iv$iv;
        Object $this$map$iv;
        Intrinsics.checkNotNullParameter((Object)theme, (String)"theme");
        super(0.0f, 0.0f, 890.0f, 600.0f);
        this.theme = theme;
        this.topBar = new UITopbar("Theme Editor");
        this.backButton = new UIBackButton((Function0<Unit>)((Function0)UIThemeEditor::backButton$lambda$0));
        this.nameSetting = new TextSetting("Name", "Theme name", this.theme.getName());
        this.nameEditor = new UITextSetting(this.nameSetting);
        this.palette = new ThemePalette(0, 0, 0, 0, 0, 0, 63, null);
        Object object = new ColorSetting[]{new ColorSetting("Primary", "Main accent color", this.palette.getPrimary()), new ColorSetting("Background", "Main background", this.palette.getBackground()), new ColorSetting("Surface", "Panels and cards", this.palette.getSurface()), new ColorSetting("Error", "Error states", this.palette.getError()), new ColorSetting("Text", "Main text color", this.palette.getText()), new ColorSetting("Text Secondary", "Secondary text", this.palette.getTextSecondary())};
        this.paletteSettings = CollectionsKt.listOf((Object[])object);
        object = this.paletteSettings;
        UIThemeEditor uIThemeEditor = this;
        boolean $i$f$map22 = false;
        void var4_7 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            ColorSetting colorSetting = (ColorSetting)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new UIColorSetting((ColorSetting)((Object)it)));
        }
        uIThemeEditor.paletteEditors = (List)destination$iv$iv;
        this.generateButton = new UIGenerateButton((Function0<Unit>)((Function0)() -> UIThemeEditor.generateButton$lambda$0(this)));
        this.copyButton = new UICopyButton((Function0<CustomTheme>)((Function0)() -> UIThemeEditor.copyButton$lambda$0(this)));
        this.deleteButton = new UIDeleteButton((Function0<CustomTheme>)((Function0)() -> UIThemeEditor.deleteButton$lambda$0(this)), (Function0<Unit>)((Function0)UIThemeEditor::deleteButton$lambda$1));
        $this$map$iv = new Pair[7];
        Object[] $i$f$map22 = new ThemeColorField[]{new ThemeColorField("Background", new ColorSetting("Background", "", this.theme.getBackground()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$0(this, arg_0))), new ThemeColorField("Panel", new ColorSetting("Panel", "", this.theme.getPanel()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$1(this, arg_0))), new ThemeColorField("Inset", new ColorSetting("Inset", "", this.theme.getInset()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$2(this, arg_0))), new ThemeColorField("Overlay", new ColorSetting("Overlay", "", this.theme.getOverlay()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$3(this, arg_0))), new ThemeColorField("Module Divider", new ColorSetting("Module Divider", "", this.theme.getModuleDivider()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$4(this, arg_0))), new ThemeColorField("Selected Overlay", new ColorSetting("Selected Overlay", "", this.theme.getSelectedOverlay()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$5(this, arg_0))), new ThemeColorField("Transparent", new ColorSetting("Transparent", "", this.theme.getTransparent()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$6(this, arg_0))), new ThemeColorField("White", new ColorSetting("White", "", this.theme.getWhite()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$7(this, arg_0))), new ThemeColorField("Black", new ColorSetting("Black", "", this.theme.getBlack()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$8(this, arg_0)))};
        $this$map$iv[0] = TuplesKt.to((Object)"Base Colors", (Object)CollectionsKt.listOf((Object[])$i$f$map22));
        $i$f$map22 = new ThemeColorField[]{new ThemeColorField("Text", new ColorSetting("Text", "", this.theme.getText()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$9(this, arg_0))), new ThemeColorField("Text Primary", new ColorSetting("Text Primary", "", this.theme.getTextPrimary()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$10(this, arg_0))), new ThemeColorField("Text Secondary", new ColorSetting("Text Secondary", "", this.theme.getTextSecondary()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$11(this, arg_0))), new ThemeColorField("Text Disabled", new ColorSetting("Text Disabled", "", this.theme.getTextDisabled()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$12(this, arg_0))), new ThemeColorField("Text Placeholder", new ColorSetting("Text Placeholder", "", this.theme.getTextPlaceholder()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$13(this, arg_0))), new ThemeColorField("Text On Accent", new ColorSetting("Text On Accent", "", this.theme.getTextOnAccent()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$14(this, arg_0))), new ThemeColorField("Selection Text", new ColorSetting("Selection Text", "", this.theme.getSelectionText()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$15(this, arg_0))), new ThemeColorField("Search Placeholder Text", new ColorSetting("Search Placeholder Text", "", this.theme.getSearchPlaceholderText()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$16(this, arg_0)))};
        $this$map$iv[1] = TuplesKt.to((Object)"Text", (Object)CollectionsKt.listOf((Object[])$i$f$map22));
        $i$f$map22 = new ThemeColorField[]{new ThemeColorField("Accent", new ColorSetting("Accent", "", this.theme.getAccent()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$17(this, arg_0))), new ThemeColorField("Accent Primary", new ColorSetting("Accent Primary", "", this.theme.getAccentPrimary()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$18(this, arg_0))), new ThemeColorField("Accent Secondary", new ColorSetting("Accent Secondary", "", this.theme.getAccentSecondary()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$19(this, arg_0))), new ThemeColorField("Selection", new ColorSetting("Selection", "", this.theme.getSelection()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$20(this, arg_0)))};
        $this$map$iv[2] = TuplesKt.to((Object)"Accent", (Object)CollectionsKt.listOf((Object[])$i$f$map22));
        $i$f$map22 = new ThemeColorField[]{new ThemeColorField("Control Background", new ColorSetting("Control Background", "", this.theme.getControlBg()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$21(this, arg_0))), new ThemeColorField("Control Border", new ColorSetting("Control Border", "", this.theme.getControlBorder()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$22(this, arg_0))), new ThemeColorField("Input Background", new ColorSetting("Input Background", "", this.theme.getInputBg()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$23(this, arg_0))), new ThemeColorField("Input Border", new ColorSetting("Input Border", "", this.theme.getInputBorder()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$24(this, arg_0))), new ThemeColorField("Scrollbar Thumb", new ColorSetting("Scrollbar Thumb", "", this.theme.getScrollbarThumb()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$25(this, arg_0))), new ThemeColorField("Scrollbar Track", new ColorSetting("Scrollbar Track", "", this.theme.getScrollbarTrack()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$26(this, arg_0))), new ThemeColorField("Slider Track", new ColorSetting("Slider Track", "", this.theme.getSliderTrack()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$27(this, arg_0))), new ThemeColorField("Slider Fill", new ColorSetting("Slider Fill", "", this.theme.getSliderFill()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$28(this, arg_0))), new ThemeColorField("Slider Thumb", new ColorSetting("Slider Thumb", "", this.theme.getSliderThumb()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$29(this, arg_0)))};
        $this$map$iv[3] = TuplesKt.to((Object)"Controls", (Object)CollectionsKt.listOf((Object[])$i$f$map22));
        $i$f$map22 = new ThemeColorField[]{new ThemeColorField("Success", new ColorSetting("Success", "", this.theme.getSuccess()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$30(this, arg_0))), new ThemeColorField("Warning", new ColorSetting("Warning", "", this.theme.getWarning()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$31(this, arg_0))), new ThemeColorField("Error", new ColorSetting("Error", "", this.theme.getError()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$32(this, arg_0))), new ThemeColorField("Info", new ColorSetting("Info", "", this.theme.getInfo()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$33(this, arg_0)))};
        $this$map$iv[4] = TuplesKt.to((Object)"Status", (Object)CollectionsKt.listOf((Object[])$i$f$map22));
        $i$f$map22 = new ThemeColorField[]{new ThemeColorField("Success Background", new ColorSetting("Success Background", "", this.theme.getSuccessBackground()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$34(this, arg_0))), new ThemeColorField("Success Border", new ColorSetting("Success Border", "", this.theme.getSuccessBorder()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$35(this, arg_0))), new ThemeColorField("Success Icon", new ColorSetting("Success Icon", "", this.theme.getSuccessIcon()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$36(this, arg_0))), new ThemeColorField("Warning Background", new ColorSetting("Warning Background", "", this.theme.getWarningBackground()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$37(this, arg_0))), new ThemeColorField("Warning Border", new ColorSetting("Warning Border", "", this.theme.getWarningBorder()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$38(this, arg_0))), new ThemeColorField("Warning Icon", new ColorSetting("Warning Icon", "", this.theme.getWarningIcon()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$39(this, arg_0))), new ThemeColorField("Error Background", new ColorSetting("Error Background", "", this.theme.getErrorBackground()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$40(this, arg_0))), new ThemeColorField("Error Border", new ColorSetting("Error Border", "", this.theme.getErrorBorder()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$41(this, arg_0))), new ThemeColorField("Error Icon", new ColorSetting("Error Icon", "", this.theme.getErrorIcon()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$42(this, arg_0))), new ThemeColorField("Info Background", new ColorSetting("Info Background", "", this.theme.getInfoBackground()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$43(this, arg_0))), new ThemeColorField("Info Border", new ColorSetting("Info Border", "", this.theme.getInfoBorder()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$44(this, arg_0))), new ThemeColorField("Info Icon", new ColorSetting("Info Icon", "", this.theme.getInfoIcon()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$45(this, arg_0)))};
        $this$map$iv[5] = TuplesKt.to((Object)"Status Backgrounds", (Object)CollectionsKt.listOf((Object[])$i$f$map22));
        $i$f$map22 = new ThemeColorField[]{new ThemeColorField("Tooltip Background", new ColorSetting("Tooltip Background", "", this.theme.getTooltipBackground()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$46(this, arg_0))), new ThemeColorField("Tooltip Border", new ColorSetting("Tooltip Border", "", this.theme.getTooltipBorder()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$47(this, arg_0))), new ThemeColorField("Tooltip Text", new ColorSetting("Tooltip Text", "", this.theme.getTooltipText()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$48(this, arg_0))), new ThemeColorField("Notification Background", new ColorSetting("Notification Background", "", this.theme.getNotificationBackground()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$49(this, arg_0))), new ThemeColorField("Notification Border", new ColorSetting("Notification Border", "", this.theme.getNotificationBorder()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$50(this, arg_0))), new ThemeColorField("Notification Text", new ColorSetting("Notification Text", "", this.theme.getNotificationText()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$51(this, arg_0))), new ThemeColorField("Notification Text Secondary", new ColorSetting("Notification Text Secondary", "", this.theme.getNotificationTextSecondary()), (Function1<? super Integer, Unit>)((Function1)arg_0 -> UIThemeEditor.colorGroups$lambda$52(this, arg_0)))};
        $this$map$iv[6] = TuplesKt.to((Object)"UI Elements", (Object)CollectionsKt.listOf((Object[])$i$f$map22));
        this.colorGroups = MapsKt.mapOf((Pair[])$this$map$iv);
        this.allColorFields = CollectionsKt.flatten((Iterable)this.colorGroups.values());
        $this$map$iv = this.colorGroups;
        uIThemeEditor = this;
        boolean $i$f$flatMap = false;
        $this$mapTo$iv$iv = $this$flatMap$iv;
        destination$iv$iv = new ArrayList();
        boolean $i$f$flatMapTo = false;
        Iterator<Object> iterator = $this$flatMapTo$iv$iv.entrySet().iterator();
        while (iterator.hasNext()) {
            void $this$mapTo$iv$iv2;
            void $this$map$iv2;
            Map.Entry element$iv$iv;
            it = element$iv$iv = (Map.Entry)iterator.next();
            boolean bl = false;
            String groupName = (String)it.getKey();
            List fields = (List)it.getValue();
            Iterable iterable = fields;
            Collection collection = CollectionsKt.listOf((Object)new UIInfoSetting(new InfoSetting(groupName, "", null, 4, null)));
            boolean $i$f$map = false;
            void var16_20 = $this$map$iv2;
            Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv2, (int)10));
            boolean $i$f$mapTo2 = false;
            for (Object item$iv$iv : $this$mapTo$iv$iv2) {
                void it2;
                ThemeColorField themeColorField = (ThemeColorField)item$iv$iv;
                Collection collection2 = destination$iv$iv2;
                boolean bl2 = false;
                collection2.add(new UIColorSetting(it2.getSetting()));
            }
            Iterable list$iv$iv = CollectionsKt.plus((Collection)collection, (Iterable)((List)destination$iv$iv2));
            CollectionsKt.addAll((Collection)destination$iv$iv, (Iterable)list$iv$iv);
        }
        uIThemeEditor.colorEditors = (List)destination$iv$iv;
        this.scrollHandler = new ScrollHandler(0.0f, 1, null);
        this.layout = new GridLayout(1, 760.0f, 60.0f, 10.0f);
        this.getComponents().add(this.backButton);
        this.getComponents().add(this.topBar);
        this.getComponents().add(this.nameEditor);
        this.getComponents().add(new UIInfoSetting(new InfoSetting("Palette", "", null, 4, null)));
        this.getComponents().addAll((Collection<UIComponent>)this.paletteEditors);
        this.getComponents().add(this.generateButton);
        this.getComponents().add(this.copyButton);
        this.getComponents().add(this.deleteButton);
        this.getComponents().addAll((Collection<UIComponent>)this.colorEditors);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void render() {
        void $this$filterIsInstanceTo$iv$iv;
        int background = ThemeManager.INSTANCE.getCurrentTheme().getBackground();
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), background, 10.0f);
        this.topBar.updateBounds(this.getX(), this.getY()).render();
        this.backButton.updateBounds(this.getX() + 20.0f, this.getY() + this.topBar.getHeight() + 20.0f).render();
        float startY = this.getY() + this.topBar.getHeight() + this.backButton.getHeight() + 20.0f;
        float visibleHeight = this.getHeight() - (this.topBar.getHeight() + this.backButton.getHeight() + 20.0f);
        List list = CollectionsKt.plus((Collection)CollectionsKt.plus((Collection)CollectionsKt.plus((Collection)CollectionsKt.plus((Collection)CollectionsKt.plus((Collection)CollectionsKt.plus((Collection)CollectionsKt.listOf((Object)this.nameEditor), (Iterable)CollectionsKt.listOf((Object)new UIInfoSetting(new InfoSetting("Palette", "", null, 4, null)))), (Iterable)this.paletteEditors), (Iterable)CollectionsKt.listOf((Object)this.generateButton)), (Iterable)CollectionsKt.listOf((Object)this.copyButton)), (Iterable)CollectionsKt.listOf((Object)this.deleteButton)), (Iterable)this.colorEditors);
        this.scrollHandler.setMaxScroll(this.layout.contentHeight(list.size()) + 20.0f, visibleHeight);
        NVGRenderer.pushScissor(this.getX(), startY, this.getWidth(), visibleHeight);
        float scrollOffset = this.scrollHandler.getOffset();
        this.layout.layout(this.getX() + 20.0f, startY + 10.0f - scrollOffset, list);
        Iterable $this$forEach$iv = list;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            UIComponent p0 = (UIComponent)element$iv;
            boolean bl = false;
            p0.render();
        }
        NVGRenderer.popScissor();
        $this$forEach$iv = this.paletteEditors;
        $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            UIColorSetting it = (UIColorSetting)element$iv;
            boolean bl = false;
            it.drawColorPicker();
        }
        Iterable $this$filterIsInstance$iv = this.colorEditors;
        boolean $i$f$filterIsInstance = false;
        Iterator iterator = $this$filterIsInstance$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterIsInstanceTo = false;
        for (Object element$iv$iv : $this$filterIsInstanceTo$iv$iv) {
            if (!(element$iv$iv instanceof UIColorSetting)) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        $this$filterIsInstance$iv = (List)destination$iv$iv;
        $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            UIColorSetting it = (UIColorSetting)element$iv;
            boolean bl = false;
            it.drawColorPicker();
        }
        this.syncTheme();
    }

    @Override
    public boolean mouseScrolled(double horizontalAmount, double verticalAmount) {
        if (ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
            this.scrollHandler.handleScroll(verticalAmount);
            return true;
        }
        return false;
    }

    private final void applyPalette() {
        this.palette.setPrimary(this.paletteSettings.get(0).getValue());
        this.palette.setBackground(this.paletteSettings.get(1).getValue());
        this.palette.setSurface(this.paletteSettings.get(2).getValue());
        this.palette.setError(this.paletteSettings.get(3).getValue());
        this.palette.setText(this.paletteSettings.get(4).getValue());
        this.palette.setTextSecondary(this.paletteSettings.get(5).getValue());
        this.palette.applyTo(this.theme);
        Iterable $this$forEach$iv = this.allColorFields;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            ThemeColorField field = (ThemeColorField)element$iv;
            boolean bl = false;
            switch (field.getLabel()) {
                case "Background": {
                    field.getSetting().setValue(this.theme.getBackground());
                    break;
                }
                case "Panel": {
                    field.getSetting().setValue(this.theme.getPanel());
                    break;
                }
                case "Inset": {
                    field.getSetting().setValue(this.theme.getInset());
                    break;
                }
                case "Overlay": {
                    field.getSetting().setValue(this.theme.getOverlay());
                    break;
                }
                case "Text": {
                    field.getSetting().setValue(this.theme.getText());
                    break;
                }
                case "Text Primary": {
                    field.getSetting().setValue(this.theme.getTextPrimary());
                    break;
                }
                case "Text Secondary": {
                    field.getSetting().setValue(this.theme.getTextSecondary());
                    break;
                }
                case "Text Disabled": {
                    field.getSetting().setValue(this.theme.getTextDisabled());
                    break;
                }
                case "Text Placeholder": {
                    field.getSetting().setValue(this.theme.getTextPlaceholder());
                    break;
                }
                case "Text On Accent": {
                    field.getSetting().setValue(this.theme.getTextOnAccent());
                    break;
                }
                case "Accent": {
                    field.getSetting().setValue(this.theme.getAccent());
                    break;
                }
                case "Accent Primary": {
                    field.getSetting().setValue(this.theme.getAccentPrimary());
                    break;
                }
                case "Accent Secondary": {
                    field.getSetting().setValue(this.theme.getAccentSecondary());
                    break;
                }
                case "Selection": {
                    field.getSetting().setValue(this.theme.getSelection());
                    break;
                }
                case "Control Background": {
                    field.getSetting().setValue(this.theme.getControlBg());
                    break;
                }
                case "Control Border": {
                    field.getSetting().setValue(this.theme.getControlBorder());
                    break;
                }
                case "Input Background": {
                    field.getSetting().setValue(this.theme.getInputBg());
                    break;
                }
                case "Input Border": {
                    field.getSetting().setValue(this.theme.getInputBorder());
                    break;
                }
                case "Success": {
                    field.getSetting().setValue(this.theme.getSuccess());
                    break;
                }
                case "Warning": {
                    field.getSetting().setValue(this.theme.getWarning());
                    break;
                }
                case "Error": {
                    field.getSetting().setValue(this.theme.getError());
                    break;
                }
                case "Info": {
                    field.getSetting().setValue(this.theme.getInfo());
                    break;
                }
                case "Scrollbar Thumb": {
                    field.getSetting().setValue(this.theme.getScrollbarThumb());
                    break;
                }
                case "Scrollbar Track": {
                    field.getSetting().setValue(this.theme.getScrollbarTrack());
                    break;
                }
                case "Slider Track": {
                    field.getSetting().setValue(this.theme.getSliderTrack());
                    break;
                }
                case "Slider Fill": {
                    field.getSetting().setValue(this.theme.getSliderFill());
                    break;
                }
                case "Slider Thumb": {
                    field.getSetting().setValue(this.theme.getSliderThumb());
                    break;
                }
                case "Tooltip Background": {
                    field.getSetting().setValue(this.theme.getTooltipBackground());
                    break;
                }
                case "Tooltip Border": {
                    field.getSetting().setValue(this.theme.getTooltipBorder());
                    break;
                }
                case "Tooltip Text": {
                    field.getSetting().setValue(this.theme.getTooltipText());
                    break;
                }
                case "Notification Background": {
                    field.getSetting().setValue(this.theme.getNotificationBackground());
                    break;
                }
                case "Notification Border": {
                    field.getSetting().setValue(this.theme.getNotificationBorder());
                    break;
                }
                case "Notification Text": {
                    field.getSetting().setValue(this.theme.getNotificationText());
                    break;
                }
                case "Notification Text Secondary": {
                    field.getSetting().setValue(this.theme.getNotificationTextSecondary());
                    break;
                }
                case "Info Background": {
                    field.getSetting().setValue(this.theme.getInfoBackground());
                    break;
                }
                case "Info Border": {
                    field.getSetting().setValue(this.theme.getInfoBorder());
                    break;
                }
                case "Info Icon": {
                    field.getSetting().setValue(this.theme.getInfoIcon());
                    break;
                }
                case "Warning Background": {
                    field.getSetting().setValue(this.theme.getWarningBackground());
                    break;
                }
                case "Warning Border": {
                    field.getSetting().setValue(this.theme.getWarningBorder());
                    break;
                }
                case "Warning Icon": {
                    field.getSetting().setValue(this.theme.getWarningIcon());
                    break;
                }
                case "Success Background": {
                    field.getSetting().setValue(this.theme.getSuccessBackground());
                    break;
                }
                case "Success Border": {
                    field.getSetting().setValue(this.theme.getSuccessBorder());
                    break;
                }
                case "Success Icon": {
                    field.getSetting().setValue(this.theme.getSuccessIcon());
                    break;
                }
                case "Error Background": {
                    field.getSetting().setValue(this.theme.getErrorBackground());
                    break;
                }
                case "Error Border": {
                    field.getSetting().setValue(this.theme.getErrorBorder());
                    break;
                }
                case "Error Icon": {
                    field.getSetting().setValue(this.theme.getErrorIcon());
                    break;
                }
                case "Selection Text": {
                    field.getSetting().setValue(this.theme.getSelectionText());
                    break;
                }
                case "Search Placeholder Text": {
                    field.getSetting().setValue(this.theme.getSearchPlaceholderText());
                    break;
                }
                case "Module Divider": {
                    field.getSetting().setValue(this.theme.getModuleDivider());
                    break;
                }
                case "Selected Overlay": {
                    field.getSetting().setValue(this.theme.getSelectedOverlay());
                    break;
                }
                case "White": {
                    field.getSetting().setValue(this.theme.getWhite());
                    break;
                }
                case "Black": {
                    field.getSetting().setValue(this.theme.getBlack());
                    break;
                }
                case "Transparent": {
                    field.getSetting().setValue(this.theme.getTransparent());
                }
            }
        }
    }

    private final void syncTheme() {
        this.theme.setName((String)this.nameSetting.getValue());
        Iterable $this$forEach$iv = this.allColorFields;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            ThemeColorField field = (ThemeColorField)element$iv;
            boolean bl = false;
            field.getApply().invoke((Object)field.getSetting().getValue());
        }
        ThemeManager.INSTANCE.setTheme(this.theme);
    }

    private static final Unit backButton$lambda$0() {
        UIConfig.INSTANCE.swapBodyPanel(new UIThemeSelector());
        return Unit.INSTANCE;
    }

    private static final Unit generateButton$lambda$0(UIThemeEditor this$0) {
        this$0.applyPalette();
        return Unit.INSTANCE;
    }

    private static final CustomTheme copyButton$lambda$0(UIThemeEditor this$0) {
        return this$0.theme;
    }

    private static final CustomTheme deleteButton$lambda$0(UIThemeEditor this$0) {
        return this$0.theme;
    }

    private static final Unit deleteButton$lambda$1() {
        UIConfig.INSTANCE.swapBodyPanel(new UIThemeSelector());
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$0(UIThemeEditor this$0, int it) {
        this$0.theme.setBackground(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$1(UIThemeEditor this$0, int it) {
        this$0.theme.setPanel(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$2(UIThemeEditor this$0, int it) {
        this$0.theme.setInset(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$3(UIThemeEditor this$0, int it) {
        this$0.theme.setOverlay(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$4(UIThemeEditor this$0, int it) {
        this$0.theme.setModuleDivider(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$5(UIThemeEditor this$0, int it) {
        this$0.theme.setSelectedOverlay(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$6(UIThemeEditor this$0, int it) {
        this$0.theme.setTransparent(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$7(UIThemeEditor this$0, int it) {
        this$0.theme.setWhite(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$8(UIThemeEditor this$0, int it) {
        this$0.theme.setBlack(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$9(UIThemeEditor this$0, int it) {
        this$0.theme.setText(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$10(UIThemeEditor this$0, int it) {
        this$0.theme.setTextPrimary(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$11(UIThemeEditor this$0, int it) {
        this$0.theme.setTextSecondary(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$12(UIThemeEditor this$0, int it) {
        this$0.theme.setTextDisabled(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$13(UIThemeEditor this$0, int it) {
        this$0.theme.setTextPlaceholder(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$14(UIThemeEditor this$0, int it) {
        this$0.theme.setTextOnAccent(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$15(UIThemeEditor this$0, int it) {
        this$0.theme.setSelectionText(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$16(UIThemeEditor this$0, int it) {
        this$0.theme.setSearchPlaceholderText(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$17(UIThemeEditor this$0, int it) {
        this$0.theme.setAccent(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$18(UIThemeEditor this$0, int it) {
        this$0.theme.setAccentPrimary(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$19(UIThemeEditor this$0, int it) {
        this$0.theme.setAccentSecondary(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$20(UIThemeEditor this$0, int it) {
        this$0.theme.setSelection(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$21(UIThemeEditor this$0, int it) {
        this$0.theme.setControlBg(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$22(UIThemeEditor this$0, int it) {
        this$0.theme.setControlBorder(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$23(UIThemeEditor this$0, int it) {
        this$0.theme.setInputBg(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$24(UIThemeEditor this$0, int it) {
        this$0.theme.setInputBorder(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$25(UIThemeEditor this$0, int it) {
        this$0.theme.setScrollbarThumb(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$26(UIThemeEditor this$0, int it) {
        this$0.theme.setScrollbarTrack(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$27(UIThemeEditor this$0, int it) {
        this$0.theme.setSliderTrack(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$28(UIThemeEditor this$0, int it) {
        this$0.theme.setSliderFill(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$29(UIThemeEditor this$0, int it) {
        this$0.theme.setSliderThumb(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$30(UIThemeEditor this$0, int it) {
        this$0.theme.setSuccess(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$31(UIThemeEditor this$0, int it) {
        this$0.theme.setWarning(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$32(UIThemeEditor this$0, int it) {
        this$0.theme.setError(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$33(UIThemeEditor this$0, int it) {
        this$0.theme.setInfo(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$34(UIThemeEditor this$0, int it) {
        this$0.theme.setSuccessBackground(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$35(UIThemeEditor this$0, int it) {
        this$0.theme.setSuccessBorder(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$36(UIThemeEditor this$0, int it) {
        this$0.theme.setSuccessIcon(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$37(UIThemeEditor this$0, int it) {
        this$0.theme.setWarningBackground(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$38(UIThemeEditor this$0, int it) {
        this$0.theme.setWarningBorder(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$39(UIThemeEditor this$0, int it) {
        this$0.theme.setWarningIcon(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$40(UIThemeEditor this$0, int it) {
        this$0.theme.setErrorBackground(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$41(UIThemeEditor this$0, int it) {
        this$0.theme.setErrorBorder(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$42(UIThemeEditor this$0, int it) {
        this$0.theme.setErrorIcon(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$43(UIThemeEditor this$0, int it) {
        this$0.theme.setInfoBackground(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$44(UIThemeEditor this$0, int it) {
        this$0.theme.setInfoBorder(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$45(UIThemeEditor this$0, int it) {
        this$0.theme.setInfoIcon(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$46(UIThemeEditor this$0, int it) {
        this$0.theme.setTooltipBackground(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$47(UIThemeEditor this$0, int it) {
        this$0.theme.setTooltipBorder(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$48(UIThemeEditor this$0, int it) {
        this$0.theme.setTooltipText(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$49(UIThemeEditor this$0, int it) {
        this$0.theme.setNotificationBackground(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$50(UIThemeEditor this$0, int it) {
        this$0.theme.setNotificationBorder(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$51(UIThemeEditor this$0, int it) {
        this$0.theme.setNotificationText(it);
        return Unit.INSTANCE;
    }

    private static final Unit colorGroups$lambda$52(UIThemeEditor this$0, int it) {
        this$0.theme.setNotificationTextSecondary(it);
        return Unit.INSTANCE;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0010\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\f\b\u0082\b\u0018\u00002\u00020\u0001B+\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u0006\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u001c\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u0006H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u0011J:\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\u0014\b\u0002\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u0006H\u00c6\u0001\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u001b\u0010\u0016\u001a\u00020\u00152\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0018\u001a\u00020\u0007H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0011\u0010\u001a\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001a\u0010\rR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001b\u001a\u0004\b\u001c\u0010\rR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001d\u001a\u0004\b\u001e\u0010\u000fR#\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u00068\u0006\u00a2\u0006\f\n\u0004\b\t\u0010\u001f\u001a\u0004\b \u0010\u0011\u00a8\u0006!"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIThemeEditor$ThemeColorField;", "", "", "label", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "setting", "Lkotlin/Function1;", "", "", "apply", "<init>", "(Ljava/lang/String;Lorg/cobalt/api/module/setting/impl/ColorSetting;Lkotlin/jvm/functions/Function1;)V", "component1", "()Ljava/lang/String;", "component2", "()Lorg/cobalt/api/module/setting/impl/ColorSetting;", "component3", "()Lkotlin/jvm/functions/Function1;", "copy", "(Ljava/lang/String;Lorg/cobalt/api/module/setting/impl/ColorSetting;Lkotlin/jvm/functions/Function1;)Lorg/cobalt/internal/ui/panel/panels/UIThemeEditor$ThemeColorField;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "()I", "toString", "Ljava/lang/String;", "getLabel", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "getSetting", "Lkotlin/jvm/functions/Function1;", "getApply", "cobalt"})
    private static final class ThemeColorField {
        @NotNull
        private final String label;
        @NotNull
        private final ColorSetting setting;
        @NotNull
        private final Function1<Integer, Unit> apply;

        public ThemeColorField(@NotNull String label, @NotNull ColorSetting setting, @NotNull Function1<? super Integer, Unit> apply) {
            Intrinsics.checkNotNullParameter((Object)label, (String)"label");
            Intrinsics.checkNotNullParameter((Object)setting, (String)"setting");
            Intrinsics.checkNotNullParameter(apply, (String)"apply");
            this.label = label;
            this.setting = setting;
            this.apply = apply;
        }

        @NotNull
        public final String getLabel() {
            return this.label;
        }

        @NotNull
        public final ColorSetting getSetting() {
            return this.setting;
        }

        @NotNull
        public final Function1<Integer, Unit> getApply() {
            return this.apply;
        }

        @NotNull
        public final String component1() {
            return this.label;
        }

        @NotNull
        public final ColorSetting component2() {
            return this.setting;
        }

        @NotNull
        public final Function1<Integer, Unit> component3() {
            return this.apply;
        }

        @NotNull
        public final ThemeColorField copy(@NotNull String label, @NotNull ColorSetting setting, @NotNull Function1<? super Integer, Unit> apply) {
            Intrinsics.checkNotNullParameter((Object)label, (String)"label");
            Intrinsics.checkNotNullParameter((Object)setting, (String)"setting");
            Intrinsics.checkNotNullParameter(apply, (String)"apply");
            return new ThemeColorField(label, setting, apply);
        }

        public static /* synthetic */ ThemeColorField copy$default(ThemeColorField themeColorField, String string, ColorSetting colorSetting, Function1 function1, int n, Object object) {
            if ((n & 1) != 0) {
                string = themeColorField.label;
            }
            if ((n & 2) != 0) {
                colorSetting = themeColorField.setting;
            }
            if ((n & 4) != 0) {
                function1 = themeColorField.apply;
            }
            return themeColorField.copy(string, colorSetting, function1);
        }

        @NotNull
        public String toString() {
            return "ThemeColorField(label=" + this.label + ", setting=" + this.setting + ", apply=" + this.apply + ")";
        }

        public int hashCode() {
            int result = this.label.hashCode();
            result = result * 31 + this.setting.hashCode();
            result = result * 31 + this.apply.hashCode();
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ThemeColorField)) {
                return false;
            }
            ThemeColorField themeColorField = (ThemeColorField)other;
            if (!Intrinsics.areEqual((Object)this.label, (Object)themeColorField.label)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.setting, (Object)themeColorField.setting)) {
                return false;
            }
            return Intrinsics.areEqual(this.apply, themeColorField.apply);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0002\u0018\u00002\u00020\u0001B\u0015\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u000f\u0010\b\u001a\u00020\u0007H\u0016\u00a2\u0006\u0004\b\b\u0010\tJ\u0017\u0010\r\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\nH\u0016\u00a2\u0006\u0004\b\r\u0010\u000eR\u001a\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0004\u0010\u000f\u00a8\u0006\u0010"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIThemeEditor$UICopyButton;", "Lorg/cobalt/internal/ui/UIComponent;", "Lkotlin/Function0;", "Lorg/cobalt/api/ui/theme/impl/CustomTheme;", "getTheme", "<init>", "(Lkotlin/jvm/functions/Function0;)V", "", "render", "()V", "", "button", "", "mouseClicked", "(I)Z", "Lkotlin/jvm/functions/Function0;", "cobalt"})
    private static final class UICopyButton
    extends UIComponent {
        @NotNull
        private final Function0<CustomTheme> getTheme;

        public UICopyButton(@NotNull Function0<CustomTheme> getTheme) {
            Intrinsics.checkNotNullParameter(getTheme, (String)"getTheme");
            super(0.0f, 0.0f, 627.5f, 40.0f);
            this.getTheme = getTheme;
        }

        @Override
        public void render() {
            boolean isHovering = ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight());
            int color = isHovering ? ThemeManager.INSTANCE.getCurrentTheme().getAccent() : ThemeManager.INSTANCE.getCurrentTheme().getControlBg();
            NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), color, 5.0f);
            NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.5f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 5.0f);
            String label = "Copy Theme to Clipboard";
            NVGRenderer.text$default(label, this.getX() + this.getWidth() / (float)2 - NVGRenderer.textWidth$default(label, 14.0f, null, 4, null) / (float)2, this.getY() + this.getHeight() / (float)2 - 7.0f, 14.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
        }

        @Override
        public boolean mouseClicked(int button) {
            if (button == 0 && ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
                String encoded = ThemeSerializer.INSTANCE.toBase64((CustomTheme)this.getTheme.invoke());
                class_310.method_1551().field_1774.method_1455(encoded);
                NotificationManager.queue$default(NotificationManager.INSTANCE, "Theme Copied", "Theme copied to clipboard", 0L, 4, null);
                return true;
            }
            return false;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0003\b\u0002\u0018\u00002\u00020\u0001B#\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0002\u00a2\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\t\u001a\u00020\u0005H\u0016\u00a2\u0006\u0004\b\t\u0010\nJ\u0017\u0010\u000e\u001a\u00020\r2\u0006\u0010\f\u001a\u00020\u000bH\u0016\u00a2\u0006\u0004\b\u000e\u0010\u000fR\u001a\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0004\u0010\u0010R\u001a\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0006\u0010\u0010R\u0016\u0010\u0011\u001a\u00020\r8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0011\u0010\u0012R\u0016\u0010\u0014\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0014\u0010\u0015\u00a8\u0006\u0016"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIThemeEditor$UIDeleteButton;", "Lorg/cobalt/internal/ui/UIComponent;", "Lkotlin/Function0;", "Lorg/cobalt/api/ui/theme/impl/CustomTheme;", "getTheme", "", "onDelete", "<init>", "(Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;)V", "render", "()V", "", "button", "", "mouseClicked", "(I)Z", "Lkotlin/jvm/functions/Function0;", "confirmPending", "Z", "", "confirmTime", "J", "cobalt"})
    private static final class UIDeleteButton
    extends UIComponent {
        @NotNull
        private final Function0<CustomTheme> getTheme;
        @NotNull
        private final Function0<Unit> onDelete;
        private boolean confirmPending;
        private long confirmTime;

        public UIDeleteButton(@NotNull Function0<CustomTheme> getTheme, @NotNull Function0<Unit> onDelete) {
            Intrinsics.checkNotNullParameter(getTheme, (String)"getTheme");
            Intrinsics.checkNotNullParameter(onDelete, (String)"onDelete");
            super(0.0f, 0.0f, 627.5f, 40.0f);
            this.getTheme = getTheme;
            this.onDelete = onDelete;
        }

        @Override
        public void render() {
            boolean isHovering = ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight());
            int errorColor = ThemeManager.INSTANCE.getCurrentTheme().getError();
            int color = this.confirmPending ? errorColor : (isHovering ? new Color(errorColor).darker().getRGB() : ThemeManager.INSTANCE.getCurrentTheme().getControlBg());
            NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), color, 5.0f);
            NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.5f, errorColor, 5.0f);
            String label = this.confirmPending ? "Click again to confirm delete" : "Delete Theme";
            NVGRenderer.text$default(label, this.getX() + this.getWidth() / (float)2 - NVGRenderer.textWidth$default(label, 14.0f, null, 4, null) / (float)2, this.getY() + this.getHeight() / (float)2 - 7.0f, 14.0f, this.confirmPending ? ThemeManager.INSTANCE.getCurrentTheme().getTextOnAccent() : ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
            if (this.confirmPending && System.currentTimeMillis() - this.confirmTime > 3000L) {
                this.confirmPending = false;
            }
        }

        @Override
        public boolean mouseClicked(int button) {
            if (button == 0 && ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
                if (this.confirmPending) {
                    CustomTheme theme = (CustomTheme)this.getTheme.invoke();
                    if (ThemeManager.INSTANCE.unregisterTheme(theme)) {
                        NotificationManager.queue$default(NotificationManager.INSTANCE, "Theme Deleted", "'" + theme.getName() + "' has been deleted", 0L, 4, null);
                        this.onDelete.invoke();
                    }
                    this.confirmPending = false;
                } else {
                    this.confirmPending = true;
                    this.confirmTime = System.currentTimeMillis();
                }
                return true;
            }
            return false;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0002\u0018\u00002\u00020\u0001B\u0015\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u000f\u0010\u0007\u001a\u00020\u0003H\u0016\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\f\u0010\rR\u001a\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0004\u0010\u000e\u00a8\u0006\u000f"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIThemeEditor$UIGenerateButton;", "Lorg/cobalt/internal/ui/UIComponent;", "Lkotlin/Function0;", "", "onClick", "<init>", "(Lkotlin/jvm/functions/Function0;)V", "render", "()V", "", "button", "", "mouseClicked", "(I)Z", "Lkotlin/jvm/functions/Function0;", "cobalt"})
    private static final class UIGenerateButton
    extends UIComponent {
        @NotNull
        private final Function0<Unit> onClick;

        public UIGenerateButton(@NotNull Function0<Unit> onClick) {
            Intrinsics.checkNotNullParameter(onClick, (String)"onClick");
            super(0.0f, 0.0f, 627.5f, 40.0f);
            this.onClick = onClick;
        }

        @Override
        public void render() {
            boolean isHovering = ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight());
            int color = isHovering ? ThemeManager.INSTANCE.getCurrentTheme().getAccent() : ThemeManager.INSTANCE.getCurrentTheme().getControlBg();
            int borderColor = ThemeManager.INSTANCE.getCurrentTheme().getControlBorder();
            NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), color, 5.0f);
            NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.5f, borderColor, 5.0f);
            NVGRenderer.text$default("Generate from Palette", this.getX() + this.getWidth() / (float)2 - NVGRenderer.textWidth$default("Generate from Palette", 14.0f, null, 4, null) / (float)2, this.getY() + this.getHeight() / (float)2 - 7.0f, 14.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
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
}

