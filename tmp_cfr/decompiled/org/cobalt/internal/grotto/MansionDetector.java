/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.collections.ArrayDeque
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_1937
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2680
 *  net.minecraft.class_2818
 *  net.minecraft.class_2826
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.grotto;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.ArrayDeque;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.internal.grotto.CrystalHollowsDetector;
import org.cobalt.internal.grotto.GrottoChat;
import org.cobalt.internal.grotto.GrottoCommands;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u008e\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0016\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001NB\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u0004\u0018\u00010\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0006J\r\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\u000f\u0010\u000b\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u000b\u0010\u0003J\u0017\u0010\u000e\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\fH\u0007\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u001f\u0010\u0013\u001a\u00020\n2\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\u0010H\u0002\u00a2\u0006\u0004\b\u0013\u0010\u0014J7\u0010\u001c\u001a\u00020\u00072\u0006\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u00102\u0006\u0010\u0018\u001a\u00020\u00102\u0006\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u0010H\u0002\u00a2\u0006\u0004\b\u001c\u0010\u001dJ'\u0010!\u001a\u00020\u00072\u0006\u0010\u001e\u001a\u00020\u00102\u0006\u0010\u001f\u001a\u00020\u00102\u0006\u0010 \u001a\u00020\u0010H\u0002\u00a2\u0006\u0004\b!\u0010\"J'\u0010#\u001a\u00020\u00072\u0006\u0010\u001e\u001a\u00020\u00102\u0006\u0010\u001f\u001a\u00020\u00102\u0006\u0010 \u001a\u00020\u0010H\u0002\u00a2\u0006\u0004\b#\u0010\"J/\u0010&\u001a\u00020\u00072\u0006\u0010\u001e\u001a\u00020\u00102\u0006\u0010\u001f\u001a\u00020\u00102\u0006\u0010 \u001a\u00020\u00102\u0006\u0010%\u001a\u00020$H\u0002\u00a2\u0006\u0004\b&\u0010'J\u0017\u0010)\u001a\u00020\u00072\u0006\u0010(\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b)\u0010*J)\u0010-\u001a\u0004\u0018\u00010\u00152\u0006\u0010,\u001a\u00020+2\u0006\u0010\u0017\u001a\u00020\u00102\u0006\u0010\u0018\u001a\u00020\u0010H\u0002\u00a2\u0006\u0004\b-\u0010.J\u001f\u00100\u001a\u00020/2\u0006\u0010\u001e\u001a\u00020\u00102\u0006\u0010 \u001a\u00020\u0010H\u0002\u00a2\u0006\u0004\b0\u00101R\u0014\u00103\u001a\u0002028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b3\u00104R\u0014\u00105\u001a\u00020\u00108\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b5\u00106R\u0014\u00107\u001a\u00020\u00108\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b7\u00106R\u0014\u00108\u001a\u00020\u00108\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b8\u00106R\u0014\u00109\u001a\u00020\u00108\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b9\u00106R\u0016\u0010:\u001a\u00020\u00108\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b:\u00106R\u0018\u0010<\u001a\u0004\u0018\u00010;8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b<\u0010=R$\u0010@\u001a\u0012\u0012\u0004\u0012\u00020/0>j\b\u0012\u0004\u0012\u00020/`?8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b@\u0010AR\u001a\u0010D\u001a\b\u0012\u0004\u0012\u00020C0B8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bD\u0010ER0\u0010H\u001a\u001e\u0012\u0004\u0012\u00020/\u0012\u0004\u0012\u00020\u00190Fj\u000e\u0012\u0004\u0012\u00020/\u0012\u0004\u0012\u00020\u0019`G8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bH\u0010IR\u0018\u0010J\u001a\u0004\u0018\u00010\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bJ\u0010KR\u0016\u0010L\u001a\u00020\u00078\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bL\u0010M\u00a8\u0006O"}, d2={"Lorg/cobalt/internal/grotto/MansionDetector;", "", "<init>", "()V", "Lnet/minecraft/class_2338;", "getMansionAnchor", "()Lnet/minecraft/class_2338;", "", "hasMansionAnchor", "()Z", "", "reset", "Lorg/cobalt/api/event/impl/client/TickEvent$End;", "event", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$End;)V", "", "centerX", "centerZ", "enqueueNearbyChunks", "(II)V", "Lnet/minecraft/class_2818;", "chunk", "chunkX", "chunkZ", "Lorg/cobalt/internal/grotto/MansionDetector$Cursor;", "cursor", "steps", "advanceCursor", "(Lnet/minecraft/class_2818;IILorg/cobalt/internal/grotto/MansionDetector$Cursor;I)Z", "x", "y", "z", "matchesCoreAtStonebrick", "(III)Z", "isAir", "Lnet/minecraft/class_2248;", "block", "isBlock", "(IIILnet/minecraft/class_2248;)Z", "pos", "isChestBlock", "(Lnet/minecraft/class_2338;)Z", "Lnet/minecraft/class_1937;", "level", "getLoadedChunk", "(Lnet/minecraft/class_1937;II)Lnet/minecraft/class_2818;", "", "chunkKey", "(II)J", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "SCAN_EVERY_TICKS", "I", "MAX_CHUNK_RADIUS", "MAX_STEPS_PER_TICK", "MAX_CHUNKS_PER_TICK", "tickCounter", "Lnet/minecraft/class_638;", "lastLevel", "Lnet/minecraft/class_638;", "Ljava/util/HashSet;", "Lkotlin/collections/HashSet;", "queuedOrScannedChunks", "Ljava/util/HashSet;", "Lkotlin/collections/ArrayDeque;", "", "chunkQueue", "Lkotlin/collections/ArrayDeque;", "Ljava/util/HashMap;", "Lkotlin/collections/HashMap;", "cursors", "Ljava/util/HashMap;", "mansionAnchor", "Lnet/minecraft/class_2338;", "announcedStart", "Z", "Cursor", "cobalt"})
@SourceDebugExtension(value={"SMAP\nMansionDetector.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MansionDetector.kt\norg/cobalt/internal/grotto/MansionDetector\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,220:1\n37#2,2:221\n*S KotlinDebug\n*F\n+ 1 MansionDetector.kt\norg/cobalt/internal/grotto/MansionDetector\n*L\n88#1:221,2\n*E\n"})
public final class MansionDetector {
    @NotNull
    public static final MansionDetector INSTANCE = new MansionDetector();
    @NotNull
    private static final class_310 mc;
    private static final int SCAN_EVERY_TICKS = 1;
    private static final int MAX_CHUNK_RADIUS = 8;
    private static final int MAX_STEPS_PER_TICK = 30000;
    private static final int MAX_CHUNKS_PER_TICK = 3;
    private static int tickCounter;
    @Nullable
    private static class_638 lastLevel;
    @NotNull
    private static final HashSet<Long> queuedOrScannedChunks;
    @NotNull
    private static final ArrayDeque<long[]> chunkQueue;
    @NotNull
    private static final HashMap<Long, Cursor> cursors;
    @Nullable
    private static class_2338 mansionAnchor;
    private static boolean announcedStart;

    private MansionDetector() {
    }

    @Nullable
    public final class_2338 getMansionAnchor() {
        return mansionAnchor;
    }

    public final boolean hasMansionAnchor() {
        return mansionAnchor != null;
    }

    private final void reset() {
        queuedOrScannedChunks.clear();
        chunkQueue.clear();
        cursors.clear();
        mansionAnchor = null;
        tickCounter = 0;
        announcedStart = false;
        lastLevel = MansionDetector.mc.field_1687;
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.End event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_638 level2 = MansionDetector.mc.field_1687;
        class_746 player = MansionDetector.mc.field_1724;
        if (level2 == null || player == null) {
            this.reset();
            return;
        }
        if (lastLevel != level2) {
            this.reset();
        }
        if (!CrystalHollowsDetector.isInCrystalHollows()) {
            return;
        }
        if (!announcedStart) {
            announcedStart = true;
            GrottoChat.autoRoutes("Mansion detector active.");
        }
        if (mansionAnchor != null) {
            return;
        }
        int n = tickCounter;
        if ((tickCounter = n + 1) % 1 != 0) {
            return;
        }
        this.enqueueNearbyChunks(player.method_31476().field_9181, player.method_31476().field_9180);
        int added = 0;
        while (added < 3 && !((Collection)chunkQueue).isEmpty()) {
            int cz;
            long[] entry = (long[])chunkQueue.removeFirst();
            int cx = (int)entry[0];
            long key = this.chunkKey(cx, cz = (int)entry[1]);
            if (cursors.containsKey(key)) continue;
            ((Map)cursors).put(key, new Cursor());
            ++added;
        }
        if (cursors.isEmpty()) {
            return;
        }
        int budget = 30000;
        int processed = 0;
        Set<Long> set = cursors.keySet();
        Intrinsics.checkNotNullExpressionValue(set, (String)"<get-keys>(...)");
        Collection $this$toTypedArray$iv = set;
        boolean $i$f$toTypedArray = false;
        Collection thisCollection$iv = $this$toTypedArray$iv;
        for (Long l : thisCollection$iv.toArray(new Long[0])) {
            Cursor cursor;
            Intrinsics.checkNotNull((Object)l);
            long key = ((Number)l).longValue();
            if (budget <= 0 || processed >= 3) break;
            if (cursors.get(key) == null) continue;
            int cx = (int)(key >> 32);
            int cz = (int)key;
            class_2818 chunk = this.getLoadedChunk((class_1937)level2, cx, cz);
            if (chunk == null) {
                cursors.remove(key);
                continue;
            }
            int stepLimit = Math.min(Math.max(1, budget), 10000);
            boolean done = this.advanceCursor(chunk, cx, cz, cursor, stepLimit);
            ++processed;
            budget -= stepLimit;
            if (mansionAnchor != null) {
                return;
            }
            if (!done) continue;
            cursors.remove(key);
        }
    }

    private final void enqueueNearbyChunks(int centerX, int centerZ) {
        for (int dx = -8; dx < 9; ++dx) {
            for (int dz = -8; dz < 9; ++dz) {
                int cx = centerX + dx;
                int cz = centerZ + dz;
                long key = this.chunkKey(cx, cz);
                if (!queuedOrScannedChunks.add(key)) continue;
                long[] lArray = new long[]{cx, cz};
                chunkQueue.add((Object)lArray);
            }
        }
    }

    private final boolean advanceCursor(class_2818 chunk, int chunkX, int chunkZ, Cursor cursor, int steps) {
        class_2826[] class_2826Array = chunk.method_12006();
        Intrinsics.checkNotNullExpressionValue((Object)class_2826Array, (String)"getSections(...)");
        class_2826[] sections = class_2826Array;
        int remaining = steps;
        while (remaining-- > 0) {
            class_2338 anchor;
            int worldZ;
            int worldX;
            class_2680 state;
            int n;
            class_2826 section;
            while (cursor.getSectionIdx() < sections.length) {
                Intrinsics.checkNotNullExpressionValue((Object)sections[cursor.getSectionIdx()], (String)"get(...)");
                if (!section.method_38292()) break;
                n = cursor.getSectionIdx();
                cursor.setSectionIdx(n + 1);
                cursor.setIdxInSection(0);
            }
            if (cursor.getSectionIdx() >= sections.length) {
                return true;
            }
            if (sections[cursor.getSectionIdx()] == null) {
                return true;
            }
            if (cursor.getIdxInSection() >= 4096) {
                n = cursor.getSectionIdx();
                cursor.setSectionIdx(n + 1);
                cursor.setIdxInSection(0);
                continue;
            }
            int n2 = cursor.getIdxInSection();
            cursor.setIdxInSection(n2 + 1);
            int idx = n2;
            int localX = idx & 0xF;
            int localY = idx >> 4 & 0xF;
            int localZ = idx >> 8 & 0xF;
            int worldY = (cursor.getSectionIdx() << 4) + localY;
            Intrinsics.checkNotNullExpressionValue((Object)section.method_12254(localX, localY, localZ), (String)"getBlockState(...)");
            if (!Intrinsics.areEqual((Object)state.method_26204(), (Object)class_2246.field_10056) || !this.matchesCoreAtStonebrick(worldX = (chunkX << 4) + localX, worldY, worldZ = (chunkZ << 4) + localZ) || !this.isChestBlock(anchor = new class_2338(worldX + 4, worldY + 3, worldZ - 8))) continue;
            mansionAnchor = anchor;
            GrottoCommands.setDetectedMansionCore(anchor);
            GrottoChat.autoRoutes("Mansion core detected at " + anchor.method_10263() + "," + anchor.method_10264() + "," + anchor.method_10260());
            return false;
        }
        return false;
    }

    private final boolean matchesCoreAtStonebrick(int x, int y, int z) {
        class_2248 class_22482 = class_2246.field_10056;
        Intrinsics.checkNotNullExpressionValue((Object)class_22482, (String)"STONE_BRICKS");
        if (!this.isBlock(x, y + 1, z, class_22482)) {
            return false;
        }
        class_2248 class_22483 = class_2246.field_10056;
        Intrinsics.checkNotNullExpressionValue((Object)class_22483, (String)"STONE_BRICKS");
        if (!this.isBlock(x, y + 2, z, class_22483)) {
            return false;
        }
        class_2248 class_22484 = class_2246.field_10056;
        Intrinsics.checkNotNullExpressionValue((Object)class_22484, (String)"STONE_BRICKS");
        if (!this.isBlock(x, y + 3, z, class_22484)) {
            return false;
        }
        class_2248 class_22485 = class_2246.field_10056;
        Intrinsics.checkNotNullExpressionValue((Object)class_22485, (String)"STONE_BRICKS");
        if (!this.isBlock(x, y + 4, z, class_22485)) {
            return false;
        }
        if (!this.isAir(x, y - 2, z)) {
            return false;
        }
        if (!this.isAir(x, y - 5, z)) {
            return false;
        }
        return this.isAir(x, y - 7, z);
    }

    private final boolean isAir(int x, int y, int z) {
        class_638 class_6382 = MansionDetector.mc.field_1687;
        if (class_6382 == null) {
            return false;
        }
        class_638 level2 = class_6382;
        return level2.method_8320(new class_2338(x, y, z)).method_26215();
    }

    private final boolean isBlock(int x, int y, int z, class_2248 block) {
        class_638 class_6382 = MansionDetector.mc.field_1687;
        if (class_6382 == null) {
            return false;
        }
        class_638 level2 = class_6382;
        return Intrinsics.areEqual((Object)level2.method_8320(new class_2338(x, y, z)).method_26204(), (Object)block);
    }

    private final boolean isChestBlock(class_2338 pos) {
        class_638 class_6382 = MansionDetector.mc.field_1687;
        if (class_6382 == null) {
            return false;
        }
        class_638 level2 = class_6382;
        class_2248 class_22482 = level2.method_8320(pos).method_26204();
        Intrinsics.checkNotNullExpressionValue((Object)class_22482, (String)"getBlock(...)");
        class_2248 block = class_22482;
        return Intrinsics.areEqual((Object)block, (Object)class_2246.field_10034) || Intrinsics.areEqual((Object)block, (Object)class_2246.field_10380);
    }

    private final class_2818 getLoadedChunk(class_1937 level2, int chunkX, int chunkZ) {
        Object object;
        Object object2 = this;
        try {
            MansionDetector $this$getLoadedChunk_u24lambda_u240 = object2;
            boolean bl = false;
            object = Result.constructor-impl((Object)level2.method_8398().method_12126(chunkX, chunkZ, false));
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        object2 = object;
        return (class_2818)(Result.isFailure-impl((Object)object2) ? null : object2);
    }

    private final long chunkKey(int x, int z) {
        return (long)x << 32 ^ (long)z & 0xFFFFFFFFL;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        queuedOrScannedChunks = new HashSet();
        chunkQueue = new ArrayDeque();
        cursors = new HashMap();
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\n\b\u0002\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003R\"\u0010\u0005\u001a\u00020\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0005\u0010\u0006\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\"\u0010\u000b\u001a\u00020\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u000b\u0010\u0006\u001a\u0004\b\f\u0010\b\"\u0004\b\r\u0010\n\u00a8\u0006\u000e"}, d2={"Lorg/cobalt/internal/grotto/MansionDetector$Cursor;", "", "<init>", "()V", "", "sectionIdx", "I", "getSectionIdx", "()I", "setSectionIdx", "(I)V", "idxInSection", "getIdxInSection", "setIdxInSection", "cobalt"})
    private static final class Cursor {
        private int sectionIdx;
        private int idxInSection;

        public final int getSectionIdx() {
            return this.sectionIdx;
        }

        public final void setSectionIdx(int n) {
            this.sectionIdx = n;
        }

        public final int getIdxInSection() {
            return this.idxInSection;
        }

        public final void setIdxInSection(int n) {
            this.idxInSection = n;
        }
    }
}

