/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.grotto;

import kotlin.Metadata;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.internal.grotto.GrottoIntegration;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000f\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0006\u001a\u0004\b\u0007\u0010\bR\u0017\u0010\t\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\t\u0010\u0006\u001a\u0004\b\n\u0010\bR\u0017\u0010\u000b\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u000b\u0010\u0006\u001a\u0004\b\f\u0010\bR\u0017\u0010\r\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\r\u0010\u0006\u001a\u0004\b\u000e\u0010\bR\u0017\u0010\u000f\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u000f\u0010\u0006\u001a\u0004\b\u0010\u0010\bR\u0017\u0010\u0011\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0011\u0010\u0006\u001a\u0004\b\u0012\u0010\b\u00a8\u0006\u0013"}, d2={"Lorg/cobalt/internal/grotto/GrottoModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "getEnabled", "()Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "renderRoutes", "getRenderRoutes", "routeObstructionHighlights", "getRouteObstructionHighlights", "scannerEnabled", "getScannerEnabled", "scannerRenderBoxes", "getScannerRenderBoxes", "scannerRenderTracers", "getScannerRenderTracers", "cobalt"})
public final class GrottoModule
extends Module {
    @NotNull
    public static final GrottoModule INSTANCE = new GrottoModule();
    @NotNull
    private static final CheckboxSetting enabled = new CheckboxSetting("Enabled", "Enable Fairy Grotto routes and commands.", true);
    @NotNull
    private static final CheckboxSetting renderRoutes = new CheckboxSetting("Render Routes", "Render route lines in-world.", true);
    @NotNull
    private static final CheckboxSetting routeObstructionHighlights = new CheckboxSetting("Route Obstructions", "Highlight obstructing blocks along the current route.", true);
    @NotNull
    private static final CheckboxSetting scannerEnabled = new CheckboxSetting("Grotto Scanner", "Scan for magenta grotto blocks in the Crystal Hollows.", false);
    @NotNull
    private static final CheckboxSetting scannerRenderBoxes = new CheckboxSetting("Grotto Scanner Boxes", "Render ESP boxes on detected grotto blocks.", true);
    @NotNull
    private static final CheckboxSetting scannerRenderTracers = new CheckboxSetting("Grotto Scanner Tracers", "Render tracers to detected grotto blocks.", true);

    private GrottoModule() {
        super("Fairy Grotto");
    }

    @NotNull
    public final CheckboxSetting getEnabled() {
        return enabled;
    }

    @NotNull
    public final CheckboxSetting getRenderRoutes() {
        return renderRoutes;
    }

    @NotNull
    public final CheckboxSetting getRouteObstructionHighlights() {
        return routeObstructionHighlights;
    }

    @NotNull
    public final CheckboxSetting getScannerEnabled() {
        return scannerEnabled;
    }

    @NotNull
    public final CheckboxSetting getScannerRenderBoxes() {
        return scannerRenderBoxes;
    }

    @NotNull
    public final CheckboxSetting getScannerRenderTracers() {
        return scannerRenderTracers;
    }

    static {
        Setting[] settingArray = new Setting[6];
        settingArray[0] = enabled;
        settingArray[1] = renderRoutes;
        settingArray[2] = routeObstructionHighlights;
        settingArray[3] = scannerEnabled;
        settingArray[4] = scannerRenderBoxes;
        settingArray[5] = scannerRenderTracers;
        INSTANCE.addSetting(settingArray);
        GrottoIntegration.init();
    }
}

