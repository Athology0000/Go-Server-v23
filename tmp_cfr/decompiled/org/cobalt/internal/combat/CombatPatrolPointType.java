/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.combat;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0010\u000e\n\u0002\b\u000b\b\u0086\u0081\u0002\u0018\u0000 \t2\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\tB\u0011\b\u0002\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0006\u001a\u0004\b\u0007\u0010\bj\u0002\b\nj\u0002\b\u000bj\u0002\b\f\u00a8\u0006\r"}, d2={"Lorg/cobalt/internal/combat/CombatPatrolPointType;", "", "", "id", "<init>", "(Ljava/lang/String;ILjava/lang/String;)V", "Ljava/lang/String;", "getId", "()Ljava/lang/String;", "Companion", "WALK", "WARP", "KILL", "cobalt"})
public final class CombatPatrolPointType
extends Enum<CombatPatrolPointType> {
    @NotNull
    public static final Companion Companion;
    @NotNull
    private final String id;
    public static final /* enum */ CombatPatrolPointType WALK;
    public static final /* enum */ CombatPatrolPointType WARP;
    public static final /* enum */ CombatPatrolPointType KILL;
    private static final /* synthetic */ CombatPatrolPointType[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    private CombatPatrolPointType(String id) {
        this.id = id;
    }

    @NotNull
    public final String getId() {
        return this.id;
    }

    public static CombatPatrolPointType[] values() {
        return (CombatPatrolPointType[])$VALUES.clone();
    }

    public static CombatPatrolPointType valueOf(String value) {
        return Enum.valueOf(CombatPatrolPointType.class, value);
    }

    @NotNull
    public static EnumEntries<CombatPatrolPointType> getEntries() {
        return $ENTRIES;
    }

    static {
        WALK = new CombatPatrolPointType("walk");
        WARP = new CombatPatrolPointType("warp");
        KILL = new CombatPatrolPointType("kill");
        $VALUES = combatPatrolPointTypeArray = new CombatPatrolPointType[]{CombatPatrolPointType.WALK, CombatPatrolPointType.WARP, CombatPatrolPointType.KILL};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        Companion = new Companion(null);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\b\u0010\u0005\u001a\u0004\u0018\u00010\u0004\u00a2\u0006\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2={"Lorg/cobalt/internal/combat/CombatPatrolPointType$Companion;", "", "<init>", "()V", "", "id", "Lorg/cobalt/internal/combat/CombatPatrolPointType;", "fromId", "(Ljava/lang/String;)Lorg/cobalt/internal/combat/CombatPatrolPointType;", "cobalt"})
    @SourceDebugExtension(value={"SMAP\nCombatPatrolModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CombatPatrolModule.kt\norg/cobalt/internal/combat/CombatPatrolPointType$Companion\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,839:1\n296#2,2:840\n*S KotlinDebug\n*F\n+ 1 CombatPatrolModule.kt\norg/cobalt/internal/combat/CombatPatrolPointType$Companion\n*L\n43#1:840,2\n*E\n"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final CombatPatrolPointType fromId(@Nullable String id) {
            CombatPatrolPointType combatPatrolPointType;
            Object v0;
            block2: {
                Iterable $this$firstOrNull$iv = (Iterable)CombatPatrolPointType.getEntries();
                boolean $i$f$firstOrNull = false;
                for (Object element$iv : $this$firstOrNull$iv) {
                    CombatPatrolPointType it = (CombatPatrolPointType)((Object)element$iv);
                    boolean bl = false;
                    if (!Intrinsics.areEqual((Object)it.getId(), (Object)id)) continue;
                    v0 = element$iv;
                    break block2;
                }
                v0 = null;
            }
            if ((combatPatrolPointType = (CombatPatrolPointType)v0) == null) {
                combatPatrolPointType = WALK;
            }
            return combatPatrolPointType;
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

