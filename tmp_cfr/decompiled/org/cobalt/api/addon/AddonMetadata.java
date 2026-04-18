/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.addon;

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\f\b\u0086\b\u0018\u00002\u00020\u0001BG\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00020\u0006\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00020\u0006\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0002\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\rJ\u0010\u0010\u000f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\rJ\u0016\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0016\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b\u0012\u0010\u0011J\u0012\u0010\u0013\u001a\u0004\u0018\u00010\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0013\u0010\rJZ\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00020\u00062\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00020\u00062\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u001b\u0010\u0018\u001a\u00020\u00172\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0011\u0010\u001b\u001a\u00020\u001aH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001b\u0010\u001cJ\u0011\u0010\u001d\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001d\u0010\rR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001e\u001a\u0004\b\u001f\u0010\rR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u001e\u001a\u0004\b \u0010\rR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001e\u001a\u0004\b!\u0010\rR\u001d\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\"\u001a\u0004\b#\u0010\u0011R\u001d\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\b\u0010\"\u001a\u0004\b$\u0010\u0011R\u0019\u0010\t\u001a\u0004\u0018\u00010\u00028\u0006\u00a2\u0006\f\n\u0004\b\t\u0010\u001e\u001a\u0004\b%\u0010\r\u00a8\u0006&"}, d2={"Lorg/cobalt/api/addon/AddonMetadata;", "", "", "id", "name", "version", "", "entrypoints", "mixins", "icon", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/lang/String;)V", "component1", "()Ljava/lang/String;", "component2", "component3", "component4", "()Ljava/util/List;", "component5", "component6", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/lang/String;)Lorg/cobalt/api/addon/AddonMetadata;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getId", "getName", "getVersion", "Ljava/util/List;", "getEntrypoints", "getMixins", "getIcon", "cobalt"})
public final class AddonMetadata {
    @NotNull
    private final String id;
    @NotNull
    private final String name;
    @NotNull
    private final String version;
    @NotNull
    private final List<String> entrypoints;
    @NotNull
    private final List<String> mixins;
    @Nullable
    private final String icon;

    public AddonMetadata(@NotNull String id, @NotNull String name, @NotNull String version, @NotNull List<String> entrypoints, @NotNull List<String> mixins, @Nullable String icon) {
        Intrinsics.checkNotNullParameter((Object)id, (String)"id");
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)version, (String)"version");
        Intrinsics.checkNotNullParameter(entrypoints, (String)"entrypoints");
        Intrinsics.checkNotNullParameter(mixins, (String)"mixins");
        this.id = id;
        this.name = name;
        this.version = version;
        this.entrypoints = entrypoints;
        this.mixins = mixins;
        this.icon = icon;
    }

    public /* synthetic */ AddonMetadata(String string, String string2, String string3, List list, List list2, String string4, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 0x20) != 0) {
            string4 = null;
        }
        this(string, string2, string3, list, list2, string4);
    }

    @NotNull
    public final String getId() {
        return this.id;
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    @NotNull
    public final String getVersion() {
        return this.version;
    }

    @NotNull
    public final List<String> getEntrypoints() {
        return this.entrypoints;
    }

    @NotNull
    public final List<String> getMixins() {
        return this.mixins;
    }

    @Nullable
    public final String getIcon() {
        return this.icon;
    }

    @NotNull
    public final String component1() {
        return this.id;
    }

    @NotNull
    public final String component2() {
        return this.name;
    }

    @NotNull
    public final String component3() {
        return this.version;
    }

    @NotNull
    public final List<String> component4() {
        return this.entrypoints;
    }

    @NotNull
    public final List<String> component5() {
        return this.mixins;
    }

    @Nullable
    public final String component6() {
        return this.icon;
    }

    @NotNull
    public final AddonMetadata copy(@NotNull String id, @NotNull String name, @NotNull String version, @NotNull List<String> entrypoints, @NotNull List<String> mixins, @Nullable String icon) {
        Intrinsics.checkNotNullParameter((Object)id, (String)"id");
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)version, (String)"version");
        Intrinsics.checkNotNullParameter(entrypoints, (String)"entrypoints");
        Intrinsics.checkNotNullParameter(mixins, (String)"mixins");
        return new AddonMetadata(id, name, version, entrypoints, mixins, icon);
    }

    public static /* synthetic */ AddonMetadata copy$default(AddonMetadata addonMetadata, String string, String string2, String string3, List list, List list2, String string4, int n, Object object) {
        if ((n & 1) != 0) {
            string = addonMetadata.id;
        }
        if ((n & 2) != 0) {
            string2 = addonMetadata.name;
        }
        if ((n & 4) != 0) {
            string3 = addonMetadata.version;
        }
        if ((n & 8) != 0) {
            list = addonMetadata.entrypoints;
        }
        if ((n & 0x10) != 0) {
            list2 = addonMetadata.mixins;
        }
        if ((n & 0x20) != 0) {
            string4 = addonMetadata.icon;
        }
        return addonMetadata.copy(string, string2, string3, list, list2, string4);
    }

    @NotNull
    public String toString() {
        return "AddonMetadata(id=" + this.id + ", name=" + this.name + ", version=" + this.version + ", entrypoints=" + this.entrypoints + ", mixins=" + this.mixins + ", icon=" + this.icon + ")";
    }

    public int hashCode() {
        int result = this.id.hashCode();
        result = result * 31 + this.name.hashCode();
        result = result * 31 + this.version.hashCode();
        result = result * 31 + ((Object)this.entrypoints).hashCode();
        result = result * 31 + ((Object)this.mixins).hashCode();
        result = result * 31 + (this.icon == null ? 0 : this.icon.hashCode());
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AddonMetadata)) {
            return false;
        }
        AddonMetadata addonMetadata = (AddonMetadata)other;
        if (!Intrinsics.areEqual((Object)this.id, (Object)addonMetadata.id)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.name, (Object)addonMetadata.name)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.version, (Object)addonMetadata.version)) {
            return false;
        }
        if (!Intrinsics.areEqual(this.entrypoints, addonMetadata.entrypoints)) {
            return false;
        }
        if (!Intrinsics.areEqual(this.mixins, addonMetadata.mixins)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.icon, (Object)addonMetadata.icon);
    }
}

