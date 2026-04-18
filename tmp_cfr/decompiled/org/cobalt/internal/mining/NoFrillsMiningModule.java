/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.Grouping
 *  kotlin.collections.GroupingKt
 *  kotlin.collections.SetsKt
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.sequences.Sequence
 *  kotlin.sequences.SequencesKt
 *  kotlin.text.MatchResult
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  net.minecraft.class_124
 *  net.minecraft.class_1297
 *  net.minecraft.class_1304
 *  net.minecraft.class_1531
 *  net.minecraft.class_1548
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_1936
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_238
 *  net.minecraft.class_239
 *  net.minecraft.class_2398
 *  net.minecraft.class_243
 *  net.minecraft.class_2504
 *  net.minecraft.class_2506
 *  net.minecraft.class_2561
 *  net.minecraft.class_2596
 *  net.minecraft.class_2649
 *  net.minecraft.class_2653
 *  net.minecraft.class_266
 *  net.minecraft.class_2675
 *  net.minecraft.class_268
 *  net.minecraft.class_2680
 *  net.minecraft.class_269
 *  net.minecraft.class_2824
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_3417
 *  net.minecraft.class_3419
 *  net.minecraft.class_355
 *  net.minecraft.class_3966
 *  net.minecraft.class_636
 *  net.minecraft.class_638
 *  net.minecraft.class_640
 *  net.minecraft.class_746
 *  net.minecraft.class_8646
 *  net.minecraft.class_9011
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.mining;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.Grouping;
import kotlin.collections.GroupingKt;
import kotlin.collections.SetsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1531;
import net.minecraft.class_1548;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1936;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_2504;
import net.minecraft.class_2506;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2649;
import net.minecraft.class_2653;
import net.minecraft.class_266;
import net.minecraft.class_2675;
import net.minecraft.class_268;
import net.minecraft.class_2680;
import net.minecraft.class_269;
import net.minecraft.class_2824;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_355;
import net.minecraft.class_3966;
import net.minecraft.class_636;
import net.minecraft.class_638;
import net.minecraft.class_640;
import net.minecraft.class_746;
import net.minecraft.class_8646;
import net.minecraft.class_9011;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.BlockChangeEvent;
import org.cobalt.api.event.impl.client.ChatEvent;
import org.cobalt.api.event.impl.client.PacketEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.ColorSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.module.setting.impl.TextSetting;
import org.cobalt.api.notification.NotificationManager;
import org.cobalt.api.util.ChatUtils;
import org.cobalt.api.util.ItemUtilsKt;
import org.cobalt.api.util.SkyblockItemUtilsKt;
import org.cobalt.api.util.render.Render3D;
import org.cobalt.mixin.client.MultiPlayerGameModeAccessor;
import org.cobalt.mixin.client.TabOverlayAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00ce\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010!\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\n\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010#\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\"\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0004\u00d5\u0001\u00d6\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\n\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\tH\u0007\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0017\u0010\r\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\fH\u0007\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0017\u0010\u0010\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u000fH\u0007\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0017\u0010\u0013\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0012H\u0007\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0017\u0010\u0016\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0015H\u0007\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0017\u0010\u001a\u001a\u00020\u00062\u0006\u0010\u0019\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u0017\u0010\u001c\u001a\u00020\u00062\u0006\u0010\u0019\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b\u001c\u0010\u001bJ\u000f\u0010\u001d\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u001d\u0010\u0003J\u0017\u0010\u001e\u001a\u00020\u00062\u0006\u0010\u0019\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b\u001e\u0010\u001bJ\u0017\u0010!\u001a\u00020\u00062\u0006\u0010 \u001a\u00020\u001fH\u0002\u00a2\u0006\u0004\b!\u0010\"J\u0017\u0010$\u001a\u00020\u00062\u0006\u0010 \u001a\u00020#H\u0002\u00a2\u0006\u0004\b$\u0010%J\u0017\u0010'\u001a\u00020\u00062\u0006\u0010 \u001a\u00020&H\u0002\u00a2\u0006\u0004\b'\u0010(J\u001f\u0010+\u001a\u00020\u00062\u0006\u0010*\u001a\u00020)2\u0006\u0010\u0005\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b+\u0010,J\u001f\u0010-\u001a\u00020\u00062\u0006\u0010*\u001a\u00020)2\u0006\u0010\u0005\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b-\u0010,J\u0017\u0010.\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b.\u0010\u0017J\u0017\u0010/\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b/\u0010\u0017J\u0017\u00100\u001a\u00020\u00062\u0006\u0010*\u001a\u00020)H\u0002\u00a2\u0006\u0004\b0\u00101J\u0017\u00102\u001a\u00020\u00062\u0006\u0010*\u001a\u00020)H\u0002\u00a2\u0006\u0004\b2\u00101J\u000f\u00103\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b3\u0010\u0003J\u000f\u00104\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b4\u0010\u0003J#\u00108\u001a\u00020\u00062\b\u00106\u001a\u0004\u0018\u0001052\b\u00107\u001a\u0004\u0018\u000105H\u0002\u00a2\u0006\u0004\b8\u00109J\u0011\u0010:\u001a\u0004\u0018\u000105H\u0002\u00a2\u0006\u0004\b:\u0010;J\u0015\u0010=\u001a\b\u0012\u0004\u0012\u0002050<H\u0002\u00a2\u0006\u0004\b=\u0010>J\u0015\u0010?\u001a\b\u0012\u0004\u0012\u0002050<H\u0002\u00a2\u0006\u0004\b?\u0010>J%\u0010D\u001a\u00020\u00062\f\u0010A\u001a\b\u0012\u0004\u0012\u0002050@2\u0006\u0010C\u001a\u00020BH\u0002\u00a2\u0006\u0004\bD\u0010EJ%\u0010G\u001a\u00020\u00062\f\u0010A\u001a\b\u0012\u0004\u0012\u0002050@2\u0006\u0010F\u001a\u000205H\u0002\u00a2\u0006\u0004\bG\u0010HJ\u0017\u0010L\u001a\u00020K2\u0006\u0010J\u001a\u00020IH\u0002\u00a2\u0006\u0004\bL\u0010MJ\u0017\u0010N\u001a\u0002052\u0006\u0010J\u001a\u00020IH\u0002\u00a2\u0006\u0004\bN\u0010OJ\u0017\u0010Q\u001a\u00020P2\u0006\u0010J\u001a\u00020IH\u0002\u00a2\u0006\u0004\bQ\u0010RJ\u0019\u0010T\u001a\u0004\u0018\u0001052\u0006\u0010S\u001a\u000205H\u0002\u00a2\u0006\u0004\bT\u0010UJ\u0017\u0010W\u001a\u00020K2\u0006\u0010V\u001a\u000205H\u0002\u00a2\u0006\u0004\bW\u0010XJ\u0017\u0010Z\u001a\u00020P2\u0006\u0010Y\u001a\u000205H\u0002\u00a2\u0006\u0004\bZ\u0010[J\u0017\u0010_\u001a\u00020^2\u0006\u0010]\u001a\u00020\\H\u0002\u00a2\u0006\u0004\b_\u0010`J\u0017\u0010b\u001a\u00020K2\u0006\u0010a\u001a\u00020^H\u0002\u00a2\u0006\u0004\bb\u0010cJ\u0017\u0010e\u001a\u00020d2\u0006\u0010a\u001a\u00020^H\u0002\u00a2\u0006\u0004\be\u0010fJ\u0019\u0010i\u001a\u0004\u0018\u0001052\u0006\u0010h\u001a\u00020gH\u0002\u00a2\u0006\u0004\bi\u0010jJ\u001f\u0010k\u001a\u00020K2\u0006\u0010\u0019\u001a\u00020\u00182\u0006\u0010h\u001a\u00020gH\u0002\u00a2\u0006\u0004\bk\u0010lJ)\u0010p\u001a\u0004\u0018\u00010m2\u0006\u0010n\u001a\u00020m2\u0006\u0010*\u001a\u00020)2\u0006\u0010o\u001a\u00020PH\u0002\u00a2\u0006\u0004\bp\u0010qJ\u0017\u0010r\u001a\u00020\u00062\u0006\u0010V\u001a\u000205H\u0002\u00a2\u0006\u0004\br\u0010sJ7\u0010z\u001a\u00020\u00062\u0006\u0010t\u001a\u0002052\u0006\u0010u\u001a\u0002052\u0006\u0010w\u001a\u00020v2\u0006\u0010y\u001a\u00020x2\u0006\u0010\u0019\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\bz\u0010{J\u0017\u0010}\u001a\u00020d2\u0006\u0010|\u001a\u00020PH\u0002\u00a2\u0006\u0004\b}\u0010~J\"\u0010\u0080\u0001\u001a\u00020d2\u0006\u0010}\u001a\u00020d2\u0006\u0010\u007f\u001a\u00020PH\u0002\u00a2\u0006\u0006\b\u0080\u0001\u0010\u0081\u0001J&\u0010\u0086\u0001\u001a\u00030\u0085\u00012\u0007\u0010\u0082\u0001\u001a\u00020m2\b\u0010\u0084\u0001\u001a\u00030\u0083\u0001H\u0002\u00a2\u0006\u0006\b\u0086\u0001\u0010\u0087\u0001J\u001a\u0010\u0089\u0001\u001a\u0002052\u0007\u0010\u0088\u0001\u001a\u000205H\u0002\u00a2\u0006\u0005\b\u0089\u0001\u0010UJ\u001a\u0010\u008a\u0001\u001a\u0002052\u0007\u0010\u0088\u0001\u001a\u000205H\u0002\u00a2\u0006\u0005\b\u008a\u0001\u0010UJ\u001c\u0010\u008d\u0001\u001a\u00020K2\b\u0010\u008c\u0001\u001a\u00030\u008b\u0001H\u0002\u00a2\u0006\u0006\b\u008d\u0001\u0010\u008e\u0001J\u0015\u0010\u008f\u0001\u001a\u000205*\u00020IH\u0002\u00a2\u0006\u0005\b\u008f\u0001\u0010OR\u0018\u0010\u0091\u0001\u001a\u00030\u0090\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0091\u0001\u0010\u0092\u0001R\u0017\u0010\u0093\u0001\u001a\u0002058\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u0093\u0001\u0010\u0094\u0001R\u0017\u0010\u0095\u0001\u001a\u0002058\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u0095\u0001\u0010\u0094\u0001R\u0017\u0010\u0096\u0001\u001a\u0002058\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u0096\u0001\u0010\u0094\u0001R\u0017\u0010\u0097\u0001\u001a\u0002058\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u0097\u0001\u0010\u0094\u0001R\u001e\u0010\u0099\u0001\u001a\t\u0012\u0004\u0012\u0002050\u0098\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0099\u0001\u0010\u009a\u0001R\u0018\u0010\u009c\u0001\u001a\u00030\u009b\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u009c\u0001\u0010\u009d\u0001R\u0018\u0010\u009e\u0001\u001a\u00030\u009b\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u009e\u0001\u0010\u009d\u0001R\u0018\u0010\u00a0\u0001\u001a\u00030\u009f\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a0\u0001\u0010\u00a1\u0001R\u0018\u0010\u00a2\u0001\u001a\u00030\u009b\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a2\u0001\u0010\u009d\u0001R\u0018\u0010\u00a3\u0001\u001a\u00030\u009b\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a3\u0001\u0010\u009d\u0001R\u0018\u0010\u00a5\u0001\u001a\u00030\u00a4\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a5\u0001\u0010\u00a6\u0001R\u0018\u0010\u00a7\u0001\u001a\u00030\u00a4\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a7\u0001\u0010\u00a6\u0001R\u0018\u0010\u00a8\u0001\u001a\u00030\u00a4\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a8\u0001\u0010\u00a6\u0001R\u0018\u0010\u00a9\u0001\u001a\u00030\u00a4\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a9\u0001\u0010\u00a6\u0001R\u0018\u0010\u00aa\u0001\u001a\u00030\u009b\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00aa\u0001\u0010\u009d\u0001R\u0018\u0010\u00ab\u0001\u001a\u00030\u00a4\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00ab\u0001\u0010\u00a6\u0001R\u0018\u0010\u00ac\u0001\u001a\u00030\u009b\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00ac\u0001\u0010\u009d\u0001R\u0018\u0010\u00ad\u0001\u001a\u00030\u009b\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00ad\u0001\u0010\u009d\u0001R\u0018\u0010\u00ae\u0001\u001a\u00030\u009b\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00ae\u0001\u0010\u009d\u0001R\u0018\u0010\u00af\u0001\u001a\u00030\u009b\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00af\u0001\u0010\u009d\u0001R\u0018\u0010\u00b0\u0001\u001a\u00030\u00a4\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00b0\u0001\u0010\u00a6\u0001R\u0018\u0010\u00b1\u0001\u001a\u00030\u009b\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00b1\u0001\u0010\u009d\u0001R\u0018\u0010\u00b2\u0001\u001a\u00030\u00a4\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00b2\u0001\u0010\u00a6\u0001R\u0018\u0010\u00b3\u0001\u001a\u00030\u009b\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00b3\u0001\u0010\u009d\u0001R\u0018\u0010\u00b4\u0001\u001a\u00030\u009b\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00b4\u0001\u0010\u009d\u0001R\u0018\u0010\u00b5\u0001\u001a\u00030\u009b\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00b5\u0001\u0010\u009d\u0001R\u0018\u0010\u00b7\u0001\u001a\u00030\u00b6\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00b7\u0001\u0010\u00b8\u0001R\u0018\u0010\u00ba\u0001\u001a\u00030\u00b9\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00ba\u0001\u0010\u00bb\u0001R\u0018\u0010\u00bc\u0001\u001a\u00030\u00b9\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00bc\u0001\u0010\u00bb\u0001R\u001e\u0010\u00be\u0001\u001a\t\u0012\u0004\u0012\u00020P0\u00bd\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00be\u0001\u0010\u00bf\u0001R\u001f\u0010\u00c1\u0001\u001a\n\u0012\u0005\u0012\u00030\u00c0\u00010\u00bd\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00c1\u0001\u0010\u00bf\u0001R7\u0010\u00c5\u0001\u001a\"\u0012\u0004\u0012\u00020m\u0012\u0005\u0012\u00030\u00c3\u00010\u00c2\u0001j\u0010\u0012\u0004\u0012\u00020m\u0012\u0005\u0012\u00030\u00c3\u0001`\u00c4\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00c5\u0001\u0010\u00c6\u0001R\u001b\u0010\u00c7\u0001\u001a\u0004\u0018\u0001058\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00c7\u0001\u0010\u0094\u0001R\u001a\u0010\u00c9\u0001\u001a\u00030\u00c8\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00c9\u0001\u0010\u00ca\u0001R\u0019\u0010\u00cb\u0001\u001a\u00020P8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00cb\u0001\u0010\u00cc\u0001R\u0019\u0010\u00cd\u0001\u001a\u00020P8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00cd\u0001\u0010\u00cc\u0001R\u001b\u0010\u00ce\u0001\u001a\u0004\u0018\u00010m8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00ce\u0001\u0010\u00cf\u0001R\u0019\u0010\u00d0\u0001\u001a\u00020K8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00d0\u0001\u0010\u00d1\u0001R\u0019\u0010\u00d2\u0001\u001a\u00020P8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00d2\u0001\u0010\u00cc\u0001R\u001e\u0010\u00d4\u0001\u001a\t\u0012\u0004\u0012\u0002050\u00d3\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00d4\u0001\u0010\u00bf\u0001\u00a8\u0006\u00d7\u0001"}, d2={"Lorg/cobalt/internal/mining/NoFrillsMiningModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "Lorg/cobalt/api/event/impl/client/ChatEvent$Receive;", "onChat", "(Lorg/cobalt/api/event/impl/client/ChatEvent$Receive;)V", "Lorg/cobalt/api/event/impl/client/PacketEvent$Incoming;", "onIncomingPacket", "(Lorg/cobalt/api/event/impl/client/PacketEvent$Incoming;)V", "Lorg/cobalt/api/event/impl/client/PacketEvent$Outgoing;", "onOutgoingPacket", "(Lorg/cobalt/api/event/impl/client/PacketEvent$Outgoing;)V", "Lorg/cobalt/api/event/impl/client/BlockChangeEvent;", "onBlockChange", "(Lorg/cobalt/api/event/impl/client/BlockChangeEvent;)V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "onRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "Lnet/minecraft/class_746;", "player", "updateHeldMiningTool", "(Lnet/minecraft/class_746;)V", "tickAbilityAlert", "armAbilityCooldownFromChat", "tickScathaAlerts", "Lnet/minecraft/class_2675;", "packet", "handleEndNodeParticle", "(Lnet/minecraft/class_2675;)V", "Lnet/minecraft/class_2653;", "handleInventorySlotUpdate", "(Lnet/minecraft/class_2653;)V", "Lnet/minecraft/class_2649;", "handleInventoryContentUpdate", "(Lnet/minecraft/class_2649;)V", "Lnet/minecraft/class_638;", "level", "renderCorpseHighlights", "(Lnet/minecraft/class_638;Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "renderGhostHighlights", "renderEndNodes", "renderTempleSkip", "updateTempleSkipSpot", "(Lnet/minecraft/class_638;)V", "pruneEndNodes", "tickShaftAnnounce", "announceMineshaft", "", "previous", "next", "handleAreaTransition", "(Ljava/lang/String;Ljava/lang/String;)V", "resolveCurrentArea", "()Ljava/lang/String;", "", "readTabListLines", "()Ljava/util/List;", "readScoreboardLines", "", "target", "Lnet/minecraft/class_2561;", "component", "appendSanitizedComponentLines", "(Ljava/util/List;Lnet/minecraft/class_2561;)V", "raw", "appendSanitizedLines", "(Ljava/util/List;Ljava/lang/String;)V", "Lnet/minecraft/class_1799;", "stack", "", "isMiningTool", "(Lnet/minecraft/class_1799;)Z", "parseMiningAbility", "(Lnet/minecraft/class_1799;)Ljava/lang/String;", "", "extractLoreCooldownTicks", "(Lnet/minecraft/class_1799;)I", "ability", "findAbilityWidgetLine", "(Ljava/lang/String;)Ljava/lang/String;", "message", "isPickaxeAbilityUseMessage", "(Ljava/lang/String;)Z", "value", "parseDurationSeconds", "(Ljava/lang/String;)I", "Lnet/minecraft/class_1531;", "stand", "Lorg/cobalt/internal/mining/NoFrillsMiningModule$CorpseType;", "detectCorpseType", "(Lnet/minecraft/class_1531;)Lorg/cobalt/internal/mining/NoFrillsMiningModule$CorpseType;", "type", "hasKeyForCorpse", "(Lorg/cobalt/internal/mining/NoFrillsMiningModule$CorpseType;)Z", "Ljava/awt/Color;", "corpseColor", "(Lorg/cobalt/internal/mining/NoFrillsMiningModule$CorpseType;)Ljava/awt/Color;", "Lnet/minecraft/class_1297;", "entity", "detectWormType", "(Lnet/minecraft/class_1297;)Ljava/lang/String;", "isWithinWormAlertRadius", "(Lnet/minecraft/class_746;Lnet/minecraft/class_1297;)Z", "Lnet/minecraft/class_2338;", "origin", "maxDepth", "findGround", "(Lnet/minecraft/class_2338;Lnet/minecraft/class_638;I)Lnet/minecraft/class_2338;", "sendServerMessage", "(Ljava/lang/String;)V", "title", "description", "Lnet/minecraft/class_3414;", "sound", "", "pitch", "notifyOverlay", "(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/class_3414;FLnet/minecraft/class_746;)V", "argb", "color", "(I)Ljava/awt/Color;", "alpha", "withAlpha", "(Ljava/awt/Color;I)Ljava/awt/Color;", "pos", "", "inflate", "Lnet/minecraft/class_238;", "blockBox", "(Lnet/minecraft/class_2338;D)Lnet/minecraft/class_238;", "text", "normalize", "stripFormatting", "Lnet/minecraft/class_2680;", "state", "isGemstoneGlass", "(Lnet/minecraft/class_2680;)Z", "getSkyblockIdSafe", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "AREA_DWARVEN_MINES", "Ljava/lang/String;", "AREA_CRYSTAL_HOLLOWS", "AREA_MINESHAFT", "AREA_THE_END", "", "AREA_NAMES", "[Ljava/lang/String;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "abilityAlert", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "abilityCooldownOverride", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "corpseHighlight", "hideOpenedCorpses", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "lapisCorpseColor", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "tungstenCorpseColor", "umberCorpseColor", "vanguardCorpseColor", "ghostVision", "ghostColor", "scathaMining", "scathaSpawnAlert", "scathaCooldownAlert", "endNodeHighlight", "endNodeColor", "templeSkip", "templeSkipColor", "gemstoneDesyncFix", "breakResetFix", "shaftAnnounce", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "shaftMessage", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "Lkotlin/text/Regex;", "tooltipStripRegex", "Lkotlin/text/Regex;", "shaftIdRegex", "", "openedCorpseIds", "Ljava/util/Set;", "Ljava/util/UUID;", "seenWormIds", "Ljava/util/LinkedHashMap;", "", "Lkotlin/collections/LinkedHashMap;", "endNodeHighlights", "Ljava/util/LinkedHashMap;", "currentArea", "Lorg/cobalt/internal/mining/NoFrillsMiningModule$ToolData;", "currentTool", "Lorg/cobalt/internal/mining/NoFrillsMiningModule$ToolData;", "abilityCooldownTicks", "I", "scathaCooldownTicks", "templeSkipSpot", "Lnet/minecraft/class_2338;", "enteringMineshaft", "Z", "shaftAnnounceTicks", "", "GEMSTONE_FIX_AREAS", "CorpseType", "ToolData", "cobalt"})
@SourceDebugExtension(value={"SMAP\nNoFrillsMiningModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NoFrillsMiningModule.kt\norg/cobalt/internal/mining/NoFrillsMiningModule\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n1#1,936:1\n1642#2,10:937\n1915#2:947\n1916#2:950\n1652#2:951\n1573#2:952\n1915#2,2:953\n1642#2,10:955\n1915#2:965\n1916#2:967\n1652#2:968\n1586#2:971\n1661#2,3:972\n2792#2,3:975\n296#2,2:978\n296#2,2:980\n2792#2,3:982\n1#3:948\n1#3:949\n1#3:966\n1342#4,2:969\n*S KotlinDebug\n*F\n+ 1 NoFrillsMiningModule.kt\norg/cobalt/internal/mining/NoFrillsMiningModule\n*L\n645#1:937,10\n645#1:947\n645#1:950\n645#1:951\n652#1:952\n718#1:953,2\n732#1:955,10\n732#1:965\n732#1:967\n732#1:968\n754#1:971\n754#1:972,3\n756#1:975,3\n794#1:978,2\n813#1:980,2\n474#1:982,3\n645#1:949\n732#1:966\n748#1:969,2\n*E\n"})
public final class NoFrillsMiningModule
extends Module {
    @NotNull
    public static final NoFrillsMiningModule INSTANCE = new NoFrillsMiningModule();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final String AREA_DWARVEN_MINES = "Dwarven Mines";
    @NotNull
    private static final String AREA_CRYSTAL_HOLLOWS = "Crystal Hollows";
    @NotNull
    private static final String AREA_MINESHAFT = "Mineshaft";
    @NotNull
    private static final String AREA_THE_END = "The End";
    @NotNull
    private static final String[] AREA_NAMES;
    @NotNull
    private static final CheckboxSetting enabled;
    @NotNull
    private static final CheckboxSetting abilityAlert;
    @NotNull
    private static final SliderSetting abilityCooldownOverride;
    @NotNull
    private static final CheckboxSetting corpseHighlight;
    @NotNull
    private static final CheckboxSetting hideOpenedCorpses;
    @NotNull
    private static final ColorSetting lapisCorpseColor;
    @NotNull
    private static final ColorSetting tungstenCorpseColor;
    @NotNull
    private static final ColorSetting umberCorpseColor;
    @NotNull
    private static final ColorSetting vanguardCorpseColor;
    @NotNull
    private static final CheckboxSetting ghostVision;
    @NotNull
    private static final ColorSetting ghostColor;
    @NotNull
    private static final CheckboxSetting scathaMining;
    @NotNull
    private static final CheckboxSetting scathaSpawnAlert;
    @NotNull
    private static final CheckboxSetting scathaCooldownAlert;
    @NotNull
    private static final CheckboxSetting endNodeHighlight;
    @NotNull
    private static final ColorSetting endNodeColor;
    @NotNull
    private static final CheckboxSetting templeSkip;
    @NotNull
    private static final ColorSetting templeSkipColor;
    @NotNull
    private static final CheckboxSetting gemstoneDesyncFix;
    @NotNull
    private static final CheckboxSetting breakResetFix;
    @NotNull
    private static final CheckboxSetting shaftAnnounce;
    @NotNull
    private static final TextSetting shaftMessage;
    @NotNull
    private static final Regex tooltipStripRegex;
    @NotNull
    private static final Regex shaftIdRegex;
    @NotNull
    private static final Set<Integer> openedCorpseIds;
    @NotNull
    private static final Set<UUID> seenWormIds;
    @NotNull
    private static final LinkedHashMap<class_2338, Long> endNodeHighlights;
    @Nullable
    private static String currentArea;
    @NotNull
    private static ToolData currentTool;
    private static int abilityCooldownTicks;
    private static int scathaCooldownTicks;
    @Nullable
    private static class_2338 templeSkipSpot;
    private static boolean enteringMineshaft;
    private static int shaftAnnounceTicks;
    @NotNull
    private static final Set<String> GEMSTONE_FIX_AREAS;

    private NoFrillsMiningModule() {
        super("NoFrills Mining");
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_746 player = NoFrillsMiningModule.mc.field_1724;
        class_638 level2 = NoFrillsMiningModule.mc.field_1687;
        if (player == null || level2 == null) {
            currentArea = null;
            currentTool = new ToolData(null, null, 3, null);
            abilityCooldownTicks = 0;
            scathaCooldownTicks = 0;
            seenWormIds.clear();
            return;
        }
        String previousArea = currentArea;
        if (!Intrinsics.areEqual((Object)previousArea, (Object)(currentArea = this.resolveCurrentArea()))) {
            this.handleAreaTransition(previousArea, currentArea);
        }
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            return;
        }
        this.updateHeldMiningTool(player);
        this.tickAbilityAlert(player);
        this.tickScathaAlerts(player);
        this.updateTempleSkipSpot(level2);
        this.pruneEndNodes(level2);
        this.tickShaftAnnounce();
    }

    @SubscribeEvent
    public final void onChat(@NotNull ChatEvent.Receive event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            return;
        }
        String string = event.getMessage();
        if (string == null) {
            return;
        }
        String raw = string;
        String message = this.stripFormatting(raw);
        if (((Boolean)abilityAlert.getValue()).booleanValue() && this.isPickaxeAbilityUseMessage(message)) {
            this.armAbilityCooldownFromChat();
        }
        if (((Boolean)scathaMining.getValue()).booleanValue() && Intrinsics.areEqual((Object)message, (Object)"You hear the sound of something approaching...")) {
            scathaCooldownTicks = 620;
        }
        if (((Boolean)shaftAnnounce.getValue()).booleanValue() && Intrinsics.areEqual((Object)currentArea, (Object)AREA_DWARVEN_MINES) && Intrinsics.areEqual((Object)message, (Object)"Sending to Mineshaft...")) {
            enteringMineshaft = true;
            shaftAnnounceTicks = -1;
        }
    }

    @SubscribeEvent
    public final void onIncomingPacket(@NotNull PacketEvent.Incoming event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            return;
        }
        class_2596<?> packet = event.getPacket();
        if (packet instanceof class_2675) {
            if (((Boolean)endNodeHighlight.getValue()).booleanValue()) {
                this.handleEndNodeParticle((class_2675)packet);
            }
        } else if (packet instanceof class_2653) {
            if (((Boolean)breakResetFix.getValue()).booleanValue()) {
                this.handleInventorySlotUpdate((class_2653)packet);
            }
        } else if (packet instanceof class_2649 && ((Boolean)breakResetFix.getValue()).booleanValue()) {
            this.handleInventoryContentUpdate((class_2649)packet);
        }
    }

    @SubscribeEvent
    public final void onOutgoingPacket(@NotNull PacketEvent.Outgoing event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!(((Boolean)enabled.getValue()).booleanValue() && ((Boolean)corpseHighlight.getValue()).booleanValue() && ((Boolean)hideOpenedCorpses.getValue()).booleanValue())) {
            return;
        }
        if (!Intrinsics.areEqual((Object)currentArea, (Object)AREA_MINESHAFT)) {
            return;
        }
        if (!(event.getPacket() instanceof class_2824)) {
            return;
        }
        class_239 class_2392 = NoFrillsMiningModule.mc.field_1765;
        class_3966 class_39662 = class_2392 instanceof class_3966 ? (class_3966)class_2392 : null;
        class_1297 class_12972 = class_39662 != null ? class_39662.method_17782() : null;
        class_1531 class_15312 = class_12972 instanceof class_1531 ? (class_1531)class_12972 : null;
        if (class_15312 == null) {
            return;
        }
        class_1531 stand = class_15312;
        CorpseType corpseType = this.detectCorpseType(stand);
        if (corpseType == CorpseType.NONE || !this.hasKeyForCorpse(corpseType)) {
            return;
        }
        ((Collection)openedCorpseIds).add(stand.method_5628());
    }

    @SubscribeEvent
    public final void onBlockChange(@NotNull BlockChangeEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabled.getValue()).booleanValue() || !((Boolean)gemstoneDesyncFix.getValue()).booleanValue()) {
            return;
        }
        if (!CollectionsKt.contains((Iterable)GEMSTONE_FIX_AREAS, (Object)currentArea)) {
            return;
        }
        if (!event.getNewBlock().method_26215() || !this.isGemstoneGlass(event.getOldBlock())) {
            return;
        }
        class_638 class_6382 = NoFrillsMiningModule.mc.field_1687;
        if (class_6382 == null) {
            return;
        }
        class_638 level2 = class_6382;
        event.getNewBlock().method_30101((class_1936)level2, event.getPos(), 3);
        event.getNewBlock().method_30102((class_1936)level2, event.getPos(), 3);
        for (class_2350 direction : class_2350.values()) {
            class_2680 neighborState;
            class_2338 neighborPos;
            Intrinsics.checkNotNullExpressionValue((Object)event.getPos().method_10093(direction), (String)"relative(...)");
            Intrinsics.checkNotNullExpressionValue((Object)level2.method_8320(neighborPos), (String)"getBlockState(...)");
            if (!this.isGemstoneGlass(neighborState)) continue;
            level2.method_8413(neighborPos, neighborState, neighborState, 3);
        }
    }

    @SubscribeEvent
    public final void onRender(@NotNull WorldRenderEvent.Last event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            return;
        }
        class_638 class_6382 = NoFrillsMiningModule.mc.field_1687;
        if (class_6382 == null) {
            return;
        }
        class_638 level2 = class_6382;
        if (((Boolean)corpseHighlight.getValue()).booleanValue() && Intrinsics.areEqual((Object)currentArea, (Object)AREA_MINESHAFT)) {
            this.renderCorpseHighlights(level2, event);
        }
        if (((Boolean)ghostVision.getValue()).booleanValue() && Intrinsics.areEqual((Object)currentArea, (Object)AREA_DWARVEN_MINES)) {
            this.renderGhostHighlights(level2, event);
        }
        if (((Boolean)endNodeHighlight.getValue()).booleanValue() && Intrinsics.areEqual((Object)currentArea, (Object)AREA_THE_END)) {
            this.renderEndNodes(event);
        }
        if (((Boolean)templeSkip.getValue()).booleanValue() && Intrinsics.areEqual((Object)currentArea, (Object)AREA_CRYSTAL_HOLLOWS)) {
            this.renderTempleSkip(event);
        }
    }

    private final void updateHeldMiningTool(class_746 player) {
        ToolData toolData;
        class_1799 class_17992 = player.method_6047();
        Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getMainHandItem(...)");
        class_1799 stack = class_17992;
        if (this.isMiningTool(stack)) {
            class_1799 class_17993 = stack.method_7972();
            Intrinsics.checkNotNullExpressionValue((Object)class_17993, (String)"copy(...)");
            toolData = new ToolData(class_17993, this.parseMiningAbility(stack));
        } else {
            toolData = new ToolData(null, null, 3, null);
        }
        currentTool = toolData;
    }

    private final void tickAbilityAlert(class_746 player) {
        int n;
        if (!((Boolean)abilityAlert.getValue()).booleanValue() || !currentTool.isValid()) {
            abilityCooldownTicks = 0;
            return;
        }
        String widget = this.findAbilityWidgetLine(currentTool.getAbility());
        if (widget != null) {
            int seconds;
            String status = ((Object)StringsKt.trim((CharSequence)StringsKt.substringAfter((String)widget, (char)':', (String)""))).toString();
            if (abilityCooldownTicks > 1 && StringsKt.equals((String)status, (String)"Available", (boolean)true)) {
                abilityCooldownTicks = 1;
            }
            if (abilityCooldownTicks == 0 && (seconds = this.parseDurationSeconds(status)) > 6) {
                abilityCooldownTicks = seconds * 20;
            }
        }
        if (abilityCooldownTicks > 0 && (abilityCooldownTicks = (n = abilityCooldownTicks) + -1) == 0) {
            String string = currentTool.getAbility();
            Locale locale = Locale.ROOT;
            Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
            String string2 = string.toUpperCase(locale);
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toUpperCase(...)");
            Object object = class_3417.field_14622.comp_349();
            Intrinsics.checkNotNullExpressionValue((Object)object, (String)"value(...)");
            this.notifyOverlay(string2, "Pickaxe ability ready.", (class_3414)object, 1.0f, player);
        }
    }

    private final void armAbilityCooldownFromChat() {
        if (!currentTool.isValid()) {
            return;
        }
        if (this.findAbilityWidgetLine(currentTool.getAbility()) != null) {
            return;
        }
        int overrideSeconds = (int)((Number)abilityCooldownOverride.getValue()).doubleValue();
        abilityCooldownTicks = overrideSeconds > 0 ? overrideSeconds * 20 : this.extractLoreCooldownTicks(currentTool.getTool());
    }

    private final void tickScathaAlerts(class_746 player) {
        int n;
        if (!((Boolean)scathaMining.getValue()).booleanValue()) {
            scathaCooldownTicks = 0;
            seenWormIds.clear();
            return;
        }
        if (!Intrinsics.areEqual((Object)currentArea, (Object)AREA_CRYSTAL_HOLLOWS)) {
            seenWormIds.clear();
            return;
        }
        class_638 class_6382 = NoFrillsMiningModule.mc.field_1687;
        if (class_6382 == null) {
            return;
        }
        class_638 level2 = class_6382;
        if (((Boolean)scathaSpawnAlert.getValue()).booleanValue()) {
            for (Object t : level2.method_18112()) {
                String type;
                Intrinsics.checkNotNullExpressionValue(t, (String)"next(...)");
                class_1297 entity = (class_1297)t;
                if (this.detectWormType(entity) == null || !this.isWithinWormAlertRadius(player, entity)) continue;
                UUID uUID = entity.method_5667();
                Intrinsics.checkNotNullExpressionValue((Object)uUID, (String)"getUUID(...)");
                if (!seenWormIds.add(uUID)) continue;
                String string = type;
                if (Intrinsics.areEqual((Object)string, (Object)"scatha")) {
                    Object object = class_3417.field_14622.comp_349();
                    Intrinsics.checkNotNullExpressionValue((Object)object, (String)"value(...)");
                    this.notifyOverlay("Scatha", "Scatha spawned nearby.", (class_3414)object, 1.0f, player);
                    continue;
                }
                if (!Intrinsics.areEqual((Object)string, (Object)"worm")) continue;
                Object object = class_3417.field_14624.comp_349();
                Intrinsics.checkNotNullExpressionValue((Object)object, (String)"value(...)");
                this.notifyOverlay("Worm", "Worm spawned nearby.", (class_3414)object, 0.7f, player);
            }
        }
        seenWormIds.removeIf(arg_0 -> NoFrillsMiningModule.tickScathaAlerts$lambda$1(arg_0 -> NoFrillsMiningModule.tickScathaAlerts$lambda$0(level2, arg_0), arg_0));
        if (scathaCooldownTicks > 0 && (scathaCooldownTicks = (n = scathaCooldownTicks) + -1) == 0 && ((Boolean)scathaCooldownAlert.getValue()).booleanValue()) {
            Object object = class_3417.field_15114.comp_349();
            Intrinsics.checkNotNullExpressionValue((Object)object, (String)"value(...)");
            this.notifyOverlay("Cooldown Ended", "Worm spawn cooldown ended.", (class_3414)object, 0.9f, player);
            ChatUtils.sendMessage("Worm spawn cooldown ended.");
        }
    }

    private final void handleEndNodeParticle(class_2675 packet) {
        boolean offsetMatches;
        if (!Intrinsics.areEqual((Object)currentArea, (Object)AREA_THE_END)) {
            return;
        }
        if (!Intrinsics.areEqual((Object)packet.method_11551().method_10295(), (Object)class_2398.field_11249)) {
            return;
        }
        if (!packet.method_11552() || !packet.method_65082()) {
            return;
        }
        if (packet.method_11545() != 2 || !(packet.method_11543() == 0.0f)) {
            return;
        }
        boolean bl = packet.method_11548() == 0.25f || packet.method_11549() == 0.25f || packet.method_11550() == 0.25f ? true : (offsetMatches = false);
        if (!offsetMatches) {
            return;
        }
        class_638 class_6382 = NoFrillsMiningModule.mc.field_1687;
        if (class_6382 == null) {
            return;
        }
        class_638 level2 = class_6382;
        class_2338 class_23382 = class_2338.method_49637((double)packet.method_11544(), (double)packet.method_11547(), (double)packet.method_11546());
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"containing(...)");
        class_2338 origin = class_23382;
        long now = System.currentTimeMillis();
        for (class_2350 direction : class_2350.values()) {
            class_2338 candidate;
            Intrinsics.checkNotNullExpressionValue((Object)origin.method_10093(direction), (String)"relative(...)");
            if (!Intrinsics.areEqual((Object)level2.method_8320(candidate).method_26204(), (Object)class_2246.field_10570)) continue;
            ((Map)endNodeHighlights).put(candidate.method_10062(), now);
            break;
        }
    }

    private final void handleInventorySlotUpdate(class_2653 packet) {
        if (packet.method_11452() != 0) {
            return;
        }
        class_746 class_7462 = NoFrillsMiningModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        int selectedHotbarSlot = player.method_31548().method_67532();
        int expectedSlot = 36 + selectedHotbarSlot;
        if (packet.method_11450() != expectedSlot) {
            return;
        }
        class_636 class_6362 = NoFrillsMiningModule.mc.field_1761;
        MultiPlayerGameModeAccessor multiPlayerGameModeAccessor = class_6362 instanceof MultiPlayerGameModeAccessor ? (MultiPlayerGameModeAccessor)class_6362 : null;
        if (multiPlayerGameModeAccessor == null) {
            return;
        }
        MultiPlayerGameModeAccessor gameMode = multiPlayerGameModeAccessor;
        gameMode.setDestroyingItemCobalt(packet.method_11449().method_7972());
    }

    private final void handleInventoryContentUpdate(class_2649 packet) {
        if (packet.comp_3837() != 0) {
            return;
        }
        class_746 class_7462 = NoFrillsMiningModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        int expectedSlot = 36 + player.method_31548().method_67532();
        List list = packet.comp_3839();
        Intrinsics.checkNotNullExpressionValue((Object)list, (String)"items(...)");
        class_1799 class_17992 = (class_1799)CollectionsKt.getOrNull((List)list, (int)expectedSlot);
        if (class_17992 == null) {
            return;
        }
        class_1799 selectedStack = class_17992;
        class_636 class_6362 = NoFrillsMiningModule.mc.field_1761;
        MultiPlayerGameModeAccessor multiPlayerGameModeAccessor = class_6362 instanceof MultiPlayerGameModeAccessor ? (MultiPlayerGameModeAccessor)class_6362 : null;
        if (multiPlayerGameModeAccessor == null) {
            return;
        }
        MultiPlayerGameModeAccessor gameMode = multiPlayerGameModeAccessor;
        gameMode.setDestroyingItemCobalt(selectedStack.method_7972());
    }

    private final void renderCorpseHighlights(class_638 level2, WorldRenderEvent.Last event) {
        for (Object t : level2.method_18112()) {
            class_238 box;
            CorpseType corpseType;
            class_1531 stand;
            Intrinsics.checkNotNullExpressionValue(t, (String)"next(...)");
            class_1297 entity = (class_1297)t;
            class_1531 class_15312 = entity instanceof class_1531 ? (class_1531)entity : null;
            if (class_15312 == null || (stand = class_15312).method_5767() || !stand.method_5805() || ((Boolean)hideOpenedCorpses.getValue()).booleanValue() && openedCorpseIds.contains(stand.method_5628()) || (corpseType = this.detectCorpseType(stand)) == CorpseType.NONE) continue;
            Color outline = this.corpseColor(corpseType);
            Color fill = this.withAlpha(outline, 96);
            Intrinsics.checkNotNullExpressionValue((Object)stand.method_5829().method_1009(0.25, 0.0, 0.25), (String)"inflate(...)");
            Render3D.drawStyledBox(event.getContext(), box, outline, fill, true, 2.2f);
        }
    }

    private final void renderGhostHighlights(class_638 level2, WorldRenderEvent.Last event) {
        Color outline = this.color(ghostColor.getValue());
        Color fill = this.withAlpha(outline, 90);
        for (Object t : level2.method_18112()) {
            class_238 box;
            class_1548 creeper;
            Intrinsics.checkNotNullExpressionValue(t, (String)"next(...)");
            class_1297 entity = (class_1297)t;
            class_1548 class_15482 = entity instanceof class_1548 ? (class_1548)entity : null;
            if (class_15482 == null || !(creeper = class_15482).method_5805() || creeper.method_23318() >= 100.0) continue;
            Intrinsics.checkNotNullExpressionValue((Object)creeper.method_5829().method_1014(0.1), (String)"inflate(...)");
            Render3D.drawStyledBox(event.getContext(), box, outline, fill, true, 2.2f);
        }
    }

    private final void renderEndNodes(WorldRenderEvent.Last event) {
        class_638 class_6382 = NoFrillsMiningModule.mc.field_1687;
        if (class_6382 == null) {
            return;
        }
        class_638 level2 = class_6382;
        Color outline = this.color(endNodeColor.getValue());
        Color fill = this.withAlpha(outline, 100);
        Set<class_2338> set = endNodeHighlights.keySet();
        Intrinsics.checkNotNullExpressionValue(set, (String)"<get-keys>(...)");
        for (class_2338 pos : CollectionsKt.toList((Iterable)set)) {
            if (!Intrinsics.areEqual((Object)level2.method_8320(pos).method_26204(), (Object)class_2246.field_10570)) {
                endNodeHighlights.remove(pos);
                continue;
            }
            class_238 box = this.blockBox(pos, 0.02);
            Render3D.drawStyledBox(event.getContext(), box, outline, fill, true, 2.2f);
        }
    }

    private final void renderTempleSkip(WorldRenderEvent.Last event) {
        class_2338 class_23382 = templeSkipSpot;
        if (class_23382 == null) {
            return;
        }
        class_2338 spot = class_23382;
        Color outline = this.color(templeSkipColor.getValue());
        Color fill = this.withAlpha(outline, 70);
        Render3D.drawStyledBox(event.getContext(), this.blockBox(spot, 0.03), outline, fill, true, 2.2f);
        class_2338 class_23383 = spot.method_10087(8);
        Intrinsics.checkNotNullExpressionValue((Object)class_23383, (String)"below(...)");
        class_2338 standPos = class_23383;
        Render3D.drawStyledBox(event.getContext(), this.blockBox(standPos, 0.03), outline, fill, true, 2.2f);
        class_243 start = new class_243((double)spot.method_10263() + 0.5, (double)spot.method_10264() + 0.5, (double)spot.method_10260() + 0.5);
        class_243 end = new class_243((double)standPos.method_10263() + 0.5, (double)standPos.method_10264() + 0.5, (double)standPos.method_10260() + 0.5);
        Render3D.drawLine(event.getContext(), start, end, outline, true, 1.8f);
    }

    private final void updateTempleSkipSpot(class_638 level2) {
        if (!((Boolean)templeSkip.getValue()).booleanValue() || !Intrinsics.areEqual((Object)currentArea, (Object)AREA_CRYSTAL_HOLLOWS) || templeSkipSpot != null) {
            return;
        }
        for (Object t : level2.method_18112()) {
            class_2338 ground;
            Intrinsics.checkNotNullExpressionValue(t, (String)"next(...)");
            class_1297 entity = (class_1297)t;
            String string = entity.method_5477().getString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
            String normalizedName = this.normalize(string);
            if (!Intrinsics.areEqual((Object)normalizedName, (Object)"kalhuiki door guardian")) continue;
            class_2338 class_23382 = entity.method_24515();
            Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"blockPosition(...)");
            if (this.findGround(class_23382, level2, 4) == null || !Intrinsics.areEqual((Object)level2.method_8320(ground).method_26204(), (Object)class_2246.field_10056)) continue;
            templeSkipSpot = ground.method_10069(20, -45, -35).method_10062();
            break;
        }
    }

    private final void pruneEndNodes(class_638 level2) {
        if (!Intrinsics.areEqual((Object)currentArea, (Object)AREA_THE_END)) {
            endNodeHighlights.clear();
            return;
        }
        long now = System.currentTimeMillis();
        endNodeHighlights.entrySet().removeIf(arg_0 -> NoFrillsMiningModule.pruneEndNodes$lambda$1(arg_0 -> NoFrillsMiningModule.pruneEndNodes$lambda$0(now, level2, arg_0), arg_0));
    }

    private final void tickShaftAnnounce() {
        if (!((Boolean)shaftAnnounce.getValue()).booleanValue()) {
            enteringMineshaft = false;
            shaftAnnounceTicks = -1;
            return;
        }
        if (enteringMineshaft && Intrinsics.areEqual((Object)currentArea, (Object)AREA_MINESHAFT) && shaftAnnounceTicks < 0) {
            shaftAnnounceTicks = 120;
        }
        if (shaftAnnounceTicks > 0) {
            int n = shaftAnnounceTicks;
            shaftAnnounceTicks = n + -1;
        }
        if (shaftAnnounceTicks == 0 && Intrinsics.areEqual((Object)currentArea, (Object)AREA_MINESHAFT)) {
            this.announceMineshaft();
            enteringMineshaft = false;
            shaftAnnounceTicks = -1;
        }
    }

    /*
     * WARNING - void declaration
     */
    private final void announceMineshaft() {
        String string;
        String string2;
        String corpseSummary;
        block7: {
            CharSequence charSequence;
            void $this$mapNotNullTo$iv$iv;
            Iterable $this$mapNotNull$iv = this.readTabListLines();
            boolean $i$f$mapNotNull = false;
            Iterable iterable = $this$mapNotNull$iv;
            Collection destination$iv$iv = new ArrayList();
            boolean $i$f$mapNotNullTo = false;
            void $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
            boolean $i$f$forEach = false;
            Iterator iterator = $this$forEach$iv$iv$iv.iterator();
            while (iterator.hasNext()) {
                String string3;
                Object element$iv$iv$iv;
                Object element$iv$iv = element$iv$iv$iv = iterator.next();
                boolean bl = false;
                String line = (String)element$iv$iv;
                boolean bl2 = false;
                if (StringsKt.endsWith((String)line, (String)": LOOTED", (boolean)true) || StringsKt.endsWith((String)line, (String)": NOT LOOTED", (boolean)true)) {
                    String string4;
                    String it = string4 = ((Object)StringsKt.trim((CharSequence)StringsKt.substringBefore$default((String)line, (char)':', null, (int)2, null))).toString();
                    boolean bl3 = false;
                    string3 = ((CharSequence)it).length() > 0 ? string4 : null;
                } else {
                    string3 = null;
                }
                if (string3 == null) continue;
                String it$iv$iv = string3;
                boolean bl4 = false;
                destination$iv$iv.add(it$iv$iv);
            }
            Iterable $this$groupingBy$iv = (List)destination$iv$iv;
            boolean $i$f$groupingBy22 = false;
            Map corpses = GroupingKt.eachCount((Grouping)((Grouping)new Grouping<String, String>($this$groupingBy$iv){
                final /* synthetic */ Iterable $this_groupingBy;
                {
                    this.$this_groupingBy = $receiver;
                }

                public Iterator<String> sourceIterator() {
                    return this.$this_groupingBy.iterator();
                }

                /*
                 * Ignored method signature, as it can't be verified against descriptor
                 * WARNING - void declaration
                 */
                public Object keyOf(Object element) {
                    void var2_2;
                    String it = (String)element;
                    boolean bl = false;
                    return var2_2;
                }
            }));
            CharSequence $i$f$groupingBy22 = CollectionsKt.joinToString$default((Iterable)corpses.entrySet(), (CharSequence)", ", null, null, (int)0, null, NoFrillsMiningModule::announceMineshaft$lambda$2, (int)30, null);
            if ($i$f$groupingBy22.length() == 0) {
                boolean bl = false;
                charSequence = "None";
            } else {
                charSequence = $i$f$groupingBy22;
            }
            corpseSummary = (String)charSequence;
            for (String line : (Iterable)this.readScoreboardLines()) {
                boolean bl = false;
                MatchResult matchResult = Regex.find$default((Regex)shaftIdRegex, (CharSequence)line, (int)0, (int)2, null);
                String string5 = matchResult != null ? matchResult.getValue() : null;
                if (string5 == null) continue;
                string2 = string5;
                break block7;
            }
            string2 = string = null;
        }
        if (string2 == null) {
            string = "Unknown ID";
        }
        String shaftId = string;
        this.sendServerMessage(StringsKt.replace$default((String)StringsKt.replace$default((String)((String)shaftMessage.getValue()), (String)"{id}", (String)shaftId, (boolean)false, (int)4, null), (String)"{corpses}", (String)corpseSummary, (boolean)false, (int)4, null));
    }

    private final void handleAreaTransition(String previous, String next) {
        if (Intrinsics.areEqual((Object)previous, (Object)AREA_MINESHAFT) && !Intrinsics.areEqual((Object)next, (Object)AREA_MINESHAFT)) {
            openedCorpseIds.clear();
        }
        if (!Intrinsics.areEqual((Object)next, (Object)AREA_CRYSTAL_HOLLOWS)) {
            seenWormIds.clear();
            templeSkipSpot = null;
        }
        if (!Intrinsics.areEqual((Object)next, (Object)AREA_THE_END)) {
            endNodeHighlights.clear();
        }
        if (enteringMineshaft && Intrinsics.areEqual((Object)next, (Object)AREA_MINESHAFT)) {
            shaftAnnounceTicks = 120;
        }
        if (Intrinsics.areEqual((Object)previous, (Object)AREA_DWARVEN_MINES) && !Intrinsics.areEqual((Object)next, (Object)AREA_MINESHAFT) && !Intrinsics.areEqual((Object)next, (Object)AREA_DWARVEN_MINES)) {
            enteringMineshaft = false;
            shaftAnnounceTicks = -1;
        }
    }

    private final String resolveCurrentArea() {
        List searchableLines = CollectionsKt.plus((Collection)this.readTabListLines(), (Iterable)this.readScoreboardLines());
        for (String line : searchableLines) {
            for (String area : AREA_NAMES) {
                if (!StringsKt.contains((CharSequence)line, (CharSequence)area, (boolean)true)) continue;
                return area;
            }
        }
        return null;
    }

    private final List<String> readTabListLines() {
        List lines;
        block4: {
            Object object;
            TabOverlayAccessor tabOverlayAccessor;
            TabOverlayAccessor component;
            lines = new ArrayList();
            class_355 class_3552 = NoFrillsMiningModule.mc.field_1705.method_1750();
            TabOverlayAccessor overlay = class_3552 instanceof TabOverlayAccessor ? (TabOverlayAccessor)class_3552 : null;
            TabOverlayAccessor tabOverlayAccessor2 = overlay;
            if (tabOverlayAccessor2 != null && (tabOverlayAccessor2 = tabOverlayAccessor2.getHeader()) != null) {
                component = tabOverlayAccessor2;
                boolean bl = false;
                INSTANCE.appendSanitizedComponentLines(lines, (class_2561)component);
            }
            if ((tabOverlayAccessor = overlay) != null && (tabOverlayAccessor = tabOverlayAccessor.getFooter()) != null) {
                component = tabOverlayAccessor;
                boolean bl = false;
                INSTANCE.appendSanitizedComponentLines(lines, (class_2561)component);
            }
            if ((object = mc.method_1562()) == null || (object = object.method_45732()) == null) break block4;
            Iterable $this$forEach$iv = (Iterable)object;
            boolean $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                class_640 info = (class_640)element$iv;
                boolean bl = false;
                Object object2 = info.method_2971();
                if (object2 == null || (object2 = object2.getString()) == null) {
                    object2 = info.method_2966().name();
                }
                Object text = object2;
                Intrinsics.checkNotNull((Object)text);
                INSTANCE.appendSanitizedLines(lines, (String)text);
            }
        }
        return lines;
    }

    /*
     * WARNING - void declaration
     */
    private final List<String> readScoreboardLines() {
        void $this$mapNotNullTo$iv$iv;
        void $this$mapNotNull$iv;
        class_638 class_6382 = NoFrillsMiningModule.mc.field_1687;
        if (class_6382 == null) {
            return CollectionsKt.emptyList();
        }
        class_638 level2 = class_6382;
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
        Iterable iterable = collection;
        boolean $i$f$mapNotNull = false;
        void var6_6 = $this$mapNotNull$iv;
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

    private final void appendSanitizedComponentLines(List<String> target, class_2561 component) {
        String string = component.getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        this.appendSanitizedLines(target, string);
    }

    private final void appendSanitizedLines(List<String> target, String raw) {
        Sequence $this$forEach$iv = SequencesKt.filter((Sequence)SequencesKt.map((Sequence)StringsKt.lineSequence((CharSequence)raw), (Function1)((Function1)new Function1<String, String>((Object)this){

            public final String invoke(String p0) {
                Intrinsics.checkNotNullParameter((Object)p0, (String)"p0");
                return NoFrillsMiningModule.access$stripFormatting((NoFrillsMiningModule)this.receiver, p0);
            }
        })), NoFrillsMiningModule::appendSanitizedLines$lambda$0);
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            String p0 = (String)element$iv;
            boolean bl = false;
            target.add(p0);
        }
    }

    /*
     * WARNING - void declaration
     */
    private final boolean isMiningTool(class_1799 stack) {
        boolean bl;
        List loreLines;
        block7: {
            void $this$mapTo$iv$iv;
            if (stack.method_7960()) {
                return false;
            }
            Iterable $this$map$iv = ItemUtilsKt.getLoreLines(stack);
            boolean $i$f$map = false;
            Iterable iterable = $this$map$iv;
            Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
            boolean $i$f$mapTo = false;
            for (Object item$iv$iv : $this$mapTo$iv$iv) {
                void it;
                class_2561 class_25612 = (class_2561)item$iv$iv;
                Collection collection = destination$iv$iv;
                boolean bl2 = false;
                String string = it.getString();
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
                collection.add(INSTANCE.stripFormatting(string));
            }
            loreLines = (List)destination$iv$iv;
            if (loreLines.isEmpty()) {
                return false;
            }
            Iterable $this$none$iv = loreLines;
            boolean $i$f$none = false;
            if ($this$none$iv instanceof Collection && ((Collection)$this$none$iv).isEmpty()) {
                bl = true;
            } else {
                for (Object element$iv : $this$none$iv) {
                    String it = (String)element$iv;
                    boolean bl3 = false;
                    if (!StringsKt.contains((CharSequence)it, (CharSequence)"Mining Speed", (boolean)true)) continue;
                    bl = false;
                    break block7;
                }
                bl = true;
            }
        }
        if (bl) {
            return false;
        }
        String string = (String)CollectionsKt.last((List)loreLines);
        Locale locale = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
        String string2 = string.toUpperCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toUpperCase(...)");
        String lastLine = string2;
        return StringsKt.contains$default((CharSequence)lastLine, (CharSequence)" DRILL", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)lastLine, (CharSequence)" PICKAXE", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)lastLine, (CharSequence)" GAUNTLET", (boolean)false, (int)2, null);
    }

    private final String parseMiningAbility(class_1799 stack) {
        for (class_2561 line : ItemUtilsKt.getLoreLines(stack)) {
            String string = line.getString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
            String stripped = this.stripFormatting(string);
            if (!StringsKt.contains((CharSequence)stripped, (CharSequence)"RIGHT CLICK", (boolean)true) || !StringsKt.contains$default((CharSequence)stripped, (char)':', (boolean)false, (int)2, null)) continue;
            String ability = ((Object)StringsKt.trim((CharSequence)StringsKt.replace((String)StringsKt.substringAfter$default((String)stripped, (char)':', null, (int)2, null), (String)"RIGHT CLICK", (String)"", (boolean)true))).toString();
            if (!(((CharSequence)ability).length() > 0)) continue;
            return ability;
        }
        return "";
    }

    private final int extractLoreCooldownTicks(class_1799 stack) {
        for (class_2561 line : CollectionsKt.asReversed(ItemUtilsKt.getLoreLines(stack))) {
            int seconds;
            String string = line.getString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
            String stripped = this.stripFormatting(string);
            if (!StringsKt.startsWith((String)stripped, (String)"Cooldown:", (boolean)true) || (seconds = this.parseDurationSeconds(((Object)StringsKt.trim((CharSequence)StringsKt.substringAfter$default((String)stripped, (char)':', null, (int)2, null))).toString())) <= 0) continue;
            return seconds * 20;
        }
        return 0;
    }

    private final String findAbilityWidgetLine(String ability) {
        Object v0;
        block2: {
            if (StringsKt.isBlank((CharSequence)ability)) {
                return null;
            }
            Iterable $this$firstOrNull$iv = this.readTabListLines();
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                String line = (String)element$iv;
                boolean bl = false;
                if (!(StringsKt.contains((CharSequence)line, (CharSequence)ability, (boolean)true) && StringsKt.contains$default((CharSequence)line, (char)':', (boolean)false, (int)2, null))) continue;
                v0 = element$iv;
                break block2;
            }
            v0 = null;
        }
        return v0;
    }

    private final boolean isPickaxeAbilityUseMessage(String message) {
        return StringsKt.startsWith$default((String)message, (String)"You used your ", (boolean)false, (int)2, null) && StringsKt.endsWith$default((String)message, (String)" Pickaxe Ability!", (boolean)false, (int)2, null);
    }

    private final int parseDurationSeconds(String value) {
        int n;
        String stripped = ((Object)StringsKt.trim((CharSequence)StringsKt.removeSuffix((String)((Object)StringsKt.trim((CharSequence)value)).toString(), (CharSequence)"s"))).toString();
        Integer n2 = StringsKt.toIntOrNull((String)stripped);
        if (n2 != null) {
            n = n2;
        } else {
            Integer n3 = StringsKt.toIntOrNull((String)StringsKt.substringBefore$default((String)stripped, (char)' ', null, (int)2, null));
            n = n3 != null ? n3 : 0;
        }
        return n;
    }

    private final CorpseType detectCorpseType(class_1531 stand) {
        CorpseType corpseType;
        Object v2;
        block3: {
            class_1799 class_17992 = stand.method_6118(class_1304.field_6169);
            Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getItemBySlot(...)");
            class_1799 helmet = class_17992;
            if (helmet.method_7960()) {
                return CorpseType.NONE;
            }
            String string = helmet.method_7964().getString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
            String name = this.stripFormatting(string);
            Iterable $this$firstOrNull$iv = (Iterable)CorpseType.getEntries();
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                CorpseType it = (CorpseType)((Object)element$iv);
                boolean bl = false;
                if (!(it != CorpseType.NONE && StringsKt.equals((String)it.getHelmetName(), (String)name, (boolean)true))) continue;
                v2 = element$iv;
                break block3;
            }
            v2 = null;
        }
        if ((corpseType = (CorpseType)v2) == null) {
            corpseType = CorpseType.NONE;
        }
        return corpseType;
    }

    private final boolean hasKeyForCorpse(CorpseType type) {
        class_746 class_7462 = NoFrillsMiningModule.mc.field_1724;
        if (class_7462 == null) {
            return false;
        }
        class_746 player = class_7462;
        String string = type.getKeyId();
        if (string == null) {
            return true;
        }
        String keyId = string;
        class_1661 class_16612 = player.method_31548();
        Intrinsics.checkNotNullExpressionValue((Object)class_16612, (String)"getInventory(...)");
        class_1661 inventory = class_16612;
        for (int index = 0; index < 36; ++index) {
            class_1799 class_17992 = inventory.method_5438(index);
            Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getItem(...)");
            String id = this.getSkyblockIdSafe(class_17992);
            if (!Intrinsics.areEqual((Object)id, (Object)keyId)) continue;
            return true;
        }
        return false;
    }

    private final Color corpseColor(CorpseType type) {
        Color color;
        switch (WhenMappings.$EnumSwitchMapping$0[type.ordinal()]) {
            case 1: {
                color = this.color(lapisCorpseColor.getValue());
                break;
            }
            case 2: {
                color = this.color(tungstenCorpseColor.getValue());
                break;
            }
            case 3: {
                color = this.color(umberCorpseColor.getValue());
                break;
            }
            case 4: {
                color = this.color(vanguardCorpseColor.getValue());
                break;
            }
            case 5: {
                Color color2 = Color.WHITE;
                color = color2;
                Intrinsics.checkNotNullExpressionValue((Object)color2, (String)"WHITE");
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
        return color;
    }

    private final String detectWormType(class_1297 entity) {
        String string = entity.method_5477().getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        String raw = this.stripFormatting(string);
        CharSequence charSequence = raw;
        Regex regex = new Regex("\\s+[0-9.,]+(?:/[0-9.,]+)?\\s*[\u2764\u2665]?$");
        String string2 = "";
        String name = ((Object)StringsKt.trim((CharSequence)regex.replace(charSequence, string2))).toString();
        return StringsKt.startsWith((String)name, (String)"[Lv10] Scatha ", (boolean)true) ? "scatha" : (StringsKt.startsWith((String)name, (String)"[Lv5] Worm ", (boolean)true) ? "worm" : null);
    }

    private final boolean isWithinWormAlertRadius(class_746 player, class_1297 entity) {
        class_2338 class_23382 = player.method_24515();
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"blockPosition(...)");
        class_2338 playerPos = class_23382;
        class_2338 class_23383 = entity.method_24515();
        Intrinsics.checkNotNullExpressionValue((Object)class_23383, (String)"blockPosition(...)");
        class_2338 wormPos = class_23383;
        return Math.abs(wormPos.method_10264() - playerPos.method_10264()) <= 4 && (Math.abs(wormPos.method_10263() - playerPos.method_10263()) <= 2 || Math.abs(wormPos.method_10260() - playerPos.method_10260()) <= 2);
    }

    private final class_2338 findGround(class_2338 origin, class_638 level2, int maxDepth) {
        int offset = 0;
        if (offset <= maxDepth) {
            while (true) {
                class_2338 candidate;
                Intrinsics.checkNotNullExpressionValue((Object)origin.method_10087(offset), (String)"below(...)");
                if (!level2.method_8320(candidate).method_26215()) {
                    return candidate.method_10062();
                }
                if (offset == maxDepth) break;
                ++offset;
            }
        }
        return null;
    }

    private final void sendServerMessage(String message) {
        String trimmed = ((Object)StringsKt.trim((CharSequence)message)).toString();
        class_746 class_7462 = NoFrillsMiningModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        if (((CharSequence)trimmed).length() == 0) {
            return;
        }
        if (StringsKt.startsWith$default((String)trimmed, (String)"/", (boolean)false, (int)2, null)) {
            player.field_3944.method_45730(StringsKt.removePrefix((String)trimmed, (CharSequence)"/"));
        } else {
            player.field_3944.method_45729(trimmed);
        }
    }

    private final void notifyOverlay(String title, String description, class_3414 sound, float pitch, class_746 player) {
        block0: {
            NotificationManager.INSTANCE.queue(title, description, 3000L);
            player.method_7353((class_2561)class_2561.method_43470((String)title), true);
            class_638 class_6382 = NoFrillsMiningModule.mc.field_1687;
            if (class_6382 == null) break block0;
            class_6382.method_8486(player.method_23317(), player.method_23318(), player.method_23321(), sound, class_3419.field_15248, 1.0f, pitch, false);
        }
    }

    private final Color color(int argb) {
        return new Color(argb, true);
    }

    private final Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), RangesKt.coerceIn((int)alpha, (int)0, (int)255));
    }

    private final class_238 blockBox(class_2338 pos, double inflate) {
        return new class_238((double)pos.method_10263() - inflate, (double)pos.method_10264() - inflate, (double)pos.method_10260() - inflate, (double)pos.method_10263() + 1.0 + inflate, (double)pos.method_10264() + 1.0 + inflate, (double)pos.method_10260() + 1.0 + inflate);
    }

    private final String normalize(String text) {
        String string = this.stripFormatting(text);
        Locale locale = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
        String string2 = string.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
        return string2;
    }

    private final String stripFormatting(String text) {
        String string = class_124.method_539((String)text);
        if (string == null) {
            string = tooltipStripRegex.replace((CharSequence)text, "");
        }
        return ((Object)StringsKt.trim((CharSequence)string)).toString();
    }

    private final boolean isGemstoneGlass(class_2680 state) {
        class_2248 class_22482 = state.method_26204();
        Intrinsics.checkNotNullExpressionValue((Object)class_22482, (String)"getBlock(...)");
        class_2248 block = class_22482;
        return block instanceof class_2506 || block instanceof class_2504;
    }

    private final String getSkyblockIdSafe(class_1799 $this$getSkyblockIdSafe) {
        Object object;
        Object object2 = $this$getSkyblockIdSafe;
        try {
            class_1799 $this$getSkyblockIdSafe_u24lambda_u240 = object2;
            boolean bl = false;
            object = Result.constructor-impl((Object)SkyblockItemUtilsKt.getSkyblockId($this$getSkyblockIdSafe_u24lambda_u240));
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        object2 = object;
        object = "";
        return (String)(Result.isFailure-impl((Object)object2) ? object : object2);
    }

    private static final boolean tickScathaAlerts$lambda$0(class_638 $level, UUID id) {
        boolean bl;
        block3: {
            Intrinsics.checkNotNullParameter((Object)id, (String)"id");
            Iterable iterable = $level.method_18112();
            Intrinsics.checkNotNullExpressionValue((Object)iterable, (String)"entitiesForRendering(...)");
            Iterable $this$none$iv = iterable;
            boolean $i$f$none = false;
            if ($this$none$iv instanceof Collection && ((Collection)$this$none$iv).isEmpty()) {
                bl = true;
            } else {
                for (Object element$iv : $this$none$iv) {
                    class_1297 it = (class_1297)element$iv;
                    boolean bl2 = false;
                    if (!(Intrinsics.areEqual((Object)it.method_5667(), (Object)id) && it.method_5805())) continue;
                    bl = false;
                    break block3;
                }
                bl = true;
            }
        }
        return bl;
    }

    private static final boolean tickScathaAlerts$lambda$1(Function1 $tmp0, Object p0) {
        return (Boolean)$tmp0.invoke(p0);
    }

    private static final boolean pruneEndNodes$lambda$0(long $now, class_638 $level, Map.Entry entry) {
        Intrinsics.checkNotNullParameter((Object)entry, (String)"<destruct>");
        Object k = entry.getKey();
        Intrinsics.checkNotNullExpressionValue(k, (String)"component1(...)");
        class_2338 pos = (class_2338)k;
        Object v = entry.getValue();
        Intrinsics.checkNotNullExpressionValue(v, (String)"component2(...)");
        Long lastSeen = (Long)v;
        return $now - lastSeen > 180000L || !Intrinsics.areEqual((Object)$level.method_8320(pos).method_26204(), (Object)class_2246.field_10570);
    }

    private static final boolean pruneEndNodes$lambda$1(Function1 $tmp0, Object p0) {
        return (Boolean)$tmp0.invoke(p0);
    }

    private static final CharSequence announceMineshaft$lambda$2(Map.Entry entry) {
        Intrinsics.checkNotNullParameter((Object)entry, (String)"<destruct>");
        String name = (String)entry.getKey();
        int count = ((Number)entry.getValue()).intValue();
        return count + "x " + name;
    }

    private static final boolean appendSanitizedLines$lambda$0(String it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return !StringsKt.isBlank((CharSequence)it);
    }

    public static final /* synthetic */ String access$stripFormatting(NoFrillsMiningModule $this, String text) {
        return $this.stripFormatting(text);
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        Object[] objectArray = new String[]{AREA_DWARVEN_MINES, AREA_CRYSTAL_HOLLOWS, AREA_MINESHAFT, AREA_THE_END};
        AREA_NAMES = objectArray;
        enabled = new CheckboxSetting("Enabled", "Enable the imported NoFrills mining helpers.", false);
        abilityAlert = new CheckboxSetting("Ability Alert", "Alert when your mining tool ability comes off cooldown.", true);
        abilityCooldownOverride = new SliderSetting("Ability CD Override", "Override the lore-derived cooldown in seconds. Set to 0 to use the item lore.", 0.0, 0.0, 120.0, 1.0);
        corpseHighlight = new CheckboxSetting("Corpse Highlight", "Highlight corpses in Glacite Mineshafts.", true);
        hideOpenedCorpses = new CheckboxSetting("Hide Opened Corpses", "Hide corpses after you interact with them if you had the right key.", true);
        lapisCorpseColor = new ColorSetting("Lapis Color", "Render color for lapis corpses.", -11184641);
        tungstenCorpseColor = new ColorSetting("Tungsten Color", "Render color for tungsten corpses.", -5592406);
        umberCorpseColor = new ColorSetting("Umber Color", "Render color for umber corpses.", -22016);
        vanguardCorpseColor = new ColorSetting("Vanguard Color", "Render color for vanguard corpses.", -43521);
        ghostVision = new CheckboxSetting("Ghost Vision", "Highlight ghosts in the Dwarven Mines.", true);
        ghostColor = new ColorSetting("Ghost Color", "Render color for ghost ESP.", -16725816);
        scathaMining = new CheckboxSetting("Scatha Mining", "Enable worm and scatha alerts plus cooldown tracking.", true);
        scathaSpawnAlert = new CheckboxSetting("Scatha Spawn Alert", "Alert when a worm or scatha spawns close to you.", true);
        scathaCooldownAlert = new CheckboxSetting("Scatha CD Alert", "Alert when the worm spawn cooldown ends.", true);
        endNodeHighlight = new CheckboxSetting("End Node Highlight", "Highlight end nodes when their witch particle packet appears.", true);
        endNodeColor = new ColorSetting("End Node Color", "Render color for ender nodes.", -16711936);
        templeSkip = new CheckboxSetting("Temple Skip", "Highlight the Jungle Temple pearl skip spot once the entrance guardian is found.", true);
        templeSkipColor = new ColorSetting("Temple Color", "Render color for the temple skip markers.", -8453889);
        gemstoneDesyncFix = new CheckboxSetting("Gemstone Desync Fix", "Force neighboring gemstone panes to update after a gemstone breaks.", true);
        breakResetFix = new CheckboxSetting("Break Reset Fix", "Keep the held mining stack synced so inventory updates do not reset block breaking progress.", true);
        shaftAnnounce = new CheckboxSetting("Shaft Announce", "Announce the mineshaft id and corpse list after entering a Glacite Mineshaft.", true);
        shaftMessage = new TextSetting("Shaft Message", "Message template. Supported placeholders: {id}, {corpses}.", "/pc !ptme Entered Mineshaft: {id}. Corpses: {corpses}.");
        tooltipStripRegex = new Regex("\u00a7[0-9A-FK-ORa-fk-or]");
        shaftIdRegex = new Regex("\\b[A-Z0-9]{4}_[A-Z0-9]\\b");
        openedCorpseIds = new LinkedHashSet();
        seenWormIds = new LinkedHashSet();
        endNodeHighlights = new LinkedHashMap();
        currentTool = new ToolData(null, null, 3, null);
        shaftAnnounceTicks = -1;
        objectArray = new Setting[]{enabled, abilityAlert, abilityCooldownOverride, corpseHighlight, hideOpenedCorpses, lapisCorpseColor, tungstenCorpseColor, umberCorpseColor, vanguardCorpseColor, ghostVision, ghostColor, scathaMining, scathaSpawnAlert, scathaCooldownAlert, endNodeHighlight, endNodeColor, templeSkip, templeSkipColor, gemstoneDesyncFix, breakResetFix, shaftAnnounce, shaftMessage};
        INSTANCE.addSetting((Setting<?>[])objectArray);
        EventBus.register(INSTANCE);
        objectArray = new String[]{AREA_DWARVEN_MINES, AREA_CRYSTAL_HOLLOWS, AREA_MINESHAFT, "Crimson Isle", "The Rift"};
        GEMSTONE_FIX_AREAS = SetsKt.setOf((Object[])objectArray);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0010\u000e\n\u0002\b\u000e\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u001b\b\u0002\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0007\u001a\u0004\b\b\u0010\tR\u0019\u0010\u0004\u001a\u0004\u0018\u00010\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0007\u001a\u0004\b\n\u0010\tj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000f\u00a8\u0006\u0010"}, d2={"Lorg/cobalt/internal/mining/NoFrillsMiningModule$CorpseType;", "", "", "helmetName", "keyId", "<init>", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V", "Ljava/lang/String;", "getHelmetName", "()Ljava/lang/String;", "getKeyId", "LAPIS", "TUNGSTEN", "UMBER", "VANGUARD", "NONE", "cobalt"})
    private static final class CorpseType
    extends Enum<CorpseType> {
        @NotNull
        private final String helmetName;
        @Nullable
        private final String keyId;
        public static final /* enum */ CorpseType LAPIS = new CorpseType("Lapis Armor Helmet", null);
        public static final /* enum */ CorpseType TUNGSTEN = new CorpseType("Mineral Helmet", "TUNGSTEN_KEY");
        public static final /* enum */ CorpseType UMBER = new CorpseType("Yog Helmet", "UMBER_KEY");
        public static final /* enum */ CorpseType VANGUARD = new CorpseType("Vanguard Helmet", "SKELETON_KEY");
        public static final /* enum */ CorpseType NONE = new CorpseType("", null);
        private static final /* synthetic */ CorpseType[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        private CorpseType(String helmetName, String keyId) {
            this.helmetName = helmetName;
            this.keyId = keyId;
        }

        @NotNull
        public final String getHelmetName() {
            return this.helmetName;
        }

        @Nullable
        public final String getKeyId() {
            return this.keyId;
        }

        public static CorpseType[] values() {
            return (CorpseType[])$VALUES.clone();
        }

        public static CorpseType valueOf(String value) {
            return Enum.valueOf(CorpseType.class, value);
        }

        @NotNull
        public static EnumEntries<CorpseType> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = corpseTypeArray = new CorpseType[]{CorpseType.LAPIS, CorpseType.TUNGSTEN, CorpseType.UMBER, CorpseType.VANGUARD, CorpseType.NONE};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\b\b\b\u0082\b\u0018\u00002\u00020\u0001B\u001b\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\r\u0010\t\u001a\u00020\b\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\u000eJ$\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u001b\u0010\u0012\u001a\u00020\b2\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0011\u0010\u0015\u001a\u00020\u0014H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u0011\u0010\u0017\u001a\u00020\u0004H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0017\u0010\u000eR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0018\u001a\u0004\b\u0019\u0010\fR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001a\u001a\u0004\b\u001b\u0010\u000e\u00a8\u0006\u001c"}, d2={"Lorg/cobalt/internal/mining/NoFrillsMiningModule$ToolData;", "", "Lnet/minecraft/class_1799;", "tool", "", "ability", "<init>", "(Lnet/minecraft/class_1799;Ljava/lang/String;)V", "", "isValid", "()Z", "component1", "()Lnet/minecraft/class_1799;", "component2", "()Ljava/lang/String;", "copy", "(Lnet/minecraft/class_1799;Ljava/lang/String;)Lorg/cobalt/internal/mining/NoFrillsMiningModule$ToolData;", "other", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Lnet/minecraft/class_1799;", "getTool", "Ljava/lang/String;", "getAbility", "cobalt"})
    private static final class ToolData {
        @NotNull
        private final class_1799 tool;
        @NotNull
        private final String ability;

        public ToolData(@NotNull class_1799 tool, @NotNull String ability) {
            Intrinsics.checkNotNullParameter((Object)tool, (String)"tool");
            Intrinsics.checkNotNullParameter((Object)ability, (String)"ability");
            this.tool = tool;
            this.ability = ability;
        }

        public /* synthetic */ ToolData(class_1799 class_17992, String string, int n, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n & 1) != 0) {
                class_1799 class_17993 = class_1799.field_8037;
                Intrinsics.checkNotNullExpressionValue((Object)class_17993, (String)"EMPTY");
                class_17992 = class_17993;
            }
            if ((n & 2) != 0) {
                string = "";
            }
            this(class_17992, string);
        }

        @NotNull
        public final class_1799 getTool() {
            return this.tool;
        }

        @NotNull
        public final String getAbility() {
            return this.ability;
        }

        public final boolean isValid() {
            return !this.tool.method_7960() && !StringsKt.isBlank((CharSequence)this.ability);
        }

        @NotNull
        public final class_1799 component1() {
            return this.tool;
        }

        @NotNull
        public final String component2() {
            return this.ability;
        }

        @NotNull
        public final ToolData copy(@NotNull class_1799 tool, @NotNull String ability) {
            Intrinsics.checkNotNullParameter((Object)tool, (String)"tool");
            Intrinsics.checkNotNullParameter((Object)ability, (String)"ability");
            return new ToolData(tool, ability);
        }

        public static /* synthetic */ ToolData copy$default(ToolData toolData, class_1799 class_17992, String string, int n, Object object) {
            if ((n & 1) != 0) {
                class_17992 = toolData.tool;
            }
            if ((n & 2) != 0) {
                string = toolData.ability;
            }
            return toolData.copy(class_17992, string);
        }

        @NotNull
        public String toString() {
            return "ToolData(tool=" + this.tool + ", ability=" + this.ability + ")";
        }

        public int hashCode() {
            int result = this.tool.hashCode();
            result = result * 31 + this.ability.hashCode();
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ToolData)) {
                return false;
            }
            ToolData toolData = (ToolData)other;
            if (!Intrinsics.areEqual((Object)this.tool, (Object)toolData.tool)) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.ability, (Object)toolData.ability);
        }

        public ToolData() {
            this(null, null, 3, null);
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[CorpseType.values().length];
            try {
                nArray[CorpseType.LAPIS.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[CorpseType.TUNGSTEN.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[CorpseType.UMBER.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[CorpseType.VANGUARD.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[CorpseType.NONE.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

