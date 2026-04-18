/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.math.MathKt
 *  kotlin.ranges.RangesKt
 *  kotlin.sequences.Sequence
 *  kotlin.sequences.SequencesKt
 *  kotlin.text.StringsKt
 *  net.minecraft.class_124
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1531
 *  net.minecraft.class_1542
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_1937
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_2382
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_2487
 *  net.minecraft.class_266
 *  net.minecraft.class_268
 *  net.minecraft.class_2680
 *  net.minecraft.class_269
 *  net.minecraft.class_310
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  net.minecraft.class_8646
 *  net.minecraft.class_9011
 *  net.minecraft.class_9279
 *  net.minecraft.class_9334
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.dungeons;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.text.StringsKt;
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2487;
import net.minecraft.class_266;
import net.minecraft.class_268;
import net.minecraft.class_2680;
import net.minecraft.class_269;
import net.minecraft.class_310;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_8646;
import net.minecraft.class_9011;
import net.minecraft.class_9279;
import net.minecraft.class_9334;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.ChatEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.WorldRenderContext;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.ActionSetting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.ColorSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.module.setting.impl.TextSetting;
import org.cobalt.api.util.ChatUtils;
import org.cobalt.api.util.InventoryUtils;
import org.cobalt.api.util.render.Render3D;
import org.cobalt.bridge.module.IBonzoStaffHelper;
import org.cobalt.internal.dungeons.DungeonKeyUtilsKt;
import org.cobalt.internal.helper.ClientGlowEspManager;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00ca\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0000\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b \b\u00c6\u0002\u0018\u00002\u00020\u00012\u00020\u0002B\t\b\u0002\u00a2\u0006\u0004\b\u0003\u0010\u0004J\u0017\u0010\b\u001a\u00020\u00072\u0006\u0010\u0006\u001a\u00020\u0005H\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\u0017\u0010\u000b\u001a\u00020\u00072\u0006\u0010\u0006\u001a\u00020\nH\u0007\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0017\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u0006\u001a\u00020\rH\u0007\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0019\u0010\u0012\u001a\u00020\u00072\b\u0010\u0011\u001a\u0004\u0018\u00010\u0010H\u0002\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0017\u0010\u0016\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0017\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0010H\u0002\u00a2\u0006\u0004\b\u0018\u0010\u0013J\u000f\u0010\u0019\u001a\u00020\u0007H\u0002\u00a2\u0006\u0004\b\u0019\u0010\u0004J\u000f\u0010\u001a\u001a\u00020\u0007H\u0002\u00a2\u0006\u0004\b\u001a\u0010\u0004J\u0017\u0010\u001d\u001a\u00020\u00072\u0006\u0010\u001c\u001a\u00020\u001bH\u0002\u00a2\u0006\u0004\b\u001d\u0010\u001eJ\u000f\u0010\u001f\u001a\u00020\u0007H\u0002\u00a2\u0006\u0004\b\u001f\u0010\u0004J\u000f\u0010 \u001a\u00020\u0007H\u0002\u00a2\u0006\u0004\b \u0010\u0004J\u0017\u0010#\u001a\u00020\u00072\u0006\u0010\"\u001a\u00020!H\u0002\u00a2\u0006\u0004\b#\u0010$J\u0017\u0010'\u001a\u00020\u00072\u0006\u0010&\u001a\u00020%H\u0002\u00a2\u0006\u0004\b'\u0010(J\u0017\u0010)\u001a\u00020\u00072\u0006\u0010&\u001a\u00020%H\u0002\u00a2\u0006\u0004\b)\u0010(J\u0017\u0010*\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b*\u0010\u0017J\u0017\u0010,\u001a\u00020%2\u0006\u0010+\u001a\u00020%H\u0002\u00a2\u0006\u0004\b,\u0010-J\u000f\u00101\u001a\u00020.H\u0000\u00a2\u0006\u0004\b/\u00100J\u0017\u00102\u001a\u00020!2\u0006\u0010\u0015\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b2\u00103J!\u00105\u001a\u0004\u0018\u0001042\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0015\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b5\u00106J\u001f\u00108\u001a\u00020!2\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u00107\u001a\u000204H\u0002\u00a2\u0006\u0004\b8\u00109J\u0017\u0010:\u001a\u00020.2\u0006\u0010\u0011\u001a\u00020\u0010H\u0002\u00a2\u0006\u0004\b:\u0010;J\u0017\u0010>\u001a\u00020!2\u0006\u0010=\u001a\u00020<H\u0002\u00a2\u0006\u0004\b>\u0010?J/\u0010C\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010@\u001a\u00020.2\u0006\u0010B\u001a\u00020AH\u0002\u00a2\u0006\u0004\bC\u0010DJ\u001f\u0010F\u001a\u00020\u00072\u0006\u0010E\u001a\u00020.2\u0006\u0010B\u001a\u00020AH\u0002\u00a2\u0006\u0004\bF\u0010GJ\u0017\u0010H\u001a\u00020\u00072\u0006\u0010B\u001a\u00020AH\u0002\u00a2\u0006\u0004\bH\u0010IJ\u0017\u0010J\u001a\u00020!2\u0006\u0010\u0011\u001a\u00020\u0010H\u0002\u00a2\u0006\u0004\bJ\u0010KJ\u0017\u0010L\u001a\u00020!2\u0006\u0010=\u001a\u00020<H\u0002\u00a2\u0006\u0004\bL\u0010?J\u0019\u0010N\u001a\u0004\u0018\u00010M2\u0006\u0010=\u001a\u00020<H\u0002\u00a2\u0006\u0004\bN\u0010OJ!\u0010S\u001a\u0004\u0018\u00018\u0000\"\u0004\b\u0000\u0010P2\b\u0010R\u001a\u0004\u0018\u00010QH\u0002\u00a2\u0006\u0004\bS\u0010TJ\u000f\u0010U\u001a\u00020!H\u0007\u00a2\u0006\u0004\bU\u0010VJ\u000f\u0010W\u001a\u00020!H\u0016\u00a2\u0006\u0004\bW\u0010VJ\u000f\u0010X\u001a\u00020!H\u0016\u00a2\u0006\u0004\bX\u0010VJ\u000f\u0010Y\u001a\u00020!H\u0016\u00a2\u0006\u0004\bY\u0010VJ\u001f\u0010\\\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020Z2\u0006\u0010\u0011\u001a\u00020[H\u0002\u00a2\u0006\u0004\b\\\u0010]J\u001f\u0010`\u001a\u00020!2\u0006\u0010_\u001a\u00020^2\u0006\u0010\u0011\u001a\u00020[H\u0002\u00a2\u0006\u0004\b`\u0010aR\u0014\u0010c\u001a\u00020b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bc\u0010dR\u0014\u0010f\u001a\u00020e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bf\u0010gR\u0014\u0010i\u001a\u00020h8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bi\u0010jR\u0014\u0010k\u001a\u00020h8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bk\u0010jR\u0014\u0010l\u001a\u00020e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bl\u0010gR\u0014\u0010m\u001a\u00020e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bm\u0010gR\u0014\u0010o\u001a\u00020n8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bo\u0010pR\u0014\u0010q\u001a\u00020n8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bq\u0010pR\u0014\u0010\u001f\u001a\u00020r8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001f\u0010sR\u0014\u0010t\u001a\u00020e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bt\u0010gR\u0014\u0010u\u001a\u00020h8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bu\u0010jR\u0014\u0010v\u001a\u00020h8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bv\u0010jR\u0014\u0010w\u001a\u00020e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bw\u0010gR\u0014\u0010x\u001a\u00020e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bx\u0010gR\u0014\u0010y\u001a\u00020e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\by\u0010gR\u0014\u0010z\u001a\u00020e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bz\u0010gR\u0014\u0010{\u001a\u00020e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b{\u0010gR\u0014\u0010|\u001a\u00020e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b|\u0010gR\u0014\u0010}\u001a\u00020e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b}\u0010gR\u0014\u0010~\u001a\u00020e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b~\u0010gR\u0017\u0010\u0080\u0001\u001a\u00020\u007f8\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0080\u0001\u0010\u0081\u0001R\u0016\u0010\u0082\u0001\u001a\u00020e8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0082\u0001\u0010gR\u0016\u0010\u0083\u0001\u001a\u00020e8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0083\u0001\u0010gR\u0017\u0010\u0084\u0001\u001a\u00020\u007f8\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0084\u0001\u0010\u0081\u0001R\u0017\u0010X\u001a\u00020!8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\bX\u0010\u0085\u0001R\u0017\u0010Y\u001a\u00020!8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\bY\u0010\u0085\u0001R\u0019\u0010\u0086\u0001\u001a\u00020!8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0086\u0001\u0010\u0085\u0001R\u0019\u0010\u0087\u0001\u001a\u00020!8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0087\u0001\u0010\u0085\u0001R\u0019\u0010\u0088\u0001\u001a\u00020!8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0088\u0001\u0010\u0085\u0001R\u0019\u0010\u0089\u0001\u001a\u00020.8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0089\u0001\u0010\u008a\u0001R\u0019\u0010\u008b\u0001\u001a\u00020\u001b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u008b\u0001\u0010\u008c\u0001R\u0019\u0010\u008d\u0001\u001a\u00020\u001b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u008d\u0001\u0010\u008c\u0001R\u0019\u0010\u008e\u0001\u001a\u00020.8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u008e\u0001\u0010\u008a\u0001R\u0019\u0010\u008f\u0001\u001a\u00020.8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u008f\u0001\u0010\u008a\u0001R\u0019\u0010\u0090\u0001\u001a\u00020.8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0090\u0001\u0010\u008a\u0001R\u0019\u0010\u0091\u0001\u001a\u00020\u001b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0091\u0001\u0010\u008c\u0001R\u0019\u0010\u0092\u0001\u001a\u00020!8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0092\u0001\u0010\u0085\u0001R\u0019\u0010\u0093\u0001\u001a\u00020A8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0093\u0001\u0010\u0094\u0001R\u0019\u0010\u0095\u0001\u001a\u00020.8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0095\u0001\u0010\u008a\u0001R\u0019\u0010\u0096\u0001\u001a\u00020A8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0096\u0001\u0010\u0094\u0001R\u0019\u0010\u0097\u0001\u001a\u00020!8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0097\u0001\u0010\u0085\u0001R\u0019\u0010\u0098\u0001\u001a\u00020A8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0098\u0001\u0010\u0094\u0001R\u0019\u0010\u0099\u0001\u001a\u00020A8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0099\u0001\u0010\u0094\u0001R\u0019\u0010\u009a\u0001\u001a\u00020.8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u009a\u0001\u0010\u008a\u0001R\u0019\u0010\u009b\u0001\u001a\u00020!8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u009b\u0001\u0010\u0085\u0001R\u0019\u0010\u009c\u0001\u001a\u00020A8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u009c\u0001\u0010\u0094\u0001R\u0017\u0010\u009d\u0001\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u009d\u0001\u0010\u009e\u0001\u00a8\u0006\u009f\u0001"}, d2={"Lorg/cobalt/internal/dungeons/DungeonsModule;", "Lorg/cobalt/api/module/Module;", "Lorg/cobalt/bridge/module/IBonzoStaffHelper;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "Lorg/cobalt/api/event/impl/client/ChatEvent$Receive;", "onChat", "(Lorg/cobalt/api/event/impl/client/ChatEvent$Receive;)V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "onRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "Lnet/minecraft/class_746;", "player", "tickBonzo", "(Lnet/minecraft/class_746;)V", "Lnet/minecraft/class_1937;", "level", "tickSuperboom", "(Lnet/minecraft/class_1937;)V", "startAttempt", "finishAttempt", "updateStatsText", "", "boostGain", "updateSuggestionText", "(D)V", "resetStats", "resetState", "", "restoreSlot", "resetSuperboomState", "(Z)V", "", "message", "debugSuperboom", "(Ljava/lang/String;)V", "onChatMessage", "updateBossStatus", "text", "stripFormatting", "(Ljava/lang/String;)Ljava/lang/String;", "", "witherKeyMapColor$cobalt", "()I", "witherKeyMapColor", "isInDungeon", "(Lnet/minecraft/class_1937;)Z", "Lnet/minecraft/class_2338;", "getLookingAtBlock", "(Lnet/minecraft/class_746;Lnet/minecraft/class_1937;)Lnet/minecraft/class_2338;", "pos", "isTargetBlock", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)Z", "findSuperboomInHotbar", "(Lnet/minecraft/class_746;)I", "Lnet/minecraft/class_1799;", "stack", "isSuperboomStack", "(Lnet/minecraft/class_1799;)Z", "superboomSlot", "", "nowTick", "runSuperboomSequence", "(Lnet/minecraft/class_746;Lnet/minecraft/class_1937;IJ)V", "slot", "executeSwitchToSlot", "(IJ)V", "executeRestoreSlot", "(J)V", "isHoldingBonzoStaff", "(Lnet/minecraft/class_746;)Z", "isBonzoStaffStack", "Lnet/minecraft/class_2487;", "getExtraAttributes", "(Lnet/minecraft/class_1799;)Lnet/minecraft/class_2487;", "T", "", "value", "unwrapOptional", "(Ljava/lang/Object;)Ljava/lang/Object;", "onLeftClick", "()Z", "isEnabled", "shouldPressBackward", "shouldCancelVelocity", "Lnet/minecraft/class_638;", "Lnet/minecraft/class_1657;", "syncDungeonMobEsp", "(Lnet/minecraft/class_638;Lnet/minecraft/class_1657;)V", "Lnet/minecraft/class_1309;", "entity", "shouldHighlightDungeonMob", "(Lnet/minecraft/class_1309;Lnet/minecraft/class_1657;)Z", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "bonzoEnabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "explosionDelay", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "sTapDuration", "adaptiveTiming", "experimentalMode", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "statsText", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "suggestionText", "Lorg/cobalt/api/module/setting/impl/ActionSetting;", "Lorg/cobalt/api/module/setting/impl/ActionSetting;", "autoSuperboomEnabled", "switchDelayTicks", "returnDelayTicks", "requireTargetBlock", "detectCrackedBricks", "detectSlabs", "detectStairs", "allowNormalTnt", "superboomDebug", "witherKeyEspEnabled", "witherKeyTracer", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "witherKeyColor", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "witherKeyLabel", "mobEspEnabled", "mobEspColor", "Z", "wasEnabled", "wasRightClicking", "waitingForExplosion", "ticksSinceClick", "I", "velocityBeforeStaff", "D", "maxVelocityAfterStaff", "ticksAtMaxVelocity", "successfulBoosts", "totalAttempts", "averageBoostStrength", "superboomSequenceActive", "lastSuperboomTick", "J", "originalSlot", "lastSwapTick", "recentlySwapped", "slotSwitchScheduledTick", "returnSwitchScheduledTick", "targetSlot", "inBoss", "lastBossCheckTick", "DUNGEON_MOB_ESP_SCOPE", "Ljava/lang/String;", "cobalt"})
public final class DungeonsModule
extends Module
implements IBonzoStaffHelper {
    @NotNull
    public static final DungeonsModule INSTANCE = new DungeonsModule();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final CheckboxSetting bonzoEnabled;
    @NotNull
    private static final SliderSetting explosionDelay;
    @NotNull
    private static final SliderSetting sTapDuration;
    @NotNull
    private static final CheckboxSetting adaptiveTiming;
    @NotNull
    private static final CheckboxSetting experimentalMode;
    @NotNull
    private static final TextSetting statsText;
    @NotNull
    private static final TextSetting suggestionText;
    @NotNull
    private static final ActionSetting resetStats;
    @NotNull
    private static final CheckboxSetting autoSuperboomEnabled;
    @NotNull
    private static final SliderSetting switchDelayTicks;
    @NotNull
    private static final SliderSetting returnDelayTicks;
    @NotNull
    private static final CheckboxSetting requireTargetBlock;
    @NotNull
    private static final CheckboxSetting detectCrackedBricks;
    @NotNull
    private static final CheckboxSetting detectSlabs;
    @NotNull
    private static final CheckboxSetting detectStairs;
    @NotNull
    private static final CheckboxSetting allowNormalTnt;
    @NotNull
    private static final CheckboxSetting superboomDebug;
    @NotNull
    private static final CheckboxSetting witherKeyEspEnabled;
    @NotNull
    private static final CheckboxSetting witherKeyTracer;
    @NotNull
    private static final ColorSetting witherKeyColor;
    @NotNull
    private static final CheckboxSetting witherKeyLabel;
    @NotNull
    private static final CheckboxSetting mobEspEnabled;
    @NotNull
    private static final ColorSetting mobEspColor;
    private static volatile boolean shouldPressBackward;
    private static volatile boolean shouldCancelVelocity;
    private static boolean wasEnabled;
    private static boolean wasRightClicking;
    private static boolean waitingForExplosion;
    private static int ticksSinceClick;
    private static double velocityBeforeStaff;
    private static double maxVelocityAfterStaff;
    private static int ticksAtMaxVelocity;
    private static int successfulBoosts;
    private static int totalAttempts;
    private static double averageBoostStrength;
    private static boolean superboomSequenceActive;
    private static long lastSuperboomTick;
    private static int originalSlot;
    private static long lastSwapTick;
    private static boolean recentlySwapped;
    private static long slotSwitchScheduledTick;
    private static long returnSwitchScheduledTick;
    private static int targetSlot;
    private static boolean inBoss;
    private static long lastBossCheckTick;
    @NotNull
    private static final String DUNGEON_MOB_ESP_SCOPE = "dungeon_mob_esp";

    private DungeonsModule() {
        super("Dungeons");
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_746 player = DungeonsModule.mc.field_1724;
        class_638 level2 = DungeonsModule.mc.field_1687;
        this.tickBonzo(player);
        if (level2 == null || player == null) {
            ClientGlowEspManager.clear$default(ClientGlowEspManager.INSTANCE, DUNGEON_MOB_ESP_SCOPE, null, 2, null);
            this.resetSuperboomState(true);
            return;
        }
        this.tickSuperboom((class_1937)level2);
        if (((Boolean)mobEspEnabled.getValue()).booleanValue() && this.isInDungeon((class_1937)level2)) {
            this.syncDungeonMobEsp(level2, (class_1657)player);
        } else {
            ClientGlowEspManager.INSTANCE.clear(DUNGEON_MOB_ESP_SCOPE, level2);
        }
    }

    @SubscribeEvent
    public final void onChat(@NotNull ChatEvent.Receive event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        String string = event.getMessage();
        if (string == null) {
            return;
        }
        String message = string;
        this.onChatMessage(message);
    }

    @SubscribeEvent
    public final void onRender(@NotNull WorldRenderEvent.Last event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_638 class_6382 = DungeonsModule.mc.field_1687;
        if (class_6382 == null) {
            return;
        }
        class_638 level2 = class_6382;
        class_746 class_7462 = DungeonsModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        if (!((Boolean)witherKeyEspEnabled.getValue()).booleanValue() || !this.isInDungeon((class_1937)level2)) {
            return;
        }
        Color stroke = new Color(witherKeyColor.getValue(), true);
        Color outerStroke = stroke.brighter();
        Color outerFill = new Color(stroke.getRed(), stroke.getGreen(), stroke.getBlue(), 72);
        Color innerFill = new Color(stroke.getRed(), stroke.getGreen(), stroke.getBlue(), 118);
        for (Object t : level2.method_18112()) {
            class_243 labelPos;
            class_238 box;
            class_1542 itemEntity;
            Intrinsics.checkNotNullExpressionValue(t, (String)"next(...)");
            class_1297 entity = (class_1297)t;
            class_1542 class_15422 = entity instanceof class_1542 ? (class_1542)entity : null;
            if (class_15422 == null || !(itemEntity = class_15422).method_5805()) continue;
            class_1799 class_17992 = itemEntity.method_6983();
            Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getItem(...)");
            if (!DungeonKeyUtilsKt.isWitherKeyItem(class_17992)) continue;
            Intrinsics.checkNotNullExpressionValue((Object)itemEntity.method_5829().method_1009(0.28, 0.18, 0.28), (String)"inflate(...)");
            WorldRenderContext worldRenderContext = event.getContext();
            class_238 class_2383 = box.method_1009(0.12, 0.08, 0.12);
            Intrinsics.checkNotNullExpressionValue((Object)class_2383, (String)"inflate(...)");
            Intrinsics.checkNotNull((Object)outerStroke);
            Render3D.drawStyledBox(worldRenderContext, class_2383, outerStroke, outerFill, true, 4.2f);
            Render3D.drawStyledBox(event.getContext(), box, stroke, innerFill, true, 2.2f);
            if (((Boolean)witherKeyTracer.getValue()).booleanValue()) {
                WorldRenderContext worldRenderContext2 = event.getContext();
                class_243 class_2432 = player.method_5836(1.0f);
                Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"getEyePosition(...)");
                class_243 class_2433 = itemEntity.method_73189().method_1031(0.0, 0.12, 0.0);
                Intrinsics.checkNotNullExpressionValue((Object)class_2433, (String)"add(...)");
                Render3D.drawLine(worldRenderContext2, class_2432, class_2433, stroke, true, 1.8f);
            }
            if (!((Boolean)witherKeyLabel.getValue()).booleanValue()) continue;
            Intrinsics.checkNotNullExpressionValue((Object)class_243.method_24953((class_2382)((class_2382)itemEntity.method_24515())).method_1031(0.0, 1.5, 0.0), (String)"add(...)");
            Render3D.drawWorldLabel(event.getContext(), labelPos, "Wither Key", stroke);
        }
    }

    private final void tickBonzo(class_746 player) {
        boolean enabledNow = (Boolean)bonzoEnabled.getValue();
        if (!enabledNow) {
            if (wasEnabled) {
                this.resetState();
            }
            wasEnabled = false;
            return;
        }
        if (!wasEnabled) {
            this.resetState();
        }
        wasEnabled = true;
        if (player == null) {
            return;
        }
        boolean isRightClicking = DungeonsModule.mc.field_1690.field_1904.method_1434();
        boolean holdingStaff = this.isHoldingBonzoStaff(player);
        if (isRightClicking && !wasRightClicking && holdingStaff) {
            this.startAttempt(player);
        }
        wasRightClicking = isRightClicking;
        if (!waitingForExplosion) {
            shouldPressBackward = false;
            shouldCancelVelocity = false;
            return;
        }
        int n = ticksSinceClick;
        ticksSinceClick = n + 1;
        double currentVelocity = player.method_18798().method_37267();
        if (currentVelocity > maxVelocityAfterStaff) {
            maxVelocityAfterStaff = currentVelocity;
            ticksAtMaxVelocity = ticksSinceClick;
        }
        int delayTicks = RangesKt.coerceIn((int)MathKt.roundToInt((double)((Number)explosionDelay.getValue()).doubleValue()), (int)1, (int)20);
        int sTapTicks = RangesKt.coerceIn((int)MathKt.roundToInt((double)((Number)sTapDuration.getValue()).doubleValue()), (int)1, (int)10);
        if (((Boolean)experimentalMode.getValue()).booleanValue() && ticksSinceClick <= delayTicks) {
            shouldCancelVelocity = true;
            shouldPressBackward = false;
        } else {
            shouldCancelVelocity = false;
            int startSTapAt = RangesKt.coerceAtLeast((int)(delayTicks - sTapTicks), (int)1);
            boolean bl = shouldPressBackward = ticksSinceClick >= startSTapAt && ticksSinceClick < delayTicks;
        }
        if (ticksSinceClick >= delayTicks + 5) {
            this.finishAttempt();
        }
    }

    private final void tickSuperboom(class_1937 level2) {
        if (!((Boolean)autoSuperboomEnabled.getValue()).booleanValue()) {
            this.resetSuperboomState(superboomSequenceActive);
            return;
        }
        long nowTick = level2.method_75260();
        if (lastSwapTick < nowTick) {
            recentlySwapped = false;
        }
        if (nowTick - lastBossCheckTick >= 20L) {
            this.updateBossStatus(level2);
            lastBossCheckTick = nowTick;
        }
        if (slotSwitchScheduledTick >= 0L && nowTick >= slotSwitchScheduledTick) {
            this.executeSwitchToSlot(targetSlot, nowTick);
            slotSwitchScheduledTick = -1L;
        }
        if (returnSwitchScheduledTick >= 0L && nowTick >= returnSwitchScheduledTick) {
            this.executeRestoreSlot(nowTick);
            returnSwitchScheduledTick = -1L;
        }
    }

    private final void startAttempt(class_746 player) {
        waitingForExplosion = true;
        ticksSinceClick = 0;
        shouldPressBackward = false;
        shouldCancelVelocity = false;
        int n = totalAttempts;
        totalAttempts = n + 1;
        maxVelocityAfterStaff = velocityBeforeStaff = player.method_18798().method_37267();
        ticksAtMaxVelocity = 0;
    }

    private final void finishAttempt() {
        boolean wasSuccessful;
        waitingForExplosion = false;
        shouldPressBackward = false;
        shouldCancelVelocity = false;
        double boostGain = maxVelocityAfterStaff - velocityBeforeStaff;
        boolean bl = wasSuccessful = boostGain > 0.5;
        if (wasSuccessful) {
            int n = successfulBoosts;
            successfulBoosts = n + 1;
            averageBoostStrength = (averageBoostStrength * (double)(successfulBoosts - 1) + boostGain) / (double)successfulBoosts;
        }
        this.updateStatsText();
        this.updateSuggestionText(boostGain);
    }

    private final void updateStatsText() {
        if (totalAttempts <= 0) {
            statsText.setValue("No data");
            return;
        }
        double rate = totalAttempts > 0 ? (double)successfulBoosts * 100.0 / (double)totalAttempts : 0.0;
        Locale locale = Locale.US;
        String string = "%d/%d (%.1f%%) - Avg: %.2f";
        Object[] objectArray = new Object[]{successfulBoosts, totalAttempts, rate, averageBoostStrength};
        String string2 = String.format(locale, string, Arrays.copyOf(objectArray, objectArray.length));
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"format(...)");
        statsText.setValue(string2);
    }

    private final void updateSuggestionText(double boostGain) {
        if (!((Boolean)adaptiveTiming.getValue()).booleanValue() || totalAttempts < 3) {
            suggestionText.setValue("");
            return;
        }
        int delayTicks = RangesKt.coerceIn((int)MathKt.roundToInt((double)((Number)explosionDelay.getValue()).doubleValue()), (int)1, (int)20);
        suggestionText.setValue(ticksAtMaxVelocity < delayTicks - 1 ? "Explosion near tick " + ticksAtMaxVelocity + ". Try delay " + (ticksAtMaxVelocity + 1) + "." : (boostGain < 0.3 ? "Weak boost. Try increasing S-tap duration." : "Settings look good."));
    }

    private final void resetStats() {
        successfulBoosts = 0;
        totalAttempts = 0;
        averageBoostStrength = 0.0;
        statsText.setValue("No data");
        suggestionText.setValue("");
    }

    private final void resetState() {
        shouldPressBackward = false;
        shouldCancelVelocity = false;
        wasRightClicking = false;
        waitingForExplosion = false;
        ticksSinceClick = 0;
    }

    private final void resetSuperboomState(boolean restoreSlot) {
        if (restoreSlot) {
            int n = originalSlot;
            boolean bl = 0 <= n ? n < 9 : false;
            if (bl) {
                InventoryUtils.holdHotbarSlot(originalSlot);
            }
        }
        superboomSequenceActive = false;
        originalSlot = -1;
        lastSwapTick = -1L;
        recentlySwapped = false;
        slotSwitchScheduledTick = -1L;
        returnSwitchScheduledTick = -1L;
        targetSlot = -1;
        inBoss = false;
        lastBossCheckTick = 0L;
        lastSuperboomTick = 0L;
    }

    private final void debugSuperboom(String message) {
        if (!((Boolean)superboomDebug.getValue()).booleanValue()) {
            return;
        }
        ChatUtils.sendDebug(message);
    }

    private final void onChatMessage(String message) {
        if (!((Boolean)autoSuperboomEnabled.getValue()).booleanValue()) {
            return;
        }
        if (StringsKt.contains$default((CharSequence)message, (CharSequence)"[BOSS]", (boolean)false, (int)2, null) && (StringsKt.contains$default((CharSequence)message, (CharSequence)"Maxor", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)message, (CharSequence)"Storm", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)message, (CharSequence)"Goldor", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)message, (CharSequence)"Necron", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)message, (CharSequence)"Wither King", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)message, (CharSequence)"Sadan", (boolean)false, (int)2, null))) {
            inBoss = true;
            this.debugSuperboom("Boss detected via chat - disabled");
            return;
        }
        if (StringsKt.contains$default((CharSequence)message, (CharSequence)"The Core entrance is opening!", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)message, (CharSequence)"PUZZLE FAIL!", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)message, (CharSequence)"PUZZLE COMPLETE!", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)message, (CharSequence)"defeated", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)message, (CharSequence)"EXTRA STATS", (boolean)false, (int)2, null)) {
            inBoss = false;
            this.debugSuperboom("Boss ended via chat - enabled");
        }
    }

    private final void updateBossStatus(class_1937 level2) {
        class_269 class_2692 = level2.method_8428();
        if (class_2692 == null) {
            DungeonsModule $this$updateBossStatus_u24lambda_u240 = this;
            boolean bl = false;
            inBoss = false;
            return;
        }
        class_269 scoreboard = class_2692;
        class_266 class_2662 = scoreboard.method_1189(class_8646.field_45157);
        if (class_2662 == null) {
            DungeonsModule $this$updateBossStatus_u24lambda_u241 = this;
            boolean bl = false;
            inBoss = false;
            return;
        }
        class_266 objective = class_2662;
        boolean detected = false;
        try {
            Collection collection = scoreboard.method_1184(objective);
            Intrinsics.checkNotNullExpressionValue((Object)collection, (String)"listPlayerScores(...)");
            Collection scores = collection;
            for (Object e : scores) {
                String stripped;
                String ownerName;
                Intrinsics.checkNotNullExpressionValue(e, (String)"next(...)");
                class_9011 score = (class_9011)e;
                Intrinsics.checkNotNullExpressionValue((Object)score.comp_2127(), (String)"owner(...)");
                class_268 team = scoreboard.method_1164(ownerName);
                String lineText = team != null ? team.method_1144().getString() + ownerName + team.method_1136().getString() : ownerName;
                String string = this.stripFormatting(lineText);
                Locale locale = Locale.ROOT;
                Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
                Intrinsics.checkNotNullExpressionValue((Object)string.toLowerCase(locale), (String)"toLowerCase(...)");
                if (!StringsKt.contains$default((CharSequence)stripped, (CharSequence)"sadan", (boolean)false, (int)2, null) && !StringsKt.contains$default((CharSequence)stripped, (CharSequence)"maxor", (boolean)false, (int)2, null) && !StringsKt.contains$default((CharSequence)stripped, (CharSequence)"storm", (boolean)false, (int)2, null) && !StringsKt.contains$default((CharSequence)stripped, (CharSequence)"goldor", (boolean)false, (int)2, null) && !StringsKt.contains$default((CharSequence)stripped, (CharSequence)"necron", (boolean)false, (int)2, null) && !StringsKt.contains$default((CharSequence)stripped, (CharSequence)"wither king", (boolean)false, (int)2, null) && !StringsKt.contains$default((CharSequence)stripped, (CharSequence)"the watcher", (boolean)false, (int)2, null) && (!StringsKt.contains$default((CharSequence)stripped, (CharSequence)"boss", (boolean)false, (int)2, null) || !StringsKt.contains$default((CharSequence)stripped, (CharSequence)"room", (boolean)false, (int)2, null) && !StringsKt.contains$default((CharSequence)stripped, (CharSequence)"fight", (boolean)false, (int)2, null))) continue;
                detected = true;
                break;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (detected != inBoss) {
            inBoss = detected;
            this.debugSuperboom(inBoss ? "Boss detected via scoreboard - disabled" : "Left boss - enabled");
        }
    }

    private final String stripFormatting(String text) {
        String string = class_124.method_539((String)text);
        if (string == null) {
            string = text;
        }
        return string;
    }

    public final int witherKeyMapColor$cobalt() {
        return witherKeyColor.getValue();
    }

    private final boolean isInDungeon(class_1937 level2) {
        String fullText;
        String score;
        class_269 class_2692 = level2.method_8428();
        if (class_2692 == null) {
            return false;
        }
        class_269 scoreboard = class_2692;
        class_266 class_2662 = scoreboard.method_1189(class_8646.field_45157);
        if (class_2662 == null) {
            return false;
        }
        class_266 objective = class_2662;
        StringBuilder allText = new StringBuilder();
        String string = objective.method_1114().getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        String display = string;
        if (((CharSequence)display).length() > 0) {
            allText.append(display).append(" ");
        }
        try {
            Collection collection = scoreboard.method_1184(objective);
            Intrinsics.checkNotNullExpressionValue((Object)collection, (String)"listPlayerScores(...)");
            Collection scores = collection;
            for (Object e : scores) {
                String ownerName;
                Intrinsics.checkNotNullExpressionValue(e, (String)"next(...)");
                score = (class_9011)e;
                Intrinsics.checkNotNullExpressionValue((Object)score.comp_2127(), (String)"owner(...)");
                class_268 team = scoreboard.method_1164(ownerName);
                String lineText = team != null ? team.method_1144().getString() + ownerName + team.method_1136().getString() : ownerName;
                allText.append(lineText).append(" ");
            }
        }
        catch (Exception scores) {
            // empty catch block
        }
        String string2 = allText.toString();
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toString(...)");
        score = fullText = this.stripFormatting(string2);
        Locale locale = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
        String string3 = score.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
        String lower = string3;
        if (StringsKt.contains$default((CharSequence)lower, (CharSequence)"hub", (boolean)false, (int)2, null)) {
            return false;
        }
        if (StringsKt.contains$default((CharSequence)lower, (CharSequence)"catacombs", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)lower, (CharSequence)"the catacombs", (boolean)false, (int)2, null)) {
            return true;
        }
        if (StringsKt.contains$default((CharSequence)lower, (CharSequence)"(e)", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)lower, (CharSequence)"entrance", (boolean)false, (int)2, null)) {
            return true;
        }
        for (int i = 1; i < 8; ++i) {
            if (StringsKt.contains$default((CharSequence)lower, (CharSequence)("(f" + i + ")"), (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)lower, (CharSequence)("floor " + i), (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)lower, (CharSequence)("f" + i), (boolean)false, (int)2, null)) {
                return true;
            }
            if (!StringsKt.contains$default((CharSequence)lower, (CharSequence)("(m" + i + ")"), (boolean)false, (int)2, null) && !StringsKt.contains$default((CharSequence)lower, (CharSequence)("master " + i), (boolean)false, (int)2, null) && !StringsKt.contains$default((CharSequence)lower, (CharSequence)("m" + i), (boolean)false, (int)2, null)) continue;
            return true;
        }
        return false;
    }

    private final class_2338 getLookingAtBlock(class_746 player, class_1937 level2) {
        double range = 5.0;
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

    private final boolean isTargetBlock(class_1937 level2, class_2338 pos) {
        class_2680 class_26802 = level2.method_8320(pos);
        Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
        class_2680 state = class_26802;
        class_2248 class_22482 = state.method_26204();
        Intrinsics.checkNotNullExpressionValue((Object)class_22482, (String)"getBlock(...)");
        class_2248 block = class_22482;
        if (Intrinsics.areEqual((Object)block, (Object)class_2246.field_10416) && ((Boolean)detectCrackedBricks.getValue()).booleanValue()) {
            return true;
        }
        if (Intrinsics.areEqual((Object)block, (Object)class_2246.field_10136) && ((Boolean)detectSlabs.getValue()).booleanValue()) {
            return true;
        }
        return Intrinsics.areEqual((Object)block, (Object)class_2246.field_10392) && (Boolean)detectStairs.getValue() != false;
    }

    private final int findSuperboomInHotbar(class_746 player) {
        class_1661 class_16612 = player.method_31548();
        Intrinsics.checkNotNullExpressionValue((Object)class_16612, (String)"getInventory(...)");
        class_1661 inventory = class_16612;
        for (int slot = 0; slot < 9; ++slot) {
            class_1799 stack;
            Intrinsics.checkNotNullExpressionValue((Object)inventory.method_5438(slot), (String)"getItem(...)");
            if (!this.isSuperboomStack(stack)) continue;
            return slot;
        }
        return -1;
    }

    private final boolean isSuperboomStack(class_1799 stack) {
        boolean bl;
        if (stack.method_7960()) {
            return false;
        }
        try {
            String string = stack.method_7909().toString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
            String string2 = string;
            Locale locale = Locale.ROOT;
            Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
            String string3 = string2.toLowerCase(locale);
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
            String itemName = string3;
            if (!StringsKt.contains$default((CharSequence)itemName, (CharSequence)"tnt", (boolean)false, (int)2, null)) {
                return false;
            }
            class_2487 attributes = this.getExtraAttributes(stack);
            if (attributes != null && attributes.method_10545("id")) {
                String id;
                String string4 = (String)this.unwrapOptional(attributes.method_10558("id"));
                if (string4 == null) {
                    string4 = "";
                }
                if (Intrinsics.areEqual((Object)(id = string4), (Object)"SUPERBOOM_TNT") || Intrinsics.areEqual((Object)id, (Object)"INFINITYBOOM_TNT")) {
                    return true;
                }
                if (((Boolean)allowNormalTnt.getValue()).booleanValue() && Intrinsics.areEqual((Object)id, (Object)"TNT")) {
                    return true;
                }
            }
            String string5 = stack.method_7964().getString();
            Intrinsics.checkNotNullExpressionValue((Object)string5, (String)"getString(...)");
            String string6 = string5;
            Locale locale2 = Locale.ROOT;
            Intrinsics.checkNotNullExpressionValue((Object)locale2, (String)"ROOT");
            String string7 = string6.toLowerCase(locale2);
            Intrinsics.checkNotNullExpressionValue((Object)string7, (String)"toLowerCase(...)");
            String displayName = string7;
            if (StringsKt.contains$default((CharSequence)displayName, (CharSequence)"superboom", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)displayName, (CharSequence)"infinityboom", (boolean)false, (int)2, null)) {
                return true;
            }
            if (((Boolean)allowNormalTnt.getValue()).booleanValue() && StringsKt.contains$default((CharSequence)displayName, (CharSequence)"tnt", (boolean)false, (int)2, null)) {
                return true;
            }
            bl = false;
        }
        catch (Exception exception) {
            bl = false;
        }
        return bl;
    }

    private final void runSuperboomSequence(class_746 player, class_1937 level2, int superboomSlot, long nowTick) {
        if (superboomSequenceActive) {
            return;
        }
        superboomSequenceActive = true;
        originalSlot = player.method_31548().method_67532();
        if (originalSlot == superboomSlot) {
            this.debugSuperboom("Already holding TNT - no swap needed.");
            superboomSequenceActive = false;
            return;
        }
        targetSlot = superboomSlot;
        int jitter = level2.field_9229.method_43048(2);
        slotSwitchScheduledTick = nowTick + 1L;
        int switchDelay = RangesKt.coerceIn((int)MathKt.roundToInt((double)((Number)switchDelayTicks.getValue()).doubleValue()), (int)3, (int)15);
        int returnDelay = RangesKt.coerceIn((int)MathKt.roundToInt((double)((Number)returnDelayTicks.getValue()).doubleValue()), (int)5, (int)25);
        returnSwitchScheduledTick = nowTick + 1L + (long)switchDelay + (long)returnDelay + (long)jitter;
    }

    private final void executeSwitchToSlot(int slot, long nowTick) {
        if (!(0 <= slot ? slot < 9 : false)) {
            return;
        }
        if (recentlySwapped) {
            this.debugSuperboom("Swap blocked: recentlySwapped");
            return;
        }
        InventoryUtils.holdHotbarSlot(slot);
        recentlySwapped = true;
        lastSwapTick = nowTick;
    }

    private final void executeRestoreSlot(long nowTick) {
        int n = originalSlot;
        if (!(0 <= n ? n < 9 : false)) {
            superboomSequenceActive = false;
            return;
        }
        if (recentlySwapped) {
            returnSwitchScheduledTick = nowTick + 1L;
            return;
        }
        InventoryUtils.holdHotbarSlot(originalSlot);
        recentlySwapped = true;
        lastSwapTick = nowTick;
        originalSlot = -1;
        superboomSequenceActive = false;
    }

    private final boolean isHoldingBonzoStaff(class_746 player) {
        class_1799 class_17992 = player.method_6047();
        Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getMainHandItem(...)");
        class_1799 mainHand = class_17992;
        class_1799 class_17993 = player.method_6079();
        Intrinsics.checkNotNullExpressionValue((Object)class_17993, (String)"getOffhandItem(...)");
        class_1799 offHand = class_17993;
        return this.isBonzoStaffStack(mainHand) || this.isBonzoStaffStack(offHand);
    }

    private final boolean isBonzoStaffStack(class_1799 stack) {
        boolean bl;
        if (stack.method_7960()) {
            return false;
        }
        try {
            String string = stack.method_7909().toString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
            String string2 = string;
            Locale locale = Locale.ROOT;
            Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
            String string3 = string2.toLowerCase(locale);
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
            String itemName = string3;
            if (!StringsKt.contains$default((CharSequence)itemName, (CharSequence)"blaze_rod", (boolean)false, (int)2, null)) {
                return false;
            }
            class_2487 attributes = this.getExtraAttributes(stack);
            if (attributes != null && attributes.method_10545("id")) {
                String id;
                String string4 = (String)this.unwrapOptional(attributes.method_10558("id"));
                if (string4 == null) {
                    string4 = "";
                }
                if (Intrinsics.areEqual((Object)(id = string4), (Object)"BONZO_STAFF")) {
                    return true;
                }
            }
            String string5 = stack.method_7964().getString();
            Intrinsics.checkNotNullExpressionValue((Object)string5, (String)"getString(...)");
            String string6 = string5;
            Locale locale2 = Locale.ROOT;
            Intrinsics.checkNotNullExpressionValue((Object)locale2, (String)"ROOT");
            String string7 = string6.toLowerCase(locale2);
            Intrinsics.checkNotNullExpressionValue((Object)string7, (String)"toLowerCase(...)");
            String displayName = string7;
            if (StringsKt.contains$default((CharSequence)displayName, (CharSequence)"bonzo", (boolean)false, (int)2, null) && StringsKt.contains$default((CharSequence)displayName, (CharSequence)"staff", (boolean)false, (int)2, null)) {
                return true;
            }
            bl = true;
        }
        catch (Exception exception) {
            bl = false;
        }
        return bl;
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

    @JvmStatic
    public static final boolean onLeftClick() {
        if (!((Boolean)autoSuperboomEnabled.getValue()).booleanValue()) {
            return false;
        }
        class_746 class_7462 = DungeonsModule.mc.field_1724;
        if (class_7462 == null) {
            return false;
        }
        class_746 player = class_7462;
        class_638 class_6382 = DungeonsModule.mc.field_1687;
        if (class_6382 == null) {
            return false;
        }
        class_638 level2 = class_6382;
        if (DungeonsModule.mc.field_1755 != null) {
            return false;
        }
        class_1799 class_17992 = player.method_6047();
        Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getMainHandItem(...)");
        class_1799 heldItem = class_17992;
        if (!heldItem.method_7960()) {
            String string = heldItem.method_7964().getString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
            if (StringsKt.contains$default((CharSequence)string, (CharSequence)"Dungeonbreaker", (boolean)false, (int)2, null)) {
                INSTANCE.debugSuperboom("Skipping - holding Dungeonbreaker");
                return false;
            }
        }
        if (!INSTANCE.isInDungeon((class_1937)level2)) {
            INSTANCE.debugSuperboom("Not in dungeon");
            return false;
        }
        if (inBoss) {
            INSTANCE.debugSuperboom("Disabled in boss");
            return false;
        }
        if (superboomSequenceActive) {
            INSTANCE.debugSuperboom("Sequence already active");
            return false;
        }
        long nowTick = level2.method_75260();
        if (nowTick - lastSuperboomTick < 10L) {
            INSTANCE.debugSuperboom("Cooldown active");
            return false;
        }
        class_2338 class_23382 = INSTANCE.getLookingAtBlock(player, (class_1937)level2);
        if (class_23382 == null) {
            DungeonsModule $this$onLeftClick_u24lambda_u240 = INSTANCE;
            boolean bl = false;
            $this$onLeftClick_u24lambda_u240.debugSuperboom("No block targeted");
            return false;
        }
        class_2338 targetBlock = class_23382;
        if (((Boolean)requireTargetBlock.getValue()).booleanValue() && !INSTANCE.isTargetBlock((class_1937)level2, targetBlock)) {
            INSTANCE.debugSuperboom("Not a target block");
            return false;
        }
        int superboomSlot = INSTANCE.findSuperboomInHotbar(player);
        if (superboomSlot == -1) {
            INSTANCE.debugSuperboom("No TNT found in hotbar");
            return false;
        }
        lastSuperboomTick = nowTick;
        INSTANCE.runSuperboomSequence(player, (class_1937)level2, superboomSlot, nowTick);
        return true;
    }

    @Override
    public boolean isEnabled() {
        return (Boolean)bonzoEnabled.getValue();
    }

    @Override
    public boolean shouldPressBackward() {
        return (Boolean)bonzoEnabled.getValue() != false && shouldPressBackward;
    }

    @Override
    public boolean shouldCancelVelocity() {
        return (Boolean)bonzoEnabled.getValue() != false && shouldCancelVelocity;
    }

    private final void syncDungeonMobEsp(class_638 level2, class_1657 player) {
        Iterable iterable = level2.method_18112();
        Intrinsics.checkNotNullExpressionValue((Object)iterable, (String)"entitiesForRendering(...)");
        List targets = SequencesKt.toList((Sequence)SequencesKt.map((Sequence)SequencesKt.filter((Sequence)SequencesKt.mapNotNull((Sequence)CollectionsKt.asSequence((Iterable)iterable), DungeonsModule::syncDungeonMobEsp$lambda$0), arg_0 -> DungeonsModule.syncDungeonMobEsp$lambda$1(player, arg_0)), DungeonsModule::syncDungeonMobEsp$lambda$2));
        ClientGlowEspManager.INSTANCE.sync(DUNGEON_MOB_ESP_SCOPE, level2, targets);
    }

    private final boolean shouldHighlightDungeonMob(class_1309 entity, class_1657 player) {
        if (!entity.method_5805() || entity.method_6032() <= 0.0f) {
            return false;
        }
        return entity != player && !(entity instanceof class_1531) && !(entity instanceof class_1657);
    }

    private static final Unit resetStats$lambda$0() {
        INSTANCE.resetStats();
        return Unit.INSTANCE;
    }

    private static final class_1309 syncDungeonMobEsp$lambda$0(class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it instanceof class_1309 ? (class_1309)it : null;
    }

    private static final boolean syncDungeonMobEsp$lambda$1(class_1657 $player, class_1309 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return INSTANCE.shouldHighlightDungeonMob(it, $player);
    }

    private static final ClientGlowEspManager.GlowTarget syncDungeonMobEsp$lambda$2(class_1309 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return new ClientGlowEspManager.GlowTarget(it, mobEspColor.getValue(), 0, 4, null);
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        bonzoEnabled = new CheckboxSetting("Bonzo Helper", "S-tap before Bonzo Staff explosion to catch boost.", false);
        explosionDelay = new SliderSetting("Explosion Delay", "Ticks until explosion (adjust for ping).", 8.0, 1.0, 20.0, 0.0, 32, null);
        sTapDuration = new SliderSetting("S-Tap Duration", "How long to press S before explosion.", 4.0, 1.0, 10.0, 0.0, 32, null);
        adaptiveTiming = new CheckboxSetting("Adaptive Timing", "Auto-learn optimal timing from attempts.", true);
        experimentalMode = new CheckboxSetting("Experimental Mode", "Cancel velocity until explosion (requires mana).", false);
        statsText = new TextSetting("Bonzo Stats", "Success rate and average boost.", "No data");
        suggestionText = new TextSetting("Bonzo Suggestion", "Timing feedback after attempts.", "");
        resetStats = new ActionSetting("Reset Bonzo Stats", "Reset Bonzo Staff helper stats.", "Reset", null, DungeonsModule::resetStats$lambda$0, 8, null);
        autoSuperboomEnabled = new CheckboxSetting("Auto Superboom", "Auto-swap to Superboom TNT in Dungeons only.", false);
        switchDelayTicks = new SliderSetting("Superboom Switch Delay", "Ticks to wait after switching slot (3-15).", 4.0, 3.0, 15.0, 0.0, 32, null);
        returnDelayTicks = new SliderSetting("Superboom Return Delay", "Ticks before switching back (5-25).", 8.0, 5.0, 25.0, 0.0, 32, null);
        requireTargetBlock = new CheckboxSetting("Superboom Target Blocks", "Only trigger on dungeon blocks.", true);
        detectCrackedBricks = new CheckboxSetting("Cracked Bricks", "Detect cracked stone bricks (weak walls).", true);
        detectSlabs = new CheckboxSetting("Smooth Stone Slabs", "Detect smooth stone slabs (crypt lids).", true);
        detectStairs = new CheckboxSetting("Stone Brick Stairs", "Detect stone brick stairs (crypts).", true);
        allowNormalTnt = new CheckboxSetting("Allow Normal TNT", "Allow normal TNT (testing).", true);
        superboomDebug = new CheckboxSetting("Superboom Debug", "Show debug messages in chat.", false);
        witherKeyEspEnabled = new CheckboxSetting("Wither Key ESP", "Highlight dropped Wither Keys in dungeons.", true);
        witherKeyTracer = new CheckboxSetting("Wither Key Tracer", "Draw a tracer to dropped Wither Keys.", true);
        witherKeyColor = new ColorSetting("Wither Key Color", "ESP color used for dropped Wither Keys.", -11672891);
        witherKeyLabel = new CheckboxSetting("Wither Key Label", "Show 'Wither Key' text above dropped Wither Key items.", true);
        mobEspEnabled = new CheckboxSetting("Mob ESP", "Apply a vanilla glow outline to dungeon mobs.", false);
        mobEspColor = new ColorSetting("Mob ESP Color", "Glow color used for dungeon mobs.", -43691);
        originalSlot = -1;
        lastSwapTick = -1L;
        slotSwitchScheduledTick = -1L;
        returnSwitchScheduledTick = -1L;
        targetSlot = -1;
        Setting[] settingArray = new Setting[]{bonzoEnabled, explosionDelay, sTapDuration, adaptiveTiming, experimentalMode, statsText, suggestionText, resetStats, autoSuperboomEnabled, switchDelayTicks, returnDelayTicks, requireTargetBlock, detectCrackedBricks, detectSlabs, detectStairs, allowNormalTnt, superboomDebug, witherKeyEspEnabled, witherKeyTracer, witherKeyColor, witherKeyLabel, mobEspEnabled, mobEspColor};
        INSTANCE.addSetting(settingArray);
        EventBus.register(INSTANCE);
    }
}

