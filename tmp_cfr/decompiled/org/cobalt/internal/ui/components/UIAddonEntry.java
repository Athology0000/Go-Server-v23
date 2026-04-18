/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.components;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.addon.Addon;
import org.cobalt.api.addon.AddonMetadata;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Image;
import org.cobalt.internal.loader.AddonLoader;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.panel.panels.UIModuleList;
import org.cobalt.internal.ui.screen.UIConfig;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0000\u0018\u0000 \u00192\u00020\u0001:\u0001\u0019B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u000f\u0010\t\u001a\u00020\bH\u0016\u00a2\u0006\u0004\b\t\u0010\nJ\u0017\u0010\u000e\u001a\u00020\r2\u0006\u0010\f\u001a\u00020\u000bH\u0016\u00a2\u0006\u0004\b\u000e\u0010\u000fR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0010\u001a\u0004\b\u0011\u0010\u0012R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0013R\u0017\u0010\u0015\u001a\u00020\u00148\u0006\u00a2\u0006\f\n\u0004\b\u0015\u0010\u0016\u001a\u0004\b\u0017\u0010\u0018\u00a8\u0006\u001a"}, d2={"Lorg/cobalt/internal/ui/components/UIAddonEntry;", "Lorg/cobalt/internal/ui/UIComponent;", "Lorg/cobalt/api/addon/AddonMetadata;", "metadata", "Lorg/cobalt/api/addon/Addon;", "addon", "<init>", "(Lorg/cobalt/api/addon/AddonMetadata;Lorg/cobalt/api/addon/Addon;)V", "", "render", "()V", "", "button", "", "mouseClicked", "(I)Z", "Lorg/cobalt/api/addon/AddonMetadata;", "getMetadata", "()Lorg/cobalt/api/addon/AddonMetadata;", "Lorg/cobalt/api/addon/Addon;", "Lorg/cobalt/api/util/ui/helper/Image;", "addonIcon", "Lorg/cobalt/api/util/ui/helper/Image;", "getAddonIcon", "()Lorg/cobalt/api/util/ui/helper/Image;", "Companion", "cobalt"})
public final class UIAddonEntry
extends UIComponent {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final AddonMetadata metadata;
    @NotNull
    private final Addon addon;
    @NotNull
    private final Image addonIcon;
    @NotNull
    private static final Image boxIcon = NVGRenderer.createImage("/assets/cobalt/textures/ui/box.svg");

    public UIAddonEntry(@NotNull AddonMetadata metadata, @NotNull Addon addon) {
        Intrinsics.checkNotNullParameter((Object)metadata, (String)"metadata");
        Intrinsics.checkNotNullParameter((Object)addon, (String)"addon");
        super(0.0f, 0.0f, 270.0f, 70.0f);
        this.metadata = metadata;
        this.addon = addon;
        Image image = AddonLoader.INSTANCE.getAddonIcon(this.metadata.getId());
        if (image == null) {
            image = boxIcon;
        }
        this.addonIcon = image;
    }

    @NotNull
    public final AddonMetadata getMetadata() {
        return this.metadata;
    }

    @NotNull
    public final Image getAddonIcon() {
        return this.addonIcon;
    }

    @Override
    public void render() {
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ThemeManager.INSTANCE.getCurrentTheme().getPanel(), 10.0f);
        NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 10.0f);
        NVGRenderer.rect(this.getX() + 10.0f, this.getY() + this.getHeight() / 2.0f - 25.0f, 50.0f, 50.0f, ThemeManager.INSTANCE.getCurrentTheme().getInset(), 5.0f);
        NVGRenderer.image$default(this.addonIcon, this.getX() + 20.0f, this.getY() + this.getHeight() / 2.0f - 15.0f, 30.0f, 30.0f, 0.0f, Intrinsics.areEqual((Object)this.addonIcon, (Object)boxIcon) ? ThemeManager.INSTANCE.getCurrentTheme().getControlBorder() : 0, 32, null);
        NVGRenderer.text$default(this.metadata.getName(), this.getX() + 75.0f, this.getY() + (this.getHeight() - 29.0f) / 2.0f, 14.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
        NVGRenderer.text$default("v" + this.metadata.getVersion(), this.getX() + 75.0f, this.getY() + (this.getHeight() - 29.0f) / 2.0f + 17.0f, 12.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
    }

    @Override
    public boolean mouseClicked(int button) {
        if (ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight()) && (button == 0 || button == 1)) {
            UIConfig.INSTANCE.swapBodyPanel(new UIModuleList(this.metadata, this.addon));
            return true;
        }
        return false;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/internal/ui/components/UIAddonEntry$Companion;", "", "<init>", "()V", "Lorg/cobalt/api/util/ui/helper/Image;", "boxIcon", "Lorg/cobalt/api/util/ui/helper/Image;", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

