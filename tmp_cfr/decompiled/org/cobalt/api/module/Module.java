/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.SettingsContainer;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010!\n\u0002\b\u0004\b&\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J+\u0010\n\u001a\u00020\t2\u001a\u0010\b\u001a\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\u00070\u0006\"\u0006\u0012\u0002\b\u00030\u0007H\u0016\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0019\u0010\r\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00070\fH\u0016\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0015\u0010\u0011\u001a\u00020\t2\u0006\u0010\u0010\u001a\u00020\u000f\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0013\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u000f0\f\u00a2\u0006\u0004\b\u0013\u0010\u000eR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0014\u001a\u0004\b\u0015\u0010\u0016R\u001e\u0010\u0018\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00070\u00178\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0019R\u001a\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00178\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001a\u0010\u0019\u00a8\u0006\u001b"}, d2={"Lorg/cobalt/api/module/Module;", "Lorg/cobalt/api/module/setting/SettingsContainer;", "", "name", "<init>", "(Ljava/lang/String;)V", "", "Lorg/cobalt/api/module/setting/Setting;", "settings", "", "addSetting", "([Lorg/cobalt/api/module/setting/Setting;)V", "", "getSettings", "()Ljava/util/List;", "Lorg/cobalt/api/hud/HudElement;", "element", "addHudElement", "(Lorg/cobalt/api/hud/HudElement;)V", "getHudElements", "Ljava/lang/String;", "getName", "()Ljava/lang/String;", "", "settingsList", "Ljava/util/List;", "hudElementsList", "cobalt"})
public abstract class Module
implements SettingsContainer {
    @NotNull
    private final String name;
    @NotNull
    private final List<Setting<?>> settingsList;
    @NotNull
    private final List<HudElement> hudElementsList;

    public Module(@NotNull String name) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        this.name = name;
        this.settingsList = new ArrayList();
        this.hudElementsList = new ArrayList();
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    @Override
    public void addSetting(Setting<?> ... settings) {
        Intrinsics.checkNotNullParameter(settings, (String)"settings");
        this.settingsList.addAll(CollectionsKt.listOf((Object[])Arrays.copyOf(settings, settings.length)));
    }

    @Override
    @NotNull
    public List<Setting<?>> getSettings() {
        return this.settingsList;
    }

    public final void addHudElement(@NotNull HudElement element) {
        Intrinsics.checkNotNullParameter((Object)element, (String)"element");
        this.hudElementsList.add(element);
    }

    @NotNull
    public final List<HudElement> getHudElements() {
        return this.hudElementsList;
    }
}

