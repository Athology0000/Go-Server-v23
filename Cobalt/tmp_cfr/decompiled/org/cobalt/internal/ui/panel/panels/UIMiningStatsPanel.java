/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.ui.panel.panels;

import java.util.Arrays;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.minecraft.class_310;
import org.cobalt.api.hud.modules.CommissionMacroModule;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.RangeSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.ui.theme.Theme;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.mining.MiningMacroModule;
import org.cobalt.internal.mining.MiningModule;
import org.cobalt.internal.ui.components.settings.UICheckboxSetting;
import org.cobalt.internal.ui.panel.UIPanel;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000^\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0006\n\u0002\b\u001d\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\n\b\u0000\u0018\u0000 Q2\u00020\u0001:\u0001QB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\u0005\u0010\u0003J'\u0010\f\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\f\u0010\rJ'\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u000f\u0010\u0010J7\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0011\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\b2\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u000b\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u0015\u0010\u0016J'\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0018\u001a\u00020\u00172\u0006\u0010\u000b\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u0019\u0010\u001aJ'\u0010\u001d\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u001c\u001a\u00020\u001b2\u0006\u0010\u000b\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u001d\u0010\u001eJ\u001f\u0010 \u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u001f\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b \u0010!J\u0017\u0010#\u001a\u00020\b2\u0006\u0010\"\u001a\u00020\u0013H\u0016\u00a2\u0006\u0004\b#\u0010$J'\u0010(\u001a\u00020\b2\u0006\u0010\"\u001a\u00020\u00132\u0006\u0010&\u001a\u00020%2\u0006\u0010'\u001a\u00020%H\u0016\u00a2\u0006\u0004\b(\u0010)J\u0017\u0010*\u001a\u00020\b2\u0006\u0010\"\u001a\u00020\u0013H\u0016\u00a2\u0006\u0004\b*\u0010$J\u001f\u0010-\u001a\u00020\b2\u0006\u0010+\u001a\u00020%2\u0006\u0010,\u001a\u00020%H\u0016\u00a2\u0006\u0004\b-\u0010.J\u000f\u0010/\u001a\u00020\bH\u0002\u00a2\u0006\u0004\b/\u00100J\u0017\u00101\u001a\u00020\u00042\u0006\u0010\u0018\u001a\u00020\u0017H\u0002\u00a2\u0006\u0004\b1\u00102J\u000f\u00103\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b3\u0010\u0003J/\u00106\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020%2\u0006\u00104\u001a\u00020\n2\u0006\u00105\u001a\u00020\n2\u0006\u0010\u001c\u001a\u00020\u001bH\u0002\u00a2\u0006\u0004\b6\u00107J'\u00108\u001a\u00020%2\u0006\u00104\u001a\u00020\n2\u0006\u00105\u001a\u00020\n2\u0006\u0010\u001c\u001a\u00020\u001bH\u0002\u00a2\u0006\u0004\b8\u00109J!\u0010;\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020%2\b\b\u0002\u0010:\u001a\u00020%H\u0002\u00a2\u0006\u0004\b;\u0010<J'\u0010@\u001a\u00020\u00062\u0006\u0010=\u001a\u00020\u00062\u0006\u0010>\u001a\u00020\n2\u0006\u0010?\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b@\u0010AJ\u0017\u0010D\u001a\u00020C2\u0006\u0010B\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\bD\u0010ER&\u0010I\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020H0G0F8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bI\u0010JR&\u0010K\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00170G0F8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bK\u0010JR\u0018\u0010L\u001a\u0004\u0018\u00010\u00178\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bL\u0010MR\u0016\u0010N\u001a\u00020\b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bN\u0010OR\u0016\u0010P\u001a\u00020\b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bP\u0010O\u00a8\u0006R"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIMiningStatsPanel;", "Lorg/cobalt/internal/ui/panel/UIPanel;", "<init>", "()V", "", "render", "", "label", "", "active", "", "rowY", "renderCheckboxRow", "(Ljava/lang/String;ZF)V", "value", "renderStatRow", "(Ljava/lang/String;Ljava/lang/String;F)V", "detail", "targeted", "", "percent", "renderCommissionRow", "(Ljava/lang/String;Ljava/lang/String;ZIF)V", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "slider", "renderSliderRow", "(Ljava/lang/String;Lorg/cobalt/api/module/setting/impl/SliderSetting;F)V", "Lorg/cobalt/api/module/setting/impl/RangeSetting;", "setting", "renderRangeRow", "(Ljava/lang/String;Lorg/cobalt/api/module/setting/impl/RangeSetting;F)V", "btnY", "renderActionButton", "(Ljava/lang/String;F)V", "button", "mouseClicked", "(I)Z", "", "offsetX", "offsetY", "mouseDragged", "(IDD)Z", "mouseReleased", "horizontalAmount", "verticalAmount", "mouseScrolled", "(DD)Z", "handlePingDelayClick", "()Z", "updateSliderFromMouse", "(Lorg/cobalt/api/module/setting/impl/SliderSetting;)V", "updatePingDelayFromMouse", "trackX", "trackW", "rangeThumbX", "(DFFLorg/cobalt/api/module/setting/impl/RangeSetting;)F", "rangeValueFromMouse", "(FFLorg/cobalt/api/module/setting/impl/RangeSetting;)D", "step", "formatNumber", "(DD)Ljava/lang/String;", "text", "maxWidth", "fontSize", "ellipsize", "(Ljava/lang/String;FF)Ljava/lang/String;", "commissionCount", "Lorg/cobalt/internal/ui/panel/panels/UIMiningStatsPanel$Companion$Layout;", "buildLayout", "(I)Lorg/cobalt/internal/ui/panel/panels/UIMiningStatsPanel$Companion$Layout;", "", "Lkotlin/Pair;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "checkboxes", "Ljava/util/List;", "sliders", "draggingSlider", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "draggingPingStart", "Z", "draggingPingEnd", "Companion", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUIMiningStatsPanel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UIMiningStatsPanel.kt\norg/cobalt/internal/ui/panel/panels/UIMiningStatsPanel\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Extensions.kt\norg/cobalt/internal/ui/util/ExtensionsKt\n*L\n1#1,476:1\n1924#2,3:477\n1924#2,3:480\n1924#2,3:483\n1924#2,3:486\n1924#2,3:489\n1924#2,3:492\n6#3:495\n6#3:496\n*S KotlinDebug\n*F\n+ 1 UIMiningStatsPanel.kt\norg/cobalt/internal/ui/panel/panels/UIMiningStatsPanel\n*L\n56#1:477,3\n75#1:480,3\n84#1:483,3\n224#1:486,3\n237#1:489,3\n280#1:492,3\n331#1:495\n362#1:496\n*E\n"})
public final class UIMiningStatsPanel
extends UIPanel {
    @NotNull
    private static final Companion Companion = new Companion(null);
    @NotNull
    private final List<Pair<String, CheckboxSetting>> checkboxes;
    @NotNull
    private final List<Pair<String, SliderSetting>> sliders;
    @Nullable
    private SliderSetting draggingSlider;
    private boolean draggingPingStart;
    private boolean draggingPingEnd;
    @Deprecated
    public static final float PAD = 14.0f;
    @Deprecated
    public static final float CB_SIZE = 12.0f;
    @Deprecated
    public static final float ITEM_H = 28.0f;
    @Deprecated
    public static final float ITEM_GAP = 4.0f;
    @Deprecated
    public static final float BTN_H = 26.0f;
    @Deprecated
    public static final float STAT_H = 20.0f;
    @Deprecated
    public static final float COMMISSION_H = 40.0f;
    @Deprecated
    public static final float COMMISSION_ROW_H = 36.0f;
    @Deprecated
    public static final float PROGRESS_BAR_H = 4.0f;
    @Deprecated
    public static final float SLIDER_H = 26.0f;
    @Deprecated
    public static final float LABEL_W = 88.0f;
    @Deprecated
    public static final float VALUE_W = 36.0f;
    @Deprecated
    public static final float RANGE_VALUE_W = 82.0f;
    @Deprecated
    public static final int TARGET_ROW_FILL = 372754545;
    @Deprecated
    public static final int TARGET_ROW_BORDER = -13121423;
    @Deprecated
    public static final int TARGET_ROW_TEXT = -9508206;
    @Deprecated
    public static final float TITLE_Y = 14.0f;
    @Deprecated
    public static final float DIV1_Y = 34.0f;
    @Deprecated
    public static final float SEC1_Y = 44.0f;
    @Deprecated
    public static final float CB_START_Y = 56.0f;

    public UIMiningStatsPanel() {
        super(0.0f, 0.0f, 300.0f, 760.0f);
        Object[] objectArray = new Pair[]{TuplesKt.to((Object)"Precision Active", (Object)MiningModule.INSTANCE.getPrecisionActive()), TuplesKt.to((Object)"Speed Boost Active", (Object)MiningModule.INSTANCE.getSpeedBoostActive()), TuplesKt.to((Object)"Front Loaded", (Object)MiningModule.INSTANCE.getFrontLoadedActive()), TuplesKt.to((Object)"Skymall", (Object)MiningModule.INSTANCE.getSkymallActive()), TuplesKt.to((Object)"Mining Gems", (Object)MiningModule.INSTANCE.getMiningGems()), TuplesKt.to((Object)"Umber / Tungsten", (Object)MiningModule.INSTANCE.getMiningUmberTungsten())};
        this.checkboxes = CollectionsKt.listOf((Object[])objectArray);
        objectArray = new Pair[]{TuplesKt.to((Object)"Block Strength", (Object)MiningModule.INSTANCE.getBlockStrength()), TuplesKt.to((Object)"Scan Radius", (Object)MiningMacroModule.INSTANCE.getScanRadius$cobalt()), TuplesKt.to((Object)"Scan Vertical", (Object)MiningMacroModule.INSTANCE.getScanVertical$cobalt()), TuplesKt.to((Object)"Scan Per Tick", (Object)MiningMacroModule.INSTANCE.getScanPerTick$cobalt()), TuplesKt.to((Object)"Max Vein Blocks", (Object)MiningMacroModule.INSTANCE.getMaxVeinBlocks$cobalt())};
        this.sliders = CollectionsKt.listOf((Object[])objectArray);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void render() {
        String label;
        int i;
        Object object;
        int n;
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        List<CommissionMacroModule.CommissionHudRow> commissionRows = CommissionMacroModule.INSTANCE.getCommissionRows();
        Companion.Layout layout = this.buildLayout(commissionRows.size());
        this.setHeight(layout.getPanelHeight());
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), theme.getBackground(), 10.0f);
        NVGRenderer.text$default("Mining Stats", this.getX() + 14.0f, this.getY() + 14.0f, 13.0f, theme.getText(), null, 32, null);
        NVGRenderer.line(this.getX() + 14.0f, this.getY() + layout.getDiv1Y(), this.getX() + this.getWidth() - 14.0f, this.getY() + layout.getDiv1Y(), 1.0f, theme.getControlBorder());
        NVGRenderer.text$default("ACTIVE BUFFS", this.getX() + 14.0f, this.getY() + layout.getSec1Y(), 9.0f, theme.getTextSecondary(), null, 32, null);
        Iterable $this$forEachIndexed$iv = this.checkboxes;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            object = (Pair)item$iv;
            i = n;
            boolean bl = false;
            label = (String)object.component1();
            CheckboxSetting setting = (CheckboxSetting)object.component2();
            this.renderCheckboxRow(label, (Boolean)setting.getValue(), this.getY() + layout.getCheckboxesStartY() + (float)i * 32.0f);
        }
        NVGRenderer.line(this.getX() + 14.0f, this.getY() + layout.getDiv2Y(), this.getX() + this.getWidth() - 14.0f, this.getY() + layout.getDiv2Y(), 1.0f, theme.getControlBorder());
        NVGRenderer.text$default("STATS", this.getX() + 14.0f, this.getY() + layout.getSec2Y(), 9.0f, theme.getTextSecondary(), null, 32, null);
        this.renderStatRow("Mining Speed", (String)MiningModule.INSTANCE.getMiningSpeedText().getValue(), this.getY() + layout.getStatsStartY() + 0.0f);
        this.renderStatRow("HOTM Mult", (String)MiningModule.INSTANCE.getHotmMultiplierText().getValue(), this.getY() + layout.getStatsStartY() + 20.0f);
        this.renderStatRow("Look Calc", MiningModule.INSTANCE.getLookTicksText().getValue() + " t", this.getY() + layout.getStatsStartY() + 40.0f);
        this.renderStatRow("Look Left", MiningModule.INSTANCE.getLookCountdownText().getValue() + " t", this.getY() + layout.getStatsStartY() + 60.0f);
        this.renderStatRow("Ping", MiningModule.INSTANCE.getPingText().getValue() + " ms", this.getY() + layout.getStatsStartY() + 80.0f);
        NVGRenderer.line(this.getX() + 14.0f, this.getY() + layout.getDiv3Y(), this.getX() + this.getWidth() - 14.0f, this.getY() + layout.getDiv3Y(), 1.0f, theme.getControlBorder());
        NVGRenderer.text$default("COMMISSIONS", this.getX() + 14.0f, this.getY() + layout.getSec3Y(), 9.0f, theme.getTextSecondary(), null, 32, null);
        if (commissionRows.isEmpty()) {
            this.renderCommissionRow("No active commissions", "Idle", false, 0, this.getY() + layout.getCommissionsStartY());
        } else {
            $this$forEachIndexed$iv = commissionRows;
            $i$f$forEachIndexed = false;
            index$iv = 0;
            for (Object item$iv : $this$forEachIndexed$iv) {
                void row;
                if ((n = index$iv++) < 0) {
                    CollectionsKt.throwIndexOverflow();
                }
                object = (CommissionMacroModule.CommissionHudRow)item$iv;
                i = n;
                boolean bl = false;
                this.renderCommissionRow(row.getLabel(), row.getDetail(), row.isTargeted(), row.getPercent(), this.getY() + layout.getCommissionsStartY() + (float)i * 40.0f);
            }
        }
        NVGRenderer.line(this.getX() + 14.0f, this.getY() + layout.getDiv4Y(), this.getX() + this.getWidth() - 14.0f, this.getY() + layout.getDiv4Y(), 1.0f, theme.getControlBorder());
        NVGRenderer.text$default("MINING SETTINGS", this.getX() + 14.0f, this.getY() + layout.getSec4Y(), 9.0f, theme.getTextSecondary(), null, 32, null);
        this.renderRangeRow("Ping Delay", MiningModule.INSTANCE.getPingDelay(), this.getY() + layout.getSettingsStartY());
        $this$forEachIndexed$iv = this.sliders;
        $i$f$forEachIndexed = false;
        index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            object = (Pair)item$iv;
            i = n;
            boolean bl = false;
            label = (String)object.component1();
            SliderSetting slider = (SliderSetting)object.component2();
            this.renderSliderRow(label, slider, this.getY() + layout.getSliderStartY() + (float)i * 30.0f);
        }
        NVGRenderer.line(this.getX() + 14.0f, this.getY() + layout.getDiv5Y(), this.getX() + this.getWidth() - 14.0f, this.getY() + layout.getDiv5Y(), 1.0f, theme.getControlBorder());
        this.renderActionButton("Scrape All", this.getY() + layout.getButtonY());
    }

    private final void renderCheckboxRow(String label, boolean active, float rowY) {
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        float rowW = this.getWidth() - 28.0f;
        boolean hovering = ExtensionsKt.isHoveringOver(this.getX() + 14.0f, rowY, rowW, 28.0f);
        NVGRenderer.rect(this.getX() + 14.0f, rowY, rowW, 28.0f, hovering ? theme.getSelectedOverlay() : theme.getControlBg(), 6.0f);
        NVGRenderer.hollowRect(this.getX() + 14.0f, rowY, rowW, 28.0f, 1.0f, theme.getControlBorder(), 6.0f);
        float cbX = this.getX() + 14.0f + 8.0f;
        float cbY = rowY + 8.0f;
        if (active) {
            NVGRenderer.rect(cbX, cbY, 12.0f, 12.0f, theme.getAccent(), 3.0f);
            NVGRenderer.image$default(UICheckboxSetting.Companion.getCheckmarkIcon(), cbX + 1.0f, cbY + 1.0f, 10.0f, 10.0f, 0.0f, theme.getTextOnAccent(), 32, null);
        } else {
            NVGRenderer.rect(cbX, cbY, 12.0f, 12.0f, theme.getControlBg(), 3.0f);
            NVGRenderer.hollowRect(cbX, cbY, 12.0f, 12.0f, 1.0f, theme.getControlBorder(), 3.0f);
        }
        NVGRenderer.text$default(label, cbX + 12.0f + 7.0f, rowY + 14.0f - 5.5f, 11.0f, theme.getText(), null, 32, null);
    }

    private final void renderStatRow(String label, String value, float rowY) {
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        NVGRenderer.text$default(label, this.getX() + 14.0f, rowY, 11.0f, theme.getTextSecondary(), null, 32, null);
        float valueWidth = NVGRenderer.textWidth$default(value, 11.0f, null, 4, null);
        NVGRenderer.text$default(value, this.getX() + this.getWidth() - 14.0f - valueWidth, rowY, 11.0f, theme.getText(), null, 32, null);
    }

    private final void renderCommissionRow(String label, String detail, boolean targeted, int percent, float rowY) {
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        float rowW = this.getWidth() - 28.0f;
        int fillColor = targeted ? 372754545 : theme.getControlBg();
        int borderColor = targeted ? -13121423 : theme.getControlBorder();
        int labelColor = targeted ? -9508206 : theme.getText();
        int detailColor = targeted ? -9508206 : theme.getTextSecondary();
        NVGRenderer.rect(this.getX() + 14.0f, rowY, rowW, 36.0f, fillColor, 6.0f);
        NVGRenderer.hollowRect(this.getX() + 14.0f, rowY, rowW, 36.0f, 1.0f, borderColor, 6.0f);
        float detailWidth = NVGRenderer.textWidth$default(detail, 10.0f, null, 4, null);
        float labelMaxWidth = RangesKt.coerceAtLeast((float)(rowW - 16.0f - detailWidth - 12.0f), (float)40.0f);
        String clippedLabel = this.ellipsize(label, labelMaxWidth, 10.0f);
        NVGRenderer.text$default(clippedLabel, this.getX() + 14.0f + 8.0f, rowY + 6.0f, 10.0f, labelColor, null, 32, null);
        NVGRenderer.text$default(detail, this.getX() + 14.0f + rowW - 8.0f - detailWidth, rowY + 6.0f, 10.0f, detailColor, null, 32, null);
        float barX = this.getX() + 14.0f + 8.0f;
        float barY = rowY + 36.0f - 4.0f - 4.0f;
        float barW = rowW - 16.0f;
        int trackColor = targeted ? 1077397617 : theme.getControlBorder() & 0xFFFFFF | 0x44000000;
        NVGRenderer.rect(barX, barY, barW, 4.0f, trackColor, 2.0f);
        float fillFrac = RangesKt.coerceIn((float)((float)percent / 100.0f), (float)0.0f, (float)1.0f);
        if (fillFrac > 0.0f) {
            int barFillColor = targeted ? -13121423 : theme.getAccent();
            NVGRenderer.rect(barX, barY, barW * fillFrac, 4.0f, barFillColor, 2.0f);
        }
    }

    private final void renderSliderRow(String label, SliderSetting slider, float rowY) {
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        float rowW = this.getWidth() - 28.0f;
        boolean hovering = ExtensionsKt.isHoveringOver(this.getX() + 14.0f, rowY, rowW, 26.0f);
        NVGRenderer.rect(this.getX() + 14.0f, rowY, rowW, 26.0f, hovering ? theme.getSelectedOverlay() : theme.getControlBg(), 6.0f);
        NVGRenderer.hollowRect(this.getX() + 14.0f, rowY, rowW, 26.0f, 1.0f, theme.getControlBorder(), 6.0f);
        NVGRenderer.text$default(label, this.getX() + 14.0f + 8.0f, rowY + 4.0f, 10.0f, theme.getText(), null, 32, null);
        float trackX = this.getX() + 14.0f + 88.0f;
        float trackW = rowW - 88.0f - 36.0f - 8.0f;
        float trackY = rowY + 13.0f - 2.0f;
        NVGRenderer.rect(trackX, trackY, trackW, 4.0f, theme.getControlBorder(), 2.0f);
        float pct = (float)RangesKt.coerceIn((double)((((Number)slider.getValue()).doubleValue() - slider.getMin()) / (slider.getMax() - slider.getMin())), (double)0.0, (double)1.0);
        float fillW = trackW * pct;
        if (fillW > 0.0f) {
            NVGRenderer.rect(trackX, trackY, fillW, 4.0f, theme.getAccent(), 2.0f);
        }
        NVGRenderer.rect(trackX + fillW - 4.0f, trackY - 3.0f, 8.0f, 10.0f, theme.getAccent(), 3.0f);
        String text = this.formatNumber(((Number)slider.getValue()).doubleValue(), slider.getStep());
        float valueWidth = NVGRenderer.textWidth$default(text, 10.0f, null, 4, null);
        NVGRenderer.text$default(text, this.getX() + this.getWidth() - 14.0f - 4.0f - valueWidth, rowY + 4.0f, 10.0f, theme.getText(), null, 32, null);
    }

    private final void renderRangeRow(String label, RangeSetting setting, float rowY) {
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        float rowW = this.getWidth() - 28.0f;
        boolean hovering = ExtensionsKt.isHoveringOver(this.getX() + 14.0f, rowY, rowW, 26.0f);
        NVGRenderer.rect(this.getX() + 14.0f, rowY, rowW, 26.0f, hovering ? theme.getSelectedOverlay() : theme.getControlBg(), 6.0f);
        NVGRenderer.hollowRect(this.getX() + 14.0f, rowY, rowW, 26.0f, 1.0f, theme.getControlBorder(), 6.0f);
        NVGRenderer.text$default(label, this.getX() + 14.0f + 8.0f, rowY + 4.0f, 10.0f, theme.getText(), null, 32, null);
        float trackX = this.getX() + 14.0f + 88.0f;
        float trackW = rowW - 88.0f - 82.0f - 10.0f;
        float trackY = rowY + 13.0f - 2.0f;
        float startX = this.rangeThumbX(((Number)((Pair)setting.getValue()).getFirst()).doubleValue(), trackX, trackW, setting);
        float endX = this.rangeThumbX(((Number)((Pair)setting.getValue()).getSecond()).doubleValue(), trackX, trackW, setting);
        NVGRenderer.rect(trackX, trackY, trackW, 4.0f, theme.getControlBorder(), 2.0f);
        NVGRenderer.rect(startX, trackY, endX - startX, 4.0f, theme.getAccent(), 2.0f);
        NVGRenderer.circle(startX, trackY + 2.0f, 5.0f, theme.getAccent());
        NVGRenderer.circle(endX, trackY + 2.0f, 5.0f, theme.getAccent());
        String text = UIMiningStatsPanel.formatNumber$default(this, ((Number)((Pair)setting.getValue()).getFirst()).doubleValue(), 0.0, 2, null) + "-" + UIMiningStatsPanel.formatNumber$default(this, ((Number)((Pair)setting.getValue()).getSecond()).doubleValue(), 0.0, 2, null);
        float valueWidth = NVGRenderer.textWidth$default(text, 10.0f, null, 4, null);
        NVGRenderer.text$default(text, this.getX() + this.getWidth() - 14.0f - 4.0f - valueWidth, rowY + 4.0f, 10.0f, theme.getText(), null, 32, null);
    }

    private final void renderActionButton(String label, float btnY) {
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        float btnW = this.getWidth() - 28.0f;
        boolean hovering = ExtensionsKt.isHoveringOver(this.getX() + 14.0f, btnY, btnW, 26.0f);
        NVGRenderer.rect(this.getX() + 14.0f, btnY, btnW, 26.0f, hovering ? theme.getSelectedOverlay() : theme.getControlBg(), 6.0f);
        NVGRenderer.hollowRect(this.getX() + 14.0f, btnY, btnW, 26.0f, 1.0f, theme.getControlBorder(), 6.0f);
        float labelWidth = NVGRenderer.textWidth$default(label, 11.0f, null, 4, null);
        NVGRenderer.text$default(label, this.getX() + 14.0f + (btnW - labelWidth) / 2.0f, btnY + 8.0f, 11.0f, theme.getText(), null, 32, null);
    }

    @Override
    public boolean mouseClicked(int button) {
        float rowW;
        float rowY;
        int i;
        Pair pair;
        int n;
        if (button != 0) {
            return false;
        }
        Companion.Layout layout = this.buildLayout(CommissionMacroModule.INSTANCE.getCommissionRows().size());
        Iterable $this$forEachIndexed$iv = this.checkboxes;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            pair = (Pair)item$iv;
            i = n;
            boolean bl = false;
            CheckboxSetting setting = (CheckboxSetting)pair.component2();
            rowY = this.getY() + layout.getCheckboxesStartY() + (float)i * 32.0f;
            rowW = this.getWidth() - 28.0f;
            if (!ExtensionsKt.isHoveringOver(this.getX() + 14.0f, rowY, rowW, 28.0f)) continue;
            setting.setValue((Boolean)setting.getValue() == false);
            return true;
        }
        if (this.handlePingDelayClick()) {
            return true;
        }
        $this$forEachIndexed$iv = this.sliders;
        $i$f$forEachIndexed = false;
        index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            pair = (Pair)item$iv;
            i = n;
            boolean bl = false;
            SliderSetting slider = (SliderSetting)pair.component2();
            rowY = this.getY() + layout.getSliderStartY() + (float)i * 30.0f;
            rowW = this.getWidth() - 28.0f;
            if (!ExtensionsKt.isHoveringOver(this.getX() + 14.0f, rowY, rowW, 26.0f)) continue;
            this.draggingSlider = slider;
            this.updateSliderFromMouse(slider);
            return true;
        }
        float btnW = this.getWidth() - 28.0f;
        if (ExtensionsKt.isHoveringOver(this.getX() + 14.0f, this.getY() + layout.getButtonY(), btnW, 26.0f)) {
            MiningModule.INSTANCE.getScrapeAll$cobalt().trigger();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(int button, double offsetX, double offsetY) {
        if (button != 0) {
            return false;
        }
        if (this.draggingPingStart || this.draggingPingEnd) {
            this.updatePingDelayFromMouse();
            return true;
        }
        SliderSetting sliderSetting = this.draggingSlider;
        if (sliderSetting == null) {
            return false;
        }
        SliderSetting slider = sliderSetting;
        this.updateSliderFromMouse(slider);
        return true;
    }

    @Override
    public boolean mouseReleased(int button) {
        if (button == 0) {
            this.draggingSlider = null;
            this.draggingPingStart = false;
            this.draggingPingEnd = false;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double horizontalAmount, double verticalAmount) {
        Companion.Layout layout = this.buildLayout(CommissionMacroModule.INSTANCE.getCommissionRows().size());
        Iterable $this$forEachIndexed$iv = this.sliders;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            int n;
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            Pair pair = (Pair)item$iv;
            int i = n;
            boolean bl = false;
            SliderSetting slider = (SliderSetting)pair.component2();
            float rowY = this.getY() + layout.getSliderStartY() + (float)i * 30.0f;
            float rowW = this.getWidth() - 28.0f;
            if (!ExtensionsKt.isHoveringOver(this.getX() + 14.0f, rowY, rowW, 26.0f)) continue;
            double step = slider.getStep() > 0.0 ? slider.getStep() : 1.0;
            slider.setValue(RangesKt.coerceIn((double)(((Number)slider.getValue()).doubleValue() + verticalAmount * step), (double)slider.getMin(), (double)slider.getMax()));
            return true;
        }
        return false;
    }

    private final boolean handlePingDelayClick() {
        double distToEnd;
        RangeSetting setting = MiningModule.INSTANCE.getPingDelay();
        float rowY = this.getY() + this.buildLayout(CommissionMacroModule.INSTANCE.getCommissionRows().size()).getSettingsStartY();
        float rowW = this.getWidth() - 28.0f;
        if (!ExtensionsKt.isHoveringOver(this.getX() + 14.0f, rowY, rowW, 26.0f)) {
            return false;
        }
        float trackX = this.getX() + 14.0f + 88.0f;
        float trackW = rowW - 88.0f - 82.0f - 10.0f;
        float trackY = rowY + 13.0f - 2.0f;
        float startX = this.rangeThumbX(((Number)((Pair)setting.getValue()).getFirst()).doubleValue(), trackX, trackW, setting);
        float endX = this.rangeThumbX(((Number)((Pair)setting.getValue()).getSecond()).doubleValue(), trackX, trackW, setting);
        if (ExtensionsKt.isHoveringOver(startX - 8.0f, trackY - 6.0f, 16.0f, 16.0f)) {
            this.draggingPingStart = true;
            return true;
        }
        if (ExtensionsKt.isHoveringOver(endX - 8.0f, trackY - 6.0f, 16.0f, 16.0f)) {
            this.draggingPingEnd = true;
            return true;
        }
        double clickedValue = this.rangeValueFromMouse(trackX, trackW, setting);
        double distToStart = Math.abs(clickedValue - ((Number)((Pair)setting.getValue()).getFirst()).doubleValue());
        if (distToStart <= (distToEnd = Math.abs(clickedValue - ((Number)((Pair)setting.getValue()).getSecond()).doubleValue()))) {
            setting.setValue(TuplesKt.to((Object)RangesKt.coerceAtMost((double)clickedValue, (double)((Number)((Pair)setting.getValue()).getSecond()).doubleValue()), (Object)((Pair)setting.getValue()).getSecond()));
            this.draggingPingStart = true;
        } else {
            setting.setValue(TuplesKt.to((Object)((Pair)setting.getValue()).getFirst(), (Object)RangesKt.coerceAtLeast((double)clickedValue, (double)((Number)((Pair)setting.getValue()).getFirst()).doubleValue())));
            this.draggingPingEnd = true;
        }
        return true;
    }

    private final void updateSliderFromMouse(SliderSetting slider) {
        float trackX = this.getX() + 14.0f + 88.0f;
        float trackW = this.getWidth() - 28.0f - 88.0f - 36.0f - 8.0f;
        boolean $i$f$getMouseX = false;
        float relX = RangesKt.coerceIn((float)((float)class_310.method_1551().field_1729.method_1603() - trackX), (float)0.0f, (float)trackW);
        float pct = relX / trackW;
        double raw = slider.getMin() + (double)pct * (slider.getMax() - slider.getMin());
        slider.setValue(slider.getStep() > 0.0 ? RangesKt.coerceIn((double)((double)Math.round(raw / slider.getStep()) * slider.getStep()), (double)slider.getMin(), (double)slider.getMax()) : RangesKt.coerceIn((double)raw, (double)slider.getMin(), (double)slider.getMax()));
    }

    private final void updatePingDelayFromMouse() {
        RangeSetting setting = MiningModule.INSTANCE.getPingDelay();
        float rowW = this.getWidth() - 28.0f;
        float trackX = this.getX() + 14.0f + 88.0f;
        float trackW = rowW - 88.0f - 82.0f - 10.0f;
        double value = this.rangeValueFromMouse(trackX, trackW, setting);
        setting.setValue(this.draggingPingStart ? TuplesKt.to((Object)RangesKt.coerceAtMost((double)value, (double)((Number)((Pair)setting.getValue()).getSecond()).doubleValue()), (Object)((Pair)setting.getValue()).getSecond()) : TuplesKt.to((Object)((Pair)setting.getValue()).getFirst(), (Object)RangesKt.coerceAtLeast((double)value, (double)((Number)((Pair)setting.getValue()).getFirst()).doubleValue())));
    }

    private final float rangeThumbX(double value, float trackX, float trackW, RangeSetting setting) {
        float pct = (float)RangesKt.coerceIn((double)((value - setting.getMin()) / (setting.getMax() - setting.getMin())), (double)0.0, (double)1.0);
        return trackX + trackW * pct;
    }

    private final double rangeValueFromMouse(float trackX, float trackW, RangeSetting setting) {
        boolean $i$f$getMouseX = false;
        float relX = RangesKt.coerceIn((float)((float)class_310.method_1551().field_1729.method_1603() - trackX), (float)0.0f, (float)trackW);
        float pct = relX / trackW;
        double raw = setting.getMin() + (double)pct * (setting.getMax() - setting.getMin());
        return Math.rint(raw * 10.0) / 10.0;
    }

    private final String formatNumber(double value, double step) {
        String string;
        double rounded = Math.rint(value * 10.0) / 10.0;
        if (step >= 1.0 && step % 1.0 == 0.0) {
            string = String.valueOf((int)rounded);
        } else if (Math.abs(rounded - (double)((int)rounded)) < 0.001) {
            string = String.valueOf((int)rounded);
        } else {
            String string2 = "%.1f";
            Object[] objectArray = new Object[]{rounded};
            String string3 = String.format(string2, Arrays.copyOf(objectArray, objectArray.length));
            string = string3;
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"format(...)");
        }
        return string;
    }

    static /* synthetic */ String formatNumber$default(UIMiningStatsPanel uIMiningStatsPanel, double d, double d2, int n, Object object) {
        if ((n & 2) != 0) {
            d2 = 0.1;
        }
        return uIMiningStatsPanel.formatNumber(d, d2);
    }

    private final String ellipsize(String text, float maxWidth, float fontSize) {
        if (NVGRenderer.textWidth$default(text, fontSize, null, 4, null) <= maxWidth) {
            return text;
        }
        for (int end = text.length(); end > 1; --end) {
            String candidate = ((Object)StringsKt.trimEnd((CharSequence)StringsKt.take((String)text, (int)end))).toString() + "...";
            if (!(NVGRenderer.textWidth$default(candidate, fontSize, null, 4, null) <= maxWidth)) continue;
            return candidate;
        }
        return "...";
    }

    private final Companion.Layout buildLayout(int commissionCount) {
        float checkboxesEndY = 56.0f + (float)this.checkboxes.size() * 32.0f - 4.0f;
        float div2Y = checkboxesEndY + 12.0f;
        float sec2Y = div2Y + 10.0f;
        float statsStartY = sec2Y + 14.0f;
        float statsEndY = statsStartY + 100.0f;
        float div3Y = statsEndY + 4.0f;
        float sec3Y = div3Y + 10.0f;
        float commissionsStartY = sec3Y + 14.0f;
        int visibleCommissions = Math.max(commissionCount, 1);
        float commissionsEndY = commissionsStartY + (float)visibleCommissions * 40.0f;
        float div4Y = commissionsEndY + 10.0f;
        float sec4Y = div4Y + 10.0f;
        float settingsStartY = sec4Y + 14.0f;
        float sliderStartY = settingsStartY + 26.0f + 4.0f;
        float slidersEndY = sliderStartY + (float)this.sliders.size() * 30.0f - 4.0f;
        float div5Y = slidersEndY + 12.0f;
        float buttonY = div5Y + 10.0f;
        float panelHeight = buttonY + 26.0f + 12.0f;
        return new Companion.Layout(34.0f, 44.0f, 56.0f, div2Y, sec2Y, statsStartY, div3Y, sec3Y, commissionsStartY, div4Y, sec4Y, settingsStartY, sliderStartY, div5Y, buttonY, panelHeight);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u000e\n\u0002\u0010\b\n\u0002\b\n\b\u0082\u0003\u0018\u00002\u00020\u0001:\u0001\u001cB\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006R\u0014\u0010\u0007\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\u0007\u0010\u0006R\u0014\u0010\b\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\b\u0010\u0006R\u0014\u0010\t\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\t\u0010\u0006R\u0014\u0010\n\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\n\u0010\u0006R\u0014\u0010\u000b\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\u000b\u0010\u0006R\u0014\u0010\f\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\f\u0010\u0006R\u0014\u0010\r\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\r\u0010\u0006R\u0014\u0010\u000e\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\u000e\u0010\u0006R\u0014\u0010\u000f\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\u000f\u0010\u0006R\u0014\u0010\u0010\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\u0010\u0010\u0006R\u0014\u0010\u0011\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\u0011\u0010\u0006R\u0014\u0010\u0012\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0006R\u0014\u0010\u0014\u001a\u00020\u00138\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\u0014\u0010\u0015R\u0014\u0010\u0016\u001a\u00020\u00138\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\u0016\u0010\u0015R\u0014\u0010\u0017\u001a\u00020\u00138\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\u0017\u0010\u0015R\u0014\u0010\u0018\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0006R\u0014\u0010\u0019\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u0006R\u0014\u0010\u001a\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\u001a\u0010\u0006R\u0014\u0010\u001b\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\u001b\u0010\u0006\u00a8\u0006\u001d"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIMiningStatsPanel$Companion;", "", "<init>", "()V", "", "PAD", "F", "CB_SIZE", "ITEM_H", "ITEM_GAP", "BTN_H", "STAT_H", "COMMISSION_H", "COMMISSION_ROW_H", "PROGRESS_BAR_H", "SLIDER_H", "LABEL_W", "VALUE_W", "RANGE_VALUE_W", "", "TARGET_ROW_FILL", "I", "TARGET_ROW_BORDER", "TARGET_ROW_TEXT", "TITLE_Y", "DIV1_Y", "SEC1_Y", "CB_START_Y", "Layout", "cobalt"})
    private static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0007\n\u0002\b&\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0014\b\u0082\b\u0018\u00002\u00020\u0001B\u0087\u0001\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u0012\u0006\u0010\u0007\u001a\u00020\u0002\u0012\u0006\u0010\b\u001a\u00020\u0002\u0012\u0006\u0010\t\u001a\u00020\u0002\u0012\u0006\u0010\n\u001a\u00020\u0002\u0012\u0006\u0010\u000b\u001a\u00020\u0002\u0012\u0006\u0010\f\u001a\u00020\u0002\u0012\u0006\u0010\r\u001a\u00020\u0002\u0012\u0006\u0010\u000e\u001a\u00020\u0002\u0012\u0006\u0010\u000f\u001a\u00020\u0002\u0012\u0006\u0010\u0010\u001a\u00020\u0002\u0012\u0006\u0010\u0011\u001a\u00020\u0002\u0012\u0006\u0010\u0012\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0010\u0010\u0015\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u0010\u0010\u0017\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0017\u0010\u0016J\u0010\u0010\u0018\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0018\u0010\u0016J\u0010\u0010\u0019\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0019\u0010\u0016J\u0010\u0010\u001a\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u001a\u0010\u0016J\u0010\u0010\u001b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u001b\u0010\u0016J\u0010\u0010\u001c\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u001c\u0010\u0016J\u0010\u0010\u001d\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u001d\u0010\u0016J\u0010\u0010\u001e\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u001e\u0010\u0016J\u0010\u0010\u001f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u001f\u0010\u0016J\u0010\u0010 \u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b \u0010\u0016J\u0010\u0010!\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b!\u0010\u0016J\u0010\u0010\"\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\"\u0010\u0016J\u0010\u0010#\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b#\u0010\u0016J\u0010\u0010$\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b$\u0010\u0016J\u0010\u0010%\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b%\u0010\u0016J\u00b0\u0001\u0010&\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u00022\b\b\u0002\u0010\u0007\u001a\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\u00022\b\b\u0002\u0010\t\u001a\u00020\u00022\b\b\u0002\u0010\n\u001a\u00020\u00022\b\b\u0002\u0010\u000b\u001a\u00020\u00022\b\b\u0002\u0010\f\u001a\u00020\u00022\b\b\u0002\u0010\r\u001a\u00020\u00022\b\b\u0002\u0010\u000e\u001a\u00020\u00022\b\b\u0002\u0010\u000f\u001a\u00020\u00022\b\b\u0002\u0010\u0010\u001a\u00020\u00022\b\b\u0002\u0010\u0011\u001a\u00020\u00022\b\b\u0002\u0010\u0012\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b&\u0010'J\u001b\u0010*\u001a\u00020)2\b\u0010(\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b*\u0010+J\u0011\u0010-\u001a\u00020,H\u00d6\u0081\u0004\u00a2\u0006\u0004\b-\u0010.J\u0011\u00100\u001a\u00020/H\u00d6\u0081\u0004\u00a2\u0006\u0004\b0\u00101R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u00102\u001a\u0004\b3\u0010\u0016R\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u00102\u001a\u0004\b4\u0010\u0016R\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u00102\u001a\u0004\b5\u0010\u0016R\u0017\u0010\u0006\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0006\u00102\u001a\u0004\b6\u0010\u0016R\u0017\u0010\u0007\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0007\u00102\u001a\u0004\b7\u0010\u0016R\u0017\u0010\b\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\b\u00102\u001a\u0004\b8\u0010\u0016R\u0017\u0010\t\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\t\u00102\u001a\u0004\b9\u0010\u0016R\u0017\u0010\n\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\n\u00102\u001a\u0004\b:\u0010\u0016R\u0017\u0010\u000b\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u000b\u00102\u001a\u0004\b;\u0010\u0016R\u0017\u0010\f\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\f\u00102\u001a\u0004\b<\u0010\u0016R\u0017\u0010\r\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\r\u00102\u001a\u0004\b=\u0010\u0016R\u0017\u0010\u000e\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u000e\u00102\u001a\u0004\b>\u0010\u0016R\u0017\u0010\u000f\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u000f\u00102\u001a\u0004\b?\u0010\u0016R\u0017\u0010\u0010\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0010\u00102\u001a\u0004\b@\u0010\u0016R\u0017\u0010\u0011\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0011\u00102\u001a\u0004\bA\u0010\u0016R\u0017\u0010\u0012\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0012\u00102\u001a\u0004\bB\u0010\u0016\u00a8\u0006C"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIMiningStatsPanel$Companion$Layout;", "", "", "div1Y", "sec1Y", "checkboxesStartY", "div2Y", "sec2Y", "statsStartY", "div3Y", "sec3Y", "commissionsStartY", "div4Y", "sec4Y", "settingsStartY", "sliderStartY", "div5Y", "buttonY", "panelHeight", "<init>", "(FFFFFFFFFFFFFFFF)V", "component1", "()F", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "copy", "(FFFFFFFFFFFFFFFF)Lorg/cobalt/internal/ui/panel/panels/UIMiningStatsPanel$Companion$Layout;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "F", "getDiv1Y", "getSec1Y", "getCheckboxesStartY", "getDiv2Y", "getSec2Y", "getStatsStartY", "getDiv3Y", "getSec3Y", "getCommissionsStartY", "getDiv4Y", "getSec4Y", "getSettingsStartY", "getSliderStartY", "getDiv5Y", "getButtonY", "getPanelHeight", "cobalt"})
        private static final class Layout {
            private final float div1Y;
            private final float sec1Y;
            private final float checkboxesStartY;
            private final float div2Y;
            private final float sec2Y;
            private final float statsStartY;
            private final float div3Y;
            private final float sec3Y;
            private final float commissionsStartY;
            private final float div4Y;
            private final float sec4Y;
            private final float settingsStartY;
            private final float sliderStartY;
            private final float div5Y;
            private final float buttonY;
            private final float panelHeight;

            public Layout(float div1Y, float sec1Y, float checkboxesStartY, float div2Y, float sec2Y, float statsStartY, float div3Y, float sec3Y, float commissionsStartY, float div4Y, float sec4Y, float settingsStartY, float sliderStartY, float div5Y, float buttonY, float panelHeight) {
                this.div1Y = div1Y;
                this.sec1Y = sec1Y;
                this.checkboxesStartY = checkboxesStartY;
                this.div2Y = div2Y;
                this.sec2Y = sec2Y;
                this.statsStartY = statsStartY;
                this.div3Y = div3Y;
                this.sec3Y = sec3Y;
                this.commissionsStartY = commissionsStartY;
                this.div4Y = div4Y;
                this.sec4Y = sec4Y;
                this.settingsStartY = settingsStartY;
                this.sliderStartY = sliderStartY;
                this.div5Y = div5Y;
                this.buttonY = buttonY;
                this.panelHeight = panelHeight;
            }

            public final float getDiv1Y() {
                return this.div1Y;
            }

            public final float getSec1Y() {
                return this.sec1Y;
            }

            public final float getCheckboxesStartY() {
                return this.checkboxesStartY;
            }

            public final float getDiv2Y() {
                return this.div2Y;
            }

            public final float getSec2Y() {
                return this.sec2Y;
            }

            public final float getStatsStartY() {
                return this.statsStartY;
            }

            public final float getDiv3Y() {
                return this.div3Y;
            }

            public final float getSec3Y() {
                return this.sec3Y;
            }

            public final float getCommissionsStartY() {
                return this.commissionsStartY;
            }

            public final float getDiv4Y() {
                return this.div4Y;
            }

            public final float getSec4Y() {
                return this.sec4Y;
            }

            public final float getSettingsStartY() {
                return this.settingsStartY;
            }

            public final float getSliderStartY() {
                return this.sliderStartY;
            }

            public final float getDiv5Y() {
                return this.div5Y;
            }

            public final float getButtonY() {
                return this.buttonY;
            }

            public final float getPanelHeight() {
                return this.panelHeight;
            }

            public final float component1() {
                return this.div1Y;
            }

            public final float component2() {
                return this.sec1Y;
            }

            public final float component3() {
                return this.checkboxesStartY;
            }

            public final float component4() {
                return this.div2Y;
            }

            public final float component5() {
                return this.sec2Y;
            }

            public final float component6() {
                return this.statsStartY;
            }

            public final float component7() {
                return this.div3Y;
            }

            public final float component8() {
                return this.sec3Y;
            }

            public final float component9() {
                return this.commissionsStartY;
            }

            public final float component10() {
                return this.div4Y;
            }

            public final float component11() {
                return this.sec4Y;
            }

            public final float component12() {
                return this.settingsStartY;
            }

            public final float component13() {
                return this.sliderStartY;
            }

            public final float component14() {
                return this.div5Y;
            }

            public final float component15() {
                return this.buttonY;
            }

            public final float component16() {
                return this.panelHeight;
            }

            @NotNull
            public final Layout copy(float div1Y, float sec1Y, float checkboxesStartY, float div2Y, float sec2Y, float statsStartY, float div3Y, float sec3Y, float commissionsStartY, float div4Y, float sec4Y, float settingsStartY, float sliderStartY, float div5Y, float buttonY, float panelHeight) {
                return new Layout(div1Y, sec1Y, checkboxesStartY, div2Y, sec2Y, statsStartY, div3Y, sec3Y, commissionsStartY, div4Y, sec4Y, settingsStartY, sliderStartY, div5Y, buttonY, panelHeight);
            }

            public static /* synthetic */ Layout copy$default(Layout layout, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, float f14, float f15, float f16, int n, Object object) {
                if ((n & 1) != 0) {
                    f = layout.div1Y;
                }
                if ((n & 2) != 0) {
                    f2 = layout.sec1Y;
                }
                if ((n & 4) != 0) {
                    f3 = layout.checkboxesStartY;
                }
                if ((n & 8) != 0) {
                    f4 = layout.div2Y;
                }
                if ((n & 0x10) != 0) {
                    f5 = layout.sec2Y;
                }
                if ((n & 0x20) != 0) {
                    f6 = layout.statsStartY;
                }
                if ((n & 0x40) != 0) {
                    f7 = layout.div3Y;
                }
                if ((n & 0x80) != 0) {
                    f8 = layout.sec3Y;
                }
                if ((n & 0x100) != 0) {
                    f9 = layout.commissionsStartY;
                }
                if ((n & 0x200) != 0) {
                    f10 = layout.div4Y;
                }
                if ((n & 0x400) != 0) {
                    f11 = layout.sec4Y;
                }
                if ((n & 0x800) != 0) {
                    f12 = layout.settingsStartY;
                }
                if ((n & 0x1000) != 0) {
                    f13 = layout.sliderStartY;
                }
                if ((n & 0x2000) != 0) {
                    f14 = layout.div5Y;
                }
                if ((n & 0x4000) != 0) {
                    f15 = layout.buttonY;
                }
                if ((n & 0x8000) != 0) {
                    f16 = layout.panelHeight;
                }
                return layout.copy(f, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16);
            }

            @NotNull
            public String toString() {
                return "Layout(div1Y=" + this.div1Y + ", sec1Y=" + this.sec1Y + ", checkboxesStartY=" + this.checkboxesStartY + ", div2Y=" + this.div2Y + ", sec2Y=" + this.sec2Y + ", statsStartY=" + this.statsStartY + ", div3Y=" + this.div3Y + ", sec3Y=" + this.sec3Y + ", commissionsStartY=" + this.commissionsStartY + ", div4Y=" + this.div4Y + ", sec4Y=" + this.sec4Y + ", settingsStartY=" + this.settingsStartY + ", sliderStartY=" + this.sliderStartY + ", div5Y=" + this.div5Y + ", buttonY=" + this.buttonY + ", panelHeight=" + this.panelHeight + ")";
            }

            public int hashCode() {
                int result = Float.hashCode(this.div1Y);
                result = result * 31 + Float.hashCode(this.sec1Y);
                result = result * 31 + Float.hashCode(this.checkboxesStartY);
                result = result * 31 + Float.hashCode(this.div2Y);
                result = result * 31 + Float.hashCode(this.sec2Y);
                result = result * 31 + Float.hashCode(this.statsStartY);
                result = result * 31 + Float.hashCode(this.div3Y);
                result = result * 31 + Float.hashCode(this.sec3Y);
                result = result * 31 + Float.hashCode(this.commissionsStartY);
                result = result * 31 + Float.hashCode(this.div4Y);
                result = result * 31 + Float.hashCode(this.sec4Y);
                result = result * 31 + Float.hashCode(this.settingsStartY);
                result = result * 31 + Float.hashCode(this.sliderStartY);
                result = result * 31 + Float.hashCode(this.div5Y);
                result = result * 31 + Float.hashCode(this.buttonY);
                result = result * 31 + Float.hashCode(this.panelHeight);
                return result;
            }

            public boolean equals(@Nullable Object other) {
                if (this == other) {
                    return true;
                }
                if (!(other instanceof Layout)) {
                    return false;
                }
                Layout layout = (Layout)other;
                if (Float.compare(this.div1Y, layout.div1Y) != 0) {
                    return false;
                }
                if (Float.compare(this.sec1Y, layout.sec1Y) != 0) {
                    return false;
                }
                if (Float.compare(this.checkboxesStartY, layout.checkboxesStartY) != 0) {
                    return false;
                }
                if (Float.compare(this.div2Y, layout.div2Y) != 0) {
                    return false;
                }
                if (Float.compare(this.sec2Y, layout.sec2Y) != 0) {
                    return false;
                }
                if (Float.compare(this.statsStartY, layout.statsStartY) != 0) {
                    return false;
                }
                if (Float.compare(this.div3Y, layout.div3Y) != 0) {
                    return false;
                }
                if (Float.compare(this.sec3Y, layout.sec3Y) != 0) {
                    return false;
                }
                if (Float.compare(this.commissionsStartY, layout.commissionsStartY) != 0) {
                    return false;
                }
                if (Float.compare(this.div4Y, layout.div4Y) != 0) {
                    return false;
                }
                if (Float.compare(this.sec4Y, layout.sec4Y) != 0) {
                    return false;
                }
                if (Float.compare(this.settingsStartY, layout.settingsStartY) != 0) {
                    return false;
                }
                if (Float.compare(this.sliderStartY, layout.sliderStartY) != 0) {
                    return false;
                }
                if (Float.compare(this.div5Y, layout.div5Y) != 0) {
                    return false;
                }
                if (Float.compare(this.buttonY, layout.buttonY) != 0) {
                    return false;
                }
                return Float.compare(this.panelHeight, layout.panelHeight) == 0;
            }
        }
    }
}

