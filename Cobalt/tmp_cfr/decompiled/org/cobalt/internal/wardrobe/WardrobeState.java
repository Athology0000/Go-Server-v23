/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_1799
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.wardrobe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_1799;
import org.cobalt.internal.wardrobe.WardrobeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010$\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u000e\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010#\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003JI\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u001a\u0010\u000b\u001a\u0016\u0012\u0004\u0012\u00020\u0006\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\t0\b2\b\u0010\f\u001a\u0004\u0018\u00010\u00062\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00060\r\u00a2\u0006\u0004\b\u000f\u0010\u0010R\"\u0010\u0012\u001a\u00020\u00118\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0012\u0010\u0013\u001a\u0004\b\u0012\u0010\u0014\"\u0004\b\u0015\u0010\u0016R$\u0010\u0017\u001a\u0004\u0018\u00010\u00068\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0017\u0010\u0018\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR$\u0010\u001d\u001a\u0004\u0018\u00010\u00068\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u001d\u0010\u0018\u001a\u0004\b\u001e\u0010\u001a\"\u0004\b\u001f\u0010\u001cR\u001d\u0010\"\u001a\b\u0012\u0004\u0012\u00020!0 8\u0006\u00a2\u0006\f\n\u0004\b\"\u0010#\u001a\u0004\b$\u0010%R\u001d\u0010'\u001a\b\u0012\u0004\u0012\u00020\u00060&8\u0006\u00a2\u0006\f\n\u0004\b'\u0010(\u001a\u0004\b)\u0010*\u00a8\u0006+"}, d2={"Lorg/cobalt/internal/wardrobe/WardrobeState;", "", "<init>", "()V", "", "reset", "", "page", "", "", "Lnet/minecraft/class_1799;", "armorBySetId", "equippedId", "", "lockedIds", "updatePage", "(ILjava/util/Map;Ljava/lang/Integer;Ljava/util/Set;)V", "", "isOpen", "Z", "()Z", "setOpen", "(Z)V", "currentVanillaPage", "Ljava/lang/Integer;", "getCurrentVanillaPage", "()Ljava/lang/Integer;", "setCurrentVanillaPage", "(Ljava/lang/Integer;)V", "equippedSlotId", "getEquippedSlotId", "setEquippedSlotId", "", "Lorg/cobalt/internal/wardrobe/WardrobeSet;", "sets", "Ljava/util/List;", "getSets", "()Ljava/util/List;", "", "favorites", "Ljava/util/Set;", "getFavorites", "()Ljava/util/Set;", "cobalt"})
public final class WardrobeState {
    @NotNull
    public static final WardrobeState INSTANCE = new WardrobeState();
    private static boolean isOpen;
    @Nullable
    private static Integer currentVanillaPage;
    @Nullable
    private static Integer equippedSlotId;
    @NotNull
    private static final List<WardrobeSet> sets;
    @NotNull
    private static final Set<Integer> favorites;

    private WardrobeState() {
    }

    public final boolean isOpen() {
        return isOpen;
    }

    public final void setOpen(boolean bl) {
        isOpen = bl;
    }

    @Nullable
    public final Integer getCurrentVanillaPage() {
        return currentVanillaPage;
    }

    public final void setCurrentVanillaPage(@Nullable Integer n) {
        currentVanillaPage = n;
    }

    @Nullable
    public final Integer getEquippedSlotId() {
        return equippedSlotId;
    }

    public final void setEquippedSlotId(@Nullable Integer n) {
        equippedSlotId = n;
    }

    @NotNull
    public final List<WardrobeSet> getSets() {
        return sets;
    }

    @NotNull
    public final Set<Integer> getFavorites() {
        return favorites;
    }

    public final void reset() {
        isOpen = false;
        currentVanillaPage = null;
        equippedSlotId = null;
        int n = ((Collection)sets).size();
        for (int i = 0; i < n; ++i) {
            Object[] objectArray = new Void[]{null, null, null, null};
            sets.set(i, WardrobeSet.copy$default(sets.get(i), 0, 0, 0, CollectionsKt.listOf((Object[])objectArray), false, 7, null));
        }
    }

    public final void updatePage(int page, @NotNull Map<Integer, ? extends List<class_1799>> armorBySetId, @Nullable Integer equippedId, @NotNull Set<Integer> lockedIds) {
        int startId;
        Intrinsics.checkNotNullParameter(armorBySetId, (String)"armorBySetId");
        Intrinsics.checkNotNullParameter(lockedIds, (String)"lockedIds");
        if (equippedId != null) {
            equippedSlotId = equippedId;
        }
        int n = startId + 9;
        for (int id = startId = (page - 1) * 9 + 1; id < n; ++id) {
            int idx = id - 1;
            boolean bl = 0 <= idx ? idx < ((Collection)sets).size() : false;
            if (!bl) continue;
            List list = armorBySetId.get(id);
            if (list == null) {
                Object[] objectArray = new Void[]{null, null, null, null};
                list = CollectionsKt.listOf((Object[])objectArray);
            }
            sets.set(idx, WardrobeSet.copy$default(sets.get(idx), 0, 0, 0, list, lockedIds.contains(id), 7, null));
        }
    }

    /*
     * WARNING - void declaration
     */
    static {
        int n = 27;
        ArrayList<WardrobeSet> arrayList = new ArrayList<WardrobeSet>(n);
        int n2 = 0;
        while (n2 < n) {
            void i;
            int n3;
            int n4 = n3 = n2++;
            ArrayList<WardrobeSet> arrayList2 = arrayList;
            boolean bl = false;
            Object[] objectArray = new Void[]{null, null, null, null};
            arrayList2.add(new WardrobeSet((int)(i + true), (int)(i / 9 + true), 36 + i % 9, CollectionsKt.listOf((Object[])objectArray), false));
        }
        sets = arrayList;
        favorites = new LinkedHashSet();
    }
}

