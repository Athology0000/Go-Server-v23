/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_1799
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.wardrobe;

import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_1799;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0012\n\u0002\u0010\u000e\n\u0002\b\u000b\b\u0086\b\u0018\u00002\u00020\u0001B7\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u000e\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006\u0012\u0006\u0010\n\u001a\u00020\t\u00a2\u0006\u0004\b\u000b\u0010\fJ\r\u0010\r\u001a\u00020\t\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0010\u0010\u0011\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u0010J\u0010\u0010\u0012\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0012\u0010\u0010J\u0018\u0010\u0013\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006H\u00c6\u0003\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0010\u0010\u0015\u001a\u00020\tH\u00c6\u0003\u00a2\u0006\u0004\b\u0015\u0010\u000eJJ\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\u0010\b\u0002\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u00062\b\b\u0002\u0010\n\u001a\u00020\tH\u00c6\u0001\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u001b\u0010\u0019\u001a\u00020\t2\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\u0011\u0010\u001b\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001b\u0010\u0010J\u0011\u0010\u001d\u001a\u00020\u001cH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001d\u0010\u001eR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001f\u001a\u0004\b \u0010\u0010R\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u001f\u001a\u0004\b!\u0010\u0010R\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001f\u001a\u0004\b\"\u0010\u0010R\u001f\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u00068\u0006\u00a2\u0006\f\n\u0004\b\b\u0010#\u001a\u0004\b$\u0010\u0014R\u0017\u0010\n\u001a\u00020\t8\u0006\u00a2\u0006\f\n\u0004\b\n\u0010%\u001a\u0004\b&\u0010\u000e\u00a8\u0006'"}, d2={"Lorg/cobalt/internal/wardrobe/WardrobeSet;", "", "", "id", "vanillaPage", "inventorySlot", "", "Lnet/minecraft/class_1799;", "armor", "", "locked", "<init>", "(IIILjava/util/List;Z)V", "isEmpty", "()Z", "component1", "()I", "component2", "component3", "component4", "()Ljava/util/List;", "component5", "copy", "(IIILjava/util/List;Z)Lorg/cobalt/internal/wardrobe/WardrobeSet;", "other", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getId", "getVanillaPage", "getInventorySlot", "Ljava/util/List;", "getArmor", "Z", "getLocked", "cobalt"})
@SourceDebugExtension(value={"SMAP\nWardrobeState.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WardrobeState.kt\norg/cobalt/internal/wardrobe/WardrobeSet\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,88:1\n1786#2,3:89\n*S KotlinDebug\n*F\n+ 1 WardrobeState.kt\norg/cobalt/internal/wardrobe/WardrobeSet\n*L\n12#1:89,3\n*E\n"})
public final class WardrobeSet {
    private final int id;
    private final int vanillaPage;
    private final int inventorySlot;
    @NotNull
    private final List<class_1799> armor;
    private final boolean locked;

    public WardrobeSet(int id, int vanillaPage, int inventorySlot, @NotNull List<class_1799> armor, boolean locked) {
        Intrinsics.checkNotNullParameter(armor, (String)"armor");
        this.id = id;
        this.vanillaPage = vanillaPage;
        this.inventorySlot = inventorySlot;
        this.armor = armor;
        this.locked = locked;
    }

    public final int getId() {
        return this.id;
    }

    public final int getVanillaPage() {
        return this.vanillaPage;
    }

    public final int getInventorySlot() {
        return this.inventorySlot;
    }

    @NotNull
    public final List<class_1799> getArmor() {
        return this.armor;
    }

    public final boolean getLocked() {
        return this.locked;
    }

    public final boolean isEmpty() {
        boolean bl;
        block3: {
            Iterable $this$all$iv = this.armor;
            boolean $i$f$all = false;
            if ($this$all$iv instanceof Collection && ((Collection)$this$all$iv).isEmpty()) {
                bl = true;
            } else {
                for (Object element$iv : $this$all$iv) {
                    class_1799 it = (class_1799)element$iv;
                    boolean bl2 = false;
                    if (it == null || it.method_7960()) continue;
                    bl = false;
                    break block3;
                }
                bl = true;
            }
        }
        return bl;
    }

    public final int component1() {
        return this.id;
    }

    public final int component2() {
        return this.vanillaPage;
    }

    public final int component3() {
        return this.inventorySlot;
    }

    @NotNull
    public final List<class_1799> component4() {
        return this.armor;
    }

    public final boolean component5() {
        return this.locked;
    }

    @NotNull
    public final WardrobeSet copy(int id, int vanillaPage, int inventorySlot, @NotNull List<class_1799> armor, boolean locked) {
        Intrinsics.checkNotNullParameter(armor, (String)"armor");
        return new WardrobeSet(id, vanillaPage, inventorySlot, armor, locked);
    }

    public static /* synthetic */ WardrobeSet copy$default(WardrobeSet wardrobeSet, int n, int n2, int n3, List list, boolean bl, int n4, Object object) {
        if ((n4 & 1) != 0) {
            n = wardrobeSet.id;
        }
        if ((n4 & 2) != 0) {
            n2 = wardrobeSet.vanillaPage;
        }
        if ((n4 & 4) != 0) {
            n3 = wardrobeSet.inventorySlot;
        }
        if ((n4 & 8) != 0) {
            list = wardrobeSet.armor;
        }
        if ((n4 & 0x10) != 0) {
            bl = wardrobeSet.locked;
        }
        return wardrobeSet.copy(n, n2, n3, list, bl);
    }

    @NotNull
    public String toString() {
        return "WardrobeSet(id=" + this.id + ", vanillaPage=" + this.vanillaPage + ", inventorySlot=" + this.inventorySlot + ", armor=" + this.armor + ", locked=" + this.locked + ")";
    }

    public int hashCode() {
        int result = Integer.hashCode(this.id);
        result = result * 31 + Integer.hashCode(this.vanillaPage);
        result = result * 31 + Integer.hashCode(this.inventorySlot);
        result = result * 31 + ((Object)this.armor).hashCode();
        result = result * 31 + Boolean.hashCode(this.locked);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof WardrobeSet)) {
            return false;
        }
        WardrobeSet wardrobeSet = (WardrobeSet)other;
        if (this.id != wardrobeSet.id) {
            return false;
        }
        if (this.vanillaPage != wardrobeSet.vanillaPage) {
            return false;
        }
        if (this.inventorySlot != wardrobeSet.inventorySlot) {
            return false;
        }
        if (!Intrinsics.areEqual(this.armor, wardrobeSet.armor)) {
            return false;
        }
        return this.locked == wardrobeSet.locked;
    }
}

