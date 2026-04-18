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
 *  kotlin.text.StringsKt
 *  net.minecraft.class_11905
 *  net.minecraft.class_11908
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.panel.panels;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
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
import org.cobalt.internal.account.AccountManagerService;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.components.UITopbar;
import org.cobalt.internal.ui.panel.UIPanel;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.cobalt.internal.ui.util.ScrollHandler;
import org.cobalt.internal.ui.util.TextInputHandler;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0000\u0018\u0000 32\u00020\u0001:\u00044563B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u0017\u0010\t\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\t\u0010\nJ\u0017\u0010\u000b\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\u000b\u0010\nJ'\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\fH\u0016\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0017\u0010\u0013\u001a\u00020\b2\u0006\u0010\u0012\u001a\u00020\u0011H\u0016\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0017\u0010\u0016\u001a\u00020\b2\u0006\u0010\u0012\u001a\u00020\u0015H\u0016\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u001f\u0010\u001a\u001a\u00020\b2\u0006\u0010\u0018\u001a\u00020\f2\u0006\u0010\u0019\u001a\u00020\fH\u0016\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u000f\u0010\u001c\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u001c\u0010\u0003J\u0015\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u001e0\u001dH\u0002\u00a2\u0006\u0004\b\u001f\u0010 R\u0014\u0010\"\u001a\u00020!8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\"\u0010#R\u0014\u0010%\u001a\u00020$8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b%\u0010&R\u0014\u0010(\u001a\u00020'8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b(\u0010)R\u0014\u0010+\u001a\u00020*8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b+\u0010,R\u0016\u0010.\u001a\u00020-8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b.\u0010/R\u001c\u00101\u001a\b\u0012\u0004\u0012\u0002000\u001d8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b1\u00102\u00a8\u00067"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIAccountManagerPanel;", "Lorg/cobalt/internal/ui/panel/UIPanel;", "<init>", "()V", "", "render", "", "button", "", "mouseClicked", "(I)Z", "mouseReleased", "", "offsetX", "offsetY", "mouseDragged", "(IDD)Z", "Lnet/minecraft/class_11905;", "input", "charTyped", "(Lnet/minecraft/class_11905;)Z", "Lnet/minecraft/class_11908;", "keyPressed", "(Lnet/minecraft/class_11908;)Z", "horizontalAmount", "verticalAmount", "mouseScrolled", "(DD)Z", "renderSessionCard", "", "Lorg/cobalt/internal/account/AccountManagerService$ManagedAccount;", "filteredAccounts", "()Ljava/util/List;", "Lorg/cobalt/internal/ui/components/UITopbar;", "topBar", "Lorg/cobalt/internal/ui/components/UITopbar;", "Lorg/cobalt/internal/ui/panel/panels/UIAccountManagerPanel$UITextField;", "aliasInput", "Lorg/cobalt/internal/ui/panel/panels/UIAccountManagerPanel$UITextField;", "Lorg/cobalt/internal/ui/panel/panels/UIAccountManagerPanel$UIActionButton;", "loginButton", "Lorg/cobalt/internal/ui/panel/panels/UIAccountManagerPanel$UIActionButton;", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "scrollHandler", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "", "filterText", "Ljava/lang/String;", "Lorg/cobalt/internal/ui/panel/panels/UIAccountManagerPanel$UIAccountEntry;", "visibleEntries", "Ljava/util/List;", "Companion", "UIAccountEntry", "UIActionButton", "UITextField", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUIAccountManagerPanel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UIAccountManagerPanel.kt\norg/cobalt/internal/ui/panel/panels/UIAccountManagerPanel\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,461:1\n1596#2:462\n1629#2,4:463\n1915#2,2:467\n1807#2,3:469\n777#2:472\n873#2,2:473\n*S KotlinDebug\n*F\n+ 1 UIAccountManagerPanel.kt\norg/cobalt/internal/ui/panel/panels/UIAccountManagerPanel\n*L\n90#1:462\n90#1:463,4\n108#1:467,2\n119#1:469,3\n179#1:472\n179#1:473,2\n*E\n"})
public final class UIAccountManagerPanel
extends UIPanel {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final UITopbar topBar = new UITopbar("Accounts");
    @NotNull
    private final UITextField aliasInput = new UITextField("Microsoft alias...", 280.0f);
    @NotNull
    private final UIActionButton loginButton = new UIActionButton("Add / Login", 130.0f, false, () -> UIAccountManagerPanel.loginButton$lambda$0(this), 4, null);
    @NotNull
    private final ScrollHandler scrollHandler = new ScrollHandler(0.0f, 1, null);
    @NotNull
    private String filterText = "";
    @NotNull
    private List<UIAccountEntry> visibleEntries = CollectionsKt.emptyList();
    private static final float ENTRY_HEIGHT = 88.0f;
    private static final float ENTRY_GAP = 12.0f;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    public UIAccountManagerPanel() {
        super(0.0f, 0.0f, 890.0f, 600.0f);
        this.getComponents().add(this.topBar);
        this.getComponents().add(this.aliasInput);
        this.getComponents().add(this.loginButton);
        this.topBar.searchChanged((Function1<? super String, Unit>)((Function1)arg_0 -> UIAccountManagerPanel._init_$lambda$0(this, arg_0)));
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void render() {
        void $this$mapIndexedTo$iv$iv;
        void $this$mapIndexed$iv;
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), theme.getBackground(), 10.0f);
        this.topBar.updateBounds(this.getX(), this.getY()).render();
        this.renderSessionCard();
        float inputY = this.getY() + this.topBar.getHeight() + 106.0f;
        this.aliasInput.updateBounds(this.getX() + 24.0f, inputY).render();
        this.loginButton.setEnabled(!AccountManagerService.INSTANCE.isBusy()).updateBounds(this.getX() + 24.0f + this.aliasInput.getWidth() + 12.0f, inputY).render();
        NVGRenderer.text$default(AccountManagerService.INSTANCE.getStatusMessage(), this.getX() + 24.0f, this.getY() + this.topBar.getHeight() + 160.0f, 14.0f, theme.getTextSecondary(), null, 32, null);
        List<AccountManagerService.ManagedAccount> accounts = this.filteredAccounts();
        float listY = this.getY() + this.topBar.getHeight() + 190.0f;
        float visibleHeight = this.getHeight() - (listY - this.getY()) - 16.0f;
        this.scrollHandler.setMaxScroll((float)accounts.size() * 100.0f + 8.0f, visibleHeight);
        NVGRenderer.pushScissor(this.getX() + 16.0f, listY, this.getWidth() - 32.0f, visibleHeight);
        float scrollOffset = this.scrollHandler.getOffset();
        Iterable iterable = accounts;
        UIAccountManagerPanel uIAccountManagerPanel = this;
        boolean $i$f$mapIndexed = false;
        Iterator iterator = $this$mapIndexed$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$mapIndexed$iv, (int)10));
        boolean $i$f$mapIndexedTo = false;
        int index$iv$iv = 0;
        for (Object item$iv$iv : $this$mapIndexedTo$iv$iv) {
            void index;
            void account;
            UIAccountEntry uIAccountEntry;
            int n;
            if ((n = index$iv$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            AccountManagerService.ManagedAccount managedAccount = (AccountManagerService.ManagedAccount)item$iv$iv;
            int n2 = n;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            UIAccountEntry $this$render_u24lambda_u240_u240 = uIAccountEntry = new UIAccountEntry((AccountManagerService.ManagedAccount)account);
            boolean bl2 = false;
            $this$render_u24lambda_u240_u240.updateBounds($this$render_u24lambda_u240_u240.getX() + 20.0f, listY + 8.0f + (float)index * 100.0f - scrollOffset);
            collection.add(uIAccountEntry);
        }
        uIAccountManagerPanel.visibleEntries = (List)destination$iv$iv;
        if (this.visibleEntries.isEmpty()) {
            NVGRenderer.text$default((String)(StringsKt.isBlank((CharSequence)this.filterText) ? "No stored accounts yet." : "No accounts match \"" + this.filterText + "\"."), this.getX() + 28.0f, listY + 18.0f, 15.0f, theme.getTextSecondary(), null, 32, null);
        } else {
            Iterable $this$forEach$iv = this.visibleEntries;
            boolean $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                UIAccountEntry entry = (UIAccountEntry)element$iv;
                boolean bl = false;
                if (!(entry.getY() + entry.getHeight() >= listY - 12.0f) || !(entry.getY() <= listY + visibleHeight + 12.0f)) continue;
                entry.render();
            }
        }
        NVGRenderer.popScissor();
    }

    @Override
    public boolean mouseClicked(int button) {
        boolean bl;
        block3: {
            Iterable $this$any$iv = this.visibleEntries;
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                bl = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    UIAccountEntry it = (UIAccountEntry)element$iv;
                    boolean bl2 = false;
                    if (!it.mouseClicked(button)) continue;
                    bl = true;
                    break block3;
                }
                bl = false;
            }
        }
        return bl || this.loginButton.mouseClicked(button) || this.aliasInput.mouseClicked(button) || this.topBar.mouseClicked(button);
    }

    @Override
    public boolean mouseReleased(int button) {
        return this.aliasInput.mouseReleased(button) || this.topBar.mouseReleased(button);
    }

    @Override
    public boolean mouseDragged(int button, double offsetX, double offsetY) {
        return this.aliasInput.mouseDragged(button, offsetX, offsetY) || this.topBar.mouseDragged(button, offsetX, offsetY);
    }

    @Override
    public boolean charTyped(@NotNull class_11905 input) {
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        return this.aliasInput.charTyped(input) || this.topBar.charTyped(input);
    }

    @Override
    public boolean keyPressed(@NotNull class_11908 input) {
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        if (input.comp_4795() == 257 && !StringsKt.isBlank((CharSequence)this.aliasInput.getText()) && !AccountManagerService.INSTANCE.isBusy()) {
            AccountManagerService.INSTANCE.login(((Object)StringsKt.trim((CharSequence)this.aliasInput.getText())).toString());
            this.aliasInput.clear();
            return true;
        }
        return this.aliasInput.keyPressed(input) || this.topBar.keyPressed(input);
    }

    @Override
    public boolean mouseScrolled(double horizontalAmount, double verticalAmount) {
        if (ExtensionsKt.isHoveringOver(this.getX(), this.getY() + this.topBar.getHeight() + 180.0f, this.getWidth(), this.getHeight() - this.topBar.getHeight() - 180.0f)) {
            this.scrollHandler.handleScroll(verticalAmount);
            return true;
        }
        return false;
    }

    private final void renderSessionCard() {
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        float cardX = this.getX() + 20.0f;
        float cardY = this.getY() + this.topBar.getHeight() + 18.0f;
        float cardWidth = this.getWidth() - 40.0f;
        float cardHeight = 72.0f;
        NVGRenderer.rect(cardX, cardY, cardWidth, cardHeight, theme.getPanel(), 10.0f);
        NVGRenderer.hollowRect(cardX, cardY, cardWidth, cardHeight, 1.0f, theme.getControlBorder(), 10.0f);
        NVGRenderer.text$default("Current Session", cardX + 16.0f, cardY + 14.0f, 16.0f, theme.getText(), null, 32, null);
        NVGRenderer.text$default(AccountManagerService.INSTANCE.getCurrentSessionName(), cardX + 16.0f, cardY + 38.0f, 20.0f, theme.getAccent(), null, 32, null);
        NVGRenderer.text$default(AccountManagerService.INSTANCE.getCurrentSessionUuid(), cardX + 16.0f, cardY + 56.0f, 12.0f, theme.getTextSecondary(), null, 32, null);
    }

    /*
     * Unable to fully structure code
     */
    private final List<AccountManagerService.ManagedAccount> filteredAccounts() {
        v0 = this.filterText.toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue((Object)v0, (String)"toLowerCase(...)");
        query = v0;
        $this$filter$iv = AccountManagerService.INSTANCE.getAccounts();
        $i$f$filter = false;
        var4_4 = $this$filter$iv;
        destination$iv$iv = new ArrayList<E>();
        $i$f$filterTo = false;
        for (T element$iv$iv : $this$filterTo$iv$iv) {
            account = (AccountManagerService.ManagedAccount)element$iv$iv;
            $i$a$-filter-UIAccountManagerPanel$filteredAccounts$1 = false;
            if (StringsKt.isBlank((CharSequence)query)) ** GOTO lbl-1000
            v1 = account.getAlias().toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue((Object)v1, (String)"toLowerCase(...)");
            if (StringsKt.contains$default((CharSequence)v1, (CharSequence)query, (boolean)false, (int)2, null)) ** GOTO lbl-1000
            v2 = account.getMinecraftName().toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue((Object)v2, (String)"toLowerCase(...)");
            if (StringsKt.contains$default((CharSequence)v2, (CharSequence)query, (boolean)false, (int)2, null)) lbl-1000:
            // 3 sources

            {
                v3 = true;
            } else {
                v3 = false;
            }
            if (!v3) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        return (List)destination$iv$iv;
    }

    private static final Unit loginButton$lambda$0(UIAccountManagerPanel this$0) {
        String alias = ((Object)StringsKt.trim((CharSequence)this$0.aliasInput.getText())).toString();
        if (((CharSequence)alias).length() > 0) {
            AccountManagerService.INSTANCE.login(alias);
            this$0.aliasInput.clear();
        }
        return Unit.INSTANCE;
    }

    private static final Unit _init_$lambda$0(UIAccountManagerPanel this$0, String filter) {
        Intrinsics.checkNotNullParameter((Object)filter, (String)"filter");
        this$0.filterText = ((Object)StringsKt.trim((CharSequence)filter)).toString();
        return Unit.INSTANCE;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006R\u0014\u0010\u0007\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0007\u0010\u0006R\u001c\u0010\n\u001a\n \t*\u0004\u0018\u00010\b0\b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\n\u0010\u000b\u00a8\u0006\f"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIAccountManagerPanel$Companion;", "", "<init>", "()V", "", "ENTRY_HEIGHT", "F", "ENTRY_GAP", "Ljava/time/format/DateTimeFormatter;", "kotlin.jvm.PlatformType", "DATE_FORMAT", "Ljava/time/format/DateTimeFormatter;", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000f\u0010\u0007\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\f\u0010\rR\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u000eR\u0014\u0010\u0010\u001a\u00020\u000f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0010\u0010\u0011R\u0014\u0010\u0012\u001a\u00020\u000f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0011\u00a8\u0006\u0013"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIAccountManagerPanel$UIAccountEntry;", "Lorg/cobalt/internal/ui/UIComponent;", "Lorg/cobalt/internal/account/AccountManagerService$ManagedAccount;", "account", "<init>", "(Lorg/cobalt/internal/account/AccountManagerService$ManagedAccount;)V", "", "render", "()V", "", "button", "", "mouseClicked", "(I)Z", "Lorg/cobalt/internal/account/AccountManagerService$ManagedAccount;", "Lorg/cobalt/internal/ui/panel/panels/UIAccountManagerPanel$UIActionButton;", "useButton", "Lorg/cobalt/internal/ui/panel/panels/UIAccountManagerPanel$UIActionButton;", "removeButton", "cobalt"})
    private static final class UIAccountEntry
    extends UIComponent {
        @NotNull
        private final AccountManagerService.ManagedAccount account;
        @NotNull
        private final UIActionButton useButton;
        @NotNull
        private final UIActionButton removeButton;

        public UIAccountEntry(@NotNull AccountManagerService.ManagedAccount account) {
            Intrinsics.checkNotNullParameter((Object)account, (String)"account");
            super(0.0f, 0.0f, 850.0f, 88.0f);
            this.account = account;
            this.useButton = new UIActionButton("Use", 74.0f, false, () -> UIAccountEntry.useButton$lambda$0(this), 4, null);
            this.removeButton = new UIActionButton("Remove", 86.0f, true, (Function0<Unit>)((Function0)() -> UIAccountEntry.removeButton$lambda$0(this)));
        }

        @Override
        public void render() {
            Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
            boolean current = AccountManagerService.INSTANCE.isCurrentAccount(this.account);
            boolean busy = AccountManagerService.INSTANCE.isBusy();
            NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), theme.getPanel(), 10.0f);
            NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), current ? 2.0f : 1.0f, current ? theme.getAccent() : theme.getControlBorder(), 10.0f);
            NVGRenderer.text$default(this.account.getAlias(), this.getX() + 16.0f, this.getY() + 16.0f, 18.0f, theme.getText(), null, 32, null);
            String profileText = StringsKt.isBlank((CharSequence)this.account.getMinecraftName()) ? "No completed login yet" : "Last profile: " + this.account.getMinecraftName();
            NVGRenderer.text$default(profileText, this.getX() + 16.0f, this.getY() + 40.0f, 14.0f, theme.getTextSecondary(), null, 32, null);
            String lastLoginText = this.account.getLastLoginAt() <= 0L ? "Last login: never" : "Last login: " + DATE_FORMAT.format(Instant.ofEpochMilli(this.account.getLastLoginAt()));
            NVGRenderer.text$default(lastLoginText, this.getX() + 16.0f, this.getY() + 60.0f, 12.0f, theme.getTextSecondary(), null, 32, null);
            if (current) {
                float pillWidth = 72.0f;
                NVGRenderer.rect(this.getX() + this.getWidth() - 220.0f, this.getY() + 14.0f, pillWidth, 24.0f, theme.getAccent(), 8.0f);
                NVGRenderer.text$default("Current", this.getX() + this.getWidth() - 220.0f + 12.0f, this.getY() + 20.0f, 13.0f, theme.getTextOnAccent(), null, 32, null);
            }
            this.useButton.setEnabled(!busy).updateBounds(this.getX() + this.getWidth() - 182.0f, this.getY() + this.getHeight() - 40.0f).render();
            this.removeButton.setEnabled(!busy).updateBounds(this.getX() + this.getWidth() - 96.0f, this.getY() + this.getHeight() - 40.0f).render();
        }

        @Override
        public boolean mouseClicked(int button) {
            return this.useButton.mouseClicked(button) || this.removeButton.mouseClicked(button);
        }

        private static final Unit useButton$lambda$0(UIAccountEntry this$0) {
            AccountManagerService.INSTANCE.login(this$0.account.getAlias());
            return Unit.INSTANCE;
        }

        private static final Unit removeButton$lambda$0(UIAccountEntry this$0) {
            AccountManagerService.INSTANCE.remove(this$0.account.getAlias());
            return Unit.INSTANCE;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\b\b\u0002\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0006\u0012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0015\u0010\u000e\u001a\u00020\u00002\u0006\u0010\r\u001a\u00020\u0006\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u000f\u0010\u0010\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0017\u0010\u0014\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u0012H\u0016\u00a2\u0006\u0004\b\u0014\u0010\u0015R\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u0016R\u0014\u0010\u0007\u001a\u00020\u00068\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0007\u0010\u0017R\u001a\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\n\u0010\u0018R\u0016\u0010\u0019\u001a\u00020\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u0017\u00a8\u0006\u001a"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIAccountManagerPanel$UIActionButton;", "Lorg/cobalt/internal/ui/UIComponent;", "", "label", "", "width", "", "destructive", "Lkotlin/Function0;", "", "onClick", "<init>", "(Ljava/lang/String;FZLkotlin/jvm/functions/Function0;)V", "value", "setEnabled", "(Z)Lorg/cobalt/internal/ui/panel/panels/UIAccountManagerPanel$UIActionButton;", "render", "()V", "", "button", "mouseClicked", "(I)Z", "Ljava/lang/String;", "Z", "Lkotlin/jvm/functions/Function0;", "enabled", "cobalt"})
    private static final class UIActionButton
    extends UIComponent {
        @NotNull
        private final String label;
        private final boolean destructive;
        @NotNull
        private final Function0<Unit> onClick;
        private boolean enabled;

        public UIActionButton(@NotNull String label, float width, boolean destructive, @NotNull Function0<Unit> onClick) {
            Intrinsics.checkNotNullParameter((Object)label, (String)"label");
            Intrinsics.checkNotNullParameter(onClick, (String)"onClick");
            super(0.0f, 0.0f, width, 30.0f);
            this.label = label;
            this.destructive = destructive;
            this.onClick = onClick;
            this.enabled = true;
        }

        public /* synthetic */ UIActionButton(String string, float f, boolean bl, Function0 function0, int n, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n & 4) != 0) {
                bl = false;
            }
            this(string, f, bl, (Function0<Unit>)function0);
        }

        @NotNull
        public final UIActionButton setEnabled(boolean value) {
            this.enabled = value;
            return this;
        }

        @Override
        public void render() {
            Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
            boolean hovering = this.enabled && ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight());
            int background = !this.enabled ? theme.getControlBg() : (this.destructive ? (hovering ? -6668995 : -9622484) : (hovering ? theme.getAccentSecondary() : theme.getControlBg()));
            int border = !this.enabled ? theme.getControlBorder() : (this.destructive ? -5220521 : (hovering ? theme.getAccent() : theme.getControlBorder()));
            int textColor = this.enabled && (hovering || this.destructive) ? theme.getTextOnAccent() : theme.getText();
            NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), background, 8.0f);
            NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.0f, border, 8.0f);
            NVGRenderer.text$default(this.label, this.getX() + this.getWidth() / 2.0f - NVGRenderer.textWidth$default(this.label, 13.0f, null, 4, null) / 2.0f, this.getY() + 9.0f, 13.0f, textColor, null, 32, null);
        }

        @Override
        public boolean mouseClicked(int button) {
            if (!this.enabled || button != 0 || !ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
                return false;
            }
            this.onClick.invoke();
            return true;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0002\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\r\u0010\b\u001a\u00020\u0002\u00a2\u0006\u0004\b\b\u0010\tJ\r\u0010\u000b\u001a\u00020\n\u00a2\u0006\u0004\b\u000b\u0010\fJ\u000f\u0010\r\u001a\u00020\nH\u0016\u00a2\u0006\u0004\b\r\u0010\fJ\u0017\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u000f\u001a\u00020\u000eH\u0016\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0017\u0010\u0013\u001a\u00020\u00102\u0006\u0010\u000f\u001a\u00020\u000eH\u0016\u00a2\u0006\u0004\b\u0013\u0010\u0012J'\u0010\u0017\u001a\u00020\u00102\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u0014H\u0016\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u0017\u0010\u001b\u001a\u00020\u00102\u0006\u0010\u001a\u001a\u00020\u0019H\u0016\u00a2\u0006\u0004\b\u001b\u0010\u001cJ\u0017\u0010\u001e\u001a\u00020\u00102\u0006\u0010\u001a\u001a\u00020\u001dH\u0016\u00a2\u0006\u0004\b\u001e\u0010\u001fR\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010 R\u0014\u0010\"\u001a\u00020!8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\"\u0010#R\u0016\u0010$\u001a\u00020\u00108\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b$\u0010%R\u0016\u0010&\u001a\u00020\u00108\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b&\u0010%\u00a8\u0006'"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIAccountManagerPanel$UITextField;", "Lorg/cobalt/internal/ui/UIComponent;", "", "placeholder", "", "width", "<init>", "(Ljava/lang/String;F)V", "getText", "()Ljava/lang/String;", "", "clear", "()V", "render", "", "button", "", "mouseClicked", "(I)Z", "mouseReleased", "", "offsetX", "offsetY", "mouseDragged", "(IDD)Z", "Lnet/minecraft/class_11905;", "input", "charTyped", "(Lnet/minecraft/class_11905;)Z", "Lnet/minecraft/class_11908;", "keyPressed", "(Lnet/minecraft/class_11908;)Z", "Ljava/lang/String;", "Lorg/cobalt/internal/ui/util/TextInputHandler;", "inputHandler", "Lorg/cobalt/internal/ui/util/TextInputHandler;", "focused", "Z", "dragging", "cobalt"})
    @SourceDebugExtension(value={"SMAP\nUIAccountManagerPanel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UIAccountManagerPanel.kt\norg/cobalt/internal/ui/panel/panels/UIAccountManagerPanel$UITextField\n+ 2 Extensions.kt\norg/cobalt/internal/ui/util/ExtensionsKt\n*L\n1#1,461:1\n6#2:462\n6#2:463\n*S KotlinDebug\n*F\n+ 1 UIAccountManagerPanel.kt\norg/cobalt/internal/ui/panel/panels/UIAccountManagerPanel$UITextField\n*L\n366#1:462\n385#1:463\n*E\n"})
    private static final class UITextField
    extends UIComponent {
        @NotNull
        private final String placeholder;
        @NotNull
        private final TextInputHandler inputHandler;
        private boolean focused;
        private boolean dragging;

        public UITextField(@NotNull String placeholder, float width) {
            Intrinsics.checkNotNullParameter((Object)placeholder, (String)"placeholder");
            super(0.0f, 0.0f, width, 36.0f);
            this.placeholder = placeholder;
            this.inputHandler = new TextInputHandler("", 64);
        }

        @NotNull
        public final String getText() {
            return this.inputHandler.getText();
        }

        public final void clear() {
            this.inputHandler.setText("");
            this.focused = false;
        }

        @Override
        public void render() {
            String text;
            Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
            int borderColor = this.focused ? theme.getAccent() : theme.getControlBorder();
            NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), theme.getInputBg(), 6.0f);
            NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.5f, borderColor, 6.0f);
            float textX = this.getX() + 12.0f;
            float textY = this.getY() + 12.0f;
            if (this.focused) {
                this.inputHandler.updateScroll(this.getWidth() - 24.0f, 13.0f);
            }
            NVGRenderer.pushScissor(this.getX() + 10.0f, this.getY() + 5.0f, this.getWidth() - 20.0f, this.getHeight() - 10.0f);
            if (this.focused) {
                this.inputHandler.renderSelection(textX, textY, 13.0f, 13.0f, theme.getSelection());
            }
            if (((CharSequence)(text = this.inputHandler.getText())).length() == 0 && !this.focused) {
                NVGRenderer.text$default(this.placeholder, textX, textY, 13.0f, theme.getSearchPlaceholderText(), null, 32, null);
            } else {
                NVGRenderer.text$default(text, textX - this.inputHandler.getTextOffset(), textY, 13.0f, theme.getText(), null, 32, null);
            }
            if (this.focused) {
                this.inputHandler.renderCursor(textX, textY, 13.0f, theme.getText());
            }
            NVGRenderer.popScissor();
        }

        @Override
        public boolean mouseClicked(int button) {
            if (button != 0) {
                return false;
            }
            if (ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
                this.focused = true;
                this.dragging = true;
                boolean $i$f$getMouseX = false;
                this.inputHandler.startSelection((float)class_310.method_1551().field_1729.method_1603(), this.getX() + 12.0f, 13.0f);
                return true;
            }
            if (this.focused) {
                this.focused = false;
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseReleased(int button) {
            if (button == 0) {
                this.dragging = false;
            }
            return false;
        }

        @Override
        public boolean mouseDragged(int button, double offsetX, double offsetY) {
            if (button == 0 && this.dragging && this.focused) {
                boolean $i$f$getMouseX = false;
                this.inputHandler.updateSelection((float)class_310.method_1551().field_1729.method_1603(), this.getX() + 12.0f, 13.0f);
                return true;
            }
            return false;
        }

        @Override
        public boolean charTyped(@NotNull class_11905 input) {
            Intrinsics.checkNotNullParameter((Object)input, (String)"input");
            if (!this.focused) {
                return false;
            }
            char c = (char)input.comp_4793();
            if (c >= ' ' && c != '\u007f') {
                this.inputHandler.insertText(String.valueOf(c));
                return true;
            }
            return false;
        }

        @Override
        public boolean keyPressed(@NotNull class_11908 input) {
            Intrinsics.checkNotNullParameter((Object)input, (String)"input");
            if (!this.focused) {
                return false;
            }
            boolean ctrl = (input.comp_4797() & 2) != 0;
            boolean shift = (input.comp_4797() & 1) != 0;
            switch (input.comp_4795()) {
                case 256: {
                    this.focused = false;
                    return true;
                }
                case 259: {
                    this.inputHandler.backspace();
                    return true;
                }
                case 261: {
                    this.inputHandler.delete();
                    return true;
                }
                case 263: {
                    this.inputHandler.moveCursorLeft(shift);
                    return true;
                }
                case 262: {
                    this.inputHandler.moveCursorRight(shift);
                    return true;
                }
                case 268: {
                    this.inputHandler.moveCursorToStart(shift);
                    return true;
                }
                case 269: {
                    this.inputHandler.moveCursorToEnd(shift);
                    return true;
                }
                case 65: {
                    if (!ctrl) break;
                    this.inputHandler.selectAll();
                    return true;
                }
            }
            return false;
        }
    }
}

