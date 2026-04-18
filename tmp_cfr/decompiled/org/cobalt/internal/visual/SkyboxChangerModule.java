/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.systems.RenderSystem$class_5590
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.Triple
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.collections.SetsKt
 *  kotlin.comparisons.ComparisonsKt
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.io.CloseableKt
 *  kotlin.jdk7.AutoCloseableKt
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.jvm.internal.SpreadBuilder
 *  kotlin.math.MathKt
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  net.minecraft.class_1011
 *  net.minecraft.class_1043
 *  net.minecraft.class_10799
 *  net.minecraft.class_276
 *  net.minecraft.class_287
 *  net.minecraft.class_290
 *  net.minecraft.class_310
 *  net.minecraft.class_9799
 *  net.minecraft.class_9801
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package org.cobalt.internal.visual;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Triple;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.io.CloseableKt;
import kotlin.jdk7.AutoCloseableKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.jvm.internal.SpreadBuilder;
import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_10799;
import net.minecraft.class_276;
import net.minecraft.class_287;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_9799;
import net.minecraft.class_9801;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00be\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u000e\n\u0002\u0010%\n\u0002\u0010!\n\u0002\b\u0016\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\r\b\u00c6\u0002\u0018\u00002\u00020\u0001:\n\u0095\u0001\u0096\u0001\u0097\u0001\u0098\u0001\u0099\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0007\u00a2\u0006\u0004\b\f\u0010\rJ\u000f\u0010\u000f\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0019\u0010\u0014\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0012\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u0011\u0010\u0016\u001a\u0004\u0018\u00010\u0013H\u0002\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u000f\u0010\u0019\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\u0011\u0010\u001c\u001a\u0004\u0018\u00010\u001bH\u0002\u00a2\u0006\u0004\b\u001c\u0010\u001dJ\u000f\u0010\u001e\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u001e\u0010\u0003J\u0019\u0010 \u001a\u0004\u0018\u00010\u00132\u0006\u0010\u001f\u001a\u00020\u001bH\u0002\u00a2\u0006\u0004\b \u0010!J3\u0010'\u001a\u0016\u0012\u0004\u0012\u00020$\u0012\n\u0012\b\u0012\u0004\u0012\u00020&0%\u0018\u00010#2\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\"\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b'\u0010(J3\u0010)\u001a\u0016\u0012\u0004\u0012\u00020$\u0012\n\u0012\b\u0012\u0004\u0012\u00020&0%\u0018\u00010#2\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\"\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b)\u0010(J3\u0010*\u001a\u0016\u0012\u0004\u0012\u00020$\u0012\n\u0012\b\u0012\u0004\u0012\u00020&0%\u0018\u00010#2\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\"\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b*\u0010(J+\u0010,\u001a\u0016\u0012\u0004\u0012\u00020$\u0012\n\u0012\b\u0012\u0004\u0012\u00020&0%\u0018\u00010#2\u0006\u0010+\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\b,\u0010-J%\u0010/\u001a\b\u0012\u0004\u0012\u00020\u00110%2\u0006\u0010+\u001a\u00020\u00112\u0006\u0010.\u001a\u00020$H\u0002\u00a2\u0006\u0004\b/\u00100J9\u00102\u001a\u0016\u0012\u0004\u0012\u00020$\u0012\n\u0012\b\u0012\u0004\u0012\u00020&0%\u0018\u00010#2\f\u00101\u001a\b\u0012\u0004\u0012\u00020\u00110%2\u0006\u0010\"\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b2\u00103J%\u00105\u001a\b\u0012\u0004\u0012\u00020&0%2\u0006\u00104\u001a\u00020\u00112\u0006\u0010\"\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b5\u00106J+\u00105\u001a\b\u0012\u0004\u0012\u00020&0%2\f\u00101\u001a\b\u0012\u0004\u0012\u00020\u00110%2\u0006\u0010\"\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b5\u00107J\u001d\u00109\u001a\b\u0012\u0004\u0012\u0002080%2\u0006\u0010\u0012\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\b9\u0010:J\u001f\u0010<\u001a\u00020&2\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010;\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b<\u0010=J%\u0010>\u001a\b\u0012\u0004\u0012\u00020&0%2\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\"\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b>\u00106J\u001f\u0010@\u001a\u00020&2\u0006\u0010?\u001a\u0002082\u0006\u0010;\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b@\u0010AJ1\u0010B\u001a\u0014\u0012\u0004\u0012\u00020$\u0012\n\u0012\b\u0012\u0004\u0012\u00020&0%0#2\u0006\u0010?\u001a\u0002082\u0006\u0010\"\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\bB\u0010CJ\u0017\u0010D\u001a\u00020\u000b2\u0006\u0010?\u001a\u000208H\u0002\u00a2\u0006\u0004\bD\u0010EJ+\u0010G\u001a\u000e\u0012\u0004\u0012\u00020$\u0012\u0004\u0012\u00020&0#2\u0006\u0010F\u001a\u0002082\u0006\u0010\"\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\bG\u0010CJ9\u0010L\u001a\u0014\u0012\u0004\u0012\u00020H\u0012\u0004\u0012\u00020H\u0012\u0004\u0012\u00020H0K2\u0006\u0010.\u001a\u00020$2\u0006\u0010I\u001a\u00020H2\u0006\u0010J\u001a\u00020HH\u0002\u00a2\u0006\u0004\bL\u0010MJ1\u0010P\u001a\u00020O2\u0006\u0010F\u001a\u0002082\u0018\u0010N\u001a\u0014\u0012\u0004\u0012\u00020H\u0012\u0004\u0012\u00020H\u0012\u0004\u0012\u00020H0KH\u0002\u00a2\u0006\u0004\bP\u0010QJ\u0017\u0010S\u001a\u00020H2\u0006\u0010R\u001a\u00020HH\u0002\u00a2\u0006\u0004\bS\u0010TJ?\u0010[\u001a\u00020O2\u0006\u0010U\u001a\u00020O2\u0006\u0010V\u001a\u00020O2\u0006\u0010W\u001a\u00020O2\u0006\u0010X\u001a\u00020O2\u0006\u0010Y\u001a\u00020H2\u0006\u0010Z\u001a\u00020HH\u0002\u00a2\u0006\u0004\b[\u0010\\J?\u0010]\u001a\u00020O2\u0006\u0010U\u001a\u00020O2\u0006\u0010V\u001a\u00020O2\u0006\u0010W\u001a\u00020O2\u0006\u0010X\u001a\u00020O2\u0006\u0010Y\u001a\u00020H2\u0006\u0010Z\u001a\u00020HH\u0002\u00a2\u0006\u0004\b]\u0010\\J!\u0010`\u001a\u0014\u0012\u0004\u0012\u00020$\u0012\n\u0012\b\u0012\u0004\u0012\u00020&0_0^H\u0002\u00a2\u0006\u0004\b`\u0010aJ;\u0010c\u001a\u0014\u0012\u0004\u0012\u00020$\u0012\n\u0012\b\u0012\u0004\u0012\u00020&0%0#2\u0018\u0010b\u001a\u0014\u0012\u0004\u0012\u00020$\u0012\n\u0012\b\u0012\u0004\u0012\u00020&0_0#H\u0002\u00a2\u0006\u0004\bc\u0010dJ)\u0010e\u001a\u00020\u00062\u0018\u0010b\u001a\u0014\u0012\u0004\u0012\u00020$\u0012\n\u0012\b\u0012\u0004\u0012\u00020&0%0#H\u0002\u00a2\u0006\u0004\be\u0010fJ\u001d\u0010h\u001a\b\u0012\u0004\u0012\u00020\u00110%2\u0006\u0010g\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\bh\u0010:J\u0017\u0010i\u001a\u00020\u000b2\u0006\u0010\u0012\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\bi\u0010jJ\u0017\u0010k\u001a\u00020\u00182\u0006\u0010\u0012\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\bk\u0010lJ\u000f\u0010m\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\bm\u0010nJ\u0017\u0010o\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\bo\u0010pJ\u0019\u0010r\u001a\u0004\u0018\u00010\u00132\u0006\u0010q\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\br\u0010sR\u001a\u0010t\u001a\b\u0012\u0004\u0012\u00020\u001b0%8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bt\u0010uR\u0014\u0010w\u001a\u00020v8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bw\u0010xR\u0014\u0010z\u001a\u00020y8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bz\u0010{R\u0014\u0010|\u001a\u00020y8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b|\u0010{R\u0014\u0010~\u001a\u00020}8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b~\u0010\u007fR\u0018\u0010\u0081\u0001\u001a\u00030\u0080\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0081\u0001\u0010\u0082\u0001R\u0018\u0010\u0084\u0001\u001a\u00030\u0083\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0084\u0001\u0010\u0085\u0001R\u0016\u0010\u0086\u0001\u001a\u00020y8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0086\u0001\u0010{R\u0016\u0010\u0087\u0001\u001a\u00020v8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0087\u0001\u0010xR\u0018\u0010\u0088\u0001\u001a\u00030\u0083\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0088\u0001\u0010\u0085\u0001R\u0018\u0010\u0089\u0001\u001a\u00030\u0083\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0089\u0001\u0010\u0085\u0001R\u0018\u0010\u008a\u0001\u001a\u00030\u0083\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u008a\u0001\u0010\u0085\u0001R\u0019\u0010\u008b\u0001\u001a\u00020\u00188\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u008b\u0001\u0010\u008c\u0001R\u0018\u0010\u008e\u0001\u001a\u00030\u008d\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u008e\u0001\u0010\u008f\u0001R\u0019\u0010\u0090\u0001\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0090\u0001\u0010\u0091\u0001R\u0019\u0010\u0092\u0001\u001a\u00020\u00188\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0092\u0001\u0010\u008c\u0001R\u001b\u0010\u0093\u0001\u001a\u0004\u0018\u00010\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0093\u0001\u0010\u0094\u0001\u00a8\u0006\u009a\u0001"}, d2={"Lorg/cobalt/internal/visual/SkyboxChangerModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/TickEvent$End;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$End;)V", "Lcom/mojang/blaze3d/buffers/GpuBufferSlice;", "fogBuffer", "", "renderCustomSky", "(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)Z", "Lorg/cobalt/internal/visual/SkyboxChangerModule$SkyboxAnimationState;", "animationState", "()Lorg/cobalt/internal/visual/SkyboxChangerModule$SkyboxAnimationState;", "Ljava/nio/file/Path;", "path", "Lorg/cobalt/internal/visual/SkyboxChangerModule$ImportedSkybox;", "loadSkybox", "(Ljava/nio/file/Path;)Lorg/cobalt/internal/visual/SkyboxChangerModule$ImportedSkybox;", "loadConfiguredSkybox", "()Lorg/cobalt/internal/visual/SkyboxChangerModule$ImportedSkybox;", "", "currentConfigurationSignature", "()Ljava/lang/String;", "Lorg/cobalt/internal/visual/SkyboxChangerModule$BuiltInPreset;", "selectedBuiltInPreset", "()Lorg/cobalt/internal/visual/SkyboxChangerModule$BuiltInPreset;", "migrateLegacyPresetSelection", "preset", "loadBundledPresetSkybox", "(Lorg/cobalt/internal/visual/SkyboxChangerModule$BuiltInPreset;)Lorg/cobalt/internal/visual/SkyboxChangerModule$ImportedSkybox;", "labelPrefix", "", "Lorg/cobalt/internal/visual/SkyboxChangerModule$SkyboxFace;", "", "Lnet/minecraft/class_1043;", "loadFileSkybox", "(Ljava/nio/file/Path;Ljava/lang/String;)Ljava/util/Map;", "loadStaticSkybox", "loadGifSkybox", "root", "loadDirectorySkybox", "(Ljava/nio/file/Path;)Ljava/util/Map;", "face", "findFaceSources", "(Ljava/nio/file/Path;Lorg/cobalt/internal/visual/SkyboxChangerModule$SkyboxFace;)Ljava/util/List;", "sources", "loadSharedSkyboxSources", "(Ljava/util/List;Ljava/lang/String;)Ljava/util/Map;", "source", "loadTextureFrames", "(Ljava/nio/file/Path;Ljava/lang/String;)Ljava/util/List;", "(Ljava/util/List;Ljava/lang/String;)Ljava/util/List;", "Ljava/awt/image/BufferedImage;", "readGifFrames", "(Ljava/nio/file/Path;)Ljava/util/List;", "label", "loadStaticTexture", "(Ljava/nio/file/Path;Ljava/lang/String;)Lnet/minecraft/class_1043;", "loadGifFrames", "image", "bufferedImageToTexture", "(Ljava/awt/image/BufferedImage;Ljava/lang/String;)Lnet/minecraft/class_1043;", "mapImageToSkybox", "(Ljava/awt/image/BufferedImage;Ljava/lang/String;)Ljava/util/Map;", "isPanoramaFrame", "(Ljava/awt/image/BufferedImage;)Z", "panorama", "convertPanoramaToCubemap", "", "u", "v", "Lkotlin/Triple;", "cubemapDirection", "(Lorg/cobalt/internal/visual/SkyboxChangerModule$SkyboxFace;DD)Lkotlin/Triple;", "direction", "", "samplePanorama", "(Ljava/awt/image/BufferedImage;Lkotlin/Triple;)I", "value", "wrapUnit", "(D)D", "topLeft", "topRight", "bottomLeft", "bottomRight", "tx", "ty", "interpolateArgb", "(IIIIDD)I", "bilerp", "", "", "mutableFaceFrameMap", "()Ljava/util/Map;", "faceFrames", "freezeFaceFrameMap", "(Ljava/util/Map;)Ljava/util/Map;", "closeFaceFrames", "(Ljava/util/Map;)V", "dir", "listImageFiles", "isSupportedImage", "(Ljava/nio/file/Path;)Z", "imageStem", "(Ljava/nio/file/Path;)Ljava/lang/String;", "resolveConfiguredPath", "()Ljava/nio/file/Path;", "ensureImportPathExists", "(Ljava/nio/file/Path;)V", "reason", "failLoad", "(Ljava/lang/String;)Lorg/cobalt/internal/visual/SkyboxChangerModule$ImportedSkybox;", "builtInPresets", "Ljava/util/List;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabledSetting", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "importInfo", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "presetInfo", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "presetSetting", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "pathSetting", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "frameDurationSetting", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "animationInfo", "animateSkySetting", "horizontalDriftSetting", "verticalDriftRangeSetting", "verticalDriftCycleSetting", "lastLoadStatus", "Ljava/lang/String;", "Lorg/cobalt/api/module/setting/impl/ActionSetting;", "reloadSetting", "Lorg/cobalt/api/module/setting/impl/ActionSetting;", "reloadQueued", "Z", "loadedSignature", "loadedSkybox", "Lorg/cobalt/internal/visual/SkyboxChangerModule$ImportedSkybox;", "BuiltInPreset", "ImportedSkybox", "SkyboxAnimationState", "SkyboxFace", "SkyboxRenderer", "cobalt"})
@SourceDebugExtension(value={"SMAP\nSkyboxChangerModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SkyboxChangerModule.kt\norg/cobalt/internal/visual/SkyboxChangerModule\n+ 2 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 5 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 6 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,800:1\n193#2,3:801\n221#2,2:818\n193#2,3:827\n193#2,3:830\n221#2,2:847\n1#3:804\n1596#4:805\n1629#4,4:806\n1300#4,2:810\n1315#4,4:812\n1924#4,2:816\n1926#4:820\n1300#4,2:821\n1315#4,4:823\n1266#4,4:835\n1915#4,2:839\n1915#4:841\n296#4,2:842\n1916#4:844\n1924#4,2:845\n1926#4:849\n1924#4,2:850\n1915#4,2:852\n1926#4:854\n1596#4:855\n1629#4,4:856\n1300#4,2:860\n1315#4,4:862\n1266#4,4:868\n1300#4,2:872\n1315#4,4:874\n1300#4,2:878\n1315#4,4:880\n1266#4,4:886\n1915#4,2:890\n777#4:892\n873#4,2:893\n1068#4:895\n1586#4:896\n1661#4,3:897\n466#5:833\n415#5:834\n466#5:866\n415#5:867\n466#5:884\n415#5:885\n37#6,2:900\n*S KotlinDebug\n*F\n+ 1 SkyboxChangerModule.kt\norg/cobalt/internal/visual/SkyboxChangerModule\n*L\n218#1:801,3\n309#1:818,2\n318#1:827,3\n321#1:830,3\n366#1:847,2\n300#1:805\n300#1:806,4\n303#1:810,2\n303#1:812,4\n307#1:816,2\n307#1:820\n317#1:821,2\n317#1:823,4\n324#1:835,4\n337#1:839,2\n345#1:841\n346#1:842,2\n345#1:844\n359#1:845,2\n359#1:849\n380#1:850,2\n383#1:852,2\n380#1:854\n419#1:855\n419#1:856,4\n437#1:860,2\n437#1:862,4\n441#1:868,4\n457#1:872,2\n457#1:874,4\n584#1:878,2\n584#1:880,4\n590#1:886,4\n594#1:890,2\n602#1:892\n602#1:893,2\n603#1:895\n79#1:896\n79#1:897,3\n324#1:833\n324#1:834\n441#1:866\n441#1:867\n590#1:884\n590#1:885\n79#1:900,2\n*E\n"})
public final class SkyboxChangerModule
extends Module {
    @NotNull
    public static final SkyboxChangerModule INSTANCE;
    @NotNull
    private static final List<BuiltInPreset> builtInPresets;
    @NotNull
    private static final CheckboxSetting enabledSetting;
    @NotNull
    private static final InfoSetting importInfo;
    @NotNull
    private static final InfoSetting presetInfo;
    @NotNull
    private static final ModeSetting presetSetting;
    @NotNull
    private static final TextSetting pathSetting;
    @NotNull
    private static final SliderSetting frameDurationSetting;
    @NotNull
    private static final InfoSetting animationInfo;
    @NotNull
    private static final CheckboxSetting animateSkySetting;
    @NotNull
    private static final SliderSetting horizontalDriftSetting;
    @NotNull
    private static final SliderSetting verticalDriftRangeSetting;
    @NotNull
    private static final SliderSetting verticalDriftCycleSetting;
    @NotNull
    private static String lastLoadStatus;
    @NotNull
    private static final ActionSetting reloadSetting;
    private static volatile boolean reloadQueued;
    @NotNull
    private static volatile String loadedSignature;
    @Nullable
    private static volatile ImportedSkybox loadedSkybox;

    private SkyboxChangerModule() {
        super("Skybox Changer");
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.End event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabledSetting.getValue()).booleanValue()) {
            return;
        }
        this.migrateLegacyPresetSelection();
        String signature = this.currentConfigurationSignature();
        if (reloadQueued || !Intrinsics.areEqual((Object)signature, (Object)loadedSignature)) {
            reloadQueued = false;
            loadedSignature = signature;
            ImportedSkybox importedSkybox = loadedSkybox;
            if (importedSkybox != null) {
                importedSkybox.close();
            }
            loadedSkybox = this.loadConfiguredSkybox();
        }
    }

    @JvmStatic
    public static final boolean renderCustomSky(@NotNull GpuBufferSlice fogBuffer) {
        Intrinsics.checkNotNullParameter((Object)fogBuffer, (String)"fogBuffer");
        if (!((Boolean)enabledSetting.getValue()).booleanValue()) {
            return false;
        }
        ImportedSkybox importedSkybox = loadedSkybox;
        if (importedSkybox == null) {
            return false;
        }
        ImportedSkybox skybox = importedSkybox;
        SkyboxRenderer.INSTANCE.render(skybox, fogBuffer, (long)((Number)frameDurationSetting.getValue()).doubleValue(), INSTANCE.animationState());
        return true;
    }

    private final SkyboxAnimationState animationState() {
        return new SkyboxAnimationState((Boolean)animateSkySetting.getValue(), (float)((Number)horizontalDriftSetting.getValue()).doubleValue(), (float)((Number)verticalDriftRangeSetting.getValue()).doubleValue(), (float)((Number)verticalDriftCycleSetting.getValue()).doubleValue());
    }

    private final ImportedSkybox loadSkybox(Path path) {
        try {
            Object object;
            Map<SkyboxFace, List<class_1043>> textures;
            block17: {
                block16: {
                    boolean bl;
                    block15: {
                        Map<SkyboxFace, List<class_1043>> map;
                        this.ensureImportPathExists(path);
                        if (Files.isRegularFile(path, new LinkOption[0])) {
                            map = this.loadFileSkybox(path, "single");
                        } else if (Files.isDirectory(path, new LinkOption[0])) {
                            map = this.loadDirectorySkybox(path);
                        } else {
                            return this.failLoad("Path not found");
                        }
                        textures = map;
                        if (textures == null) break block16;
                        Map<SkyboxFace, List<class_1043>> $this$any$iv = textures;
                        boolean $i$f$any = false;
                        if ($this$any$iv.isEmpty()) {
                            bl = false;
                        } else {
                            object = $this$any$iv.entrySet().iterator();
                            while (object.hasNext()) {
                                Map.Entry<SkyboxFace, List<class_1043>> element$iv;
                                Map.Entry<SkyboxFace, List<class_1043>> it = element$iv = object.next();
                                boolean bl2 = false;
                                if (!it.getValue().isEmpty()) continue;
                                bl = true;
                                break block15;
                            }
                            bl = false;
                        }
                    }
                    if (!bl) break block17;
                }
                return this.failLoad("Missing skybox frames");
            }
            Iterator iterator = ((Iterable)textures.values()).iterator();
            if (!iterator.hasNext()) {
                throw new NoSuchElementException();
            }
            List it = (List)iterator.next();
            boolean bl = false;
            object = it.size();
            while (iterator.hasNext()) {
                List it2 = (List)iterator.next();
                $i$a$-maxOf-SkyboxChangerModule$loadSkybox$2 = false;
                Comparable comparable = Integer.valueOf(it2.size());
                if (object.compareTo(comparable) >= 0) continue;
                object = comparable;
            }
            lastLoadStatus = "Loaded " + (Comparable)object + " frame(s)";
            return new ImportedSkybox(textures);
        }
        catch (Throwable t) {
            t.printStackTrace();
            return this.failLoad("Load failed");
        }
    }

    private final ImportedSkybox loadConfiguredSkybox() {
        BuiltInPreset preset = this.selectedBuiltInPreset();
        if (preset != null) {
            return this.loadBundledPresetSkybox(preset);
        }
        return this.loadSkybox(this.resolveConfiguredPath());
    }

    private final String currentConfigurationSignature() {
        BuiltInPreset preset = this.selectedBuiltInPreset();
        if (preset != null) {
            return "preset:" + preset.getResourceName();
        }
        return ((Object)this.resolveConfiguredPath().toAbsolutePath().normalize()).toString();
    }

    private final BuiltInPreset selectedBuiltInPreset() {
        this.migrateLegacyPresetSelection();
        return (BuiltInPreset)CollectionsKt.getOrNull(builtInPresets, (int)(((Number)presetSetting.getValue()).intValue() - 1));
    }

    private final void migrateLegacyPresetSelection() {
        int migratedValue;
        block2: {
            int n;
            int n2;
            block1: {
                n2 = ((Number)presetSetting.getValue()).intValue();
                if (n2 != 0) break block1;
                n = 0;
                break block2;
            }
            n = (1 <= n2 ? n2 <= builtInPresets.size() : false) ? ((Number)presetSetting.getValue()).intValue() : (n2 == 6 ? 1 : (n2 == 7 ? 2 : (migratedValue = (1 <= n2 ? n2 < 6 : false) ? 0 : 0)));
        }
        if (((Number)presetSetting.getValue()).intValue() != migratedValue) {
            presetSetting.setValue(migratedValue);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final ImportedSkybox loadBundledPresetSkybox(BuiltInPreset preset) {
        try {
            Object object;
            block11: {
                block10: {
                    BufferedImage bufferedImage;
                    String resourcePath = "/assets/cobalt/skyboxes/presets/" + preset.getResourceName();
                    object = SkyboxChangerModule.class.getResourceAsStream(resourcePath);
                    if (object == null) break block10;
                    Closeable closeable = (Closeable)object;
                    Throwable throwable = null;
                    try {
                        InputStream input = (InputStream)closeable;
                        boolean bl = false;
                        BufferedImage bufferedImage2 = ImageIO.read(input);
                        bufferedImage = bufferedImage2;
                    }
                    catch (Throwable throwable2) {
                        throwable = throwable2;
                        throw throwable2;
                    }
                    finally {
                        CloseableKt.closeFinally((Closeable)closeable, (Throwable)throwable);
                    }
                    object = bufferedImage;
                    if (bufferedImage != null) break block11;
                }
                return this.failLoad("Missing preset " + preset.getDisplayName());
            }
            Object image = object;
            Map<SkyboxFace, List<class_1043>> textures = this.mapImageToSkybox((BufferedImage)image, StringsKt.substringBeforeLast$default((String)preset.getResourceName(), (char)'.', null, (int)2, null));
            lastLoadStatus = "Loaded " + preset.getDisplayName();
            return new ImportedSkybox(textures);
        }
        catch (Throwable t) {
            t.printStackTrace();
            return this.failLoad("Preset load failed");
        }
    }

    private final Map<SkyboxFace, List<class_1043>> loadFileSkybox(Path path, String labelPrefix) {
        String string = ((Object)path.getFileName()).toString().toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
        String lower = string;
        return StringsKt.endsWith$default((String)lower, (String)".gif", (boolean)false, (int)2, null) ? this.loadGifSkybox(path, labelPrefix) : this.loadStaticSkybox(path, labelPrefix);
    }

    private final Map<SkyboxFace, List<class_1043>> loadStaticSkybox(Path path, String labelPrefix) {
        BufferedImage bufferedImage = ImageIO.read(path.toFile());
        if (bufferedImage == null) {
            return null;
        }
        BufferedImage image = bufferedImage;
        return this.mapImageToSkybox(image, labelPrefix);
    }

    /*
     * WARNING - void declaration
     */
    private final Map<SkyboxFace, List<class_1043>> loadGifSkybox(Path path, String labelPrefix) {
        List<BufferedImage> frames = this.readGifFrames(path);
        if (frames.isEmpty()) {
            return null;
        }
        if (!this.isPanoramaFrame((BufferedImage)CollectionsKt.first(frames))) {
            void $this$associateWithTo$iv$iv;
            Object object;
            void $this$mapIndexedTo$iv$iv;
            Iterable $this$mapIndexed$iv = frames;
            boolean $i$f$mapIndexed = false;
            Iterable iterable = $this$mapIndexed$iv;
            Iterable destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$mapIndexed$iv, (int)10));
            boolean $i$f$mapIndexedTo = false;
            int index$iv$iv = 0;
            for (Object item$iv$iv : $this$mapIndexedTo$iv$iv) {
                void frameIndex;
                void frame;
                int n;
                if ((n = index$iv$iv++) < 0) {
                    CollectionsKt.throwIndexOverflow();
                }
                BufferedImage bufferedImage = (BufferedImage)item$iv$iv;
                int n2 = n;
                object = destination$iv$iv;
                boolean bl = false;
                object.add(INSTANCE.bufferedImageToTexture((BufferedImage)frame, labelPrefix + "-" + (int)frameIndex));
            }
            List textures = (List)destination$iv$iv;
            Iterable $this$associateWith$iv = (Iterable)SkyboxFace.getEntries();
            boolean $i$f$associateWith = false;
            LinkedHashMap result$iv = new LinkedHashMap(RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateWith$iv, (int)10)), (int)16));
            destination$iv$iv = $this$associateWith$iv;
            Map destination$iv$iv2 = result$iv;
            boolean $i$f$associateWithTo = false;
            for (Object element$iv$iv : $this$associateWithTo$iv$iv) {
                SkyboxFace skyboxFace = (SkyboxFace)((Object)element$iv$iv);
                Object t = element$iv$iv;
                object = destination$iv$iv2;
                boolean bl = false;
                List list = textures;
                object.put(t, list);
            }
            return destination$iv$iv2;
        }
        Map<SkyboxFace, List<class_1043>> faceFrames = this.mutableFaceFrameMap();
        Iterable $this$forEachIndexed$iv = frames;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            void frame;
            Map<SkyboxFace, class_1043> cubemap;
            int n;
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            BufferedImage bufferedImage = (BufferedImage)item$iv;
            int frameIndex = n;
            boolean bl = false;
            Map<SkyboxFace, class_1043> $this$forEach$iv = cubemap = INSTANCE.convertPanoramaToCubemap((BufferedImage)frame, labelPrefix + "-" + frameIndex);
            boolean $i$f$forEach = false;
            Iterator<Map.Entry<SkyboxFace, class_1043>> iterator = $this$forEach$iv.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<SkyboxFace, class_1043> element$iv;
                Map.Entry<SkyboxFace, class_1043> entry = element$iv = iterator.next();
                boolean bl2 = false;
                SkyboxFace face = entry.getKey();
                class_1043 texture = entry.getValue();
                ((Collection)MapsKt.getValue(faceFrames, (Object)((Object)face))).add(texture);
            }
        }
        return this.freezeFaceFrameMap(faceFrames);
    }

    /*
     * WARNING - void declaration
     */
    private final Map<SkyboxFace, List<class_1043>> loadDirectorySkybox(Path root) {
        boolean hasCubemapLayout;
        Map.Entry element$iv;
        boolean $i$f$any;
        Map $this$any$iv;
        Map cubemapSources;
        List<Path> list;
        Map map;
        Object object;
        Object $this$associateWithTo$iv$iv;
        Map destination$iv$iv;
        block11: {
            Iterable $this$associateWith$iv = (Iterable)SkyboxFace.getEntries();
            boolean $i$f$associateWith = false;
            LinkedHashMap result$iv = new LinkedHashMap(RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateWith$iv, (int)10)), (int)16));
            Iterable iterable = $this$associateWith$iv;
            destination$iv$iv = result$iv;
            boolean $i$f$associateWithTo = false;
            Iterator iterator = $this$associateWithTo$iv$iv.iterator();
            while (iterator.hasNext()) {
                void it;
                Object element$iv$iv = iterator.next();
                SkyboxFace skyboxFace = (SkyboxFace)((Object)element$iv$iv);
                object = element$iv$iv;
                map = destination$iv$iv;
                boolean bl = false;
                list = INSTANCE.findFaceSources(root, (SkyboxFace)it);
                map.put(object, list);
            }
            $this$any$iv = cubemapSources = destination$iv$iv;
            $i$f$any = false;
            if ($this$any$iv.isEmpty()) {
                v0 = false;
            } else {
                $this$associateWithTo$iv$iv = $this$any$iv.entrySet().iterator();
                while ($this$associateWithTo$iv$iv.hasNext()) {
                    Map.Entry it = element$iv = $this$associateWithTo$iv$iv.next();
                    boolean bl = false;
                    boolean bl2 = !((Collection)it.getValue()).isEmpty();
                    if (!bl2) continue;
                    v0 = true;
                    break block11;
                }
                v0 = hasCubemapLayout = false;
            }
        }
        if (hasCubemapLayout) {
            void $this$associateByTo$iv$iv$iv;
            void $this$mapValuesTo$iv$iv;
            boolean bl;
            block12: {
                $this$any$iv = cubemapSources;
                $i$f$any = false;
                if ($this$any$iv.isEmpty()) {
                    bl = false;
                } else {
                    $this$associateWithTo$iv$iv = $this$any$iv.entrySet().iterator();
                    while ($this$associateWithTo$iv$iv.hasNext()) {
                        Map.Entry it = element$iv = $this$associateWithTo$iv$iv.next();
                        boolean bl3 = false;
                        if (!((List)it.getValue()).isEmpty()) continue;
                        bl = true;
                        break block12;
                    }
                    bl = false;
                }
            }
            if (bl) {
                return null;
            }
            Map $this$mapValues$iv = cubemapSources;
            boolean $i$f$mapValues = false;
            $this$associateWithTo$iv$iv = $this$mapValues$iv;
            destination$iv$iv = new LinkedHashMap(MapsKt.mapCapacity((int)$this$mapValues$iv.size()));
            boolean $i$f$mapValuesTo = false;
            Iterable bl3 = $this$mapValuesTo$iv$iv.entrySet();
            Map destination$iv$iv$iv = destination$iv$iv;
            boolean $i$f$associateByTo = false;
            for (Object element$iv$iv$iv : $this$associateByTo$iv$iv$iv) {
                void it$iv$iv;
                Map.Entry entry = (Map.Entry)element$iv$iv$iv;
                Map map2 = destination$iv$iv$iv;
                boolean bl4 = false;
                Map.Entry entry2 = (Map.Entry)element$iv$iv$iv;
                object = it$iv$iv.getKey();
                map = map2;
                boolean bl5 = false;
                SkyboxFace face = (SkyboxFace)((Object)entry2.getKey());
                List sources = (List)entry2.getValue();
                String string = face.name().toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
                list = INSTANCE.loadTextureFrames(sources, string);
                map.put(object, list);
            }
            return destination$iv$iv$iv;
        }
        List<Path> frames = this.listImageFiles(root);
        if (frames.isEmpty()) {
            return null;
        }
        return this.loadSharedSkyboxSources(frames, "shared");
    }

    private final List<Path> findFaceSources(Path root, SkyboxFace face) {
        String alias;
        List<Path> rootImages = this.listImageFiles(root);
        Iterable $this$forEach$iv = face.getAliases();
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            alias = (String)element$iv;
            boolean bl = false;
            Path aliasDir = root.resolve(alias);
            if (!Files.isDirectory(aliasDir, new LinkOption[0])) continue;
            Intrinsics.checkNotNull((Object)aliasDir);
            List<Path> files = INSTANCE.listImageFiles(aliasDir);
            if (!(!((Collection)files).isEmpty())) continue;
            return files;
        }
        $this$forEach$iv = face.getAliases();
        $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            Path file;
            Object v0;
            block3: {
                alias = (String)element$iv;
                boolean bl = false;
                Iterable $this$firstOrNull$iv = rootImages;
                boolean $i$f$firstOrNull = false;
                for (Object element$iv2 : $this$firstOrNull$iv) {
                    Path it = (Path)element$iv2;
                    boolean bl2 = false;
                    if (!StringsKt.equals((String)INSTANCE.imageStem(it), (String)alias, (boolean)true)) continue;
                    v0 = element$iv2;
                    break block3;
                }
                v0 = null;
            }
            Path path = file = (Path)v0;
            if (path == null) continue;
            return CollectionsKt.listOf((Object)path);
        }
        return CollectionsKt.emptyList();
    }

    /*
     * WARNING - void declaration
     */
    private final Map<SkyboxFace, List<class_1043>> loadSharedSkyboxSources(List<? extends Path> sources, String labelPrefix) {
        Map<SkyboxFace, List<class_1043>> faceFrames = this.mutableFaceFrameMap();
        Iterable $this$forEachIndexed$iv = sources;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            void source;
            int n;
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            Path path = (Path)item$iv;
            int sourceIndex = n;
            boolean bl = false;
            Map<SkyboxFace, List<class_1043>> loaded = INSTANCE.loadFileSkybox((Path)source, labelPrefix + "-" + sourceIndex);
            if (loaded == null) {
                INSTANCE.closeFaceFrames(faceFrames);
                return null;
            }
            Map<SkyboxFace, List<class_1043>> $this$forEach$iv = loaded;
            boolean $i$f$forEach = false;
            Iterator<Map.Entry<SkyboxFace, List<class_1043>>> iterator = $this$forEach$iv.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<SkyboxFace, List<class_1043>> element$iv;
                Map.Entry<SkyboxFace, List<class_1043>> entry = element$iv = iterator.next();
                boolean bl2 = false;
                SkyboxFace face = entry.getKey();
                List<class_1043> textures = entry.getValue();
                CollectionsKt.addAll((Collection)((Collection)MapsKt.getValue(faceFrames, (Object)((Object)face))), (Iterable)textures);
            }
        }
        return this.freezeFaceFrameMap(faceFrames);
    }

    private final List<class_1043> loadTextureFrames(Path source, String labelPrefix) {
        return this.loadTextureFrames(CollectionsKt.listOf((Object)source), labelPrefix);
    }

    /*
     * WARNING - void declaration
     */
    private final List<class_1043> loadTextureFrames(List<? extends Path> sources, String labelPrefix) {
        List textures = new ArrayList();
        Iterable $this$forEachIndexed$iv = sources;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            String lower;
            void source;
            int n;
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            Path path = (Path)item$iv;
            int sourceIndex = n;
            boolean bl = false;
            Intrinsics.checkNotNullExpressionValue((Object)((Object)source.getFileName()).toString().toLowerCase(Locale.ROOT), (String)"toLowerCase(...)");
            if (StringsKt.endsWith$default((String)lower, (String)".gif", (boolean)false, (int)2, null)) {
                Iterable $this$forEach$iv = INSTANCE.loadGifFrames((Path)source, labelPrefix + "-" + sourceIndex);
                boolean $i$f$forEach = false;
                for (Object element$iv : $this$forEach$iv) {
                    class_1043 it = (class_1043)element$iv;
                    boolean bl2 = false;
                    ((Collection)textures).add(it);
                }
                continue;
            }
            ((Collection)textures).add(INSTANCE.loadStaticTexture((Path)source, labelPrefix + "-" + sourceIndex));
        }
        return textures;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final List<BufferedImage> readGifFrames(Path path) {
        List frames = new ArrayList();
        Closeable closeable = ImageIO.createImageInputStream(path.toFile());
        Throwable throwable = null;
        try {
            ImageInputStream input = (ImageInputStream)closeable;
            boolean bl = false;
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                List list = CollectionsKt.emptyList();
                return list;
            }
            ImageReader reader = readers.next();
            try {
                reader.setInput(input);
                int frameCount = reader.getNumImages(true);
                for (int frameIndex = 0; frameIndex < frameCount; ++frameIndex) {
                    ((Collection)frames).add(reader.read(frameIndex));
                }
            }
            finally {
                reader.dispose();
            }
            Unit unit = Unit.INSTANCE;
        }
        catch (Throwable throwable2) {
            throwable = throwable2;
            throw throwable2;
        }
        finally {
            CloseableKt.closeFinally((Closeable)closeable, (Throwable)throwable);
        }
        return frames;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final class_1043 loadStaticTexture(Path path, String label) {
        Closeable closeable = Files.newInputStream(path, new OpenOption[0]);
        Throwable throwable = null;
        try {
            InputStream input = (InputStream)closeable;
            boolean bl = false;
            class_1011 class_10112 = class_1011.method_4309((InputStream)input);
            Intrinsics.checkNotNullExpressionValue((Object)class_10112, (String)"read(...)");
            class_1011 image = class_10112;
            class_1043 class_10432 = new class_1043(() -> SkyboxChangerModule.loadStaticTexture$lambda$0$0(label), image);
            return class_10432;
        }
        catch (Throwable throwable2) {
            throwable = throwable2;
            throw throwable2;
        }
        finally {
            CloseableKt.closeFinally((Closeable)closeable, (Throwable)throwable);
        }
    }

    /*
     * WARNING - void declaration
     */
    private final List<class_1043> loadGifFrames(Path path, String labelPrefix) {
        void $this$mapIndexedTo$iv$iv;
        Iterable $this$mapIndexed$iv = this.readGifFrames(path);
        boolean $i$f$mapIndexed = false;
        Iterable iterable = $this$mapIndexed$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$mapIndexed$iv, (int)10));
        boolean $i$f$mapIndexedTo = false;
        int index$iv$iv = 0;
        for (Object item$iv$iv : $this$mapIndexedTo$iv$iv) {
            void frameIndex;
            void frame;
            int n;
            if ((n = index$iv$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            BufferedImage bufferedImage = (BufferedImage)item$iv$iv;
            int n2 = n;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(INSTANCE.bufferedImageToTexture((BufferedImage)frame, labelPrefix + "-" + (int)frameIndex));
        }
        return (List)destination$iv$iv;
    }

    private final class_1043 bufferedImageToTexture(BufferedImage image, String label) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage)image, "png", out);
        class_1011 class_10112 = class_1011.method_49277((byte[])out.toByteArray());
        Intrinsics.checkNotNullExpressionValue((Object)class_10112, (String)"read(...)");
        class_1011 class_10113 = class_10112;
        return new class_1043(() -> SkyboxChangerModule.bufferedImageToTexture$lambda$0(label), class_10113);
    }

    /*
     * WARNING - void declaration
     */
    private final Map<SkyboxFace, List<class_1043>> mapImageToSkybox(BufferedImage image, String labelPrefix) {
        void $this$associateByTo$iv$iv$iv;
        void $this$mapValuesTo$iv$iv;
        Map<SkyboxFace, class_1043> cubemap;
        if (!this.isPanoramaFrame(image)) {
            void $this$associateWithTo$iv$iv;
            class_1043 texture = this.bufferedImageToTexture(image, labelPrefix);
            Iterable $this$associateWith$iv = (Iterable)SkyboxFace.getEntries();
            boolean $i$f$associateWith = false;
            LinkedHashMap result$iv = new LinkedHashMap(RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateWith$iv, (int)10)), (int)16));
            Iterable iterable = $this$associateWith$iv;
            Map destination$iv$iv = result$iv;
            boolean $i$f$associateWithTo = false;
            for (Object element$iv$iv : $this$associateWithTo$iv$iv) {
                SkyboxFace skyboxFace = (SkyboxFace)((Object)element$iv$iv);
                Object t = element$iv$iv;
                Map map = destination$iv$iv;
                boolean bl = false;
                List list = CollectionsKt.listOf((Object)texture);
                map.put(t, list);
            }
            return destination$iv$iv;
        }
        Map<SkyboxFace, class_1043> $this$mapValues$iv = cubemap = this.convertPanoramaToCubemap(image, labelPrefix);
        boolean $i$f$mapValues = false;
        Map<SkyboxFace, class_1043> result$iv = $this$mapValues$iv;
        Map destination$iv$iv = new LinkedHashMap(MapsKt.mapCapacity((int)$this$mapValues$iv.size()));
        boolean $i$f$mapValuesTo = false;
        Iterable $i$f$associateWithTo = $this$mapValuesTo$iv$iv.entrySet();
        Map destination$iv$iv$iv = destination$iv$iv;
        boolean $i$f$associateByTo = false;
        for (Object element$iv$iv$iv : $this$associateByTo$iv$iv$iv) {
            void it;
            void it$iv$iv;
            Map.Entry entry = (Map.Entry)element$iv$iv$iv;
            Map map = destination$iv$iv$iv;
            boolean bl = false;
            Map.Entry entry2 = (Map.Entry)element$iv$iv$iv;
            Object k = it$iv$iv.getKey();
            Map map2 = map;
            boolean bl2 = false;
            List list = CollectionsKt.listOf(it.getValue());
            map2.put(k, list);
        }
        return destination$iv$iv$iv;
    }

    private final boolean isPanoramaFrame(BufferedImage image) {
        if (image.getWidth() < 4 || image.getHeight() < 2) {
            return false;
        }
        double ratio = (double)image.getWidth() / (double)image.getHeight();
        return 1.85 <= ratio ? ratio <= 2.15 : false;
    }

    /*
     * WARNING - void declaration
     */
    private final Map<SkyboxFace, class_1043> convertPanoramaToCubemap(BufferedImage panorama, String labelPrefix) {
        void $this$associateWithTo$iv$iv;
        int faceSize = RangesKt.coerceAtMost((int)RangesKt.coerceAtLeast((int)(panorama.getWidth() / 4), (int)1), (int)RangesKt.coerceAtLeast((int)(panorama.getHeight() / 2), (int)1));
        Iterable $this$associateWith$iv = (Iterable)SkyboxFace.getEntries();
        boolean $i$f$associateWith = false;
        LinkedHashMap result$iv = new LinkedHashMap(RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateWith$iv, (int)10)), (int)16));
        Iterable iterable = $this$associateWith$iv;
        Map destination$iv$iv = result$iv;
        boolean $i$f$associateWithTo = false;
        for (Object element$iv$iv : $this$associateWithTo$iv$iv) {
            void face;
            SkyboxFace skyboxFace = (SkyboxFace)((Object)element$iv$iv);
            Object t = element$iv$iv;
            Map map = destination$iv$iv;
            boolean bl = false;
            BufferedImage faceImage = new BufferedImage(faceSize, faceSize, 2);
            for (int y = 0; y < faceSize; ++y) {
                double v = 2.0 * ((double)y + 0.5) / (double)faceSize - 1.0;
                for (int x = 0; x < faceSize; ++x) {
                    double u = 2.0 * ((double)x + 0.5) / (double)faceSize - 1.0;
                    Triple<Double, Double, Double> direction = INSTANCE.cubemapDirection((SkyboxFace)face, u, v);
                    faceImage.setRGB(x, y, INSTANCE.samplePanorama(panorama, direction));
                }
            }
            String string = face.name().toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
            class_1043 class_10432 = INSTANCE.bufferedImageToTexture(faceImage, labelPrefix + "-" + string);
            map.put(t, class_10432);
        }
        return destination$iv$iv;
    }

    private final Triple<Double, Double, Double> cubemapDirection(SkyboxFace face, double u, double v) {
        Triple direction = switch (WhenMappings.$EnumSwitchMapping$0[face.ordinal()]) {
            case 1 -> new Triple((Object)u, (Object)v, (Object)1.0);
            case 2 -> new Triple((Object)u, (Object)(-v), (Object)-1.0);
            case 3 -> new Triple((Object)-1.0, (Object)(-u), (Object)v);
            case 4 -> new Triple((Object)1.0, (Object)u, (Object)v);
            case 5 -> new Triple((Object)u, (Object)1.0, (Object)(-v));
            case 6 -> new Triple((Object)u, (Object)-1.0, (Object)v);
            default -> throw new NoWhenBranchMatchedException();
        };
        double length = Math.sqrt(((Number)direction.getFirst()).doubleValue() * ((Number)direction.getFirst()).doubleValue() + ((Number)direction.getSecond()).doubleValue() * ((Number)direction.getSecond()).doubleValue() + ((Number)direction.getThird()).doubleValue() * ((Number)direction.getThird()).doubleValue());
        return new Triple((Object)(((Number)direction.getFirst()).doubleValue() / length), (Object)(((Number)direction.getSecond()).doubleValue() / length), (Object)(((Number)direction.getThird()).doubleValue() / length));
    }

    private final int samplePanorama(BufferedImage panorama, Triple<Double, Double, Double> direction) {
        double longitude = Math.atan2(((Number)direction.getFirst()).doubleValue(), ((Number)direction.getThird()).doubleValue());
        double latitude = Math.asin(RangesKt.coerceIn((double)((Number)direction.getSecond()).doubleValue(), (double)-1.0, (double)1.0));
        double wrappedX = this.wrapUnit(longitude / (Math.PI * 2) + 0.5) * (double)panorama.getWidth();
        double clampedY = RangesKt.coerceIn((double)(0.5 - latitude / Math.PI), (double)0.0, (double)1.0) * (double)(panorama.getHeight() - 1);
        int x0 = (int)Math.floor(wrappedX) % panorama.getWidth();
        int x1 = (x0 + 1) % panorama.getWidth();
        int y0 = RangesKt.coerceIn((int)((int)Math.floor(clampedY)), (int)0, (int)(panorama.getHeight() - 1));
        int y1 = RangesKt.coerceAtMost((int)(y0 + 1), (int)(panorama.getHeight() - 1));
        double tx = wrappedX - Math.floor(wrappedX);
        double ty = clampedY - Math.floor(clampedY);
        int c00 = panorama.getRGB(x0, y0);
        int c10 = panorama.getRGB(x1, y0);
        int c01 = panorama.getRGB(x0, y1);
        int c11 = panorama.getRGB(x1, y1);
        return this.interpolateArgb(c00, c10, c01, c11, tx, ty);
    }

    private final double wrapUnit(double value) {
        double wrapped = value % 1.0;
        return wrapped < 0.0 ? wrapped + 1.0 : wrapped;
    }

    private final int interpolateArgb(int topLeft, int topRight, int bottomLeft, int bottomRight, double tx, double ty) {
        int alpha = this.bilerp(topLeft >>> 24 & 0xFF, topRight >>> 24 & 0xFF, bottomLeft >>> 24 & 0xFF, bottomRight >>> 24 & 0xFF, tx, ty);
        int red = this.bilerp(topLeft >>> 16 & 0xFF, topRight >>> 16 & 0xFF, bottomLeft >>> 16 & 0xFF, bottomRight >>> 16 & 0xFF, tx, ty);
        int green = this.bilerp(topLeft >>> 8 & 0xFF, topRight >>> 8 & 0xFF, bottomLeft >>> 8 & 0xFF, bottomRight >>> 8 & 0xFF, tx, ty);
        int blue = this.bilerp(topLeft & 0xFF, topRight & 0xFF, bottomLeft & 0xFF, bottomRight & 0xFF, tx, ty);
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    private final int bilerp(int topLeft, int topRight, int bottomLeft, int bottomRight, double tx, double ty) {
        double top = (double)topLeft + (double)(topRight - topLeft) * tx;
        double bottom = (double)bottomLeft + (double)(bottomRight - bottomLeft) * tx;
        return RangesKt.coerceIn((int)MathKt.roundToInt((double)(top + (bottom - top) * ty)), (int)0, (int)255);
    }

    /*
     * WARNING - void declaration
     */
    private final Map<SkyboxFace, List<class_1043>> mutableFaceFrameMap() {
        void $this$associateWithTo$iv$iv;
        Iterable $this$associateWith$iv = (Iterable)SkyboxFace.getEntries();
        boolean $i$f$associateWith = false;
        LinkedHashMap result$iv = new LinkedHashMap(RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateWith$iv, (int)10)), (int)16));
        Iterable iterable = $this$associateWith$iv;
        Map destination$iv$iv = result$iv;
        boolean $i$f$associateWithTo = false;
        for (Object element$iv$iv : $this$associateWithTo$iv$iv) {
            SkyboxFace skyboxFace = (SkyboxFace)((Object)element$iv$iv);
            Object t = element$iv$iv;
            Map map = destination$iv$iv;
            boolean bl = false;
            List list = new ArrayList();
            map.put(t, list);
        }
        return MapsKt.toMutableMap((Map)destination$iv$iv);
    }

    /*
     * WARNING - void declaration
     */
    private final Map<SkyboxFace, List<class_1043>> freezeFaceFrameMap(Map<SkyboxFace, ? extends List<class_1043>> faceFrames) {
        void $this$associateByTo$iv$iv$iv;
        void $this$mapValuesTo$iv$iv;
        Map<SkyboxFace, ? extends List<class_1043>> $this$mapValues$iv = faceFrames;
        boolean $i$f$mapValues = false;
        Map<SkyboxFace, ? extends List<class_1043>> map = $this$mapValues$iv;
        Map destination$iv$iv = new LinkedHashMap(MapsKt.mapCapacity((int)$this$mapValues$iv.size()));
        boolean $i$f$mapValuesTo = false;
        Iterable iterable = $this$mapValuesTo$iv$iv.entrySet();
        Map destination$iv$iv$iv = destination$iv$iv;
        boolean $i$f$associateByTo = false;
        for (Object element$iv$iv$iv : $this$associateByTo$iv$iv$iv) {
            void it;
            void it$iv$iv;
            Map.Entry entry = (Map.Entry)element$iv$iv$iv;
            Map map2 = destination$iv$iv$iv;
            boolean bl = false;
            Map.Entry entry2 = (Map.Entry)element$iv$iv$iv;
            Object k = it$iv$iv.getKey();
            Map map3 = map2;
            boolean bl2 = false;
            List list = CollectionsKt.toList((Iterable)((Iterable)it.getValue()));
            map3.put(k, list);
        }
        return destination$iv$iv$iv;
    }

    private final void closeFaceFrames(Map<SkyboxFace, ? extends List<? extends class_1043>> faceFrames) {
        Iterable $this$forEach$iv = CollectionsKt.toSet((Iterable)CollectionsKt.flatten((Iterable)faceFrames.values()));
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            class_1043 it = (class_1043)element$iv;
            boolean bl = false;
            it.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    private final List<Path> listImageFiles(Path dir) {
        if (!Files.isDirectory(dir, new LinkOption[0])) {
            return CollectionsKt.emptyList();
        }
        var2_2 = Files.newDirectoryStream(dir);
        var3_3 = null;
        try {
            stream = (DirectoryStream)var2_2;
            $i$a$-use-SkyboxChangerModule$listImageFiles$1 = false;
            Intrinsics.checkNotNull((Object)stream);
            var6_8 = stream;
            $i$f$filter = false;
            var8_10 = $this$filter$iv;
            destination$iv$iv = new ArrayList<E>();
            $i$f$filterTo = false;
            for (T element$iv$iv : $this$filterTo$iv$iv) {
                it = (Path)element$iv$iv;
                $i$a$-filter-SkyboxChangerModule$listImageFiles$1$1 = false;
                if (!Files.isRegularFile(it, new LinkOption[0])) ** GOTO lbl-1000
                Intrinsics.checkNotNull((Object)it);
                if (SkyboxChangerModule.INSTANCE.isSupportedImage(it)) {
                    v0 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v0 = false;
                }
                if (!v0) continue;
                destination$iv$iv.add(element$iv$iv);
            }
            $this$filter$iv = (List)destination$iv$iv;
            $i$f$sortedBy = false;
            var4_4 = CollectionsKt.toList((Iterable)CollectionsKt.sortedWith((Iterable)$this$sortedBy$iv, (Comparator)new Comparator(){

                public final int compare(T a, T b) {
                    Path it = (Path)a;
                    boolean bl = false;
                    String string = ((Object)it.getFileName()).toString().toLowerCase(Locale.ROOT);
                    Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
                    it = (Path)b;
                    Comparable comparable = (Comparable)((Object)string);
                    bl = false;
                    String string2 = ((Object)it.getFileName()).toString().toLowerCase(Locale.ROOT);
                    Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
                    return ComparisonsKt.compareValues((Comparable)comparable, (Comparable)((Comparable)((Object)string2)));
                }
            }));
        }
        catch (Throwable var5_6) {
            var3_3 = var5_6;
            throw var5_6;
        }
        finally {
            CloseableKt.closeFinally((Closeable)var2_2, (Throwable)var3_3);
        }
        return var4_4;
    }

    private final boolean isSupportedImage(Path path) {
        String string = ((Object)path.getFileName()).toString().toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
        String lower = string;
        return StringsKt.endsWith$default((String)lower, (String)".png", (boolean)false, (int)2, null) || StringsKt.endsWith$default((String)lower, (String)".jpg", (boolean)false, (int)2, null) || StringsKt.endsWith$default((String)lower, (String)".jpeg", (boolean)false, (int)2, null) || StringsKt.endsWith$default((String)lower, (String)".gif", (boolean)false, (int)2, null);
    }

    private final String imageStem(Path path) {
        String string;
        String name = ((Object)path.getFileName()).toString();
        int dot = StringsKt.lastIndexOf$default((CharSequence)name, (char)'.', (int)0, (boolean)false, (int)6, null);
        if (dot <= 0) {
            string = name;
        } else {
            String string2 = name.substring(0, dot);
            string = string2;
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"substring(...)");
        }
        return string;
    }

    private final Path resolveConfiguredPath() {
        CharSequence charSequence;
        Path gameDir = class_310.method_1551().field_1697.toPath();
        CharSequence charSequence2 = ((Object)StringsKt.trim((CharSequence)((String)pathSetting.getValue()))).toString();
        if (charSequence2.length() == 0) {
            boolean bl = false;
            charSequence = "cobalt/skyboxes/custom";
        } else {
            charSequence = charSequence2;
        }
        String raw = (String)charSequence;
        Path configured = Path.of(raw, new String[0]);
        if (configured.isAbsolute()) {
            Path path = configured.normalize();
            Intrinsics.checkNotNullExpressionValue((Object)path, (String)"normalize(...)");
            return path;
        }
        Path path = gameDir.resolve(configured).normalize();
        Intrinsics.checkNotNullExpressionValue((Object)path, (String)"normalize(...)");
        return path;
    }

    private final void ensureImportPathExists(Path path) {
        Object lower;
        Object object;
        block9: {
            block8: {
                if (Files.exists(path, new LinkOption[0])) {
                    return;
                }
                object = path.getFileName();
                if (object == null || (object = object.toString()) == null) break block8;
                String string = ((String)object).toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
                object = string;
                if (string != null) break block9;
            }
            object = "";
        }
        if (StringsKt.endsWith$default((String)(lower = object), (String)".png", (boolean)false, (int)2, null) || StringsKt.endsWith$default((String)lower, (String)".jpg", (boolean)false, (int)2, null) || StringsKt.endsWith$default((String)lower, (String)".jpeg", (boolean)false, (int)2, null) || StringsKt.endsWith$default((String)lower, (String)".gif", (boolean)false, (int)2, null)) {
            Path path2 = path.getParent();
            if (path2 != null) {
                Path it = path2;
                boolean bl = false;
                v3 = Files.createDirectories(it, new FileAttribute[0]);
            } else {
                v3 = null;
            }
        } else {
            v3 = Files.createDirectories(path, new FileAttribute[0]);
        }
    }

    private final ImportedSkybox failLoad(String reason) {
        lastLoadStatus = reason;
        return null;
    }

    private static final String reloadSetting$lambda$0() {
        return StringsKt.startsWith$default((String)lastLoadStatus, (String)"Loaded", (boolean)false, (int)2, null) ? "Reload" : "Retry";
    }

    private static final Unit reloadSetting$lambda$1() {
        reloadQueued = true;
        return Unit.INSTANCE;
    }

    private static final String loadStaticTexture$lambda$0$0(String $label) {
        return "Skybox:" + $label;
    }

    private static final String bufferedImageToTexture$lambda$0(String $label) {
        return "Skybox:" + $label;
    }

    /*
     * WARNING - void declaration
     */
    static {
        Collection<String> collection;
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        INSTANCE = new SkyboxChangerModule();
        Object[] objectArray = new BuiltInPreset[]{new BuiltInPreset("Nimbus Ember", "preset-6.png"), new BuiltInPreset("Violet Expanse", "preset-7.png"), new BuiltInPreset("Starfield Nebulae", "preset-8.png")};
        builtInPresets = CollectionsKt.listOf((Object[])objectArray);
        enabledSetting = new CheckboxSetting("Enabled", "Replace vanilla sky rendering with an imported skybox.", false);
        importInfo = (InfoSetting)SettingKt.inGroup((Setting)new InfoSetting("Import Format", "A single 2:1 PNG/JPG becomes a seamless cubemap. You can also use a GIF, a folder of ordered frames, or a folder with front/back/left/right/up/down subfolders.", InfoType.INFO), "Import");
        presetInfo = (InfoSetting)SettingKt.inGroup((Setting)new InfoSetting("Preset Slots", "Nimbus Ember, Violet Expanse, and Starfield Nebulae are bundled with the mod. Use Custom if you want to import your own panorama, GIF, or cubemap from disk.", InfoType.INFO), "Presets");
        objectArray = new SpreadBuilder(2);
        objectArray.add((Object)"Custom");
        Iterable iterable = builtInPresets;
        Setting[] settingArray = objectArray;
        int n = 0;
        String string = "Choose one of the bundled sky presets or keep using a custom path.";
        String string2 = "Preset";
        boolean $i$f$map = false;
        void var3_7 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            BuiltInPreset builtInPreset = (BuiltInPreset)item$iv$iv;
            collection = destination$iv$iv;
            boolean bl = false;
            collection.add(it.getDisplayName());
        }
        collection = (List)destination$iv$iv;
        Collection $this$toTypedArray$iv = collection;
        boolean $i$f$toTypedArray = false;
        Collection thisCollection$iv = $this$toTypedArray$iv;
        settingArray.addSpread((Object)thisCollection$iv.toArray(new String[0]));
        String[] stringArray = (String[])objectArray.toArray((Object[])new String[objectArray.size()]);
        int n2 = n;
        String string3 = string;
        String string4 = string2;
        presetSetting = (ModeSetting)SettingKt.inGroup((Setting)new ModeSetting(string4, string3, n2, stringArray), "Presets");
        pathSetting = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("Skybox Path", "Relative paths resolve from the Minecraft game folder.", "cobalt/skyboxes/custom"), "Import");
        frameDurationSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Frame Duration", "Milliseconds per animation frame.", 100.0, 16.0, 1000.0, 1.0), "Import");
        animationInfo = (InfoSetting)SettingKt.inGroup((Setting)new InfoSetting("Sky Motion", "Animate the sky by slowly rotating the imported panorama or cubemap around you.", InfoType.INFO), "Animation");
        animateSkySetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Animate Sky", "Continuously drift the skybox instead of leaving it static.", true), "Animation");
        horizontalDriftSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Horizontal Drift", "Degrees per second of horizontal sky movement.", 0.35, 0.0, 8.0, 0.01), "Animation");
        verticalDriftRangeSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Vertical Drift Range", "Maximum up/down drift in degrees.", 2.5, 0.0, 20.0, 0.1), "Animation");
        verticalDriftCycleSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Vertical Drift Cycle", "Seconds for one full up/down motion.", 24.0, 2.0, 120.0, 0.5), "Animation");
        lastLoadStatus = "Idle";
        reloadSetting = (ActionSetting)SettingKt.inGroup((Setting)new ActionSetting("Reload Skybox", "Reload skybox textures from disk.", "Reload", (Function0<String>)((Function0)SkyboxChangerModule::reloadSetting$lambda$0), (Function0<Unit>)((Function0)SkyboxChangerModule::reloadSetting$lambda$1)), "Import");
        reloadQueued = true;
        loadedSignature = "";
        objectArray = new Setting[]{enabledSetting, presetInfo, presetSetting, importInfo, pathSetting, frameDurationSetting, animationInfo, animateSkySetting, horizontalDriftSetting, verticalDriftRangeSetting, verticalDriftCycleSetting, reloadSetting};
        INSTANCE.addSetting((Setting<?>[])objectArray);
        EventBus.register(INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0007\b\u0082\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\bJ$\u0010\n\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\n\u0010\u000bJ\u001b\u0010\u000e\u001a\u00020\r2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0011\u0010\u0011\u001a\u00020\u0010H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0011\u0010\u0013\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0013\u0010\bR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0014\u001a\u0004\b\u0015\u0010\bR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0014\u001a\u0004\b\u0016\u0010\b\u00a8\u0006\u0017"}, d2={"Lorg/cobalt/internal/visual/SkyboxChangerModule$BuiltInPreset;", "", "", "displayName", "resourceName", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", "component1", "()Ljava/lang/String;", "component2", "copy", "(Ljava/lang/String;Ljava/lang/String;)Lorg/cobalt/internal/visual/SkyboxChangerModule$BuiltInPreset;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getDisplayName", "getResourceName", "cobalt"})
    private static final class BuiltInPreset {
        @NotNull
        private final String displayName;
        @NotNull
        private final String resourceName;

        public BuiltInPreset(@NotNull String displayName, @NotNull String resourceName) {
            Intrinsics.checkNotNullParameter((Object)displayName, (String)"displayName");
            Intrinsics.checkNotNullParameter((Object)resourceName, (String)"resourceName");
            this.displayName = displayName;
            this.resourceName = resourceName;
        }

        @NotNull
        public final String getDisplayName() {
            return this.displayName;
        }

        @NotNull
        public final String getResourceName() {
            return this.resourceName;
        }

        @NotNull
        public final String component1() {
            return this.displayName;
        }

        @NotNull
        public final String component2() {
            return this.resourceName;
        }

        @NotNull
        public final BuiltInPreset copy(@NotNull String displayName, @NotNull String resourceName) {
            Intrinsics.checkNotNullParameter((Object)displayName, (String)"displayName");
            Intrinsics.checkNotNullParameter((Object)resourceName, (String)"resourceName");
            return new BuiltInPreset(displayName, resourceName);
        }

        public static /* synthetic */ BuiltInPreset copy$default(BuiltInPreset builtInPreset, String string, String string2, int n, Object object) {
            if ((n & 1) != 0) {
                string = builtInPreset.displayName;
            }
            if ((n & 2) != 0) {
                string2 = builtInPreset.resourceName;
            }
            return builtInPreset.copy(string, string2);
        }

        @NotNull
        public String toString() {
            return "BuiltInPreset(displayName=" + this.displayName + ", resourceName=" + this.resourceName + ")";
        }

        public int hashCode() {
            int result = this.displayName.hashCode();
            result = result * 31 + this.resourceName.hashCode();
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof BuiltInPreset)) {
                return false;
            }
            BuiltInPreset builtInPreset = (BuiltInPreset)other;
            if (!Intrinsics.areEqual((Object)this.displayName, (Object)builtInPreset.displayName)) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.resourceName, (Object)builtInPreset.resourceName);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0082\b\u0018\u00002\u00020\u0001B!\u0012\u0018\u0010\u0006\u001a\u0014\u0012\u0004\u0012\u00020\u0003\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0002\u00a2\u0006\u0004\b\u0007\u0010\bJ'\u0010\r\u001a\u0004\u0018\u00010\u00052\u0006\u0010\t\u001a\u00020\u00032\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\n\u00a2\u0006\u0004\b\r\u0010\u000eJ\r\u0010\u0010\u001a\u00020\u000f\u00a2\u0006\u0004\b\u0010\u0010\u0011J\"\u0010\u0012\u001a\u0014\u0012\u0004\u0012\u00020\u0003\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0002H\u00c2\u0003\u00a2\u0006\u0004\b\u0012\u0010\u0013J,\u0010\u0014\u001a\u00020\u00002\u001a\b\u0002\u0010\u0006\u001a\u0014\u0012\u0004\u0012\u00020\u0003\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u001b\u0010\u0018\u001a\u00020\u00172\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0011\u0010\u001b\u001a\u00020\u001aH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001b\u0010\u001cJ\u0011\u0010\u001e\u001a\u00020\u001dH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001e\u0010\u001fR&\u0010\u0006\u001a\u0014\u0012\u0004\u0012\u00020\u0003\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0006\u0010 \u00a8\u0006!"}, d2={"Lorg/cobalt/internal/visual/SkyboxChangerModule$ImportedSkybox;", "", "", "Lorg/cobalt/internal/visual/SkyboxChangerModule$SkyboxFace;", "", "Lnet/minecraft/class_1043;", "faceFrames", "<init>", "(Ljava/util/Map;)V", "face", "", "frameDurationMs", "nowMs", "frame", "(Lorg/cobalt/internal/visual/SkyboxChangerModule$SkyboxFace;JJ)Lnet/minecraft/class_1043;", "", "close", "()V", "component1", "()Ljava/util/Map;", "copy", "(Ljava/util/Map;)Lorg/cobalt/internal/visual/SkyboxChangerModule$ImportedSkybox;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "Ljava/util/Map;", "cobalt"})
    @SourceDebugExtension(value={"SMAP\nSkyboxChangerModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SkyboxChangerModule.kt\norg/cobalt/internal/visual/SkyboxChangerModule$ImportedSkybox\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,800:1\n1915#2,2:801\n*S KotlinDebug\n*F\n+ 1 SkyboxChangerModule.kt\norg/cobalt/internal/visual/SkyboxChangerModule$ImportedSkybox\n*L\n660#1:801,2\n*E\n"})
    private static final class ImportedSkybox {
        @NotNull
        private final Map<SkyboxFace, List<class_1043>> faceFrames;

        public ImportedSkybox(@NotNull Map<SkyboxFace, ? extends List<? extends class_1043>> faceFrames) {
            Intrinsics.checkNotNullParameter(faceFrames, (String)"faceFrames");
            this.faceFrames = faceFrames;
        }

        @Nullable
        public final class_1043 frame(@NotNull SkyboxFace face, long frameDurationMs, long nowMs) {
            List frames;
            Intrinsics.checkNotNullParameter((Object)((Object)face), (String)"face");
            List list = this.faceFrames.get((Object)face);
            if (list == null) {
                list = CollectionsKt.emptyList();
            }
            if ((frames = list).isEmpty()) {
                return null;
            }
            long duration = RangesKt.coerceAtLeast((long)frameDurationMs, (long)16L);
            int index = (int)(nowMs / duration % (long)frames.size());
            return (class_1043)frames.get(index);
        }

        public final void close() {
            Iterable $this$forEach$iv = CollectionsKt.toSet((Iterable)CollectionsKt.flatten((Iterable)this.faceFrames.values()));
            boolean $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                class_1043 it = (class_1043)element$iv;
                boolean bl = false;
                it.close();
            }
        }

        private final Map<SkyboxFace, List<class_1043>> component1() {
            return this.faceFrames;
        }

        @NotNull
        public final ImportedSkybox copy(@NotNull Map<SkyboxFace, ? extends List<? extends class_1043>> faceFrames) {
            Intrinsics.checkNotNullParameter(faceFrames, (String)"faceFrames");
            return new ImportedSkybox(faceFrames);
        }

        public static /* synthetic */ ImportedSkybox copy$default(ImportedSkybox importedSkybox, Map map, int n, Object object) {
            if ((n & 1) != 0) {
                map = importedSkybox.faceFrames;
            }
            return importedSkybox.copy(map);
        }

        @NotNull
        public String toString() {
            return "ImportedSkybox(faceFrames=" + this.faceFrames + ")";
        }

        public int hashCode() {
            return ((Object)this.faceFrames).hashCode();
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ImportedSkybox)) {
                return false;
            }
            ImportedSkybox importedSkybox = (ImportedSkybox)other;
            return Intrinsics.areEqual(this.faceFrames, importedSkybox.faceFrames);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0010\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\t\b\u0082\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0004\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\f\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\rJ\u0010\u0010\u000f\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\rJ8\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u001b\u0010\u0013\u001a\u00020\u00022\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0011\u0010\u0016\u001a\u00020\u0015H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0019\u001a\u00020\u0018H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0019\u0010\u001aR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001b\u001a\u0004\b\u001c\u0010\u000bR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001d\u001a\u0004\b\u001e\u0010\rR\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001d\u001a\u0004\b\u001f\u0010\rR\u0017\u0010\u0007\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u001d\u001a\u0004\b \u0010\r\u00a8\u0006!"}, d2={"Lorg/cobalt/internal/visual/SkyboxChangerModule$SkyboxAnimationState;", "", "", "enabled", "", "horizontalDegreesPerSecond", "verticalDegrees", "verticalCycleSeconds", "<init>", "(ZFFF)V", "component1", "()Z", "component2", "()F", "component3", "component4", "copy", "(ZFFF)Lorg/cobalt/internal/visual/SkyboxChangerModule$SkyboxAnimationState;", "other", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "Z", "getEnabled", "F", "getHorizontalDegreesPerSecond", "getVerticalDegrees", "getVerticalCycleSeconds", "cobalt"})
    private static final class SkyboxAnimationState {
        private final boolean enabled;
        private final float horizontalDegreesPerSecond;
        private final float verticalDegrees;
        private final float verticalCycleSeconds;

        public SkyboxAnimationState(boolean enabled, float horizontalDegreesPerSecond, float verticalDegrees, float verticalCycleSeconds) {
            this.enabled = enabled;
            this.horizontalDegreesPerSecond = horizontalDegreesPerSecond;
            this.verticalDegrees = verticalDegrees;
            this.verticalCycleSeconds = verticalCycleSeconds;
        }

        public final boolean getEnabled() {
            return this.enabled;
        }

        public final float getHorizontalDegreesPerSecond() {
            return this.horizontalDegreesPerSecond;
        }

        public final float getVerticalDegrees() {
            return this.verticalDegrees;
        }

        public final float getVerticalCycleSeconds() {
            return this.verticalCycleSeconds;
        }

        public final boolean component1() {
            return this.enabled;
        }

        public final float component2() {
            return this.horizontalDegreesPerSecond;
        }

        public final float component3() {
            return this.verticalDegrees;
        }

        public final float component4() {
            return this.verticalCycleSeconds;
        }

        @NotNull
        public final SkyboxAnimationState copy(boolean enabled, float horizontalDegreesPerSecond, float verticalDegrees, float verticalCycleSeconds) {
            return new SkyboxAnimationState(enabled, horizontalDegreesPerSecond, verticalDegrees, verticalCycleSeconds);
        }

        public static /* synthetic */ SkyboxAnimationState copy$default(SkyboxAnimationState skyboxAnimationState, boolean bl, float f, float f2, float f3, int n, Object object) {
            if ((n & 1) != 0) {
                bl = skyboxAnimationState.enabled;
            }
            if ((n & 2) != 0) {
                f = skyboxAnimationState.horizontalDegreesPerSecond;
            }
            if ((n & 4) != 0) {
                f2 = skyboxAnimationState.verticalDegrees;
            }
            if ((n & 8) != 0) {
                f3 = skyboxAnimationState.verticalCycleSeconds;
            }
            return skyboxAnimationState.copy(bl, f, f2, f3);
        }

        @NotNull
        public String toString() {
            return "SkyboxAnimationState(enabled=" + this.enabled + ", horizontalDegreesPerSecond=" + this.horizontalDegreesPerSecond + ", verticalDegrees=" + this.verticalDegrees + ", verticalCycleSeconds=" + this.verticalCycleSeconds + ")";
        }

        public int hashCode() {
            int result = Boolean.hashCode(this.enabled);
            result = result * 31 + Float.hashCode(this.horizontalDegreesPerSecond);
            result = result * 31 + Float.hashCode(this.verticalDegrees);
            result = result * 31 + Float.hashCode(this.verticalCycleSeconds);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof SkyboxAnimationState)) {
                return false;
            }
            SkyboxAnimationState skyboxAnimationState = (SkyboxAnimationState)other;
            if (this.enabled != skyboxAnimationState.enabled) {
                return false;
            }
            if (Float.compare(this.horizontalDegreesPerSecond, skyboxAnimationState.horizontalDegreesPerSecond) != 0) {
                return false;
            }
            if (Float.compare(this.verticalDegrees, skyboxAnimationState.verticalDegrees) != 0) {
                return false;
            }
            return Float.compare(this.verticalCycleSeconds, skyboxAnimationState.verticalCycleSeconds) == 0;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\r\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0017\b\u0002\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006R\u001d\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0007\u001a\u0004\b\b\u0010\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000f\u00a8\u0006\u0010"}, d2={"Lorg/cobalt/internal/visual/SkyboxChangerModule$SkyboxFace;", "", "", "", "aliases", "<init>", "(Ljava/lang/String;ILjava/util/Set;)V", "Ljava/util/Set;", "getAliases", "()Ljava/util/Set;", "FRONT", "BACK", "LEFT", "RIGHT", "UP", "DOWN", "cobalt"})
    private static final class SkyboxFace
    extends Enum<SkyboxFace> {
        @NotNull
        private final Set<String> aliases;
        public static final /* enum */ SkyboxFace FRONT;
        public static final /* enum */ SkyboxFace BACK;
        public static final /* enum */ SkyboxFace LEFT;
        public static final /* enum */ SkyboxFace RIGHT;
        public static final /* enum */ SkyboxFace UP;
        public static final /* enum */ SkyboxFace DOWN;
        private static final /* synthetic */ SkyboxFace[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        private SkyboxFace(Set<String> aliases) {
            this.aliases = aliases;
        }

        @NotNull
        public final Set<String> getAliases() {
            return this.aliases;
        }

        public static SkyboxFace[] values() {
            return (SkyboxFace[])$VALUES.clone();
        }

        public static SkyboxFace valueOf(String value) {
            return Enum.valueOf(SkyboxFace.class, value);
        }

        @NotNull
        public static EnumEntries<SkyboxFace> getEntries() {
            return $ENTRIES;
        }

        static {
            Object[] objectArray = new String[]{"front", "north"};
            FRONT = new SkyboxFace(SetsKt.setOf((Object[])objectArray));
            objectArray = new String[]{"back", "south"};
            BACK = new SkyboxFace(SetsKt.setOf((Object[])objectArray));
            objectArray = new String[]{"left", "west"};
            LEFT = new SkyboxFace(SetsKt.setOf((Object[])objectArray));
            objectArray = new String[]{"right", "east"};
            RIGHT = new SkyboxFace(SetsKt.setOf((Object[])objectArray));
            objectArray = new String[]{"up", "top"};
            UP = new SkyboxFace(SetsKt.setOf((Object[])objectArray));
            objectArray = new String[]{"down", "bottom"};
            DOWN = new SkyboxFace(SetsKt.setOf((Object[])objectArray));
            $VALUES = skyboxFaceArray = new SkyboxFace[]{SkyboxFace.FRONT, SkyboxFace.BACK, SkyboxFace.LEFT, SkyboxFace.RIGHT, SkyboxFace.UP, SkyboxFace.DOWN};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c2\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J-\u0010\r\u001a\u00020\f2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\n\u00a2\u0006\u0004\b\r\u0010\u000eJ\u001f\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u000f\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u000f\u0010\u0013\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b\u0013\u0010\u0003J\u0017\u0010\u0017\u001a\u00020\u00162\u0006\u0010\u0015\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u0017\u0010\u0019\u001a\u00020\u00102\u0006\u0010\u0015\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b\u0019\u0010\u001aR\u0014\u0010\u001c\u001a\u00020\u001b8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u001dR\u0014\u0010\u001f\u001a\u00020\u001e8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001f\u0010 R\u0014\u0010!\u001a\u00020\u001b8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b!\u0010\u001dR\u0014\u0010#\u001a\u00020\"8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b#\u0010$R\u0014\u0010%\u001a\u00020\b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b%\u0010&R0\u0010)\u001a\u001e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00160'j\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u0016`(8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b)\u0010*\u00a8\u0006+"}, d2={"Lorg/cobalt/internal/visual/SkyboxChangerModule$SkyboxRenderer;", "", "<init>", "()V", "Lorg/cobalt/internal/visual/SkyboxChangerModule$ImportedSkybox;", "skybox", "Lcom/mojang/blaze3d/buffers/GpuBufferSlice;", "fogBuffer", "", "frameDurationMs", "Lorg/cobalt/internal/visual/SkyboxChangerModule$SkyboxAnimationState;", "animation", "", "render", "(Lorg/cobalt/internal/visual/SkyboxChangerModule$ImportedSkybox;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;JLorg/cobalt/internal/visual/SkyboxChangerModule$SkyboxAnimationState;)V", "nowMs", "Lorg/joml/Matrix4f;", "animatedModelView", "(JLorg/cobalt/internal/visual/SkyboxChangerModule$SkyboxAnimationState;)Lorg/joml/Matrix4f;", "ensureFaceBuffers", "Lorg/cobalt/internal/visual/SkyboxChangerModule$SkyboxFace;", "face", "Lcom/mojang/blaze3d/buffers/GpuBuffer;", "buildFaceBuffer", "(Lorg/cobalt/internal/visual/SkyboxChangerModule$SkyboxFace;)Lcom/mojang/blaze3d/buffers/GpuBuffer;", "faceRotation", "(Lorg/cobalt/internal/visual/SkyboxChangerModule$SkyboxFace;)Lorg/joml/Matrix4f;", "", "SKY_RADIUS", "F", "", "STATIC_VERTEX_BUFFER_USAGE", "I", "DEG_TO_RAD", "", "TWO_PI", "D", "animationStartNs", "J", "Ljava/util/LinkedHashMap;", "Lkotlin/collections/LinkedHashMap;", "faceBuffers", "Ljava/util/LinkedHashMap;", "cobalt"})
    @SourceDebugExtension(value={"SMAP\nSkyboxChangerModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SkyboxChangerModule.kt\norg/cobalt/internal/visual/SkyboxChangerModule$SkyboxRenderer\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,800:1\n1915#2,2:801\n1915#2,2:803\n*S KotlinDebug\n*F\n+ 1 SkyboxChangerModule.kt\norg/cobalt/internal/visual/SkyboxChangerModule$SkyboxRenderer\n*L\n726#1:801,2\n758#1:803,2\n*E\n"})
    private static final class SkyboxRenderer {
        @NotNull
        public static final SkyboxRenderer INSTANCE = new SkyboxRenderer();
        private static final float SKY_RADIUS = 100.0f;
        private static final int STATIC_VERTEX_BUFFER_USAGE = 40;
        private static final float DEG_TO_RAD = (float)Math.PI / 180;
        private static final double TWO_PI = Math.PI * 2;
        private static final long animationStartNs = System.nanoTime();
        @NotNull
        private static final LinkedHashMap<SkyboxFace, GpuBuffer> faceBuffers = new LinkedHashMap();

        private SkyboxRenderer() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public final void render(@NotNull ImportedSkybox skybox, @NotNull GpuBufferSlice fogBuffer, long frameDurationMs, @NotNull SkyboxAnimationState animation) {
            Intrinsics.checkNotNullParameter((Object)skybox, (String)"skybox");
            Intrinsics.checkNotNullParameter((Object)fogBuffer, (String)"fogBuffer");
            Intrinsics.checkNotNullParameter((Object)animation, (String)"animation");
            this.ensureFaceBuffers();
            class_310 class_3102 = class_310.method_1551();
            Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
            class_310 mc = class_3102;
            class_276 class_2762 = mc.method_1522();
            Intrinsics.checkNotNullExpressionValue((Object)class_2762, (String)"getMainRenderTarget(...)");
            class_276 target = class_2762;
            GpuTextureView gpuTextureView = target.method_71639();
            if (gpuTextureView == null) {
                return;
            }
            GpuTextureView colorView = gpuTextureView;
            GpuTextureView gpuTextureView2 = target.method_71640();
            if (gpuTextureView2 == null) {
                return;
            }
            GpuTextureView depthView = gpuTextureView2;
            RenderSystem.class_5590 class_55902 = RenderSystem.getSequentialBuffer((VertexFormat.class_5596)VertexFormat.class_5596.field_27382);
            Intrinsics.checkNotNullExpressionValue((Object)class_55902, (String)"getSequentialBuffer(...)");
            RenderSystem.class_5590 indexBufferSource = class_55902;
            GpuBuffer gpuBuffer = indexBufferSource.method_68274(6);
            Intrinsics.checkNotNullExpressionValue((Object)gpuBuffer, (String)"getBuffer(...)");
            GpuBuffer quadIndexBuffer = gpuBuffer;
            long now = System.currentTimeMillis();
            Matrix4f modelView = this.animatedModelView(now, animation);
            GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().method_71106((Matrix4fc)modelView, (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f());
            Intrinsics.checkNotNullExpressionValue((Object)gpuBufferSlice, (String)"writeTransform(...)");
            GpuBufferSlice transforms = gpuBufferSlice;
            RenderSystem.setShaderFog((GpuBufferSlice)fogBuffer);
            AutoCloseable autoCloseable = (AutoCloseable)RenderSystem.getDevice().createCommandEncoder().createRenderPass(SkyboxRenderer::render$lambda$0, colorView, OptionalInt.empty(), depthView, OptionalDouble.empty());
            Throwable throwable = null;
            try {
                RenderPass pass = (RenderPass)autoCloseable;
                boolean bl = false;
                pass.setPipeline(class_10799.field_56875);
                RenderSystem.bindDefaultUniforms((RenderPass)pass);
                pass.setUniform("DynamicTransforms", transforms);
                pass.setIndexBuffer(quadIndexBuffer, indexBufferSource.method_31924());
                Iterable $this$forEach$iv = (Iterable)SkyboxFace.getEntries();
                boolean $i$f$forEach = false;
                for (Object element$iv : $this$forEach$iv) {
                    GpuBuffer vertexBuffer;
                    class_1043 texture;
                    SkyboxFace face = (SkyboxFace)((Object)element$iv);
                    boolean bl2 = false;
                    if (skybox.frame(face, frameDurationMs, now) == null || faceBuffers.get((Object)face) == null) continue;
                    pass.bindTexture("Sampler0", texture.method_71659(), texture.method_75484());
                    pass.setVertexBuffer(0, vertexBuffer);
                    pass.drawIndexed(0, 0, 6, 1);
                }
                Unit unit = Unit.INSTANCE;
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
            finally {
                AutoCloseableKt.closeFinally((AutoCloseable)autoCloseable, (Throwable)throwable);
            }
        }

        private final Matrix4f animatedModelView(long nowMs, SkyboxAnimationState animation) {
            Matrix4f matrix = new Matrix4f((Matrix4fc)RenderSystem.getModelViewMatrix());
            if (!animation.getEnabled()) {
                return matrix;
            }
            double elapsedSeconds = (double)(System.nanoTime() - animationStartNs) / 1.0E9;
            double horizontalDegrees = elapsedSeconds * (double)animation.getHorizontalDegreesPerSecond() % 360.0;
            float horizontalRadians = (float)(horizontalDegrees * (Math.PI / 180));
            matrix.rotateY(horizontalRadians);
            if (animation.getVerticalDegrees() > 0.0f) {
                double cycleSeconds = RangesKt.coerceAtLeast((float)animation.getVerticalCycleSeconds(), (float)0.1f);
                float verticalRadians = (float)Math.sin(elapsedSeconds / cycleSeconds * (Math.PI * 2)) * animation.getVerticalDegrees() * ((float)Math.PI / 180);
                matrix.rotateX(verticalRadians);
            }
            return matrix;
        }

        private final void ensureFaceBuffers() {
            if (!((Map)faceBuffers).isEmpty()) {
                return;
            }
            Iterable $this$forEach$iv = (Iterable)SkyboxFace.getEntries();
            boolean $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                SkyboxFace face = (SkyboxFace)((Object)element$iv);
                boolean bl = false;
                ((Map)faceBuffers).put(face, INSTANCE.buildFaceBuffer(face));
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private final GpuBuffer buildFaceBuffer(SkyboxFace face) {
            GpuBuffer gpuBuffer;
            class_9799 class_97992 = class_9799.method_72201((int)(4 * class_290.field_1575.getVertexSize()));
            Intrinsics.checkNotNullExpressionValue((Object)class_97992, (String)"exactlySized(...)");
            class_9799 bytes = class_97992;
            class_287 builder = new class_287(bytes, VertexFormat.class_5596.field_27382, class_290.field_1575);
            Matrix4f rotation = this.faceRotation(face);
            builder.method_22918((Matrix4fc)rotation, -100.0f, -100.0f, -100.0f).method_22913(0.0f, 0.0f).method_39415(-1);
            builder.method_22918((Matrix4fc)rotation, -100.0f, -100.0f, 100.0f).method_22913(0.0f, 1.0f).method_39415(-1);
            builder.method_22918((Matrix4fc)rotation, 100.0f, -100.0f, 100.0f).method_22913(1.0f, 1.0f).method_39415(-1);
            builder.method_22918((Matrix4fc)rotation, 100.0f, -100.0f, -100.0f).method_22913(1.0f, 0.0f).method_39415(-1);
            class_9801 class_98012 = builder.method_60800();
            Intrinsics.checkNotNullExpressionValue((Object)class_98012, (String)"buildOrThrow(...)");
            class_9801 mesh = class_98012;
            try {
                gpuBuffer = RenderSystem.getDevice().createBuffer(() -> SkyboxRenderer.buildFaceBuffer$lambda$0(face), 40, mesh.method_60818());
                Intrinsics.checkNotNull((Object)gpuBuffer);
            }
            finally {
                mesh.close();
                bytes.close();
            }
            return gpuBuffer;
        }

        private final Matrix4f faceRotation(SkyboxFace face) {
            Matrix4f matrix = new Matrix4f();
            switch (WhenMappings.$EnumSwitchMapping$0[face.ordinal()]) {
                case 1: {
                    Unit unit = Unit.INSTANCE;
                    break;
                }
                case 2: {
                    Unit unit = matrix.rotationX(1.5707964f);
                    break;
                }
                case 3: {
                    Unit unit = matrix.rotationX(-1.5707964f);
                    break;
                }
                case 4: {
                    Unit unit = matrix.rotationX((float)Math.PI);
                    break;
                }
                case 5: {
                    Unit unit = matrix.rotationZ(1.5707964f);
                    break;
                }
                case 6: {
                    Unit unit = matrix.rotationZ(-1.5707964f);
                    break;
                }
                default: {
                    throw new NoWhenBranchMatchedException();
                }
            }
            return matrix;
        }

        private static final String render$lambda$0() {
            return "cobalt_skybox";
        }

        private static final String buildFaceBuffer$lambda$0(SkyboxFace $face) {
            String string = $face.name().toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
            return "cobalt_skybox_" + string;
        }

        @Metadata(mv={2, 3, 0}, k=3, xi=48)
        public static final class WhenMappings {
            public static final /* synthetic */ int[] $EnumSwitchMapping$0;

            static {
                int[] nArray = new int[SkyboxFace.values().length];
                try {
                    nArray[SkyboxFace.DOWN.ordinal()] = 1;
                }
                catch (NoSuchFieldError noSuchFieldError) {
                    // empty catch block
                }
                try {
                    nArray[SkyboxFace.BACK.ordinal()] = 2;
                }
                catch (NoSuchFieldError noSuchFieldError) {
                    // empty catch block
                }
                try {
                    nArray[SkyboxFace.FRONT.ordinal()] = 3;
                }
                catch (NoSuchFieldError noSuchFieldError) {
                    // empty catch block
                }
                try {
                    nArray[SkyboxFace.UP.ordinal()] = 4;
                }
                catch (NoSuchFieldError noSuchFieldError) {
                    // empty catch block
                }
                try {
                    nArray[SkyboxFace.RIGHT.ordinal()] = 5;
                }
                catch (NoSuchFieldError noSuchFieldError) {
                    // empty catch block
                }
                try {
                    nArray[SkyboxFace.LEFT.ordinal()] = 6;
                }
                catch (NoSuchFieldError noSuchFieldError) {
                    // empty catch block
                }
                $EnumSwitchMapping$0 = nArray;
            }
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[SkyboxFace.values().length];
            try {
                nArray[SkyboxFace.FRONT.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[SkyboxFace.BACK.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[SkyboxFace.LEFT.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[SkyboxFace.RIGHT.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[SkyboxFace.UP.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[SkyboxFace.DOWN.ordinal()] = 6;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

