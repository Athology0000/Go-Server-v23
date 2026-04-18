/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_239
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_3965
 *  net.minecraft.class_638
 *  net.minecraft.class_7923
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.grotto;

import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_638;
import net.minecraft.class_7923;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u0007B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0011\u0010\u0005\u001a\u0004\u0018\u00010\u0004H\u0007\u00a2\u0006\u0004\b\u0005\u0010\u0006\u00a8\u0006\b"}, d2={"Lorg/cobalt/internal/grotto/LookedAtBlockUtils;", "", "<init>", "()V", "Lorg/cobalt/internal/grotto/LookedAtBlockUtils$LookedAtBlockInfo;", "getLookedAtBlockInfo", "()Lorg/cobalt/internal/grotto/LookedAtBlockUtils$LookedAtBlockInfo;", "LookedAtBlockInfo", "cobalt"})
public final class LookedAtBlockUtils {
    @NotNull
    public static final LookedAtBlockUtils INSTANCE = new LookedAtBlockUtils();

    private LookedAtBlockUtils() {
    }

    @JvmStatic
    @Nullable
    public static final LookedAtBlockInfo getLookedAtBlockInfo() {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        class_638 class_6382 = mc.field_1687;
        if (class_6382 == null) {
            return null;
        }
        class_638 level2 = class_6382;
        class_239 class_2392 = mc.field_1765;
        if (class_2392 == null) {
            return null;
        }
        class_239 hit = class_2392;
        if (hit.method_17783() != class_239.class_240.field_1332) {
            return null;
        }
        class_3965 blockHit = (class_3965)hit;
        class_2338 class_23382 = blockHit.method_17777();
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"getBlockPos(...)");
        class_2338 pos = class_23382;
        class_2680 class_26802 = level2.method_8320(pos);
        Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
        class_2680 state = class_26802;
        class_2248 class_22482 = state.method_26204();
        Intrinsics.checkNotNullExpressionValue((Object)class_22482, (String)"getBlock(...)");
        class_2248 block = class_22482;
        String string = class_7923.field_41175.method_10221((Object)block).toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        String name = string;
        int id = class_7923.field_41175.method_10206((Object)block);
        int meta = class_2248.method_9507((class_2680)state);
        return new LookedAtBlockInfo(name, id, meta, pos.method_10263(), pos.method_10264(), pos.method_10260());
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\r\b\u0086\b\u0018\u00002\u00020\u0001B7\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0004\u0012\u0006\u0010\b\u001a\u00020\u0004\u0012\u0006\u0010\t\u001a\u00020\u0004\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0010\u0010\u0010\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u000fJ\u0010\u0010\u0011\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u000fJ\u0010\u0010\u0012\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0012\u0010\u000fJ\u0010\u0010\u0013\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0013\u0010\u000fJL\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u00042\b\b\u0002\u0010\b\u001a\u00020\u00042\b\b\u0002\u0010\t\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u001b\u0010\u0018\u001a\u00020\u00172\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0011\u0010\u001a\u001a\u00020\u0004H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001a\u0010\u000fJ\u0011\u0010\u001b\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001b\u0010\rR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001c\u001a\u0004\b\u001d\u0010\rR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001e\u001a\u0004\b\u001f\u0010\u000fR\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001e\u001a\u0004\b \u0010\u000fR\u0017\u0010\u0007\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u001e\u001a\u0004\b!\u0010\u000fR\u0017\u0010\b\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\b\u0010\u001e\u001a\u0004\b\"\u0010\u000fR\u0017\u0010\t\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\t\u0010\u001e\u001a\u0004\b#\u0010\u000f\u00a8\u0006$"}, d2={"Lorg/cobalt/internal/grotto/LookedAtBlockUtils$LookedAtBlockInfo;", "", "", "name", "", "id", "meta", "x", "y", "z", "<init>", "(Ljava/lang/String;IIIII)V", "component1", "()Ljava/lang/String;", "component2", "()I", "component3", "component4", "component5", "component6", "copy", "(Ljava/lang/String;IIIII)Lorg/cobalt/internal/grotto/LookedAtBlockUtils$LookedAtBlockInfo;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "toString", "Ljava/lang/String;", "getName", "I", "getId", "getMeta", "getX", "getY", "getZ", "cobalt"})
    public static final class LookedAtBlockInfo {
        @NotNull
        private final String name;
        private final int id;
        private final int meta;
        private final int x;
        private final int y;
        private final int z;

        public LookedAtBlockInfo(@NotNull String name, int id, int meta, int x, int y, int z) {
            Intrinsics.checkNotNullParameter((Object)name, (String)"name");
            this.name = name;
            this.id = id;
            this.meta = meta;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @NotNull
        public final String getName() {
            return this.name;
        }

        public final int getId() {
            return this.id;
        }

        public final int getMeta() {
            return this.meta;
        }

        public final int getX() {
            return this.x;
        }

        public final int getY() {
            return this.y;
        }

        public final int getZ() {
            return this.z;
        }

        @NotNull
        public final String component1() {
            return this.name;
        }

        public final int component2() {
            return this.id;
        }

        public final int component3() {
            return this.meta;
        }

        public final int component4() {
            return this.x;
        }

        public final int component5() {
            return this.y;
        }

        public final int component6() {
            return this.z;
        }

        @NotNull
        public final LookedAtBlockInfo copy(@NotNull String name, int id, int meta, int x, int y, int z) {
            Intrinsics.checkNotNullParameter((Object)name, (String)"name");
            return new LookedAtBlockInfo(name, id, meta, x, y, z);
        }

        public static /* synthetic */ LookedAtBlockInfo copy$default(LookedAtBlockInfo lookedAtBlockInfo, String string, int n, int n2, int n3, int n4, int n5, int n6, Object object) {
            if ((n6 & 1) != 0) {
                string = lookedAtBlockInfo.name;
            }
            if ((n6 & 2) != 0) {
                n = lookedAtBlockInfo.id;
            }
            if ((n6 & 4) != 0) {
                n2 = lookedAtBlockInfo.meta;
            }
            if ((n6 & 8) != 0) {
                n3 = lookedAtBlockInfo.x;
            }
            if ((n6 & 0x10) != 0) {
                n4 = lookedAtBlockInfo.y;
            }
            if ((n6 & 0x20) != 0) {
                n5 = lookedAtBlockInfo.z;
            }
            return lookedAtBlockInfo.copy(string, n, n2, n3, n4, n5);
        }

        @NotNull
        public String toString() {
            return "LookedAtBlockInfo(name=" + this.name + ", id=" + this.id + ", meta=" + this.meta + ", x=" + this.x + ", y=" + this.y + ", z=" + this.z + ")";
        }

        public int hashCode() {
            int result = this.name.hashCode();
            result = result * 31 + Integer.hashCode(this.id);
            result = result * 31 + Integer.hashCode(this.meta);
            result = result * 31 + Integer.hashCode(this.x);
            result = result * 31 + Integer.hashCode(this.y);
            result = result * 31 + Integer.hashCode(this.z);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof LookedAtBlockInfo)) {
                return false;
            }
            LookedAtBlockInfo lookedAtBlockInfo = (LookedAtBlockInfo)other;
            if (!Intrinsics.areEqual((Object)this.name, (Object)lookedAtBlockInfo.name)) {
                return false;
            }
            if (this.id != lookedAtBlockInfo.id) {
                return false;
            }
            if (this.meta != lookedAtBlockInfo.meta) {
                return false;
            }
            if (this.x != lookedAtBlockInfo.x) {
                return false;
            }
            if (this.y != lookedAtBlockInfo.y) {
                return false;
            }
            return this.z == lookedAtBlockInfo.z;
        }
    }
}

