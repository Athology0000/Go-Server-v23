/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.components.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import org.cobalt.api.ui.theme.Theme;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Image;
import org.cobalt.internal.routes.RoutePickerSetting;
import org.cobalt.internal.routes.RouteStore;
import org.cobalt.internal.routes.SavedRoute;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.cobalt.internal.ui.util.ScrollHandler;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\u0007\n\u0002\b\u0007\b\u0000\u0018\u0000 22\u00020\u0001:\u00012B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000f\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\t\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\t\u0010\bJ\u000f\u0010\n\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\n\u0010\bJ\r\u0010\u000b\u001a\u00020\u0006\u00a2\u0006\u0004\b\u000b\u0010\bJ\u0017\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\fH\u0016\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u001f\u0010\u0014\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u0011H\u0016\u00a2\u0006\u0004\b\u0014\u0010\u0015R\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u0016R\u0016\u0010\u0017\u001a\u00020\u000e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0017\u0010\u0018R\u0014\u0010\u001a\u001a\u00020\u00198\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001a\u0010\u001bR\u001c\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001c8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u001fR\u0014\u0010#\u001a\u00020 8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b!\u0010\"R\u0014\u0010&\u001a\u00020\u000e8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b$\u0010%R\u001a\u0010)\u001a\b\u0012\u0004\u0012\u00020 0\u001c8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b'\u0010(R\u0014\u0010+\u001a\u00020\u000e8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b*\u0010%R\u0014\u0010/\u001a\u00020,8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b-\u0010.R\u0014\u00101\u001a\u00020,8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b0\u0010.\u00a8\u00063"}, d2={"Lorg/cobalt/internal/ui/components/settings/UIRoutePickerSetting;", "Lorg/cobalt/internal/ui/UIComponent;", "Lorg/cobalt/internal/routes/RoutePickerSetting;", "setting", "<init>", "(Lorg/cobalt/internal/routes/RoutePickerSetting;)V", "", "refreshRoutes", "()V", "render", "renderButton", "renderDropdown", "", "button", "", "mouseClicked", "(I)Z", "", "horizontalAmount", "verticalAmount", "mouseScrolled", "(DD)Z", "Lorg/cobalt/internal/routes/RoutePickerSetting;", "isExpanded", "Z", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "scrollHandler", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "", "Lorg/cobalt/internal/routes/SavedRoute;", "cachedRoutes", "Ljava/util/List;", "", "getCurrentName", "()Ljava/lang/String;", "currentName", "getHasValue", "()Z", "hasValue", "getAllOptions", "()Ljava/util/List;", "allOptions", "getNeedsScroll", "needsScroll", "", "getButtonWidth", "()F", "buttonWidth", "getDropdownWidth", "dropdownWidth", "Companion", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUIRoutePickerSetting.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UIRoutePickerSetting.kt\norg/cobalt/internal/ui/components/settings/UIRoutePickerSetting\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,186:1\n1#2:187\n1586#3:188\n1661#3,3:189\n1924#3,3:192\n1924#3,3:195\n*S KotlinDebug\n*F\n+ 1 UIRoutePickerSetting.kt\norg/cobalt/internal/ui/components/settings/UIRoutePickerSetting\n*L\n25#1:188\n25#1:189,3\n99#1:192,3\n149#1:195,3\n*E\n"})
public final class UIRoutePickerSetting
extends UIComponent {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final RoutePickerSetting setting;
    private boolean isExpanded;
    @NotNull
    private final ScrollHandler scrollHandler;
    @NotNull
    private List<SavedRoute> cachedRoutes;
    @NotNull
    private static final Image caretIcon = NVGRenderer.createImage("/assets/cobalt/textures/ui/caret-down.svg");

    public UIRoutePickerSetting(@NotNull RoutePickerSetting setting) {
        Intrinsics.checkNotNullParameter((Object)setting, (String)"setting");
        super(0.0f, 0.0f, 627.5f, 60.0f);
        this.setting = setting;
        this.scrollHandler = new ScrollHandler(0.0f, 1, null);
        this.cachedRoutes = CollectionsKt.emptyList();
    }

    private final String getCurrentName() {
        CharSequence charSequence;
        CharSequence charSequence2 = this.setting.getValue();
        if (StringsKt.isBlank((CharSequence)charSequence2)) {
            boolean bl = false;
            charSequence = "None";
        } else {
            charSequence = charSequence2;
        }
        return (String)charSequence;
    }

    private final boolean getHasValue() {
        return !StringsKt.isBlank((CharSequence)this.setting.getValue());
    }

    /*
     * WARNING - void declaration
     */
    private final List<String> getAllOptions() {
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        Iterable iterable = this.cachedRoutes;
        Collection collection = CollectionsKt.listOf((Object)"None");
        boolean $i$f$map = false;
        void var3_4 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            SavedRoute savedRoute = (SavedRoute)item$iv$iv;
            Collection collection2 = destination$iv$iv;
            boolean bl = false;
            collection2.add(it.getName());
        }
        return CollectionsKt.plus((Collection)collection, (Iterable)((List)destination$iv$iv));
    }

    private final boolean getNeedsScroll() {
        return this.getAllOptions().size() > 6;
    }

    private final float getButtonWidth() {
        return Math.max(NVGRenderer.textWidth$default(this.getCurrentName(), 13.0f, null, 4, null) + 60.0f, 140.0f);
    }

    private final float getDropdownWidth() {
        Float f;
        Iterator iterator = ((Iterable)this.getAllOptions()).iterator();
        if (!iterator.hasNext()) {
            f = null;
        } else {
            String it = (String)iterator.next();
            boolean bl = false;
            float f2 = NVGRenderer.textWidth$default(it, 13.0f, null, 4, null);
            while (iterator.hasNext()) {
                String it2 = (String)iterator.next();
                $i$a$-maxOfOrNull-UIRoutePickerSetting$dropdownWidth$maxW$1 = false;
                float f3 = NVGRenderer.textWidth$default(it2, 13.0f, null, 4, null);
                f2 = Math.max(f2, f3);
            }
            f = Float.valueOf(f2);
        }
        float maxW = f != null ? f.floatValue() : 100.0f;
        return Math.max(maxW + 50.0f + (this.getNeedsScroll() ? 12.0f : 0.0f), 140.0f);
    }

    private final void refreshRoutes() {
        this.cachedRoutes = RouteStore.INSTANCE.listByType(this.setting.getRouteType());
    }

    @Override
    public void render() {
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), theme.getControlBg(), 10.0f);
        NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.0f, theme.getControlBorder(), 10.0f);
        NVGRenderer.text$default(this.setting.getName(), this.getX() + 20.0f, this.getY() + 14.5f, 15.0f, theme.getText(), null, 32, null);
        NVGRenderer.text$default(this.setting.getDescription(), this.getX() + 20.0f, this.getY() + 32.0f, 12.0f, theme.getTextSecondary(), null, 32, null);
        this.renderButton();
    }

    private final void renderButton() {
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        float bw = this.getButtonWidth();
        float bx = this.getX() + this.getWidth() - bw - 20.0f;
        float by = this.getY() + 15.0f;
        boolean hovering = ExtensionsKt.isHoveringOver(bx, by, bw, 30.0f);
        int border = this.isExpanded ? theme.getAccent() : theme.getControlBorder();
        int bg = hovering ? theme.getSelectedOverlay() : theme.getControlBg();
        int textColor = this.getHasValue() ? theme.getText() : theme.getTextSecondary();
        NVGRenderer.rect(bx, by, bw, 30.0f, bg, 5.0f);
        NVGRenderer.hollowRect(bx, by, bw, 30.0f, 2.0f, border, 5.0f);
        NVGRenderer.text$default(this.getCurrentName(), bx + 10.0f, by + 9.0f, 13.0f, textColor, null, 32, null);
        float caretX = bx + bw - 22.5f;
        float caretY = by + 7.0f;
        if (this.isExpanded) {
            NVGRenderer.push();
            NVGRenderer.translate(caretX + 8.0f, caretY + 8.0f);
            NVGRenderer.rotate((float)Math.PI);
            NVGRenderer.image(caretIcon, -8.0f, -8.0f, 16.0f, 16.0f, 0.0f, theme.getTextSecondary());
            NVGRenderer.pop();
        } else {
            NVGRenderer.image(caretIcon, caretX, caretY, 16.0f, 16.0f, 0.0f, theme.getTextSecondary());
        }
    }

    /*
     * WARNING - void declaration
     */
    public final void renderDropdown() {
        if (!this.isExpanded) {
            return;
        }
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        List<String> opts = this.getAllOptions();
        float ddw = this.getDropdownWidth();
        float ddx = this.getX() + this.getWidth() - ddw - 20.0f;
        float ddy = this.getY() + 52.0f;
        int visibleCount = this.getNeedsScroll() ? 6 : opts.size();
        float visibleH = (float)visibleCount * 28.0f + 6.0f;
        float contentH = (float)opts.size() * 28.0f + 6.0f;
        this.scrollHandler.setMaxScroll(contentH, visibleH);
        NVGRenderer.rect(ddx, ddy, ddw, visibleH, theme.getPanel(), 5.0f);
        NVGRenderer.hollowRect(ddx, ddy, ddw, visibleH, 2.0f, theme.getAccent(), 5.0f);
        NVGRenderer.pushScissor(ddx, ddy, ddw, visibleH);
        float offset = this.scrollHandler.getOffset();
        float sbExtraW = this.getNeedsScroll() ? 12.0f : 0.0f;
        Iterable $this$forEachIndexed$iv = opts;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            void option;
            int n;
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            String string = (String)item$iv;
            int i = n;
            boolean bl = false;
            float oy = ddy + 5.0f + (float)i * 28.0f - offset;
            boolean isSelected = Intrinsics.areEqual((Object)option, (Object)this.getCurrentName());
            boolean isHovering = ExtensionsKt.isHoveringOver(ddx + 2.0f, oy, ddw - 4.0f - sbExtraW, 25.0f);
            if (isSelected) {
                NVGRenderer.rect(ddx + 5.0f, oy, ddw - 10.0f - sbExtraW, 25.0f, theme.getSelectedOverlay(), 5.0f);
            } else if (isHovering) {
                NVGRenderer.rect(ddx + 5.0f, oy, ddw - 10.0f - sbExtraW, 25.0f, theme.getControlBg(), 5.0f);
            }
            int tc = isSelected ? theme.getAccent() : theme.getText();
            NVGRenderer.text$default((String)option, ddx + 17.0f, oy + 6.5f, 13.0f, tc, null, 32, null);
        }
        NVGRenderer.popScissor();
        if (this.getNeedsScroll()) {
            float sbx = ddx + ddw - 9.0f;
            float sby = ddy + 3.0f;
            float sbh = visibleH - 6.0f;
            float thumbH = visibleH / contentH * sbh;
            float thumbY = sby + offset / RangesKt.coerceAtLeast((float)this.scrollHandler.getMaxScroll(), (float)1.0f) * (sbh - thumbH);
            NVGRenderer.rect(sbx, thumbY, 4.0f, thumbH, theme.getScrollbarThumb(), 2.0f);
        }
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public boolean mouseClicked(int button) {
        float by;
        if (button != 0) {
            return false;
        }
        float bw = this.getButtonWidth();
        float bx = this.getX() + this.getWidth() - bw - 20.0f;
        if (ExtensionsKt.isHoveringOver(bx, by = this.getY() + 15.0f, bw, 30.0f)) {
            boolean bl = this.isExpanded = !this.isExpanded;
            if (this.isExpanded) {
                this.refreshRoutes();
            } else {
                this.scrollHandler.reset();
            }
            return true;
        }
        if (this.isExpanded) {
            float sbExtraW;
            float ddw = this.getDropdownWidth();
            float ddx = this.getX() + this.getWidth() - ddw - 20.0f;
            float ddy = this.getY() + 52.0f;
            List<String> opts = this.getAllOptions();
            int visibleCount = this.getNeedsScroll() ? 6 : opts.size();
            float visibleH = (float)visibleCount * 28.0f + 6.0f;
            float f = sbExtraW = this.getNeedsScroll() ? 12.0f : 0.0f;
            if (ExtensionsKt.isHoveringOver(ddx, ddy, ddw, visibleH)) {
                float offset = this.scrollHandler.getOffset();
                Iterable $this$forEachIndexed$iv = opts;
                boolean $i$f$forEachIndexed = false;
                int index$iv = 0;
                for (Object item$iv : $this$forEachIndexed$iv) {
                    void option;
                    int n;
                    if ((n = index$iv++) < 0) {
                        CollectionsKt.throwIndexOverflow();
                    }
                    String string = (String)item$iv;
                    int i = n;
                    boolean bl = false;
                    float oy = ddy + 5.0f + (float)i * 28.0f - offset;
                    if (!ExtensionsKt.isHoveringOver(ddx + 2.0f, oy, ddw - 4.0f - sbExtraW, 25.0f)) continue;
                    this.setting.setValue((String)(Intrinsics.areEqual((Object)option, (Object)"None") ? "" : option));
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
        float visibleH;
        float ddy;
        if (!this.isExpanded || !this.getNeedsScroll()) {
            return false;
        }
        float ddw = this.getDropdownWidth();
        float ddx = this.getX() + this.getWidth() - ddw - 20.0f;
        if (ExtensionsKt.isHoveringOver(ddx, ddy = this.getY() + 52.0f, ddw, visibleH = 174.0f)) {
            this.scrollHandler.handleScroll(verticalAmount);
            return true;
        }
        return false;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/internal/ui/components/settings/UIRoutePickerSetting$Companion;", "", "<init>", "()V", "Lorg/cobalt/api/util/ui/helper/Image;", "caretIcon", "Lorg/cobalt/api/util/ui/helper/Image;", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

