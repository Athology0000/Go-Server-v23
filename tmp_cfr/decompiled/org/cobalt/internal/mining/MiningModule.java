/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.Unit
 *  kotlin.collections.ArraysKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.io.CloseableKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.Charsets
 *  kotlin.text.StringsKt
 *  net.minecraft.class_124
 *  net.minecraft.class_1735
 *  net.minecraft.class_1799
 *  net.minecraft.class_1836
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_239
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_2561
 *  net.minecraft.class_2596
 *  net.minecraft.class_2675
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_3965
 *  net.minecraft.class_437
 *  net.minecraft.class_465
 *  net.minecraft.class_634
 *  net.minecraft.class_638
 *  net.minecraft.class_640
 *  net.minecraft.class_746
 *  net.minecraft.class_7472
 *  net.minecraft.class_7923
 *  net.minecraft.class_9449
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.mining;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import net.minecraft.class_124;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_1836;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2675;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_634;
import net.minecraft.class_638;
import net.minecraft.class_640;
import net.minecraft.class_746;
import net.minecraft.class_7472;
import net.minecraft.class_7923;
import net.minecraft.class_9449;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.BlockChangeEvent;
import org.cobalt.api.event.impl.client.ChatEvent;
import org.cobalt.api.event.impl.client.PacketEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.ActionSetting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.ModeSetting;
import org.cobalt.api.module.setting.impl.RangeSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.module.setting.impl.TextSetting;
import org.cobalt.api.notification.NotificationManager;
import org.cobalt.api.util.InventoryUtils;
import org.cobalt.api.util.ItemUtilsKt;
import org.cobalt.internal.mining.AutoLanternModule;
import org.cobalt.internal.mining.MiningBlockRegistry;
import org.cobalt.internal.mining.MiningBreakThresholds;
import org.cobalt.internal.mining.MiningMacroModule;
import org.cobalt.internal.mining.MiningNukerController;
import org.cobalt.internal.mining.MiningPrecisionTracker;
import org.cobalt.internal.rotation.RotationsModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0088\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b,\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\t\n\u0002\b\u0014\n\u0002\u0018\u0002\n\u0002\b\u0011\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0004\u00fc\u0001\u00fd\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\n\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\tH\u0007\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0017\u0010\r\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\fH\u0007\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0017\u0010\u0011\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u000fH\u0002\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u001b\u0010\u0015\u001a\u00020\u00062\n\u0010\u0014\u001a\u0006\u0012\u0002\b\u00030\u0013H\u0002\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u001b\u0010\u0017\u001a\u00020\u00062\n\u0010\u0014\u001a\u0006\u0012\u0002\b\u00030\u0013H\u0002\u00a2\u0006\u0004\b\u0017\u0010\u0016J'\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00190\u00182\n\u0010\u0014\u001a\u0006\u0012\u0002\b\u00030\u0013H\u0002\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u001f\u0010 \u001a\u0004\u0018\u00010\u001f2\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001cH\u0002\u00a2\u0006\u0004\b \u0010!J\u001f\u0010\"\u001a\u0004\u0018\u00010\u00192\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001cH\u0002\u00a2\u0006\u0004\b\"\u0010#J\u000f\u0010$\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b$\u0010\u0003J\u000f\u0010%\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b%\u0010\u0003J\u000f\u0010&\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b&\u0010\u0003J\u0017\u0010)\u001a\u00020\u001f2\b\b\u0002\u0010(\u001a\u00020'\u00a2\u0006\u0004\b)\u0010*J\u0017\u0010,\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020+H\u0007\u00a2\u0006\u0004\b,\u0010-J\u0017\u0010/\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020.H\u0007\u00a2\u0006\u0004\b/\u00100J\u000f\u00101\u001a\u00020\u001fH\u0002\u00a2\u0006\u0004\b1\u00102J\u000f\u00103\u001a\u00020\u0019H\u0002\u00a2\u0006\u0004\b3\u00104J\u000f\u00105\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b5\u0010\u0003J\u001d\u00107\u001a\u0004\u0018\u0001062\n\u0010\u0014\u001a\u0006\u0012\u0002\b\u00030\u0013H\u0002\u00a2\u0006\u0004\b7\u00108J#\u0010:\u001a\u00020\u00062\n\u0010\u0014\u001a\u0006\u0012\u0002\b\u00030\u00132\u0006\u00109\u001a\u000206H\u0002\u00a2\u0006\u0004\b:\u0010;J\u001d\u0010=\u001a\u0004\u0018\u00010<2\n\u0010\u0014\u001a\u0006\u0012\u0002\b\u00030\u0013H\u0002\u00a2\u0006\u0004\b=\u0010>J\u0019\u0010@\u001a\u0004\u0018\u00010\u001f2\u0006\u0010?\u001a\u00020\u000fH\u0002\u00a2\u0006\u0004\b@\u0010AJ)\u0010B\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u000f0\u00182\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001cH\u0002\u00a2\u0006\u0004\bB\u0010CJ\u0019\u0010E\u001a\u0004\u0018\u00010\u001f2\u0006\u0010D\u001a\u00020\u000fH\u0002\u00a2\u0006\u0004\bE\u0010AJ\u0017\u0010F\u001a\u00020\u000f2\u0006\u0010D\u001a\u00020\u000fH\u0002\u00a2\u0006\u0004\bF\u0010GJ\u000f\u0010H\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bH\u0010\u0003J\u000f\u0010I\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bI\u0010\u0003J\u001d\u0010L\u001a\u0010\u0012\u0004\u0012\u00020K\u0012\u0004\u0012\u00020\u000f\u0018\u00010JH\u0002\u00a2\u0006\u0004\bL\u0010MJ\u000f\u0010N\u001a\u00020\u001fH\u0002\u00a2\u0006\u0004\bN\u00102J\u0017\u0010P\u001a\u00020\u00192\u0006\u0010O\u001a\u00020\u001fH\u0002\u00a2\u0006\u0004\bP\u0010QJ\u000f\u0010S\u001a\u00020RH\u0002\u00a2\u0006\u0004\bS\u0010TJ\u0017\u0010V\u001a\u00020\u001f2\u0006\u0010U\u001a\u00020\u001fH\u0002\u00a2\u0006\u0004\bV\u0010WJ\u000f\u0010X\u001a\u00020'H\u0002\u00a2\u0006\u0004\bX\u0010YJ\u000f\u0010Z\u001a\u00020'H\u0002\u00a2\u0006\u0004\bZ\u0010YJ\u001b\u0010]\u001a\u00020\\2\n\b\u0002\u0010[\u001a\u0004\u0018\u00010\u001fH\u0002\u00a2\u0006\u0004\b]\u0010^J\u0017\u0010`\u001a\u00020\u00192\u0006\u0010_\u001a\u00020\u000fH\u0002\u00a2\u0006\u0004\b`\u0010aJ\u0017\u0010b\u001a\u00020\u000f2\u0006\u0010?\u001a\u00020\u000fH\u0002\u00a2\u0006\u0004\bb\u0010GJ\u0017\u0010d\u001a\u00020\u000f2\u0006\u0010c\u001a\u00020\u001fH\u0002\u00a2\u0006\u0004\bd\u0010eJ#\u0010f\u001a\u00020\u000f2\u0012\u0010c\u001a\u000e\u0012\u0004\u0012\u00020\u001f\u0012\u0004\u0012\u00020\u001f0JH\u0002\u00a2\u0006\u0004\bf\u0010gJ\u001d\u0010j\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001c2\u0006\u0010i\u001a\u00020hH\u0002\u00a2\u0006\u0004\bj\u0010kJ'\u0010q\u001a\u0004\u0018\u00010p2\n\u0010m\u001a\u0006\u0012\u0002\b\u00030l2\b\u0010o\u001a\u0004\u0018\u00010nH\u0002\u00a2\u0006\u0004\bq\u0010rJ\u0017\u0010t\u001a\u00020\u00062\u0006\u0010s\u001a\u00020\u000fH\u0002\u00a2\u0006\u0004\bt\u0010\u0012J\u000f\u0010v\u001a\u00020uH\u0002\u00a2\u0006\u0004\bv\u0010wJ\u0017\u0010y\u001a\u00020\u00192\u0006\u0010x\u001a\u00020\u000fH\u0002\u00a2\u0006\u0004\by\u0010aJ\r\u0010z\u001a\u00020\u000f\u00a2\u0006\u0004\bz\u0010{J\u0013\u0010|\u001a\b\u0012\u0004\u0012\u00020\u000f0\u001c\u00a2\u0006\u0004\b|\u0010}J\u000f\u0010~\u001a\u0004\u0018\u00010K\u00a2\u0006\u0004\b~\u0010\u007fJ\u0011\u0010\u0080\u0001\u001a\u0004\u0018\u00010\u000f\u00a2\u0006\u0005\b\u0080\u0001\u0010{J\u000f\u0010\u0081\u0001\u001a\u00020'\u00a2\u0006\u0005\b\u0081\u0001\u0010YJ\u000f\u0010\u0082\u0001\u001a\u00020\u0019\u00a2\u0006\u0005\b\u0082\u0001\u00104J!\u0010\u0083\u0001\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020'0J0\u001c\u00a2\u0006\u0005\b\u0083\u0001\u0010}J\u0015\u0010\u0084\u0001\u001a\b\u0012\u0004\u0012\u00020\u000f0\u001c\u00a2\u0006\u0005\b\u0084\u0001\u0010}J\u0015\u0010\u0085\u0001\u001a\b\u0012\u0004\u0012\u00020\u000f0\u001c\u00a2\u0006\u0005\b\u0085\u0001\u0010}R\u0018\u0010\u0087\u0001\u001a\u00030\u0086\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0087\u0001\u0010\u0088\u0001R\u001d\u0010\u008a\u0001\u001a\u00030\u0089\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u008a\u0001\u0010\u008b\u0001\u001a\u0006\b\u008c\u0001\u0010\u008d\u0001R\u001d\u0010\u008f\u0001\u001a\u00030\u008e\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u008f\u0001\u0010\u0090\u0001\u001a\u0006\b\u0091\u0001\u0010\u0092\u0001R\u001d\u0010\u0094\u0001\u001a\u00030\u0093\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u0094\u0001\u0010\u0095\u0001\u001a\u0006\b\u0096\u0001\u0010\u0097\u0001R\u001d\u0010\u0099\u0001\u001a\u00030\u0098\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u0099\u0001\u0010\u009a\u0001\u001a\u0006\b\u009b\u0001\u0010\u009c\u0001R\u001d\u0010\u009e\u0001\u001a\u00030\u009d\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u009e\u0001\u0010\u009f\u0001\u001a\u0006\b\u00a0\u0001\u0010\u00a1\u0001R\u001d\u0010\u00a2\u0001\u001a\u00030\u0098\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00a2\u0001\u0010\u009a\u0001\u001a\u0006\b\u00a3\u0001\u0010\u009c\u0001R\u001d\u0010\u00a4\u0001\u001a\u00030\u0098\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00a4\u0001\u0010\u009a\u0001\u001a\u0006\b\u00a5\u0001\u0010\u009c\u0001R\u001d\u0010\u00a6\u0001\u001a\u00030\u0089\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00a6\u0001\u0010\u008b\u0001\u001a\u0006\b\u00a7\u0001\u0010\u008d\u0001R\u001d\u0010\u00a8\u0001\u001a\u00030\u0089\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00a8\u0001\u0010\u008b\u0001\u001a\u0006\b\u00a9\u0001\u0010\u008d\u0001R\u001d\u0010\u00aa\u0001\u001a\u00030\u0089\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00aa\u0001\u0010\u008b\u0001\u001a\u0006\b\u00ab\u0001\u0010\u008d\u0001R\u001d\u0010\u00ac\u0001\u001a\u00030\u0089\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00ac\u0001\u0010\u008b\u0001\u001a\u0006\b\u00ad\u0001\u0010\u008d\u0001R\u001d\u0010\u00ae\u0001\u001a\u00030\u0089\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00ae\u0001\u0010\u008b\u0001\u001a\u0006\b\u00af\u0001\u0010\u008d\u0001R\u001d\u0010\u00b0\u0001\u001a\u00030\u0089\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00b0\u0001\u0010\u008b\u0001\u001a\u0006\b\u00b1\u0001\u0010\u008d\u0001R\u001d\u0010\u00b2\u0001\u001a\u00030\u0089\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00b2\u0001\u0010\u008b\u0001\u001a\u0006\b\u00b3\u0001\u0010\u008d\u0001R\u001d\u0010\u00b4\u0001\u001a\u00030\u0098\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00b4\u0001\u0010\u009a\u0001\u001a\u0006\b\u00b5\u0001\u0010\u009c\u0001R\u001d\u0010\u00b6\u0001\u001a\u00030\u0098\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00b6\u0001\u0010\u009a\u0001\u001a\u0006\b\u00b7\u0001\u0010\u009c\u0001R\u001d\u0010\u00b8\u0001\u001a\u00030\u0098\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00b8\u0001\u0010\u009a\u0001\u001a\u0006\b\u00b9\u0001\u0010\u009c\u0001R\u001d\u0010\u00ba\u0001\u001a\u00030\u0089\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00ba\u0001\u0010\u008b\u0001\u001a\u0006\b\u00bb\u0001\u0010\u008d\u0001R\u001d\u0010\u00bc\u0001\u001a\u00030\u0089\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00bc\u0001\u0010\u008b\u0001\u001a\u0006\b\u00bd\u0001\u0010\u008d\u0001R\u001d\u0010\u00be\u0001\u001a\u00030\u008e\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00be\u0001\u0010\u0090\u0001\u001a\u0006\b\u00bf\u0001\u0010\u0092\u0001R\u001d\u0010\u00c0\u0001\u001a\u00030\u008e\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00c0\u0001\u0010\u0090\u0001\u001a\u0006\b\u00c1\u0001\u0010\u0092\u0001R\u001d\u0010\u00c2\u0001\u001a\u00030\u008e\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00c2\u0001\u0010\u0090\u0001\u001a\u0006\b\u00c3\u0001\u0010\u0092\u0001R\u001d\u0010\u00c4\u0001\u001a\u00030\u0093\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00c4\u0001\u0010\u0095\u0001\u001a\u0006\b\u00c5\u0001\u0010\u0097\u0001R\u001d\u0010\u00c6\u0001\u001a\u00030\u0093\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00c6\u0001\u0010\u0095\u0001\u001a\u0006\b\u00c7\u0001\u0010\u0097\u0001R\u001d\u0010\u00c8\u0001\u001a\u00030\u0098\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u00c8\u0001\u0010\u009a\u0001\u001a\u0006\b\u00c9\u0001\u0010\u009c\u0001R \u0010\u00cb\u0001\u001a\u00030\u00ca\u00018\u0000X\u0080\u0004\u00a2\u0006\u0010\n\u0006\b\u00cb\u0001\u0010\u00cc\u0001\u001a\u0006\b\u00cd\u0001\u0010\u00ce\u0001R\u0018\u0010\u00cf\u0001\u001a\u00030\u00ca\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00cf\u0001\u0010\u00cc\u0001R\u0019\u0010\u00d0\u0001\u001a\u00020\u001f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00d0\u0001\u0010\u00d1\u0001R%\u0010\u00d2\u0001\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00190\u00188\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00d2\u0001\u0010\u00d3\u0001R%\u0010\u00d4\u0001\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u000f0\u00188\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00d4\u0001\u0010\u00d3\u0001R\u0019\u0010\u00d5\u0001\u001a\u00020\u001f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00d5\u0001\u0010\u00d1\u0001R\u0019\u0010\u00d6\u0001\u001a\u00020'8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00d6\u0001\u0010\u00d7\u0001R\u001a\u0010\u00d9\u0001\u001a\u00030\u00d8\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00d9\u0001\u0010\u00da\u0001R\u0019\u0010\u00db\u0001\u001a\u00020'8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00db\u0001\u0010\u00d7\u0001R\u001a\u0010\u00dc\u0001\u001a\u00030\u00d8\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00dc\u0001\u0010\u00da\u0001R\u0019\u0010\u00dd\u0001\u001a\u00020'8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00dd\u0001\u0010\u00d7\u0001R\u0019\u0010\u00de\u0001\u001a\u00020'8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00de\u0001\u0010\u00d7\u0001R\u001b\u0010\u00df\u0001\u001a\u0004\u0018\u00010\u000f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00df\u0001\u0010\u00e0\u0001R\u001a\u0010\u00e1\u0001\u001a\u00030\u00d8\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00e1\u0001\u0010\u00da\u0001R\u0019\u0010\u00e2\u0001\u001a\u00020'8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00e2\u0001\u0010\u00d7\u0001R\u0013\u0010\u00e3\u0001\u001a\u00020'8F\u00a2\u0006\u0007\u001a\u0005\b\u00e3\u0001\u0010YR\u0019\u0010\u00e4\u0001\u001a\u00020'8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00e4\u0001\u0010\u00d7\u0001R\u0019\u0010\u00e5\u0001\u001a\u00020'8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00e5\u0001\u0010\u00d7\u0001R\u001b\u0010\u00e6\u0001\u001a\u0004\u0018\u00010K8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00e6\u0001\u0010\u00e7\u0001R\u001b\u0010\u00e8\u0001\u001a\u0004\u0018\u00010\u000f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00e8\u0001\u0010\u00e0\u0001R\u001b\u0010\u00e9\u0001\u001a\u0004\u0018\u00010K8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00e9\u0001\u0010\u00e7\u0001R\u001b\u0010\u00ea\u0001\u001a\u0004\u0018\u00010\u000f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00ea\u0001\u0010\u00e0\u0001R\u001b\u0010\u00eb\u0001\u001a\u0004\u0018\u00010\u001f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00eb\u0001\u0010\u00ec\u0001R\"\u0010\u00ef\u0001\u001a\r \u00ee\u0001*\u0005\u0018\u00010\u00ed\u00010\u00ed\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00ef\u0001\u0010\u00f0\u0001R\"\u0010\u00f1\u0001\u001a\r \u00ee\u0001*\u0005\u0018\u00010\u00ed\u00010\u00ed\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00f1\u0001\u0010\u00f0\u0001R\"\u0010\u00f2\u0001\u001a\r \u00ee\u0001*\u0005\u0018\u00010\u00ed\u00010\u00ed\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00f2\u0001\u0010\u00f0\u0001R\"\u0010\u00f3\u0001\u001a\r \u00ee\u0001*\u0005\u0018\u00010\u00ed\u00010\u00ed\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00f3\u0001\u0010\u00f0\u0001R\"\u0010\u00f4\u0001\u001a\r \u00ee\u0001*\u0005\u0018\u00010\u00ed\u00010\u00ed\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u00f4\u0001\u0010\u00f0\u0001R\u0017\u0010\u00f5\u0001\u001a\u00020\u000f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00f5\u0001\u0010\u00e0\u0001R\u0017\u0010\u00f6\u0001\u001a\u00020\u000f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00f6\u0001\u0010\u00e0\u0001R\u0017\u0010\u00f7\u0001\u001a\u00020\u000f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00f7\u0001\u0010\u00e0\u0001R\u0018\u0010\u00f8\u0001\u001a\u00030\u00d8\u00018\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00f8\u0001\u0010\u00da\u0001R\u0018\u0010\u00f9\u0001\u001a\u00030\u00d8\u00018\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00f9\u0001\u0010\u00da\u0001R\u001b\u0010\u00fa\u0001\u001a\u0004\u0018\u00010<8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u00fa\u0001\u0010\u00fb\u0001\u00a8\u0006\u00fe\u0001"}, d2={"Lorg/cobalt/internal/mining/MiningModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "Lorg/cobalt/api/event/impl/client/ChatEvent$Receive;", "onChat", "(Lorg/cobalt/api/event/impl/client/ChatEvent$Receive;)V", "Lorg/cobalt/api/event/impl/client/PacketEvent$Outgoing;", "onPacket", "(Lorg/cobalt/api/event/impl/client/PacketEvent$Outgoing;)V", "", "command", "sendCommand", "(Ljava/lang/String;)V", "Lnet/minecraft/class_465;", "screen", "captureMiningSpeedFromStats", "(Lnet/minecraft/class_465;)V", "captureHotmPerks", "", "", "parseHotmPerks", "(Lnet/minecraft/class_465;)Ljava/util/Map;", "", "Lnet/minecraft/class_2561;", "lines", "", "parseMiningSpeed", "(Ljava/util/List;)Ljava/lang/Double;", "parsePerkLevel", "(Ljava/util/List;)Ljava/lang/Integer;", "applyHotmPerksToToggles", "updateHotmMultiplier", "updateLookTicks", "", "includePingDelay", "getCalculatedLookTicks", "(Z)D", "Lorg/cobalt/api/event/impl/client/PacketEvent$Incoming;", "onIncomingPacket", "(Lorg/cobalt/api/event/impl/client/PacketEvent$Incoming;)V", "Lorg/cobalt/api/event/impl/client/BlockChangeEvent;", "onBlockChange", "(Lorg/cobalt/api/event/impl/client/BlockChangeEvent;)V", "computePingDelayTicks", "()D", "getPingMs", "()I", "exportCombinedStats", "Lnet/minecraft/class_1735;", "findMiningStatsSlot", "(Lnet/minecraft/class_465;)Lnet/minecraft/class_1735;", "slot", "trySetHoveredSlot", "(Lnet/minecraft/class_465;Lnet/minecraft/class_1735;)V", "Ljava/lang/reflect/Field;", "resolveHoveredSlotField", "(Lnet/minecraft/class_465;)Ljava/lang/reflect/Field;", "text", "extractNumber", "(Ljava/lang/String;)Ljava/lang/Double;", "parseMiningStats", "(Ljava/util/List;)Ljava/util/Map;", "raw", "parseMiningSpeedStatValue", "normalizeStatName", "(Ljava/lang/String;)Ljava/lang/String;", "updateBlockDetection", "resetLookTickSample", "Lkotlin/Pair;", "Lnet/minecraft/class_2338;", "resolveLookTarget", "()Lkotlin/Pair;", "resolveBlockStrength", "effectiveSpeed", "resolveBaseMineTicks", "(D)I", "Lorg/cobalt/internal/mining/MiningModule$ResolvedMiningTarget;", "resolveMiningTarget", "()Lorg/cobalt/internal/mining/MiningModule$ResolvedMiningTarget;", "baseSpeed", "computeEffectiveMiningSpeed", "(D)D", "hasSelectedGemstones", "()Z", "isPrecisionBonusApplied", "baseOverride", "Lorg/cobalt/internal/mining/MiningModule$DerivedMiningSpeed;", "computeDerivedMiningSpeed", "(Ljava/lang/Double;)Lorg/cobalt/internal/mining/MiningModule$DerivedMiningSpeed;", "nameContains", "getPerkLevel", "(Ljava/lang/String;)I", "stripFormatting", "value", "formatNumber", "(D)Ljava/lang/String;", "formatRange", "(Lkotlin/Pair;)Ljava/lang/String;", "Lnet/minecraft/class_1799;", "stack", "getTooltipLines", "(Lnet/minecraft/class_1799;)Ljava/util/List;", "Ljava/lang/Class;", "ctxClass", "Lnet/minecraft/class_1937;", "level", "", "buildTooltipContext", "(Ljava/lang/Class;Lnet/minecraft/class_1937;)Ljava/lang/Object;", "message", "notify", "Lorg/cobalt/internal/mining/MiningNukerController$Config;", "buildNukerConfig", "()Lorg/cobalt/internal/mining/MiningNukerController$Config;", "roman", "romanToInt", "getMiningCategory", "()Ljava/lang/String;", "buildOverlayRows", "()Ljava/util/List;", "getDetectedBlockPos", "()Lnet/minecraft/class_2338;", "getDetectedBlockId", "isNukerActive", "getPowderChestCount", "getBuffStatuses", "buildBuffStatusRows", "getActivePerks", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "getEnabled", "()Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "blockStrength", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "getBlockStrength", "()Lorg/cobalt/api/module/setting/impl/SliderSetting;", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "blockType", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "getBlockType", "()Lorg/cobalt/api/module/setting/impl/ModeSetting;", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "detectedBlockText", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "getDetectedBlockText", "()Lorg/cobalt/api/module/setting/impl/TextSetting;", "Lorg/cobalt/api/module/setting/impl/RangeSetting;", "pingDelay", "Lorg/cobalt/api/module/setting/impl/RangeSetting;", "getPingDelay", "()Lorg/cobalt/api/module/setting/impl/RangeSetting;", "miningSpeedText", "getMiningSpeedText", "hotmMultiplierText", "getHotmMultiplierText", "miningGems", "getMiningGems", "precisionActive", "getPrecisionActive", "speedBoostActive", "getSpeedBoostActive", "autoActivateSpeedBoost", "getAutoActivateSpeedBoost", "frontLoadedActive", "getFrontLoadedActive", "skymallActive", "getSkymallActive", "miningUmberTungsten", "getMiningUmberTungsten", "pingText", "getPingText", "lookTicksText", "getLookTicksText", "lookCountdownText", "getLookCountdownText", "nukerEnabled", "getNukerEnabled", "powderChestCollector", "getPowderChestCollector", "nukerRange", "getNukerRange", "nukerCooldownMs", "getNukerCooldownMs", "nukerBlocksPerTick", "getNukerBlocksPerTick", "nukerTargetMode", "getNukerTargetMode", "nukerToolMode", "getNukerToolMode", "nukerCustomMatchers", "getNukerCustomMatchers", "Lorg/cobalt/api/module/setting/impl/ActionSetting;", "scrapeAll", "Lorg/cobalt/api/module/setting/impl/ActionSetting;", "getScrapeAll$cobalt", "()Lorg/cobalt/api/module/setting/impl/ActionSetting;", "exportHotm", "miningSpeed", "D", "hotmPerks", "Ljava/util/Map;", "miningStats", "hotmMultiplier", "pendingStatsScrape", "Z", "", "pendingStatsTick", "J", "pendingHotmScrape", "pendingHotmTick", "pendingScrapeAll", "pendingHotmAfterStats", "lastHotmSignature", "Ljava/lang/String;", "lastHotmParseTick", "miningSpeedBoostBuffActive", "isMiningSpeedBoostActive", "pendingSpeedBoostUse", "pendingSpeedBoostRelease", "detectedBlockPos", "Lnet/minecraft/class_2338;", "detectedBlockId", "sampledLookTargetPos", "sampledLookTargetId", "sampledPingDelayTicks", "Ljava/lang/Double;", "Ljava/util/regex/Pattern;", "kotlin.jvm.PlatformType", "NUMBER_PATTERN", "Ljava/util/regex/Pattern;", "LEVEL_PATTERN", "ROMAN_PATTERN", "STAT_PAIR_PATTERN", "LEADING_DECORATION_PATTERN", "MINING_SPEED_BOOST_USED", "MINING_SPEED_BOOST_EXPIRED", "MINING_SPEED_BOOST_READY", "STATS_SCRAPE_TIMEOUT_TICKS", "HOTM_SCRAPE_TIMEOUT_TICKS", "hoveredSlotField", "Ljava/lang/reflect/Field;", "ResolvedMiningTarget", "DerivedMiningSpeed", "cobalt"})
@SourceDebugExtension(value={"SMAP\nMiningModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MiningModule.kt\norg/cobalt/internal/mining/MiningModule\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 5 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,1147:1\n1807#2,3:1148\n1807#2,3:1153\n1807#2,3:1163\n1807#2,3:1173\n1807#2,3:1176\n1807#2,3:1179\n1807#2,3:1182\n221#3,2:1151\n221#3,2:1156\n1#4:1158\n1401#5,2:1159\n1401#5,2:1161\n3938#5:1166\n4474#5,2:1167\n1401#5,2:1169\n1401#5,2:1171\n*S KotlinDebug\n*F\n+ 1 MiningModule.kt\norg/cobalt/internal/mining/MiningModule\n*L\n544#1:1148,3\n659#1:1153,3\n862#1:1163,3\n1080#1:1173,3\n1081#1:1176,3\n1082#1:1179,3\n1083#1:1182,3\n649#1:1151,2\n662#1:1156,2\n720#1:1159,2\n727#1:1161,2\n961#1:1166\n961#1:1167,2\n985#1:1169,2\n994#1:1171,2\n*E\n"})
public final class MiningModule
extends Module {
    @NotNull
    public static final MiningModule INSTANCE = new MiningModule();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final CheckboxSetting enabled;
    @NotNull
    private static final SliderSetting blockStrength;
    @NotNull
    private static final ModeSetting blockType;
    @NotNull
    private static final TextSetting detectedBlockText;
    @NotNull
    private static final RangeSetting pingDelay;
    @NotNull
    private static final TextSetting miningSpeedText;
    @NotNull
    private static final TextSetting hotmMultiplierText;
    @NotNull
    private static final CheckboxSetting miningGems;
    @NotNull
    private static final CheckboxSetting precisionActive;
    @NotNull
    private static final CheckboxSetting speedBoostActive;
    @NotNull
    private static final CheckboxSetting autoActivateSpeedBoost;
    @NotNull
    private static final CheckboxSetting frontLoadedActive;
    @NotNull
    private static final CheckboxSetting skymallActive;
    @NotNull
    private static final CheckboxSetting miningUmberTungsten;
    @NotNull
    private static final TextSetting pingText;
    @NotNull
    private static final TextSetting lookTicksText;
    @NotNull
    private static final TextSetting lookCountdownText;
    @NotNull
    private static final CheckboxSetting nukerEnabled;
    @NotNull
    private static final CheckboxSetting powderChestCollector;
    @NotNull
    private static final SliderSetting nukerRange;
    @NotNull
    private static final SliderSetting nukerCooldownMs;
    @NotNull
    private static final SliderSetting nukerBlocksPerTick;
    @NotNull
    private static final ModeSetting nukerTargetMode;
    @NotNull
    private static final ModeSetting nukerToolMode;
    @NotNull
    private static final TextSetting nukerCustomMatchers;
    @NotNull
    private static final ActionSetting scrapeAll;
    @NotNull
    private static final ActionSetting exportHotm;
    private static double miningSpeed;
    @NotNull
    private static Map<String, Integer> hotmPerks;
    @NotNull
    private static Map<String, String> miningStats;
    private static double hotmMultiplier;
    private static boolean pendingStatsScrape;
    private static long pendingStatsTick;
    private static boolean pendingHotmScrape;
    private static long pendingHotmTick;
    private static boolean pendingScrapeAll;
    private static boolean pendingHotmAfterStats;
    @Nullable
    private static String lastHotmSignature;
    private static long lastHotmParseTick;
    private static boolean miningSpeedBoostBuffActive;
    private static boolean pendingSpeedBoostUse;
    private static boolean pendingSpeedBoostRelease;
    @Nullable
    private static class_2338 detectedBlockPos;
    @Nullable
    private static String detectedBlockId;
    @Nullable
    private static class_2338 sampledLookTargetPos;
    @Nullable
    private static String sampledLookTargetId;
    @Nullable
    private static Double sampledPingDelayTicks;
    private static final Pattern NUMBER_PATTERN;
    private static final Pattern LEVEL_PATTERN;
    private static final Pattern ROMAN_PATTERN;
    private static final Pattern STAT_PAIR_PATTERN;
    private static final Pattern LEADING_DECORATION_PATTERN;
    @NotNull
    private static final String MINING_SPEED_BOOST_USED = "You used your Mining Speed Boost Pickaxe Ability!";
    @NotNull
    private static final String MINING_SPEED_BOOST_EXPIRED = "Your Mining Speed Boost has expired!";
    @NotNull
    private static final String MINING_SPEED_BOOST_READY = "Mining Speed Boost is now available!";
    private static final long STATS_SCRAPE_TIMEOUT_TICKS = 60L;
    private static final long HOTM_SCRAPE_TIMEOUT_TICKS = 100L;
    @Nullable
    private static Field hoveredSlotField;

    private MiningModule() {
        super("Mining");
    }

    @NotNull
    public final CheckboxSetting getEnabled() {
        return enabled;
    }

    @NotNull
    public final SliderSetting getBlockStrength() {
        return blockStrength;
    }

    @NotNull
    public final ModeSetting getBlockType() {
        return blockType;
    }

    @NotNull
    public final TextSetting getDetectedBlockText() {
        return detectedBlockText;
    }

    @NotNull
    public final RangeSetting getPingDelay() {
        return pingDelay;
    }

    @NotNull
    public final TextSetting getMiningSpeedText() {
        return miningSpeedText;
    }

    @NotNull
    public final TextSetting getHotmMultiplierText() {
        return hotmMultiplierText;
    }

    @NotNull
    public final CheckboxSetting getMiningGems() {
        return miningGems;
    }

    @NotNull
    public final CheckboxSetting getPrecisionActive() {
        return precisionActive;
    }

    @NotNull
    public final CheckboxSetting getSpeedBoostActive() {
        return speedBoostActive;
    }

    @NotNull
    public final CheckboxSetting getAutoActivateSpeedBoost() {
        return autoActivateSpeedBoost;
    }

    @NotNull
    public final CheckboxSetting getFrontLoadedActive() {
        return frontLoadedActive;
    }

    @NotNull
    public final CheckboxSetting getSkymallActive() {
        return skymallActive;
    }

    @NotNull
    public final CheckboxSetting getMiningUmberTungsten() {
        return miningUmberTungsten;
    }

    @NotNull
    public final TextSetting getPingText() {
        return pingText;
    }

    @NotNull
    public final TextSetting getLookTicksText() {
        return lookTicksText;
    }

    @NotNull
    public final TextSetting getLookCountdownText() {
        return lookCountdownText;
    }

    @NotNull
    public final CheckboxSetting getNukerEnabled() {
        return nukerEnabled;
    }

    @NotNull
    public final CheckboxSetting getPowderChestCollector() {
        return powderChestCollector;
    }

    @NotNull
    public final SliderSetting getNukerRange() {
        return nukerRange;
    }

    @NotNull
    public final SliderSetting getNukerCooldownMs() {
        return nukerCooldownMs;
    }

    @NotNull
    public final SliderSetting getNukerBlocksPerTick() {
        return nukerBlocksPerTick;
    }

    @NotNull
    public final ModeSetting getNukerTargetMode() {
        return nukerTargetMode;
    }

    @NotNull
    public final ModeSetting getNukerToolMode() {
        return nukerToolMode;
    }

    @NotNull
    public final TextSetting getNukerCustomMatchers() {
        return nukerCustomMatchers;
    }

    @NotNull
    public final ActionSetting getScrapeAll$cobalt() {
        return scrapeAll;
    }

    public final boolean isMiningSpeedBoostActive() {
        return miningSpeedBoostBuffActive;
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        boolean trackingActive;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (MiningModule.mc.field_1724 == null || MiningModule.mc.field_1687 == null) {
            miningSpeedBoostBuffActive = false;
            MiningNukerController.INSTANCE.reset(true);
        }
        if (pendingSpeedBoostRelease) {
            MiningModule.mc.field_1690.field_1904.method_23481(false);
            pendingSpeedBoostRelease = false;
        }
        if (pendingSpeedBoostUse) {
            pendingSpeedBoostUse = false;
            class_746 player = MiningModule.mc.field_1724;
            if (player != null && MiningModule.mc.field_1755 == null && MiningMacroModule.INSTANCE.isActive()) {
                MiningModule.mc.field_1690.field_1904.method_23481(true);
                pendingSpeedBoostRelease = true;
            }
        }
        class_437 screen = MiningModule.mc.field_1755;
        if (pendingHotmAfterStats && screen == null) {
            pendingHotmAfterStats = false;
            pendingHotmScrape = true;
            class_638 class_6382 = MiningModule.mc.field_1687;
            pendingHotmTick = class_6382 != null ? class_6382.method_75260() : 0L;
            this.sendCommand("/hotm");
        }
        if (screen instanceof class_465) {
            if (pendingStatsScrape) {
                this.captureMiningSpeedFromStats((class_465)screen);
            }
            this.captureHotmPerks((class_465)screen);
        }
        boolean bl = trackingActive = (Boolean)enabled.getValue() != false || MiningMacroModule.INSTANCE.isActive() || (Boolean)nukerEnabled.getValue() != false;
        if (!trackingActive) {
            lookCountdownText.setValue("0");
            return;
        }
        int pingMs = this.getPingMs();
        pingText.setValue(String.valueOf(pingMs));
        this.updateBlockDetection();
        this.updateLookTicks();
        if (((Boolean)nukerEnabled.getValue()).booleanValue() && !MiningMacroModule.INSTANCE.isActive()) {
            MiningNukerController.INSTANCE.tick(this.buildNukerConfig());
        } else if (!((Boolean)nukerEnabled.getValue()).booleanValue()) {
            MiningNukerController.reset$default(MiningNukerController.INSTANCE, false, 1, null);
        }
    }

    @SubscribeEvent
    public final void onChat(@NotNull ChatEvent.Receive event) {
        String stripped;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        String string = event.getMessage();
        if (string == null) {
            return;
        }
        String message = string;
        switch (stripped = ((Object)StringsKt.trim((CharSequence)this.stripFormatting(message))).toString()) {
            case "You used your Mining Speed Boost Pickaxe Ability!": {
                miningSpeedBoostBuffActive = true;
                break;
            }
            case "Your Mining Speed Boost has expired!": {
                miningSpeedBoostBuffActive = false;
                break;
            }
            case "Mining Speed Boost is now available!": {
                miningSpeedBoostBuffActive = false;
                if (!((Boolean)autoActivateSpeedBoost.getValue()).booleanValue()) break;
                pendingSpeedBoostUse = true;
            }
        }
        if (((Boolean)nukerEnabled.getValue()).booleanValue()) {
            MiningNukerController.INSTANCE.onChatMessage(stripped);
        }
    }

    @SubscribeEvent
    public final void onPacket(@NotNull PacketEvent.Outgoing event) {
        String string;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_2596<?> packet = event.getPacket();
        if (packet instanceof class_7472) {
            string = ((class_7472)packet).comp_808();
        } else if (packet instanceof class_9449) {
            string = ((class_9449)packet).comp_2532();
        } else {
            return;
        }
        String string2 = string;
        Intrinsics.checkNotNull((Object)string2);
        String command = string2;
        if (StringsKt.equals((String)StringsKt.substringBefore$default((String)((Object)StringsKt.trim((CharSequence)command)).toString(), (char)' ', null, (int)2, null), (String)"hotm", (boolean)true)) {
            pendingHotmScrape = true;
            class_638 class_6382 = MiningModule.mc.field_1687;
            pendingHotmTick = class_6382 != null ? class_6382.method_75260() : 0L;
        }
    }

    private final void sendCommand(String command) {
        block1: {
            class_746 class_7462 = MiningModule.mc.field_1724;
            if (class_7462 == null) {
                return;
            }
            class_746 player = class_7462;
            String trimmed = StringsKt.removePrefix((String)((Object)StringsKt.trim((CharSequence)command)).toString(), (CharSequence)"/");
            class_634 class_6342 = player.field_3944;
            if (class_6342 == null) break block1;
            class_6342.method_45730(trimmed);
        }
    }

    private final void captureMiningSpeedFromStats(class_465<?> screen) {
        Double value;
        long now;
        String string = screen.method_25440().getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        String string2 = string;
        Locale locale = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
        String string3 = string2.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
        String title = string3;
        if (!StringsKt.contains$default((CharSequence)title, (CharSequence)"stat", (boolean)false, (int)2, null) && !StringsKt.contains$default((CharSequence)title, (CharSequence)"profile", (boolean)false, (int)2, null)) {
            return;
        }
        class_638 class_6382 = MiningModule.mc.field_1687;
        long l = now = class_6382 != null ? class_6382.method_75260() : 0L;
        if (now - pendingStatsTick > 60L) {
            pendingStatsScrape = false;
            this.notify("Failed to scrape Mining Speed (timeout).");
            return;
        }
        class_1735 class_17352 = this.findMiningStatsSlot(screen);
        if (class_17352 == null) {
            return;
        }
        class_1735 slot = class_17352;
        this.trySetHoveredSlot(screen, slot);
        class_1799 class_17992 = slot.method_7677();
        Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getItem(...)");
        class_1799 stack = class_17992;
        List<class_2561> lines = this.getTooltipLines(stack);
        if (lines.isEmpty()) {
            return;
        }
        Map<String, String> stats = this.parseMiningStats(lines);
        if (!stats.isEmpty()) {
            miningStats = stats;
            this.exportCombinedStats();
        }
        if ((value = this.parseMiningSpeed(lines)) != null) {
            miningSpeed = value;
            miningSpeedText.setValue(this.formatNumber(value));
            pendingStatsScrape = false;
            this.notify("Captured Mining Speed: " + this.formatNumber(value));
            if (pendingScrapeAll) {
                pendingScrapeAll = false;
                pendingHotmAfterStats = true;
                mc.method_1507(null);
            }
        }
    }

    private final void captureHotmPerks(class_465<?> screen) {
        long now;
        String string = screen.method_25440().getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        String string2 = string;
        Locale locale = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
        String string3 = string2.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
        String title = string3;
        if (!StringsKt.contains$default((CharSequence)title, (CharSequence)"heart", (boolean)false, (int)2, null) && !StringsKt.contains$default((CharSequence)title, (CharSequence)"hotm", (boolean)false, (int)2, null)) {
            return;
        }
        if (pendingHotmScrape) {
            long now2;
            class_638 class_6382 = MiningModule.mc.field_1687;
            long l = now2 = class_6382 != null ? class_6382.method_75260() : 0L;
            if (now2 - pendingHotmTick > 100L) {
                pendingHotmScrape = false;
                this.notify("Failed to scrape HOTM perks (timeout).");
                return;
            }
        }
        String signature = title + ":" + screen.method_17577().field_7763 + ":" + screen.method_17577().field_7761.size();
        class_638 class_6383 = MiningModule.mc.field_1687;
        long l = now = class_6383 != null ? class_6383.method_75260() : 0L;
        if (Intrinsics.areEqual((Object)signature, (Object)lastHotmSignature) && now - lastHotmParseTick < 10L) {
            return;
        }
        lastHotmSignature = signature;
        lastHotmParseTick = now;
        Map<String, Integer> perks = this.parseHotmPerks(screen);
        if (perks.isEmpty()) {
            return;
        }
        hotmPerks = perks;
        this.exportCombinedStats();
        this.updateHotmMultiplier();
        this.applyHotmPerksToToggles();
        if (pendingHotmScrape) {
            pendingHotmScrape = false;
            this.notify("Exported HOTM perks (" + perks.size() + ").");
        }
    }

    private final Map<String, Integer> parseHotmPerks(class_465<?> screen) {
        LinkedHashMap result = new LinkedHashMap();
        Iterator iterator = screen.method_17577().field_7761.iterator();
        Intrinsics.checkNotNullExpressionValue((Object)iterator, (String)"iterator(...)");
        Iterator iterator2 = iterator;
        while (iterator2.hasNext()) {
            List<class_2561> lore;
            Integer level2;
            class_1799 stack;
            class_1735 slot = (class_1735)iterator2.next();
            Intrinsics.checkNotNullExpressionValue((Object)slot.method_7677(), (String)"getItem(...)");
            if (stack.method_7960()) continue;
            String string = stack.method_7964().getString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
            String name = this.stripFormatting(string);
            if (StringsKt.isBlank((CharSequence)name) || (level2 = this.parsePerkLevel(lore = this.getTooltipLines(stack))) == null || level2 <= 0) continue;
            ((Map)result).put(name, level2);
        }
        return result;
    }

    private final Double parseMiningSpeed(List<? extends class_2561> lines) {
        for (class_2561 class_25612 : lines) {
            String string = class_25612.getString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
            String raw = ((Object)StringsKt.trim((CharSequence)this.stripFormatting(string))).toString();
            Double statValue = this.parseMiningSpeedStatValue(raw);
            if (statValue == null) continue;
            return statValue;
        }
        return null;
    }

    private final Integer parsePerkLevel(List<? extends class_2561> lines) {
        for (class_2561 class_25612 : lines) {
            String string = class_25612.getString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
            String raw = this.stripFormatting(string);
            Matcher levelMatch = LEVEL_PATTERN.matcher(raw);
            if (levelMatch.find()) {
                String string2 = levelMatch.group(1);
                Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"group(...)");
                return StringsKt.toIntOrNull((String)string2);
            }
            Matcher romanMatch = ROMAN_PATTERN.matcher(raw);
            if (!romanMatch.find()) continue;
            String string3 = romanMatch.group(1);
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"group(...)");
            return this.romanToInt(string3);
        }
        return null;
    }

    private final void applyHotmPerksToToggles() {
        if (this.getPerkLevel("precision miner") > 0) {
            precisionActive.setValue(true);
        }
        if (this.getPerkLevel("mining speed boost") > 0) {
            speedBoostActive.setValue(true);
        }
        if (this.getPerkLevel("front loaded") > 0) {
            frontLoadedActive.setValue(true);
        }
        if (this.getPerkLevel("skymall") > 0) {
            skymallActive.setValue(true);
        }
        if (this.getPerkLevel("professional") > 0) {
            miningGems.setValue(this.hasSelectedGemstones());
        }
        List<String> selectedTypes = MiningMacroModule.INSTANCE.getSelectedTypesInOrder();
        if (this.getPerkLevel("strong arm") > 0) {
            boolean bl;
            block10: {
                Iterable $this$any$iv = selectedTypes;
                boolean $i$f$any = false;
                if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                    bl = false;
                } else {
                    for (Object element$iv : $this$any$iv) {
                        String it = (String)element$iv;
                        boolean bl2 = false;
                        if (!(Intrinsics.areEqual((Object)it, (Object)"Umber") || Intrinsics.areEqual((Object)it, (Object)"Tungsten"))) continue;
                        bl = true;
                        break block10;
                    }
                    bl = false;
                }
            }
            if (bl) {
                miningUmberTungsten.setValue(true);
            }
        }
    }

    private final void updateHotmMultiplier() {
        double mult;
        double base = miningSpeed;
        if (base <= 0.0) {
            hotmMultiplier = 1.0;
            hotmMultiplierText.setValue("1.00");
            return;
        }
        double effective = this.computeEffectiveMiningSpeed(base);
        hotmMultiplier = mult = effective / base;
        Locale locale = Locale.US;
        String string = "%.2f";
        Object[] objectArray = new Object[]{mult};
        String string2 = String.format(locale, string, Arrays.copyOf(objectArray, objectArray.length));
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"format(...)");
        hotmMultiplierText.setValue(string2);
    }

    private final void updateLookTicks() {
        double total = this.getCalculatedLookTicks(true);
        if (total <= 0.0) {
            lookTicksText.setValue("0");
            lookCountdownText.setValue("0");
            return;
        }
        Locale locale = Locale.US;
        String string = "%.2f";
        Object[] objectArray = new Object[]{total};
        String string2 = String.format(locale, string, Arrays.copyOf(objectArray, objectArray.length));
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"format(...)");
        lookTicksText.setValue(string2);
        Pair<class_2338, String> target = this.resolveLookTarget();
        double trackedTicks = MiningMacroModule.INSTANCE.getTrackedTargetTicks((Boolean)MiningMacroModule.INSTANCE.getTickGliding().getValue());
        double remaining = target == null ? 0.0 : (trackedTicks > 0.0 ? RangesKt.coerceAtLeast((double)(total - trackedTicks), (double)0.0) : total);
        Locale locale2 = Locale.US;
        String string3 = "%.2f";
        Object[] objectArray2 = new Object[]{remaining};
        String string4 = String.format(locale2, string3, Arrays.copyOf(objectArray2, objectArray2.length));
        Intrinsics.checkNotNullExpressionValue((Object)string4, (String)"format(...)");
        lookCountdownText.setValue(string4);
    }

    public final double getCalculatedLookTicks(boolean includePingDelay) {
        double effectiveSpeed = this.computeEffectiveMiningSpeed(miningSpeed);
        if (effectiveSpeed <= 0.0) {
            return 0.0;
        }
        double baseTicks = this.resolveBaseMineTicks(effectiveSpeed);
        return includePingDelay ? baseTicks + this.computePingDelayTicks() : baseTicks;
    }

    public static /* synthetic */ double getCalculatedLookTicks$default(MiningModule miningModule, boolean bl, int n, Object object) {
        if ((n & 1) != 0) {
            bl = true;
        }
        return miningModule.getCalculatedLookTicks(bl);
    }

    @SubscribeEvent
    public final void onIncomingPacket(@NotNull PacketEvent.Incoming event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)nukerEnabled.getValue()).booleanValue() || !((Boolean)powderChestCollector.getValue()).booleanValue()) {
            return;
        }
        class_2596<?> class_25962 = event.getPacket();
        class_2675 class_26752 = class_25962 instanceof class_2675 ? (class_2675)class_25962 : null;
        if (class_26752 == null) {
            return;
        }
        class_2675 packet = class_26752;
        MiningNukerController.INSTANCE.onParticlePacket(packet);
    }

    @SubscribeEvent
    public final void onBlockChange(@NotNull BlockChangeEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)nukerEnabled.getValue()).booleanValue() || !((Boolean)powderChestCollector.getValue()).booleanValue()) {
            return;
        }
        MiningNukerController.INSTANCE.onBlockChange(event.getPos(), event.getOldBlock(), event.getNewBlock());
    }

    private final double computePingDelayTicks() {
        double d;
        CharSequence charSequence;
        String id;
        Pair<class_2338, String> target;
        Pair<class_2338, String> pair = target = this.resolveLookTarget();
        class_2338 pos = pair != null ? (class_2338)pair.getFirst() : null;
        Pair<class_2338, String> pair2 = target;
        String string = id = pair2 != null ? (String)pair2.getSecond() : null;
        if (pos == null || (charSequence = (CharSequence)id) == null || charSequence.length() == 0) {
            this.resetLookTickSample();
            return 0.0;
        }
        Double cached = sampledPingDelayTicks;
        if (cached != null && Intrinsics.areEqual((Object)pos, (Object)sampledLookTargetPos) && Intrinsics.areEqual((Object)id, (Object)sampledLookTargetId)) {
            return cached;
        }
        double sampled = d = RotationsModule.INSTANCE.sample((Pair<Double, Double>)((Pair)pingDelay.getValue()));
        boolean bl = false;
        sampledLookTargetPos = pos;
        sampledLookTargetId = id;
        sampledPingDelayTicks = sampled;
        return d;
    }

    private final int getPingMs() {
        class_640 info;
        class_746 class_7462 = MiningModule.mc.field_1724;
        if (class_7462 == null) {
            return 0;
        }
        class_746 player = class_7462;
        class_634 class_6342 = player.field_3944;
        class_640 class_6402 = info = class_6342 != null ? class_6342.method_2871(player.method_5667()) : null;
        return class_6402 != null ? class_6402.method_2959() : 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void exportCombinedStats() {
        File file = MiningModule.mc.field_1697;
        if (file == null) {
            return;
        }
        File dir = file;
        File outDir = new File(dir, "config/cobalt");
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        File file2 = new File(outDir, "hotm-perks.txt");
        String stamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()).format(Instant.now());
        Object object = file2;
        Object object2 = Charsets.UTF_8;
        int n = 8192;
        Object object3 = object;
        object = (object3 = (Writer)new OutputStreamWriter((OutputStream)new FileOutputStream((File)object3), (Charset)object2)) instanceof BufferedWriter ? (BufferedWriter)object3 : new BufferedWriter((Writer)object3, n);
        object2 = null;
        try {
            Object object4;
            Object object5;
            BufferedWriter writer = (BufferedWriter)object;
            boolean bl = false;
            ((Appendable)writer).append("# HOTM + Mining Stats Export @ " + stamp).append('\n');
            ((Appendable)writer).append("").append('\n');
            ((Appendable)writer).append("[HOTM Perks]").append('\n');
            if (hotmPerks.isEmpty()) {
                ((Appendable)writer).append("None").append('\n');
            } else {
                Map<String, Integer> $this$forEach$iv = hotmPerks;
                boolean $i$f$forEach = false;
                for (Map.Entry<String, Integer> element$iv : $this$forEach$iv.entrySet()) {
                    object5 = element$iv;
                    boolean bl2 = false;
                    String name = object5.getKey();
                    int level2 = ((Number)object5.getValue()).intValue();
                    ((Appendable)writer).append(name + ": " + level2).append('\n');
                }
            }
            ((Appendable)writer).append("").append('\n');
            ((Appendable)writer).append("[Mining Stats]").append('\n');
            if (miningStats.isEmpty() && miningSpeed <= 0.0) {
                ((Appendable)writer).append("None").append('\n');
            } else {
                LinkedHashMap<String, String> merged = new LinkedHashMap<String, String>(miningStats);
                if (miningSpeed > 0.0) {
                    boolean bl3;
                    block19: {
                        Set<String> set = merged.keySet();
                        Intrinsics.checkNotNullExpressionValue(set, (String)"<get-keys>(...)");
                        Iterable $this$any$iv = set;
                        boolean $i$f$any = false;
                        if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                            bl3 = false;
                        } else {
                            for (Map.Entry element$iv : $this$any$iv) {
                                String it = (String)((Object)element$iv);
                                boolean bl4 = false;
                                if (!StringsKt.equals((String)it, (String)"Mining Speed", (boolean)true)) continue;
                                bl3 = true;
                                break block19;
                            }
                            bl3 = false;
                        }
                    }
                    if (!bl3) {
                        ((Map)merged).put("Mining Speed", INSTANCE.formatNumber(miningSpeed));
                    }
                }
                Map $this$forEach$iv = merged;
                boolean $i$f$forEach = false;
                object4 = $this$forEach$iv.entrySet().iterator();
                while (object4.hasNext()) {
                    Map.Entry element$iv;
                    Map.Entry entry = element$iv = object4.next();
                    boolean bl5 = false;
                    String name = (String)entry.getKey();
                    String value = (String)entry.getValue();
                    ((Appendable)writer).append(name + ": " + value).append('\n');
                }
            }
            ((Appendable)writer).append("").append('\n');
            ((Appendable)writer).append("[Derived Mining Speed]").append('\n');
            DerivedMiningSpeed derived = MiningModule.computeDerivedMiningSpeed$default(INSTANCE, null, 1, null);
            ((Appendable)writer).append("Base Speed: " + INSTANCE.formatNumber(derived.getBaseSpeed())).append('\n');
            ((Appendable)writer).append("HOTM Passive: +" + INSTANCE.formatNumber(derived.getPassiveBonus())).append('\n');
            ((Appendable)writer).append("Strong Arm: +" + INSTANCE.formatNumber(derived.getStrongArmBonus()) + " (active=" + miningUmberTungsten.getValue() + ")").append('\n');
            ((Appendable)writer).append("Gem Bonus (Professional): +" + INSTANCE.formatNumber(derived.getProfessionalBonus()) + " (active=" + INSTANCE.hasSelectedGemstones() + ")").append('\n');
            ((Appendable)writer).append("Front Loaded: +" + INSTANCE.formatNumber(derived.getFrontLoadedBonus()) + " (active=" + frontLoadedActive.getValue() + ")").append('\n');
            ((Appendable)writer).append("Skymall: +" + INSTANCE.formatNumber(derived.getSkymallBonus()) + " (active=" + skymallActive.getValue() + ")").append('\n');
            Appendable $this$forEach$iv = writer;
            object4 = Locale.US;
            object5 = "%.2f";
            Object object6 = new Object[]{derived.getPrecisionMultiplier()};
            String string = String.format((Locale)object4, (String)object5, Arrays.copyOf(object6, ((Object[])object6).length));
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"format(...)");
            $this$forEach$iv.append("Precision Miner: x" + string + " (active=" + precisionActive.getValue() + ")").append('\n');
            ((Appendable)writer).append("Speed Boost: +" + INSTANCE.formatNumber(derived.getSpeedBoostBonus()) + " (active=" + speedBoostActive.getValue() + ")").append('\n');
            ((Appendable)writer).append("Effective Speed: " + INSTANCE.formatNumber(derived.getEffectiveSpeed())).append('\n');
            String string2 = (String)ArraysKt.getOrNull((Object[])blockType.getOptions(), (int)((Number)blockType.getValue()).intValue());
            if (string2 == null) {
                string2 = "Custom";
            }
            String blockLabel = string2;
            ((Appendable)writer).append("Block Type: " + blockLabel).append('\n');
            ((Appendable)writer).append("Block Strength: " + INSTANCE.formatNumber(INSTANCE.resolveBlockStrength())).append('\n');
            ((Appendable)writer).append("").append('\n');
            ((Appendable)writer).append("[Warm Heart]").append('\n');
            ((Appendable)writer).append("Warm Heart Level: " + derived.getWarmHeartLevel()).append('\n');
            Appendable appendable = writer;
            object5 = Locale.US;
            object6 = "%.2f";
            Object[] objectArray = new Object[]{derived.getWarmHeartCold()};
            String string3 = String.format((Locale)object5, (String)object6, Arrays.copyOf(objectArray, objectArray.length));
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"format(...)");
            object4 = "Cold Reduction: " + string3;
            Appendable appendable2 = appendable.append((CharSequence)object4).append('\n');
        }
        catch (Throwable throwable) {
            object2 = throwable;
            throw throwable;
        }
        finally {
            CloseableKt.closeFinally((Closeable)object, (Throwable)object2);
        }
    }

    private final class_1735 findMiningStatsSlot(class_465<?> screen) {
        Iterator iterator = screen.method_17577().field_7761.iterator();
        Intrinsics.checkNotNullExpressionValue((Object)iterator, (String)"iterator(...)");
        Iterator iterator2 = iterator;
        while (iterator2.hasNext()) {
            String name;
            class_1799 stack;
            class_1735 slot = (class_1735)iterator2.next();
            Intrinsics.checkNotNullExpressionValue((Object)slot.method_7677(), (String)"getItem(...)");
            if (stack.method_7960()) continue;
            String string = stack.method_7964().getString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
            String string2 = this.stripFormatting(string);
            Locale locale = Locale.ROOT;
            Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
            Intrinsics.checkNotNullExpressionValue((Object)string2.toLowerCase(locale), (String)"toLowerCase(...)");
            if (StringsKt.contains$default((CharSequence)name, (CharSequence)"mining stats", (boolean)false, (int)2, null)) {
                return slot;
            }
            List<class_2561> lore = this.getTooltipLines(stack);
            for (class_2561 line : lore) {
                String string3 = line.getString();
                Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"getString(...)");
                String raw = ((Object)StringsKt.trim((CharSequence)this.stripFormatting(string3))).toString();
                if (this.parseMiningSpeedStatValue(raw) == null) continue;
                return slot;
            }
        }
        return null;
    }

    private final void trySetHoveredSlot(class_465<?> screen, class_1735 slot) {
        Field field;
        Field field2 = hoveredSlotField;
        Field field3 = field2;
        if (field3 == null) {
            Field field4;
            Field it = field4 = this.resolveHoveredSlotField(screen);
            boolean bl = false;
            hoveredSlotField = it;
            field3 = field4;
        }
        if ((field = field3) != null) {
            try {
                field.set(screen, slot);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    /*
     * Unable to fully structure code
     */
    private final Field resolveHoveredSlotField(class_465<?> screen) {
        block7: {
            block6: {
                cls = screen.getClass();
                v0 = cls.getDeclaredFields();
                Intrinsics.checkNotNullExpressionValue((Object)v0, (String)"getDeclaredFields(...)");
                $this$firstOrNull$iv = v0;
                $i$f$firstOrNull = false;
                var7_7 = $this$firstOrNull$iv.length;
                for (var6_6 = 0; var6_6 < var7_7; ++var6_6) {
                    element$iv = $this$firstOrNull$iv[var6_6];
                    it = (Field)element$iv;
                    $i$a$-firstOrNull-MiningModule$resolveHoveredSlotField$byName$1 = false;
                    if (!class_1735.class.isAssignableFrom(it.getType())) ** GOTO lbl-1000
                    v1 = it.getName();
                    Intrinsics.checkNotNullExpressionValue((Object)v1, (String)"getName(...)");
                    if (StringsKt.contains((CharSequence)v1, (CharSequence)"hover", (boolean)true)) {
                        v2 = true;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v2 = false;
                    }
                    if (!v2) continue;
                    v3 = element$iv;
                    break block6;
                }
                v3 = null;
            }
            byName = (Field)v3;
            if (byName != null) {
                byName.setAccessible(true);
                return byName;
            }
            v4 = cls.getDeclaredFields();
            Intrinsics.checkNotNullExpressionValue((Object)v4, (String)"getDeclaredFields(...)");
            $this$firstOrNull$iv = v4;
            $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                it = (Field)element$iv;
                $i$a$-firstOrNull-MiningModule$resolveHoveredSlotField$byType$1 = false;
                if (!class_1735.class.isAssignableFrom(it.getType())) continue;
                v5 = element$iv;
                break block7;
            }
            v5 = null;
        }
        byType = (Field)v5;
        if (byType != null) {
            byType.setAccessible(true);
            return byType;
        }
        return null;
    }

    private final Double extractNumber(String text) {
        Matcher match = NUMBER_PATTERN.matcher(text);
        if (!match.find()) {
            return null;
        }
        String string = match.group(1);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"group(...)");
        String raw = StringsKt.replace$default((String)string, (String)",", (String)"", (boolean)false, (int)4, null);
        return StringsKt.toDoubleOrNull((String)raw);
    }

    private final Map<String, String> parseMiningStats(List<? extends class_2561> lines) {
        LinkedHashMap result = new LinkedHashMap();
        for (class_2561 class_25612 : lines) {
            Double statValue;
            String string = class_25612.getString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
            String raw = ((Object)StringsKt.trim((CharSequence)this.stripFormatting(string))).toString();
            if (((CharSequence)raw).length() == 0) continue;
            Matcher match = STAT_PAIR_PATTERN.matcher(raw);
            if (match.find()) {
                String string2 = match.group(1);
                Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"group(...)");
                String name = ((Object)StringsKt.trim((CharSequence)string2)).toString();
                String string3 = match.group(2);
                Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"group(...)");
                String value = ((Object)StringsKt.trim((CharSequence)string3)).toString();
                if (!StringsKt.isBlank((CharSequence)name) && !StringsKt.isBlank((CharSequence)value)) {
                    ((Map)result).put(name, value);
                    continue;
                }
            }
            if ((statValue = this.parseMiningSpeedStatValue(raw)) == null) continue;
            ((Map)result).put("Mining Speed", this.formatNumber(statValue));
        }
        return result;
    }

    private final Double parseMiningSpeedStatValue(String raw) {
        Matcher match = STAT_PAIR_PATTERN.matcher(raw);
        if (!match.find()) {
            return null;
        }
        String string = match.group(1);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"group(...)");
        String name = this.normalizeStatName(string);
        if (!StringsKt.equals((String)name, (String)"Mining Speed", (boolean)true)) {
            return null;
        }
        String string2 = match.group(2);
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"group(...)");
        return StringsKt.toDoubleOrNull((String)StringsKt.replace$default((String)string2, (String)",", (String)"", (boolean)false, (int)4, null));
    }

    private final String normalizeStatName(String raw) {
        String stripped = ((Object)StringsKt.trim((CharSequence)raw)).toString();
        String cleaned = LEADING_DECORATION_PATTERN.matcher(stripped).replaceAll("");
        Intrinsics.checkNotNull((Object)cleaned);
        return ((Object)StringsKt.trim((CharSequence)cleaned)).toString();
    }

    private final void updateBlockDetection() {
        String id;
        class_638 class_6382 = MiningModule.mc.field_1687;
        if (class_6382 == null) {
            return;
        }
        class_638 level2 = class_6382;
        class_239 hit = MiningModule.mc.field_1765;
        if (!(hit instanceof class_3965) || ((class_3965)hit).method_17783() != class_239.class_240.field_1332) {
            detectedBlockPos = null;
            detectedBlockId = null;
            detectedBlockText.setValue("Unknown");
            return;
        }
        detectedBlockPos = ((class_3965)hit).method_17777();
        class_2680 class_26802 = level2.method_8320(((class_3965)hit).method_17777());
        Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
        class_2680 state = class_26802;
        String string = class_7923.field_41175.method_10221((Object)state.method_26204()).toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        detectedBlockId = id = string;
        String label = MiningBlockRegistry.INSTANCE.getBLOCK_ID_TO_TYPE().get(id);
        if (label != null) {
            detectedBlockText.setValue(label);
        } else {
            detectedBlockText.setValue(StringsKt.substringAfter$default((String)id, (char)':', null, (int)2, null));
        }
    }

    private final void resetLookTickSample() {
        sampledLookTargetPos = null;
        sampledLookTargetId = null;
        sampledPingDelayTicks = null;
    }

    private final Pair<class_2338, String> resolveLookTarget() {
        class_638 level2 = MiningModule.mc.field_1687;
        if (level2 != null) {
            class_2338 class_23382 = MiningMacroModule.INSTANCE.getTrackedTargetBlock();
            if (class_23382 != null) {
                class_2338 pos = class_23382;
                boolean bl = false;
                String string = class_7923.field_41175.method_10221((Object)level2.method_8320(pos).method_26204()).toString();
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
                String id = string;
                return TuplesKt.to((Object)pos, (Object)id);
            }
        }
        class_2338 class_23383 = detectedBlockPos;
        if (class_23383 == null) {
            return null;
        }
        class_2338 pos = class_23383;
        String string = detectedBlockId;
        if (string == null) {
            return null;
        }
        String id = string;
        return TuplesKt.to((Object)pos, (Object)id);
    }

    private final double resolveBlockStrength() {
        return this.resolveMiningTarget().getHardness();
    }

    private final int resolveBaseMineTicks(double effectiveSpeed) {
        ResolvedMiningTarget resolved = this.resolveMiningTarget();
        return MiningBreakThresholds.INSTANCE.getOptimalTicks(resolved.getType(), effectiveSpeed, resolved.getHardness());
    }

    private final ResolvedMiningTarget resolveMiningTarget() {
        Double knownHardness;
        String selectedType;
        Double targetHardness;
        String targetType;
        String string;
        Object targetId;
        Object object;
        block14: {
            block13: {
                String string2;
                class_638 level2 = MiningModule.mc.field_1687;
                object = level2;
                if (object == null) break block13;
                class_638 activeLevel = object;
                boolean bl = false;
                class_2338 class_23382 = MiningMacroModule.INSTANCE.getTrackedTargetBlock();
                if (class_23382 != null) {
                    class_2338 pos = class_23382;
                    boolean bl2 = false;
                    string2 = class_7923.field_41175.method_10221((Object)activeLevel.method_8320(pos).method_26204()).toString();
                } else {
                    string2 = null;
                }
                object = string2;
                if (string2 != null) break block14;
            }
            object = detectedBlockId;
        }
        Object object2 = targetId = object;
        if (object2 != null) {
            Object it = object2;
            boolean bl = false;
            string = MiningBlockRegistry.INSTANCE.getBLOCK_ID_TO_TYPE().get(it);
        } else {
            string = null;
        }
        String string3 = targetType = string;
        if (string3 != null) {
            String it = string3;
            boolean bl = false;
            v6 = MiningBlockRegistry.INSTANCE.getBLOCK_HARDNESS().get(it);
        } else {
            v6 = targetHardness = null;
        }
        if (targetHardness != null) {
            return new ResolvedMiningTarget(targetType, targetHardness);
        }
        String string4 = selectedType = (String)ArraysKt.getOrNull((Object[])blockType.getOptions(), (int)((Number)blockType.getValue()).intValue());
        if (string4 != null) {
            String it = string4;
            boolean bl = false;
            v8 = MiningBlockRegistry.INSTANCE.getBLOCK_HARDNESS().get(it);
        } else {
            v8 = knownHardness = null;
        }
        if (knownHardness != null) {
            return new ResolvedMiningTarget(selectedType, knownHardness);
        }
        return new ResolvedMiningTarget(null, ((Number)blockStrength.getValue()).doubleValue());
    }

    private final double computeEffectiveMiningSpeed(double baseSpeed) {
        if (baseSpeed <= 0.0) {
            return 0.0;
        }
        DerivedMiningSpeed derived = this.computeDerivedMiningSpeed(baseSpeed);
        return derived.getEffectiveSpeed();
    }

    private final boolean hasSelectedGemstones() {
        String selectedType;
        boolean bl;
        block4: {
            List<String> selectedTypes = MiningMacroModule.INSTANCE.getSelectedTypesInOrder();
            Iterable $this$any$iv = selectedTypes;
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                bl = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    String it = (String)element$iv;
                    boolean bl2 = false;
                    if (!StringsKt.contains$default((CharSequence)it, (CharSequence)"Gemstone", (boolean)false, (int)2, null)) continue;
                    bl = true;
                    break block4;
                }
                bl = false;
            }
        }
        if (bl) {
            return true;
        }
        String string = selectedType = (String)ArraysKt.getOrNull((Object[])blockType.getOptions(), (int)((Number)blockType.getValue()).intValue());
        return string != null ? StringsKt.contains$default((CharSequence)string, (CharSequence)"Gemstone", (boolean)false, (int)2, null) : false;
    }

    private final boolean isPrecisionBonusApplied() {
        if (!((Boolean)precisionActive.getValue()).booleanValue()) {
            return false;
        }
        if (!MiningMacroModule.INSTANCE.isActive()) {
            return true;
        }
        return MiningMacroModule.INSTANCE.isUsingPrecisionPoint();
    }

    private final DerivedMiningSpeed computeDerivedMiningSpeed(Double baseOverride) {
        Double d = baseOverride;
        double base = d != null ? d : miningSpeed;
        double perkMiningSpeed = (double)this.getPerkLevel("mining speed") * 20.0;
        double speedyMineman = (double)this.getPerkLevel("speedy mineman") * 40.0;
        int strongArmLevel = this.getPerkLevel("strong arm");
        double strongArm = (Boolean)miningUmberTungsten.getValue() != false ? (double)strongArmLevel * 5.0 : 0.0;
        double passive = perkMiningSpeed + speedyMineman + strongArm;
        int professionalLevel = this.getPerkLevel("professional");
        double professionalBonus = this.hasSelectedGemstones() && professionalLevel > 0 ? 55.0 + (double)RangesKt.coerceAtLeast((int)(professionalLevel - 1), (int)0) * 5.0 : 0.0;
        double frontLoadedBonus = (Boolean)frontLoadedActive.getValue() != false ? 250.0 : 0.0;
        double skymallBonus = (Boolean)skymallActive.getValue() != false ? 100.0 : 0.0;
        double baseTotal = base + passive + professionalBonus + frontLoadedBonus + skymallBonus;
        int precisionLevel = this.getPerkLevel("precision miner");
        double precisionMultiplier = this.isPrecisionBonusApplied() && precisionLevel > 0 ? 1.0 + 0.3 * (double)precisionLevel : 1.0;
        double afterPrecision = baseTotal * precisionMultiplier;
        double speedBoostBonus = (Boolean)speedBoostActive.getValue() != false && this.getPerkLevel("mining speed boost") > 0 && miningSpeedBoostBuffActive ? afterPrecision * 2.5 : 0.0;
        int warmHeartLevel = this.getPerkLevel("warm heart");
        double warmHeartCold = (double)warmHeartLevel * 0.4;
        double effective = afterPrecision + speedBoostBonus;
        return new DerivedMiningSpeed(base, passive, strongArm, professionalBonus, frontLoadedBonus, skymallBonus, precisionMultiplier, speedBoostBonus, effective, warmHeartLevel, warmHeartCold);
    }

    static /* synthetic */ DerivedMiningSpeed computeDerivedMiningSpeed$default(MiningModule miningModule, Double d, int n, Object object) {
        if ((n & 1) != 0) {
            d = null;
        }
        return miningModule.computeDerivedMiningSpeed(d);
    }

    private final int getPerkLevel(String nameContains) {
        String string = nameContains;
        Locale locale = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
        String string2 = string.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
        String target = string2;
        for (Map.Entry entry : hotmPerks.entrySet()) {
            String name = (String)entry.getKey();
            int level2 = ((Number)entry.getValue()).intValue();
            String string3 = name;
            Locale locale2 = Locale.ROOT;
            Intrinsics.checkNotNullExpressionValue((Object)locale2, (String)"ROOT");
            String string4 = string3.toLowerCase(locale2);
            Intrinsics.checkNotNullExpressionValue((Object)string4, (String)"toLowerCase(...)");
            if (!StringsKt.contains$default((CharSequence)string4, (CharSequence)target, (boolean)false, (int)2, null)) continue;
            return level2;
        }
        return 0;
    }

    private final String stripFormatting(String text) {
        String string = class_124.method_539((String)text);
        if (string == null) {
            string = text;
        }
        return string;
    }

    private final String formatNumber(double value) {
        String string;
        if (value % 1.0 == 0.0) {
            string = String.valueOf((int)value);
        } else {
            Locale locale = Locale.US;
            String string2 = "%.2f";
            Object[] objectArray = new Object[]{value};
            String string3 = String.format(locale, string2, Arrays.copyOf(objectArray, objectArray.length));
            string = string3;
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"format(...)");
        }
        return string;
    }

    private final String formatRange(Pair<Double, Double> value) {
        return this.formatNumber(((Number)value.getFirst()).doubleValue()) + "-" + this.formatNumber(((Number)value.getSecond()).doubleValue());
    }

    /*
     * WARNING - void declaration
     */
    private final List<class_2561> getTooltipLines(class_1799 stack) {
        List<class_2561> list;
        class_746 player = MiningModule.mc.field_1724;
        class_638 level2 = MiningModule.mc.field_1687;
        try {
            void $this$filterTo$iv$iv;
            Method[] methodArray = stack.getClass().getMethods();
            Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"getMethods(...)");
            Object[] $this$filter$iv = methodArray;
            boolean $i$f$filter = false;
            Object[] objectArray = $this$filter$iv;
            Collection destination$iv$iv = new ArrayList();
            boolean $i$f$filterTo2 = false;
            for (void element$iv$iv : $this$filterTo$iv$iv) {
                Method it = (Method)element$iv$iv;
                boolean bl = false;
                if (!Intrinsics.areEqual((Object)it.getName(), (Object)"getTooltipLines")) continue;
                destination$iv$iv.add(element$iv$iv);
            }
            List methods = (List)destination$iv$iv;
            for (Method method : methods) {
                Object object;
                Class<?>[] params = method.getParameterTypes();
                if (params.length == 2 && class_1836.class.isAssignableFrom(params[1])) {
                    Object[] objectArray2 = new Object[]{player, class_1836.field_41070};
                    Object $i$f$filterTo2 = method.invoke((Object)stack, objectArray2);
                    List list2 = $i$f$filterTo2 instanceof List ? (List)$i$f$filterTo2 : null;
                    if (list2 == null) {
                        list2 = CollectionsKt.emptyList();
                    }
                    return list2;
                }
                if (params.length != 3 || !class_1836.class.isAssignableFrom(params[2])) continue;
                Class<?> ctxParam = params[0];
                if (level2 != null && ctxParam.isAssignableFrom(level2.getClass())) {
                    object = level2;
                } else {
                    Intrinsics.checkNotNull(ctxParam);
                    object = this.buildTooltipContext(ctxParam, (class_1937)level2);
                }
                Object ctx = object;
                Object[] objectArray3 = new Object[]{ctx, player, class_1836.field_41070};
                Object object2 = method.invoke((Object)stack, objectArray3);
                List list3 = object2 instanceof List ? (List)object2 : null;
                if (list3 == null) {
                    list3 = CollectionsKt.emptyList();
                }
                return list3;
            }
            list = ItemUtilsKt.getLoreLines(stack);
        }
        catch (Exception exception) {
            list = ItemUtilsKt.getLoreLines(stack);
        }
        return list;
    }

    /*
     * Could not resolve type clashes
     * Unable to fully structure code
     */
    private final Object buildTooltipContext(Class<?> ctxClass, class_1937 level) {
        block12: {
            if (level != null) {
                block11: {
                    v0 = ctxClass.getMethods();
                    Intrinsics.checkNotNullExpressionValue((Object)v0, (String)"getMethods(...)");
                    $this$firstOrNull$iv /* !! */  = v0;
                    $i$f$firstOrNull = false;
                    for (Object element$iv : $this$firstOrNull$iv /* !! */ ) {
                        it = (Method)element$iv;
                        $i$a$-firstOrNull-MiningModule$buildTooltipContext$ofMethod$1 = false;
                        if (!(Intrinsics.areEqual((Object)it.getName(), (Object)"of") != false && it.getParameterTypes().length == 1)) continue;
                        v1 = element$iv;
                        break block11;
                    }
                    v1 = null;
                }
                ofMethod = (Method)v1;
                if (ofMethod != null) {
                    try {
                        $this$firstOrNull$iv /* !! */  = new Object[]{level};
                        $this$firstOrNull$iv /* !! */  = ofMethod.invoke(null, $this$firstOrNull$iv /* !! */ );
                    }
                    catch (Exception <unused var>) {
                        $this$firstOrNull$iv /* !! */  = null;
                    }
                    return $this$firstOrNull$iv /* !! */ ;
                }
            }
            v2 = ctxClass.getMethods();
            Intrinsics.checkNotNullExpressionValue((Object)v2, (String)"getMethods(...)");
            $this$firstOrNull$iv /* !! */  = v2;
            $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv /* !! */ ) {
                it = (Method)element$iv;
                $i$a$-firstOrNull-MiningModule$buildTooltipContext$emptyMethod$1 = false;
                if (!Intrinsics.areEqual((Object)it.getName(), (Object)"empty")) ** GOTO lbl-1000
                v3 = it.getParameterTypes();
                Intrinsics.checkNotNullExpressionValue(v3, (String)"getParameterTypes(...)");
                if (((Object[])v3).length == 0) {
                    v4 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v4 = false;
                }
                if (!v4) continue;
                v5 = element$iv;
                break block12;
            }
            v5 = null;
        }
        emptyMethod = (Method)v5;
        if (emptyMethod != null) {
            try {
                var4_3 = emptyMethod.invoke(null, new Object[0]);
            }
            catch (Exception <unused var>) {
                var4_3 = null;
            }
            return var4_3;
        }
        return null;
    }

    private final void notify(String message) {
        NotificationManager.INSTANCE.queue("Mining", message, 2000L);
    }

    private final MiningNukerController.Config buildNukerConfig() {
        int n = (int)((Number)nukerRange.getValue()).doubleValue();
        int n2 = (int)((Number)nukerCooldownMs.getValue()).doubleValue();
        int n3 = (int)((Number)nukerBlocksPerTick.getValue()).doubleValue();
        return new MiningNukerController.Config(n, n2, n3, switch (((Number)nukerTargetMode.getValue()).intValue()) {
            case 1 -> MiningNukerController.TargetMode.EXPOSED_OR_SOFT;
            case 2 -> MiningNukerController.TargetMode.CUSTOM;
            default -> MiningNukerController.TargetMode.EXPOSED_ONLY;
        }, switch (((Number)nukerToolMode.getValue()).intValue()) {
            case 1 -> MiningNukerController.ToolMode.SOFT;
            case 2 -> MiningNukerController.ToolMode.CUSTOM;
            default -> MiningNukerController.ToolMode.STONE;
        }, MiningNukerController.INSTANCE.parseCustomMatchers((String)nukerCustomMatchers.getValue()), (Boolean)powderChestCollector.getValue());
    }

    private final int romanToInt(String roman) {
        int sum = 0;
        int last = 0;
        String string = roman;
        Locale locale = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
        String string2 = string.toUpperCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toUpperCase(...)");
        String chars = string2;
        for (int i = chars.length() - 1; -1 < i; --i) {
            int value;
            switch (chars.charAt(i)) {
                case 'I': {
                    int n = 1;
                    break;
                }
                case 'V': {
                    int n = 5;
                    break;
                }
                case 'X': {
                    int n = 10;
                    break;
                }
                default: {
                    int n = value = 0;
                }
            }
            sum = value < last ? (sum -= value) : (sum += value);
            last = value;
        }
        return sum;
    }

    @NotNull
    public final String getMiningCategory() {
        String string;
        List<String> types = MiningMacroModule.INSTANCE.getSelectedTypesInOrder();
        if (types.isEmpty()) {
            string = "None";
        } else {
            String it;
            boolean bl;
            boolean $i$f$any;
            Iterable $this$any$iv;
            block20: {
                $this$any$iv = types;
                $i$f$any = false;
                if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                    bl = false;
                } else {
                    for (Object element$iv : $this$any$iv) {
                        it = (String)element$iv;
                        boolean bl2 = false;
                        if (!StringsKt.contains$default((CharSequence)it, (CharSequence)"Gemstone", (boolean)false, (int)2, null)) continue;
                        bl = true;
                        break block20;
                    }
                    bl = false;
                }
            }
            if (bl) {
                string = "Gemstone";
            } else {
                boolean bl3;
                block21: {
                    $this$any$iv = types;
                    $i$f$any = false;
                    if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                        bl3 = false;
                    } else {
                        for (Object element$iv : $this$any$iv) {
                            it = (String)element$iv;
                            boolean bl4 = false;
                            if (!(StringsKt.startsWith$default((String)it, (String)"Mithril", (boolean)false, (int)2, null) || Intrinsics.areEqual((Object)it, (Object)"Titanium"))) continue;
                            bl3 = true;
                            break block21;
                        }
                        bl3 = false;
                    }
                }
                if (bl3) {
                    string = "Mithril";
                } else {
                    boolean bl5;
                    block22: {
                        $this$any$iv = types;
                        $i$f$any = false;
                        if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                            bl5 = false;
                        } else {
                            for (Object element$iv : $this$any$iv) {
                                it = (String)element$iv;
                                boolean bl6 = false;
                                if (!(Intrinsics.areEqual((Object)it, (Object)"Umber") || Intrinsics.areEqual((Object)it, (Object)"Tungsten") || Intrinsics.areEqual((Object)it, (Object)"Glacite"))) continue;
                                bl5 = true;
                                break block22;
                            }
                            bl5 = false;
                        }
                    }
                    if (bl5) {
                        string = "Tunnel";
                    } else {
                        boolean bl7;
                        block23: {
                            $this$any$iv = types;
                            $i$f$any = false;
                            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                                bl7 = false;
                            } else {
                                for (Object element$iv : $this$any$iv) {
                                    it = (String)element$iv;
                                    boolean bl8 = false;
                                    if (!StringsKt.startsWith$default((String)it, (String)"Pure", (boolean)false, (int)2, null)) continue;
                                    bl7 = true;
                                    break block23;
                                }
                                bl7 = false;
                            }
                        }
                        string = bl7 ? "Pure Ore" : (String)CollectionsKt.first(types);
                    }
                }
            }
        }
        return string;
    }

    @NotNull
    public final List<String> buildOverlayRows() {
        Object[] objectArray = new String[]{"Type:     " + this.getMiningCategory(), "Block:    " + detectedBlockText.getValue(), "Hardness: " + this.formatNumber(this.resolveBlockStrength()), "Speed:    " + miningSpeedText.getValue(), "Ping:     " + pingText.getValue() + " ms", "Delay:    " + this.formatRange((Pair<Double, Double>)((Pair)pingDelay.getValue())) + " ticks", "LookCalc: " + lookTicksText.getValue() + " ticks", "LookLeft: " + lookCountdownText.getValue() + " ticks"};
        List rows = CollectionsKt.mutableListOf((Object[])objectArray);
        if (((Boolean)nukerEnabled.getValue()).booleanValue()) {
            ((Collection)rows).add("Nuker:    " + (MiningMacroModule.INSTANCE.isActive() ? "Paused" : "Active"));
            if (((Boolean)powderChestCollector.getValue()).booleanValue()) {
                ((Collection)rows).add("Powder:   " + (MiningNukerController.INSTANCE.hasQueuedPowderChest() ? "Queued" : "Idle"));
                ((Collection)rows).add("Opened:   " + MiningNukerController.INSTANCE.getPowderChestsCollected());
            }
        }
        CollectionsKt.addAll((Collection)rows, (Iterable)MiningPrecisionTracker.INSTANCE.buildOverlayRows());
        return rows;
    }

    @Nullable
    public final class_2338 getDetectedBlockPos() {
        return detectedBlockPos;
    }

    @Nullable
    public final String getDetectedBlockId() {
        return detectedBlockId;
    }

    public final boolean isNukerActive() {
        return (Boolean)nukerEnabled.getValue() != false && !MiningMacroModule.INSTANCE.isActive();
    }

    public final int getPowderChestCount() {
        return MiningNukerController.INSTANCE.getPowderChestsCollected();
    }

    @NotNull
    public final List<Pair<String, Boolean>> getBuffStatuses() {
        Object[] objectArray = new Pair[]{TuplesKt.to((Object)"Lantern Buff", (Object)AutoLanternModule.INSTANCE.isLanternBuffActive()), TuplesKt.to((Object)"Mining Speed Boost", (Object)miningSpeedBoostBuffActive)};
        return CollectionsKt.listOf((Object[])objectArray);
    }

    @NotNull
    public final List<String> buildBuffStatusRows() {
        Object[] objectArray = new String[]{"Lantern Buff: " + AutoLanternModule.INSTANCE.getLanternBuffStatus(), "Mining Speed Boost: " + (miningSpeedBoostBuffActive ? "Active" : "Inactive")};
        return CollectionsKt.listOf((Object[])objectArray);
    }

    @NotNull
    public final List<String> getActivePerks() {
        int lvl;
        List result = new ArrayList();
        if (((Boolean)precisionActive.getValue()).booleanValue() && (lvl = this.getPerkLevel("precision miner")) > 0) {
            Locale locale = Locale.US;
            String string = "%.1f";
            Object[] objectArray = new Object[]{1.0 + 0.3 * (double)lvl};
            String string2 = String.format(locale, string, Arrays.copyOf(objectArray, objectArray.length));
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"format(...)");
            result.add("Precision Miner x" + string2);
        }
        if (((Boolean)speedBoostActive.getValue()).booleanValue() && this.getPerkLevel("mining speed boost") > 0) {
            result.add("Speed Boost x2.5");
        }
        if (((Boolean)frontLoadedActive.getValue()).booleanValue()) {
            result.add("Front Loaded +250 MS");
        }
        if (((Boolean)skymallActive.getValue()).booleanValue()) {
            result.add("Skymall +100 MS");
        }
        if (this.hasSelectedGemstones() && (lvl = this.getPerkLevel("professional")) > 0) {
            result.add("Professional +" + (55 + RangesKt.coerceAtLeast((int)(lvl - 1), (int)0) * 5) + " MS");
        }
        if (((Boolean)miningUmberTungsten.getValue()).booleanValue() && (lvl = this.getPerkLevel("strong arm")) > 0) {
            result.add("Strong Arm +" + lvl * 5 + " MS");
        }
        return result;
    }

    private static final Unit scrapeAll$lambda$0() {
        int drillSlot = InventoryUtils.findItemInHotbar("drill");
        if (drillSlot >= 0) {
            InventoryUtils.holdHotbarSlot(drillSlot);
        }
        pendingStatsScrape = true;
        class_638 class_6382 = MiningModule.mc.field_1687;
        pendingStatsTick = class_6382 != null ? class_6382.method_75260() : 0L;
        pendingScrapeAll = true;
        pendingHotmAfterStats = false;
        INSTANCE.sendCommand("/stats");
        return Unit.INSTANCE;
    }

    private static final Unit exportHotm$lambda$0() {
        class_465 screen;
        class_437 class_4372 = MiningModule.mc.field_1755;
        class_465 class_4652 = screen = class_4372 instanceof class_465 ? (class_465)class_4372 : null;
        if (screen == null) {
            INSTANCE.notify("Open /hotm first.");
            return Unit.INSTANCE;
        }
        Map<String, Integer> perks = INSTANCE.parseHotmPerks(screen);
        if (perks.isEmpty()) {
            INSTANCE.notify("No HOTM perks found on this screen.");
            return Unit.INSTANCE;
        }
        hotmPerks = perks;
        INSTANCE.exportCombinedStats();
        INSTANCE.updateHotmMultiplier();
        INSTANCE.notify("Exported HOTM perks (" + perks.size() + ").");
        return Unit.INSTANCE;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        enabled = new CheckboxSetting("Enabled", "Enable mining stats tracking.", false);
        blockStrength = new SliderSetting("Block Strength", "Fallback block strength used when the current block type is unknown.", 10.0, 1.0, 6000.0, 0.0, 32, null);
        blockType = new ModeSetting("Block Type", "Select a known block hardness/threshold profile to calculate ticks.", 0, MiningBlockRegistry.INSTANCE.getBLOCK_TYPES());
        detectedBlockText = new TextSetting("Detected Block", "Last detected block type (from crosshair).", "Unknown");
        pingDelay = new RangeSetting("Ping Delay", "Random extra ticks added to look time.", (Pair<Double, Double>)new Pair((Object)0.6, (Object)1.4), 0.0, 4.0);
        miningSpeedText = new TextSetting("Mining Speed", "Last captured mining speed from /stats.", "0");
        hotmMultiplierText = new TextSetting("HOTM Mult", "Derived HOTM multiplier (currently precision miner only).", "1.00");
        miningGems = new CheckboxSetting("Mining Gems", "Apply gem-only perks (Professional).", false);
        precisionActive = new CheckboxSetting("Precision Active", "Precision Miner particles are active (+30% mining speed).", false);
        speedBoostActive = new CheckboxSetting("Speed Boost Active", "Mining Speed Boost active (+250% mining speed).", false);
        autoActivateSpeedBoost = new CheckboxSetting("Auto Speed Boost", "Automatically right-click to activate Mining Speed Boost when it comes off cooldown.", false);
        frontLoadedActive = new CheckboxSetting("Front Loaded Active", "Front Loaded active (+250 mining speed).", false);
        skymallActive = new CheckboxSetting("Skymall Active", "Skymall active (+100 mining speed).", false);
        miningUmberTungsten = new CheckboxSetting("Mining Umber/Tungsten", "Apply Strong Arm bonus only while mining Umber or Tungsten.", false);
        pingText = new TextSetting("Ping", "Current ping (ms).", "0");
        lookTicksText = new TextSetting("Look Calc", "Computed total ticks to look at a block.", "0");
        lookCountdownText = new TextSetting("Look Left", "Remaining ticks until the next mining target, including ping delay.", "0");
        nukerEnabled = new CheckboxSetting("Nuker Active", "Mine nearby blocks using a recovered Jasper-style nuker pass.", false);
        powderChestCollector = new CheckboxSetting("Powder Chest Aura", "Right-click nearby powder chests while the nuker is active.", false);
        nukerRange = new SliderSetting("Nuker Range", "Horizontal and vertical range used by the nearby block nuker.", 4.0, 1.0, 8.0, 1.0);
        nukerCooldownMs = new SliderSetting("Nuker Cooldown MS", "Delay between recovered nuker bursts.", 100.0, 10.0, 500.0, 5.0);
        nukerBlocksPerTick = new SliderSetting("Nuker Blocks/Tick", "Maximum nearby blocks to start breaking each burst.", 1.0, 1.0, 8.0, 1.0);
        String[] stringArray = new String[]{"Exposed Only", "Exposed Or Soft", "Custom"};
        nukerTargetMode = new ModeSetting("Nuker Target Mode", "Recovered nuker target filter.", 0, stringArray);
        stringArray = new String[]{"Stone", "Soft", "Custom"};
        nukerToolMode = new ModeSetting("Nuker Tool Mode", "Tool family required before the nuker fires.", 0, stringArray);
        nukerCustomMatchers = new TextSetting("Nuker Custom Matchers", "Comma/newline-separated block ids or raw ids. Examples: minecraft:stone, 1, 1:5.", "");
        scrapeAll = new ActionSetting("Scrape All", "Equip drill, scrape /stats, then auto-scrape /hotm after.", "Scrape", null, MiningModule::scrapeAll$lambda$0, 8, null);
        exportHotm = new ActionSetting("Export HOTM", "Export the current Heart of the Mountain perks to a text file.", "Export", null, MiningModule::exportHotm$lambda$0, 8, null);
        hotmPerks = MapsKt.emptyMap();
        miningStats = MapsKt.emptyMap();
        hotmMultiplier = 1.0;
        MiningPrecisionTracker.INSTANCE.ensureInitialized();
        String side = "__side__";
        blockStrength.setUiGroup(side);
        pingDelay.setUiGroup(side);
        miningSpeedText.setUiGroup(side);
        hotmMultiplierText.setUiGroup(side);
        miningGems.setUiGroup(side);
        precisionActive.setUiGroup(side);
        speedBoostActive.setUiGroup(side);
        autoActivateSpeedBoost.setUiGroup(side);
        frontLoadedActive.setUiGroup(side);
        skymallActive.setUiGroup(side);
        miningUmberTungsten.setUiGroup(side);
        pingText.setUiGroup(side);
        lookTicksText.setUiGroup(side);
        lookCountdownText.setUiGroup(side);
        nukerEnabled.setUiGroup(side);
        powderChestCollector.setUiGroup(side);
        nukerRange.setUiGroup(side);
        nukerCooldownMs.setUiGroup(side);
        nukerBlocksPerTick.setUiGroup(side);
        nukerTargetMode.setUiGroup(side);
        nukerToolMode.setUiGroup(side);
        nukerCustomMatchers.setUiGroup(side);
        scrapeAll.setUiGroup(side);
        Setting[] settingArray = new Setting[26];
        settingArray[0] = enabled;
        settingArray[1] = blockStrength;
        settingArray[2] = blockType;
        settingArray[3] = pingDelay;
        settingArray[4] = miningSpeedText;
        settingArray[5] = hotmMultiplierText;
        settingArray[6] = miningGems;
        settingArray[7] = precisionActive;
        settingArray[8] = speedBoostActive;
        settingArray[9] = autoActivateSpeedBoost;
        settingArray[10] = frontLoadedActive;
        settingArray[11] = skymallActive;
        settingArray[12] = miningUmberTungsten;
        settingArray[13] = pingText;
        settingArray[14] = lookTicksText;
        settingArray[15] = lookCountdownText;
        settingArray[16] = nukerEnabled;
        settingArray[17] = powderChestCollector;
        settingArray[18] = nukerRange;
        settingArray[19] = nukerCooldownMs;
        settingArray[20] = nukerBlocksPerTick;
        settingArray[21] = nukerTargetMode;
        settingArray[22] = nukerToolMode;
        settingArray[23] = nukerCustomMatchers;
        settingArray[24] = scrapeAll;
        settingArray[25] = exportHotm;
        INSTANCE.addSetting(settingArray);
        EventBus.register(INSTANCE);
        NUMBER_PATTERN = Pattern.compile("([0-9][0-9,]*)");
        LEVEL_PATTERN = Pattern.compile("Level\\s+([0-9]+)");
        ROMAN_PATTERN = Pattern.compile("(?:Tier|Level)\\s+([IVX]+)", 2);
        STAT_PAIR_PATTERN = Pattern.compile("^(.+?)\\s*[:\\s]+([0-9][0-9,]*(?:\\.[0-9]+)?)$");
        LEADING_DECORATION_PATTERN = Pattern.compile("^[^A-Za-z0-9]+");
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0006\n\u0002\b\t\n\u0002\u0010\b\n\u0002\b\u0014\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0010\b\u0082\b\u0018\u00002\u00020\u0001B_\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u0012\u0006\u0010\u0007\u001a\u00020\u0002\u0012\u0006\u0010\b\u001a\u00020\u0002\u0012\u0006\u0010\t\u001a\u00020\u0002\u0012\u0006\u0010\n\u001a\u00020\u0002\u0012\u0006\u0010\u000b\u001a\u00020\u0002\u0012\u0006\u0010\r\u001a\u00020\f\u0012\u0006\u0010\u000e\u001a\u00020\u0002\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0010\u0010\u0011\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0010\u0010\u0013\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0013\u0010\u0012J\u0010\u0010\u0014\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0014\u0010\u0012J\u0010\u0010\u0015\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0015\u0010\u0012J\u0010\u0010\u0016\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0016\u0010\u0012J\u0010\u0010\u0017\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0017\u0010\u0012J\u0010\u0010\u0018\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0018\u0010\u0012J\u0010\u0010\u0019\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0019\u0010\u0012J\u0010\u0010\u001a\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u001a\u0010\u0012J\u0010\u0010\u001b\u001a\u00020\fH\u00c6\u0003\u00a2\u0006\u0004\b\u001b\u0010\u001cJ\u0010\u0010\u001d\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u001d\u0010\u0012J~\u0010\u001e\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u00022\b\b\u0002\u0010\u0007\u001a\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\u00022\b\b\u0002\u0010\t\u001a\u00020\u00022\b\b\u0002\u0010\n\u001a\u00020\u00022\b\b\u0002\u0010\u000b\u001a\u00020\u00022\b\b\u0002\u0010\r\u001a\u00020\f2\b\b\u0002\u0010\u000e\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\u001e\u0010\u001fJ\u001b\u0010\"\u001a\u00020!2\b\u0010 \u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\"\u0010#J\u0011\u0010$\u001a\u00020\fH\u00d6\u0081\u0004\u00a2\u0006\u0004\b$\u0010\u001cJ\u0011\u0010&\u001a\u00020%H\u00d6\u0081\u0004\u00a2\u0006\u0004\b&\u0010'R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010(\u001a\u0004\b)\u0010\u0012R\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010(\u001a\u0004\b*\u0010\u0012R\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010(\u001a\u0004\b+\u0010\u0012R\u0017\u0010\u0006\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010(\u001a\u0004\b,\u0010\u0012R\u0017\u0010\u0007\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010(\u001a\u0004\b-\u0010\u0012R\u0017\u0010\b\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\b\u0010(\u001a\u0004\b.\u0010\u0012R\u0017\u0010\t\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\t\u0010(\u001a\u0004\b/\u0010\u0012R\u0017\u0010\n\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\n\u0010(\u001a\u0004\b0\u0010\u0012R\u0017\u0010\u000b\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u000b\u0010(\u001a\u0004\b1\u0010\u0012R\u0017\u0010\r\u001a\u00020\f8\u0006\u00a2\u0006\f\n\u0004\b\r\u00102\u001a\u0004\b3\u0010\u001cR\u0017\u0010\u000e\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u000e\u0010(\u001a\u0004\b4\u0010\u0012\u00a8\u00065"}, d2={"Lorg/cobalt/internal/mining/MiningModule$DerivedMiningSpeed;", "", "", "baseSpeed", "passiveBonus", "strongArmBonus", "professionalBonus", "frontLoadedBonus", "skymallBonus", "precisionMultiplier", "speedBoostBonus", "effectiveSpeed", "", "warmHeartLevel", "warmHeartCold", "<init>", "(DDDDDDDDDID)V", "component1", "()D", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "()I", "component11", "copy", "(DDDDDDDDDID)Lorg/cobalt/internal/mining/MiningModule$DerivedMiningSpeed;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "D", "getBaseSpeed", "getPassiveBonus", "getStrongArmBonus", "getProfessionalBonus", "getFrontLoadedBonus", "getSkymallBonus", "getPrecisionMultiplier", "getSpeedBoostBonus", "getEffectiveSpeed", "I", "getWarmHeartLevel", "getWarmHeartCold", "cobalt"})
    private static final class DerivedMiningSpeed {
        private final double baseSpeed;
        private final double passiveBonus;
        private final double strongArmBonus;
        private final double professionalBonus;
        private final double frontLoadedBonus;
        private final double skymallBonus;
        private final double precisionMultiplier;
        private final double speedBoostBonus;
        private final double effectiveSpeed;
        private final int warmHeartLevel;
        private final double warmHeartCold;

        public DerivedMiningSpeed(double baseSpeed, double passiveBonus, double strongArmBonus, double professionalBonus, double frontLoadedBonus, double skymallBonus, double precisionMultiplier, double speedBoostBonus, double effectiveSpeed, int warmHeartLevel, double warmHeartCold) {
            this.baseSpeed = baseSpeed;
            this.passiveBonus = passiveBonus;
            this.strongArmBonus = strongArmBonus;
            this.professionalBonus = professionalBonus;
            this.frontLoadedBonus = frontLoadedBonus;
            this.skymallBonus = skymallBonus;
            this.precisionMultiplier = precisionMultiplier;
            this.speedBoostBonus = speedBoostBonus;
            this.effectiveSpeed = effectiveSpeed;
            this.warmHeartLevel = warmHeartLevel;
            this.warmHeartCold = warmHeartCold;
        }

        public final double getBaseSpeed() {
            return this.baseSpeed;
        }

        public final double getPassiveBonus() {
            return this.passiveBonus;
        }

        public final double getStrongArmBonus() {
            return this.strongArmBonus;
        }

        public final double getProfessionalBonus() {
            return this.professionalBonus;
        }

        public final double getFrontLoadedBonus() {
            return this.frontLoadedBonus;
        }

        public final double getSkymallBonus() {
            return this.skymallBonus;
        }

        public final double getPrecisionMultiplier() {
            return this.precisionMultiplier;
        }

        public final double getSpeedBoostBonus() {
            return this.speedBoostBonus;
        }

        public final double getEffectiveSpeed() {
            return this.effectiveSpeed;
        }

        public final int getWarmHeartLevel() {
            return this.warmHeartLevel;
        }

        public final double getWarmHeartCold() {
            return this.warmHeartCold;
        }

        public final double component1() {
            return this.baseSpeed;
        }

        public final double component2() {
            return this.passiveBonus;
        }

        public final double component3() {
            return this.strongArmBonus;
        }

        public final double component4() {
            return this.professionalBonus;
        }

        public final double component5() {
            return this.frontLoadedBonus;
        }

        public final double component6() {
            return this.skymallBonus;
        }

        public final double component7() {
            return this.precisionMultiplier;
        }

        public final double component8() {
            return this.speedBoostBonus;
        }

        public final double component9() {
            return this.effectiveSpeed;
        }

        public final int component10() {
            return this.warmHeartLevel;
        }

        public final double component11() {
            return this.warmHeartCold;
        }

        @NotNull
        public final DerivedMiningSpeed copy(double baseSpeed, double passiveBonus, double strongArmBonus, double professionalBonus, double frontLoadedBonus, double skymallBonus, double precisionMultiplier, double speedBoostBonus, double effectiveSpeed, int warmHeartLevel, double warmHeartCold) {
            return new DerivedMiningSpeed(baseSpeed, passiveBonus, strongArmBonus, professionalBonus, frontLoadedBonus, skymallBonus, precisionMultiplier, speedBoostBonus, effectiveSpeed, warmHeartLevel, warmHeartCold);
        }

        public static /* synthetic */ DerivedMiningSpeed copy$default(DerivedMiningSpeed derivedMiningSpeed, double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9, int n, double d10, int n2, Object object) {
            if ((n2 & 1) != 0) {
                d = derivedMiningSpeed.baseSpeed;
            }
            if ((n2 & 2) != 0) {
                d2 = derivedMiningSpeed.passiveBonus;
            }
            if ((n2 & 4) != 0) {
                d3 = derivedMiningSpeed.strongArmBonus;
            }
            if ((n2 & 8) != 0) {
                d4 = derivedMiningSpeed.professionalBonus;
            }
            if ((n2 & 0x10) != 0) {
                d5 = derivedMiningSpeed.frontLoadedBonus;
            }
            if ((n2 & 0x20) != 0) {
                d6 = derivedMiningSpeed.skymallBonus;
            }
            if ((n2 & 0x40) != 0) {
                d7 = derivedMiningSpeed.precisionMultiplier;
            }
            if ((n2 & 0x80) != 0) {
                d8 = derivedMiningSpeed.speedBoostBonus;
            }
            if ((n2 & 0x100) != 0) {
                d9 = derivedMiningSpeed.effectiveSpeed;
            }
            if ((n2 & 0x200) != 0) {
                n = derivedMiningSpeed.warmHeartLevel;
            }
            if ((n2 & 0x400) != 0) {
                d10 = derivedMiningSpeed.warmHeartCold;
            }
            return derivedMiningSpeed.copy(d, d2, d3, d4, d5, d6, d7, d8, d9, n, d10);
        }

        @NotNull
        public String toString() {
            return "DerivedMiningSpeed(baseSpeed=" + this.baseSpeed + ", passiveBonus=" + this.passiveBonus + ", strongArmBonus=" + this.strongArmBonus + ", professionalBonus=" + this.professionalBonus + ", frontLoadedBonus=" + this.frontLoadedBonus + ", skymallBonus=" + this.skymallBonus + ", precisionMultiplier=" + this.precisionMultiplier + ", speedBoostBonus=" + this.speedBoostBonus + ", effectiveSpeed=" + this.effectiveSpeed + ", warmHeartLevel=" + this.warmHeartLevel + ", warmHeartCold=" + this.warmHeartCold + ")";
        }

        public int hashCode() {
            int result = Double.hashCode(this.baseSpeed);
            result = result * 31 + Double.hashCode(this.passiveBonus);
            result = result * 31 + Double.hashCode(this.strongArmBonus);
            result = result * 31 + Double.hashCode(this.professionalBonus);
            result = result * 31 + Double.hashCode(this.frontLoadedBonus);
            result = result * 31 + Double.hashCode(this.skymallBonus);
            result = result * 31 + Double.hashCode(this.precisionMultiplier);
            result = result * 31 + Double.hashCode(this.speedBoostBonus);
            result = result * 31 + Double.hashCode(this.effectiveSpeed);
            result = result * 31 + Integer.hashCode(this.warmHeartLevel);
            result = result * 31 + Double.hashCode(this.warmHeartCold);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof DerivedMiningSpeed)) {
                return false;
            }
            DerivedMiningSpeed derivedMiningSpeed = (DerivedMiningSpeed)other;
            if (Double.compare(this.baseSpeed, derivedMiningSpeed.baseSpeed) != 0) {
                return false;
            }
            if (Double.compare(this.passiveBonus, derivedMiningSpeed.passiveBonus) != 0) {
                return false;
            }
            if (Double.compare(this.strongArmBonus, derivedMiningSpeed.strongArmBonus) != 0) {
                return false;
            }
            if (Double.compare(this.professionalBonus, derivedMiningSpeed.professionalBonus) != 0) {
                return false;
            }
            if (Double.compare(this.frontLoadedBonus, derivedMiningSpeed.frontLoadedBonus) != 0) {
                return false;
            }
            if (Double.compare(this.skymallBonus, derivedMiningSpeed.skymallBonus) != 0) {
                return false;
            }
            if (Double.compare(this.precisionMultiplier, derivedMiningSpeed.precisionMultiplier) != 0) {
                return false;
            }
            if (Double.compare(this.speedBoostBonus, derivedMiningSpeed.speedBoostBonus) != 0) {
                return false;
            }
            if (Double.compare(this.effectiveSpeed, derivedMiningSpeed.effectiveSpeed) != 0) {
                return false;
            }
            if (this.warmHeartLevel != derivedMiningSpeed.warmHeartLevel) {
                return false;
            }
            return Double.compare(this.warmHeartCold, derivedMiningSpeed.warmHeartCold) == 0;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\b\b\u0082\b\u0018\u00002\u00020\u0001B\u0019\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0012\u0010\b\u001a\u0004\u0018\u00010\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ&\u0010\f\u001a\u00020\u00002\n\b\u0002\u0010\u0003\u001a\u0004\u0018\u00010\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u001b\u0010\u0010\u001a\u00020\u000f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0011\u0010\u0013\u001a\u00020\u0012H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0011\u0010\u0015\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0015\u0010\tR\u0019\u0010\u0003\u001a\u0004\u0018\u00010\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0016\u001a\u0004\b\u0017\u0010\tR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0018\u001a\u0004\b\u0019\u0010\u000b\u00a8\u0006\u001a"}, d2={"Lorg/cobalt/internal/mining/MiningModule$ResolvedMiningTarget;", "", "", "type", "", "hardness", "<init>", "(Ljava/lang/String;D)V", "component1", "()Ljava/lang/String;", "component2", "()D", "copy", "(Ljava/lang/String;D)Lorg/cobalt/internal/mining/MiningModule$ResolvedMiningTarget;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getType", "D", "getHardness", "cobalt"})
    private static final class ResolvedMiningTarget {
        @Nullable
        private final String type;
        private final double hardness;

        public ResolvedMiningTarget(@Nullable String type, double hardness) {
            this.type = type;
            this.hardness = hardness;
        }

        @Nullable
        public final String getType() {
            return this.type;
        }

        public final double getHardness() {
            return this.hardness;
        }

        @Nullable
        public final String component1() {
            return this.type;
        }

        public final double component2() {
            return this.hardness;
        }

        @NotNull
        public final ResolvedMiningTarget copy(@Nullable String type, double hardness) {
            return new ResolvedMiningTarget(type, hardness);
        }

        public static /* synthetic */ ResolvedMiningTarget copy$default(ResolvedMiningTarget resolvedMiningTarget, String string, double d, int n, Object object) {
            if ((n & 1) != 0) {
                string = resolvedMiningTarget.type;
            }
            if ((n & 2) != 0) {
                d = resolvedMiningTarget.hardness;
            }
            return resolvedMiningTarget.copy(string, d);
        }

        @NotNull
        public String toString() {
            return "ResolvedMiningTarget(type=" + this.type + ", hardness=" + this.hardness + ")";
        }

        public int hashCode() {
            int result = this.type == null ? 0 : this.type.hashCode();
            result = result * 31 + Double.hashCode(this.hardness);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ResolvedMiningTarget)) {
                return false;
            }
            ResolvedMiningTarget resolvedMiningTarget = (ResolvedMiningTarget)other;
            if (!Intrinsics.areEqual((Object)this.type, (Object)resolvedMiningTarget.type)) {
                return false;
            }
            return Double.compare(this.hardness, resolvedMiningTarget.hardness) == 0;
        }
    }
}

