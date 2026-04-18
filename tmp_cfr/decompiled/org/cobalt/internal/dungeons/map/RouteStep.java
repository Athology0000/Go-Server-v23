/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_2338
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.dungeons.map;

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_2338;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0014\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0010\b\u0086\b\u0018\u00002\u00020\u0001By\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\u000b\u001a\u00020\n\u0012\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\r0\u0002\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0016\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0016\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0013\u0010\u0012J\u0016\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0014\u0010\u0012J\u0016\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0015\u0010\u0012J\u0016\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0016\u0010\u0012J\u0010\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u0010\u0010\u0019\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\u0016\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u001b\u0010\u0012J\u0016\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\r0\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u001c\u0010\u0012J\u0094\u0001\u0010\u001d\u001a\u00020\u00002\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00022\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00030\u00022\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u00022\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00030\u00022\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00030\u00022\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\n2\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00030\u00022\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\r0\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\u001d\u0010\u001eJ\u001b\u0010!\u001a\u00020 2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b!\u0010\"J\u0011\u0010$\u001a\u00020#H\u00d6\u0081\u0004\u00a2\u0006\u0004\b$\u0010%J\u0011\u0010&\u001a\u00020\nH\u00d6\u0081\u0004\u00a2\u0006\u0004\b&\u0010\u001aR\u001d\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010'\u001a\u0004\b(\u0010\u0012R\u001d\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00030\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010'\u001a\u0004\b)\u0010\u0012R\u001d\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010'\u001a\u0004\b*\u0010\u0012R\u001d\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00030\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010'\u001a\u0004\b+\u0010\u0012R\u001d\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00030\u00028\u0006\u00a2\u0006\f\n\u0004\b\b\u0010'\u001a\u0004\b,\u0010\u0012R\u0017\u0010\t\u001a\u00020\u00038\u0006\u00a2\u0006\f\n\u0004\b\t\u0010-\u001a\u0004\b.\u0010\u0018R\u0017\u0010\u000b\u001a\u00020\n8\u0006\u00a2\u0006\f\n\u0004\b\u000b\u0010/\u001a\u0004\b0\u0010\u001aR\u001d\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00030\u00028\u0006\u00a2\u0006\f\n\u0004\b\f\u0010'\u001a\u0004\b1\u0010\u0012R\u001d\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\r0\u00028\u0006\u00a2\u0006\f\n\u0004\b\u000e\u0010'\u001a\u0004\b2\u0010\u0012\u00a8\u00063"}, d2={"Lorg/cobalt/internal/dungeons/map/RouteStep;", "", "", "Lnet/minecraft/class_2338;", "pathLocations", "etherwarps", "mines", "interacts", "tnts", "secretPos", "", "secretType", "enderPearls", "", "enderPearlAngles", "<init>", "(Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Lnet/minecraft/class_2338;Ljava/lang/String;Ljava/util/List;Ljava/util/List;)V", "component1", "()Ljava/util/List;", "component2", "component3", "component4", "component5", "component6", "()Lnet/minecraft/class_2338;", "component7", "()Ljava/lang/String;", "component8", "component9", "copy", "(Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Lnet/minecraft/class_2338;Ljava/lang/String;Ljava/util/List;Ljava/util/List;)Lorg/cobalt/internal/dungeons/map/RouteStep;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/util/List;", "getPathLocations", "getEtherwarps", "getMines", "getInteracts", "getTnts", "Lnet/minecraft/class_2338;", "getSecretPos", "Ljava/lang/String;", "getSecretType", "getEnderPearls", "getEnderPearlAngles", "cobalt"})
public final class RouteStep {
    @NotNull
    private final List<class_2338> pathLocations;
    @NotNull
    private final List<class_2338> etherwarps;
    @NotNull
    private final List<class_2338> mines;
    @NotNull
    private final List<class_2338> interacts;
    @NotNull
    private final List<class_2338> tnts;
    @NotNull
    private final class_2338 secretPos;
    @NotNull
    private final String secretType;
    @NotNull
    private final List<class_2338> enderPearls;
    @NotNull
    private final List<float[]> enderPearlAngles;

    public RouteStep(@NotNull List<? extends class_2338> pathLocations, @NotNull List<? extends class_2338> etherwarps, @NotNull List<? extends class_2338> mines, @NotNull List<? extends class_2338> interacts, @NotNull List<? extends class_2338> tnts, @NotNull class_2338 secretPos, @NotNull String secretType, @NotNull List<? extends class_2338> enderPearls, @NotNull List<float[]> enderPearlAngles) {
        Intrinsics.checkNotNullParameter(pathLocations, (String)"pathLocations");
        Intrinsics.checkNotNullParameter(etherwarps, (String)"etherwarps");
        Intrinsics.checkNotNullParameter(mines, (String)"mines");
        Intrinsics.checkNotNullParameter(interacts, (String)"interacts");
        Intrinsics.checkNotNullParameter(tnts, (String)"tnts");
        Intrinsics.checkNotNullParameter((Object)secretPos, (String)"secretPos");
        Intrinsics.checkNotNullParameter((Object)secretType, (String)"secretType");
        Intrinsics.checkNotNullParameter(enderPearls, (String)"enderPearls");
        Intrinsics.checkNotNullParameter(enderPearlAngles, (String)"enderPearlAngles");
        this.pathLocations = pathLocations;
        this.etherwarps = etherwarps;
        this.mines = mines;
        this.interacts = interacts;
        this.tnts = tnts;
        this.secretPos = secretPos;
        this.secretType = secretType;
        this.enderPearls = enderPearls;
        this.enderPearlAngles = enderPearlAngles;
    }

    @NotNull
    public final List<class_2338> getPathLocations() {
        return this.pathLocations;
    }

    @NotNull
    public final List<class_2338> getEtherwarps() {
        return this.etherwarps;
    }

    @NotNull
    public final List<class_2338> getMines() {
        return this.mines;
    }

    @NotNull
    public final List<class_2338> getInteracts() {
        return this.interacts;
    }

    @NotNull
    public final List<class_2338> getTnts() {
        return this.tnts;
    }

    @NotNull
    public final class_2338 getSecretPos() {
        return this.secretPos;
    }

    @NotNull
    public final String getSecretType() {
        return this.secretType;
    }

    @NotNull
    public final List<class_2338> getEnderPearls() {
        return this.enderPearls;
    }

    @NotNull
    public final List<float[]> getEnderPearlAngles() {
        return this.enderPearlAngles;
    }

    @NotNull
    public final List<class_2338> component1() {
        return this.pathLocations;
    }

    @NotNull
    public final List<class_2338> component2() {
        return this.etherwarps;
    }

    @NotNull
    public final List<class_2338> component3() {
        return this.mines;
    }

    @NotNull
    public final List<class_2338> component4() {
        return this.interacts;
    }

    @NotNull
    public final List<class_2338> component5() {
        return this.tnts;
    }

    @NotNull
    public final class_2338 component6() {
        return this.secretPos;
    }

    @NotNull
    public final String component7() {
        return this.secretType;
    }

    @NotNull
    public final List<class_2338> component8() {
        return this.enderPearls;
    }

    @NotNull
    public final List<float[]> component9() {
        return this.enderPearlAngles;
    }

    @NotNull
    public final RouteStep copy(@NotNull List<? extends class_2338> pathLocations, @NotNull List<? extends class_2338> etherwarps, @NotNull List<? extends class_2338> mines, @NotNull List<? extends class_2338> interacts, @NotNull List<? extends class_2338> tnts, @NotNull class_2338 secretPos, @NotNull String secretType, @NotNull List<? extends class_2338> enderPearls, @NotNull List<float[]> enderPearlAngles) {
        Intrinsics.checkNotNullParameter(pathLocations, (String)"pathLocations");
        Intrinsics.checkNotNullParameter(etherwarps, (String)"etherwarps");
        Intrinsics.checkNotNullParameter(mines, (String)"mines");
        Intrinsics.checkNotNullParameter(interacts, (String)"interacts");
        Intrinsics.checkNotNullParameter(tnts, (String)"tnts");
        Intrinsics.checkNotNullParameter((Object)secretPos, (String)"secretPos");
        Intrinsics.checkNotNullParameter((Object)secretType, (String)"secretType");
        Intrinsics.checkNotNullParameter(enderPearls, (String)"enderPearls");
        Intrinsics.checkNotNullParameter(enderPearlAngles, (String)"enderPearlAngles");
        return new RouteStep(pathLocations, etherwarps, mines, interacts, tnts, secretPos, secretType, enderPearls, enderPearlAngles);
    }

    public static /* synthetic */ RouteStep copy$default(RouteStep routeStep, List list, List list2, List list3, List list4, List list5, class_2338 class_23382, String string, List list6, List list7, int n, Object object) {
        if ((n & 1) != 0) {
            list = routeStep.pathLocations;
        }
        if ((n & 2) != 0) {
            list2 = routeStep.etherwarps;
        }
        if ((n & 4) != 0) {
            list3 = routeStep.mines;
        }
        if ((n & 8) != 0) {
            list4 = routeStep.interacts;
        }
        if ((n & 0x10) != 0) {
            list5 = routeStep.tnts;
        }
        if ((n & 0x20) != 0) {
            class_23382 = routeStep.secretPos;
        }
        if ((n & 0x40) != 0) {
            string = routeStep.secretType;
        }
        if ((n & 0x80) != 0) {
            list6 = routeStep.enderPearls;
        }
        if ((n & 0x100) != 0) {
            list7 = routeStep.enderPearlAngles;
        }
        return routeStep.copy(list, list2, list3, list4, list5, class_23382, string, list6, list7);
    }

    @NotNull
    public String toString() {
        return "RouteStep(pathLocations=" + this.pathLocations + ", etherwarps=" + this.etherwarps + ", mines=" + this.mines + ", interacts=" + this.interacts + ", tnts=" + this.tnts + ", secretPos=" + this.secretPos + ", secretType=" + this.secretType + ", enderPearls=" + this.enderPearls + ", enderPearlAngles=" + this.enderPearlAngles + ")";
    }

    public int hashCode() {
        int result = ((Object)this.pathLocations).hashCode();
        result = result * 31 + ((Object)this.etherwarps).hashCode();
        result = result * 31 + ((Object)this.mines).hashCode();
        result = result * 31 + ((Object)this.interacts).hashCode();
        result = result * 31 + ((Object)this.tnts).hashCode();
        result = result * 31 + this.secretPos.hashCode();
        result = result * 31 + this.secretType.hashCode();
        result = result * 31 + ((Object)this.enderPearls).hashCode();
        result = result * 31 + ((Object)this.enderPearlAngles).hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RouteStep)) {
            return false;
        }
        RouteStep routeStep = (RouteStep)other;
        if (!Intrinsics.areEqual(this.pathLocations, routeStep.pathLocations)) {
            return false;
        }
        if (!Intrinsics.areEqual(this.etherwarps, routeStep.etherwarps)) {
            return false;
        }
        if (!Intrinsics.areEqual(this.mines, routeStep.mines)) {
            return false;
        }
        if (!Intrinsics.areEqual(this.interacts, routeStep.interacts)) {
            return false;
        }
        if (!Intrinsics.areEqual(this.tnts, routeStep.tnts)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.secretPos, (Object)routeStep.secretPos)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.secretType, (Object)routeStep.secretType)) {
            return false;
        }
        if (!Intrinsics.areEqual(this.enderPearls, routeStep.enderPearls)) {
            return false;
        }
        return Intrinsics.areEqual(this.enderPearlAngles, routeStep.enderPearlAngles);
    }
}

