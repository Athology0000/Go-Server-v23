/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.ui.panel.panels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import org.cobalt.api.ui.theme.Theme;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.routes.RouteEditMode;
import org.cobalt.internal.routes.RoutePoint;
import org.cobalt.internal.routes.RouteStore;
import org.cobalt.internal.routes.RouteType;
import org.cobalt.internal.routes.RouteTypeKt;
import org.cobalt.internal.routes.SavedRoute;
import org.cobalt.internal.routes.SubRouteKey;
import org.cobalt.internal.ui.components.UITopbar;
import org.cobalt.internal.ui.panel.UIPanel;
import org.cobalt.internal.ui.panel.panels.UINewRouteModal;
import org.cobalt.internal.ui.screen.UIConfig;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.cobalt.internal.ui.util.ScrollHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0086\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010#\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0000\u0018\u0000 L2\u00020\u0001:\u0004MNOLB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u000f\u0010\u0006\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\u0006\u0010\u0003J\u0017\u0010\t\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u0007H\u0002\u00a2\u0006\u0004\b\t\u0010\nJQ\u0010\u0018\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u000e2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00160\u0013H\u0002\u00a2\u0006\u0004\b\u0018\u0010\u0019JE\u0010\u001e\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\u001a\u001a\u00020\u00112\u0006\u0010\u001b\u001a\u00020\u00072\u0006\u0010\u001c\u001a\u00020\u00072\u0006\u0010\u001d\u001a\u00020\u00072\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00160\u0013H\u0002\u00a2\u0006\u0004\b\u001e\u0010\u001fJ\u001f\u0010\"\u001a\u00020\u00042\u0006\u0010 \u001a\u00020\u00072\u0006\u0010!\u001a\u00020\u0007H\u0002\u00a2\u0006\u0004\b\"\u0010#J\u0017\u0010&\u001a\u00020\u000e2\u0006\u0010%\u001a\u00020$H\u0016\u00a2\u0006\u0004\b&\u0010'J\u001f\u0010+\u001a\u00020\u000e2\u0006\u0010)\u001a\u00020(2\u0006\u0010*\u001a\u00020(H\u0016\u00a2\u0006\u0004\b+\u0010,J\u000f\u0010-\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b-\u0010\u0003J\u000f\u0010.\u001a\u00020\u0007H\u0002\u00a2\u0006\u0004\b.\u0010/J\u0017\u00101\u001a\u0002002\u0006\u0010\f\u001a\u00020\u000bH\u0002\u00a2\u0006\u0004\b1\u00102J'\u00106\u001a\u0002002\u0006\u00103\u001a\u0002002\u0006\u00104\u001a\u00020\u00072\u0006\u00105\u001a\u00020\u0007H\u0002\u00a2\u0006\u0004\b6\u00107R\u0014\u00109\u001a\u0002088\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b9\u0010:R\u001c\u0010;\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00108\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b;\u0010<R\u001c\u0010=\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00108\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b=\u0010<R\u001a\u0010?\u001a\b\u0012\u0004\u0012\u0002000>8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b?\u0010@R\u0018\u0010B\u001a\u0004\u0018\u00010A8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bB\u0010CR\u0016\u0010D\u001a\u0002008\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bD\u0010ER\u0018\u0010F\u001a\u0004\u0018\u0001008\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bF\u0010ER\u0014\u0010H\u001a\u00020G8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bH\u0010IR\u001c\u0010K\u001a\b\u0012\u0004\u0012\u00020J0\u00108\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bK\u0010<R\u001c\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00140\u00108\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0015\u0010<R\u001c\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00160\u00108\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0017\u0010<\u00a8\u0006P"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIRoutesPanel;", "Lorg/cobalt/internal/ui/panel/UIPanel;", "<init>", "()V", "", "refresh", "render", "", "stripY", "renderTypeFilterStrip", "(F)V", "Lorg/cobalt/internal/routes/SavedRoute;", "route", "cardY", "", "expanded", "", "Lorg/cobalt/internal/routes/SubRouteKey;", "subs", "", "Lorg/cobalt/internal/ui/panel/panels/UIRoutesPanel$CardArea;", "cardAreas", "Lorg/cobalt/internal/ui/panel/panels/UIRoutesPanel$SubRowArea;", "subRowAreas", "renderCard", "(Lorg/cobalt/internal/routes/SavedRoute;FZLjava/util/List;Ljava/util/List;Ljava/util/List;)V", "sub", "cardX", "subY", "cardW", "renderSubRow", "(Lorg/cobalt/internal/routes/SavedRoute;Lorg/cobalt/internal/routes/SubRouteKey;FFFLjava/util/List;)V", "ex", "ey", "renderEmptyState", "(FF)V", "", "button", "mouseClicked", "(I)Z", "", "horizontalAmount", "verticalAmount", "mouseScrolled", "(DD)Z", "applyFilter", "measureTotalCardsHeight", "()F", "", "buildPointSummary", "(Lorg/cobalt/internal/routes/SavedRoute;)Ljava/lang/String;", "text", "maxWidth", "size", "ellipsize", "(Ljava/lang/String;FF)Ljava/lang/String;", "Lorg/cobalt/internal/ui/components/UITopbar;", "topBar", "Lorg/cobalt/internal/ui/components/UITopbar;", "allRoutes", "Ljava/util/List;", "filteredRoutes", "", "expandedRoutes", "Ljava/util/Set;", "Lorg/cobalt/internal/routes/RouteType;", "activeTypeFilter", "Lorg/cobalt/internal/routes/RouteType;", "searchText", "Ljava/lang/String;", "pendingDelete", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "scroll", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "Lorg/cobalt/internal/ui/panel/panels/UIRoutesPanel$TabHitbox;", "tabHitboxes", "Companion", "TabHitbox", "CardArea", "SubRowArea", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUIRoutesPanel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UIRoutesPanel.kt\norg/cobalt/internal/ui/panel/panels/UIRoutesPanel\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,489:1\n296#2,2:490\n296#2,2:492\n777#2:494\n873#2,2:495\n1#3:497\n*S KotlinDebug\n*F\n+ 1 UIRoutesPanel.kt\norg/cobalt/internal/ui/panel/panels/UIRoutesPanel\n*L\n363#1:490,2\n384#1:492,2\n427#1:494\n427#1:495,2\n*E\n"})
public final class UIRoutesPanel
extends UIPanel {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final UITopbar topBar = new UITopbar("Routes");
    @NotNull
    private List<SavedRoute> allRoutes = CollectionsKt.emptyList();
    @NotNull
    private List<SavedRoute> filteredRoutes = CollectionsKt.emptyList();
    @NotNull
    private final Set<String> expandedRoutes = new LinkedHashSet();
    @Nullable
    private RouteType activeTypeFilter;
    @NotNull
    private String searchText = "";
    @Nullable
    private String pendingDelete;
    @NotNull
    private final ScrollHandler scroll = new ScrollHandler(0.0f, 1, null);
    @NotNull
    private List<TabHitbox> tabHitboxes = CollectionsKt.emptyList();
    @NotNull
    private List<CardArea> cardAreas = CollectionsKt.emptyList();
    @NotNull
    private List<SubRowArea> subRowAreas = CollectionsKt.emptyList();
    private static final float PAD_H = 16.0f;
    private static final float PAD_V = 12.0f;
    private static final float TAB_STRIP_H = 46.0f;
    private static final float TAB_H = 28.0f;
    private static final float TAB_PAD_H = 12.0f;
    private static final float TAB_GAP = 6.0f;
    private static final float CARD_HEADER_H = 56.0f;
    private static final float CARD_GAP = 8.0f;
    private static final float BTN_H = 26.0f;
    private static final float BTN_H_SUB = 22.0f;
    private static final float SUB_ROW_H = 44.0f;
    private static final float SUB_SECTION_PAD = 8.0f;
    private static final float EMPTY_STATE_H = 80.0f;

    public UIRoutesPanel() {
        super(0.0f, 0.0f, 890.0f, 600.0f);
        this.refresh();
        this.getComponents().add(this.topBar);
        this.topBar.searchChanged((Function1<? super String, Unit>)((Function1)arg_0 -> UIRoutesPanel._init_$lambda$0(this, arg_0)));
    }

    public final void refresh() {
        this.allRoutes = RouteStore.INSTANCE.loadAll();
        this.applyFilter();
    }

    @Override
    public void render() {
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), theme.getBackground(), 10.0f);
        this.topBar.updateBounds(this.getX(), this.getY()).render();
        float tabStripY = this.getY() + this.topBar.getHeight();
        this.renderTypeFilterStrip(tabStripY);
        float contentY = tabStripY + 46.0f;
        float visibleH = this.getHeight() - (contentY - this.getY());
        float totalH = this.measureTotalCardsHeight();
        this.scroll.setMaxScroll(totalH + 12.0f, visibleH);
        NVGRenderer.pushScissor(this.getX(), contentY, this.getWidth(), visibleH);
        float scrollOffset = this.scroll.getOffset();
        List newCardAreas = new ArrayList();
        List newSubRowAreas = new ArrayList();
        float curY = contentY + 12.0f - scrollOffset;
        if (this.filteredRoutes.isEmpty()) {
            this.renderEmptyState(this.getX() + 16.0f, curY);
        } else {
            for (SavedRoute route : this.filteredRoutes) {
                boolean expanded = this.expandedRoutes.contains(route.getName());
                List<SubRouteKey> subs = expanded ? RouteTypeKt.subRoutesFor(route.getType()) : CollectionsKt.emptyList();
                float cardH = 56.0f + (expanded ? (float)subs.size() * 44.0f + 8.0f : 0.0f);
                if (curY + cardH >= contentY && curY <= contentY + visibleH) {
                    this.renderCard(route, curY, expanded, subs, newCardAreas, newSubRowAreas);
                }
                curY += cardH + 8.0f;
            }
        }
        NVGRenderer.popScissor();
        this.cardAreas = newCardAreas;
        this.subRowAreas = newSubRowAreas;
    }

    private final void renderTypeFilterStrip(float stripY) {
        float btnX;
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        float h = 46.0f;
        NVGRenderer.rect(this.getX(), stripY, this.getWidth(), h, theme.getPanel(), 0.0f);
        NVGRenderer.line(this.getX(), stripY + h, this.getX() + this.getWidth(), stripY + h, 1.0f, theme.getModuleDivider());
        List allTabs = CollectionsKt.plus((Collection)CollectionsKt.listOf(null), (Iterable)((Iterable)RouteType.getEntries()));
        List newHitboxes = new ArrayList();
        float curX = this.getX() + 16.0f;
        float tabY = stripY + (h - 28.0f) / 2.0f;
        for (RouteType type : allTabs) {
            Object object = type;
            if (object == null || (object = object.getLabel()) == null) {
                object = "All";
            }
            Object label = object;
            float tw = NVGRenderer.textWidth$default((String)label, 11.0f, null, 4, null);
            float bw = tw + 24.0f;
            boolean isActive = this.activeTypeFilter == type;
            boolean isHover = ExtensionsKt.isHoveringOver(curX, tabY, bw, 28.0f);
            int bg = isActive ? theme.getAccent() : (isHover ? theme.getSelectedOverlay() : theme.getControlBg());
            int textColor = isActive ? theme.getTextOnAccent() : theme.getText();
            NVGRenderer.rect(curX, tabY, bw, 28.0f, bg, 5.0f);
            NVGRenderer.hollowRect(curX, tabY, bw, 28.0f, 1.0f, theme.getControlBorder(), 5.0f);
            NVGRenderer.text$default((String)label, curX + 12.0f, tabY + 6.0f, 11.0f, textColor, null, 32, null);
            newHitboxes.add(new TabHitbox(type, curX, tabY, bw, 28.0f));
            curX += bw + 6.0f;
        }
        this.tabHitboxes = newHitboxes;
        String btnLabel = "+ New Route";
        float btnW = NVGRenderer.textWidth$default(btnLabel, 11.0f, null, 4, null) + 24.0f;
        boolean btnHover = ExtensionsKt.isHoveringOver(btnX = this.getX() + this.getWidth() - 16.0f - btnW, tabY, btnW, 28.0f);
        NVGRenderer.rect(btnX, tabY, btnW, 28.0f, btnHover ? theme.getAccent() : theme.getControlBg(), 5.0f);
        NVGRenderer.hollowRect(btnX, tabY, btnW, 28.0f, 1.0f, theme.getControlBorder(), 5.0f);
        NVGRenderer.text$default(btnLabel, btnX + 12.0f, tabY + 6.0f, 11.0f, btnHover ? theme.getTextOnAccent() : theme.getText(), null, 32, null);
    }

    private final void renderCard(SavedRoute route, float cardY, boolean expanded, List<? extends SubRouteKey> subs, List<CardArea> cardAreas, List<SubRowArea> subRowAreas) {
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        float cardX = this.getX() + 16.0f;
        float cardW = this.getWidth() - 32.0f;
        List subs2 = expanded ? subs : CollectionsKt.emptyList();
        float cardH = 56.0f + (expanded ? (float)subs2.size() * 44.0f + 8.0f : 0.0f);
        boolean hovering = ExtensionsKt.isHoveringOver(cardX, cardY, cardW, 56.0f);
        NVGRenderer.rect(cardX, cardY, cardW, cardH, theme.getControlBg(), 8.0f);
        NVGRenderer.hollowRect(cardX, cardY, cardW, cardH, 1.0f, theme.getControlBorder(), 8.0f);
        NVGRenderer.rect(cardX, cardY, 4.0f, cardH, (int)route.getType().getColor(), 8.0f);
        String typeLabel = route.getType().getLabel();
        float typeBadgeW = NVGRenderer.textWidth$default(typeLabel, 9.0f, null, 4, null) + 12.0f;
        NVGRenderer.rect(cardX + 12.0f, cardY + 12.0f, typeBadgeW, 18.0f, (int)route.getType().getColor() & 0x33FFFFFF | 0x33000000, 4.0f);
        NVGRenderer.text$default(typeLabel, cardX + 18.0f, cardY + 14.0f, 9.0f, (int)route.getType().getColor(), null, 32, null);
        NVGRenderer.text$default(this.ellipsize(route.getName(), 380.0f, 13.0f), cardX + 12.0f + typeBadgeW + 10.0f, cardY + 14.0f, 13.0f, theme.getText(), null, 32, null);
        String ptSummary = this.buildPointSummary(route);
        NVGRenderer.text$default(ptSummary, cardX + 12.0f + typeBadgeW + 10.0f, cardY + 30.0f, 9.0f, theme.getTextSecondary(), null, 32, null);
        String loadedName = RouteStore.INSTANCE.getLoadedName(route.getType());
        if (Intrinsics.areEqual((Object)loadedName, (Object)route.getName())) {
            String armedTxt = "\u25cf Armed";
            float armedW = NVGRenderer.textWidth$default(armedTxt, 10.0f, null, 4, null);
            NVGRenderer.text$default(armedTxt, cardX + cardW - 220.0f - armedW - 8.0f, cardY + 17.0f, 10.0f, theme.getAccent(), null, 32, null);
        }
        String toggleLabel = expanded ? "\u25b2" : "\u25bc";
        String loadLabel = "\u25b6 Load";
        float toggleW = NVGRenderer.textWidth$default(toggleLabel, 11.0f, null, 4, null) + 20.0f;
        float loadW = NVGRenderer.textWidth$default(loadLabel, 11.0f, null, 4, null) + 20.0f;
        float deleteW = 24.0f;
        float deleteX = cardX + cardW - deleteW - 6.0f;
        float toggleX = deleteX - toggleW - 6.0f;
        float loadX = toggleX - loadW - 8.0f;
        float btnY = cardY + 15.0f;
        boolean loadHover = ExtensionsKt.isHoveringOver(loadX, btnY, loadW, 26.0f);
        boolean isLoaded = Intrinsics.areEqual((Object)loadedName, (Object)route.getName());
        NVGRenderer.rect(loadX, btnY, loadW, 26.0f, isLoaded ? (int)route.getType().getColor() : (loadHover ? theme.getSelectedOverlay() : theme.getPanel()), 6.0f);
        NVGRenderer.hollowRect(loadX, btnY, loadW, 26.0f, 1.0f, isLoaded ? (int)route.getType().getColor() : theme.getControlBorder(), 6.0f);
        NVGRenderer.text$default(loadLabel, loadX + 10.0f, btnY + 5.0f, 11.0f, isLoaded ? theme.getTextOnAccent() : theme.getText(), null, 32, null);
        boolean toggleHover = ExtensionsKt.isHoveringOver(toggleX, btnY, toggleW, 26.0f);
        NVGRenderer.rect(toggleX, btnY, toggleW, 26.0f, toggleHover ? theme.getSelectedOverlay() : theme.getPanel(), 6.0f);
        NVGRenderer.hollowRect(toggleX, btnY, toggleW, 26.0f, 1.0f, theme.getControlBorder(), 6.0f);
        float toggleLabelW = NVGRenderer.textWidth$default(toggleLabel, 11.0f, null, 4, null);
        NVGRenderer.text$default(toggleLabel, toggleX + (toggleW - toggleLabelW) / 2.0f, btnY + 5.0f, 11.0f, theme.getText(), null, 32, null);
        if (hovering || ExtensionsKt.isHoveringOver(deleteX, btnY, deleteW, 26.0f)) {
            boolean deleteHover = ExtensionsKt.isHoveringOver(deleteX, btnY, deleteW, 26.0f);
            NVGRenderer.rect(deleteX, btnY, deleteW, 26.0f, deleteHover ? 0x66FF4444 : theme.getPanel(), 6.0f);
            NVGRenderer.hollowRect(deleteX, btnY, deleteW, 26.0f, 1.0f, deleteHover ? -38037 : theme.getControlBorder(), 6.0f);
            NVGRenderer.text$default("\u2715", deleteX + 5.0f, btnY + 5.0f, 11.0f, deleteHover ? -38037 : theme.getTextSecondary(), null, 32, null);
        }
        cardAreas.add(new CardArea(route.getName(), cardY, 56.0f, loadX, loadW, toggleX, toggleW, deleteX, deleteW));
        if (expanded) {
            float subY = cardY + 56.0f + 4.0f;
            for (SubRouteKey sub : subs) {
                this.renderSubRow(route, sub, cardX, subY, cardW, subRowAreas);
                subY += 44.0f;
            }
        }
    }

    private final void renderSubRow(SavedRoute route, SubRouteKey sub, float cardX, float subY, float cardW, List<SubRowArea> subRowAreas) {
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        float rowX = cardX + 12.0f;
        float rowW = cardW - 24.0f;
        float rowH = 38.0f;
        NVGRenderer.rect(rowX, subY, rowW, rowH, theme.getPanel(), 6.0f);
        NVGRenderer.hollowRect(rowX, subY, rowW, rowH, 1.0f, theme.getControlBorder(), 6.0f);
        List<RoutePoint> pts = route.getSubRoute(sub);
        String subLabel = sub.getIcon() + " " + sub.getLabel();
        String ptCount = pts.size() + " pts";
        NVGRenderer.text$default(subLabel, rowX + 12.0f, subY + 6.0f, 11.0f, theme.getText(), null, 32, null);
        NVGRenderer.text$default(ptCount, rowX + 12.0f, subY + 21.0f, 9.0f, theme.getTextSecondary(), null, 32, null);
        String editLabel = "\u270f Edit";
        String insertLabel = "+ Insert";
        float editW = NVGRenderer.textWidth$default(editLabel, 10.0f, null, 4, null) + 18.0f;
        float insertW = NVGRenderer.textWidth$default(insertLabel, 10.0f, null, 4, null) + 18.0f;
        float btnH = 22.0f;
        float btnY = subY + (rowH - btnH) / 2.0f;
        float insertX = rowX + rowW - insertW - 8.0f;
        float editX = insertX - editW - 6.0f;
        boolean editHover = ExtensionsKt.isHoveringOver(editX, btnY, editW, btnH);
        boolean insertHover = ExtensionsKt.isHoveringOver(insertX, btnY, insertW, btnH);
        NVGRenderer.rect(editX, btnY, editW, btnH, editHover ? theme.getSelectedOverlay() : theme.getControlBg(), 5.0f);
        NVGRenderer.hollowRect(editX, btnY, editW, btnH, 1.0f, theme.getControlBorder(), 5.0f);
        NVGRenderer.text$default(editLabel, editX + 9.0f, btnY + 4.0f, 10.0f, theme.getText(), null, 32, null);
        NVGRenderer.rect(insertX, btnY, insertW, btnH, insertHover ? theme.getSelectedOverlay() : theme.getControlBg(), 5.0f);
        NVGRenderer.hollowRect(insertX, btnY, insertW, btnH, 1.0f, theme.getControlBorder(), 5.0f);
        NVGRenderer.text$default(insertLabel, insertX + 9.0f, btnY + 4.0f, 10.0f, theme.getText(), null, 32, null);
        subRowAreas.add(new SubRowArea(route.getName(), sub, subY, editX, editW, insertX, insertW));
    }

    private final void renderEmptyState(float ex, float ey) {
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        float bw = this.getWidth() - 32.0f;
        float bh = 80.0f;
        NVGRenderer.rect(ex, ey, bw, bh, theme.getControlBg(), 8.0f);
        NVGRenderer.hollowRect(ex, ey, bw, bh, 1.0f, theme.getControlBorder(), 8.0f);
        NVGRenderer.text$default(((CharSequence)this.searchText).length() > 0 || this.activeTypeFilter != null ? "No routes match the current filter." : "No saved routes found. Click + New Route to create one.", ex + 16.0f, ey + 18.0f, 12.0f, theme.getText(), null, 32, null);
        NVGRenderer.text$default("Routes are stored in config/cobalt/routes2/", ex + 16.0f, ey + 38.0f, 10.0f, theme.getTextSecondary(), null, 32, null);
    }

    @Override
    public boolean mouseClicked(int button) {
        SavedRoute route;
        boolean bl;
        SavedRoute it;
        boolean $i$f$firstOrNull;
        Iterable $this$firstOrNull$iv;
        if (button != 0) {
            return super.mouseClicked(button);
        }
        for (TabHitbox tab : this.tabHitboxes) {
            if (!ExtensionsKt.isHoveringOver(tab.getX(), tab.getY(), tab.getW(), tab.getH())) continue;
            this.activeTypeFilter = tab.getType();
            this.applyFilter();
            this.scroll.reset();
            return true;
        }
        float tabStripY = this.getY() + this.topBar.getHeight();
        float tabY = tabStripY + 9.0f;
        String btnLabel = "+ New Route";
        float btnW = NVGRenderer.textWidth$default(btnLabel, 11.0f, null, 4, null) + 24.0f;
        float btnX = this.getX() + this.getWidth() - 16.0f - btnW;
        if (ExtensionsKt.isHoveringOver(btnX, tabY, btnW, 28.0f)) {
            UIConfig.INSTANCE.swapBodyPanel(new UINewRouteModal());
            return true;
        }
        float btnH = 26.0f;
        for (SubRowArea subRowArea : this.subRowAreas) {
            Object v0;
            block13: {
                $this$firstOrNull$iv = this.filteredRoutes;
                $i$f$firstOrNull = false;
                for (Object element$iv : $this$firstOrNull$iv) {
                    it = (SavedRoute)element$iv;
                    bl = false;
                    if (!Intrinsics.areEqual((Object)it.getName(), (Object)subRowArea.getRouteName())) continue;
                    v0 = element$iv;
                    break block13;
                }
                v0 = null;
            }
            if ((SavedRoute)v0 == null) continue;
            if (ExtensionsKt.isHoveringOver(subRowArea.getEditX(), subRowArea.getY() + 8.0f, subRowArea.getEditW(), 22.0f)) {
                RouteEditMode.INSTANCE.enterEdit(route, subRowArea.getSub(), (Function0<Unit>)((Function0)UIRoutesPanel::mouseClicked$lambda$1));
                return true;
            }
            if (!ExtensionsKt.isHoveringOver(subRowArea.getInsertX(), subRowArea.getY() + 8.0f, subRowArea.getInsertW(), 22.0f)) continue;
            int insertAfter = route.getSubRoute(subRowArea.getSub()).size() - 1;
            RouteEditMode.INSTANCE.enterInsertMode(route, subRowArea.getSub(), RangesKt.coerceAtLeast((int)insertAfter, (int)0), (Function0<Unit>)((Function0)UIRoutesPanel::mouseClicked$lambda$2));
            return true;
        }
        for (CardArea cardArea : this.cardAreas) {
            Object v1;
            block14: {
                $this$firstOrNull$iv = this.filteredRoutes;
                $i$f$firstOrNull = false;
                for (Object element$iv : $this$firstOrNull$iv) {
                    it = (SavedRoute)element$iv;
                    bl = false;
                    if (!Intrinsics.areEqual((Object)it.getName(), (Object)cardArea.getName())) continue;
                    v1 = element$iv;
                    break block14;
                }
                v1 = null;
            }
            if ((SavedRoute)v1 == null) continue;
            if (ExtensionsKt.isHoveringOver(cardArea.getDeleteX(), cardArea.getHeaderY() + (56.0f - btnH) / 2.0f, cardArea.getDeleteW(), btnH)) {
                RouteStore.INSTANCE.delete(route.getName());
                if (Intrinsics.areEqual((Object)RouteStore.INSTANCE.getLoadedName(route.getType()), (Object)route.getName())) {
                    RouteStore.INSTANCE.clearLoaded(route.getType());
                }
                this.expandedRoutes.remove(route.getName());
                this.refresh();
                return true;
            }
            if (ExtensionsKt.isHoveringOver(cardArea.getLoadX(), cardArea.getHeaderY() + (56.0f - btnH) / 2.0f, cardArea.getLoadW(), btnH)) {
                if (Intrinsics.areEqual((Object)RouteStore.INSTANCE.getLoadedName(route.getType()), (Object)route.getName())) {
                    RouteStore.INSTANCE.clearLoaded(route.getType());
                } else {
                    RouteStore.INSTANCE.setLoaded(route);
                }
                return true;
            }
            if (!ExtensionsKt.isHoveringOver(cardArea.getToggleX(), cardArea.getHeaderY() + (56.0f - btnH) / 2.0f, cardArea.getToggleW(), btnH)) continue;
            boolean bl2 = this.expandedRoutes.contains(route.getName()) ? this.expandedRoutes.remove(route.getName()) : this.expandedRoutes.add(route.getName());
            return true;
        }
        return super.mouseClicked(button);
    }

    @Override
    public boolean mouseScrolled(double horizontalAmount, double verticalAmount) {
        if (!ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
            return false;
        }
        this.scroll.handleScroll(verticalAmount);
        return true;
    }

    /*
     * WARNING - void declaration
     */
    private final void applyFilter() {
        void $this$filterTo$iv$iv;
        void $this$filter$iv;
        Iterable iterable = this.allRoutes;
        UIRoutesPanel uIRoutesPanel = this;
        boolean $i$f$filter = false;
        void var3_4 = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            SavedRoute route = (SavedRoute)element$iv$iv;
            boolean bl = false;
            boolean matchesType = this.activeTypeFilter == null || route.getType() == this.activeTypeFilter;
            boolean matchesSearch = ((CharSequence)this.searchText).length() == 0 || StringsKt.contains((CharSequence)route.getName(), (CharSequence)this.searchText, (boolean)true);
            if (!(matchesType && matchesSearch)) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        uIRoutesPanel.filteredRoutes = (List)destination$iv$iv;
    }

    private final float measureTotalCardsHeight() {
        float h = 0.0f;
        for (SavedRoute route : this.filteredRoutes) {
            boolean expanded = this.expandedRoutes.contains(route.getName());
            List<SubRouteKey> subs = expanded ? RouteTypeKt.subRoutesFor(route.getType()) : CollectionsKt.emptyList();
            h += 56.0f + (expanded ? (float)subs.size() * 44.0f + 8.0f : 0.0f) + 8.0f;
        }
        return this.filteredRoutes.isEmpty() ? 80.0f : h;
    }

    private final String buildPointSummary(SavedRoute route) {
        CharSequence charSequence;
        CharSequence charSequence2;
        List parts = new ArrayList();
        switch (WhenMappings.$EnumSwitchMapping$0[route.getType().ordinal()]) {
            case 1: {
                if (!((Collection)route.getTravelRoute()).isEmpty()) {
                    ((Collection)parts).add(route.getTravelRoute().size() + " travel");
                }
                if (!(!((Collection)route.getLoopOrArea()).isEmpty())) break;
                ((Collection)parts).add(route.getLoopOrArea().size() + " loop");
                break;
            }
            case 2: {
                if (!((Collection)route.getTravelRoute()).isEmpty()) {
                    ((Collection)parts).add(route.getTravelRoute().size() + " travel");
                }
                if (!(!((Collection)route.getLoopOrArea()).isEmpty())) break;
                ((Collection)parts).add(route.getLoopOrArea().size() + " area");
                break;
            }
            default: {
                ((Collection)parts).add(route.getPoints().size() + " pts");
            }
        }
        if ((charSequence2 = (CharSequence)CollectionsKt.joinToString$default((Iterable)parts, (CharSequence)"  \u00b7  ", null, null, (int)0, null, null, (int)62, null)).length() == 0) {
            boolean bl = false;
            charSequence = "0 pts";
        } else {
            charSequence = charSequence2;
        }
        return (String)charSequence;
    }

    private final String ellipsize(String text, float maxWidth, float size) {
        if (NVGRenderer.textWidth$default(text, size, null, 4, null) <= maxWidth) {
            return text;
        }
        for (int end = text.length(); end > 1; --end) {
            String string = text.substring(0, end);
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"substring(...)");
            String candidate = ((Object)StringsKt.trimEnd((CharSequence)string)).toString() + "...";
            if (!(NVGRenderer.textWidth$default(candidate, size, null, 4, null) <= maxWidth)) continue;
            return candidate;
        }
        return "...";
    }

    private static final Unit _init_$lambda$0(UIRoutesPanel this$0, String text) {
        Intrinsics.checkNotNullParameter((Object)text, (String)"text");
        this$0.searchText = text;
        this$0.applyFilter();
        return Unit.INSTANCE;
    }

    private static final Unit mouseClicked$lambda$1() {
        UIConfig.INSTANCE.swapBodyPanel(new UIRoutesPanel());
        UIConfig.INSTANCE.openUI();
        return Unit.INSTANCE;
    }

    private static final Unit mouseClicked$lambda$2() {
        UIConfig.INSTANCE.swapBodyPanel(new UIRoutesPanel());
        UIConfig.INSTANCE.openUI();
        return Unit.INSTANCE;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000f\b\u0082\b\u0018\u00002\u00020\u0001BO\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0004\u0012\u0006\u0010\b\u001a\u00020\u0004\u0012\u0006\u0010\t\u001a\u00020\u0004\u0012\u0006\u0010\n\u001a\u00020\u0004\u0012\u0006\u0010\u000b\u001a\u00020\u0004\u0012\u0006\u0010\f\u001a\u00020\u0004\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0010\u0010\u0011\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0010\u0010\u0013\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0013\u0010\u0012J\u0010\u0010\u0014\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0014\u0010\u0012J\u0010\u0010\u0015\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0015\u0010\u0012J\u0010\u0010\u0016\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0016\u0010\u0012J\u0010\u0010\u0017\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0017\u0010\u0012J\u0010\u0010\u0018\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0018\u0010\u0012J\u0010\u0010\u0019\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0019\u0010\u0012Jj\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u00042\b\b\u0002\u0010\b\u001a\u00020\u00042\b\b\u0002\u0010\t\u001a\u00020\u00042\b\b\u0002\u0010\n\u001a\u00020\u00042\b\b\u0002\u0010\u000b\u001a\u00020\u00042\b\b\u0002\u0010\f\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u001b\u0010\u001e\u001a\u00020\u001d2\b\u0010\u001c\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u001e\u0010\u001fJ\u0011\u0010!\u001a\u00020 H\u00d6\u0081\u0004\u00a2\u0006\u0004\b!\u0010\"J\u0011\u0010#\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b#\u0010\u0010R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010$\u001a\u0004\b%\u0010\u0010R\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010&\u001a\u0004\b'\u0010\u0012R\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010&\u001a\u0004\b(\u0010\u0012R\u0017\u0010\u0007\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010&\u001a\u0004\b)\u0010\u0012R\u0017\u0010\b\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\b\u0010&\u001a\u0004\b*\u0010\u0012R\u0017\u0010\t\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\t\u0010&\u001a\u0004\b+\u0010\u0012R\u0017\u0010\n\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\n\u0010&\u001a\u0004\b,\u0010\u0012R\u0017\u0010\u000b\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u000b\u0010&\u001a\u0004\b-\u0010\u0012R\u0017\u0010\f\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\f\u0010&\u001a\u0004\b.\u0010\u0012\u00a8\u0006/"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIRoutesPanel$CardArea;", "", "", "name", "", "headerY", "headerH", "loadX", "loadW", "toggleX", "toggleW", "deleteX", "deleteW", "<init>", "(Ljava/lang/String;FFFFFFFF)V", "component1", "()Ljava/lang/String;", "component2", "()F", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;FFFFFFFF)Lorg/cobalt/internal/ui/panel/panels/UIRoutesPanel$CardArea;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getName", "F", "getHeaderY", "getHeaderH", "getLoadX", "getLoadW", "getToggleX", "getToggleW", "getDeleteX", "getDeleteW", "cobalt"})
    private static final class CardArea {
        @NotNull
        private final String name;
        private final float headerY;
        private final float headerH;
        private final float loadX;
        private final float loadW;
        private final float toggleX;
        private final float toggleW;
        private final float deleteX;
        private final float deleteW;

        public CardArea(@NotNull String name, float headerY, float headerH, float loadX, float loadW, float toggleX, float toggleW, float deleteX, float deleteW) {
            Intrinsics.checkNotNullParameter((Object)name, (String)"name");
            this.name = name;
            this.headerY = headerY;
            this.headerH = headerH;
            this.loadX = loadX;
            this.loadW = loadW;
            this.toggleX = toggleX;
            this.toggleW = toggleW;
            this.deleteX = deleteX;
            this.deleteW = deleteW;
        }

        @NotNull
        public final String getName() {
            return this.name;
        }

        public final float getHeaderY() {
            return this.headerY;
        }

        public final float getHeaderH() {
            return this.headerH;
        }

        public final float getLoadX() {
            return this.loadX;
        }

        public final float getLoadW() {
            return this.loadW;
        }

        public final float getToggleX() {
            return this.toggleX;
        }

        public final float getToggleW() {
            return this.toggleW;
        }

        public final float getDeleteX() {
            return this.deleteX;
        }

        public final float getDeleteW() {
            return this.deleteW;
        }

        @NotNull
        public final String component1() {
            return this.name;
        }

        public final float component2() {
            return this.headerY;
        }

        public final float component3() {
            return this.headerH;
        }

        public final float component4() {
            return this.loadX;
        }

        public final float component5() {
            return this.loadW;
        }

        public final float component6() {
            return this.toggleX;
        }

        public final float component7() {
            return this.toggleW;
        }

        public final float component8() {
            return this.deleteX;
        }

        public final float component9() {
            return this.deleteW;
        }

        @NotNull
        public final CardArea copy(@NotNull String name, float headerY, float headerH, float loadX, float loadW, float toggleX, float toggleW, float deleteX, float deleteW) {
            Intrinsics.checkNotNullParameter((Object)name, (String)"name");
            return new CardArea(name, headerY, headerH, loadX, loadW, toggleX, toggleW, deleteX, deleteW);
        }

        public static /* synthetic */ CardArea copy$default(CardArea cardArea, String string, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int n, Object object) {
            if ((n & 1) != 0) {
                string = cardArea.name;
            }
            if ((n & 2) != 0) {
                f = cardArea.headerY;
            }
            if ((n & 4) != 0) {
                f2 = cardArea.headerH;
            }
            if ((n & 8) != 0) {
                f3 = cardArea.loadX;
            }
            if ((n & 0x10) != 0) {
                f4 = cardArea.loadW;
            }
            if ((n & 0x20) != 0) {
                f5 = cardArea.toggleX;
            }
            if ((n & 0x40) != 0) {
                f6 = cardArea.toggleW;
            }
            if ((n & 0x80) != 0) {
                f7 = cardArea.deleteX;
            }
            if ((n & 0x100) != 0) {
                f8 = cardArea.deleteW;
            }
            return cardArea.copy(string, f, f2, f3, f4, f5, f6, f7, f8);
        }

        @NotNull
        public String toString() {
            return "CardArea(name=" + this.name + ", headerY=" + this.headerY + ", headerH=" + this.headerH + ", loadX=" + this.loadX + ", loadW=" + this.loadW + ", toggleX=" + this.toggleX + ", toggleW=" + this.toggleW + ", deleteX=" + this.deleteX + ", deleteW=" + this.deleteW + ")";
        }

        public int hashCode() {
            int result = this.name.hashCode();
            result = result * 31 + Float.hashCode(this.headerY);
            result = result * 31 + Float.hashCode(this.headerH);
            result = result * 31 + Float.hashCode(this.loadX);
            result = result * 31 + Float.hashCode(this.loadW);
            result = result * 31 + Float.hashCode(this.toggleX);
            result = result * 31 + Float.hashCode(this.toggleW);
            result = result * 31 + Float.hashCode(this.deleteX);
            result = result * 31 + Float.hashCode(this.deleteW);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CardArea)) {
                return false;
            }
            CardArea cardArea = (CardArea)other;
            if (!Intrinsics.areEqual((Object)this.name, (Object)cardArea.name)) {
                return false;
            }
            if (Float.compare(this.headerY, cardArea.headerY) != 0) {
                return false;
            }
            if (Float.compare(this.headerH, cardArea.headerH) != 0) {
                return false;
            }
            if (Float.compare(this.loadX, cardArea.loadX) != 0) {
                return false;
            }
            if (Float.compare(this.loadW, cardArea.loadW) != 0) {
                return false;
            }
            if (Float.compare(this.toggleX, cardArea.toggleX) != 0) {
                return false;
            }
            if (Float.compare(this.toggleW, cardArea.toggleW) != 0) {
                return false;
            }
            if (Float.compare(this.deleteX, cardArea.deleteX) != 0) {
                return false;
            }
            return Float.compare(this.deleteW, cardArea.deleteW) == 0;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u000f\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006R\u0014\u0010\u0007\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0007\u0010\u0006R\u0014\u0010\b\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\b\u0010\u0006R\u0014\u0010\t\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\t\u0010\u0006R\u0014\u0010\n\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\n\u0010\u0006R\u0014\u0010\u000b\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u000b\u0010\u0006R\u0014\u0010\f\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\f\u0010\u0006R\u0014\u0010\r\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\r\u0010\u0006R\u0014\u0010\u000e\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u000e\u0010\u0006R\u0014\u0010\u000f\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u000f\u0010\u0006R\u0014\u0010\u0010\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0010\u0010\u0006R\u0014\u0010\u0011\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0011\u0010\u0006R\u0014\u0010\u0012\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0006\u00a8\u0006\u0013"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIRoutesPanel$Companion;", "", "<init>", "()V", "", "PAD_H", "F", "PAD_V", "TAB_STRIP_H", "TAB_H", "TAB_PAD_H", "TAB_GAP", "CARD_HEADER_H", "CARD_GAP", "BTN_H", "BTN_H_SUB", "SUB_ROW_H", "SUB_SECTION_PAD", "EMPTY_STATE_H", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0014\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000e\b\u0082\b\u0018\u00002\u00020\u0001B?\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\u0006\u0012\u0006\u0010\t\u001a\u00020\u0006\u0012\u0006\u0010\n\u001a\u00020\u0006\u0012\u0006\u0010\u000b\u001a\u00020\u0006\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0010\u0010\u0010\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0010\u0010\u0012\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0010\u0010\u0014\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b\u0014\u0010\u0013J\u0010\u0010\u0015\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b\u0015\u0010\u0013J\u0010\u0010\u0016\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b\u0016\u0010\u0013J\u0010\u0010\u0017\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b\u0017\u0010\u0013JV\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\b\u001a\u00020\u00062\b\b\u0002\u0010\t\u001a\u00020\u00062\b\b\u0002\u0010\n\u001a\u00020\u00062\b\b\u0002\u0010\u000b\u001a\u00020\u0006H\u00c6\u0001\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u001b\u0010\u001c\u001a\u00020\u001b2\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u001c\u0010\u001dJ\u0011\u0010\u001f\u001a\u00020\u001eH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001f\u0010 J\u0011\u0010!\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b!\u0010\u000fR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\"\u001a\u0004\b#\u0010\u000fR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010$\u001a\u0004\b%\u0010\u0011R\u0017\u0010\u0007\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010&\u001a\u0004\b'\u0010\u0013R\u0017\u0010\b\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\b\u0010&\u001a\u0004\b(\u0010\u0013R\u0017\u0010\t\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\t\u0010&\u001a\u0004\b)\u0010\u0013R\u0017\u0010\n\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\n\u0010&\u001a\u0004\b*\u0010\u0013R\u0017\u0010\u000b\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\u000b\u0010&\u001a\u0004\b+\u0010\u0013\u00a8\u0006,"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIRoutesPanel$SubRowArea;", "", "", "routeName", "Lorg/cobalt/internal/routes/SubRouteKey;", "sub", "", "y", "editX", "editW", "insertX", "insertW", "<init>", "(Ljava/lang/String;Lorg/cobalt/internal/routes/SubRouteKey;FFFFF)V", "component1", "()Ljava/lang/String;", "component2", "()Lorg/cobalt/internal/routes/SubRouteKey;", "component3", "()F", "component4", "component5", "component6", "component7", "copy", "(Ljava/lang/String;Lorg/cobalt/internal/routes/SubRouteKey;FFFFF)Lorg/cobalt/internal/ui/panel/panels/UIRoutesPanel$SubRowArea;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getRouteName", "Lorg/cobalt/internal/routes/SubRouteKey;", "getSub", "F", "getY", "getEditX", "getEditW", "getInsertX", "getInsertW", "cobalt"})
    private static final class SubRowArea {
        @NotNull
        private final String routeName;
        @NotNull
        private final SubRouteKey sub;
        private final float y;
        private final float editX;
        private final float editW;
        private final float insertX;
        private final float insertW;

        public SubRowArea(@NotNull String routeName, @NotNull SubRouteKey sub, float y, float editX, float editW, float insertX, float insertW) {
            Intrinsics.checkNotNullParameter((Object)routeName, (String)"routeName");
            Intrinsics.checkNotNullParameter((Object)((Object)sub), (String)"sub");
            this.routeName = routeName;
            this.sub = sub;
            this.y = y;
            this.editX = editX;
            this.editW = editW;
            this.insertX = insertX;
            this.insertW = insertW;
        }

        @NotNull
        public final String getRouteName() {
            return this.routeName;
        }

        @NotNull
        public final SubRouteKey getSub() {
            return this.sub;
        }

        public final float getY() {
            return this.y;
        }

        public final float getEditX() {
            return this.editX;
        }

        public final float getEditW() {
            return this.editW;
        }

        public final float getInsertX() {
            return this.insertX;
        }

        public final float getInsertW() {
            return this.insertW;
        }

        @NotNull
        public final String component1() {
            return this.routeName;
        }

        @NotNull
        public final SubRouteKey component2() {
            return this.sub;
        }

        public final float component3() {
            return this.y;
        }

        public final float component4() {
            return this.editX;
        }

        public final float component5() {
            return this.editW;
        }

        public final float component6() {
            return this.insertX;
        }

        public final float component7() {
            return this.insertW;
        }

        @NotNull
        public final SubRowArea copy(@NotNull String routeName, @NotNull SubRouteKey sub, float y, float editX, float editW, float insertX, float insertW) {
            Intrinsics.checkNotNullParameter((Object)routeName, (String)"routeName");
            Intrinsics.checkNotNullParameter((Object)((Object)sub), (String)"sub");
            return new SubRowArea(routeName, sub, y, editX, editW, insertX, insertW);
        }

        public static /* synthetic */ SubRowArea copy$default(SubRowArea subRowArea, String string, SubRouteKey subRouteKey, float f, float f2, float f3, float f4, float f5, int n, Object object) {
            if ((n & 1) != 0) {
                string = subRowArea.routeName;
            }
            if ((n & 2) != 0) {
                subRouteKey = subRowArea.sub;
            }
            if ((n & 4) != 0) {
                f = subRowArea.y;
            }
            if ((n & 8) != 0) {
                f2 = subRowArea.editX;
            }
            if ((n & 0x10) != 0) {
                f3 = subRowArea.editW;
            }
            if ((n & 0x20) != 0) {
                f4 = subRowArea.insertX;
            }
            if ((n & 0x40) != 0) {
                f5 = subRowArea.insertW;
            }
            return subRowArea.copy(string, subRouteKey, f, f2, f3, f4, f5);
        }

        @NotNull
        public String toString() {
            return "SubRowArea(routeName=" + this.routeName + ", sub=" + this.sub + ", y=" + this.y + ", editX=" + this.editX + ", editW=" + this.editW + ", insertX=" + this.insertX + ", insertW=" + this.insertW + ")";
        }

        public int hashCode() {
            int result = this.routeName.hashCode();
            result = result * 31 + this.sub.hashCode();
            result = result * 31 + Float.hashCode(this.y);
            result = result * 31 + Float.hashCode(this.editX);
            result = result * 31 + Float.hashCode(this.editW);
            result = result * 31 + Float.hashCode(this.insertX);
            result = result * 31 + Float.hashCode(this.insertW);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof SubRowArea)) {
                return false;
            }
            SubRowArea subRowArea = (SubRowArea)other;
            if (!Intrinsics.areEqual((Object)this.routeName, (Object)subRowArea.routeName)) {
                return false;
            }
            if (this.sub != subRowArea.sub) {
                return false;
            }
            if (Float.compare(this.y, subRowArea.y) != 0) {
                return false;
            }
            if (Float.compare(this.editX, subRowArea.editX) != 0) {
                return false;
            }
            if (Float.compare(this.editW, subRowArea.editW) != 0) {
                return false;
            }
            if (Float.compare(this.insertX, subRowArea.insertX) != 0) {
                return false;
            }
            return Float.compare(this.insertW, subRowArea.insertW) == 0;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\n\b\u0082\b\u0018\u00002\u00020\u0001B1\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0004\u0012\u0006\u0010\b\u001a\u00020\u0004\u00a2\u0006\u0004\b\t\u0010\nJ\u0012\u0010\u000b\u001a\u0004\u0018\u00010\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\u000eJ\u0010\u0010\u0010\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u000eJ\u0010\u0010\u0011\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u000eJD\u0010\u0012\u001a\u00020\u00002\n\b\u0002\u0010\u0003\u001a\u0004\u0018\u00010\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u00042\b\b\u0002\u0010\b\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u001b\u0010\u0016\u001a\u00020\u00152\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0019\u001a\u00020\u0018H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\u0011\u0010\u001c\u001a\u00020\u001bH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001c\u0010\u001dR\u0019\u0010\u0003\u001a\u0004\u0018\u00010\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001e\u001a\u0004\b\u001f\u0010\fR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010 \u001a\u0004\b!\u0010\u000eR\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010 \u001a\u0004\b\"\u0010\u000eR\u0017\u0010\u0007\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010 \u001a\u0004\b#\u0010\u000eR\u0017\u0010\b\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\b\u0010 \u001a\u0004\b$\u0010\u000e\u00a8\u0006%"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIRoutesPanel$TabHitbox;", "", "Lorg/cobalt/internal/routes/RouteType;", "type", "", "x", "y", "w", "h", "<init>", "(Lorg/cobalt/internal/routes/RouteType;FFFF)V", "component1", "()Lorg/cobalt/internal/routes/RouteType;", "component2", "()F", "component3", "component4", "component5", "copy", "(Lorg/cobalt/internal/routes/RouteType;FFFF)Lorg/cobalt/internal/ui/panel/panels/UIRoutesPanel$TabHitbox;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "Lorg/cobalt/internal/routes/RouteType;", "getType", "F", "getX", "getY", "getW", "getH", "cobalt"})
    private static final class TabHitbox {
        @Nullable
        private final RouteType type;
        private final float x;
        private final float y;
        private final float w;
        private final float h;

        public TabHitbox(@Nullable RouteType type, float x, float y, float w, float h) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        @Nullable
        public final RouteType getType() {
            return this.type;
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

        @Nullable
        public final RouteType component1() {
            return this.type;
        }

        public final float component2() {
            return this.x;
        }

        public final float component3() {
            return this.y;
        }

        public final float component4() {
            return this.w;
        }

        public final float component5() {
            return this.h;
        }

        @NotNull
        public final TabHitbox copy(@Nullable RouteType type, float x, float y, float w, float h) {
            return new TabHitbox(type, x, y, w, h);
        }

        public static /* synthetic */ TabHitbox copy$default(TabHitbox tabHitbox, RouteType routeType, float f, float f2, float f3, float f4, int n, Object object) {
            if ((n & 1) != 0) {
                routeType = tabHitbox.type;
            }
            if ((n & 2) != 0) {
                f = tabHitbox.x;
            }
            if ((n & 4) != 0) {
                f2 = tabHitbox.y;
            }
            if ((n & 8) != 0) {
                f3 = tabHitbox.w;
            }
            if ((n & 0x10) != 0) {
                f4 = tabHitbox.h;
            }
            return tabHitbox.copy(routeType, f, f2, f3, f4);
        }

        @NotNull
        public String toString() {
            return "TabHitbox(type=" + this.type + ", x=" + this.x + ", y=" + this.y + ", w=" + this.w + ", h=" + this.h + ")";
        }

        public int hashCode() {
            int result = this.type == null ? 0 : this.type.hashCode();
            result = result * 31 + Float.hashCode(this.x);
            result = result * 31 + Float.hashCode(this.y);
            result = result * 31 + Float.hashCode(this.w);
            result = result * 31 + Float.hashCode(this.h);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof TabHitbox)) {
                return false;
            }
            TabHitbox tabHitbox = (TabHitbox)other;
            if (this.type != tabHitbox.type) {
                return false;
            }
            if (Float.compare(this.x, tabHitbox.x) != 0) {
                return false;
            }
            if (Float.compare(this.y, tabHitbox.y) != 0) {
                return false;
            }
            if (Float.compare(this.w, tabHitbox.w) != 0) {
                return false;
            }
            return Float.compare(this.h, tabHitbox.h) == 0;
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[RouteType.values().length];
            try {
                nArray[RouteType.ORE_MINER.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RouteType.PATROL.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

