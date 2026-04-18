/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.TuplesKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.math.MathKt
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_1309
 *  net.minecraft.class_1799
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_490
 *  net.minecraft.class_638
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.wardrobe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_490;
import net.minecraft.class_638;
import org.cobalt.api.ui.theme.Theme;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.wardrobe.WardrobeFakePlayer;
import org.cobalt.internal.wardrobe.WardrobeFakePlayerCache;
import org.cobalt.internal.wardrobe.WardrobeModule;
import org.cobalt.internal.wardrobe.WardrobeSet;
import org.cobalt.internal.wardrobe.WardrobeState;
import org.cobalt.internal.wardrobe.WardrobeStateKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u0011\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001&B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001d\u0010\t\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\t\u0010\nJ?\u0010\u0013\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u00042\u000e\u0010\r\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\f0\u000b2\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0015\u0010\t\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\t\u0010\u0015R\u0014\u0010\u0017\u001a\u00020\u00168\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0017\u0010\u0018R\u0014\u0010\u0019\u001a\u00020\u00168\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u0018R\u0014\u0010\u001a\u001a\u00020\u00168\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001a\u0010\u0018R\u0014\u0010\u001b\u001a\u00020\u000e8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001b\u0010\u001cR\u0014\u0010\u001d\u001a\u00020\u00168\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001d\u0010\u0018R\u0014\u0010\u001e\u001a\u00020\u00168\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u0018R\u0014\u0010\u001f\u001a\u00020\u00168\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001f\u0010\u0018R\u0014\u0010 \u001a\u00020\u00168\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b \u0010\u0018R\u0014\u0010!\u001a\u00020\u00168\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b!\u0010\u0018R\u0014\u0010\"\u001a\u00020\u00168\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\"\u0010\u0018R\u0014\u0010#\u001a\u00020\u00168\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b#\u0010\u0018R\u0014\u0010$\u001a\u00020\u000e8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b$\u0010\u001cR\u0014\u0010%\u001a\u00020\u00168\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b%\u0010\u0018\u00a8\u0006'"}, d2={"Lorg/cobalt/internal/wardrobe/WardrobeRenderer;", "", "<init>", "()V", "Lnet/minecraft/class_332;", "graphics", "Lorg/cobalt/internal/wardrobe/WardrobeModule;", "module", "", "render", "(Lnet/minecraft/class_332;Lorg/cobalt/internal/wardrobe/WardrobeModule;)V", "", "Lnet/minecraft/class_1799;", "armor", "", "tooltipX", "tooltipY", "Lnet/minecraft/class_310;", "mc", "renderArmorTooltip", "(Lnet/minecraft/class_332;Ljava/util/List;IILnet/minecraft/class_310;)V", "(Lnet/minecraft/class_332;)V", "", "SLOT_W", "F", "SLOT_H", "SLOT_GAP", "MAX_COLS", "I", "PADDING", "TAB_H", "TAB_W", "TAB_GAP", "BTN_H", "BTN_W", "BTN_GAP", "PLAYER_SCALE", "SECTION_GAP", "Rect", "cobalt"})
@SourceDebugExtension(value={"SMAP\nWardrobeRenderer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WardrobeRenderer.kt\norg/cobalt/internal/wardrobe/WardrobeRenderer\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,211:1\n1596#2:212\n1629#2,4:213\n296#2,2:217\n296#2,2:225\n221#3,2:219\n221#3,2:221\n221#3,2:223\n*S KotlinDebug\n*F\n+ 1 WardrobeRenderer.kt\norg/cobalt/internal/wardrobe/WardrobeRenderer\n*L\n56#1:212\n56#1:213,4\n64#1:217,2\n200#1:225,2\n77#1:219,2\n118#1:221,2\n145#1:223,2\n*E\n"})
public final class WardrobeRenderer {
    @NotNull
    public static final WardrobeRenderer INSTANCE = new WardrobeRenderer();
    private static final float SLOT_W = 66.0f;
    private static final float SLOT_H = 120.0f;
    private static final float SLOT_GAP = 8.0f;
    private static final int MAX_COLS = 9;
    private static final float PADDING = 16.0f;
    private static final float TAB_H = 26.0f;
    private static final float TAB_W = 72.0f;
    private static final float TAB_GAP = 6.0f;
    private static final float BTN_H = 26.0f;
    private static final float BTN_W = 80.0f;
    private static final float BTN_GAP = 8.0f;
    private static final int PLAYER_SCALE = 40;
    private static final float SECTION_GAP = 8.0f;

    private WardrobeRenderer() {
    }

    /*
     * WARNING - void declaration
     */
    public final void render(@NotNull class_332 graphics, @NotNull WardrobeModule module) {
        block14: {
            WardrobeSet set;
            Object[] element$iv;
            Object v2;
            Map slotRects;
            int row;
            int n;
            float oy;
            float ox;
            float contentH;
            float contentW;
            List<WardrobeSet> sets;
            float my;
            float mx;
            float sh;
            float sw;
            float guiScale;
            class_638 level2;
            class_310 mc;
            block13: {
                void $this$mapIndexedTo$iv$iv;
                Intrinsics.checkNotNullParameter((Object)graphics, (String)"graphics");
                Intrinsics.checkNotNullParameter((Object)module, (String)"module");
                class_310 class_3102 = class_310.method_1551();
                Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
                mc = class_3102;
                class_638 class_6382 = mc.field_1687;
                if (class_6382 == null) {
                    return;
                }
                level2 = class_6382;
                guiScale = mc.method_22683().method_4495();
                sw = mc.method_22683().method_4480();
                sh = mc.method_22683().method_4507();
                mx = (float)mc.field_1729.method_1603();
                my = (float)mc.field_1729.method_1604();
                sets = module.setsOnCurrentCustomPage();
                int cols = RangesKt.coerceAtMost((int)sets.size(), (int)9);
                int rows = sets.isEmpty() ? 1 : (sets.size() + 9 - 1) / 9;
                float gridW = (float)cols * 66.0f + (float)RangesKt.coerceAtLeast((int)(cols - 1), (int)0) * 8.0f;
                float btnsW = 168.0f;
                contentW = Math.max(gridW, btnsW) + 32.0f;
                contentH = 16.0f + (float)rows * 120.0f + (float)RangesKt.coerceAtLeast((int)(rows - 1), (int)0) * 8.0f + 8.0f + 26.0f + 16.0f;
                ox = (sw - contentW) / 2.0f;
                oy = (sh - contentH) / 2.0f;
                Iterable $this$mapIndexed$iv = sets;
                boolean $i$f$mapIndexed = false;
                Iterable iterable = $this$mapIndexed$iv;
                Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$mapIndexed$iv, (int)10));
                boolean $i$f$mapIndexedTo = false;
                int index$iv$iv = 0;
                for (Object item$iv$iv : $this$mapIndexedTo$iv$iv) {
                    void set2;
                    void i;
                    int n2;
                    if ((n2 = index$iv$iv++) < 0) {
                        CollectionsKt.throwIndexOverflow();
                    }
                    WardrobeSet wardrobeSet = (WardrobeSet)item$iv$iv;
                    int n3 = n2;
                    Collection collection = destination$iv$iv;
                    n = 0;
                    void col = i % 9;
                    row = i / 9;
                    float x = ox + 16.0f + (float)col * 74.0f;
                    float y = oy + 16.0f + (float)row * 128.0f;
                    collection.add(TuplesKt.to((Object)set2.getId(), (Object)new Rect(x, y, 66.0f, 120.0f)));
                }
                slotRects = MapsKt.toMap((Iterable)((List)destination$iv$iv));
                Iterable $this$firstOrNull$iv = slotRects.entrySet();
                boolean $i$f$firstOrNull = false;
                for (Object element$iv2 : $this$firstOrNull$iv) {
                    Map.Entry entry = (Map.Entry)element$iv2;
                    boolean bl = false;
                    Rect r = (Rect)entry.getValue();
                    if (!r.contains(mx, my)) continue;
                    v2 = element$iv2;
                    break block13;
                }
                v2 = null;
            }
            Map.Entry entry = v2;
            Integer hoveredId = entry != null ? (Integer)entry.getKey() : null;
            NVGRenderer.INSTANCE.beginFrame(sw, sh);
            Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
            NVGRenderer.rect(ox, oy, contentW, contentH, theme.getPanel(), 12.0f);
            module.setTabHitboxes(CollectionsKt.emptyList());
            List newSlotHitboxes = new ArrayList();
            Map $this$forEach$iv = slotRects;
            boolean $i$f$forEach = false;
            Iterator element$iv2 = $this$forEach$iv.entrySet().iterator();
            while (element$iv2.hasNext()) {
                Object[] bl = element$iv = element$iv2.next();
                boolean bl2 = false;
                int id = ((Number)bl.getKey()).intValue();
                Rect r = (Rect)bl.getValue();
                Integer n4 = WardrobeState.INSTANCE.getEquippedSlotId();
                n = id;
                boolean isEquipped = n4 != null && n4 == n;
                boolean isFav = WardrobeState.INSTANCE.getFavorites().contains(id);
                Integer n5 = hoveredId;
                row = id;
                boolean isHovered = n5 != null && n5 == row;
                int cardBg = isEquipped ? theme.getSelectedOverlay() : (isHovered ? theme.getOverlay() : theme.getControlBg());
                int border = isEquipped ? theme.getAccent() : (isFav ? -10496 : theme.getControlBorder());
                NVGRenderer.rect(r.getX(), r.getY(), r.getW(), r.getH(), cardBg, 8.0f);
                NVGRenderer.hollowRect(r.getX(), r.getY(), r.getW(), r.getH(), 1.5f, border, 8.0f);
                newSlotHitboxes.add(new WardrobeModule.SlotHitbox(id, r.getX(), r.getY(), r.getW(), r.getH()));
            }
            module.setSlotHitboxes(newSlotHitboxes);
            float btnY = oy + contentH - 16.0f - 26.0f;
            float btnX = ox + 16.0f;
            NVGRenderer.rect(btnX, btnY, 80.0f, 26.0f, theme.getControlBg(), 6.0f);
            NVGRenderer.hollowRect(btnX, btnY, 80.0f, 26.0f, 1.0f, theme.getControlBorder(), 6.0f);
            NVGRenderer.text$default("\u25c4 Back", btnX + 40.0f, btnY + 13.0f - 5.0f, 11.0f, theme.getText(), null, 32, null);
            float closeBtnX = btnX + 80.0f + 8.0f;
            NVGRenderer.rect(closeBtnX, btnY, 80.0f, 26.0f, theme.getControlBg(), 6.0f);
            NVGRenderer.hollowRect(closeBtnX, btnY, 80.0f, 26.0f, 1.0f, theme.getControlBorder(), 6.0f);
            NVGRenderer.text$default("\u2715 Close", closeBtnX + 40.0f, btnY + 13.0f - 5.0f, 11.0f, -39322, null, 32, null);
            element$iv = new WardrobeModule.ButtonHitbox[]{new WardrobeModule.ButtonHitbox(WardrobeModule.ButtonType.BACK, btnX, btnY, 80.0f, 26.0f), new WardrobeModule.ButtonHitbox(WardrobeModule.ButtonType.CLOSE, closeBtnX, btnY, 80.0f, 26.0f)};
            module.setButtonHitboxes(CollectionsKt.listOf((Object[])element$iv));
            NVGRenderer.INSTANCE.endFrame();
            float mouseXGui = mx / guiScale;
            float mouseYGui = my / guiScale;
            Map $this$forEach$iv2 = slotRects;
            boolean $i$f$forEach2 = false;
            Iterator r = $this$forEach$iv2.entrySet().iterator();
            while (r.hasNext()) {
                Map.Entry element$iv3;
                Map.Entry isEquipped = element$iv3 = r.next();
                boolean bl = false;
                int id = ((Number)isEquipped.getKey()).intValue();
                Rect r2 = (Rect)isEquipped.getValue();
                if ((WardrobeSet)CollectionsKt.getOrNull(WardrobeState.INSTANCE.getSets(), (int)(id - 1)) == null || set.isEmpty()) continue;
                WardrobeFakePlayer fp = WardrobeFakePlayerCache.INSTANCE.get(id, set.getArmor(), level2);
                int gx1 = MathKt.roundToInt((float)(r2.getX() / guiScale));
                int gy1 = MathKt.roundToInt((float)(r2.getY() / guiScale));
                int gx2 = MathKt.roundToInt((float)((r2.getX() + r2.getW()) / guiScale));
                int gy2 = MathKt.roundToInt((float)((r2.getY() + r2.getH()) / guiScale));
                try {
                    class_490.method_2486((class_332)graphics, (int)gx1, (int)gy1, (int)gx2, (int)gy2, (int)40, (float)0.0f, (float)mouseXGui, (float)mouseYGui, (class_1309)((class_1309)fp));
                }
                catch (Exception exception) {
                }
            }
            NVGRenderer.INSTANCE.beginFrame(sw, sh);
            $this$forEach$iv2 = slotRects;
            $i$f$forEach2 = false;
            r = $this$forEach$iv2.entrySet().iterator();
            while (r.hasNext()) {
                Map.Entry element$iv4;
                Map.Entry isEquipped = element$iv4 = r.next();
                boolean bl = false;
                int id = ((Number)isEquipped.getKey()).intValue();
                Rect r3 = (Rect)isEquipped.getValue();
                if ((WardrobeSet)CollectionsKt.getOrNull(WardrobeState.INSTANCE.getSets(), (int)(id - 1)) == null) continue;
                NVGRenderer.text$default(WardrobeStateKt.displayName(set), r3.getX() + r3.getW() / 2.0f, r3.getY() + r3.getH() - 18.0f, 10.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
                Integer n6 = WardrobeState.INSTANCE.getEquippedSlotId();
                int fp = id;
                if (n6 != null && n6 == fp) {
                    float bw = 52.0f;
                    float bh = 14.0f;
                    float bx = r3.getX() + (r3.getW() - bw) / 2.0f;
                    float by = r3.getY() + 4.0f;
                    NVGRenderer.rect(bx, by, bw, bh, ThemeManager.INSTANCE.getCurrentTheme().getAccent(), 4.0f);
                    NVGRenderer.text$default("Equipped", bx + bw / 2.0f, by + 1.0f, 9.0f, -1, null, 32, null);
                }
                int heartColor = WardrobeState.INSTANCE.getFavorites().contains(id) ? -48060 : -11184811;
                NVGRenderer.text$default("\u2665", r3.getX() + r3.getW() - 14.0f, r3.getY() + 6.0f, 13.0f, heartColor, null, 32, null);
            }
            if (sets.isEmpty()) {
                NVGRenderer.text$default("No sets on this page", sw / 2.0f, sh / 2.0f - 10.0f, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
            }
            NVGRenderer.INSTANCE.endFrame();
            Integer n7 = hoveredId;
            if (n7 == null) break block14;
            int id = ((Number)n7).intValue();
            boolean bl = false;
            WardrobeSet wardrobeSet = (WardrobeSet)CollectionsKt.getOrNull(WardrobeState.INSTANCE.getSets(), (int)(id - 1));
            if (wardrobeSet != null) {
                WardrobeSet set3 = wardrobeSet;
                Rect rect = (Rect)slotRects.get(id);
                if (rect != null) {
                    Rect rect2 = rect;
                    int tooltipX = (int)((rect2.getX() + rect2.getW() + 4.0f) / guiScale);
                    int tooltipY = (int)(rect2.getY() / guiScale);
                    INSTANCE.renderArmorTooltip(graphics, set3.getArmor(), tooltipX, tooltipY, mc);
                }
            }
        }
    }

    private final void renderArmorTooltip(class_332 graphics, List<class_1799> armor, int tooltipX, int tooltipY, class_310 mc) {
        class_1799 class_17992;
        Object v0;
        block4: {
            Iterable $this$firstOrNull$iv = CollectionsKt.filterNotNull((Iterable)armor);
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                class_1799 it = (class_1799)element$iv;
                boolean bl = false;
                if (!(!it.method_7960())) continue;
                v0 = element$iv;
                break block4;
            }
            v0 = null;
        }
        class_1799 class_17993 = class_17992 = (class_1799)v0;
        if (class_17993 == null) {
            return;
        }
        class_1799 stack = class_17993;
        try {
            graphics.method_51446(mc.field_1772, stack, tooltipX, tooltipY);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public final void render(@NotNull class_332 graphics) {
        Intrinsics.checkNotNullParameter((Object)graphics, (String)"graphics");
        this.render(graphics, WardrobeModule.INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0007\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\f\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\b\u0082\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0007\u0010\bJ\u001d\u0010\f\u001a\u00020\u000b2\u0006\u0010\t\u001a\u00020\u00022\u0006\u0010\n\u001a\u00020\u0002\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0010\u0010\u0010\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u000fJ\u0010\u0010\u0011\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u000fJ\u0010\u0010\u0012\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0012\u0010\u000fJ8\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u001b\u0010\u0016\u001a\u00020\u000b2\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0019\u001a\u00020\u0018H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\u0011\u0010\u001c\u001a\u00020\u001bH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001c\u0010\u001dR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001e\u001a\u0004\b\u001f\u0010\u000fR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u001e\u001a\u0004\b \u0010\u000fR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001e\u001a\u0004\b!\u0010\u000fR\u0017\u0010\u0006\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001e\u001a\u0004\b\"\u0010\u000f\u00a8\u0006#"}, d2={"Lorg/cobalt/internal/wardrobe/WardrobeRenderer$Rect;", "", "", "x", "y", "w", "h", "<init>", "(FFFF)V", "px", "py", "", "contains", "(FF)Z", "component1", "()F", "component2", "component3", "component4", "copy", "(FFFF)Lorg/cobalt/internal/wardrobe/WardrobeRenderer$Rect;", "other", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "F", "getX", "getY", "getW", "getH", "cobalt"})
    private static final class Rect {
        private final float x;
        private final float y;
        private final float w;
        private final float h;

        public Rect(float x, float y, float w, float h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        public final float getX() {
            return this.x;
        }

        public final float getY() {
            return this.y;
        }

        public final float getW() {
            return this.w;
        }

        public final float getH() {
            return this.h;
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public final boolean contains(float px, float py) {
            float f = this.x;
            if (!(px <= this.x + this.w)) return false;
            if (!(f <= px)) return false;
            boolean bl = true;
            if (!bl) return false;
            f = this.y;
            if (!(py <= this.y + this.h)) return false;
            if (!(f <= py)) return false;
            return true;
        }

        public final float component1() {
            return this.x;
        }

        public final float component2() {
            return this.y;
        }

        public final float component3() {
            return this.w;
        }

        public final float component4() {
            return this.h;
        }

        @NotNull
        public final Rect copy(float x, float y, float w, float h) {
            return new Rect(x, y, w, h);
        }

        public static /* synthetic */ Rect copy$default(Rect rect, float f, float f2, float f3, float f4, int n, Object object) {
            if ((n & 1) != 0) {
                f = rect.x;
            }
            if ((n & 2) != 0) {
                f2 = rect.y;
            }
            if ((n & 4) != 0) {
                f3 = rect.w;
            }
            if ((n & 8) != 0) {
                f4 = rect.h;
            }
            return rect.copy(f, f2, f3, f4);
        }

        @NotNull
        public String toString() {
            return "Rect(x=" + this.x + ", y=" + this.y + ", w=" + this.w + ", h=" + this.h + ")";
        }

        public int hashCode() {
            int result = Float.hashCode(this.x);
            result = result * 31 + Float.hashCode(this.y);
            result = result * 31 + Float.hashCode(this.w);
            result = result * 31 + Float.hashCode(this.h);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Rect)) {
                return false;
            }
            Rect rect = (Rect)other;
            if (Float.compare(this.x, rect.x) != 0) {
                return false;
            }
            if (Float.compare(this.y, rect.y) != 0) {
                return false;
            }
            if (Float.compare(this.w, rect.w) != 0) {
                return false;
            }
            return Float.compare(this.h, rect.h) == 0;
        }
    }
}

