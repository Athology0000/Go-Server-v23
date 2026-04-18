/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.StringsKt
 *  net.minecraft.class_11905
 *  net.minecraft.class_11908
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.ui.panel.panels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_310;
import org.cobalt.api.ui.theme.Theme;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.routes.RouteEditMode;
import org.cobalt.internal.routes.RouteStore;
import org.cobalt.internal.routes.RouteType;
import org.cobalt.internal.routes.RouteTypeKt;
import org.cobalt.internal.routes.SavedRoute;
import org.cobalt.internal.routes.SubRouteKey;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.animation.ColorAnimation;
import org.cobalt.internal.ui.components.UIBackButton;
import org.cobalt.internal.ui.components.UITopbar;
import org.cobalt.internal.ui.panel.UIPanel;
import org.cobalt.internal.ui.panel.panels.UIRoutesPanel;
import org.cobalt.internal.ui.screen.UIConfig;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.cobalt.internal.ui.util.GridLayout;
import org.cobalt.internal.ui.util.TextInputHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000|\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u0000\u0018\u0000 92\u00020\u0001:\u0002:9B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u0017\u0010\b\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\b\u0010\tJ\u0017\u0010\r\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\nH\u0016\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0017\u0010\u000f\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\nH\u0016\u00a2\u0006\u0004\b\u000f\u0010\u000eJ'\u0010\u0013\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\u0010H\u0016\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0017\u0010\u0017\u001a\u00020\f2\u0006\u0010\u0016\u001a\u00020\u0015H\u0016\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u0017\u0010\u001a\u001a\u00020\f2\u0006\u0010\u0016\u001a\u00020\u0019H\u0016\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u0017\u0010\u001e\u001a\u00020\u00042\u0006\u0010\u001d\u001a\u00020\u001cH\u0002\u00a2\u0006\u0004\b\u001e\u0010\u001fJ\u0017\u0010 \u001a\u00020\u00042\u0006\u0010\u001d\u001a\u00020\u001cH\u0002\u00a2\u0006\u0004\b \u0010\u001fR\u0014\u0010\"\u001a\u00020!8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\"\u0010#R\u0014\u0010%\u001a\u00020$8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b%\u0010&R\u001e\u0010)\u001a\f\u0012\b\u0012\u00060(R\u00020\u00000'8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b)\u0010*R\u0014\u0010,\u001a\u00020+8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b,\u0010-R\u0018\u0010.\u001a\u0004\u0018\u00010\u001c8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b.\u0010/R\u0014\u00101\u001a\u0002008\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b1\u00102R\u0016\u00103\u001a\u00020\f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b3\u00104R\u0016\u00105\u001a\u00020\f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b5\u00104R\u0018\u00107\u001a\u0004\u0018\u0001068\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b7\u00108\u00a8\u0006;"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UINewRouteModal;", "Lorg/cobalt/internal/ui/panel/UIPanel;", "<init>", "()V", "", "render", "", "startY", "renderNameSection", "(F)V", "", "button", "", "mouseClicked", "(I)Z", "mouseReleased", "", "offsetX", "offsetY", "mouseDragged", "(IDD)Z", "Lnet/minecraft/class_11905;", "input", "charTyped", "(Lnet/minecraft/class_11905;)Z", "Lnet/minecraft/class_11908;", "keyPressed", "(Lnet/minecraft/class_11908;)Z", "Lorg/cobalt/internal/routes/RouteType;", "type", "tryCreateRoute", "(Lorg/cobalt/internal/routes/RouteType;)V", "onTypeSelected", "Lorg/cobalt/internal/ui/components/UITopbar;", "topBar", "Lorg/cobalt/internal/ui/components/UITopbar;", "Lorg/cobalt/internal/ui/components/UIBackButton;", "backButton", "Lorg/cobalt/internal/ui/components/UIBackButton;", "", "Lorg/cobalt/internal/ui/panel/panels/UINewRouteModal$TypeCard;", "typeCards", "Ljava/util/List;", "Lorg/cobalt/internal/ui/util/GridLayout;", "cardGrid", "Lorg/cobalt/internal/ui/util/GridLayout;", "selectedType", "Lorg/cobalt/internal/routes/RouteType;", "Lorg/cobalt/internal/ui/util/TextInputHandler;", "nameInput", "Lorg/cobalt/internal/ui/util/TextInputHandler;", "nameInputFocused", "Z", "nameInputDragging", "", "nameError", "Ljava/lang/String;", "Companion", "TypeCard", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUINewRouteModal.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UINewRouteModal.kt\norg/cobalt/internal/ui/panel/panels/UINewRouteModal\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Extensions.kt\norg/cobalt/internal/ui/util/ExtensionsKt\n*L\n1#1,303:1\n1586#2:304\n1661#2,3:305\n1915#2,2:308\n1807#2,3:313\n1#3:310\n6#4:311\n6#4:312\n*S KotlinDebug\n*F\n+ 1 UINewRouteModal.kt\norg/cobalt/internal/ui/panel/panels/UINewRouteModal\n*L\n33#1:304\n33#1:305,3\n59#1:308,2\n230#1:313,3\n141#1:311\n168#1:312\n*E\n"})
public final class UINewRouteModal
extends UIPanel {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final UITopbar topBar = new UITopbar("New Route");
    @NotNull
    private final UIBackButton backButton = new UIBackButton((Function0<Unit>)((Function0)UINewRouteModal::backButton$lambda$0));
    @NotNull
    private final List<TypeCard> typeCards;
    @NotNull
    private final GridLayout cardGrid;
    @Nullable
    private RouteType selectedType;
    @NotNull
    private final TextInputHandler nameInput;
    private boolean nameInputFocused;
    private boolean nameInputDragging;
    @Nullable
    private String nameError;

    /*
     * WARNING - void declaration
     */
    public UINewRouteModal() {
        super(0.0f, 0.0f, 890.0f, 600.0f);
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        Iterable iterable = (Iterable)RouteType.getEntries();
        UINewRouteModal uINewRouteModal = this;
        boolean $i$f$map = false;
        void var3_4 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            RouteType routeType = (RouteType)((Object)item$iv$iv);
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new TypeCard((RouteType)it));
        }
        uINewRouteModal.typeCards = (List)destination$iv$iv;
        this.cardGrid = new GridLayout(3, 255.0f, 100.0f, 20.0f);
        this.nameInput = new TextInputHandler("", 64);
        this.getComponents().addAll((Collection<UIComponent>)this.typeCards);
        this.getComponents().add(this.backButton);
        this.getComponents().add(this.topBar);
    }

    @Override
    public void render() {
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), theme.getBackground(), 10.0f);
        this.topBar.updateBounds(this.getX(), this.getY()).render();
        this.backButton.updateBounds(this.getX() + 20.0f, this.getY() + this.topBar.getHeight() + 20.0f).render();
        float cardsStartY = this.getY() + this.topBar.getHeight() + 70.0f;
        this.cardGrid.layout(this.getX() + 20.0f, cardsStartY, this.typeCards);
        Iterable $this$forEach$iv = this.typeCards;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            TypeCard it = (TypeCard)element$iv;
            boolean bl = false;
            it.render();
        }
        if (this.selectedType != null) {
            this.renderNameSection(cardsStartY + this.cardGrid.contentHeight(this.typeCards.size()) + 24.0f);
        }
    }

    private final void renderNameSection(float startY) {
        block6: {
            CharSequence charSequence;
            CharSequence charSequence2;
            Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
            RouteType routeType = this.selectedType;
            if (routeType == null) {
                return;
            }
            RouteType type = routeType;
            NVGRenderer.text$default("Route Name for " + type.getLabel(), this.getX() + 20.0f, startY, 12.0f, theme.getTextSecondary(), null, 32, null);
            float inputX = this.getX() + 20.0f;
            float inputY = startY + 18.0f;
            float inputW = 360.0f;
            float inputH = 34.0f;
            int borderColor = this.nameError != null ? -38037 : (this.nameInputFocused ? theme.getAccent() : theme.getInputBorder());
            NVGRenderer.rect(inputX, inputY, inputW, inputH, theme.getInputBg(), 6.0f);
            NVGRenderer.hollowRect(inputX, inputY, inputW, inputH, 2.0f, borderColor, 6.0f);
            float textX = inputX + 10.0f;
            float textY = inputY + 11.0f;
            float viewW = inputW - 20.0f;
            if (this.nameInputFocused) {
                this.nameInput.updateScroll(viewW, 13.0f);
            }
            NVGRenderer.pushScissor(inputX + 10.0f, inputY, viewW, inputH);
            if (this.nameInputFocused) {
                this.nameInput.renderSelection(textX, textY, 13.0f, 13.0f, theme.getSelection());
            }
            if ((charSequence2 = (CharSequence)this.nameInput.getText()).length() == 0) {
                boolean bl = false;
                charSequence = !this.nameInputFocused ? "Enter route name..." : "";
            } else {
                charSequence = charSequence2;
            }
            NVGRenderer.text$default((String)charSequence, textX - this.nameInput.getTextOffset(), textY, 13.0f, ((CharSequence)this.nameInput.getText()).length() == 0 ? theme.getTextSecondary() : theme.getText(), null, 32, null);
            if (this.nameInputFocused) {
                this.nameInput.renderCursor(textX, textY, 13.0f, theme.getText());
            }
            NVGRenderer.popScissor();
            float btnX = inputX + inputW + 12.0f;
            float btnW = 90.0f;
            float btnH = inputH;
            boolean canCreate = ((CharSequence)this.nameInput.getText()).length() > 0;
            boolean hovering = ExtensionsKt.isHoveringOver(btnX, inputY, btnW, btnH);
            int btnBg = hovering && canCreate ? theme.getAccent() : (canCreate ? theme.getControlBg() : theme.getOverlay());
            NVGRenderer.rect(btnX, inputY, btnW, btnH, btnBg, 6.0f);
            NVGRenderer.hollowRect(btnX, inputY, btnW, btnH, 1.0f, canCreate ? theme.getControlBorder() : theme.getOverlay(), 6.0f);
            String btnLabel = "Create";
            float btnLabelW = NVGRenderer.textWidth$default(btnLabel, 12.0f, null, 4, null);
            NVGRenderer.text$default(btnLabel, btnX + (btnW - btnLabelW) / 2.0f, inputY + 11.0f, 12.0f, hovering && canCreate ? theme.getTextOnAccent() : (canCreate ? theme.getText() : theme.getTextSecondary()), null, 32, null);
            String string = this.nameError;
            if (string == null) break block6;
            String err = string;
            boolean bl = false;
            NVGRenderer.text$default(err, inputX, inputY + inputH + 6.0f, 10.0f, -38037, null, 32, null);
        }
    }

    @Override
    public boolean mouseClicked(int button) {
        if (button != 0) {
            return super.mouseClicked(button);
        }
        RouteType selType = this.selectedType;
        if (selType != null) {
            float cardsStartY = this.getY() + this.topBar.getHeight() + 70.0f;
            float inputStartY = cardsStartY + this.cardGrid.contentHeight(this.typeCards.size()) + 24.0f;
            float inputX = this.getX() + 20.0f;
            float inputY = inputStartY + 18.0f;
            float inputW = 360.0f;
            float inputH = 34.0f;
            float btnX = inputX + inputW + 12.0f;
            float btnW = 90.0f;
            if (ExtensionsKt.isHoveringOver(inputX, inputY, inputW, inputH)) {
                this.nameInputFocused = true;
                this.nameInputDragging = true;
                boolean $i$f$getMouseX = false;
                this.nameInput.startSelection((float)class_310.method_1551().field_1729.method_1603(), inputX + 10.0f, 13.0f);
                return true;
            }
            if (ExtensionsKt.isHoveringOver(btnX, inputY, btnW, inputH)) {
                this.tryCreateRoute(selType);
                return true;
            }
            if (this.nameInputFocused && !ExtensionsKt.isHoveringOver(inputX, inputY, inputW, inputH)) {
                this.nameInputFocused = false;
            }
        }
        return super.mouseClicked(button);
    }

    @Override
    public boolean mouseReleased(int button) {
        if (button == 0) {
            this.nameInputDragging = false;
        }
        return super.mouseReleased(button);
    }

    @Override
    public boolean mouseDragged(int button, double offsetX, double offsetY) {
        if (button == 0 && this.nameInputDragging && this.nameInputFocused) {
            float cardsStartY = this.getY() + this.topBar.getHeight() + 70.0f;
            float inputStartY = cardsStartY + this.cardGrid.contentHeight(this.typeCards.size()) + 24.0f;
            float inputX = this.getX() + 20.0f;
            boolean $i$f$getMouseX = false;
            this.nameInput.updateSelection((float)class_310.method_1551().field_1729.method_1603(), inputX + 10.0f, 13.0f);
            return true;
        }
        return super.mouseDragged(button, offsetX, offsetY);
    }

    @Override
    public boolean charTyped(@NotNull class_11905 input) {
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        if (!this.nameInputFocused) {
            return false;
        }
        char ch = (char)input.comp_4793();
        if (ch >= ' ' && ch != '\u007f') {
            this.nameInput.insertText(String.valueOf(ch));
            this.nameError = null;
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(@NotNull class_11908 input) {
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        if (!this.nameInputFocused) {
            return false;
        }
        boolean ctrl = (input.comp_4797() & 2) != 0;
        boolean shift = (input.comp_4797() & 1) != 0;
        switch (input.comp_4795()) {
            case 257: {
                RouteType routeType = this.selectedType;
                if (routeType != null) {
                    RouteType it = routeType;
                    boolean bl = false;
                    this.tryCreateRoute(it);
                }
                return true;
            }
            case 256: {
                this.nameInputFocused = false;
                return true;
            }
            case 259: {
                this.nameInput.backspace();
                this.nameError = null;
                return true;
            }
            case 261: {
                this.nameInput.delete();
                this.nameError = null;
                return true;
            }
            case 263: {
                this.nameInput.moveCursorLeft(shift);
                return true;
            }
            case 262: {
                this.nameInput.moveCursorRight(shift);
                return true;
            }
            case 268: {
                this.nameInput.moveCursorToStart(shift);
                return true;
            }
            case 269: {
                this.nameInput.moveCursorToEnd(shift);
                return true;
            }
            case 65: {
                if (!ctrl) break;
                this.nameInput.selectAll();
                return true;
            }
            case 86: {
                if (!ctrl) break;
                String string = class_310.method_1551().field_1774.method_1460();
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getClipboard(...)");
                String clip = string;
                if (((CharSequence)clip).length() > 0) {
                    this.nameInput.insertText(clip);
                    this.nameError = null;
                }
                return true;
            }
            case 67: {
                if (!ctrl) break;
                String string = this.nameInput.copy();
                if (string != null) {
                    String it = string;
                    boolean bl = false;
                    class_310.method_1551().field_1774.method_1455(it);
                }
                return true;
            }
            case 88: {
                if (!ctrl) break;
                String string = this.nameInput.cut();
                if (string != null) {
                    String it = string;
                    boolean bl = false;
                    class_310.method_1551().field_1774.method_1455(it);
                    this.nameError = null;
                }
                return true;
            }
        }
        return false;
    }

    private final void tryCreateRoute(RouteType type) {
        boolean existing;
        String name;
        block6: {
            name = ((Object)StringsKt.trim((CharSequence)this.nameInput.getText())).toString();
            if (((CharSequence)name).length() == 0) {
                this.nameError = "Name cannot be empty.";
                return;
            }
            if (!RouteStore.INSTANCE.isValidName(name)) {
                this.nameError = "Name contains invalid characters.";
                return;
            }
            Iterable $this$any$iv = RouteStore.INSTANCE.loadAll();
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                v0 = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    SavedRoute it = (SavedRoute)element$iv;
                    boolean bl = false;
                    if (!StringsKt.equals((String)it.getName(), (String)name, (boolean)true)) continue;
                    v0 = true;
                    break block6;
                }
                v0 = existing = false;
            }
        }
        if (existing) {
            this.nameError = "A route with that name already exists.";
            return;
        }
        SavedRoute route = new SavedRoute(name, type, null, null, null, 28, null);
        RouteStore.INSTANCE.save(route);
        SubRouteKey firstSub = (SubRouteKey)((Object)CollectionsKt.first(RouteTypeKt.subRoutesFor(type)));
        RouteEditMode.INSTANCE.enterEdit(route, firstSub, (Function0<Unit>)((Function0)UINewRouteModal::tryCreateRoute$lambda$1));
    }

    private final void onTypeSelected(RouteType type) {
        this.selectedType = type;
        this.nameInputFocused = true;
        this.nameError = null;
    }

    private static final Unit backButton$lambda$0() {
        UIConfig.INSTANCE.swapBodyPanel(new UIRoutesPanel());
        return Unit.INSTANCE;
    }

    private static final Unit tryCreateRoute$lambda$1() {
        UIConfig.INSTANCE.swapBodyPanel(new UIRoutesPanel());
        UIConfig.INSTANCE.openUI();
        return Unit.INSTANCE;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UINewRouteModal$Companion;", "", "<init>", "()V", "Lorg/cobalt/internal/routes/RouteType;", "type", "", "typeDescription", "(Lorg/cobalt/internal/routes/RouteType;)Ljava/lang/String;", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        private final String typeDescription(RouteType type) {
            return switch (WhenMappings.$EnumSwitchMapping$0[type.ordinal()]) {
                case 1 -> "Travel + mining loop route";
                case 2 -> "Commission waypoint route";
                case 3 -> "Travel + combat patrol area";
                case 4 -> "Gemstone warp-and-mine loop";
                case 5 -> "Tunnel anchor route";
                default -> throw new NoWhenBranchMatchedException();
            };
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
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
                    nArray[RouteType.COMMISSION.ordinal()] = 2;
                }
                catch (NoSuchFieldError noSuchFieldError) {
                    // empty catch block
                }
                try {
                    nArray[RouteType.PATROL.ordinal()] = 3;
                }
                catch (NoSuchFieldError noSuchFieldError) {
                    // empty catch block
                }
                try {
                    nArray[RouteType.GEMSTONE.ordinal()] = 4;
                }
                catch (NoSuchFieldError noSuchFieldError) {
                    // empty catch block
                }
                try {
                    nArray[RouteType.TUNNEL.ordinal()] = 5;
                }
                catch (NoSuchFieldError noSuchFieldError) {
                    // empty catch block
                }
                $EnumSwitchMapping$0 = nArray;
            }
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000f\u0010\u0007\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\f\u0010\rR\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u000eR\u0014\u0010\u0010\u001a\u00020\u000f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0010\u0010\u0011R\u0016\u0010\u0012\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0013\u00a8\u0006\u0014"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UINewRouteModal$TypeCard;", "Lorg/cobalt/internal/ui/UIComponent;", "Lorg/cobalt/internal/routes/RouteType;", "type", "<init>", "(Lorg/cobalt/internal/ui/panel/panels/UINewRouteModal;Lorg/cobalt/internal/routes/RouteType;)V", "", "render", "()V", "", "button", "", "mouseClicked", "(I)Z", "Lorg/cobalt/internal/routes/RouteType;", "Lorg/cobalt/internal/ui/animation/ColorAnimation;", "colorAnim", "Lorg/cobalt/internal/ui/animation/ColorAnimation;", "wasHovering", "Z", "cobalt"})
    public final class TypeCard
    extends UIComponent {
        @NotNull
        private final RouteType type;
        @NotNull
        private final ColorAnimation colorAnim;
        private boolean wasHovering;

        public TypeCard(RouteType type) {
            Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
            super(0.0f, 0.0f, 255.0f, 100.0f);
            this.type = type;
            this.colorAnim = new ColorAnimation(160L);
        }

        @Override
        public void render() {
            boolean hovering = ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight());
            if (hovering != this.wasHovering) {
                this.colorAnim.start();
                this.wasHovering = hovering;
            }
            boolean selected = UINewRouteModal.this.selectedType == this.type;
            Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
            int bg = selected ? this.colorAnim.get(theme.getSelectedOverlay(), theme.getSelectedOverlay(), false) : this.colorAnim.get(theme.getControlBg(), theme.getOverlay(), !hovering);
            int borderColor = selected ? (int)this.type.getColor() : theme.getControlBorder();
            NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), bg, 10.0f);
            NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.5f, borderColor, 10.0f);
            NVGRenderer.rect(this.getX() + 14.0f, this.getY() + 16.0f, 6.0f, 24.0f, (int)this.type.getColor(), 3.0f);
            NVGRenderer.text$default(this.type.getLabel(), this.getX() + 28.0f, this.getY() + 18.0f, 14.0f, theme.getText(), null, 32, null);
            String desc = Companion.typeDescription(this.type);
            NVGRenderer.text$default(desc, this.getX() + 28.0f, this.getY() + 38.0f, 10.0f, theme.getTextSecondary(), null, 32, null);
            if (selected) {
                String check = "\u2713 Selected";
                float checkW = NVGRenderer.textWidth$default(check, 10.0f, null, 4, null);
                NVGRenderer.text$default(check, this.getX() + this.getWidth() - checkW - 14.0f, this.getY() + this.getHeight() - 18.0f, 10.0f, (int)this.type.getColor(), null, 32, null);
            }
        }

        @Override
        public boolean mouseClicked(int button) {
            if (button == 0 && ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
                UINewRouteModal.this.onTypeSelected(this.type);
                return true;
            }
            return false;
        }
    }
}

