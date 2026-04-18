/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.collections.CollectionsKt
 *  kotlin.comparisons.ComparisonsKt
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  net.minecraft.class_124
 *  net.minecraft.class_1268
 *  net.minecraft.class_1657
 *  net.minecraft.class_1799
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_243
 *  net.minecraft.class_266
 *  net.minecraft.class_2675
 *  net.minecraft.class_268
 *  net.minecraft.class_2680
 *  net.minecraft.class_269
 *  net.minecraft.class_310
 *  net.minecraft.class_3965
 *  net.minecraft.class_636
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  net.minecraft.class_7923
 *  net.minecraft.class_8646
 *  net.minecraft.class_9011
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.mining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.minecraft.class_124;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_266;
import net.minecraft.class_2675;
import net.minecraft.class_268;
import net.minecraft.class_2680;
import net.minecraft.class_269;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_636;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_7923;
import net.minecraft.class_8646;
import net.minecraft.class_9011;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00a2\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0006\n\u0002\b\t\n\u0002\u0010\u0011\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0018\u0002\n\u0002\b\f\b\u00c0\u0002\u0018\u00002\u00020\u0001:\u0007}~\u007f\u0080\u0001\u0081\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0015\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\t\u00a2\u0006\u0004\b\f\u0010\rJ%\u0010\u0013\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\u0010\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0015\u0010\u0017\u001a\u00020\u000b2\u0006\u0010\u0016\u001a\u00020\u0015\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u0017\u0010\u001a\u001a\u00020\u000b2\b\b\u0002\u0010\u0019\u001a\u00020\u0006\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\r\u0010\u001d\u001a\u00020\u001c\u00a2\u0006\u0004\b\u001d\u0010\u001eJ\r\u0010\u001f\u001a\u00020\u0006\u00a2\u0006\u0004\b\u001f\u0010 J\u001b\u0010$\u001a\b\u0012\u0004\u0012\u00020#0\"2\u0006\u0010!\u001a\u00020\t\u00a2\u0006\u0004\b$\u0010%J\u001d\u0010'\u001a\b\u0012\u0004\u0012\u00020&0\"2\u0006\u0010\u0005\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b'\u0010(J/\u0010,\u001a\u00020\u00062\u0006\u0010*\u001a\u00020)2\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010+\u001a\u00020\u00102\u0006\u0010\u0005\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b,\u0010-J%\u0010/\u001a\u00020\u00062\u0006\u0010+\u001a\u00020\u00102\f\u0010.\u001a\b\u0012\u0004\u0012\u00020#0\"H\u0002\u00a2\u0006\u0004\b/\u00100J\u0017\u00103\u001a\u00020\u00062\u0006\u00102\u001a\u000201H\u0002\u00a2\u0006\u0004\b3\u00104J\u001f\u00106\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u00105\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b6\u00107J\u0019\u00108\u001a\u0004\u0018\u00010\u000e2\u0006\u00105\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b8\u00109J\u0017\u0010:\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b:\u0010;J\u000f\u0010<\u001a\u00020\u000bH\u0002\u00a2\u0006\u0004\b<\u0010\u0003J\u001f\u0010=\u001a\u00020\u00062\u0006\u0010*\u001a\u00020)2\u0006\u0010\u000f\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b=\u0010>J\u0017\u0010?\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b?\u0010@J\u001f\u0010D\u001a\u00020C2\u0006\u0010A\u001a\u00020\u000e2\u0006\u0010B\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\bD\u0010EJ\u000f\u0010F\u001a\u00020\u000bH\u0002\u00a2\u0006\u0004\bF\u0010\u0003J\u000f\u0010G\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bG\u0010 J\u001d\u0010H\u001a\b\u0012\u0004\u0012\u00020\t0\"2\u0006\u0010*\u001a\u00020)H\u0002\u00a2\u0006\u0004\bH\u0010IJ\u001f\u0010L\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010K\u001a\u00020JH\u0002\u00a2\u0006\u0004\bL\u0010MJ\u0017\u0010N\u001a\u00020J2\u0006\u0010\u000f\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\bN\u0010OJ\u0017\u0010Q\u001a\u00020\t2\u0006\u0010P\u001a\u00020\tH\u0002\u00a2\u0006\u0004\bQ\u0010RJ+\u0010V\u001a\u00020\u00062\u0006\u0010S\u001a\u00020\t2\u0012\u0010U\u001a\n\u0012\u0006\b\u0001\u0012\u00020\t0T\"\u00020\tH\u0002\u00a2\u0006\u0004\bV\u0010WJ\u0017\u0010X\u001a\u00020\u00062\u0006\u0010+\u001a\u00020\u0010H\u0002\u00a2\u0006\u0004\bX\u0010YR\u0014\u0010[\u001a\u00020Z8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b[\u0010\\R0\u0010`\u001a\u001e\u0012\u0004\u0012\u00020^\u0012\u0004\u0012\u00020^0]j\u000e\u0012\u0004\u0012\u00020^\u0012\u0004\u0012\u00020^`_8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b`\u0010aR0\u0010b\u001a\u001e\u0012\u0004\u0012\u00020^\u0012\u0004\u0012\u00020^0]j\u000e\u0012\u0004\u0012\u00020^\u0012\u0004\u0012\u00020^`_8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bb\u0010aR\u0016\u0010c\u001a\u00020^8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bc\u0010dR\u0018\u0010e\u001a\u0004\u0018\u00010\u000e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\be\u0010fR\u0016\u0010g\u001a\u00020^8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bg\u0010dR\u0016\u0010h\u001a\u00020^8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bh\u0010dR\u0016\u0010i\u001a\u00020\u001c8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bi\u0010jR\u0016\u0010k\u001a\u00020\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bk\u0010lR\u0016\u0010m\u001a\u00020^8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bm\u0010dR\u0014\u0010n\u001a\u00020^8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bn\u0010dR\u0014\u0010o\u001a\u00020^8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bo\u0010dR\u0014\u0010p\u001a\u00020^8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bp\u0010dR\u0014\u0010q\u001a\u00020\u001c8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bq\u0010jR\u0014\u0010r\u001a\u00020\u001c8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\br\u0010jR\u0014\u0010s\u001a\u00020J8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bs\u0010tR\u0014\u0010u\u001a\u00020J8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bu\u0010tR\u0014\u0010w\u001a\u00020v8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bw\u0010xR\u0014\u0010y\u001a\u00020v8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\by\u0010xR\u0014\u0010z\u001a\u00020\t8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bz\u0010{R\u0014\u0010|\u001a\u00020\t8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b|\u0010{\u00a8\u0006\u0082\u0001"}, d2={"Lorg/cobalt/internal/mining/MiningNukerController;", "", "<init>", "()V", "Lorg/cobalt/internal/mining/MiningNukerController$Config;", "config", "", "tick", "(Lorg/cobalt/internal/mining/MiningNukerController$Config;)Z", "", "message", "", "onChatMessage", "(Ljava/lang/String;)V", "Lnet/minecraft/class_2338;", "pos", "Lnet/minecraft/class_2680;", "oldState", "newState", "onBlockChange", "(Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_2680;)V", "Lnet/minecraft/class_2675;", "packet", "onParticlePacket", "(Lnet/minecraft/class_2675;)V", "clearCounts", "reset", "(Z)V", "", "getPowderChestsCollected", "()I", "hasQueuedPowderChest", "()Z", "raw", "", "Lorg/cobalt/internal/mining/MiningNukerController$CustomMatcher;", "parseCustomMatchers", "(Ljava/lang/String;)Ljava/util/List;", "Lorg/cobalt/internal/mining/MiningNukerController$Candidate;", "collectCandidates", "(Lorg/cobalt/internal/mining/MiningNukerController$Config;)Ljava/util/List;", "Lnet/minecraft/class_1937;", "level", "state", "matchesTargetMode", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lorg/cobalt/internal/mining/MiningNukerController$Config;)Z", "matchers", "matchesCustom", "(Lnet/minecraft/class_2680;Ljava/util/List;)Z", "Lorg/cobalt/internal/mining/MiningNukerController$ToolMode;", "toolMode", "hasRequiredTool", "(Lorg/cobalt/internal/mining/MiningNukerController$ToolMode;)Z", "playerPos", "handlePowderChest", "(Lorg/cobalt/internal/mining/MiningNukerController$Config;Lnet/minecraft/class_2338;)Z", "choosePowderChestTarget", "(Lnet/minecraft/class_2338;)Lnet/minecraft/class_2338;", "markPowderChestHint", "(Lnet/minecraft/class_2338;)V", "flagPowderChestSearch", "isExposed", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)Z", "isRecentlyMined", "(Lnet/minecraft/class_2338;)Z", "from", "to", "Lnet/minecraft/class_2350;", "preferredFace", "(Lnet/minecraft/class_2338;Lnet/minecraft/class_2338;)Lnet/minecraft/class_2350;", "pruneExpired", "isPowderChestArea", "readScoreboardLines", "(Lnet/minecraft/class_1937;)Ljava/util/List;", "", "maxDistanceSq", "isNearPlayer", "(Lnet/minecraft/class_2338;D)Z", "distanceToBlockSq", "(Lnet/minecraft/class_2338;)D", "text", "stripFormatting", "(Ljava/lang/String;)Ljava/lang/String;", "value", "", "parts", "containsAny", "(Ljava/lang/String;[Ljava/lang/String;)Z", "isChestState", "(Lnet/minecraft/class_2680;)Z", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Ljava/util/HashMap;", "", "Lkotlin/collections/HashMap;", "recentlyMined", "Ljava/util/HashMap;", "clickedChests", "nextMineMs", "J", "powderChestHint", "Lnet/minecraft/class_2338;", "powderChestHintMs", "powderChestSearchUntilMs", "powderChestsCollected", "I", "cachedPowderArea", "Z", "cachedPowderAreaTick", "RECENT_MINE_TTL_MS", "POWDER_CHEST_HINT_TTL_MS", "POWDER_CHEST_CLICK_COOLDOWN_MS", "POWDER_CHEST_SCAN_HORIZONTAL", "POWDER_CHEST_SCAN_VERTICAL", "POWDER_CHEST_INTERACT_RANGE_SQ", "D", "MAX_CHEST_HINT_DISTANCE_SQ", "Lkotlin/text/Regex;", "NUMERIC_VALUE", "Lkotlin/text/Regex;", "NUMERIC_PAIR", "TREASURE_CHEST_UNCOVERED", "Ljava/lang/String;", "TREASURE_CHEST_LOCKPICKED", "TargetMode", "ToolMode", "CustomMatcher", "Config", "Candidate", "cobalt"})
@SourceDebugExtension(value={"SMAP\nMiningNukerController.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MiningNukerController.kt\norg/cobalt/internal/mining/MiningNukerController\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,422:1\n1021#2,2:423\n1807#2,3:426\n1642#2,10:429\n1915#2:439\n1916#2:441\n1652#2:442\n1#3:425\n1#3:440\n13225#4,2:443\n*S KotlinDebug\n*F\n+ 1 MiningNukerController.kt\norg/cobalt/internal/mining/MiningNukerController\n*L\n211#1:423,2\n366#1:426,3\n376#1:429,10\n376#1:439\n376#1:441\n376#1:442\n376#1:440\n401#1:443,2\n*E\n"})
public final class MiningNukerController {
    @NotNull
    public static final MiningNukerController INSTANCE = new MiningNukerController();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final HashMap<Long, Long> recentlyMined;
    @NotNull
    private static final HashMap<Long, Long> clickedChests;
    private static long nextMineMs;
    @Nullable
    private static class_2338 powderChestHint;
    private static long powderChestHintMs;
    private static long powderChestSearchUntilMs;
    private static int powderChestsCollected;
    private static boolean cachedPowderArea;
    private static long cachedPowderAreaTick;
    private static final long RECENT_MINE_TTL_MS = 750L;
    private static final long POWDER_CHEST_HINT_TTL_MS = 2500L;
    private static final long POWDER_CHEST_CLICK_COOLDOWN_MS = 30000L;
    private static final int POWDER_CHEST_SCAN_HORIZONTAL = 6;
    private static final int POWDER_CHEST_SCAN_VERTICAL = 4;
    private static final double POWDER_CHEST_INTERACT_RANGE_SQ = 42.25;
    private static final double MAX_CHEST_HINT_DISTANCE_SQ = 225.0;
    @NotNull
    private static final Regex NUMERIC_VALUE;
    @NotNull
    private static final Regex NUMERIC_PAIR;
    @NotNull
    private static final String TREASURE_CHEST_UNCOVERED = "You uncovered a treasure chest!";
    @NotNull
    private static final String TREASURE_CHEST_LOCKPICKED = "You have successfully picked the lock on this chest!";

    private MiningNukerController() {
    }

    public final boolean tick(@NotNull Config config) {
        long now;
        Intrinsics.checkNotNullParameter((Object)config, (String)"config");
        class_746 class_7462 = MiningNukerController.mc.field_1724;
        if (class_7462 == null) {
            return false;
        }
        class_746 player = class_7462;
        class_638 class_6382 = MiningNukerController.mc.field_1687;
        if (class_6382 == null) {
            return false;
        }
        class_638 level2 = class_6382;
        if (MiningNukerController.mc.field_1755 != null) {
            return false;
        }
        this.pruneExpired();
        if (config.getPowderChestCollector()) {
            class_2338 class_23382 = player.method_24515();
            Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"blockPosition(...)");
            if (this.handlePowderChest(config, class_23382)) {
                return true;
            }
        }
        if ((now = System.currentTimeMillis()) < nextMineMs) {
            return false;
        }
        if (!this.hasRequiredTool(config.getToolMode())) {
            return false;
        }
        List<Candidate> candidates = this.collectCandidates(config);
        if (candidates.isEmpty()) {
            return false;
        }
        int started = 0;
        for (Candidate candidate : candidates) {
            class_2338 class_23383 = player.method_24515();
            Intrinsics.checkNotNullExpressionValue((Object)class_23383, (String)"blockPosition(...)");
            class_2350 face = this.preferredFace(class_23383, candidate.getPos());
            class_636 class_6362 = MiningNukerController.mc.field_1761;
            if (class_6362 != null) {
                class_6362.method_2910(candidate.getPos(), face);
            }
            player.method_6104(class_1268.field_5808);
            ((Map)recentlyMined).put(candidate.getPos().method_10063(), now);
            if (++started < RangesKt.coerceAtLeast((int)config.getBlocksPerTick(), (int)1)) continue;
        }
        if (started > 0) {
            nextMineMs = now + (long)RangesKt.coerceAtLeast((int)config.getCooldownMs(), (int)10);
            return true;
        }
        return false;
    }

    public final void onChatMessage(@NotNull String message) {
        Intrinsics.checkNotNullParameter((Object)message, (String)"message");
        String string = ((Object)StringsKt.trim((CharSequence)message)).toString();
        if (Intrinsics.areEqual((Object)string, (Object)TREASURE_CHEST_UNCOVERED)) {
            this.flagPowderChestSearch();
        } else if (Intrinsics.areEqual((Object)string, (Object)TREASURE_CHEST_LOCKPICKED)) {
            powderChestHint = null;
            powderChestHintMs = 0L;
            powderChestSearchUntilMs = 0L;
        }
    }

    public final void onBlockChange(@NotNull class_2338 pos, @NotNull class_2680 oldState, @NotNull class_2680 newState) {
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        Intrinsics.checkNotNullParameter((Object)oldState, (String)"oldState");
        Intrinsics.checkNotNullParameter((Object)newState, (String)"newState");
        if (!this.isPowderChestArea()) {
            return;
        }
        if (this.isChestState(oldState) && !this.isChestState(newState) && Intrinsics.areEqual((Object)powderChestHint, (Object)pos)) {
            powderChestHint = null;
            powderChestHintMs = 0L;
        }
        if (!this.isChestState(oldState) && this.isChestState(newState) && this.isNearPlayer(pos, 225.0)) {
            this.markPowderChestHint(pos);
        }
    }

    public final void onParticlePacket(@NotNull class_2675 packet) {
        Intrinsics.checkNotNullParameter((Object)packet, (String)"packet");
        if (!this.isPowderChestArea()) {
            return;
        }
        if (System.currentTimeMillis() > powderChestSearchUntilMs) {
            return;
        }
        class_2338 class_23382 = class_2338.method_49637((double)packet.method_11544(), (double)packet.method_11547(), (double)packet.method_11546());
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"containing(...)");
        class_2338 origin = class_23382;
        class_638 class_6382 = MiningNukerController.mc.field_1687;
        if (class_6382 == null) {
            return;
        }
        class_638 level2 = class_6382;
        for (int dy = -1; dy < 2; ++dy) {
            for (int dx = -1; dx < 2; ++dx) {
                for (int dz = -1; dz < 2; ++dz) {
                    class_2338 candidate;
                    Intrinsics.checkNotNullExpressionValue((Object)origin.method_10069(dx, dy, dz), (String)"offset(...)");
                    class_2680 class_26802 = level2.method_8320(candidate);
                    Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
                    if (!this.isChestState(class_26802) || !this.isNearPlayer(candidate, 225.0)) continue;
                    this.markPowderChestHint(candidate);
                    return;
                }
            }
        }
    }

    public final void reset(boolean clearCounts) {
        recentlyMined.clear();
        clickedChests.clear();
        nextMineMs = 0L;
        powderChestHint = null;
        powderChestHintMs = 0L;
        powderChestSearchUntilMs = 0L;
        cachedPowderArea = false;
        cachedPowderAreaTick = -1L;
        if (clearCounts) {
            powderChestsCollected = 0;
        }
    }

    public static /* synthetic */ void reset$default(MiningNukerController miningNukerController, boolean bl, int n, Object object) {
        if ((n & 1) != 0) {
            bl = false;
        }
        miningNukerController.reset(bl);
    }

    public final int getPowderChestsCollected() {
        return powderChestsCollected;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final boolean hasQueuedPowderChest() {
        class_638 class_6382 = MiningNukerController.mc.field_1687;
        if (class_6382 == null) {
            return false;
        }
        class_638 level2 = class_6382;
        class_2338 class_23382 = powderChestHint;
        if (class_23382 == null) {
            return false;
        }
        class_2338 hint = class_23382;
        if (System.currentTimeMillis() - powderChestHintMs > 2500L) return false;
        class_2680 class_26802 = level2.method_8320(hint);
        Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
        if (!this.isChestState(class_26802)) return false;
        return true;
    }

    @NotNull
    public final List<CustomMatcher> parseCustomMatchers(@NotNull String raw) {
        Intrinsics.checkNotNullParameter((Object)raw, (String)"raw");
        List parsed = new ArrayList();
        char[] cArray = new char[]{',', ';', '\n', '\r'};
        for (String token : StringsKt.split$default((CharSequence)raw, (char[])cArray, (boolean)false, (int)0, (int)6, null)) {
            String trimmed = ((Object)StringsKt.trim((CharSequence)token)).toString();
            if (((CharSequence)trimmed).length() == 0) continue;
            if (NUMERIC_PAIR.matches((CharSequence)trimmed)) {
                Integer n = StringsKt.toIntOrNull((String)StringsKt.substringBefore$default((String)trimmed, (char)':', null, (int)2, null));
                if (n == null) {
                    continue;
                }
                int blockId = n;
                Integer n2 = StringsKt.toIntOrNull((String)StringsKt.substringAfter$default((String)trimmed, (char)':', null, (int)2, null));
                if (n2 == null) {
                    continue;
                }
                int stateId = n2;
                ((Collection)parsed).add(new CustomMatcher(null, blockId, stateId, 1, null));
                continue;
            }
            if (NUMERIC_VALUE.matches((CharSequence)trimmed)) {
                ((Collection)parsed).add(new CustomMatcher(null, StringsKt.toIntOrNull((String)trimmed), null, 5, null));
                continue;
            }
            Collection collection = parsed;
            String string = trimmed;
            Locale locale = Locale.ROOT;
            Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
            String string2 = string.toLowerCase(locale);
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
            collection.add(new CustomMatcher(string2, null, null, 6, null));
        }
        return parsed;
    }

    private final List<Candidate> collectCandidates(Config config) {
        class_638 class_6382 = MiningNukerController.mc.field_1687;
        if (class_6382 == null) {
            return CollectionsKt.emptyList();
        }
        class_638 level2 = class_6382;
        class_746 class_7462 = MiningNukerController.mc.field_1724;
        if (class_7462 == null) {
            return CollectionsKt.emptyList();
        }
        class_746 player = class_7462;
        class_2338 class_23382 = player.method_24515();
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"blockPosition(...)");
        class_2338 origin = class_23382;
        int range = RangesKt.coerceAtLeast((int)config.getRange(), (int)1);
        ArrayList candidates = new ArrayList();
        int dy = -range;
        if (dy <= range) {
            while (true) {
                int dx;
                if ((dx = -range) <= range) {
                    while (true) {
                        int dz;
                        if ((dz = -range) <= range) {
                            while (true) {
                                class_2680 state;
                                class_2338 pos;
                                Intrinsics.checkNotNullExpressionValue((Object)origin.method_10069(dx, dy, dz), (String)"offset(...)");
                                Intrinsics.checkNotNullExpressionValue((Object)level2.method_8320(pos), (String)"getBlockState(...)");
                                if (!(state.method_26215() || this.isChestState(state) || this.isRecentlyMined(pos) || state.method_26165((class_1657)player, (class_1922)level2, pos) <= 0.0f || !this.matchesTargetMode((class_1937)level2, pos, state, config))) {
                                    Collection collection = candidates;
                                    class_2338 class_23383 = pos.method_10062();
                                    Intrinsics.checkNotNullExpressionValue((Object)class_23383, (String)"immutable(...)");
                                    collection.add(new Candidate(class_23383, this.distanceToBlockSq(pos)));
                                }
                                if (dz == range) break;
                                ++dz;
                            }
                        }
                        if (dx == range) break;
                        ++dx;
                    }
                }
                if (dy == range) break;
                ++dy;
            }
        }
        List $this$sortBy$iv = candidates;
        boolean $i$f$sortBy = false;
        if ($this$sortBy$iv.size() > 1) {
            CollectionsKt.sortWith((List)$this$sortBy$iv, (Comparator)new Comparator(){

                public final int compare(T a, T b) {
                    Candidate it = (Candidate)a;
                    boolean bl = false;
                    Comparable comparable = Double.valueOf(it.getDistSq());
                    it = (Candidate)b;
                    Comparable comparable2 = comparable;
                    bl = false;
                    return ComparisonsKt.compareValues((Comparable)comparable2, (Comparable)Double.valueOf(it.getDistSq()));
                }
            });
        }
        return candidates;
    }

    private final boolean matchesTargetMode(class_1937 level2, class_2338 pos, class_2680 state, Config config) {
        return switch (WhenMappings.$EnumSwitchMapping$0[config.getTargetMode().ordinal()]) {
            case 1 -> this.isExposed(level2, pos);
            case 2 -> {
                if (this.isExposed(level2, pos) || !state.method_26234((class_1922)level2, pos)) {
                    yield true;
                }
                yield false;
            }
            case 3 -> this.matchesCustom(state, config.getCustomMatchers());
            default -> throw new NoWhenBranchMatchedException();
        };
    }

    private final boolean matchesCustom(class_2680 state, List<CustomMatcher> matchers) {
        if (matchers.isEmpty()) {
            return false;
        }
        String string = class_7923.field_41175.method_10221((Object)state.method_26204()).toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        String string2 = string;
        Locale locale = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
        String string3 = string2.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
        String blockId = string3;
        int rawBlockId = class_7923.field_41175.method_10206((Object)state.method_26204());
        int rawStateId = class_2248.method_9507((class_2680)state);
        for (CustomMatcher matcher : matchers) {
            if (matcher.getBlockId() != null && StringsKt.equals((String)matcher.getBlockId(), (String)blockId, (boolean)true)) {
                return true;
            }
            if (matcher.getRawBlockId() == null) continue;
            Integer n = matcher.getRawBlockId();
            int n2 = rawBlockId;
            if (n == null || n != n2) continue;
            if (matcher.getRawStateId() != null) {
                Integer n3 = matcher.getRawStateId();
                n2 = rawStateId;
                if (n3 == null || n3 != n2) continue;
            }
            return true;
        }
        return false;
    }

    private final boolean hasRequiredTool(ToolMode toolMode) {
        class_746 class_7462 = MiningNukerController.mc.field_1724;
        if (class_7462 == null) {
            return false;
        }
        class_746 player = class_7462;
        class_1799 class_17992 = player.method_6047();
        Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getMainHandItem(...)");
        class_1799 stack = class_17992;
        if (stack.method_7960()) {
            return false;
        }
        String string = stack.method_7964().getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        String string2 = this.stripFormatting(string);
        Locale locale = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
        String string3 = string2.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
        String toolName = string3;
        return switch (WhenMappings.$EnumSwitchMapping$1[toolMode.ordinal()]) {
            case 1 -> {
                String[] var6_6 = new String[]{"pickaxe", "drill", "gauntlet"};
                yield this.containsAny(toolName, var6_6);
            }
            case 2 -> {
                String[] var6_7 = new String[]{"shovel", "axe", "drill", "gauntlet"};
                yield this.containsAny(toolName, var6_7);
            }
            case 3 -> {
                if (((CharSequence)toolName).length() > 0) {
                    yield true;
                }
                yield false;
            }
            default -> throw new NoWhenBranchMatchedException();
        };
    }

    private final boolean handlePowderChest(Config config, class_2338 playerPos) {
        if (!this.isPowderChestArea()) {
            return false;
        }
        class_2338 class_23382 = this.choosePowderChestTarget(playerPos);
        if (class_23382 == null) {
            return false;
        }
        class_2338 target = class_23382;
        if (this.distanceToBlockSq(target) > 42.25) {
            return false;
        }
        long key = target.method_10063();
        long now = System.currentTimeMillis();
        Long lastClick = clickedChests.get(key);
        if (lastClick != null && now - lastClick < 30000L) {
            return false;
        }
        class_746 class_7462 = MiningNukerController.mc.field_1724;
        if (class_7462 == null) {
            return false;
        }
        class_746 player = class_7462;
        class_3965 hit = new class_3965(new class_243((double)target.method_10263() + 0.5, (double)target.method_10264() + 0.5, (double)target.method_10260() + 0.5), class_2350.field_11036, target, false);
        class_636 class_6362 = MiningNukerController.mc.field_1761;
        if (class_6362 != null) {
            class_6362.method_2896(player, class_1268.field_5808, hit);
        }
        player.method_6104(class_1268.field_5808);
        ((Map)clickedChests).put(key, now);
        int n = powderChestsCollected;
        powderChestsCollected = n + 1;
        powderChestSearchUntilMs = now + 2500L;
        return true;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    private final class_2338 choosePowderChestTarget(class_2338 playerPos) {
        block8: {
            v0 = MiningNukerController.mc.field_1687;
            if (v0 == null) {
                return null;
            }
            level = v0;
            now = System.currentTimeMillis();
            var5_4 = MiningNukerController.powderChestHint;
            if (var5_4 == null) break block8;
            it = var7_5 = var5_4;
            $i$a$-takeIf-MiningNukerController$choosePowderChestTarget$1 = false;
            if (now - MiningNukerController.powderChestHintMs > 2500L) ** GOTO lbl-1000
            v1 = level.method_8320(it);
            Intrinsics.checkNotNullExpressionValue((Object)v1, (String)"getBlockState(...)");
            if (MiningNukerController.INSTANCE.isChestState(v1)) {
                v2 = true;
            } else lbl-1000:
            // 2 sources

            {
                v2 = false;
            }
            v3 /* !! */  = var6_9 = v2 != false ? var7_5 : null;
            if (var6_9 != null) {
                it = var6_9;
                $i$a$-let-MiningNukerController$choosePowderChestTarget$2 = false;
                return it;
            }
        }
        if (now > MiningNukerController.powderChestSearchUntilMs) {
            return null;
        }
        best = null;
        bestDist = Infinity;
        for (dy = -4; dy < 5; ++dy) {
            for (dx = -6; dx < 7; ++dx) {
                for (dz = -6; dz < 7; ++dz) {
                    Intrinsics.checkNotNullExpressionValue((Object)playerPos.method_10069(dx, dy, dz), (String)"offset(...)");
                    v4 = level.method_8320(pos);
                    Intrinsics.checkNotNullExpressionValue((Object)v4, (String)"getBlockState(...)");
                    if (!this.isChestState(v4) || !((distSq = this.distanceToBlockSq(pos)) < bestDist)) continue;
                    bestDist = distSq;
                    best = pos.method_10062();
                }
            }
        }
        return best;
    }

    private final void markPowderChestHint(class_2338 pos) {
        powderChestHint = pos.method_10062();
        powderChestHintMs = System.currentTimeMillis();
        powderChestSearchUntilMs = powderChestHintMs + 2500L;
    }

    private final void flagPowderChestSearch() {
        powderChestSearchUntilMs = System.currentTimeMillis() + 2500L;
    }

    private final boolean isExposed(class_1937 level2, class_2338 pos) {
        for (class_2350 direction : class_2350.values()) {
            class_2680 neighbor;
            class_2338 neighborPos;
            Intrinsics.checkNotNullExpressionValue((Object)pos.method_10093(direction), (String)"relative(...)");
            Intrinsics.checkNotNullExpressionValue((Object)level2.method_8320(neighborPos), (String)"getBlockState(...)");
            if (!neighbor.method_26215() && neighbor.method_26234((class_1922)level2, neighborPos)) continue;
            return true;
        }
        return false;
    }

    private final boolean isRecentlyMined(class_2338 pos) {
        Long l = recentlyMined.get(pos.method_10063());
        if (l == null) {
            return false;
        }
        long minedAt = l;
        return System.currentTimeMillis() - minedAt < 750L;
    }

    private final class_2350 preferredFace(class_2338 from, class_2338 to) {
        return to.method_10264() > from.method_10264() ? class_2350.field_11033 : (to.method_10264() < from.method_10264() ? class_2350.field_11036 : (to.method_10263() > from.method_10263() ? class_2350.field_11039 : (to.method_10263() < from.method_10263() ? class_2350.field_11034 : (to.method_10260() > from.method_10260() ? class_2350.field_11043 : class_2350.field_11035))));
    }

    private final void pruneExpired() {
        long now = System.currentTimeMillis();
        recentlyMined.entrySet().removeIf(arg_0 -> MiningNukerController.pruneExpired$lambda$1(arg_0 -> MiningNukerController.pruneExpired$lambda$0(now, arg_0), arg_0));
        clickedChests.entrySet().removeIf(arg_0 -> MiningNukerController.pruneExpired$lambda$3(arg_0 -> MiningNukerController.pruneExpired$lambda$2(now, arg_0), arg_0));
    }

    private final boolean isPowderChestArea() {
        boolean bl;
        block5: {
            class_638 class_6382 = MiningNukerController.mc.field_1687;
            if (class_6382 == null) {
                return false;
            }
            class_638 level2 = class_6382;
            if (cachedPowderAreaTick == level2.method_75260()) {
                return cachedPowderArea;
            }
            cachedPowderAreaTick = level2.method_75260();
            Iterable $this$any$iv = this.readScoreboardLines((class_1937)level2);
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                bl = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    String line = (String)element$iv;
                    boolean bl2 = false;
                    if (!(StringsKt.contains((CharSequence)line, (CharSequence)"Crystal Hollows", (boolean)true) || StringsKt.contains((CharSequence)line, (CharSequence)"Mineshaft", (boolean)true))) continue;
                    bl = true;
                    break block5;
                }
                bl = false;
            }
        }
        cachedPowderArea = bl;
        return cachedPowderArea;
    }

    /*
     * WARNING - void declaration
     */
    private final List<String> readScoreboardLines(class_1937 level2) {
        void $this$mapNotNullTo$iv$iv;
        class_269 class_2692 = level2.method_8428();
        Intrinsics.checkNotNullExpressionValue((Object)class_2692, (String)"getScoreboard(...)");
        class_269 scoreboard = class_2692;
        class_266 class_2662 = scoreboard.method_1189(class_8646.field_45157);
        if (class_2662 == null) {
            return CollectionsKt.emptyList();
        }
        class_266 objective = class_2662;
        Collection collection = scoreboard.method_1184(objective);
        Intrinsics.checkNotNullExpressionValue((Object)collection, (String)"listPlayerScores(...)");
        Iterable $this$mapNotNull$iv = collection;
        boolean $i$f$mapNotNull = false;
        Iterable iterable = $this$mapNotNull$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$mapNotNullTo = false;
        void $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
        boolean $i$f$forEach = false;
        Iterator iterator = $this$forEach$iv$iv$iv.iterator();
        while (iterator.hasNext()) {
            String it$iv$iv;
            String string;
            String ownerName;
            class_268 team;
            Object element$iv$iv$iv;
            Object element$iv$iv = element$iv$iv$iv = iterator.next();
            boolean bl = false;
            class_9011 score = (class_9011)element$iv$iv;
            boolean bl2 = false;
            Intrinsics.checkNotNullExpressionValue((Object)score.comp_2127(), (String)"owner(...)");
            class_268 class_2682 = team = scoreboard.method_1164(ownerName);
            String raw = class_2682 != null ? class_2682.method_1144().getString() + ownerName + team.method_1136().getString() : ownerName;
            String it = string = INSTANCE.stripFormatting(raw);
            boolean bl3 = false;
            if ((!StringsKt.isBlank((CharSequence)it) ? string : null) == null) continue;
            it$iv$iv = it$iv$iv;
            boolean bl4 = false;
            destination$iv$iv.add(it$iv$iv);
        }
        return (List)destination$iv$iv;
    }

    private final boolean isNearPlayer(class_2338 pos, double maxDistanceSq) {
        return this.distanceToBlockSq(pos) <= maxDistanceSq;
    }

    private final double distanceToBlockSq(class_2338 pos) {
        class_746 class_7462 = MiningNukerController.mc.field_1724;
        if (class_7462 == null) {
            return Double.POSITIVE_INFINITY;
        }
        class_746 player = class_7462;
        double dx = (double)pos.method_10263() + 0.5 - player.method_23317();
        double dy = (double)pos.method_10264() + 0.5 - player.method_23318();
        double dz = (double)pos.method_10260() + 0.5 - player.method_23321();
        return dx * dx + dy * dy + dz * dz;
    }

    private final String stripFormatting(String text) {
        String string = class_124.method_539((String)text);
        if (string == null) {
            string = text;
        }
        return string;
    }

    private final boolean containsAny(String value, String ... parts) {
        boolean bl;
        block1: {
            String[] $this$any$iv = parts;
            boolean $i$f$any = false;
            int n = $this$any$iv.length;
            for (int i = 0; i < n; ++i) {
                String element$iv;
                String it = element$iv = $this$any$iv[i];
                boolean bl2 = false;
                if (!StringsKt.contains((CharSequence)value, (CharSequence)it, (boolean)true)) continue;
                bl = true;
                break block1;
            }
            bl = false;
        }
        return bl;
    }

    private final boolean isChestState(class_2680 state) {
        return state.method_27852(class_2246.field_10034) || state.method_27852(class_2246.field_10380);
    }

    private static final boolean pruneExpired$lambda$0(long $now, Map.Entry it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        Object v = it.getValue();
        Intrinsics.checkNotNullExpressionValue(v, (String)"<get-value>(...)");
        return $now - ((Number)v).longValue() >= 750L;
    }

    private static final boolean pruneExpired$lambda$1(Function1 $tmp0, Object p0) {
        return (Boolean)$tmp0.invoke(p0);
    }

    private static final boolean pruneExpired$lambda$2(long $now, Map.Entry it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        Object v = it.getValue();
        Intrinsics.checkNotNullExpressionValue(v, (String)"<get-value>(...)");
        return $now - ((Number)v).longValue() >= 30000L;
    }

    private static final boolean pruneExpired$lambda$3(Function1 $tmp0, Object p0) {
        return (Boolean)$tmp0.invoke(p0);
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        recentlyMined = new HashMap();
        clickedChests = new HashMap();
        cachedPowderAreaTick = -1L;
        NUMERIC_VALUE = new Regex("^\\d+$");
        NUMERIC_PAIR = new Regex("^\\d+:\\d+$");
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0082\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ$\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u001b\u0010\u0010\u001a\u00020\u000f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0011\u0010\u0013\u001a\u00020\u0012H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0011\u0010\u0016\u001a\u00020\u0015H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0018\u001a\u0004\b\u0019\u0010\tR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001a\u001a\u0004\b\u001b\u0010\u000b\u00a8\u0006\u001c"}, d2={"Lorg/cobalt/internal/mining/MiningNukerController$Candidate;", "", "Lnet/minecraft/class_2338;", "pos", "", "distSq", "<init>", "(Lnet/minecraft/class_2338;D)V", "component1", "()Lnet/minecraft/class_2338;", "component2", "()D", "copy", "(Lnet/minecraft/class_2338;D)Lorg/cobalt/internal/mining/MiningNukerController$Candidate;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "Lnet/minecraft/class_2338;", "getPos", "D", "getDistSq", "cobalt"})
    private static final class Candidate {
        @NotNull
        private final class_2338 pos;
        private final double distSq;

        public Candidate(@NotNull class_2338 pos, double distSq) {
            Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
            this.pos = pos;
            this.distSq = distSq;
        }

        @NotNull
        public final class_2338 getPos() {
            return this.pos;
        }

        public final double getDistSq() {
            return this.distSq;
        }

        @NotNull
        public final class_2338 component1() {
            return this.pos;
        }

        public final double component2() {
            return this.distSq;
        }

        @NotNull
        public final Candidate copy(@NotNull class_2338 pos, double distSq) {
            Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
            return new Candidate(pos, distSq);
        }

        public static /* synthetic */ Candidate copy$default(Candidate candidate, class_2338 class_23382, double d, int n, Object object) {
            if ((n & 1) != 0) {
                class_23382 = candidate.pos;
            }
            if ((n & 2) != 0) {
                d = candidate.distSq;
            }
            return candidate.copy(class_23382, d);
        }

        @NotNull
        public String toString() {
            return "Candidate(pos=" + this.pos + ", distSq=" + this.distSq + ")";
        }

        public int hashCode() {
            int result = this.pos.hashCode();
            result = result * 31 + Double.hashCode(this.distSq);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Candidate)) {
                return false;
            }
            Candidate candidate = (Candidate)other;
            if (!Intrinsics.areEqual((Object)this.pos, (Object)candidate.pos)) {
                return false;
            }
            return Double.compare(this.distSq, candidate.distSq) == 0;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0015\n\u0002\u0010\u000e\n\u0002\b\u000f\b\u0086\b\u0018\u00002\u00020\u0001BE\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\t\u001a\u00020\b\u0012\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u0012\u0006\u0010\u000e\u001a\u00020\r\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0010\u0010\u0011\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0010\u0010\u0013\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0013\u0010\u0012J\u0010\u0010\u0014\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0014\u0010\u0012J\u0010\u0010\u0015\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u0010\u0010\u0017\u001a\u00020\bH\u00c6\u0003\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u0016\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u000b0\nH\u00c6\u0003\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\u0010\u0010\u001b\u001a\u00020\rH\u00c6\u0003\u00a2\u0006\u0004\b\u001b\u0010\u001cJ\\\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\t\u001a\u00020\b2\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\b\b\u0002\u0010\u000e\u001a\u00020\rH\u00c6\u0001\u00a2\u0006\u0004\b\u001d\u0010\u001eJ\u001b\u0010 \u001a\u00020\r2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b \u0010!J\u0011\u0010\"\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\"\u0010\u0012J\u0011\u0010$\u001a\u00020#H\u00d6\u0081\u0004\u00a2\u0006\u0004\b$\u0010%R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010&\u001a\u0004\b'\u0010\u0012R\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010&\u001a\u0004\b(\u0010\u0012R\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010&\u001a\u0004\b)\u0010\u0012R\u0017\u0010\u0007\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010*\u001a\u0004\b+\u0010\u0016R\u0017\u0010\t\u001a\u00020\b8\u0006\u00a2\u0006\f\n\u0004\b\t\u0010,\u001a\u0004\b-\u0010\u0018R\u001d\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\n8\u0006\u00a2\u0006\f\n\u0004\b\f\u0010.\u001a\u0004\b/\u0010\u001aR\u0017\u0010\u000e\u001a\u00020\r8\u0006\u00a2\u0006\f\n\u0004\b\u000e\u00100\u001a\u0004\b1\u0010\u001c\u00a8\u00062"}, d2={"Lorg/cobalt/internal/mining/MiningNukerController$Config;", "", "", "range", "cooldownMs", "blocksPerTick", "Lorg/cobalt/internal/mining/MiningNukerController$TargetMode;", "targetMode", "Lorg/cobalt/internal/mining/MiningNukerController$ToolMode;", "toolMode", "", "Lorg/cobalt/internal/mining/MiningNukerController$CustomMatcher;", "customMatchers", "", "powderChestCollector", "<init>", "(IIILorg/cobalt/internal/mining/MiningNukerController$TargetMode;Lorg/cobalt/internal/mining/MiningNukerController$ToolMode;Ljava/util/List;Z)V", "component1", "()I", "component2", "component3", "component4", "()Lorg/cobalt/internal/mining/MiningNukerController$TargetMode;", "component5", "()Lorg/cobalt/internal/mining/MiningNukerController$ToolMode;", "component6", "()Ljava/util/List;", "component7", "()Z", "copy", "(IIILorg/cobalt/internal/mining/MiningNukerController$TargetMode;Lorg/cobalt/internal/mining/MiningNukerController$ToolMode;Ljava/util/List;Z)Lorg/cobalt/internal/mining/MiningNukerController$Config;", "other", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getRange", "getCooldownMs", "getBlocksPerTick", "Lorg/cobalt/internal/mining/MiningNukerController$TargetMode;", "getTargetMode", "Lorg/cobalt/internal/mining/MiningNukerController$ToolMode;", "getToolMode", "Ljava/util/List;", "getCustomMatchers", "Z", "getPowderChestCollector", "cobalt"})
    public static final class Config {
        private final int range;
        private final int cooldownMs;
        private final int blocksPerTick;
        @NotNull
        private final TargetMode targetMode;
        @NotNull
        private final ToolMode toolMode;
        @NotNull
        private final List<CustomMatcher> customMatchers;
        private final boolean powderChestCollector;

        public Config(int range, int cooldownMs, int blocksPerTick, @NotNull TargetMode targetMode, @NotNull ToolMode toolMode, @NotNull List<CustomMatcher> customMatchers, boolean powderChestCollector) {
            Intrinsics.checkNotNullParameter((Object)((Object)targetMode), (String)"targetMode");
            Intrinsics.checkNotNullParameter((Object)((Object)toolMode), (String)"toolMode");
            Intrinsics.checkNotNullParameter(customMatchers, (String)"customMatchers");
            this.range = range;
            this.cooldownMs = cooldownMs;
            this.blocksPerTick = blocksPerTick;
            this.targetMode = targetMode;
            this.toolMode = toolMode;
            this.customMatchers = customMatchers;
            this.powderChestCollector = powderChestCollector;
        }

        public final int getRange() {
            return this.range;
        }

        public final int getCooldownMs() {
            return this.cooldownMs;
        }

        public final int getBlocksPerTick() {
            return this.blocksPerTick;
        }

        @NotNull
        public final TargetMode getTargetMode() {
            return this.targetMode;
        }

        @NotNull
        public final ToolMode getToolMode() {
            return this.toolMode;
        }

        @NotNull
        public final List<CustomMatcher> getCustomMatchers() {
            return this.customMatchers;
        }

        public final boolean getPowderChestCollector() {
            return this.powderChestCollector;
        }

        public final int component1() {
            return this.range;
        }

        public final int component2() {
            return this.cooldownMs;
        }

        public final int component3() {
            return this.blocksPerTick;
        }

        @NotNull
        public final TargetMode component4() {
            return this.targetMode;
        }

        @NotNull
        public final ToolMode component5() {
            return this.toolMode;
        }

        @NotNull
        public final List<CustomMatcher> component6() {
            return this.customMatchers;
        }

        public final boolean component7() {
            return this.powderChestCollector;
        }

        @NotNull
        public final Config copy(int range, int cooldownMs, int blocksPerTick, @NotNull TargetMode targetMode, @NotNull ToolMode toolMode, @NotNull List<CustomMatcher> customMatchers, boolean powderChestCollector) {
            Intrinsics.checkNotNullParameter((Object)((Object)targetMode), (String)"targetMode");
            Intrinsics.checkNotNullParameter((Object)((Object)toolMode), (String)"toolMode");
            Intrinsics.checkNotNullParameter(customMatchers, (String)"customMatchers");
            return new Config(range, cooldownMs, blocksPerTick, targetMode, toolMode, customMatchers, powderChestCollector);
        }

        public static /* synthetic */ Config copy$default(Config config, int n, int n2, int n3, TargetMode targetMode, ToolMode toolMode, List list, boolean bl, int n4, Object object) {
            if ((n4 & 1) != 0) {
                n = config.range;
            }
            if ((n4 & 2) != 0) {
                n2 = config.cooldownMs;
            }
            if ((n4 & 4) != 0) {
                n3 = config.blocksPerTick;
            }
            if ((n4 & 8) != 0) {
                targetMode = config.targetMode;
            }
            if ((n4 & 0x10) != 0) {
                toolMode = config.toolMode;
            }
            if ((n4 & 0x20) != 0) {
                list = config.customMatchers;
            }
            if ((n4 & 0x40) != 0) {
                bl = config.powderChestCollector;
            }
            return config.copy(n, n2, n3, targetMode, toolMode, list, bl);
        }

        @NotNull
        public String toString() {
            return "Config(range=" + this.range + ", cooldownMs=" + this.cooldownMs + ", blocksPerTick=" + this.blocksPerTick + ", targetMode=" + this.targetMode + ", toolMode=" + this.toolMode + ", customMatchers=" + this.customMatchers + ", powderChestCollector=" + this.powderChestCollector + ")";
        }

        public int hashCode() {
            int result = Integer.hashCode(this.range);
            result = result * 31 + Integer.hashCode(this.cooldownMs);
            result = result * 31 + Integer.hashCode(this.blocksPerTick);
            result = result * 31 + this.targetMode.hashCode();
            result = result * 31 + this.toolMode.hashCode();
            result = result * 31 + ((Object)this.customMatchers).hashCode();
            result = result * 31 + Boolean.hashCode(this.powderChestCollector);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Config)) {
                return false;
            }
            Config config = (Config)other;
            if (this.range != config.range) {
                return false;
            }
            if (this.cooldownMs != config.cooldownMs) {
                return false;
            }
            if (this.blocksPerTick != config.blocksPerTick) {
                return false;
            }
            if (this.targetMode != config.targetMode) {
                return false;
            }
            if (this.toolMode != config.toolMode) {
                return false;
            }
            if (!Intrinsics.areEqual(this.customMatchers, config.customMatchers)) {
                return false;
            }
            return this.powderChestCollector == config.powderChestCollector;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u000b\b\u0086\b\u0018\u00002\u00020\u0001B+\u0012\n\b\u0002\u0010\u0003\u001a\u0004\u0018\u00010\u0002\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0004\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0012\u0010\t\u001a\u0004\u0018\u00010\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\nJ\u0012\u0010\u000b\u001a\u0004\u0018\u00010\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0012\u0010\r\u001a\u0004\u0018\u00010\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\fJ4\u0010\u000e\u001a\u00020\u00002\n\b\u0002\u0010\u0003\u001a\u0004\u0018\u00010\u00022\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00042\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u001b\u0010\u0012\u001a\u00020\u00112\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0011\u0010\u0014\u001a\u00020\u0004H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u0011\u0010\u0016\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0016\u0010\nR\u0019\u0010\u0003\u001a\u0004\u0018\u00010\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0017\u001a\u0004\b\u0018\u0010\nR\u0019\u0010\u0005\u001a\u0004\u0018\u00010\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0019\u001a\u0004\b\u001a\u0010\fR\u0019\u0010\u0006\u001a\u0004\u0018\u00010\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u0019\u001a\u0004\b\u001b\u0010\f\u00a8\u0006\u001c"}, d2={"Lorg/cobalt/internal/mining/MiningNukerController$CustomMatcher;", "", "", "blockId", "", "rawBlockId", "rawStateId", "<init>", "(Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;)V", "component1", "()Ljava/lang/String;", "component2", "()Ljava/lang/Integer;", "component3", "copy", "(Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;)Lorg/cobalt/internal/mining/MiningNukerController$CustomMatcher;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "()I", "toString", "Ljava/lang/String;", "getBlockId", "Ljava/lang/Integer;", "getRawBlockId", "getRawStateId", "cobalt"})
    public static final class CustomMatcher {
        @Nullable
        private final String blockId;
        @Nullable
        private final Integer rawBlockId;
        @Nullable
        private final Integer rawStateId;

        public CustomMatcher(@Nullable String blockId, @Nullable Integer rawBlockId, @Nullable Integer rawStateId) {
            this.blockId = blockId;
            this.rawBlockId = rawBlockId;
            this.rawStateId = rawStateId;
        }

        public /* synthetic */ CustomMatcher(String string, Integer n, Integer n2, int n3, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n3 & 1) != 0) {
                string = null;
            }
            if ((n3 & 2) != 0) {
                n = null;
            }
            if ((n3 & 4) != 0) {
                n2 = null;
            }
            this(string, n, n2);
        }

        @Nullable
        public final String getBlockId() {
            return this.blockId;
        }

        @Nullable
        public final Integer getRawBlockId() {
            return this.rawBlockId;
        }

        @Nullable
        public final Integer getRawStateId() {
            return this.rawStateId;
        }

        @Nullable
        public final String component1() {
            return this.blockId;
        }

        @Nullable
        public final Integer component2() {
            return this.rawBlockId;
        }

        @Nullable
        public final Integer component3() {
            return this.rawStateId;
        }

        @NotNull
        public final CustomMatcher copy(@Nullable String blockId, @Nullable Integer rawBlockId, @Nullable Integer rawStateId) {
            return new CustomMatcher(blockId, rawBlockId, rawStateId);
        }

        public static /* synthetic */ CustomMatcher copy$default(CustomMatcher customMatcher, String string, Integer n, Integer n2, int n3, Object object) {
            if ((n3 & 1) != 0) {
                string = customMatcher.blockId;
            }
            if ((n3 & 2) != 0) {
                n = customMatcher.rawBlockId;
            }
            if ((n3 & 4) != 0) {
                n2 = customMatcher.rawStateId;
            }
            return customMatcher.copy(string, n, n2);
        }

        @NotNull
        public String toString() {
            return "CustomMatcher(blockId=" + this.blockId + ", rawBlockId=" + this.rawBlockId + ", rawStateId=" + this.rawStateId + ")";
        }

        public int hashCode() {
            int result = this.blockId == null ? 0 : this.blockId.hashCode();
            result = result * 31 + (this.rawBlockId == null ? 0 : ((Object)this.rawBlockId).hashCode());
            result = result * 31 + (this.rawStateId == null ? 0 : ((Object)this.rawStateId).hashCode());
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CustomMatcher)) {
                return false;
            }
            CustomMatcher customMatcher = (CustomMatcher)other;
            if (!Intrinsics.areEqual((Object)this.blockId, (Object)customMatcher.blockId)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.rawBlockId, (Object)customMatcher.rawBlockId)) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.rawStateId, (Object)customMatcher.rawStateId);
        }

        public CustomMatcher() {
            this(null, null, null, 7, null);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/internal/mining/MiningNukerController$TargetMode;", "", "<init>", "(Ljava/lang/String;I)V", "EXPOSED_ONLY", "EXPOSED_OR_SOFT", "CUSTOM", "cobalt"})
    public static final class TargetMode
    extends Enum<TargetMode> {
        public static final /* enum */ TargetMode EXPOSED_ONLY = new TargetMode();
        public static final /* enum */ TargetMode EXPOSED_OR_SOFT = new TargetMode();
        public static final /* enum */ TargetMode CUSTOM = new TargetMode();
        private static final /* synthetic */ TargetMode[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        public static TargetMode[] values() {
            return (TargetMode[])$VALUES.clone();
        }

        public static TargetMode valueOf(String value) {
            return Enum.valueOf(TargetMode.class, value);
        }

        @NotNull
        public static EnumEntries<TargetMode> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = targetModeArray = new TargetMode[]{TargetMode.EXPOSED_ONLY, TargetMode.EXPOSED_OR_SOFT, TargetMode.CUSTOM};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/internal/mining/MiningNukerController$ToolMode;", "", "<init>", "(Ljava/lang/String;I)V", "STONE", "SOFT", "CUSTOM", "cobalt"})
    public static final class ToolMode
    extends Enum<ToolMode> {
        public static final /* enum */ ToolMode STONE = new ToolMode();
        public static final /* enum */ ToolMode SOFT = new ToolMode();
        public static final /* enum */ ToolMode CUSTOM = new ToolMode();
        private static final /* synthetic */ ToolMode[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        public static ToolMode[] values() {
            return (ToolMode[])$VALUES.clone();
        }

        public static ToolMode valueOf(String value) {
            return Enum.valueOf(ToolMode.class, value);
        }

        @NotNull
        public static EnumEntries<ToolMode> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = toolModeArray = new ToolMode[]{ToolMode.STONE, ToolMode.SOFT, ToolMode.CUSTOM};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;
        public static final /* synthetic */ int[] $EnumSwitchMapping$1;

        static {
            int[] nArray = new int[TargetMode.values().length];
            try {
                nArray[TargetMode.EXPOSED_ONLY.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[TargetMode.EXPOSED_OR_SOFT.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[TargetMode.CUSTOM.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
            nArray = new int[ToolMode.values().length];
            try {
                nArray[ToolMode.STONE.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[ToolMode.SOFT.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[ToolMode.CUSTOM.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$1 = nArray;
        }
    }
}

