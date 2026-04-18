/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.SetsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.StringsKt
 *  net.minecraft.class_10735
 *  net.minecraft.class_1297
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_1922
 *  net.minecraft.class_2189
 *  net.minecraft.class_2248
 *  net.minecraft.class_2269
 *  net.minecraft.class_2286
 *  net.minecraft.class_2302
 *  net.minecraft.class_2311
 *  net.minecraft.class_2338
 *  net.minecraft.class_2356
 *  net.minecraft.class_2358
 *  net.minecraft.class_2362
 *  net.minecraft.class_2374
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_2399
 *  net.minecraft.class_2401
 *  net.minecraft.class_2404
 *  net.minecraft.class_2420
 *  net.minecraft.class_2421
 *  net.minecraft.class_2423
 *  net.minecraft.class_243
 *  net.minecraft.class_2443
 *  net.minecraft.class_2457
 *  net.minecraft.class_2459
 *  net.minecraft.class_2462
 *  net.minecraft.class_2473
 *  net.minecraft.class_2476
 *  net.minecraft.class_2484
 *  net.minecraft.class_2487
 *  net.minecraft.class_2488
 *  net.minecraft.class_2513
 *  net.minecraft.class_2521
 *  net.minecraft.class_2523
 *  net.minecraft.class_2525
 *  net.minecraft.class_2526
 *  net.minecraft.class_2527
 *  net.minecraft.class_2537
 *  net.minecraft.class_2538
 *  net.minecraft.class_2541
 *  net.minecraft.class_2549
 *  net.minecraft.class_2560
 *  net.minecraft.class_2561
 *  net.minecraft.class_2577
 *  net.minecraft.class_2671
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 *  net.minecraft.class_437
 *  net.minecraft.class_5808
 *  net.minecraft.class_5815
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  net.minecraft.class_7923
 *  net.minecraft.class_9279
 *  net.minecraft.class_9334
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.etherwarp;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import net.minecraft.class_10735;
import net.minecraft.class_1297;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1922;
import net.minecraft.class_2189;
import net.minecraft.class_2248;
import net.minecraft.class_2269;
import net.minecraft.class_2286;
import net.minecraft.class_2302;
import net.minecraft.class_2311;
import net.minecraft.class_2338;
import net.minecraft.class_2356;
import net.minecraft.class_2358;
import net.minecraft.class_2362;
import net.minecraft.class_2374;
import net.minecraft.class_239;
import net.minecraft.class_2399;
import net.minecraft.class_2401;
import net.minecraft.class_2404;
import net.minecraft.class_2420;
import net.minecraft.class_2421;
import net.minecraft.class_2423;
import net.minecraft.class_243;
import net.minecraft.class_2443;
import net.minecraft.class_2457;
import net.minecraft.class_2459;
import net.minecraft.class_2462;
import net.minecraft.class_2473;
import net.minecraft.class_2476;
import net.minecraft.class_2484;
import net.minecraft.class_2487;
import net.minecraft.class_2488;
import net.minecraft.class_2513;
import net.minecraft.class_2521;
import net.minecraft.class_2523;
import net.minecraft.class_2525;
import net.minecraft.class_2526;
import net.minecraft.class_2527;
import net.minecraft.class_2537;
import net.minecraft.class_2538;
import net.minecraft.class_2541;
import net.minecraft.class_2549;
import net.minecraft.class_2560;
import net.minecraft.class_2561;
import net.minecraft.class_2577;
import net.minecraft.class_2671;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_437;
import net.minecraft.class_5808;
import net.minecraft.class_5815;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_7923;
import net.minecraft.class_9279;
import net.minecraft.class_9334;
import org.cobalt.api.util.ItemUtilsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000r\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\b\t\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001EB\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J\r\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\r\u0010\u000b\u001a\u00020\n\u00a2\u0006\u0004\b\u000b\u0010\fJ\r\u0010\r\u001a\u00020\n\u00a2\u0006\u0004\b\r\u0010\fJ\r\u0010\u000f\u001a\u00020\u000e\u00a2\u0006\u0004\b\u000f\u0010\u0010J\r\u0010\u0011\u001a\u00020\u000e\u00a2\u0006\u0004\b\u0011\u0010\u0010J\u000f\u0010\u0013\u001a\u0004\u0018\u00010\u0012\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0017\u0010\u0016\u001a\u00020\u00072\b\u0010\u0015\u001a\u0004\u0018\u00010\u0012\u00a2\u0006\u0004\b\u0016\u0010\u0017J\r\u0010\u0018\u001a\u00020\u0007\u00a2\u0006\u0004\b\u0018\u0010\tJ\u000f\u0010\u0019\u001a\u0004\u0018\u00010\u0012\u00a2\u0006\u0004\b\u0019\u0010\u0014J/\u0010 \u001a\u00020\u000e2\u0006\u0010\u001b\u001a\u00020\u001a2\u0006\u0010\u001d\u001a\u00020\u001c2\u0006\u0010\u001e\u001a\u00020\u00072\u0006\u0010\u001f\u001a\u00020\u0007H\u0002\u00a2\u0006\u0004\b \u0010!J/\u0010%\u001a\u00020\u000e2\u0006\u0010\"\u001a\u00020\u001a2\u0006\u0010#\u001a\u00020\u001a2\u0006\u0010\u001f\u001a\u00020\u00072\u0006\u0010$\u001a\u00020\u001cH\u0002\u00a2\u0006\u0004\b%\u0010&J\u0017\u0010)\u001a\u00020\u00072\b\u0010(\u001a\u0004\u0018\u00010'\u00a2\u0006\u0004\b)\u0010*J\u0017\u0010-\u001a\u00020+2\u0006\u0010,\u001a\u00020+H\u0002\u00a2\u0006\u0004\b-\u0010.J\u0019\u00100\u001a\u0004\u0018\u00010/2\u0006\u0010(\u001a\u00020'H\u0002\u00a2\u0006\u0004\b0\u00101J!\u00104\u001a\u0004\u0018\u00018\u0000\"\u0004\b\u0000\u001022\b\u00103\u001a\u0004\u0018\u00010\u0001H\u0002\u00a2\u0006\u0004\b4\u00105J\u001f\u00108\u001a\u00020\u00072\u0006\u00107\u001a\u0002062\u0006\u0010\u001b\u001a\u00020\u0012H\u0002\u00a2\u0006\u0004\b8\u00109R\u0014\u0010;\u001a\u00020:8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b;\u0010<R\u001a\u0010>\u001a\b\u0012\u0004\u0012\u00020+0=8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b>\u0010?R\u001a\u0010@\u001a\b\u0012\u0004\u0012\u00020+0=8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b@\u0010?R\u001a\u0010A\u001a\b\u0012\u0004\u0012\u00020+0=8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bA\u0010?R\u001a\u0010B\u001a\b\u0012\u0004\u0012\u00020+0=8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bB\u0010?R\u0014\u0010C\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bC\u0010D\u00a8\u0006F"}, d2={"Lorg/cobalt/internal/etherwarp/EtherwarpLogic;", "", "<init>", "()V", "Ljava/util/BitSet;", "initPassableBlocks", "()Ljava/util/BitSet;", "", "holdingEtherwarpItem", "()Z", "", "findEtherwarpHotbarSlot", "()I", "getEtherwarpRange", "Lorg/cobalt/internal/etherwarp/EtherwarpLogic$EtherPos;", "getEtherwarpResult", "()Lorg/cobalt/internal/etherwarp/EtherwarpLogic$EtherPos;", "getEtherwarpResultSneaking", "Lnet/minecraft/class_2338;", "getLookingAtBlock", "()Lnet/minecraft/class_2338;", "pos", "isBlockEtherwarpable", "(Lnet/minecraft/class_2338;)Z", "canEtherwarp", "getLookingAtBlockTrace", "Lnet/minecraft/class_243;", "position", "", "distance", "returnEnd", "etherWarp", "getEtherPos", "(Lnet/minecraft/class_243;DZZ)Lorg/cobalt/internal/etherwarp/EtherwarpLogic$EtherPos;", "start", "end", "etherwarpRange", "traverseVoxels", "(Lnet/minecraft/class_243;Lnet/minecraft/class_243;ZD)Lorg/cobalt/internal/etherwarp/EtherwarpLogic$EtherPos;", "Lnet/minecraft/class_1799;", "stack", "isEtherwarpStack", "(Lnet/minecraft/class_1799;)Z", "", "name", "normalizeName", "(Ljava/lang/String;)Ljava/lang/String;", "Lnet/minecraft/class_2487;", "getExtraAttributes", "(Lnet/minecraft/class_1799;)Lnet/minecraft/class_2487;", "T", "value", "unwrapOptional", "(Ljava/lang/Object;)Ljava/lang/Object;", "Lnet/minecraft/class_2680;", "state", "isBlockPassable", "(Lnet/minecraft/class_2680;Lnet/minecraft/class_2338;)Z", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "", "etherwarpItemIds", "Ljava/util/Set;", "nameHints", "etherwarpLoreHints", "aotvNameHints", "passableBlockIds", "Ljava/util/BitSet;", "EtherPos", "cobalt"})
@SourceDebugExtension(value={"SMAP\nEtherwarpLogic.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EtherwarpLogic.kt\norg/cobalt/internal/etherwarp/EtherwarpLogic\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,467:1\n1807#2,3:468\n1807#2,3:471\n1807#2,2:474\n1807#2,3:476\n1809#2:479\n*S KotlinDebug\n*F\n+ 1 EtherwarpLogic.kt\norg/cobalt/internal/etherwarp/EtherwarpLogic\n*L\n391#1:468,3\n394#1:471,3\n396#1:474,2\n398#1:476,3\n396#1:479\n*E\n"})
public final class EtherwarpLogic {
    @NotNull
    public static final EtherwarpLogic INSTANCE = new EtherwarpLogic();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final Set<String> etherwarpItemIds;
    @NotNull
    private static final Set<String> nameHints;
    @NotNull
    private static final Set<String> etherwarpLoreHints;
    @NotNull
    private static final Set<String> aotvNameHints;
    @NotNull
    private static final BitSet passableBlockIds;

    private EtherwarpLogic() {
    }

    private final BitSet initPassableBlocks() {
        BitSet bitSet = new BitSet();
        Class[] classArray = new Class[]{class_2269.class, class_2577.class, class_2484.class, class_2549.class, class_2399.class, class_2473.class, class_2356.class, class_2513.class, class_2302.class, class_2443.class, class_2488.class, class_2538.class, class_2537.class, class_2358.class, class_2189.class, class_2527.class, class_2362.class, class_2521.class, class_2526.class, class_10735.class, class_2476.class, class_2525.class, class_2523.class, class_2404.class, class_2541.class, class_2420.class, class_2671.class, class_5815.class, class_2560.class, class_2311.class, class_5808.class, class_2401.class, class_2421.class, class_2423.class, class_2457.class, class_2286.class, class_2459.class, class_2462.class};
        Class[] passableTypes = classArray;
        block0: for (Object e : class_7923.field_41175) {
            Intrinsics.checkNotNullExpressionValue(e, (String)"next(...)");
            class_2248 block = (class_2248)e;
            for (Class passableType : passableTypes) {
                if (!passableType.isInstance(block)) continue;
                int rawId = class_2248.method_9507((class_2680)block.method_9564());
                bitSet.set(rawId);
                continue block0;
            }
        }
        return bitSet;
    }

    public final boolean holdingEtherwarpItem() {
        class_746 class_7462 = EtherwarpLogic.mc.field_1724;
        if (class_7462 == null) {
            return false;
        }
        class_746 player = class_7462;
        return this.isEtherwarpStack(player.method_6047()) || this.isEtherwarpStack(player.method_6079());
    }

    public final int findEtherwarpHotbarSlot() {
        class_746 class_7462 = EtherwarpLogic.mc.field_1724;
        if (class_7462 == null) {
            return -1;
        }
        class_746 player = class_7462;
        class_1661 class_16612 = player.method_31548();
        Intrinsics.checkNotNullExpressionValue((Object)class_16612, (String)"getInventory(...)");
        class_1661 inventory = class_16612;
        for (int i = 0; i < 9; ++i) {
            class_1799 stack;
            Intrinsics.checkNotNullExpressionValue((Object)inventory.method_5438(i), (String)"getItem(...)");
            if (stack.method_7960() || !this.isEtherwarpStack(stack)) continue;
            return i;
        }
        return -1;
    }

    public final int getEtherwarpRange() {
        int n;
        int tunedTransmission;
        class_746 class_7462 = EtherwarpLogic.mc.field_1724;
        if (class_7462 == null) {
            return 57;
        }
        class_746 player = class_7462;
        if (EtherwarpLogic.mc.field_1687 == null) {
            return 57;
        }
        class_1799 class_17992 = player.method_6047();
        Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getMainHandItem(...)");
        class_1799 stack = class_17992;
        if (stack.method_7960()) {
            class_1799 class_17993 = player.method_6079();
            Intrinsics.checkNotNullExpressionValue((Object)class_17993, (String)"getOffhandItem(...)");
            stack = class_17993;
            if (stack.method_7960()) {
                return 57;
            }
        }
        class_2487 class_24872 = this.getExtraAttributes(stack);
        if (class_24872 == null) {
            return 57;
        }
        class_2487 attributes = class_24872;
        if (attributes.method_10545("tuned_transmission")) {
            Byte tunedByte;
            Object object;
            int tunedInt;
            Integer n2 = (Integer)this.unwrapOptional(attributes.method_10550("tuned_transmission"));
            int n3 = tunedInt = n2 != null ? n2 : 0;
            v6 = tunedInt != 0 ? tunedInt : ((object = (tunedByte = (Byte)this.unwrapOptional(attributes.method_10571("tuned_transmission")))) != null && (object = String.valueOf(((Byte)object).byteValue())) != null && (object = StringsKt.toIntOrNull((String)object)) != null ? (Integer)object : 0);
        } else {
            v6 = tunedTransmission = 0;
        }
        if (attributes.method_10545("ether_transmission")) {
            Integer n4 = (Integer)this.unwrapOptional(attributes.method_10550("ether_transmission"));
            n = n4 != null ? n4 : 0;
        } else {
            n = 0;
        }
        int etherTransmission = n;
        tunedTransmission = Math.max(tunedTransmission, etherTransmission);
        return 57 + tunedTransmission;
    }

    @NotNull
    public final EtherPos getEtherwarpResult() {
        class_746 class_7462 = EtherwarpLogic.mc.field_1724;
        if (class_7462 == null) {
            return EtherPos.Companion.getNONE();
        }
        class_746 player = class_7462;
        if (EtherwarpLogic.mc.field_1687 == null) {
            return EtherPos.Companion.getNONE();
        }
        double range = this.getEtherwarpRange();
        class_243 playerPos = new class_243(player.method_23317(), player.method_23318(), player.method_23321());
        return this.getEtherPos(playerPos, range, true, true);
    }

    @NotNull
    public final EtherPos getEtherwarpResultSneaking() {
        class_746 class_7462 = EtherwarpLogic.mc.field_1724;
        if (class_7462 == null) {
            return EtherPos.Companion.getNONE();
        }
        class_746 player = class_7462;
        if (EtherwarpLogic.mc.field_1687 == null) {
            return EtherPos.Companion.getNONE();
        }
        double range = this.getEtherwarpRange();
        class_243 playerPos = new class_243(player.method_23317(), player.method_23318(), player.method_23321());
        class_243 class_2432 = playerPos.method_1031(0.0, 1.54, 0.0);
        Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"add(...)");
        class_243 startPos = class_2432;
        double raytraceDistance = 200.0;
        class_243 class_2433 = player.method_5828(1.0f).method_1021(raytraceDistance).method_1019(startPos);
        Intrinsics.checkNotNullExpressionValue((Object)class_2433, (String)"add(...)");
        class_243 endPos = class_2433;
        EtherPos result = this.traverseVoxels(startPos, endPos, true, range);
        return !Intrinsics.areEqual((Object)result, (Object)EtherPos.Companion.getNONE()) ? result : new EtherPos(false, class_2338.method_49638((class_2374)((class_2374)endPos)), null);
    }

    @Nullable
    public final class_2338 getLookingAtBlock() {
        EtherPos result = this.getEtherwarpResult();
        return result.getSucceeded() ? result.getPos() : null;
    }

    public final boolean isBlockEtherwarpable(@Nullable class_2338 pos) {
        if (pos == null) {
            return false;
        }
        EtherPos result = this.getEtherwarpResult();
        return result.getSucceeded() && Intrinsics.areEqual((Object)pos, (Object)result.getPos());
    }

    public final boolean canEtherwarp() {
        if (EtherwarpLogic.mc.field_1724 == null) {
            return false;
        }
        if (EtherwarpLogic.mc.field_1687 == null) {
            return false;
        }
        class_437 class_4372 = EtherwarpLogic.mc.field_1755;
        if (class_4372 == null) {
            return true;
        }
        class_437 screen = class_4372;
        return Intrinsics.areEqual((Object)screen.getClass().getSimpleName(), (Object)"ChatScreen");
    }

    @Nullable
    public final class_2338 getLookingAtBlockTrace() {
        class_746 class_7462 = EtherwarpLogic.mc.field_1724;
        if (class_7462 == null) {
            return null;
        }
        class_746 player = class_7462;
        class_638 class_6382 = EtherwarpLogic.mc.field_1687;
        if (class_6382 == null) {
            return null;
        }
        class_638 level2 = class_6382;
        double range = this.getEtherwarpRange();
        class_243 class_2432 = player.method_5836(1.0f);
        Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"getEyePosition(...)");
        class_243 eyePos = class_2432;
        class_243 class_2433 = player.method_5828(1.0f);
        Intrinsics.checkNotNullExpressionValue((Object)class_2433, (String)"getViewVector(...)");
        class_243 lookVec = class_2433;
        class_243 class_2434 = eyePos.method_1019(lookVec.method_1021(range));
        Intrinsics.checkNotNullExpressionValue((Object)class_2434, (String)"add(...)");
        class_243 traceEnd = class_2434;
        class_3965 class_39652 = level2.method_17742(new class_3959(eyePos, traceEnd, class_3959.class_3960.field_17559, class_3959.class_242.field_1348, (class_1297)player));
        Intrinsics.checkNotNullExpressionValue((Object)class_39652, (String)"clip(...)");
        class_3965 result = class_39652;
        if (result.method_17783() == class_239.class_240.field_1332) {
            class_2338 class_23382 = result.method_17777();
            Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"getBlockPos(...)");
            class_2338 pos = class_23382;
            class_2680 class_26802 = level2.method_8320(pos);
            Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
            class_2680 state = class_26802;
            if (!state.method_26215()) {
                return pos;
            }
        }
        return null;
    }

    private final EtherPos getEtherPos(class_243 position, double distance, boolean returnEnd, boolean etherWarp) {
        class_746 class_7462 = EtherwarpLogic.mc.field_1724;
        if (class_7462 == null) {
            return EtherPos.Companion.getNONE();
        }
        class_746 player = class_7462;
        double eyeHeight = player.method_5715() ? 1.54 : 1.62;
        class_243 class_2432 = position.method_1031(0.0, eyeHeight, 0.0);
        Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"add(...)");
        class_243 startPos = class_2432;
        double raytraceDistance = 200.0;
        class_243 class_2433 = player.method_5828(1.0f).method_1021(raytraceDistance).method_1019(startPos);
        Intrinsics.checkNotNullExpressionValue((Object)class_2433, (String)"add(...)");
        class_243 endPos = class_2433;
        EtherPos result = this.traverseVoxels(startPos, endPos, etherWarp, distance);
        if (!Intrinsics.areEqual((Object)result, (Object)EtherPos.Companion.getNONE()) || !returnEnd) {
            return result;
        }
        return new EtherPos(false, class_2338.method_49638((class_2374)((class_2374)endPos)), null);
    }

    private final EtherPos traverseVoxels(class_243 start, class_243 end, boolean etherWarp, double etherwarpRange) {
        class_638 class_6382 = EtherwarpLogic.mc.field_1687;
        if (class_6382 == null) {
            return EtherPos.Companion.getNONE();
        }
        class_638 level2 = class_6382;
        double x0 = start.field_1352;
        double y0 = start.field_1351;
        double z0 = start.field_1350;
        double x1 = end.field_1352;
        double y1 = end.field_1351;
        double z1 = end.field_1350;
        int x = (int)Math.floor(x0);
        int y = (int)Math.floor(y0);
        int z = (int)Math.floor(z0);
        int endX = (int)Math.floor(x1);
        int endY = (int)Math.floor(y1);
        int endZ = (int)Math.floor(z1);
        double dirX = x1 - x0;
        double dirY = y1 - y0;
        double dirZ = z1 - z0;
        int stepX = (int)Math.signum(dirX);
        int stepY = (int)Math.signum(dirY);
        int stepZ = (int)Math.signum(dirZ);
        double invDirX = !(dirX == 0.0) ? 1.0 / dirX : Double.MAX_VALUE;
        double invDirY = !(dirY == 0.0) ? 1.0 / dirY : Double.MAX_VALUE;
        double invDirZ = !(dirZ == 0.0) ? 1.0 / dirZ : Double.MAX_VALUE;
        double tDeltaX = Math.abs(invDirX * (double)stepX);
        double tDeltaY = Math.abs(invDirY * (double)stepY);
        double tDeltaZ = Math.abs(invDirZ * (double)stepZ);
        double tMaxX = Math.abs(((double)(x + Math.max(stepX, 0)) - x0) * invDirX);
        double tMaxY = Math.abs(((double)(y + Math.max(stepY, 0)) - y0) * invDirY);
        double tMaxZ = Math.abs(((double)(z + Math.max(stepZ, 0)) - z0) * invDirZ);
        for (int i = 0; i < 1000; ++i) {
            boolean isSolid;
            class_2680 currentBlock;
            class_2338 blockPos = new class_2338(x, y, z);
            Intrinsics.checkNotNullExpressionValue((Object)level2.method_8320(blockPos), (String)"getBlockState(...)");
            int currentBlockId = class_2248.method_9507((class_2680)currentBlock);
            boolean bl = isSolid = !passableBlockIds.get(currentBlockId);
            if (isSolid && etherWarp || currentBlockId != 0 && !etherWarp) {
                if (!etherWarp && passableBlockIds.get(currentBlockId)) {
                    return new EtherPos(false, blockPos, currentBlock);
                }
                class_2338 class_23382 = blockPos.method_10086(1);
                Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"above(...)");
                class_2338 footPos = class_23382;
                class_2680 class_26802 = level2.method_8320(footPos);
                Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
                class_2680 footBlock = class_26802;
                int footBlockId = class_2248.method_9507((class_2680)footBlock);
                if (!passableBlockIds.get(footBlockId)) {
                    return new EtherPos(false, blockPos, currentBlock);
                }
                class_2338 class_23383 = blockPos.method_10086(2);
                Intrinsics.checkNotNullExpressionValue((Object)class_23383, (String)"above(...)");
                class_2338 headPos = class_23383;
                class_2680 class_26803 = level2.method_8320(headPos);
                Intrinsics.checkNotNullExpressionValue((Object)class_26803, (String)"getBlockState(...)");
                class_2680 headBlock = class_26803;
                int headBlockId = class_2248.method_9507((class_2680)headBlock);
                if (!passableBlockIds.get(headBlockId)) {
                    return new EtherPos(false, blockPos, currentBlock);
                }
                class_243 blockCenter = new class_243((double)blockPos.method_10263() + 0.5, (double)blockPos.method_10264() + 0.5, (double)blockPos.method_10260() + 0.5);
                double distanceToBlock = start.method_1022(blockCenter);
                boolean withinRange = distanceToBlock <= etherwarpRange;
                return new EtherPos(withinRange, blockPos, currentBlock);
            }
            if (x == endX && y == endY && z == endZ) {
                return EtherPos.Companion.getNONE();
            }
            if (tMaxX <= tMaxY && tMaxX <= tMaxZ) {
                tMaxX += tDeltaX;
                x += stepX;
                continue;
            }
            if (tMaxY <= tMaxZ) {
                tMaxY += tDeltaY;
                y += stepY;
                continue;
            }
            tMaxZ += tDeltaZ;
            z += stepZ;
        }
        return EtherPos.Companion.getNONE();
    }

    public final boolean isEtherwarpStack(@Nullable class_1799 stack) {
        block25: {
            if (stack == null || stack.method_7960()) {
                return false;
            }
            try {
                boolean bl;
                block27: {
                    boolean bl2;
                    block24: {
                        String it;
                        boolean bl3;
                        boolean $i$f$any;
                        Iterable $this$any$iv;
                        String displayName;
                        String displayNameRaw;
                        block23: {
                            boolean isShovelOrSword;
                            String string = stack.method_7909().toString();
                            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
                            String string2 = string;
                            Locale locale = Locale.ROOT;
                            Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
                            String string3 = string2.toLowerCase(locale);
                            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
                            String itemName = string3;
                            boolean bl4 = isShovelOrSword = StringsKt.contains$default((CharSequence)itemName, (CharSequence)"shovel", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)itemName, (CharSequence)"sword", (boolean)false, (int)2, null);
                            if (!isShovelOrSword) {
                                return false;
                            }
                            class_2487 attributes = this.getExtraAttributes(stack);
                            if (attributes != null && attributes.method_10545("id")) {
                                String id;
                                String string4 = (String)this.unwrapOptional(attributes.method_10558("id"));
                                if (string4 == null) {
                                    string4 = "";
                                }
                                if (((CharSequence)(id = string4)).length() > 0 && etherwarpItemIds.contains(id)) {
                                    return true;
                                }
                                if (((CharSequence)id).length() > 0 && (Intrinsics.areEqual((Object)id, (Object)"ASPECT_OF_THE_VOID") || Intrinsics.areEqual((Object)id, (Object)"ASPECT_OF_THE_END"))) {
                                    Byte by = (Byte)this.unwrapOptional(attributes.method_10571("ethermerge"));
                                    if ((by != null ? by : (byte)0) == 1) {
                                        return true;
                                    }
                                }
                            }
                            String string5 = stack.method_7964().getString();
                            Intrinsics.checkNotNullExpressionValue((Object)string5, (String)"getString(...)");
                            String string6 = string5;
                            Locale locale2 = Locale.ROOT;
                            Intrinsics.checkNotNullExpressionValue((Object)locale2, (String)"ROOT");
                            String string7 = string6.toLowerCase(locale2);
                            Intrinsics.checkNotNullExpressionValue((Object)string7, (String)"toLowerCase(...)");
                            displayNameRaw = string7;
                            displayName = this.normalizeName(displayNameRaw);
                            $this$any$iv = nameHints;
                            $i$f$any = false;
                            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                                bl3 = false;
                            } else {
                                for (Object element$iv : $this$any$iv) {
                                    it = (String)element$iv;
                                    boolean bl5 = false;
                                    if (!(StringsKt.contains$default((CharSequence)displayName, (CharSequence)it, (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)displayNameRaw, (CharSequence)it, (boolean)false, (int)2, null))) continue;
                                    bl3 = true;
                                    break block23;
                                }
                                bl3 = false;
                            }
                        }
                        if (bl3) {
                            return true;
                        }
                        $this$any$iv = aotvNameHints;
                        $i$f$any = false;
                        if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                            bl2 = false;
                        } else {
                            for (Object element$iv : $this$any$iv) {
                                it = (String)element$iv;
                                boolean bl6 = false;
                                if (!(StringsKt.contains$default((CharSequence)displayName, (CharSequence)it, (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)displayNameRaw, (CharSequence)it, (boolean)false, (int)2, null))) continue;
                                bl2 = true;
                                break block24;
                            }
                            bl2 = false;
                        }
                    }
                    if (!bl2) break block25;
                    List<class_2561> loreLines = ItemUtilsKt.getLoreLines(stack);
                    Iterable $this$any$iv = loreLines;
                    boolean $i$f$any = false;
                    if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                        bl = false;
                    } else {
                        for (Object element$iv : $this$any$iv) {
                            boolean bl7;
                            block26: {
                                String string;
                                class_2561 line = (class_2561)element$iv;
                                boolean bl8 = false;
                                Intrinsics.checkNotNullExpressionValue((Object)line.getString(), (String)"getString(...)");
                                Locale locale = Locale.ROOT;
                                Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
                                Intrinsics.checkNotNullExpressionValue((Object)string.toLowerCase(locale), (String)"toLowerCase(...)");
                                Iterable $this$any$iv2 = etherwarpLoreHints;
                                boolean $i$f$any2 = false;
                                if ($this$any$iv2 instanceof Collection && ((Collection)$this$any$iv2).isEmpty()) {
                                    bl7 = false;
                                } else {
                                    for (Object element$iv2 : $this$any$iv2) {
                                        String text;
                                        String hint = (String)element$iv2;
                                        boolean bl9 = false;
                                        if (!StringsKt.contains$default((CharSequence)text, (CharSequence)hint, (boolean)false, (int)2, null)) continue;
                                        bl7 = true;
                                        break block26;
                                    }
                                    bl7 = false;
                                }
                            }
                            if (!bl7) continue;
                            bl = true;
                            break block27;
                        }
                        bl = false;
                    }
                }
                if (bl) {
                    return true;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return false;
    }

    private final String normalizeName(String name) {
        String result = ((Object)StringsKt.trim((CharSequence)name)).toString();
        if (StringsKt.startsWith$default((String)result, (String)"warped ", (boolean)false, (int)2, null)) {
            result = ((Object)StringsKt.trim((CharSequence)StringsKt.removePrefix((String)result, (CharSequence)"warped "))).toString();
        }
        if (StringsKt.startsWith$default((String)result, (String)"heroic ", (boolean)false, (int)2, null)) {
            result = ((Object)StringsKt.trim((CharSequence)StringsKt.removePrefix((String)result, (CharSequence)"heroic "))).toString();
        }
        return result;
    }

    private final class_2487 getExtraAttributes(class_1799 stack) {
        Object var2_2;
        try {
            class_9279 customData = (class_9279)stack.method_58694(class_9334.field_49628);
            if (customData != null) {
                class_2487 class_24872 = (class_2487)this.unwrapOptional(customData.method_57461());
                if (class_24872 == null) {
                    return null;
                }
                class_2487 nbt = class_24872;
                if (nbt.method_10545("ExtraAttributes")) {
                    return (class_2487)this.unwrapOptional(nbt.method_10562("ExtraAttributes"));
                }
                if (nbt.method_10545("extra_attributes")) {
                    return (class_2487)this.unwrapOptional(nbt.method_10562("extra_attributes"));
                }
                if (nbt.method_10545("id")) {
                    return nbt;
                }
            }
            var2_2 = null;
        }
        catch (Exception exception) {
            var2_2 = null;
        }
        return var2_2;
    }

    private final <T> T unwrapOptional(Object value) {
        if (value == null) {
            return null;
        }
        Object object = value;
        return (T)(object instanceof Optional ? ((Optional)value).orElse(null) : (object instanceof OptionalInt ? (((OptionalInt)value).isPresent() ? (Object)((OptionalInt)value).orElse(0) : null) : (object instanceof OptionalLong ? (((OptionalLong)value).isPresent() ? (Object)((OptionalLong)value).orElse(0L) : null) : (object instanceof OptionalDouble ? (((OptionalDouble)value).isPresent() ? (Object)((OptionalDouble)value).orElse(0.0) : null) : value))));
    }

    private final boolean isBlockPassable(class_2680 state, class_2338 position) {
        boolean bl;
        if (state.method_26215()) {
            return true;
        }
        try {
            int rawId = class_2248.method_9507((class_2680)state);
            if (passableBlockIds.get(rawId)) {
                return true;
            }
            class_638 class_6382 = EtherwarpLogic.mc.field_1687;
            Intrinsics.checkNotNull((Object)class_6382, (String)"null cannot be cast to non-null type net.minecraft.world.level.BlockGetter");
            bl = !state.method_26234((class_1922)class_6382, position);
        }
        catch (Exception exception) {
            bl = false;
        }
        return bl;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        Object[] objectArray = new String[]{"ETHERWARP_CONDUIT", "ETHERWARP_TRANSMITTER", "ETHERWARP_MERGER", "WARPED_ASPECT_OF_THE_VOID", "ASPECT_OF_THE_VOID", "ASPECT_OF_THE_END"};
        etherwarpItemIds = SetsKt.setOf((Object[])objectArray);
        objectArray = new String[]{"etherwarp conduit", "etherwarp transmitter", "etherwarp merger", "etherwarp"};
        nameHints = SetsKt.setOf((Object[])objectArray);
        objectArray = new String[]{"ether transmission", "etherwarp"};
        etherwarpLoreHints = SetsKt.setOf((Object[])objectArray);
        objectArray = new String[]{"aspect of the void", "warped aspect of the void", "aspect of the end"};
        aotvNameHints = SetsKt.setOf((Object[])objectArray);
        passableBlockIds = INSTANCE.initPassableBlocks();
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\n\b\u0086\b\u0018\u0000 !2\u00020\u0001:\u0001!B#\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0004\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0012\u0010\f\u001a\u0004\u0018\u00010\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\rJ\u0012\u0010\u000e\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\u000fJ2\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00042\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0006H\u00c6\u0001\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u001b\u0010\u0013\u001a\u00020\u00022\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0011\u0010\u0016\u001a\u00020\u0015H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0019\u001a\u00020\u0018H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0019\u0010\u001aR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001b\u001a\u0004\b\u001c\u0010\u000bR\u0019\u0010\u0005\u001a\u0004\u0018\u00010\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001d\u001a\u0004\b\u001e\u0010\rR\u0019\u0010\u0007\u001a\u0004\u0018\u00010\u00068\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u001f\u001a\u0004\b \u0010\u000f\u00a8\u0006\""}, d2={"Lorg/cobalt/internal/etherwarp/EtherwarpLogic$EtherPos;", "", "", "succeeded", "Lnet/minecraft/class_2338;", "pos", "Lnet/minecraft/class_2680;", "state", "<init>", "(ZLnet/minecraft/class_2338;Lnet/minecraft/class_2680;)V", "component1", "()Z", "component2", "()Lnet/minecraft/class_2338;", "component3", "()Lnet/minecraft/class_2680;", "copy", "(ZLnet/minecraft/class_2338;Lnet/minecraft/class_2680;)Lorg/cobalt/internal/etherwarp/EtherwarpLogic$EtherPos;", "other", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "Z", "getSucceeded", "Lnet/minecraft/class_2338;", "getPos", "Lnet/minecraft/class_2680;", "getState", "Companion", "cobalt"})
    public static final class EtherPos {
        @NotNull
        public static final Companion Companion = new Companion(null);
        private final boolean succeeded;
        @Nullable
        private final class_2338 pos;
        @Nullable
        private final class_2680 state;
        @NotNull
        private static final EtherPos NONE = new EtherPos(false, null, null);

        public EtherPos(boolean succeeded, @Nullable class_2338 pos, @Nullable class_2680 state) {
            this.succeeded = succeeded;
            this.pos = pos;
            this.state = state;
        }

        public final boolean getSucceeded() {
            return this.succeeded;
        }

        @Nullable
        public final class_2338 getPos() {
            return this.pos;
        }

        @Nullable
        public final class_2680 getState() {
            return this.state;
        }

        public final boolean component1() {
            return this.succeeded;
        }

        @Nullable
        public final class_2338 component2() {
            return this.pos;
        }

        @Nullable
        public final class_2680 component3() {
            return this.state;
        }

        @NotNull
        public final EtherPos copy(boolean succeeded, @Nullable class_2338 pos, @Nullable class_2680 state) {
            return new EtherPos(succeeded, pos, state);
        }

        public static /* synthetic */ EtherPos copy$default(EtherPos etherPos, boolean bl, class_2338 class_23382, class_2680 class_26802, int n, Object object) {
            if ((n & 1) != 0) {
                bl = etherPos.succeeded;
            }
            if ((n & 2) != 0) {
                class_23382 = etherPos.pos;
            }
            if ((n & 4) != 0) {
                class_26802 = etherPos.state;
            }
            return etherPos.copy(bl, class_23382, class_26802);
        }

        @NotNull
        public String toString() {
            return "EtherPos(succeeded=" + this.succeeded + ", pos=" + this.pos + ", state=" + this.state + ")";
        }

        public int hashCode() {
            int result = Boolean.hashCode(this.succeeded);
            result = result * 31 + (this.pos == null ? 0 : this.pos.hashCode());
            result = result * 31 + (this.state == null ? 0 : this.state.hashCode());
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof EtherPos)) {
                return false;
            }
            EtherPos etherPos = (EtherPos)other;
            if (this.succeeded != etherPos.succeeded) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.pos, (Object)etherPos.pos)) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.state, (Object)etherPos.state);
        }

        @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0006\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2={"Lorg/cobalt/internal/etherwarp/EtherwarpLogic$EtherPos$Companion;", "", "<init>", "()V", "Lorg/cobalt/internal/etherwarp/EtherwarpLogic$EtherPos;", "NONE", "Lorg/cobalt/internal/etherwarp/EtherwarpLogic$EtherPos;", "getNONE", "()Lorg/cobalt/internal/etherwarp/EtherwarpLogic$EtherPos;", "cobalt"})
        public static final class Companion {
            private Companion() {
            }

            @NotNull
            public final EtherPos getNONE() {
                return NONE;
            }

            public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
                this();
            }
        }
    }
}

