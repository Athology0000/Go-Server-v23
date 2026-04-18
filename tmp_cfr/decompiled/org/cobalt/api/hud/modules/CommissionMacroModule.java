/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.Pair
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.TuplesKt
 *  kotlin.collections.ArraysKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.IndexedValue
 *  kotlin.collections.MapsKt
 *  kotlin.collections.SetsKt
 *  kotlin.comparisons.ComparisonsKt
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.jvm.internal.SpreadBuilder
 *  kotlin.ranges.RangesKt
 *  kotlin.sequences.Sequence
 *  kotlin.sequences.SequencesKt
 *  kotlin.text.CharsKt
 *  kotlin.text.MatchResult
 *  kotlin.text.Regex
 *  kotlin.text.RegexOption
 *  kotlin.text.StringsKt
 *  net.minecraft.class_124
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1531
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1735
 *  net.minecraft.class_1799
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2371
 *  net.minecraft.class_2382
 *  net.minecraft.class_243
 *  net.minecraft.class_2561
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  net.minecraft.class_465
 *  net.minecraft.class_634
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.hud.modules;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IndexedValue;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.jvm.internal.SpreadBuilder;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.text.CharsKt;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.RegexOption;
import kotlin.text.StringsKt;
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2371;
import net.minecraft.class_2382;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_634;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.ChatEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.hud.modules.CommissionMacroModule$hasNearbyGlaciteWalker$;
import org.cobalt.api.hud.modules.CommissionMacroModule$hasNearbyGoblin$;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.InfoSetting;
import org.cobalt.api.module.setting.impl.InfoType;
import org.cobalt.api.module.setting.impl.ModeSetting;
import org.cobalt.api.module.setting.impl.TextSetting;
import org.cobalt.api.pathfinder.jni.NativePathfinder;
import org.cobalt.api.pathfinder.jni.PathCommand;
import org.cobalt.api.pathfinder.jni.PathStatus;
import org.cobalt.api.pathfinder.minecraft.MinecraftPathingRules;
import org.cobalt.api.rotation.RotationExecutor;
import org.cobalt.api.rotation.strategy.TrackingRotationStrategy;
import org.cobalt.api.util.AngleUtils;
import org.cobalt.api.util.ChatUtils;
import org.cobalt.api.util.InventoryUtils;
import org.cobalt.api.util.ItemUtilsKt;
import org.cobalt.api.util.helper.Rotation;
import org.cobalt.api.util.player.MovementManager;
import org.cobalt.internal.combat.CombatMacroModule;
import org.cobalt.internal.etherwarp.EtherwarpLogic;
import org.cobalt.internal.mining.MiningBlockRegistry;
import org.cobalt.internal.mining.MiningMacroModule;
import org.cobalt.internal.mining.MiningProfitTracker;
import org.cobalt.internal.mining.RoutesModule;
import org.cobalt.internal.pathfinding.PathfindingModule;
import org.cobalt.internal.routes.RoutePickerSetting;
import org.cobalt.internal.routes.RouteStore;
import org.cobalt.internal.routes.RouteType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00b0\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\b\n\u0002\b\u0012\n\u0002\u0010\u0007\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0018\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010$\n\u0002\b\n\n\u0002\u0010\"\n\u0002\b\u0016\n\u0002\u0010\u0000\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\t\n\u0002\b\u001b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b$\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0012\u00e5\u0002\u00e6\u0002\u00e7\u0002\u00e8\u0002\u00e9\u0002\u00ea\u0002\u00eb\u0002\u00ec\u0002\u00ed\u0002B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0013\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0013\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\u0004\u00a2\u0006\u0004\b\t\u0010\u0007J\u0013\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\u0004\u00a2\u0006\u0004\b\u000b\u0010\u0007J\u0017\u0010\u000e\u001a\u0004\u0018\u00010\r2\u0006\u0010\f\u001a\u00020\n\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u001d\u0010\u0012\u001a\u00020\u00112\u0006\u0010\f\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\r\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u001f\u0010\u0015\u001a\u00020\u00142\u0006\u0010\f\u001a\u00020\n2\b\u0010\u0010\u001a\u0004\u0018\u00010\r\u00a2\u0006\u0004\b\u0015\u0010\u0016J\r\u0010\u0017\u001a\u00020\u0014\u00a2\u0006\u0004\b\u0017\u0010\u0003J\u0013\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\r0\u0004\u00a2\u0006\u0004\b\u0018\u0010\u0007J\u0015\u0010\u0019\u001a\u00020\u00112\u0006\u0010\u0010\u001a\u00020\r\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\u0015\u0010\u001b\u001a\u00020\u00142\u0006\u0010\u0010\u001a\u00020\r\u00a2\u0006\u0004\b\u001b\u0010\u001cJ\u001d\u0010\u001e\u001a\u00020\u00142\u0006\u0010\u0010\u001a\u00020\r2\u0006\u0010\u001d\u001a\u00020\u0011\u00a2\u0006\u0004\b\u001e\u0010\u001fJ\r\u0010 \u001a\u00020\u0014\u00a2\u0006\u0004\b \u0010\u0003J\u0017\u0010#\u001a\u00020\u00142\u0006\u0010\"\u001a\u00020!H\u0007\u00a2\u0006\u0004\b#\u0010$J\u0017\u0010&\u001a\u00020\u00142\u0006\u0010\"\u001a\u00020%H\u0007\u00a2\u0006\u0004\b&\u0010'J\u0017\u0010*\u001a\u00020\u00142\u0006\u0010)\u001a\u00020(H\u0002\u00a2\u0006\u0004\b*\u0010+J\u000f\u0010,\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b,\u0010\u0003J\u000f\u0010-\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b-\u0010\u0003J\u0017\u0010.\u001a\u00020\u00142\u0006\u0010)\u001a\u00020(H\u0002\u00a2\u0006\u0004\b.\u0010+J\u0017\u0010/\u001a\u00020\u00142\u0006\u0010)\u001a\u00020(H\u0002\u00a2\u0006\u0004\b/\u0010+J\u000f\u00100\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b0\u0010\u0003J\u000f\u00101\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b1\u0010\u0003J\u000f\u00102\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b2\u0010\u0003J\u000f\u00103\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b3\u0010\u0003J\u000f\u00104\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b4\u0010\u0003J\u0019\u00106\u001a\u0002052\b\u0010)\u001a\u0004\u0018\u00010(H\u0002\u00a2\u0006\u0004\b6\u00107J\u001f\u00109\u001a\u00020\u00112\u0006\u0010)\u001a\u00020(2\u0006\u00108\u001a\u000205H\u0002\u00a2\u0006\u0004\b9\u0010:J\u0017\u0010;\u001a\u00020\u00142\u0006\u00108\u001a\u000205H\u0002\u00a2\u0006\u0004\b;\u0010<J\u0017\u0010=\u001a\u00020\u00142\u0006\u00108\u001a\u000205H\u0002\u00a2\u0006\u0004\b=\u0010<J\u0017\u0010@\u001a\u00020\u00112\u0006\u0010?\u001a\u00020>H\u0002\u00a2\u0006\u0004\b@\u0010AJ\u0017\u0010B\u001a\u00020\u00112\u0006\u0010?\u001a\u00020>H\u0002\u00a2\u0006\u0004\bB\u0010AJ\u0017\u0010C\u001a\u00020\r2\u0006\u0010?\u001a\u00020>H\u0002\u00a2\u0006\u0004\bC\u0010DJ\u0017\u0010E\u001a\u00020\u00142\u0006\u0010?\u001a\u00020>H\u0002\u00a2\u0006\u0004\bE\u0010FJ\u001f\u0010G\u001a\u00020\u00112\u0006\u0010)\u001a\u00020(2\u0006\u0010?\u001a\u00020>H\u0002\u00a2\u0006\u0004\bG\u0010HJ\u001f\u0010I\u001a\u00020\u00112\u0006\u0010)\u001a\u00020(2\u0006\u0010?\u001a\u00020>H\u0002\u00a2\u0006\u0004\bI\u0010HJ\u0017\u0010J\u001a\u00020\u00142\u0006\u0010)\u001a\u00020(H\u0002\u00a2\u0006\u0004\bJ\u0010+J\u0017\u0010K\u001a\u00020\u00112\u0006\u0010)\u001a\u00020(H\u0002\u00a2\u0006\u0004\bK\u0010LJ\u0017\u0010M\u001a\u00020\u00112\u0006\u0010)\u001a\u00020(H\u0002\u00a2\u0006\u0004\bM\u0010LJ\u001f\u0010P\u001a\u00020\u00142\u0006\u0010)\u001a\u00020(2\u0006\u0010O\u001a\u00020NH\u0002\u00a2\u0006\u0004\bP\u0010QJ3\u0010U\u001a\u00020\u00142\u0006\u00108\u001a\u0002052\u0006\u0010R\u001a\u00020\u00112\b\b\u0002\u0010S\u001a\u00020\u00112\b\b\u0002\u0010T\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\bU\u0010VJ\u001f\u0010W\u001a\u00020\u00112\u0006\u00108\u001a\u0002052\u0006\u0010R\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\bW\u0010XJ\u001f\u0010Y\u001a\u00020\u00112\u0006\u00108\u001a\u0002052\u0006\u0010R\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\bY\u0010XJ\u0019\u0010[\u001a\u0004\u0018\u00010Z2\u0006\u00108\u001a\u000205H\u0002\u00a2\u0006\u0004\b[\u0010\\J\u0017\u0010]\u001a\u00020\r2\u0006\u00108\u001a\u000205H\u0002\u00a2\u0006\u0004\b]\u0010^J\u000f\u0010_\u001a\u00020ZH\u0002\u00a2\u0006\u0004\b_\u0010`J\u000f\u0010a\u001a\u00020NH\u0002\u00a2\u0006\u0004\ba\u0010bJ\u0019\u0010c\u001a\u00020\u00142\b\b\u0002\u00108\u001a\u000205H\u0002\u00a2\u0006\u0004\bc\u0010<J!\u0010e\u001a\u0004\u0018\u00010d2\u0006\u0010)\u001a\u00020(2\u0006\u00108\u001a\u000205H\u0002\u00a2\u0006\u0004\be\u0010fJ\u0017\u0010g\u001a\u00020\u00112\u0006\u00108\u001a\u000205H\u0002\u00a2\u0006\u0004\bg\u0010hJ\u001f\u0010i\u001a\u00020N2\u0006\u0010)\u001a\u00020(2\u0006\u0010O\u001a\u00020dH\u0002\u00a2\u0006\u0004\bi\u0010jJ'\u0010l\u001a\u0004\u0018\u00010N2\u0006\u0010)\u001a\u00020(2\f\u0010k\u001a\b\u0012\u0004\u0012\u00020N0\u0004H\u0002\u00a2\u0006\u0004\bl\u0010mJ\u0019\u0010p\u001a\u0004\u0018\u00010o2\u0006\u0010n\u001a\u00020NH\u0002\u00a2\u0006\u0004\bp\u0010qJ\u0019\u0010r\u001a\u0004\u0018\u00010o2\u0006\u0010)\u001a\u00020(H\u0002\u00a2\u0006\u0004\br\u0010sJ\u0017\u0010u\u001a\u00020\u00112\u0006\u0010t\u001a\u00020oH\u0002\u00a2\u0006\u0004\bu\u0010vJ\u0017\u0010w\u001a\u00020\u00112\u0006\u0010t\u001a\u00020oH\u0002\u00a2\u0006\u0004\bw\u0010vJ\u0017\u0010x\u001a\u00020\r2\u0006\u0010t\u001a\u00020oH\u0002\u00a2\u0006\u0004\bx\u0010yJ\u0017\u0010{\u001a\u00020z2\u0006\u0010)\u001a\u00020(H\u0002\u00a2\u0006\u0004\b{\u0010|J\u0017\u0010}\u001a\u00020z2\u0006\u0010)\u001a\u00020(H\u0002\u00a2\u0006\u0004\b}\u0010|J\u0018\u0010\u007f\u001a\u00020\u00142\u0006\u0010~\u001a\u00020zH\u0002\u00a2\u0006\u0005\b\u007f\u0010\u0080\u0001J\u001b\u0010\u0082\u0001\u001a\u00020\r2\u0007\u0010\u0081\u0001\u001a\u00020\rH\u0002\u00a2\u0006\u0006\b\u0082\u0001\u0010\u0083\u0001J\u001b\u0010\u0085\u0001\u001a\u00020N2\u0007\u0010\u0084\u0001\u001a\u00020NH\u0002\u00a2\u0006\u0006\b\u0085\u0001\u0010\u0086\u0001J\u001a\u0010\u0087\u0001\u001a\u00020\u00142\u0006\u0010t\u001a\u00020oH\u0002\u00a2\u0006\u0006\b\u0087\u0001\u0010\u0088\u0001J\u0019\u0010\u0089\u0001\u001a\u00020\u00142\u0006\u0010)\u001a\u00020(H\u0002\u00a2\u0006\u0005\b\u0089\u0001\u0010+J\u0019\u0010\u008a\u0001\u001a\u00020\u00112\u0006\u0010)\u001a\u00020(H\u0002\u00a2\u0006\u0005\b\u008a\u0001\u0010LJ\u001b\u0010\u008b\u0001\u001a\u0004\u0018\u00010o2\u0006\u0010)\u001a\u00020(H\u0002\u00a2\u0006\u0005\b\u008b\u0001\u0010sJ\u0019\u0010\u008c\u0001\u001a\u00020\u00112\u0006\u0010t\u001a\u00020oH\u0002\u00a2\u0006\u0005\b\u008c\u0001\u0010vJB\u0010\u0092\u0001\u001a\u00020\u00142\u0006\u0010)\u001a\u00020(2\b\u0010\u008e\u0001\u001a\u00030\u008d\u00012\b\u0010\u008f\u0001\u001a\u00030\u008d\u00012\b\u0010\u0090\u0001\u001a\u00030\u008d\u00012\b\u0010\u0091\u0001\u001a\u00030\u008d\u0001H\u0002\u00a2\u0006\u0006\b\u0092\u0001\u0010\u0093\u0001J\u0012\u0010\u0094\u0001\u001a\u00020\u0011H\u0002\u00a2\u0006\u0006\b\u0094\u0001\u0010\u0095\u0001J\u001a\u0010\u0096\u0001\u001a\u00020\u00142\u0006\u0010O\u001a\u00020NH\u0002\u00a2\u0006\u0006\b\u0096\u0001\u0010\u0097\u0001J!\u0010\u0098\u0001\u001a\u00020\u00142\u0006\u0010)\u001a\u00020(2\u0006\u0010O\u001a\u00020NH\u0002\u00a2\u0006\u0005\b\u0098\u0001\u0010QJ!\u0010\u0099\u0001\u001a\u00020\u00142\u0006\u0010)\u001a\u00020(2\u0006\u0010O\u001a\u00020NH\u0002\u00a2\u0006\u0005\b\u0099\u0001\u0010QJ\u0019\u0010\u009a\u0001\u001a\u00020\u00142\u0006\u0010)\u001a\u00020(H\u0002\u00a2\u0006\u0005\b\u009a\u0001\u0010+J\u001d\u0010\u009c\u0001\u001a\u0005\u0018\u00010\u009b\u00012\u0006\u0010)\u001a\u00020(H\u0002\u00a2\u0006\u0006\b\u009c\u0001\u0010\u009d\u0001J)\u0010\u009e\u0001\u001a\u00020z2\u0006\u0010)\u001a\u00020(2\r\u0010k\u001a\t\u0012\u0005\u0012\u00030\u009b\u00010\u0004H\u0002\u00a2\u0006\u0006\b\u009e\u0001\u0010\u009f\u0001J\u0011\u0010\u00a0\u0001\u001a\u00020\u0014H\u0002\u00a2\u0006\u0005\b\u00a0\u0001\u0010\u0003J\u0011\u0010\u00a1\u0001\u001a\u00020\u0014H\u0002\u00a2\u0006\u0005\b\u00a1\u0001\u0010\u0003JQ\u0010\u00a9\u0001\u001a\u00020\u00142\u0007\u0010\u00a2\u0001\u001a\u00020\u00112\u0007\u0010\u00a3\u0001\u001a\u00020\u00112\u0007\u0010\u00a4\u0001\u001a\u00020\u00112\u0007\u0010\u00a5\u0001\u001a\u00020\u00112\u0007\u0010\u00a6\u0001\u001a\u00020\u00112\u0007\u0010\u00a7\u0001\u001a\u00020\u00112\u0007\u0010\u00a8\u0001\u001a\u00020\u0011H\u0002\u00a2\u0006\u0006\b\u00a9\u0001\u0010\u00aa\u0001J!\u0010\u00ae\u0001\u001a\u00030\u00ad\u00012\f\u0010\u00ac\u0001\u001a\u0007\u0012\u0002\b\u00030\u00ab\u0001H\u0002\u00a2\u0006\u0006\b\u00ae\u0001\u0010\u00af\u0001J \u0010\u00b0\u0001\u001a\u00020z2\f\u0010\u00ac\u0001\u001a\u0007\u0012\u0002\b\u00030\u00ab\u0001H\u0002\u00a2\u0006\u0006\b\u00b0\u0001\u0010\u00b1\u0001J*\u0010\u00b4\u0001\u001a\u00020\u00112\r\u0010\u00b2\u0001\u001a\b\u0012\u0004\u0012\u00020\r0\u00042\u0007\u0010\u00b3\u0001\u001a\u00020\rH\u0002\u00a2\u0006\u0006\b\u00b4\u0001\u0010\u00b5\u0001J'\u0010\u00b7\u0001\u001a\t\u0012\u0005\u0012\u00030\u00b6\u00010\u00042\f\u0010\u00ac\u0001\u001a\u0007\u0012\u0002\b\u00030\u00ab\u0001H\u0002\u00a2\u0006\u0006\b\u00b7\u0001\u0010\u00b8\u0001J\"\u0010\u00bb\u0001\u001a\b\u0012\u0004\u0012\u00020\r0\u00042\b\u0010\u00ba\u0001\u001a\u00030\u00b9\u0001H\u0002\u00a2\u0006\u0006\b\u00bb\u0001\u0010\u00bc\u0001JA\u0010\u00bf\u0001\u001a\u00020\r2\b\u0010\u00ba\u0001\u001a\u00030\u00b9\u00012\r\u0010\u00b2\u0001\u001a\b\u0012\u0004\u0012\u00020\r0\u00042\t\u0010\u00bd\u0001\u001a\u0004\u0018\u00010\r2\t\u0010\u00be\u0001\u001a\u0004\u0018\u00010\rH\u0002\u00a2\u0006\u0006\b\u00bf\u0001\u0010\u00c0\u0001J#\u0010\u00c1\u0001\u001a\u0004\u0018\u00010\r2\r\u0010\u00b2\u0001\u001a\b\u0012\u0004\u0012\u00020\r0\u0004H\u0002\u00a2\u0006\u0006\b\u00c1\u0001\u0010\u00c2\u0001J*\u0010\u00c3\u0001\u001a\u00020\r2\r\u0010\u00b2\u0001\u001a\b\u0012\u0004\u0012\u00020\r0\u00042\u0007\u0010\u00bd\u0001\u001a\u00020\rH\u0002\u00a2\u0006\u0006\b\u00c3\u0001\u0010\u00c4\u0001J*\u0010\u00c5\u0001\u001a\u00020\r2\r\u0010\u00b2\u0001\u001a\b\u0012\u0004\u0012\u00020\r0\u00042\u0007\u0010\u00be\u0001\u001a\u00020\rH\u0002\u00a2\u0006\u0006\b\u00c5\u0001\u0010\u00c4\u0001J\u001d\u0010\u00c6\u0001\u001a\u0004\u0018\u00010\r2\u0007\u0010\u0081\u0001\u001a\u00020\rH\u0002\u00a2\u0006\u0006\b\u00c6\u0001\u0010\u0083\u0001J#\u0010\u00c7\u0001\u001a\u0004\u0018\u00010\r2\r\u0010\u00b2\u0001\u001a\b\u0012\u0004\u0012\u00020\r0\u0004H\u0002\u00a2\u0006\u0006\b\u00c7\u0001\u0010\u00c2\u0001J\u001d\u0010\u00c8\u0001\u001a\u0004\u0018\u00010\r2\u0007\u0010\u0081\u0001\u001a\u00020\rH\u0002\u00a2\u0006\u0006\b\u00c8\u0001\u0010\u0083\u0001J\u001b\u0010\u00c9\u0001\u001a\u00020\r2\u0007\u0010\u00bd\u0001\u001a\u00020\rH\u0002\u00a2\u0006\u0006\b\u00c9\u0001\u0010\u0083\u0001J\u001b\u0010\u00cb\u0001\u001a\u00020z2\u0007\u0010\u00ca\u0001\u001a\u00020>H\u0002\u00a2\u0006\u0006\b\u00cb\u0001\u0010\u00cc\u0001J\u001b\u0010\u00ce\u0001\u001a\u00020\r2\u0007\u0010\u00cd\u0001\u001a\u00020\rH\u0002\u00a2\u0006\u0006\b\u00ce\u0001\u0010\u0083\u0001J\u001b\u0010\u00cf\u0001\u001a\u00020\r2\u0007\u0010\u00cd\u0001\u001a\u00020\rH\u0002\u00a2\u0006\u0006\b\u00cf\u0001\u0010\u0083\u0001J*\u0010\u00d0\u0001\u001a\u00020\u00112\r\u0010\u00b2\u0001\u001a\b\u0012\u0004\u0012\u00020\r0\u00042\u0007\u0010\u00b3\u0001\u001a\u00020\rH\u0002\u00a2\u0006\u0006\b\u00d0\u0001\u0010\u00b5\u0001J\u001a\u0010\u00d1\u0001\u001a\u00020\u00112\u0007\u0010\u00b3\u0001\u001a\u00020\rH\u0002\u00a2\u0006\u0005\b\u00d1\u0001\u0010\u001aJ(\u0010\u00d3\u0001\u001a\u000f\u0012\u0004\u0012\u00020z\u0012\u0004\u0012\u00020z0\u00d2\u00012\u0007\u0010\u00b3\u0001\u001a\u00020\rH\u0002\u00a2\u0006\u0006\b\u00d3\u0001\u0010\u00d4\u0001J#\u0010\u00d5\u0001\u001a\u0004\u0018\u00010\r2\r\u0010\u00b2\u0001\u001a\b\u0012\u0004\u0012\u00020\r0\u0004H\u0002\u00a2\u0006\u0006\b\u00d5\u0001\u0010\u00c2\u0001J#\u0010\u00d6\u0001\u001a\u0004\u0018\u00010\r2\r\u0010\u00b2\u0001\u001a\b\u0012\u0004\u0012\u00020\r0\u0004H\u0002\u00a2\u0006\u0006\b\u00d6\u0001\u0010\u00c2\u0001J\u0019\u0010\u00d7\u0001\u001a\u00020\u00142\u0006\u0010?\u001a\u00020>H\u0002\u00a2\u0006\u0005\b\u00d7\u0001\u0010FJ\u0011\u0010\u00d8\u0001\u001a\u00020\u0014H\u0002\u00a2\u0006\u0005\b\u00d8\u0001\u0010\u0003J\u0019\u0010\u00d9\u0001\u001a\u00020\u00112\u0006\u0010?\u001a\u00020>H\u0002\u00a2\u0006\u0005\b\u00d9\u0001\u0010AJ\u001b\u0010\u00da\u0001\u001a\u0004\u0018\u00010\r2\u0006\u0010?\u001a\u00020>H\u0002\u00a2\u0006\u0005\b\u00da\u0001\u0010DJ7\u0010\u00dd\u0001\u001a\b\u0012\u0004\u0012\u00020\b0\u00042\u0007\u0010\u00ca\u0001\u001a\u00020>2\u0014\u0010\u00dc\u0001\u001a\u000f\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\b0\u00db\u0001H\u0002\u00a2\u0006\u0006\b\u00dd\u0001\u0010\u00de\u0001J\u0019\u0010\u00df\u0001\u001a\u00020\u00112\u0006\u0010?\u001a\u00020>H\u0002\u00a2\u0006\u0005\b\u00df\u0001\u0010AJ,\u0010\u00e1\u0001\u001a\u0004\u0018\u00010\r2\r\u0010\u00e0\u0001\u001a\b\u0012\u0004\u0012\u00020\b0\u00042\u0007\u0010\u00ca\u0001\u001a\u00020>H\u0002\u00a2\u0006\u0006\b\u00e1\u0001\u0010\u00e2\u0001J$\u0010\u00e4\u0001\u001a\u00020z2\u0007\u0010\u00e3\u0001\u001a\u00020\r2\u0007\u0010\u00ca\u0001\u001a\u00020>H\u0002\u00a2\u0006\u0006\b\u00e4\u0001\u0010\u00e5\u0001J3\u0010\u00e8\u0001\u001a\u0004\u0018\u00010\r2\r\u0010\u00e0\u0001\u001a\b\u0012\u0004\u0012\u00020\b0\u00042\u000e\u0010\u00e7\u0001\u001a\t\u0012\u0004\u0012\u00020\r0\u00e6\u0001H\u0002\u00a2\u0006\u0006\b\u00e8\u0001\u0010\u00e9\u0001J1\u0010\u00eb\u0001\u001a\t\u0012\u0004\u0012\u00020\r0\u00e6\u00012\u0006\u0010?\u001a\u00020>2\u000e\u0010\u00ea\u0001\u001a\t\u0012\u0004\u0012\u00020\r0\u00e6\u0001H\u0002\u00a2\u0006\u0006\b\u00eb\u0001\u0010\u00ec\u0001J\u0019\u0010\u00ed\u0001\u001a\u00020\r2\u0006\u0010?\u001a\u00020>H\u0002\u00a2\u0006\u0005\b\u00ed\u0001\u0010DJ\u001d\u0010\u00ef\u0001\u001a\u0004\u0018\u00010\r2\u0007\u0010\u00ee\u0001\u001a\u00020\rH\u0002\u00a2\u0006\u0006\b\u00ef\u0001\u0010\u0083\u0001J\u001d\u0010\u00f0\u0001\u001a\u0004\u0018\u00010\n2\u0007\u0010\u00ee\u0001\u001a\u00020\rH\u0002\u00a2\u0006\u0006\b\u00f0\u0001\u0010\u00f1\u0001J,\u0010\u00f3\u0001\u001a\u0004\u0018\u00010\r2\u0007\u0010\u00ca\u0001\u001a\u00020>2\r\u0010\u00f2\u0001\u001a\b\u0012\u0004\u0012\u00020\b0\u0004H\u0002\u00a2\u0006\u0006\b\u00f3\u0001\u0010\u00f4\u0001J#\u0010\u00f5\u0001\u001a\u00020\u00112\u0007\u0010\u00e3\u0001\u001a\u00020\r2\u0006\u0010\f\u001a\u00020\nH\u0002\u00a2\u0006\u0006\b\u00f5\u0001\u0010\u00f6\u0001J2\u0010\u00f8\u0001\u001a\u0004\u0018\u00010\b2\u0014\u0010\u00f7\u0001\u001a\u000f\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\b0\u00db\u00012\u0006\u0010\u0010\u001a\u00020\rH\u0002\u00a2\u0006\u0006\b\u00f8\u0001\u0010\u00f9\u0001J\u0019\u0010\u00fa\u0001\u001a\u00020\r2\u0006\u0010\f\u001a\u00020\nH\u0002\u00a2\u0006\u0005\b\u00fa\u0001\u0010\u000fJ\u0014\u0010\u00fb\u0001\u001a\u0004\u0018\u00010\rH\u0002\u00a2\u0006\u0006\b\u00fb\u0001\u0010\u00fc\u0001J#\u0010\u00ff\u0001\u001a\t\u0012\u0005\u0012\u00030\u00fd\u00010\u00042\b\u0010\u00fe\u0001\u001a\u00030\u00fd\u0001H\u0002\u00a2\u0006\u0006\b\u00ff\u0001\u0010\u0080\u0002J\u001e\u0010\u0082\u0002\u001a\u0004\u0018\u00010\r2\b\u0010\u0081\u0002\u001a\u00030\u00fd\u0001H\u0002\u00a2\u0006\u0006\b\u0082\u0002\u0010\u0083\u0002J \u0010\u0084\u0002\u001a\u0004\u0018\u00010\r2\n\u0010\u00cd\u0001\u001a\u0005\u0018\u00010\u00fd\u0001H\u0002\u00a2\u0006\u0006\b\u0084\u0002\u0010\u0083\u0002J\u001c\u0010\u0087\u0002\u001a\u00020\u00142\b\u0010\u0086\u0002\u001a\u00030\u0085\u0002H\u0002\u00a2\u0006\u0006\b\u0087\u0002\u0010\u0088\u0002J\u001a\u0010\u008a\u0002\u001a\u00020\u00142\u0007\u0010\u0089\u0002\u001a\u00020\rH\u0002\u00a2\u0006\u0005\b\u008a\u0002\u0010\u001cJ\u0011\u0010\u008b\u0002\u001a\u00020\u0014H\u0002\u00a2\u0006\u0005\b\u008b\u0002\u0010\u0003J\u0011\u0010\u008c\u0002\u001a\u00020\u0014H\u0002\u00a2\u0006\u0005\b\u008c\u0002\u0010\u0003R\u0018\u0010\u008e\u0002\u001a\u00030\u008d\u00028\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u008e\u0002\u0010\u008f\u0002R\u0018\u0010\u0091\u0002\u001a\u00030\u0090\u00028\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0091\u0002\u0010\u0092\u0002R\u0018\u0010\u0094\u0002\u001a\u00030\u0093\u00028\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0094\u0002\u0010\u0095\u0002R\u0018\u0010\u0097\u0002\u001a\u00030\u0096\u00028\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0097\u0002\u0010\u0098\u0002R\u0018\u0010\u009a\u0002\u001a\u00030\u0099\u00028\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u009a\u0002\u0010\u009b\u0002R\u0018\u0010\u009d\u0002\u001a\u00030\u009c\u00028\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u009d\u0002\u0010\u009e\u0002R\u0018\u0010\u009f\u0002\u001a\u00030\u009c\u00028\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u009f\u0002\u0010\u009e\u0002R\u0018\u0010\u00a0\u0002\u001a\u00030\u009c\u00028\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a0\u0002\u0010\u009e\u0002R\u0018\u0010\u00a1\u0002\u001a\u00030\u009c\u00028\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a1\u0002\u0010\u009e\u0002R\u0017\u0010\u00a2\u0002\u001a\u00020N8\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a2\u0002\u0010\u00a3\u0002R\u0018\u0010\u00a5\u0002\u001a\u00030\u00a4\u00028\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a5\u0002\u0010\u00a6\u0002R\u0018\u0010\u00a7\u0002\u001a\u00030\u00a4\u00028\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a7\u0002\u0010\u00a6\u0002R\u0018\u0010\u00a8\u0002\u001a\u00030\u00a4\u00028\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a8\u0002\u0010\u00a6\u0002R\u0018\u0010\u00a9\u0002\u001a\u00030\u00a4\u00028\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a9\u0002\u0010\u00a6\u0002R\u0018\u0010\u00aa\u0002\u001a\u00030\u00a4\u00028\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00aa\u0002\u0010\u00a6\u0002R\u001a\u0010\u00ab\u0002\u001a\u00030\u0085\u00028\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00ab\u0002\u0010\u00ac\u0002R\u001a\u0010\u00ae\u0002\u001a\u00030\u00ad\u00028\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00ae\u0002\u0010\u00af\u0002R\u0019\u0010\u00b0\u0002\u001a\u0002058\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00b0\u0002\u0010\u00b1\u0002R\u001b\u0010\u00ca\u0001\u001a\u0004\u0018\u00010>8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00ca\u0001\u0010\u00b2\u0002R\u001f\u0010\u00b3\u0002\u001a\b\u0012\u0004\u0012\u00020>0\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00b3\u0002\u0010\u00b4\u0002R\u001b\u0010\u00b5\u0002\u001a\u0004\u0018\u00010\r8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00b5\u0002\u0010\u00b6\u0002R\u0019\u0010\u00b7\u0002\u001a\u00020\u00118\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00b7\u0002\u0010\u00b8\u0002R\u0019\u0010\u00b9\u0002\u001a\u00020z8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00b9\u0002\u0010\u00ba\u0002R\u0019\u0010\u00bb\u0002\u001a\u00020z8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00bb\u0002\u0010\u00ba\u0002R\u0019\u0010\u00bc\u0002\u001a\u00020\u00118\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00bc\u0002\u0010\u00b8\u0002R\u001c\u0010\u00bd\u0002\u001a\u0005\u0018\u00010\u008d\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00bd\u0002\u0010\u00be\u0002R\u0019\u0010\u00bf\u0002\u001a\u00020z8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00bf\u0002\u0010\u00ba\u0002R\u0019\u0010\u00c0\u0002\u001a\u00020z8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00c0\u0002\u0010\u00ba\u0002R\u0019\u0010\u00c1\u0002\u001a\u00020z8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00c1\u0002\u0010\u00ba\u0002R\u0019\u0010\u00c2\u0002\u001a\u00020z8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00c2\u0002\u0010\u00ba\u0002R\u001b\u0010\u00c3\u0002\u001a\u0004\u0018\u00010N8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00c3\u0002\u0010\u00a3\u0002R\u0019\u0010\u00c4\u0002\u001a\u00020z8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00c4\u0002\u0010\u00ba\u0002R\u001d\u0010\u00c5\u0002\u001a\b\u0012\u0004\u0012\u00020N0\u00048\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00c5\u0002\u0010\u00b4\u0002R\u0017\u0010\u00c6\u0002\u001a\u00020N8\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00c6\u0002\u0010\u00a3\u0002R\u0017\u0010\u00c7\u0002\u001a\u00020N8\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00c7\u0002\u0010\u00a3\u0002R\u001d\u0010\u00c8\u0002\u001a\b\u0012\u0004\u0012\u00020\r0\u00048\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00c8\u0002\u0010\u00b4\u0002R5\u0010\u00cb\u0002\u001a \u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\r0\u00c9\u0002j\u000f\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\r`\u00ca\u00028\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00cb\u0002\u0010\u00cc\u0002R5\u0010\u00cd\u0002\u001a \u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\r0\u00c9\u0002j\u000f\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\r`\u00ca\u00028\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00cd\u0002\u0010\u00cc\u0002R$\u0010\u00ce\u0002\u001a\u000f\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\r0\u00db\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00ce\u0002\u0010\u00cf\u0002R\u001d\u0010\u00d0\u0002\u001a\b\u0012\u0004\u0012\u00020\r0\u00048\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00d0\u0002\u0010\u00b4\u0002R\u001d\u0010\u00d1\u0002\u001a\b\u0012\u0004\u0012\u00020\r0\u00048\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00d1\u0002\u0010\u00b4\u0002R\u001d\u0010\u00d2\u0002\u001a\b\u0012\u0004\u0012\u00020\r0\u00048\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00d2\u0002\u0010\u00b4\u0002R\u001d\u0010\u00d3\u0002\u001a\b\u0012\u0004\u0012\u00020\r0\u00048\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00d3\u0002\u0010\u00b4\u0002R$\u0010\u00d4\u0002\u001a\u000f\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020z0\u00db\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00d4\u0002\u0010\u00cf\u0002R5\u0010\u00d5\u0002\u001a \u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\r0\u00c9\u0002j\u000f\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\r`\u00ca\u00028\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00d5\u0002\u0010\u00cc\u0002RA\u0010\u00d6\u0002\u001a,\u0012\u0004\u0012\u00020\n\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\u00040\u00c9\u0002j\u0015\u0012\u0004\u0012\u00020\n\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\u0004`\u00ca\u00028\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00d6\u0002\u0010\u00cc\u0002R\u001d\u0010\u00d7\u0002\u001a\b\u0012\u0004\u0012\u00020\r0\u00048\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00d7\u0002\u0010\u00b4\u0002R\u0014\u0010\u00d9\u0002\u001a\u00020\r8F\u00a2\u0006\b\u001a\u0006\b\u00d8\u0002\u0010\u00fc\u0001R\u0014\u0010\u00db\u0002\u001a\u00020\r8F\u00a2\u0006\b\u001a\u0006\b\u00da\u0002\u0010\u00fc\u0001R\u0014\u0010\u00dd\u0002\u001a\u00020\r8F\u00a2\u0006\b\u001a\u0006\b\u00dc\u0002\u0010\u00fc\u0001R\u0014\u0010\u00df\u0002\u001a\u00020\r8F\u00a2\u0006\b\u001a\u0006\b\u00de\u0002\u0010\u00fc\u0001R\u0016\u0010\u00e1\u0002\u001a\u0004\u0018\u00010\r8F\u00a2\u0006\b\u001a\u0006\b\u00e0\u0002\u0010\u00fc\u0001R\u0014\u0010\u00e3\u0002\u001a\u00020\r8F\u00a2\u0006\b\u001a\u0006\b\u00e2\u0002\u0010\u00fc\u0001R\u0014\u0010\u00e4\u0002\u001a\u00020\u00118F\u00a2\u0006\b\u001a\u0006\b\u00e4\u0002\u0010\u0095\u0001\u00a8\u0006\u00ee\u0002"}, d2={"Lorg/cobalt/api/hud/modules/CommissionMacroModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "", "Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionHudRow;", "getCommissionRows", "()Ljava/util/List;", "Lorg/cobalt/internal/mining/RoutesModule$SavedRouteInfo;", "getAvailableRouteInfos", "Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionRouteZone;", "getRouteZones", "zone", "", "getAssignedRouteName", "(Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionRouteZone;)Ljava/lang/String;", "name", "", "isRouteAssigned", "(Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionRouteZone;Ljava/lang/String;)Z", "", "assignRoute", "(Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionRouteZone;Ljava/lang/String;)V", "clearRouteAssignments", "getSelectedRouteNames", "isRouteSelected", "(Ljava/lang/String;)Z", "toggleRouteSelection", "(Ljava/lang/String;)V", "selected", "setRouteSelected", "(Ljava/lang/String;Z)V", "clearRouteSelections", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "Lorg/cobalt/api/event/impl/client/ChatEvent$Receive;", "onChat", "(Lorg/cobalt/api/event/impl/client/ChatEvent$Receive;)V", "Lnet/minecraft/class_1657;", "player", "handleOpenPigeon", "(Lnet/minecraft/class_1657;)V", "handleWarpToDwarves", "handleWarpToForgeEmissary", "handleWalkToEmissary", "handleOpenEmissary", "handleWarpToRouteStart", "handleReadGui", "handleClaimGui", "handleMining", "handleReturnToDwarves", "Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionMode;", "refreshMode", "(Lnet/minecraft/class_1657;)Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionMode;", "mode", "ensureCommissionSourceAvailable", "(Lnet/minecraft/class_1657;Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionMode;)Z", "transitionToLoopStart", "(Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionMode;)V", "transitionToTurnIn", "Lorg/cobalt/api/hud/modules/CommissionMacroModule$Commission;", "c", "isGlaciteWalkerCommission", "(Lorg/cobalt/api/hud/modules/CommissionMacroModule$Commission;)Z", "isGoblinSlayerCommission", "combatAutomationTarget", "(Lorg/cobalt/api/hud/modules/CommissionMacroModule$Commission;)Ljava/lang/String;", "startCombatWorkModule", "(Lorg/cobalt/api/hud/modules/CommissionMacroModule$Commission;)V", "handleGlaciteWalkerCombatStartup", "(Lnet/minecraft/class_1657;Lorg/cobalt/api/hud/modules/CommissionMacroModule$Commission;)Z", "handleGoblinSlayerCombatStartup", "equipDrillForGlaciteWalkers", "hasNearbyGlaciteWalker", "(Lnet/minecraft/class_1657;)Z", "hasNearbyGoblin", "Lnet/minecraft/class_2338;", "target", "tickCommissionNavigation", "(Lnet/minecraft/class_1657;Lnet/minecraft/class_2338;)V", "alreadyAtSource", "allowDwarvenWarp", "allowForgeWarp", "transitionToCommissionSource", "(Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionMode;ZZZ)V", "shouldWarpToDwarvesForEmissary", "(Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionMode;Z)Z", "shouldWarpToForgeForEmissary", "Lorg/cobalt/api/hud/modules/CommissionMacroModule$RouteWarpDestination;", "getEmissaryWarpDestination", "(Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionMode;)Lorg/cobalt/api/hud/modules/CommissionMacroModule$RouteWarpDestination;", "configuredEmissaryDisplayName", "(Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionMode;)Ljava/lang/String;", "getRouteWarpDestination", "()Lorg/cobalt/api/hud/modules/CommissionMacroModule$RouteWarpDestination;", "getElizaTargetPos", "()Lnet/minecraft/class_2338;", "disableForMissingEmissaryTarget", "Lorg/cobalt/api/hud/modules/CommissionMacroModule$EmissaryTarget;", "resolveEmissaryTarget", "(Lnet/minecraft/class_1657;Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionMode;)Lorg/cobalt/api/hud/modules/CommissionMacroModule$EmissaryTarget;", "shouldUseForgeEmissaryRoute", "(Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionMode;)Z", "resolveEmissaryNavigationTarget", "(Lnet/minecraft/class_1657;Lorg/cobalt/api/hud/modules/CommissionMacroModule$EmissaryTarget;)Lnet/minecraft/class_2338;", "nodes", "resolveNextEmissaryWalkNode", "(Lnet/minecraft/class_1657;Ljava/util/List;)Lnet/minecraft/class_2338;", "targetPos", "Lnet/minecraft/class_1297;", "findEmissaryInteractionEntity", "(Lnet/minecraft/class_2338;)Lnet/minecraft/class_1297;", "findNearestLoadedEmissaryInteractionEntity", "(Lnet/minecraft/class_1657;)Lnet/minecraft/class_1297;", "entity", "entityNameMatchesEliza", "(Lnet/minecraft/class_1297;)Z", "entityNameMatchesAnyEmissary", "formatEmissaryLabel", "(Lnet/minecraft/class_1297;)Ljava/lang/String;", "", "findAotvHotbarSlot", "(Lnet/minecraft/class_1657;)I", "findPigeonHotbarSlot", "slot", "pressHotbarSlot", "(I)V", "raw", "normalizeName", "(Ljava/lang/String;)Ljava/lang/String;", "base", "findWalkTargetNear", "(Lnet/minecraft/class_2338;)Lnet/minecraft/class_2338;", "faceEntity", "(Lnet/minecraft/class_1297;)V", "applyForgeWarpPigeonLook", "ensureSafeRoyalPigeonLook", "findNearestLoadedFredInteractionEntity", "entityNameMatchesFred", "", "targetYaw", "targetPitch", "maxYawStep", "maxPitchStep", "applyTickRotation", "(Lnet/minecraft/class_1657;FFFF)V", "nativePathActive", "()Z", "startEmissaryNavigation", "(Lnet/minecraft/class_2338;)V", "ensureEmissaryWalkMovement", "applyEmissaryWalkFallbackMovement", "applyEmissaryWalkCameraRotation", "Lnet/minecraft/class_243;", "resolveEmissaryWalkLookPoint", "(Lnet/minecraft/class_1657;)Lnet/minecraft/class_243;", "nearestEmissaryWalkNodeIndex", "(Lnet/minecraft/class_1657;Ljava/util/List;)I", "stopEmissaryNavigation", "syncEmissaryWalkKeys", "forward", "backward", "left", "right", "jump", "shift", "sprint", "setEmissaryWalkKeys", "(ZZZZZZZ)V", "Lnet/minecraft/class_465;", "screen", "Lorg/cobalt/api/hud/modules/CommissionMacroModule$ParsedCommissionSelection;", "parseCommissionSelectionFromGui", "(Lnet/minecraft/class_465;)Lorg/cobalt/api/hud/modules/CommissionMacroModule$ParsedCommissionSelection;", "findClaimSlot", "(Lnet/minecraft/class_465;)I", "lines", "combined", "isClaimableCommissionSlot", "(Ljava/util/List;Ljava/lang/String;)Z", "Lnet/minecraft/class_1735;", "getCommissionCandidateSlots", "(Lnet/minecraft/class_465;)Ljava/util/List;", "Lnet/minecraft/class_1799;", "item", "buildGuiTextLines", "(Lnet/minecraft/class_1799;)Ljava/util/List;", "miningTarget", "combatTarget", "buildCommissionLabel", "(Lnet/minecraft/class_1799;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", "findKnownCommissionLabel", "(Ljava/util/List;)Ljava/lang/String;", "buildMiningCommissionLabel", "(Ljava/util/List;Ljava/lang/String;)Ljava/lang/String;", "buildCombatCommissionLabel", "canonicalizeKnownCommissionLabel", "extractMiningAreaLabel", "canonicalizeMiningArea", "miningCommissionSuffix", "commission", "commissionSelectionRank", "(Lorg/cobalt/api/hud/modules/CommissionMacroModule$Commission;)I", "value", "titleCase", "normalizeComparisonText", "looksLikeCommissionEntry", "isClaimCommissionText", "Lkotlin/Pair;", "parseCommissionProgress", "(Ljava/lang/String;)Lkotlin/Pair;", "extractMiningKeyword", "extractCombatCommissionTarget", "startWorkModule", "stopWorkModule", "workModuleIsActive", "findMatchingRouteName", "", "availableRoutesByName", "resolveSelectableRoutes", "(Lorg/cobalt/api/hud/modules/CommissionMacroModule$Commission;Ljava/util/Map;)Ljava/util/List;", "shouldUseRandomAssignedCommissionRoute", "routes", "selectRouteByCommissionName", "(Ljava/util/List;Lorg/cobalt/api/hud/modules/CommissionMacroModule$Commission;)Ljava/lang/String;", "routeName", "scoreRouteName", "(Ljava/lang/String;Lorg/cobalt/api/hud/modules/CommissionMacroModule$Commission;)I", "", "preferredTypes", "selectRouteByType", "(Ljava/util/List;Ljava/util/Set;)Ljava/lang/String;", "requiredTypes", "preferredRouteMineTypes", "(Lorg/cobalt/api/hud/modules/CommissionMacroModule$Commission;Ljava/util/Set;)Ljava/util/Set;", "preferredMiningMacroMineTypes", "label", "extractCommissionAreaKey", "resolveCommissionRouteZone", "(Ljava/lang/String;)Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionRouteZone;", "availableRoutes", "resolveAssignedRouteName", "(Lorg/cobalt/api/hud/modules/CommissionMacroModule$Commission;Ljava/util/List;)Ljava/lang/String;", "routeMatchesZone", "(Ljava/lang/String;Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionRouteZone;)Z", "routesByName", "findRouteInfoByName", "(Ljava/util/Map;Ljava/lang/String;)Lorg/cobalt/internal/mining/RoutesModule$SavedRouteInfo;", "slotKeyForZone", "detectAreaFromTabList", "()Ljava/lang/String;", "", "connection", "resolveTabEntries", "(Ljava/lang/Object;)Ljava/util/List;", "entry", "resolveEntryDisplayName", "(Ljava/lang/Object;)Ljava/lang/String;", "coerceText", "Lorg/cobalt/api/hud/modules/CommissionMacroModule$State;", "newState", "transition", "(Lorg/cobalt/api/hud/modules/CommissionMacroModule$State;)V", "msg", "setStatus", "resetCommissionState", "resetMacro", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lorg/cobalt/api/rotation/strategy/TrackingRotationStrategy;", "emissaryWalkRotStrategy", "Lorg/cobalt/api/rotation/strategy/TrackingRotationStrategy;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "info", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "routeWarpSetting", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "statusText", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "modeText", "commText", "areaText", "elizaTargetPos", "Lnet/minecraft/class_2338;", "Lorg/cobalt/internal/routes/RoutePickerSetting;", "royalRoutePicker", "Lorg/cobalt/internal/routes/RoutePickerSetting;", "cliffsideRoutePicker", "lavaRoutePicker", "rampRoutePicker", "upperRoutePicker", "state", "Lorg/cobalt/api/hud/modules/CommissionMacroModule$State;", "", "stateTick", "J", "currentMode", "Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionMode;", "Lorg/cobalt/api/hud/modules/CommissionMacroModule$Commission;", "activeCommissions", "Ljava/util/List;", "activeMiningRouteName", "Ljava/lang/String;", "pendingUseRelease", "Z", "pendingSlotRestore", "I", "pigeonCooldownTicks", "pendingForgeWarpPigeonLook", "forgeWarpPigeonTargetYaw", "Ljava/lang/Float;", "forgeWarpPigeonLookStableTicks", "openAttempts", "claimAttempts", "readAttempts", "lastEmissaryPathTarget", "activeEmissaryWalkNodeIndex", "forgeEmissaryWalkNodes", "greatIceWallGatePos", "goblinBurrowsCenterPos", "AREA_NAMES", "Ljava/util/LinkedHashMap;", "Lkotlin/collections/LinkedHashMap;", "ORE_TO_TYPE", "Ljava/util/LinkedHashMap;", "ORE_TO_MINE_TYPES", "ORE_TO_ZONE", "Ljava/util/Map;", "COMMISSION_PRIORITY", "MINING_OBJECTIVE_PREFIXES", "COMBAT_OBJECTIVE_PREFIXES", "COMMISSION_OBJECTIVE_PREFIXES", "COMMISSION_PRIORITY_INDEX", "MINING_AREA_ALIASES", "COMMISSION_ROUTE_ZONE_ALIASES", "GLACITE_WALKER_NAME_KEYWORDS", "getStatusDisplay", "statusDisplay", "getModeDisplay", "modeDisplay", "getCommissionDisplay", "commissionDisplay", "getCurrentZoneDisplay", "currentZoneDisplay", "getTargetedCommissionName", "targetedCommissionName", "getTargetZoneDisplay", "targetZoneDisplay", "isRunning", "CommissionType", "Commission", "CommissionHudRow", "ParsedCommissionSelection", "CommissionMode", "CommissionRouteZone", "EmissaryTarget", "State", "RouteWarpDestination", "cobalt"})
@SourceDebugExtension(value={"SMAP\nCommissionMacroModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CommissionMacroModule.kt\norg/cobalt/api/hud/modules/CommissionMacroModule\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n+ 5 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 6 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,2196:1\n1586#2:2197\n1661#2,3:2198\n1915#2,2:2202\n1924#2,3:2204\n1807#2,3:2207\n1807#2,3:2212\n2469#2,14:2219\n1696#2,8:2235\n832#2:2243\n862#2,2:2244\n1642#2,10:2246\n1915#2:2256\n1916#2:2258\n1652#2:2259\n777#2:2260\n873#2,2:2261\n1807#2,3:2263\n296#2:2266\n1807#2,3:2267\n297#2:2270\n296#2,2:2271\n296#2,2:2273\n296#2,2:2275\n777#2:2277\n873#2,2:2278\n1807#2,3:2280\n2792#2,3:2283\n1220#2,2:2286\n1249#2,4:2288\n1586#2:2292\n1661#2,3:2293\n777#2:2296\n873#2,2:2297\n1586#2:2299\n1661#2,3:2300\n777#2:2303\n873#2,2:2304\n777#2:2306\n873#2:2307\n1807#2,3:2308\n874#2:2311\n1642#2,10:2312\n1915#2:2322\n1916#2:2324\n1652#2:2325\n1696#2,8:2326\n1642#2,10:2334\n1915#2:2344\n1916#2:2346\n1652#2:2347\n1642#2,10:2348\n1915#2:2358\n1916#2:2360\n1652#2:2361\n1696#2,8:2362\n1586#2:2370\n1661#2,3:2371\n777#2:2374\n873#2,2:2375\n2045#2,14:2377\n296#2:2391\n1807#2,3:2392\n297#2:2395\n296#2,2:2396\n296#2:2398\n1807#2,3:2399\n297#2:2402\n1220#2,2:2403\n1249#2,4:2405\n1807#2,3:2409\n296#2,2:2412\n1642#2,10:2414\n1915#2:2424\n1916#2:2426\n1652#2:2427\n1586#2:2428\n1661#2,3:2429\n777#2:2432\n873#2,2:2433\n1205#2,2:2449\n1282#2,4:2451\n1#3:2201\n1#3:2257\n1#3:2323\n1#3:2345\n1#3:2359\n1#3:2425\n479#4:2210\n1276#4:2211\n1277#4:2215\n479#4:2216\n1276#4,2:2217\n614#4:2233\n614#4:2234\n1401#5,2:2435\n1401#5,2:2437\n1401#5,2:2439\n1401#5,2:2441\n1401#5,2:2443\n37#6,2:2445\n37#6,2:2447\n*S KotlinDebug\n*F\n+ 1 CommissionMacroModule.kt\norg/cobalt/api/hud/modules/CommissionMacroModule\n*L\n385#1:2197\n385#1:2198,3\n418#1:2202,2\n773#1:2204,3\n964#1:2207,3\n1059#1:2212,3\n1238#1:2219,14\n1611#1:2235,8\n1654#1:2243\n1654#1:2244,2\n1663#1:2246,10\n1663#1:2256\n1663#1:2258\n1663#1:2259\n1664#1:2260\n1664#1:2261,2\n1707#1:2263,3\n1710#1:2266\n1711#1:2267,3\n1710#1:2270\n1723#1:2271,2\n1727#1:2273,2\n1740#1:2275,2\n1774#1:2277\n1774#1:2278,2\n1798#1:2280,3\n1838#1:2283,3\n1918#1:2286,2\n1918#1:2288,4\n1924#1:2292\n1924#1:2293,3\n1925#1:2296\n1925#1:2297,2\n1926#1:2299\n1926#1:2300,3\n1931#1:2303\n1931#1:2304,2\n1936#1:2306\n1936#1:2307\n1936#1:2308,3\n1936#1:2311\n1971#1:2312,10\n1971#1:2322\n1971#1:2324\n1971#1:2325\n1972#1:2326,8\n1976#1:2334,10\n1976#1:2344\n1976#1:2346\n1976#1:2347\n1977#1:2348,10\n1977#1:2358\n1977#1:2360\n1977#1:2361\n1978#1:2362,8\n1996#1:2370\n1996#1:2371,3\n1997#1:2374\n1997#1:2375,2\n1998#1:2377,14\n2036#1:2391\n2036#1:2392,3\n2036#1:2395\n2061#1:2396,2\n2066#1:2398\n2067#1:2399,3\n2066#1:2402\n2078#1:2403,2\n2078#1:2405,4\n2083#1:2409,3\n2091#1:2412,2\n2110#1:2414,10\n2110#1:2424\n2110#1:2426\n2110#1:2427\n2111#1:2428\n2111#1:2429,3\n2112#1:2432\n2112#1:2433,2\n357#1:2449,2\n357#1:2451,4\n1663#1:2257\n1971#1:2323\n1976#1:2345\n1977#1:2359\n2110#1:2425\n1055#1:2210\n1057#1:2211\n1057#1:2215\n1068#1:2216\n1070#1:2217,2\n1277#1:2233\n1414#1:2234\n2124#1:2435,2\n2136#1:2437,2\n2141#1:2439,2\n2143#1:2441,2\n2153#1:2443,2\n345#1:2445,2\n346#1:2447,2\n*E\n"})
public final class CommissionMacroModule
extends Module {
    @NotNull
    public static final CommissionMacroModule INSTANCE;
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final TrackingRotationStrategy emissaryWalkRotStrategy;
    @NotNull
    private static final CheckboxSetting enabled;
    @NotNull
    private static final InfoSetting info;
    @NotNull
    private static final ModeSetting routeWarpSetting;
    @NotNull
    private static final TextSetting statusText;
    @NotNull
    private static final TextSetting modeText;
    @NotNull
    private static final TextSetting commText;
    @NotNull
    private static final TextSetting areaText;
    @NotNull
    private static final class_2338 elizaTargetPos;
    @NotNull
    private static final RoutePickerSetting royalRoutePicker;
    @NotNull
    private static final RoutePickerSetting cliffsideRoutePicker;
    @NotNull
    private static final RoutePickerSetting lavaRoutePicker;
    @NotNull
    private static final RoutePickerSetting rampRoutePicker;
    @NotNull
    private static final RoutePickerSetting upperRoutePicker;
    @NotNull
    private static State state;
    private static long stateTick;
    @NotNull
    private static CommissionMode currentMode;
    @Nullable
    private static Commission commission;
    @NotNull
    private static List<Commission> activeCommissions;
    @Nullable
    private static String activeMiningRouteName;
    private static boolean pendingUseRelease;
    private static int pendingSlotRestore;
    private static int pigeonCooldownTicks;
    private static boolean pendingForgeWarpPigeonLook;
    @Nullable
    private static Float forgeWarpPigeonTargetYaw;
    private static int forgeWarpPigeonLookStableTicks;
    private static int openAttempts;
    private static int claimAttempts;
    private static int readAttempts;
    @Nullable
    private static class_2338 lastEmissaryPathTarget;
    private static int activeEmissaryWalkNodeIndex;
    @NotNull
    private static final List<class_2338> forgeEmissaryWalkNodes;
    @NotNull
    private static final class_2338 greatIceWallGatePos;
    @NotNull
    private static final class_2338 goblinBurrowsCenterPos;
    @NotNull
    private static final List<String> AREA_NAMES;
    @NotNull
    private static final LinkedHashMap<String, String> ORE_TO_TYPE;
    @NotNull
    private static final LinkedHashMap<String, String> ORE_TO_MINE_TYPES;
    @NotNull
    private static final Map<String, String> ORE_TO_ZONE;
    @NotNull
    private static final List<String> COMMISSION_PRIORITY;
    @NotNull
    private static final List<String> MINING_OBJECTIVE_PREFIXES;
    @NotNull
    private static final List<String> COMBAT_OBJECTIVE_PREFIXES;
    @NotNull
    private static final List<String> COMMISSION_OBJECTIVE_PREFIXES;
    @NotNull
    private static final Map<String, Integer> COMMISSION_PRIORITY_INDEX;
    @NotNull
    private static final LinkedHashMap<String, String> MINING_AREA_ALIASES;
    @NotNull
    private static final LinkedHashMap<CommissionRouteZone, List<String>> COMMISSION_ROUTE_ZONE_ALIASES;
    @NotNull
    private static final List<String> GLACITE_WALKER_NAME_KEYWORDS;

    private CommissionMacroModule() {
        super("Commission Macro");
    }

    @NotNull
    public final String getStatusDisplay() {
        return (String)statusText.getValue();
    }

    @NotNull
    public final String getModeDisplay() {
        return (String)modeText.getValue();
    }

    @NotNull
    public final String getCommissionDisplay() {
        return (String)commText.getValue();
    }

    @NotNull
    public final String getCurrentZoneDisplay() {
        return (String)areaText.getValue();
    }

    @Nullable
    public final String getTargetedCommissionName() {
        Commission commission = CommissionMacroModule.commission;
        return commission != null ? commission.getLabel() : null;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final List<CommissionHudRow> getCommissionRows() {
        void $this$mapTo$iv$iv;
        Iterable $this$map$iv = activeCommissions;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void active;
            Commission commission = (Commission)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            int percent = active.getMax() > 0 ? RangesKt.coerceIn((int)((int)((double)active.getCurrent() / (double)active.getMax() * 100.0)), (int)0, (int)100) : 0;
            collection.add(new CommissionHudRow(active.getLabel(), percent + "%", Intrinsics.areEqual((Object)active, (Object)CommissionMacroModule.commission), percent));
        }
        return (List)destination$iv$iv;
    }

    @NotNull
    public final String getTargetZoneDisplay() {
        String string;
        Commission commission = CommissionMacroModule.commission;
        if (commission == null) {
            return "Unknown";
        }
        Commission c = commission;
        if (c.getType() == CommissionType.MINING) {
            string = ORE_TO_ZONE.get(c.getTarget());
            if (string == null) {
                string = "Unknown";
            }
        } else {
            string = "Combat Zone";
        }
        return string;
    }

    public final boolean isRunning() {
        return (Boolean)enabled.getValue();
    }

    @NotNull
    public final List<RoutesModule.SavedRouteInfo> getAvailableRouteInfos() {
        return RoutesModule.INSTANCE.getSavedRouteInfos();
    }

    @NotNull
    public final List<CommissionRouteZone> getRouteZones() {
        return ArraysKt.toList((Object[])CommissionRouteZone.values());
    }

    @Nullable
    public final String getAssignedRouteName(@NotNull CommissionRouteZone zone) {
        Intrinsics.checkNotNullParameter((Object)((Object)zone), (String)"zone");
        return RouteStore.INSTANCE.getSlotRoute(this.slotKeyForZone(zone));
    }

    public final boolean isRouteAssigned(@NotNull CommissionRouteZone zone, @NotNull String name) {
        Intrinsics.checkNotNullParameter((Object)((Object)zone), (String)"zone");
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        String string = this.getAssignedRouteName(zone);
        return string != null ? StringsKt.equals((String)string, (String)((Object)StringsKt.trim((CharSequence)name)).toString(), (boolean)false) : false;
    }

    /*
     * WARNING - void declaration
     */
    public final void assignRoute(@NotNull CommissionRouteZone zone, @Nullable String name) {
        String string;
        Intrinsics.checkNotNullParameter((Object)((Object)zone), (String)"zone");
        RouteStore routeStore = RouteStore.INSTANCE;
        String string2 = this.slotKeyForZone(zone);
        String string3 = name;
        if (string3 != null && (string3 = ((Object)StringsKt.trim((CharSequence)string3)).toString()) != null) {
            void it;
            String string4;
            String string5 = string4 = string3;
            String string6 = string2;
            RouteStore routeStore2 = routeStore;
            boolean bl = false;
            boolean bl2 = ((CharSequence)it).length() > 0;
            routeStore = routeStore2;
            string2 = string6;
            string = bl2 ? string4 : null;
        } else {
            string = null;
        }
        routeStore.setSlotRoute(string2, string);
    }

    public final void clearRouteAssignments() {
        Iterable $this$forEach$iv = this.getRouteZones();
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            CommissionRouteZone zone = (CommissionRouteZone)((Object)element$iv);
            boolean bl = false;
            RouteStore.INSTANCE.clearSlotRoute(INSTANCE.slotKeyForZone(zone));
        }
    }

    @NotNull
    public final List<String> getSelectedRouteNames() {
        return CollectionsKt.emptyList();
    }

    public final boolean isRouteSelected(@NotNull String name) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        return false;
    }

    public final void toggleRouteSelection(@NotNull String name) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
    }

    public final void setRouteSelected(@NotNull String name, boolean selected) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
    }

    public final void clearRouteSelections() {
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (pendingUseRelease) {
            class_304 class_3042 = CommissionMacroModule.mc.field_1690.field_1904;
            if (class_3042 != null) {
                class_3042.method_23481(false);
            }
            pendingUseRelease = false;
        }
        if (pendingSlotRestore >= 0) {
            this.pressHotbarSlot(pendingSlotRestore);
            pendingSlotRestore = -1;
        }
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            if (state != State.IDLE) {
                this.resetMacro();
            }
            return;
        }
        class_746 class_7462 = CommissionMacroModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        if (pigeonCooldownTicks > 0) {
            int n = pigeonCooldownTicks;
            pigeonCooldownTicks = n + -1;
        }
        long l = stateTick;
        stateTick = l + 1L;
        switch (WhenMappings.$EnumSwitchMapping$0[state.ordinal()]) {
            case 1: {
                CommissionMode mode = this.refreshMode((class_1657)player);
                if (!this.ensureCommissionSourceAvailable((class_1657)player, mode)) {
                    return;
                }
                ChatUtils.sendMessage("Commission Macro: starting in " + mode.getLabel() + " mode.");
                this.transitionToLoopStart(mode);
                break;
            }
            case 2: {
                this.handleOpenPigeon((class_1657)player);
                break;
            }
            case 3: {
                this.handleWarpToDwarves();
                break;
            }
            case 4: {
                this.handleWarpToForgeEmissary();
                break;
            }
            case 5: {
                this.handleWalkToEmissary((class_1657)player);
                break;
            }
            case 6: {
                this.handleOpenEmissary((class_1657)player);
                break;
            }
            case 7: {
                this.handleReadGui();
                break;
            }
            case 8: {
                this.handleClaimGui();
                break;
            }
            case 9: {
                this.handleWarpToRouteStart();
                break;
            }
            case 10: {
                this.handleMining();
                break;
            }
            case 11: {
                this.handleReturnToDwarves();
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
    }

    @SubscribeEvent
    public final void onChat(@NotNull ChatEvent.Receive event) {
        String string;
        block11: {
            block10: {
                Intrinsics.checkNotNullParameter((Object)event, (String)"event");
                if (!((Boolean)enabled.getValue()).booleanValue()) {
                    return;
                }
                string = event.getMessage();
                if (string == null) break block10;
                String string2 = string.toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
                string = string2;
                if (string2 != null) break block11;
            }
            return;
        }
        String msg = string;
        Object object = Regex.find$default((Regex)new Regex("this ability is on cooldown for\\s+([0-9]+)s"), (CharSequence)msg, (int)0, (int)2, null);
        if (object != null && (object = object.getGroupValues()) != null && (object = (String)CollectionsKt.getOrNull((List)object, (int)1)) != null && (object = StringsKt.toIntOrNull((String)object)) != null) {
            int seconds = ((Number)object).intValue();
            boolean bl = false;
            if (state == State.OPEN_PIGEON) {
                pigeonCooldownTicks = Math.max(pigeonCooldownTicks, seconds * 20 + 10);
                openAttempts = Math.max(0, openAttempts - 1);
                INSTANCE.setStatus("Waiting for Royal Pigeon cooldown... (" + seconds + "s)");
            }
        }
        if (state == State.MINING) {
            boolean isComplete;
            boolean bl = isComplete = StringsKt.contains$default((CharSequence)msg, (CharSequence)"commission complete", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)msg, (CharSequence)"completed a commission", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)msg, (CharSequence)"you've completed", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)msg, (CharSequence)"commission completed", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)msg, (CharSequence)"commission", (boolean)false, (int)2, null) && StringsKt.contains$default((CharSequence)msg, (CharSequence)"complete", (boolean)false, (int)2, null);
            if (isComplete) {
                class_746 player = CommissionMacroModule.mc.field_1724;
                CommissionMode mode = this.refreshMode((class_1657)player);
                String destination = mode.getUsesPigeon() ? "Royal Pigeon" : (this.shouldUseForgeEmissaryRoute(mode) ? "emissary" : (mode.getUsesWarps() ? "Emissary Eliza" : "emissary"));
                this.setStatus("Commission complete! Returning to " + destination + "...");
                ChatUtils.sendMessage("Commission Macro: commission complete, returning to " + destination + ".");
                this.stopWorkModule();
                this.transitionToTurnIn(mode);
            }
        }
    }

    private final void handleOpenPigeon(class_1657 player) {
        CommissionMode mode = this.refreshMode(player);
        if (!mode.getUsesPigeon()) {
            CommissionMacroModule.transitionToCommissionSource$default(this, mode, false, false, false, 12, null);
            return;
        }
        if (CommissionMacroModule.mc.field_1755 instanceof class_465) {
            openAttempts = 0;
            pendingForgeWarpPigeonLook = false;
            forgeWarpPigeonTargetYaw = null;
            forgeWarpPigeonLookStableTicks = 0;
            this.transition(State.READ_GUI);
            return;
        }
        if (pendingForgeWarpPigeonLook) {
            this.applyForgeWarpPigeonLook(player);
        }
        if (this.ensureSafeRoyalPigeonLook(player)) {
            this.setStatus("Aiming Royal Pigeon away from Fred...");
            return;
        }
        if (pigeonCooldownTicks > 0) {
            this.setStatus("Waiting for Royal Pigeon cooldown... (" + (pigeonCooldownTicks + 19) / 20 + "s)");
            return;
        }
        if (stateTick % 10L != 0L) {
            return;
        }
        int pigeonSlot = this.findPigeonHotbarSlot(player);
        if (!(0 <= pigeonSlot ? pigeonSlot < 9 : false)) {
            CommissionMacroModule.transitionToCommissionSource$default(this, mode, false, false, false, 12, null);
            return;
        }
        if (openAttempts >= 6) {
            ChatUtils.sendMessage("Commission Macro: could not open Royal Pigeon GUI. Disabling.");
            enabled.setValue(false);
            return;
        }
        int restoreSlot = player.method_31548().method_67532();
        boolean bl = 0 <= restoreSlot ? restoreSlot < 9 : false;
        if (bl && restoreSlot != pigeonSlot) {
            pendingSlotRestore = restoreSlot;
        }
        this.pressHotbarSlot(pigeonSlot);
        class_304 class_3042 = CommissionMacroModule.mc.field_1690.field_1904;
        if (class_3042 != null) {
            class_3042.method_23481(false);
        }
        class_304 class_3043 = CommissionMacroModule.mc.field_1690.field_1904;
        if (class_3043 != null) {
            class_3043.method_23481(true);
        }
        pendingUseRelease = true;
        int n = openAttempts;
        openAttempts = n + 1;
        this.setStatus("Opening Royal Pigeon... (attempt " + openAttempts + ")");
    }

    private final void handleWarpToDwarves() {
        CommissionMode mode = this.refreshMode((class_1657)CommissionMacroModule.mc.field_1724);
        if (stateTick == 1L) {
            class_746 class_7462 = CommissionMacroModule.mc.field_1724;
            Object object = class_7462 instanceof class_746 ? class_7462 : null;
            if (object != null && (object = object.field_3944) != null) {
                object.method_45730("warp dwarves");
            }
            areaText.setValue("Dwarven Mines");
            this.setStatus("Warping to dwarves...");
        }
        if (stateTick >= 100L) {
            this.transitionToCommissionSource(mode, false, false, false);
        }
    }

    private final void handleWarpToForgeEmissary() {
        CommissionMode mode = this.refreshMode((class_1657)CommissionMacroModule.mc.field_1724);
        if (stateTick == 1L) {
            class_746 class_7462 = CommissionMacroModule.mc.field_1724;
            Object object = class_7462 instanceof class_746 ? class_7462 : null;
            if (object != null && (object = object.field_3944) != null) {
                object.method_45730("warp forge");
            }
            areaText.setValue("Forge");
            this.setStatus("Warping to forge emissary...");
        }
        if (stateTick >= 100L) {
            this.transitionToCommissionSource(mode, false, false, false);
        }
    }

    private final void handleWalkToEmissary(class_1657 player) {
        PathCommand cmd;
        if (CommissionMacroModule.mc.field_1755 instanceof class_465) {
            this.stopEmissaryNavigation();
            openAttempts = 0;
            this.transition(State.READ_GUI);
            return;
        }
        EmissaryTarget target = this.resolveEmissaryTarget(player, this.refreshMode(player));
        if (target == null) {
            CommissionMacroModule.disableForMissingEmissaryTarget$default(this, null, 1, null);
            return;
        }
        class_1297 emissary = target.getInteractionEntity();
        if (emissary != null && player.method_5858(emissary) <= 12.25) {
            this.stopEmissaryNavigation();
            this.transition(State.OPEN_EMISSARY);
            return;
        }
        class_2338 walkTarget = this.resolveEmissaryNavigationTarget(player, target);
        class_2338 pathTarget = this.findWalkTargetNear(walkTarget);
        if (Intrinsics.areEqual((Object)walkTarget, (Object)target.getWalkPos()) && player.method_24515().method_10262((class_2382)pathTarget) <= 2.25) {
            this.stopEmissaryNavigation();
            this.transition(State.OPEN_EMISSARY);
            return;
        }
        if (!Intrinsics.areEqual((Object)lastEmissaryPathTarget, (Object)pathTarget) || !this.nativePathActive() || stateTick % 40L == 1L) {
            this.startEmissaryNavigation(pathTarget);
        }
        if ((cmd = NativePathfinder.INSTANCE.tick()) != null) {
            cmd.applyToPlayer();
            this.applyEmissaryWalkCameraRotation(player);
            this.ensureEmissaryWalkMovement(player, pathTarget);
        } else if (lastEmissaryPathTarget != null) {
            this.applyEmissaryWalkCameraRotation(player);
            this.applyEmissaryWalkFallbackMovement(player, pathTarget);
        }
        this.syncEmissaryWalkKeys();
        this.setStatus("Walking to " + target.getLabel() + "...");
    }

    private final void handleOpenEmissary(class_1657 player) {
        if (CommissionMacroModule.mc.field_1755 instanceof class_465) {
            openAttempts = 0;
            this.transition(State.READ_GUI);
            return;
        }
        EmissaryTarget target = this.resolveEmissaryTarget(player, this.refreshMode(player));
        if (target == null) {
            CommissionMacroModule.disableForMissingEmissaryTarget$default(this, null, 1, null);
            return;
        }
        class_1297 emissary = target.getInteractionEntity();
        if (emissary == null) {
            if (player.method_24515().method_10262((class_2382)target.getWalkPos()) <= 25.0 && stateTick > 80L) {
                ChatUtils.sendMessage("Commission Macro: reached emissary target but could not find " + target.getLabel() + ". Disabling.");
                enabled.setValue(false);
                return;
            }
            this.transition(State.WALK_TO_EMISSARY);
            return;
        }
        if (player.method_5858(emissary) > 12.25) {
            this.transition(State.WALK_TO_EMISSARY);
            return;
        }
        this.stopEmissaryNavigation();
        this.faceEntity(emissary);
        if (stateTick == 1L) {
            this.setStatus("Facing " + target.getLabel() + "...");
            return;
        }
        if (stateTick % 4L != 0L) {
            return;
        }
        if (openAttempts >= 8) {
            ChatUtils.sendMessage("Commission Macro: could not open " + target.getLabel() + " after 8 attempts. Disabling.");
            enabled.setValue(false);
            return;
        }
        class_304 class_3042 = CommissionMacroModule.mc.field_1690.field_1904;
        if (class_3042 != null) {
            class_3042.method_23481(false);
        }
        class_304 class_3043 = CommissionMacroModule.mc.field_1690.field_1904;
        if (class_3043 != null) {
            class_3043.method_23481(true);
        }
        pendingUseRelease = true;
        int n = openAttempts;
        openAttempts = n + 1;
        this.setStatus("Opening " + target.getLabel() + "... (attempt " + openAttempts + ")");
    }

    private final void handleWarpToRouteStart() {
        class_746 player = CommissionMacroModule.mc.field_1724;
        CommissionMode mode = this.refreshMode((class_1657)player);
        if (stateTick == 1L) {
            class_746 class_7462 = CommissionMacroModule.mc.field_1724;
            Object object = class_7462 instanceof class_746 ? class_7462 : null;
            if (object != null && (object = object.field_3944) != null) {
                object.method_45730("warp forge");
            }
            areaText.setValue("Forge");
            if (mode.getUsesPigeon()) {
                pendingForgeWarpPigeonLook = true;
                forgeWarpPigeonTargetYaw = null;
                forgeWarpPigeonLookStableTicks = 0;
            }
            this.setStatus("Warping to forge for route...");
        }
        if (player != null && mode.getUsesPigeon() && pendingForgeWarpPigeonLook) {
            long l = stateTick;
            boolean bl = 15L <= l ? l < 96L : false;
            if (bl) {
                this.applyForgeWarpPigeonLook((class_1657)player);
            }
        }
        if (stateTick >= 100L) {
            pendingForgeWarpPigeonLook = false;
            forgeWarpPigeonTargetYaw = null;
            forgeWarpPigeonLookStableTicks = 0;
            this.transition(State.MINING);
        }
    }

    /*
     * WARNING - void declaration
     */
    private final void handleReadGui() {
        class_465 screen;
        class_746 player = CommissionMacroModule.mc.field_1724;
        CommissionMode mode = this.refreshMode((class_1657)player);
        class_437 class_4372 = CommissionMacroModule.mc.field_1755;
        class_465 class_4652 = screen = class_4372 instanceof class_465 ? (class_465)class_4372 : null;
        if (screen == null) {
            CommissionMacroModule.transitionToCommissionSource$default(this, mode, false, false, false, 12, null);
            return;
        }
        if (stateTick < 5L) {
            return;
        }
        if (stateTick % (long)5 != 0L) {
            return;
        }
        int claimSlot = this.findClaimSlot(screen);
        if (claimSlot >= 0) {
            this.setStatus("Found claimable commission - claiming...");
            this.transition(State.CLAIM_GUI);
            return;
        }
        ParsedCommissionSelection parsed = this.parseCommissionSelectionFromGui(screen);
        activeCommissions = parsed.getCommissions();
        Commission selected = parsed.getSelected();
        if (selected != null) {
            String string;
            commission = selected;
            TextSetting textSetting = commText;
            String string2 = selected.getType().name().toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
            String string3 = string2;
            if (((CharSequence)string3).length() > 0) {
                void it22;
                char c = string3.charAt(0);
                StringBuilder stringBuilder = new StringBuilder();
                TextSetting textSetting2 = textSetting;
                int n = 0;
                String string4 = String.valueOf((char)it22);
                Intrinsics.checkNotNull((Object)string4, (String)"null cannot be cast to non-null type java.lang.String");
                String string5 = string4.toUpperCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)string5, (String)"toUpperCase(...)");
                CharSequence charSequence = string5;
                textSetting = textSetting2;
                StringBuilder stringBuilder2 = stringBuilder.append((Object)charSequence);
                String it22 = string3;
                n = 1;
                String string6 = it22.substring(n);
                Intrinsics.checkNotNullExpressionValue((Object)string6, (String)"substring(...)");
                string = stringBuilder2.append(string6).toString();
            } else {
                string = string3;
            }
            textSetting.setValue(string + ": " + selected.getLabel() + " (" + selected.getCurrent() + "/" + selected.getMax() + ")");
            if (!((Collection)activeCommissions).isEmpty()) {
                ChatUtils.sendMessage("Commission Macro: available commissions:");
                Iterable $this$forEachIndexed$iv = activeCommissions;
                boolean $i$f$forEachIndexed = false;
                int index$iv = 0;
                for (Object item$iv : $this$forEachIndexed$iv) {
                    void c;
                    int n;
                    if ((n = index$iv++) < 0) {
                        CollectionsKt.throwIndexOverflow();
                    }
                    Commission commission = (Commission)item$iv;
                    int i = n;
                    boolean bl = false;
                    String pct = c.getMax() > 0 ? c.getCurrent() + "/" + c.getMax() : "?";
                    String marker = Intrinsics.areEqual((Object)c, (Object)selected) ? " \u25c4 selected" : "";
                    ChatUtils.sendMessage("  " + (i + 1) + ". " + c.getLabel() + " (" + pct + ")" + marker);
                }
            }
            class_746 class_7462 = CommissionMacroModule.mc.field_1724;
            if (class_7462 != null) {
                class_7462.method_7346();
            }
            openAttempts = 0;
            this.setStatus("Commission: " + selected.getLabel());
            if (selected.getType() == CommissionType.MINING) {
                this.transition(State.WARP_TO_ROUTE_START);
            } else {
                this.transition(State.MINING);
            }
            return;
        }
        int $this$forEachIndexed$iv = readAttempts;
        if ((readAttempts = $this$forEachIndexed$iv + 1) >= 30) {
            class_746 class_7463 = CommissionMacroModule.mc.field_1724;
            if (class_7463 != null) {
                class_7463.method_7346();
            }
            String source = mode.getUsesPigeon() ? "Royal Pigeon" : "emissary GUI";
            ChatUtils.sendMessage("Commission Macro: could not read any commission from " + source + ". Disabling.");
            enabled.setValue(false);
        }
    }

    private final void handleClaimGui() {
        class_465 screen;
        class_746 player = CommissionMacroModule.mc.field_1724;
        CommissionMode mode = this.refreshMode((class_1657)player);
        class_437 class_4372 = CommissionMacroModule.mc.field_1755;
        class_465 class_4652 = screen = class_4372 instanceof class_465 ? (class_465)class_4372 : null;
        if (screen == null) {
            this.resetCommissionState();
            CommissionMacroModule.transitionToCommissionSource$default(this, mode, false, false, false, 12, null);
            return;
        }
        if (stateTick < 4L || stateTick % 4L != 0L) {
            return;
        }
        int claimSlot = this.findClaimSlot(screen);
        if (claimSlot >= 0) {
            if (claimAttempts >= 8) {
                class_746 class_7462 = CommissionMacroModule.mc.field_1724;
                if (class_7462 != null) {
                    class_7462.method_7346();
                }
                ChatUtils.sendMessage("Commission Macro: claim slot did not clear after " + claimAttempts + " attempts. Reopening commissions.");
                this.resetCommissionState();
                CommissionMacroModule.transitionToCommissionSource$default(this, mode, false, false, false, 12, null);
                return;
            }
            InventoryUtils.clickSlot$default(claimSlot, null, null, 6, null);
            int n = claimAttempts;
            claimAttempts = n + 1;
            this.setStatus("Claiming commission (slot " + claimSlot + ", attempt " + claimAttempts + ")...");
            return;
        }
        if (claimAttempts > 0) {
            ChatUtils.sendMessage("Commission Macro: claim step complete. Reading next commission in the same menu...");
            this.resetCommissionState();
            this.transition(State.READ_GUI);
            return;
        }
        ParsedCommissionSelection parsed = this.parseCommissionSelectionFromGui(screen);
        if (parsed.getSelected() != null || !((Collection)parsed.getCommissions()).isEmpty()) {
            this.transition(State.READ_GUI);
            return;
        }
        class_746 class_7463 = CommissionMacroModule.mc.field_1724;
        if (class_7463 != null) {
            class_7463.method_7346();
        }
        this.resetCommissionState();
        CommissionMacroModule.transitionToCommissionSource$default(this, mode, false, false, false, 12, null);
    }

    private final void handleMining() {
        class_746 player = CommissionMacroModule.mc.field_1724;
        CommissionMode mode = this.refreshMode((class_1657)player);
        Commission commission = CommissionMacroModule.commission;
        if (commission == null) {
            CommissionMacroModule $this$handleMining_u24lambda_u240 = this;
            boolean bl = false;
            CommissionMacroModule.transitionToCommissionSource$default($this$handleMining_u24lambda_u240, mode, false, false, false, 12, null);
            return;
        }
        Commission c = commission;
        if (!this.workModuleIsActive(c)) {
            if (activeMiningRouteName != null && !RoutesModule.INSTANCE.isRunning()) {
                Object object = RoutesModule.INSTANCE.getLastAutomationCompletionPos();
                if (object == null) {
                    class_746 class_7462 = player;
                    object = class_7462 != null ? class_7462.method_24515() : null;
                }
                class_2338 routeCompletionAnchor = object;
                activeMiningRouteName = null;
                switch (WhenMappings.$EnumSwitchMapping$1[c.getType().ordinal()]) {
                    case 1: {
                        MiningMacroModule.startForAutomation$default(MiningMacroModule.INSTANCE, this.preferredMiningMacroMineTypes(c), routeCompletionAnchor, null, null, 12, null);
                        break;
                    }
                    case 2: {
                        this.startCombatWorkModule(c);
                        break;
                    }
                    default: {
                        throw new NoWhenBranchMatchedException();
                    }
                }
                String action = c.getType() == CommissionType.MINING ? "Mining" : "Combat";
                this.setStatus(action + ": " + c.getLabel());
                String string = action.toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
                ChatUtils.sendMessage("Commission Macro: route complete, starting " + string + " - " + c.getLabel());
                return;
            }
            if (player != null && this.handleGlaciteWalkerCombatStartup((class_1657)player, c)) {
                return;
            }
            if (player != null && this.handleGoblinSlayerCombatStartup((class_1657)player, c)) {
                return;
            }
            this.stopEmissaryNavigation();
            this.startWorkModule(c);
            String action = c.getType() == CommissionType.MINING ? "Mining" : "Combat";
            this.setStatus(action + ": " + c.getLabel());
            String string = action.toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
            ChatUtils.sendMessage("Commission Macro: starting " + string + " - " + c.getLabel());
        }
    }

    private final void handleReturnToDwarves() {
        CommissionMode mode = this.refreshMode((class_1657)CommissionMacroModule.mc.field_1724);
        if (mode.getUsesPigeon()) {
            this.transitionToTurnIn(mode);
            return;
        }
        if (stateTick == 1L) {
            class_746 class_7462 = CommissionMacroModule.mc.field_1724;
            Object object = class_7462 instanceof class_746 ? class_7462 : null;
            if (object != null && (object = object.field_3944) != null) {
                object.method_45730("warp dwarves");
            }
            areaText.setValue("Dwarven Mines");
            this.setStatus("Returning to Emissary Eliza...");
        }
        if (stateTick >= 100L) {
            CommissionMacroModule.transitionToCommissionSource$default(this, mode, false, false, false, 8, null);
        }
    }

    /*
     * Unable to fully structure code
     */
    private final CommissionMode refreshMode(class_1657 player) {
        block3: {
            block4: {
                block2: {
                    if (player == null) break block2;
                    var3_2 = this.findAotvHotbarSlot(player);
                    v0 = 0 <= var3_2 ? var3_2 < 9 : false;
                    if (!v0) break block2;
                    var3_2 = this.findPigeonHotbarSlot(player);
                    v1 = 0 <= var3_2 ? var3_2 < 9 : false;
                    if (!v1) break block2;
                    v2 = CommissionMode.AOTV_PIGEON;
                    break block3;
                }
                if (player == null) break block4;
                var3_2 = this.findAotvHotbarSlot(player);
                v3 = 0 <= var3_2 ? var3_2 < 9 : false;
                if (!v3) break block4;
                v2 = CommissionMode.AOTV_ELIZA;
                break block3;
            }
            if (player == null) ** GOTO lbl-1000
            var3_2 = this.findPigeonHotbarSlot(player);
            v4 = 0 <= var3_2 ? var3_2 < 9 : false;
            if (v4) {
                v2 = CommissionMode.WALK_PIGEON;
            } else lbl-1000:
            // 2 sources

            {
                v2 = CommissionMode.WALK_ONLY;
            }
        }
        CommissionMacroModule.currentMode = resolved = v2;
        CommissionMacroModule.modeText.setValue(resolved.getLabel());
        return resolved;
    }

    private final boolean ensureCommissionSourceAvailable(class_1657 player, CommissionMode mode) {
        if (mode.getUsesPigeon()) {
            return true;
        }
        if (this.resolveEmissaryTarget(player, mode) != null) {
            return true;
        }
        if (this.shouldWarpToForgeForEmissary(mode, false)) {
            return true;
        }
        if (this.shouldWarpToDwarvesForEmissary(mode, false)) {
            return true;
        }
        this.disableForMissingEmissaryTarget(mode);
        return false;
    }

    private final void transitionToLoopStart(CommissionMode mode) {
        MiningProfitTracker.INSTANCE.resetSession();
        CommissionMacroModule.transitionToCommissionSource$default(this, mode, false, true, false, 8, null);
    }

    private final void transitionToTurnIn(CommissionMode mode) {
        openAttempts = 0;
        readAttempts = 0;
        claimAttempts = 0;
        if (mode.getUsesPigeon()) {
            this.setStatus("Opening Royal Pigeon...");
            this.transition(State.OPEN_PIGEON);
        } else if (this.shouldWarpToForgeForEmissary(mode, false)) {
            this.setStatus("Returning to forge emissary...");
            this.transition(State.WARP_TO_FORGE_EMISSARY);
        } else if (this.shouldWarpToDwarvesForEmissary(mode, false)) {
            this.setStatus("Returning to Emissary Eliza...");
            this.transition(State.RETURN_TO_DWARVES);
        } else {
            this.transitionToCommissionSource(mode, false, false, false);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private final boolean isGlaciteWalkerCommission(Commission c) {
        String keyword;
        if (c.getType() != CommissionType.COMBAT) return false;
        Iterable $this$any$iv = GLACITE_WALKER_NAME_KEYWORDS;
        boolean $i$f$any = false;
        if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
            return false;
        }
        Iterator iterator = $this$any$iv.iterator();
        do {
            if (!iterator.hasNext()) return false;
            Object element$iv = iterator.next();
            keyword = (String)element$iv;
            boolean bl = false;
        } while (!StringsKt.contains$default((CharSequence)INSTANCE.normalizeComparisonText(c.getTarget()), (CharSequence)keyword, (boolean)false, (int)2, null));
        return true;
    }

    private final boolean isGoblinSlayerCommission(Commission c) {
        if (c.getType() != CommissionType.COMBAT) {
            return false;
        }
        String normalizedLabel = this.normalizeComparisonText(c.getLabel());
        String normalizedTarget = this.normalizeComparisonText(c.getTarget());
        if (StringsKt.contains$default((CharSequence)normalizedLabel, (CharSequence)"golden goblin", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)normalizedLabel, (CharSequence)"goblin raid", (boolean)false, (int)2, null)) {
            return false;
        }
        return Intrinsics.areEqual((Object)normalizedLabel, (Object)"goblin slayer") || Intrinsics.areEqual((Object)normalizedTarget, (Object)"goblin");
    }

    private final String combatAutomationTarget(Commission c) {
        return this.isGlaciteWalkerCommission(c) ? "glacite walker" : c.getTarget();
    }

    private final void startCombatWorkModule(Commission c) {
        activeMiningRouteName = null;
        Object object = CommissionMacroModule.mc.field_1724;
        if (object != null) {
            class_746 class_7462;
            class_746 it = class_7462 = object;
            boolean bl = false;
            object = INSTANCE.isGlaciteWalkerCommission(c) ? class_7462 : null;
            if (object != null) {
                class_1657 p0 = (class_1657)object;
                boolean bl2 = false;
                this.equipDrillForGlaciteWalkers(p0);
            }
        }
        CombatMacroModule.INSTANCE.startForAutomation(this.combatAutomationTarget(c));
    }

    private final boolean handleGlaciteWalkerCombatStartup(class_1657 player, Commission c) {
        if (!this.isGlaciteWalkerCommission(c)) {
            return false;
        }
        this.equipDrillForGlaciteWalkers(player);
        if (CommissionMacroModule.mc.field_1755 instanceof class_465) {
            class_746 class_7462 = CommissionMacroModule.mc.field_1724;
            if (class_7462 != null) {
                class_7462.method_7346();
            }
        }
        if (this.hasNearbyGlaciteWalker(player) || player.method_5649((double)greatIceWallGatePos.method_10263() + 0.5, (double)greatIceWallGatePos.method_10264(), (double)greatIceWallGatePos.method_10260() + 0.5) <= 196.0) {
            this.stopEmissaryNavigation();
            this.startCombatWorkModule(c);
            this.setStatus("Combat: " + c.getLabel());
            ChatUtils.sendMessage("Commission Macro: at Great Ice Wall, starting combat - " + c.getLabel());
            return true;
        }
        this.tickCommissionNavigation(player, greatIceWallGatePos);
        this.setStatus("Walking to Great Ice Wall gate...");
        return true;
    }

    private final boolean handleGoblinSlayerCombatStartup(class_1657 player, Commission c) {
        boolean atGreatIceWall;
        if (!this.isGoblinSlayerCommission(c)) {
            return false;
        }
        if (CommissionMacroModule.mc.field_1755 instanceof class_465) {
            class_746 class_7462 = CommissionMacroModule.mc.field_1724;
            if (class_7462 != null) {
                class_7462.method_7346();
            }
        }
        double goblinBurrowsDistanceSq = player.method_5649((double)goblinBurrowsCenterPos.method_10263() + 0.5, (double)goblinBurrowsCenterPos.method_10264(), (double)goblinBurrowsCenterPos.method_10260() + 0.5);
        if (this.hasNearbyGoblin(player) || goblinBurrowsDistanceSq <= 1296.0) {
            this.stopEmissaryNavigation();
            this.startCombatWorkModule(c);
            this.setStatus("Combat: " + c.getLabel());
            ChatUtils.sendMessage("Commission Macro: at Goblin Burrows, starting combat - " + c.getLabel());
            return true;
        }
        boolean bl = atGreatIceWall = this.hasNearbyGlaciteWalker(player) || player.method_5649((double)greatIceWallGatePos.method_10263() + 0.5, (double)greatIceWallGatePos.method_10264(), (double)greatIceWallGatePos.method_10260() + 0.5) <= 1764.0;
        if (atGreatIceWall) {
            this.tickCommissionNavigation(player, goblinBurrowsCenterPos);
            this.setStatus("Walking to Goblin Burrows...");
            return true;
        }
        this.tickCommissionNavigation(player, greatIceWallGatePos);
        this.setStatus("Walking to Great Ice Wall...");
        return true;
    }

    private final void equipDrillForGlaciteWalkers(class_1657 player) {
        String string = player.method_31548().method_5438(player.method_31548().method_67532()).method_7964().getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        String selectedName = this.normalizeName(string);
        if (StringsKt.contains$default((CharSequence)selectedName, (CharSequence)"drill", (boolean)false, (int)2, null)) {
            return;
        }
        int drillSlot = InventoryUtils.findItemInHotbar("drill");
        boolean bl = 0 <= drillSlot ? drillSlot < 9 : false;
        if (bl) {
            InventoryUtils.holdHotbarSlot(drillSlot);
        }
    }

    private final boolean hasNearbyGlaciteWalker(class_1657 player) {
        boolean bl;
        block6: {
            class_638 class_6382 = CommissionMacroModule.mc.field_1687;
            if (class_6382 == null) {
                return false;
            }
            class_638 level2 = class_6382;
            Iterable iterable = level2.method_18112();
            Intrinsics.checkNotNullExpressionValue((Object)iterable, (String)"entitiesForRendering(...)");
            Sequence $this$filterIsInstance$iv = CollectionsKt.asSequence((Iterable)iterable);
            boolean $i$f$filterIsInstance = false;
            Sequence sequence = SequencesKt.filter((Sequence)$this$filterIsInstance$iv, (Function1)hasNearbyGlaciteWalker$$inlined$filterIsInstance$1.INSTANCE);
            Intrinsics.checkNotNull((Object)sequence, (String)"null cannot be cast to non-null type kotlin.sequences.Sequence<R of kotlin.sequences.SequencesKt___SequencesKt.filterIsInstance>");
            Sequence $this$any$iv = SequencesKt.filter((Sequence)sequence, arg_0 -> CommissionMacroModule.hasNearbyGlaciteWalker$lambda$0(player, arg_0));
            boolean $i$f$any = false;
            for (Object element$iv : $this$any$iv) {
                boolean bl2;
                class_1309 entity;
                block5: {
                    entity = (class_1309)element$iv;
                    boolean bl3 = false;
                    String string = entity.method_5477().getString();
                    Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
                    String name = INSTANCE.normalizeName(string);
                    Iterable $this$any$iv2 = GLACITE_WALKER_NAME_KEYWORDS;
                    boolean $i$f$any2 = false;
                    if ($this$any$iv2 instanceof Collection && ((Collection)$this$any$iv2).isEmpty()) {
                        bl2 = false;
                    } else {
                        for (Object element$iv2 : $this$any$iv2) {
                            CharSequence p0 = (CharSequence)element$iv2;
                            boolean bl4 = false;
                            if (!StringsKt.contains$default((CharSequence)name, (CharSequence)p0, (boolean)false, (int)2, null)) continue;
                            bl2 = true;
                            break block5;
                        }
                        bl2 = false;
                    }
                }
                if (!(bl2 && player.method_5858((class_1297)entity) <= 1600.0)) continue;
                bl = true;
                break block6;
            }
            bl = false;
        }
        return bl;
    }

    private final boolean hasNearbyGoblin(class_1657 player) {
        boolean bl;
        block2: {
            class_638 class_6382 = CommissionMacroModule.mc.field_1687;
            if (class_6382 == null) {
                return false;
            }
            class_638 level2 = class_6382;
            Iterable iterable = level2.method_18112();
            Intrinsics.checkNotNullExpressionValue((Object)iterable, (String)"entitiesForRendering(...)");
            Sequence $this$filterIsInstance$iv = CollectionsKt.asSequence((Iterable)iterable);
            boolean $i$f$filterIsInstance = false;
            Sequence sequence = SequencesKt.filter((Sequence)$this$filterIsInstance$iv, (Function1)hasNearbyGoblin$$inlined$filterIsInstance$1.INSTANCE);
            Intrinsics.checkNotNull((Object)sequence, (String)"null cannot be cast to non-null type kotlin.sequences.Sequence<R of kotlin.sequences.SequencesKt___SequencesKt.filterIsInstance>");
            Sequence $this$any$iv = SequencesKt.filter((Sequence)sequence, arg_0 -> CommissionMacroModule.hasNearbyGoblin$lambda$0(player, arg_0));
            boolean $i$f$any = false;
            for (Object element$iv : $this$any$iv) {
                class_1309 entity = (class_1309)element$iv;
                boolean bl2 = false;
                String string = entity.method_5477().getString();
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
                String name = INSTANCE.normalizeName(string);
                if (!(Intrinsics.areEqual((Object)name, (Object)"goblin") && player.method_5858((class_1297)entity) <= 1600.0)) continue;
                bl = true;
                break block2;
            }
            bl = false;
        }
        return bl;
    }

    private final void tickCommissionNavigation(class_1657 player, class_2338 target) {
        PathCommand cmd;
        class_2338 pathTarget = this.findWalkTargetNear(target);
        if (!Intrinsics.areEqual((Object)lastEmissaryPathTarget, (Object)pathTarget) || !this.nativePathActive() || stateTick % 40L == 1L) {
            this.startEmissaryNavigation(pathTarget);
        }
        if ((cmd = NativePathfinder.INSTANCE.tick()) != null) {
            cmd.applyToPlayer();
            this.applyEmissaryWalkCameraRotation(player);
            this.ensureEmissaryWalkMovement(player, pathTarget);
        } else if (lastEmissaryPathTarget != null) {
            this.applyEmissaryWalkCameraRotation(player);
            this.applyEmissaryWalkFallbackMovement(player, pathTarget);
        }
        this.syncEmissaryWalkKeys();
    }

    private final void transitionToCommissionSource(CommissionMode mode, boolean alreadyAtSource, boolean allowDwarvenWarp, boolean allowForgeWarp) {
        openAttempts = 0;
        readAttempts = 0;
        claimAttempts = 0;
        if (mode.getUsesPigeon()) {
            this.setStatus("Opening Royal Pigeon...");
            this.transition(State.OPEN_PIGEON);
        } else if (allowForgeWarp && this.shouldWarpToForgeForEmissary(mode, alreadyAtSource)) {
            this.setStatus("Warping to forge emissary...");
            this.transition(State.WARP_TO_FORGE_EMISSARY);
        } else if (allowDwarvenWarp && this.shouldWarpToDwarvesForEmissary(mode, alreadyAtSource)) {
            this.setStatus("Warping to dwarves...");
            this.transition(State.WARP_TO_DWARVES);
        } else if (alreadyAtSource) {
            this.setStatus("Opening " + this.configuredEmissaryDisplayName(mode) + "...");
            this.transition(State.OPEN_EMISSARY);
        } else {
            this.setStatus("Walking to emissary...");
            this.transition(State.WALK_TO_EMISSARY);
        }
    }

    static /* synthetic */ void transitionToCommissionSource$default(CommissionMacroModule commissionMacroModule, CommissionMode commissionMode, boolean bl, boolean bl2, boolean bl3, int n, Object object) {
        if ((n & 4) != 0) {
            bl2 = true;
        }
        if ((n & 8) != 0) {
            bl3 = true;
        }
        commissionMacroModule.transitionToCommissionSource(commissionMode, bl, bl2, bl3);
    }

    private final boolean shouldWarpToDwarvesForEmissary(CommissionMode mode, boolean alreadyAtSource) {
        if (this.getEmissaryWarpDestination(mode) != RouteWarpDestination.DWARVES) {
            return false;
        }
        if (mode.getUsesPigeon() || alreadyAtSource) {
            return false;
        }
        class_746 class_7462 = CommissionMacroModule.mc.field_1724;
        if (class_7462 == null) {
            return true;
        }
        class_746 player = class_7462;
        EmissaryTarget emissaryTarget = this.resolveEmissaryTarget((class_1657)player, mode);
        if (emissaryTarget == null) {
            return true;
        }
        EmissaryTarget target = emissaryTarget;
        class_1297 class_12972 = target.getInteractionEntity();
        if (class_12972 != null) {
            class_1297 entity = class_12972;
            boolean bl = false;
            if (player.method_5858(entity) <= 36.0) {
                return false;
            }
        }
        return player.method_24515().method_10262((class_2382)target.getWalkPos()) > 36.0;
    }

    private final boolean shouldWarpToForgeForEmissary(CommissionMode mode, boolean alreadyAtSource) {
        if (this.getEmissaryWarpDestination(mode) != RouteWarpDestination.FORGE) {
            return false;
        }
        if (mode.getUsesPigeon() || alreadyAtSource) {
            return false;
        }
        class_746 class_7462 = CommissionMacroModule.mc.field_1724;
        if (class_7462 == null) {
            return true;
        }
        class_746 player = class_7462;
        EmissaryTarget emissaryTarget = this.resolveEmissaryTarget((class_1657)player, mode);
        if (emissaryTarget == null) {
            return true;
        }
        EmissaryTarget target = emissaryTarget;
        class_1297 class_12972 = target.getInteractionEntity();
        if (class_12972 != null) {
            class_1297 entity = class_12972;
            boolean bl = false;
            if (player.method_5858(entity) <= 36.0) {
                return false;
            }
        }
        return player.method_24515().method_10262((class_2382)target.getWalkPos()) > 36.0;
    }

    private final RouteWarpDestination getEmissaryWarpDestination(CommissionMode mode) {
        return mode.getUsesPigeon() ? null : this.getRouteWarpDestination();
    }

    private final String configuredEmissaryDisplayName(CommissionMode mode) {
        return this.shouldUseForgeEmissaryRoute(mode) ? "emissary" : "Emissary Eliza";
    }

    private final RouteWarpDestination getRouteWarpDestination() {
        return ((Number)routeWarpSetting.getValue()).intValue() == 1 ? RouteWarpDestination.DWARVES : RouteWarpDestination.FORGE;
    }

    private final class_2338 getElizaTargetPos() {
        return elizaTargetPos;
    }

    private final void disableForMissingEmissaryTarget(CommissionMode mode) {
        this.setStatus("Missing emissary target");
        String detail = this.shouldUseForgeEmissaryRoute(mode) ? "the configured forge emissary path" : "hard-coded Eliza coords -37 201 -130";
        ChatUtils.sendMessage("Commission Macro: no emissary target found near " + detail + ". Disabling.");
        enabled.setValue(false);
    }

    static /* synthetic */ void disableForMissingEmissaryTarget$default(CommissionMacroModule commissionMacroModule, CommissionMode commissionMode, int n, Object object) {
        if ((n & 1) != 0) {
            commissionMode = currentMode;
        }
        commissionMacroModule.disableForMissingEmissaryTarget(commissionMode);
    }

    private final EmissaryTarget resolveEmissaryTarget(class_1657 player, CommissionMode mode) {
        class_1297 nearestLoaded;
        block13: {
            Object object;
            class_2338 walkPos;
            class_2338 nearestLoaded2;
            block17: {
                block16: {
                    class_2338 p0;
                    class_2338 class_23382;
                    block15: {
                        block14: {
                            Object object2;
                            if (!this.shouldUseForgeEmissaryRoute(mode)) break block13;
                            class_1297 class_12972 = this.findNearestLoadedEmissaryInteractionEntity(player);
                            if (class_12972 != null) {
                                boolean bl;
                                class_1297 class_12973;
                                class_1297 entity = class_12973 = class_12972;
                                boolean bl2 = false;
                                class_2338 class_23383 = (class_2338)CollectionsKt.lastOrNull(forgeEmissaryWalkNodes);
                                if (class_23383 != null) {
                                    class_2338 end = class_23383;
                                    boolean bl3 = false;
                                    bl = entity.method_24515().method_10262((class_2382)end) <= 256.0;
                                } else {
                                    bl = false;
                                }
                                object2 = bl ? class_12973 : null;
                            } else {
                                object2 = null;
                            }
                            if ((class_23382 = (nearestLoaded2 = object2)) == null || (class_23382 = class_23382.method_24515()) == null) break block14;
                            p0 = class_23382;
                            boolean bl = false;
                            class_2338 class_23384 = this.findWalkTargetNear(p0);
                            class_23382 = class_23384;
                            if (class_23384 != null) break block15;
                        }
                        class_2338 class_23385 = (class_2338)CollectionsKt.lastOrNull(forgeEmissaryWalkNodes);
                        if (class_23385 != null) {
                            class_2338 p02 = class_23385;
                            boolean bl = false;
                            class_23382 = this.findWalkTargetNear(p02);
                        } else {
                            return null;
                        }
                    }
                    walkPos = class_23382;
                    object = nearestLoaded2;
                    if (object == null) break block16;
                    p0 = object;
                    boolean bl = false;
                    String string = this.formatEmissaryLabel((class_1297)p0);
                    object = string;
                    if (string != null) break block17;
                }
                object = "Emissary";
            }
            Object label = object;
            return new EmissaryTarget(walkPos, (class_1297)nearestLoaded2, (String)label, forgeEmissaryWalkNodes);
        }
        if (!mode.getUsesWarps() && (nearestLoaded = this.findNearestLoadedEmissaryInteractionEntity(player)) != null) {
            class_2338 class_23386 = nearestLoaded.method_24515();
            Intrinsics.checkNotNullExpressionValue((Object)class_23386, (String)"blockPosition(...)");
            return new EmissaryTarget(this.findWalkTargetNear(class_23386), nearestLoaded, this.formatEmissaryLabel(nearestLoaded), null, 8, null);
        }
        class_2338 elizaPos = this.getElizaTargetPos();
        return new EmissaryTarget(this.findWalkTargetNear(elizaPos), this.findEmissaryInteractionEntity(elizaPos), "Emissary Eliza", null, 8, null);
    }

    private final boolean shouldUseForgeEmissaryRoute(CommissionMode mode) {
        return !mode.getUsesPigeon() && this.getRouteWarpDestination() == RouteWarpDestination.FORGE;
    }

    private final class_2338 resolveEmissaryNavigationTarget(class_1657 player, EmissaryTarget target) {
        class_2338 finalNode = (class_2338)CollectionsKt.lastOrNull(target.getWalkNodes());
        class_1297 class_12972 = target.getInteractionEntity();
        if (class_12972 != null) {
            boolean canDirectToEntity;
            class_1297 entity = class_12972;
            boolean bl = false;
            boolean bl2 = canDirectToEntity = finalNode == null || player.method_24515().method_10262((class_2382)finalNode) <= 144.0 || player.method_5858(entity) <= 100.0;
            if (canDirectToEntity) {
                class_2338 class_23382 = entity.method_24515();
                Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"blockPosition(...)");
                return INSTANCE.findWalkTargetNear(class_23382);
            }
        }
        if (target.getWalkNodes().isEmpty()) {
            return target.getWalkPos();
        }
        class_2338 class_23383 = this.resolveNextEmissaryWalkNode(player, target.getWalkNodes());
        if (class_23383 == null) {
            return target.getWalkPos();
        }
        class_2338 node = class_23383;
        return this.findWalkTargetNear(node);
    }

    private final class_2338 resolveNextEmissaryWalkNode(class_1657 player, List<? extends class_2338> nodes) {
        class_2338 currentNode;
        if (nodes.isEmpty()) {
            return null;
        }
        class_2338 class_23382 = player.method_24515();
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"blockPosition(...)");
        class_2338 playerPos = class_23382;
        int n = ((Collection)nodes).size();
        int n2 = activeEmissaryWalkNodeIndex;
        if (!(0 <= n2 ? n2 < n : false)) {
            Object v1;
            Iterable $this$minByOrNull$iv = (Iterable)CollectionsKt.getIndices((Collection)nodes);
            boolean $i$f$minByOrNull = false;
            Iterator iterator$iv = $this$minByOrNull$iv.iterator();
            if (!iterator$iv.hasNext()) {
                v1 = null;
            } else {
                Object minElem$iv = iterator$iv.next();
                if (!iterator$iv.hasNext()) {
                    v1 = minElem$iv;
                } else {
                    int index = ((Number)minElem$iv).intValue();
                    boolean bl = false;
                    double minValue$iv = playerPos.method_10262((class_2382)nodes.get(index));
                    do {
                        Object e$iv = iterator$iv.next();
                        int index2 = ((Number)e$iv).intValue();
                        $i$a$-minByOrNull-CommissionMacroModule$resolveNextEmissaryWalkNode$1 = false;
                        double v$iv = playerPos.method_10262((class_2382)nodes.get(index2));
                        if (Double.compare(minValue$iv, v$iv) <= 0) continue;
                        minElem$iv = e$iv;
                        minValue$iv = v$iv;
                    } while (iterator$iv.hasNext());
                    v1 = minElem$iv;
                }
            }
            Integer n3 = v1;
            int n4 = activeEmissaryWalkNodeIndex = n3 != null ? n3 : 0;
        }
        while (activeEmissaryWalkNodeIndex < CollectionsKt.getLastIndex(nodes) && !(playerPos.method_10262((class_2382)(currentNode = nodes.get(activeEmissaryWalkNodeIndex))) > 9.0)) {
            n2 = activeEmissaryWalkNodeIndex;
            activeEmissaryWalkNodeIndex = n2 + 1;
        }
        return nodes.get(RangesKt.coerceIn((int)activeEmissaryWalkNodeIndex, (int)0, (int)CollectionsKt.getLastIndex(nodes)));
    }

    private final class_1297 findEmissaryInteractionEntity(class_2338 targetPos) {
        class_638 class_6382 = CommissionMacroModule.mc.field_1687;
        if (class_6382 == null) {
            return null;
        }
        class_638 level2 = class_6382;
        class_746 class_7462 = CommissionMacroModule.mc.field_1724;
        if (class_7462 == null) {
            return null;
        }
        class_746 player = class_7462;
        Iterable iterable = level2.method_18112();
        Intrinsics.checkNotNullExpressionValue((Object)iterable, (String)"entitiesForRendering(...)");
        Function1[] function1Array = new Function1[]{CommissionMacroModule::findEmissaryInteractionEntity$lambda$3, arg_0 -> CommissionMacroModule.findEmissaryInteractionEntity$lambda$4(player, arg_0)};
        class_1297 class_12972 = (class_1297)SequencesKt.firstOrNull((Sequence)SequencesKt.sortedWith((Sequence)SequencesKt.filter((Sequence)SequencesKt.filter((Sequence)SequencesKt.filter((Sequence)CollectionsKt.asSequence((Iterable)iterable), arg_0 -> CommissionMacroModule.findEmissaryInteractionEntity$lambda$0(player, arg_0)), arg_0 -> CommissionMacroModule.findEmissaryInteractionEntity$lambda$1(targetPos, arg_0)), CommissionMacroModule::findEmissaryInteractionEntity$lambda$2), (Comparator)ComparisonsKt.compareBy((Function1[])function1Array)));
        if (class_12972 == null) {
            return null;
        }
        class_1297 anchor = class_12972;
        if (!(anchor instanceof class_1531)) {
            return anchor;
        }
        Iterable iterable2 = level2.method_18112();
        Intrinsics.checkNotNullExpressionValue((Object)iterable2, (String)"entitiesForRendering(...)");
        function1Array = new Function1[]{CommissionMacroModule::findEmissaryInteractionEntity$lambda$6, arg_0 -> CommissionMacroModule.findEmissaryInteractionEntity$lambda$7(anchor, arg_0)};
        class_1297 class_12973 = (class_1297)SequencesKt.firstOrNull((Sequence)SequencesKt.sortedWith((Sequence)SequencesKt.filter((Sequence)CollectionsKt.asSequence((Iterable)iterable2), arg_0 -> CommissionMacroModule.findEmissaryInteractionEntity$lambda$5(player, anchor, arg_0)), (Comparator)ComparisonsKt.compareBy((Function1[])function1Array)));
        if (class_12973 == null) {
            class_12973 = anchor;
        }
        return class_12973;
    }

    /*
     * WARNING - void declaration
     */
    private final class_1297 findNearestLoadedEmissaryInteractionEntity(class_1657 player) {
        void $this$sortedBy$iv;
        class_638 class_6382 = CommissionMacroModule.mc.field_1687;
        if (class_6382 == null) {
            return null;
        }
        class_638 level2 = class_6382;
        Iterable iterable = level2.method_18112();
        Intrinsics.checkNotNullExpressionValue((Object)iterable, (String)"entitiesForRendering(...)");
        Sequence sequence = SequencesKt.filter((Sequence)SequencesKt.filter((Sequence)CollectionsKt.asSequence((Iterable)iterable), arg_0 -> CommissionMacroModule.findNearestLoadedEmissaryInteractionEntity$lambda$0(player, arg_0)), CommissionMacroModule::findNearestLoadedEmissaryInteractionEntity$lambda$1);
        boolean $i$f$sortedBy = false;
        List anchors = SequencesKt.toList((Sequence)SequencesKt.sortedWith((Sequence)$this$sortedBy$iv, (Comparator)new Comparator(player){
            final /* synthetic */ class_1657 $player$inlined;
            {
                this.$player$inlined = class_16572;
            }

            public final int compare(T a, T b) {
                class_1297 it = (class_1297)a;
                boolean bl = false;
                Comparable comparable = Double.valueOf(this.$player$inlined.method_5858(it));
                it = (class_1297)b;
                Comparable comparable2 = comparable;
                bl = false;
                return ComparisonsKt.compareValues((Comparable)comparable2, (Comparable)Double.valueOf(this.$player$inlined.method_5858(it)));
            }
        }));
        if (anchors.isEmpty()) {
            return null;
        }
        Object object = CollectionsKt.first((List)anchors);
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"first(...)");
        class_1297 anchor = (class_1297)object;
        if (!(anchor instanceof class_1531)) {
            return anchor;
        }
        Iterable iterable2 = level2.method_18112();
        Intrinsics.checkNotNullExpressionValue((Object)iterable2, (String)"entitiesForRendering(...)");
        Function1[] function1Array = new Function1[]{CommissionMacroModule::findNearestLoadedEmissaryInteractionEntity$lambda$4, arg_0 -> CommissionMacroModule.findNearestLoadedEmissaryInteractionEntity$lambda$5(anchor, arg_0)};
        class_1297 class_12972 = (class_1297)SequencesKt.firstOrNull((Sequence)SequencesKt.sortedWith((Sequence)SequencesKt.filter((Sequence)CollectionsKt.asSequence((Iterable)iterable2), arg_0 -> CommissionMacroModule.findNearestLoadedEmissaryInteractionEntity$lambda$3(player, anchor, arg_0)), (Comparator)ComparisonsKt.compareBy((Function1[])function1Array)));
        if (class_12972 == null) {
            class_12972 = anchor;
        }
        return class_12972;
    }

    private final boolean entityNameMatchesEliza(class_1297 entity) {
        String string = entity.method_5477().getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        String name = this.normalizeName(string);
        return StringsKt.contains$default((CharSequence)name, (CharSequence)"emissary eliza", (boolean)false, (int)2, null) || Intrinsics.areEqual((Object)name, (Object)"eliza");
    }

    private final boolean entityNameMatchesAnyEmissary(class_1297 entity) {
        String string = entity.method_5477().getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        String name = this.normalizeName(string);
        return StringsKt.contains$default((CharSequence)name, (CharSequence)"emissary", (boolean)false, (int)2, null) || Intrinsics.areEqual((Object)name, (Object)"eliza");
    }

    private final String formatEmissaryLabel(class_1297 entity) {
        CharSequence charSequence;
        String string = entity.method_5477().getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        String normalized = this.normalizeName(string);
        CharSequence charSequence2 = StringsKt.removePrefix((String)normalized, (CharSequence)"emissary ");
        if (StringsKt.isBlank((CharSequence)charSequence2)) {
            boolean bl = false;
            charSequence = "emissary";
        } else {
            charSequence = charSequence2;
        }
        String p0 = (String)charSequence;
        boolean bl = false;
        String it = this.titleCase(p0);
        boolean bl2 = false;
        return StringsKt.startsWith$default((String)it, (String)"Emissary ", (boolean)false, (int)2, null) ? it : "Emissary " + it;
    }

    private final int findAotvHotbarSlot(class_1657 player) {
        for (int i = 0; i < 9; ++i) {
            String name;
            class_1799 stack;
            Intrinsics.checkNotNullExpressionValue((Object)player.method_31548().method_5438(i), (String)"getItem(...)");
            if (stack.method_7960() || !EtherwarpLogic.INSTANCE.isEtherwarpStack(stack)) continue;
            String string = class_124.method_539((String)stack.method_7964().getString());
            if (string == null) continue;
            String string2 = string;
            Locale locale = Locale.US;
            Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"US");
            String string3 = string2.toLowerCase(locale);
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
            string = string3;
            if (string3 == null || !StringsKt.contains$default((CharSequence)(name = string), (CharSequence)"aspect of the void", (boolean)false, (int)2, null) && !StringsKt.contains$default((CharSequence)name, (CharSequence)"warped aspect of the void", (boolean)false, (int)2, null)) continue;
            return i;
        }
        return -1;
    }

    private final int findPigeonHotbarSlot(class_1657 player) {
        for (int i = 0; i < 9; ++i) {
            String name;
            class_1799 stack;
            Intrinsics.checkNotNullExpressionValue((Object)player.method_31548().method_5438(i), (String)"getItem(...)");
            if (stack.method_7960()) continue;
            String string = class_124.method_539((String)stack.method_7964().getString());
            if (string == null) continue;
            String string2 = string;
            Locale locale = Locale.US;
            Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"US");
            String string3 = string2.toLowerCase(locale);
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
            string = string3;
            if (string3 == null || !StringsKt.contains$default((CharSequence)(name = string), (CharSequence)"royal pigeon", (boolean)false, (int)2, null)) continue;
            return i;
        }
        return -1;
    }

    private final void pressHotbarSlot(int slot) {
        block1: {
            if (!(0 <= slot ? slot < 9 : false)) {
                return;
            }
            class_746 class_7462 = CommissionMacroModule.mc.field_1724;
            if (class_7462 == null || (class_7462 = class_7462.method_31548()) == null) break block1;
            class_7462.method_61496(slot);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private final String normalizeName(String raw) {
        String string = class_124.method_539((String)raw);
        if (string == null) return "";
        String string2 = string;
        Locale locale = Locale.US;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"US");
        String string3 = string2.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
        String string4 = string3;
        if (string4 == null) return "";
        Regex regex = new Regex("\\s+");
        CharSequence charSequence = string4;
        String string5 = " ";
        string2 = regex.replace(charSequence, string5);
        if (string2 == null) return "";
        String string6 = ((Object)StringsKt.trim((CharSequence)string2)).toString();
        String string7 = string6;
        if (string6 != null) return string7;
        return "";
    }

    private final class_2338 findWalkTargetNear(class_2338 base) {
        class_638 class_6382 = CommissionMacroModule.mc.field_1687;
        if (class_6382 == null) {
            return base;
        }
        class_638 level2 = class_6382;
        if (MinecraftPathingRules.INSTANCE.isWalkable((class_1937)level2, base)) {
            return base;
        }
        for (int radius = 1; radius < 4; ++radius) {
            block1: for (int dy = -2; dy < 3; ++dy) {
                int dx = -radius;
                if (dx > radius) continue;
                while (true) {
                    int dz;
                    if ((dz = -radius) <= radius) {
                        while (true) {
                            class_2338 candidate;
                            Intrinsics.checkNotNullExpressionValue((Object)base.method_10069(dx, dy, dz), (String)"offset(...)");
                            if (MinecraftPathingRules.INSTANCE.isWalkable((class_1937)level2, candidate)) {
                                return candidate;
                            }
                            if (dz == radius) break;
                            ++dz;
                        }
                    }
                    if (dx == radius) continue block1;
                    ++dx;
                }
            }
        }
        return base;
    }

    private final void faceEntity(class_1297 entity) {
        class_746 class_7462 = CommissionMacroModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        Rotation rotation = AngleUtils.INSTANCE.getRotation(entity);
        this.applyTickRotation((class_1657)player, rotation.getYaw(), rotation.getPitch(), 18.0f, 14.0f);
    }

    private final void applyForgeWarpPigeonLook(class_1657 player) {
        float f;
        Float f2 = forgeWarpPigeonTargetYaw;
        if (f2 != null) {
            f = f2.floatValue();
        } else {
            float f3;
            float it = f3 = AngleUtils.INSTANCE.normalizeAngle(player.method_36454() + 30.0f);
            boolean bl = false;
            forgeWarpPigeonTargetYaw = Float.valueOf(it);
            f = f3;
        }
        float targetYaw = f;
        this.applyTickRotation(player, targetYaw, -18.0f, 12.0f, 7.0f);
    }

    /*
     * Unable to fully structure code
     */
    private final boolean ensureSafeRoyalPigeonLook(class_1657 player) {
        fred = this.findNearestLoadedFredInteractionEntity(player);
        if (fred == null || player.method_5858(fred) > 64.0) {
            if (!CommissionMacroModule.pendingForgeWarpPigeonLook) {
                CommissionMacroModule.forgeWarpPigeonTargetYaw = null;
            }
            CommissionMacroModule.forgeWarpPigeonLookStableTicks = 0;
            return false;
        }
        fredYaw = AngleUtils.INSTANCE.getRotation(fred).getYaw();
        rightTurn = AngleUtils.INSTANCE.normalizeAngle(fredYaw + 82.0f);
        leftTurn = AngleUtils.INSTANCE.normalizeAngle(fredYaw - 82.0f);
        v0 = CommissionMacroModule.forgeWarpPigeonTargetYaw;
        if (v0 == null) ** GOTO lbl-1000
        var9_6 = v0;
        stored = ((Number)var9_6).floatValue();
        $i$a$-takeIf-CommissionMacroModule$ensureSafeRoyalPigeonLook$targetYaw$1 = false;
        rightError = Math.abs(AngleUtils.INSTANCE.getRotationDelta(stored, rightTurn));
        v0 = Math.min(rightError, leftError = Math.abs(AngleUtils.INSTANCE.getRotationDelta(stored, leftTurn))) <= 18.0f != false ? var9_6 : null;
        if (v0 != null) {
            v1 = v0.floatValue();
        } else lbl-1000:
        // 2 sources

        {
            $this$ensureSafeRoyalPigeonLook_u24lambda_u241 = this;
            $i$a$-run-CommissionMacroModule$ensureSafeRoyalPigeonLook$targetYaw$2 = false;
            rightCost = Math.abs(AngleUtils.INSTANCE.getRotationDelta(player.method_36454(), rightTurn));
            chosen = var14_12 = rightCost <= (leftCost = Math.abs(AngleUtils.INSTANCE.getRotationDelta(player.method_36454(), leftTurn))) ? rightTurn : leftTurn;
            $i$a$-also-CommissionMacroModule$ensureSafeRoyalPigeonLook$targetYaw$2$1 = false;
            CommissionMacroModule.forgeWarpPigeonTargetYaw = Float.valueOf(chosen);
            v1 = var14_12;
        }
        targetYaw = v1;
        this.applyTickRotation(player, targetYaw, -18.0f, 12.0f, 7.0f);
        yawReady = Math.abs(AngleUtils.INSTANCE.getRotationDelta(player.method_36454(), targetYaw)) <= 3.5f;
        pitchReady = Math.abs(player.method_36455() - -18.0f) <= 2.5f;
        CommissionMacroModule.forgeWarpPigeonLookStableTicks = yawReady != false && pitchReady != false ? CommissionMacroModule.forgeWarpPigeonLookStableTicks + 1 : 0;
        return CommissionMacroModule.forgeWarpPigeonLookStableTicks < 2;
    }

    /*
     * WARNING - void declaration
     */
    private final class_1297 findNearestLoadedFredInteractionEntity(class_1657 player) {
        void $this$sortedBy$iv;
        class_638 class_6382 = CommissionMacroModule.mc.field_1687;
        if (class_6382 == null) {
            return null;
        }
        class_638 level2 = class_6382;
        Iterable iterable = level2.method_18112();
        Intrinsics.checkNotNullExpressionValue((Object)iterable, (String)"entitiesForRendering(...)");
        Sequence sequence = SequencesKt.filter((Sequence)SequencesKt.filter((Sequence)CollectionsKt.asSequence((Iterable)iterable), arg_0 -> CommissionMacroModule.findNearestLoadedFredInteractionEntity$lambda$0(player, arg_0)), CommissionMacroModule::findNearestLoadedFredInteractionEntity$lambda$1);
        boolean $i$f$sortedBy = false;
        List anchors = SequencesKt.toList((Sequence)SequencesKt.sortedWith((Sequence)$this$sortedBy$iv, (Comparator)new Comparator(player){
            final /* synthetic */ class_1657 $player$inlined;
            {
                this.$player$inlined = class_16572;
            }

            public final int compare(T a, T b) {
                class_1297 it = (class_1297)a;
                boolean bl = false;
                Comparable comparable = Double.valueOf(this.$player$inlined.method_5858(it));
                it = (class_1297)b;
                Comparable comparable2 = comparable;
                bl = false;
                return ComparisonsKt.compareValues((Comparable)comparable2, (Comparable)Double.valueOf(this.$player$inlined.method_5858(it)));
            }
        }));
        if (anchors.isEmpty()) {
            return null;
        }
        Object object = CollectionsKt.first((List)anchors);
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"first(...)");
        class_1297 anchor = (class_1297)object;
        if (!(anchor instanceof class_1531)) {
            return anchor;
        }
        Iterable iterable2 = level2.method_18112();
        Intrinsics.checkNotNullExpressionValue((Object)iterable2, (String)"entitiesForRendering(...)");
        Function1[] function1Array = new Function1[]{CommissionMacroModule::findNearestLoadedFredInteractionEntity$lambda$4, arg_0 -> CommissionMacroModule.findNearestLoadedFredInteractionEntity$lambda$5(anchor, arg_0)};
        class_1297 class_12972 = (class_1297)SequencesKt.firstOrNull((Sequence)SequencesKt.sortedWith((Sequence)SequencesKt.filter((Sequence)CollectionsKt.asSequence((Iterable)iterable2), arg_0 -> CommissionMacroModule.findNearestLoadedFredInteractionEntity$lambda$3(player, anchor, arg_0)), (Comparator)ComparisonsKt.compareBy((Function1[])function1Array)));
        if (class_12972 == null) {
            class_12972 = anchor;
        }
        return class_12972;
    }

    private final boolean entityNameMatchesFred(class_1297 entity) {
        String string = entity.method_5477().getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        String name = this.normalizeName(string);
        return Intrinsics.areEqual((Object)name, (Object)"fred") || StringsKt.endsWith$default((String)name, (String)" fred", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)name, (CharSequence)" fred ", (boolean)false, (int)2, null);
    }

    private final void applyTickRotation(class_1657 player, float targetYaw, float targetPitch, float maxYawStep, float maxPitchStep) {
        float yawStep = RangesKt.coerceIn((float)AngleUtils.INSTANCE.getRotationDelta(player.method_36454(), targetYaw), (float)(-maxYawStep), (float)maxYawStep);
        float pitchStep = RangesKt.coerceIn((float)(targetPitch - player.method_36455()), (float)(-maxPitchStep), (float)maxPitchStep);
        float nextYaw = AngleUtils.INSTANCE.normalizeAngle(player.method_36454() + yawStep);
        float nextPitch = RangesKt.coerceIn((float)(player.method_36455() + pitchStep), (float)-90.0f, (float)90.0f);
        player.method_36456(nextYaw);
        player.method_36457(nextPitch);
        player.field_6241 = nextYaw;
        player.field_6283 = nextYaw;
    }

    private final boolean nativePathActive() {
        PathStatus it = NativePathfinder.INSTANCE.getStatus();
        boolean bl = false;
        return it != PathStatus.IDLE && it != PathStatus.ARRIVED && it != PathStatus.FAILED;
    }

    private final void startEmissaryNavigation(class_2338 target) {
        PathfindingModule.INSTANCE.ensureEnabledForAutomation("commission macro");
        NativePathfinder.INSTANCE.stop();
        MovementManager.setMovementLock(false);
        NativePathfinder.INSTANCE.setTarget((double)target.method_10263() + 0.5, target.method_10264(), (double)target.method_10260() + 0.5);
        lastEmissaryPathTarget = target;
    }

    private final void ensureEmissaryWalkMovement(class_1657 player, class_2338 target) {
        if (MovementManager.forcedForward || MovementManager.forcedBackward || MovementManager.forcedLeft || MovementManager.forcedRight || MovementManager.forcedJump) {
            return;
        }
        this.applyEmissaryWalkFallbackMovement(player, target);
    }

    private final void applyEmissaryWalkFallbackMovement(class_1657 player, class_2338 target) {
        double dz;
        double dx = (double)target.method_10263() + 0.5 - player.method_23317();
        double len = Math.sqrt(dx * dx + (dz = (double)target.method_10260() + 0.5 - player.method_23321()) * dz);
        if (len < 0.05) {
            MovementManager.clearForcedMovement();
            return;
        }
        double nx = dx / len;
        double nz = dz / len;
        double yawRad = Math.toRadians(player.method_36454());
        double sinYaw = Math.sin(yawRad);
        double cosYaw = Math.cos(yawRad);
        float fwd = (float)(-nx * sinYaw + nz * cosYaw);
        float str = (float)(nx * cosYaw + nz * sinYaw);
        float threshold = 0.2f;
        MovementManager.setMovementLock(true);
        MovementManager.setForcedMovement(fwd > threshold, fwd < -threshold, str < -threshold, str > threshold, player.method_24828() && player.field_5976, false, fwd > threshold);
        player.method_5728(fwd > threshold);
    }

    private final void applyEmissaryWalkCameraRotation(class_1657 player) {
        class_243 class_2432 = this.resolveEmissaryWalkLookPoint(player);
        if (class_2432 == null) {
            return;
        }
        class_243 lookPoint = class_2432;
        class_243 class_2433 = player.method_33571();
        Intrinsics.checkNotNullExpressionValue((Object)class_2433, (String)"getEyePosition(...)");
        Rotation rotation = AngleUtils.INSTANCE.getRotation(class_2433, lookPoint);
        RotationExecutor.INSTANCE.rotateTo(rotation, emissaryWalkRotStrategy);
    }

    private final class_243 resolveEmissaryWalkLookPoint(class_1657 player) {
        int nearestIndex;
        List<class_243> nodes = NativePathfinder.INSTANCE.getCachedPathNodes();
        if (!((Collection)nodes).isEmpty() && (nearestIndex = this.nearestEmissaryWalkNodeIndex(player, nodes)) >= 0) {
            int guideIndex = RangesKt.coerceAtMost((int)(nearestIndex + 2), (int)CollectionsKt.getLastIndex(nodes));
            class_243 guideNode = nodes.get(guideIndex);
            return new class_243(guideNode.field_1352, Math.max(guideNode.field_1351 + 0.6, player.method_33571().field_1351), guideNode.field_1350);
        }
        class_2338 class_23382 = lastEmissaryPathTarget;
        if (class_23382 == null) {
            return null;
        }
        class_2338 target = class_23382;
        return new class_243((double)target.method_10263() + 0.5, (double)target.method_10264() + 0.6, (double)target.method_10260() + 0.5);
    }

    private final int nearestEmissaryWalkNodeIndex(class_1657 player, List<? extends class_243> nodes) {
        int nearestIndex = -1;
        double nearestDistSq = Double.POSITIVE_INFINITY;
        int n = ((Collection)nodes).size();
        for (int index = 0; index < n; ++index) {
            double dz;
            class_243 node = nodes.get(index);
            double dx = node.field_1352 - player.method_23317();
            double distSq = dx * dx + (dz = node.field_1350 - player.method_23321()) * dz;
            if (!(distSq < nearestDistSq)) continue;
            nearestDistSq = distSq;
            nearestIndex = index;
        }
        return nearestIndex;
    }

    private final void stopEmissaryNavigation() {
        if (this.nativePathActive()) {
            NativePathfinder.INSTANCE.stop();
        }
        MovementManager.clearForcedMovement();
        MovementManager.setMovementLock(false);
        this.setEmissaryWalkKeys(false, false, false, false, false, false, false);
        lastEmissaryPathTarget = null;
        activeEmissaryWalkNodeIndex = -1;
        RotationExecutor.INSTANCE.stopRotating();
    }

    private final void syncEmissaryWalkKeys() {
        boolean hasForcedMovement = MovementManager.hasForcedMovement;
        this.setEmissaryWalkKeys(hasForcedMovement && MovementManager.forcedForward, hasForcedMovement && MovementManager.forcedBackward, hasForcedMovement && MovementManager.forcedLeft, hasForcedMovement && MovementManager.forcedRight, hasForcedMovement && MovementManager.forcedJump, hasForcedMovement && MovementManager.forcedShift, hasForcedMovement && MovementManager.forcedSprint);
    }

    private final void setEmissaryWalkKeys(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean shift, boolean sprint) {
        block6: {
            class_304 class_3042 = CommissionMacroModule.mc.field_1690.field_1894;
            if (class_3042 != null) {
                class_3042.method_23481(forward);
            }
            class_304 class_3043 = CommissionMacroModule.mc.field_1690.field_1881;
            if (class_3043 != null) {
                class_3043.method_23481(backward);
            }
            class_304 class_3044 = CommissionMacroModule.mc.field_1690.field_1913;
            if (class_3044 != null) {
                class_3044.method_23481(left);
            }
            class_304 class_3045 = CommissionMacroModule.mc.field_1690.field_1849;
            if (class_3045 != null) {
                class_3045.method_23481(right);
            }
            class_304 class_3046 = CommissionMacroModule.mc.field_1690.field_1903;
            if (class_3046 != null) {
                class_3046.method_23481(jump);
            }
            class_304 class_3047 = CommissionMacroModule.mc.field_1690.field_1832;
            if (class_3047 != null) {
                class_3047.method_23481(shift);
            }
            class_304 class_3048 = CommissionMacroModule.mc.field_1690.field_1867;
            if (class_3048 == null) break block6;
            class_3048.method_23481(sprint);
        }
    }

    private final ParsedCommissionSelection parseCommissionSelectionFromGui(class_465<?> screen) {
        List commissions = new ArrayList();
        for (class_1735 slot : this.getCommissionCandidateSlots(screen)) {
            String string;
            String miningTarget;
            String combined;
            class_1799 item;
            if (!slot.method_7681()) continue;
            Intrinsics.checkNotNullExpressionValue((Object)slot.method_7677(), (String)"getItem(...)");
            List<String> lines = this.buildGuiTextLines(item);
            if (lines.isEmpty() || !this.looksLikeCommissionEntry(lines, combined = CollectionsKt.joinToString$default((Iterable)lines, (CharSequence)"\n", null, null, (int)0, null, null, (int)62, null)) || this.isClaimCommissionText(combined)) continue;
            Iterator iterator = this.parseCommissionProgress(combined);
            int current = ((Number)iterator.component1()).intValue();
            int max = ((Number)iterator.component2()).intValue();
            if (max > 0 && current >= max) continue;
            String miningKeyword = this.extractMiningKeyword(lines);
            if (miningKeyword != null) {
                String it;
                boolean bl = false;
                v0 = ORE_TO_TYPE.get(it);
            } else {
                v0 = miningTarget = null;
            }
            if (miningKeyword != null) {
                String it;
                boolean bl = false;
                string = ORE_TO_MINE_TYPES.get(it);
            } else {
                string = null;
            }
            String mineTypes = string;
            String combatTarget = this.extractCombatCommissionTarget(lines);
            String label = this.buildCommissionLabel(item, lines, miningTarget, combatTarget);
            if (miningTarget != null && mineTypes != null) {
                ((Collection)commissions).add(new Commission(label, CommissionType.MINING, miningTarget, mineTypes, current, max));
                continue;
            }
            if (combatTarget == null) continue;
            ((Collection)commissions).add(new Commission(label, CommissionType.COMBAT, combatTarget, combatTarget, current, max));
        }
        Iterable $this$distinctBy$iv = commissions;
        boolean $i$f$distinctBy22 = false;
        HashSet<String> set$iv = new HashSet<String>();
        ArrayList list$iv = new ArrayList();
        for (Object e$iv : $this$distinctBy$iv) {
            Commission commission = (Commission)e$iv;
            boolean bl = false;
            Object[] objectArray = new String[6];
            objectArray[0] = commission.getType().name();
            String string = commission.getLabel();
            Locale locale = Locale.US;
            Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"US");
            Intrinsics.checkNotNullExpressionValue((Object)string.toLowerCase(locale), (String)"toLowerCase(...)");
            string = commission.getTarget();
            Locale locale2 = Locale.US;
            Intrinsics.checkNotNullExpressionValue((Object)locale2, (String)"US");
            Intrinsics.checkNotNullExpressionValue((Object)string.toLowerCase(locale2), (String)"toLowerCase(...)");
            string = commission.getMineTypes();
            Locale locale3 = Locale.US;
            Intrinsics.checkNotNullExpressionValue((Object)locale3, (String)"US");
            Intrinsics.checkNotNullExpressionValue((Object)string.toLowerCase(locale3), (String)"toLowerCase(...)");
            objectArray[4] = String.valueOf(commission.getCurrent());
            objectArray[5] = String.valueOf(commission.getMax());
            String key$iv = CollectionsKt.joinToString$default((Iterable)CollectionsKt.listOf((Object[])objectArray), (CharSequence)"|", null, null, (int)0, null, null, (int)62, null);
            if (!set$iv.add(key$iv)) continue;
            list$iv.add(e$iv);
        }
        List unique = list$iv;
        Function1[] $i$f$distinctBy22 = new Function1[]{CommissionMacroModule::parseCommissionSelectionFromGui$lambda$3, CommissionMacroModule::parseCommissionSelectionFromGui$lambda$4, CommissionMacroModule::parseCommissionSelectionFromGui$lambda$5};
        List sorted = CollectionsKt.sortedWith((Iterable)unique, (Comparator)ComparisonsKt.compareBy((Function1[])$i$f$distinctBy22));
        Commission selected = (Commission)CollectionsKt.firstOrNull((List)sorted);
        return new ParsedCommissionSelection(sorted, selected);
    }

    private final int findClaimSlot(class_465<?> screen) {
        for (class_1735 slot : this.getCommissionCandidateSlots(screen)) {
            String combined;
            class_1799 item;
            if (!slot.method_7681()) continue;
            Intrinsics.checkNotNullExpressionValue((Object)slot.method_7677(), (String)"getItem(...)");
            List<String> lines = this.buildGuiTextLines(item);
            if (lines.isEmpty() || !this.isClaimableCommissionSlot(lines, combined = CollectionsKt.joinToString$default((Iterable)lines, (CharSequence)"\n", null, null, (int)0, null, null, (int)62, null))) continue;
            return slot.field_7874;
        }
        return -1;
    }

    private final boolean isClaimableCommissionSlot(List<String> lines, String combined) {
        if (!this.looksLikeCommissionEntry(lines, combined)) {
            return false;
        }
        if (this.isClaimCommissionText(combined)) {
            return true;
        }
        Pair<Integer, Integer> pair = this.parseCommissionProgress(combined);
        int current = ((Number)pair.component1()).intValue();
        int max = ((Number)pair.component2()).intValue();
        return max > 0 && current >= max;
    }

    /*
     * WARNING - void declaration
     */
    private final List<class_1735> getCommissionCandidateSlots(class_465<?> screen) {
        List list;
        void $this$filterNotTo$iv$iv;
        class_2371 class_23712 = screen.method_17577().field_7761;
        Intrinsics.checkNotNullExpressionValue((Object)class_23712, (String)"slots");
        Iterable $this$filterNot$iv = (Iterable)class_23712;
        boolean $i$f$filterNot = false;
        Iterable iterable = $this$filterNot$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterNotTo = false;
        for (Object element$iv$iv : $this$filterNotTo$iv$iv) {
            class_1735 it = (class_1735)element$iv$iv;
            boolean bl = false;
            if (it.field_7871 instanceof class_1661) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        List containerSlots = (List)destination$iv$iv;
        if (!((Collection)containerSlots).isEmpty()) {
            list = containerSlots;
        } else {
            class_2371 class_23713 = screen.method_17577().field_7761;
            Intrinsics.checkNotNullExpressionValue((Object)class_23713, (String)"slots");
            list = (List)class_23713;
        }
        return list;
    }

    /*
     * Unable to fully structure code
     */
    private final List<String> buildGuiTextLines(class_1799 item) {
        lines = new ArrayList<E>();
        v0 = class_124.method_539((String)item.method_7964().getString());
        if (v0 == null) ** GOTO lbl-1000
        v1 = v0.toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue((Object)v1, (String)"toLowerCase(...)");
        v0 = v1;
        if (v1 != null) {
            v2 = StringsKt.trim((CharSequence)v0).toString();
        } else lbl-1000:
        // 2 sources

        {
            v2 = v3 = null;
        }
        if (v2 == null) {
            v3 = "";
        }
        if (StringsKt.isBlank((CharSequence)(name = v3)) == false) {
            ((Collection)lines).add(name);
        }
        var4_4 = lines;
        var5_5 = ItemUtilsKt.getLoreLines(item);
        $i$f$mapNotNull = false;
        var7_7 = $this$mapNotNull$iv;
        destination$iv$iv = new ArrayList<E>();
        $i$f$mapNotNullTo = false;
        $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
        $i$f$forEach = false;
        var12_13 = $this$forEach$iv$iv$iv.iterator();
        while (var12_13.hasNext()) {
            element$iv$iv = element$iv$iv$iv = var12_13.next();
            $i$a$-forEach-CollectionsKt___CollectionsKt$mapNotNullTo$1$iv$iv = false;
            it = (class_2561)element$iv$iv;
            $i$a$-mapNotNull-CommissionMacroModule$buildGuiTextLines$1 = false;
            v4 = class_124.method_539((String)it.getString());
            if (v4 == null) ** GOTO lbl-1000
            v5 = v4.toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue((Object)v5, (String)"toLowerCase(...)");
            v4 = v5;
            if (v5 != null) {
                v6 = StringsKt.trim((CharSequence)v4).toString();
            } else lbl-1000:
            // 2 sources

            {
                v6 = null;
            }
            if (v6 == null) continue;
            it$iv$iv = v6;
            $i$a$-let-CollectionsKt___CollectionsKt$mapNotNullTo$1$1$iv$iv = false;
            destination$iv$iv.add(it$iv$iv);
        }
        $this$mapNotNull$iv = (List)destination$iv$iv;
        $i$f$filter = false;
        $this$mapNotNullTo$iv$iv = $this$filter$iv;
        destination$iv$iv = new ArrayList<E>();
        $i$f$filterTo = false;
        for (T element$iv$iv : $this$filterTo$iv$iv) {
            it = (String)element$iv$iv;
            $i$a$-filter-CommissionMacroModule$buildGuiTextLines$2 = false;
            v7 = !StringsKt.isBlank((CharSequence)it);
            if (!v7) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        var5_5 = (List)destination$iv$iv;
        CollectionsKt.addAll((Collection)var4_4, (Iterable)var5_5);
        return lines;
    }

    private final String buildCommissionLabel(class_1799 item, List<String> lines, String miningTarget, String combatTarget) {
        CharSequence charSequence;
        String itemName;
        String string;
        String string2;
        CharSequence charSequence22;
        Regex regex;
        String it2;
        String string3 = this.findKnownCommissionLabel(lines);
        if (string3 != null) {
            String it2 = string3;
            boolean charSequence22 = false;
            return it2;
        }
        CharSequence charSequence3 = class_124.method_539((String)item.method_7964().getString());
        if ((charSequence3 != null && (it2 = (regex = new Regex("\\s+")).replace(charSequence22 = (CharSequence)charSequence3, string2 = " ")) != null ? ((Object)StringsKt.trim((CharSequence)it2)).toString() : (string = null)) == null) {
            string = "";
        }
        if (!StringsKt.isBlank((CharSequence)(itemName = string)) && !new Regex("Commission\\s*#\\d+", RegexOption.IGNORE_CASE).matches(charSequence3 = (CharSequence)itemName)) {
            charSequence3 = this.canonicalizeKnownCommissionLabel(itemName);
            if (charSequence3 != null) {
                CharSequence it3 = charSequence3;
                boolean bl = false;
                return it3;
            }
            return itemName;
        }
        if (miningTarget != null) {
            return this.buildMiningCommissionLabel(lines, miningTarget);
        }
        if (combatTarget != null) {
            return this.buildCombatCommissionLabel(lines, combatTarget);
        }
        charSequence3 = itemName;
        if (StringsKt.isBlank((CharSequence)charSequence3)) {
            boolean bl = false;
            charSequence = "Unknown Commission";
        } else {
            charSequence = charSequence3;
        }
        return charSequence;
    }

    private final String findKnownCommissionLabel(List<String> lines) {
        String string;
        block1: {
            for (String p0 : (Iterable)lines) {
                boolean bl = false;
                String string2 = this.canonicalizeKnownCommissionLabel(p0);
                if (string2 == null) continue;
                string = string2;
                break block1;
            }
            string = null;
        }
        return string;
    }

    private final String buildMiningCommissionLabel(List<String> lines, String miningTarget) {
        String area = this.extractMiningAreaLabel(lines);
        String suffix = this.miningCommissionSuffix(miningTarget);
        if (area != null) {
            return area + " " + suffix;
        }
        String string = suffix;
        return Intrinsics.areEqual((Object)string, (Object)"Titanium") ? "Titanium Miner" : (Intrinsics.areEqual((Object)string, (Object)"Mithril") ? "Mithril Miner" : suffix);
    }

    private final String buildCombatCommissionLabel(List<String> lines, String combatTarget) {
        Object object;
        String normalizedTarget = this.normalizeComparisonText(combatTarget);
        if (StringsKt.contains$default((CharSequence)normalizedTarget, (CharSequence)"glacite walker", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)normalizedTarget, (CharSequence)"ice walker", (boolean)false, (int)2, null)) {
            object = "Glacite Walker Slayer";
        } else if (StringsKt.contains$default((CharSequence)normalizedTarget, (CharSequence)"star sentry", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)normalizedTarget, (CharSequence)"crystal sentry", (boolean)false, (int)2, null)) {
            object = "Star Sentry Puncher";
        } else if (StringsKt.contains$default((CharSequence)normalizedTarget, (CharSequence)"treasure hoarder", (boolean)false, (int)2, null)) {
            object = "Treasure Hoarder Puncher";
        } else if (StringsKt.contains$default((CharSequence)normalizedTarget, (CharSequence)"golden goblin", (boolean)false, (int)2, null)) {
            object = "Golden Goblin Slayer";
        } else {
            boolean bl;
            block20: {
                Iterable $this$any$iv = lines;
                boolean $i$f$any = false;
                if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                    bl = false;
                } else {
                    for (Object element$iv : $this$any$iv) {
                        String it = (String)element$iv;
                        boolean bl2 = false;
                        if (!StringsKt.contains$default((CharSequence)INSTANCE.normalizeComparisonText(it), (CharSequence)"goblin raid", (boolean)false, (int)2, null)) continue;
                        bl = true;
                        break block20;
                    }
                    bl = false;
                }
            }
            if (bl) {
                object = "Goblin Raid";
            } else if (StringsKt.contains$default((CharSequence)normalizedTarget, (CharSequence)"goblin", (boolean)false, (int)2, null)) {
                object = "Goblin Slayer";
            } else {
                String string;
                Object v3;
                block22: {
                    Iterable $this$firstOrNull$iv = lines;
                    boolean $i$f$firstOrNull = false;
                    for (Object element$iv : $this$firstOrNull$iv) {
                        boolean bl3;
                        block21: {
                            String it = (String)element$iv;
                            boolean bl4 = false;
                            Iterable $this$any$iv = COMMISSION_OBJECTIVE_PREFIXES;
                            boolean $i$f$any = false;
                            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                                bl3 = false;
                            } else {
                                for (Object element$iv2 : $this$any$iv) {
                                    String p0 = (String)element$iv2;
                                    boolean bl5 = false;
                                    if (!StringsKt.startsWith$default((String)it, (String)p0, (boolean)false, (int)2, null)) continue;
                                    bl3 = true;
                                    break block21;
                                }
                                bl3 = false;
                            }
                        }
                        if (!bl3) continue;
                        v3 = element$iv;
                        break block22;
                    }
                    v3 = null;
                }
                if ((string = (String)v3) == null) {
                    string = "";
                }
                String objectiveLine = string;
                CharSequence $i$f$firstOrNull = StringsKt.substringBefore$default((String)StringsKt.substringAfter((String)objectiveLine, (String)" in ", (String)""), (String)" progress", null, (int)2, null);
                Regex regex = new Regex("[^a-z0-9' -]");
                String string2 = " ";
                $i$f$firstOrNull = regex.replace($i$f$firstOrNull, string2);
                regex = new Regex("\\s+");
                string2 = " ";
                String area = ((Object)StringsKt.trim((CharSequence)regex.replace($i$f$firstOrNull, string2))).toString();
                String targetLabel = this.titleCase(combatTarget);
                object = !StringsKt.isBlank((CharSequence)area) && !Intrinsics.areEqual((Object)area, (Object)objectiveLine) ? this.titleCase(area) + " " + targetLabel : targetLabel;
            }
        }
        return object;
    }

    private final String canonicalizeKnownCommissionLabel(String raw) {
        Object v0;
        block1: {
            String normalized = this.normalizeComparisonText(raw);
            Iterable $this$firstOrNull$iv = COMMISSION_PRIORITY;
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                String it = (String)element$iv;
                boolean bl = false;
                if (!Intrinsics.areEqual((Object)INSTANCE.normalizeComparisonText(it), (Object)normalized)) continue;
                v0 = element$iv;
                break block1;
            }
            v0 = null;
        }
        return v0;
    }

    private final String extractMiningAreaLabel(List<String> lines) {
        String objectiveLine;
        String inlineArea;
        Object object;
        String string;
        Object v0;
        block4: {
            Iterable $this$firstOrNull$iv = lines;
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                String it = (String)element$iv;
                boolean bl = false;
                if (!StringsKt.startsWith$default((String)it, (String)"mine ", (boolean)false, (int)2, null)) continue;
                v0 = element$iv;
                break block4;
            }
            v0 = null;
        }
        if ((string = (String)v0) == null) {
            string = "";
        }
        if ((object = this.canonicalizeMiningArea(inlineArea = ((Object)StringsKt.trim((CharSequence)StringsKt.substringBefore$default((String)StringsKt.substringAfter((String)(objectiveLine = string), (String)" in ", (String)""), (String)" progress", null, (int)2, null))).toString())) != null) {
            String it = object;
            boolean bl = false;
            return it;
        }
        for (String line : lines) {
            String string2 = this.canonicalizeMiningArea(line);
            if (string2 == null) continue;
            String it = string2;
            boolean bl = false;
            return it;
        }
        return null;
    }

    private final String canonicalizeMiningArea(String raw) {
        Object v2;
        block2: {
            String normalized = this.normalizeComparisonText(raw);
            if (StringsKt.isBlank((CharSequence)normalized)) {
                return null;
            }
            Set<Map.Entry<String, String>> set = MINING_AREA_ALIASES.entrySet();
            Intrinsics.checkNotNullExpressionValue(set, (String)"<get-entries>(...)");
            Iterable $this$firstOrNull$iv = set;
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                Map.Entry entry = (Map.Entry)element$iv;
                boolean bl = false;
                Intrinsics.checkNotNull((Object)entry);
                Object k = entry.getKey();
                Intrinsics.checkNotNullExpressionValue(k, (String)"component1(...)");
                String alias = (String)k;
                if (!StringsKt.contains$default((CharSequence)normalized, (CharSequence)alias, (boolean)false, (int)2, null)) continue;
                v2 = element$iv;
                break block2;
            }
            v2 = null;
        }
        Map.Entry entry = v2;
        return entry != null ? (String)entry.getValue() : null;
    }

    private final String miningCommissionSuffix(String miningTarget) {
        String normalized = MiningBlockRegistry.INSTANCE.normalizeType(miningTarget);
        return Intrinsics.areEqual((Object)normalized, (Object)"Titanium") ? "Titanium" : (StringsKt.startsWith$default((String)normalized, (String)"Mithril", (boolean)false, (int)2, null) ? "Mithril" : normalized);
    }

    private final int commissionSelectionRank(Commission commission) {
        String normalizedLabel = this.normalizeComparisonText(commission.getLabel());
        Integer n = COMMISSION_PRIORITY_INDEX.get(normalizedLabel);
        if (n != null) {
            int it = ((Number)n).intValue();
            boolean bl = false;
            return it;
        }
        return switch (WhenMappings.$EnumSwitchMapping$1[commission.getType().ordinal()]) {
            case 1 -> {
                switch (this.miningCommissionSuffix(commission.getTarget())) {
                    case "Titanium": {
                        yield COMMISSION_PRIORITY.size() + 10;
                    }
                    case "Mithril": {
                        yield COMMISSION_PRIORITY.size() + 11;
                    }
                    case "Glacite": {
                        yield COMMISSION_PRIORITY.size() + 20;
                    }
                    case "Umber": {
                        yield COMMISSION_PRIORITY.size() + 21;
                    }
                    case "Tungsten": {
                        yield COMMISSION_PRIORITY.size() + 22;
                    }
                }
                yield COMMISSION_PRIORITY.size() + 40;
            }
            case 2 -> {
                if (StringsKt.contains$default((CharSequence)normalizedLabel, (CharSequence)"glacite walker", (boolean)false, (int)2, null)) {
                    yield COMMISSION_PRIORITY.size() + 30;
                }
                if (StringsKt.contains$default((CharSequence)normalizedLabel, (CharSequence)"goblin", (boolean)false, (int)2, null)) {
                    yield COMMISSION_PRIORITY.size() + 31;
                }
                yield COMMISSION_PRIORITY.size() + 50;
            }
            default -> throw new NoWhenBranchMatchedException();
        };
    }

    /*
     * WARNING - void declaration
     */
    private final String titleCase(String value) {
        void $this$filterTo$iv$iv;
        CharSequence charSequence = value;
        Regex regex = new Regex("\\s+");
        int n = 0;
        Iterable $this$filter$iv = regex.split(charSequence, n);
        boolean $i$f$filter = false;
        Iterable iterable = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            String it = (String)element$iv$iv;
            boolean bl = false;
            boolean bl2 = !StringsKt.isBlank((CharSequence)it);
            if (!bl2) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        return CollectionsKt.joinToString$default((Iterable)((List)destination$iv$iv), (CharSequence)" ", null, null, (int)0, null, CommissionMacroModule::titleCase$lambda$1, (int)30, null);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private final String normalizeComparisonText(String value) {
        String string = class_124.method_539((String)value);
        if (string == null) return "";
        String string2 = string;
        Locale locale = Locale.US;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"US");
        String string3 = string2.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
        String string4 = string3;
        if (string4 == null) return "";
        string2 = StringsKt.replace$default((String)string4, (String)"&", (String)" and ", (boolean)false, (int)4, null);
        if (string2 == null) return "";
        Regex regex = new Regex("[^a-z0-9' ]");
        CharSequence charSequence = string2;
        String string5 = " ";
        String string6 = regex.replace(charSequence, string5);
        if (string6 == null) return "";
        string5 = new Regex("\\s+");
        CharSequence charSequence2 = string6;
        String string7 = " ";
        charSequence = string5.replace(charSequence2, string7);
        if (charSequence == null) return "";
        String string8 = ((Object)StringsKt.trim((CharSequence)charSequence)).toString();
        String string9 = string8;
        if (string8 != null) return string9;
        return "";
    }

    private final boolean looksLikeCommissionEntry(List<String> lines, String combined) {
        boolean bl;
        block13: {
            String firstLine;
            String string = (String)CollectionsKt.firstOrNull(lines);
            if (string == null) {
                string = "";
            }
            if (StringsKt.contains$default((CharSequence)(firstLine = string), (CharSequence)"close", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)firstLine, (CharSequence)"back", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)firstLine, (CharSequence)"next page", (boolean)false, (int)2, null)) {
                return false;
            }
            if (StringsKt.contains$default((CharSequence)combined, (CharSequence)"royal pigeon", (boolean)false, (int)2, null)) {
                return false;
            }
            String normalizedFirstLine = this.normalizeComparisonText(firstLine);
            if (this.canonicalizeKnownCommissionLabel(firstLine) != null) {
                return true;
            }
            if (Intrinsics.areEqual((Object)normalizedFirstLine, (Object)"goblin raid") || Intrinsics.areEqual((Object)normalizedFirstLine, (Object)"star sentry puncher")) {
                return true;
            }
            if (this.isClaimCommissionText(combined)) {
                return true;
            }
            Iterable $this$any$iv = lines;
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                bl = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    boolean bl2;
                    block12: {
                        String line = (String)element$iv;
                        boolean bl3 = false;
                        Iterable $this$any$iv2 = COMMISSION_OBJECTIVE_PREFIXES;
                        boolean $i$f$any2 = false;
                        if ($this$any$iv2 instanceof Collection && ((Collection)$this$any$iv2).isEmpty()) {
                            bl2 = false;
                        } else {
                            for (Object element$iv2 : $this$any$iv2) {
                                String p0 = (String)element$iv2;
                                boolean bl4 = false;
                                if (!StringsKt.startsWith$default((String)line, (String)p0, (boolean)false, (int)2, null)) continue;
                                bl2 = true;
                                break block12;
                            }
                            bl2 = false;
                        }
                    }
                    if (!bl2) continue;
                    bl = true;
                    break block13;
                }
                bl = false;
            }
        }
        boolean hasObjectiveLine = bl;
        boolean hasProgressMarker = StringsKt.contains$default((CharSequence)combined, (CharSequence)" progress", (boolean)false, (int)2, null) || new Regex("([0-9,]+)\\s*/\\s*([0-9,]+)").containsMatchIn((CharSequence)combined) || new Regex("([0-9]{1,3}(?:\\.[0-9]+)?)\\s*%").containsMatchIn((CharSequence)combined);
        return hasObjectiveLine && hasProgressMarker;
    }

    private final boolean isClaimCommissionText(String combined) {
        String normalized = this.normalizeComparisonText(combined);
        return StringsKt.contains$default((CharSequence)normalized, (CharSequence)"click to claim", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)normalized, (CharSequence)"click here to claim", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)normalized, (CharSequence)"claim reward", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)normalized, (CharSequence)"claim rewards", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)normalized, (CharSequence)"claim commission", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)normalized, (CharSequence)"commission complete", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)normalized, (CharSequence)"commission completed", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)normalized, (CharSequence)"claim", (boolean)false, (int)2, null) && StringsKt.contains$default((CharSequence)normalized, (CharSequence)"reward", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)normalized, (CharSequence)"completed", (boolean)false, (int)2, null);
    }

    private final Pair<Integer, Integer> parseCommissionProgress(String combined) {
        MatchResult ratioMatch = Regex.find$default((Regex)new Regex("([0-9,]+)\\s*/\\s*([0-9,]+)"), (CharSequence)combined, (int)0, (int)2, null);
        if (ratioMatch != null) {
            Integer n = StringsKt.toIntOrNull((String)StringsKt.replace$default((String)((String)ratioMatch.getGroupValues().get(1)), (String)",", (String)"", (boolean)false, (int)4, null));
            int current = n != null ? n : 0;
            Integer n2 = StringsKt.toIntOrNull((String)StringsKt.replace$default((String)((String)ratioMatch.getGroupValues().get(2)), (String)",", (String)"", (boolean)false, (int)4, null));
            int max = n2 != null ? n2 : 100;
            return TuplesKt.to((Object)current, (Object)RangesKt.coerceAtLeast((int)max, (int)1));
        }
        MatchResult percentMatch = Regex.find$default((Regex)new Regex("([0-9]{1,3}(?:\\.[0-9]+)?)\\s*%"), (CharSequence)combined, (int)0, (int)2, null);
        if (percentMatch != null) {
            Double d = StringsKt.toDoubleOrNull((String)((String)percentMatch.getGroupValues().get(1)));
            double percent = d != null ? d : 0.0;
            return TuplesKt.to((Object)RangesKt.coerceIn((int)((int)percent), (int)0, (int)100), (Object)100);
        }
        if (this.isClaimCommissionText(combined)) {
            return TuplesKt.to((Object)100, (Object)100);
        }
        return TuplesKt.to((Object)0, (Object)100);
    }

    private final String extractMiningKeyword(List<String> lines) {
        for (String line : lines) {
            boolean bl;
            block5: {
                Iterable $this$none$iv = MINING_OBJECTIVE_PREFIXES;
                boolean $i$f$none = false;
                if ($this$none$iv instanceof Collection && ((Collection)$this$none$iv).isEmpty()) {
                    bl = true;
                } else {
                    for (Object element$iv : $this$none$iv) {
                        String p0 = (String)element$iv;
                        boolean bl2 = false;
                        if (!StringsKt.startsWith$default((String)line, (String)p0, (boolean)false, (int)2, null)) continue;
                        bl = false;
                        break block5;
                    }
                    bl = true;
                }
            }
            if (bl) continue;
            Iterator<String> iterator = ORE_TO_TYPE.keySet().iterator();
            while (iterator.hasNext()) {
                String keyword;
                Intrinsics.checkNotNullExpressionValue((Object)iterator.next(), (String)"next(...)");
                if (!StringsKt.contains$default((CharSequence)line, (CharSequence)keyword, (boolean)false, (int)2, null)) continue;
                return keyword;
            }
        }
        return null;
    }

    private final String extractCombatCommissionTarget(List<String> lines) {
        Regex objectiveRe = new Regex("(?:kill|slay|defeat|punch|damage)\\s+(?:[0-9,]+\\s*/\\s*[0-9,]+\\s+)?(?:[0-9,]+\\s+)?(.+)");
        for (String line : lines) {
            MatchResult match;
            if (Regex.find$default((Regex)objectiveRe, (CharSequence)line, (int)0, (int)2, null) == null) continue;
            CharSequence charSequence = StringsKt.substringBefore$default((String)StringsKt.substringBefore$default((String)((String)match.getGroupValues().get(1)), (String)" progress", null, (int)2, null), (String)" in ", null, (int)2, null);
            Regex regex = new Regex("[^a-z0-9' -]");
            String string = " ";
            regex = new Regex("\\s+");
            String rawTarget = ((Object)StringsKt.trim((CharSequence)regex.replace(charSequence = (CharSequence)regex.replace(charSequence, string), string = " "))).toString();
            if (StringsKt.isBlank((CharSequence)rawTarget)) continue;
            String target = StringsKt.removeSuffix((String)StringsKt.removeSuffix((String)rawTarget, (CharSequence)" mobs"), (CharSequence)" mob");
            boolean bl = false;
            return StringsKt.endsWith$default((String)target, (String)"ies", (boolean)false, (int)2, null) && target.length() > 3 ? StringsKt.dropLast((String)target, (int)3) + "y" : (StringsKt.endsWith$default((String)target, (String)"s", (boolean)false, (int)2, null) && !StringsKt.endsWith$default((String)target, (String)"ss", (boolean)false, (int)2, null) && target.length() > 3 ? StringsKt.dropLast((String)target, (int)1) : target);
        }
        return null;
    }

    private final void startWorkModule(Commission c) {
        switch (WhenMappings.$EnumSwitchMapping$1[c.getType().ordinal()]) {
            case 1: {
                String routeName = this.findMatchingRouteName(c);
                if (routeName != null && RoutesModule.loadAndStartAutomationRoute$default(RoutesModule.INSTANCE, routeName, false, false, "commission automation", 4, null)) {
                    activeMiningRouteName = routeName;
                    ChatUtils.sendMessage("Commission Macro: using route \"" + routeName + "\" for " + c.getLabel() + ".");
                    return;
                }
                if (routeName != null) {
                    ChatUtils.sendMessage("Commission Macro: route \"" + routeName + "\" could not be started. Falling back to mining macro.");
                } else {
                    ChatUtils.sendMessage("Commission Macro: no route assigned or matched for " + c.getLabel() + ". Falling back to mining macro.");
                }
                activeMiningRouteName = null;
                MiningMacroModule.startForAutomation$default(MiningMacroModule.INSTANCE, this.preferredMiningMacroMineTypes(c), null, null, null, 14, null);
                break;
            }
            case 2: {
                this.startCombatWorkModule(c);
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
    }

    private final void stopWorkModule() {
        Commission commission = CommissionMacroModule.commission;
        CommissionType commissionType = commission != null ? commission.getType() : null;
        switch (commissionType == null ? -1 : WhenMappings.$EnumSwitchMapping$1[commissionType.ordinal()]) {
            case 1: {
                if (activeMiningRouteName != null && RoutesModule.INSTANCE.isRunning()) {
                    RoutesModule.stopForAutomation$default(RoutesModule.INSTANCE, null, 1, null);
                }
                activeMiningRouteName = null;
                MiningMacroModule.INSTANCE.stopForAutomation();
                break;
            }
            case 2: {
                CombatMacroModule.INSTANCE.stopForAutomation();
                break;
            }
            case -1: {
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
    }

    private final boolean workModuleIsActive(Commission c) {
        return switch (WhenMappings.$EnumSwitchMapping$1[c.getType().ordinal()]) {
            case 1 -> {
                if (activeMiningRouteName != null && RoutesModule.INSTANCE.isRunning() || MiningMacroModule.INSTANCE.isActive()) {
                    yield true;
                }
                yield false;
            }
            case 2 -> CombatMacroModule.INSTANCE.isActive();
            default -> throw new NoWhenBranchMatchedException();
        };
    }

    /*
     * WARNING - void declaration
     */
    private final String findMatchingRouteName(Commission c) {
        List areaRoutes;
        String targetZone;
        List namedZoneRoutes;
        List list;
        List list2;
        Iterable $this$filter$iv;
        void $this$mapTo$iv$iv;
        void $this$filterTo$iv$iv;
        Iterable $this$filter$iv2;
        void $this$mapTo$iv$iv2;
        Iterable $this$map$iv;
        Object object;
        void $this$associateByTo$iv$iv;
        List<RoutesModule.SavedRouteInfo> availableRoutes = RoutesModule.INSTANCE.getSavedRouteInfos();
        String string = this.resolveAssignedRouteName(c, availableRoutes);
        if (string != null) {
            String it = string;
            boolean bl = false;
            return it;
        }
        Iterable $this$associateBy$iv = availableRoutes;
        boolean $i$f$associateBy = false;
        int capacity$iv22 = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateBy$iv, (int)10)), (int)16);
        Iterable iterable = $this$associateBy$iv;
        Map destination$iv$iv = new LinkedHashMap(capacity$iv22);
        boolean $i$f$associateByTo = false;
        for (Object element$iv$iv : $this$associateByTo$iv$iv) {
            void it;
            RoutesModule.SavedRouteInfo savedRouteInfo = (RoutesModule.SavedRouteInfo)element$iv$iv;
            object = destination$iv$iv;
            boolean bl = false;
            object.put(it.getName(), element$iv$iv);
        }
        Map availableRoutesByName = destination$iv$iv;
        List selectedRoutes = this.resolveSelectableRoutes(c, availableRoutesByName);
        if (selectedRoutes.isEmpty()) {
            return null;
        }
        Object capacity$iv22 = new char[]{','};
        capacity$iv22 = StringsKt.split$default((CharSequence)c.getMineTypes(), (char[])capacity$iv22, (boolean)false, (int)0, (int)6, null);
        boolean $i$f$map = false;
        destination$iv$iv = $this$map$iv;
        Iterable destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv2) {
            void it;
            String bl = (String)item$iv$iv;
            object = destination$iv$iv2;
            boolean bl2 = false;
            object.add(((Object)StringsKt.trim((CharSequence)((CharSequence)it))).toString());
        }
        $this$map$iv = (List)destination$iv$iv2;
        boolean $i$f$filter22 = false;
        $this$mapTo$iv$iv2 = $this$filter$iv2;
        destination$iv$iv2 = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            String it = (String)element$iv$iv;
            boolean bl = false;
            boolean bl3 = ((CharSequence)it).length() > 0;
            if (!bl3) continue;
            destination$iv$iv2.add(element$iv$iv);
        }
        $this$filter$iv2 = (List)destination$iv$iv2;
        MiningBlockRegistry $i$f$filter22 = MiningBlockRegistry.INSTANCE;
        boolean $i$f$map2 = false;
        destination$iv$iv2 = $this$map$iv;
        Collection destination$iv$iv3 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo2 = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void p0;
            String bl = (String)item$iv$iv;
            object = destination$iv$iv3;
            boolean bl4 = false;
            object.add($i$f$filter22.normalizeType((String)p0));
        }
        Set requiredTypes = CollectionsKt.toSet((Iterable)((List)destination$iv$iv3));
        Set<String> preferredTypes = this.preferredRouteMineTypes(c, requiredTypes);
        CommissionRouteZone commissionRouteZone = this.resolveCommissionRouteZone(c.getLabel());
        if (commissionRouteZone != null) {
            void $this$filterTo$iv$iv2;
            CommissionRouteZone zone = commissionRouteZone;
            boolean bl = false;
            $this$filter$iv = selectedRoutes;
            boolean $i$f$filter = false;
            Iterable p0 = $this$filter$iv;
            Collection destination$iv$iv4 = new ArrayList();
            boolean $i$f$filterTo2 = false;
            for (Object element$iv$iv : $this$filterTo$iv$iv2) {
                RoutesModule.SavedRouteInfo it = (RoutesModule.SavedRouteInfo)element$iv$iv;
                boolean bl5 = false;
                if (!INSTANCE.routeMatchesZone(it.getName(), zone)) continue;
                destination$iv$iv4.add(element$iv$iv);
            }
            list2 = (List)destination$iv$iv4;
        } else {
            list2 = list = null;
        }
        if (list2 == null) {
            list = namedZoneRoutes = CollectionsKt.emptyList();
        }
        if ((targetZone = ORE_TO_ZONE.get(c.getTarget())) != null) {
            void $this$filterTo$iv$iv3;
            Iterable $this$filter$iv3 = selectedRoutes;
            boolean $i$f$filter = false;
            $this$filter$iv = $this$filter$iv3;
            Collection destination$iv$iv5 = new ArrayList();
            boolean $i$f$filterTo3 = false;
            for (Object element$iv$iv : $this$filterTo$iv$iv3) {
                boolean bl;
                block23: {
                    RoutesModule.SavedRouteInfo route = (RoutesModule.SavedRouteInfo)element$iv$iv;
                    boolean bl6 = false;
                    Iterable $this$any$iv = route.getMineTypes();
                    boolean $i$f$any = false;
                    if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                        bl = false;
                    } else {
                        for (Object element$iv : $this$any$iv) {
                            String type = (String)element$iv;
                            boolean bl7 = false;
                            if (!Intrinsics.areEqual((Object)ORE_TO_ZONE.get(type), (Object)targetZone)) continue;
                            bl = true;
                            break block23;
                        }
                        bl = false;
                    }
                }
                if (!bl) continue;
                destination$iv$iv5.add(element$iv$iv);
            }
            v5 = (List)destination$iv$iv5;
        } else {
            v5 = areaRoutes = CollectionsKt.emptyList();
        }
        if (this.shouldUseRandomAssignedCommissionRoute(c)) {
            List list3;
            List $i$f$filter = areaRoutes;
            if ($i$f$filter.isEmpty()) {
                boolean bl = false;
                list3 = selectedRoutes;
            } else {
                list3 = $i$f$filter;
            }
            List randomPool = CollectionsKt.shuffled((Iterable)list3);
            String string2 = this.selectRouteByType(randomPool, preferredTypes);
            if (string2 == null) {
                RoutesModule.SavedRouteInfo savedRouteInfo = (RoutesModule.SavedRouteInfo)CollectionsKt.firstOrNull((List)randomPool);
                string2 = savedRouteInfo != null ? savedRouteInfo.getName() : null;
            }
            return string2;
        }
        boolean hasZone = !((Collection)namedZoneRoutes).isEmpty();
        List areaRoutePool = hasZone ? areaRoutes : CollectionsKt.shuffled((Iterable)areaRoutes);
        List selectedRoutePool = hasZone ? selectedRoutes : CollectionsKt.shuffled((Iterable)selectedRoutes);
        String string3 = this.selectRouteByCommissionName(namedZoneRoutes, c);
        if (string3 == null && (string3 = this.selectRouteByType(namedZoneRoutes, preferredTypes)) == null) {
            RoutesModule.SavedRouteInfo savedRouteInfo = (RoutesModule.SavedRouteInfo)CollectionsKt.firstOrNull((List)namedZoneRoutes);
            string3 = savedRouteInfo != null ? savedRouteInfo.getName() : null;
            if (string3 == null && (string3 = this.selectRouteByCommissionName(areaRoutePool, c)) == null && (string3 = this.selectRouteByCommissionName(selectedRoutePool, c)) == null && (string3 = this.selectRouteByType(areaRoutePool, preferredTypes)) == null) {
                RoutesModule.SavedRouteInfo savedRouteInfo2 = (RoutesModule.SavedRouteInfo)CollectionsKt.firstOrNull((List)areaRoutePool);
                string3 = savedRouteInfo2 != null ? savedRouteInfo2.getName() : null;
                if (string3 == null && (string3 = this.selectRouteByType(selectedRoutePool, preferredTypes)) == null) {
                    RoutesModule.SavedRouteInfo savedRouteInfo3 = (RoutesModule.SavedRouteInfo)CollectionsKt.firstOrNull((List)selectedRoutePool);
                    string3 = savedRouteInfo3 != null ? savedRouteInfo3.getName() : null;
                }
            }
        }
        return string3;
    }

    private final List<RoutesModule.SavedRouteInfo> resolveSelectableRoutes(Commission commission, Map<String, RoutesModule.SavedRouteInfo> availableRoutesByName) {
        Iterable $this$distinctBy$iv;
        Object it$iv$iv;
        boolean bl;
        String name;
        boolean bl2;
        Object element$iv$iv;
        Object element$iv$iv$iv;
        Iterable $this$mapNotNullTo$iv$iv;
        Iterable $this$mapNotNull$iv;
        Iterable iterable = this.getSelectedRouteNames();
        boolean $i$f$mapNotNull = false;
        void var6_5 = $this$mapNotNull$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$mapNotNullTo = false;
        Iterable $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
        boolean $i$f$forEach = false;
        Iterator iterator = $this$forEach$iv$iv$iv.iterator();
        while (iterator.hasNext()) {
            element$iv$iv = element$iv$iv$iv = iterator.next();
            bl2 = false;
            name = (String)element$iv$iv;
            boolean bl3 = false;
            if (INSTANCE.findRouteInfoByName(availableRoutesByName, name) == null) continue;
            bl = false;
            destination$iv$iv.add(it$iv$iv);
        }
        $this$mapNotNull$iv = (List)destination$iv$iv;
        boolean $i$f$distinctBy = false;
        Iterable<String> set$iv = new HashSet<String>();
        ArrayList list$iv = new ArrayList();
        for (Object e$iv : $this$distinctBy$iv) {
            RoutesModule.SavedRouteInfo it = (RoutesModule.SavedRouteInfo)e$iv;
            boolean bl4 = false;
            String key$iv = INSTANCE.normalizeComparisonText(it.getName());
            if (!((HashSet)set$iv).add(key$iv)) continue;
            list$iv.add(e$iv);
        }
        List selectedRoutes = list$iv;
        if (!((Collection)selectedRoutes).isEmpty()) {
            return selectedRoutes;
        }
        if (!this.shouldUseRandomAssignedCommissionRoute(commission)) {
            return CollectionsKt.emptyList();
        }
        $this$distinctBy$iv = this.getRouteZones();
        $i$f$mapNotNull = false;
        set$iv = $this$mapNotNull$iv;
        destination$iv$iv = new ArrayList();
        $i$f$mapNotNullTo = false;
        $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
        $i$f$forEach = false;
        Iterator bl4 = $this$forEach$iv$iv$iv.iterator();
        while (bl4.hasNext()) {
            element$iv$iv = element$iv$iv$iv = bl4.next();
            bl2 = false;
            CommissionRouteZone p0 = (CommissionRouteZone)((Object)element$iv$iv);
            boolean bl5 = false;
            if (this.getAssignedRouteName(p0) == null) continue;
            bl = false;
            destination$iv$iv.add(it$iv$iv);
        }
        $this$mapNotNull$iv = (List)destination$iv$iv;
        $i$f$mapNotNull = false;
        $this$mapNotNullTo$iv$iv = $this$mapNotNull$iv;
        destination$iv$iv = new ArrayList();
        $i$f$mapNotNullTo = false;
        $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
        $i$f$forEach = false;
        bl4 = $this$forEach$iv$iv$iv.iterator();
        while (bl4.hasNext()) {
            element$iv$iv = element$iv$iv$iv = bl4.next();
            bl2 = false;
            name = (String)element$iv$iv;
            boolean bl6 = false;
            if (INSTANCE.findRouteInfoByName(availableRoutesByName, name) == null) continue;
            bl = false;
            destination$iv$iv.add(it$iv$iv);
        }
        $this$mapNotNull$iv = (List)destination$iv$iv;
        $i$f$distinctBy = false;
        set$iv = new HashSet();
        list$iv = new ArrayList();
        for (Object e$iv : $this$distinctBy$iv) {
            RoutesModule.SavedRouteInfo it = (RoutesModule.SavedRouteInfo)e$iv;
            boolean bl7 = false;
            String key$iv = INSTANCE.normalizeComparisonText(it.getName());
            if (!((HashSet)set$iv).add(key$iv)) continue;
            list$iv.add(e$iv);
        }
        return list$iv;
    }

    private final boolean shouldUseRandomAssignedCommissionRoute(Commission c) {
        if (c.getType() != CommissionType.MINING) {
            return false;
        }
        if (this.resolveCommissionRouteZone(c.getLabel()) != null) {
            return false;
        }
        String string = this.miningCommissionSuffix(c.getTarget());
        return Intrinsics.areEqual((Object)string, (Object)"Titanium") || Intrinsics.areEqual((Object)string, (Object)"Mithril");
    }

    /*
     * WARNING - void declaration
     */
    private final String selectRouteByCommissionName(List<RoutesModule.SavedRouteInfo> routes, Commission commission) {
        Object v0;
        void $this$maxByOrNull$iv;
        void $this$filterTo$iv$iv;
        Iterable $this$filter$iv;
        Pair route;
        void $this$mapTo$iv$iv;
        Iterable $this$map$iv;
        Iterable iterable = routes;
        boolean $i$f$map = false;
        void var6_5 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            RoutesModule.SavedRouteInfo savedRouteInfo = (RoutesModule.SavedRouteInfo)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(TuplesKt.to((Object)route, (Object)INSTANCE.scoreRouteName(route.getName(), commission)));
        }
        $this$map$iv = (List)destination$iv$iv;
        boolean $i$f$filter = false;
        $this$mapTo$iv$iv = $this$filter$iv;
        destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo2 = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            route = (Pair)element$iv$iv;
            boolean bl = false;
            int score = ((Number)route.component2()).intValue();
            if (!(score > 0)) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        $this$filter$iv = (List)destination$iv$iv;
        boolean $i$f$maxByOrNull = false;
        Iterator iterator$iv = $this$maxByOrNull$iv.iterator();
        if (!iterator$iv.hasNext()) {
            v0 = null;
        } else {
            Object maxElem$iv = iterator$iv.next();
            if (!iterator$iv.hasNext()) {
                v0 = maxElem$iv;
            } else {
                int score2;
                Pair $i$f$filterTo2 = (Pair)maxElem$iv;
                boolean bl = false;
                int maxValue$iv = score2 = ((Number)$i$f$filterTo2.component2()).intValue();
                do {
                    Object e$iv = iterator$iv.next();
                    Pair score2 = (Pair)e$iv;
                    $i$a$-maxByOrNull-CommissionMacroModule$selectRouteByCommissionName$best$3 = false;
                    int score3 = ((Number)score2.component2()).intValue();
                    int v$iv = score3;
                    if (maxValue$iv >= v$iv) continue;
                    maxElem$iv = e$iv;
                    maxValue$iv = v$iv;
                } while (iterator$iv.hasNext());
                v0 = maxElem$iv;
            }
        }
        Pair pair = v0;
        if (pair == null) {
            return null;
        }
        Pair best = pair;
        return ((RoutesModule.SavedRouteInfo)best.getFirst()).getName();
    }

    private final int scoreRouteName(String routeName, Commission commission) {
        String normalizedRouteName = this.normalizeComparisonText(routeName);
        String normalizedLabel = this.normalizeComparisonText(commission.getLabel());
        int score = 0;
        if (StringsKt.contains$default((CharSequence)normalizedRouteName, (CharSequence)normalizedLabel, (boolean)false, (int)2, null)) {
            score += 100;
        }
        String string = this.extractCommissionAreaKey(commission.getLabel());
        if (string != null) {
            String areaKey = string;
            boolean bl = false;
            if (StringsKt.contains$default((CharSequence)normalizedRouteName, (CharSequence)areaKey, (boolean)false, (int)2, null)) {
                score += 40;
            }
        }
        String string2 = this.miningCommissionSuffix(commission.getTarget());
        if (Intrinsics.areEqual((Object)string2, (Object)"Titanium")) {
            if (StringsKt.contains$default((CharSequence)normalizedRouteName, (CharSequence)"titanium", (boolean)false, (int)2, null)) {
                score += 20;
            }
        } else if (Intrinsics.areEqual((Object)string2, (Object)"Mithril")) {
            if (StringsKt.contains$default((CharSequence)normalizedRouteName, (CharSequence)"mithril", (boolean)false, (int)2, null)) {
                score += 20;
            }
        } else {
            String targetKey = this.normalizeComparisonText(commission.getTarget());
            if (!StringsKt.isBlank((CharSequence)targetKey) && StringsKt.contains$default((CharSequence)normalizedRouteName, (CharSequence)targetKey, (boolean)false, (int)2, null)) {
                score += 20;
            }
        }
        return score;
    }

    private final String selectRouteByType(List<RoutesModule.SavedRouteInfo> routes, Set<String> preferredTypes) {
        Object v1;
        block5: {
            Iterable $this$firstOrNull$iv = routes;
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                boolean bl;
                block4: {
                    RoutesModule.SavedRouteInfo route = (RoutesModule.SavedRouteInfo)element$iv;
                    boolean bl2 = false;
                    Iterable $this$any$iv = route.getMineTypes();
                    boolean $i$f$any = false;
                    if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                        bl = false;
                    } else {
                        for (Object element$iv2 : $this$any$iv) {
                            String it = (String)element$iv2;
                            boolean bl3 = false;
                            if (!preferredTypes.contains(it)) continue;
                            bl = true;
                            break block4;
                        }
                        bl = false;
                    }
                }
                if (!bl) continue;
                v1 = element$iv;
                break block5;
            }
            v1 = null;
        }
        RoutesModule.SavedRouteInfo savedRouteInfo = v1;
        return savedRouteInfo != null ? savedRouteInfo.getName() : null;
    }

    private final Set<String> preferredRouteMineTypes(Commission c, Set<String> requiredTypes) {
        Object object = new String[]{"Mithril (Gray)", "Mithril (Dark)", "Mithril (Hot)"};
        Set dwarvenMithrilTypes = SetsKt.setOf((Object[])object);
        return switch (c.getTarget()) {
            case "Titanium" -> {
                LinkedHashSet<String> var5_5;
                LinkedHashSet<String> $this$preferredRouteMineTypes_u24lambda_u240 = var5_5 = new LinkedHashSet<String>();
                boolean $i$a$-apply-CommissionMacroModule$preferredRouteMineTypes$1 = false;
                $this$preferredRouteMineTypes_u24lambda_u240.addAll(dwarvenMithrilTypes);
                $this$preferredRouteMineTypes_u24lambda_u240.add("Titanium");
                yield var5_5;
            }
            case "Mithril (Dark)", "Mithril (Hot)", "Mithril (Gray)" -> dwarvenMithrilTypes;
            default -> requiredTypes;
        };
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private final String preferredMiningMacroMineTypes(Commission c) {
        String string = c.getTarget();
        switch (string.hashCode()) {
            case 227869382: {
                if (string.equals("Mithril (Dark)")) return "Mithril (Gray), Mithril (Dark), Mithril (Hot)";
                break;
            }
            case -1815236523: {
                if (string.equals("Titanium")) return "Mithril (Gray), Mithril (Dark), Mithril (Hot)";
                break;
            }
            case -962348087: {
                if (string.equals("Mithril (Hot)")) return "Mithril (Gray), Mithril (Dark), Mithril (Hot)";
                break;
            }
            case 231130489: {
                if (!string.equals("Mithril (Gray)")) break;
                return "Mithril (Gray), Mithril (Dark), Mithril (Hot)";
            }
        }
        String string2 = c.getMineTypes();
        return string2;
    }

    private final String extractCommissionAreaKey(String label) {
        Object v2;
        block1: {
            String normalized = this.normalizeComparisonText(label);
            Set<String> set = MINING_AREA_ALIASES.keySet();
            Intrinsics.checkNotNullExpressionValue(set, (String)"<get-keys>(...)");
            Iterable $this$firstOrNull$iv = set;
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                String it = (String)element$iv;
                boolean bl = false;
                CharSequence charSequence = normalized;
                Intrinsics.checkNotNull((Object)it);
                if (!StringsKt.contains$default((CharSequence)charSequence, (CharSequence)it, (boolean)false, (int)2, null)) continue;
                v2 = element$iv;
                break block1;
            }
            v2 = null;
        }
        return v2;
    }

    private final CommissionRouteZone resolveCommissionRouteZone(String label) {
        Object v3;
        block5: {
            String normalized = this.normalizeComparisonText(label);
            Set<Map.Entry<CommissionRouteZone, List<String>>> set = COMMISSION_ROUTE_ZONE_ALIASES.entrySet();
            Intrinsics.checkNotNullExpressionValue(set, (String)"<get-entries>(...)");
            Iterable $this$firstOrNull$iv = set;
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                boolean bl;
                block4: {
                    Map.Entry entry = (Map.Entry)element$iv;
                    boolean bl2 = false;
                    Intrinsics.checkNotNull((Object)entry);
                    Object v = entry.getValue();
                    Intrinsics.checkNotNullExpressionValue(v, (String)"component2(...)");
                    List aliases = (List)v;
                    Iterable $this$any$iv = aliases;
                    boolean $i$f$any = false;
                    if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                        bl = false;
                    } else {
                        for (Object element$iv2 : $this$any$iv) {
                            String alias = (String)element$iv2;
                            boolean bl3 = false;
                            if (!StringsKt.contains$default((CharSequence)normalized, (CharSequence)alias, (boolean)false, (int)2, null)) continue;
                            bl = true;
                            break block4;
                        }
                        bl = false;
                    }
                }
                if (!bl) continue;
                v3 = element$iv;
                break block5;
            }
            v3 = null;
        }
        Map.Entry entry = v3;
        return entry != null ? (CommissionRouteZone)((Object)entry.getKey()) : null;
    }

    /*
     * WARNING - void declaration
     */
    private final String resolveAssignedRouteName(Commission commission, List<RoutesModule.SavedRouteInfo> availableRoutes) {
        void $this$associateByTo$iv$iv;
        void $this$associateBy$iv;
        if (commission.getType() != CommissionType.MINING) {
            return null;
        }
        CommissionRouteZone commissionRouteZone = this.resolveCommissionRouteZone(commission.getLabel());
        if (commissionRouteZone == null) {
            return null;
        }
        CommissionRouteZone zone = commissionRouteZone;
        String string = this.getAssignedRouteName(zone);
        if (string == null) {
            return null;
        }
        String assignedName = string;
        Iterable iterable = availableRoutes;
        CommissionMacroModule commissionMacroModule = this;
        boolean $i$f$associateBy = false;
        int capacity$iv = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateBy$iv, (int)10)), (int)16);
        void var8_9 = $this$associateBy$iv;
        Map destination$iv$iv = new LinkedHashMap(capacity$iv);
        boolean $i$f$associateByTo = false;
        for (Object element$iv$iv : $this$associateByTo$iv$iv) {
            void it;
            RoutesModule.SavedRouteInfo savedRouteInfo = (RoutesModule.SavedRouteInfo)element$iv$iv;
            Map map = destination$iv$iv;
            boolean bl = false;
            map.put(it.getName(), element$iv$iv);
        }
        RoutesModule.SavedRouteInfo savedRouteInfo = commissionMacroModule.findRouteInfoByName(destination$iv$iv, assignedName);
        return savedRouteInfo != null ? savedRouteInfo.getName() : null;
    }

    /*
     * WARNING - void declaration
     */
    private final boolean routeMatchesZone(String routeName, CommissionRouteZone zone) {
        boolean bl;
        block4: {
            void $this$any$iv;
            String normalized = this.normalizeComparisonText(routeName);
            List list = COMMISSION_ROUTE_ZONE_ALIASES.get((Object)zone);
            if (list == null) {
                list = CollectionsKt.emptyList();
            }
            Iterable iterable = list;
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                bl = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    String alias = (String)element$iv;
                    boolean bl2 = false;
                    if (!StringsKt.contains$default((CharSequence)normalized, (CharSequence)alias, (boolean)false, (int)2, null)) continue;
                    bl = true;
                    break block4;
                }
                bl = false;
            }
        }
        return bl;
    }

    private final RoutesModule.SavedRouteInfo findRouteInfoByName(Map<String, RoutesModule.SavedRouteInfo> routesByName, String name) {
        Object v0;
        Object object;
        block2: {
            object = routesByName.get(name);
            if (object != null) {
                RoutesModule.SavedRouteInfo it = object;
                boolean bl = false;
                return it;
            }
            Iterable $this$firstOrNull$iv = routesByName.entrySet();
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                Map.Entry entry = (Map.Entry)element$iv;
                boolean bl = false;
                String routeName = (String)entry.getKey();
                if (!StringsKt.equals((String)routeName, (String)name, (boolean)true)) continue;
                v0 = element$iv;
                break block2;
            }
            v0 = null;
        }
        object = v0;
        return object != null ? (RoutesModule.SavedRouteInfo)object.getValue() : null;
    }

    private final String slotKeyForZone(CommissionRouteZone zone) {
        return switch (WhenMappings.$EnumSwitchMapping$2[zone.ordinal()]) {
            case 1 -> "commission:royal";
            case 2 -> "commission:cliffside";
            case 3 -> "commission:lava";
            case 4 -> "commission:ramp";
            case 5 -> "commission:upper";
            default -> throw new NoWhenBranchMatchedException();
        };
    }

    /*
     * WARNING - void declaration
     */
    private final String detectAreaFromTabList() {
        Object object;
        class_634 class_6342 = mc.method_1562();
        if (class_6342 == null) {
            return null;
        }
        class_634 connection = class_6342;
        try {
            void $this$filterTo$iv$iv;
            String it;
            Iterable $this$mapTo$iv$iv;
            Iterable $this$mapNotNullTo$iv$iv;
            Iterable $this$mapNotNull$iv = this.resolveTabEntries(connection);
            boolean $i$f$mapNotNull = false;
            Iterable iterable = $this$mapNotNull$iv;
            Collection destination$iv$iv = new ArrayList();
            boolean $i$f$mapNotNullTo = false;
            void $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
            boolean $i$f$forEach = false;
            Object object2 = $this$forEach$iv$iv$iv.iterator();
            while (object2.hasNext()) {
                String it$iv$iv;
                Object element$iv$iv$iv;
                Object element$iv$iv = element$iv$iv$iv = object2.next();
                boolean bl = false;
                Object it2 = element$iv$iv;
                boolean bl2 = false;
                if (INSTANCE.resolveEntryDisplayName(it2) == null) continue;
                boolean bl3 = false;
                destination$iv$iv.add(it$iv$iv);
            }
            Iterable $this$map$iv = (List)destination$iv$iv;
            boolean $i$f$map = false;
            $this$mapNotNullTo$iv$iv = $this$map$iv;
            destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
            boolean $i$f$mapTo = false;
            for (Object item$iv$iv : $this$mapTo$iv$iv) {
                String string;
                Collection collection;
                block12: {
                    block11: {
                        object2 = (String)item$iv$iv;
                        collection = destination$iv$iv;
                        boolean bl = false;
                        string = class_124.method_539((String)it);
                        if (string == null) break block11;
                        String string2 = string.toLowerCase(Locale.ROOT);
                        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
                        string = string2;
                        if (string2 != null && (string = ((Object)StringsKt.trim((CharSequence)string)).toString()) != null) break block12;
                    }
                    string = "";
                }
                collection.add(string);
            }
            Iterable $this$filter$iv = (List)destination$iv$iv;
            boolean $i$f$filter = false;
            $this$mapTo$iv$iv = $this$filter$iv;
            destination$iv$iv = new ArrayList();
            boolean $i$f$filterTo = false;
            for (Object element$iv$iv : $this$filterTo$iv$iv) {
                it = (String)element$iv$iv;
                boolean bl = false;
                boolean bl4 = !StringsKt.isBlank((CharSequence)it);
                if (!bl4) continue;
                destination$iv$iv.add(element$iv$iv);
            }
            object = (List)destination$iv$iv;
        }
        catch (Exception exception) {
            object = CollectionsKt.emptyList();
        }
        List lines = object;
        for (String line : lines) {
            for (String area : AREA_NAMES) {
                if (!StringsKt.contains((CharSequence)line, (CharSequence)area, (boolean)true)) continue;
                return area;
            }
        }
        return null;
    }

    private final List<Object> resolveTabEntries(Object connection) {
        Object[] objectArray = new String[]{"listPlayerEntries", "getListedOnlinePlayers", "getOnlinePlayers"};
        for (String name : CollectionsKt.listOf((Object[])objectArray)) {
            Object object;
            Object object2;
            block5: {
                Method[] methodArray = connection.getClass().getMethods();
                Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"getMethods(...)");
                Object[] $this$firstOrNull$iv = methodArray;
                boolean $i$f$firstOrNull = false;
                for (Object element$iv : $this$firstOrNull$iv) {
                    Method it = (Method)element$iv;
                    boolean bl = false;
                    if (!(Intrinsics.areEqual((Object)it.getName(), (Object)name) && it.getParameterCount() == 0)) continue;
                    object2 = element$iv;
                    break block5;
                }
                object2 = null;
            }
            if ((Method)object2 == null) continue;
            Object object3 = this;
            try {
                Method method;
                CommissionMacroModule $this$resolveTabEntries_u24lambda_u241 = object3;
                boolean bl = false;
                object = Result.constructor-impl((Object)method.invoke(connection, new Object[0]));
            }
            catch (Throwable throwable) {
                object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
            }
            object3 = object;
            Object object4 = Result.isFailure-impl((Object)object3) ? null : object3;
            if (object4 == null) continue;
            Object result = object4;
            Object object5 = result;
            if (object5 instanceof Collection) {
                return CollectionsKt.filterNotNull((Iterable)((Iterable)result));
            }
            if (!(object5 instanceof Iterable)) continue;
            return CollectionsKt.filterNotNull((Iterable)((Iterable)result));
        }
        return CollectionsKt.emptyList();
    }

    private final String resolveEntryDisplayName(Object entry) {
        Method method;
        Object[] name22;
        Object[] objectArray = new String[]{"getTabListDisplayName", "tabListDisplayName", "getDisplayName", "displayName"};
        for (Object[] name22 : CollectionsKt.listOf((Object[])objectArray)) {
            Object object;
            String text;
            CharSequence charSequence;
            Object $this$resolveEntryDisplayName_u24lambda_u241;
            Object object2;
            block11: {
                Method[] methodArray = entry.getClass().getMethods();
                Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"getMethods(...)");
                Object[] objectArray2 = methodArray;
                boolean $i$f$firstOrNull = false;
                for (Object element$iv : objectArray2) {
                    Method it = (Method)element$iv;
                    boolean bl = false;
                    if (!(Intrinsics.areEqual((Object)it.getName(), (Object)name22) && it.getParameterCount() == 0)) continue;
                    object2 = element$iv;
                    break block11;
                }
                object2 = null;
            }
            if ((Method)object2 == null) continue;
            CommissionMacroModule commissionMacroModule = this;
            CommissionMacroModule commissionMacroModule2 = this;
            try {
                $this$resolveEntryDisplayName_u24lambda_u241 = commissionMacroModule;
                boolean bl = false;
                $this$resolveEntryDisplayName_u24lambda_u241 = Result.constructor-impl((Object)method.invoke(entry, new Object[0]));
            }
            catch (Throwable bl) {
                $this$resolveEntryDisplayName_u24lambda_u241 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
            }
            if ((charSequence = (CharSequence)(text = commissionMacroModule2.coerceText(Result.isFailure-impl((Object)(object = $this$resolveEntryDisplayName_u24lambda_u241)) ? null : object))) == null || StringsKt.isBlank((CharSequence)charSequence)) continue;
            return text;
        }
        name22 = new String[]{"getProfile", "getGameProfile", "profile"};
        for (String name : CollectionsKt.listOf((Object[])name22)) {
            Object object;
            Object object3;
            Object profile;
            block13: {
                Object $this$resolveEntryDisplayName_u24lambda_u243;
                Object object4;
                block12: {
                    Method[] methodArray = entry.getClass().getMethods();
                    Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"getMethods(...)");
                    Object[] objectArray3 = methodArray;
                    boolean $i$f$firstOrNull = false;
                    for (Object element$iv : objectArray3) {
                        Method it = (Method)element$iv;
                        boolean bl = false;
                        if (!(Intrinsics.areEqual((Object)it.getName(), (Object)name) && it.getParameterCount() == 0)) continue;
                        object4 = element$iv;
                        break block12;
                    }
                    object4 = null;
                }
                if ((Method)object4 == null) continue;
                Object $i$f$firstOrNull = this;
                try {
                    $this$resolveEntryDisplayName_u24lambda_u243 = $i$f$firstOrNull;
                    boolean bl = false;
                    $this$resolveEntryDisplayName_u24lambda_u243 = Result.constructor-impl((Object)method.invoke(entry, new Object[0]));
                }
                catch (Throwable bl) {
                    $this$resolveEntryDisplayName_u24lambda_u243 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
                }
                $i$f$firstOrNull = $this$resolveEntryDisplayName_u24lambda_u243;
                if ((Result.isFailure-impl((Object)$i$f$firstOrNull) ? null : $i$f$firstOrNull) == null) continue;
                profile = profile;
                Method[] methodArray = profile.getClass().getMethods();
                Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"getMethods(...)");
                Object[] $this$firstOrNull$iv2 = methodArray;
                boolean $i$f$firstOrNull2 = false;
                for (Object element$iv : $this$firstOrNull$iv2) {
                    Method it = (Method)element$iv;
                    boolean bl = false;
                    if (!(Intrinsics.areEqual((Object)it.getName(), (Object)"getName") && it.getParameterCount() == 0)) continue;
                    object3 = element$iv;
                    break block13;
                }
                object3 = null;
            }
            if ((Method)object3 == null) continue;
            Object object5 = this;
            try {
                Method method2;
                CommissionMacroModule $this$resolveEntryDisplayName_u24lambda_u245 = object5;
                boolean bl = false;
                Object object6 = method2.invoke(profile, new Object[0]);
                object = Result.constructor-impl((Object)(object6 instanceof String ? (String)object6 : null));
            }
            catch (Throwable throwable) {
                object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
            }
            object5 = object;
            String n = (String)(Result.isFailure-impl((Object)object5) ? null : object5);
            if ((object5 = (CharSequence)n) == null || StringsKt.isBlank((CharSequence)object5)) continue;
            return n;
        }
        return null;
    }

    private final String coerceText(Object value) {
        Object object;
        Method m;
        Object object2;
        block7: {
            if (value == null) {
                return null;
            }
            if (value instanceof String) {
                return (String)value;
            }
            Method[] methodArray = value.getClass().getMethods();
            Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"getMethods(...)");
            Object[] $this$firstOrNull$iv = methodArray;
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                Method it = (Method)element$iv;
                boolean bl = false;
                if (!(Intrinsics.areEqual((Object)it.getName(), (Object)"getString") && it.getParameterCount() == 0)) continue;
                object2 = element$iv;
                break block7;
            }
            object2 = null;
        }
        Method method = m = (Method)object2;
        if (method != null) {
            Object object3;
            Method it = method;
            boolean bl = false;
            Object object4 = INSTANCE;
            try {
                CommissionMacroModule $this$coerceText_u24lambda_u241_u240 = object4;
                boolean bl2 = false;
                object3 = Result.constructor-impl((Object)it.invoke(value, new Object[0]));
            }
            catch (Throwable throwable) {
                object3 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
            }
            object4 = object3;
            object = Result.isFailure-impl((Object)object4) ? null : object4;
        } else {
            object = null;
        }
        Object raw = object;
        return raw instanceof String ? (String)raw : value.toString();
    }

    private final void transition(State newState) {
        state = newState;
        stateTick = 0L;
    }

    private final void setStatus(String msg) {
        statusText.setValue(msg);
    }

    private final void resetCommissionState() {
        commission = null;
        activeCommissions = CollectionsKt.emptyList();
        activeMiningRouteName = null;
        commText.setValue("None");
        openAttempts = 0;
        claimAttempts = 0;
        readAttempts = 0;
    }

    private final void resetMacro() {
        this.stopWorkModule();
        this.stopEmissaryNavigation();
        NativePathfinder.INSTANCE.stop();
        MovementManager.setMovementLock(false);
        if (pendingUseRelease) {
            class_304 class_3042 = CommissionMacroModule.mc.field_1690.field_1904;
            if (class_3042 != null) {
                class_3042.method_23481(false);
            }
            pendingUseRelease = false;
        }
        pigeonCooldownTicks = 0;
        pendingForgeWarpPigeonLook = false;
        forgeWarpPigeonTargetYaw = null;
        forgeWarpPigeonLookStableTicks = 0;
        state = State.IDLE;
        stateTick = 0L;
        this.resetCommissionState();
        this.setStatus("Idle");
        areaText.setValue("Unknown");
    }

    private static final boolean hasNearbyGlaciteWalker$lambda$0(class_1657 $player, class_1309 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return !Intrinsics.areEqual((Object)it, (Object)$player) && !(it instanceof class_1531) && it.method_5805();
    }

    private static final boolean hasNearbyGoblin$lambda$0(class_1657 $player, class_1309 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return !Intrinsics.areEqual((Object)it, (Object)$player) && !(it instanceof class_1531) && it.method_5805();
    }

    private static final boolean findEmissaryInteractionEntity$lambda$0(class_746 $player, class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return !Intrinsics.areEqual((Object)it, (Object)$player);
    }

    private static final boolean findEmissaryInteractionEntity$lambda$1(class_2338 $targetPos, class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.method_5649((double)$targetPos.method_10263() + 0.5, (double)$targetPos.method_10264(), (double)$targetPos.method_10260() + 0.5) <= 64.0;
    }

    private static final boolean findEmissaryInteractionEntity$lambda$2(class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return INSTANCE.entityNameMatchesEliza(it);
    }

    private static final Comparable findEmissaryInteractionEntity$lambda$3(class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it instanceof class_1531 ? (Comparable)Integer.valueOf(1) : (Comparable)Integer.valueOf(0);
    }

    private static final Comparable findEmissaryInteractionEntity$lambda$4(class_746 $player, class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return Double.valueOf($player.method_5858(it));
    }

    private static final boolean findEmissaryInteractionEntity$lambda$5(class_746 $player, class_1297 $anchor, class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return !Intrinsics.areEqual((Object)it, (Object)$player) && !(it instanceof class_1531) && it.method_5858($anchor) <= 16.0;
    }

    private static final Comparable findEmissaryInteractionEntity$lambda$6(class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it instanceof class_1657 ? (Comparable)Integer.valueOf(0) : (Comparable)Integer.valueOf(1);
    }

    private static final Comparable findEmissaryInteractionEntity$lambda$7(class_1297 $anchor, class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return Double.valueOf($anchor.method_5858(it));
    }

    private static final boolean findNearestLoadedEmissaryInteractionEntity$lambda$0(class_1657 $player, class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return !Intrinsics.areEqual((Object)it, (Object)$player);
    }

    private static final boolean findNearestLoadedEmissaryInteractionEntity$lambda$1(class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return INSTANCE.entityNameMatchesAnyEmissary(it);
    }

    private static final boolean findNearestLoadedEmissaryInteractionEntity$lambda$3(class_1657 $player, class_1297 $anchor, class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return !Intrinsics.areEqual((Object)it, (Object)$player) && !(it instanceof class_1531) && it.method_5858($anchor) <= 16.0;
    }

    private static final Comparable findNearestLoadedEmissaryInteractionEntity$lambda$4(class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it instanceof class_1657 ? (Comparable)Integer.valueOf(0) : (Comparable)Integer.valueOf(1);
    }

    private static final Comparable findNearestLoadedEmissaryInteractionEntity$lambda$5(class_1297 $anchor, class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return Double.valueOf($anchor.method_5858(it));
    }

    private static final boolean findNearestLoadedFredInteractionEntity$lambda$0(class_1657 $player, class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return !Intrinsics.areEqual((Object)it, (Object)$player);
    }

    private static final boolean findNearestLoadedFredInteractionEntity$lambda$1(class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return INSTANCE.entityNameMatchesFred(it);
    }

    private static final boolean findNearestLoadedFredInteractionEntity$lambda$3(class_1657 $player, class_1297 $anchor, class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return !Intrinsics.areEqual((Object)it, (Object)$player) && !(it instanceof class_1531) && it.method_5858($anchor) <= 16.0;
    }

    private static final Comparable findNearestLoadedFredInteractionEntity$lambda$4(class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it instanceof class_1657 ? (Comparable)Integer.valueOf(0) : (Comparable)Integer.valueOf(1);
    }

    private static final Comparable findNearestLoadedFredInteractionEntity$lambda$5(class_1297 $anchor, class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return Double.valueOf($anchor.method_5858(it));
    }

    private static final Comparable parseCommissionSelectionFromGui$lambda$3(Commission it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return Integer.valueOf(INSTANCE.commissionSelectionRank(it));
    }

    private static final Comparable parseCommissionSelectionFromGui$lambda$4(Commission it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getType() == CommissionType.MINING ? (Comparable)Integer.valueOf(0) : (Comparable)Integer.valueOf(1);
    }

    private static final Comparable parseCommissionSelectionFromGui$lambda$5(Commission it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return (Comparable)((Object)INSTANCE.normalizeComparisonText(it.getLabel()));
    }

    /*
     * WARNING - void declaration
     */
    private static final CharSequence titleCase$lambda$1(String word) {
        String string;
        Intrinsics.checkNotNullParameter((Object)word, (String)"word");
        String string2 = word;
        Locale locale = Locale.US;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"US");
        String string3 = string2.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
        string2 = string3;
        if (((CharSequence)string2).length() > 0) {
            String string4;
            void it;
            char c = string2.charAt(0);
            StringBuilder stringBuilder = new StringBuilder();
            boolean bl = false;
            if (Character.isLowerCase((char)it)) {
                Locale locale2 = Locale.US;
                Intrinsics.checkNotNullExpressionValue((Object)locale2, (String)"US");
                string4 = CharsKt.titlecase((char)it, (Locale)locale2);
            } else {
                string4 = String.valueOf((char)it);
            }
            StringBuilder stringBuilder2 = stringBuilder.append((Object)string4);
            String string5 = string2;
            int n = 1;
            String string6 = string5.substring(n);
            Intrinsics.checkNotNullExpressionValue((Object)string6, (String)"substring(...)");
            string = stringBuilder2.append(string6).toString();
        } else {
            string = string2;
        }
        return string;
    }

    /*
     * WARNING - void declaration
     */
    static {
        void $this$associateTo$iv$iv;
        INSTANCE = new CommissionMacroModule();
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        emissaryWalkRotStrategy = new TrackingRotationStrategy(15.0f, 10.0f);
        enabled = new CheckboxSetting("Enabled", "Complete commissions in a loop. Mode is auto-detected from AOTV and Royal Pigeon availability.", false);
        info = new InfoSetting("How It Works", "If Royal Pigeon is available it is used for reading and claiming commissions. Otherwise the dropdown chooses whether emissary access starts from /warp forge or /warp dwarves. Mining commissions always /warp forge before route start, and route warp points are walked if no AOTV/Etherwarp is available.", InfoType.INFO);
        Object[] objectArray = new String[]{"Warp Forge", "Warp Dwarves"};
        routeWarpSetting = new ModeSetting("Emissary Warp", "Warp used before heading to the emissary when Royal Pigeon is not available.", 0, (String[])objectArray);
        statusText = new TextSetting("Status", "Current macro state.", "Idle");
        modeText = new TextSetting("Mode", "Auto-detected commission mode.", "Unknown");
        commText = new TextSetting("Commission", "Detected commission.", "None");
        areaText = new TextSetting("Area", "Detected map area.", "Unknown");
        elizaTargetPos = new class_2338(-37, 201, -130);
        royalRoutePicker = new RoutePickerSetting("Royal Mines Route", "Route used for Royal Mines commissions.", RouteType.COMMISSION, "commission:royal");
        cliffsideRoutePicker = new RoutePickerSetting("Cliffside Veins Route", "Route used for Cliffside Veins commissions.", RouteType.COMMISSION, "commission:cliffside");
        lavaRoutePicker = new RoutePickerSetting("Lava Springs Route", "Route used for Lava Springs commissions.", RouteType.COMMISSION, "commission:lava");
        rampRoutePicker = new RoutePickerSetting("Rampart's Quarry Route", "Route used for Rampart's Quarry commissions.", RouteType.COMMISSION, "commission:ramp");
        upperRoutePicker = new RoutePickerSetting("Upper Mines Route", "Route used for Upper Mines commissions.", RouteType.COMMISSION, "commission:upper");
        state = State.IDLE;
        currentMode = CommissionMode.WALK_ONLY;
        activeCommissions = CollectionsKt.emptyList();
        pendingSlotRestore = -1;
        activeEmissaryWalkNodeIndex = -1;
        objectArray = new class_2338[]{new class_2338(8, 148, -66), new class_2338(11, 148, -61), new class_2338(11, 148, -51), new class_2338(4, 146, -43), new class_2338(10, 144, -15), new class_2338(41, 135, 17), new class_2338(42, 134, 21)};
        forgeEmissaryWalkNodes = CollectionsKt.listOf((Object[])objectArray);
        greatIceWallGatePos = new class_2338(0, 128, 150);
        goblinBurrowsCenterPos = new class_2338(-100, 163, 150);
        objectArray = new String[]{"Dwarven Mines", "Crystal Hollows", "Glacite Tunnels", "Deep Caverns", "Spider's Den", "The End", "Crimson Isle"};
        AREA_NAMES = CollectionsKt.listOf((Object[])objectArray);
        objectArray = new Pair[]{TuplesKt.to((Object)"titanium", (Object)"Titanium"), TuplesKt.to((Object)"mithril", (Object)"Mithril (Gray)"), TuplesKt.to((Object)"ruby", (Object)"Ruby Gemstone"), TuplesKt.to((Object)"amber", (Object)"Amber Gemstone"), TuplesKt.to((Object)"amethyst", (Object)"Amethyst Gemstone"), TuplesKt.to((Object)"jade", (Object)"Jade Gemstone"), TuplesKt.to((Object)"sapphire", (Object)"Sapphire Gemstone"), TuplesKt.to((Object)"opal", (Object)"Opal Gemstone"), TuplesKt.to((Object)"topaz", (Object)"Topaz Gemstone"), TuplesKt.to((Object)"jasper", (Object)"Jasper Gemstone"), TuplesKt.to((Object)"onyx", (Object)"Onyx Gemstone"), TuplesKt.to((Object)"aquamarine", (Object)"Aquamarine Gemstone"), TuplesKt.to((Object)"citrine", (Object)"Citrine Gemstone"), TuplesKt.to((Object)"peridot", (Object)"Peridot Gemstone"), TuplesKt.to((Object)"umber", (Object)"Umber"), TuplesKt.to((Object)"tungsten", (Object)"Tungsten"), TuplesKt.to((Object)"glacite", (Object)"Glacite"), TuplesKt.to((Object)"sulphur", (Object)"Sulphur"), TuplesKt.to((Object)"coal", (Object)"Pure Coal"), TuplesKt.to((Object)"iron", (Object)"Pure Iron"), TuplesKt.to((Object)"gold", (Object)"Pure Gold"), TuplesKt.to((Object)"lapis", (Object)"Pure Lapis"), TuplesKt.to((Object)"redstone", (Object)"Pure Redstone"), TuplesKt.to((Object)"emerald", (Object)"Pure Emerald"), TuplesKt.to((Object)"diamond", (Object)"Pure Diamond"), TuplesKt.to((Object)"quartz", (Object)"Pure Quartz")};
        ORE_TO_TYPE = MapsKt.linkedMapOf((Pair[])objectArray);
        objectArray = new Pair[]{TuplesKt.to((Object)"mithril", (Object)"Mithril (Gray), Mithril (Dark), Mithril (Hot)"), TuplesKt.to((Object)"titanium", (Object)"Titanium"), TuplesKt.to((Object)"ruby", (Object)"Ruby Gemstone"), TuplesKt.to((Object)"amber", (Object)"Amber Gemstone"), TuplesKt.to((Object)"amethyst", (Object)"Amethyst Gemstone"), TuplesKt.to((Object)"jade", (Object)"Jade Gemstone"), TuplesKt.to((Object)"sapphire", (Object)"Sapphire Gemstone"), TuplesKt.to((Object)"opal", (Object)"Opal Gemstone"), TuplesKt.to((Object)"topaz", (Object)"Topaz Gemstone"), TuplesKt.to((Object)"jasper", (Object)"Jasper Gemstone"), TuplesKt.to((Object)"onyx", (Object)"Onyx Gemstone"), TuplesKt.to((Object)"aquamarine", (Object)"Aquamarine Gemstone"), TuplesKt.to((Object)"citrine", (Object)"Citrine Gemstone"), TuplesKt.to((Object)"peridot", (Object)"Peridot Gemstone"), TuplesKt.to((Object)"umber", (Object)"Umber"), TuplesKt.to((Object)"tungsten", (Object)"Tungsten"), TuplesKt.to((Object)"glacite", (Object)"Glacite"), TuplesKt.to((Object)"sulphur", (Object)"Sulphur"), TuplesKt.to((Object)"coal", (Object)"Pure Coal"), TuplesKt.to((Object)"iron", (Object)"Pure Iron"), TuplesKt.to((Object)"gold", (Object)"Pure Gold"), TuplesKt.to((Object)"lapis", (Object)"Pure Lapis"), TuplesKt.to((Object)"redstone", (Object)"Pure Redstone"), TuplesKt.to((Object)"emerald", (Object)"Pure Emerald"), TuplesKt.to((Object)"diamond", (Object)"Pure Diamond"), TuplesKt.to((Object)"quartz", (Object)"Pure Quartz")};
        ORE_TO_MINE_TYPES = MapsKt.linkedMapOf((Pair[])objectArray);
        objectArray = new Pair[]{TuplesKt.to((Object)"Titanium", (Object)"Dwarven Mines"), TuplesKt.to((Object)"Mithril (Gray)", (Object)"Dwarven Mines"), TuplesKt.to((Object)"Pure Coal", (Object)"Dwarven Mines"), TuplesKt.to((Object)"Pure Iron", (Object)"Dwarven Mines"), TuplesKt.to((Object)"Pure Gold", (Object)"Dwarven Mines"), TuplesKt.to((Object)"Pure Lapis", (Object)"Dwarven Mines"), TuplesKt.to((Object)"Pure Redstone", (Object)"Dwarven Mines"), TuplesKt.to((Object)"Pure Emerald", (Object)"Dwarven Mines"), TuplesKt.to((Object)"Pure Diamond", (Object)"Dwarven Mines"), TuplesKt.to((Object)"Pure Quartz", (Object)"Dwarven Mines"), TuplesKt.to((Object)"Ruby Gemstone", (Object)"Crystal Hollows"), TuplesKt.to((Object)"Amber Gemstone", (Object)"Crystal Hollows"), TuplesKt.to((Object)"Amethyst Gemstone", (Object)"Crystal Hollows"), TuplesKt.to((Object)"Jade Gemstone", (Object)"Crystal Hollows"), TuplesKt.to((Object)"Sapphire Gemstone", (Object)"Crystal Hollows"), TuplesKt.to((Object)"Opal Gemstone", (Object)"Crystal Hollows"), TuplesKt.to((Object)"Topaz Gemstone", (Object)"Crystal Hollows"), TuplesKt.to((Object)"Jasper Gemstone", (Object)"Crystal Hollows"), TuplesKt.to((Object)"Umber", (Object)"Glacite Tunnels"), TuplesKt.to((Object)"Tungsten", (Object)"Glacite Tunnels"), TuplesKt.to((Object)"Glacite", (Object)"Glacite Tunnels"), TuplesKt.to((Object)"Sulphur", (Object)"Glacite Tunnels"), TuplesKt.to((Object)"Onyx Gemstone", (Object)"Glacite Tunnels"), TuplesKt.to((Object)"Aquamarine Gemstone", (Object)"Glacite Tunnels"), TuplesKt.to((Object)"Citrine Gemstone", (Object)"Glacite Tunnels"), TuplesKt.to((Object)"Peridot Gemstone", (Object)"Glacite Tunnels")};
        ORE_TO_ZONE = MapsKt.mapOf((Pair[])objectArray);
        objectArray = new String[]{"Royal Mines Titanium", "Cliffside Veins Titanium", "Lava Springs Titanium", "Rampart's Quarry Titanium", "Upper Mines Titanium", "Royal Mines Mithril", "Cliffside Veins Mithril", "Lava Springs Mithril", "Rampart's Quarry Mithril", "Upper Mines Mithril", "Glacite Walker Slayer", "Goblin Slayer", "Goblin Raid", "Star Sentry Puncher", "Titanium Miner", "Mithril Miner"};
        COMMISSION_PRIORITY = CollectionsKt.listOf((Object[])objectArray);
        objectArray = new String[]{"mine ", "collect "};
        MINING_OBJECTIVE_PREFIXES = CollectionsKt.listOf((Object[])objectArray);
        objectArray = new String[]{"kill ", "slay ", "defeat ", "punch ", "damage "};
        COMBAT_OBJECTIVE_PREFIXES = CollectionsKt.listOf((Object[])objectArray);
        objectArray = new SpreadBuilder(9);
        Collection $this$toTypedArray$iv = MINING_OBJECTIVE_PREFIXES;
        boolean $i$f$toTypedArray = false;
        Iterable thisCollection$iv = $this$toTypedArray$iv;
        objectArray.addSpread((Object)thisCollection$iv.toArray(new String[0]));
        $this$toTypedArray$iv = COMBAT_OBJECTIVE_PREFIXES;
        $i$f$toTypedArray = false;
        thisCollection$iv = $this$toTypedArray$iv;
        objectArray.addSpread((Object)thisCollection$iv.toArray(new String[0]));
        objectArray.add((Object)"participate ");
        objectArray.add((Object)"deposit ");
        objectArray.add((Object)"open ");
        objectArray.add((Object)"enter ");
        objectArray.add((Object)"loot ");
        objectArray.add((Object)"obtain ");
        objectArray.add((Object)"find ");
        COMMISSION_OBJECTIVE_PREFIXES = CollectionsKt.listOf((Object[])objectArray.toArray((Object[])new String[objectArray.size()]));
        Iterable $this$associate$iv = CollectionsKt.withIndex((Iterable)COMMISSION_PRIORITY);
        boolean $i$f$associate = false;
        int capacity$iv = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associate$iv, (int)10)), (int)16);
        thisCollection$iv = $this$associate$iv;
        Map destination$iv$iv = new LinkedHashMap(capacity$iv);
        boolean $i$f$associateTo = false;
        for (Object element$iv$iv : $this$associateTo$iv$iv) {
            Map map = destination$iv$iv;
            IndexedValue indexedValue = (IndexedValue)element$iv$iv;
            boolean bl = false;
            int index = indexedValue.component1();
            String label = (String)indexedValue.component2();
            indexedValue = TuplesKt.to((Object)INSTANCE.normalizeComparisonText(label), (Object)index);
            map.put(indexedValue.getFirst(), indexedValue.getSecond());
        }
        COMMISSION_PRIORITY_INDEX = destination$iv$iv;
        objectArray = new Pair[]{TuplesKt.to((Object)"royal mines", (Object)"Royal Mines"), TuplesKt.to((Object)"cliffside veins", (Object)"Cliffside Veins"), TuplesKt.to((Object)"lava springs", (Object)"Lava Springs"), TuplesKt.to((Object)"rampart's quarry", (Object)"Rampart's Quarry"), TuplesKt.to((Object)"ramparts quarry", (Object)"Rampart's Quarry"), TuplesKt.to((Object)"upper mines", (Object)"Upper Mines")};
        MINING_AREA_ALIASES = MapsKt.linkedMapOf((Pair[])objectArray);
        objectArray = new Pair[5];
        Object[] objectArray2 = new String[]{"royal mines", "royal"};
        objectArray[0] = TuplesKt.to((Object)((Object)CommissionRouteZone.ROYAL), (Object)CollectionsKt.listOf((Object[])objectArray2));
        objectArray2 = new String[]{"cliffside veins", "cliffside"};
        objectArray[1] = TuplesKt.to((Object)((Object)CommissionRouteZone.CLIFFSIDE), (Object)CollectionsKt.listOf((Object[])objectArray2));
        objectArray2 = new String[]{"lava springs", "lava"};
        objectArray[2] = TuplesKt.to((Object)((Object)CommissionRouteZone.LAVA), (Object)CollectionsKt.listOf((Object[])objectArray2));
        objectArray2 = new String[]{"rampart's quarry", "ramparts quarry", "ramp"};
        objectArray[3] = TuplesKt.to((Object)((Object)CommissionRouteZone.RAMP), (Object)CollectionsKt.listOf((Object[])objectArray2));
        objectArray2 = new String[]{"upper mines", "upper"};
        objectArray[4] = TuplesKt.to((Object)((Object)CommissionRouteZone.UPPER), (Object)CollectionsKt.listOf((Object[])objectArray2));
        COMMISSION_ROUTE_ZONE_ALIASES = MapsKt.linkedMapOf((Pair[])objectArray);
        objectArray = new String[]{"glacite walker", "ice walker"};
        GLACITE_WALKER_NAME_KEYWORDS = CollectionsKt.listOf((Object[])objectArray);
        objectArray = new Setting[]{enabled, info, routeWarpSetting, statusText, modeText, commText, areaText, royalRoutePicker, cliffsideRoutePicker, lavaRoutePicker, rampRoutePicker, upperRoutePicker};
        INSTANCE.addSetting((Setting<?>[])objectArray);
        EventBus.register(INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0010\b\u0082\b\u0018\u00002\u00020\u0001B7\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u0012\u0006\u0010\u0007\u001a\u00020\u0002\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\n\u001a\u00020\b\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0010\u0010\u0011\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u000eJ\u0010\u0010\u0012\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0012\u0010\u000eJ\u0010\u0010\u0013\u001a\u00020\bH\u00c6\u0003\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0010\u0010\u0015\u001a\u00020\bH\u00c6\u0003\u00a2\u0006\u0004\b\u0015\u0010\u0014JL\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u00022\b\b\u0002\u0010\u0007\u001a\u00020\u00022\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\bH\u00c6\u0001\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u001b\u0010\u001a\u001a\u00020\u00192\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u0011\u0010\u001c\u001a\u00020\bH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001c\u0010\u0014J\u0011\u0010\u001d\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001d\u0010\u000eR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001e\u001a\u0004\b\u001f\u0010\u000eR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010 \u001a\u0004\b!\u0010\u0010R\u0017\u0010\u0006\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001e\u001a\u0004\b\"\u0010\u000eR\u0017\u0010\u0007\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u001e\u001a\u0004\b#\u0010\u000eR\u0017\u0010\t\u001a\u00020\b8\u0006\u00a2\u0006\f\n\u0004\b\t\u0010$\u001a\u0004\b%\u0010\u0014R\u0017\u0010\n\u001a\u00020\b8\u0006\u00a2\u0006\f\n\u0004\b\n\u0010$\u001a\u0004\b&\u0010\u0014R\u0011\u0010'\u001a\u00020\u00198F\u00a2\u0006\u0006\u001a\u0004\b'\u0010(\u00a8\u0006)"}, d2={"Lorg/cobalt/api/hud/modules/CommissionMacroModule$Commission;", "", "", "label", "Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionType;", "type", "target", "mineTypes", "", "current", "max", "<init>", "(Ljava/lang/String;Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionType;Ljava/lang/String;Ljava/lang/String;II)V", "component1", "()Ljava/lang/String;", "component2", "()Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionType;", "component3", "component4", "component5", "()I", "component6", "copy", "(Ljava/lang/String;Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionType;Ljava/lang/String;Ljava/lang/String;II)Lorg/cobalt/api/hud/modules/CommissionMacroModule$Commission;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "toString", "Ljava/lang/String;", "getLabel", "Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionType;", "getType", "getTarget", "getMineTypes", "I", "getCurrent", "getMax", "isComplete", "()Z", "cobalt"})
    private static final class Commission {
        @NotNull
        private final String label;
        @NotNull
        private final CommissionType type;
        @NotNull
        private final String target;
        @NotNull
        private final String mineTypes;
        private final int current;
        private final int max;

        public Commission(@NotNull String label, @NotNull CommissionType type, @NotNull String target, @NotNull String mineTypes, int current, int max) {
            Intrinsics.checkNotNullParameter((Object)label, (String)"label");
            Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
            Intrinsics.checkNotNullParameter((Object)target, (String)"target");
            Intrinsics.checkNotNullParameter((Object)mineTypes, (String)"mineTypes");
            this.label = label;
            this.type = type;
            this.target = target;
            this.mineTypes = mineTypes;
            this.current = current;
            this.max = max;
        }

        @NotNull
        public final String getLabel() {
            return this.label;
        }

        @NotNull
        public final CommissionType getType() {
            return this.type;
        }

        @NotNull
        public final String getTarget() {
            return this.target;
        }

        @NotNull
        public final String getMineTypes() {
            return this.mineTypes;
        }

        public final int getCurrent() {
            return this.current;
        }

        public final int getMax() {
            return this.max;
        }

        public final boolean isComplete() {
            return this.max > 0 && this.current >= this.max;
        }

        @NotNull
        public final String component1() {
            return this.label;
        }

        @NotNull
        public final CommissionType component2() {
            return this.type;
        }

        @NotNull
        public final String component3() {
            return this.target;
        }

        @NotNull
        public final String component4() {
            return this.mineTypes;
        }

        public final int component5() {
            return this.current;
        }

        public final int component6() {
            return this.max;
        }

        @NotNull
        public final Commission copy(@NotNull String label, @NotNull CommissionType type, @NotNull String target, @NotNull String mineTypes, int current, int max) {
            Intrinsics.checkNotNullParameter((Object)label, (String)"label");
            Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
            Intrinsics.checkNotNullParameter((Object)target, (String)"target");
            Intrinsics.checkNotNullParameter((Object)mineTypes, (String)"mineTypes");
            return new Commission(label, type, target, mineTypes, current, max);
        }

        public static /* synthetic */ Commission copy$default(Commission commission, String string, CommissionType commissionType, String string2, String string3, int n, int n2, int n3, Object object) {
            if ((n3 & 1) != 0) {
                string = commission.label;
            }
            if ((n3 & 2) != 0) {
                commissionType = commission.type;
            }
            if ((n3 & 4) != 0) {
                string2 = commission.target;
            }
            if ((n3 & 8) != 0) {
                string3 = commission.mineTypes;
            }
            if ((n3 & 0x10) != 0) {
                n = commission.current;
            }
            if ((n3 & 0x20) != 0) {
                n2 = commission.max;
            }
            return commission.copy(string, commissionType, string2, string3, n, n2);
        }

        @NotNull
        public String toString() {
            return "Commission(label=" + this.label + ", type=" + this.type + ", target=" + this.target + ", mineTypes=" + this.mineTypes + ", current=" + this.current + ", max=" + this.max + ")";
        }

        public int hashCode() {
            int result = this.label.hashCode();
            result = result * 31 + this.type.hashCode();
            result = result * 31 + this.target.hashCode();
            result = result * 31 + this.mineTypes.hashCode();
            result = result * 31 + Integer.hashCode(this.current);
            result = result * 31 + Integer.hashCode(this.max);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Commission)) {
                return false;
            }
            Commission commission = (Commission)other;
            if (!Intrinsics.areEqual((Object)this.label, (Object)commission.label)) {
                return false;
            }
            if (this.type != commission.type) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.target, (Object)commission.target)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.mineTypes, (Object)commission.mineTypes)) {
                return false;
            }
            if (this.current != commission.current) {
                return false;
            }
            return this.max == commission.max;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0018\b\u0086\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\fJ\u0010\u0010\u000e\u001a\u00020\u0005H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0010\u0010\u0010\u001a\u00020\u0007H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u0011J8\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\u0007H\u00c6\u0001\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u001b\u0010\u0015\u001a\u00020\u00052\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u0011\u0010\u0017\u001a\u00020\u0007H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0017\u0010\u0011J\u0011\u0010\u0018\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0018\u0010\fR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0019\u001a\u0004\b\u001a\u0010\fR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0019\u001a\u0004\b\u001b\u0010\fR\u0017\u0010\u0006\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001c\u001a\u0004\b\u0006\u0010\u000fR\u0017\u0010\b\u001a\u00020\u00078\u0006\u00a2\u0006\f\n\u0004\b\b\u0010\u001d\u001a\u0004\b\u001e\u0010\u0011\u00a8\u0006\u001f"}, d2={"Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionHudRow;", "", "", "label", "detail", "", "isTargeted", "", "percent", "<init>", "(Ljava/lang/String;Ljava/lang/String;ZI)V", "component1", "()Ljava/lang/String;", "component2", "component3", "()Z", "component4", "()I", "copy", "(Ljava/lang/String;Ljava/lang/String;ZI)Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionHudRow;", "other", "equals", "(Ljava/lang/Object;)Z", "hashCode", "toString", "Ljava/lang/String;", "getLabel", "getDetail", "Z", "I", "getPercent", "cobalt"})
    public static final class CommissionHudRow {
        @NotNull
        private final String label;
        @NotNull
        private final String detail;
        private final boolean isTargeted;
        private final int percent;

        public CommissionHudRow(@NotNull String label, @NotNull String detail, boolean isTargeted, int percent) {
            Intrinsics.checkNotNullParameter((Object)label, (String)"label");
            Intrinsics.checkNotNullParameter((Object)detail, (String)"detail");
            this.label = label;
            this.detail = detail;
            this.isTargeted = isTargeted;
            this.percent = percent;
        }

        @NotNull
        public final String getLabel() {
            return this.label;
        }

        @NotNull
        public final String getDetail() {
            return this.detail;
        }

        public final boolean isTargeted() {
            return this.isTargeted;
        }

        public final int getPercent() {
            return this.percent;
        }

        @NotNull
        public final String component1() {
            return this.label;
        }

        @NotNull
        public final String component2() {
            return this.detail;
        }

        public final boolean component3() {
            return this.isTargeted;
        }

        public final int component4() {
            return this.percent;
        }

        @NotNull
        public final CommissionHudRow copy(@NotNull String label, @NotNull String detail, boolean isTargeted, int percent) {
            Intrinsics.checkNotNullParameter((Object)label, (String)"label");
            Intrinsics.checkNotNullParameter((Object)detail, (String)"detail");
            return new CommissionHudRow(label, detail, isTargeted, percent);
        }

        public static /* synthetic */ CommissionHudRow copy$default(CommissionHudRow commissionHudRow, String string, String string2, boolean bl, int n, int n2, Object object) {
            if ((n2 & 1) != 0) {
                string = commissionHudRow.label;
            }
            if ((n2 & 2) != 0) {
                string2 = commissionHudRow.detail;
            }
            if ((n2 & 4) != 0) {
                bl = commissionHudRow.isTargeted;
            }
            if ((n2 & 8) != 0) {
                n = commissionHudRow.percent;
            }
            return commissionHudRow.copy(string, string2, bl, n);
        }

        @NotNull
        public String toString() {
            return "CommissionHudRow(label=" + this.label + ", detail=" + this.detail + ", isTargeted=" + this.isTargeted + ", percent=" + this.percent + ")";
        }

        public int hashCode() {
            int result = this.label.hashCode();
            result = result * 31 + this.detail.hashCode();
            result = result * 31 + Boolean.hashCode(this.isTargeted);
            result = result * 31 + Integer.hashCode(this.percent);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CommissionHudRow)) {
                return false;
            }
            CommissionHudRow commissionHudRow = (CommissionHudRow)other;
            if (!Intrinsics.areEqual((Object)this.label, (Object)commissionHudRow.label)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.detail, (Object)commissionHudRow.detail)) {
                return false;
            }
            if (this.isTargeted != commissionHudRow.isTargeted) {
                return false;
            }
            return this.percent == commissionHudRow.percent;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0010\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B!\b\u0002\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\t\u001a\u0004\b\n\u0010\u000bR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\f\u001a\u0004\b\r\u0010\u000eR\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\f\u001a\u0004\b\u000f\u0010\u000ej\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013\u00a8\u0006\u0014"}, d2={"Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionMode;", "", "", "label", "", "usesWarps", "usesPigeon", "<init>", "(Ljava/lang/String;ILjava/lang/String;ZZ)V", "Ljava/lang/String;", "getLabel", "()Ljava/lang/String;", "Z", "getUsesWarps", "()Z", "getUsesPigeon", "WALK_ONLY", "WALK_PIGEON", "AOTV_ELIZA", "AOTV_PIGEON", "cobalt"})
    private static final class CommissionMode
    extends Enum<CommissionMode> {
        @NotNull
        private final String label;
        private final boolean usesWarps;
        private final boolean usesPigeon;
        public static final /* enum */ CommissionMode WALK_ONLY = new CommissionMode("Walk Only", false, false);
        public static final /* enum */ CommissionMode WALK_PIGEON = new CommissionMode("Walk + Pigeon", false, true);
        public static final /* enum */ CommissionMode AOTV_ELIZA = new CommissionMode("AOTV + Emissary", true, false);
        public static final /* enum */ CommissionMode AOTV_PIGEON = new CommissionMode("AOTV + Pigeon", true, true);
        private static final /* synthetic */ CommissionMode[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        private CommissionMode(String label, boolean usesWarps, boolean usesPigeon) {
            this.label = label;
            this.usesWarps = usesWarps;
            this.usesPigeon = usesPigeon;
        }

        @NotNull
        public final String getLabel() {
            return this.label;
        }

        public final boolean getUsesWarps() {
            return this.usesWarps;
        }

        public final boolean getUsesPigeon() {
            return this.usesPigeon;
        }

        public static CommissionMode[] values() {
            return (CommissionMode[])$VALUES.clone();
        }

        public static CommissionMode valueOf(String value) {
            return Enum.valueOf(CommissionMode.class, value);
        }

        @NotNull
        public static EnumEntries<CommissionMode> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = commissionModeArray = new CommissionMode[]{CommissionMode.WALK_ONLY, CommissionMode.WALK_PIGEON, CommissionMode.AOTV_ELIZA, CommissionMode.AOTV_PIGEON};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0010\u000e\n\u0002\b\u000e\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0019\b\u0002\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0007\u001a\u0004\b\b\u0010\tR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0007\u001a\u0004\b\n\u0010\tj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000f\u00a8\u0006\u0010"}, d2={"Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionRouteZone;", "", "", "label", "areaLabel", "<init>", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V", "Ljava/lang/String;", "getLabel", "()Ljava/lang/String;", "getAreaLabel", "ROYAL", "CLIFFSIDE", "LAVA", "RAMP", "UPPER", "cobalt"})
    public static final class CommissionRouteZone
    extends Enum<CommissionRouteZone> {
        @NotNull
        private final String label;
        @NotNull
        private final String areaLabel;
        public static final /* enum */ CommissionRouteZone ROYAL = new CommissionRouteZone("Royal", "Royal Mines");
        public static final /* enum */ CommissionRouteZone CLIFFSIDE = new CommissionRouteZone("Cliffside", "Cliffside Veins");
        public static final /* enum */ CommissionRouteZone LAVA = new CommissionRouteZone("Lava", "Lava Springs");
        public static final /* enum */ CommissionRouteZone RAMP = new CommissionRouteZone("Ramp", "Rampart's Quarry");
        public static final /* enum */ CommissionRouteZone UPPER = new CommissionRouteZone("Upper", "Upper Mines");
        private static final /* synthetic */ CommissionRouteZone[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        private CommissionRouteZone(String label, String areaLabel) {
            this.label = label;
            this.areaLabel = areaLabel;
        }

        @NotNull
        public final String getLabel() {
            return this.label;
        }

        @NotNull
        public final String getAreaLabel() {
            return this.areaLabel;
        }

        public static CommissionRouteZone[] values() {
            return (CommissionRouteZone[])$VALUES.clone();
        }

        public static CommissionRouteZone valueOf(String value) {
            return Enum.valueOf(CommissionRouteZone.class, value);
        }

        @NotNull
        public static EnumEntries<CommissionRouteZone> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = commissionRouteZoneArray = new CommissionRouteZone[]{CommissionRouteZone.ROYAL, CommissionRouteZone.CLIFFSIDE, CommissionRouteZone.LAVA, CommissionRouteZone.RAMP, CommissionRouteZone.UPPER};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005\u00a8\u0006\u0006"}, d2={"Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionType;", "", "<init>", "(Ljava/lang/String;I)V", "MINING", "COMBAT", "cobalt"})
    private static final class CommissionType
    extends Enum<CommissionType> {
        public static final /* enum */ CommissionType MINING = new CommissionType();
        public static final /* enum */ CommissionType COMBAT = new CommissionType();
        private static final /* synthetic */ CommissionType[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        public static CommissionType[] values() {
            return (CommissionType[])$VALUES.clone();
        }

        public static CommissionType valueOf(String value) {
            return Enum.valueOf(CommissionType.class, value);
        }

        @NotNull
        public static EnumEntries<CommissionType> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = commissionTypeArray = new CommissionType[]{CommissionType.MINING, CommissionType.COMBAT};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\f\b\u0082\b\u0018\u00002\u00020\u0001B1\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00020\b\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\rJ\u0012\u0010\u000e\u001a\u0004\u0018\u00010\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0010\u0010\u0010\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0016\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00020\bH\u00c6\u0003\u00a2\u0006\u0004\b\u0012\u0010\u0013J@\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u00062\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00020\bH\u00c6\u0001\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u001b\u0010\u0018\u001a\u00020\u00172\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0011\u0010\u001b\u001a\u00020\u001aH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001b\u0010\u001cJ\u0011\u0010\u001d\u001a\u00020\u0006H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001d\u0010\u0011R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001e\u001a\u0004\b\u001f\u0010\rR\u0019\u0010\u0005\u001a\u0004\u0018\u00010\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010 \u001a\u0004\b!\u0010\u000fR\u0017\u0010\u0007\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\"\u001a\u0004\b#\u0010\u0011R\u001d\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00020\b8\u0006\u00a2\u0006\f\n\u0004\b\t\u0010$\u001a\u0004\b%\u0010\u0013\u00a8\u0006&"}, d2={"Lorg/cobalt/api/hud/modules/CommissionMacroModule$EmissaryTarget;", "", "Lnet/minecraft/class_2338;", "walkPos", "Lnet/minecraft/class_1297;", "interactionEntity", "", "label", "", "walkNodes", "<init>", "(Lnet/minecraft/class_2338;Lnet/minecraft/class_1297;Ljava/lang/String;Ljava/util/List;)V", "component1", "()Lnet/minecraft/class_2338;", "component2", "()Lnet/minecraft/class_1297;", "component3", "()Ljava/lang/String;", "component4", "()Ljava/util/List;", "copy", "(Lnet/minecraft/class_2338;Lnet/minecraft/class_1297;Ljava/lang/String;Ljava/util/List;)Lorg/cobalt/api/hud/modules/CommissionMacroModule$EmissaryTarget;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Lnet/minecraft/class_2338;", "getWalkPos", "Lnet/minecraft/class_1297;", "getInteractionEntity", "Ljava/lang/String;", "getLabel", "Ljava/util/List;", "getWalkNodes", "cobalt"})
    private static final class EmissaryTarget {
        @NotNull
        private final class_2338 walkPos;
        @Nullable
        private final class_1297 interactionEntity;
        @NotNull
        private final String label;
        @NotNull
        private final List<class_2338> walkNodes;

        public EmissaryTarget(@NotNull class_2338 walkPos, @Nullable class_1297 interactionEntity, @NotNull String label, @NotNull List<? extends class_2338> walkNodes) {
            Intrinsics.checkNotNullParameter((Object)walkPos, (String)"walkPos");
            Intrinsics.checkNotNullParameter((Object)label, (String)"label");
            Intrinsics.checkNotNullParameter(walkNodes, (String)"walkNodes");
            this.walkPos = walkPos;
            this.interactionEntity = interactionEntity;
            this.label = label;
            this.walkNodes = walkNodes;
        }

        public /* synthetic */ EmissaryTarget(class_2338 class_23382, class_1297 class_12972, String string, List list, int n, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n & 8) != 0) {
                list = CollectionsKt.emptyList();
            }
            this(class_23382, class_12972, string, list);
        }

        @NotNull
        public final class_2338 getWalkPos() {
            return this.walkPos;
        }

        @Nullable
        public final class_1297 getInteractionEntity() {
            return this.interactionEntity;
        }

        @NotNull
        public final String getLabel() {
            return this.label;
        }

        @NotNull
        public final List<class_2338> getWalkNodes() {
            return this.walkNodes;
        }

        @NotNull
        public final class_2338 component1() {
            return this.walkPos;
        }

        @Nullable
        public final class_1297 component2() {
            return this.interactionEntity;
        }

        @NotNull
        public final String component3() {
            return this.label;
        }

        @NotNull
        public final List<class_2338> component4() {
            return this.walkNodes;
        }

        @NotNull
        public final EmissaryTarget copy(@NotNull class_2338 walkPos, @Nullable class_1297 interactionEntity, @NotNull String label, @NotNull List<? extends class_2338> walkNodes) {
            Intrinsics.checkNotNullParameter((Object)walkPos, (String)"walkPos");
            Intrinsics.checkNotNullParameter((Object)label, (String)"label");
            Intrinsics.checkNotNullParameter(walkNodes, (String)"walkNodes");
            return new EmissaryTarget(walkPos, interactionEntity, label, walkNodes);
        }

        public static /* synthetic */ EmissaryTarget copy$default(EmissaryTarget emissaryTarget, class_2338 class_23382, class_1297 class_12972, String string, List list, int n, Object object) {
            if ((n & 1) != 0) {
                class_23382 = emissaryTarget.walkPos;
            }
            if ((n & 2) != 0) {
                class_12972 = emissaryTarget.interactionEntity;
            }
            if ((n & 4) != 0) {
                string = emissaryTarget.label;
            }
            if ((n & 8) != 0) {
                list = emissaryTarget.walkNodes;
            }
            return emissaryTarget.copy(class_23382, class_12972, string, list);
        }

        @NotNull
        public String toString() {
            return "EmissaryTarget(walkPos=" + this.walkPos + ", interactionEntity=" + this.interactionEntity + ", label=" + this.label + ", walkNodes=" + this.walkNodes + ")";
        }

        public int hashCode() {
            int result = this.walkPos.hashCode();
            result = result * 31 + (this.interactionEntity == null ? 0 : this.interactionEntity.hashCode());
            result = result * 31 + this.label.hashCode();
            result = result * 31 + ((Object)this.walkNodes).hashCode();
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof EmissaryTarget)) {
                return false;
            }
            EmissaryTarget emissaryTarget = (EmissaryTarget)other;
            if (!Intrinsics.areEqual((Object)this.walkPos, (Object)emissaryTarget.walkPos)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.interactionEntity, (Object)emissaryTarget.interactionEntity)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.label, (Object)emissaryTarget.label)) {
                return false;
            }
            return Intrinsics.areEqual(this.walkNodes, emissaryTarget.walkNodes);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0082\b\u0018\u00002\u00020\u0001B\u001f\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0016\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\u0012\u0010\n\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ,\u0010\f\u001a\u00020\u00002\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00022\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u001b\u0010\u0010\u001a\u00020\u000f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0011\u0010\u0013\u001a\u00020\u0012H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0011\u0010\u0016\u001a\u00020\u0015H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017R\u001d\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0018\u001a\u0004\b\u0019\u0010\tR\u0019\u0010\u0005\u001a\u0004\u0018\u00010\u00038\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001a\u001a\u0004\b\u001b\u0010\u000b\u00a8\u0006\u001c"}, d2={"Lorg/cobalt/api/hud/modules/CommissionMacroModule$ParsedCommissionSelection;", "", "", "Lorg/cobalt/api/hud/modules/CommissionMacroModule$Commission;", "commissions", "selected", "<init>", "(Ljava/util/List;Lorg/cobalt/api/hud/modules/CommissionMacroModule$Commission;)V", "component1", "()Ljava/util/List;", "component2", "()Lorg/cobalt/api/hud/modules/CommissionMacroModule$Commission;", "copy", "(Ljava/util/List;Lorg/cobalt/api/hud/modules/CommissionMacroModule$Commission;)Lorg/cobalt/api/hud/modules/CommissionMacroModule$ParsedCommissionSelection;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "Ljava/util/List;", "getCommissions", "Lorg/cobalt/api/hud/modules/CommissionMacroModule$Commission;", "getSelected", "cobalt"})
    private static final class ParsedCommissionSelection {
        @NotNull
        private final List<Commission> commissions;
        @Nullable
        private final Commission selected;

        public ParsedCommissionSelection(@NotNull List<Commission> commissions, @Nullable Commission selected) {
            Intrinsics.checkNotNullParameter(commissions, (String)"commissions");
            this.commissions = commissions;
            this.selected = selected;
        }

        @NotNull
        public final List<Commission> getCommissions() {
            return this.commissions;
        }

        @Nullable
        public final Commission getSelected() {
            return this.selected;
        }

        @NotNull
        public final List<Commission> component1() {
            return this.commissions;
        }

        @Nullable
        public final Commission component2() {
            return this.selected;
        }

        @NotNull
        public final ParsedCommissionSelection copy(@NotNull List<Commission> commissions, @Nullable Commission selected) {
            Intrinsics.checkNotNullParameter(commissions, (String)"commissions");
            return new ParsedCommissionSelection(commissions, selected);
        }

        public static /* synthetic */ ParsedCommissionSelection copy$default(ParsedCommissionSelection parsedCommissionSelection, List list, Commission commission, int n, Object object) {
            if ((n & 1) != 0) {
                list = parsedCommissionSelection.commissions;
            }
            if ((n & 2) != 0) {
                commission = parsedCommissionSelection.selected;
            }
            return parsedCommissionSelection.copy(list, commission);
        }

        @NotNull
        public String toString() {
            return "ParsedCommissionSelection(commissions=" + this.commissions + ", selected=" + this.selected + ")";
        }

        public int hashCode() {
            int result = ((Object)this.commissions).hashCode();
            result = result * 31 + (this.selected == null ? 0 : this.selected.hashCode());
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ParsedCommissionSelection)) {
                return false;
            }
            ParsedCommissionSelection parsedCommissionSelection = (ParsedCommissionSelection)other;
            if (!Intrinsics.areEqual(this.commissions, parsedCommissionSelection.commissions)) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.selected, (Object)parsedCommissionSelection.selected);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0010\u000e\n\u0002\b\r\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B!\b\u0002\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0006\u0010\u0007R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\b\u001a\u0004\b\t\u0010\nR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\b\u001a\u0004\b\u000b\u0010\nR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\b\u001a\u0004\b\f\u0010\nj\u0002\b\rj\u0002\b\u000e\u00a8\u0006\u000f"}, d2={"Lorg/cobalt/api/hud/modules/CommissionMacroModule$RouteWarpDestination;", "", "", "command", "statusLabel", "areaLabel", "<init>", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "Ljava/lang/String;", "getCommand", "()Ljava/lang/String;", "getStatusLabel", "getAreaLabel", "FORGE", "DWARVES", "cobalt"})
    private static final class RouteWarpDestination
    extends Enum<RouteWarpDestination> {
        @NotNull
        private final String command;
        @NotNull
        private final String statusLabel;
        @NotNull
        private final String areaLabel;
        public static final /* enum */ RouteWarpDestination FORGE = new RouteWarpDestination("warp forge", "forge", "Forge");
        public static final /* enum */ RouteWarpDestination DWARVES = new RouteWarpDestination("warp dwarves", "dwarves", "Dwarven Mines");
        private static final /* synthetic */ RouteWarpDestination[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        private RouteWarpDestination(String command, String statusLabel, String areaLabel) {
            this.command = command;
            this.statusLabel = statusLabel;
            this.areaLabel = areaLabel;
        }

        @NotNull
        public final String getCommand() {
            return this.command;
        }

        @NotNull
        public final String getStatusLabel() {
            return this.statusLabel;
        }

        @NotNull
        public final String getAreaLabel() {
            return this.areaLabel;
        }

        public static RouteWarpDestination[] values() {
            return (RouteWarpDestination[])$VALUES.clone();
        }

        public static RouteWarpDestination valueOf(String value) {
            return Enum.valueOf(RouteWarpDestination.class, value);
        }

        @NotNull
        public static EnumEntries<RouteWarpDestination> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = routeWarpDestinationArray = new RouteWarpDestination[]{RouteWarpDestination.FORGE, RouteWarpDestination.DWARVES};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u000e\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000e\u00a8\u0006\u000f"}, d2={"Lorg/cobalt/api/hud/modules/CommissionMacroModule$State;", "", "<init>", "(Ljava/lang/String;I)V", "IDLE", "OPEN_PIGEON", "WARP_TO_DWARVES", "WARP_TO_FORGE_EMISSARY", "WALK_TO_EMISSARY", "OPEN_EMISSARY", "READ_GUI", "CLAIM_GUI", "WARP_TO_ROUTE_START", "MINING", "RETURN_TO_DWARVES", "cobalt"})
    private static final class State
    extends Enum<State> {
        public static final /* enum */ State IDLE = new State();
        public static final /* enum */ State OPEN_PIGEON = new State();
        public static final /* enum */ State WARP_TO_DWARVES = new State();
        public static final /* enum */ State WARP_TO_FORGE_EMISSARY = new State();
        public static final /* enum */ State WALK_TO_EMISSARY = new State();
        public static final /* enum */ State OPEN_EMISSARY = new State();
        public static final /* enum */ State READ_GUI = new State();
        public static final /* enum */ State CLAIM_GUI = new State();
        public static final /* enum */ State WARP_TO_ROUTE_START = new State();
        public static final /* enum */ State MINING = new State();
        public static final /* enum */ State RETURN_TO_DWARVES = new State();
        private static final /* synthetic */ State[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        public static State[] values() {
            return (State[])$VALUES.clone();
        }

        public static State valueOf(String value) {
            return Enum.valueOf(State.class, value);
        }

        @NotNull
        public static EnumEntries<State> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = stateArray = new State[]{State.IDLE, State.OPEN_PIGEON, State.WARP_TO_DWARVES, State.WARP_TO_FORGE_EMISSARY, State.WALK_TO_EMISSARY, State.OPEN_EMISSARY, State.READ_GUI, State.CLAIM_GUI, State.WARP_TO_ROUTE_START, State.MINING, State.RETURN_TO_DWARVES};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;
        public static final /* synthetic */ int[] $EnumSwitchMapping$1;
        public static final /* synthetic */ int[] $EnumSwitchMapping$2;

        static {
            int[] nArray = new int[State.values().length];
            try {
                nArray[State.IDLE.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.OPEN_PIGEON.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.WARP_TO_DWARVES.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.WARP_TO_FORGE_EMISSARY.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.WALK_TO_EMISSARY.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.OPEN_EMISSARY.ordinal()] = 6;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.READ_GUI.ordinal()] = 7;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.CLAIM_GUI.ordinal()] = 8;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.WARP_TO_ROUTE_START.ordinal()] = 9;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.MINING.ordinal()] = 10;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.RETURN_TO_DWARVES.ordinal()] = 11;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
            nArray = new int[CommissionType.values().length];
            try {
                nArray[CommissionType.MINING.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[CommissionType.COMBAT.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$1 = nArray;
            nArray = new int[CommissionRouteZone.values().length];
            try {
                nArray[CommissionRouteZone.ROYAL.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[CommissionRouteZone.CLIFFSIDE.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[CommissionRouteZone.LAVA.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[CommissionRouteZone.RAMP.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[CommissionRouteZone.UPPER.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$2 = nArray;
        }
    }
}

