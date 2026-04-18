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
 *  kotlin.Unit
 *  kotlin.collections.ArraysKt
 *  kotlin.collections.SetsKt
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.io.FilesKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.farming;

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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.SetsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.SettingKt;
import org.cobalt.api.module.setting.impl.ActionSetting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.InfoSetting;
import org.cobalt.api.module.setting.impl.InfoType;
import org.cobalt.api.module.setting.impl.ModeSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.module.setting.impl.TextSetting;
import org.cobalt.api.util.ChatUtils;
import org.cobalt.api.util.InventoryUtils;
import org.cobalt.api.util.player.MovementManager;
import org.cobalt.api.util.render.Render3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00da\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\b\u0011\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000e\n\u0002\b\u001b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0011\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0004\u00d0\u0001\u00d1\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\n\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\tH\u0007\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0017\u0010\u000e\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u000f\u0010\u0010\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u0010\u0010\u0003J\u0017\u0010\u0011\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b\u0011\u0010\u000fJ\u0017\u0010\u0012\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b\u0012\u0010\u000fJ\u0017\u0010\u0013\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b\u0013\u0010\u000fJ\u0017\u0010\u0014\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b\u0014\u0010\u000fJ\u0017\u0010\u0015\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b\u0015\u0010\u000fJ\u0017\u0010\u0016\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b\u0016\u0010\u000fJ\u000f\u0010\u0017\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u0017\u0010\u0003J\u000f\u0010\u0018\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u0018\u0010\u0003J%\u0010\u001e\u001a\u00020\u00062\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001a0\u00192\u0006\u0010\u001d\u001a\u00020\u001cH\u0002\u00a2\u0006\u0004\b\u001e\u0010\u001fJ\u000f\u0010 \u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b \u0010\u0003J?\u0010&\u001a\u00020\u00062\u0006\u0010!\u001a\u00020\u001c2\u0006\u0010\"\u001a\u00020\u001c2\u0006\u0010#\u001a\u00020\u001c2\u0006\u0010$\u001a\u00020\u001c2\u0006\u0010%\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001cH\u0002\u00a2\u0006\u0004\b&\u0010'J\u000f\u0010)\u001a\u00020(H\u0002\u00a2\u0006\u0004\b)\u0010*J\u000f\u0010+\u001a\u00020\u001aH\u0002\u00a2\u0006\u0004\b+\u0010,J\u000f\u0010-\u001a\u00020\u001aH\u0002\u00a2\u0006\u0004\b-\u0010,J\u000f\u0010.\u001a\u00020\u001aH\u0002\u00a2\u0006\u0004\b.\u0010,J\u0017\u00100\u001a\u00020\u001a2\u0006\u0010/\u001a\u00020(H\u0002\u00a2\u0006\u0004\b0\u00101J\u0017\u00102\u001a\u00020\u001a2\u0006\u0010/\u001a\u00020(H\u0002\u00a2\u0006\u0004\b2\u00101J\u001f\u00104\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\f2\u0006\u00103\u001a\u00020\u001cH\u0002\u00a2\u0006\u0004\b4\u00105J\u0017\u00106\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b6\u0010\u000fJ\u001f\u00108\u001a\u00020\u001c2\u0006\u0010\r\u001a\u00020\f2\u0006\u00107\u001a\u00020\u001aH\u0002\u00a2\u0006\u0004\b8\u00109J/\u0010>\u001a\u00020:2\u0006\u0010;\u001a\u00020:2\u0006\u0010<\u001a\u00020:2\u0006\u00107\u001a\u00020\u001a2\u0006\u0010=\u001a\u00020:H\u0002\u00a2\u0006\u0004\b>\u0010?J+\u0010A\u001a\u000e\u0012\u0004\u0012\u00020:\u0012\u0004\u0012\u00020:0@2\u0006\u00107\u001a\u00020\u001a2\u0006\u0010=\u001a\u00020:H\u0002\u00a2\u0006\u0004\bA\u0010BJ\u001f\u0010C\u001a\u00020\u001c2\u0006\u0010\r\u001a\u00020\f2\u0006\u00107\u001a\u00020\u001aH\u0002\u00a2\u0006\u0004\bC\u00109J\u001f\u0010H\u001a\u00020\u001c2\u0006\u0010E\u001a\u00020D2\u0006\u0010G\u001a\u00020FH\u0002\u00a2\u0006\u0004\bH\u0010IJ\u001f\u0010J\u001a\u00020\u001c2\u0006\u0010E\u001a\u00020D2\u0006\u0010G\u001a\u00020FH\u0002\u00a2\u0006\u0004\bJ\u0010IJ\u000f\u0010K\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bK\u0010\u0003J\u0017\u0010L\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\fH\u0002\u00a2\u0006\u0004\bL\u0010\u000fJ\u0017\u0010M\u001a\u00020\u001c2\u0006\u0010\r\u001a\u00020\fH\u0002\u00a2\u0006\u0004\bM\u0010NJ\u001f\u0010P\u001a\u00020\u001c2\u0006\u0010\r\u001a\u00020\f2\u0006\u0010O\u001a\u00020FH\u0002\u00a2\u0006\u0004\bP\u0010QJ\u0017\u0010T\u001a\u00020\u001c2\u0006\u0010S\u001a\u00020RH\u0002\u00a2\u0006\u0004\bT\u0010UJ\u000f\u0010V\u001a\u00020\u001cH\u0002\u00a2\u0006\u0004\bV\u0010WJ\u000f\u0010X\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bX\u0010\u0003J\u000f\u0010Y\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bY\u0010\u0003J\u000f\u0010Z\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bZ\u0010\u0003J\u000f\u0010[\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b[\u0010\u0003J\u000f\u0010\\\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\\\u0010\u0003J\u000f\u0010]\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b]\u0010\u0003J\u000f\u0010^\u001a\u00020RH\u0002\u00a2\u0006\u0004\b^\u0010_J\u0017\u0010a\u001a\u00020\u00062\u0006\u0010`\u001a\u00020(H\u0002\u00a2\u0006\u0004\ba\u0010bJ\u0017\u0010d\u001a\u00020:2\u0006\u0010c\u001a\u00020:H\u0002\u00a2\u0006\u0004\bd\u0010eR\u0014\u0010f\u001a\u00020R8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bf\u0010gR\u0014\u0010h\u001a\u00020(8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bh\u0010iR\u0014\u0010j\u001a\u00020(8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bj\u0010iR\u0014\u0010k\u001a\u00020:8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bk\u0010lR\u0014\u0010m\u001a\u00020(8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bm\u0010iR\u0014\u0010o\u001a\u00020n8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bo\u0010pR\u001c\u0010s\u001a\n r*\u0004\u0018\u00010q0q8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bs\u0010tR\u001b\u0010z\u001a\u00020u8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\bv\u0010w\u001a\u0004\bx\u0010yR\u001a\u0010|\u001a\b\u0012\u0004\u0012\u00020R0{8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b|\u0010}R\u001a\u0010~\u001a\b\u0012\u0004\u0012\u00020R0{8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b~\u0010}R\u001a\u0010\u007f\u001a\b\u0012\u0004\u0012\u00020R0{8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u007f\u0010}R\u001c\u0010\u0080\u0001\u001a\b\u0012\u0004\u0012\u00020R0{8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0080\u0001\u0010}R\u0013\u0010\u0081\u0001\u001a\u00020\u001c8F\u00a2\u0006\u0007\u001a\u0005\b\u0081\u0001\u0010WR\u0018\u0010\u0083\u0001\u001a\u00030\u0082\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0083\u0001\u0010\u0084\u0001R\u0018\u0010\u0086\u0001\u001a\u00030\u0085\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0086\u0001\u0010\u0087\u0001R\u0018\u0010\u0088\u0001\u001a\u00030\u0085\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0088\u0001\u0010\u0087\u0001R\u0018\u0010\u008a\u0001\u001a\u00030\u0089\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u008a\u0001\u0010\u008b\u0001R\u0018\u0010\u008c\u0001\u001a\u00030\u0082\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u008c\u0001\u0010\u0084\u0001R\u0018\u0010\u008e\u0001\u001a\u00030\u008d\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u008e\u0001\u0010\u008f\u0001R\u0018\u0010\u0090\u0001\u001a\u00030\u0089\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0090\u0001\u0010\u008b\u0001R\u0018\u0010\u0092\u0001\u001a\u00030\u0091\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0092\u0001\u0010\u0093\u0001R\u0018\u0010\u0094\u0001\u001a\u00030\u0091\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0094\u0001\u0010\u0093\u0001R\u0018\u0010\u0095\u0001\u001a\u00030\u008d\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0095\u0001\u0010\u008f\u0001R\u0018\u0010\u0096\u0001\u001a\u00030\u0082\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0096\u0001\u0010\u0084\u0001R\u0018\u0010\u0097\u0001\u001a\u00030\u0089\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0097\u0001\u0010\u008b\u0001R\u0018\u0010\u0098\u0001\u001a\u00030\u0089\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0098\u0001\u0010\u008b\u0001R\u0018\u0010\u0099\u0001\u001a\u00030\u0089\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0099\u0001\u0010\u008b\u0001R\u0018\u0010\u009a\u0001\u001a\u00030\u0091\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u009a\u0001\u0010\u0093\u0001R\u0018\u0010\u009b\u0001\u001a\u00030\u0091\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u009b\u0001\u0010\u0093\u0001R\u0018\u0010\u009c\u0001\u001a\u00030\u0091\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u009c\u0001\u0010\u0093\u0001R\u0018\u0010\u009d\u0001\u001a\u00030\u0091\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u009d\u0001\u0010\u0093\u0001R\u0018\u0010\u009e\u0001\u001a\u00030\u0091\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u009e\u0001\u0010\u0093\u0001R\u0018\u0010\u009f\u0001\u001a\u00030\u0082\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u009f\u0001\u0010\u0084\u0001R\u0018\u0010\u00a0\u0001\u001a\u00030\u0082\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a0\u0001\u0010\u0084\u0001R\u0018\u0010\u00a1\u0001\u001a\u00030\u0091\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a1\u0001\u0010\u0093\u0001R\u0018\u0010\u00a2\u0001\u001a\u00030\u0082\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a2\u0001\u0010\u0084\u0001R\u0018\u0010\u00a3\u0001\u001a\u00030\u0082\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a3\u0001\u0010\u0084\u0001R\u0018\u0010\u00a5\u0001\u001a\u00030\u00a4\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a5\u0001\u0010\u00a6\u0001R\u0018\u0010\u00a7\u0001\u001a\u00030\u0082\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a7\u0001\u0010\u0084\u0001R\u0018\u0010\u00a8\u0001\u001a\u00030\u0085\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a8\u0001\u0010\u0087\u0001R\u0018\u0010\u00a9\u0001\u001a\u00030\u0082\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00a9\u0001\u0010\u0084\u0001R\u0018\u0010\u00aa\u0001\u001a\u00030\u00a4\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00aa\u0001\u0010\u00a6\u0001R\u0018\u0010\u00ab\u0001\u001a\u00030\u0091\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00ab\u0001\u0010\u0093\u0001R\u0018\u0010\u00ac\u0001\u001a\u00030\u0091\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00ac\u0001\u0010\u0093\u0001R\u0018\u0010\u00ad\u0001\u001a\u00030\u0091\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00ad\u0001\u0010\u0093\u0001R\u0018\u0010\u00ae\u0001\u001a\u00030\u0091\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00ae\u0001\u0010\u0093\u0001R\u0018\u0010\u00af\u0001\u001a\u00030\u0082\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00af\u0001\u0010\u0084\u0001R\u0018\u0010\u00b0\u0001\u001a\u00030\u008d\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00b0\u0001\u0010\u008f\u0001R\u0018\u0010\u00b1\u0001\u001a\u00030\u008d\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00b1\u0001\u0010\u008f\u0001R\u0018\u0010\u00b2\u0001\u001a\u00030\u008d\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00b2\u0001\u0010\u008f\u0001R\u0019\u0010\u00b3\u0001\u001a\u00020\u001c8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00b3\u0001\u0010\u00b4\u0001R\u001a\u0010\u00b6\u0001\u001a\u00030\u00b5\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00b6\u0001\u0010\u00b7\u0001R\u0018\u0010\u00b8\u0001\u001a\u00020(8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u00b8\u0001\u0010iR\u0018\u0010\u00b9\u0001\u001a\u00020(8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u00b9\u0001\u0010iR\u0018\u0010\u00ba\u0001\u001a\u00020(8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u00ba\u0001\u0010iR\u0018\u0010\u00bb\u0001\u001a\u00020(8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u00bb\u0001\u0010iR\u0018\u0010\u00bc\u0001\u001a\u00020(8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u00bc\u0001\u0010iR\u0018\u0010\u00bd\u0001\u001a\u00020(8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u00bd\u0001\u0010iR\u0018\u0010\u00be\u0001\u001a\u00020:8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u00be\u0001\u0010lR\u0018\u0010\u00bf\u0001\u001a\u00020:8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u00bf\u0001\u0010lR\u0019\u0010\u00c0\u0001\u001a\u00020\u001c8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00c0\u0001\u0010\u00b4\u0001R\u0018\u0010\u00c1\u0001\u001a\u00020(8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u00c1\u0001\u0010iR\u0018\u0010\u00c2\u0001\u001a\u00020(8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u00c2\u0001\u0010iR\u0018\u0010\u00c3\u0001\u001a\u00020:8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u00c3\u0001\u0010lR\u0018\u0010\u00c4\u0001\u001a\u00020:8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u00c4\u0001\u0010lR\u0018\u0010\u00c5\u0001\u001a\u00020(8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u00c5\u0001\u0010iR\u001c\u0010\u00c7\u0001\u001a\u0005\u0018\u00010\u00c6\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00c7\u0001\u0010\u00c8\u0001R\u001a\u0010\u00ca\u0001\u001a\u00030\u00c9\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00ca\u0001\u0010\u00cb\u0001R\u0018\u0010\u00cc\u0001\u001a\u00020(8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u00cc\u0001\u0010iR\u001e\u0010\u00ce\u0001\u001a\t\u0012\u0004\u0012\u00020F0\u00cd\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00ce\u0001\u0010\u00cf\u0001\u00a8\u0006\u00d2\u0001"}, d2={"Lorg/cobalt/internal/farming/FarmingMacroModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "onRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "Lnet/minecraft/class_746;", "player", "startMacro", "(Lnet/minecraft/class_746;)V", "stopMacro", "handleFarming", "beginRowSwitch", "handleRowSwitch", "finishRowSwitch", "handleWaitingForWarp", "finishWarp", "applyFarmingInputs", "applyRowSwitchInputs", "", "Lorg/cobalt/internal/farming/FarmingMacroModule$InputKey;", "inputs", "", "attack", "applyInputs", "(Ljava/util/Set;Z)V", "releaseMovementAndAttack", "forward", "backward", "left", "right", "sprint", "syncMovementKeys", "(ZZZZZZ)V", "", "currentStrategy", "()I", "currentTravelInput", "()Lorg/cobalt/internal/farming/FarmingMacroModule$InputKey;", "currentRowSwitchInput", "currentLaneInput", "value", "laneKeyFromSetting", "(I)Lorg/cobalt/internal/farming/FarmingMacroModule$InputKey;", "rowKeyFromSetting", "blockedNow", "updateMovementHistory", "(Lnet/minecraft/class_746;Z)V", "rememberPosition", "inputKey", "isBlockedInDirection", "(Lnet/minecraft/class_746;Lorg/cobalt/internal/farming/FarmingMacroModule$InputKey;)Z", "", "dx", "dz", "yawDegrees", "movementAlongInput", "(DDLorg/cobalt/internal/farming/FarmingMacroModule$InputKey;D)D", "Lkotlin/Pair;", "inputVector", "(Lorg/cobalt/internal/farming/FarmingMacroModule$InputKey;D)Lkotlin/Pair;", "isDirectionWalkable", "Lnet/minecraft/class_1937;", "level", "Lnet/minecraft/class_2338;", "pos", "canWalkThrough", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)Z", "hasSupportBelow", "maybeSelectTool", "lockRotation", "handleRewarpPointTrigger", "(Lnet/minecraft/class_746;)Z", "point", "isStandingOnRewarpPoint", "(Lnet/minecraft/class_746;Lnet/minecraft/class_2338;)Z", "", "message", "tryStartWarp", "(Ljava/lang/String;)Z", "sendWarpCommand", "()Z", "addCurrentRewarpPoint", "removeNearestRewarpPoint", "loadRewarpPoints", "saveRewarpPoints", "updateRewarpInfo", "updateStateInfo", "currentLaneLabel", "()Ljava/lang/String;", "presetIndex", "applyPreset", "(I)V", "yaw", "normalizeYaw", "(D)D", "REWARP_FILE_NAME", "Ljava/lang/String;", "REWARP_ARM_TICKS", "I", "SUCCESSFUL_TRAVEL_RESET_TICKS", "WARP_ARRIVAL_DIST_SQ", "D", "ROW_SWITCH_TIMEOUT_BUFFER_TICKS", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lcom/google/gson/Gson;", "kotlin.jvm.PlatformType", "gson", "Lcom/google/gson/Gson;", "Ljava/io/File;", "rewarpFile$delegate", "Lkotlin/Lazy;", "getRewarpFile", "()Ljava/io/File;", "rewarpFile", "", "laneKeyOptions", "[Ljava/lang/String;", "rowKeyOptions", "strategyOptions", "presetOptions", "isActive", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabledSetting", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "infoSetting", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "stateInfo", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "presetSetting", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "autoApplyPresetSetting", "Lorg/cobalt/api/module/setting/impl/ActionSetting;", "applyPresetAction", "Lorg/cobalt/api/module/setting/impl/ActionSetting;", "strategySetting", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "yawSetting", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "pitchSetting", "captureRotationSetting", "holdForwardDuringRowsSetting", "laneKeyOneSetting", "laneKeyTwoSetting", "rowSwitchKeySetting", "rowSwitchTicksSetting", "rowSwitchTravelSetting", "collisionTicksSetting", "switchCooldownTicksSetting", "minTravelPerTickSetting", "attackDuringSwitchSetting", "antiStuckSetting", "antiStuckAttemptsSetting", "sprintSetting", "autoSelectToolSetting", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "toolNameSetting", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "pauseInScreensSetting", "rewarpInfo", "autoRewarpSetting", "warpCommandSetting", "rewarpStandTicksSetting", "warpRetryMsSetting", "warpRetryLimitSetting", "postWarpPauseTicksSetting", "renderRewarpsSetting", "addRewarpAction", "removeNearestRewarpAction", "clearRewarpAction", "wasEnabled", "Z", "Lorg/cobalt/internal/farming/FarmingMacroModule$MacroState;", "state", "Lorg/cobalt/internal/farming/FarmingMacroModule$MacroState;", "activeLaneIndex", "blockedTicks", "switchCooldownTicks", "activeTicks", "stableTravelTicks", "unstuckAttempts", "lastX", "lastZ", "hasLastPos", "rowSwitchTicksRemaining", "rowSwitchElapsedTicks", "rowSwitchStartX", "rowSwitchStartZ", "rewarpStandingTicks", "Lnet/minecraft/class_243;", "warpStartPos", "Lnet/minecraft/class_243;", "", "lastWarpAttemptMs", "J", "warpRetryCount", "", "rewarpPoints", "Ljava/util/List;", "MacroState", "InputKey", "cobalt"})
@SourceDebugExtension(value={"SMAP\nFarmingMacroModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FarmingMacroModule.kt\norg/cobalt/internal/farming/FarmingMacroModule\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1108:1\n1915#2,2:1109\n1807#2,3:1111\n1807#2,3:1114\n2469#2,14:1117\n1915#2,2:1132\n1915#2,2:1134\n1#3:1131\n*S KotlinDebug\n*F\n+ 1 FarmingMacroModule.kt\norg/cobalt/internal/farming/FarmingMacroModule\n*L\n466#1:1109,2\n922#1:1111,3\n972#1:1114,3\n984#1:1117,14\n1004#1:1132,2\n1020#1:1134,2\n*E\n"})
public final class FarmingMacroModule
extends Module {
    @NotNull
    public static final FarmingMacroModule INSTANCE = new FarmingMacroModule();
    @NotNull
    private static final String REWARP_FILE_NAME = "farming_rewarps.json";
    private static final int REWARP_ARM_TICKS = 20;
    private static final int SUCCESSFUL_TRAVEL_RESET_TICKS = 12;
    private static final double WARP_ARRIVAL_DIST_SQ = 25.0;
    private static final int ROW_SWITCH_TIMEOUT_BUFFER_TICKS = 12;
    @NotNull
    private static final class_310 mc;
    private static final Gson gson;
    @NotNull
    private static final Lazy rewarpFile$delegate;
    @NotNull
    private static final String[] laneKeyOptions;
    @NotNull
    private static final String[] rowKeyOptions;
    @NotNull
    private static final String[] strategyOptions;
    @NotNull
    private static final String[] presetOptions;
    @NotNull
    private static final CheckboxSetting enabledSetting;
    @NotNull
    private static final InfoSetting infoSetting;
    @NotNull
    private static final InfoSetting stateInfo;
    @NotNull
    private static final ModeSetting presetSetting;
    @NotNull
    private static final CheckboxSetting autoApplyPresetSetting;
    @NotNull
    private static final ActionSetting applyPresetAction;
    @NotNull
    private static final ModeSetting strategySetting;
    @NotNull
    private static final SliderSetting yawSetting;
    @NotNull
    private static final SliderSetting pitchSetting;
    @NotNull
    private static final ActionSetting captureRotationSetting;
    @NotNull
    private static final CheckboxSetting holdForwardDuringRowsSetting;
    @NotNull
    private static final ModeSetting laneKeyOneSetting;
    @NotNull
    private static final ModeSetting laneKeyTwoSetting;
    @NotNull
    private static final ModeSetting rowSwitchKeySetting;
    @NotNull
    private static final SliderSetting rowSwitchTicksSetting;
    @NotNull
    private static final SliderSetting rowSwitchTravelSetting;
    @NotNull
    private static final SliderSetting collisionTicksSetting;
    @NotNull
    private static final SliderSetting switchCooldownTicksSetting;
    @NotNull
    private static final SliderSetting minTravelPerTickSetting;
    @NotNull
    private static final CheckboxSetting attackDuringSwitchSetting;
    @NotNull
    private static final CheckboxSetting antiStuckSetting;
    @NotNull
    private static final SliderSetting antiStuckAttemptsSetting;
    @NotNull
    private static final CheckboxSetting sprintSetting;
    @NotNull
    private static final CheckboxSetting autoSelectToolSetting;
    @NotNull
    private static final TextSetting toolNameSetting;
    @NotNull
    private static final CheckboxSetting pauseInScreensSetting;
    @NotNull
    private static final InfoSetting rewarpInfo;
    @NotNull
    private static final CheckboxSetting autoRewarpSetting;
    @NotNull
    private static final TextSetting warpCommandSetting;
    @NotNull
    private static final SliderSetting rewarpStandTicksSetting;
    @NotNull
    private static final SliderSetting warpRetryMsSetting;
    @NotNull
    private static final SliderSetting warpRetryLimitSetting;
    @NotNull
    private static final SliderSetting postWarpPauseTicksSetting;
    @NotNull
    private static final CheckboxSetting renderRewarpsSetting;
    @NotNull
    private static final ActionSetting addRewarpAction;
    @NotNull
    private static final ActionSetting removeNearestRewarpAction;
    @NotNull
    private static final ActionSetting clearRewarpAction;
    private static boolean wasEnabled;
    @NotNull
    private static MacroState state;
    private static int activeLaneIndex;
    private static int blockedTicks;
    private static int switchCooldownTicks;
    private static int activeTicks;
    private static int stableTravelTicks;
    private static int unstuckAttempts;
    private static double lastX;
    private static double lastZ;
    private static boolean hasLastPos;
    private static int rowSwitchTicksRemaining;
    private static int rowSwitchElapsedTicks;
    private static double rowSwitchStartX;
    private static double rowSwitchStartZ;
    private static int rewarpStandingTicks;
    @Nullable
    private static class_243 warpStartPos;
    private static long lastWarpAttemptMs;
    private static int warpRetryCount;
    @NotNull
    private static final List<class_2338> rewarpPoints;

    private FarmingMacroModule() {
        super("Farming Macro");
    }

    private final File getRewarpFile() {
        Lazy lazy = rewarpFile$delegate;
        return (File)lazy.getValue();
    }

    public final boolean isActive() {
        return (Boolean)enabledSetting.getValue();
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        boolean pauseForScreen;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabledSetting.getValue()).booleanValue()) {
            if (wasEnabled) {
                this.stopMacro();
            }
            wasEnabled = false;
            this.updateStateInfo();
            return;
        }
        class_746 player = FarmingMacroModule.mc.field_1724;
        if (player == null) {
            this.stopMacro();
            wasEnabled = false;
            this.updateStateInfo();
            return;
        }
        if (!wasEnabled) {
            this.startMacro(player);
        }
        wasEnabled = true;
        boolean bl = pauseForScreen = (Boolean)pauseInScreensSetting.getValue() != false && FarmingMacroModule.mc.field_1755 != null && state != MacroState.WAITING_FOR_WARP;
        if (pauseForScreen) {
            this.releaseMovementAndAttack();
            this.updateStateInfo();
            return;
        }
        int n = activeTicks;
        activeTicks = n + 1;
        MovementManager.setLookLock(true);
        this.lockRotation(player);
        this.maybeSelectTool();
        switch (WhenMappings.$EnumSwitchMapping$0[state.ordinal()]) {
            case 1: {
                this.handleFarming(player);
                break;
            }
            case 2: {
                this.handleRowSwitch(player);
                break;
            }
            case 3: {
                this.handleWaitingForWarp(player);
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
        this.updateStateInfo();
    }

    @SubscribeEvent
    public final void onRender(@NotNull WorldRenderEvent.Last event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)renderRewarpsSetting.getValue()).booleanValue() || rewarpPoints.isEmpty()) {
            return;
        }
        class_746 player = FarmingMacroModule.mc.field_1724;
        Iterable $this$forEach$iv = rewarpPoints;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            class_2338 point2 = (class_2338)element$iv;
            boolean bl = false;
            if (player != null && player.method_5649((double)point2.method_10263() + 0.5, (double)point2.method_10264() + 0.5, (double)point2.method_10260() + 0.5) > 4096.0) continue;
            boolean standingOnPoint = player != null && INSTANCE.isStandingOnRewarpPoint(player, point2);
            Color color = standingOnPoint ? new Color(76, 255, 114, 220) : new Color(76, 173, 208, 170);
            double inflate = standingOnPoint ? 0.08 : 0.03;
            class_238 box = new class_238((double)point2.method_10263() - inflate, (double)point2.method_10264() - inflate, (double)point2.method_10260() - inflate, (double)point2.method_10263() + 1.0 + inflate, (double)point2.method_10264() + 1.0 + inflate, (double)point2.method_10260() + 1.0 + inflate);
            Render3D.drawBox(event.getContext(), box, color, true);
        }
    }

    private final void startMacro(class_746 player) {
        if (((Boolean)autoApplyPresetSetting.getValue()).booleanValue()) {
            this.applyPreset(((Number)presetSetting.getValue()).intValue());
        }
        state = MacroState.FARMING;
        activeLaneIndex = 0;
        blockedTicks = 0;
        switchCooldownTicks = 0;
        activeTicks = 0;
        stableTravelTicks = 0;
        unstuckAttempts = 0;
        rowSwitchTicksRemaining = 0;
        rowSwitchElapsedTicks = 0;
        rowSwitchStartX = player.method_23317();
        rowSwitchStartZ = player.method_23321();
        rewarpStandingTicks = 0;
        warpStartPos = null;
        lastWarpAttemptMs = 0L;
        warpRetryCount = 0;
        this.rememberPosition(player);
        MovementManager.setLookLock(true);
        MovementManager.setMovementLock(true);
        this.releaseMovementAndAttack();
    }

    private final void stopMacro() {
        enabledSetting.setValue(false);
        wasEnabled = false;
        state = MacroState.FARMING;
        blockedTicks = 0;
        switchCooldownTicks = 0;
        activeTicks = 0;
        stableTravelTicks = 0;
        unstuckAttempts = 0;
        rowSwitchTicksRemaining = 0;
        rowSwitchElapsedTicks = 0;
        rewarpStandingTicks = 0;
        warpStartPos = null;
        lastWarpAttemptMs = 0L;
        warpRetryCount = 0;
        hasLastPos = false;
        MovementManager.setLookLock(false);
        MovementManager.setMovementLock(false);
        this.releaseMovementAndAttack();
    }

    private final void handleFarming(class_746 player) {
        if (this.handleRewarpPointTrigger(player)) {
            return;
        }
        InputKey travelKey = this.currentTravelInput();
        boolean blockedNow = this.isBlockedInDirection(player, travelKey);
        this.updateMovementHistory(player, blockedNow);
        if (switchCooldownTicks > 0) {
            int n = switchCooldownTicks;
            switchCooldownTicks = n + -1;
        }
        int n = blockedTicks = blockedNow && switchCooldownTicks <= 0 ? blockedTicks + 1 : 0;
        if (blockedTicks >= RangesKt.coerceAtLeast((int)((int)((Number)collisionTicksSetting.getValue()).doubleValue()), (int)1)) {
            this.beginRowSwitch(player);
            return;
        }
        this.applyFarmingInputs();
    }

    private final void beginRowSwitch(class_746 player) {
        int switchTicks;
        int attemptsLimit;
        int n;
        blockedTicks = 0;
        stableTravelTicks = 0;
        if (((Boolean)antiStuckSetting.getValue()).booleanValue() && (unstuckAttempts = (n = unstuckAttempts) + 1) > (attemptsLimit = RangesKt.coerceAtLeast((int)((int)((Number)antiStuckAttemptsSetting.getValue()).doubleValue()), (int)1))) {
            if (this.tryStartWarp("Farming macro: stuck, using warp command.")) {
                return;
            }
            unstuckAttempts = 0;
        }
        if ((switchTicks = RangesKt.coerceAtLeast((int)((int)((Number)rowSwitchTicksSetting.getValue()).doubleValue()), (int)0)) == 0) {
            this.finishRowSwitch(player);
            return;
        }
        state = MacroState.SWITCHING_ROW;
        rowSwitchTicksRemaining = switchTicks;
        rowSwitchElapsedTicks = 0;
        rowSwitchStartX = player.method_23317();
        rowSwitchStartZ = player.method_23321();
        this.applyRowSwitchInputs();
    }

    private final void handleRowSwitch(class_746 player) {
        if (this.handleRewarpPointTrigger(player)) {
            return;
        }
        this.applyRowSwitchInputs();
        int n = rowSwitchTicksRemaining;
        rowSwitchTicksRemaining = n + -1;
        n = rowSwitchElapsedTicks;
        rowSwitchElapsedTicks = n + 1;
        double travelNeeded = RangesKt.coerceAtLeast((double)((Number)rowSwitchTravelSetting.getValue()).doubleValue(), (double)0.0);
        double travelDone = this.movementAlongInput(player.method_23317() - rowSwitchStartX, player.method_23321() - rowSwitchStartZ, this.currentRowSwitchInput(), ((Number)yawSetting.getValue()).doubleValue());
        int minSwitchTicks = RangesKt.coerceAtLeast((int)((int)((Number)rowSwitchTicksSetting.getValue()).doubleValue()), (int)0);
        boolean minimumHoldReached = rowSwitchElapsedTicks >= minSwitchTicks;
        boolean travelReached = travelNeeded <= 0.0 || travelDone >= travelNeeded;
        int timeoutTicks = RangesKt.coerceAtLeast((int)(minSwitchTicks + 12), (int)12);
        if (minimumHoldReached && travelReached || rowSwitchElapsedTicks >= timeoutTicks) {
            this.finishRowSwitch(player);
        }
    }

    private final void finishRowSwitch(class_746 player) {
        activeLaneIndex = 1 - activeLaneIndex;
        state = MacroState.FARMING;
        switchCooldownTicks = RangesKt.coerceAtLeast((int)((int)((Number)switchCooldownTicksSetting.getValue()).doubleValue()), (int)1);
        blockedTicks = 0;
        stableTravelTicks = 0;
        rowSwitchTicksRemaining = 0;
        rowSwitchElapsedTicks = 0;
        rewarpStandingTicks = 0;
        this.rememberPosition(player);
    }

    private final void handleWaitingForWarp(class_746 player) {
        this.releaseMovementAndAttack();
        class_243 startPos = warpStartPos;
        if (startPos != null && player.method_73189().method_1025(startPos) >= 25.0) {
            this.finishWarp(player);
            return;
        }
        long retryDelayMs = RangesKt.coerceAtLeast((long)((long)((Number)warpRetryMsSetting.getValue()).doubleValue()), (long)1000L);
        long now = System.currentTimeMillis();
        if (now - lastWarpAttemptMs < retryDelayMs) {
            return;
        }
        int retryLimit = RangesKt.coerceAtLeast((int)((int)((Number)warpRetryLimitSetting.getValue()).doubleValue()), (int)1);
        if (warpRetryCount >= retryLimit) {
            ChatUtils.sendMessage("Farming macro: warp retry limit reached, stopping.");
            this.stopMacro();
            return;
        }
        if (!this.sendWarpCommand()) {
            ChatUtils.sendMessage("Farming macro: warp command is empty, stopping.");
            this.stopMacro();
            return;
        }
        int n = warpRetryCount;
        warpRetryCount = n + 1;
        lastWarpAttemptMs = now;
    }

    private final void finishWarp(class_746 player) {
        state = MacroState.FARMING;
        activeLaneIndex = 0;
        blockedTicks = 0;
        switchCooldownTicks = RangesKt.coerceAtLeast((int)((int)((Number)postWarpPauseTicksSetting.getValue()).doubleValue()), (int)0);
        stableTravelTicks = 0;
        unstuckAttempts = 0;
        rowSwitchTicksRemaining = 0;
        rowSwitchElapsedTicks = 0;
        rewarpStandingTicks = 0;
        warpStartPos = null;
        lastWarpAttemptMs = 0L;
        warpRetryCount = 0;
        activeTicks = 0;
        this.rememberPosition(player);
        this.releaseMovementAndAttack();
    }

    private final void applyFarmingInputs() {
        LinkedHashSet<InputKey> inputs = new LinkedHashSet<InputKey>();
        switch (this.currentStrategy()) {
            case 0: {
                InputKey laneKey = this.currentLaneInput();
                inputs.add(laneKey);
                if (!((Boolean)holdForwardDuringRowsSetting.getValue()).booleanValue() || laneKey == InputKey.BACKWARD) break;
                inputs.add(InputKey.FORWARD);
                break;
            }
            case 1: {
                inputs.add(InputKey.BACKWARD);
            }
        }
        this.applyInputs((Set<? extends InputKey>)inputs, true);
    }

    private final void applyRowSwitchInputs() {
        InputKey[] inputKeyArray = new InputKey[]{this.currentRowSwitchInput()};
        LinkedHashSet inputs = SetsKt.linkedSetOf((Object[])inputKeyArray);
        this.applyInputs(inputs, (Boolean)attackDuringSwitchSetting.getValue());
    }

    private final void applyInputs(Set<? extends InputKey> inputs, boolean attack) {
        boolean forward = inputs.contains((Object)InputKey.FORWARD);
        boolean backward = inputs.contains((Object)InputKey.BACKWARD);
        boolean left = inputs.contains((Object)InputKey.LEFT);
        boolean right = inputs.contains((Object)InputKey.RIGHT);
        boolean sprint = (Boolean)sprintSetting.getValue() != false && !((Collection)inputs).isEmpty();
        MovementManager.setMovementLock(true);
        MovementManager.setForcedMovement(forward, backward, left, right, false, false, sprint);
        MovementManager.forcedActionsEnabled = true;
        MovementManager.forcedAttack = attack;
        MovementManager.forcedUse = false;
        this.syncMovementKeys(forward, backward, left, right, sprint, attack);
    }

    private final void releaseMovementAndAttack() {
        MovementManager.clearForcedMovement();
        this.syncMovementKeys(false, false, false, false, false, false);
    }

    private final void syncMovementKeys(boolean forward, boolean backward, boolean left, boolean right, boolean sprint, boolean attack) {
        FarmingMacroModule.mc.field_1690.field_1894.method_23481(forward);
        FarmingMacroModule.mc.field_1690.field_1881.method_23481(backward);
        FarmingMacroModule.mc.field_1690.field_1913.method_23481(left);
        FarmingMacroModule.mc.field_1690.field_1849.method_23481(right);
        FarmingMacroModule.mc.field_1690.field_1867.method_23481(sprint);
        FarmingMacroModule.mc.field_1690.field_1886.method_23481(attack);
        FarmingMacroModule.mc.field_1690.field_1904.method_23481(false);
    }

    private final int currentStrategy() {
        return RangesKt.coerceIn((int)((Number)strategySetting.getValue()).intValue(), (int)0, (int)ArraysKt.getLastIndex((Object[])strategyOptions));
    }

    private final InputKey currentTravelInput() {
        return this.currentStrategy() == 1 ? InputKey.BACKWARD : this.currentLaneInput();
    }

    private final InputKey currentRowSwitchInput() {
        return this.currentStrategy() == 1 ? this.currentLaneInput() : this.rowKeyFromSetting(((Number)rowSwitchKeySetting.getValue()).intValue());
    }

    private final InputKey currentLaneInput() {
        ModeSetting setting = activeLaneIndex == 0 ? laneKeyOneSetting : laneKeyTwoSetting;
        return this.laneKeyFromSetting(((Number)setting.getValue()).intValue());
    }

    private final InputKey laneKeyFromSetting(int value) {
        return switch (RangesKt.coerceIn((int)value, (int)0, (int)ArraysKt.getLastIndex((Object[])laneKeyOptions))) {
            case 0 -> InputKey.LEFT;
            case 1 -> InputKey.RIGHT;
            case 2 -> InputKey.BACKWARD;
            default -> InputKey.LEFT;
        };
    }

    private final InputKey rowKeyFromSetting(int value) {
        return RangesKt.coerceIn((int)value, (int)0, (int)ArraysKt.getLastIndex((Object[])rowKeyOptions)) == 1 ? InputKey.BACKWARD : InputKey.FORWARD;
    }

    private final void updateMovementHistory(class_746 player, boolean blockedNow) {
        if (blockedNow) {
            stableTravelTicks = 0;
        } else if ((stableTravelTicks = RangesKt.coerceAtMost((int)(stableTravelTicks + 1), (int)12)) >= 12) {
            unstuckAttempts = 0;
        }
        this.rememberPosition(player);
    }

    private final void rememberPosition(class_746 player) {
        lastX = player.method_23317();
        lastZ = player.method_23321();
        hasLastPos = true;
    }

    private final boolean isBlockedInDirection(class_746 player, InputKey inputKey) {
        boolean directionWalkable = this.isDirectionWalkable(player, inputKey);
        if (!hasLastPos) {
            return player.field_5976 || !directionWalkable;
        }
        boolean blockedNow = player.field_5976;
        double dx = player.method_23317() - lastX;
        double dz = player.method_23321() - lastZ;
        double travel = this.movementAlongInput(dx, dz, inputKey, ((Number)yawSetting.getValue()).doubleValue());
        double minTravel = RangesKt.coerceAtLeast((double)((Number)minTravelPerTickSetting.getValue()).doubleValue(), (double)0.0);
        if (activeTicks > 5 && !directionWalkable && travel <= minTravel && player.method_24828()) {
            blockedNow = true;
        }
        return blockedNow;
    }

    private final double movementAlongInput(double dx, double dz, InputKey inputKey, double yawDegrees) {
        Pair<Double, Double> vector = this.inputVector(inputKey, yawDegrees);
        return dx * ((Number)vector.getFirst()).doubleValue() + dz * ((Number)vector.getSecond()).doubleValue();
    }

    private final Pair<Double, Double> inputVector(InputKey inputKey, double yawDegrees) {
        double forwardZ;
        double yawRad = Math.toRadians(this.normalizeYaw(yawDegrees));
        double forwardX = -Math.sin(yawRad);
        double rightX = forwardZ = Math.cos(yawRad);
        double rightZ = -forwardX;
        Pair vector = switch (WhenMappings.$EnumSwitchMapping$1[inputKey.ordinal()]) {
            case 1 -> new Pair((Object)forwardX, (Object)forwardZ);
            case 2 -> new Pair((Object)(-forwardX), (Object)(-forwardZ));
            case 3 -> new Pair((Object)(-rightX), (Object)(-rightZ));
            case 4 -> new Pair((Object)rightX, (Object)rightZ);
            default -> throw new NoWhenBranchMatchedException();
        };
        return vector;
    }

    private final boolean isDirectionWalkable(class_746 player, InputKey inputKey) {
        class_638 class_6382 = FarmingMacroModule.mc.field_1687;
        if (class_6382 == null) {
            return true;
        }
        class_638 level2 = class_6382;
        Pair<Double, Double> vector = this.inputVector(inputKey, ((Number)yawSetting.getValue()).doubleValue());
        class_2338 class_23382 = class_2338.method_49637((double)player.method_23317(), (double)player.method_23318(), (double)player.method_23321());
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"containing(...)");
        class_2338 currentFeet = class_23382;
        class_2338 class_23383 = class_2338.method_49637((double)(player.method_23317() + ((Number)vector.getFirst()).doubleValue() * 0.85), (double)player.method_23318(), (double)(player.method_23321() + ((Number)vector.getSecond()).doubleValue() * 0.85));
        Intrinsics.checkNotNullExpressionValue((Object)class_23383, (String)"containing(...)");
        class_2338 targetFeet = class_23383;
        if (Intrinsics.areEqual((Object)targetFeet, (Object)currentFeet)) {
            class_2338 class_23384 = class_2338.method_49637((double)(player.method_23317() + ((Number)vector.getFirst()).doubleValue() * 1.15), (double)player.method_23318(), (double)(player.method_23321() + ((Number)vector.getSecond()).doubleValue() * 1.15));
            Intrinsics.checkNotNullExpressionValue((Object)class_23384, (String)"containing(...)");
            targetFeet = class_23384;
        }
        class_2338 class_23385 = targetFeet.method_10084();
        Intrinsics.checkNotNullExpressionValue((Object)class_23385, (String)"above(...)");
        class_2338 headPos = class_23385;
        class_2338 class_23386 = targetFeet.method_10074();
        Intrinsics.checkNotNullExpressionValue((Object)class_23386, (String)"below(...)");
        class_2338 floorPos = class_23386;
        return this.canWalkThrough((class_1937)level2, targetFeet) && this.canWalkThrough((class_1937)level2, headPos) && this.hasSupportBelow((class_1937)level2, floorPos);
    }

    private final boolean canWalkThrough(class_1937 level2, class_2338 pos) {
        class_2680 class_26802 = level2.method_8320(pos);
        Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
        class_2680 state = class_26802;
        if (state.method_26215()) {
            return true;
        }
        if (!state.method_26227().method_15769()) {
            return false;
        }
        return state.method_26220((class_1922)level2, pos).method_1110();
    }

    private final boolean hasSupportBelow(class_1937 level2, class_2338 pos) {
        class_2680 class_26802 = level2.method_8320(pos);
        Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
        class_2680 state = class_26802;
        return !state.method_26220((class_1922)level2, pos).method_1110();
    }

    private final void maybeSelectTool() {
        if (!((Boolean)autoSelectToolSetting.getValue()).booleanValue()) {
            return;
        }
        String toolName = ((Object)StringsKt.trim((CharSequence)((String)toolNameSetting.getValue()))).toString();
        if (((CharSequence)toolName).length() == 0) {
            return;
        }
        int slot = InventoryUtils.findItemInHotbar(toolName);
        boolean bl = 0 <= slot ? slot < 9 : false;
        if (bl) {
            InventoryUtils.holdHotbarSlot(slot);
        }
    }

    private final void lockRotation(class_746 player) {
        float yaw = (float)this.normalizeYaw(((Number)yawSetting.getValue()).doubleValue());
        float pitch = RangesKt.coerceIn((float)((float)((Number)pitchSetting.getValue()).doubleValue()), (float)-89.0f, (float)89.0f);
        player.method_36456(yaw);
        player.method_36457(pitch);
        player.field_6241 = yaw;
        player.field_6283 = yaw;
    }

    private final boolean handleRewarpPointTrigger(class_746 player) {
        boolean standing;
        block6: {
            if (!((Boolean)autoRewarpSetting.getValue()).booleanValue() || activeTicks < 20 || rewarpPoints.isEmpty()) {
                rewarpStandingTicks = 0;
                return false;
            }
            Iterable $this$any$iv = rewarpPoints;
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                v0 = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    class_2338 it = (class_2338)element$iv;
                    boolean bl = false;
                    if (!INSTANCE.isStandingOnRewarpPoint(player, it)) continue;
                    v0 = true;
                    break block6;
                }
                v0 = standing = false;
            }
        }
        if (!standing) {
            rewarpStandingTicks = 0;
            return false;
        }
        int $this$any$iv = rewarpStandingTicks;
        int triggerTicks = RangesKt.coerceAtLeast((int)((int)((Number)rewarpStandTicksSetting.getValue()).doubleValue()), (int)1);
        if ((rewarpStandingTicks = $this$any$iv + 1) < triggerTicks) {
            return false;
        }
        return this.tryStartWarp("Farming macro: standing on rewarp point, warping.");
    }

    private final boolean isStandingOnRewarpPoint(class_746 player, class_2338 point2) {
        double centerX = (double)point2.method_10263() + 0.5;
        double centerZ = (double)point2.method_10260() + 0.5;
        double dx = player.method_23317() - centerX;
        double dz = player.method_23321() - centerZ;
        double dy = Math.abs(player.method_23318() - (double)point2.method_10264());
        return dx * dx + dz * dz <= 0.81 && dy <= 1.5;
    }

    private final boolean tryStartWarp(String message) {
        if (state == MacroState.WAITING_FOR_WARP) {
            return true;
        }
        if (!this.sendWarpCommand()) {
            return false;
        }
        state = MacroState.WAITING_FOR_WARP;
        class_746 class_7462 = FarmingMacroModule.mc.field_1724;
        warpStartPos = class_7462 != null ? class_7462.method_73189() : null;
        lastWarpAttemptMs = System.currentTimeMillis();
        warpRetryCount = 0;
        rewarpStandingTicks = 0;
        this.releaseMovementAndAttack();
        ChatUtils.sendMessage(message);
        return true;
    }

    private final boolean sendWarpCommand() {
        block1: {
            String trimmed = StringsKt.removePrefix((String)((Object)StringsKt.trim((CharSequence)((String)warpCommandSetting.getValue()))).toString(), (CharSequence)"/");
            if (((CharSequence)trimmed).length() == 0) {
                return false;
            }
            class_746 class_7462 = FarmingMacroModule.mc.field_1724;
            if (class_7462 == null || (class_7462 = class_7462.field_3944) == null) break block1;
            class_7462.method_45730(trimmed);
        }
        return true;
    }

    private final void addCurrentRewarpPoint() {
        boolean bl;
        class_2338 point2;
        block5: {
            class_746 class_7462 = FarmingMacroModule.mc.field_1724;
            if (class_7462 == null) {
                return;
            }
            class_746 player = class_7462;
            class_2338 class_23382 = player.method_24515();
            Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"blockPosition(...)");
            point2 = class_23382;
            Iterable $this$any$iv = rewarpPoints;
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                bl = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    class_2338 it = (class_2338)element$iv;
                    boolean bl2 = false;
                    if (!Intrinsics.areEqual((Object)it, (Object)point2)) continue;
                    bl = true;
                    break block5;
                }
                bl = false;
            }
        }
        if (bl) {
            ChatUtils.sendMessage("Farming macro: that rewarp point already exists.");
            return;
        }
        rewarpPoints.add(point2);
        this.saveRewarpPoints();
        this.updateRewarpInfo();
        ChatUtils.sendMessage("Farming macro: added rewarp at " + point2.method_10263() + " " + point2.method_10264() + " " + point2.method_10260() + ".");
    }

    private final void removeNearestRewarpPoint() {
        Object v1;
        class_746 class_7462 = FarmingMacroModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        Iterable $this$minByOrNull$iv = rewarpPoints;
        boolean $i$f$minByOrNull = false;
        Iterator iterator$iv = $this$minByOrNull$iv.iterator();
        if (!iterator$iv.hasNext()) {
            v1 = null;
        } else {
            Object minElem$iv = iterator$iv.next();
            if (!iterator$iv.hasNext()) {
                v1 = minElem$iv;
            } else {
                class_2338 it = (class_2338)minElem$iv;
                boolean bl = false;
                double minValue$iv = player.method_5649((double)it.method_10263() + 0.5, (double)it.method_10264() + 0.5, (double)it.method_10260() + 0.5);
                do {
                    Object e$iv = iterator$iv.next();
                    class_2338 it2 = (class_2338)e$iv;
                    $i$a$-minByOrNull-FarmingMacroModule$removeNearestRewarpPoint$nearest$1 = false;
                    double v$iv = player.method_5649((double)it2.method_10263() + 0.5, (double)it2.method_10264() + 0.5, (double)it2.method_10260() + 0.5);
                    if (Double.compare(minValue$iv, v$iv) <= 0) continue;
                    minElem$iv = e$iv;
                    minValue$iv = v$iv;
                } while (iterator$iv.hasNext());
                v1 = minElem$iv;
            }
        }
        class_2338 nearest = v1;
        if (nearest == null) {
            ChatUtils.sendMessage("Farming macro: no rewarp points saved.");
            return;
        }
        rewarpPoints.remove(nearest);
        this.saveRewarpPoints();
        this.updateRewarpInfo();
        ChatUtils.sendMessage("Farming macro: removed rewarp at " + nearest.method_10263() + " " + nearest.method_10264() + " " + nearest.method_10260() + ".");
    }

    private final void loadRewarpPoints() {
        Object $this$loadRewarpPoints_u24lambda_u241;
        String text;
        Object $this$loadRewarpPoints_u24lambda_u240;
        rewarpPoints.clear();
        if (!this.getRewarpFile().exists()) {
            return;
        }
        Object object = this;
        try {
            $this$loadRewarpPoints_u24lambda_u240 = object;
            boolean bl = false;
            $this$loadRewarpPoints_u24lambda_u240 = Result.constructor-impl((Object)FilesKt.readText$default((File)super.getRewarpFile(), null, (int)1, null));
        }
        catch (Throwable bl) {
            $this$loadRewarpPoints_u24lambda_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
        }
        object = $this$loadRewarpPoints_u24lambda_u240;
        String string = (String)(Result.isFailure-impl((Object)object) ? null : object);
        String string2 = string != null ? ((Object)StringsKt.trim((CharSequence)string)).toString() : null;
        if (string2 == null) {
            string2 = "";
        }
        if (((CharSequence)(text = string2)).length() == 0) {
            return;
        }
        $this$loadRewarpPoints_u24lambda_u240 = this;
        try {
            $this$loadRewarpPoints_u24lambda_u241 = (FarmingMacroModule)$this$loadRewarpPoints_u24lambda_u240;
            boolean bl = false;
            $this$loadRewarpPoints_u24lambda_u241 = Result.constructor-impl((Object)JsonParser.parseString((String)text).getAsJsonObject());
        }
        catch (Throwable throwable) {
            $this$loadRewarpPoints_u24lambda_u241 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        $this$loadRewarpPoints_u24lambda_u240 = $this$loadRewarpPoints_u24lambda_u241;
        JsonObject jsonObject = (JsonObject)(Result.isFailure-impl((Object)$this$loadRewarpPoints_u24lambda_u240) ? null : $this$loadRewarpPoints_u24lambda_u240);
        if (jsonObject == null) {
            return;
        }
        JsonObject root = jsonObject;
        JsonArray jsonArray = root.getAsJsonArray("points");
        if (jsonArray == null) {
            return;
        }
        JsonArray points = jsonArray;
        Iterable $this$forEach$iv = (Iterable)points;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            JsonObject obj;
            Object $this$loadRewarpPoints_u24lambda_u242_u240;
            JsonElement element = (JsonElement)element$iv;
            boolean bl = false;
            Object object2 = INSTANCE;
            try {
                $this$loadRewarpPoints_u24lambda_u242_u240 = object2;
                boolean bl2 = false;
                $this$loadRewarpPoints_u24lambda_u242_u240 = Result.constructor-impl((Object)element.getAsJsonObject());
            }
            catch (Throwable throwable) {
                $this$loadRewarpPoints_u24lambda_u242_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
            }
            object2 = $this$loadRewarpPoints_u24lambda_u242_u240;
            if ((JsonObject)(Result.isFailure-impl((Object)object2) ? null : object2) == null) continue;
            JsonElement jsonElement = obj.get("x");
            if (jsonElement == null) {
                continue;
            }
            int x = jsonElement.getAsInt();
            JsonElement jsonElement2 = obj.get("y");
            if (jsonElement2 == null) {
                continue;
            }
            int y = jsonElement2.getAsInt();
            JsonElement jsonElement3 = obj.get("z");
            if (jsonElement3 == null) {
                continue;
            }
            int z = jsonElement3.getAsInt();
            rewarpPoints.add(new class_2338(x, y, z));
        }
    }

    private final void saveRewarpPoints() {
        if (!this.getRewarpFile().getParentFile().exists()) {
            this.getRewarpFile().getParentFile().mkdirs();
        }
        JsonObject root = new JsonObject();
        JsonArray points = new JsonArray();
        Iterable $this$forEach$iv = rewarpPoints;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            class_2338 point2 = (class_2338)element$iv;
            boolean bl = false;
            JsonObject obj = new JsonObject();
            obj.addProperty("x", (Number)point2.method_10263());
            obj.addProperty("y", (Number)point2.method_10264());
            obj.addProperty("z", (Number)point2.method_10260());
            points.add((JsonElement)obj);
        }
        root.add("points", (JsonElement)points);
        File file = this.getRewarpFile();
        String string = gson.toJson((JsonElement)root);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toJson(...)");
        FilesKt.writeText$default((File)file, (String)string, null, (int)2, null);
    }

    private final void updateRewarpInfo() {
        rewarpInfo.setValue(rewarpPoints.size() + " points");
    }

    private final void updateStateInfo() {
        stateInfo.setValue((Boolean)enabledSetting.getValue() == false ? "Idle" : (state == MacroState.WAITING_FOR_WARP ? "Warping (" + warpRetryCount + "/" + (int)((Number)warpRetryLimitSetting.getValue()).doubleValue() + ")" : (state == MacroState.SWITCHING_ROW ? "Switching Row" : (this.currentStrategy() == 1 ? "Farming (" + this.currentLaneLabel() + ")" : "Farming (" + this.currentLaneLabel() + " + " + ((Boolean)holdForwardDuringRowsSetting.getValue() != false ? "W" : "-") + ")"))));
    }

    private final String currentLaneLabel() {
        return activeLaneIndex == 0 ? laneKeyOptions[RangesKt.coerceIn((int)((Number)laneKeyOneSetting.getValue()).intValue(), (int)0, (int)ArraysKt.getLastIndex((Object[])laneKeyOptions))] : laneKeyOptions[RangesKt.coerceIn((int)((Number)laneKeyTwoSetting.getValue()).intValue(), (int)0, (int)ArraysKt.getLastIndex((Object[])laneKeyOptions))];
    }

    private final void applyPreset(int presetIndex) {
        switch (RangesKt.coerceIn((int)presetIndex, (int)0, (int)ArraysKt.getLastIndex((Object[])presetOptions))) {
            case 1: {
                strategySetting.setValue(0);
                pitchSetting.setValue(3.0);
                laneKeyOneSetting.setValue(0);
                laneKeyTwoSetting.setValue(1);
                rowSwitchKeySetting.setValue(0);
                holdForwardDuringRowsSetting.setValue(true);
                rowSwitchTicksSetting.setValue(3.0);
                rowSwitchTravelSetting.setValue(0.6);
                minTravelPerTickSetting.setValue(0.02);
                break;
            }
            case 2: {
                strategySetting.setValue(0);
                pitchSetting.setValue(0.0);
                laneKeyOneSetting.setValue(0);
                laneKeyTwoSetting.setValue(1);
                rowSwitchKeySetting.setValue(0);
                holdForwardDuringRowsSetting.setValue(true);
                rowSwitchTicksSetting.setValue(3.0);
                rowSwitchTravelSetting.setValue(0.6);
                minTravelPerTickSetting.setValue(0.02);
                break;
            }
            case 3: {
                strategySetting.setValue(0);
                pitchSetting.setValue(29.0);
                laneKeyOneSetting.setValue(0);
                laneKeyTwoSetting.setValue(1);
                rowSwitchKeySetting.setValue(0);
                holdForwardDuringRowsSetting.setValue(true);
                rowSwitchTicksSetting.setValue(4.0);
                rowSwitchTravelSetting.setValue(0.75);
                minTravelPerTickSetting.setValue(0.02);
                break;
            }
            case 4: {
                strategySetting.setValue(1);
                pitchSetting.setValue(0.0);
                laneKeyOneSetting.setValue(0);
                laneKeyTwoSetting.setValue(1);
                holdForwardDuringRowsSetting.setValue(false);
                rowSwitchTicksSetting.setValue(4.0);
                rowSwitchTravelSetting.setValue(0.7);
                minTravelPerTickSetting.setValue(0.015);
            }
        }
    }

    private final double normalizeYaw(double yaw) {
        double result;
        for (result = yaw; result > 180.0; result -= 360.0) {
        }
        while (result <= -180.0) {
            result += 360.0;
        }
        return Math.abs(result) < 1.0E-6 ? 0.0 : result;
    }

    private static final File rewarpFile_delegate$lambda$0() {
        return new File(FarmingMacroModule.mc.field_1697, "config/cobalt/farming_rewarps.json");
    }

    private static final Unit applyPresetAction$lambda$0() {
        INSTANCE.applyPreset(((Number)presetSetting.getValue()).intValue());
        return Unit.INSTANCE;
    }

    private static final Unit captureRotationSetting$lambda$0() {
        class_746 class_7462 = FarmingMacroModule.mc.field_1724;
        if (class_7462 == null) {
            return Unit.INSTANCE;
        }
        class_746 player = class_7462;
        yawSetting.setValue(INSTANCE.normalizeYaw(player.method_36454()));
        pitchSetting.setValue(RangesKt.coerceIn((double)player.method_36455(), (double)-89.0, (double)89.0));
        return Unit.INSTANCE;
    }

    private static final Unit addRewarpAction$lambda$0() {
        INSTANCE.addCurrentRewarpPoint();
        return Unit.INSTANCE;
    }

    private static final Unit removeNearestRewarpAction$lambda$0() {
        INSTANCE.removeNearestRewarpPoint();
        return Unit.INSTANCE;
    }

    private static final Unit clearRewarpAction$lambda$0() {
        rewarpPoints.clear();
        INSTANCE.saveRewarpPoints();
        INSTANCE.updateRewarpInfo();
        ChatUtils.sendMessage("Farming macro: cleared all rewarp points.");
        return Unit.INSTANCE;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        gson = new GsonBuilder().setPrettyPrinting().create();
        rewarpFile$delegate = LazyKt.lazy(FarmingMacroModule::rewarpFile_delegate$lambda$0);
        Object[] objectArray = new String[]{"A", "D", "S"};
        laneKeyOptions = objectArray;
        objectArray = new String[]{"W", "S"};
        rowKeyOptions = objectArray;
        objectArray = new String[]{"Vertical", "Sugar Cane"};
        strategyOptions = objectArray;
        objectArray = new String[]{"Custom", "Vertical Crop", "Nether Wart", "Pumpkin/Melon", "Sugar Cane"};
        presetOptions = objectArray;
        enabledSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Enabled", "FarmHelper-style lane farming with rewarp and stuck recovery.", false), "Macro");
        infoSetting = (InfoSetting)SettingKt.inGroup((Setting)new InfoSetting("Macro", "FarmHelper-inspired first pass: presets, row switching, anti-stuck, and rewarp points.", InfoType.INFO), "Macro");
        stateInfo = (InfoSetting)SettingKt.inGroup((Setting)new InfoSetting("State", "Idle", InfoType.INFO), "Macro");
        presetSetting = (ModeSetting)SettingKt.inGroup((Setting)new ModeSetting("Preset", "Optional FarmHelper preset. Use Apply Preset to fill pitch and strategy values.", 0, presetOptions), "Macro");
        autoApplyPresetSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Auto Apply Preset", "Apply the selected preset whenever the macro starts.", false), "Macro");
        applyPresetAction = (ActionSetting)SettingKt.inGroup((Setting)new ActionSetting("Apply Preset", "Apply the selected FarmHelper preset to this module.", "Apply", null, FarmingMacroModule::applyPresetAction$lambda$0, 8, null), "Macro");
        strategySetting = (ModeSetting)SettingKt.inGroup((Setting)new ModeSetting("Strategy", "Vertical uses A/D rows with W or S row changes. Sugar Cane uses S rows with A/D switches.", 0, strategyOptions), "Macro");
        yawSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Yaw", "Locked camera yaw.", 0.0, -180.0, 180.0, 1.0), "Macro");
        pitchSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Pitch", "Locked camera pitch.", 70.0, -90.0, 90.0, 1.0), "Macro");
        captureRotationSetting = (ActionSetting)SettingKt.inGroup((Setting)new ActionSetting("Capture Rotation", "Copy your current yaw and pitch into the lock settings.", "Capture", null, FarmingMacroModule::captureRotationSetting$lambda$0, 8, null), "Macro");
        holdForwardDuringRowsSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Hold W During Rows", "Vertical strategy holds W while moving along A/D crop rows.", true), "Movement");
        laneKeyOneSetting = (ModeSetting)SettingKt.inGroup((Setting)new ModeSetting("Lane Key 1", "Primary lane key.", 0, laneKeyOptions), "Movement");
        laneKeyTwoSetting = (ModeSetting)SettingKt.inGroup((Setting)new ModeSetting("Lane Key 2", "Secondary lane key.", 1, laneKeyOptions), "Movement");
        rowSwitchKeySetting = (ModeSetting)SettingKt.inGroup((Setting)new ModeSetting("Row Switch Key", "Vertical strategy key used to step onto the next row.", 0, rowKeyOptions), "Movement");
        rowSwitchTicksSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Row Switch Ticks", "Ticks to hold the row-switch key before flipping lanes. Set to 0 to keep the old instant flip.", 3.0, 0.0, 20.0, 1.0), "Movement");
        rowSwitchTravelSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Row Switch Travel", "Travel along the row-switch direction needed before the switch can finish.", 0.6, 0.0, 2.0, 0.05), "Movement");
        collisionTicksSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Collision Ticks", "Ticks of blocked travel before row switching.", 4.0, 1.0, 20.0, 1.0), "Recovery");
        switchCooldownTicksSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Switch Cooldown", "Ticks to wait after each lane swap.", 10.0, 1.0, 30.0, 1.0), "Recovery");
        minTravelPerTickSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Min Travel", "Minimum travel in the active direction before it counts as blocked.", 0.02, 0.0, 0.2, 0.0, 32, null), "Recovery");
        attackDuringSwitchSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Attack During Switch", "Keep breaking crops while switching rows.", true), "Recovery");
        antiStuckSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Anti Stuck", "Count repeated failed row switches and rewarp when the macro stays stuck.", true), "Recovery");
        antiStuckAttemptsSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Attempts Until Rewarp", "Consecutive failed row switches before the macro uses the warp command.", 5.0, 1.0, 12.0, 1.0), "Recovery");
        sprintSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Sprint", "Hold sprint while farming.", false), "Recovery");
        autoSelectToolSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Auto Select Tool", "Select the first hotbar item whose name contains the tool text.", true), "Tool");
        toolNameSetting = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("Tool Name", "Hotbar item name fragment to select before farming.", "hoe"), "Tool");
        pauseInScreensSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Pause In Screens", "Release movement and attack while a screen is open.", true), "Tool");
        rewarpInfo = (InfoSetting)SettingKt.inGroup((Setting)new InfoSetting("Rewarps", "0 points", InfoType.INFO), "Rewarp");
        autoRewarpSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Auto Rewarp", "Use the warp command when you stand on a saved rewarp point.", true), "Rewarp");
        warpCommandSetting = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("Warp Command", "Command used for FarmHelper-style reset. Slash is optional.", "warp garden"), "Rewarp");
        rewarpStandTicksSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Trigger Ticks", "Ticks spent standing on a rewarp point before warping.", 4.0, 1.0, 20.0, 1.0), "Rewarp");
        warpRetryMsSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Retry Delay", "Milliseconds between warp retries if the position never changes.", 5000.0, 1000.0, 10000.0, 250.0), "Rewarp");
        warpRetryLimitSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Retry Limit", "How many times the warp command can be retried before the macro stops.", 3.0, 1.0, 10.0, 1.0), "Rewarp");
        postWarpPauseTicksSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Post Warp Pause", "Ticks to wait after a successful warp before checking for blocked travel again.", 15.0, 0.0, 60.0, 1.0), "Rewarp");
        renderRewarpsSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Render Rewarps", "Draw saved rewarp points in the world.", true), "Rewarp");
        addRewarpAction = (ActionSetting)SettingKt.inGroup((Setting)new ActionSetting("Add Rewarp", "Save your current block as a rewarp point.", "Add", null, FarmingMacroModule::addRewarpAction$lambda$0, 8, null), "Rewarp");
        removeNearestRewarpAction = (ActionSetting)SettingKt.inGroup((Setting)new ActionSetting("Remove Nearest", "Remove the closest saved rewarp point.", "Remove", null, FarmingMacroModule::removeNearestRewarpAction$lambda$0, 8, null), "Rewarp");
        clearRewarpAction = (ActionSetting)SettingKt.inGroup((Setting)new ActionSetting("Clear Rewarps", "Remove every saved rewarp point.", "Clear", null, FarmingMacroModule::clearRewarpAction$lambda$0, 8, null), "Rewarp");
        state = MacroState.FARMING;
        rewarpPoints = new ArrayList();
        objectArray = new Setting[]{enabledSetting, infoSetting, stateInfo, presetSetting, autoApplyPresetSetting, applyPresetAction, strategySetting, yawSetting, pitchSetting, captureRotationSetting, holdForwardDuringRowsSetting, laneKeyOneSetting, laneKeyTwoSetting, rowSwitchKeySetting, rowSwitchTicksSetting, rowSwitchTravelSetting, collisionTicksSetting, switchCooldownTicksSetting, minTravelPerTickSetting, attackDuringSwitchSetting, antiStuckSetting, antiStuckAttemptsSetting, sprintSetting, autoSelectToolSetting, toolNameSetting, pauseInScreensSetting, rewarpInfo, autoRewarpSetting, warpCommandSetting, rewarpStandTicksSetting, warpRetryMsSetting, warpRetryLimitSetting, postWarpPauseTicksSetting, renderRewarpsSetting, addRewarpAction, removeNearestRewarpAction, clearRewarpAction};
        INSTANCE.addSetting((Setting<?>[])objectArray);
        INSTANCE.loadRewarpPoints();
        INSTANCE.updateRewarpInfo();
        INSTANCE.updateStateInfo();
        EventBus.register(INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007\u00a8\u0006\b"}, d2={"Lorg/cobalt/internal/farming/FarmingMacroModule$InputKey;", "", "<init>", "(Ljava/lang/String;I)V", "FORWARD", "BACKWARD", "LEFT", "RIGHT", "cobalt"})
    private static final class InputKey
    extends Enum<InputKey> {
        public static final /* enum */ InputKey FORWARD = new InputKey();
        public static final /* enum */ InputKey BACKWARD = new InputKey();
        public static final /* enum */ InputKey LEFT = new InputKey();
        public static final /* enum */ InputKey RIGHT = new InputKey();
        private static final /* synthetic */ InputKey[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        public static InputKey[] values() {
            return (InputKey[])$VALUES.clone();
        }

        public static InputKey valueOf(String value) {
            return Enum.valueOf(InputKey.class, value);
        }

        @NotNull
        public static EnumEntries<InputKey> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = inputKeyArray = new InputKey[]{InputKey.FORWARD, InputKey.BACKWARD, InputKey.LEFT, InputKey.RIGHT};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/internal/farming/FarmingMacroModule$MacroState;", "", "<init>", "(Ljava/lang/String;I)V", "FARMING", "SWITCHING_ROW", "WAITING_FOR_WARP", "cobalt"})
    private static final class MacroState
    extends Enum<MacroState> {
        public static final /* enum */ MacroState FARMING = new MacroState();
        public static final /* enum */ MacroState SWITCHING_ROW = new MacroState();
        public static final /* enum */ MacroState WAITING_FOR_WARP = new MacroState();
        private static final /* synthetic */ MacroState[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        public static MacroState[] values() {
            return (MacroState[])$VALUES.clone();
        }

        public static MacroState valueOf(String value) {
            return Enum.valueOf(MacroState.class, value);
        }

        @NotNull
        public static EnumEntries<MacroState> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = macroStateArray = new MacroState[]{MacroState.FARMING, MacroState.SWITCHING_ROW, MacroState.WAITING_FOR_WARP};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;
        public static final /* synthetic */ int[] $EnumSwitchMapping$1;

        static {
            int[] nArray = new int[MacroState.values().length];
            try {
                nArray[MacroState.FARMING.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[MacroState.SWITCHING_ROW.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[MacroState.WAITING_FOR_WARP.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
            nArray = new int[InputKey.values().length];
            try {
                nArray[InputKey.FORWARD.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[InputKey.BACKWARD.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[InputKey.LEFT.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[InputKey.RIGHT.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$1 = nArray;
        }
    }
}

