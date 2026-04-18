/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.JvmStatic
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.grotto;

import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import org.cobalt.api.event.EventBus;
import org.cobalt.internal.grotto.CrystalHollowsDetector;
import org.cobalt.internal.grotto.GrottoCommands;
import org.cobalt.internal.grotto.GrottoRouteRenderer;
import org.cobalt.internal.grotto.GrottoScanner;
import org.cobalt.internal.grotto.MansionDetector;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0005\u0010\u0003R\u0016\u0010\u0007\u001a\u00020\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2={"Lorg/cobalt/internal/grotto/GrottoIntegration;", "", "<init>", "()V", "", "init", "", "initialized", "Z", "cobalt"})
public final class GrottoIntegration {
    @NotNull
    public static final GrottoIntegration INSTANCE = new GrottoIntegration();
    private static boolean initialized;

    private GrottoIntegration() {
    }

    @JvmStatic
    public static final void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        GrottoRouteRenderer.init();
        GrottoCommands.register();
        EventBus.register(GrottoScanner.INSTANCE);
        EventBus.register(CrystalHollowsDetector.INSTANCE);
        EventBus.register(MansionDetector.INSTANCE);
    }
}

