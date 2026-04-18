/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.module.Module;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u0007\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u001d\u0010\f\u001a\u00020\t2\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u0000\u00a2\u0006\u0004\b\n\u0010\u000bR\u001a\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\r8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u000e\u0010\u000f\u00a8\u0006\u0010"}, d2={"Lorg/cobalt/api/module/ModuleManager;", "", "<init>", "()V", "", "Lorg/cobalt/api/module/Module;", "getModules", "()Ljava/util/List;", "modules", "", "addModules$cobalt", "(Ljava/util/List;)V", "addModules", "", "moduleList", "Ljava/util/List;", "cobalt"})
public final class ModuleManager {
    @NotNull
    public static final ModuleManager INSTANCE = new ModuleManager();
    @NotNull
    private static final List<Module> moduleList = new ArrayList();

    private ModuleManager() {
    }

    @JvmStatic
    @NotNull
    public static final List<Module> getModules() {
        return moduleList;
    }

    public final void addModules$cobalt(@NotNull List<? extends Module> modules) {
        Intrinsics.checkNotNullParameter(modules, (String)"modules");
        moduleList.addAll((Collection<Module>)modules);
    }
}

