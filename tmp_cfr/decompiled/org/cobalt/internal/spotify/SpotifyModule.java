/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.Triple
 *  kotlin.TuplesKt
 *  kotlin.Unit
 *  kotlin.collections.ArraysKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function3
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  net.minecraft.class_1041
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.spotify;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import javax.imageio.ImageIO;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Triple;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.minecraft.class_1041;
import net.minecraft.class_310;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.MouseEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.NvgEvent;
import org.cobalt.api.hud.HudAnchor;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.hud.HudElementBuilder;
import org.cobalt.api.hud.HudModuleDSLKt;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.ModeSetting;
import org.cobalt.api.module.setting.impl.TextSetting;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Gradient;
import org.cobalt.api.util.ui.helper.Image;
import org.cobalt.internal.spotify.SpotifyParticles;
import org.cobalt.internal.spotify.SpotifyPoller;
import org.cobalt.internal.spotify.SpotifyTrack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00a6\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0014\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0018\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0014\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0010\u0014\n\u0002\b\u001c\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0002\u00c1\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J!\u0010\b\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\b\u0010\tJ\u0017\u0010\r\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\r\u0010\u000eJ\u000f\u0010\u0010\u001a\u00020\u000fH\u0002\u00a2\u0006\u0004\b\u0010\u0010\u0003J\u0017\u0010\u0013\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u0011H\u0007\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0017\u0010\u0016\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u0015H\u0007\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0017\u0010\u0019\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u0018H\u0007\u00a2\u0006\u0004\b\u0019\u0010\u001aJ?\u0010\"\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\f2\u0006\u0010\u001c\u001a\u00020\f2\u0006\u0010\u001d\u001a\u00020\f2\u0006\u0010\u001e\u001a\u00020\f2\u0006\u0010\u001f\u001a\u00020\f2\u0006\u0010!\u001a\u00020 H\u0002\u00a2\u0006\u0004\b\"\u0010#JG\u0010(\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\f2\u0006\u0010\u001c\u001a\u00020\f2\u0006\u0010$\u001a\u00020\f2\u0006\u0010%\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010&\u001a\u00020\u00062\u0006\u0010'\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b(\u0010)J1\u0010,\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\f2\u0006\u0010\u001c\u001a\u00020\f2\b\u0010+\u001a\u0004\u0018\u00010*2\u0006\u0010\u000b\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b,\u0010-JG\u00102\u001a\u00020\u000f2\u0006\u0010.\u001a\u00020\f2\u0006\u0010/\u001a\u00020\f2\u0006\u00100\u001a\u00020\f2\u0006\u00101\u001a\u00020\f2\u0006\u0010+\u001a\u00020*2\u0006\u0010&\u001a\u00020\u00062\u0006\u0010'\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b2\u00103J'\u00107\u001a\u00020\u000f2\u0006\u00104\u001a\u00020\f2\u0006\u00105\u001a\u00020 2\u0006\u00106\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b7\u00108J\u0017\u0010:\u001a\u00020\u000f2\u0006\u00109\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b:\u0010;JG\u0010<\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\f2\u0006\u0010\u001c\u001a\u00020\f2\u0006\u0010$\u001a\u00020\f2\u0006\u0010%\u001a\u00020\f2\u0006\u0010&\u001a\u00020\u00062\u0006\u0010'\u001a\u00020\u00062\u0006\u00105\u001a\u00020 H\u0002\u00a2\u0006\u0004\b<\u0010=JW\u0010D\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\f2\u0006\u0010>\u001a\u00020\f2\u0006\u0010$\u001a\u00020\f2\u0006\u0010%\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010@\u001a\u00020?2\u0006\u0010A\u001a\u00020\f2\u0006\u0010B\u001a\u00020\f2\u0006\u0010C\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bD\u0010EJ\u0017\u0010G\u001a\u00020\f2\u0006\u0010F\u001a\u00020\fH\u0002\u00a2\u0006\u0004\bG\u0010HJ\u0017\u0010I\u001a\u00020\u000f2\u0006\u0010\u000b\u001a\u00020\nH\u0002\u00a2\u0006\u0004\bI\u0010JJ#\u0010M\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060L2\u0006\u0010K\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\bM\u0010NJA\u0010T\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060S2\u0006\u0010O\u001a\u00020\u00062\u0006\u0010P\u001a\u00020\u00062\u0006\u0010Q\u001a\u00020\u00062\u0006\u0010R\u001a\u00020\fH\u0002\u00a2\u0006\u0004\bT\u0010UJ/\u0010W\u001a\u00020\u00062\u0006\u0010O\u001a\u00020\u00062\u0006\u0010P\u001a\u00020\u00062\u0006\u0010Q\u001a\u00020\u00062\u0006\u0010V\u001a\u00020\fH\u0002\u00a2\u0006\u0004\bW\u0010XJG\u0010]\u001a\u00020\u000f2\u0006\u0010Y\u001a\u00020\u00042\u0006\u0010\u001b\u001a\u00020\f2\u0006\u0010\u001c\u001a\u00020\f2\u0006\u0010Z\u001a\u00020\f2\u0006\u0010[\u001a\u00020\f2\u0006\u0010\\\u001a\u00020\u00062\u0006\u00104\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b]\u0010^J'\u0010_\u001a\u00020\u00042\u0006\u0010Y\u001a\u00020\u00042\u0006\u0010Z\u001a\u00020\f2\u0006\u0010[\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b_\u0010`J'\u0010c\u001a\u00020\u00062\u0006\u0010a\u001a\u00020\u00062\u0006\u0010b\u001a\u00020\u00062\u0006\u0010F\u001a\u00020\fH\u0002\u00a2\u0006\u0004\bc\u0010dJ\u0017\u0010e\u001a\u00020\f2\u0006\u0010F\u001a\u00020\fH\u0002\u00a2\u0006\u0004\be\u0010HJ\u001f\u0010W\u001a\u00020\u00062\u0006\u0010\\\u001a\u00020\u00062\u0006\u0010V\u001a\u00020\fH\u0002\u00a2\u0006\u0004\bW\u0010fJ\u001f\u0010g\u001a\u00020\u00062\u0006\u0010\\\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bg\u0010hJ\u0017\u0010j\u001a\u00020\u00042\u0006\u0010i\u001a\u00020\nH\u0002\u00a2\u0006\u0004\bj\u0010kR\u0014\u0010m\u001a\u00020l8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bm\u0010nR\u0014\u0010p\u001a\u00020o8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bp\u0010qR\u0014\u0010r\u001a\u00020o8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\br\u0010qR\u0014\u0010t\u001a\u00020s8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bt\u0010uR\u0014\u0010w\u001a\u00020v8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bw\u0010xR\u0014\u0010y\u001a\u00020v8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\by\u0010xR\u0014\u0010z\u001a\u00020v8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bz\u0010xR\u0014\u0010{\u001a\u00020v8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b{\u0010xR\u0014\u0010|\u001a\u00020v8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b|\u0010xR\u0014\u0010\u007f\u001a\u00020\u00068BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b}\u0010~R\u0016\u0010\u0081\u0001\u001a\u00020\u00068BX\u0082\u0004\u00a2\u0006\u0007\u001a\u0005\b\u0080\u0001\u0010~R\u0019\u0010\u0082\u0001\u001a\u00020\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0082\u0001\u0010\u0083\u0001R\u0019\u0010\u0084\u0001\u001a\u00020\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0084\u0001\u0010\u0083\u0001R\u0019\u0010\u0085\u0001\u001a\u00020\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0085\u0001\u0010\u0083\u0001R\u0016\u0010\u0087\u0001\u001a\u00020\u00068BX\u0082\u0004\u00a2\u0006\u0007\u001a\u0005\b\u0086\u0001\u0010~R\u0016\u0010\u0089\u0001\u001a\u00020\u00068BX\u0082\u0004\u00a2\u0006\u0007\u001a\u0005\b\u0088\u0001\u0010~R\u0019\u0010\u008a\u0001\u001a\u00020\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u008a\u0001\u0010\u0083\u0001R\u001c\u0010\u008c\u0001\u001a\u0005\u0018\u00010\u008b\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u008c\u0001\u0010\u008d\u0001R\u001c\u0010\u008e\u0001\u001a\u0005\u0018\u00010\u008b\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u008e\u0001\u0010\u008d\u0001R\u0019\u0010\u008f\u0001\u001a\u00020\n8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u008f\u0001\u0010\u0090\u0001R\u0017\u0010\u0091\u0001\u001a\u00020\n8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u0091\u0001\u0010\u0090\u0001R\u0019\u0010\u0092\u0001\u001a\u00020 8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0092\u0001\u0010\u0093\u0001R\u001c\u0010\u0094\u0001\u001a\u0005\u0018\u00010\u008b\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0094\u0001\u0010\u008d\u0001R\u001c\u0010\u0095\u0001\u001a\u0005\u0018\u00010\u008b\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0095\u0001\u0010\u008d\u0001R\u001c\u0010\u0096\u0001\u001a\u0005\u0018\u00010\u008b\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0096\u0001\u0010\u008d\u0001R\u001c\u0010\u0097\u0001\u001a\u0005\u0018\u00010\u008b\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0097\u0001\u0010\u008d\u0001R\u0019\u0010\u0098\u0001\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0098\u0001\u0010\u0099\u0001R\u0019\u0010\u009a\u0001\u001a\u00020\n8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u009a\u0001\u0010\u0090\u0001R\u0019\u0010\u009b\u0001\u001a\u00020\f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u009b\u0001\u0010\u009c\u0001R\u0019\u0010\u009d\u0001\u001a\u00020\f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u009d\u0001\u0010\u009c\u0001R\u0019\u0010\u009e\u0001\u001a\u00020\f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u009e\u0001\u0010\u009c\u0001R\u0018\u0010\u00a0\u0001\u001a\u00030\u009f\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a0\u0001\u0010\u00a1\u0001R\u0019\u0010\u00a2\u0001\u001a\u00020\f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00a2\u0001\u0010\u009c\u0001R\u0019\u0010\u00a3\u0001\u001a\u00020\f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00a3\u0001\u0010\u009c\u0001R\u0019\u0010\u00a4\u0001\u001a\u00020\f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00a4\u0001\u0010\u009c\u0001R\u0019\u0010\u00a5\u0001\u001a\u00020\f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00a5\u0001\u0010\u009c\u0001R\u0019\u0010\u00a6\u0001\u001a\u00020\f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00a6\u0001\u0010\u009c\u0001R\u0019\u0010\u00a7\u0001\u001a\u00020\f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00a7\u0001\u0010\u009c\u0001R\u0017\u0010\u00a8\u0001\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00a8\u0001\u0010\u009c\u0001R\u0017\u0010\u00a9\u0001\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00a9\u0001\u0010\u009c\u0001R\u0017\u0010\u00aa\u0001\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00aa\u0001\u0010\u009c\u0001R\u0017\u0010\u00ab\u0001\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00ab\u0001\u0010\u009c\u0001R\u0017\u0010\u00ac\u0001\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00ac\u0001\u0010\u009c\u0001R\u0017\u0010\u00ad\u0001\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00ad\u0001\u0010\u009c\u0001R\u0017\u0010\u00ae\u0001\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00ae\u0001\u0010\u009c\u0001R\u0017\u0010\u00af\u0001\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00af\u0001\u0010\u009c\u0001R\u0017\u0010\u00b0\u0001\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00b0\u0001\u0010\u009c\u0001R\u0017\u0010\u00b1\u0001\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00b1\u0001\u0010\u009c\u0001R\u0017\u0010\u00b2\u0001\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00b2\u0001\u0010\u009c\u0001R\u0017\u0010\u00b3\u0001\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00b3\u0001\u0010\u009c\u0001R\u0017\u0010\u00b4\u0001\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00b4\u0001\u0010\u009c\u0001R\u0017\u0010\u00b5\u0001\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00b5\u0001\u0010\u009c\u0001R\u0017\u0010\u00b6\u0001\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00b6\u0001\u0010\u009c\u0001R\u0017\u0010\u00b7\u0001\u001a\u00020\u00068\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00b7\u0001\u0010\u0083\u0001R\u0017\u0010\u00b8\u0001\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00b8\u0001\u0010\u009c\u0001R\u0017\u0010\u00b9\u0001\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00b9\u0001\u0010\u009c\u0001R\u0017\u0010\u00ba\u0001\u001a\u00020\u00068\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00ba\u0001\u0010\u0083\u0001R\u0017\u0010\u00bb\u0001\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00bb\u0001\u0010\u009c\u0001R\u001d\u0010\u00bd\u0001\u001a\u00030\u00bc\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00bd\u0001\u0010\u00be\u0001\u001a\u0006\b\u00bf\u0001\u0010\u00c0\u0001\u00a8\u0006\u00c2\u0001"}, d2={"Lorg/cobalt/internal/spotify/SpotifyModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "", "hex", "", "alpha", "hexToArgb", "(Ljava/lang/String;I)I", "", "now", "", "artFadeAlpha", "(J)F", "", "ensureIconsLoaded", "Lorg/cobalt/api/event/impl/render/NvgEvent;", "event", "onNvg", "(Lorg/cobalt/api/event/impl/render/NvgEvent;)V", "Lorg/cobalt/api/event/impl/client/MouseEvent$LeftClick;", "onMouseClick", "(Lorg/cobalt/api/event/impl/client/MouseEvent$LeftClick;)V", "Lorg/cobalt/api/event/impl/client/TickEvent$End;", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$End;)V", "x", "y", "screenX", "screenY", "scale", "", "showControls", "renderHudContent", "(FFFFFZ)V", "w", "h", "c1", "c2", "drawBackground", "(FFFFJII)V", "Lorg/cobalt/internal/spotify/SpotifyTrack;", "track", "drawAlbumArt", "(FFLorg/cobalt/internal/spotify/SpotifyTrack;J)V", "barX", "barY", "barW", "barH", "drawProgressBar", "(FFFFLorg/cobalt/internal/spotify/SpotifyTrack;II)V", "dt", "isPlaying", "audioLevel", "updateWaveformState", "(FZF)V", "value", "pushWaveformSample", "(F)V", "drawWaveform", "(FFFFIIZ)V", "midY", "Lorg/cobalt/internal/spotify/SpotifyModule$WaveLayerSpec;", "spec", "activity", "alphaScale", "beatAlpha", "drawWaveformLayer", "(FFFFFLorg/cobalt/internal/spotify/SpotifyModule$WaveLayerSpec;FFI)V", "t", "sampleWaveform", "(F)F", "refreshArtCache", "(J)V", "path", "Lkotlin/Pair;", "extractDominantColors", "(Ljava/lang/String;)Lkotlin/Pair;", "r", "g", "b", "factor", "Lkotlin/Triple;", "boostSaturation", "(IIIF)Lkotlin/Triple;", "degrees", "shiftHue", "(IIIF)I", "text", "maxW", "size", "color", "drawScrollingTitle", "(Ljava/lang/String;FFFFIF)V", "truncate", "(Ljava/lang/String;FF)Ljava/lang/String;", "from", "to", "lerpColor", "(IIF)I", "smoothStep", "(IF)I", "withAlpha", "(II)I", "ms", "formatMs", "(J)Ljava/lang/String;", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "color1Setting", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "color2Setting", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "gradientDirectionSetting", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "autoColorSetting", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "glowSetting", "particlesSetting", "showTimeSetting", "waveformSetting", "getManualColor1", "()I", "manualColor1", "getManualColor2", "manualColor2", "artDominantC1", "I", "artDominantC2", "dominantArtVersion", "getColor1", "color1", "getColor2", "color2", "cachedArtVersion", "Lorg/cobalt/api/util/ui/helper/Image;", "cachedArtImage", "Lorg/cobalt/api/util/ui/helper/Image;", "prevArtImage", "artFadeStartMs", "J", "ART_FADE_MS", "iconsLoaded", "Z", "imgPrev", "imgPlay", "imgPause", "imgNext", "lastTrackName", "Ljava/lang/String;", "lastRenderMs", "titleScrollX", "F", "titleScrollDir", "titleScrollPause", "", "waveformHistory", "[F", "waveformSampleTimer", "waveformEnvelope", "waveformPeak", "waveformBeatPulse", "waveformMotionTime", "waveformAdaptivePeak", "W", "H", "ART", "ART_FRAME", "CORNER", "PAD", "TEXT_X", "BTN_W", "BTN_H", "BTN_Y_L", "BAR_Y", "WAVE_W", "WAVE_H", "WAVE_Y", "WAVE_GAP", "WAVE_BARS", "WAVE_SAMPLE_S", "WAVE_NOISE_FLOOR", "WAVE_RENDER_SAMPLES", "WAVE_BEAT_TRIGGER", "Lorg/cobalt/api/hud/HudElement;", "spotifyHud", "Lorg/cobalt/api/hud/HudElement;", "getSpotifyHud", "()Lorg/cobalt/api/hud/HudElement;", "WaveLayerSpec", "cobalt"})
@SourceDebugExtension(value={"SMAP\nSpotifyModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SpotifyModule.kt\norg/cobalt/internal/spotify/SpotifyModule\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,773:1\n1#2:774\n1915#3,2:775\n*S KotlinDebug\n*F\n+ 1 SpotifyModule.kt\norg/cobalt/internal/spotify/SpotifyModule\n*L\n308#1:775,2\n*E\n"})
public final class SpotifyModule
extends Module {
    @NotNull
    public static final SpotifyModule INSTANCE = new SpotifyModule();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final TextSetting color1Setting;
    @NotNull
    private static final TextSetting color2Setting;
    @NotNull
    private static final ModeSetting gradientDirectionSetting;
    @NotNull
    private static final CheckboxSetting autoColorSetting;
    @NotNull
    private static final CheckboxSetting glowSetting;
    @NotNull
    private static final CheckboxSetting particlesSetting;
    @NotNull
    private static final CheckboxSetting showTimeSetting;
    @NotNull
    private static final CheckboxSetting waveformSetting;
    private static int artDominantC1;
    private static int artDominantC2;
    private static int dominantArtVersion;
    private static int cachedArtVersion;
    @Nullable
    private static Image cachedArtImage;
    @Nullable
    private static Image prevArtImage;
    private static long artFadeStartMs;
    private static final long ART_FADE_MS = 700L;
    private static boolean iconsLoaded;
    @Nullable
    private static Image imgPrev;
    @Nullable
    private static Image imgPlay;
    @Nullable
    private static Image imgPause;
    @Nullable
    private static Image imgNext;
    @NotNull
    private static String lastTrackName;
    private static long lastRenderMs;
    private static float titleScrollX;
    private static float titleScrollDir;
    private static float titleScrollPause;
    @NotNull
    private static final float[] waveformHistory;
    private static float waveformSampleTimer;
    private static float waveformEnvelope;
    private static float waveformPeak;
    private static float waveformBeatPulse;
    private static float waveformMotionTime;
    private static float waveformAdaptivePeak;
    private static final float W = 305.0f;
    private static final float H = 90.0f;
    private static final float ART = 60.0f;
    private static final float ART_FRAME = 60.0f;
    private static final float CORNER = 10.0f;
    private static final float PAD = 8.0f;
    private static final float TEXT_X = 76.0f;
    private static final float BTN_W = 32.0f;
    private static final float BTN_H = 18.0f;
    private static final float BTN_Y_L = 54.0f;
    private static final float BAR_Y = 76.0f;
    private static final float WAVE_W = 82.0f;
    private static final float WAVE_H = 26.0f;
    private static final float WAVE_Y = 16.0f;
    private static final float WAVE_GAP = 8.0f;
    private static final int WAVE_BARS = 18;
    private static final float WAVE_SAMPLE_S = 0.045f;
    private static final float WAVE_NOISE_FLOOR = 5.0E-5f;
    private static final int WAVE_RENDER_SAMPLES = 38;
    private static final float WAVE_BEAT_TRIGGER = 0.12f;
    @NotNull
    private static final HudElement spotifyHud;

    private SpotifyModule() {
        super("Spotify");
    }

    private final int hexToArgb(String hex, int alpha) {
        char[] cArray = new char[]{'#'};
        String stripped = StringsKt.padStart((String)StringsKt.takeLast((String)StringsKt.trimStart((String)((Object)StringsKt.trim((CharSequence)hex)).toString(), (char[])cArray), (int)6), (int)6, (char)'0');
        Integer n = StringsKt.toIntOrNull((String)stripped, (int)16);
        int rgb = n != null ? n : 1947988;
        return alpha << 24 | rgb & 0xFFFFFF;
    }

    static /* synthetic */ int hexToArgb$default(SpotifyModule spotifyModule, String string, int n, int n2, Object object) {
        if ((n2 & 2) != 0) {
            n = 255;
        }
        return spotifyModule.hexToArgb(string, n);
    }

    private final int getManualColor1() {
        return SpotifyModule.hexToArgb$default(this, (String)color1Setting.getValue(), 0, 2, null);
    }

    private final int getManualColor2() {
        return SpotifyModule.hexToArgb$default(this, (String)color2Setting.getValue(), 0, 2, null);
    }

    private final int getColor1() {
        return (Boolean)autoColorSetting.getValue() != false ? artDominantC1 : this.getManualColor1();
    }

    private final int getColor2() {
        return (Boolean)autoColorSetting.getValue() != false ? artDominantC2 : this.getManualColor2();
    }

    private final float artFadeAlpha(long now) {
        return RangesKt.coerceIn((float)((float)(now - artFadeStartMs) / (float)700L), (float)0.0f, (float)1.0f);
    }

    private final void ensureIconsLoaded() {
        Object object;
        Object $this$ensureIconsLoaded_u24lambda_u242;
        Object $this$ensureIconsLoaded_u24lambda_u241;
        Object $this$ensureIconsLoaded_u24lambda_u240;
        if (iconsLoaded) {
            return;
        }
        iconsLoaded = true;
        Object object2 = this;
        try {
            $this$ensureIconsLoaded_u24lambda_u240 = object2;
            boolean bl = false;
            $this$ensureIconsLoaded_u24lambda_u240 = Result.constructor-impl((Object)NVGRenderer.createImage("/assets/cobalt/textures/ui/ic_spotify_prev.svg"));
        }
        catch (Throwable bl) {
            $this$ensureIconsLoaded_u24lambda_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
        }
        object2 = $this$ensureIconsLoaded_u24lambda_u240;
        imgPrev = (Image)(Result.isFailure-impl((Object)object2) ? null : object2);
        object2 = this;
        try {
            $this$ensureIconsLoaded_u24lambda_u241 = (SpotifyModule)object2;
            boolean bl = false;
            $this$ensureIconsLoaded_u24lambda_u241 = Result.constructor-impl((Object)NVGRenderer.createImage("/assets/cobalt/textures/ui/ic_spotify_play.svg"));
        }
        catch (Throwable bl) {
            $this$ensureIconsLoaded_u24lambda_u241 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
        }
        object2 = $this$ensureIconsLoaded_u24lambda_u241;
        imgPlay = (Image)(Result.isFailure-impl((Object)object2) ? null : object2);
        object2 = this;
        try {
            $this$ensureIconsLoaded_u24lambda_u242 = (SpotifyModule)object2;
            boolean bl = false;
            $this$ensureIconsLoaded_u24lambda_u242 = Result.constructor-impl((Object)NVGRenderer.createImage("/assets/cobalt/textures/ui/ic_spotify_pause.svg"));
        }
        catch (Throwable bl) {
            $this$ensureIconsLoaded_u24lambda_u242 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
        }
        object2 = $this$ensureIconsLoaded_u24lambda_u242;
        imgPause = (Image)(Result.isFailure-impl((Object)object2) ? null : object2);
        object2 = this;
        try {
            SpotifyModule $this$ensureIconsLoaded_u24lambda_u243 = (SpotifyModule)object2;
            boolean bl = false;
            object = Result.constructor-impl((Object)NVGRenderer.createImage("/assets/cobalt/textures/ui/ic_spotify_next.svg"));
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        object2 = object;
        imgNext = (Image)(Result.isFailure-impl((Object)object2) ? null : object2);
    }

    @NotNull
    public final HudElement getSpotifyHud() {
        return spotifyHud;
    }

    @SubscribeEvent
    public final void onNvg(@NotNull NvgEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (SpotifyModule.mc.field_1755 == null) {
            return;
        }
        if (!spotifyHud.getEnabled()) {
            return;
        }
        class_1041 class_10412 = mc.method_22683();
        Intrinsics.checkNotNullExpressionValue((Object)class_10412, (String)"getWindow(...)");
        class_1041 window = class_10412;
        float sw = window.method_4480();
        float sh = window.method_4507();
        Pair<Float, Float> pair = spotifyHud.getScreenPosition(sw, sh);
        float sx = ((Number)pair.component1()).floatValue();
        float sy = ((Number)pair.component2()).floatValue();
        float s = spotifyHud.getScale();
        NVGRenderer.INSTANCE.beginFrame(sw, sh);
        NVGRenderer.push();
        NVGRenderer.translate(sx, sy);
        NVGRenderer.scale(s, s);
        this.renderHudContent(0.0f, 0.0f, sx, sy, s, true);
        NVGRenderer.pop();
        NVGRenderer.INSTANCE.endFrame();
    }

    @SubscribeEvent
    public final void onMouseClick(@NotNull MouseEvent.LeftClick event) {
        float btnStartX;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (SpotifyModule.mc.field_1755 == null) {
            return;
        }
        if (!spotifyHud.getEnabled()) {
            return;
        }
        if (SpotifyPoller.INSTANCE.getCurrent() == null) {
            return;
        }
        class_1041 class_10412 = mc.method_22683();
        Intrinsics.checkNotNullExpressionValue((Object)class_10412, (String)"getWindow(...)");
        class_1041 window = class_10412;
        float sw = window.method_4480();
        float sh = window.method_4507();
        Pair<Float, Float> pair = spotifyHud.getScreenPosition(sw, sh);
        float sx = ((Number)pair.component1()).floatValue();
        float sy = ((Number)pair.component2()).floatValue();
        float s = spotifyHud.getScale();
        float mx = (float)SpotifyModule.mc.field_1729.method_1603();
        float my = (float)SpotifyModule.mc.field_1729.method_1604();
        float lmx = (mx - sx) / s;
        float lmy = (my - sy) / s;
        float totalBtnW = 112.0f;
        float textAreaW = 221.0f;
        float b0x = btnStartX = 76.0f + (textAreaW - totalBtnW) / 2.0f;
        float b1x = btnStartX + 32.0f + 8.0f;
        float b2x = btnStartX + 80.0f;
        if (SpotifyModule.onMouseClick$hitsBtn(lmx, lmy, b0x)) {
            SpotifyPoller.INSTANCE.sendCommand(177);
            event.setCancelled(true);
        } else if (SpotifyModule.onMouseClick$hitsBtn(lmx, lmy, b1x)) {
            SpotifyPoller.INSTANCE.sendCommand(179);
            event.setCancelled(true);
        } else if (SpotifyModule.onMouseClick$hitsBtn(lmx, lmy, b2x)) {
            SpotifyPoller.INSTANCE.sendCommand(176);
            event.setCancelled(true);
        }
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.End event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!spotifyHud.getEnabled()) {
            return;
        }
        SpotifyPoller.INSTANCE.update();
    }

    private final void renderHudContent(float x, float y, float screenX, float screenY, float scale, boolean showControls) {
        long now = System.currentTimeMillis();
        float dt = RangesKt.coerceIn((float)((float)(now - lastRenderMs) / 1000.0f), (float)0.0f, (float)0.1f);
        lastRenderMs = now;
        float it = waveformMotionTime + dt;
        boolean bl = false;
        waveformMotionTime = it >= 10000.0f ? it - 10000.0f : it;
        int c1 = this.getColor1();
        int c2 = this.getColor2();
        SpotifyTrack track = SpotifyPoller.INSTANCE.getCurrent();
        this.refreshArtCache(now);
        this.drawBackground(x, y, 305.0f, 90.0f, now, c1, c2);
        this.drawAlbumArt(x, y, track, now);
        int textColor = -1;
        int dimColor = -1146570544;
        if (track == null) {
            NVGRenderer.text$default("Open Spotify and play something", x + 76.0f, y + 45.0f - 5.0f, 10.0f, dimColor, null, 32, null);
            this.updateWaveformState(dt, false, 0.0f);
            return;
        }
        this.updateWaveformState(dt, track.isPlaying(), SpotifyPoller.INSTANCE.getAudioLevel());
        if (!Intrinsics.areEqual((Object)track.getName(), (Object)lastTrackName)) {
            lastTrackName = track.getName();
            titleScrollX = 0.0f;
            titleScrollDir = 1.0f;
            titleScrollPause = 1.5f;
            ArraysKt.fill$default((float[])waveformHistory, (float)0.0f, (int)0, (int)0, (int)6, null);
            waveformSampleTimer = 0.0f;
            waveformEnvelope = 0.0f;
            waveformPeak = 0.0f;
            waveformBeatPulse = 0.0f;
            waveformMotionTime = 0.0f;
            waveformAdaptivePeak = 0.08f;
            if (((Boolean)particlesSetting.getValue()).booleanValue()) {
                SpotifyParticles.INSTANCE.burst(x + 8.0f, y + 76.0f, 289.0f, c1, c2);
            }
        }
        float waveformReservedW = (Boolean)waveformSetting.getValue() != false ? 90.0f : 0.0f;
        float textMaxW = RangesKt.coerceAtLeast((float)(221.0f - waveformReservedW), (float)60.0f);
        this.drawScrollingTitle(track.getName(), x + 76.0f, y + 18.0f, textMaxW, 12.0f, textColor, dt);
        NVGRenderer.text$default(this.truncate(track.getArtist(), textMaxW, 10.0f), x + 76.0f, y + 33.0f, 10.0f, dimColor, null, 32, null);
        if (((Boolean)showTimeSetting.getValue()).booleanValue()) {
            String timeStr = this.formatMs(track.getCurrentProgressMs()) + " / " + this.formatMs(track.getDurationMs());
            NVGRenderer.text$default(timeStr, x + 76.0f, y + 46.0f, 9.0f, dimColor, null, 32, null);
        }
        if (((Boolean)waveformSetting.getValue()).booleanValue()) {
            this.drawWaveform(x + 305.0f - 8.0f - 82.0f, y + 16.0f, 82.0f, 26.0f, c1, c2, track.isPlaying());
        }
        if (showControls) {
            float btnStartX;
            this.ensureIconsLoaded();
            float rawMx = (float)SpotifyModule.mc.field_1729.method_1603();
            float rawMy = (float)SpotifyModule.mc.field_1729.method_1604();
            float lmx = scale > 0.0f ? (rawMx - screenX) / scale : rawMx;
            float lmy = scale > 0.0f ? (rawMy - screenY) / scale : rawMy;
            float totalBtnW = 112.0f;
            float textAreaW = 221.0f;
            float b0x = btnStartX = x + 76.0f + (textAreaW - totalBtnW) / 2.0f;
            float b1x = btnStartX + 32.0f + 8.0f;
            float b2x = btnStartX + 80.0f;
            float bY = y + 54.0f;
            Image playImg = track.isPlaying() ? imgPause : imgPlay;
            Object[] objectArray = new Pair[]{TuplesKt.to((Object)Float.valueOf(b0x), (Object)imgPrev), TuplesKt.to((Object)Float.valueOf(b1x), (Object)playImg), TuplesKt.to((Object)Float.valueOf(b2x), (Object)imgNext)};
            Iterable $this$forEach$iv = CollectionsKt.listOf((Object[])objectArray);
            boolean $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                Pair pair = (Pair)element$iv;
                boolean bl2 = false;
                float bx = ((Number)pair.component1()).floatValue();
                Image img = (Image)pair.component2();
                float localBx = bx - x;
                boolean hovered = lmx >= localBx && lmx <= localBx + 32.0f && lmy >= 54.0f && lmy <= 72.0f;
                int bgAlpha = hovered ? 85 : 34;
                NVGRenderer.rect(bx, bY, 32.0f, 18.0f, bgAlpha << 24 | 0xFFFFFF, 6.0f);
                NVGRenderer.hollowRect(bx, bY, 32.0f, 18.0f, 1.0f, 0x33FFFFFF, 6.0f);
                if (img == null) continue;
                float iconSize = 12.0f;
                NVGRenderer.image(img, bx + (32.0f - iconSize) / 2.0f, bY + (18.0f - iconSize) / 2.0f, iconSize, iconSize, 0.0f, hovered ? -1 : -855638017);
            }
        }
        this.drawProgressBar(x + 8.0f, y + 76.0f, 289.0f, 6.0f, track, c1, c2);
        if (((Boolean)particlesSetting.getValue()).booleanValue()) {
            float fillW = RangesKt.coerceIn((float)(289.0f * ((float)track.getCurrentProgressMs() / (float)track.getDurationMs())), (float)0.0f, (float)289.0f);
            SpotifyParticles.INSTANCE.update(dt, x + 8.0f, y + 76.0f, fillW, c1, c2, track.isPlaying());
            SpotifyParticles.INSTANCE.render();
        }
    }

    private final void drawBackground(float x, float y, float w, float h, long now, int c1, int c2) {
        float twoPi = (float)Math.PI * 2;
        if (((Boolean)glowSetting.getValue()).booleanValue()) {
            float pulse = 0.4f + 0.6f * (float)Math.cos((float)(now % 4000L) / 4000.0f * twoPi);
            int a2 = RangesKt.coerceIn((int)((int)((float)24 * pulse)), (int)0, (int)40);
            int a1 = RangesKt.coerceIn((int)((int)((float)42 * pulse)), (int)0, (int)64);
            NVGRenderer.hollowRect(x - 3.0f, y - 3.0f, w + 6.0f, h + 6.0f, 2.5f, a2 << 24 | c1 & 0xFFFFFF, 13.0f);
            NVGRenderer.hollowRect(x - 1.5f, y - 1.5f, w + 3.0f, h + 3.0f, 1.5f, a1 << 24 | c1 & 0xFFFFFF, 11.5f);
        }
        NVGRenderer.rect(x, y, w, h, -16118246, 10.0f);
        NVGRenderer.gradientRect(x, y, w, h * 0.5f, 0x14FFFFFF, 0, Gradient.TopToBottom, 10.0f);
        float angle = (float)(now % 10000L) / 10000.0f * twoPi;
        float shiftX = (float)Math.cos(angle) * (w * 0.42f);
        NVGRenderer.hollowGradientRectShifted(x, y, w, h, 1.5f, c1, c2, Gradient.LeftToRight, 10.0f, shiftX, 0.0f);
    }

    private final void drawAlbumArt(float x, float y, SpotifyTrack track, long now) {
        int a;
        float artX = x + 8.0f;
        float artY = y + 8.0f;
        NVGRenderer.rect(artX, artY, 60.0f, 60.0f, -15723231, 8.0f);
        NVGRenderer.hollowRect(artX, artY, 60.0f, 60.0f, 1.0f, 0x22FFFFFF, 8.0f);
        float fade = this.artFadeAlpha(now);
        Image prev = prevArtImage;
        Image curr = cachedArtImage;
        if (prev != null && fade < 1.0f) {
            a = RangesKt.coerceIn((int)((int)((1.0f - fade) * (float)255)), (int)0, (int)255);
            NVGRenderer.image(prev, artX, artY, 60.0f, 60.0f, 6.0f, a << 24 | 0xFFFFFF);
        }
        if (curr != null) {
            a = RangesKt.coerceIn((int)((int)(fade * (float)255)), (int)0, (int)255);
            NVGRenderer.image(curr, artX, artY, 60.0f, 60.0f, 6.0f, a << 24 | 0xFFFFFF);
            SpotifyTrack spotifyTrack = track;
            boolean bl = spotifyTrack != null ? !spotifyTrack.isPlaying() : false;
            if (bl) {
                NVGRenderer.rect(artX, artY, 60.0f, 60.0f, 0x55000000, 6.0f);
            }
        } else {
            NVGRenderer.rect(artX, artY, 60.0f, 60.0f, -15460316, 6.0f);
            float noteW = NVGRenderer.textWidth$default("\u266a", 22.0f, null, 4, null);
            NVGRenderer.text$default("\u266a", artX + 30.0f - noteW / 2.0f, artY + 30.0f - 11.0f, 22.0f, 0x33FFFFFF, null, 32, null);
        }
        if (fade >= 1.0f && prev != null) {
            NVGRenderer.deleteImage(prev);
            prevArtImage = null;
        }
    }

    private final void drawProgressBar(float barX, float barY, float barW, float barH, SpotifyTrack track, int c1, int c2) {
        float barR = barH / 2.0f;
        float prog = RangesKt.coerceIn((float)((float)track.getCurrentProgressMs() / (float)track.getDurationMs()), (float)0.0f, (float)1.0f);
        float fillW = RangesKt.coerceAtLeast((float)(barW * prog), (float)0.0f);
        NVGRenderer.rect(barX, barY, barW, barH, -15065024, barR);
        if (fillW > barR * 2.0f) {
            Gradient gradient = ((Number)gradientDirectionSetting.getValue()).intValue() == 1 ? Gradient.TopToBottom : Gradient.LeftToRight;
            NVGRenderer.pushScissor(barX, barY, fillW, barH);
            NVGRenderer.gradientRect(barX, barY, barW, barH, c1, c2, gradient, barR);
            NVGRenderer.popScissor();
            float tipX = barX + fillW;
            NVGRenderer.circle(tipX, barY + barH / 2.0f, barH * 0.7f, 0x88000000 | c1 & 0xFFFFFF);
            NVGRenderer.circle(tipX, barY + barH / 2.0f, barH * 0.38f, -855638017);
        }
    }

    private final void updateWaveformState(float dt, boolean isPlaying, float audioLevel) {
        float rawLevel = isPlaying ? RangesKt.coerceIn((float)audioLevel, (float)0.0f, (float)1.0f) : 0.0f;
        float cleanedLevel = rawLevel <= 5.0E-5f ? 0.0f : RangesKt.coerceIn((float)((rawLevel - 5.0E-5f) / 0.99995f), (float)0.0f, (float)1.0f);
        float adaptiveDecay = isPlaying ? 0.22f : 0.5f;
        waveformAdaptivePeak = RangesKt.coerceIn((float)(cleanedLevel > waveformAdaptivePeak ? cleanedLevel : Math.max(cleanedLevel, waveformAdaptivePeak - dt * adaptiveDecay)), (float)0.035f, (float)1.0f);
        float normalizedLevel = cleanedLevel <= 0.0f ? 0.0f : RangesKt.coerceIn((float)(cleanedLevel / waveformAdaptivePeak), (float)0.0f, (float)1.0f);
        float target = normalizedLevel <= 0.0f ? 0.0f : RangesKt.coerceIn((float)((float)Math.pow(normalizedLevel, 0.48f) * 0.92f + (float)Math.pow(cleanedLevel, 0.26f) * 0.32f), (float)0.0f, (float)1.0f);
        float response = target > waveformEnvelope ? 1.0f - (float)Math.exp(-18.0f * dt) : 1.0f - (float)Math.exp(-5.5f * dt);
        float previousEnvelope = waveformEnvelope;
        waveformEnvelope += (target - waveformEnvelope) * response;
        float f = RangesKt.coerceAtLeast((float)(target - previousEnvelope), (float)0.0f);
        if (f >= 0.12f) {
            waveformBeatPulse = 1.0f;
        }
        waveformBeatPulse = RangesKt.coerceAtLeast((float)(waveformBeatPulse - dt * 1.9f), (float)0.0f);
        waveformPeak = Math.max(target, waveformPeak - dt * 0.85f);
        waveformSampleTimer += dt;
        while (waveformSampleTimer >= 0.045f) {
            waveformSampleTimer -= 0.045f;
            float shaped = waveformEnvelope <= 0.002f ? 0.0f : RangesKt.coerceIn((float)((float)Math.pow(waveformEnvelope, 0.34f) * 0.92f + RangesKt.coerceIn((float)f, (float)0.0f, (float)1.0f) * 0.42f + waveformBeatPulse * 0.18f), (float)0.0f, (float)1.0f);
            this.pushWaveformSample(shaped);
        }
        if (!isPlaying && rawLevel <= 0.0f) {
            int n = waveformHistory.length;
            for (int i = 0; i < n; ++i) {
                float[] fArray = waveformHistory;
                int n2 = i;
                fArray[n2] = fArray[n2] * 0.86f;
                if (!(waveformHistory[i] < 0.01f)) continue;
                SpotifyModule.waveformHistory[i] = 0.0f;
            }
            waveformEnvelope *= 0.84f;
            waveformPeak *= 0.9f;
            waveformAdaptivePeak = Math.max(0.08f, waveformAdaptivePeak * 0.92f);
        }
    }

    private final void pushWaveformSample(float value) {
        for (int i = 0; i < 17; ++i) {
            SpotifyModule.waveformHistory[i] = waveformHistory[i + 1];
        }
        SpotifyModule.waveformHistory[17] = value;
    }

    private final void drawWaveform(float x, float y, float w, float h, int c1, int c2, boolean isPlaying) {
        float midY = y + h / 2.0f;
        float now = waveformMotionTime;
        float activity = isPlaying ? 1.0f : 0.48f;
        int centerColor = this.lerpColor(c1, c2, 0.5f);
        int baseAlpha = RangesKt.coerceIn((int)((int)(((float)24 + waveformPeak * (float)40) * activity)), (int)0, (int)72);
        int pulseAlpha = RangesKt.coerceIn((int)((int)(((float)16 + waveformBeatPulse * (float)54) * activity)), (int)0, (int)84);
        WaveLayerSpec[] waveLayerSpecArray = new WaveLayerSpec[]{new WaveLayerSpec(this.shiftHue(c1, -16.0f), -0.95f, 0.82f, 0.95f, 0.25f), new WaveLayerSpec(c1, -0.25f, 1.08f, 1.12f, 0.42f), new WaveLayerSpec(centerColor, 0.45f, 1.36f, 1.32f, 0.56f), new WaveLayerSpec(c2, 1.05f, 1.68f, 1.06f, 0.38f), new WaveLayerSpec(this.shiftHue(c2, 16.0f), 1.55f, 2.02f, 0.88f, 0.22f)};
        WaveLayerSpec[] layers = waveLayerSpecArray;
        NVGRenderer.line(x + 2.0f, midY, x + w - 2.0f, midY, 1.0f, this.withAlpha(centerColor, baseAlpha));
        if (pulseAlpha > 0) {
            NVGRenderer.line(x + 6.0f, midY, x + w - 6.0f, midY, 2.3f, this.withAlpha(centerColor, pulseAlpha / 2));
        }
        int n = layers.length;
        for (int i = 0; i < n; ++i) {
            int index = i;
            WaveLayerSpec layer = layers[i];
            float alphaScale = index == 2 ? 1.0f : 0.78f;
            this.drawWaveformLayer(x, midY, w, h, now, layer, activity, alphaScale, pulseAlpha);
        }
    }

    private final void drawWaveformLayer(float x, float midY, float w, float h, float now, WaveLayerSpec spec, float activity, float alphaScale, int beatAlpha) {
        int samples = 38;
        int glowAlpha = RangesKt.coerceIn((int)((int)(((float)22 + waveformPeak * (float)66) * activity * alphaScale)), (int)0, (int)84);
        int coreAlpha = RangesKt.coerceIn((int)((int)(((float)70 + waveformEnvelope * (float)152 + waveformBeatPulse * (float)54) * activity * alphaScale)), (int)0, (int)255);
        int accentAlpha = RangesKt.coerceIn((int)((int)(((float)16 + (float)beatAlpha * 0.3f) * alphaScale)), (int)0, (int)50);
        float prevX = x;
        float prevY = midY;
        int sample = 0;
        while (true) {
            float t = (float)sample / (float)samples;
            float energy = this.sampleWaveform(t);
            float edge = (float)Math.pow((float)Math.sin(t * (float)Math.PI), 0.92f);
            float ripple = (float)Math.sin(t * (float)Math.PI * spec.getFrequency() - now * (2.6f + spec.getFrequency() * 0.45f) + spec.getPhaseOffset());
            float detail = (float)Math.sin(t * (float)Math.PI * (spec.getFrequency() * 1.9f + 0.8f) + now * (1.4f + spec.getShimmerScale()) - spec.getPhaseOffset() * 0.7f);
            float sway = (float)Math.sin(now * (0.85f + spec.getShimmerScale() * 0.6f) + t * (float)Math.PI * 2.3f + spec.getPhaseOffset()) * 0.12f;
            float heightScale = (0.08f + energy * (0.52f + spec.getAmplitudeScale() * 0.18f) + waveformBeatPulse * 0.08f) * edge * activity;
            float displacement = (ripple * 0.72f + detail * 0.22f + sway) * (h * spec.getAmplitudeScale() * heightScale);
            float px = x + t * w;
            float py = midY + displacement;
            if (sample > 0) {
                NVGRenderer.line(prevX, prevY, px, py, 4.6f, this.withAlpha(spec.getColor(), glowAlpha));
                NVGRenderer.line(prevX, prevY, px, py, 2.15f, this.withAlpha(spec.getColor(), coreAlpha));
                if (accentAlpha > 0 && sample % 6 == 0) {
                    NVGRenderer.circle(px, py, 1.3f, this.withAlpha(-1, accentAlpha));
                }
            }
            prevX = px;
            prevY = py;
            if (sample == samples) break;
            ++sample;
        }
    }

    private final float sampleWaveform(float t) {
        float scaled = RangesKt.coerceIn((float)t, (float)0.0f, (float)1.0f) * (float)17;
        int start = RangesKt.coerceIn((int)((int)scaled), (int)0, (int)17);
        int end = RangesKt.coerceAtMost((int)(start + 1), (int)17);
        float mix = this.smoothStep(scaled - (float)start);
        return RangesKt.coerceIn((float)(waveformHistory[start] + (waveformHistory[end] - waveformHistory[start]) * mix), (float)0.0f, (float)1.0f);
    }

    private final void refreshArtCache(long now) {
        Object object;
        String path;
        Object it;
        int ver = SpotifyPoller.INSTANCE.getArtVersion();
        if (ver == cachedArtVersion) {
            return;
        }
        cachedArtVersion = ver;
        Image old = cachedArtImage;
        if (old != null) {
            Image image = prevArtImage;
            if (image != null) {
                it = image;
                boolean bl = false;
                NVGRenderer.deleteImage((Image)it);
            }
            prevArtImage = old;
            cachedArtImage = null;
            artFadeStartMs = now;
        }
        if (((CharSequence)(path = SpotifyPoller.INSTANCE.getCurrentArtPath())).length() > 0) {
            Object $this$refreshArtCache_u24lambda_u241;
            object = this;
            try {
                $this$refreshArtCache_u24lambda_u241 = object;
                boolean bl = false;
                cachedArtImage = NVGRenderer.createImage(path);
                $this$refreshArtCache_u24lambda_u241 = Result.constructor-impl((Object)Unit.INSTANCE);
            }
            catch (Throwable bl) {
                $this$refreshArtCache_u24lambda_u241 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
            }
            if (artFadeStartMs == 0L) {
                artFadeStartMs = now;
            }
        }
        if (((Boolean)autoColorSetting.getValue()).booleanValue() && ((CharSequence)path).length() > 0 && ver != dominantArtVersion) {
            dominantArtVersion = ver;
            it = object = new Thread(() -> SpotifyModule.refreshArtCache$lambda$2(path), "art-color");
            boolean bl = false;
            ((Thread)it).setDaemon(true);
            ((Thread)object).start();
        }
    }

    private final Pair<Integer, Integer> extractDominantColors(String path) {
        Object object;
        Object $this$extractDominantColors_u24lambda_u240;
        Object object2 = this;
        try {
            Pair pair;
            $this$extractDominantColors_u24lambda_u240 = object2;
            boolean bl = false;
            BufferedImage bufferedImage = ImageIO.read(new File(path));
            if (bufferedImage == null) {
                pair = TuplesKt.to((Object)-14829228, (Object)-6596170);
            } else {
                BufferedImage img = bufferedImage;
                int w = img.getWidth();
                int h = img.getHeight();
                int step = Math.max(1, Math.min(w, h) / 20);
                long rSum = 0L;
                long gSum = 0L;
                long bSum = 0L;
                int count = 0;
                for (int iy = 0; iy < h; iy += step) {
                    for (int ix = 0; ix < w; ix += step) {
                        int rgb = img.getRGB(ix, iy);
                        rSum += (long)(rgb >> 16 & 0xFF);
                        gSum += (long)(rgb >> 8 & 0xFF);
                        bSum += (long)(rgb & 0xFF);
                        ++count;
                    }
                }
                if (count == 0) {
                    pair = TuplesKt.to((Object)-14829228, (Object)-6596170);
                } else {
                    int r = (int)(rSum / (long)count);
                    int g = (int)(gSum / (long)count);
                    int b = (int)(bSum / (long)count);
                    Triple<Integer, Integer, Integer> triple = super.boostSaturation(r, g, b, 1.6f);
                    int bR = ((Number)triple.component1()).intValue();
                    int bG = ((Number)triple.component2()).intValue();
                    int bB = ((Number)triple.component3()).intValue();
                    int c1 = 0xFF000000 | bR << 16 | bG << 8 | bB;
                    int c2 = super.shiftHue(bR, bG, bB, 35.0f);
                    pair = TuplesKt.to((Object)c1, (Object)c2);
                }
            }
            $this$extractDominantColors_u24lambda_u240 = Result.constructor-impl((Object)pair);
        }
        catch (Throwable bl) {
            $this$extractDominantColors_u24lambda_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
        }
        object2 = $this$extractDominantColors_u24lambda_u240;
        Throwable throwable = Result.exceptionOrNull-impl((Object)object2);
        if (throwable == null) {
            object = object2;
        } else {
            Throwable it = throwable;
            boolean bl = false;
            object = TuplesKt.to((Object)-14829228, (Object)-6596170);
        }
        return (Pair)object;
    }

    private final Triple<Integer, Integer, Integer> boostSaturation(int r, int g, int b, float factor) {
        float rf = (float)r / 255.0f;
        float gf = (float)g / 255.0f;
        float bf = (float)b / 255.0f;
        float grey = 0.299f * rf + 0.587f * gf + 0.114f * bf;
        int nr = RangesKt.coerceIn((int)((int)((grey + (rf - grey) * factor) * 255.0f)), (int)0, (int)255);
        int ng = RangesKt.coerceIn((int)((int)((grey + (gf - grey) * factor) * 255.0f)), (int)0, (int)255);
        int nb = RangesKt.coerceIn((int)((int)((grey + (bf - grey) * factor) * 255.0f)), (int)0, (int)255);
        return new Triple((Object)nr, (Object)ng, (Object)nb);
    }

    private final int shiftHue(int r, int g, int b, float degrees) {
        float min;
        float rf = (float)r / 255.0f;
        float gf = (float)g / 255.0f;
        float bf = (float)b / 255.0f;
        float max = Math.max(rf, Math.max(gf, bf));
        float d = max - (min = Math.min(rf, Math.min(gf, bf)));
        if (d < 0.001f) {
            return 0xFF000000 | r << 16 | g << 8 | b;
        }
        float f = max;
        float h = f == rf ? (gf - bf) / d + (gf < bf ? 6.0f : 0.0f) : (f == gf ? (bf - rf) / d + 2.0f : (rf - gf) / d + 4.0f);
        h = ((h / 6.0f + degrees / 360.0f) % 1.0f + 1.0f) % 1.0f;
        float s = d / max;
        float v = max;
        int i = (int)(h * 6.0f);
        float p = v * (1.0f - s);
        float q = v * (1.0f - s * (h * 6.0f - (float)i));
        float t = v * (1.0f - s * (1.0f - (h * 6.0f - (float)i)));
        Triple triple = switch (i % 6) {
            case 0 -> new Triple((Object)Float.valueOf(v), (Object)Float.valueOf(t), (Object)Float.valueOf(p));
            case 1 -> new Triple((Object)Float.valueOf(q), (Object)Float.valueOf(v), (Object)Float.valueOf(p));
            case 2 -> new Triple((Object)Float.valueOf(p), (Object)Float.valueOf(v), (Object)Float.valueOf(t));
            case 3 -> new Triple((Object)Float.valueOf(p), (Object)Float.valueOf(q), (Object)Float.valueOf(v));
            case 4 -> new Triple((Object)Float.valueOf(t), (Object)Float.valueOf(p), (Object)Float.valueOf(v));
            default -> new Triple((Object)Float.valueOf(v), (Object)Float.valueOf(p), (Object)Float.valueOf(q));
        };
        float nr = ((Number)triple.component1()).floatValue();
        float ng = ((Number)triple.component2()).floatValue();
        float nb = ((Number)triple.component3()).floatValue();
        return 0xFF000000 | RangesKt.coerceIn((int)((int)(nr * (float)255)), (int)0, (int)255) << 16 | RangesKt.coerceIn((int)((int)(ng * (float)255)), (int)0, (int)255) << 8 | RangesKt.coerceIn((int)((int)(nb * (float)255)), (int)0, (int)255);
    }

    private final void drawScrollingTitle(String text, float x, float y, float maxW, float size, int color, float dt) {
        float fullW = NVGRenderer.textWidth$default(text, size, null, 4, null);
        if (fullW <= maxW) {
            NVGRenderer.textShadow$default(text, x, y, size, color, null, 32, null);
            return;
        }
        float scrollRange = fullW - maxW;
        float scrollSpeed = 40.0f;
        if (dt > 0.0f) {
            if (titleScrollPause > 0.0f) {
                titleScrollPause -= dt;
            } else if ((titleScrollX += scrollSpeed * dt * titleScrollDir) >= scrollRange) {
                titleScrollX = scrollRange;
                titleScrollDir = -1.0f;
                titleScrollPause = 1.2f;
            } else if (titleScrollX <= 0.0f) {
                titleScrollX = 0.0f;
                titleScrollDir = 1.0f;
                titleScrollPause = 1.2f;
            }
        }
        NVGRenderer.pushScissor(x, y, maxW, size + 2.0f);
        NVGRenderer.textShadow$default(text, x - titleScrollX, y, size, color, null, 32, null);
        NVGRenderer.popScissor();
    }

    private final String truncate(String text, float maxW, float size) {
        if (NVGRenderer.textWidth$default(text, size, null, 4, null) <= maxW) {
            return text;
        }
        String t = text;
        while (((CharSequence)t).length() > 0 && NVGRenderer.textWidth$default(t + "...", size, null, 4, null) > maxW) {
            t = StringsKt.dropLast((String)t, (int)1);
        }
        return t + "...";
    }

    private final int lerpColor(int from, int to, float t) {
        float clamped = RangesKt.coerceIn((float)t, (float)0.0f, (float)1.0f);
        int fa = from >>> 24 & 0xFF;
        int fr = from >>> 16 & 0xFF;
        int fg = from >>> 8 & 0xFF;
        int fb = from & 0xFF;
        int ta = to >>> 24 & 0xFF;
        int tr = to >>> 16 & 0xFF;
        int tg = to >>> 8 & 0xFF;
        int tb = to & 0xFF;
        int a = RangesKt.coerceIn((int)((int)((float)fa + (float)(ta - fa) * clamped)), (int)0, (int)255);
        int r = RangesKt.coerceIn((int)((int)((float)fr + (float)(tr - fr) * clamped)), (int)0, (int)255);
        int g = RangesKt.coerceIn((int)((int)((float)fg + (float)(tg - fg) * clamped)), (int)0, (int)255);
        int b = RangesKt.coerceIn((int)((int)((float)fb + (float)(tb - fb) * clamped)), (int)0, (int)255);
        return a << 24 | r << 16 | g << 8 | b;
    }

    private final float smoothStep(float t) {
        float clamped = RangesKt.coerceIn((float)t, (float)0.0f, (float)1.0f);
        return clamped * clamped * (3.0f - 2.0f * clamped);
    }

    private final int shiftHue(int color, float degrees) {
        return this.shiftHue(color >>> 16 & 0xFF, color >>> 8 & 0xFF, color & 0xFF, degrees);
    }

    private final int withAlpha(int color, int alpha) {
        return RangesKt.coerceIn((int)alpha, (int)0, (int)255) << 24 | color & 0xFFFFFF;
    }

    private final String formatMs(long ms) {
        long total = RangesKt.coerceAtLeast((long)(ms / 1000L), (long)0L);
        String string = "%d:%02d";
        Object[] objectArray = new Object[]{total / 60L, total % 60L};
        String string2 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"format(...)");
        return string2;
    }

    private static final float spotifyHud$lambda$0$0() {
        return 305.0f;
    }

    private static final float spotifyHud$lambda$0$1() {
        return 90.0f;
    }

    private static final Unit spotifyHud$lambda$0$2(float x, float y, float scale) {
        INSTANCE.renderHudContent(x, y, 0.0f, 0.0f, scale, false);
        return Unit.INSTANCE;
    }

    private static final Unit spotifyHud$lambda$0(HudElementBuilder $this$hudElement) {
        Intrinsics.checkNotNullParameter((Object)$this$hudElement, (String)"$this$hudElement");
        $this$hudElement.setAnchor(HudAnchor.BOTTOM_LEFT);
        $this$hudElement.setOffsetX(10.0f);
        $this$hudElement.setOffsetY(10.0f);
        $this$hudElement.width((Function0<Float>)((Function0)SpotifyModule::spotifyHud$lambda$0$0));
        $this$hudElement.height((Function0<Float>)((Function0)SpotifyModule::spotifyHud$lambda$0$1));
        $this$hudElement.render((Function3<? super Float, ? super Float, ? super Float, Unit>)((Function3)SpotifyModule::spotifyHud$lambda$0$2));
        return Unit.INSTANCE;
    }

    private static final boolean onMouseClick$hitsBtn(float lmx, float lmy, float bx) {
        return lmx >= bx && lmx <= bx + 32.0f && lmy >= 54.0f && lmy <= 72.0f;
    }

    private static final void refreshArtCache$lambda$2(String $path) {
        Pair<Integer, Integer> pair = INSTANCE.extractDominantColors($path);
        int dc1 = ((Number)pair.component1()).intValue();
        int dc2 = ((Number)pair.component2()).intValue();
        artDominantC1 = dc1;
        artDominantC2 = dc2;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        color1Setting = new TextSetting("Color 1", "Gradient start color (hex, no #).", "1DB954");
        color2Setting = new TextSetting("Color 2", "Gradient end color (hex, no #).", "9B59B6");
        Object[] objectArray = new String[]{"Left->Right", "Top->Bottom"};
        gradientDirectionSetting = new ModeSetting("Gradient", "Progress bar gradient direction.", 0, (String[])objectArray);
        autoColorSetting = new CheckboxSetting("Auto Color", "Derive gradient color from album art.", true);
        glowSetting = new CheckboxSetting("Glow", "Animated glow border.", true);
        particlesSetting = new CheckboxSetting("Particles", "Sparkle particle effects.", true);
        showTimeSetting = new CheckboxSetting("Show Time", "Show elapsed / total time.", true);
        waveformSetting = new CheckboxSetting("Waveform", "Show a Spotify audio-reactive waveform.", true);
        artDominantC1 = -14829228;
        artDominantC2 = -6596170;
        dominantArtVersion = -1;
        cachedArtVersion = -1;
        lastTrackName = "";
        lastRenderMs = System.currentTimeMillis();
        titleScrollDir = 1.0f;
        waveformHistory = new float[18];
        waveformAdaptivePeak = 0.08f;
        spotifyHud = HudModuleDSLKt.hudElement(INSTANCE, "spotify-now-playing", "Spotify Now Playing", "Spotify currently playing HUD", (Function1<? super HudElementBuilder, Unit>)((Function1)SpotifyModule::spotifyHud$lambda$0));
        objectArray = new Setting[]{color1Setting, color2Setting, gradientDirectionSetting, autoColorSetting, glowSetting, particlesSetting, showTimeSetting, waveformSetting};
        INSTANCE.addSetting((Setting<?>[])objectArray);
        EventBus.register(INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\n\b\u0082\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0004\u0012\u0006\u0010\b\u001a\u00020\u0004\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\u000eJ\u0010\u0010\u0010\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u000eJ\u0010\u0010\u0011\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u000eJB\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u00042\b\b\u0002\u0010\b\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u001b\u0010\u0016\u001a\u00020\u00152\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0018\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0018\u0010\fJ\u0011\u0010\u001a\u001a\u00020\u0019H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001a\u0010\u001bR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001c\u001a\u0004\b\u001d\u0010\fR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001e\u001a\u0004\b\u001f\u0010\u000eR\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001e\u001a\u0004\b \u0010\u000eR\u0017\u0010\u0007\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u001e\u001a\u0004\b!\u0010\u000eR\u0017\u0010\b\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\b\u0010\u001e\u001a\u0004\b\"\u0010\u000e\u00a8\u0006#"}, d2={"Lorg/cobalt/internal/spotify/SpotifyModule$WaveLayerSpec;", "", "", "color", "", "phaseOffset", "frequency", "amplitudeScale", "shimmerScale", "<init>", "(IFFFF)V", "component1", "()I", "component2", "()F", "component3", "component4", "component5", "copy", "(IFFFF)Lorg/cobalt/internal/spotify/SpotifyModule$WaveLayerSpec;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getColor", "F", "getPhaseOffset", "getFrequency", "getAmplitudeScale", "getShimmerScale", "cobalt"})
    private static final class WaveLayerSpec {
        private final int color;
        private final float phaseOffset;
        private final float frequency;
        private final float amplitudeScale;
        private final float shimmerScale;

        public WaveLayerSpec(int color, float phaseOffset, float frequency, float amplitudeScale, float shimmerScale) {
            this.color = color;
            this.phaseOffset = phaseOffset;
            this.frequency = frequency;
            this.amplitudeScale = amplitudeScale;
            this.shimmerScale = shimmerScale;
        }

        public final int getColor() {
            return this.color;
        }

        public final float getPhaseOffset() {
            return this.phaseOffset;
        }

        public final float getFrequency() {
            return this.frequency;
        }

        public final float getAmplitudeScale() {
            return this.amplitudeScale;
        }

        public final float getShimmerScale() {
            return this.shimmerScale;
        }

        public final int component1() {
            return this.color;
        }

        public final float component2() {
            return this.phaseOffset;
        }

        public final float component3() {
            return this.frequency;
        }

        public final float component4() {
            return this.amplitudeScale;
        }

        public final float component5() {
            return this.shimmerScale;
        }

        @NotNull
        public final WaveLayerSpec copy(int color, float phaseOffset, float frequency, float amplitudeScale, float shimmerScale) {
            return new WaveLayerSpec(color, phaseOffset, frequency, amplitudeScale, shimmerScale);
        }

        public static /* synthetic */ WaveLayerSpec copy$default(WaveLayerSpec waveLayerSpec, int n, float f, float f2, float f3, float f4, int n2, Object object) {
            if ((n2 & 1) != 0) {
                n = waveLayerSpec.color;
            }
            if ((n2 & 2) != 0) {
                f = waveLayerSpec.phaseOffset;
            }
            if ((n2 & 4) != 0) {
                f2 = waveLayerSpec.frequency;
            }
            if ((n2 & 8) != 0) {
                f3 = waveLayerSpec.amplitudeScale;
            }
            if ((n2 & 0x10) != 0) {
                f4 = waveLayerSpec.shimmerScale;
            }
            return waveLayerSpec.copy(n, f, f2, f3, f4);
        }

        @NotNull
        public String toString() {
            return "WaveLayerSpec(color=" + this.color + ", phaseOffset=" + this.phaseOffset + ", frequency=" + this.frequency + ", amplitudeScale=" + this.amplitudeScale + ", shimmerScale=" + this.shimmerScale + ")";
        }

        public int hashCode() {
            int result = Integer.hashCode(this.color);
            result = result * 31 + Float.hashCode(this.phaseOffset);
            result = result * 31 + Float.hashCode(this.frequency);
            result = result * 31 + Float.hashCode(this.amplitudeScale);
            result = result * 31 + Float.hashCode(this.shimmerScale);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof WaveLayerSpec)) {
                return false;
            }
            WaveLayerSpec waveLayerSpec = (WaveLayerSpec)other;
            if (this.color != waveLayerSpec.color) {
                return false;
            }
            if (Float.compare(this.phaseOffset, waveLayerSpec.phaseOffset) != 0) {
                return false;
            }
            if (Float.compare(this.frequency, waveLayerSpec.frequency) != 0) {
                return false;
            }
            if (Float.compare(this.amplitudeScale, waveLayerSpec.amplitudeScale) != 0) {
                return false;
            }
            return Float.compare(this.shimmerScale, waveLayerSpec.shimmerScale) == 0;
        }
    }
}

