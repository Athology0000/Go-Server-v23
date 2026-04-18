/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
 */
package org.cobalt;

import kotlin.Metadata;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.cobalt.internal.loader.AddonLoader;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\u0005\u0010\u0003\u00a8\u0006\u0006"}, d2={"Lorg/cobalt/PreLaunch;", "Lnet/fabricmc/loader/api/entrypoint/PreLaunchEntrypoint;", "<init>", "()V", "", "onPreLaunch", "cobalt"})
public final class PreLaunch
implements PreLaunchEntrypoint {
    public void onPreLaunch() {
        AddonLoader.INSTANCE.findAddons();
    }
}

