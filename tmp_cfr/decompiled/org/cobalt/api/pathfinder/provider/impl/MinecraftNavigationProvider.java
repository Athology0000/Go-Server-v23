/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_10
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_2241
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2323
 *  net.minecraft.class_2338
 *  net.minecraft.class_2349
 *  net.minecraft.class_2350$class_2351
 *  net.minecraft.class_2399
 *  net.minecraft.class_2482
 *  net.minecraft.class_2510
 *  net.minecraft.class_2541
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_2769
 *  net.minecraft.class_310
 *  net.minecraft.class_3481
 *  net.minecraft.class_3486
 *  net.minecraft.class_3610
 *  net.minecraft.class_3726
 *  net.minecraft.class_5800
 *  net.minecraft.class_638
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.pathfinder.provider.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_10;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2241;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2323;
import net.minecraft.class_2338;
import net.minecraft.class_2349;
import net.minecraft.class_2350;
import net.minecraft.class_2399;
import net.minecraft.class_2482;
import net.minecraft.class_2510;
import net.minecraft.class_2541;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2769;
import net.minecraft.class_310;
import net.minecraft.class_3481;
import net.minecraft.class_3486;
import net.minecraft.class_3610;
import net.minecraft.class_3726;
import net.minecraft.class_5800;
import net.minecraft.class_638;
import org.cobalt.api.pathfinder.pathing.context.EnvironmentContext;
import org.cobalt.api.pathfinder.provider.NavigationPoint;
import org.cobalt.api.pathfinder.provider.NavigationPointProvider;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0004\u0018\u0000 \u001f2\u00020\u0001:\u0001\u001fB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J!\u0010\t\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u00042\b\u0010\u0007\u001a\u0004\u0018\u00010\u0006H\u0016\u00a2\u0006\u0004\b\t\u0010\nJ'\u0010\u0012\u001a\u00020\u00112\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u000fH\u0002\u00a2\u0006\u0004\b\u0012\u0010\u0013J'\u0010\u0014\u001a\u00020\u00112\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u000fH\u0002\u00a2\u0006\u0004\b\u0014\u0010\u0013J\u001f\u0010\u0016\u001a\u00020\u00152\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u000fH\u0002\u00a2\u0006\u0004\b\u0016\u0010\u0017R\u0014\u0010\u0019\u001a\u00020\u00188\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u001aR \u0010\u001d\u001a\u000e\u0012\u0004\u0012\u00020\u001c\u0012\u0004\u0012\u00020\b0\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001d\u0010\u001e\u00a8\u0006 "}, d2={"Lorg/cobalt/api/pathfinder/provider/impl/MinecraftNavigationProvider;", "Lorg/cobalt/api/pathfinder/provider/NavigationPointProvider;", "<init>", "()V", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "position", "Lorg/cobalt/api/pathfinder/pathing/context/EnvironmentContext;", "environmentContext", "Lorg/cobalt/api/pathfinder/provider/NavigationPoint;", "getNavigationPoint", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/pathing/context/EnvironmentContext;)Lorg/cobalt/api/pathfinder/provider/NavigationPoint;", "Lnet/minecraft/class_1937;", "level", "Lnet/minecraft/class_2680;", "state", "Lnet/minecraft/class_2338;", "pos", "", "canWalkThrough", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2680;Lnet/minecraft/class_2338;)Z", "canWalkOn", "", "calculateFloorLevel", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)D", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Ljava/util/concurrent/ConcurrentHashMap;", "", "cache", "Ljava/util/concurrent/ConcurrentHashMap;", "Companion", "cobalt"})
@SourceDebugExtension(value={"SMAP\nMinecraftNavigationProvider.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MinecraftNavigationProvider.kt\norg/cobalt/api/pathfinder/provider/impl/MinecraftNavigationProvider\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,157:1\n1#2:158\n*E\n"})
public final class MinecraftNavigationProvider
implements NavigationPointProvider {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final class_310 mc;
    @NotNull
    private final ConcurrentHashMap<Long, NavigationPoint> cache;
    private static final int MAX_CACHE_SIZE = 50000;

    public MinecraftNavigationProvider() {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        this.mc = class_3102;
        this.cache = new ConcurrentHashMap(8192);
    }

    @Override
    @NotNull
    public NavigationPoint getNavigationPoint(@NotNull PathPosition position, @Nullable EnvironmentContext environmentContext) {
        int z;
        int y;
        Intrinsics.checkNotNullParameter((Object)position, (String)"position");
        class_638 class_6382 = this.mc.field_1687;
        if (class_6382 == null) {
            return new NavigationPoint(){

                public boolean isTraversable() {
                    return false;
                }

                public boolean hasFloor() {
                    return false;
                }

                public double getFloorLevel() {
                    return 0.0;
                }

                public boolean isClimbable() {
                    return false;
                }

                public boolean isLiquid() {
                    return false;
                }
            };
        }
        class_638 level2 = class_6382;
        int x = position.getFlooredX();
        class_2338 blockPos = new class_2338(x, y = position.getFlooredY(), z = position.getFlooredZ());
        long key = blockPos.method_10063();
        NavigationPoint navigationPoint = this.cache.get(key);
        if (navigationPoint != null) {
            NavigationPoint it = navigationPoint;
            boolean bl = false;
            return it;
        }
        class_2680 class_26802 = level2.method_8320(blockPos);
        Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
        class_2680 feetState = class_26802;
        class_2680 class_26803 = level2.method_8320(blockPos.method_10084());
        Intrinsics.checkNotNullExpressionValue((Object)class_26803, (String)"getBlockState(...)");
        class_2680 headState = class_26803;
        class_2680 class_26804 = level2.method_8320(blockPos.method_10074());
        Intrinsics.checkNotNullExpressionValue((Object)class_26804, (String)"getBlockState(...)");
        class_2680 belowState = class_26804;
        boolean canPassFeetVal = this.canWalkThrough((class_1937)level2, feetState, blockPos);
        class_1937 class_19372 = (class_1937)level2;
        class_2338 class_23382 = blockPos.method_10084();
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"above(...)");
        boolean canPassHeadVal = this.canWalkThrough(class_19372, headState, class_23382);
        class_1937 class_19373 = (class_1937)level2;
        class_2338 class_23383 = blockPos.method_10074();
        Intrinsics.checkNotNullExpressionValue((Object)class_23383, (String)"below(...)");
        boolean hasStableFloorVal = this.canWalkOn(class_19373, belowState, class_23383);
        double floorLevelVal = this.calculateFloorLevel((class_1937)level2, blockPos);
        boolean isClimbingVal = feetState.method_26204() instanceof class_2399 || feetState.method_26204() instanceof class_2541;
        boolean isLiquidVal = !feetState.method_26227().method_15769();
        NavigationPoint point2 = new NavigationPoint(canPassFeetVal, canPassHeadVal, hasStableFloorVal, floorLevelVal, isClimbingVal, isLiquidVal){
            final /* synthetic */ boolean $canPassFeetVal;
            final /* synthetic */ boolean $canPassHeadVal;
            final /* synthetic */ boolean $hasStableFloorVal;
            final /* synthetic */ double $floorLevelVal;
            final /* synthetic */ boolean $isClimbingVal;
            final /* synthetic */ boolean $isLiquidVal;
            {
                this.$canPassFeetVal = $canPassFeetVal;
                this.$canPassHeadVal = $canPassHeadVal;
                this.$hasStableFloorVal = $hasStableFloorVal;
                this.$floorLevelVal = $floorLevelVal;
                this.$isClimbingVal = $isClimbingVal;
                this.$isLiquidVal = $isLiquidVal;
            }

            public boolean isTraversable() {
                return this.$canPassFeetVal && this.$canPassHeadVal;
            }

            public boolean hasFloor() {
                return this.$hasStableFloorVal;
            }

            public double getFloorLevel() {
                return this.$floorLevelVal;
            }

            public boolean isClimbable() {
                return this.$isClimbingVal;
            }

            public boolean isLiquid() {
                return this.$isLiquidVal;
            }
        };
        if (this.cache.size() > 50000) {
            this.cache.clear();
        }
        ((Map)this.cache).put(key, point2);
        return point2;
    }

    private final boolean canWalkThrough(class_1937 level2, class_2680 state, class_2338 pos) {
        if (state.method_26215()) {
            return true;
        }
        if (!state.method_26227().method_15769()) {
            return false;
        }
        if (state.method_26164(class_3481.field_15487) || state.method_27852(class_2246.field_10588) || state.method_27852(class_2246.field_28682)) {
            return true;
        }
        if (state.method_27852(class_2246.field_27879) || state.method_27852(class_2246.field_10029) || state.method_27852(class_2246.field_16999) || state.method_27852(class_2246.field_21211) || state.method_27852(class_2246.field_10302) || state.method_27852(class_2246.field_10606) || state.method_27852(class_2246.field_28048)) {
            return true;
        }
        class_2248 class_22482 = state.method_26204();
        Intrinsics.checkNotNullExpressionValue((Object)class_22482, (String)"getBlock(...)");
        class_2248 block = class_22482;
        if (block instanceof class_2323) {
            return (Boolean)state.method_11654((class_2769)class_2323.field_10945) != false ? true : ((class_2323)block).method_51169().comp_1471();
        }
        if (block instanceof class_2349) {
            Comparable comparable = state.method_11654((class_2769)class_2349.field_11026);
            Intrinsics.checkNotNullExpressionValue((Object)comparable, (String)"getValue(...)");
            return (Boolean)comparable;
        }
        if (block instanceof class_2241) {
            return true;
        }
        if (state.method_26164(class_3481.field_16584) || state.method_26164(class_3481.field_15504)) {
            return false;
        }
        return state.method_26171(class_10.field_50) || state.method_26227().method_15767(class_3486.field_15517);
    }

    private final boolean canWalkOn(class_1937 level2, class_2680 state, class_2338 pos) {
        class_2248 class_22482 = state.method_26204();
        Intrinsics.checkNotNullExpressionValue((Object)class_22482, (String)"getBlock(...)");
        class_2248 block = class_22482;
        if (state.method_26234((class_1922)level2, pos) && !Intrinsics.areEqual((Object)block, (Object)class_2246.field_10092) && !Intrinsics.areEqual((Object)block, (Object)class_2246.field_10422) && !Intrinsics.areEqual((Object)block, (Object)class_2246.field_21211)) {
            return true;
        }
        return block instanceof class_5800 || block instanceof class_2399 || block instanceof class_2541 || Intrinsics.areEqual((Object)block, (Object)class_2246.field_10362) || Intrinsics.areEqual((Object)block, (Object)class_2246.field_10194) || Intrinsics.areEqual((Object)block, (Object)class_2246.field_10114) || Intrinsics.areEqual((Object)block, (Object)class_2246.field_10034) || Intrinsics.areEqual((Object)block, (Object)class_2246.field_10443) || Intrinsics.areEqual((Object)block, (Object)class_2246.field_10033) || block instanceof class_2510 || block instanceof class_2482 || block instanceof class_2241;
    }

    private final double calculateFloorLevel(class_1937 level2, class_2338 pos) {
        class_3610 class_36102 = level2.method_8316(pos);
        Intrinsics.checkNotNullExpressionValue((Object)class_36102, (String)"getFluidState(...)");
        class_3610 state = class_36102;
        if (state.method_15767(class_3486.field_15517)) {
            return (double)pos.method_10264() + 0.5;
        }
        class_2338 class_23382 = pos.method_10074();
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"below(...)");
        class_2338 belowPos = class_23382;
        class_2680 class_26802 = level2.method_8320(belowPos);
        Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
        class_2680 belowState = class_26802;
        class_265 class_2652 = belowState.method_26194((class_1922)level2, belowPos, class_3726.method_16194());
        Intrinsics.checkNotNullExpressionValue((Object)class_2652, (String)"getCollisionShape(...)");
        class_265 shape = class_2652;
        return shape.method_1110() ? (double)belowPos.method_10264() : (double)belowPos.method_10264() + shape.method_1105(class_2350.class_2351.field_11052);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/api/pathfinder/provider/impl/MinecraftNavigationProvider$Companion;", "", "<init>", "()V", "", "MAX_CACHE_SIZE", "I", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

