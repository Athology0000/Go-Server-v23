/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.CharsKt
 *  kotlin.text.StringsKt
 *  net.minecraft.class_124
 *  net.minecraft.class_1309
 *  net.minecraft.class_268
 *  net.minecraft.class_269
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.helper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.CharsKt;
import kotlin.text.StringsKt;
import net.minecraft.class_124;
import net.minecraft.class_1309;
import net.minecraft.class_268;
import net.minecraft.class_269;
import net.minecraft.class_310;
import net.minecraft.class_638;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000x\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u001e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010%\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0003/01B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J+\u0010\f\u001a\u00020\u000b2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\u0004\b\f\u0010\rJ!\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\u0005\u001a\u00020\u00042\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0004\b\u000e\u0010\u000fJ%\u0010\u0013\u001a\u00020\u000b2\u0006\u0010\u0007\u001a\u00020\u00062\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010H\u0002\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0019\u0010\u0017\u001a\u0004\u0018\u00010\u00162\u0006\u0010\u0015\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u001f\u0010\u001e\u001a\u00020\u001d2\u0006\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u001c\u001a\u00020\u001bH\u0002\u00a2\u0006\u0004\b\u001e\u0010\u001fJ\u0017\u0010!\u001a\u00020 2\u0006\u0010\u001c\u001a\u00020\u001bH\u0002\u00a2\u0006\u0004\b!\u0010\"R,\u0010$\u001a\u001a\u0012\u0004\u0012\u00020\u0004\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00160#0#8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b$\u0010%R \u0010'\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020&0#8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b'\u0010%R&\u0010+\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020 \u0012\u0004\u0012\u00020*0)0(8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b+\u0010,R\u0014\u0010-\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b-\u0010.\u00a8\u00062"}, d2={"Lorg/cobalt/internal/helper/ClientGlowEspManager;", "", "<init>", "()V", "", "scope", "Lnet/minecraft/class_638;", "level", "", "Lorg/cobalt/internal/helper/ClientGlowEspManager$GlowTarget;", "targets", "", "sync", "(Ljava/lang/String;Lnet/minecraft/class_638;Ljava/util/Collection;)V", "clear", "(Ljava/lang/String;Lnet/minecraft/class_638;)V", "", "Ljava/util/UUID;", "affected", "reconcile", "(Lnet/minecraft/class_638;Ljava/util/Set;)V", "uuid", "Lorg/cobalt/internal/helper/ClientGlowEspManager$GlowRequest;", "bestRequest", "(Ljava/util/UUID;)Lorg/cobalt/internal/helper/ClientGlowEspManager$GlowRequest;", "Lnet/minecraft/class_269;", "scoreboard", "", "argb", "Lnet/minecraft/class_268;", "ensureGlowTeam", "(Lnet/minecraft/class_269;I)Lnet/minecraft/class_268;", "Lnet/minecraft/class_124;", "nearestChatFormatting", "(I)Lnet/minecraft/class_124;", "", "requestsByScope", "Ljava/util/Map;", "Lorg/cobalt/internal/helper/ClientGlowEspManager$AppliedGlow;", "appliedByEntity", "", "Lkotlin/Pair;", "Ljava/awt/Color;", "CHAT_FORMATTING_COLORS", "Ljava/util/List;", "GLOW_TEAM_PREFIX", "Ljava/lang/String;", "GlowTarget", "GlowRequest", "AppliedGlow", "cobalt"})
@SourceDebugExtension(value={"SMAP\nClientGlowEspManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ClientGlowEspManager.kt\norg/cobalt/internal/helper/ClientGlowEspManager\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,165:1\n1#2:166\n812#3,12:167\n1220#3,2:179\n1249#3,4:181\n2469#3,14:185\n*S KotlinDebug\n*F\n+ 1 ClientGlowEspManager.kt\norg/cobalt/internal/helper/ClientGlowEspManager\n*L\n73#1:167,12\n74#1:179,2\n74#1:181,4\n136#1:185,14\n*E\n"})
public final class ClientGlowEspManager {
    @NotNull
    public static final ClientGlowEspManager INSTANCE = new ClientGlowEspManager();
    @NotNull
    private static final Map<String, Map<UUID, GlowRequest>> requestsByScope = new LinkedHashMap();
    @NotNull
    private static final Map<UUID, AppliedGlow> appliedByEntity = new LinkedHashMap();
    @NotNull
    private static final List<Pair<class_124, Color>> CHAT_FORMATTING_COLORS;
    @NotNull
    private static final String GLOW_TEAM_PREFIX = "cbg_";

    private ClientGlowEspManager() {
    }

    public final void sync(@NotNull String scope, @NotNull class_638 level2, @NotNull Collection<GlowTarget> targets) {
        Intrinsics.checkNotNullParameter((Object)scope, (String)"scope");
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        Intrinsics.checkNotNullParameter(targets, (String)"targets");
        LinkedHashMap next = new LinkedHashMap(targets.size());
        for (GlowTarget target : targets) {
            class_1309 entity = target.getEntity();
            if (!entity.method_5805()) continue;
            Map map = next;
            UUID uUID = entity.method_5667();
            String string = entity.method_5820();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getScoreboardName(...)");
            GlowRequest glowRequest = new GlowRequest(string, target.getArgb(), target.getPriority());
            map.put(uUID, glowRequest);
        }
        Map<UUID, GlowRequest> previous = requestsByScope.get(scope);
        LinkedHashSet affected = new LinkedHashSet();
        Map<UUID, GlowRequest> map = previous;
        if (map != null && (map = map.keySet()) != null) {
            Collection p0 = (Collection)((Object)map);
            boolean bl = false;
            affected.addAll(p0);
        }
        affected.addAll(next.keySet());
        if (next.isEmpty()) {
            requestsByScope.remove(scope);
        } else {
            requestsByScope.put(scope, next);
        }
        this.reconcile(level2, affected);
    }

    public final void clear(@NotNull String scope, @Nullable class_638 level2) {
        Intrinsics.checkNotNullParameter((Object)scope, (String)"scope");
        Map<UUID, GlowRequest> map = requestsByScope.remove(scope);
        if (map == null || (map = map.keySet()) == null) {
            return;
        }
        Map<UUID, GlowRequest> removed = map;
        if (level2 == null) {
            Iterator iterator = removed.iterator();
            while (iterator.hasNext()) {
                UUID uuid = (UUID)iterator.next();
                if (this.bestRequest(uuid) != null) continue;
                appliedByEntity.remove(uuid);
            }
            return;
        }
        this.reconcile(level2, new LinkedHashSet((Collection)((Object)removed)));
    }

    public static /* synthetic */ void clear$default(ClientGlowEspManager clientGlowEspManager, String string, class_638 class_6382, int n, Object object) {
        if ((n & 2) != 0) {
            class_6382 = class_310.method_1551().field_1687;
        }
        clientGlowEspManager.clear(string, class_6382);
    }

    /*
     * WARNING - void declaration
     */
    private final void reconcile(class_638 level2, Set<UUID> affected) {
        void $this$associateByTo$iv$iv;
        void $this$associateBy$iv;
        void $this$filterIsInstanceTo$iv$iv;
        Iterable $this$filterIsInstance$iv;
        if (affected.isEmpty()) {
            return;
        }
        class_269 class_2692 = level2.method_8428();
        Intrinsics.checkNotNullExpressionValue((Object)class_2692, (String)"getScoreboard(...)");
        class_269 scoreboard = class_2692;
        Iterable iterable = level2.method_18112();
        Intrinsics.checkNotNullExpressionValue((Object)iterable, (String)"entitiesForRendering(...)");
        Iterable iterable2 = iterable;
        boolean $i$f$filterIsInstance = false;
        void var7_7 = $this$filterIsInstance$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterIsInstanceTo = false;
        for (Object element$iv$iv : $this$filterIsInstanceTo$iv$iv) {
            if (!(element$iv$iv instanceof class_1309)) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        $this$filterIsInstance$iv = (List)destination$iv$iv;
        boolean $i$f$associateBy = false;
        int capacity$iv = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateBy$iv, (int)10)), (int)16);
        destination$iv$iv = $this$associateBy$iv;
        Map destination$iv$iv2 = new LinkedHashMap(capacity$iv);
        boolean $i$f$associateByTo = false;
        for (Object element$iv$iv : $this$associateByTo$iv$iv) {
            void it;
            class_1309 class_13092 = (class_1309)element$iv$iv;
            Map map = destination$iv$iv2;
            boolean bl = false;
            map.put(it.method_5667(), element$iv$iv);
        }
        Map loadedLivingById = destination$iv$iv2;
        for (UUID uuid : affected) {
            GlowRequest request = this.bestRequest(uuid);
            AppliedGlow applied = appliedByEntity.get(uuid);
            if (request == null) {
                class_1309 class_13093 = (class_1309)loadedLivingById.get(uuid);
                if (class_13093 != null) {
                    class_13093.method_5834(false);
                }
                if (applied == null) continue;
                scoreboard.method_1195(applied.getScoreHolder());
                appliedByEntity.remove(uuid);
                continue;
            }
            class_1309 living = (class_1309)loadedLivingById.get(uuid);
            if (living == null || !living.method_5805()) continue;
            class_268 team = this.ensureGlowTeam(scoreboard, request.getArgb());
            class_268 currentTeam = scoreboard.method_1164(request.getScoreHolder());
            if (currentTeam == null || !Intrinsics.areEqual((Object)currentTeam.method_1197(), (Object)team.method_1197())) {
                if (currentTeam != null) {
                    String string = currentTeam.method_1197();
                    Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getName(...)");
                    if (StringsKt.startsWith$default((String)string, (String)GLOW_TEAM_PREFIX, (boolean)false, (int)2, null)) {
                        scoreboard.method_1157(request.getScoreHolder(), currentTeam);
                    }
                }
                scoreboard.method_1172(request.getScoreHolder(), team);
            }
            if (applied != null && !Intrinsics.areEqual((Object)applied.getScoreHolder(), (Object)request.getScoreHolder())) {
                scoreboard.method_1195(applied.getScoreHolder());
            }
            living.method_5834(true);
            appliedByEntity.put(uuid, new AppliedGlow(request.getScoreHolder()));
        }
    }

    private final GlowRequest bestRequest(UUID uuid) {
        GlowRequest best = null;
        for (Map<UUID, GlowRequest> scopeRequests : requestsByScope.values()) {
            GlowRequest candidate;
            if (scopeRequests.get(uuid) == null || best != null && candidate.getPriority() < best.getPriority()) continue;
            best = candidate;
        }
        return best;
    }

    private final class_268 ensureGlowTeam(class_269 scoreboard, int argb) {
        class_124 formatting = this.nearestChatFormatting(argb);
        String string = Integer.toString(formatting.ordinal(), CharsKt.checkRadix((int)16));
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        String teamName = GLOW_TEAM_PREFIX + string;
        class_268 class_2682 = scoreboard.method_1153(teamName);
        if (class_2682 == null) {
            class_268 class_2683 = scoreboard.method_1171(teamName);
            class_2682 = class_2683;
            Intrinsics.checkNotNullExpressionValue((Object)class_2683, (String)"addPlayerTeam(...)");
        }
        class_268 team = class_2682;
        team.method_1141(formatting);
        return team;
    }

    private final class_124 nearestChatFormatting(int argb) {
        class_124 class_1242;
        Object v0;
        Color source = new Color(argb, true);
        Iterable $this$minByOrNull$iv = CHAT_FORMATTING_COLORS;
        boolean $i$f$minByOrNull = false;
        Iterator iterator$iv = $this$minByOrNull$iv.iterator();
        if (!iterator$iv.hasNext()) {
            v0 = null;
        } else {
            Object minElem$iv = iterator$iv.next();
            if (!iterator$iv.hasNext()) {
                v0 = minElem$iv;
            } else {
                Pair pair = (Pair)minElem$iv;
                boolean bl = false;
                Color candidate = (Color)pair.component2();
                int dr = source.getRed() - candidate.getRed();
                int dg = source.getGreen() - candidate.getGreen();
                int db = source.getBlue() - candidate.getBlue();
                int minValue$iv = dr * dr + dg * dg + db * db;
                do {
                    int db2;
                    int dg2;
                    Object e$iv = iterator$iv.next();
                    candidate = (Pair)e$iv;
                    $i$a$-minByOrNull-ClientGlowEspManager$nearestChatFormatting$1 = false;
                    Color candidate2 = (Color)candidate.component2();
                    int dr2 = source.getRed() - candidate2.getRed();
                    int v$iv = dr2 * dr2 + (dg2 = source.getGreen() - candidate2.getGreen()) * dg2 + (db2 = source.getBlue() - candidate2.getBlue()) * db2;
                    if (minValue$iv <= v$iv) continue;
                    minElem$iv = e$iv;
                    minValue$iv = v$iv;
                } while (iterator$iv.hasNext());
                v0 = minElem$iv;
            }
        }
        Pair pair = v0;
        return pair != null && (class_1242 = (class_124)pair.getFirst()) != null ? class_1242 : class_124.field_1068;
    }

    static {
        Object[] objectArray = new Pair[]{TuplesKt.to((Object)class_124.field_1074, (Object)new Color(0)), TuplesKt.to((Object)class_124.field_1058, (Object)new Color(170)), TuplesKt.to((Object)class_124.field_1077, (Object)new Color(43520)), TuplesKt.to((Object)class_124.field_1062, (Object)new Color(43690)), TuplesKt.to((Object)class_124.field_1079, (Object)new Color(0xAA0000)), TuplesKt.to((Object)class_124.field_1064, (Object)new Color(0xAA00AA)), TuplesKt.to((Object)class_124.field_1065, (Object)new Color(0xFFAA00)), TuplesKt.to((Object)class_124.field_1080, (Object)new Color(0xAAAAAA)), TuplesKt.to((Object)class_124.field_1063, (Object)new Color(0x555555)), TuplesKt.to((Object)class_124.field_1078, (Object)new Color(0x5555FF)), TuplesKt.to((Object)class_124.field_1060, (Object)new Color(0x55FF55)), TuplesKt.to((Object)class_124.field_1075, (Object)new Color(0x55FFFF)), TuplesKt.to((Object)class_124.field_1061, (Object)new Color(0xFF5555)), TuplesKt.to((Object)class_124.field_1076, (Object)new Color(0xFF55FF)), TuplesKt.to((Object)class_124.field_1054, (Object)new Color(0xFFFF55)), TuplesKt.to((Object)class_124.field_1068, (Object)new Color(0xFFFFFF))};
        CHAT_FORMATTING_COLORS = CollectionsKt.listOf((Object[])objectArray);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0006\b\u0082\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\u0006\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u001a\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\b\u0010\tJ\u001b\u0010\f\u001a\u00020\u000b2\b\u0010\n\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\f\u0010\rJ\u0011\u0010\u000f\u001a\u00020\u000eH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0011\u0010\u0011\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0011\u0010\u0007R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0012\u001a\u0004\b\u0013\u0010\u0007\u00a8\u0006\u0014"}, d2={"Lorg/cobalt/internal/helper/ClientGlowEspManager$AppliedGlow;", "", "", "scoreHolder", "<init>", "(Ljava/lang/String;)V", "component1", "()Ljava/lang/String;", "copy", "(Ljava/lang/String;)Lorg/cobalt/internal/helper/ClientGlowEspManager$AppliedGlow;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getScoreHolder", "cobalt"})
    private static final class AppliedGlow {
        @NotNull
        private final String scoreHolder;

        public AppliedGlow(@NotNull String scoreHolder) {
            Intrinsics.checkNotNullParameter((Object)scoreHolder, (String)"scoreHolder");
            this.scoreHolder = scoreHolder;
        }

        @NotNull
        public final String getScoreHolder() {
            return this.scoreHolder;
        }

        @NotNull
        public final String component1() {
            return this.scoreHolder;
        }

        @NotNull
        public final AppliedGlow copy(@NotNull String scoreHolder) {
            Intrinsics.checkNotNullParameter((Object)scoreHolder, (String)"scoreHolder");
            return new AppliedGlow(scoreHolder);
        }

        public static /* synthetic */ AppliedGlow copy$default(AppliedGlow appliedGlow, String string, int n, Object object) {
            if ((n & 1) != 0) {
                string = appliedGlow.scoreHolder;
            }
            return appliedGlow.copy(string);
        }

        @NotNull
        public String toString() {
            return "AppliedGlow(scoreHolder=" + this.scoreHolder + ")";
        }

        public int hashCode() {
            return this.scoreHolder.hashCode();
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AppliedGlow)) {
                return false;
            }
            AppliedGlow appliedGlow = (AppliedGlow)other;
            return Intrinsics.areEqual((Object)this.scoreHolder, (Object)appliedGlow.scoreHolder);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\n\b\u0082\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\fJ.\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u001b\u0010\u0012\u001a\u00020\u00112\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0011\u0010\u0014\u001a\u00020\u0004H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0014\u0010\fJ\u0011\u0010\u0015\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0015\u0010\nR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0016\u001a\u0004\b\u0017\u0010\nR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0018\u001a\u0004\b\u0019\u0010\fR\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u0018\u001a\u0004\b\u001a\u0010\f\u00a8\u0006\u001b"}, d2={"Lorg/cobalt/internal/helper/ClientGlowEspManager$GlowRequest;", "", "", "scoreHolder", "", "argb", "priority", "<init>", "(Ljava/lang/String;II)V", "component1", "()Ljava/lang/String;", "component2", "()I", "component3", "copy", "(Ljava/lang/String;II)Lorg/cobalt/internal/helper/ClientGlowEspManager$GlowRequest;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "toString", "Ljava/lang/String;", "getScoreHolder", "I", "getArgb", "getPriority", "cobalt"})
    private static final class GlowRequest {
        @NotNull
        private final String scoreHolder;
        private final int argb;
        private final int priority;

        public GlowRequest(@NotNull String scoreHolder, int argb, int priority) {
            Intrinsics.checkNotNullParameter((Object)scoreHolder, (String)"scoreHolder");
            this.scoreHolder = scoreHolder;
            this.argb = argb;
            this.priority = priority;
        }

        @NotNull
        public final String getScoreHolder() {
            return this.scoreHolder;
        }

        public final int getArgb() {
            return this.argb;
        }

        public final int getPriority() {
            return this.priority;
        }

        @NotNull
        public final String component1() {
            return this.scoreHolder;
        }

        public final int component2() {
            return this.argb;
        }

        public final int component3() {
            return this.priority;
        }

        @NotNull
        public final GlowRequest copy(@NotNull String scoreHolder, int argb, int priority) {
            Intrinsics.checkNotNullParameter((Object)scoreHolder, (String)"scoreHolder");
            return new GlowRequest(scoreHolder, argb, priority);
        }

        public static /* synthetic */ GlowRequest copy$default(GlowRequest glowRequest, String string, int n, int n2, int n3, Object object) {
            if ((n3 & 1) != 0) {
                string = glowRequest.scoreHolder;
            }
            if ((n3 & 2) != 0) {
                n = glowRequest.argb;
            }
            if ((n3 & 4) != 0) {
                n2 = glowRequest.priority;
            }
            return glowRequest.copy(string, n, n2);
        }

        @NotNull
        public String toString() {
            return "GlowRequest(scoreHolder=" + this.scoreHolder + ", argb=" + this.argb + ", priority=" + this.priority + ")";
        }

        public int hashCode() {
            int result = this.scoreHolder.hashCode();
            result = result * 31 + Integer.hashCode(this.argb);
            result = result * 31 + Integer.hashCode(this.priority);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof GlowRequest)) {
                return false;
            }
            GlowRequest glowRequest = (GlowRequest)other;
            if (!Intrinsics.areEqual((Object)this.scoreHolder, (Object)glowRequest.scoreHolder)) {
                return false;
            }
            if (this.argb != glowRequest.argb) {
                return false;
            }
            return this.priority == glowRequest.priority;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\b\b\u0086\b\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\fJ.\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u001b\u0010\u0012\u001a\u00020\u00112\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0011\u0010\u0014\u001a\u00020\u0004H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0014\u0010\fJ\u0011\u0010\u0016\u001a\u00020\u0015H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0018\u001a\u0004\b\u0019\u0010\nR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001a\u001a\u0004\b\u001b\u0010\fR\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001a\u001a\u0004\b\u001c\u0010\f\u00a8\u0006\u001d"}, d2={"Lorg/cobalt/internal/helper/ClientGlowEspManager$GlowTarget;", "", "Lnet/minecraft/class_1309;", "entity", "", "argb", "priority", "<init>", "(Lnet/minecraft/class_1309;II)V", "component1", "()Lnet/minecraft/class_1309;", "component2", "()I", "component3", "copy", "(Lnet/minecraft/class_1309;II)Lorg/cobalt/internal/helper/ClientGlowEspManager$GlowTarget;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "Lnet/minecraft/class_1309;", "getEntity", "I", "getArgb", "getPriority", "cobalt"})
    public static final class GlowTarget {
        @NotNull
        private final class_1309 entity;
        private final int argb;
        private final int priority;

        public GlowTarget(@NotNull class_1309 entity, int argb, int priority) {
            Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
            this.entity = entity;
            this.argb = argb;
            this.priority = priority;
        }

        public /* synthetic */ GlowTarget(class_1309 class_13092, int n, int n2, int n3, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n3 & 4) != 0) {
                n2 = 0;
            }
            this(class_13092, n, n2);
        }

        @NotNull
        public final class_1309 getEntity() {
            return this.entity;
        }

        public final int getArgb() {
            return this.argb;
        }

        public final int getPriority() {
            return this.priority;
        }

        @NotNull
        public final class_1309 component1() {
            return this.entity;
        }

        public final int component2() {
            return this.argb;
        }

        public final int component3() {
            return this.priority;
        }

        @NotNull
        public final GlowTarget copy(@NotNull class_1309 entity, int argb, int priority) {
            Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
            return new GlowTarget(entity, argb, priority);
        }

        public static /* synthetic */ GlowTarget copy$default(GlowTarget glowTarget, class_1309 class_13092, int n, int n2, int n3, Object object) {
            if ((n3 & 1) != 0) {
                class_13092 = glowTarget.entity;
            }
            if ((n3 & 2) != 0) {
                n = glowTarget.argb;
            }
            if ((n3 & 4) != 0) {
                n2 = glowTarget.priority;
            }
            return glowTarget.copy(class_13092, n, n2);
        }

        @NotNull
        public String toString() {
            return "GlowTarget(entity=" + this.entity + ", argb=" + this.argb + ", priority=" + this.priority + ")";
        }

        public int hashCode() {
            int result = this.entity.hashCode();
            result = result * 31 + Integer.hashCode(this.argb);
            result = result * 31 + Integer.hashCode(this.priority);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof GlowTarget)) {
                return false;
            }
            GlowTarget glowTarget = (GlowTarget)other;
            if (!Intrinsics.areEqual((Object)this.entity, (Object)glowTarget.entity)) {
                return false;
            }
            if (this.argb != glowTarget.argb) {
                return false;
            }
            return this.priority == glowTarget.priority;
        }
    }
}

