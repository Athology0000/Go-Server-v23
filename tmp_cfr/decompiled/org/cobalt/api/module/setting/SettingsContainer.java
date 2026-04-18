/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.module.setting;

import java.util.List;
import kotlin.Metadata;
import org.cobalt.api.module.setting.Setting;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J+\u0010\u0006\u001a\u00020\u00052\u001a\u0010\u0004\u001a\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\u00030\u0002\"\u0006\u0012\u0002\b\u00030\u0003H&\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0019\u0010\t\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00030\bH&\u00a2\u0006\u0004\b\t\u0010\n\u00a8\u0006\u000b\u00c0\u0006\u0003"}, d2={"Lorg/cobalt/api/module/setting/SettingsContainer;", "", "", "Lorg/cobalt/api/module/setting/Setting;", "settings", "", "addSetting", "([Lorg/cobalt/api/module/setting/Setting;)V", "", "getSettings", "()Ljava/util/List;", "cobalt"})
public interface SettingsContainer {
    public void addSetting(Setting<?> ... var1);

    @NotNull
    public List<Setting<?>> getSettings();
}

