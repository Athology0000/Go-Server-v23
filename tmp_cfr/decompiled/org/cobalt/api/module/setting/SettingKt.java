/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.module.setting;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.cobalt.api.module.setting.Setting;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=2, xi=48, d1={"\u0000\u0010\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\u001a/\u0010\u0005\u001a\u00028\u0001\"\u0004\b\u0000\u0010\u0000\"\u000e\b\u0001\u0010\u0002*\b\u0012\u0004\u0012\u00028\u00000\u0001*\u00028\u00012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2={"T", "Lorg/cobalt/api/module/setting/Setting;", "S", "", "group", "inGroup", "(Lorg/cobalt/api/module/setting/Setting;Ljava/lang/String;)Lorg/cobalt/api/module/setting/Setting;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nSetting.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Setting.kt\norg/cobalt/api/module/setting/SettingKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,65:1\n1#2:66\n*E\n"})
public final class SettingKt {
    @NotNull
    public static final <T, S extends Setting<T>> S inGroup(@NotNull S $this$inGroup, @NotNull String group) {
        CharSequence charSequence;
        Intrinsics.checkNotNullParameter($this$inGroup, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)group, (String)"group");
        S s = $this$inGroup;
        CharSequence charSequence2 = ((Object)StringsKt.trim((CharSequence)group)).toString();
        if (StringsKt.isBlank((CharSequence)charSequence2)) {
            S s2 = s;
            boolean bl = false;
            charSequence = "General";
            s = s2;
        } else {
            charSequence = charSequence2;
        }
        s.setUiGroup((String)charSequence);
        return $this$inGroup;
    }
}

