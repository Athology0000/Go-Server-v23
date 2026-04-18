/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  kotlin.Metadata
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.comparisons.ComparisonsKt
 *  kotlin.io.FilesKt
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.StringsKt
 *  net.minecraft.class_1923
 *  net.minecraft.class_1937
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_2680
 *  net.minecraft.class_2818
 *  net.minecraft.class_2826
 *  net.minecraft.class_310
 *  net.minecraft.class_315
 *  net.minecraft.class_5321
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.grotto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_5321;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.WorldRenderContext;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.util.render.Render3D;
import org.cobalt.internal.grotto.CrystalHollowsDetector;
import org.cobalt.internal.grotto.GrottoChat;
import org.cobalt.internal.mining.FairyModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00e6\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010#\n\u0002\u0010\"\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0006\u0097\u0001\u0098\u0001\u0099\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u000f\u0010\u0006\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u0006\u0010\u0003J\u001f\u0010\n\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u0007H\u0002\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0015\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u0007\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u000f\u0010\u0010\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0010\u0010\u0003J\u0017\u0010\u0012\u001a\u00020\u00072\b\u0010\u0011\u001a\u0004\u0018\u00010\r\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0017\u0010\u0014\u001a\u00020\u00072\b\u0010\u0011\u001a\u0004\u0018\u00010\r\u00a2\u0006\u0004\b\u0014\u0010\u0013J\u0017\u0010\u0015\u001a\u00020\u00072\b\u0010\u0011\u001a\u0004\u0018\u00010\r\u00a2\u0006\u0004\b\u0015\u0010\u0013J\r\u0010\u0016\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0003J\u000f\u0010\u0017\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u0017\u0010\u0003J\u000f\u0010\u0018\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u0018\u0010\u0003J\u0017\u0010\u001b\u001a\u00020\u00042\u0006\u0010\u001a\u001a\u00020\u0019H\u0007\u00a2\u0006\u0004\b\u001b\u0010\u001cJ\u0017\u0010\u001e\u001a\u00020\u00042\u0006\u0010\u001a\u001a\u00020\u001dH\u0007\u00a2\u0006\u0004\b\u001e\u0010\u001fJ\u000f\u0010 \u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b \u0010\u0003J\u0017\u0010#\u001a\u00020\u00042\u0006\u0010\"\u001a\u00020!H\u0002\u00a2\u0006\u0004\b#\u0010$J\u001f\u0010(\u001a\u00020\u00042\u0006\u0010&\u001a\u00020%2\u0006\u0010'\u001a\u00020%H\u0002\u00a2\u0006\u0004\b(\u0010)J)\u0010-\u001a\u0004\u0018\u00010,2\u0006\u0010\"\u001a\u00020!2\u0006\u0010*\u001a\u00020%2\u0006\u0010+\u001a\u00020%H\u0002\u00a2\u0006\u0004\b-\u0010.J-\u00100\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010/\u001a\u00020,2\u0006\u0010*\u001a\u00020%2\u0006\u0010+\u001a\u00020%H\u0002\u00a2\u0006\u0004\b0\u00101J\u0019\u00104\u001a\u00020\u00072\b\u00103\u001a\u0004\u0018\u000102H\u0002\u00a2\u0006\u0004\b4\u00105J\u000f\u00106\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b6\u0010\u0003J\u000f\u00107\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b7\u0010\u0003J\u000f\u00108\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b8\u0010\u0003J\u000f\u00109\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b9\u0010\u0003J'\u0010=\u001a\u00020\u00042\u0006\u0010:\u001a\u00020%2\u0006\u0010;\u001a\u00020%2\u0006\u0010<\u001a\u00020%H\u0002\u00a2\u0006\u0004\b=\u0010>J'\u0010@\u001a\u00020?2\u0006\u0010:\u001a\u00020%2\u0006\u0010;\u001a\u00020%2\u0006\u0010<\u001a\u00020%H\u0002\u00a2\u0006\u0004\b@\u0010AJ\u001f\u0010D\u001a\u00020%2\u0006\u0010B\u001a\u00020%2\u0006\u0010C\u001a\u00020%H\u0002\u00a2\u0006\u0004\bD\u0010EJ+\u0010J\u001a\u00020%2\u0012\u0010H\u001a\u000e\u0012\u0004\u0012\u00020G\u0012\u0004\u0012\u00020%0F2\u0006\u0010I\u001a\u00020GH\u0002\u00a2\u0006\u0004\bJ\u0010KR\u0014\u0010M\u001a\u00020L8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bM\u0010NR\u0014\u0010O\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bO\u0010PR\u0014\u0010Q\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bQ\u0010PR\u0014\u0010R\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bR\u0010PR\u0014\u0010S\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bS\u0010PR\u0014\u0010T\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bT\u0010PR\u0014\u0010U\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bU\u0010PR\u0014\u0010V\u001a\u00020?8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bV\u0010WR\u0014\u0010X\u001a\u00020?8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bX\u0010WR\u0014\u0010Y\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bY\u0010PR\u0014\u0010Z\u001a\u00020?8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bZ\u0010WR\u0014\u0010[\u001a\u00020?8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b[\u0010WR\u0014\u0010\\\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\\\u0010PR\u0014\u0010]\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b]\u0010PR\u0014\u0010^\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b^\u0010PR\u0014\u0010_\u001a\u00020?8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b_\u0010WR$\u0010b\u001a\u0012\u0012\u0004\u0012\u00020\r0`j\b\u0012\u0004\u0012\u00020\r`a8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bb\u0010cR$\u0010f\u001a\u0012\u0012\u0004\u0012\u00020?0dj\b\u0012\u0004\u0012\u00020?`e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bf\u0010gR$\u0010h\u001a\u0012\u0012\u0004\u0012\u00020?0dj\b\u0012\u0004\u0012\u00020?`e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bh\u0010gR0\u0010k\u001a\u001e\u0012\u0004\u0012\u00020?\u0012\u0004\u0012\u00020\r0ij\u000e\u0012\u0004\u0012\u00020?\u0012\u0004\u0012\u00020\r`j8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bk\u0010lR\u001c\u0010o\u001a\n n*\u0004\u0018\u00010m0m8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bo\u0010pR\u0018\u0010r\u001a\u0004\u0018\u00010q8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\br\u0010sR\u0016\u0010t\u001a\u00020\u00078\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bt\u0010uR \u0010v\u001a\u000e\u0012\u0004\u0012\u00020G\u0012\u0004\u0012\u00020?0F8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bv\u0010wR \u0010x\u001a\u000e\u0012\u0004\u0012\u00020G\u0012\u0004\u0012\u00020%0F8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bx\u0010wR8\u0010{\u001a&\u0012\f\u0012\n n*\u0004\u0018\u00010G0G n*\u0012\u0012\f\u0012\n n*\u0004\u0018\u00010G0G\u0018\u00010z0y8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b{\u0010|R$\u0010~\u001a\u0012\u0012\u0004\u0012\u00020}0`j\b\u0012\u0004\u0012\u00020}`a8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b~\u0010cR\u001e\u0010\u0081\u0001\u001a\t\u0012\u0005\u0012\u00030\u0080\u00010\u007f8\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0081\u0001\u0010\u0082\u0001R\u001f\u0010\u0085\u0001\u001a\n\u0012\u0005\u0012\u00030\u0084\u00010\u0083\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0085\u0001\u0010\u0086\u0001R\u0018\u0010\u0087\u0001\u001a\u00020?8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0087\u0001\u0010WR\u0018\u0010\u0088\u0001\u001a\u00020%8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0088\u0001\u0010PR\u0018\u0010\u0089\u0001\u001a\u00020%8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0089\u0001\u0010PR\u0018\u0010\u008a\u0001\u001a\u00020\u00078\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u008a\u0001\u0010uR\u001b\u0010\u008b\u0001\u001a\u0004\u0018\u00010!8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u008b\u0001\u0010\u008c\u0001R#\u0010\u008f\u0001\u001a\f\u0012\u0005\u0012\u00030\u008e\u0001\u0018\u00010\u008d\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u008f\u0001\u0010\u0090\u0001R\u0018\u0010\u0091\u0001\u001a\u00020%8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0091\u0001\u0010PR\u0018\u0010\u0092\u0001\u001a\u00020\u00078\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0092\u0001\u0010uR\u001c\u0010\u0094\u0001\u001a\u0005\u0018\u00010\u0093\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0094\u0001\u0010\u0095\u0001R\u0018\u0010\u0096\u0001\u001a\u00020\u00078\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0096\u0001\u0010u\u00a8\u0006\u009a\u0001"}, d2={"Lorg/cobalt/internal/grotto/GrottoScanner;", "", "<init>", "()V", "", "toggle", "syncSetting", "", "value", "announce", "setEnabled", "(ZZ)V", "", "Lnet/minecraft/class_2338;", "getMagentaBlocks", "()Ljava/util/List;", "initBlacklistFile", "pos", "isBlacklisted", "(Lnet/minecraft/class_2338;)Z", "addPermanentBlacklist", "removePermanentBlacklist", "clearPermanentBlacklist", "loadUserBlacklist", "saveUserBlacklist", "Lorg/cobalt/api/event/impl/client/TickEvent$End;", "event", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$End;)V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "onRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "applyResults", "Lnet/minecraft/class_638;", "level", "pruneMinedBlocks", "(Lnet/minecraft/class_638;)V", "", "centerX", "centerZ", "rebuildChunkQueue", "(II)V", "chunkX", "chunkZ", "Lnet/minecraft/class_2818;", "getLoadedChunk", "(Lnet/minecraft/class_638;II)Lnet/minecraft/class_2818;", "chunk", "scanChunkOffThread", "(Lnet/minecraft/class_2818;II)Ljava/util/List;", "Lnet/minecraft/class_2680;", "state", "isMagentaState", "(Lnet/minecraft/class_2680;)Z", "startWorkerIfNeeded", "stopWorker", "workerLoop", "clearAll", "x", "y", "z", "addBuiltIn", "(III)V", "", "pack", "(III)J", "a", "b", "floorDiv", "(II)I", "Ljava/util/concurrent/ConcurrentHashMap;", "", "map", "key", "inc", "(Ljava/util/concurrent/ConcurrentHashMap;Ljava/lang/String;)I", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "MAX_CHUNK_RADIUS", "I", "MIN_SCAN_Y", "MAX_SCAN_Y", "MAX_ENQUEUE_PER_TICK", "MAX_RESULTS_APPLY_PER_TICK", "MAX_BACKLOG", "EMPTY_RETRY_MS_FAST", "J", "EMPTY_RETRY_MS_SLOW", "EMPTY_TRIES_BEFORE_FINALIZE", "ERROR_RETRY_MS", "SCAN_EVERY_TICKS", "RENDER_MAX_DIST_BLOCKS", "TRACER_CELL_SIZE", "TRACER_MAX", "PRUNE_INTERVAL_TICKS", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "magentaBlocks", "Ljava/util/ArrayList;", "Ljava/util/HashSet;", "Lkotlin/collections/HashSet;", "magentaKeys", "Ljava/util/HashSet;", "builtInBlacklist", "Ljava/util/HashMap;", "Lkotlin/collections/HashMap;", "userBlacklist", "Ljava/util/HashMap;", "Lcom/google/gson/Gson;", "kotlin.jvm.PlatformType", "gson", "Lcom/google/gson/Gson;", "Ljava/io/File;", "blacklistFile", "Ljava/io/File;", "blacklistLoaded", "Z", "cooldownUntilMs", "Ljava/util/concurrent/ConcurrentHashMap;", "emptyTries", "", "", "queuedChunks", "Ljava/util/Set;", "Lorg/cobalt/internal/grotto/GrottoScanner$ChunkEntry;", "chunkQueue", "Ljava/util/concurrent/BlockingQueue;", "Lorg/cobalt/internal/grotto/GrottoScanner$ChunkWork;", "workQueue", "Ljava/util/concurrent/BlockingQueue;", "Ljava/util/concurrent/ConcurrentLinkedQueue;", "Lorg/cobalt/internal/grotto/GrottoScanner$ScanResult;", "resultQueue", "Ljava/util/concurrent/ConcurrentLinkedQueue;", "tickCounter", "queueCenterChunkX", "queueCenterChunkZ", "queueValid", "lastWorld", "Lnet/minecraft/class_638;", "Lnet/minecraft/class_5321;", "Lnet/minecraft/class_1937;", "lastDimension", "Lnet/minecraft/class_5321;", "generation", "workerRunning", "Ljava/util/concurrent/ExecutorService;", "executor", "Ljava/util/concurrent/ExecutorService;", "enabled", "ChunkEntry", "ChunkWork", "ScanResult", "cobalt"})
@SourceDebugExtension(value={"SMAP\nGrottoScanner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GrottoScanner.kt\norg/cobalt/internal/grotto/GrottoScanner\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,577:1\n1#2:578\n1915#3,2:579\n1915#3,2:581\n1915#3,2:583\n1915#3,2:585\n1915#3,2:587\n*S KotlinDebug\n*F\n+ 1 GrottoScanner.kt\norg/cobalt/internal/grotto/GrottoScanner\n*L\n189#1:579,2\n210#1:581,2\n303#1:583,2\n342#1:585,2\n380#1:587,2\n*E\n"})
public final class GrottoScanner {
    @NotNull
    public static final GrottoScanner INSTANCE = new GrottoScanner();
    @NotNull
    private static final class_310 mc;
    private static final int MAX_CHUNK_RADIUS = 8;
    private static final int MIN_SCAN_Y = 30;
    private static final int MAX_SCAN_Y = 185;
    private static final int MAX_ENQUEUE_PER_TICK = 64;
    private static final int MAX_RESULTS_APPLY_PER_TICK = 8;
    private static final int MAX_BACKLOG = 512;
    private static final long EMPTY_RETRY_MS_FAST = 600L;
    private static final long EMPTY_RETRY_MS_SLOW = 1500L;
    private static final int EMPTY_TRIES_BEFORE_FINALIZE = 10;
    private static final long ERROR_RETRY_MS = 350L;
    private static final long SCAN_EVERY_TICKS = 1L;
    private static final int RENDER_MAX_DIST_BLOCKS = 140;
    private static final int TRACER_CELL_SIZE = 10;
    private static final int TRACER_MAX = 120;
    private static final long PRUNE_INTERVAL_TICKS = 20L;
    @NotNull
    private static final ArrayList<class_2338> magentaBlocks;
    @NotNull
    private static final HashSet<Long> magentaKeys;
    @NotNull
    private static final HashSet<Long> builtInBlacklist;
    @NotNull
    private static final HashMap<Long, class_2338> userBlacklist;
    private static final Gson gson;
    @Nullable
    private static File blacklistFile;
    private static boolean blacklistLoaded;
    @NotNull
    private static final ConcurrentHashMap<String, Long> cooldownUntilMs;
    @NotNull
    private static final ConcurrentHashMap<String, Integer> emptyTries;
    private static final Set<String> queuedChunks;
    @NotNull
    private static final ArrayList<ChunkEntry> chunkQueue;
    @NotNull
    private static final BlockingQueue<ChunkWork> workQueue;
    @NotNull
    private static final ConcurrentLinkedQueue<ScanResult> resultQueue;
    private static long tickCounter;
    private static int queueCenterChunkX;
    private static int queueCenterChunkZ;
    private static boolean queueValid;
    @Nullable
    private static class_638 lastWorld;
    @Nullable
    private static class_5321<class_1937> lastDimension;
    private static int generation;
    private static boolean workerRunning;
    @Nullable
    private static ExecutorService executor;
    private static boolean enabled;

    private GrottoScanner() {
    }

    public final void toggle() {
        this.setEnabled(!enabled, true);
    }

    private final void syncSetting() {
        boolean desired = (Boolean)FairyModule.INSTANCE.getScannerEnabled().getValue();
        if (desired != enabled) {
            this.setEnabled(desired, false);
        }
    }

    private final void setEnabled(boolean value, boolean announce) {
        if (enabled == value) {
            return;
        }
        enabled = value;
        FairyModule.INSTANCE.getScannerEnabled().setValue(value);
        if (announce) {
            String state = enabled ? "Enabled" : "Disabled";
            GrottoChat.grotto(state);
        }
        if (enabled) {
            GrottoScanner.initBlacklistFile();
            this.startWorkerIfNeeded();
        } else {
            this.stopWorker();
            this.clearAll();
        }
    }

    @JvmStatic
    @NotNull
    public static final List<class_2338> getMagentaBlocks() {
        return magentaBlocks;
    }

    @JvmStatic
    public static final void initBlacklistFile() {
        if (blacklistFile == null) {
            blacklistFile = new File(GrottoScanner.mc.field_1697, "config/cobalt/grotto_blacklist.json");
        }
        if (!blacklistLoaded) {
            INSTANCE.loadUserBlacklist();
            blacklistLoaded = true;
        }
    }

    public final boolean isBlacklisted(@Nullable class_2338 pos) {
        if (pos == null) {
            return false;
        }
        long key = this.pack(pos.method_10263(), pos.method_10264(), pos.method_10260());
        return builtInBlacklist.contains(key) || userBlacklist.containsKey(key);
    }

    public final boolean addPermanentBlacklist(@Nullable class_2338 pos) {
        if (pos == null) {
            return false;
        }
        GrottoScanner.initBlacklistFile();
        long key = this.pack(pos.method_10263(), pos.method_10264(), pos.method_10260());
        if (builtInBlacklist.contains(key)) {
            return false;
        }
        if (userBlacklist.containsKey(key)) {
            return false;
        }
        ((Map)userBlacklist).put(key, pos);
        this.saveUserBlacklist();
        return true;
    }

    public final boolean removePermanentBlacklist(@Nullable class_2338 pos) {
        if (pos == null) {
            return false;
        }
        GrottoScanner.initBlacklistFile();
        long key = this.pack(pos.method_10263(), pos.method_10264(), pos.method_10260());
        class_2338 removed = userBlacklist.remove(key);
        if (removed != null) {
            this.saveUserBlacklist();
            return true;
        }
        return false;
    }

    public final void clearPermanentBlacklist() {
        GrottoScanner.initBlacklistFile();
        userBlacklist.clear();
        this.saveUserBlacklist();
    }

    private final void loadUserBlacklist() {
        Object $this$loadUserBlacklist_u24lambda_u241;
        String text;
        Object $this$loadUserBlacklist_u24lambda_u240;
        userBlacklist.clear();
        File file = blacklistFile;
        if (file == null) {
            return;
        }
        File file2 = file;
        if (!file2.exists()) {
            return;
        }
        Object object = this;
        try {
            $this$loadUserBlacklist_u24lambda_u240 = object;
            boolean bl = false;
            $this$loadUserBlacklist_u24lambda_u240 = Result.constructor-impl((Object)FilesKt.readText$default((File)file2, null, (int)1, null));
        }
        catch (Throwable bl) {
            $this$loadUserBlacklist_u24lambda_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
        }
        object = $this$loadUserBlacklist_u24lambda_u240;
        String string = (String)(Result.isFailure-impl((Object)object) ? null : object);
        String string2 = string != null ? ((Object)StringsKt.trim((CharSequence)string)).toString() : null;
        if (string2 == null) {
            string2 = "";
        }
        if (((CharSequence)(text = string2)).length() == 0) {
            return;
        }
        $this$loadUserBlacklist_u24lambda_u240 = this;
        try {
            $this$loadUserBlacklist_u24lambda_u241 = (GrottoScanner)$this$loadUserBlacklist_u24lambda_u240;
            boolean bl = false;
            $this$loadUserBlacklist_u24lambda_u241 = Result.constructor-impl((Object)JsonParser.parseString((String)text).getAsJsonObject());
        }
        catch (Throwable throwable) {
            $this$loadUserBlacklist_u24lambda_u241 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        $this$loadUserBlacklist_u24lambda_u240 = $this$loadUserBlacklist_u24lambda_u241;
        JsonObject jsonObject = (JsonObject)(Result.isFailure-impl((Object)$this$loadUserBlacklist_u24lambda_u240) ? null : $this$loadUserBlacklist_u24lambda_u240);
        if (jsonObject == null) {
            return;
        }
        JsonObject obj = jsonObject;
        JsonArray jsonArray = obj.getAsJsonArray("blocks");
        if (jsonArray == null) {
            return;
        }
        JsonArray blocks = jsonArray;
        Iterable $this$forEach$iv = (Iterable)blocks;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            JsonObject entry;
            JsonElement element = (JsonElement)element$iv;
            boolean bl = false;
            if (!element.isJsonObject() || !(entry = element.getAsJsonObject()).has("x") || !entry.has("y") || !entry.has("z")) continue;
            int x = entry.get("x").getAsInt();
            int y = entry.get("y").getAsInt();
            int z = entry.get("z").getAsInt();
            long key = INSTANCE.pack(x, y, z);
            ((Map)userBlacklist).put(key, new class_2338(x, y, z));
        }
    }

    private final void saveUserBlacklist() {
        File file = blacklistFile;
        if (file == null) {
            return;
        }
        File file2 = file;
        File file3 = file2.getParentFile();
        if (file3 != null) {
            file3.mkdirs();
        }
        JsonObject root = new JsonObject();
        root.addProperty("version", (Number)1);
        JsonArray blocks = new JsonArray();
        Collection<class_2338> collection = userBlacklist.values();
        Intrinsics.checkNotNullExpressionValue(collection, (String)"<get-values>(...)");
        Iterable $this$forEach$iv = collection;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            class_2338 pos = (class_2338)element$iv;
            boolean bl = false;
            JsonObject obj = new JsonObject();
            obj.addProperty("x", (Number)pos.method_10263());
            obj.addProperty("y", (Number)pos.method_10264());
            obj.addProperty("z", (Number)pos.method_10260());
            blocks.add((JsonElement)obj);
        }
        root.add("blocks", (JsonElement)blocks);
        String string = gson.toJson((JsonElement)root);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toJson(...)");
        FilesKt.writeText$default((File)file2, (String)string, null, (int)2, null);
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.End event) {
        long l;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        this.syncSetting();
        if (!enabled) {
            return;
        }
        class_638 level2 = GrottoScanner.mc.field_1687;
        class_746 player = GrottoScanner.mc.field_1724;
        if (level2 == null || player == null) {
            return;
        }
        if (!CrystalHollowsDetector.isInCrystalHollows()) {
            return;
        }
        GrottoScanner.initBlacklistFile();
        this.startWorkerIfNeeded();
        if (level2 != lastWorld) {
            lastWorld = level2;
            this.clearAll();
        }
        class_5321 class_53212 = level2.method_27983();
        Intrinsics.checkNotNullExpressionValue((Object)class_53212, (String)"dimension(...)");
        class_5321 dimension = class_53212;
        if (!Intrinsics.areEqual(lastDimension, (Object)dimension)) {
            lastDimension = dimension;
            this.clearAll();
        }
        if ((tickCounter = (l = tickCounter) + 1L) % 1L != 0L) {
            return;
        }
        this.applyResults();
        if (tickCounter % 20L == 0L) {
            this.pruneMinedBlocks(level2);
        }
        class_1923 class_19232 = player.method_31476();
        Intrinsics.checkNotNullExpressionValue((Object)class_19232, (String)"chunkPosition(...)");
        class_1923 chunkPos = class_19232;
        int cx = chunkPos.field_9181;
        int cz = chunkPos.field_9180;
        if (!queueValid || cx != queueCenterChunkX || cz != queueCenterChunkZ) {
            this.rebuildChunkQueue(cx, cz);
        }
        if (chunkQueue.isEmpty()) {
            return;
        }
        if (workQueue.size() > 512) {
            return;
        }
        long now = System.currentTimeMillis();
        int enqueued = 0;
        while (!((Collection)chunkQueue).isEmpty() && enqueued < 64) {
            class_2818 chunk;
            ChunkEntry entry;
            Intrinsics.checkNotNullExpressionValue((Object)chunkQueue.remove(0), (String)"removeAt(...)");
            String key = entry.getCx() + "," + entry.getCz();
            if (queuedChunks.contains(key)) continue;
            Long cooldown = cooldownUntilMs.get(key);
            if (cooldown != null) {
                long l2 = Long.MAX_VALUE;
                if (cooldown == l2 || now < cooldown) continue;
            }
            if (this.getLoadedChunk(level2, entry.getCx(), entry.getCz()) == null) continue;
            queuedChunks.add(key);
            workQueue.offer(new ChunkWork(chunk, entry.getCx(), entry.getCz(), generation));
            ++enqueued;
        }
    }

    @SubscribeEvent
    public final void onRender(@NotNull WorldRenderEvent.Last event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!enabled) {
            return;
        }
        if (!CrystalHollowsDetector.isInCrystalHollows()) {
            return;
        }
        class_746 class_7462 = GrottoScanner.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        class_638 class_6382 = GrottoScanner.mc.field_1687;
        if (class_6382 == null) {
            return;
        }
        class_638 level2 = class_6382;
        if (magentaBlocks.isEmpty()) {
            return;
        }
        WorldRenderContext context = event.getContext();
        class_243 class_2432 = player.method_33571();
        Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"getEyePosition(...)");
        class_243 camPos = class_2432;
        double maxDistSq = 19600.0;
        if (((Boolean)FairyModule.INSTANCE.getScannerRenderBoxes().getValue()).booleanValue()) {
            Iterable $this$forEach$iv = magentaBlocks;
            boolean $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                double dz;
                double dy;
                class_2338 pos = (class_2338)element$iv;
                boolean bl = false;
                double dx = (double)pos.method_10263() + 0.5 - camPos.field_1352;
                if (dx * dx + (dy = (double)pos.method_10264() + 0.5 - camPos.field_1351) * dy + (dz = (double)pos.method_10260() + 0.5 - camPos.field_1350) * dz > maxDistSq) continue;
                class_238 box = new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + 1.0, (double)pos.method_10264() + 1.0, (double)pos.method_10260() + 1.0);
                Render3D.drawBox(context, box, new Color(255, 0, 255, 115), true);
            }
        }
        if (((Boolean)FairyModule.INSTANCE.getScannerRenderTracers().getValue()).booleanValue()) {
            HashMap selected = new HashMap();
            HashMap selectedDist = new HashMap();
            Iterator<class_2338> iterator = magentaBlocks.iterator();
            Intrinsics.checkNotNullExpressionValue(iterator, (String)"iterator(...)");
            Iterator<Object> iterator2 = iterator;
            while (iterator2.hasNext()) {
                int cellZ;
                double dz;
                double dy;
                Object object = iterator2.next();
                Intrinsics.checkNotNullExpressionValue((Object)object, (String)"next(...)");
                class_2338 pos = (class_2338)object;
                double dx = (double)pos.method_10263() + 0.5 - camPos.field_1352;
                double distSq = dx * dx + (dy = (double)pos.method_10264() + 0.5 - camPos.field_1351) * dy + (dz = (double)pos.method_10260() + 0.5 - camPos.field_1350) * dz;
                if (distSq > maxDistSq) continue;
                int cellX = this.floorDiv(pos.method_10263(), 10);
                long cellKey = (long)cellX << 32 ^ (long)(cellZ = this.floorDiv(pos.method_10260(), 10)) & 0xFFFFFFFFL;
                Double existingDist = (Double)selectedDist.get(cellKey);
                if (existingDist == null || distSq < existingDist) {
                    ((Map)selectedDist).put(cellKey, distSq);
                    ((Map)selected).put(cellKey, pos);
                }
                if (selected.size() < 120) continue;
            }
            class_243 class_2433 = player.method_33571();
            Intrinsics.checkNotNullExpressionValue((Object)class_2433, (String)"getEyePosition(...)");
            class_243 start = class_2433;
            Collection collection = selected.values();
            Intrinsics.checkNotNullExpressionValue(collection, (String)"<get-values>(...)");
            Iterable $this$forEach$iv = collection;
            boolean $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                class_2338 pos = (class_2338)element$iv;
                boolean bl = false;
                class_243 end = new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264() + 0.5, (double)pos.method_10260() + 0.5);
                Render3D.drawLine(context, start, end, new Color(255, 0, 255, 200), true, 0.75f);
            }
        }
    }

    private final void applyResults() {
        int applied = 0;
        long now = System.currentTimeMillis();
        while (applied < 8 && resultQueue.poll() != null) {
            List found;
            ScanResult result;
            if (result.getGen() != generation) continue;
            String key = result.getChunkX() + "," + result.getChunkZ();
            if (result.getHadError()) {
                ((Map)cooldownUntilMs).put(key, now + 350L);
                ++applied;
                continue;
            }
            List list = result.getFound();
            if (list == null) {
                list = CollectionsKt.emptyList();
            }
            if ((found = list).isEmpty()) {
                int tries = this.inc(emptyTries, key);
                long delay = tries <= 4 ? 600L : 1500L;
                ((Map)cooldownUntilMs).put(key, now + delay);
                if (tries >= 10) {
                    ((Map)cooldownUntilMs).put(key, Long.MAX_VALUE);
                }
                ++applied;
                continue;
            }
            emptyTries.remove(key);
            ((Map)cooldownUntilMs).put(key, Long.MAX_VALUE);
            Iterable $this$forEach$iv = found;
            boolean $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                class_2338 pos = (class_2338)element$iv;
                boolean bl = false;
                long packed = INSTANCE.pack(pos.method_10263(), pos.method_10264(), pos.method_10260());
                if (builtInBlacklist.contains(packed) || userBlacklist.containsKey(packed) || !magentaKeys.add(packed)) continue;
                magentaBlocks.add(pos);
            }
            ++applied;
        }
    }

    private final void pruneMinedBlocks(class_638 level2) {
        if (magentaBlocks.isEmpty()) {
            return;
        }
        Iterator<class_2338> iterator = magentaBlocks.iterator();
        Intrinsics.checkNotNullExpressionValue(iterator, (String)"iterator(...)");
        Iterator<class_2338> iterator2 = iterator;
        while (iterator2.hasNext()) {
            class_2338 pos;
            Intrinsics.checkNotNullExpressionValue((Object)iterator2.next(), (String)"next(...)");
            if (this.isMagentaState(level2.method_8320(pos))) continue;
            iterator2.remove();
            magentaKeys.remove(this.pack(pos.method_10263(), pos.method_10264(), pos.method_10260()));
        }
    }

    private final void rebuildChunkQueue(int centerX, int centerZ) {
        chunkQueue.clear();
        int radius = 8;
        class_315 class_3152 = GrottoScanner.mc.field_1690;
        Intrinsics.checkNotNullExpressionValue((Object)class_3152, (String)"options");
        class_315 options = class_3152;
        Object object = options.method_42503().method_41753();
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"get(...)");
        int n = ((Number)object).intValue();
        radius = Math.min(radius, n);
        int x = centerX - radius;
        int n2 = centerX + radius;
        if (x <= n2) {
            while (true) {
                int n3;
                int z;
                if ((z = centerZ - radius) <= (n3 = centerZ + radius)) {
                    while (true) {
                        int dx = x - centerX;
                        int dz = z - centerZ;
                        int dist = dx * dx + dz * dz;
                        chunkQueue.add(new ChunkEntry(x, z, dist));
                        if (z == n3) break;
                        ++z;
                    }
                }
                if (x == n2) break;
                ++x;
            }
        }
        CollectionsKt.sortWith((List)chunkQueue, (Comparator)new Comparator(){

            public final int compare(T a, T b) {
                ChunkEntry it = (ChunkEntry)a;
                boolean bl = false;
                Comparable comparable = Integer.valueOf(it.getDist2());
                it = (ChunkEntry)b;
                Comparable comparable2 = comparable;
                bl = false;
                return ComparisonsKt.compareValues((Comparable)comparable2, (Comparable)Integer.valueOf(it.getDist2()));
            }
        });
        queueCenterChunkX = centerX;
        queueCenterChunkZ = centerZ;
        queueValid = true;
    }

    private final class_2818 getLoadedChunk(class_638 level2, int chunkX, int chunkZ) {
        Object object;
        Object object2 = this;
        try {
            GrottoScanner $this$getLoadedChunk_u24lambda_u240 = object2;
            boolean bl = false;
            object = Result.constructor-impl((Object)level2.method_2935().method_12126(chunkX, chunkZ, false));
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        object2 = object;
        return (class_2818)(Result.isFailure-impl((Object)object2) ? null : object2);
    }

    private final List<class_2338> scanChunkOffThread(class_2818 chunk, int chunkX, int chunkZ) {
        ArrayList<class_2338> found = new ArrayList<class_2338>();
        class_2826[] class_2826Array = chunk.method_12006();
        Intrinsics.checkNotNullExpressionValue((Object)class_2826Array, (String)"getSections(...)");
        class_2826[] sections = class_2826Array;
        int n = sections.length;
        for (int sectionIdx = 0; sectionIdx < n; ++sectionIdx) {
            int baseY;
            class_2826 section;
            if (sections[sectionIdx] == null || section.method_38292() || (baseY = sectionIdx << 4) > 185 || baseY + 15 < 30) continue;
            for (int localY = 0; localY < 16; ++localY) {
                int worldY = baseY + localY;
                if (worldY < 30 || worldY > 185) continue;
                for (int localZ = 0; localZ < 16; ++localZ) {
                    for (int localX = 0; localX < 16; ++localX) {
                        class_2680 state;
                        Intrinsics.checkNotNullExpressionValue((Object)section.method_12254(localX, localY, localZ), (String)"getBlockState(...)");
                        if (!this.isMagentaState(state)) continue;
                        int worldX = (chunkX << 4) + localX;
                        int worldZ = (chunkZ << 4) + localZ;
                        found.add(new class_2338(worldX, worldY, worldZ));
                    }
                }
            }
        }
        return found;
    }

    private final boolean isMagentaState(class_2680 state) {
        if (state == null) {
            return false;
        }
        class_2248 class_22482 = state.method_26204();
        Intrinsics.checkNotNullExpressionValue((Object)class_22482, (String)"getBlock(...)");
        class_2248 block = class_22482;
        return Intrinsics.areEqual((Object)block, (Object)class_2246.field_10574) || Intrinsics.areEqual((Object)block, (Object)class_2246.field_10469);
    }

    private final void startWorkerIfNeeded() {
        block6: {
            block5: {
                block4: {
                    if (workerRunning && executor != null) {
                        return;
                    }
                    workerRunning = true;
                    if (executor == null) break block4;
                    ExecutorService executorService = executor;
                    Intrinsics.checkNotNull((Object)executorService);
                    if (!executorService.isShutdown()) break block5;
                }
                executor = Executors.newSingleThreadExecutor(GrottoScanner::startWorkerIfNeeded$lambda$0);
            }
            ExecutorService executorService = executor;
            if (executorService == null) break block6;
            executorService.submit(GrottoScanner::startWorkerIfNeeded$lambda$1);
        }
    }

    private final void stopWorker() {
        int n = generation;
        generation = n + 1;
        workerRunning = false;
        GrottoScanner grottoScanner = this;
        try {
            GrottoScanner $this$stopWorker_u24lambda_u240 = grottoScanner;
            boolean bl = false;
            ExecutorService executorService = executor;
            Object object = Result.constructor-impl(executorService != null ? executorService.shutdownNow() : null);
        }
        catch (Throwable throwable) {
            Object object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        executor = null;
        workQueue.clear();
        resultQueue.clear();
        queuedChunks.clear();
    }

    private final void workerLoop() {
        while (workerRunning && !Thread.currentThread().isInterrupted()) {
            List<class_2338> list;
            ChunkWork chunkWork;
            try {
                chunkWork = workQueue.poll(250L, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                return;
            }
            ChunkWork work = chunkWork;
            if (work == null) continue;
            if (work.getGen() != generation) {
                queuedChunks.remove(work.getChunkX() + "," + work.getChunkZ());
                continue;
            }
            boolean hadError = false;
            try {
                list = this.scanChunkOffThread(work.getChunk(), work.getChunkX(), work.getChunkZ());
            }
            catch (Throwable throwable) {
                hadError = true;
                list = CollectionsKt.emptyList();
            }
            List<class_2338> found = list;
            queuedChunks.remove(work.getChunkX() + "," + work.getChunkZ());
            resultQueue.add(new ScanResult(found, work.getChunkX(), work.getChunkZ(), hadError, work.getGen()));
        }
    }

    private final void clearAll() {
        magentaBlocks.clear();
        magentaKeys.clear();
        cooldownUntilMs.clear();
        emptyTries.clear();
        queuedChunks.clear();
        chunkQueue.clear();
        workQueue.clear();
        resultQueue.clear();
        queueValid = false;
        tickCounter = 0L;
        int n = generation;
        generation = n + 1;
    }

    private final void addBuiltIn(int x, int y, int z) {
        builtInBlacklist.add(this.pack(x, y, z));
    }

    private final long pack(int x, int y, int z) {
        long lx = (long)x & 0x3FFFFFFL;
        long ly = (long)y & 0xFFFL;
        long lz = (long)z & 0x3FFFFFFL;
        return lx << 38 | ly << 26 | lz;
    }

    private final int floorDiv(int a, int b) {
        int r = a / b;
        int mod = a % b;
        if (mod != 0 && mod < 0 != b < 0) {
            --r;
        }
        return r;
    }

    private final int inc(ConcurrentHashMap<String, Integer> map, String key) {
        Integer n = map.get(key);
        int next = (n != null ? n : 0) + 1;
        ((Map)map).put(key, next);
        return next;
    }

    private static final Thread startWorkerIfNeeded$lambda$0(Runnable runnable) {
        Thread thread = new Thread(runnable, "GrottoScanner");
        thread.setDaemon(true);
        return thread;
    }

    private static final void startWorkerIfNeeded$lambda$1() {
        INSTANCE.workerLoop();
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        magentaBlocks = new ArrayList();
        magentaKeys = new HashSet();
        builtInBlacklist = new HashSet();
        userBlacklist = new HashMap();
        gson = new GsonBuilder().setPrettyPrinting().create();
        cooldownUntilMs = new ConcurrentHashMap();
        emptyTries = new ConcurrentHashMap();
        queuedChunks = Collections.newSetFromMap(new ConcurrentHashMap());
        chunkQueue = new ArrayList();
        workQueue = new LinkedBlockingQueue();
        resultQueue = new ConcurrentLinkedQueue();
        INSTANCE.addBuiltIn(522, 116, 561);
        INSTANCE.addBuiltIn(523, 116, 560);
        INSTANCE.addBuiltIn(523, 115, 559);
        INSTANCE.addBuiltIn(522, 115, 557);
        INSTANCE.addBuiltIn(521, 116, 559);
        INSTANCE.addBuiltIn(521, 116, 558);
        INSTANCE.addBuiltIn(520, 115, 559);
        INSTANCE.addBuiltIn(520, 117, 559);
        INSTANCE.addBuiltIn(521, 119, 560);
        INSTANCE.addBuiltIn(520, 118, 560);
        INSTANCE.addBuiltIn(519, 117, 560);
        INSTANCE.addBuiltIn(506, 117, 559);
        INSTANCE.addBuiltIn(505, 119, 560);
        INSTANCE.addBuiltIn(504, 118, 560);
        INSTANCE.addBuiltIn(504, 117, 559);
        INSTANCE.addBuiltIn(506, 115, 558);
        INSTANCE.addBuiltIn(503, 116, 559);
        INSTANCE.addBuiltIn(504, 115, 558);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0082\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\tJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\tJ.\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u001b\u0010\u0010\u001a\u00020\u000f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0011\u0010\u0012\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0012\u0010\tJ\u0011\u0010\u0014\u001a\u00020\u0013H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0014\u0010\u0015R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0016\u001a\u0004\b\u0017\u0010\tR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0016\u001a\u0004\b\u0018\u0010\tR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0016\u001a\u0004\b\u0019\u0010\t\u00a8\u0006\u001a"}, d2={"Lorg/cobalt/internal/grotto/GrottoScanner$ChunkEntry;", "", "", "cx", "cz", "dist2", "<init>", "(III)V", "component1", "()I", "component2", "component3", "copy", "(III)Lorg/cobalt/internal/grotto/GrottoScanner$ChunkEntry;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getCx", "getCz", "getDist2", "cobalt"})
    private static final class ChunkEntry {
        private final int cx;
        private final int cz;
        private final int dist2;

        public ChunkEntry(int cx, int cz, int dist2) {
            this.cx = cx;
            this.cz = cz;
            this.dist2 = dist2;
        }

        public final int getCx() {
            return this.cx;
        }

        public final int getCz() {
            return this.cz;
        }

        public final int getDist2() {
            return this.dist2;
        }

        public final int component1() {
            return this.cx;
        }

        public final int component2() {
            return this.cz;
        }

        public final int component3() {
            return this.dist2;
        }

        @NotNull
        public final ChunkEntry copy(int cx, int cz, int dist2) {
            return new ChunkEntry(cx, cz, dist2);
        }

        public static /* synthetic */ ChunkEntry copy$default(ChunkEntry chunkEntry, int n, int n2, int n3, int n4, Object object) {
            if ((n4 & 1) != 0) {
                n = chunkEntry.cx;
            }
            if ((n4 & 2) != 0) {
                n2 = chunkEntry.cz;
            }
            if ((n4 & 4) != 0) {
                n3 = chunkEntry.dist2;
            }
            return chunkEntry.copy(n, n2, n3);
        }

        @NotNull
        public String toString() {
            return "ChunkEntry(cx=" + this.cx + ", cz=" + this.cz + ", dist2=" + this.dist2 + ")";
        }

        public int hashCode() {
            int result = Integer.hashCode(this.cx);
            result = result * 31 + Integer.hashCode(this.cz);
            result = result * 31 + Integer.hashCode(this.dist2);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ChunkEntry)) {
                return false;
            }
            ChunkEntry chunkEntry = (ChunkEntry)other;
            if (this.cx != chunkEntry.cx) {
                return false;
            }
            if (this.cz != chunkEntry.cz) {
                return false;
            }
            return this.dist2 == chunkEntry.dist2;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\t\b\u0082\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0004\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\f\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\rJ\u0010\u0010\u000f\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\rJ8\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u001b\u0010\u0014\u001a\u00020\u00132\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u0011\u0010\u0016\u001a\u00020\u0004H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0016\u0010\rJ\u0011\u0010\u0018\u001a\u00020\u0017H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0018\u0010\u0019R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001a\u001a\u0004\b\u001b\u0010\u000bR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001c\u001a\u0004\b\u001d\u0010\rR\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001c\u001a\u0004\b\u001e\u0010\rR\u0017\u0010\u0007\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u001c\u001a\u0004\b\u001f\u0010\r\u00a8\u0006 "}, d2={"Lorg/cobalt/internal/grotto/GrottoScanner$ChunkWork;", "", "Lnet/minecraft/class_2818;", "chunk", "", "chunkX", "chunkZ", "gen", "<init>", "(Lnet/minecraft/class_2818;III)V", "component1", "()Lnet/minecraft/class_2818;", "component2", "()I", "component3", "component4", "copy", "(Lnet/minecraft/class_2818;III)Lorg/cobalt/internal/grotto/GrottoScanner$ChunkWork;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "Lnet/minecraft/class_2818;", "getChunk", "I", "getChunkX", "getChunkZ", "getGen", "cobalt"})
    private static final class ChunkWork {
        @NotNull
        private final class_2818 chunk;
        private final int chunkX;
        private final int chunkZ;
        private final int gen;

        public ChunkWork(@NotNull class_2818 chunk, int chunkX, int chunkZ, int gen) {
            Intrinsics.checkNotNullParameter((Object)chunk, (String)"chunk");
            this.chunk = chunk;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.gen = gen;
        }

        @NotNull
        public final class_2818 getChunk() {
            return this.chunk;
        }

        public final int getChunkX() {
            return this.chunkX;
        }

        public final int getChunkZ() {
            return this.chunkZ;
        }

        public final int getGen() {
            return this.gen;
        }

        @NotNull
        public final class_2818 component1() {
            return this.chunk;
        }

        public final int component2() {
            return this.chunkX;
        }

        public final int component3() {
            return this.chunkZ;
        }

        public final int component4() {
            return this.gen;
        }

        @NotNull
        public final ChunkWork copy(@NotNull class_2818 chunk, int chunkX, int chunkZ, int gen) {
            Intrinsics.checkNotNullParameter((Object)chunk, (String)"chunk");
            return new ChunkWork(chunk, chunkX, chunkZ, gen);
        }

        public static /* synthetic */ ChunkWork copy$default(ChunkWork chunkWork, class_2818 class_28182, int n, int n2, int n3, int n4, Object object) {
            if ((n4 & 1) != 0) {
                class_28182 = chunkWork.chunk;
            }
            if ((n4 & 2) != 0) {
                n = chunkWork.chunkX;
            }
            if ((n4 & 4) != 0) {
                n2 = chunkWork.chunkZ;
            }
            if ((n4 & 8) != 0) {
                n3 = chunkWork.gen;
            }
            return chunkWork.copy(class_28182, n, n2, n3);
        }

        @NotNull
        public String toString() {
            return "ChunkWork(chunk=" + this.chunk + ", chunkX=" + this.chunkX + ", chunkZ=" + this.chunkZ + ", gen=" + this.gen + ")";
        }

        public int hashCode() {
            int result = this.chunk.hashCode();
            result = result * 31 + Integer.hashCode(this.chunkX);
            result = result * 31 + Integer.hashCode(this.chunkZ);
            result = result * 31 + Integer.hashCode(this.gen);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ChunkWork)) {
                return false;
            }
            ChunkWork chunkWork = (ChunkWork)other;
            if (!Intrinsics.areEqual((Object)this.chunk, (Object)chunkWork.chunk)) {
                return false;
            }
            if (this.chunkX != chunkWork.chunkX) {
                return false;
            }
            if (this.chunkZ != chunkWork.chunkZ) {
                return false;
            }
            return this.gen == chunkWork.gen;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0012\n\u0002\u0010\u000e\n\u0002\b\u000b\b\u0082\b\u0018\u00002\u00020\u0001B7\u0012\u000e\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\n\u001a\u00020\u0005\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0018\u0010\r\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\u0005H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0010\u0010\u0011\u001a\u00020\u0005H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u0010J\u0010\u0010\u0012\u001a\u00020\bH\u00c6\u0003\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0010\u0010\u0014\u001a\u00020\u0005H\u00c6\u0003\u00a2\u0006\u0004\b\u0014\u0010\u0010JJ\u0010\u0015\u001a\u00020\u00002\u0010\b\u0002\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\u0005H\u00c6\u0001\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u001b\u0010\u0018\u001a\u00020\b2\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0011\u0010\u001a\u001a\u00020\u0005H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001a\u0010\u0010J\u0011\u0010\u001c\u001a\u00020\u001bH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001c\u0010\u001dR\u001f\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u001e\u001a\u0004\b\u001f\u0010\u000eR\u0017\u0010\u0006\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010 \u001a\u0004\b!\u0010\u0010R\u0017\u0010\u0007\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010 \u001a\u0004\b\"\u0010\u0010R\u0017\u0010\t\u001a\u00020\b8\u0006\u00a2\u0006\f\n\u0004\b\t\u0010#\u001a\u0004\b$\u0010\u0013R\u0017\u0010\n\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\n\u0010 \u001a\u0004\b%\u0010\u0010\u00a8\u0006&"}, d2={"Lorg/cobalt/internal/grotto/GrottoScanner$ScanResult;", "", "", "Lnet/minecraft/class_2338;", "found", "", "chunkX", "chunkZ", "", "hadError", "gen", "<init>", "(Ljava/util/List;IIZI)V", "component1", "()Ljava/util/List;", "component2", "()I", "component3", "component4", "()Z", "component5", "copy", "(Ljava/util/List;IIZI)Lorg/cobalt/internal/grotto/GrottoScanner$ScanResult;", "other", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "Ljava/util/List;", "getFound", "I", "getChunkX", "getChunkZ", "Z", "getHadError", "getGen", "cobalt"})
    private static final class ScanResult {
        @Nullable
        private final List<class_2338> found;
        private final int chunkX;
        private final int chunkZ;
        private final boolean hadError;
        private final int gen;

        public ScanResult(@Nullable List<? extends class_2338> found, int chunkX, int chunkZ, boolean hadError, int gen) {
            this.found = found;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.hadError = hadError;
            this.gen = gen;
        }

        @Nullable
        public final List<class_2338> getFound() {
            return this.found;
        }

        public final int getChunkX() {
            return this.chunkX;
        }

        public final int getChunkZ() {
            return this.chunkZ;
        }

        public final boolean getHadError() {
            return this.hadError;
        }

        public final int getGen() {
            return this.gen;
        }

        @Nullable
        public final List<class_2338> component1() {
            return this.found;
        }

        public final int component2() {
            return this.chunkX;
        }

        public final int component3() {
            return this.chunkZ;
        }

        public final boolean component4() {
            return this.hadError;
        }

        public final int component5() {
            return this.gen;
        }

        @NotNull
        public final ScanResult copy(@Nullable List<? extends class_2338> found, int chunkX, int chunkZ, boolean hadError, int gen) {
            return new ScanResult(found, chunkX, chunkZ, hadError, gen);
        }

        public static /* synthetic */ ScanResult copy$default(ScanResult scanResult, List list, int n, int n2, boolean bl, int n3, int n4, Object object) {
            if ((n4 & 1) != 0) {
                list = scanResult.found;
            }
            if ((n4 & 2) != 0) {
                n = scanResult.chunkX;
            }
            if ((n4 & 4) != 0) {
                n2 = scanResult.chunkZ;
            }
            if ((n4 & 8) != 0) {
                bl = scanResult.hadError;
            }
            if ((n4 & 0x10) != 0) {
                n3 = scanResult.gen;
            }
            return scanResult.copy(list, n, n2, bl, n3);
        }

        @NotNull
        public String toString() {
            return "ScanResult(found=" + this.found + ", chunkX=" + this.chunkX + ", chunkZ=" + this.chunkZ + ", hadError=" + this.hadError + ", gen=" + this.gen + ")";
        }

        public int hashCode() {
            int result = this.found == null ? 0 : ((Object)this.found).hashCode();
            result = result * 31 + Integer.hashCode(this.chunkX);
            result = result * 31 + Integer.hashCode(this.chunkZ);
            result = result * 31 + Boolean.hashCode(this.hadError);
            result = result * 31 + Integer.hashCode(this.gen);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ScanResult)) {
                return false;
            }
            ScanResult scanResult = (ScanResult)other;
            if (!Intrinsics.areEqual(this.found, scanResult.found)) {
                return false;
            }
            if (this.chunkX != scanResult.chunkX) {
                return false;
            }
            if (this.chunkZ != scanResult.chunkZ) {
                return false;
            }
            if (this.hadError != scanResult.hadError) {
                return false;
            }
            return this.gen == scanResult.gen;
        }
    }
}

