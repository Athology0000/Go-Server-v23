/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.ArraysKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.components.settings;

import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import org.cobalt.api.module.setting.impl.ModeSetting;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Image;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.animation.ColorAnimation;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.cobalt.internal.ui.util.ScrollHandler;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b\u0007\b\u0000\u0018\u0000 *2\u00020\u0001:\u0001*B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000f\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\n\u0010\u000bJ\u000f\u0010\f\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\f\u0010\u000bJ\r\u0010\r\u001a\u00020\t\u00a2\u0006\u0004\b\r\u0010\u000bJ\u0017\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u000e\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u001f\u0010\u0015\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u0012H\u0016\u00a2\u0006\u0004\b\u0015\u0010\u0016R\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u0017R\u0014\u0010\u0019\u001a\u00020\u00188\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u001aR\u0016\u0010\u001b\u001a\u00020\u000f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001b\u0010\u001cR\u0016\u0010\u001d\u001a\u00020\u000f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001d\u0010\u001cR\u0014\u0010\u001f\u001a\u00020\u001e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001f\u0010 R\u0014\u0010#\u001a\u00020\u000f8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b!\u0010\"R\u0014\u0010'\u001a\u00020$8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b%\u0010&R\u0014\u0010)\u001a\u00020$8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b(\u0010&\u00a8\u0006+"}, d2={"Lorg/cobalt/internal/ui/components/settings/UIModeSetting;", "Lorg/cobalt/internal/ui/UIComponent;", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "setting", "<init>", "(Lorg/cobalt/api/module/setting/impl/ModeSetting;)V", "", "selectedIndex", "()I", "", "render", "()V", "renderButton", "renderDropdown", "button", "", "mouseClicked", "(I)Z", "", "horizontalAmount", "verticalAmount", "mouseScrolled", "(DD)Z", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "Lorg/cobalt/internal/ui/animation/ColorAnimation;", "colorAnim", "Lorg/cobalt/internal/ui/animation/ColorAnimation;", "wasHovering", "Z", "isExpanded", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "scrollHandler", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "getNeedsScroll", "()Z", "needsScroll", "", "getButtonWidth", "()F", "buttonWidth", "getDropdownWidth", "dropdownWidth", "Companion", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUIModeSetting.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UIModeSetting.kt\norg/cobalt/internal/ui/components/settings/UIModeSetting\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,227:1\n1#2:228\n14125#3,3:229\n14125#3,3:232\n*S KotlinDebug\n*F\n+ 1 UIModeSetting.kt\norg/cobalt/internal/ui/components/settings/UIModeSetting\n*L\n119#1:229,3\n185#1:232,3\n*E\n"})
public final class UIModeSetting
extends UIComponent {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final ModeSetting setting;
    @NotNull
    private final ColorAnimation colorAnim;
    private boolean wasHovering;
    private boolean isExpanded;
    @NotNull
    private final ScrollHandler scrollHandler;
    @NotNull
    private static final Image caretIcon = NVGRenderer.createImage("/assets/cobalt/textures/ui/caret-down.svg");

    public UIModeSetting(@NotNull ModeSetting setting) {
        Intrinsics.checkNotNullParameter((Object)setting, (String)"setting");
        super(0.0f, 0.0f, 627.5f, 60.0f);
        this.setting = setting;
        this.colorAnim = new ColorAnimation(150L);
        this.scrollHandler = new ScrollHandler(0.0f, 1, null);
    }

    private final int selectedIndex() {
        if (this.setting.getOptions().length == 0) {
            return 0;
        }
        int safeIndex = RangesKt.coerceIn((int)((Number)this.setting.getValue()).intValue(), (int)0, (int)ArraysKt.getLastIndex((Object[])this.setting.getOptions()));
        if (((Number)this.setting.getValue()).intValue() != safeIndex) {
            this.setting.setValue(safeIndex);
        }
        return safeIndex;
    }

    private final boolean getNeedsScroll() {
        return this.setting.getOptions().length > 5;
    }

    private final float getButtonWidth() {
        return Math.max(NVGRenderer.textWidth$default(this.setting.getOptions()[this.selectedIndex()], 13.0f, null, 4, null) + 50.0f, 120.0f);
    }

    private final float getDropdownWidth() {
        Float f;
        Object[] objectArray = this.setting.getOptions();
        if (objectArray.length == 0) {
            f = null;
        } else {
            String it = objectArray[0];
            boolean bl = false;
            float f2 = NVGRenderer.textWidth$default(it, 13.0f, null, 4, null);
            int n = 1;
            int n2 = ArraysKt.getLastIndex((Object[])objectArray);
            if (n <= n2) {
                while (true) {
                    Object it2 = objectArray[n];
                    $i$a$-maxOfOrNull-UIModeSetting$dropdownWidth$maxWidth$1 = false;
                    float f3 = NVGRenderer.textWidth$default((String)it2, 13.0f, null, 4, null);
                    f2 = Math.max(f2, f3);
                    if (n == n2) break;
                    ++n;
                }
            }
            f = Float.valueOf(f2);
        }
        float maxWidth = f != null ? f.floatValue() : 100.0f;
        float scrollbarWidth = this.getNeedsScroll() ? 8.0f : 0.0f;
        return Math.max(maxWidth + 50.0f + scrollbarWidth, 120.0f);
    }

    @Override
    public void render() {
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), 10.0f);
        NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 10.0f);
        NVGRenderer.text$default(this.setting.getName(), this.getX() + 20.0f, this.getY() + 14.5f, 15.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
        NVGRenderer.text$default(this.setting.getDescription(), this.getX() + 20.0f, this.getY() + 32.0f, 12.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
        this.renderButton();
    }

    private final void renderButton() {
        float buttonY;
        float currentButtonWidth = this.getButtonWidth();
        float buttonX = this.getX() + this.getWidth() - currentButtonWidth - 20.0f;
        boolean hovering = ExtensionsKt.isHoveringOver(buttonX, buttonY = this.getY() + 15.0f, currentButtonWidth, 30.0f);
        if (hovering != this.wasHovering) {
            this.colorAnim.start();
            this.wasHovering = hovering;
        }
        int bgColor = this.colorAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), ThemeManager.INSTANCE.getCurrentTheme().getSelectedOverlay(), !hovering);
        int borderColor = this.colorAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), ThemeManager.INSTANCE.getCurrentTheme().getAccent(), !hovering);
        int textColor = this.colorAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getText(), ThemeManager.INSTANCE.getCurrentTheme().getTextPrimary(), !hovering);
        NVGRenderer.rect(buttonX, buttonY, currentButtonWidth, 30.0f, bgColor, 5.0f);
        NVGRenderer.hollowRect(buttonX, buttonY, currentButtonWidth, 30.0f, 2.0f, borderColor, 5.0f);
        NVGRenderer.text$default(this.setting.getOptions()[this.selectedIndex()], buttonX + 10.0f, buttonY + 9.0f, 13.0f, textColor, null, 32, null);
        float caretX = buttonX + currentButtonWidth - 22.5f;
        float caretY = buttonY + 7.0f;
        if (this.isExpanded) {
            NVGRenderer.push();
            NVGRenderer.translate(caretX + 8.0f, caretY + 8.0f);
            NVGRenderer.rotate((float)Math.PI);
            NVGRenderer.image(caretIcon, -8.0f, -8.0f, 16.0f, 16.0f, 0.0f, textColor);
            NVGRenderer.pop();
        } else {
            NVGRenderer.image(caretIcon, caretX, caretY, 16.0f, 16.0f, 0.0f, textColor);
        }
    }

    /*
     * WARNING - void declaration
     */
    public final void renderDropdown() {
        if (!this.isExpanded) {
            return;
        }
        float currentDropdownWidth = this.getDropdownWidth();
        float dropdownX = this.getX() + this.getWidth() - currentDropdownWidth - 20.0f;
        float dropdownY = this.getY() + 52.0f;
        int visibleOptions = this.getNeedsScroll() ? 5 : this.setting.getOptions().length;
        float visibleHeight = (float)visibleOptions * 28.0f + 6.0f;
        float contentHeight = (float)this.setting.getOptions().length * 28.0f + 6.0f;
        this.scrollHandler.setMaxScroll(contentHeight, visibleHeight);
        NVGRenderer.rect(dropdownX, dropdownY, currentDropdownWidth, visibleHeight, ThemeManager.INSTANCE.getCurrentTheme().getPanel(), 5.0f);
        NVGRenderer.hollowRect(dropdownX, dropdownY, currentDropdownWidth, visibleHeight, 2.0f, ThemeManager.INSTANCE.getCurrentTheme().getAccent(), 5.0f);
        NVGRenderer.pushScissor(dropdownX, dropdownY, currentDropdownWidth, visibleHeight);
        float scrollOffset = this.scrollHandler.getOffset();
        String[] $this$forEachIndexed$iv = this.setting.getOptions();
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (String item$iv : $this$forEachIndexed$iv) {
            void option;
            int n = index$iv++;
            String string = item$iv;
            int index = n;
            boolean bl = false;
            float optionY = dropdownY + 5.0f + (float)index * 28.0f - scrollOffset;
            boolean isSelected = index == this.selectedIndex();
            boolean isHovering = ExtensionsKt.isHoveringOver(dropdownX + 2.0f, optionY, currentDropdownWidth - 4.0f - (this.getNeedsScroll() ? 8.0f : 0.0f), 25.0f);
            if (isSelected) {
                NVGRenderer.rect(dropdownX + 5.0f, optionY, currentDropdownWidth - 10.0f - (this.getNeedsScroll() ? 8.0f : 0.0f), 25.0f, ThemeManager.INSTANCE.getCurrentTheme().getSelectedOverlay(), 5.0f);
            } else if (isHovering) {
                NVGRenderer.rect(dropdownX + 5.0f, optionY, currentDropdownWidth - 10.0f - (this.getNeedsScroll() ? 8.0f : 0.0f), 25.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), 5.0f);
            }
            int textColor = isSelected ? ThemeManager.INSTANCE.getCurrentTheme().getAccent() : ThemeManager.INSTANCE.getCurrentTheme().getText();
            NVGRenderer.text$default((String)option, dropdownX + 17.0f, optionY + 6.5f, 13.0f, textColor, null, 32, null);
        }
        NVGRenderer.popScissor();
        if (this.getNeedsScroll()) {
            float scrollbarX = dropdownX + currentDropdownWidth - 9.0f;
            float scrollbarY = dropdownY + 3.0f;
            float scrollbarHeight = visibleHeight - 6.0f;
            float thumbHeight = visibleHeight / contentHeight * scrollbarHeight;
            float thumbY = scrollbarY + scrollOffset / this.scrollHandler.getMaxScroll() * (scrollbarHeight - thumbHeight);
            NVGRenderer.rect(scrollbarX, thumbY, 4.0f, thumbHeight, ThemeManager.INSTANCE.getCurrentTheme().getScrollbarThumb(), 2.0f);
        }
    }

    @Override
    public boolean mouseClicked(int button) {
        float buttonY;
        if (button != 0) {
            return false;
        }
        float currentButtonWidth = this.getButtonWidth();
        float buttonX = this.getX() + this.getWidth() - currentButtonWidth - 20.0f;
        if (ExtensionsKt.isHoveringOver(buttonX, buttonY = this.getY() + 15.0f, currentButtonWidth, 30.0f)) {
            boolean bl = this.isExpanded = !this.isExpanded;
            if (!this.isExpanded) {
                this.scrollHandler.reset();
            }
            return true;
        }
        if (this.isExpanded) {
            int visibleOptions;
            float visibleHeight;
            float dropdownY;
            float currentDropdownWidth = this.getDropdownWidth();
            float dropdownX = this.getX() + this.getWidth() - currentDropdownWidth - 20.0f;
            if (ExtensionsKt.isHoveringOver(dropdownX, dropdownY = this.getY() + 52.0f, currentDropdownWidth, visibleHeight = (float)(visibleOptions = this.getNeedsScroll() ? 5 : this.setting.getOptions().length) * 28.0f + 6.0f)) {
                float scrollOffset = this.scrollHandler.getOffset();
                String[] $this$forEachIndexed$iv = this.setting.getOptions();
                boolean $i$f$forEachIndexed = false;
                int index$iv = 0;
                for (String item$iv : $this$forEachIndexed$iv) {
                    int index = index$iv++;
                    boolean bl = false;
                    float optionY = dropdownY + 5.0f + (float)index * 28.0f - scrollOffset;
                    if (!ExtensionsKt.isHoveringOver(dropdownX + 2.0f, optionY, currentDropdownWidth - 4.0f - (this.getNeedsScroll() ? 8.0f : 0.0f), 25.0f)) continue;
                    this.setting.setValue(index);
                    this.isExpanded = false;
                    this.scrollHandler.reset();
                    return true;
                }
                return true;
            }
            this.isExpanded = false;
            this.scrollHandler.reset();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double horizontalAmount, double verticalAmount) {
        int visibleOptions;
        float visibleHeight;
        float dropdownY;
        if (!this.isExpanded || !this.getNeedsScroll()) {
            return false;
        }
        float currentDropdownWidth = this.getDropdownWidth();
        float dropdownX = this.getX() + this.getWidth() - currentDropdownWidth - 20.0f;
        if (ExtensionsKt.isHoveringOver(dropdownX, dropdownY = this.getY() + 52.0f, currentDropdownWidth, visibleHeight = (float)(visibleOptions = this.getNeedsScroll() ? 5 : this.setting.getOptions().length) * 28.0f + 6.0f)) {
            this.scrollHandler.handleScroll(verticalAmount);
            return true;
        }
        return false;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/internal/ui/components/settings/UIModeSetting$Companion;", "", "<init>", "()V", "Lorg/cobalt/api/util/ui/helper/Image;", "caretIcon", "Lorg/cobalt/api/util/ui/helper/Image;", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

