/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_11905
 *  net.minecraft.class_11908
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.ui.components;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.components.UISearchBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000f\u0010\u0007\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\f\u0010\rJ\u0017\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\u000e\u0010\rJ'\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u000fH\u0016\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0017\u0010\u0016\u001a\u00020\u000b2\u0006\u0010\u0015\u001a\u00020\u0014H\u0016\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0017\u0010\u0019\u001a\u00020\u000b2\u0006\u0010\u0015\u001a\u00020\u0018H\u0016\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\r\u0010\u001b\u001a\u00020\u0002\u00a2\u0006\u0004\b\u001b\u0010\u001cJ\r\u0010\u001d\u001a\u00020\u0006\u00a2\u0006\u0004\b\u001d\u0010\bJ!\u0010 \u001a\u00020\u00062\u0012\u0010\u001f\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00060\u001e\u00a2\u0006\u0004\b \u0010!R\u0016\u0010\u0003\u001a\u00020\u00028\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\"R\u0014\u0010$\u001a\u00020#8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b$\u0010%R$\u0010&\u001a\u0010\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u001e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b&\u0010'R\u0016\u0010(\u001a\u00020\u00028\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b(\u0010\"\u00a8\u0006)"}, d2={"Lorg/cobalt/internal/ui/components/UITopbar;", "Lorg/cobalt/internal/ui/UIComponent;", "", "title", "<init>", "(Ljava/lang/String;)V", "", "render", "()V", "", "button", "", "mouseClicked", "(I)Z", "mouseReleased", "", "offsetX", "offsetY", "mouseDragged", "(IDD)Z", "Lnet/minecraft/class_11905;", "input", "charTyped", "(Lnet/minecraft/class_11905;)Z", "Lnet/minecraft/class_11908;", "keyPressed", "(Lnet/minecraft/class_11908;)Z", "getSearchText", "()Ljava/lang/String;", "clearSearch", "Lkotlin/Function1;", "callback", "searchChanged", "(Lkotlin/jvm/functions/Function1;)V", "Ljava/lang/String;", "Lorg/cobalt/internal/ui/components/UISearchBar;", "searchBar", "Lorg/cobalt/internal/ui/components/UISearchBar;", "onSearchChanged", "Lkotlin/jvm/functions/Function1;", "lastSearchText", "cobalt"})
public final class UITopbar
extends UIComponent {
    @NotNull
    private String title;
    @NotNull
    private final UISearchBar searchBar;
    @Nullable
    private Function1<? super String, Unit> onSearchChanged;
    @NotNull
    private String lastSearchText;

    public UITopbar(@NotNull String title) {
        Intrinsics.checkNotNullParameter((Object)title, (String)"title");
        super(0.0f, 0.0f, 890.0f, 70.0f);
        this.title = title;
        this.searchBar = new UISearchBar();
        this.lastSearchText = "";
    }

    @Override
    public void render() {
        block1: {
            NVGRenderer.text$default(this.title, this.getX() + 40.0f, this.getY() + this.getHeight() / (float)2 - 10.0f, 20.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
            NVGRenderer.line(this.getX(), this.getY() + this.getHeight(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getModuleDivider());
            this.searchBar.updateBounds(this.getX() + this.getWidth() - 320.0f, this.getY() + 15.0f).render();
            String currentSearchText = this.searchBar.getSearchText();
            if (Intrinsics.areEqual((Object)currentSearchText, (Object)this.lastSearchText)) break block1;
            this.lastSearchText = currentSearchText;
            Function1<? super String, Unit> function1 = this.onSearchChanged;
            if (function1 != null) {
                function1.invoke((Object)currentSearchText);
            }
        }
    }

    @Override
    public boolean mouseClicked(int button) {
        return this.searchBar.mouseClicked(button);
    }

    @Override
    public boolean mouseReleased(int button) {
        return this.searchBar.mouseReleased(button);
    }

    @Override
    public boolean mouseDragged(int button, double offsetX, double offsetY) {
        return this.searchBar.mouseDragged(button, offsetX, offsetY);
    }

    @Override
    public boolean charTyped(@NotNull class_11905 input) {
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        return this.searchBar.charTyped(input);
    }

    @Override
    public boolean keyPressed(@NotNull class_11908 input) {
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        return this.searchBar.keyPressed(input);
    }

    @NotNull
    public final String getSearchText() {
        return this.searchBar.getSearchText();
    }

    public final void clearSearch() {
        this.searchBar.clearSearch();
    }

    public final void searchChanged(@NotNull Function1<? super String, Unit> callback) {
        Intrinsics.checkNotNullParameter(callback, (String)"callback");
        this.onSearchChanged = callback;
    }
}

