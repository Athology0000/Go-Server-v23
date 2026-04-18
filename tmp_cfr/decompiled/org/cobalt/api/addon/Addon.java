/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.addon;

import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import org.cobalt.api.module.Module;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\b&\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H&\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u000f\u0010\u0006\u001a\u00020\u0004H&\u00a2\u0006\u0004\b\u0006\u0010\u0003J\u0015\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u0016\u00a2\u0006\u0004\b\t\u0010\n\u00a8\u0006\u000b"}, d2={"Lorg/cobalt/api/addon/Addon;", "", "<init>", "()V", "", "onLoad", "onUnload", "", "Lorg/cobalt/api/module/Module;", "getModules", "()Ljava/util/List;", "cobalt"})
public abstract class Addon {
    public abstract void onLoad();

    public abstract void onUnload();

    @NotNull
    public List<Module> getModules() {
        return CollectionsKt.emptyList();
    }
}

