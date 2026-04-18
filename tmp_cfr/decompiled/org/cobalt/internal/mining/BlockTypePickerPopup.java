/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.ArraysKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_310
 *  net.minecraft.class_315
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.mining;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_310;
import net.minecraft.class_315;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.MouseEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.NvgEvent;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.ui.theme.Theme;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.MouseUtils;
import org.cobalt.api.util.player.MovementManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Gradient;
import org.cobalt.internal.helper.Config;
import org.cobalt.internal.mining.MiningBlockRegistry;
import org.cobalt.internal.mining.MiningMacroModule;
import org.cobalt.internal.mining.MiningModule;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u000f\b\u00c0\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u0017\u0010\b\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006H\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\u0017\u0010\u000b\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\nH\u0007\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0017\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\rH\u0007\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0017\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0010H\u0007\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u000f\u0010\u0013\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u0013\u0010\u0003J\u000f\u0010\u0014\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u0014\u0010\u0003J\u0017\u0010\u0017\u001a\u00020\u00042\u0006\u0010\u0016\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b\u0017\u0010\u0018R\u0014\u0010\u001a\u001a\u00020\u00198\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001a\u0010\u001bR$\u0010\u001d\u001a\u00020\u00152\u0006\u0010\u001c\u001a\u00020\u00158\u0006@BX\u0086\u000e\u00a2\u0006\f\n\u0004\b\u001d\u0010\u001e\u001a\u0004\b\u001f\u0010 R$\u0010$\u001a\u0012\u0012\u0004\u0012\u00020\"0!j\b\u0012\u0004\u0012\u00020\"`#8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b$\u0010%R\u0014\u0010'\u001a\u00020&8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b'\u0010(R\u0014\u0010)\u001a\u00020&8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b)\u0010(R\u0014\u0010*\u001a\u00020&8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b*\u0010(R\u0014\u0010,\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b,\u0010-R\u0014\u0010.\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b.\u0010-R\u0014\u0010/\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b/\u0010-R\u0014\u00100\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b0\u0010-R\u0014\u00101\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b1\u0010-R\u0014\u00102\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b2\u0010-R\u0014\u00103\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b3\u0010-R\u0014\u00104\u001a\u00020&8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b4\u0010(R\u0014\u00107\u001a\u00020+8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b5\u00106R\u0016\u00108\u001a\u00020+8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b8\u0010-R\u0016\u00109\u001a\u00020+8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b9\u0010-\u00a8\u0006:"}, d2={"Lorg/cobalt/internal/mining/BlockTypePickerPopup;", "", "<init>", "()V", "", "open", "Lorg/cobalt/api/event/impl/render/NvgEvent;", "event", "onRender", "(Lorg/cobalt/api/event/impl/render/NvgEvent;)V", "Lorg/cobalt/api/event/impl/client/MouseEvent$LeftClick;", "onMouseLeft", "(Lorg/cobalt/api/event/impl/client/MouseEvent$LeftClick;)V", "Lorg/cobalt/api/event/impl/client/MouseEvent$RightClick;", "onMouseRight", "(Lorg/cobalt/api/event/impl/client/MouseEvent$RightClick;)V", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "commit", "close", "", "on", "lockPlayer", "(Z)V", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "value", "visible", "Z", "getVisible", "()Z", "Ljava/util/LinkedHashSet;", "", "Lkotlin/collections/LinkedHashSet;", "selectedTypes", "Ljava/util/LinkedHashSet;", "", "OUTLINE_START", "I", "OUTLINE_END", "PANEL_COLOR", "", "PANEL_W", "F", "RADIUS", "PAD", "TITLE_H", "FOOTER_H", "ITEM_H", "ITEM_GAP", "COLS", "getPANEL_H", "()F", "PANEL_H", "panelX", "panelY", "cobalt"})
@SourceDebugExtension(value={"SMAP\nBlockTypePickerPopup.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BlockTypePickerPopup.kt\norg/cobalt/internal/mining/BlockTypePickerPopup\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 4 Extensions.kt\norg/cobalt/internal/ui/util/ExtensionsKt\n+ 5 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,270:1\n1915#2,2:271\n1924#2,3:276\n1924#2,3:284\n1642#2,10:287\n1915#2:297\n1916#2:299\n1652#2:300\n1807#2,3:301\n3938#3:273\n4474#3,2:274\n3938#3:281\n4474#3,2:282\n6#4:279\n9#4:280\n1#5:298\n*S KotlinDebug\n*F\n+ 1 BlockTypePickerPopup.kt\norg/cobalt/internal/mining/BlockTypePickerPopup\n*L\n54#1:271,2\n116#1:276,3\n190#1:284,3\n241#1:287,10\n241#1:297\n241#1:299\n241#1:300\n246#1:301,3\n72#1:273\n72#1:274,2\n184#1:281\n184#1:282,2\n181#1:279\n182#1:280\n241#1:298\n*E\n"})
public final class BlockTypePickerPopup {
    @NotNull
    public static final BlockTypePickerPopup INSTANCE = new BlockTypePickerPopup();
    @NotNull
    private static final class_310 mc;
    private static boolean visible;
    @NotNull
    private static final LinkedHashSet<String> selectedTypes;
    private static final int OUTLINE_START;
    private static final int OUTLINE_END;
    private static final int PANEL_COLOR;
    private static final float PANEL_W = 560.0f;
    private static final float RADIUS = 10.0f;
    private static final float PAD = 16.0f;
    private static final float TITLE_H = 40.0f;
    private static final float FOOTER_H = 44.0f;
    private static final float ITEM_H = 28.0f;
    private static final float ITEM_GAP = 4.0f;
    private static final int COLS = 3;
    private static float panelX;
    private static float panelY;

    private BlockTypePickerPopup() {
    }

    public final boolean getVisible() {
        return visible;
    }

    private final float getPANEL_H() {
        int rows = (MiningBlockRegistry.INSTANCE.getBLOCK_TYPES().length + 3 - 1) / 3;
        return 56.0f + (float)rows * 28.0f + (float)(rows - 1) * 4.0f + 16.0f + 44.0f;
    }

    public final void open() {
        if (BlockTypePickerPopup.mc.field_1755 != null) {
            mc.method_1507(null);
        }
        selectedTypes.clear();
        Iterable $this$forEach$iv = MiningMacroModule.INSTANCE.getSelectedTypesInOrder();
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            String it = (String)element$iv;
            boolean bl = false;
            selectedTypes.add(it);
        }
        visible = true;
        this.lockPlayer(true);
        MouseUtils.ungrabMouse();
    }

    /*
     * WARNING - void declaration
     */
    @SubscribeEvent
    public final void onRender(@NotNull NvgEvent event) {
        void $this$filterTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!visible) {
            return;
        }
        if (BlockTypePickerPopup.mc.field_1755 != null) {
            visible = false;
            this.lockPlayer(false);
            return;
        }
        float sw = mc.method_22683().method_4480();
        float sh = mc.method_22683().method_4507();
        float ph = this.getPANEL_H();
        panelX = sw / 2.0f - 280.0f;
        panelY = sh / 2.0f - ph / 2.0f;
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        String[] $this$filter$iv = MiningBlockRegistry.INSTANCE.getBLOCK_TYPES();
        boolean $i$f$filter = false;
        String[] stringArray = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        int n = ((void)$this$filterTo$iv$iv).length;
        for (int i = 0; i < n; ++i) {
            void element$iv$iv;
            void it = element$iv$iv = $this$filterTo$iv$iv[i];
            boolean bl = false;
            if (!(!Intrinsics.areEqual((Object)it, (Object)"Custom"))) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        List types = (List)destination$iv$iv;
        float angle = (float)(System.currentTimeMillis() % 12000L) / 12000.0f * ((float)Math.PI * 2);
        float shiftX = (float)Math.cos(angle) * 252.0f;
        float shiftY = (float)Math.sin(angle) * (ph * 0.45f);
        NVGRenderer.INSTANCE.beginFrame(sw, sh);
        NVGRenderer.rect(0.0f, 0.0f, sw, sh, new Color(0, 0, 0, 150).getRGB());
        NVGRenderer.rect(panelX, panelY, 560.0f, ph, PANEL_COLOR, 10.0f);
        NVGRenderer.hollowGradientRectShifted(panelX, panelY, 560.0f, ph, 5.0f, OUTLINE_START & 0xFFFFFF | 0x45000000, OUTLINE_END & 0xFFFFFF | 0x45000000, Gradient.LeftToRight, 10.0f, shiftX, shiftY);
        NVGRenderer.hollowGradientRectShifted(panelX, panelY, 560.0f, ph, 1.5f, OUTLINE_START, OUTLINE_END, Gradient.LeftToRight, 10.0f, shiftX, shiftY);
        NVGRenderer.text$default("Block Types", panelX + 16.0f, panelY + 13.0f, 15.0f, -1, null, 32, null);
        String selLabel = selectedTypes.isEmpty() ? "None" : selectedTypes.size() + " selected";
        float selW = NVGRenderer.textWidth$default(selLabel, 11.0f, null, 4, null);
        NVGRenderer.text$default(selLabel, panelX + 560.0f - selW - 16.0f, panelY + 14.0f, 11.0f, -1426063361, null, 32, null);
        NVGRenderer.line(panelX + 16.0f, panelY + 40.0f, panelX + 560.0f - 16.0f, panelY + 40.0f, 1.0f, theme.getControlBorder());
        float contentX = panelX + 16.0f;
        float contentY = panelY + 40.0f + 16.0f;
        float colW = 170.66667f;
        Iterable $this$forEachIndexed$iv = types;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            void typeName;
            int n2;
            if ((n2 = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            String string = (String)item$iv;
            int i = n2;
            boolean bl = false;
            int col = i % 3;
            int row = i / 3;
            float ix = contentX + (float)col * (colW + 8.0f);
            float iy = contentY + (float)row * 32.0f;
            boolean selected = selectedTypes.contains(typeName);
            boolean hovering = ExtensionsKt.isHoveringOver(ix, iy, colW, 28.0f);
            NVGRenderer.rect(ix, iy, colW, 28.0f, hovering ? theme.getSelectedOverlay() : theme.getControlBg(), 6.0f);
            if (selected) {
                NVGRenderer.hollowGradientRectShifted(ix, iy, colW, 28.0f, 1.0f, OUTLINE_START, OUTLINE_END, Gradient.LeftToRight, 6.0f, shiftX, shiftY);
            } else {
                NVGRenderer.hollowRect(ix, iy, colW, 28.0f, 1.0f, theme.getControlBorder(), 6.0f);
            }
            float cbS = 12.0f;
            float cbX = ix + 7.0f;
            float cbY = iy + (28.0f - cbS) / 2.0f;
            if (selected) {
                NVGRenderer.gradientRect(cbX, cbY, cbS, cbS, OUTLINE_START, OUTLINE_END, Gradient.LeftToRight, 3.0f);
            } else {
                NVGRenderer.rect(cbX, cbY, cbS, cbS, theme.getControlBg(), 3.0f);
                NVGRenderer.hollowRect(cbX, cbY, cbS, cbS, 1.0f, theme.getControlBorder(), 3.0f);
            }
            if (selected) {
                NVGRenderer.text$default("\u2713", cbX + 1.0f, cbY + 1.0f, 9.0f, -1, null, 32, null);
            }
            NVGRenderer.text$default((String)typeName, cbX + cbS + 5.0f, iy + 9.0f, 10.0f, selected ? -1 : theme.getText(), null, 32, null);
        }
        float divY = panelY + ph - 44.0f;
        NVGRenderer.line(panelX + 16.0f, divY, panelX + 560.0f - 16.0f, divY, 1.0f, theme.getControlBorder());
        float btnW = 120.0f;
        float btnH = 28.0f;
        float btnX = panelX + 280.0f - btnW / 2.0f;
        float btnY = divY + (44.0f - btnH) / 2.0f;
        boolean btnHover = ExtensionsKt.isHoveringOver(btnX, btnY, btnW, btnH);
        int btnAlpha = btnHover ? 255 : 204;
        NVGRenderer.gradientRect(btnX, btnY, btnW, btnH, btnAlpha << 24 | OUTLINE_START & 0xFFFFFF, btnAlpha << 24 | OUTLINE_END & 0xFFFFFF, Gradient.LeftToRight, 6.0f);
        float doneW = NVGRenderer.textWidth$default("Done", 12.0f, null, 4, null);
        NVGRenderer.text$default("Done", btnX + btnW / 2.0f - doneW / 2.0f, btnY + 8.0f, 12.0f, -1, null, 32, null);
        NVGRenderer.INSTANCE.endFrame();
    }

    /*
     * WARNING - void declaration
     */
    @SubscribeEvent
    public final void onMouseLeft(@NotNull MouseEvent.LeftClick event) {
        void $this$filterTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!visible) {
            return;
        }
        event.setCancelled(true);
        boolean $i$f$getMouseX = false;
        float mx = (float)class_310.method_1551().field_1729.method_1603();
        boolean $i$f$getMouseY = false;
        float my = (float)class_310.method_1551().field_1729.method_1604();
        float ph = this.getPANEL_H();
        String[] $this$filter$iv = MiningBlockRegistry.INSTANCE.getBLOCK_TYPES();
        boolean $i$f$filter = false;
        String[] stringArray = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        int n = ((void)$this$filterTo$iv$iv).length;
        for (int i = 0; i < n; ++i) {
            void element$iv$iv;
            void it = element$iv$iv = $this$filterTo$iv$iv[i];
            boolean bl = false;
            if (!(!Intrinsics.areEqual((Object)it, (Object)"Custom"))) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        List types = (List)destination$iv$iv;
        float contentX = panelX + 16.0f;
        float contentY = panelY + 40.0f + 16.0f;
        float colW = 170.66667f;
        Iterable $this$forEachIndexed$iv = types;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            void typeName;
            int n2;
            if ((n2 = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            String bl = (String)item$iv;
            int i = n2;
            boolean bl2 = false;
            int col = i % 3;
            int row = i / 3;
            float ix = contentX + (float)col * (colW + 8.0f);
            float iy = contentY + (float)row * 32.0f;
            if (!(mx >= ix) || !(mx <= ix + colW) || !(my >= iy) || !(my <= iy + 28.0f)) continue;
            boolean bl3 = selectedTypes.contains(typeName) ? selectedTypes.remove(typeName) : selectedTypes.add((String)typeName);
            return;
        }
        float divY = panelY + ph - 44.0f;
        float btnW = 120.0f;
        float btnH = 28.0f;
        float btnX = panelX + 280.0f - btnW / 2.0f;
        float btnY = divY + (44.0f - btnH) / 2.0f;
        if (mx >= btnX && mx <= btnX + btnW && my >= btnY && my <= btnY + btnH) {
            this.commit();
            return;
        }
        if (!ExtensionsKt.isHoveringOver(panelX, panelY, 560.0f, ph)) {
            this.commit();
        }
    }

    @SubscribeEvent
    public final void onMouseRight(@NotNull MouseEvent.RightClick event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!visible) {
            return;
        }
        event.setCancelled(true);
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!visible) {
            return;
        }
        if (BlockTypePickerPopup.mc.field_1755 != null) {
            visible = false;
            this.lockPlayer(false);
            return;
        }
        this.lockPlayer(true);
    }

    /*
     * WARNING - void declaration
     */
    private final void commit() {
        boolean bl;
        CheckboxSetting checkboxSetting;
        block6: {
            void $this$any$iv;
            void $this$mapNotNullTo$iv$iv;
            int idx;
            MiningMacroModule.INSTANCE.getBlockTypes$cobalt().setValue(selectedTypes.isEmpty() ? "None" : CollectionsKt.joinToString$default((Iterable)selectedTypes, (CharSequence)", ", null, null, (int)0, null, null, (int)62, null));
            String firstSelected = (String)CollectionsKt.firstOrNull((Iterable)selectedTypes);
            if (firstSelected != null && (idx = ArraysKt.indexOf((Object[])MiningBlockRegistry.INSTANCE.getBLOCK_TYPES(), (Object)firstSelected)) >= 0) {
                MiningModule.INSTANCE.getBlockType().setValue(idx);
            }
            Iterable $this$mapNotNull$iv = selectedTypes;
            boolean $i$f$mapNotNull = false;
            Iterable iterable = $this$mapNotNull$iv;
            Collection destination$iv$iv = new ArrayList();
            boolean $i$f$mapNotNullTo = false;
            void $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
            boolean $i$f$forEach = false;
            Iterator iterator = $this$forEach$iv$iv$iv.iterator();
            while (iterator.hasNext()) {
                Double it$iv$iv;
                Object element$iv$iv$iv;
                Object element$iv$iv = element$iv$iv$iv = iterator.next();
                boolean bl2 = false;
                String it = (String)element$iv$iv;
                boolean bl3 = false;
                if (MiningBlockRegistry.INSTANCE.getBLOCK_HARDNESS().get(it) == null) continue;
                boolean bl4 = false;
                destination$iv$iv.add(it$iv$iv);
            }
            List hardnesses = (List)destination$iv$iv;
            if (!((Collection)hardnesses).isEmpty()) {
                MiningModule.INSTANCE.getBlockStrength().setValue(CollectionsKt.maxOrThrow((Iterable)hardnesses));
            }
            $this$mapNotNull$iv = selectedTypes;
            checkboxSetting = MiningModule.INSTANCE.getMiningUmberTungsten();
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                bl = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    String it = (String)element$iv;
                    boolean bl5 = false;
                    if (!(Intrinsics.areEqual((Object)it, (Object)"Umber") || Intrinsics.areEqual((Object)it, (Object)"Tungsten"))) continue;
                    bl = true;
                    break block6;
                }
                bl = false;
            }
        }
        boolean bl6 = bl;
        checkboxSetting.setValue(bl6);
        Config.INSTANCE.saveModulesConfig();
        this.close();
    }

    private final void close() {
        visible = false;
        this.lockPlayer(false);
        if (BlockTypePickerPopup.mc.field_1755 == null) {
            MouseUtils.grabMouse();
        }
    }

    private final void lockPlayer(boolean on) {
        MovementManager.setLookLock(on);
        MovementManager.setMovementLock(on);
        if (!on) {
            return;
        }
        class_315 $this$lockPlayer_u24lambda_u240 = BlockTypePickerPopup.mc.field_1690;
        boolean bl = false;
        $this$lockPlayer_u24lambda_u240.field_1894.method_23481(false);
        $this$lockPlayer_u24lambda_u240.field_1881.method_23481(false);
        $this$lockPlayer_u24lambda_u240.field_1913.method_23481(false);
        $this$lockPlayer_u24lambda_u240.field_1849.method_23481(false);
        $this$lockPlayer_u24lambda_u240.field_1903.method_23481(false);
        $this$lockPlayer_u24lambda_u240.field_1832.method_23481(false);
        $this$lockPlayer_u24lambda_u240.field_1867.method_23481(false);
        $this$lockPlayer_u24lambda_u240.field_1886.method_23481(false);
        $this$lockPlayer_u24lambda_u240.field_1904.method_23481(false);
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        selectedTypes = new LinkedHashSet();
        OUTLINE_START = -13769985;
        OUTLINE_END = -38195;
        PANEL_COLOR = -267382760;
    }
}

