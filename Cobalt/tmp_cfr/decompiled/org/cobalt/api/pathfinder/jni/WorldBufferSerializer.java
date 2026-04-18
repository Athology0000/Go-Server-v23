/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_3481
 *  net.minecraft.class_3486
 *  net.minecraft.class_3610
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.pathfinder.jni;

import java.util.HashMap;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3481;
import net.minecraft.class_3486;
import net.minecraft.class_3610;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.pathfinder.jni.WorldBufferResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000`\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0005\n\u0002\b\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\r\u0010\n\u001a\u00020\t\u00a2\u0006\u0004\b\n\u0010\u0003JO\u0010\u0017\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b\u0017\u0010\u0018JG\u0010\u001b\u001a\u00020\t2\u0006\u0010\u0019\u001a\u00020\u000b2\u0006\u0010\u001a\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b\u001b\u0010\u001cJG\u0010\u001e\u001a\u00020\t2\u0006\u0010\u0019\u001a\u00020\u000b2\u0006\u0010\u001d\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b\u001e\u0010\u001cJG\u0010\u001f\u001a\u00020\t2\u0006\u0010\u0019\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b\u001f\u0010\u001cJG\u0010 \u001a\u00020\t2\u0006\u0010\u0019\u001a\u00020\u000b2\u0006\u0010\u001a\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b \u0010\u001cJ?\u0010!\u001a\u00020\t2\u0006\u0010\u0019\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b!\u0010\"J'\u0010'\u001a\u00020&2\u0006\u0010$\u001a\u00020#2\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020%H\u0002\u00a2\u0006\u0004\b'\u0010(R\u0014\u0010)\u001a\u00020\u000e8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b)\u0010*R\u0014\u0010+\u001a\u00020\u000e8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b+\u0010*R\u0014\u0010,\u001a\u00020\u000e8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b,\u0010*R\u0014\u0010-\u001a\u00020\u000e8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b-\u0010*R\u0014\u0010.\u001a\u00020\u000e8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b.\u0010*R\u0014\u0010/\u001a\u00020&8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b/\u00100R\u0014\u00101\u001a\u00020&8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b1\u00100R\u0014\u00102\u001a\u00020&8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b2\u00100R\u0014\u00103\u001a\u00020&8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b3\u00100R\u0014\u00104\u001a\u00020&8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b4\u00100R\u0014\u00105\u001a\u00020&8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b5\u00100R\u0018\u00106\u001a\u0004\u0018\u00010\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b6\u00107R\u0016\u00108\u001a\u00020\u000e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b8\u0010*R\u0016\u00109\u001a\u00020\u000e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b9\u0010*R\u0016\u0010:\u001a\u00020\u000e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b:\u0010*R0\u0010>\u001a\u001e\u0012\u0004\u0012\u00020#\u0012\u0004\u0012\u00020<0;j\u000e\u0012\u0004\u0012\u00020#\u0012\u0004\u0012\u00020<`=8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b>\u0010?\u00a8\u0006@"}, d2={"Lorg/cobalt/api/pathfinder/jni/WorldBufferSerializer;", "", "<init>", "()V", "Lnet/minecraft/class_310;", "mc", "Lorg/cobalt/api/pathfinder/jni/WorldBufferResult;", "serialize", "(Lnet/minecraft/class_310;)Lorg/cobalt/api/pathfinder/jni/WorldBufferResult;", "", "invalidate", "", "src", "dst", "", "ddx", "bx", "by", "bz", "Lnet/minecraft/class_1937;", "level", "Lnet/minecraft/class_2338$class_2339;", "pos", "shiftX", "([B[BIIIILnet/minecraft/class_1937;Lnet/minecraft/class_2338$class_2339;)V", "buf", "ddy", "shiftY", "([BIIIILnet/minecraft/class_1937;Lnet/minecraft/class_2338$class_2339;)V", "ddz", "shiftZ", "refillXSlice", "refillYSlice", "fullRebuild", "([BIIILnet/minecraft/class_1937;Lnet/minecraft/class_2338$class_2339;)V", "Lnet/minecraft/class_2680;", "state", "Lnet/minecraft/class_2338;", "", "classify", "(Lnet/minecraft/class_2680;Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)B", "BUF_W", "I", "BUF_H", "BUF_D", "BUF_STRIDE_Z", "BUF_STRIDE_Y", "BT_AIR", "B", "BT_SOLID", "BT_WATER", "BT_LAVA", "BT_LADDER", "BT_STEP", "cachedBuf", "[B", "cachedBx", "cachedBy", "cachedBz", "Ljava/util/HashMap;", "", "Lkotlin/collections/HashMap;", "heightByState", "Ljava/util/HashMap;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nWorldBufferSerializer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WorldBufferSerializer.kt\norg/cobalt/api/pathfinder/jni/WorldBufferSerializer\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,310:1\n383#2,7:311\n*S KotlinDebug\n*F\n+ 1 WorldBufferSerializer.kt\norg/cobalt/api/pathfinder/jni/WorldBufferSerializer\n*L\n299#1:311,7\n*E\n"})
public final class WorldBufferSerializer {
    @NotNull
    public static final WorldBufferSerializer INSTANCE = new WorldBufferSerializer();
    private static final int BUF_W = 96;
    private static final int BUF_H = 40;
    private static final int BUF_D = 96;
    private static final int BUF_STRIDE_Z = 96;
    private static final int BUF_STRIDE_Y = 9216;
    private static final byte BT_AIR = 0;
    private static final byte BT_SOLID = 1;
    private static final byte BT_WATER = 2;
    private static final byte BT_LAVA = 3;
    private static final byte BT_LADDER = 4;
    private static final byte BT_STEP = 5;
    @Nullable
    private static byte[] cachedBuf;
    private static int cachedBx;
    private static int cachedBy;
    private static int cachedBz;
    @NotNull
    private static final HashMap<class_2680, Double> heightByState;

    private WorldBufferSerializer() {
    }

    @Nullable
    public final WorldBufferResult serialize(@NotNull class_310 mc) {
        Intrinsics.checkNotNullParameter((Object)mc, (String)"mc");
        class_638 class_6382 = mc.field_1687;
        if (class_6382 == null) {
            return null;
        }
        class_638 level2 = class_6382;
        class_746 class_7462 = mc.field_1724;
        if (class_7462 == null) {
            return null;
        }
        class_746 player = class_7462;
        int bx = player.method_31477() - 48;
        int by = player.method_31478() - 10;
        int bz = player.method_31479() - 48;
        byte[] cached = cachedBuf;
        if (cached != null && bx == cachedBx && by == cachedBy && bz == cachedBz) {
            return new WorldBufferResult(cached, bx, by, bz);
        }
        byte[] buf = new byte[368640];
        class_2338.class_2339 mpos = new class_2338.class_2339();
        int ddx = bx - cachedBx;
        int ddy = by - cachedBy;
        int ddz = bz - cachedBz;
        if (cached != null && ddx >= -1 && ddx <= 1 && ddy >= -1 && ddy <= 1 && ddz >= -1 && ddz <= 1) {
            if (ddx != 0) {
                this.shiftX(cached, buf, ddx, bx, by, bz, (class_1937)level2, mpos);
            } else {
                System.arraycopy(cached, 0, buf, 0, buf.length);
            }
            if (ddy != 0) {
                this.shiftY(buf, ddy, bx, by, bz, (class_1937)level2, mpos);
            }
            if (ddz != 0) {
                this.shiftZ(buf, ddz, bx, by, bz, (class_1937)level2, mpos);
            }
            if (ddx != 0 && (ddy != 0 || ddz != 0)) {
                this.refillXSlice(buf, ddx, bx, by, bz, (class_1937)level2, mpos);
            }
            if (ddy != 0 && ddz != 0) {
                this.refillYSlice(buf, ddy, bx, by, bz, (class_1937)level2, mpos);
            }
        } else {
            this.fullRebuild(buf, bx, by, bz, (class_1937)level2, mpos);
        }
        cachedBuf = buf;
        cachedBx = bx;
        cachedBy = by;
        cachedBz = bz;
        return new WorldBufferResult(buf, bx, by, bz);
    }

    public final void invalidate() {
        cachedBuf = null;
        heightByState.clear();
    }

    private final void shiftX(byte[] src, byte[] dst, int ddx, int bx, int by, int bz, class_1937 level2, class_2338.class_2339 pos) {
        if (ddx > 0) {
            for (int dy = 0; dy < 40; ++dy) {
                for (int dz = 0; dz < 96; ++dz) {
                    int base = dy * 9216 + dz * 96;
                    System.arraycopy(src, base + ddx, dst, base, 96 - ddx);
                    for (int dx = 96 - ddx; dx < 96; ++dx) {
                        pos.method_10103(bx + dx, by + dy, bz + dz);
                        class_2680 class_26802 = level2.method_8320((class_2338)pos);
                        Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
                        dst[base + dx] = this.classify(class_26802, level2, (class_2338)pos);
                    }
                }
            }
        } else {
            int shift = -ddx;
            for (int dy = 0; dy < 40; ++dy) {
                for (int dz = 0; dz < 96; ++dz) {
                    int base = dy * 9216 + dz * 96;
                    System.arraycopy(src, base, dst, base + shift, 96 - shift);
                    for (int dx = 0; dx < shift; ++dx) {
                        pos.method_10103(bx + dx, by + dy, bz + dz);
                        class_2680 class_26803 = level2.method_8320((class_2338)pos);
                        Intrinsics.checkNotNullExpressionValue((Object)class_26803, (String)"getBlockState(...)");
                        dst[base + dx] = this.classify(class_26803, level2, (class_2338)pos);
                    }
                }
            }
        }
    }

    private final void shiftY(byte[] buf, int ddy, int bx, int by, int bz, class_1937 level2, class_2338.class_2339 pos) {
        if (ddy > 0) {
            System.arraycopy(buf, ddy * 9216, buf, 0, (40 - ddy) * 9216);
            for (int dy = 40 - ddy; dy < 40; ++dy) {
                for (int dz = 0; dz < 96; ++dz) {
                    int base = dy * 9216 + dz * 96;
                    for (int dx = 0; dx < 96; ++dx) {
                        pos.method_10103(bx + dx, by + dy, bz + dz);
                        class_2680 class_26802 = level2.method_8320((class_2338)pos);
                        Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
                        buf[base + dx] = this.classify(class_26802, level2, (class_2338)pos);
                    }
                }
            }
        } else {
            int shift = -ddy;
            System.arraycopy(buf, 0, buf, shift * 9216, (40 - shift) * 9216);
            for (int dy = 0; dy < shift; ++dy) {
                for (int dz = 0; dz < 96; ++dz) {
                    int base = dy * 9216 + dz * 96;
                    for (int dx = 0; dx < 96; ++dx) {
                        pos.method_10103(bx + dx, by + dy, bz + dz);
                        class_2680 class_26803 = level2.method_8320((class_2338)pos);
                        Intrinsics.checkNotNullExpressionValue((Object)class_26803, (String)"getBlockState(...)");
                        buf[base + dx] = this.classify(class_26803, level2, (class_2338)pos);
                    }
                }
            }
        }
    }

    private final void shiftZ(byte[] buf, int ddz, int bx, int by, int bz, class_1937 level2, class_2338.class_2339 pos) {
        if (ddz > 0) {
            for (int dy = 0; dy < 40; ++dy) {
                int baseY = dy * 9216;
                System.arraycopy(buf, baseY + ddz * 96, buf, baseY, (96 - ddz) * 96);
                for (int dz = 96 - ddz; dz < 96; ++dz) {
                    int base = baseY + dz * 96;
                    for (int dx = 0; dx < 96; ++dx) {
                        pos.method_10103(bx + dx, by + dy, bz + dz);
                        class_2680 class_26802 = level2.method_8320((class_2338)pos);
                        Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
                        buf[base + dx] = this.classify(class_26802, level2, (class_2338)pos);
                    }
                }
            }
        } else {
            int shift = -ddz;
            for (int dy = 0; dy < 40; ++dy) {
                int baseY = dy * 9216;
                System.arraycopy(buf, baseY, buf, baseY + shift * 96, (96 - shift) * 96);
                for (int dz = 0; dz < shift; ++dz) {
                    int base = baseY + dz * 96;
                    for (int dx = 0; dx < 96; ++dx) {
                        pos.method_10103(bx + dx, by + dy, bz + dz);
                        class_2680 class_26803 = level2.method_8320((class_2338)pos);
                        Intrinsics.checkNotNullExpressionValue((Object)class_26803, (String)"getBlockState(...)");
                        buf[base + dx] = this.classify(class_26803, level2, (class_2338)pos);
                    }
                }
            }
        }
    }

    private final void refillXSlice(byte[] buf, int ddx, int bx, int by, int bz, class_1937 level2, class_2338.class_2339 pos) {
        int xStart = ddx > 0 ? 96 - ddx : 0;
        int xEnd = ddx > 0 ? 96 : -ddx;
        for (int dy = 0; dy < 40; ++dy) {
            int baseY = dy * 9216;
            for (int dz = 0; dz < 96; ++dz) {
                int baseYZ = baseY + dz * 96;
                for (int dx = xStart; dx < xEnd; ++dx) {
                    pos.method_10103(bx + dx, by + dy, bz + dz);
                    class_2680 class_26802 = level2.method_8320((class_2338)pos);
                    Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
                    buf[baseYZ + dx] = this.classify(class_26802, level2, (class_2338)pos);
                }
            }
        }
    }

    private final void refillYSlice(byte[] buf, int ddy, int bx, int by, int bz, class_1937 level2, class_2338.class_2339 pos) {
        int yStart = ddy > 0 ? 40 - ddy : 0;
        int yEnd = ddy > 0 ? 40 : -ddy;
        for (int dy = yStart; dy < yEnd; ++dy) {
            int baseY = dy * 9216;
            for (int dz = 0; dz < 96; ++dz) {
                int baseYZ = baseY + dz * 96;
                for (int dx = 0; dx < 96; ++dx) {
                    pos.method_10103(bx + dx, by + dy, bz + dz);
                    class_2680 class_26802 = level2.method_8320((class_2338)pos);
                    Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
                    buf[baseYZ + dx] = this.classify(class_26802, level2, (class_2338)pos);
                }
            }
        }
    }

    private final void fullRebuild(byte[] buf, int bx, int by, int bz, class_1937 level2, class_2338.class_2339 pos) {
        for (int dy = 0; dy < 40; ++dy) {
            int baseY = dy * 9216;
            for (int dz = 0; dz < 96; ++dz) {
                int baseYZ = baseY + dz * 96;
                for (int dx = 0; dx < 96; ++dx) {
                    pos.method_10103(bx + dx, by + dy, bz + dz);
                    class_2680 class_26802 = level2.method_8320((class_2338)pos);
                    Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
                    buf[baseYZ + dx] = this.classify(class_26802, level2, (class_2338)pos);
                }
            }
        }
    }

    /*
     * WARNING - void declaration
     */
    private final byte classify(class_2680 state, class_1937 level2, class_2338 pos) {
        Object object;
        void $this$getOrPut$iv;
        if (state.method_26215()) {
            return 0;
        }
        class_3610 class_36102 = state.method_26227();
        Intrinsics.checkNotNullExpressionValue((Object)class_36102, (String)"getFluidState(...)");
        class_3610 fluid = class_36102;
        if (!fluid.method_15769()) {
            return (byte)(fluid.method_15767(class_3486.field_15517) ? 2 : (fluid.method_15767(class_3486.field_15518) ? 3 : 2));
        }
        if (state.method_26164(class_3481.field_22414)) {
            return 4;
        }
        Map map = heightByState;
        class_2680 key$iv = state;
        boolean $i$f$getOrPut = false;
        Object value$iv = $this$getOrPut$iv.get(key$iv);
        if (value$iv == null) {
            boolean bl = false;
            class_265 class_2652 = state.method_26220((class_1922)level2, pos);
            Intrinsics.checkNotNullExpressionValue((Object)class_2652, (String)"getCollisionShape(...)");
            class_265 shape = class_2652;
            Double answer$iv = shape.method_1110() ? 0.0 : shape.method_1107().field_1325;
            $this$getOrPut$iv.put(key$iv, answer$iv);
            object = answer$iv;
        } else {
            object = value$iv;
        }
        double maxHeight = ((Number)object).doubleValue();
        return (byte)(maxHeight < 0.1 ? 0 : (maxHeight < 0.75 ? 5 : 1));
    }

    static {
        cachedBx = Integer.MIN_VALUE;
        cachedBy = Integer.MIN_VALUE;
        cachedBz = Integer.MIN_VALUE;
        heightByState = new HashMap(512);
    }
}

