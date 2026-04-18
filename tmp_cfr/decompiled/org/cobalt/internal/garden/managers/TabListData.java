/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.garden.managers;

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u001a\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0006\u0010\t\u001a\u00020\b\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\rJ\u0016\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0010\u0010\u0011\u001a\u00020\bH\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u0012J>\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\b\b\u0002\u0010\t\u001a\u00020\bH\u00c6\u0001\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u001b\u0010\u0016\u001a\u00020\b2\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0018\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0018\u0010\rJ\u0011\u0010\u0019\u001a\u00020\u0006H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0019\u0010\u001aR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001b\u001a\u0004\b\u001c\u0010\rR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u001b\u001a\u0004\b\u001d\u0010\rR\u001d\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u001e\u001a\u0004\b\u001f\u0010\u0010R\u0017\u0010\t\u001a\u00020\b8\u0006\u00a2\u0006\f\n\u0004\b\t\u0010 \u001a\u0004\b!\u0010\u0012\u00a8\u0006\""}, d2={"Lorg/cobalt/internal/garden/managers/TabListData;", "", "", "alivePests", "cooldownSeconds", "", "", "infestedPlots", "", "bonusActive", "<init>", "(IILjava/util/List;Z)V", "component1", "()I", "component2", "component3", "()Ljava/util/List;", "component4", "()Z", "copy", "(IILjava/util/List;Z)Lorg/cobalt/internal/garden/managers/TabListData;", "other", "equals", "(Ljava/lang/Object;)Z", "hashCode", "toString", "()Ljava/lang/String;", "I", "getAlivePests", "getCooldownSeconds", "Ljava/util/List;", "getInfestedPlots", "Z", "getBonusActive", "cobalt"})
public final class TabListData {
    private final int alivePests;
    private final int cooldownSeconds;
    @NotNull
    private final List<String> infestedPlots;
    private final boolean bonusActive;

    public TabListData(int alivePests, int cooldownSeconds, @NotNull List<String> infestedPlots, boolean bonusActive) {
        Intrinsics.checkNotNullParameter(infestedPlots, (String)"infestedPlots");
        this.alivePests = alivePests;
        this.cooldownSeconds = cooldownSeconds;
        this.infestedPlots = infestedPlots;
        this.bonusActive = bonusActive;
    }

    public final int getAlivePests() {
        return this.alivePests;
    }

    public final int getCooldownSeconds() {
        return this.cooldownSeconds;
    }

    @NotNull
    public final List<String> getInfestedPlots() {
        return this.infestedPlots;
    }

    public final boolean getBonusActive() {
        return this.bonusActive;
    }

    public final int component1() {
        return this.alivePests;
    }

    public final int component2() {
        return this.cooldownSeconds;
    }

    @NotNull
    public final List<String> component3() {
        return this.infestedPlots;
    }

    public final boolean component4() {
        return this.bonusActive;
    }

    @NotNull
    public final TabListData copy(int alivePests, int cooldownSeconds, @NotNull List<String> infestedPlots, boolean bonusActive) {
        Intrinsics.checkNotNullParameter(infestedPlots, (String)"infestedPlots");
        return new TabListData(alivePests, cooldownSeconds, infestedPlots, bonusActive);
    }

    public static /* synthetic */ TabListData copy$default(TabListData tabListData, int n, int n2, List list, boolean bl, int n3, Object object) {
        if ((n3 & 1) != 0) {
            n = tabListData.alivePests;
        }
        if ((n3 & 2) != 0) {
            n2 = tabListData.cooldownSeconds;
        }
        if ((n3 & 4) != 0) {
            list = tabListData.infestedPlots;
        }
        if ((n3 & 8) != 0) {
            bl = tabListData.bonusActive;
        }
        return tabListData.copy(n, n2, list, bl);
    }

    @NotNull
    public String toString() {
        return "TabListData(alivePests=" + this.alivePests + ", cooldownSeconds=" + this.cooldownSeconds + ", infestedPlots=" + this.infestedPlots + ", bonusActive=" + this.bonusActive + ")";
    }

    public int hashCode() {
        int result = Integer.hashCode(this.alivePests);
        result = result * 31 + Integer.hashCode(this.cooldownSeconds);
        result = result * 31 + ((Object)this.infestedPlots).hashCode();
        result = result * 31 + Boolean.hashCode(this.bonusActive);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TabListData)) {
            return false;
        }
        TabListData tabListData = (TabListData)other;
        if (this.alivePests != tabListData.alivePests) {
            return false;
        }
        if (this.cooldownSeconds != tabListData.cooldownSeconds) {
            return false;
        }
        if (!Intrinsics.areEqual(this.infestedPlots, tabListData.infestedPlots)) {
            return false;
        }
        return this.bonusActive == tabListData.bonusActive;
    }
}

