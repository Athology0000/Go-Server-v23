/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.Triple
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.components.settings;

import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Triple;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.module.setting.impl.InfoSetting;
import org.cobalt.api.module.setting.impl.InfoType;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Image;
import org.cobalt.internal.ui.UIComponent;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J!\u0010\b\u001a\u0014\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00070\u0006H\u0002\u00a2\u0006\u0004\b\b\u0010\tJ\u000f\u0010\u000b\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u000b\u0010\fJ\u000f\u0010\u000e\u001a\u00020\rH\u0016\u00a2\u0006\u0004\b\u000e\u0010\u000fR\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u0010\u00a8\u0006\u0011"}, d2={"Lorg/cobalt/internal/ui/components/settings/UIInfoSetting;", "Lorg/cobalt/internal/ui/UIComponent;", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "setting", "<init>", "(Lorg/cobalt/api/module/setting/impl/InfoSetting;)V", "Lkotlin/Triple;", "", "getColors", "()Lkotlin/Triple;", "", "getIcon", "()Ljava/lang/String;", "", "render", "()V", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "cobalt"})
public final class UIInfoSetting
extends UIComponent {
    @NotNull
    private final InfoSetting setting;

    public UIInfoSetting(@NotNull InfoSetting setting) {
        Intrinsics.checkNotNullParameter((Object)setting, (String)"setting");
        super(0.0f, 0.0f, 627.5f, setting.getType() == InfoType.SEPARATOR ? 32.0f : 60.0f);
        this.setting = setting;
    }

    private final Triple<Integer, Integer, Integer> getColors() {
        return switch (WhenMappings.$EnumSwitchMapping$0[this.setting.getType().ordinal()]) {
            case 1 -> new Triple((Object)0, (Object)ThemeManager.INSTANCE.getCurrentTheme().getTextDisabled(), (Object)ThemeManager.INSTANCE.getCurrentTheme().getTextDisabled());
            case 2 -> new Triple((Object)ThemeManager.INSTANCE.getCurrentTheme().getInfoBackground(), (Object)ThemeManager.INSTANCE.getCurrentTheme().getInfoBorder(), (Object)ThemeManager.INSTANCE.getCurrentTheme().getInfoIcon());
            case 3 -> new Triple((Object)ThemeManager.INSTANCE.getCurrentTheme().getWarningBackground(), (Object)ThemeManager.INSTANCE.getCurrentTheme().getWarningBorder(), (Object)ThemeManager.INSTANCE.getCurrentTheme().getWarningIcon());
            case 4 -> new Triple((Object)ThemeManager.INSTANCE.getCurrentTheme().getSuccessBackground(), (Object)ThemeManager.INSTANCE.getCurrentTheme().getSuccessBorder(), (Object)ThemeManager.INSTANCE.getCurrentTheme().getSuccessIcon());
            case 5 -> new Triple((Object)ThemeManager.INSTANCE.getCurrentTheme().getErrorBackground(), (Object)ThemeManager.INSTANCE.getCurrentTheme().getErrorBorder(), (Object)ThemeManager.INSTANCE.getCurrentTheme().getErrorIcon());
            default -> throw new NoWhenBranchMatchedException();
        };
    }

    private final String getIcon() {
        return switch (WhenMappings.$EnumSwitchMapping$0[this.setting.getType().ordinal()]) {
            case 1 -> "";
            case 2 -> "/assets/cobalt/textures/ui/info.svg";
            case 3 -> "/assets/cobalt/textures/ui/warning.svg";
            case 4 -> "/assets/cobalt/textures/ui/checkmark.svg";
            case 5 -> "/assets/cobalt/textures/ui/error.svg";
            default -> throw new NoWhenBranchMatchedException();
        };
    }

    @Override
    public void render() {
        if (this.setting.getType() == InfoType.SEPARATOR) {
            float cy = this.getY() + this.getHeight() / 2.0f;
            String label = this.setting.getName();
            if (((CharSequence)label).length() == 0) {
                NVGRenderer.line(this.getX(), cy, this.getX() + this.getWidth(), cy, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextDisabled());
            } else {
                float textW = NVGRenderer.textWidth$default(label, 11.0f, null, 4, null);
                float gap = 8.0f;
                float lineY = cy;
                NVGRenderer.line(this.getX(), lineY, this.getX() + (this.getWidth() - textW) / 2.0f - gap, lineY, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextDisabled());
                NVGRenderer.text$default(label, this.getX() + (this.getWidth() - textW) / 2.0f, cy - 6.0f, 11.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
                NVGRenderer.line(this.getX() + (this.getWidth() + textW) / 2.0f + gap, lineY, this.getX() + this.getWidth(), lineY, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextDisabled());
            }
            return;
        }
        Triple<Integer, Integer, Integer> triple = this.getColors();
        int bgColor = ((Number)triple.component1()).intValue();
        int borderColor = ((Number)triple.component2()).intValue();
        int iconColor = ((Number)triple.component3()).intValue();
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), bgColor, 10.0f);
        NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.5f, borderColor, 10.0f);
        float iconSize = 24.0f;
        float iconX = this.getX() + 12.0f;
        float iconY = this.getY() + this.getHeight() / 2.0f - iconSize / 2.0f;
        try {
            Image icon = NVGRenderer.createImage(this.getIcon());
            NVGRenderer.image$default(icon, iconX, iconY, iconSize, iconSize, 0.0f, iconColor, 32, null);
        }
        catch (Exception icon) {
            // empty catch block
        }
        if (((CharSequence)this.setting.getName()).length() > 0) {
            float titleY = this.getY() + this.getHeight() / 2.0f - 14.0f;
            NVGRenderer.text$default(this.setting.getName(), this.getX() + 50.0f, titleY, 15.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
            float textY = this.getY() + this.getHeight() / 2.0f + 5.0f;
            NVGRenderer.text$default(this.setting.getText(), this.getX() + 50.0f, textY, 12.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
        } else {
            float textY = this.getY() + this.getHeight() / 2.0f - 6.0f;
            NVGRenderer.text$default(this.setting.getText(), this.getX() + 50.0f, textY, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[InfoType.values().length];
            try {
                nArray[InfoType.SEPARATOR.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[InfoType.INFO.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[InfoType.WARNING.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[InfoType.SUCCESS.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[InfoType.ERROR.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

