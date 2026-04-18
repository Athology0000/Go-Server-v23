/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.routes;

import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.internal.routes.RoutePointType;
import org.cobalt.internal.routes.RouteType;
import org.cobalt.internal.routes.SubRouteKey;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=2, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\u001a#\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0001\u001a\u00020\u00002\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0006\u0010\u0007\u001a\u001b\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00020\u00042\u0006\u0010\u0001\u001a\u00020\u0000\u00a2\u0006\u0004\b\b\u0010\t\u00a8\u0006\n"}, d2={"Lorg/cobalt/internal/routes/RouteType;", "type", "Lorg/cobalt/internal/routes/SubRouteKey;", "sub", "", "Lorg/cobalt/internal/routes/RoutePointType;", "allowedPointTypes", "(Lorg/cobalt/internal/routes/RouteType;Lorg/cobalt/internal/routes/SubRouteKey;)Ljava/util/List;", "subRoutesFor", "(Lorg/cobalt/internal/routes/RouteType;)Ljava/util/List;", "cobalt"})
public final class RouteTypeKt {
    @NotNull
    public static final List<RoutePointType> allowedPointTypes(@NotNull RouteType type, @NotNull SubRouteKey sub) {
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        Intrinsics.checkNotNullParameter((Object)((Object)sub), (String)"sub");
        return switch (WhenMappings.$EnumSwitchMapping$1[type.ordinal()]) {
            case 1 -> {
                switch (WhenMappings.$EnumSwitchMapping$0[sub.ordinal()]) {
                    case 1: {
                        RoutePointType[] var2_2 = new RoutePointType[]{RoutePointType.WALK, RoutePointType.WARP};
                        yield CollectionsKt.listOf((Object[])var2_2);
                    }
                    case 2: {
                        RoutePointType[] var2_3 = new RoutePointType[]{RoutePointType.WARP, RoutePointType.MINE, RoutePointType.VEIN, RoutePointType.LANTERN};
                        yield CollectionsKt.listOf((Object[])var2_3);
                    }
                }
                yield CollectionsKt.emptyList();
            }
            case 2 -> {
                if (WhenMappings.$EnumSwitchMapping$0[sub.ordinal()] == 3) {
                    RoutePointType[] var2_4 = new RoutePointType[]{RoutePointType.WALK, RoutePointType.WARP, RoutePointType.MINE};
                    yield CollectionsKt.listOf((Object[])var2_4);
                }
                yield CollectionsKt.emptyList();
            }
            case 3 -> {
                switch (WhenMappings.$EnumSwitchMapping$0[sub.ordinal()]) {
                    case 1: {
                        RoutePointType[] var2_5 = new RoutePointType[]{RoutePointType.WALK, RoutePointType.WARP};
                        yield CollectionsKt.listOf((Object[])var2_5);
                    }
                    case 4: {
                        RoutePointType[] var2_6 = new RoutePointType[]{RoutePointType.WALK, RoutePointType.WARP, RoutePointType.KILL};
                        yield CollectionsKt.listOf((Object[])var2_6);
                    }
                }
                yield CollectionsKt.emptyList();
            }
            case 4 -> {
                if (WhenMappings.$EnumSwitchMapping$0[sub.ordinal()] == 3) {
                    RoutePointType[] var2_7 = new RoutePointType[]{RoutePointType.WARP, RoutePointType.MINE, RoutePointType.VEIN, RoutePointType.LANTERN};
                    yield CollectionsKt.listOf((Object[])var2_7);
                }
                yield CollectionsKt.emptyList();
            }
            case 5 -> {
                if (WhenMappings.$EnumSwitchMapping$0[sub.ordinal()] == 3) {
                    RoutePointType[] var2_8 = new RoutePointType[]{RoutePointType.WALK, RoutePointType.WARP, RoutePointType.MINE, RoutePointType.VEIN, RoutePointType.LANTERN};
                    yield CollectionsKt.listOf((Object[])var2_8);
                }
                yield CollectionsKt.emptyList();
            }
            default -> throw new NoWhenBranchMatchedException();
        };
    }

    @NotNull
    public static final List<SubRouteKey> subRoutesFor(@NotNull RouteType type) {
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        return switch (WhenMappings.$EnumSwitchMapping$1[type.ordinal()]) {
            case 1 -> {
                SubRouteKey[] var1_1 = new SubRouteKey[]{SubRouteKey.TRAVEL, SubRouteKey.LOOP};
                yield CollectionsKt.listOf((Object[])var1_1);
            }
            case 3 -> {
                SubRouteKey[] var1_2 = new SubRouteKey[]{SubRouteKey.TRAVEL, SubRouteKey.AREA};
                yield CollectionsKt.listOf((Object[])var1_2);
            }
            case 2, 4, 5 -> CollectionsKt.listOf((Object)((Object)SubRouteKey.POINTS));
            default -> throw new NoWhenBranchMatchedException();
        };
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;
        public static final /* synthetic */ int[] $EnumSwitchMapping$1;

        static {
            int[] nArray = new int[SubRouteKey.values().length];
            try {
                nArray[SubRouteKey.TRAVEL.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[SubRouteKey.LOOP.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[SubRouteKey.POINTS.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[SubRouteKey.AREA.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
            nArray = new int[RouteType.values().length];
            try {
                nArray[RouteType.ORE_MINER.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RouteType.COMMISSION.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RouteType.PATROL.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RouteType.GEMSTONE.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RouteType.TUNNEL.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$1 = nArray;
        }
    }
}

