/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.collections.ArraysKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.internal.ProgressionUtilKt
 *  kotlin.io.CloseableKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.MatchResult
 *  kotlin.text.Regex
 *  kotlin.text.RegexOption
 *  kotlin.text.StringsKt
 *  net.minecraft.class_124
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1806
 *  net.minecraft.class_1937
 *  net.minecraft.class_20
 *  net.minecraft.class_21
 *  net.minecraft.class_22
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2482
 *  net.minecraft.class_266
 *  net.minecraft.class_268
 *  net.minecraft.class_2680
 *  net.minecraft.class_269
 *  net.minecraft.class_2769
 *  net.minecraft.class_2771
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  net.minecraft.class_7923
 *  net.minecraft.class_8646
 *  net.minecraft.class_9011
 *  net.minecraft.class_9209
 *  net.minecraft.class_9334
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.dungeons.map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.internal.ProgressionUtilKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.RegexOption;
import kotlin.text.StringsKt;
import net.minecraft.class_124;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1806;
import net.minecraft.class_1937;
import net.minecraft.class_20;
import net.minecraft.class_21;
import net.minecraft.class_22;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2482;
import net.minecraft.class_266;
import net.minecraft.class_268;
import net.minecraft.class_2680;
import net.minecraft.class_269;
import net.minecraft.class_2769;
import net.minecraft.class_2771;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_7923;
import net.minecraft.class_8646;
import net.minecraft.class_9011;
import net.minecraft.class_9209;
import net.minecraft.class_9334;
import org.cobalt.internal.dungeons.map.DoorKind;
import org.cobalt.internal.dungeons.map.DungeonDoor;
import org.cobalt.internal.dungeons.map.DungeonFloor;
import org.cobalt.internal.dungeons.map.DungeonRoom;
import org.cobalt.internal.dungeons.map.GridComponent;
import org.cobalt.internal.dungeons.map.LoadedRoute;
import org.cobalt.internal.dungeons.map.MapColorHint;
import org.cobalt.internal.dungeons.map.MapPlayerMarker;
import org.cobalt.internal.dungeons.map.RoomCheckmark;
import org.cobalt.internal.dungeons.map.RoomDefinition;
import org.cobalt.internal.dungeons.map.RoomDirection;
import org.cobalt.internal.dungeons.map.RoomKind;
import org.cobalt.internal.dungeons.map.RoomNeighbor;
import org.cobalt.internal.dungeons.map.RouteStep;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00f8\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010$\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0014\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0012\n\u0002\u0010!\n\u0002\b\u0012\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0006\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0015\u0010\t\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\b0\u0004\u00a2\u0006\u0004\b\t\u0010\nJ\u0013\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\u000b\u00a2\u0006\u0004\b\r\u0010\u000eJ\r\u0010\u0010\u001a\u00020\u000f\u00a2\u0006\u0004\b\u0010\u0010\u0011J\r\u0010\u0013\u001a\u00020\u0012\u00a2\u0006\u0004\b\u0013\u0010\u0003J\u0017\u0010\u0016\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0014\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u001f\u0010\u001a\u001a\u0004\u0018\u00010\u00182\u0006\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u0019\u001a\u00020\u0018\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u000f\u0010\u001c\u001a\u00020\u0012H\u0002\u00a2\u0006\u0004\b\u001c\u0010\u0003J\u000f\u0010\u001d\u001a\u00020\u0012H\u0002\u00a2\u0006\u0004\b\u001d\u0010\u0003J\u0017\u0010 \u001a\u00020\u00122\u0006\u0010\u001f\u001a\u00020\u001eH\u0002\u00a2\u0006\u0004\b \u0010!J\u000f\u0010\"\u001a\u00020\u0012H\u0002\u00a2\u0006\u0004\b\"\u0010\u0003J#\u0010'\u001a\u000e\u0012\u0004\u0012\u00020%\u0012\u0004\u0012\u00020&0$2\u0006\u0010#\u001a\u00020\u000fH\u0002\u00a2\u0006\u0004\b'\u0010(J#\u0010)\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020%0$2\u0006\u0010#\u001a\u00020\u000fH\u0002\u00a2\u0006\u0004\b)\u0010(J#\u0010*\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00150$2\u0006\u0010#\u001a\u00020\u000fH\u0002\u00a2\u0006\u0004\b*\u0010(J9\u00100\u001a\u00028\u0000\"\u0004\b\u0000\u0010+2\u0006\u0010#\u001a\u00020\u000f2\u0006\u0010,\u001a\u00028\u00002\u0012\u0010/\u001a\u000e\u0012\u0004\u0012\u00020.\u0012\u0004\u0012\u00028\u00000-H\u0002\u00a2\u0006\u0004\b0\u00101J\u0019\u00105\u001a\u0004\u0018\u0001042\u0006\u00103\u001a\u000202H\u0002\u00a2\u0006\u0004\b5\u00106J\u001f\u0010:\u001a\b\u0012\u0004\u0012\u0002090\u000b2\b\u00108\u001a\u0004\u0018\u000107H\u0002\u00a2\u0006\u0004\b:\u0010;J\u001f\u0010<\u001a\b\u0012\u0004\u0012\u00020\u00180\u000b2\b\u00108\u001a\u0004\u0018\u000107H\u0002\u00a2\u0006\u0004\b<\u0010;J\u001b\u0010=\u001a\u0004\u0018\u00010\u00182\b\u00108\u001a\u0004\u0018\u000107H\u0002\u00a2\u0006\u0004\b=\u0010>J\u0017\u0010@\u001a\u00020\u000f2\u0006\u0010?\u001a\u00020\u000fH\u0002\u00a2\u0006\u0004\b@\u0010AJ\u0017\u0010D\u001a\u00020\u001e2\u0006\u0010C\u001a\u00020BH\u0002\u00a2\u0006\u0004\bD\u0010EJ\u0017\u0010F\u001a\u00020\u000f2\u0006\u0010?\u001a\u00020\u000fH\u0002\u00a2\u0006\u0004\bF\u0010AJ\u0017\u0010G\u001a\u00020\u00122\u0006\u0010C\u001a\u00020BH\u0002\u00a2\u0006\u0004\bG\u0010HJ)\u0010M\u001a\u00020\u00122\u0006\u0010J\u001a\u00020I2\u0006\u0010\u0014\u001a\u00020\u00052\b\b\u0002\u0010L\u001a\u00020KH\u0002\u00a2\u0006\u0004\bM\u0010NJ\u0017\u0010P\u001a\u00020\u00122\u0006\u0010O\u001a\u00020\bH\u0002\u00a2\u0006\u0004\bP\u0010QJ)\u0010T\u001a\u00020\u00122\u0006\u0010R\u001a\u00020\u00052\u0006\u0010S\u001a\u00020\u00052\b\u0010C\u001a\u0004\u0018\u00010BH\u0002\u00a2\u0006\u0004\bT\u0010UJ\u0017\u0010V\u001a\u00020\u00122\u0006\u0010C\u001a\u00020BH\u0002\u00a2\u0006\u0004\bV\u0010HJ\u0017\u0010W\u001a\u00020\u00122\u0006\u0010C\u001a\u00020BH\u0002\u00a2\u0006\u0004\bW\u0010HJ\u000f\u0010X\u001a\u00020\u0012H\u0002\u00a2\u0006\u0004\bX\u0010\u0003J\u001f\u0010\\\u001a\u00020\u00122\u0006\u0010Z\u001a\u00020Y2\u0006\u0010[\u001a\u00020YH\u0002\u00a2\u0006\u0004\b\\\u0010]J\u0017\u0010^\u001a\u00020\u00122\u0006\u0010C\u001a\u00020BH\u0002\u00a2\u0006\u0004\b^\u0010HJ\u0017\u0010a\u001a\u00020\u00122\u0006\u0010`\u001a\u00020_H\u0002\u00a2\u0006\u0004\ba\u0010bJ\u0019\u0010d\u001a\u0004\u0018\u00010c2\u0006\u0010`\u001a\u00020_H\u0002\u00a2\u0006\u0004\bd\u0010eJ\u0017\u0010h\u001a\u00020K2\u0006\u0010g\u001a\u00020fH\u0002\u00a2\u0006\u0004\bh\u0010iJ\u0017\u0010l\u001a\u00020\u00122\u0006\u0010k\u001a\u00020jH\u0002\u00a2\u0006\u0004\bl\u0010mJ\u0017\u0010n\u001a\u00020\u00122\u0006\u0010g\u001a\u00020fH\u0002\u00a2\u0006\u0004\bn\u0010oJ7\u0010t\u001a\u00020Y2\u0006\u0010?\u001a\u00020Y2\u0006\u0010p\u001a\u00020Y2\u0006\u0010q\u001a\u00020Y2\u0006\u0010r\u001a\u00020Y2\u0006\u0010s\u001a\u00020YH\u0002\u00a2\u0006\u0004\bt\u0010uJ\u001f\u0010v\u001a\u00020\u00122\u0006\u0010C\u001a\u00020B2\u0006\u0010\u0014\u001a\u00020\u0005H\u0002\u00a2\u0006\u0004\bv\u0010wJ\u001f\u0010x\u001a\u00020\u00122\u0006\u0010C\u001a\u00020B2\u0006\u0010\u0014\u001a\u00020\u0005H\u0002\u00a2\u0006\u0004\bx\u0010wJ'\u0010{\u001a\u00020%2\u0006\u0010C\u001a\u00020B2\u0006\u0010y\u001a\u00020%2\u0006\u0010z\u001a\u00020%H\u0002\u00a2\u0006\u0004\b{\u0010|J%\u0010\u0081\u0001\u001a\u0004\u0018\u00010%2\u0006\u0010~\u001a\u00020}2\u0007\u0010\u0080\u0001\u001a\u00020\u007fH\u0002\u00a2\u0006\u0006\b\u0081\u0001\u0010\u0082\u0001J\u0017\u0010\u0083\u0001\u001a\b\u0012\u0004\u0012\u00020I0\u000bH\u0002\u00a2\u0006\u0005\b\u0083\u0001\u0010\u000eJ*\u0010\u0084\u0001\u001a\u00020K2\u0006\u0010C\u001a\u00020B2\u0006\u0010y\u001a\u00020%2\u0006\u0010z\u001a\u00020%H\u0002\u00a2\u0006\u0006\b\u0084\u0001\u0010\u0085\u0001J)\u0010\u0086\u0001\u001a\u00020%2\u0006\u0010C\u001a\u00020B2\u0006\u0010y\u001a\u00020%2\u0006\u0010z\u001a\u00020%H\u0002\u00a2\u0006\u0005\b\u0086\u0001\u0010|J5\u0010\u0088\u0001\u001a\u0004\u0018\u00010\u007f2\u0006\u0010C\u001a\u00020B2\u0006\u0010y\u001a\u00020%2\u0007\u0010\u0087\u0001\u001a\u00020%2\u0006\u0010z\u001a\u00020%H\u0002\u00a2\u0006\u0006\b\u0088\u0001\u0010\u0089\u0001R\u0018\u0010\u008b\u0001\u001a\u00030\u008a\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u008b\u0001\u0010\u008c\u0001R\u0018\u0010\u008e\u0001\u001a\u00030\u008d\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u008e\u0001\u0010\u008f\u0001R\u001a\u0010\u0091\u0001\u001a\u00030\u0090\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0091\u0001\u0010\u0092\u0001R\u0019\u0010\u0093\u0001\u001a\u00020K8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0093\u0001\u0010\u0094\u0001R\u0019\u0010\u0095\u0001\u001a\u00020%8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0095\u0001\u0010\u0096\u0001R%\u0010\u0097\u0001\u001a\u000e\u0012\u0004\u0012\u00020%\u0012\u0004\u0012\u00020&0$8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0097\u0001\u0010\u0098\u0001R%\u0010\u0099\u0001\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020%0$8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0099\u0001\u0010\u0098\u0001R%\u0010\u009a\u0001\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00150$8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u009a\u0001\u0010\u0098\u0001R\u0019\u0010\u009b\u0001\u001a\u00020\u000f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u009b\u0001\u0010\u009c\u0001R\u001b\u0010\u009d\u0001\u001a\u0004\u0018\u00010c8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u009d\u0001\u0010\u009e\u0001R\u0019\u0010\u009f\u0001\u001a\u00020%8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u009f\u0001\u0010\u0096\u0001R\u0019\u0010\u00a0\u0001\u001a\u00020%8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00a0\u0001\u0010\u0096\u0001R\u0019\u0010\u00a1\u0001\u001a\u00020%8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00a1\u0001\u0010\u0096\u0001R\u0019\u0010\u00a2\u0001\u001a\u00020%8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00a2\u0001\u0010\u0096\u0001R\u001e\u0010\u00a4\u0001\u001a\t\u0012\u0004\u0012\u00020I0\u00a3\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a4\u0001\u0010\u00a5\u0001R\u001f\u0010\u00a6\u0001\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u00048\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a6\u0001\u0010\u00a7\u0001R\u001f\u0010\u00a8\u0001\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\b0\u00048\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a8\u0001\u0010\u00a9\u0001R\u001e\u0010\u00aa\u0001\u001a\t\u0012\u0004\u0012\u00020\f0\u00a3\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00aa\u0001\u0010\u00a5\u0001R)\u0010\u00ab\u0001\u001a\u00020\u001e2\u0006\u0010?\u001a\u00020\u001e8\u0006@BX\u0086\u000e\u00a2\u0006\u0010\n\u0006\b\u00ab\u0001\u0010\u00ac\u0001\u001a\u0006\b\u00ad\u0001\u0010\u00ae\u0001R-\u0010\u00af\u0001\u001a\u0004\u0018\u00010\u00052\b\u0010?\u001a\u0004\u0018\u00010\u00058\u0006@BX\u0086\u000e\u00a2\u0006\u0010\n\u0006\b\u00af\u0001\u0010\u00b0\u0001\u001a\u0006\b\u00b1\u0001\u0010\u00b2\u0001R)\u0010\u00b3\u0001\u001a\u00020K2\u0006\u0010?\u001a\u00020K8\u0006@BX\u0086\u000e\u00a2\u0006\u0010\n\u0006\b\u00b3\u0001\u0010\u0094\u0001\u001a\u0006\b\u00b3\u0001\u0010\u00b4\u0001\u00a8\u0006\u00b5\u0001"}, d2={"Lorg/cobalt/internal/dungeons/map/DungeonScanState;", "", "<init>", "()V", "", "Lorg/cobalt/internal/dungeons/map/DungeonRoom;", "getRooms", "()[Lorg/cobalt/internal/dungeons/map/DungeonRoom;", "Lorg/cobalt/internal/dungeons/map/DungeonDoor;", "getDoors", "()[Lorg/cobalt/internal/dungeons/map/DungeonDoor;", "", "Lorg/cobalt/internal/dungeons/map/MapPlayerMarker;", "getMapMarkers", "()Ljava/util/List;", "", "getReferenceStatus", "()Ljava/lang/String;", "", "tick", "room", "Lorg/cobalt/internal/dungeons/map/LoadedRoute;", "resolveRoute", "(Lorg/cobalt/internal/dungeons/map/DungeonRoom;)Lorg/cobalt/internal/dungeons/map/LoadedRoute;", "Lnet/minecraft/class_2338;", "relative", "relativeToActual", "(Lorg/cobalt/internal/dungeons/map/DungeonRoom;Lnet/minecraft/class_2338;)Lnet/minecraft/class_2338;", "reset", "resetTransientState", "Lorg/cobalt/internal/dungeons/map/DungeonFloor;", "newFloor", "setFloor", "(Lorg/cobalt/internal/dungeons/map/DungeonFloor;)V", "loadReferenceDataIfNeeded", "resourcePath", "", "", "Lorg/cobalt/internal/dungeons/map/RoomDefinition;", "loadRoomDefinitions", "(Ljava/lang/String;)Ljava/util/Map;", "loadLegacyBlocks", "loadRoutes", "T", "fallback", "Lkotlin/Function1;", "Ljava/io/Reader;", "loader", "readBundledResource", "(Ljava/lang/String;Ljava/lang/Object;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "Lcom/google/gson/JsonObject;", "obj", "Lorg/cobalt/internal/dungeons/map/RouteStep;", "parseRouteStep", "(Lcom/google/gson/JsonObject;)Lorg/cobalt/internal/dungeons/map/RouteStep;", "Lcom/google/gson/JsonElement;", "element", "", "parseAngleList", "(Lcom/google/gson/JsonElement;)Ljava/util/List;", "parseBlockPosList", "parseBlockPos", "(Lcom/google/gson/JsonElement;)Lnet/minecraft/class_2338;", "value", "normalizeRouteKey", "(Ljava/lang/String;)Ljava/lang/String;", "Lnet/minecraft/class_638;", "level", "detectFloor", "(Lnet/minecraft/class_638;)Lorg/cobalt/internal/dungeons/map/DungeonFloor;", "cleanScoreboardLine", "scanAvailableComponents", "(Lnet/minecraft/class_638;)V", "Lorg/cobalt/internal/dungeons/map/GridComponent;", "component", "", "force", "addRoom", "(Lorg/cobalt/internal/dungeons/map/GridComponent;Lorg/cobalt/internal/dungeons/map/DungeonRoom;Z)V", "door", "addDoor", "(Lorg/cobalt/internal/dungeons/map/DungeonDoor;)V", "primary", "secondary", "mergeRooms", "(Lorg/cobalt/internal/dungeons/map/DungeonRoom;Lorg/cobalt/internal/dungeons/map/DungeonRoom;Lnet/minecraft/class_638;)V", "checkDoorStates", "checkRoomRotations", "softReset", "", "playerX", "playerZ", "updateCurrentRoom", "(DD)V", "updateFromDungeonMap", "Lnet/minecraft/class_746;", "player", "updateCachedMapId", "(Lnet/minecraft/class_746;)V", "Lnet/minecraft/class_9209;", "currentDungeonMapId", "(Lnet/minecraft/class_746;)Lnet/minecraft/class_9209;", "", "colors", "scanMapDimensions", "([B)Z", "Lnet/minecraft/class_22;", "mapState", "updateMarkersFromMap", "(Lnet/minecraft/class_22;)V", "updateRoomsFromMap", "([B)V", "inMin", "inMax", "outMin", "outMax", "rescale", "(DDDDD)D", "scanRoomDefinition", "(Lnet/minecraft/class_638;Lorg/cobalt/internal/dungeons/map/DungeonRoom;)V", "findRoomRotation", "x", "z", "hashCeiling", "(Lnet/minecraft/class_638;II)I", "Lnet/minecraft/class_2680;", "state", "Lnet/minecraft/class_2248;", "block", "getLegacyBlockId", "(Lnet/minecraft/class_2680;Lnet/minecraft/class_2248;)Ljava/lang/Integer;", "findAvailableComponents", "isChunkLoaded", "(Lnet/minecraft/class_638;II)Z", "getHighestY", "y", "getBlock", "(Lnet/minecraft/class_638;III)Lnet/minecraft/class_2248;", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lkotlin/text/Regex;", "floorRegex", "Lkotlin/text/Regex;", "", "lastProcessedTick", "J", "referenceDataLoaded", "Z", "softResetCounter", "I", "roomDefinitionsByCore", "Ljava/util/Map;", "legacyBlockIds", "routesByKey", "referenceStatus", "Ljava/lang/String;", "cachedMapId", "Lnet/minecraft/class_9209;", "roomSizeOnMap", "roomGapOnMap", "mapOffsetX", "mapOffsetZ", "", "availableComponents", "Ljava/util/List;", "rooms", "[Lorg/cobalt/internal/dungeons/map/DungeonRoom;", "doors", "[Lorg/cobalt/internal/dungeons/map/DungeonDoor;", "mapMarkers", "floor", "Lorg/cobalt/internal/dungeons/map/DungeonFloor;", "getFloor", "()Lorg/cobalt/internal/dungeons/map/DungeonFloor;", "currentRoom", "Lorg/cobalt/internal/dungeons/map/DungeonRoom;", "getCurrentRoom", "()Lorg/cobalt/internal/dungeons/map/DungeonRoom;", "isInDungeon", "()Z", "cobalt"})
@SourceDebugExtension(value={"SMAP\nDungeonScanState.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DungeonScanState.kt\norg/cobalt/internal/dungeons/map/DungeonScanState\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,815:1\n491#2,7:816\n437#3:823\n513#3,5:824\n1#4:829\n*S KotlinDebug\n*F\n+ 1 DungeonScanState.kt\norg/cobalt/internal/dungeons/map/DungeonScanState\n*L\n125#1:816,7\n304#1:823\n304#1:824,5\n*E\n"})
public final class DungeonScanState {
    @NotNull
    public static final DungeonScanState INSTANCE = new DungeonScanState();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final Regex floorRegex;
    private static long lastProcessedTick;
    private static boolean referenceDataLoaded;
    private static int softResetCounter;
    @NotNull
    private static Map<Integer, RoomDefinition> roomDefinitionsByCore;
    @NotNull
    private static Map<String, Integer> legacyBlockIds;
    @NotNull
    private static Map<String, LoadedRoute> routesByKey;
    @NotNull
    private static String referenceStatus;
    @Nullable
    private static class_9209 cachedMapId;
    private static int roomSizeOnMap;
    private static int roomGapOnMap;
    private static int mapOffsetX;
    private static int mapOffsetZ;
    @NotNull
    private static final List<GridComponent> availableComponents;
    @NotNull
    private static final DungeonRoom[] rooms;
    @NotNull
    private static final DungeonDoor[] doors;
    @NotNull
    private static final List<MapPlayerMarker> mapMarkers;
    @NotNull
    private static DungeonFloor floor;
    @Nullable
    private static DungeonRoom currentRoom;
    private static boolean isInDungeon;

    private DungeonScanState() {
    }

    @NotNull
    public final DungeonFloor getFloor() {
        return floor;
    }

    @Nullable
    public final DungeonRoom getCurrentRoom() {
        return currentRoom;
    }

    public final boolean isInDungeon() {
        return isInDungeon;
    }

    @NotNull
    public final DungeonRoom[] getRooms() {
        return rooms;
    }

    @NotNull
    public final DungeonDoor[] getDoors() {
        return doors;
    }

    @NotNull
    public final List<MapPlayerMarker> getMapMarkers() {
        return mapMarkers;
    }

    @NotNull
    public final String getReferenceStatus() {
        return referenceStatus;
    }

    public final void tick() {
        class_638 level2 = DungeonScanState.mc.field_1687;
        class_746 player = DungeonScanState.mc.field_1724;
        if (level2 == null || player == null) {
            this.reset();
            return;
        }
        long gameTime = level2.method_75260();
        if (gameTime == lastProcessedTick) {
            return;
        }
        lastProcessedTick = gameTime;
        this.loadReferenceDataIfNeeded();
        DungeonFloor detectedFloor = this.detectFloor(level2);
        boolean bl = isInDungeon = detectedFloor != DungeonFloor.NONE;
        if (!isInDungeon) {
            this.resetTransientState();
            return;
        }
        if (detectedFloor != floor) {
            this.setFloor(detectedFloor);
        }
        this.scanAvailableComponents(level2);
        this.checkRoomRotations(level2);
        this.checkDoorStates(level2);
        int n = softResetCounter;
        softResetCounter = n + 1;
        if (softResetCounter >= 20) {
            softResetCounter = 0;
            this.softReset();
        }
        this.updateCurrentRoom(player.method_23317(), player.method_23321());
        this.updateFromDungeonMap(level2);
    }

    @Nullable
    public final LoadedRoute resolveRoute(@NotNull DungeonRoom room) {
        Intrinsics.checkNotNullParameter((Object)room, (String)"room");
        String string = room.getName();
        if (string == null) {
            return null;
        }
        String roomName = string;
        Object object = new LinkedHashSet<Object>();
        LinkedHashSet<Object> $this$resolveRoute_u24lambda_u240 = object;
        boolean bl = false;
        if (room.getTotalSecrets() > 0) {
            $this$resolveRoute_u24lambda_u240.add(roomName + "-" + room.getTotalSecrets());
        }
        $this$resolveRoute_u24lambda_u240.add(roomName);
        LinkedHashSet<Object> candidates = object;
        Iterator iterator = candidates.iterator();
        Intrinsics.checkNotNullExpressionValue(iterator, (String)"iterator(...)");
        object = iterator;
        while (object.hasNext()) {
            LoadedRoute route;
            Object e = object.next();
            Intrinsics.checkNotNullExpressionValue(e, (String)"next(...)");
            String candidate = (String)e;
            LoadedRoute loadedRoute = route = routesByKey.get(this.normalizeRouteKey(candidate));
            if (loadedRoute == null) continue;
            return loadedRoute;
        }
        String normalizedName = this.normalizeRouteKey(roomName);
        Map<String, LoadedRoute> $this$filterKeys$iv = routesByKey;
        boolean $i$f$filterKeys = false;
        LinkedHashMap<String, LoadedRoute> result$iv = new LinkedHashMap<String, LoadedRoute>();
        for (Map.Entry<String, LoadedRoute> entry$iv : $this$filterKeys$iv.entrySet()) {
            String key = entry$iv.getKey();
            boolean bl2 = false;
            if (!StringsKt.startsWith$default((String)key, (String)normalizedName, (boolean)false, (int)2, null)) continue;
            result$iv.put(entry$iv.getKey(), entry$iv.getValue());
        }
        Map partialMatches = result$iv;
        if (partialMatches.size() == 1) {
            return (LoadedRoute)CollectionsKt.first((Iterable)partialMatches.values());
        }
        return null;
    }

    @Nullable
    public final class_2338 relativeToActual(@NotNull DungeonRoom room, @NotNull class_2338 relative) {
        Intrinsics.checkNotNullParameter((Object)room, (String)"room");
        Intrinsics.checkNotNullParameter((Object)relative, (String)"relative");
        RoomDirection roomDirection = room.getDirection();
        if (roomDirection == null) {
            return null;
        }
        RoomDirection direction = roomDirection;
        class_2338 class_23382 = room.getCorner();
        if (class_23382 == null) {
            return null;
        }
        class_2338 corner = class_23382;
        return switch (WhenMappings.$EnumSwitchMapping$0[direction.ordinal()]) {
            case 1 -> new class_2338(relative.method_10263() + corner.method_10263(), relative.method_10264(), relative.method_10260() + corner.method_10260());
            case 2 -> new class_2338(-relative.method_10260() + corner.method_10263(), relative.method_10264(), relative.method_10263() + corner.method_10260());
            case 3 -> new class_2338(relative.method_10260() + corner.method_10263(), relative.method_10264(), -relative.method_10263() + corner.method_10260());
            case 4 -> new class_2338(-relative.method_10263() + corner.method_10263(), relative.method_10264(), -relative.method_10260() + corner.method_10260());
            default -> throw new NoWhenBranchMatchedException();
        };
    }

    private final void reset() {
        floor = DungeonFloor.NONE;
        isInDungeon = false;
        currentRoom = null;
        lastProcessedTick = Long.MIN_VALUE;
        this.resetTransientState();
    }

    private final void resetTransientState() {
        int index;
        currentRoom = null;
        roomSizeOnMap = -1;
        roomGapOnMap = -1;
        mapOffsetX = -1;
        mapOffsetZ = -1;
        softResetCounter = 0;
        mapMarkers.clear();
        availableComponents.clear();
        availableComponents.addAll((Collection<GridComponent>)this.findAvailableComponents());
        int n = rooms.length;
        for (index = 0; index < n; ++index) {
            DungeonScanState.rooms[index] = null;
        }
        n = doors.length;
        for (index = 0; index < n; ++index) {
            DungeonScanState.doors[index] = null;
        }
    }

    private final void setFloor(DungeonFloor newFloor) {
        floor = newFloor;
        this.resetTransientState();
    }

    private final void loadReferenceDataIfNeeded() {
        StringBuilder stringBuilder;
        if (referenceDataLoaded) {
            return;
        }
        roomDefinitionsByCore = this.loadRoomDefinitions("/assets/dungeons/data/rooms.json");
        legacyBlockIds = this.loadLegacyBlocks("/assets/dungeons/data/legacy_blocks.json");
        routesByKey = this.loadRoutes("/assets/dungeons/data/routes.json");
        referenceDataLoaded = true;
        StringBuilder $this$loadReferenceDataIfNeeded_u24lambda_u240 = stringBuilder = new StringBuilder();
        boolean bl = false;
        $this$loadReferenceDataIfNeeded_u24lambda_u240.append("Bundled data");
        $this$loadReferenceDataIfNeeded_u24lambda_u240.append(" | rooms ");
        $this$loadReferenceDataIfNeeded_u24lambda_u240.append(roomDefinitionsByCore.isEmpty() ? "missing" : Integer.valueOf(roomDefinitionsByCore.size()));
        $this$loadReferenceDataIfNeeded_u24lambda_u240.append(" | legacy ");
        $this$loadReferenceDataIfNeeded_u24lambda_u240.append(legacyBlockIds.isEmpty() ? "missing" : Integer.valueOf(legacyBlockIds.size()));
        $this$loadReferenceDataIfNeeded_u24lambda_u240.append(" | routes ");
        $this$loadReferenceDataIfNeeded_u24lambda_u240.append(routesByKey.isEmpty() ? "missing" : Integer.valueOf(routesByKey.size()));
        referenceStatus = stringBuilder.toString();
        DungeonFloor oldFloor = floor;
        boolean oldInDungeon = isInDungeon;
        this.resetTransientState();
        floor = oldFloor;
        isInDungeon = oldInDungeon;
    }

    private final Map<Integer, RoomDefinition> loadRoomDefinitions(String resourcePath) {
        return this.readBundledResource(resourcePath, MapsKt.emptyMap(), DungeonScanState::loadRoomDefinitions$lambda$0);
    }

    private final Map<String, Integer> loadLegacyBlocks(String resourcePath) {
        return this.readBundledResource(resourcePath, MapsKt.emptyMap(), DungeonScanState::loadLegacyBlocks$lambda$0);
    }

    private final Map<String, LoadedRoute> loadRoutes(String resourcePath) {
        return this.readBundledResource(resourcePath, MapsKt.emptyMap(), DungeonScanState::loadRoutes$lambda$0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final <T> T readBundledResource(String resourcePath, T fallback, Function1<? super Reader, ? extends T> loader) {
        Object object;
        Object object2 = this;
        try {
            Object object3;
            DungeonScanState $this$readBundledResource_u24lambda_u240 = object2;
            boolean bl = false;
            InputStream inputStream = DungeonScanState.class.getResourceAsStream(resourcePath);
            if (inputStream != null) {
                Closeable closeable = inputStream;
                Throwable throwable = null;
                try {
                    Object object4;
                    InputStream stream = (InputStream)closeable;
                    boolean bl2 = false;
                    Closeable closeable2 = new InputStreamReader(stream, StandardCharsets.UTF_8);
                    Throwable throwable2 = null;
                    try {
                        object4 = loader.invoke((Object)closeable2);
                    }
                    catch (Throwable throwable3) {
                        throwable2 = throwable3;
                        throw throwable3;
                    }
                    finally {
                        CloseableKt.closeFinally((Closeable)closeable2, (Throwable)throwable2);
                    }
                    Object object5 = object4;
                    object3 = object5;
                }
                catch (Throwable throwable4) {
                    throwable = throwable4;
                    throw throwable4;
                }
                finally {
                    CloseableKt.closeFinally((Closeable)closeable, (Throwable)throwable);
                }
            }
            object3 = null;
            object = Result.constructor-impl(object3);
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        object2 = object;
        Object object6 = Result.isFailure-impl((Object)object2) ? null : object2;
        if (object6 == null) {
            object6 = fallback;
        }
        return (T)object6;
    }

    private final RouteStep parseRouteStep(JsonObject obj) {
        JsonObject jsonObject = obj.getAsJsonObject("secret");
        if (jsonObject == null) {
            return null;
        }
        JsonObject secret = jsonObject;
        class_2338 class_23382 = this.parseBlockPos(secret.get("location"));
        if (class_23382 == null) {
            return null;
        }
        class_2338 secretLocation = class_23382;
        JsonElement jsonElement = secret.get("type");
        String string = jsonElement != null ? jsonElement.getAsString() : null;
        if (string == null) {
            string = "";
        }
        String secretType = string;
        return new RouteStep(this.parseBlockPosList(obj.get("locations")), this.parseBlockPosList(obj.get("etherwarps")), this.parseBlockPosList(obj.get("mines")), this.parseBlockPosList(obj.get("interacts")), this.parseBlockPosList(obj.get("tnts")), secretLocation, secretType, this.parseBlockPosList(obj.get("enderpearls")), this.parseAngleList(obj.get("enderpearlangles")));
    }

    private final List<float[]> parseAngleList(JsonElement element) {
        if (element == null || !element.isJsonArray()) {
            return CollectionsKt.emptyList();
        }
        ArrayList<float[]> list = new ArrayList<float[]>();
        Iterator iterator = element.getAsJsonArray().iterator();
        Intrinsics.checkNotNullExpressionValue((Object)iterator, (String)"iterator(...)");
        Iterator iterator2 = iterator;
        while (iterator2.hasNext()) {
            JsonArray arr;
            JsonElement entry = (JsonElement)iterator2.next();
            if (!entry.isJsonArray() || (arr = entry.getAsJsonArray()).size() < 2) continue;
            float[] fArray = new float[]{arr.get(0).getAsFloat(), arr.get(1).getAsFloat()};
            list.add(fArray);
        }
        return list;
    }

    private final List<class_2338> parseBlockPosList(JsonElement element) {
        if (element == null || !element.isJsonArray()) {
            return CollectionsKt.emptyList();
        }
        ArrayList<class_2338> list = new ArrayList<class_2338>();
        Iterator iterator = element.getAsJsonArray().iterator();
        Intrinsics.checkNotNullExpressionValue((Object)iterator, (String)"iterator(...)");
        Iterator iterator2 = iterator;
        while (iterator2.hasNext()) {
            class_2338 pos;
            JsonElement entry = (JsonElement)iterator2.next();
            if (this.parseBlockPos(entry) == null) continue;
            list.add(pos);
        }
        return list;
    }

    private final class_2338 parseBlockPos(JsonElement element) {
        if (element == null || !element.isJsonArray()) {
            return null;
        }
        JsonArray arr = element.getAsJsonArray();
        if (arr.size() < 3) {
            return null;
        }
        return new class_2338(arr.get(0).getAsInt(), arr.get(1).getAsInt(), arr.get(2).getAsInt());
    }

    /*
     * WARNING - void declaration
     */
    private final String normalizeRouteKey(String value) {
        void $this$filterTo$iv$iv;
        String string = value;
        Locale locale = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
        String string2 = string.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
        String $this$filter$iv = string2;
        boolean $i$f$filter = false;
        CharSequence charSequence = $this$filter$iv;
        Appendable destination$iv$iv = new StringBuilder();
        boolean $i$f$filterTo = false;
        int n = $this$filterTo$iv$iv.length();
        for (int index$iv$iv = 0; index$iv$iv < n; ++index$iv$iv) {
            char element$iv$iv;
            char it = element$iv$iv = $this$filterTo$iv$iv.charAt(index$iv$iv);
            boolean bl = false;
            if (!Character.isLetterOrDigit(it)) continue;
            destination$iv$iv.append(element$iv$iv);
        }
        return ((StringBuilder)destination$iv$iv).toString();
    }

    private final DungeonFloor detectFloor(class_638 level2) {
        List list;
        class_269 class_2692 = level2.method_8428();
        Intrinsics.checkNotNullExpressionValue((Object)class_2692, (String)"getScoreboard(...)");
        class_269 scoreboard = class_2692;
        class_266 class_2662 = scoreboard.method_1189(class_8646.field_45157);
        if (class_2662 == null) {
            return DungeonFloor.NONE;
        }
        class_266 objective = class_2662;
        List $this$detectFloor_u24lambda_u240 = list = CollectionsKt.createListBuilder();
        boolean bl = false;
        String string = objective.method_1114().getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        $this$detectFloor_u24lambda_u240.add(INSTANCE.cleanScoreboardLine(string));
        try {
            for (Object e : scoreboard.method_1184(objective)) {
                boolean bl2;
                String owner;
                Intrinsics.checkNotNullExpressionValue(e, (String)"next(...)");
                class_9011 score = (class_9011)e;
                Intrinsics.checkNotNullExpressionValue((Object)score.comp_2127(), (String)"owner(...)");
                class_268 team = scoreboard.method_1164(owner);
                if (team != null) {
                    $this$detectFloor_u24lambda_u240.add(INSTANCE.cleanScoreboardLine(team.method_1144().getString() + team.method_1136().getString()));
                    bl2 = $this$detectFloor_u24lambda_u240.add(INSTANCE.cleanScoreboardLine(team.method_1144().getString() + owner + team.method_1136().getString()));
                    continue;
                }
                bl2 = $this$detectFloor_u24lambda_u240.add(INSTANCE.cleanScoreboardLine(owner));
            }
        }
        catch (Exception exception) {
        }
        List lines = CollectionsKt.build((List)list);
        String joined = CollectionsKt.joinToString$default((Iterable)lines, (CharSequence)" ", null, null, (int)0, null, null, (int)62, null);
        if (StringsKt.contains((CharSequence)joined, (CharSequence)"Hub", (boolean)true)) {
            return DungeonFloor.NONE;
        }
        for (String line : lines) {
            MatchResult matchResult = Regex.find$default((Regex)floorRegex, (CharSequence)line, (int)0, (int)2, null);
            if (matchResult == null) continue;
            MatchResult match = matchResult;
            return DungeonFloor.Companion.fromName((String)match.getGroupValues().get(1));
        }
        return DungeonFloor.NONE;
    }

    private final String cleanScoreboardLine(String value) {
        String string;
        String string2;
        String string3;
        CharSequence charSequence;
        Object object;
        String string4;
        String string5 = class_124.method_539((String)value);
        if ((string5 != null && (string4 = (object = new Regex("[^\\x20-\\x7E]")).replace(charSequence = (CharSequence)string5, string3 = " ")) != null && (charSequence = (string3 = new Regex("\\s+")).replace((CharSequence)(object = (CharSequence)string4), string2 = " ")) != null ? ((Object)StringsKt.trim((CharSequence)charSequence)).toString() : (string = null)) == null) {
            string = "";
        }
        return string;
    }

    private final void scanAvailableComponents(class_638 level2) {
        if (availableComponents.isEmpty()) {
            return;
        }
        ArrayList<GridComponent> toRemove = new ArrayList<GridComponent>();
        for (GridComponent component : availableComponents) {
            class_2338 worldCenter = component.toWorldCenter();
            if (!this.isChunkLoaded(level2, worldCenter.method_10263(), worldCenter.method_10260())) continue;
            toRemove.add(component);
            int roofHeight = this.getHighestY(level2, worldCenter.method_10263(), worldCenter.method_10260());
            if (component.isDoor()) {
                if (roofHeight == 0 || roofHeight >= 85) continue;
                DungeonDoor door = new DungeonDoor(component);
                if (component.getZ() % 2 == 1) {
                    door.setRotation(0);
                }
                this.addDoor(door);
                continue;
            }
            if (roofHeight <= 0) continue;
            GridComponent[] gridComponentArray = new GridComponent[]{component};
            DungeonRoom room = new DungeonRoom(CollectionsKt.mutableListOf((Object[])gridComponentArray), roofHeight);
            this.scanRoomDefinition(level2, room);
            if (room.getType() == RoomKind.ENTRANCE) {
                room.setExplored(true);
                room.setCheckmark(RoomCheckmark.NONE);
            }
            DungeonScanState.addRoom$default(this, component, room, false, 4, null);
            for (RoomNeighbor neighbor : component.roomNeighbors()) {
                boolean aboveEmpty;
                class_2338 doorCenter = neighbor.getDoor().toWorldCenter();
                class_2248 blockAtHeight = this.getBlock(level2, doorCenter.method_10263(), roofHeight, doorCenter.method_10260());
                class_2248 blockAbove = this.getBlock(level2, doorCenter.method_10263(), roofHeight + 1, doorCenter.method_10260());
                boolean heightEmpty = Intrinsics.areEqual((Object)blockAtHeight, (Object)class_2246.field_10124);
                boolean bl = aboveEmpty = blockAbove == null || Intrinsics.areEqual((Object)blockAbove, (Object)class_2246.field_10124);
                if (room.getType() == RoomKind.ENTRANCE && !heightEmpty) {
                    class_2248 blockAt76 = this.getBlock(level2, doorCenter.method_10263(), 76, doorCenter.method_10260());
                    if (blockAt76 == null || Intrinsics.areEqual((Object)blockAt76, (Object)class_2246.field_10124)) continue;
                    DungeonDoor door = new DungeonDoor(neighbor.getDoor());
                    door.setType(DoorKind.ENTRANCE);
                    this.addDoor(door);
                    continue;
                }
                if (heightEmpty || !aboveEmpty) continue;
                int neighborIndex = neighbor.getRoom().roomIndex();
                boolean bl2 = 0 <= neighborIndex ? neighborIndex < rooms.length : false;
                if (!bl2) continue;
                DungeonRoom neighborRoom = rooms[neighborIndex];
                if (neighborRoom == null) {
                    room.addComponent(neighbor.getRoom(), false);
                    DungeonScanState.addRoom$default(this, neighbor.getRoom(), room, false, 4, null);
                    continue;
                }
                if (neighborRoom.getType() == RoomKind.ENTRANCE || neighborRoom == room) continue;
                this.mergeRooms(neighborRoom, room, level2);
                room = neighborRoom;
            }
        }
        availableComponents.removeAll(CollectionsKt.toSet((Iterable)toRemove));
    }

    private final void addRoom(GridComponent component, DungeonRoom room, boolean force) {
        int index = component.roomIndex();
        if (!(0 <= index ? index < rooms.length : false)) {
            return;
        }
        if (!force && rooms[index] != null) {
            DungeonRoom dungeonRoom = rooms[index];
            if (dungeonRoom == null) {
                return;
            }
            DungeonRoom existing = dungeonRoom;
            if (room.getName() == null) {
                this.mergeRooms(existing, room, DungeonScanState.mc.field_1687);
            } else {
                this.mergeRooms(room, existing, DungeonScanState.mc.field_1687);
            }
            return;
        }
        DungeonScanState.rooms[index] = room;
        for (GridComponent doorComponent : component.neighboringDoors()) {
            DungeonDoor door;
            int doorIndex = doorComponent.doorIndex();
            boolean bl = 0 <= doorIndex ? doorIndex < doors.length : false;
            if (!bl || doors[doorIndex] == null) continue;
            door.getRooms().add(room);
            room.getDoors().add(door);
        }
    }

    static /* synthetic */ void addRoom$default(DungeonScanState dungeonScanState, GridComponent gridComponent, DungeonRoom dungeonRoom, boolean bl, int n, Object object) {
        if ((n & 4) != 0) {
            bl = false;
        }
        dungeonScanState.addRoom(gridComponent, dungeonRoom, bl);
    }

    private final void addDoor(DungeonDoor door) {
        int index = door.getComponent().doorIndex();
        if (!(0 <= index ? index < doors.length : false)) {
            return;
        }
        DungeonScanState.doors[index] = door;
        for (GridComponent roomComponent : door.getComponent().neighboringRooms()) {
            DungeonRoom room;
            int roomIndex = roomComponent.roomIndex();
            boolean bl = 0 <= roomIndex ? roomIndex < rooms.length : false;
            if (!bl || rooms[roomIndex] == null) continue;
            room.getDoors().add(door);
            door.getRooms().add(room);
        }
    }

    private final void mergeRooms(DungeonRoom primary, DungeonRoom secondary, class_638 level2) {
        if (primary == secondary) {
            return;
        }
        for (GridComponent component : secondary.getComponents()) {
            primary.addComponent(component, false);
            this.addRoom(component, primary, true);
        }
        primary.updateShape();
        if (level2 != null) {
            this.scanRoomDefinition(level2, primary);
            primary.setCorner(null);
            primary.setRotation(-1);
        }
        if (secondary.getExplored()) {
            primary.setExplored(true);
        }
        if (secondary.getCheckmark().ordinal() > primary.getCheckmark().ordinal()) {
            primary.setCheckmark(secondary.getCheckmark());
        }
        Iterator iterator = secondary.getDoors().iterator();
        Intrinsics.checkNotNullExpressionValue(iterator, (String)"iterator(...)");
        Iterator<GridComponent> iterator2 = iterator;
        while (iterator2.hasNext()) {
            GridComponent gridComponent = iterator2.next();
            Intrinsics.checkNotNullExpressionValue((Object)gridComponent, (String)"next(...)");
            DungeonDoor door = (DungeonDoor)((Object)gridComponent);
            door.getRooms().remove(secondary);
            door.getRooms().add(primary);
            primary.getDoors().add(door);
        }
    }

    private final void checkDoorStates(class_638 level2) {
        HashSet<DungeonDoor> seen = new HashSet<DungeonDoor>();
        for (DungeonDoor door : doors) {
            class_2248 blockAt69;
            class_2338 center;
            if (door == null || !seen.add(door) || door.getOpened() || !this.isChunkLoaded(level2, (center = door.getComponent().toWorldCenter()).method_10263(), center.method_10260())) continue;
            Intrinsics.checkNotNullExpressionValue((Object)level2.method_8320(new class_2338(center.method_10263(), 69, center.method_10260())).method_26204(), (String)"getBlock(...)");
            door.updateFromBlock(blockAt69);
        }
    }

    private final void checkRoomRotations(class_638 level2) {
        HashSet<DungeonRoom> seen = new HashSet<DungeonRoom>();
        for (DungeonRoom room : rooms) {
            if (room == null || !seen.add(room) || room.getRotation() != -1) continue;
            this.findRoomRotation(level2, room);
        }
    }

    private final void softReset() {
        availableComponents.clear();
        int z = 0;
        int n = ProgressionUtilKt.getProgressionLastElement((int)0, (int)10, (int)2);
        if (z <= n) {
            while (true) {
                int n2;
                int x;
                if ((x = 0) <= (n2 = ProgressionUtilKt.getProgressionLastElement((int)0, (int)10, (int)2))) {
                    while (true) {
                        GridComponent component;
                        int index;
                        boolean bl = 0 <= (index = (component = new GridComponent(x, z)).roomIndex()) ? index < rooms.length : false;
                        if (bl && rooms[index] == null) {
                            availableComponents.add(component);
                        }
                        if (x == n2) break;
                        x += 2;
                    }
                }
                if (z == n) break;
                z += 2;
            }
        }
        for (z = 0; z < 11; ++z) {
            for (int x = 0; x < 11; ++x) {
                GridComponent component = new GridComponent(x, z);
                if (!component.isDoor()) continue;
                int index = component.doorIndex();
                boolean bl = 0 <= index ? index < doors.length : false;
                if (!bl || doors[index] != null) continue;
                availableComponents.add(component);
            }
        }
    }

    private final void updateCurrentRoom(double playerX, double playerZ) {
        block1: {
            GridComponent component = GridComponent.Companion.fromWorld(playerX, playerZ);
            int roomIndex = component.roomIndex();
            if (!(0 <= roomIndex ? roomIndex < rooms.length : false)) {
                currentRoom = null;
                return;
            }
            DungeonRoom dungeonRoom = currentRoom = rooms[roomIndex];
            if (dungeonRoom == null) break block1;
            dungeonRoom.setExplored(true);
        }
    }

    private final void updateFromDungeonMap(class_638 level2) {
        class_746 class_7462 = DungeonScanState.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        this.updateCachedMapId(player);
        class_9209 class_92092 = this.currentDungeonMapId(player);
        if (class_92092 == null) {
            return;
        }
        class_9209 mapId = class_92092;
        class_22 class_222 = class_1806.method_7997((class_9209)mapId, (class_1937)((class_1937)level2));
        if (class_222 == null) {
            return;
        }
        class_22 mapState = class_222;
        Intrinsics.checkNotNullExpressionValue((Object)mapState.field_122, (String)"colors");
        byte[] colors = mapState.field_122;
        if (roomSizeOnMap == -1 && !this.scanMapDimensions(colors)) {
            return;
        }
        this.updateMarkersFromMap(mapState);
        this.updateRoomsFromMap(colors);
    }

    private final void updateCachedMapId(class_746 player) {
        for (int slot = 0; slot < 9; ++slot) {
            class_9209 mapId;
            class_1799 stack;
            Intrinsics.checkNotNullExpressionValue((Object)player.method_31548().method_5438(slot), (String)"getItem(...)");
            if (!stack.method_31574(class_1802.field_8204)) continue;
            class_9209 class_92092 = (class_9209)stack.method_58694(class_9334.field_49646);
            if (class_92092 == null) continue;
            cachedMapId = mapId = class_92092;
            return;
        }
    }

    private final class_9209 currentDungeonMapId(class_746 player) {
        class_9209 class_92092;
        class_1799 class_17992 = player.method_31548().method_5438(8);
        Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getItem(...)");
        class_1799 slotEight = class_17992;
        if (slotEight.method_31574(class_1802.field_8204) && (class_92092 = (class_9209)slotEight.method_58694(class_9334.field_49646)) != null) {
            class_9209 it = class_92092;
            boolean bl = false;
            cachedMapId = it;
            return it;
        }
        return cachedMapId;
    }

    private final boolean scanMapDimensions(byte[] colors) {
        int left;
        if (floor == DungeonFloor.NONE) {
            return false;
        }
        int entranceIndex = 0;
        int searchIndex = 0;
        while (entranceIndex < colors.length && colors[entranceIndex] != MapColorHint.ROOM_ENTRANCE.getColor()) {
            entranceIndex = ((++searchIndex & 7) << 4) + (searchIndex >> 3 << 11);
        }
        if (entranceIndex >= colors.length) {
            return false;
        }
        int right = entranceIndex;
        for (left = entranceIndex; left > 0 && colors[left - 1] == MapColorHint.ROOM_ENTRANCE.getColor(); --left) {
        }
        while (right < ArraysKt.getLastIndex((byte[])colors) && colors[right + 1] == MapColorHint.ROOM_ENTRANCE.getColor()) {
            ++right;
        }
        int bottom = entranceIndex;
        for (int top = entranceIndex; top >= 128 && colors[top - 128] == MapColorHint.ROOM_ENTRANCE.getColor(); top -= 128) {
        }
        while (bottom < colors.length - 128 && colors[bottom + 128] == MapColorHint.ROOM_ENTRANCE.getColor()) {
            bottom += 128;
        }
        bottom >>= 7;
        roomSizeOnMap = (right &= 0x7F) - (left &= 0x7F) + 1;
        roomGapOnMap = roomSizeOnMap + 4;
        mapOffsetX = left % roomGapOnMap;
        mapOffsetZ = (top >>= 7) % roomGapOnMap;
        int mapWidth = roomGapOnMap * (floor.getRoomsWide() - 1) + roomSizeOnMap;
        int mapHeight = roomGapOnMap * (floor.getRoomsTall() - 1) + roomSizeOnMap;
        if (128 - mapWidth >= roomGapOnMap * 2) {
            mapOffsetX += roomGapOnMap;
        }
        if (128 - mapHeight >= roomGapOnMap * 2) {
            mapOffsetZ += roomGapOnMap;
        }
        return true;
    }

    private final void updateMarkersFromMap(class_22 mapState) {
        if (floor == DungeonFloor.NONE || roomGapOnMap <= 0) {
            return;
        }
        mapMarkers.clear();
        Iterable iterable = mapState.method_32373();
        Intrinsics.checkNotNullExpressionValue((Object)iterable, (String)"getDecorations(...)");
        Iterable decorations = iterable;
        for (Object t : decorations) {
            Intrinsics.checkNotNullExpressionValue(t, (String)"next(...)");
            class_20 decoration = (class_20)t;
            if (Intrinsics.areEqual((Object)decoration.comp_1842().comp_349(), (Object)class_21.field_95.comp_349())) continue;
            double componentX = this.rescale(((double)decoration.comp_1843() + 128.0) * 0.5, mapOffsetX, mapOffsetX + roomGapOnMap * floor.getRoomsWide(), 0.0, (double)floor.getRoomsWide() * 2.0);
            double componentZ = this.rescale(((double)decoration.comp_1844() + 128.0) * 0.5, mapOffsetZ, mapOffsetZ + roomGapOnMap * floor.getRoomsTall(), 0.0, (double)floor.getRoomsTall() * 2.0);
            double rotation = -((double)decoration.comp_1845() / 16.0 * 360.0 + 90.0) / 180.0 * Math.PI;
            mapMarkers.add(new MapPlayerMarker(componentX, componentZ, rotation));
        }
    }

    private final void updateRoomsFromMap(byte[] colors) {
        if (colors.length < 16384) {
            return;
        }
        HashSet<DungeonRoom> seen = new HashSet<DungeonRoom>();
        for (int roomIndex = 0; roomIndex < 36; ++roomIndex) {
            byte by;
            DungeonRoom room = rooms[roomIndex];
            if (room != null && !seen.add(room)) continue;
            int roomX = roomIndex % 6;
            int roomZ = roomIndex / 6;
            int mapRoomX = mapOffsetX + roomX * roomGapOnMap;
            int mapRoomZ = mapOffsetZ + roomZ * roomGapOnMap;
            int mapCenterX = mapRoomX + roomSizeOnMap / 2 - 1;
            int mapCenterZ = mapRoomZ + roomSizeOnMap / 2 + 1;
            int cornerIndex = mapRoomX + mapRoomZ * 128;
            int centerIndex = mapCenterX + mapCenterZ * 128;
            boolean bl = 0 <= cornerIndex ? cornerIndex < colors.length : false;
            if (!bl) continue;
            boolean bl2 = 0 <= centerIndex ? centerIndex < colors.length : false;
            if (!bl2) continue;
            byte roomColor = colors[cornerIndex];
            byte centerColor = colors[centerIndex];
            if (roomColor == MapColorHint.EMPTY.getColor()) continue;
            if (room == null) {
                GridComponent component = new GridComponent(roomX * 2, roomZ * 2);
                Object[] objectArray = new GridComponent[]{component};
                room = new DungeonRoom(CollectionsKt.mutableListOf((Object[])objectArray), 0);
                DungeonScanState.addRoom$default(this, component, room, false, 4, null);
            } else {
                seen.add(room);
            }
            if (room.getType() == RoomKind.UNKNOWN) {
                room.setType(RoomKind.Companion.fromMapColor(roomColor));
            }
            if (!room.getExplored() && roomColor != MapColorHint.ROOM_UNOPENED.getColor()) {
                room.setExplored(true);
            }
            room.setCheckmark(roomColor == centerColor ? RoomCheckmark.NONE : ((by = centerColor) == MapColorHint.CHECK_WHITE.getColor() ? RoomCheckmark.WHITE : (by == MapColorHint.CHECK_GREEN.getColor() ? RoomCheckmark.GREEN : (by == MapColorHint.CHECK_FAIL.getColor() ? RoomCheckmark.FAILED : (by == MapColorHint.CHECK_UNKNOWN.getColor() ? RoomCheckmark.UNEXPLORED : RoomCheckmark.NONE)))));
        }
    }

    private final double rescale(double value, double inMin, double inMax, double outMin, double outMax) {
        if (inMax - inMin == 0.0) {
            return outMin;
        }
        return outMin + (value - inMin) * (outMax - outMin) / (inMax - inMin);
    }

    private final void scanRoomDefinition(class_638 level2, DungeonRoom room) {
        for (GridComponent component : room.getComponents()) {
            RoomDefinition definition;
            class_2338 center = component.toWorldCenter();
            if (!this.isChunkLoaded(level2, center.method_10263(), center.method_10260())) continue;
            if (room.getHeight() == 0) {
                room.setHeight(this.getHighestY(level2, center.method_10263(), center.method_10260()));
            }
            if (roomDefinitionsByCore.get(this.hashCeiling(level2, center.method_10263(), center.method_10260())) == null) continue;
            room.applyDefinition(definition);
        }
        room.updateShape();
    }

    private final void findRoomRotation(class_638 level2, DungeonRoom room) {
        if (room.getHeight() == 0) {
            return;
        }
        if (room.getType() == RoomKind.FAIRY) {
            GridComponent gridComponent = (GridComponent)CollectionsKt.firstOrNull(room.getComponents());
            if (gridComponent == null || (gridComponent = gridComponent.toWorldCenter()) == null) {
                return;
            }
            GridComponent center = gridComponent;
            room.setRotation(0);
            room.setCorner(new class_2338(center.method_10263() - 15, 0, center.method_10260() - 15));
            return;
        }
        for (GridComponent component : room.getComponents()) {
            class_2338 center = component.toWorldCenter();
            class_2338[] class_2338Array = new class_2338[]{new class_2338(center.method_10263() - 15, 0, center.method_10260() - 15), new class_2338(center.method_10263() + 15, 0, center.method_10260() - 15), new class_2338(center.method_10263() + 15, 0, center.method_10260() + 15), new class_2338(center.method_10263() - 15, 0, center.method_10260() + 15)};
            class_2338[] candidates = class_2338Array;
            int n = candidates.length;
            for (int i = 0; i < n; ++i) {
                int index = i;
                class_2338 candidate = candidates[i];
                if (!this.isChunkLoaded(level2, candidate.method_10263(), candidate.method_10260()) || !level2.method_8320(new class_2338(candidate.method_10263(), room.getHeight(), candidate.method_10260())).method_27852(class_2246.field_10409)) continue;
                room.setRotation(index * 90);
                room.setCorner(candidate);
                return;
            }
        }
    }

    private final int hashCeiling(class_638 level2, int x, int z) {
        if (legacyBlockIds.isEmpty()) {
            return 0;
        }
        StringBuilder builder = new StringBuilder();
        for (int y = 140; 11 < y; --y) {
            class_2248 block;
            class_2680 state;
            Intrinsics.checkNotNullExpressionValue((Object)level2.method_8320(new class_2338(x, y, z)), (String)"getBlockState(...)");
            Intrinsics.checkNotNullExpressionValue((Object)state.method_26204(), (String)"getBlock(...)");
            Integer n = this.getLegacyBlockId(state, block);
            if (n == null) {
                continue;
            }
            int blockId = n;
            if (Intrinsics.areEqual((Object)block, (Object)class_2246.field_10576) || Intrinsics.areEqual((Object)block, (Object)class_2246.field_10034)) {
                builder.append('0');
                continue;
            }
            builder.append(blockId);
        }
        return builder.toString().hashCode();
    }

    private final Integer getLegacyBlockId(class_2680 state, class_2248 block) {
        String string = class_7923.field_41175.method_10221((Object)block).toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        Object registryName = string;
        if (!state.method_26227().method_15769()) {
            if (state.method_26227().method_15771()) {
                if (Intrinsics.areEqual((Object)block, (Object)class_2246.field_10382)) {
                    return 9;
                }
                if (Intrinsics.areEqual((Object)block, (Object)class_2246.field_10164)) {
                    return 11;
                }
            } else {
                if (Intrinsics.areEqual((Object)block, (Object)class_2246.field_10382)) {
                    return 8;
                }
                if (Intrinsics.areEqual((Object)block, (Object)class_2246.field_10164)) {
                    return 10;
                }
            }
        }
        if (block instanceof class_2482) {
            registryName = (String)registryName + "[type=" + ((class_2771)state.method_11654((class_2769)class_2482.field_11501)).method_15434() + "]";
        }
        return legacyBlockIds.get(registryName);
    }

    private final List<GridComponent> findAvailableComponents() {
        ArrayList<GridComponent> positions = new ArrayList<GridComponent>();
        for (int z = 0; z < 11; ++z) {
            for (int x = 0; x < 11; ++x) {
                GridComponent component = new GridComponent(x, z);
                if (x % 2 != 0 && z % 2 != 0) continue;
                positions.add(component);
            }
        }
        return positions;
    }

    private final boolean isChunkLoaded(class_638 level2, int x, int z) {
        return level2.method_8393(x >> 4, z >> 4);
    }

    private final int getHighestY(class_638 level2, int x, int z) {
        for (int y = 256; -1 < y; --y) {
            class_2680 state;
            Intrinsics.checkNotNullExpressionValue((Object)level2.method_8320(new class_2338(x, y, z)), (String)"getBlockState(...)");
            if (state.method_26215() || state.method_27852(class_2246.field_10205)) continue;
            return y;
        }
        return 0;
    }

    private final class_2248 getBlock(class_638 level2, int x, int y, int z) {
        Object object;
        Object object2 = this;
        try {
            DungeonScanState $this$getBlock_u24lambda_u240 = object2;
            boolean bl = false;
            object = Result.constructor-impl((Object)level2.method_8320(new class_2338(x, y, z)).method_26204());
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        object2 = object;
        return (class_2248)(Result.isFailure-impl((Object)object2) ? null : object2);
    }

    private static final Map loadRoomDefinitions$lambda$0(Reader reader) {
        Map map;
        Intrinsics.checkNotNullParameter((Object)reader, (String)"reader");
        JsonArray root = JsonParser.parseReader((Reader)reader).getAsJsonArray();
        Map $this$loadRoomDefinitions_u24lambda_u240_u240 = map = MapsKt.createMapBuilder();
        boolean bl = false;
        Iterator iterator = root.iterator();
        Intrinsics.checkNotNullExpressionValue((Object)iterator, (String)"iterator(...)");
        Iterator iterator2 = iterator;
        while (iterator2.hasNext()) {
            Iterator iterator3;
            JsonArray cores;
            int n;
            JsonElement element = (JsonElement)iterator2.next();
            if (!element.isJsonObject()) continue;
            JsonObject obj = element.getAsJsonObject();
            JsonElement jsonElement = obj.get("name");
            String string = jsonElement != null ? jsonElement.getAsString() : null;
            if (string == null) {
                string = "";
            }
            JsonElement jsonElement2 = obj.get("type");
            RoomKind roomKind = RoomKind.Companion.fromName(jsonElement2 != null ? jsonElement2.getAsString() : null);
            JsonElement jsonElement3 = obj.get("secrets");
            if (jsonElement3 != null) {
                n = jsonElement3.getAsInt();
            } else {
                JsonElement jsonElement4 = obj.get("crypts");
                n = jsonElement4 != null ? jsonElement4.getAsInt() : 0;
            }
            RoomDefinition definition = new RoomDefinition(string, roomKind, n);
            if (obj.getAsJsonArray("cores") == null) continue;
            Intrinsics.checkNotNullExpressionValue((Object)cores.iterator(), (String)"iterator(...)");
            while (iterator3.hasNext()) {
                JsonElement coreElement = (JsonElement)iterator3.next();
                $this$loadRoomDefinitions_u24lambda_u240_u240.put(coreElement.getAsInt(), definition);
            }
        }
        return MapsKt.build((Map)map);
    }

    private static final Map loadLegacyBlocks$lambda$0(Reader reader) {
        Map map;
        Intrinsics.checkNotNullParameter((Object)reader, (String)"reader");
        JsonObject root = JsonParser.parseReader((Reader)reader).getAsJsonObject();
        Map $this$loadLegacyBlocks_u24lambda_u240_u240 = map = MapsKt.createMapBuilder();
        boolean bl = false;
        for (Map.Entry entry : root.entrySet()) {
            Intrinsics.checkNotNull((Object)entry);
            String key = (String)entry.getKey();
            JsonElement value = (JsonElement)entry.getValue();
            Intrinsics.checkNotNull((Object)key);
            $this$loadLegacyBlocks_u24lambda_u240_u240.put(key, value.getAsInt());
        }
        return MapsKt.build((Map)map);
    }

    private static final Map loadRoutes$lambda$0(Reader reader) {
        Map map;
        Intrinsics.checkNotNullParameter((Object)reader, (String)"reader");
        JsonObject root = JsonParser.parseReader((Reader)reader).getAsJsonObject();
        Map $this$loadRoutes_u24lambda_u240_u240 = map = MapsKt.createMapBuilder();
        boolean bl = false;
        for (Map.Entry entry : root.entrySet()) {
            Iterator iterator;
            Intrinsics.checkNotNull((Object)entry);
            String rawKey = (String)entry.getKey();
            JsonElement value = (JsonElement)entry.getValue();
            if (!value.isJsonArray()) continue;
            List steps = new ArrayList();
            Intrinsics.checkNotNullExpressionValue((Object)value.getAsJsonArray().iterator(), (String)"iterator(...)");
            while (iterator.hasNext()) {
                RouteStep step;
                JsonElement stepElement = (JsonElement)iterator.next();
                if (!stepElement.isJsonObject()) continue;
                JsonObject jsonObject = stepElement.getAsJsonObject();
                Intrinsics.checkNotNullExpressionValue((Object)jsonObject, (String)"getAsJsonObject(...)");
                if (INSTANCE.parseRouteStep(jsonObject) == null) continue;
                steps.add(step);
            }
            if (!(!((Collection)steps).isEmpty())) continue;
            Intrinsics.checkNotNull((Object)rawKey);
            $this$loadRoutes_u24lambda_u240_u240.put(INSTANCE.normalizeRouteKey(rawKey), new LoadedRoute(rawKey, steps));
        }
        return MapsKt.build((Map)map);
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        floorRegex = new Regex("The Catacombs \\((Entrance|E|F[1-7]|M[1-7]|Floor [IVX]+|Master Mode Floor [IVX]+)\\)", RegexOption.IGNORE_CASE);
        lastProcessedTick = Long.MIN_VALUE;
        roomDefinitionsByCore = MapsKt.emptyMap();
        legacyBlockIds = MapsKt.emptyMap();
        routesByKey = MapsKt.emptyMap();
        referenceStatus = "Waiting for bundled data";
        roomSizeOnMap = -1;
        roomGapOnMap = -1;
        mapOffsetX = -1;
        mapOffsetZ = -1;
        availableComponents = new ArrayList();
        rooms = new DungeonRoom[36];
        doors = new DungeonDoor[60];
        mapMarkers = new ArrayList();
        floor = DungeonFloor.NONE;
        INSTANCE.reset();
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[RoomDirection.values().length];
            try {
                nArray[RoomDirection.NW.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RoomDirection.NE.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RoomDirection.SW.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RoomDirection.SE.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

