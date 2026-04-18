/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.module.setting.impl;

import kotlin.Metadata;
import org.cobalt.api.ui.theme.ThemeManager;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0006\u001a\u00020\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007\u00a8\u0006\b"}, d2={"Lorg/cobalt/api/module/setting/impl/RainbowPhaseProvider;", "", "<init>", "()V", "", "speed", "getHue", "(F)F", "cobalt"})
public final class RainbowPhaseProvider {
    @NotNull
    public static final RainbowPhaseProvider INSTANCE = new RainbowPhaseProvider();

    private RainbowPhaseProvider() {
    }

    public final float getHue(float speed) {
        return ThemeManager.INSTANCE.getRainbowHue(speed);
    }

    public static /* synthetic */ float getHue$default(RainbowPhaseProvider rainbowPhaseProvider, float f, int n, Object object) {
        if ((n & 1) != 0) {
            f = 1.0f;
        }
        return rainbowPhaseProvider.getHue(f);
    }
}

