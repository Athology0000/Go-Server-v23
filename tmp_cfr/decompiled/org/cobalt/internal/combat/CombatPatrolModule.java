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
 *  kotlin.Lazy
 *  kotlin.LazyKt
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.Pair
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.TuplesKt
 *  kotlin.Unit
 *  kotlin.collections.ArraysKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.io.FilesKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_2382
 *  net.minecraft.class_239
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.combat;

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
import java.util.Iterator;
import java.util.List;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.MouseEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.ActionSetting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.InfoSetting;
import org.cobalt.api.module.setting.impl.InfoType;
import org.cobalt.api.module.setting.impl.ModeSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.module.setting.impl.TextSetting;
import org.cobalt.api.pathfinder.jni.NativePathfinder;
import org.cobalt.api.pathfinder.jni.PathCommand;
import org.cobalt.api.pathfinder.jni.PathStatus;
import org.cobalt.api.pathfinder.minecraft.MinecraftPathingRules;
import org.cobalt.api.rotation.RotationExecutor;
import org.cobalt.api.util.AngleUtils;
import org.cobalt.api.util.ChatUtils;
import org.cobalt.api.util.InventoryUtils;
import org.cobalt.api.util.helper.Rotation;
import org.cobalt.api.util.player.MovementManager;
import org.cobalt.api.util.render.Render3D;
import org.cobalt.internal.combat.CombatPatrolPoint;
import org.cobalt.internal.combat.CombatPatrolPointType;
import org.cobalt.internal.etherwarp.EtherwarpLogic;
import org.cobalt.internal.pathfinding.PathfindingModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00de\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0002\b\u001c\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010!\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0010\t\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b!\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0002\u00d6\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\n\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\tH\u0007\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0017\u0010\f\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\f\u0010\u000bJ\u0017\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\rH\u0007\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0015\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u0011\u001a\u00020\u0010\u00a2\u0006\u0004\b\u0012\u0010\u0013J\r\u0010\u0014\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0014\u0010\u0003J\u000f\u0010\u0015\u001a\u00020\u0010H\u0002\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u000f\u0010\u0017\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u0017\u0010\u0003J\u0017\u0010\u001b\u001a\u00020\u001a2\u0006\u0010\u0019\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b\u001b\u0010\u001cJ\u0017\u0010\u001e\u001a\u00020\u001d2\u0006\u0010\u0019\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b\u001e\u0010\u001fJ\u000f\u0010!\u001a\u00020\u0006H\u0000\u00a2\u0006\u0004\b \u0010\u0003J\u000f\u0010#\u001a\u00020\u0006H\u0000\u00a2\u0006\u0004\b\"\u0010\u0003J\u000f\u0010%\u001a\u00020\u0006H\u0000\u00a2\u0006\u0004\b$\u0010\u0003J\r\u0010&\u001a\u00020\u0006\u00a2\u0006\u0004\b&\u0010\u0003J\u0017\u0010(\u001a\u00020\u00062\b\b\u0002\u0010'\u001a\u00020\u0018\u00a2\u0006\u0004\b(\u0010)J\u000f\u0010*\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b*\u0010\u0003J\u0017\u0010/\u001a\u00020\u00062\u0006\u0010,\u001a\u00020+H\u0000\u00a2\u0006\u0004\b-\u0010.J\u000f\u00101\u001a\u000200H\u0002\u00a2\u0006\u0004\b1\u00102J\r\u00103\u001a\u00020\u0006\u00a2\u0006\u0004\b3\u0010\u0003J\r\u00104\u001a\u00020\u0006\u00a2\u0006\u0004\b4\u0010\u0003J\r\u00105\u001a\u00020\u0006\u00a2\u0006\u0004\b5\u0010\u0003J\u0017\u00106\u001a\u00020\u00062\u0006\u0010,\u001a\u00020+H\u0002\u00a2\u0006\u0004\b6\u0010.J\u000f\u00107\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b7\u0010\u0003J\u001f\u0010<\u001a\u00020\u00062\u0006\u00109\u001a\u0002082\u0006\u0010;\u001a\u00020:H\u0002\u00a2\u0006\u0004\b<\u0010=J\u001f\u0010@\u001a\u00020\u001a2\u0006\u00109\u001a\u0002082\u0006\u0010?\u001a\u00020>H\u0002\u00a2\u0006\u0004\b@\u0010AJ+\u0010E\u001a\u000e\u0012\u0004\u0012\u00020D\u0012\u0004\u0012\u00020D0C2\u0006\u00109\u001a\u0002082\u0006\u0010?\u001a\u00020BH\u0002\u00a2\u0006\u0004\bE\u0010FJ'\u0010G\u001a\u00020B2\u0006\u0010;\u001a\u00020:2\u0006\u00109\u001a\u0002082\u0006\u0010?\u001a\u00020>H\u0002\u00a2\u0006\u0004\bG\u0010HJ\u000f\u0010I\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bI\u0010\u0003J\u000f\u0010J\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bJ\u0010\u0003J!\u0010L\u001a\u0004\u0018\u00010>2\u0006\u0010;\u001a\u00020:2\u0006\u0010K\u001a\u00020>H\u0002\u00a2\u0006\u0004\bL\u0010MJ!\u0010O\u001a\u0004\u0018\u00010>2\u0006\u0010;\u001a\u00020:2\u0006\u0010N\u001a\u00020>H\u0002\u00a2\u0006\u0004\bO\u0010MJ\u001f\u0010Q\u001a\u00020\u001a2\u0006\u0010;\u001a\u00020:2\u0006\u0010P\u001a\u00020>H\u0002\u00a2\u0006\u0004\bQ\u0010RJ3\u0010T\u001a\u00020\u001a2\u0006\u0010;\u001a\u00020:2\u0006\u00109\u001a\u0002082\u0006\u0010?\u001a\u00020>2\n\b\u0002\u0010S\u001a\u0004\u0018\u00010BH\u0002\u00a2\u0006\u0004\bT\u0010UJ\u001f\u0010V\u001a\u00020\u001a2\u0006\u00109\u001a\u0002082\u0006\u0010?\u001a\u00020>H\u0002\u00a2\u0006\u0004\bV\u0010AJ'\u0010W\u001a\u00020\u001a2\u0006\u0010;\u001a\u00020:2\u0006\u00109\u001a\u0002082\u0006\u0010?\u001a\u00020>H\u0002\u00a2\u0006\u0004\bW\u0010XJ/\u0010Y\u001a\u00020\u001a2\u0006\u0010;\u001a\u00020:2\u0006\u00109\u001a\u0002082\u0006\u0010?\u001a\u00020>2\u0006\u0010,\u001a\u00020BH\u0002\u00a2\u0006\u0004\bY\u0010UJ'\u0010]\u001a\u00020B2\u0006\u0010Z\u001a\u00020B2\u0006\u0010[\u001a\u00020B2\u0006\u0010\\\u001a\u00020DH\u0002\u00a2\u0006\u0004\b]\u0010^J/\u0010c\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\t2\u0006\u0010_\u001a\u00020B2\u0006\u0010`\u001a\u00020D2\u0006\u0010b\u001a\u00020aH\u0002\u00a2\u0006\u0004\bc\u0010dR\u0014\u0010f\u001a\u00020e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bf\u0010gR\u001c\u0010j\u001a\n i*\u0004\u0018\u00010h0h8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bj\u0010kR\u001b\u0010p\u001a\u00020\u001d8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\bl\u0010m\u001a\u0004\bn\u0010oR \u0010r\u001a\b\u0012\u0004\u0012\u00020+0q8\u0000X\u0080\u0004\u00a2\u0006\f\n\u0004\br\u0010s\u001a\u0004\bt\u0010uR$\u0010x\u001a\u00020v2\u0006\u0010w\u001a\u00020v8\u0006@BX\u0086\u000e\u00a2\u0006\f\n\u0004\bx\u0010y\u001a\u0004\bz\u0010{R$\u0010|\u001a\u00020\u001a2\u0006\u0010w\u001a\u00020\u001a8\u0006@BX\u0086\u000e\u00a2\u0006\f\n\u0004\b|\u0010}\u001a\u0004\b~\u0010\u007fR\u0018\u0010\u0080\u0001\u001a\u00020\u001a8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0080\u0001\u0010}R\u0019\u0010\u0081\u0001\u001a\u0002008\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0081\u0001\u0010\u0082\u0001R\u0019\u0010\u0083\u0001\u001a\u0002008\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0083\u0001\u0010\u0082\u0001R\u0018\u0010\u0084\u0001\u001a\u00020\u001a8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0084\u0001\u0010}R\u0018\u0010\u0085\u0001\u001a\u00020\u001a8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0085\u0001\u0010}R\u001b\u0010\u0086\u0001\u001a\u0004\u0018\u00010>8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0086\u0001\u0010\u0087\u0001R\u0019\u0010\u0088\u0001\u001a\u0002008\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0088\u0001\u0010\u0082\u0001R\u001b\u0010\u0089\u0001\u001a\u0004\u0018\u00010+8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0089\u0001\u0010\u008a\u0001R\u0019\u0010\u008b\u0001\u001a\u00020D8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u008b\u0001\u0010\u008c\u0001R\u001a\u0010\u008e\u0001\u001a\u00030\u008d\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u008e\u0001\u0010\u008f\u0001R\u001a\u0010\u0090\u0001\u001a\u00030\u008d\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0090\u0001\u0010\u008f\u0001R\u001a\u0010\u0091\u0001\u001a\u00030\u008d\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0091\u0001\u0010\u008f\u0001R\u0019\u0010\u0092\u0001\u001a\u0002008\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0092\u0001\u0010\u0082\u0001R\u001b\u0010\u0093\u0001\u001a\u0004\u0018\u00010>8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0093\u0001\u0010\u0087\u0001R\u001a\u0010\u0094\u0001\u001a\u00030\u008d\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0094\u0001\u0010\u008f\u0001R\u001d\u0010\u0096\u0001\u001a\u00030\u0095\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u0096\u0001\u0010\u0097\u0001\u001a\u0006\b\u0098\u0001\u0010\u0099\u0001R \u0010\u009b\u0001\u001a\u00030\u009a\u00018\u0000X\u0080\u0004\u00a2\u0006\u0010\n\u0006\b\u009b\u0001\u0010\u009c\u0001\u001a\u0006\b\u009d\u0001\u0010\u009e\u0001R\u0018\u0010\u00a0\u0001\u001a\u00030\u009f\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a0\u0001\u0010\u00a1\u0001R\u0018\u0010\u00a2\u0001\u001a\u00030\u009f\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a2\u0001\u0010\u00a1\u0001R\u0018\u0010\u00a3\u0001\u001a\u00030\u0095\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a3\u0001\u0010\u0097\u0001R\u001d\u0010\u00a4\u0001\u001a\u00030\u0095\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00a4\u0001\u0010\u0097\u0001\u001a\u0006\b\u00a5\u0001\u0010\u0099\u0001R\u001d\u0010\u00a6\u0001\u001a\u00030\u0095\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00a6\u0001\u0010\u0097\u0001\u001a\u0006\b\u00a7\u0001\u0010\u0099\u0001R\u001d\u0010\u00a9\u0001\u001a\u00030\u00a8\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00a9\u0001\u0010\u00aa\u0001\u001a\u0006\b\u00ab\u0001\u0010\u00ac\u0001R\u001d\u0010\u00ae\u0001\u001a\u00030\u00ad\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00ae\u0001\u0010\u00af\u0001\u001a\u0006\b\u00b0\u0001\u0010\u00b1\u0001R\u001d\u0010\u00b2\u0001\u001a\u00030\u00ad\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00b2\u0001\u0010\u00af\u0001\u001a\u0006\b\u00b3\u0001\u0010\u00b1\u0001R\u001d\u0010\u00b4\u0001\u001a\u00030\u00a8\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00b4\u0001\u0010\u00aa\u0001\u001a\u0006\b\u00b5\u0001\u0010\u00ac\u0001R\u0018\u0010\u00b7\u0001\u001a\u00030\u00b6\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00b7\u0001\u0010\u00b8\u0001R\u0018\u0010\u00b9\u0001\u001a\u00030\u00b6\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00b9\u0001\u0010\u00b8\u0001R\u0018\u0010\u00ba\u0001\u001a\u00030\u00b6\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00ba\u0001\u0010\u00b8\u0001R\u0018\u0010\u00bb\u0001\u001a\u00030\u00b6\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00bb\u0001\u0010\u00b8\u0001R\u0018\u0010\u00bc\u0001\u001a\u00030\u00b6\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00bc\u0001\u0010\u00b8\u0001R\u0018\u0010\u00bd\u0001\u001a\u00030\u00b6\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00bd\u0001\u0010\u00b8\u0001R\u0018\u0010\u00be\u0001\u001a\u00030\u00b6\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00be\u0001\u0010\u00b8\u0001R\u0013\u0010\u00bf\u0001\u001a\u00020\u001a8F\u00a2\u0006\u0007\u001a\u0005\b\u00bf\u0001\u0010\u007fR\u0016\u0010\u00c2\u0001\u001a\u0004\u0018\u00010+8F\u00a2\u0006\b\u001a\u0006\b\u00c0\u0001\u0010\u00c1\u0001R\u0014\u0010\u00c5\u0001\u001a\u00020D8F\u00a2\u0006\b\u001a\u0006\b\u00c3\u0001\u0010\u00c4\u0001R\u0017\u0010\u00c6\u0001\u001a\u00020D8\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00c6\u0001\u0010\u008c\u0001R\u0017\u0010\u00c7\u0001\u001a\u00020D8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00c7\u0001\u0010\u008c\u0001R\u0017\u0010\u00c8\u0001\u001a\u00020D8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00c8\u0001\u0010\u008c\u0001R\u0017\u0010\u00c9\u0001\u001a\u00020D8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00c9\u0001\u0010\u008c\u0001R\u0017\u0010\u00ca\u0001\u001a\u00020D8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00ca\u0001\u0010\u008c\u0001R\u0017\u0010\u00cb\u0001\u001a\u00020D8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00cb\u0001\u0010\u008c\u0001R\u0017\u0010\u00cc\u0001\u001a\u00020D8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00cc\u0001\u0010\u008c\u0001R\u0017\u0010\u00cd\u0001\u001a\u00020D8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00cd\u0001\u0010\u008c\u0001R\u0017\u0010\u00ce\u0001\u001a\u00020D8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00ce\u0001\u0010\u008c\u0001R\u0018\u0010\u00cf\u0001\u001a\u00030\u008d\u00018\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00cf\u0001\u0010\u008f\u0001R\u0018\u0010\u00d0\u0001\u001a\u00030\u008d\u00018\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00d0\u0001\u0010\u008f\u0001R\u0017\u0010\u00d1\u0001\u001a\u00020D8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00d1\u0001\u0010\u008c\u0001R\u0018\u0010\u00d2\u0001\u001a\u00030\u008d\u00018\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00d2\u0001\u0010\u008f\u0001R\u0017\u0010\u00d3\u0001\u001a\u0002008\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00d3\u0001\u0010\u0082\u0001R\u0017\u0010\u00d4\u0001\u001a\u0002008\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00d4\u0001\u0010\u0082\u0001R\u0017\u0010\u00d5\u0001\u001a\u00020D8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00d5\u0001\u0010\u008c\u0001\u00a8\u0006\u00d7\u0001"}, d2={"Lorg/cobalt/internal/combat/CombatPatrolModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "onRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "renderRoute", "Lorg/cobalt/api/event/impl/client/MouseEvent$RightClick;", "onRightClick", "(Lorg/cobalt/api/event/impl/client/MouseEvent$RightClick;)V", "Lorg/cobalt/internal/combat/CombatPatrolPointType;", "type", "applyPickedType", "(Lorg/cobalt/internal/combat/CombatPatrolPointType;)V", "cancelPendingPick", "currentPointType", "()Lorg/cobalt/internal/combat/CombatPatrolPointType;", "addPointFromPlayer", "", "name", "", "isValidName", "(Ljava/lang/String;)Z", "Ljava/io/File;", "routeFile", "(Ljava/lang/String;)Ljava/io/File;", "saveRoute$cobalt", "saveRoute", "loadRoute$cobalt", "loadRoute", "updatePointsInfo$cobalt", "updatePointsInfo", "startPatrol", "msg", "stopPatrol", "(Ljava/lang/String;)V", "advanceAndNavigate", "Lorg/cobalt/internal/combat/CombatPatrolPoint;", "point", "navigateTo$cobalt", "(Lorg/cobalt/internal/combat/CombatPatrolPoint;)V", "navigateTo", "", "findNearestIndex", "()I", "onCombatInterrupt", "onCombatResume", "onKillZoneCleared", "startWarpPoint", "cancelWarp", "Lnet/minecraft/class_1657;", "player", "Lnet/minecraft/class_1937;", "level", "handleWarp", "(Lnet/minecraft/class_1657;Lnet/minecraft/class_1937;)V", "Lnet/minecraft/class_2338;", "target", "hasArrived", "(Lnet/minecraft/class_1657;Lnet/minecraft/class_2338;)Z", "Lnet/minecraft/class_243;", "Lkotlin/Pair;", "", "applyWarpHeadRotation", "(Lnet/minecraft/class_1657;Lnet/minecraft/class_243;)Lkotlin/Pair;", "resolveWarpAimPoint", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_1657;Lnet/minecraft/class_2338;)Lnet/minecraft/class_243;", "advanceWarpFrameTime", "resetWarpStageTimer", "rawPoint", "resolveWarpPoint", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)Lnet/minecraft/class_2338;", "pos", "candidateWarpBlock", "block", "isWarpBlockViable", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)Z", "aimPoint", "canWarpToTarget", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_1657;Lnet/minecraft/class_2338;Lnet/minecraft/class_243;)Z", "isStandingOnWarpTarget", "wasJustWarpedToTarget", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_1657;Lnet/minecraft/class_2338;)Z", "hasLineOfSight", "from", "to", "t", "blendVec3", "(Lnet/minecraft/class_243;Lnet/minecraft/class_243;D)Lnet/minecraft/class_243;", "center", "radius", "Ljava/awt/Color;", "color", "drawSphereRings", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;Lnet/minecraft/class_243;DLjava/awt/Color;)V", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lcom/google/gson/Gson;", "kotlin.jvm.PlatformType", "gson", "Lcom/google/gson/Gson;", "patrolDir$delegate", "Lkotlin/Lazy;", "getPatrolDir", "()Ljava/io/File;", "patrolDir", "", "patrolPoints", "Ljava/util/List;", "getPatrolPoints$cobalt", "()Ljava/util/List;", "Lorg/cobalt/internal/combat/CombatPatrolModule$PatrolState;", "value", "patrolState", "Lorg/cobalt/internal/combat/CombatPatrolModule$PatrolState;", "getPatrolState", "()Lorg/cobalt/internal/combat/CombatPatrolModule$PatrolState;", "patrolOwnsPathfinder", "Z", "getPatrolOwnsPathfinder", "()Z", "patrolRunning", "routeIndex", "I", "killZoneClearTicks", "killZoneClearedThisTick", "navPathActivated", "pendingClickPos", "Lnet/minecraft/class_2338;", "warpStage", "warpTargetPoint", "Lorg/cobalt/internal/combat/CombatPatrolPoint;", "warpStageElapsedMs", "D", "", "warpStageLastNs", "J", "warpLookLastNs", "warpCooldownUntil", "warpRestoreSlot", "lastSuccessfulWarpTarget", "lastSuccessfulWarpTick", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "getEnabled", "()Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "routeName", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "getRouteName$cobalt", "()Lorg/cobalt/api/module/setting/impl/TextSetting;", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "pointsInfo", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "statusInfo", "recordOnRightClick", "loopRoute", "getLoopRoute", "startFromNearest", "getStartFromNearest", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "pointType", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "getPointType", "()Lorg/cobalt/api/module/setting/impl/ModeSetting;", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "killZoneRadius", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "getKillZoneRadius", "()Lorg/cobalt/api/module/setting/impl/SliderSetting;", "killZoneDwellTicks", "getKillZoneDwellTicks", "aotvSlot", "getAotvSlot", "Lorg/cobalt/api/module/setting/impl/ActionSetting;", "addPointAction", "Lorg/cobalt/api/module/setting/impl/ActionSetting;", "removeLastAction", "clearRouteAction", "saveRouteAction", "loadRouteAction", "startPatrolAction", "stopPatrolAction", "isPatrolRunning", "getCurrentKillPoint", "()Lorg/cobalt/internal/combat/CombatPatrolPoint;", "currentKillPoint", "getKillZoneRadiusValue", "()D", "killZoneRadiusValue", "TWO_PI", "NAV_TRIVIAL_DIST_SQ", "WARP_AIM_TOLERANCE", "WARP_LOOK_YAW_SPEED_DPS", "WARP_LOOK_PITCH_SPEED_DPS", "WARP_ALIGN_MS", "WARP_SNEAK_MS", "WARP_POST_MS", "WARP_STAGE1_TIMEOUT_MS", "WARP_RETRY_COOLDOWN_TICKS", "WARP_COOLDOWN_TICKS", "WARP_TOTAL_TIMEOUT_MS", "WARP_REPEAT_BLOCK_SUPPRESS_TICKS", "WARP_RESOLVE_RADIUS", "WARP_RESOLVE_VERTICAL", "ARRIVAL_DISTANCE_SQ", "PatrolState", "cobalt"})
@SourceDebugExtension(value={"SMAP\nCombatPatrolModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CombatPatrolModule.kt\norg/cobalt/internal/combat/CombatPatrolModule\n+ 2 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,839:1\n1874#2,2:840\n1915#3,2:842\n1915#3,2:845\n2469#3,14:847\n1#4:844\n*S KotlinDebug\n*F\n+ 1 CombatPatrolModule.kt\norg/cobalt/internal/combat/CombatPatrolModule\n*L\n317#1:840,2\n329#1:842,2\n355#1:845,2\n436#1:847,14\n*E\n"})
public final class CombatPatrolModule
extends Module {
    @NotNull
    public static final CombatPatrolModule INSTANCE = new CombatPatrolModule();
    @NotNull
    private static final class_310 mc;
    private static final Gson gson;
    @NotNull
    private static final Lazy patrolDir$delegate;
    @NotNull
    private static final List<CombatPatrolPoint> patrolPoints;
    @NotNull
    private static PatrolState patrolState;
    private static boolean patrolOwnsPathfinder;
    private static boolean patrolRunning;
    private static int routeIndex;
    private static int killZoneClearTicks;
    private static boolean killZoneClearedThisTick;
    private static boolean navPathActivated;
    @Nullable
    private static class_2338 pendingClickPos;
    private static int warpStage;
    @Nullable
    private static CombatPatrolPoint warpTargetPoint;
    private static double warpStageElapsedMs;
    private static long warpStageLastNs;
    private static long warpLookLastNs;
    private static long warpCooldownUntil;
    private static int warpRestoreSlot;
    @Nullable
    private static class_2338 lastSuccessfulWarpTarget;
    private static long lastSuccessfulWarpTick;
    @NotNull
    private static final CheckboxSetting enabled;
    @NotNull
    private static final TextSetting routeName;
    @NotNull
    private static final InfoSetting pointsInfo;
    @NotNull
    private static final InfoSetting statusInfo;
    @NotNull
    private static final CheckboxSetting recordOnRightClick;
    @NotNull
    private static final CheckboxSetting loopRoute;
    @NotNull
    private static final CheckboxSetting startFromNearest;
    @NotNull
    private static final ModeSetting pointType;
    @NotNull
    private static final SliderSetting killZoneRadius;
    @NotNull
    private static final SliderSetting killZoneDwellTicks;
    @NotNull
    private static final ModeSetting aotvSlot;
    @NotNull
    private static final ActionSetting addPointAction;
    @NotNull
    private static final ActionSetting removeLastAction;
    @NotNull
    private static final ActionSetting clearRouteAction;
    @NotNull
    private static final ActionSetting saveRouteAction;
    @NotNull
    private static final ActionSetting loadRouteAction;
    @NotNull
    private static final ActionSetting startPatrolAction;
    @NotNull
    private static final ActionSetting stopPatrolAction;
    private static final double TWO_PI;
    private static final double NAV_TRIVIAL_DIST_SQ = 4.0;
    private static final double WARP_AIM_TOLERANCE = 6.0;
    private static final double WARP_LOOK_YAW_SPEED_DPS = 360.0;
    private static final double WARP_LOOK_PITCH_SPEED_DPS = 300.0;
    private static final double WARP_ALIGN_MS = 170.0;
    private static final double WARP_SNEAK_MS = 85.0;
    private static final double WARP_POST_MS = 70.0;
    private static final double WARP_STAGE1_TIMEOUT_MS = 240.0;
    private static final long WARP_RETRY_COOLDOWN_TICKS = 4L;
    private static final long WARP_COOLDOWN_TICKS = 1L;
    private static final double WARP_TOTAL_TIMEOUT_MS = 765.0;
    private static final long WARP_REPEAT_BLOCK_SUPPRESS_TICKS = 10L;
    private static final int WARP_RESOLVE_RADIUS = 2;
    private static final int WARP_RESOLVE_VERTICAL = 2;
    private static final double ARRIVAL_DISTANCE_SQ = 36.0;

    private CombatPatrolModule() {
        super("Combat Patrol");
    }

    private final File getPatrolDir() {
        Lazy lazy = patrolDir$delegate;
        return (File)lazy.getValue();
    }

    @NotNull
    public final List<CombatPatrolPoint> getPatrolPoints$cobalt() {
        return patrolPoints;
    }

    @NotNull
    public final PatrolState getPatrolState() {
        return patrolState;
    }

    public final boolean getPatrolOwnsPathfinder() {
        return patrolOwnsPathfinder;
    }

    @NotNull
    public final CheckboxSetting getEnabled() {
        return enabled;
    }

    @NotNull
    public final TextSetting getRouteName$cobalt() {
        return routeName;
    }

    @NotNull
    public final CheckboxSetting getLoopRoute() {
        return loopRoute;
    }

    @NotNull
    public final CheckboxSetting getStartFromNearest() {
        return startFromNearest;
    }

    @NotNull
    public final ModeSetting getPointType() {
        return pointType;
    }

    @NotNull
    public final SliderSetting getKillZoneRadius() {
        return killZoneRadius;
    }

    @NotNull
    public final SliderSetting getKillZoneDwellTicks() {
        return killZoneDwellTicks;
    }

    @NotNull
    public final ModeSetting getAotvSlot() {
        return aotvSlot;
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        this.updatePointsInfo$cobalt();
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            if (patrolRunning) {
                this.stopPatrol("Patrol module disabled.");
            }
            return;
        }
        if (patrolPoints.isEmpty()) {
            if (patrolRunning) {
                this.stopPatrol("No patrol points.");
            }
            return;
        }
        if (!patrolRunning) {
            return;
        }
        switch (WhenMappings.$EnumSwitchMapping$0[patrolState.ordinal()]) {
            case 1: {
                class_638 level2;
                boolean confirmedArrival;
                double d;
                PathStatus s;
                PathCommand cmd;
                PathCommand pathCommand = cmd = NativePathfinder.INSTANCE.tick();
                if (pathCommand != null) {
                    pathCommand.applyToPlayer();
                } else {
                    MovementManager.clearForcedMovement();
                }
                if (cmd != null) {
                    navPathActivated = true;
                }
                if ((s = NativePathfinder.INSTANCE.getStatus()) != PathStatus.ARRIVED && s != PathStatus.FAILED) break;
                CombatPatrolPoint current = (CombatPatrolPoint)CollectionsKt.getOrNull(patrolPoints, (int)routeIndex);
                class_746 player = CombatPatrolModule.mc.field_1724;
                if (player != null && current != null) {
                    double dx = player.method_23317() - ((double)current.getX() + 0.5);
                    double dy = player.method_23318() - (double)current.getY();
                    double dz = player.method_23321() - ((double)current.getZ() + 0.5);
                    d = dx * dx + dy * dy + dz * dz;
                } else {
                    d = Double.MAX_VALUE;
                }
                double distSq = d;
                boolean bl = confirmedArrival = s == PathStatus.FAILED || navPathActivated || distSq <= 4.0;
                if (confirmedArrival) {
                    navPathActivated = false;
                    if (s == PathStatus.FAILED) {
                        ChatUtils.sendMessage("Patrol: pathfinding failed at point " + (routeIndex + 1) + ", skipping.");
                    }
                    CombatPatrolPoint combatPatrolPoint = current;
                    if ((combatPatrolPoint != null ? combatPatrolPoint.getType() : null) == CombatPatrolPointType.KILL) {
                        NativePathfinder.INSTANCE.stop();
                        patrolOwnsPathfinder = false;
                        patrolState = PatrolState.AT_KILL_ZONE;
                        killZoneClearTicks = 0;
                        break;
                    }
                    this.advanceAndNavigate();
                    break;
                }
                if (current == null || (level2 = CombatPatrolModule.mc.field_1687) == null) break;
                class_2338 class_23382 = MinecraftPathingRules.INSTANCE.resolveTarget((class_1937)level2, new class_2338(current.getX(), current.getY(), current.getZ()));
                if (class_23382 == null) {
                    class_23382 = new class_2338(current.getX(), current.getY(), current.getZ());
                }
                class_2338 resolved = class_23382;
                NativePathfinder.INSTANCE.setTarget((double)resolved.method_10263() + 0.5, resolved.method_10264(), (double)resolved.method_10260() + 0.5);
                break;
            }
            case 2: {
                if (!(warpStageElapsedMs > 765.0)) break;
                ChatUtils.sendMessage("Patrol: warp timed out at point " + (routeIndex + 1) + ", skipping.");
                this.cancelWarp();
                this.advanceAndNavigate();
                break;
            }
            case 3: {
                if (killZoneClearedThisTick) {
                    int n = killZoneClearTicks;
                    if ((killZoneClearTicks = n + 1) >= (int)((Number)killZoneDwellTicks.getValue()).doubleValue()) {
                        killZoneClearTicks = 0;
                        this.advanceAndNavigate();
                    }
                } else {
                    killZoneClearTicks = 0;
                }
                killZoneClearedThisTick = false;
                break;
            }
            case 4: 
            case 5: {
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
        statusInfo.setValue(patrolState.name());
    }

    @SubscribeEvent
    public final void onRender(@NotNull WorldRenderEvent.Last event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (patrolRunning && patrolState == PatrolState.WARPING && CombatPatrolModule.mc.field_1755 == null) {
            class_746 player = CombatPatrolModule.mc.field_1724;
            class_638 level2 = CombatPatrolModule.mc.field_1687;
            if (player != null && level2 != null) {
                this.handleWarp((class_1657)player, (class_1937)level2);
            }
        }
        if (((Boolean)enabled.getValue()).booleanValue() && !((Collection)patrolPoints).isEmpty()) {
            this.renderRoute(event);
        }
    }

    private final void renderRoute(WorldRenderEvent.Last event) {
        List<CombatPatrolPoint> points = patrolPoints;
        int n = ((Collection)points).size();
        for (int i = 0; i < n; ++i) {
            CombatPatrolPoint p = points.get(i);
            boolean isActive = patrolRunning && i == routeIndex;
            double inflate = isActive ? 0.07 : 0.02;
            class_238 box = new class_238((double)p.getX() - inflate, (double)p.getY() - inflate, (double)p.getZ() - inflate, (double)p.getX() + 1.0 + inflate, (double)p.getY() + 1.0 + inflate, (double)p.getZ() + 1.0 + inflate);
            Color color = switch (WhenMappings.$EnumSwitchMapping$1[p.getType().ordinal()]) {
                case 1 -> {
                    if (isActive) {
                        yield new Color(100, 220, 255, 255);
                    }
                    yield new Color(70, 150, 255, 180);
                }
                case 2 -> {
                    if (isActive) {
                        yield new Color(220, 110, 255, 255);
                    }
                    yield new Color(170, 70, 220, 180);
                }
                case 3 -> {
                    if (isActive) {
                        yield new Color(255, 80, 80, 255);
                    }
                    yield new Color(210, 50, 50, 180);
                }
                default -> throw new NoWhenBranchMatchedException();
            };
            Render3D.drawBox(event.getContext(), box, color, true);
            if (p.getType() == CombatPatrolPointType.KILL) {
                Color zoneColor = isActive ? new Color(255, 80, 80, 200) : new Color(210, 50, 50, 140);
                this.drawSphereRings(event, new class_243((double)p.getX() + 0.5, (double)p.getY() + 0.5, (double)p.getZ() + 0.5), ((Number)killZoneRadius.getValue()).doubleValue(), zoneColor);
            }
            if (i >= points.size() - 1 && !((Boolean)loopRoute.getValue()).booleanValue()) continue;
            CombatPatrolPoint next = points.get((i + 1) % points.size());
            Color lineColor = isActive ? new Color(255, 255, 255, 200) : new Color(190, 190, 190, 120);
            Render3D.drawLine(event.getContext(), new class_243((double)p.getX() + 0.5, (double)p.getY() + 0.5, (double)p.getZ() + 0.5), new class_243((double)next.getX() + 0.5, (double)next.getY() + 0.5, (double)next.getZ() + 0.5), lineColor, true, 1.5f);
        }
    }

    @SubscribeEvent
    public final void onRightClick(@NotNull MouseEvent.RightClick event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabled.getValue()).booleanValue() || !((Boolean)recordOnRightClick.getValue()).booleanValue()) {
            return;
        }
        class_239 hit = CombatPatrolModule.mc.field_1765;
        if (hit instanceof class_3965 && ((class_3965)hit).method_17783() == class_239.class_240.field_1332) {
            event.setCancelled(true);
            pendingClickPos = ((class_3965)hit).method_17777();
            this.applyPickedType(this.currentPointType());
        }
    }

    public final void applyPickedType(@NotNull CombatPatrolPointType type) {
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        class_2338 class_23382 = pendingClickPos;
        if (class_23382 == null) {
            return;
        }
        class_2338 pos = class_23382;
        pendingClickPos = null;
        patrolPoints.add(new CombatPatrolPoint(pos.method_10263(), pos.method_10264(), pos.method_10260(), type));
        ChatUtils.sendMessage("Patrol point added at " + pos.method_10263() + " " + pos.method_10264() + " " + pos.method_10260() + " (" + type.getId() + ").");
        this.updatePointsInfo$cobalt();
    }

    public final void cancelPendingPick() {
        pendingClickPos = null;
    }

    private final CombatPatrolPointType currentPointType() {
        return switch (((Number)pointType.getValue()).intValue()) {
            case 1 -> CombatPatrolPointType.WARP;
            case 2 -> CombatPatrolPointType.KILL;
            default -> CombatPatrolPointType.WALK;
        };
    }

    private final void addPointFromPlayer() {
        class_746 class_7462 = CombatPatrolModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        class_2338 class_23382 = player.method_24515();
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"blockPosition(...)");
        class_2338 pos = class_23382;
        CombatPatrolPointType type = this.currentPointType();
        patrolPoints.add(new CombatPatrolPoint(pos.method_10263(), pos.method_10264(), pos.method_10260(), type));
        ChatUtils.sendMessage("Patrol point added (" + patrolPoints.size() + " total, type=" + type.getId() + ").");
        this.updatePointsInfo$cobalt();
    }

    private final boolean isValidName(String name) {
        boolean bl;
        block3: {
            if (Intrinsics.areEqual((Object)name, (Object)".") || Intrinsics.areEqual((Object)name, (Object)"..")) {
                return false;
            }
            if (StringsKt.endsWith$default((String)name, (String)".", (boolean)false, (int)2, null) || StringsKt.endsWith$default((String)name, (String)" ", (boolean)false, (int)2, null)) {
                return false;
            }
            CharSequence $this$none$iv = name;
            boolean $i$f$none = false;
            for (int i = 0; i < $this$none$iv.length(); ++i) {
                char element$iv;
                char it = element$iv = $this$none$iv.charAt(i);
                boolean bl2 = false;
                char[] cArray = new char[]{'\\', '/', ':', '*', '?', '\"', '<', '>', '|'};
                if (!ArraysKt.contains((char[])cArray, (char)it)) continue;
                bl = false;
                break block3;
            }
            bl = true;
        }
        return bl;
    }

    private final File routeFile(String name) {
        return new File(this.getPatrolDir(), name + ".json");
    }

    public final void saveRoute$cobalt() {
        String name = ((Object)StringsKt.trim((CharSequence)((String)routeName.getValue()))).toString();
        if (((CharSequence)name).length() == 0) {
            ChatUtils.sendMessage("Route name is empty.");
            return;
        }
        if (!this.isValidName(name)) {
            ChatUtils.sendMessage("Invalid route name characters.");
            return;
        }
        if (!this.getPatrolDir().exists()) {
            this.getPatrolDir().mkdirs();
        }
        JsonObject root = new JsonObject();
        JsonArray arr = new JsonArray();
        Iterable $this$forEach$iv = patrolPoints;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            CombatPatrolPoint p = (CombatPatrolPoint)element$iv;
            boolean bl = false;
            JsonObject o = new JsonObject();
            o.addProperty("x", (Number)p.getX());
            o.addProperty("y", (Number)p.getY());
            o.addProperty("z", (Number)p.getZ());
            o.addProperty("type", p.getType().getId());
            arr.add((JsonElement)o);
        }
        root.add("points", (JsonElement)arr);
        File file = this.routeFile(name);
        String string = gson.toJson((JsonElement)root);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toJson(...)");
        FilesKt.writeText$default((File)file, (String)string, null, (int)2, null);
        ChatUtils.sendMessage("Saved patrol route \"" + name + "\" (" + patrolPoints.size() + " points).");
    }

    public final void loadRoute$cobalt() {
        Object $this$loadRoute_u24lambda_u241;
        String text;
        Object $this$loadRoute_u24lambda_u240;
        String name = ((Object)StringsKt.trim((CharSequence)((String)routeName.getValue()))).toString();
        if (((CharSequence)name).length() == 0) {
            ChatUtils.sendMessage("Route name is empty.");
            return;
        }
        if (!this.isValidName(name)) {
            ChatUtils.sendMessage("Invalid route name characters.");
            return;
        }
        File file = this.routeFile(name);
        if (!file.exists()) {
            ChatUtils.sendMessage("Route \"" + name + "\" not found.");
            return;
        }
        Object object = this;
        try {
            $this$loadRoute_u24lambda_u240 = object;
            boolean bl = false;
            $this$loadRoute_u24lambda_u240 = Result.constructor-impl((Object)FilesKt.readText$default((File)file, null, (int)1, null));
        }
        catch (Throwable bl) {
            $this$loadRoute_u24lambda_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
        }
        object = $this$loadRoute_u24lambda_u240;
        String string = (String)(Result.isFailure-impl((Object)object) ? null : object);
        String string2 = string != null ? ((Object)StringsKt.trim((CharSequence)string)).toString() : null;
        if (string2 == null) {
            string2 = "";
        }
        if (((CharSequence)(text = string2)).length() == 0) {
            ChatUtils.sendMessage("Route file is empty.");
            return;
        }
        $this$loadRoute_u24lambda_u240 = this;
        try {
            $this$loadRoute_u24lambda_u241 = (CombatPatrolModule)$this$loadRoute_u24lambda_u240;
            boolean bl = false;
            $this$loadRoute_u24lambda_u241 = Result.constructor-impl((Object)JsonParser.parseString((String)text));
        }
        catch (Throwable bl) {
            $this$loadRoute_u24lambda_u241 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
        }
        $this$loadRoute_u24lambda_u240 = $this$loadRoute_u24lambda_u241;
        JsonElement jsonElement = (JsonElement)(Result.isFailure-impl((Object)$this$loadRoute_u24lambda_u240) ? null : $this$loadRoute_u24lambda_u240);
        if (jsonElement == null) {
            CombatPatrolModule $this$loadRoute_u24lambda_u242 = this;
            boolean bl = false;
            ChatUtils.sendMessage("Route file is invalid JSON.");
            return;
        }
        JsonElement parsed = jsonElement;
        JsonObject jsonObject = parsed.getAsJsonObject();
        if (jsonObject == null || (jsonObject = jsonObject.getAsJsonArray("points")) == null) {
            CombatPatrolModule $this$loadRoute_u24lambda_u243 = this;
            boolean bl = false;
            ChatUtils.sendMessage("Route file has no \"points\" array.");
            return;
        }
        JsonObject arr = jsonObject;
        List loaded = new ArrayList();
        Iterable $this$forEach$iv = (Iterable)arr;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            JsonElement el = (JsonElement)element$iv;
            boolean bl = false;
            JsonObject o = el.getAsJsonObject();
            JsonElement jsonElement2 = o.get("x");
            if (jsonElement2 == null) {
                continue;
            }
            int x = jsonElement2.getAsInt();
            JsonElement jsonElement3 = o.get("y");
            if (jsonElement3 == null) {
                continue;
            }
            int y = jsonElement3.getAsInt();
            JsonElement jsonElement4 = o.get("z");
            if (jsonElement4 == null) {
                continue;
            }
            int z = jsonElement4.getAsInt();
            JsonElement jsonElement5 = o.get("type");
            CombatPatrolPointType t = CombatPatrolPointType.Companion.fromId(jsonElement5 != null ? jsonElement5.getAsString() : null);
            loaded.add(new CombatPatrolPoint(x, y, z, t));
        }
        patrolPoints.clear();
        patrolPoints.addAll(loaded);
        this.updatePointsInfo$cobalt();
        ChatUtils.sendMessage("Loaded patrol route \"" + name + "\" (" + patrolPoints.size() + " points).");
    }

    public final void updatePointsInfo$cobalt() {
        pointsInfo.setValue(patrolPoints.size() + " points");
    }

    public final void startPatrol() {
        if (patrolPoints.isEmpty()) {
            ChatUtils.sendMessage("No patrol points. Add some first.");
            return;
        }
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            enabled.setValue(true);
        }
        PathfindingModule.INSTANCE.ensureEnabledForAutomation("combat-patrol");
        routeIndex = (Boolean)startFromNearest.getValue() != false ? this.findNearestIndex() : 0;
        patrolRunning = true;
        killZoneClearTicks = 0;
        this.navigateTo$cobalt(patrolPoints.get(routeIndex));
        ChatUtils.sendMessage("Combat patrol started at point " + (routeIndex + 1) + "/" + patrolPoints.size() + ".");
    }

    public final void stopPatrol(@NotNull String msg) {
        Intrinsics.checkNotNullParameter((Object)msg, (String)"msg");
        this.cancelWarp();
        if (patrolRunning && ((CharSequence)msg).length() > 0) {
            ChatUtils.sendMessage(msg);
        }
        patrolRunning = false;
        patrolOwnsPathfinder = false;
        patrolState = PatrolState.IDLE;
        killZoneClearTicks = 0;
        killZoneClearedThisTick = false;
        NativePathfinder.INSTANCE.stop();
        MovementManager.setMovementLock(false);
        statusInfo.setValue("Idle");
    }

    public static /* synthetic */ void stopPatrol$default(CombatPatrolModule combatPatrolModule, String string, int n, Object object) {
        if ((n & 1) != 0) {
            string = "";
        }
        combatPatrolModule.stopPatrol(string);
    }

    public final boolean isPatrolRunning() {
        return patrolRunning;
    }

    private final void advanceAndNavigate() {
        int n = routeIndex;
        if ((routeIndex = n + 1) >= patrolPoints.size()) {
            if (((Boolean)loopRoute.getValue()).booleanValue()) {
                routeIndex = 0;
            } else {
                this.stopPatrol("Patrol complete.");
                return;
            }
        }
        this.navigateTo$cobalt(patrolPoints.get(routeIndex));
    }

    /*
     * Unable to fully structure code
     */
    public final void navigateTo$cobalt(@NotNull CombatPatrolPoint point) {
        Intrinsics.checkNotNullParameter((Object)point, (String)"point");
        switch (WhenMappings.$EnumSwitchMapping$1[point.getType().ordinal()]) {
            case 1: 
            case 3: {
                level = CombatPatrolModule.mc.field_1687;
                if (level != null) {
                    v0 = MinecraftPathingRules.INSTANCE.resolveTarget((class_1937)level, new class_2338(point.getX(), point.getY(), point.getZ()));
                    if (v0 == null) {
                        v0 = new class_2338(point.getX(), point.getY(), point.getZ());
                    }
                } else {
                    v0 = new class_2338(point.getX(), point.getY(), point.getZ());
                }
                resolved = v0;
                NativePathfinder.INSTANCE.setTarget((double)resolved.method_10263() + 0.5, resolved.method_10264(), (double)resolved.method_10260() + 0.5);
                CombatPatrolModule.navPathActivated = false;
                CombatPatrolModule.patrolOwnsPathfinder = true;
                CombatPatrolModule.patrolState = PatrolState.NAVIGATING;
                break;
            }
            case 2: {
                target = new class_2338(point.getX(), point.getY(), point.getZ());
                v1 = CombatPatrolModule.mc.field_1687;
                if (v1 == null) ** GOTO lbl27
                it = v1;
                $i$a$-let-CombatPatrolModule$navigateTo$resolved$1 = false;
                v2 = CombatPatrolModule.INSTANCE.resolveWarpPoint((class_1937)it, target);
                v1 = v2;
                if (v2 != null) ** GOTO lbl28
lbl27:
                // 2 sources

                v1 = target;
lbl28:
                // 2 sources

                resolved = v1;
                this.startWarpPoint(CombatPatrolPoint.copy$default(point, resolved.method_10263(), resolved.method_10264(), resolved.method_10260(), null, 8, null));
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
    }

    private final int findNearestIndex() {
        Object v1;
        class_746 class_7462 = CombatPatrolModule.mc.field_1724;
        if (class_7462 == null) {
            return 0;
        }
        class_746 player = class_7462;
        double px = player.method_23317();
        double py = player.method_23318();
        double pz = player.method_23321();
        Iterable $this$minByOrNull$iv = (Iterable)CollectionsKt.getIndices((Collection)patrolPoints);
        boolean $i$f$minByOrNull = false;
        Iterator iterator$iv = $this$minByOrNull$iv.iterator();
        if (!iterator$iv.hasNext()) {
            v1 = null;
        } else {
            Object minElem$iv = iterator$iv.next();
            if (!iterator$iv.hasNext()) {
                v1 = minElem$iv;
            } else {
                int i = ((Number)minElem$iv).intValue();
                boolean bl = false;
                CombatPatrolPoint p = patrolPoints.get(i);
                double dx = (double)p.getX() + 0.5 - px;
                double dy = (double)p.getY() - py;
                double dz = (double)p.getZ() + 0.5 - pz;
                double minValue$iv = dx * dx + dy * dy + dz * dz;
                do {
                    Object e$iv = iterator$iv.next();
                    i = ((Number)e$iv).intValue();
                    bl = false;
                    p = patrolPoints.get(i);
                    dx = (double)p.getX() + 0.5 - px;
                    double v$iv = dx * dx + (dy = (double)p.getY() - py) * dy + (dz = (double)p.getZ() + 0.5 - pz) * dz;
                    if (Double.compare(minValue$iv, v$iv) <= 0) continue;
                    minElem$iv = e$iv;
                    minValue$iv = v$iv;
                } while (iterator$iv.hasNext());
                v1 = minElem$iv;
            }
        }
        Integer n = v1;
        return n != null ? n : 0;
    }

    public final void onCombatInterrupt() {
        if (patrolState != PatrolState.NAVIGATING && patrolState != PatrolState.WARPING) {
            return;
        }
        NativePathfinder.INSTANCE.stop();
        patrolOwnsPathfinder = false;
        patrolState = PatrolState.COMBAT_INTERRUPT;
    }

    public final void onCombatResume() {
        if (patrolState != PatrolState.COMBAT_INTERRUPT) {
            return;
        }
        CombatPatrolPoint combatPatrolPoint = (CombatPatrolPoint)CollectionsKt.getOrNull(patrolPoints, (int)routeIndex);
        if (combatPatrolPoint == null) {
            CombatPatrolModule $this$onCombatResume_u24lambda_u240 = this;
            boolean bl = false;
            $this$onCombatResume_u24lambda_u240.stopPatrol("Route index out of bounds.");
            return;
        }
        CombatPatrolPoint point2 = combatPatrolPoint;
        this.navigateTo$cobalt(point2);
    }

    public final void onKillZoneCleared() {
        if (patrolState != PatrolState.AT_KILL_ZONE) {
            return;
        }
        killZoneClearedThisTick = true;
    }

    @Nullable
    public final CombatPatrolPoint getCurrentKillPoint() {
        return patrolState == PatrolState.AT_KILL_ZONE ? (CombatPatrolPoint)CollectionsKt.getOrNull(patrolPoints, (int)routeIndex) : null;
    }

    public final double getKillZoneRadiusValue() {
        return ((Number)killZoneRadius.getValue()).doubleValue();
    }

    private final void startWarpPoint(CombatPatrolPoint point2) {
        class_746 class_7462 = CombatPatrolModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        class_638 class_6382 = CombatPatrolModule.mc.field_1687;
        if (class_6382 == null) {
            return;
        }
        class_638 level2 = class_6382;
        class_2338 target = new class_2338(point2.getX(), point2.getY(), point2.getZ());
        if (level2.method_75260() < warpCooldownUntil) {
            NativePathfinder.INSTANCE.setTarget((double)point2.getX() + 0.5, point2.getY(), (double)point2.getZ() + 0.5);
            patrolOwnsPathfinder = true;
            patrolState = PatrolState.NAVIGATING;
            return;
        }
        if (this.wasJustWarpedToTarget((class_1937)level2, (class_1657)player, target)) {
            this.advanceAndNavigate();
            return;
        }
        int slot = ((Number)aotvSlot.getValue()).intValue();
        if (!EtherwarpLogic.INSTANCE.holdingEtherwarpItem()) {
            int currentSlot = player.method_31548().method_67532();
            InventoryUtils.holdHotbarSlot(slot);
            if (!EtherwarpLogic.INSTANCE.holdingEtherwarpItem()) {
                InventoryUtils.holdHotbarSlot(currentSlot);
                ChatUtils.sendMessage("Patrol: no AOTV in slot " + (slot + 1) + ", skipping warp.");
                this.advanceAndNavigate();
                return;
            }
            warpRestoreSlot = currentSlot;
        }
        NativePathfinder.INSTANCE.stop();
        RotationExecutor.INSTANCE.stopRotating();
        CombatPatrolModule.mc.field_1690.field_1904.method_23481(false);
        CombatPatrolModule.mc.field_1690.field_1832.method_23481(false);
        warpTargetPoint = point2;
        warpStage = 0;
        warpStageElapsedMs = 0.0;
        warpStageLastNs = 0L;
        warpLookLastNs = 0L;
        patrolOwnsPathfinder = true;
        patrolState = PatrolState.WARPING;
    }

    private final void cancelWarp() {
        CombatPatrolModule.mc.field_1690.field_1904.method_23481(false);
        CombatPatrolModule.mc.field_1690.field_1832.method_23481(false);
        RotationExecutor.INSTANCE.stopRotating();
        int n = warpRestoreSlot;
        boolean bl = 0 <= n ? n < 9 : false;
        if (bl) {
            InventoryUtils.holdHotbarSlot(warpRestoreSlot);
        }
        warpRestoreSlot = -1;
        warpStage = 0;
        warpTargetPoint = null;
        warpStageElapsedMs = 0.0;
        warpStageLastNs = 0L;
        warpLookLastNs = 0L;
        patrolOwnsPathfinder = false;
    }

    private final void handleWarp(class_1657 player, class_1937 level2) {
        CombatPatrolPoint combatPatrolPoint = warpTargetPoint;
        if (combatPatrolPoint == null) {
            CombatPatrolModule $this$handleWarp_u24lambda_u240 = this;
            boolean bl = false;
            $this$handleWarp_u24lambda_u240.cancelWarp();
            return;
        }
        CombatPatrolPoint pointData = combatPatrolPoint;
        class_2338 target = new class_2338(pointData.getX(), pointData.getY(), pointData.getZ());
        class_243 warpAimPoint = this.resolveWarpAimPoint(level2, player, target);
        this.advanceWarpFrameTime();
        switch (warpStage) {
            case 0: {
                Pair<Double, Double> bl = this.applyWarpHeadRotation(player, warpAimPoint);
                double yawError = ((Number)bl.component1()).doubleValue();
                double pitchError = ((Number)bl.component2()).doubleValue();
                if (!(yawError <= 6.0 && pitchError <= 6.0) && !(warpStageElapsedMs >= 170.0)) break;
                CombatPatrolModule.mc.field_1690.field_1832.method_23481(true);
                warpStage = 1;
                this.resetWarpStageTimer();
                break;
            }
            case 1: {
                this.applyWarpHeadRotation(player, warpAimPoint);
                CombatPatrolModule.mc.field_1690.field_1832.method_23481(true);
                if (!this.canWarpToTarget(level2, player, target, warpAimPoint)) {
                    if (warpStageElapsedMs >= 240.0) {
                        CombatPatrolModule.mc.field_1690.field_1832.method_23481(false);
                        warpCooldownUntil = level2.method_75260() + 4L;
                        this.cancelWarp();
                        this.advanceAndNavigate();
                    }
                    return;
                }
                if (!(warpStageElapsedMs >= 85.0)) break;
                boolean shiftKeyHeld = CombatPatrolModule.mc.field_1690.field_1832.method_1434();
                boolean playerIsShifting = player.method_5715();
                if (!shiftKeyHeld || !playerIsShifting) {
                    return;
                }
                CombatPatrolModule.mc.field_1690.field_1904.method_23481(true);
                warpStage = 2;
                this.resetWarpStageTimer();
                break;
            }
            default: {
                class_243 class_2432;
                class_243 postWarpAim;
                CombatPatrolPoint nextPoint;
                CombatPatrolModule.mc.field_1690.field_1904.method_23481(false);
                boolean landed = this.hasArrived(player, target);
                int nextIndex = routeIndex + 1;
                CombatPatrolPoint combatPatrolPoint2 = nextPoint = (CombatPatrolPoint)CollectionsKt.getOrNull(patrolPoints, (int)(nextIndex < patrolPoints.size() ? nextIndex : 0));
                if (combatPatrolPoint2 != null) {
                    CombatPatrolPoint it = combatPatrolPoint2;
                    boolean bl = false;
                    v2 = new class_243((double)it.getX() + 0.5, (double)it.getY() + 0.6, (double)it.getZ() + 0.5);
                } else {
                    v2 = postWarpAim = null;
                }
                if (postWarpAim != null) {
                    double t = RangesKt.coerceIn((double)(warpStageElapsedMs / 70.0), (double)0.0, (double)1.0);
                    class_2432 = this.blendVec3(warpAimPoint, postWarpAim, t);
                } else {
                    class_2432 = warpAimPoint;
                }
                class_243 frameAim = class_2432;
                this.applyWarpHeadRotation(player, frameAim);
                CombatPatrolModule.mc.field_1690.field_1832.method_23481(true);
                if (!(warpStageElapsedMs >= 70.0)) break;
                CombatPatrolModule.mc.field_1690.field_1832.method_23481(false);
                warpCooldownUntil = level2.method_75260() + 1L;
                if (landed) {
                    lastSuccessfulWarpTarget = target;
                    lastSuccessfulWarpTick = level2.method_75260();
                }
                this.cancelWarp();
                this.advanceAndNavigate();
            }
        }
    }

    private final boolean hasArrived(class_1657 player, class_2338 target) {
        double distSq = player.method_24515().method_10262((class_2382)target);
        return distSq <= 36.0;
    }

    private final Pair<Double, Double> applyWarpHeadRotation(class_1657 player, class_243 target) {
        Rotation targetRotation = AngleUtils.INSTANCE.getRotation(target);
        long now = System.nanoTime();
        double dtSec = warpLookLastNs == 0L ? 0.016666666666666666 : RangesKt.coerceIn((double)((double)(now - warpLookLastNs) / 1.0E9), (double)0.004166666666666667, (double)0.08);
        warpLookLastNs = now;
        double maxYawStep = 360.0 * dtSec;
        double maxPitchStep = 300.0 * dtSec;
        double yawDelta = AngleUtils.INSTANCE.getRotationDelta(player.method_36454(), targetRotation.getYaw());
        double pitchDelta = targetRotation.getPitch() - player.method_36455();
        float yawStep = (float)RangesKt.coerceIn((double)yawDelta, (double)(-maxYawStep), (double)maxYawStep);
        float pitchStep = (float)RangesKt.coerceIn((double)pitchDelta, (double)(-maxPitchStep), (double)maxPitchStep);
        player.method_36456(AngleUtils.INSTANCE.normalizeAngle(player.method_36454() + yawStep));
        player.field_6241 = player.method_36454();
        player.field_6283 = player.method_36454();
        player.method_36457(RangesKt.coerceIn((float)(player.method_36455() + pitchStep), (float)-89.9f, (float)89.9f));
        double yawError = Math.abs(AngleUtils.INSTANCE.getRotationDelta(player.method_36454(), targetRotation.getYaw()));
        double pitchError = Math.abs(targetRotation.getPitch() - player.method_36455());
        return TuplesKt.to((Object)yawError, (Object)pitchError);
    }

    private final class_243 resolveWarpAimPoint(class_1937 level2, class_1657 player, class_2338 target) {
        class_243 center = new class_243((double)target.method_10263() + 0.5, (double)target.method_10264() + 0.5, (double)target.method_10260() + 0.5);
        class_243 class_2432 = player.method_33571();
        Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"getEyePosition(...)");
        class_243 eye = class_2432;
        double towardX = RangesKt.coerceIn((double)(eye.field_1352 - center.field_1352), (double)-0.28, (double)0.28);
        double towardZ = RangesKt.coerceIn((double)(eye.field_1350 - center.field_1350), (double)-0.28, (double)0.28);
        class_243[] class_243Array = new class_243[]{center, new class_243(center.field_1352, center.field_1351 + 0.26, center.field_1350), new class_243(center.field_1352, center.field_1351 - 0.2, center.field_1350), new class_243(center.field_1352 + towardX, center.field_1351, center.field_1350), new class_243(center.field_1352, center.field_1351, center.field_1350 + towardZ), new class_243(center.field_1352 + towardX, center.field_1351 + 0.18, center.field_1350 + towardZ), new class_243(center.field_1352 + towardX, center.field_1351 - 0.12, center.field_1350 + towardZ), new class_243(center.field_1352 + 0.24, center.field_1351, center.field_1350), new class_243(center.field_1352 - 0.24, center.field_1351, center.field_1350), new class_243(center.field_1352, center.field_1351, center.field_1350 + 0.24), new class_243(center.field_1352, center.field_1351, center.field_1350 - 0.24)};
        List candidates = CollectionsKt.listOf((Object[])class_243Array);
        for (class_243 candidate : candidates) {
            if (!this.hasLineOfSight(level2, player, target, candidate)) continue;
            return candidate;
        }
        return center;
    }

    private final void advanceWarpFrameTime() {
        long now = System.nanoTime();
        double dtMs = warpStageLastNs == 0L ? 0.0 : RangesKt.coerceIn((double)((double)(now - warpStageLastNs) / 1000000.0), (double)0.0, (double)80.0);
        warpStageLastNs = now;
        warpStageElapsedMs += dtMs;
    }

    private final void resetWarpStageTimer() {
        warpStageElapsedMs = 0.0;
        warpStageLastNs = System.nanoTime();
    }

    private final class_2338 resolveWarpPoint(class_1937 level2, class_2338 rawPoint) {
        class_2338 direct = this.candidateWarpBlock(level2, rawPoint);
        if (direct != null && this.isWarpBlockViable(level2, direct)) {
            return direct;
        }
        class_2338 best = null;
        double bestDistSq = Double.POSITIVE_INFINITY;
        for (int dy = -2; dy < 3; ++dy) {
            for (int dx = -2; dx < 3; ++dx) {
                for (int dz = -2; dz < 3; ++dz) {
                    double distSq;
                    class_2338 candidate;
                    class_2338 probe;
                    Intrinsics.checkNotNullExpressionValue((Object)rawPoint.method_10069(dx, dy, dz), (String)"offset(...)");
                    if (this.candidateWarpBlock(level2, probe) == null || !this.isWarpBlockViable(level2, candidate) || !((distSq = candidate.method_10262((class_2382)rawPoint)) < bestDistSq)) continue;
                    bestDistSq = distSq;
                    best = candidate;
                }
            }
        }
        return best;
    }

    /*
     * WARNING - void declaration
     */
    private final class_2338 candidateWarpBlock(class_1937 level2, class_2338 pos) {
        Object object;
        if (MinecraftPathingRules.INSTANCE.isWalkable(level2, pos)) {
            void var3_3;
            class_2338 class_23382 = pos.method_10074();
            Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"below(...)");
            class_2338 support = class_23382;
            object = level2.method_8320(support).method_26215() ? null : var3_3;
        } else {
            object = level2.method_8320(pos).method_26215() ? null : pos;
        }
        return object;
    }

    private final boolean isWarpBlockViable(class_1937 level2, class_2338 block) {
        if (level2.method_8320(block).method_26215()) {
            return false;
        }
        class_2338 class_23382 = block.method_10084();
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"above(...)");
        if (!MinecraftPathingRules.INSTANCE.isPassable(level2, class_23382)) {
            return false;
        }
        class_2338 class_23383 = block.method_10086(2);
        Intrinsics.checkNotNullExpressionValue((Object)class_23383, (String)"above(...)");
        return MinecraftPathingRules.INSTANCE.isPassable(level2, class_23383);
    }

    private final boolean canWarpToTarget(class_1937 level2, class_1657 player, class_2338 target, class_243 aimPoint) {
        if (!this.isWarpBlockViable(level2, target)) {
            return false;
        }
        if (!EtherwarpLogic.INSTANCE.canEtherwarp()) {
            return false;
        }
        class_243 class_2432 = player.method_33571();
        Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"getEyePosition(...)");
        class_243 eye = class_2432;
        class_243 class_2433 = aimPoint;
        if (class_2433 == null) {
            class_2433 = this.resolveWarpAimPoint(level2, player, target);
        }
        class_243 point2 = class_2433;
        double range = (double)EtherwarpLogic.INSTANCE.getEtherwarpRange() + 0.5;
        if (eye.method_1025(point2) > range * range) {
            return false;
        }
        EtherwarpLogic.EtherPos result = EtherwarpLogic.INSTANCE.getEtherwarpResultSneaking();
        return result.getSucceeded() && Intrinsics.areEqual((Object)result.getPos(), (Object)target);
    }

    static /* synthetic */ boolean canWarpToTarget$default(CombatPatrolModule combatPatrolModule, class_1937 class_19372, class_1657 class_16572, class_2338 class_23382, class_243 class_2432, int n, Object object) {
        if ((n & 8) != 0) {
            class_2432 = null;
        }
        return combatPatrolModule.canWarpToTarget(class_19372, class_16572, class_23382, class_2432);
    }

    private final boolean isStandingOnWarpTarget(class_1657 player, class_2338 target) {
        return Intrinsics.areEqual((Object)player.method_24515().method_10074(), (Object)target);
    }

    private final boolean wasJustWarpedToTarget(class_1937 level2, class_1657 player, class_2338 target) {
        class_2338 class_23382 = lastSuccessfulWarpTarget;
        if (class_23382 == null) {
            return false;
        }
        class_2338 lastTarget = class_23382;
        if (lastSuccessfulWarpTick < 0L) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)lastTarget, (Object)target)) {
            return false;
        }
        if (!this.isStandingOnWarpTarget(player, target)) {
            return false;
        }
        return level2.method_75260() - lastSuccessfulWarpTick <= 10L;
    }

    private final boolean hasLineOfSight(class_1937 level2, class_1657 player, class_2338 target, class_243 point2) {
        class_243 class_2432 = player.method_33571();
        Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"getEyePosition(...)");
        class_243 eye = class_2432;
        class_3965 class_39652 = level2.method_17742(new class_3959(eye, point2, class_3959.class_3960.field_17559, class_3959.class_242.field_1348, (class_1297)player));
        Intrinsics.checkNotNullExpressionValue((Object)class_39652, (String)"clip(...)");
        class_3965 hit = class_39652;
        return hit.method_17783() == class_239.class_240.field_1332 && Intrinsics.areEqual((Object)hit.method_17777(), (Object)target);
    }

    private final class_243 blendVec3(class_243 from, class_243 to, double t) {
        double clamped = RangesKt.coerceIn((double)t, (double)0.0, (double)1.0);
        return new class_243(from.field_1352 + (to.field_1352 - from.field_1352) * clamped, from.field_1351 + (to.field_1351 - from.field_1351) * clamped, from.field_1350 + (to.field_1350 - from.field_1350) * clamped);
    }

    private final void drawSphereRings(WorldRenderEvent.Last event, class_243 center, double radius, Color color) {
        int segments = 40;
        for (int i = 0; i < segments; ++i) {
            double a1 = (double)i * TWO_PI / (double)segments;
            double a2 = (double)(i + 1) * TWO_PI / (double)segments;
            double c1 = Math.cos(a1);
            double s1 = Math.sin(a1);
            double c2 = Math.cos(a2);
            double s2 = Math.sin(a2);
            Render3D.drawLine(event.getContext(), new class_243(center.field_1352 + radius * c1, center.field_1351, center.field_1350 + radius * s1), new class_243(center.field_1352 + radius * c2, center.field_1351, center.field_1350 + radius * s2), color, true, 1.2f);
            Render3D.drawLine(event.getContext(), new class_243(center.field_1352 + radius * c1, center.field_1351 + radius * s1, center.field_1350), new class_243(center.field_1352 + radius * c2, center.field_1351 + radius * s2, center.field_1350), color, true, 1.2f);
            Render3D.drawLine(event.getContext(), new class_243(center.field_1352, center.field_1351 + radius * c1, center.field_1350 + radius * s1), new class_243(center.field_1352, center.field_1351 + radius * c2, center.field_1350 + radius * s2), color, true, 1.2f);
        }
    }

    private static final File patrolDir_delegate$lambda$0() {
        return new File(CombatPatrolModule.mc.field_1697, "config/cobalt/combat_patrol");
    }

    private static final Unit addPointAction$lambda$0() {
        INSTANCE.addPointFromPlayer();
        return Unit.INSTANCE;
    }

    private static final Unit removeLastAction$lambda$0() {
        if (!((Collection)patrolPoints).isEmpty()) {
            patrolPoints.remove(CollectionsKt.getLastIndex(patrolPoints));
            INSTANCE.updatePointsInfo$cobalt();
        }
        return Unit.INSTANCE;
    }

    private static final Unit clearRouteAction$lambda$0() {
        patrolPoints.clear();
        INSTANCE.updatePointsInfo$cobalt();
        return Unit.INSTANCE;
    }

    private static final Unit saveRouteAction$lambda$0() {
        INSTANCE.saveRoute$cobalt();
        return Unit.INSTANCE;
    }

    private static final Unit loadRouteAction$lambda$0() {
        INSTANCE.loadRoute$cobalt();
        return Unit.INSTANCE;
    }

    private static final Unit startPatrolAction$lambda$0() {
        INSTANCE.startPatrol();
        return Unit.INSTANCE;
    }

    private static final Unit stopPatrolAction$lambda$0() {
        CombatPatrolModule.stopPatrol$default(INSTANCE, null, 1, null);
        return Unit.INSTANCE;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        gson = new GsonBuilder().setPrettyPrinting().create();
        patrolDir$delegate = LazyKt.lazy(CombatPatrolModule::patrolDir_delegate$lambda$0);
        patrolPoints = new ArrayList();
        patrolState = PatrolState.IDLE;
        warpRestoreSlot = -1;
        lastSuccessfulWarpTick = -1L;
        enabled = new CheckboxSetting("Enabled", "Enable the Combat Patrol module.", false);
        routeName = new TextSetting("Route Name", "Name used for save/load.", "default");
        pointsInfo = new InfoSetting("Points", "Number of recorded points.", InfoType.INFO);
        statusInfo = new InfoSetting("Status", "Current patrol state.", InfoType.INFO);
        recordOnRightClick = new CheckboxSetting("Record on Right Click", "Append a point when you right-click a block.", false);
        loopRoute = new CheckboxSetting("Loop Route", "Wrap back to first point at end.", true);
        startFromNearest = new CheckboxSetting("Start From Nearest", "Begin at the closest point to your position.", true);
        Object[] objectArray = new String[]{"Walk", "Warp", "Kill"};
        pointType = new ModeSetting("Point Type", "Type used when adding points.", 0, (String[])objectArray);
        killZoneRadius = new SliderSetting("Kill Zone Radius", "Mob search radius around a Kill point (blocks).", 16.0, 4.0, 64.0, 0.0, 32, null);
        killZoneDwellTicks = new SliderSetting("Kill Zone Dwell Ticks", "Zero-mob ticks required before advancing past a Kill point.", 60.0, 10.0, 200.0, 1.0);
        objectArray = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        aotvSlot = new ModeSetting("AOTV Slot", "Hotbar slot (1-9) holding your AOTV item.", 0, (String[])objectArray);
        addPointAction = new ActionSetting("Add Point", "Record your current position.", "Add", null, CombatPatrolModule::addPointAction$lambda$0, 8, null);
        removeLastAction = new ActionSetting("Remove Last", "Remove the last recorded point.", "Remove", null, CombatPatrolModule::removeLastAction$lambda$0, 8, null);
        clearRouteAction = new ActionSetting("Clear Route", "Remove all patrol points.", "Clear", null, CombatPatrolModule::clearRouteAction$lambda$0, 8, null);
        saveRouteAction = new ActionSetting("Save Route", "Save route to disk.", "Save", null, CombatPatrolModule::saveRouteAction$lambda$0, 8, null);
        loadRouteAction = new ActionSetting("Load Route", "Load route from disk.", "Load", null, CombatPatrolModule::loadRouteAction$lambda$0, 8, null);
        startPatrolAction = new ActionSetting("Start Patrol", "Start the patrol.", "Start", null, CombatPatrolModule::startPatrolAction$lambda$0, 8, null);
        stopPatrolAction = new ActionSetting("Stop Patrol", "Stop the patrol.", "Stop", null, CombatPatrolModule::stopPatrolAction$lambda$0, 8, null);
        objectArray = new Setting[18];
        objectArray[0] = enabled;
        objectArray[1] = routeName;
        objectArray[2] = pointsInfo;
        objectArray[3] = statusInfo;
        objectArray[4] = recordOnRightClick;
        objectArray[5] = loopRoute;
        objectArray[6] = startFromNearest;
        objectArray[7] = pointType;
        objectArray[8] = killZoneRadius;
        objectArray[9] = killZoneDwellTicks;
        objectArray[10] = aotvSlot;
        objectArray[11] = addPointAction;
        objectArray[12] = removeLastAction;
        objectArray[13] = clearRouteAction;
        objectArray[14] = saveRouteAction;
        objectArray[15] = loadRouteAction;
        objectArray[16] = startPatrolAction;
        objectArray[17] = stopPatrolAction;
        INSTANCE.addSetting((Setting<?>[])objectArray);
        statusInfo.setValue("Idle");
        EventBus.register(INSTANCE);
        TWO_PI = Math.PI * 2;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\b\u00a8\u0006\t"}, d2={"Lorg/cobalt/internal/combat/CombatPatrolModule$PatrolState;", "", "<init>", "(Ljava/lang/String;I)V", "IDLE", "NAVIGATING", "WARPING", "COMBAT_INTERRUPT", "AT_KILL_ZONE", "cobalt"})
    public static final class PatrolState
    extends Enum<PatrolState> {
        public static final /* enum */ PatrolState IDLE = new PatrolState();
        public static final /* enum */ PatrolState NAVIGATING = new PatrolState();
        public static final /* enum */ PatrolState WARPING = new PatrolState();
        public static final /* enum */ PatrolState COMBAT_INTERRUPT = new PatrolState();
        public static final /* enum */ PatrolState AT_KILL_ZONE = new PatrolState();
        private static final /* synthetic */ PatrolState[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        public static PatrolState[] values() {
            return (PatrolState[])$VALUES.clone();
        }

        public static PatrolState valueOf(String value) {
            return Enum.valueOf(PatrolState.class, value);
        }

        @NotNull
        public static EnumEntries<PatrolState> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = patrolStateArray = new PatrolState[]{PatrolState.IDLE, PatrolState.NAVIGATING, PatrolState.WARPING, PatrolState.COMBAT_INTERRUPT, PatrolState.AT_KILL_ZONE};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;
        public static final /* synthetic */ int[] $EnumSwitchMapping$1;

        static {
            int[] nArray = new int[PatrolState.values().length];
            try {
                nArray[PatrolState.NAVIGATING.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[PatrolState.WARPING.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[PatrolState.AT_KILL_ZONE.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[PatrolState.COMBAT_INTERRUPT.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[PatrolState.IDLE.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
            nArray = new int[CombatPatrolPointType.values().length];
            try {
                nArray[CombatPatrolPointType.WALK.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[CombatPatrolPointType.WARP.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[CombatPatrolPointType.KILL.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$1 = nArray;
        }
    }
}

