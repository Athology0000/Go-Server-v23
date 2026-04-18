/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.dungeons.map;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.internal.dungeons.map.RoomKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u000b\b\u0086\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\f\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\u000fJ.\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u0006H\u00c6\u0001\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u001b\u0010\u0014\u001a\u00020\u00132\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u0011\u0010\u0016\u001a\u00020\u0006H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0016\u0010\u000fJ\u0011\u0010\u0017\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0017\u0010\u000bR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0018\u001a\u0004\b\u0019\u0010\u000bR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001a\u001a\u0004\b\u001b\u0010\rR\u0017\u0010\u0007\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u001c\u001a\u0004\b\u001d\u0010\u000f\u00a8\u0006\u001e"}, d2={"Lorg/cobalt/internal/dungeons/map/RoomDefinition;", "", "", "name", "Lorg/cobalt/internal/dungeons/map/RoomKind;", "type", "", "secrets", "<init>", "(Ljava/lang/String;Lorg/cobalt/internal/dungeons/map/RoomKind;I)V", "component1", "()Ljava/lang/String;", "component2", "()Lorg/cobalt/internal/dungeons/map/RoomKind;", "component3", "()I", "copy", "(Ljava/lang/String;Lorg/cobalt/internal/dungeons/map/RoomKind;I)Lorg/cobalt/internal/dungeons/map/RoomDefinition;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "toString", "Ljava/lang/String;", "getName", "Lorg/cobalt/internal/dungeons/map/RoomKind;", "getType", "I", "getSecrets", "cobalt"})
public final class RoomDefinition {
    @NotNull
    private final String name;
    @NotNull
    private final RoomKind type;
    private final int secrets;

    public RoomDefinition(@NotNull String name, @NotNull RoomKind type, int secrets) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        this.name = name;
        this.type = type;
        this.secrets = secrets;
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    @NotNull
    public final RoomKind getType() {
        return this.type;
    }

    public final int getSecrets() {
        return this.secrets;
    }

    @NotNull
    public final String component1() {
        return this.name;
    }

    @NotNull
    public final RoomKind component2() {
        return this.type;
    }

    public final int component3() {
        return this.secrets;
    }

    @NotNull
    public final RoomDefinition copy(@NotNull String name, @NotNull RoomKind type, int secrets) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        return new RoomDefinition(name, type, secrets);
    }

    public static /* synthetic */ RoomDefinition copy$default(RoomDefinition roomDefinition, String string, RoomKind roomKind, int n, int n2, Object object) {
        if ((n2 & 1) != 0) {
            string = roomDefinition.name;
        }
        if ((n2 & 2) != 0) {
            roomKind = roomDefinition.type;
        }
        if ((n2 & 4) != 0) {
            n = roomDefinition.secrets;
        }
        return roomDefinition.copy(string, roomKind, n);
    }

    @NotNull
    public String toString() {
        return "RoomDefinition(name=" + this.name + ", type=" + this.type + ", secrets=" + this.secrets + ")";
    }

    public int hashCode() {
        int result = this.name.hashCode();
        result = result * 31 + this.type.hashCode();
        result = result * 31 + Integer.hashCode(this.secrets);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RoomDefinition)) {
            return false;
        }
        RoomDefinition roomDefinition = (RoomDefinition)other;
        if (!Intrinsics.areEqual((Object)this.name, (Object)roomDefinition.name)) {
            return false;
        }
        if (this.type != roomDefinition.type) {
            return false;
        }
        return this.secrets == roomDefinition.secrets;
    }
}

