/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_1937
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2680
 *  net.minecraft.class_7923
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.grotto;

import java.util.HashMap;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_1937;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_7923;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J7\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\b0\n2\b\u0010\u0005\u001a\u0004\u0018\u00010\u00042\b\u0010\u0007\u001a\u0004\u0018\u00010\u00062\u0006\u0010\t\u001a\u00020\bH\u0007\u00a2\u0006\u0004\b\f\u0010\r\u00a8\u0006\u000e"}, d2={"Lorg/cobalt/internal/grotto/BlockScanUtils;", "", "<init>", "()V", "Lnet/minecraft/class_1937;", "level", "Lnet/minecraft/class_2338;", "base", "", "radius", "", "", "scanAround", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;I)Ljava/util/Map;", "cobalt"})
public final class BlockScanUtils {
    @NotNull
    public static final BlockScanUtils INSTANCE = new BlockScanUtils();

    private BlockScanUtils() {
    }

    @JvmStatic
    @NotNull
    public static final Map<String, Integer> scanAround(@Nullable class_1937 level2, @Nullable class_2338 base, int radius) {
        HashMap result = new HashMap();
        if (level2 == null || base == null || radius <= 0) {
            return result;
        }
        int bx = base.method_10263();
        int by = base.method_10264();
        int bz = base.method_10260();
        int dx = -radius;
        if (dx <= radius) {
            while (true) {
                int dy;
                if ((dy = -radius) <= radius) {
                    while (true) {
                        int dz;
                        if ((dz = -radius) <= radius) {
                            while (true) {
                                String id;
                                class_2248 block;
                                class_2680 state;
                                class_2338 pos = new class_2338(bx + dx, by + dy, bz + dz);
                                Intrinsics.checkNotNullExpressionValue((Object)level2.method_8320(pos), (String)"getBlockState(...)");
                                Intrinsics.checkNotNullExpressionValue((Object)state.method_26204(), (String)"getBlock(...)");
                                Intrinsics.checkNotNullExpressionValue((Object)class_7923.field_41175.method_10221((Object)block).toString(), (String)"toString(...)");
                                int stateId = class_2248.method_9507((class_2680)state);
                                String key = id + ":" + stateId;
                                Map map = result;
                                Integer n = (Integer)result.get(key);
                                Integer n2 = (n != null ? n : 0) + 1;
                                map.put(key, n2);
                                if (dz == radius) break;
                                ++dz;
                            }
                        }
                        if (dy == radius) break;
                        ++dy;
                    }
                }
                if (dx == radius) break;
                ++dx;
            }
        }
        return result;
    }
}

