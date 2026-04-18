/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.opengl.GlStateManager
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.GpuTexture
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.io.CloseableKt
 *  kotlin.io.TextStreamsKt
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.Charsets
 *  net.minecraft.class_10865
 *  net.minecraft.class_10868
 *  net.minecraft.class_276
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.nanovg.NSVGImage
 *  org.lwjgl.nanovg.NVGColor
 *  org.lwjgl.nanovg.NVGPaint
 *  org.lwjgl.nanovg.NanoSVG
 *  org.lwjgl.nanovg.NanoVG
 *  org.lwjgl.nanovg.NanoVGGL3
 *  org.lwjgl.opengl.GL33C
 *  org.lwjgl.stb.STBImage
 *  org.lwjgl.system.MemoryUtil
 */
package org.cobalt.api.util.ui;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.io.CloseableKt;
import kotlin.io.TextStreamsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.Charsets;
import net.minecraft.class_10865;
import net.minecraft.class_10868;
import net.minecraft.class_276;
import net.minecraft.class_310;
import org.cobalt.api.util.ui.helper.Font;
import org.cobalt.api.util.ui.helper.Gradient;
import org.cobalt.api.util.ui.helper.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.nanovg.NSVGImage;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoSVG;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u009a\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u001b\n\u0002\u0010\u000b\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u0014\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u0015\n\u0002\b\u0012\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0006\u009c\u0001\u009d\u0001\u009e\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001d\u0010\b\u001a\u00020\u00072\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0004\b\b\u0010\tJ\r\u0010\n\u001a\u00020\u0007\u00a2\u0006\u0004\b\n\u0010\u0003J'\u0010\r\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\r\u0010\u000eJ\u000f\u0010\u000f\u001a\u00020\u0007H\u0007\u00a2\u0006\u0004\b\u000f\u0010\u0003J\u000f\u0010\u0010\u001a\u00020\u0007H\u0007\u00a2\u0006\u0004\b\u0010\u0010\u0003J\u001f\u0010\u0013\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0013\u0010\tJ\u001f\u0010\u0014\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0014\u0010\tJ\u0017\u0010\u0016\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0017\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0018\u0010\u0017J/\u0010\u001b\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u001a\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u001b\u0010\u001cJ\u000f\u0010\u001d\u001a\u00020\u0007H\u0007\u00a2\u0006\u0004\b\u001d\u0010\u0003J?\u0010$\u001a\u00020\u00072\u0006\u0010\u001e\u001a\u00020\u00042\u0006\u0010\u001f\u001a\u00020\u00042\u0006\u0010 \u001a\u00020\u00042\u0006\u0010!\u001a\u00020\u00042\u0006\u0010\"\u001a\u00020\u00042\u0006\u0010#\u001a\u00020\u000bH\u0007\u00a2\u0006\u0004\b$\u0010%JG\u0010)\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u001a\u001a\u00020\u00042\u0006\u0010#\u001a\u00020\u000b2\u0006\u0010&\u001a\u00020\u00042\u0006\u0010(\u001a\u00020'H\u0007\u00a2\u0006\u0004\b)\u0010*J?\u0010+\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u001a\u001a\u00020\u00042\u0006\u0010#\u001a\u00020\u000b2\u0006\u0010&\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b+\u0010,J7\u0010+\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u001a\u001a\u00020\u00042\u0006\u0010#\u001a\u00020\u000bH\u0007\u00a2\u0006\u0004\b+\u0010-JG\u0010.\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u001a\u001a\u00020\u00042\u0006\u0010\"\u001a\u00020\u00042\u0006\u0010#\u001a\u00020\u000b2\u0006\u0010&\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b.\u0010/JW\u00104\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u001a\u001a\u00020\u00042\u0006\u0010\"\u001a\u00020\u00042\u0006\u00100\u001a\u00020\u000b2\u0006\u00101\u001a\u00020\u000b2\u0006\u00103\u001a\u0002022\u0006\u0010&\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b4\u00105Jg\u00108\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u001a\u001a\u00020\u00042\u0006\u0010\"\u001a\u00020\u00042\u0006\u00100\u001a\u00020\u000b2\u0006\u00101\u001a\u00020\u000b2\u0006\u00103\u001a\u0002022\u0006\u0010&\u001a\u00020\u00042\u0006\u00106\u001a\u00020\u00042\u0006\u00107\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b8\u00109JO\u0010:\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u001a\u001a\u00020\u00042\u0006\u00100\u001a\u00020\u000b2\u0006\u00101\u001a\u00020\u000b2\u0006\u00103\u001a\u0002022\u0006\u0010&\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b:\u0010;J/\u0010<\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010&\u001a\u00020\u00042\u0006\u0010#\u001a\u00020\u000bH\u0007\u00a2\u0006\u0004\b<\u0010=JA\u0010?\u001a\u00020\u00072\u0006\u0010?\u001a\u00020>2\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010@\u001a\u00020\u00042\u0006\u0010#\u001a\u00020\u000b2\b\b\u0002\u0010B\u001a\u00020AH\u0007\u00a2\u0006\u0004\b?\u0010CJS\u0010D\u001a\u00020\u00072\u0006\u0010?\u001a\u00020>2\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010@\u001a\u00020\u00042\u0006\u00100\u001a\u00020\u000b2\u0006\u00101\u001a\u00020\u000b2\b\b\u0002\u00103\u001a\u0002022\b\b\u0002\u0010B\u001a\u00020AH\u0007\u00a2\u0006\u0004\bD\u0010EJA\u0010F\u001a\u00020\u00072\u0006\u0010?\u001a\u00020>2\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010@\u001a\u00020\u00042\u0006\u0010#\u001a\u00020\u000b2\b\b\u0002\u0010B\u001a\u00020AH\u0007\u00a2\u0006\u0004\bF\u0010CJ)\u0010G\u001a\u00020\u00042\u0006\u0010?\u001a\u00020>2\u0006\u0010@\u001a\u00020\u00042\b\b\u0002\u0010B\u001a\u00020AH\u0007\u00a2\u0006\u0004\bG\u0010HJS\u0010J\u001a\u00020\u00072\u0006\u0010?\u001a\u00020>2\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u00042\u0006\u0010@\u001a\u00020\u00042\u0006\u0010#\u001a\u00020\u000b2\b\b\u0002\u0010B\u001a\u00020A2\b\b\u0002\u0010I\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\bJ\u0010KJ1\u0010N\u001a\u00020\u00042\u0006\u0010?\u001a\u00020>2\u0006\u0010L\u001a\u00020\u00042\u0006\u0010M\u001a\u00020\u00042\b\b\u0002\u0010B\u001a\u00020AH\u0007\u00a2\u0006\u0004\bN\u0010OJ9\u0010Q\u001a\u00020P2\u0006\u0010?\u001a\u00020>2\u0006\u0010\u0019\u001a\u00020\u00042\u0006\u0010@\u001a\u00020\u00042\u0006\u0010B\u001a\u00020A2\b\b\u0002\u0010I\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\bQ\u0010RJ'\u0010V\u001a\u00020\u000b2\u0006\u0010S\u001a\u00020\u000b2\u0006\u0010T\u001a\u00020\u000b2\u0006\u0010U\u001a\u00020\u000bH\u0007\u00a2\u0006\u0004\bV\u0010WJK\u0010Y\u001a\u00020\u00072\u0006\u0010Y\u001a\u00020X2\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u001a\u001a\u00020\u00042\b\b\u0002\u0010&\u001a\u00020\u00042\b\b\u0002\u0010Z\u001a\u00020\u000bH\u0007\u00a2\u0006\u0004\bY\u0010[J\u0017\u0010]\u001a\u00020X2\u0006\u0010\\\u001a\u00020>H\u0007\u00a2\u0006\u0004\b]\u0010^J\u0017\u0010_\u001a\u00020\u00072\u0006\u0010Y\u001a\u00020XH\u0007\u00a2\u0006\u0004\b_\u0010`J\u0017\u0010a\u001a\u00020\u000b2\u0006\u0010Y\u001a\u00020XH\u0002\u00a2\u0006\u0004\ba\u0010bJ\u0017\u0010c\u001a\u00020\u000b2\u0006\u0010Y\u001a\u00020XH\u0002\u00a2\u0006\u0004\bc\u0010bJ\u0017\u0010d\u001a\u00020\u000b2\u0006\u0010Y\u001a\u00020XH\u0002\u00a2\u0006\u0004\bd\u0010bJ\u0017\u0010#\u001a\u00020\u00072\u0006\u0010#\u001a\u00020\u000bH\u0002\u00a2\u0006\u0004\b#\u0010eJ\u001f\u0010#\u001a\u00020\u00072\u0006\u00100\u001a\u00020\u000b2\u0006\u00101\u001a\u00020\u000bH\u0002\u00a2\u0006\u0004\b#\u0010fJG\u00103\u001a\u00020\u00072\u0006\u00100\u001a\u00020\u000b2\u0006\u00101\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u001a\u001a\u00020\u00042\u0006\u0010g\u001a\u000202H\u0002\u00a2\u0006\u0004\b3\u0010hJ\u0017\u0010i\u001a\u00020\u000b2\u0006\u0010B\u001a\u00020AH\u0002\u00a2\u0006\u0004\bi\u0010jR\u0014\u0010l\u001a\u00020k8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bl\u0010mR\u001c\u0010p\u001a\n o*\u0004\u0018\u00010n0n8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bp\u0010qR\u001c\u0010s\u001a\n o*\u0004\u0018\u00010r0r8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bs\u0010tR\u001c\u0010u\u001a\n o*\u0004\u0018\u00010r0r8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bu\u0010tR\u0017\u0010v\u001a\u00020A8\u0006\u00a2\u0006\f\n\u0004\bv\u0010w\u001a\u0004\bx\u0010yR0\u0010}\u001a\u001e\u0012\u0004\u0012\u00020A\u0012\u0004\u0012\u00020{0zj\u000e\u0012\u0004\u0012\u00020A\u0012\u0004\u0012\u00020{`|8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b}\u0010~R\u0015\u0010\u007f\u001a\u00020P8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u007f\u0010\u0080\u0001R4\u0010\u0082\u0001\u001a \u0012\u0004\u0012\u00020X\u0012\u0005\u0012\u00030\u0081\u00010zj\u000f\u0012\u0004\u0012\u00020X\u0012\u0005\u0012\u00030\u0081\u0001`|8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0082\u0001\u0010~R\u001c\u0010\u0084\u0001\u001a\u0005\u0018\u00010\u0083\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0084\u0001\u0010\u0085\u0001R\u0019\u0010\u0086\u0001\u001a\u00020'8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0086\u0001\u0010\u0087\u0001R\u001a\u0010\u0089\u0001\u001a\u00030\u0088\u00018\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0089\u0001\u0010\u008a\u0001R\u0019\u0010\u008b\u0001\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u008b\u0001\u0010\u008c\u0001R\u0018\u0010\u008e\u0001\u001a\u00030\u008d\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u008e\u0001\u0010\u008f\u0001R\u0019\u0010\u0090\u0001\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0090\u0001\u0010\u008c\u0001R\u0019\u0010\u0091\u0001\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0091\u0001\u0010\u008c\u0001R\u0019\u0010\u0092\u0001\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u0092\u0001\u0010\u008c\u0001R\u0019\u0010\u0095\u0001\u001a\u00020\u000b*\u00020\u000b8\u00c6\u0002\u00a2\u0006\b\u001a\u0006\b\u0093\u0001\u0010\u0094\u0001R\u0019\u0010\u0097\u0001\u001a\u00020\u000b*\u00020\u000b8\u00c6\u0002\u00a2\u0006\b\u001a\u0006\b\u0096\u0001\u0010\u0094\u0001R\u0019\u0010\u0099\u0001\u001a\u00020\u000b*\u00020\u000b8\u00c6\u0002\u00a2\u0006\b\u001a\u0006\b\u0098\u0001\u0010\u0094\u0001R\u0019\u0010\u009b\u0001\u001a\u00020\u000b*\u00020\u000b8\u00c6\u0002\u00a2\u0006\b\u001a\u0006\b\u009a\u0001\u0010\u0094\u0001\u00a8\u0006\u009f\u0001"}, d2={"Lorg/cobalt/api/util/ui/NVGRenderer;", "", "<init>", "()V", "", "width", "height", "", "beginFrame", "(FF)V", "endFrame", "", "texId", "drawRawTexture", "(IFF)V", "push", "pop", "x", "y", "scale", "translate", "amount", "rotate", "(F)V", "globalAlpha", "w", "h", "pushScissor", "(FFFF)V", "popScissor", "x1", "y1", "x2", "y2", "thickness", "color", "line", "(FFFFFI)V", "radius", "", "roundTop", "drawHalfRoundedRect", "(FFFFIFZ)V", "rect", "(FFFFIF)V", "(FFFFI)V", "hollowRect", "(FFFFFIF)V", "color1", "color2", "Lorg/cobalt/api/util/ui/helper/Gradient;", "gradient", "hollowGradientRect", "(FFFFFIILorg/cobalt/api/util/ui/helper/Gradient;F)V", "shiftX", "shiftY", "hollowGradientRectShifted", "(FFFFFIILorg/cobalt/api/util/ui/helper/Gradient;FFF)V", "gradientRect", "(FFFFIILorg/cobalt/api/util/ui/helper/Gradient;F)V", "circle", "(FFFI)V", "", "text", "size", "Lorg/cobalt/api/util/ui/helper/Font;", "font", "(Ljava/lang/String;FFFILorg/cobalt/api/util/ui/helper/Font;)V", "textGradient", "(Ljava/lang/String;FFFIILorg/cobalt/api/util/ui/helper/Gradient;Lorg/cobalt/api/util/ui/helper/Font;)V", "textShadow", "textWidth", "(Ljava/lang/String;FLorg/cobalt/api/util/ui/helper/Font;)F", "lineHeight", "drawWrappedString", "(Ljava/lang/String;FFFFILorg/cobalt/api/util/ui/helper/Font;F)V", "maxWidth", "fontSize", "getWrappedStringHeight", "(Ljava/lang/String;FFLorg/cobalt/api/util/ui/helper/Font;)F", "", "wrappedTextBounds", "(Ljava/lang/String;FFLorg/cobalt/api/util/ui/helper/Font;F)[F", "textureId", "textureWidth", "textureHeight", "createNVGImage", "(III)I", "Lorg/cobalt/api/util/ui/helper/Image;", "image", "colorMask", "(Lorg/cobalt/api/util/ui/helper/Image;FFFFFI)V", "resourcePath", "createImage", "(Ljava/lang/String;)Lorg/cobalt/api/util/ui/helper/Image;", "deleteImage", "(Lorg/cobalt/api/util/ui/helper/Image;)V", "getImage", "(Lorg/cobalt/api/util/ui/helper/Image;)I", "loadImage", "loadSVG", "(I)V", "(II)V", "direction", "(IIFFFFLorg/cobalt/api/util/ui/helper/Gradient;)V", "getFontID", "(Lorg/cobalt/api/util/ui/helper/Font;)I", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lorg/lwjgl/nanovg/NVGPaint;", "kotlin.jvm.PlatformType", "nvgPaint", "Lorg/lwjgl/nanovg/NVGPaint;", "Lorg/lwjgl/nanovg/NVGColor;", "nvgColor", "Lorg/lwjgl/nanovg/NVGColor;", "nvgColor2", "interFont", "Lorg/cobalt/api/util/ui/helper/Font;", "getInterFont", "()Lorg/cobalt/api/util/ui/helper/Font;", "Ljava/util/HashMap;", "Lorg/cobalt/api/util/ui/NVGRenderer$NVGFont;", "Lkotlin/collections/HashMap;", "fontMap", "Ljava/util/HashMap;", "fontBounds", "[F", "Lorg/cobalt/api/util/ui/NVGRenderer$NVGImage;", "images", "Lorg/cobalt/api/util/ui/NVGRenderer$Scissor;", "scissor", "Lorg/cobalt/api/util/ui/NVGRenderer$Scissor;", "drawing", "Z", "", "vg", "J", "prevFramebuffer", "I", "", "prevViewport", "[I", "prevActiveTexture", "prevBoundTexture2D", "prevSampler0", "getRed", "(I)I", "red", "getGreen", "green", "getBlue", "blue", "getAlpha", "alpha", "Scissor", "NVGImage", "NVGFont", "cobalt"})
@SourceDebugExtension(value={"SMAP\nNVGRenderer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NVGRenderer.kt\norg/cobalt/api/util/ui/NVGRenderer\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,597:1\n591#1:599\n592#1:600\n593#1:601\n594#1:602\n591#1,4:617\n591#1:621\n592#1:622\n593#1:623\n594#1:624\n591#1:625\n592#1:626\n593#1:627\n594#1:628\n1#2:598\n383#3,7:603\n383#3,7:610\n383#3,7:629\n*S KotlinDebug\n*F\n+ 1 NVGRenderer.kt\norg/cobalt/api/util/ui/NVGRenderer\n*L\n456#1:599\n457#1:600\n458#1:601\n459#1:602\n532#1:617,4\n537#1:621\n538#1:622\n539#1:623\n540#1:624\n544#1:625\n545#1:626\n546#1:627\n547#1:628\n478#1:603,7\n479#1:610,7\n562#1:629,7\n*E\n"})
public final class NVGRenderer {
    @NotNull
    public static final NVGRenderer INSTANCE = new NVGRenderer();
    @NotNull
    private static final class_310 mc;
    private static final NVGPaint nvgPaint;
    private static final NVGColor nvgColor;
    private static final NVGColor nvgColor2;
    @NotNull
    private static final Font interFont;
    @NotNull
    private static final HashMap<Font, NVGFont> fontMap;
    @NotNull
    private static final float[] fontBounds;
    @NotNull
    private static final HashMap<Image, NVGImage> images;
    @Nullable
    private static Scissor scissor;
    private static boolean drawing;
    private static long vg;
    private static int prevFramebuffer;
    @NotNull
    private static final int[] prevViewport;
    private static int prevActiveTexture;
    private static int prevBoundTexture2D;
    private static int prevSampler0;

    private NVGRenderer() {
    }

    @NotNull
    public final Font getInterFont() {
        return interFont;
    }

    public final void beginFrame(float width, float height) {
        if (!(!drawing)) {
            boolean $i$a$-check-NVGRenderer$beginFrame$22 = false;
            String $i$a$-check-NVGRenderer$beginFrame$22 = "[NVGRenderer] Already drawing, but called beginFrame";
            throw new IllegalStateException($i$a$-check-NVGRenderer$beginFrame$22.toString());
        }
        prevFramebuffer = GL33C.glGetInteger((int)36006);
        GL33C.glGetIntegerv((int)2978, (int[])prevViewport);
        prevActiveTexture = GL33C.glGetInteger((int)34016);
        prevBoundTexture2D = GL33C.glGetInteger((int)32873);
        class_276 class_2762 = mc.method_1522();
        Intrinsics.checkNotNullExpressionValue((Object)class_2762, (String)"getMainRenderTarget(...)");
        class_276 framebuffer = class_2762;
        GpuTexture gpuTexture = framebuffer.method_30277();
        Intrinsics.checkNotNull((Object)gpuTexture, (String)"null cannot be cast to non-null type com.mojang.blaze3d.opengl.GlTexture");
        class_10868 class_108682 = (class_10868)gpuTexture;
        GpuDevice gpuDevice = RenderSystem.getDevice();
        Intrinsics.checkNotNull((Object)gpuDevice, (String)"null cannot be cast to non-null type com.mojang.blaze3d.opengl.GlDevice");
        int glFramebuffer = class_108682.method_68426(((class_10865)gpuDevice).method_68401(), null);
        scissor = null;
        GlStateManager._glBindFramebuffer((int)36160, (int)glFramebuffer);
        GlStateManager._viewport((int)0, (int)0, (int)framebuffer.field_1482, (int)framebuffer.field_1481);
        GlStateManager._activeTexture((int)33984);
        prevSampler0 = GL33C.glGetInteger((int)35097);
        GL33C.glBindSampler((int)0, (int)0);
        NanoVG.nvgBeginFrame((long)vg, (float)width, (float)height, (float)1.0f);
        NanoVG.nvgTextAlign((long)vg, (int)9);
        drawing = true;
    }

    public final void endFrame() {
        if (!drawing) {
            boolean bl = false;
            String string = "[NVGRenderer] Not drawing, but called endFrame";
            throw new IllegalStateException(string.toString());
        }
        NanoVG.nvgEndFrame((long)vg);
        GL33C.glDisable((int)2960);
        GL33C.glStencilMask((int)255);
        GL33C.glClear((int)1024);
        GL33C.glBindVertexArray((int)0);
        GL33C.glBindSampler((int)0, (int)prevSampler0);
        GlStateManager._disableCull();
        GlStateManager._disableDepthTest();
        GlStateManager._enableBlend();
        GlStateManager._blendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager._glUseProgram((int)0);
        GlStateManager._activeTexture((int)prevActiveTexture);
        GlStateManager._bindTexture((int)prevBoundTexture2D);
        GlStateManager._glBindFramebuffer((int)36160, (int)prevFramebuffer);
        GlStateManager._viewport((int)prevViewport[0], (int)prevViewport[1], (int)prevViewport[2], (int)prevViewport[3]);
        scissor = null;
        drawing = false;
    }

    @JvmStatic
    public static final void drawRawTexture(int texId, float width, float height) {
        int imgId = NanoVGGL3.nvglCreateImageFromHandle((long)vg, (int)texId, (int)((int)width), (int)((int)height), (int)65568);
        NanoVG.nvgImagePattern((long)vg, (float)0.0f, (float)height, (float)width, (float)(-height), (float)0.0f, (int)imgId, (float)1.0f, (NVGPaint)nvgPaint);
        NanoVG.nvgBeginPath((long)vg);
        NanoVG.nvgRect((long)vg, (float)0.0f, (float)0.0f, (float)width, (float)height);
        NanoVG.nvgFillPaint((long)vg, (NVGPaint)nvgPaint);
        NanoVG.nvgFill((long)vg);
        NanoVG.nvgDeleteImage((long)vg, (int)imgId);
    }

    @JvmStatic
    public static final void push() {
        NanoVG.nvgSave((long)vg);
    }

    @JvmStatic
    public static final void pop() {
        NanoVG.nvgRestore((long)vg);
    }

    @JvmStatic
    public static final void scale(float x, float y) {
        NanoVG.nvgScale((long)vg, (float)x, (float)y);
    }

    @JvmStatic
    public static final void translate(float x, float y) {
        NanoVG.nvgTranslate((long)vg, (float)x, (float)y);
    }

    @JvmStatic
    public static final void rotate(float amount) {
        NanoVG.nvgRotate((long)vg, (float)amount);
    }

    @JvmStatic
    public static final void globalAlpha(float amount) {
        NanoVG.nvgGlobalAlpha((long)vg, (float)RangesKt.coerceIn((float)amount, (float)0.0f, (float)1.0f));
    }

    @JvmStatic
    public static final void pushScissor(float x, float y, float w, float h) {
        block0: {
            Scissor scissor = NVGRenderer.scissor = new Scissor(NVGRenderer.scissor, x, y, w + x, h + y);
            if (scissor == null) break block0;
            scissor.applyScissor();
        }
    }

    @JvmStatic
    public static final void popScissor() {
        block0: {
            NanoVG.nvgResetScissor((long)vg);
            Scissor scissor = NVGRenderer.scissor;
            Scissor scissor2 = NVGRenderer.scissor = scissor != null ? scissor.getPrevious() : null;
            if (scissor2 == null) break block0;
            scissor2.applyScissor();
        }
    }

    @JvmStatic
    public static final void line(float x1, float y1, float x2, float y2, float thickness, int color) {
        NanoVG.nvgBeginPath((long)vg);
        NanoVG.nvgMoveTo((long)vg, (float)x1, (float)y1);
        NanoVG.nvgLineTo((long)vg, (float)x2, (float)y2);
        NanoVG.nvgStrokeWidth((long)vg, (float)thickness);
        INSTANCE.color(color);
        NanoVG.nvgStrokeColor((long)vg, (NVGColor)nvgColor);
        NanoVG.nvgStroke((long)vg);
    }

    @JvmStatic
    public static final void drawHalfRoundedRect(float x, float y, float w, float h, int color, float radius, boolean roundTop) {
        NanoVG.nvgBeginPath((long)vg);
        if (roundTop) {
            NanoVG.nvgMoveTo((long)vg, (float)x, (float)(y + h));
            NanoVG.nvgLineTo((long)vg, (float)(x + w), (float)(y + h));
            NanoVG.nvgLineTo((long)vg, (float)(x + w), (float)(y + radius));
            NanoVG.nvgArcTo((long)vg, (float)(x + w), (float)y, (float)(x + w - radius), (float)y, (float)radius);
            NanoVG.nvgLineTo((long)vg, (float)(x + radius), (float)y);
            NanoVG.nvgArcTo((long)vg, (float)x, (float)y, (float)x, (float)(y + radius), (float)radius);
            NanoVG.nvgLineTo((long)vg, (float)x, (float)(y + h));
        } else {
            NanoVG.nvgMoveTo((long)vg, (float)x, (float)y);
            NanoVG.nvgLineTo((long)vg, (float)(x + w), (float)y);
            NanoVG.nvgLineTo((long)vg, (float)(x + w), (float)(y + h - radius));
            NanoVG.nvgArcTo((long)vg, (float)(x + w), (float)(y + h), (float)(x + w - radius), (float)(y + h), (float)radius);
            NanoVG.nvgLineTo((long)vg, (float)(x + radius), (float)(y + h));
            NanoVG.nvgArcTo((long)vg, (float)x, (float)(y + h), (float)x, (float)(y + h - radius), (float)radius);
            NanoVG.nvgLineTo((long)vg, (float)x, (float)y);
        }
        NanoVG.nvgClosePath((long)vg);
        INSTANCE.color(color);
        NanoVG.nvgFillColor((long)vg, (NVGColor)nvgColor);
        NanoVG.nvgFill((long)vg);
    }

    @JvmStatic
    public static final void rect(float x, float y, float w, float h, int color, float radius) {
        NanoVG.nvgBeginPath((long)vg);
        NanoVG.nvgRoundedRect((long)vg, (float)x, (float)y, (float)w, (float)(h + 0.5f), (float)radius);
        INSTANCE.color(color);
        NanoVG.nvgFillColor((long)vg, (NVGColor)nvgColor);
        NanoVG.nvgFill((long)vg);
    }

    @JvmStatic
    public static final void rect(float x, float y, float w, float h, int color) {
        NanoVG.nvgBeginPath((long)vg);
        NanoVG.nvgRect((long)vg, (float)x, (float)y, (float)w, (float)(h + 0.5f));
        INSTANCE.color(color);
        NanoVG.nvgFillColor((long)vg, (NVGColor)nvgColor);
        NanoVG.nvgFill((long)vg);
    }

    @JvmStatic
    public static final void hollowRect(float x, float y, float w, float h, float thickness, int color, float radius) {
        NanoVG.nvgBeginPath((long)vg);
        NanoVG.nvgRoundedRect((long)vg, (float)x, (float)y, (float)w, (float)h, (float)radius);
        NanoVG.nvgStrokeWidth((long)vg, (float)thickness);
        NanoVG.nvgPathWinding((long)vg, (int)2);
        INSTANCE.color(color);
        NanoVG.nvgStrokeColor((long)vg, (NVGColor)nvgColor);
        NanoVG.nvgStroke((long)vg);
    }

    @JvmStatic
    public static final void hollowGradientRect(float x, float y, float w, float h, float thickness, int color1, int color2, @NotNull Gradient gradient, float radius) {
        Intrinsics.checkNotNullParameter((Object)((Object)gradient), (String)"gradient");
        NanoVG.nvgBeginPath((long)vg);
        NanoVG.nvgRoundedRect((long)vg, (float)x, (float)y, (float)w, (float)h, (float)radius);
        NanoVG.nvgStrokeWidth((long)vg, (float)thickness);
        INSTANCE.gradient(color1, color2, x, y, w, h, gradient);
        NanoVG.nvgStrokePaint((long)vg, (NVGPaint)nvgPaint);
        NanoVG.nvgStroke((long)vg);
    }

    @JvmStatic
    public static final void hollowGradientRectShifted(float x, float y, float w, float h, float thickness, int color1, int color2, @NotNull Gradient gradient, float radius, float shiftX, float shiftY) {
        Intrinsics.checkNotNullParameter((Object)((Object)gradient), (String)"gradient");
        NanoVG.nvgBeginPath((long)vg);
        NanoVG.nvgRoundedRect((long)vg, (float)x, (float)y, (float)w, (float)h, (float)radius);
        NanoVG.nvgStrokeWidth((long)vg, (float)thickness);
        INSTANCE.gradient(color1, color2, x + shiftX, y + shiftY, w, h, gradient);
        NanoVG.nvgStrokePaint((long)vg, (NVGPaint)nvgPaint);
        NanoVG.nvgStroke((long)vg);
    }

    @JvmStatic
    public static final void gradientRect(float x, float y, float w, float h, int color1, int color2, @NotNull Gradient gradient, float radius) {
        Intrinsics.checkNotNullParameter((Object)((Object)gradient), (String)"gradient");
        NanoVG.nvgBeginPath((long)vg);
        NanoVG.nvgRoundedRect((long)vg, (float)x, (float)y, (float)w, (float)h, (float)radius);
        INSTANCE.gradient(color1, color2, x, y, w, h, gradient);
        NanoVG.nvgFillPaint((long)vg, (NVGPaint)nvgPaint);
        NanoVG.nvgFill((long)vg);
    }

    @JvmStatic
    public static final void circle(float x, float y, float radius, int color) {
        NanoVG.nvgBeginPath((long)vg);
        NanoVG.nvgCircle((long)vg, (float)x, (float)y, (float)radius);
        INSTANCE.color(color);
        NanoVG.nvgFillColor((long)vg, (NVGColor)nvgColor);
        NanoVG.nvgFill((long)vg);
    }

    @JvmStatic
    public static final void text(@NotNull String text, float x, float y, float size, int color, @NotNull Font font) {
        Intrinsics.checkNotNullParameter((Object)text, (String)"text");
        Intrinsics.checkNotNullParameter((Object)font, (String)"font");
        NanoVG.nvgFontSize((long)vg, (float)size);
        NanoVG.nvgFontFaceId((long)vg, (int)INSTANCE.getFontID(font));
        INSTANCE.color(color);
        NanoVG.nvgFillColor((long)vg, (NVGColor)nvgColor);
        NanoVG.nvgText((long)vg, (float)x, (float)(y + 0.5f), (CharSequence)text);
    }

    public static /* synthetic */ void text$default(String string, float f, float f2, float f3, int n, Font font, int n2, Object object) {
        if ((n2 & 0x20) != 0) {
            font = interFont;
        }
        NVGRenderer.text(string, f, f2, f3, n, font);
    }

    @JvmStatic
    public static final void textGradient(@NotNull String text, float x, float y, float size, int color1, int color2, @NotNull Gradient gradient, @NotNull Font font) {
        Intrinsics.checkNotNullParameter((Object)text, (String)"text");
        Intrinsics.checkNotNullParameter((Object)((Object)gradient), (String)"gradient");
        Intrinsics.checkNotNullParameter((Object)font, (String)"font");
        NanoVG.nvgFontSize((long)vg, (float)size);
        NanoVG.nvgFontFaceId((long)vg, (int)INSTANCE.getFontID(font));
        float textWidth = NVGRenderer.textWidth(text, size, font);
        INSTANCE.gradient(color1, color2, x, y, textWidth, size, gradient);
        NanoVG.nvgFillPaint((long)vg, (NVGPaint)nvgPaint);
        NanoVG.nvgText((long)vg, (float)x, (float)(y + 0.5f), (CharSequence)text);
    }

    public static /* synthetic */ void textGradient$default(String string, float f, float f2, float f3, int n, int n2, Gradient gradient, Font font, int n3, Object object) {
        if ((n3 & 0x40) != 0) {
            gradient = Gradient.LeftToRight;
        }
        if ((n3 & 0x80) != 0) {
            font = interFont;
        }
        NVGRenderer.textGradient(string, f, f2, f3, n, n2, gradient, font);
    }

    @JvmStatic
    public static final void textShadow(@NotNull String text, float x, float y, float size, int color, @NotNull Font font) {
        Intrinsics.checkNotNullParameter((Object)text, (String)"text");
        Intrinsics.checkNotNullParameter((Object)font, (String)"font");
        NanoVG.nvgFontFaceId((long)vg, (int)INSTANCE.getFontID(font));
        NanoVG.nvgFontSize((long)vg, (float)size);
        INSTANCE.color(-16777216);
        NanoVG.nvgFillColor((long)vg, (NVGColor)nvgColor);
        NanoVG.nvgText((long)vg, (float)((float)Math.rint(x + 3.0f)), (float)((float)Math.rint(y + 3.0f)), (CharSequence)text);
        INSTANCE.color(color);
        NanoVG.nvgFillColor((long)vg, (NVGColor)nvgColor);
        NanoVG.nvgText((long)vg, (float)((float)Math.rint(x)), (float)((float)Math.rint(y)), (CharSequence)text);
    }

    public static /* synthetic */ void textShadow$default(String string, float f, float f2, float f3, int n, Font font, int n2, Object object) {
        if ((n2 & 0x20) != 0) {
            font = interFont;
        }
        NVGRenderer.textShadow(string, f, f2, f3, n, font);
    }

    @JvmStatic
    public static final float textWidth(@NotNull String text, float size, @NotNull Font font) {
        Intrinsics.checkNotNullParameter((Object)text, (String)"text");
        Intrinsics.checkNotNullParameter((Object)font, (String)"font");
        NanoVG.nvgFontSize((long)vg, (float)size);
        NanoVG.nvgFontFaceId((long)vg, (int)INSTANCE.getFontID(font));
        return NanoVG.nvgTextBounds((long)vg, (float)0.0f, (float)0.0f, (CharSequence)text, (float[])fontBounds);
    }

    public static /* synthetic */ float textWidth$default(String string, float f, Font font, int n, Object object) {
        if ((n & 4) != 0) {
            font = interFont;
        }
        return NVGRenderer.textWidth(string, f, font);
    }

    @JvmStatic
    public static final void drawWrappedString(@NotNull String text, float x, float y, float w, float size, int color, @NotNull Font font, float lineHeight) {
        Intrinsics.checkNotNullParameter((Object)text, (String)"text");
        Intrinsics.checkNotNullParameter((Object)font, (String)"font");
        NanoVG.nvgFontSize((long)vg, (float)size);
        NanoVG.nvgFontFaceId((long)vg, (int)INSTANCE.getFontID(font));
        NanoVG.nvgTextLineHeight((long)vg, (float)lineHeight);
        INSTANCE.color(color);
        NanoVG.nvgFillColor((long)vg, (NVGColor)nvgColor);
        NanoVG.nvgTextBox((long)vg, (float)x, (float)y, (float)w, (CharSequence)text);
    }

    public static /* synthetic */ void drawWrappedString$default(String string, float f, float f2, float f3, float f4, int n, Font font, float f5, int n2, Object object) {
        if ((n2 & 0x40) != 0) {
            font = interFont;
        }
        if ((n2 & 0x80) != 0) {
            f5 = 1.0f;
        }
        NVGRenderer.drawWrappedString(string, f, f2, f3, f4, n, font, f5);
    }

    @JvmStatic
    public static final float getWrappedStringHeight(@NotNull String text, float maxWidth, float fontSize, @NotNull Font font) {
        Intrinsics.checkNotNullParameter((Object)text, (String)"text");
        Intrinsics.checkNotNullParameter((Object)font, (String)"font");
        NanoVG.nvgFontSize((long)vg, (float)fontSize);
        NanoVG.nvgFontFaceId((long)vg, (int)INSTANCE.getFontID(font));
        float[] bounds = new float[4];
        NanoVG.nvgTextBoxBounds((long)vg, (float)0.0f, (float)0.0f, (float)maxWidth, (CharSequence)text, (float[])bounds);
        return bounds[3] - bounds[1];
    }

    public static /* synthetic */ float getWrappedStringHeight$default(String string, float f, float f2, Font font, int n, Object object) {
        if ((n & 8) != 0) {
            font = interFont;
        }
        return NVGRenderer.getWrappedStringHeight(string, f, f2, font);
    }

    @JvmStatic
    @NotNull
    public static final float[] wrappedTextBounds(@NotNull String text, float w, float size, @NotNull Font font, float lineHeight) {
        Intrinsics.checkNotNullParameter((Object)text, (String)"text");
        Intrinsics.checkNotNullParameter((Object)font, (String)"font");
        float[] bounds = new float[4];
        NanoVG.nvgFontSize((long)vg, (float)size);
        NanoVG.nvgFontFaceId((long)vg, (int)INSTANCE.getFontID(font));
        NanoVG.nvgTextLineHeight((long)vg, (float)lineHeight);
        NanoVG.nvgTextBoxBounds((long)vg, (float)0.0f, (float)0.0f, (float)w, (CharSequence)text, (float[])bounds);
        return bounds;
    }

    public static /* synthetic */ float[] wrappedTextBounds$default(String string, float f, float f2, Font font, float f3, int n, Object object) {
        if ((n & 0x10) != 0) {
            f3 = 1.0f;
        }
        return NVGRenderer.wrappedTextBounds(string, f, f2, font, f3);
    }

    @JvmStatic
    public static final int createNVGImage(int textureId, int textureWidth, int textureHeight) {
        return NanoVGGL3.nvglCreateImageFromHandle((long)vg, (int)textureId, (int)textureWidth, (int)textureHeight, (int)65568);
    }

    @JvmStatic
    public static final void image(@NotNull Image image, float x, float y, float w, float h, float radius, int colorMask) {
        Intrinsics.checkNotNullParameter((Object)image, (String)"image");
        NanoVG.nvgImagePattern((long)vg, (float)x, (float)y, (float)w, (float)h, (float)0.0f, (int)INSTANCE.getImage(image), (float)1.0f, (NVGPaint)nvgPaint);
        if (colorMask != 0) {
            NVGRenderer nVGRenderer = INSTANCE;
            int $this$red$iv = colorMask;
            boolean $i$f$getRed = false;
            NVGRenderer this_$iv = INSTANCE;
            int $this$green$iv = colorMask;
            boolean $i$f$getGreen = false;
            this_$iv = INSTANCE;
            int $this$blue$iv = colorMask;
            boolean $i$f$getBlue = false;
            this_$iv = INSTANCE;
            int $this$alpha$iv = colorMask;
            boolean $i$f$getAlpha = false;
            NanoVG.nvgRGBA((byte)((byte)($this$red$iv >> 16 & 0xFF)), (byte)((byte)($this$green$iv >> 8 & 0xFF)), (byte)((byte)($this$blue$iv & 0xFF)), (byte)((byte)($this$alpha$iv >> 24 & 0xFF)), (NVGColor)nvgPaint.innerColor());
        }
        NanoVG.nvgBeginPath((long)vg);
        if (radius == 0.0f) {
            NanoVG.nvgRect((long)vg, (float)x, (float)y, (float)w, (float)(h + 0.5f));
        } else {
            NanoVG.nvgRoundedRect((long)vg, (float)x, (float)y, (float)w, (float)(h + 0.5f), (float)radius);
        }
        NanoVG.nvgFillPaint((long)vg, (NVGPaint)nvgPaint);
        NanoVG.nvgFill((long)vg);
    }

    public static /* synthetic */ void image$default(Image image, float f, float f2, float f3, float f4, float f5, int n, int n2, Object object) {
        if ((n2 & 0x20) != 0) {
            f5 = 0.0f;
        }
        if ((n2 & 0x40) != 0) {
            n = 0;
        }
        NVGRenderer.image(image, f, f2, f3, f4, f5, n);
    }

    /*
     * WARNING - void declaration
     */
    @JvmStatic
    @NotNull
    public static final Image createImage(@NotNull String resourcePath) {
        Image image;
        Image image2;
        Object v1;
        block8: {
            Intrinsics.checkNotNullParameter((Object)resourcePath, (String)"resourcePath");
            Set<Image> set = images.keySet();
            Intrinsics.checkNotNullExpressionValue(set, (String)"<get-keys>(...)");
            Iterable iterable = set;
            for (Object t : iterable) {
                Image it = (Image)t;
                boolean bl = false;
                if (!Intrinsics.areEqual((Object)it.getIdentifier(), (Object)resourcePath)) continue;
                v1 = t;
                break block8;
            }
            v1 = null;
        }
        if ((image2 = (Image)v1) == null) {
            image2 = new Image(resourcePath, false, null, null, 14, null);
        }
        if ((image = image2).isSVG()) {
            Object object;
            void $this$getOrPut$iv22;
            Map map = images;
            key$iv = image;
            boolean $i$f$getOrPut = false;
            value$iv = $this$getOrPut$iv22.get(key$iv);
            if (value$iv == null) {
                boolean bl = false;
                NVGImage answer$iv = new NVGImage(0, INSTANCE.loadSVG(image));
                $this$getOrPut$iv22.put(key$iv, answer$iv);
                object = answer$iv;
            } else {
                object = value$iv;
            }
            NVGImage nVGImage = (NVGImage)object;
            int $this$getOrPut$iv22 = nVGImage.getCount();
            nVGImage.setCount($this$getOrPut$iv22 + 1);
        } else {
            Object object;
            Map $this$getOrPut$iv22 = images;
            key$iv = image;
            boolean $i$f$getOrPut = false;
            value$iv = $this$getOrPut$iv22.get(key$iv);
            if (value$iv == null) {
                boolean bl = false;
                NVGImage answer$iv = new NVGImage(0, INSTANCE.loadImage(image));
                $this$getOrPut$iv22.put(key$iv, answer$iv);
                object = answer$iv;
            } else {
                object = value$iv;
            }
            NVGImage nVGImage = (NVGImage)object;
            int n = nVGImage.getCount();
            nVGImage.setCount(n + 1);
        }
        return image;
    }

    @JvmStatic
    public static final void deleteImage(@NotNull Image image) {
        Intrinsics.checkNotNullParameter((Object)image, (String)"image");
        NVGImage nVGImage = images.get(image);
        if (nVGImage == null) {
            return;
        }
        NVGImage nvgImage = nVGImage;
        int n = nvgImage.getCount();
        nvgImage.setCount(n + -1);
        if (nvgImage.getCount() == 0) {
            NanoVG.nvgDeleteImage((long)vg, (int)nvgImage.getNvg());
            images.remove(image);
        }
    }

    private final int getImage(Image image) {
        NVGImage nVGImage = images.get(image);
        if (nVGImage == null) {
            throw new IllegalStateException("Image (" + image.getIdentifier() + ") doesn't exist");
        }
        return nVGImage.getNvg();
    }

    private final int loadImage(Image image) {
        int[] w = new int[1];
        int[] h = new int[1];
        int[] channels = new int[1];
        ByteBuffer byteBuffer = STBImage.stbi_load_from_memory((ByteBuffer)image.buffer(), (int[])w, (int[])h, (int[])channels, (int)4);
        if (byteBuffer == null) {
            throw new NullPointerException("Failed to load image: " + image.getIdentifier());
        }
        ByteBuffer buffer = byteBuffer;
        return NanoVG.nvgCreateImageRGBA((long)vg, (int)w[0], (int)h[0], (int)0, (ByteBuffer)buffer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final int loadSVG(Image image) {
        int n;
        Object it;
        Closeable closeable = image.getStream();
        Throwable throwable = null;
        try {
            it = (InputStream)closeable;
            boolean bl = false;
            InputStream inputStream = it;
            Charset charset = Charsets.UTF_8;
            Reader reader = new InputStreamReader(inputStream, charset);
            n = 8192;
            it = TextStreamsKt.readText((Reader)(reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader, n)));
        }
        catch (Throwable bl) {
            throwable = bl;
            throw bl;
        }
        finally {
            CloseableKt.closeFinally((Closeable)closeable, (Throwable)throwable);
        }
        Object vec = it;
        NSVGImage nSVGImage = NanoSVG.nsvgParse((CharSequence)((CharSequence)vec), (CharSequence)"px", (float)96.0f);
        if (nSVGImage == null) {
            throw new IllegalStateException("Failed to parse " + image.getIdentifier());
        }
        NSVGImage svg = nSVGImage;
        int width = (int)svg.width();
        int height = (int)svg.height();
        ByteBuffer buffer = MemoryUtil.memAlloc((int)(width * height * 4));
        try {
            long rasterizer = NanoSVG.nsvgCreateRasterizer();
            NanoSVG.nsvgRasterize((long)rasterizer, (NSVGImage)svg, (float)0.0f, (float)0.0f, (float)1.0f, (ByteBuffer)buffer, (int)width, (int)height, (int)(width * 4));
            int nvgImage = NanoVG.nvgCreateImageRGBA((long)vg, (int)width, (int)height, (int)0, (ByteBuffer)buffer);
            NanoSVG.nsvgDeleteRasterizer((long)rasterizer);
            n = nvgImage;
            return n;
        }
        finally {
            NanoSVG.nsvgDelete((NSVGImage)svg);
            MemoryUtil.memFree((Buffer)buffer);
        }
    }

    private final void color(int color) {
        NVGRenderer nVGRenderer = this;
        int $this$red$iv = color;
        boolean $i$f$getRed = false;
        NVGRenderer this_$iv = this;
        int $this$green$iv = color;
        boolean $i$f$getGreen = false;
        this_$iv = this;
        int $this$blue$iv = color;
        boolean $i$f$getBlue = false;
        this_$iv = this;
        int $this$alpha$iv = color;
        boolean $i$f$getAlpha = false;
        NanoVG.nvgRGBA((byte)((byte)($this$red$iv >> 16 & 0xFF)), (byte)((byte)($this$green$iv >> 8 & 0xFF)), (byte)((byte)($this$blue$iv & 0xFF)), (byte)((byte)($this$alpha$iv >> 24 & 0xFF)), (NVGColor)nvgColor);
    }

    private final void color(int color1, int color2) {
        NVGRenderer nVGRenderer = this;
        int $this$red$iv = color1;
        boolean $i$f$getRed = false;
        NVGRenderer this_$iv = this;
        int $this$green$iv = color1;
        boolean $i$f$getGreen = false;
        this_$iv = this;
        int $this$blue$iv = color1;
        boolean $i$f$getBlue = false;
        this_$iv = this;
        int $this$alpha$iv = color1;
        boolean $i$f$getAlpha = false;
        NanoVG.nvgRGBA((byte)((byte)($this$red$iv >> 16 & 0xFF)), (byte)((byte)($this$green$iv >> 8 & 0xFF)), (byte)((byte)($this$blue$iv & 0xFF)), (byte)((byte)($this$alpha$iv >> 24 & 0xFF)), (NVGColor)nvgColor);
        this_$iv = this;
        $this$red$iv = color2;
        $i$f$getRed = false;
        this_$iv = this;
        $this$green$iv = color2;
        $i$f$getGreen = false;
        this_$iv = this;
        $this$blue$iv = color2;
        $i$f$getBlue = false;
        this_$iv = this;
        $this$alpha$iv = color2;
        $i$f$getAlpha = false;
        NanoVG.nvgRGBA((byte)((byte)($this$red$iv >> 16 & 0xFF)), (byte)((byte)($this$green$iv >> 8 & 0xFF)), (byte)((byte)($this$blue$iv & 0xFF)), (byte)((byte)($this$alpha$iv >> 24 & 0xFF)), (NVGColor)nvgColor2);
    }

    private final void gradient(int color1, int color2, float x, float y, float w, float h, Gradient direction) {
        this.color(color1, color2);
        switch (WhenMappings.$EnumSwitchMapping$0[direction.ordinal()]) {
            case 1: {
                NVGPaint nVGPaint = NanoVG.nvgLinearGradient((long)vg, (float)x, (float)y, (float)(x + w), (float)y, (NVGColor)nvgColor, (NVGColor)nvgColor2, (NVGPaint)nvgPaint);
                break;
            }
            case 2: {
                NVGPaint nVGPaint = NanoVG.nvgLinearGradient((long)vg, (float)x, (float)y, (float)x, (float)(y + h), (NVGColor)nvgColor, (NVGColor)nvgColor2, (NVGPaint)nvgPaint);
                break;
            }
            case 3: {
                NVGPaint nVGPaint = NanoVG.nvgLinearGradient((long)vg, (float)x, (float)y, (float)(x + w), (float)(y + h), (NVGColor)nvgColor, (NVGColor)nvgColor2, (NVGPaint)nvgPaint);
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
    }

    /*
     * WARNING - void declaration
     */
    private final int getFontID(Font font) {
        Object object;
        void $this$getOrPut$iv;
        Map map = fontMap;
        Font key$iv = font;
        boolean $i$f$getOrPut = false;
        Object value$iv = $this$getOrPut$iv.get(key$iv);
        if (value$iv == null) {
            boolean bl = false;
            ByteBuffer buffer = font.buffer();
            NVGFont answer$iv = new NVGFont(NanoVG.nvgCreateFontMem((long)vg, (CharSequence)font.getName(), (ByteBuffer)buffer, (boolean)false), buffer);
            $this$getOrPut$iv.put(key$iv, answer$iv);
            object = answer$iv;
        } else {
            object = value$iv;
        }
        return ((NVGFont)object).getId();
    }

    public final int getRed(int $this$red) {
        boolean $i$f$getRed = false;
        return $this$red >> 16 & 0xFF;
    }

    public final int getGreen(int $this$green) {
        boolean $i$f$getGreen = false;
        return $this$green >> 8 & 0xFF;
    }

    public final int getBlue(int $this$blue) {
        boolean $i$f$getBlue = false;
        return $this$blue & 0xFF;
    }

    public final int getAlpha(int $this$alpha) {
        boolean $i$f$getAlpha = false;
        return $this$alpha >> 24 & 0xFF;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        nvgPaint = NVGPaint.malloc();
        nvgColor = NVGColor.malloc();
        nvgColor2 = NVGColor.malloc();
        interFont = new Font("Inter", "/assets/cobalt/fonts/Inter.otf");
        fontMap = new HashMap();
        fontBounds = new float[4];
        images = new HashMap();
        vg = -1L;
        prevViewport = new int[4];
        prevActiveTexture = 33984;
        vg = NanoVGGL3.nvgCreate((int)3);
        if (!(vg != -1L)) {
            boolean bl = false;
            String string = "Failed to initialize NanoVG";
            throw new IllegalArgumentException(string.toString());
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0082\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ$\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u001b\u0010\u0010\u001a\u00020\u000f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0011\u0010\u0012\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0012\u0010\tJ\u0011\u0010\u0014\u001a\u00020\u0013H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0014\u0010\u0015R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0016\u001a\u0004\b\u0017\u0010\tR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0018\u001a\u0004\b\u0019\u0010\u000b\u00a8\u0006\u001a"}, d2={"Lorg/cobalt/api/util/ui/NVGRenderer$NVGFont;", "", "", "id", "Ljava/nio/ByteBuffer;", "buffer", "<init>", "(ILjava/nio/ByteBuffer;)V", "component1", "()I", "component2", "()Ljava/nio/ByteBuffer;", "copy", "(ILjava/nio/ByteBuffer;)Lorg/cobalt/api/util/ui/NVGRenderer$NVGFont;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getId", "Ljava/nio/ByteBuffer;", "getBuffer", "cobalt"})
    private static final class NVGFont {
        private final int id;
        @NotNull
        private final ByteBuffer buffer;

        public NVGFont(int id, @NotNull ByteBuffer buffer) {
            Intrinsics.checkNotNullParameter((Object)buffer, (String)"buffer");
            this.id = id;
            this.buffer = buffer;
        }

        public final int getId() {
            return this.id;
        }

        @NotNull
        public final ByteBuffer getBuffer() {
            return this.buffer;
        }

        public final int component1() {
            return this.id;
        }

        @NotNull
        public final ByteBuffer component2() {
            return this.buffer;
        }

        @NotNull
        public final NVGFont copy(int id, @NotNull ByteBuffer buffer) {
            Intrinsics.checkNotNullParameter((Object)buffer, (String)"buffer");
            return new NVGFont(id, buffer);
        }

        public static /* synthetic */ NVGFont copy$default(NVGFont nVGFont, int n, ByteBuffer byteBuffer, int n2, Object object) {
            if ((n2 & 1) != 0) {
                n = nVGFont.id;
            }
            if ((n2 & 2) != 0) {
                byteBuffer = nVGFont.buffer;
            }
            return nVGFont.copy(n, byteBuffer);
        }

        @NotNull
        public String toString() {
            return "NVGFont(id=" + this.id + ", buffer=" + this.buffer + ")";
        }

        public int hashCode() {
            int result = Integer.hashCode(this.id);
            result = result * 31 + this.buffer.hashCode();
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof NVGFont)) {
                return false;
            }
            NVGFont nVGFont = (NVGFont)other;
            if (this.id != nVGFont.id) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.buffer, (Object)nVGFont.buffer);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\b\b\u0082\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\bJ$\u0010\n\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\n\u0010\u000bJ\u001b\u0010\u000e\u001a\u00020\r2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0011\u0010\u0010\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0010\u0010\bJ\u0011\u0010\u0012\u001a\u00020\u0011H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0012\u0010\u0013R\"\u0010\u0003\u001a\u00020\u00028\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0003\u0010\u0014\u001a\u0004\b\u0015\u0010\b\"\u0004\b\u0016\u0010\u0017R\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0014\u001a\u0004\b\u0018\u0010\b\u00a8\u0006\u0019"}, d2={"Lorg/cobalt/api/util/ui/NVGRenderer$NVGImage;", "", "", "count", "nvg", "<init>", "(II)V", "component1", "()I", "component2", "copy", "(II)Lorg/cobalt/api/util/ui/NVGRenderer$NVGImage;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getCount", "setCount", "(I)V", "getNvg", "cobalt"})
    private static final class NVGImage {
        private int count;
        private final int nvg;

        public NVGImage(int count, int nvg) {
            this.count = count;
            this.nvg = nvg;
        }

        public final int getCount() {
            return this.count;
        }

        public final void setCount(int n) {
            this.count = n;
        }

        public final int getNvg() {
            return this.nvg;
        }

        public final int component1() {
            return this.count;
        }

        public final int component2() {
            return this.nvg;
        }

        @NotNull
        public final NVGImage copy(int count, int nvg) {
            return new NVGImage(count, nvg);
        }

        public static /* synthetic */ NVGImage copy$default(NVGImage nVGImage, int n, int n2, int n3, Object object) {
            if ((n3 & 1) != 0) {
                n = nVGImage.count;
            }
            if ((n3 & 2) != 0) {
                n2 = nVGImage.nvg;
            }
            return nVGImage.copy(n, n2);
        }

        @NotNull
        public String toString() {
            return "NVGImage(count=" + this.count + ", nvg=" + this.nvg + ")";
        }

        public int hashCode() {
            int result = Integer.hashCode(this.count);
            result = result * 31 + Integer.hashCode(this.nvg);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof NVGImage)) {
                return false;
            }
            NVGImage nVGImage = (NVGImage)other;
            if (this.count != nVGImage.count) {
                return false;
            }
            return this.nvg == nVGImage.nvg;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\f\b\u0002\u0018\u00002\u00020\u0001B1\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0000\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\r\u0010\u000b\u001a\u00020\n\u00a2\u0006\u0004\b\u000b\u0010\fR\u0019\u0010\u0002\u001a\u0004\u0018\u00010\u00008\u0006\u00a2\u0006\f\n\u0004\b\u0002\u0010\r\u001a\u0004\b\u000e\u0010\u000fR\u0017\u0010\u0004\u001a\u00020\u00038\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0010\u001a\u0004\b\u0011\u0010\u0012R\u0017\u0010\u0005\u001a\u00020\u00038\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0010\u001a\u0004\b\u0013\u0010\u0012R\u0017\u0010\u0006\u001a\u00020\u00038\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u0010\u001a\u0004\b\u0014\u0010\u0012R\u0017\u0010\u0007\u001a\u00020\u00038\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u0010\u001a\u0004\b\u0015\u0010\u0012\u00a8\u0006\u0016"}, d2={"Lorg/cobalt/api/util/ui/NVGRenderer$Scissor;", "", "previous", "", "x", "y", "maxX", "maxY", "<init>", "(Lorg/cobalt/api/util/ui/NVGRenderer$Scissor;FFFF)V", "", "applyScissor", "()V", "Lorg/cobalt/api/util/ui/NVGRenderer$Scissor;", "getPrevious", "()Lorg/cobalt/api/util/ui/NVGRenderer$Scissor;", "F", "getX", "()F", "getY", "getMaxX", "getMaxY", "cobalt"})
    private static final class Scissor {
        @Nullable
        private final Scissor previous;
        private final float x;
        private final float y;
        private final float maxX;
        private final float maxY;

        public Scissor(@Nullable Scissor previous, float x, float y, float maxX, float maxY) {
            this.previous = previous;
            this.x = x;
            this.y = y;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        @Nullable
        public final Scissor getPrevious() {
            return this.previous;
        }

        public final float getX() {
            return this.x;
        }

        public final float getY() {
            return this.y;
        }

        public final float getMaxX() {
            return this.maxX;
        }

        public final float getMaxY() {
            return this.maxY;
        }

        public final void applyScissor() {
            if (this.previous == null) {
                NanoVG.nvgScissor((long)vg, (float)this.x, (float)this.y, (float)(this.maxX - this.x), (float)(this.maxY - this.y));
            } else {
                float x = Math.max(this.x, this.previous.x);
                float y = Math.max(this.y, this.previous.y);
                float width = Math.max(0.0f, Math.min(this.maxX, this.previous.maxX) - x);
                float height = Math.max(0.0f, Math.min(this.maxY, this.previous.maxY) - y);
                NanoVG.nvgScissor((long)vg, (float)x, (float)y, (float)width, (float)height);
            }
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[Gradient.values().length];
            try {
                nArray[Gradient.LeftToRight.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[Gradient.TopToBottom.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[Gradient.TopLeftToBottomRight.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

