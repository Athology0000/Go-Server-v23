/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_2338
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.routes;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_2338;
import org.cobalt.internal.routes.RoutePointType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0086\b\u0018\u00002\u00020\u0001BW\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0004\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0004\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0004\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0004\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u000b\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0010\u0010\u0011\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0010\u0010\u0013\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0013\u0010\u0012J\u0010\u0010\u0014\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0014\u0010\u0012J\u0012\u0010\u0015\u001a\u0004\u0018\u00010\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u0012\u0010\u0017\u001a\u0004\u0018\u00010\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0017\u0010\u0016J\u0012\u0010\u0018\u001a\u0004\u0018\u00010\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0018\u0010\u0016J\u0012\u0010\u0019\u001a\u0004\u0018\u00010\u000bH\u00c6\u0003\u00a2\u0006\u0004\b\u0019\u0010\u001aJh\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u00042\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00042\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00042\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00042\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u000bH\u00c6\u0001\u00a2\u0006\u0004\b\u001b\u0010\u001cJ\u001b\u0010\u001f\u001a\u00020\u001e2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u001f\u0010 J\u0011\u0010!\u001a\u00020\u0004H\u00d6\u0081\u0004\u00a2\u0006\u0004\b!\u0010\u0012J\u0011\u0010\"\u001a\u00020\u000bH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\"\u0010\u001aR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010#\u001a\u0004\b$\u0010\u0010R\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010%\u001a\u0004\b&\u0010\u0012R\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010%\u001a\u0004\b'\u0010\u0012R\u0017\u0010\u0007\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010%\u001a\u0004\b(\u0010\u0012R\u0019\u0010\b\u001a\u0004\u0018\u00010\u00048\u0006\u00a2\u0006\f\n\u0004\b\b\u0010)\u001a\u0004\b*\u0010\u0016R\u0019\u0010\t\u001a\u0004\u0018\u00010\u00048\u0006\u00a2\u0006\f\n\u0004\b\t\u0010)\u001a\u0004\b+\u0010\u0016R\u0019\u0010\n\u001a\u0004\u0018\u00010\u00048\u0006\u00a2\u0006\f\n\u0004\b\n\u0010)\u001a\u0004\b,\u0010\u0016R\u0019\u0010\f\u001a\u0004\u0018\u00010\u000b8\u0006\u00a2\u0006\f\n\u0004\b\f\u0010-\u001a\u0004\b.\u0010\u001aR\u0011\u00102\u001a\u00020/8F\u00a2\u0006\u0006\u001a\u0004\b0\u00101R\u0013\u00104\u001a\u0004\u0018\u00010/8F\u00a2\u0006\u0006\u001a\u0004\b3\u00101\u00a8\u00065"}, d2={"Lorg/cobalt/internal/routes/RoutePoint;", "", "Lorg/cobalt/internal/routes/RoutePointType;", "type", "", "x", "y", "z", "mx", "my", "mz", "", "blockId", "<init>", "(Lorg/cobalt/internal/routes/RoutePointType;IIILjava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/String;)V", "component1", "()Lorg/cobalt/internal/routes/RoutePointType;", "component2", "()I", "component3", "component4", "component5", "()Ljava/lang/Integer;", "component6", "component7", "component8", "()Ljava/lang/String;", "copy", "(Lorg/cobalt/internal/routes/RoutePointType;IIILjava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/String;)Lorg/cobalt/internal/routes/RoutePoint;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "toString", "Lorg/cobalt/internal/routes/RoutePointType;", "getType", "I", "getX", "getY", "getZ", "Ljava/lang/Integer;", "getMx", "getMy", "getMz", "Ljava/lang/String;", "getBlockId", "Lnet/minecraft/class_2338;", "getPos", "()Lnet/minecraft/class_2338;", "pos", "getMineEnd", "mineEnd", "cobalt"})
public final class RoutePoint {
    @NotNull
    private final RoutePointType type;
    private final int x;
    private final int y;
    private final int z;
    @Nullable
    private final Integer mx;
    @Nullable
    private final Integer my;
    @Nullable
    private final Integer mz;
    @Nullable
    private final String blockId;

    public RoutePoint(@NotNull RoutePointType type, int x, int y, int z, @Nullable Integer mx, @Nullable Integer my, @Nullable Integer mz, @Nullable String blockId) {
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mx = mx;
        this.my = my;
        this.mz = mz;
        this.blockId = blockId;
    }

    public /* synthetic */ RoutePoint(RoutePointType routePointType, int n, int n2, int n3, Integer n4, Integer n5, Integer n6, String string, int n7, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n7 & 0x10) != 0) {
            n4 = null;
        }
        if ((n7 & 0x20) != 0) {
            n5 = null;
        }
        if ((n7 & 0x40) != 0) {
            n6 = null;
        }
        if ((n7 & 0x80) != 0) {
            string = null;
        }
        this(routePointType, n, n2, n3, n4, n5, n6, string);
    }

    @NotNull
    public final RoutePointType getType() {
        return this.type;
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

    @Nullable
    public final Integer getMx() {
        return this.mx;
    }

    @Nullable
    public final Integer getMy() {
        return this.my;
    }

    @Nullable
    public final Integer getMz() {
        return this.mz;
    }

    @Nullable
    public final String getBlockId() {
        return this.blockId;
    }

    @NotNull
    public final class_2338 getPos() {
        return new class_2338(this.x, this.y, this.z);
    }

    @Nullable
    public final class_2338 getMineEnd() {
        return this.mx != null && this.my != null && this.mz != null ? new class_2338(this.mx.intValue(), this.my.intValue(), this.mz.intValue()) : null;
    }

    @NotNull
    public final RoutePointType component1() {
        return this.type;
    }

    public final int component2() {
        return this.x;
    }

    public final int component3() {
        return this.y;
    }

    public final int component4() {
        return this.z;
    }

    @Nullable
    public final Integer component5() {
        return this.mx;
    }

    @Nullable
    public final Integer component6() {
        return this.my;
    }

    @Nullable
    public final Integer component7() {
        return this.mz;
    }

    @Nullable
    public final String component8() {
        return this.blockId;
    }

    @NotNull
    public final RoutePoint copy(@NotNull RoutePointType type, int x, int y, int z, @Nullable Integer mx, @Nullable Integer my, @Nullable Integer mz, @Nullable String blockId) {
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        return new RoutePoint(type, x, y, z, mx, my, mz, blockId);
    }

    public static /* synthetic */ RoutePoint copy$default(RoutePoint routePoint, RoutePointType routePointType, int n, int n2, int n3, Integer n4, Integer n5, Integer n6, String string, int n7, Object object) {
        if ((n7 & 1) != 0) {
            routePointType = routePoint.type;
        }
        if ((n7 & 2) != 0) {
            n = routePoint.x;
        }
        if ((n7 & 4) != 0) {
            n2 = routePoint.y;
        }
        if ((n7 & 8) != 0) {
            n3 = routePoint.z;
        }
        if ((n7 & 0x10) != 0) {
            n4 = routePoint.mx;
        }
        if ((n7 & 0x20) != 0) {
            n5 = routePoint.my;
        }
        if ((n7 & 0x40) != 0) {
            n6 = routePoint.mz;
        }
        if ((n7 & 0x80) != 0) {
            string = routePoint.blockId;
        }
        return routePoint.copy(routePointType, n, n2, n3, n4, n5, n6, string);
    }

    @NotNull
    public String toString() {
        return "RoutePoint(type=" + this.type + ", x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", mx=" + this.mx + ", my=" + this.my + ", mz=" + this.mz + ", blockId=" + this.blockId + ")";
    }

    public int hashCode() {
        int result = this.type.hashCode();
        result = result * 31 + Integer.hashCode(this.x);
        result = result * 31 + Integer.hashCode(this.y);
        result = result * 31 + Integer.hashCode(this.z);
        result = result * 31 + (this.mx == null ? 0 : ((Object)this.mx).hashCode());
        result = result * 31 + (this.my == null ? 0 : ((Object)this.my).hashCode());
        result = result * 31 + (this.mz == null ? 0 : ((Object)this.mz).hashCode());
        result = result * 31 + (this.blockId == null ? 0 : this.blockId.hashCode());
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RoutePoint)) {
            return false;
        }
        RoutePoint routePoint = (RoutePoint)other;
        if (this.type != routePoint.type) {
            return false;
        }
        if (this.x != routePoint.x) {
            return false;
        }
        if (this.y != routePoint.y) {
            return false;
        }
        if (this.z != routePoint.z) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.mx, (Object)routePoint.mx)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.my, (Object)routePoint.my)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.mz, (Object)routePoint.mz)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.blockId, (Object)routePoint.blockId);
    }
}

