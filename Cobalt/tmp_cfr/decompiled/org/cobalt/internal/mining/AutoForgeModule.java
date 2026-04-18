/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.collections.CollectionsKt
 *  kotlin.comparisons.ComparisonsKt
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.sequences.Sequence
 *  kotlin.sequences.SequencesKt
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  net.minecraft.class_124
 *  net.minecraft.class_1297
 *  net.minecraft.class_1531
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1735
 *  net.minecraft.class_1799
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2371
 *  net.minecraft.class_2561
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  net.minecraft.class_465
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.mining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_1531;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2371;
import net.minecraft.class_2561;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.InfoSetting;
import org.cobalt.api.module.setting.impl.InfoType;
import org.cobalt.api.module.setting.impl.TextSetting;
import org.cobalt.api.pathfinder.jni.NativePathfinder;
import org.cobalt.api.pathfinder.jni.PathStatus;
import org.cobalt.api.pathfinder.minecraft.MinecraftPathingRules;
import org.cobalt.api.util.AngleUtils;
import org.cobalt.api.util.ChatUtils;
import org.cobalt.api.util.InventoryUtils;
import org.cobalt.api.util.ItemUtilsKt;
import org.cobalt.api.util.helper.Rotation;
import org.cobalt.internal.mining.RoutesModule;
import org.cobalt.internal.pathfinding.PathfindingModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00a4\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\t\n\u0002\b\u0018\n\u0002\u0010\u0006\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0004\u0082\u0001\u0083\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\t\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\t\u0010\u0003J\u000f\u0010\n\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\n\u0010\u0003J\u0017\u0010\r\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\u000bH\u0002\u00a2\u0006\u0004\b\r\u0010\u000eJ\u000f\u0010\u000f\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u000f\u0010\u0003J\u000f\u0010\u0010\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u0010\u0010\u0003J\u000f\u0010\u0011\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u0011\u0010\u0003J\u000f\u0010\u0012\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u0012\u0010\u0003J\u000f\u0010\u0013\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u0013\u0010\u0003J\u0015\u0010\u0015\u001a\b\u0012\u0002\b\u0003\u0018\u00010\u0014H\u0002\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u0019\u0010\u0018\u001a\u0004\u0018\u00010\u00172\u0006\u0010\f\u001a\u00020\u000bH\u0002\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0017\u0010\u001c\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001aH\u0002\u00a2\u0006\u0004\b\u001c\u0010\u001dJ\u0017\u0010\u001f\u001a\u00020\u00062\u0006\u0010\u001e\u001a\u00020\u0017H\u0002\u00a2\u0006\u0004\b\u001f\u0010 J!\u0010$\u001a\b\u0012\u0004\u0012\u00020#0\"2\n\u0010!\u001a\u0006\u0012\u0002\b\u00030\u0014H\u0002\u00a2\u0006\u0004\b$\u0010%J!\u0010'\u001a\b\u0012\u0004\u0012\u00020&0\"2\n\u0010!\u001a\u0006\u0012\u0002\b\u00030\u0014H\u0002\u00a2\u0006\u0004\b'\u0010%J\u001b\u0010)\u001a\u00020(2\n\u0010!\u001a\u0006\u0012\u0002\b\u00030\u0014H\u0002\u00a2\u0006\u0004\b)\u0010*J\u001b\u0010,\u001a\u00020+2\n\u0010!\u001a\u0006\u0012\u0002\b\u00030\u0014H\u0002\u00a2\u0006\u0004\b,\u0010-J\u001b\u0010.\u001a\u00020+2\n\u0010!\u001a\u0006\u0012\u0002\b\u00030\u0014H\u0002\u00a2\u0006\u0004\b.\u0010-J\u001b\u0010/\u001a\u00020+2\n\u0010!\u001a\u0006\u0012\u0002\b\u00030\u0014H\u0002\u00a2\u0006\u0004\b/\u0010-J\u001b\u00100\u001a\u00020+2\n\u0010!\u001a\u0006\u0012\u0002\b\u00030\u0014H\u0002\u00a2\u0006\u0004\b0\u0010-J\u001b\u00101\u001a\u00020+2\n\u0010!\u001a\u0006\u0012\u0002\b\u00030\u0014H\u0002\u00a2\u0006\u0004\b1\u0010-J\u001b\u00102\u001a\u00020(2\n\u0010!\u001a\u0006\u0012\u0002\b\u00030\u0014H\u0002\u00a2\u0006\u0004\b2\u0010*J\u001b\u00103\u001a\u00020(2\n\u0010!\u001a\u0006\u0012\u0002\b\u00030\u0014H\u0002\u00a2\u0006\u0004\b3\u0010*J\u0017\u00106\u001a\u00020(2\u0006\u00105\u001a\u000204H\u0002\u00a2\u0006\u0004\b6\u00107J\u000f\u00108\u001a\u000204H\u0002\u00a2\u0006\u0004\b8\u00109J\u000f\u0010:\u001a\u000204H\u0002\u00a2\u0006\u0004\b:\u00109J\u0015\u0010;\u001a\b\u0012\u0004\u0012\u0002040\"H\u0002\u00a2\u0006\u0004\b;\u0010<J\u0017\u0010?\u001a\u0002042\u0006\u0010>\u001a\u00020=H\u0002\u00a2\u0006\u0004\b?\u0010@J\u0017\u0010B\u001a\u0002042\u0006\u0010A\u001a\u000204H\u0002\u00a2\u0006\u0004\bB\u0010CJ\u000f\u0010D\u001a\u00020(H\u0002\u00a2\u0006\u0004\bD\u0010EJ\u000f\u0010F\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bF\u0010\u0003J\u0017\u0010H\u001a\u00020\u00062\u0006\u0010G\u001a\u000204H\u0002\u00a2\u0006\u0004\bH\u0010IJ\u0017\u0010K\u001a\u00020\u00062\u0006\u0010J\u001a\u000204H\u0002\u00a2\u0006\u0004\bK\u0010IJ\u000f\u0010L\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bL\u0010\u0003J\u0017\u0010O\u001a\u00020\u00062\u0006\u0010N\u001a\u00020MH\u0002\u00a2\u0006\u0004\bO\u0010PJ\u0017\u0010Q\u001a\u00020\u00062\u0006\u0010G\u001a\u000204H\u0002\u00a2\u0006\u0004\bQ\u0010IR\u0014\u0010S\u001a\u00020R8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bS\u0010TR\u0014\u0010V\u001a\u00020U8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bV\u0010WR\u0014\u0010Y\u001a\u00020X8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bY\u0010ZR\u0014\u0010\\\u001a\u00020[8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\\\u0010]R\u0014\u0010^\u001a\u00020[8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b^\u0010]R\u0014\u0010_\u001a\u00020[8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b_\u0010]R\u0014\u0010`\u001a\u00020[8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b`\u0010]R\u0014\u0010a\u001a\u00020U8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\ba\u0010WR\u0014\u0010b\u001a\u00020U8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bb\u0010WR\u0016\u0010c\u001a\u00020M8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bc\u0010dR\u0016\u0010f\u001a\u00020e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bf\u0010gR\u0016\u0010h\u001a\u00020+8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bh\u0010iR\u0016\u0010j\u001a\u00020+8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bj\u0010iR\u0016\u0010k\u001a\u00020+8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bk\u0010iR\u0016\u0010l\u001a\u00020(8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bl\u0010mR\u0016\u0010n\u001a\u00020(8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bn\u0010mR\u0016\u0010o\u001a\u00020(8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bo\u0010mR\u0018\u0010p\u001a\u0004\u0018\u00010\u001a8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bp\u0010qR\u0016\u0010r\u001a\u00020(8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\br\u0010mR\u0011\u0010s\u001a\u00020(8F\u00a2\u0006\u0006\u001a\u0004\bs\u0010ER\u0014\u0010t\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bt\u0010iR\u0014\u0010u\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bu\u0010iR\u0014\u0010v\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bv\u0010iR\u0014\u0010w\u001a\u00020e8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bw\u0010gR\u0014\u0010x\u001a\u00020e8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bx\u0010gR\u0014\u0010y\u001a\u00020e8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\by\u0010gR\u0014\u0010z\u001a\u00020e8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bz\u0010gR\u0014\u0010{\u001a\u00020e8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b{\u0010gR\u0014\u0010|\u001a\u00020e8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b|\u0010gR\u0014\u0010}\u001a\u00020e8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b}\u0010gR\u0015\u0010\u007f\u001a\u00020~8\u0002X\u0082T\u00a2\u0006\u0007\n\u0005\b\u007f\u0010\u0080\u0001R\u0017\u0010\u0081\u0001\u001a\u00020~8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u0081\u0001\u0010\u0080\u0001\u00a8\u0006\u0084\u0001"}, d2={"Lorg/cobalt/internal/mining/AutoForgeModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "startRun", "handleWaitRoute", "Lnet/minecraft/class_1657;", "player", "handleOpenNpc", "(Lnet/minecraft/class_1657;)V", "handleSelectSlot", "handleSelectRecipe", "handleConfirmRecipe", "handleVerifyStarted", "handleComplete", "Lnet/minecraft/class_465;", "requireForgeScreen", "()Lnet/minecraft/class_465;", "Lnet/minecraft/class_1297;", "findForgeInteractionEntity", "(Lnet/minecraft/class_1657;)Lnet/minecraft/class_1297;", "Lnet/minecraft/class_2338;", "base", "findWalkTargetNear", "(Lnet/minecraft/class_2338;)Lnet/minecraft/class_2338;", "entity", "faceEntity", "(Lnet/minecraft/class_1297;)V", "screen", "", "Lorg/cobalt/internal/mining/AutoForgeModule$SlotText;", "getForgeSlotTexts", "(Lnet/minecraft/class_465;)Ljava/util/List;", "Lnet/minecraft/class_1735;", "getForgeCandidateSlots", "", "isForgeLikeScreen", "(Lnet/minecraft/class_465;)Z", "", "findClaimReadySlot", "(Lnet/minecraft/class_465;)I", "findEmptyForgeSlot", "findMaterialRecipeSlot", "findStartForgeSlot", "findNextPageSlot", "hasBusyForgeSlot", "hasStartedMaterial", "", "text", "isNavigationSlot", "(Ljava/lang/String;)Z", "normalizedNpcQuery", "()Ljava/lang/String;", "normalizedMaterialQuery", "materialTokens", "()Ljava/util/List;", "Lnet/minecraft/class_2561;", "component", "normalizeComponentText", "(Lnet/minecraft/class_2561;)Ljava/lang/String;", "raw", "normalizeText", "(Ljava/lang/String;)Ljava/lang/String;", "nativePathActive", "()Z", "stopNpcNavigation", "message", "finishSuccess", "(Ljava/lang/String;)V", "reason", "disable", "resetRuntime", "Lorg/cobalt/internal/mining/AutoForgeModule$State;", "next", "transition", "(Lorg/cobalt/internal/mining/AutoForgeModule$State;)V", "setStatus", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "info", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "statusText", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "routeNameText", "npcNameText", "materialText", "autoClaimReady", "closeOnDone", "state", "Lorg/cobalt/internal/mining/AutoForgeModule$State;", "", "stateTick", "J", "openAttempts", "I", "confirmAttempts", "recipePageTurns", "claimedReadyThisRun", "Z", "routeStartedByModule", "pendingUseRelease", "lastNpcPathTarget", "Lnet/minecraft/class_2338;", "npcPathOwned", "isRunning", "MAX_OPEN_ATTEMPTS", "MAX_CONFIRM_ATTEMPTS", "MAX_RECIPE_PAGE_TURNS", "NPC_LOOKUP_TIMEOUT_TICKS", "NON_FORGE_SCREEN_TIMEOUT_TICKS", "SLOT_SELECTION_TIMEOUT_TICKS", "RECIPE_SELECTION_TIMEOUT_TICKS", "CONFIRM_SELECTION_TIMEOUT_TICKS", "VERIFY_RETRY_TICKS", "VERIFY_START_TIMEOUT_TICKS", "", "NPC_INTERACT_DISTANCE_SQ", "D", "NPC_ANCHOR_ENTITY_RANGE_SQ", "State", "SlotText", "cobalt"})
@SourceDebugExtension(value={"SMAP\nAutoForgeModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AutoForgeModule.kt\norg/cobalt/internal/mining/AutoForgeModule\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,765:1\n1642#2,10:766\n1915#2:776\n1586#2:777\n1661#2,3:778\n777#2:781\n873#2,2:782\n1916#2:785\n1652#2:786\n832#2:787\n862#2,2:788\n1807#2,3:790\n296#2,2:793\n1807#2,3:795\n1807#2,3:798\n1586#2:801\n1661#2,3:802\n777#2:805\n873#2,2:806\n1#3:784\n1#3:808\n*S KotlinDebug\n*F\n+ 1 AutoForgeModule.kt\norg/cobalt/internal/mining/AutoForgeModule\n*L\n496#1:766,10\n496#1:776\n502#1:777\n502#1:778,3\n503#1:781\n503#1:782,2\n496#1:785\n496#1:786\n509#1:787\n509#1:788,2\n516#1:790,3\n641#1:793,2\n646#1:795,3\n657#1:798,3\n683#1:801\n683#1:802,3\n684#1:805\n684#1:806,2\n496#1:784\n*E\n"})
public final class AutoForgeModule
extends Module {
    @NotNull
    public static final AutoForgeModule INSTANCE = new AutoForgeModule();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final CheckboxSetting enabled;
    @NotNull
    private static final InfoSetting info;
    @NotNull
    private static final TextSetting statusText;
    @NotNull
    private static final TextSetting routeNameText;
    @NotNull
    private static final TextSetting npcNameText;
    @NotNull
    private static final TextSetting materialText;
    @NotNull
    private static final CheckboxSetting autoClaimReady;
    @NotNull
    private static final CheckboxSetting closeOnDone;
    @NotNull
    private static State state;
    private static long stateTick;
    private static int openAttempts;
    private static int confirmAttempts;
    private static int recipePageTurns;
    private static boolean claimedReadyThisRun;
    private static boolean routeStartedByModule;
    private static boolean pendingUseRelease;
    @Nullable
    private static class_2338 lastNpcPathTarget;
    private static boolean npcPathOwned;
    private static final int MAX_OPEN_ATTEMPTS = 8;
    private static final int MAX_CONFIRM_ATTEMPTS = 3;
    private static final int MAX_RECIPE_PAGE_TURNS = 6;
    private static final long NPC_LOOKUP_TIMEOUT_TICKS = 120L;
    private static final long NON_FORGE_SCREEN_TIMEOUT_TICKS = 30L;
    private static final long SLOT_SELECTION_TIMEOUT_TICKS = 50L;
    private static final long RECIPE_SELECTION_TIMEOUT_TICKS = 70L;
    private static final long CONFIRM_SELECTION_TIMEOUT_TICKS = 50L;
    private static final long VERIFY_RETRY_TICKS = 8L;
    private static final long VERIFY_START_TIMEOUT_TICKS = 40L;
    private static final double NPC_INTERACT_DISTANCE_SQ = 20.25;
    private static final double NPC_ANCHOR_ENTITY_RANGE_SQ = 16.0;

    private AutoForgeModule() {
        super("Auto Forge");
    }

    public final boolean isRunning() {
        return (Boolean)enabled.getValue() != false && state != State.IDLE;
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (pendingUseRelease) {
            class_304 class_3042 = AutoForgeModule.mc.field_1690.field_1904;
            if (class_3042 != null) {
                class_3042.method_23481(false);
            }
            pendingUseRelease = false;
        }
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            if (state != State.IDLE) {
                this.resetRuntime();
            }
            return;
        }
        class_746 class_7462 = AutoForgeModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        long l = stateTick;
        stateTick = l + 1L;
        switch (WhenMappings.$EnumSwitchMapping$0[state.ordinal()]) {
            case 1: {
                this.startRun();
                break;
            }
            case 2: {
                this.handleWaitRoute();
                break;
            }
            case 3: {
                this.handleOpenNpc((class_1657)player);
                break;
            }
            case 4: {
                this.handleSelectSlot();
                break;
            }
            case 5: {
                this.handleSelectRecipe();
                break;
            }
            case 6: {
                this.handleConfirmRecipe();
                break;
            }
            case 7: {
                this.handleVerifyStarted();
                break;
            }
            case 8: {
                this.handleComplete();
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
    }

    private final void startRun() {
        if (StringsKt.isBlank((CharSequence)this.normalizedMaterialQuery())) {
            this.disable("Set a forge material first.");
            return;
        }
        if (StringsKt.isBlank((CharSequence)this.normalizedNpcQuery())) {
            this.disable("Set a forge NPC first.");
            return;
        }
        openAttempts = 0;
        confirmAttempts = 0;
        recipePageTurns = 0;
        claimedReadyThisRun = false;
        routeStartedByModule = false;
        String routeName = ((Object)StringsKt.trim((CharSequence)((String)routeNameText.getValue()))).toString();
        if (((CharSequence)routeName).length() > 0) {
            boolean started = RoutesModule.INSTANCE.loadAndStartAutomationRoute(routeName, false, false, "forge automation");
            if (!started) {
                this.disable("Could not start forge route \"" + routeName + "\".");
                return;
            }
            routeStartedByModule = true;
            this.setStatus("Following forge route...");
            this.transition(State.WAIT_ROUTE);
            return;
        }
        this.setStatus("Opening forge NPC...");
        this.transition(State.OPEN_NPC);
    }

    private final void handleWaitRoute() {
        if (RoutesModule.INSTANCE.isRunning()) {
            this.setStatus("Following forge route...");
            return;
        }
        routeStartedByModule = false;
        this.setStatus("Opening forge NPC...");
        this.transition(State.OPEN_NPC);
    }

    private final void handleOpenNpc(class_1657 player) {
        class_465 screen;
        class_437 class_4372 = AutoForgeModule.mc.field_1755;
        class_465 class_4652 = screen = class_4372 instanceof class_465 ? (class_465)class_4372 : null;
        if (screen != null) {
            this.stopNpcNavigation();
            if (this.isForgeLikeScreen(screen)) {
                this.setStatus("Forge menu opened.");
                this.transition(State.SELECT_SLOT);
                return;
            }
            if (stateTick > 30L) {
                this.disable("Opened non-forge menu \"" + screen.method_25440().getString() + "\".");
            }
            return;
        }
        class_1297 npc = this.findForgeInteractionEntity(player);
        if (npc == null) {
            if (stateTick > 120L) {
                this.disable("Could not find forge NPC \"" + ((Object)StringsKt.trim((CharSequence)((String)npcNameText.getValue()))).toString() + "\" nearby.");
            } else {
                this.setStatus("Looking for " + ((Object)StringsKt.trim((CharSequence)((String)npcNameText.getValue()))).toString() + "...");
            }
            return;
        }
        if (player.method_5858(npc) > 20.25) {
            class_2338 class_23382 = npc.method_24515();
            Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"blockPosition(...)");
            class_2338 walkTarget = this.findWalkTargetNear(class_23382);
            if (!Intrinsics.areEqual((Object)lastNpcPathTarget, (Object)walkTarget) || !this.nativePathActive() || stateTick % 40L == 1L) {
                PathfindingModule.INSTANCE.ensureEnabledForAutomation("auto forge");
                PathfindingModule.INSTANCE.startTo((double)walkTarget.method_10263() + 0.5, walkTarget.method_10264(), (double)walkTarget.method_10260() + 0.5);
                lastNpcPathTarget = walkTarget;
                npcPathOwned = true;
            }
            if (stateTick > 120L) {
                this.disable("Reached the forge route but " + ((Object)StringsKt.trim((CharSequence)((String)npcNameText.getValue()))).toString() + " is still out of reach.");
            } else {
                this.setStatus("Walking to " + ((Object)StringsKt.trim((CharSequence)((String)npcNameText.getValue()))).toString() + "...");
            }
            return;
        }
        this.stopNpcNavigation();
        this.faceEntity(npc);
        if (stateTick == 1L) {
            this.setStatus("Facing " + ((Object)StringsKt.trim((CharSequence)((String)npcNameText.getValue()))).toString() + "...");
            return;
        }
        if (stateTick % 4L != 0L) {
            return;
        }
        if (openAttempts >= 8) {
            this.disable("Could not open " + ((Object)StringsKt.trim((CharSequence)((String)npcNameText.getValue()))).toString() + " after 8 attempts.");
            return;
        }
        class_304 class_3042 = AutoForgeModule.mc.field_1690.field_1904;
        if (class_3042 != null) {
            class_3042.method_23481(false);
        }
        class_304 class_3043 = AutoForgeModule.mc.field_1690.field_1904;
        if (class_3043 != null) {
            class_3043.method_23481(true);
        }
        pendingUseRelease = true;
        int n = openAttempts;
        openAttempts = n + 1;
        this.setStatus("Opening " + ((Object)StringsKt.trim((CharSequence)((String)npcNameText.getValue()))).toString() + "... (attempt " + openAttempts + ")");
    }

    private final void handleSelectSlot() {
        int claimSlot;
        class_465<?> class_4652 = this.requireForgeScreen();
        if (class_4652 == null) {
            return;
        }
        class_465<?> screen = class_4652;
        if (stateTick < 3L || stateTick % 3L != 0L) {
            return;
        }
        if (((Boolean)autoClaimReady.getValue()).booleanValue() && !claimedReadyThisRun && (claimSlot = this.findClaimReadySlot(screen)) >= 0) {
            InventoryUtils.clickSlot$default(claimSlot, null, null, 6, null);
            claimedReadyThisRun = true;
            this.setStatus("Claiming ready forge slot...");
            this.transition(State.SELECT_SLOT);
            return;
        }
        if (this.findStartForgeSlot(screen) >= 0) {
            this.transition(State.CONFIRM_RECIPE);
            return;
        }
        if (this.findMaterialRecipeSlot(screen) >= 0) {
            this.transition(State.SELECT_RECIPE);
            return;
        }
        int emptySlot = this.findEmptyForgeSlot(screen);
        if (emptySlot >= 0) {
            InventoryUtils.clickSlot$default(emptySlot, null, null, 6, null);
            this.setStatus("Opening empty forge slot...");
            this.transition(State.SELECT_RECIPE);
            return;
        }
        if (stateTick >= 50L && this.hasBusyForgeSlot(screen)) {
            this.disable("No free forge slots were available.");
            return;
        }
        if (stateTick >= 50L) {
            this.disable("Could not find an empty forge slot.");
        }
    }

    private final void handleSelectRecipe() {
        class_465<?> class_4652 = this.requireForgeScreen();
        if (class_4652 == null) {
            return;
        }
        class_465<?> screen = class_4652;
        if (stateTick < 3L || stateTick % 3L != 0L) {
            return;
        }
        if (this.findStartForgeSlot(screen) >= 0) {
            this.transition(State.CONFIRM_RECIPE);
            return;
        }
        int recipeSlot = this.findMaterialRecipeSlot(screen);
        if (recipeSlot >= 0) {
            InventoryUtils.clickSlot$default(recipeSlot, null, null, 6, null);
            this.setStatus("Selecting " + ((Object)StringsKt.trim((CharSequence)((String)materialText.getValue()))).toString() + "...");
            this.transition(State.CONFIRM_RECIPE);
            return;
        }
        int nextPageSlot = this.findNextPageSlot(screen);
        if (nextPageSlot >= 0 && recipePageTurns < 6) {
            InventoryUtils.clickSlot$default(nextPageSlot, null, null, 6, null);
            int n = recipePageTurns;
            recipePageTurns = n + 1;
            this.setStatus("Searching recipes... (page " + (recipePageTurns + 1) + ")");
            this.transition(State.SELECT_RECIPE);
            return;
        }
        if (this.findEmptyForgeSlot(screen) >= 0) {
            this.transition(State.SELECT_SLOT);
            return;
        }
        if (stateTick >= 70L) {
            this.disable("Could not find recipe \"" + ((Object)StringsKt.trim((CharSequence)((String)materialText.getValue()))).toString() + "\".");
        }
    }

    private final void handleConfirmRecipe() {
        class_465<?> class_4652 = this.requireForgeScreen();
        if (class_4652 == null) {
            return;
        }
        class_465<?> screen = class_4652;
        if (stateTick < 3L || stateTick % 3L != 0L) {
            return;
        }
        int startSlot = this.findStartForgeSlot(screen);
        if (startSlot >= 0) {
            InventoryUtils.clickSlot$default(startSlot, null, null, 6, null);
            confirmAttempts = 1;
            this.setStatus("Starting " + ((Object)StringsKt.trim((CharSequence)((String)materialText.getValue()))).toString() + "...");
            this.transition(State.VERIFY_STARTED);
            return;
        }
        if (this.findMaterialRecipeSlot(screen) >= 0) {
            this.transition(State.SELECT_RECIPE);
            return;
        }
        if (this.findEmptyForgeSlot(screen) >= 0) {
            this.transition(State.SELECT_SLOT);
            return;
        }
        if (stateTick >= 50L) {
            this.disable("Could not confirm recipe \"" + ((Object)StringsKt.trim((CharSequence)((String)materialText.getValue()))).toString() + "\".");
        }
    }

    private final void handleVerifyStarted() {
        int startSlot;
        class_465 screen;
        class_437 class_4372 = AutoForgeModule.mc.field_1755;
        class_465 class_4652 = screen = class_4372 instanceof class_465 ? (class_465)class_4372 : null;
        if (screen == null) {
            this.finishSuccess("Started " + ((Object)StringsKt.trim((CharSequence)((String)materialText.getValue()))).toString() + ".");
            return;
        }
        if (!this.isForgeLikeScreen(screen) && stateTick > 30L) {
            this.finishSuccess("Started " + ((Object)StringsKt.trim((CharSequence)((String)materialText.getValue()))).toString() + ".");
            return;
        }
        if (this.hasStartedMaterial(screen)) {
            this.finishSuccess("Started " + ((Object)StringsKt.trim((CharSequence)((String)materialText.getValue()))).toString() + ".");
            return;
        }
        if (stateTick >= 8L && (startSlot = this.findStartForgeSlot(screen)) >= 0 && confirmAttempts < 3) {
            InventoryUtils.clickSlot$default(startSlot, null, null, 6, null);
            int n = confirmAttempts;
            confirmAttempts = n + 1;
            this.setStatus("Retrying forge start... (attempt " + confirmAttempts + ")");
            this.transition(State.VERIFY_STARTED);
            return;
        }
        if (stateTick >= 40L) {
            this.disable("Forge start did not confirm for " + ((Object)StringsKt.trim((CharSequence)((String)materialText.getValue()))).toString() + ".");
        }
    }

    private final void handleComplete() {
        block1: {
            if (stateTick != 1L || !((Boolean)closeOnDone.getValue()).booleanValue()) break block1;
            class_746 class_7462 = AutoForgeModule.mc.field_1724;
            if (class_7462 != null) {
                class_7462.method_7346();
            }
        }
    }

    private final class_465<?> requireForgeScreen() {
        class_465 screen;
        class_437 class_4372 = AutoForgeModule.mc.field_1755;
        class_465 class_4652 = screen = class_4372 instanceof class_465 ? (class_465)class_4372 : null;
        if (screen == null) {
            if (state == State.VERIFY_STARTED) {
                this.finishSuccess("Started " + ((Object)StringsKt.trim((CharSequence)((String)materialText.getValue()))).toString() + ".");
            } else {
                this.setStatus("Reopening forge NPC...");
                openAttempts = 0;
                this.transition(State.OPEN_NPC);
            }
            return null;
        }
        if (!this.isForgeLikeScreen(screen) && stateTick >= 30L) {
            this.disable("Opened non-forge menu \"" + screen.method_25440().getString() + "\".");
            return null;
        }
        return screen;
    }

    private final class_1297 findForgeInteractionEntity(class_1657 player) {
        class_638 class_6382 = AutoForgeModule.mc.field_1687;
        if (class_6382 == null) {
            return null;
        }
        class_638 level2 = class_6382;
        String targetName = this.normalizedNpcQuery();
        if (StringsKt.isBlank((CharSequence)targetName)) {
            return null;
        }
        Iterable iterable = level2.method_18112();
        Intrinsics.checkNotNullExpressionValue((Object)iterable, (String)"entitiesForRendering(...)");
        Function1[] function1Array = new Function1[]{AutoForgeModule::findForgeInteractionEntity$lambda$2, arg_0 -> AutoForgeModule.findForgeInteractionEntity$lambda$3(player, arg_0)};
        class_1297 class_12972 = (class_1297)SequencesKt.firstOrNull((Sequence)SequencesKt.sortedWith((Sequence)SequencesKt.filter((Sequence)SequencesKt.filter((Sequence)CollectionsKt.asSequence((Iterable)iterable), arg_0 -> AutoForgeModule.findForgeInteractionEntity$lambda$0(player, arg_0)), arg_0 -> AutoForgeModule.findForgeInteractionEntity$lambda$1(targetName, arg_0)), (Comparator)ComparisonsKt.compareBy((Function1[])function1Array)));
        if (class_12972 == null) {
            return null;
        }
        class_1297 anchor = class_12972;
        if (!(anchor instanceof class_1531)) {
            return anchor;
        }
        Iterable iterable2 = level2.method_18112();
        Intrinsics.checkNotNullExpressionValue((Object)iterable2, (String)"entitiesForRendering(...)");
        function1Array = new Function1[]{AutoForgeModule::findForgeInteractionEntity$lambda$5, arg_0 -> AutoForgeModule.findForgeInteractionEntity$lambda$6(anchor, arg_0)};
        class_1297 class_12973 = (class_1297)SequencesKt.firstOrNull((Sequence)SequencesKt.sortedWith((Sequence)SequencesKt.filter((Sequence)CollectionsKt.asSequence((Iterable)iterable2), arg_0 -> AutoForgeModule.findForgeInteractionEntity$lambda$4(player, anchor, arg_0)), (Comparator)ComparisonsKt.compareBy((Function1[])function1Array)));
        if (class_12973 == null) {
            class_12973 = anchor;
        }
        return class_12973;
    }

    private final class_2338 findWalkTargetNear(class_2338 base) {
        class_638 class_6382 = AutoForgeModule.mc.field_1687;
        if (class_6382 == null) {
            return base;
        }
        class_638 level2 = class_6382;
        if (MinecraftPathingRules.INSTANCE.isWalkable((class_1937)level2, base)) {
            return base;
        }
        for (int radius = 1; radius < 4; ++radius) {
            block1: for (int dy = -2; dy < 3; ++dy) {
                int dx = -radius;
                if (dx > radius) continue;
                while (true) {
                    int dz;
                    if ((dz = -radius) <= radius) {
                        while (true) {
                            class_2338 candidate;
                            Intrinsics.checkNotNullExpressionValue((Object)base.method_10069(dx, dy, dz), (String)"offset(...)");
                            if (MinecraftPathingRules.INSTANCE.isWalkable((class_1937)level2, candidate)) {
                                return candidate;
                            }
                            if (dz == radius) break;
                            ++dz;
                        }
                    }
                    if (dx == radius) continue block1;
                    ++dx;
                }
            }
        }
        return base;
    }

    private final void faceEntity(class_1297 entity) {
        class_746 class_7462 = AutoForgeModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        Rotation rotation = AngleUtils.INSTANCE.getRotation(entity);
        player.method_36456(rotation.getYaw());
        player.method_36457(rotation.getPitch());
        player.field_6241 = rotation.getYaw();
        player.field_6283 = rotation.getYaw();
    }

    /*
     * WARNING - void declaration
     */
    private final List<SlotText> getForgeSlotTexts(class_465<?> screen) {
        void $this$mapNotNullTo$iv$iv;
        Iterable $this$mapNotNull$iv = this.getForgeCandidateSlots(screen);
        boolean $i$f$mapNotNull = false;
        Iterable iterable = $this$mapNotNull$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$mapNotNullTo = false;
        void $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
        boolean $i$f$forEach = false;
        Iterator iterator = $this$forEach$iv$iv$iv.iterator();
        while (iterator.hasNext()) {
            SlotText slotText;
            Object element$iv$iv$iv;
            Object element$iv$iv = element$iv$iv$iv = iterator.next();
            boolean bl = false;
            class_1735 slot = (class_1735)element$iv$iv;
            boolean bl2 = false;
            if (!slot.method_7681()) {
                slotText = null;
            } else {
                String string = slot.method_7677().method_7964().getString();
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
                String name = INSTANCE.normalizeText(string);
                if (StringsKt.isBlank((CharSequence)name)) {
                    slotText = null;
                } else {
                    void $this$filterTo$iv$iv;
                    void $this$filter$iv;
                    void $this$mapTo$iv$iv;
                    Iterable $this$map$iv;
                    class_1799 class_17992 = slot.method_7677();
                    Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getItem(...)");
                    Iterable iterable2 = ItemUtilsKt.getLoreLines(class_17992);
                    AutoForgeModule autoForgeModule = INSTANCE;
                    boolean $i$f$map22 = false;
                    void var19_21 = $this$map$iv;
                    Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
                    boolean $i$f$mapTo = false;
                    for (Object item$iv$iv : $this$mapTo$iv$iv) {
                        void p0;
                        class_2561 class_25612 = (class_2561)item$iv$iv;
                        Collection collection = destination$iv$iv2;
                        boolean bl3 = false;
                        collection.add(autoForgeModule.normalizeComponentText((class_2561)p0));
                    }
                    $this$map$iv = (List)destination$iv$iv2;
                    boolean $i$f$filter = false;
                    void $i$f$map22 = $this$filter$iv;
                    Collection destination$iv$iv3 = new ArrayList();
                    boolean $i$f$filterTo = false;
                    for (Object element$iv$iv2 : $this$filterTo$iv$iv) {
                        String it = (String)element$iv$iv2;
                        boolean bl4 = false;
                        boolean bl5 = !StringsKt.isBlank((CharSequence)it);
                        if (!bl5) continue;
                        destination$iv$iv3.add(element$iv$iv2);
                    }
                    List lore = (List)destination$iv$iv3;
                    slotText = new SlotText(slot.field_7874, name, CollectionsKt.joinToString$default((Iterable)CollectionsKt.plus((Collection)CollectionsKt.listOf((Object)name), (Iterable)lore), (CharSequence)"\n", null, null, (int)0, null, null, (int)62, null));
                }
            }
            if (slotText == null) continue;
            SlotText it$iv$iv = slotText;
            boolean bl6 = false;
            destination$iv$iv.add(it$iv$iv);
        }
        return (List)destination$iv$iv;
    }

    /*
     * WARNING - void declaration
     */
    private final List<class_1735> getForgeCandidateSlots(class_465<?> screen) {
        List list;
        void $this$filterNotTo$iv$iv;
        class_2371 class_23712 = screen.method_17577().field_7761;
        Intrinsics.checkNotNullExpressionValue((Object)class_23712, (String)"slots");
        Iterable $this$filterNot$iv = (Iterable)class_23712;
        boolean $i$f$filterNot = false;
        Iterable iterable = $this$filterNot$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterNotTo = false;
        for (Object element$iv$iv : $this$filterNotTo$iv$iv) {
            class_1735 it = (class_1735)element$iv$iv;
            boolean bl = false;
            if (it.field_7871 instanceof class_1661) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        List containerSlots = (List)destination$iv$iv;
        if (!((Collection)containerSlots).isEmpty()) {
            list = containerSlots;
        } else {
            class_2371 class_23713 = screen.method_17577().field_7761;
            Intrinsics.checkNotNullExpressionValue((Object)class_23713, (String)"slots");
            list = (List)class_23713;
        }
        return list;
    }

    private final boolean isForgeLikeScreen(class_465<?> screen) {
        boolean bl;
        block4: {
            String string = screen.method_25440().getString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
            String title = this.normalizeText(string);
            if (StringsKt.contains$default((CharSequence)title, (CharSequence)"forge", (boolean)false, (int)2, null)) {
                return true;
            }
            Iterable $this$any$iv = this.getForgeSlotTexts(screen);
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                bl = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    SlotText text = (SlotText)element$iv;
                    boolean bl2 = false;
                    if (!(StringsKt.contains$default((CharSequence)text.getCombined(), (CharSequence)"forge", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)text.getCombined(), (CharSequence)"empty forge slot", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)text.getCombined(), (CharSequence)"click to forge", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)text.getCombined(), (CharSequence)"ready to collect", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)text.getCombined(), (CharSequence)"time remaining", (boolean)false, (int)2, null))) continue;
                    bl = true;
                    break block4;
                }
                bl = false;
            }
        }
        return bl;
    }

    private final int findClaimReadySlot(class_465<?> screen) {
        int bestIndex = -1;
        int bestScore = Integer.MIN_VALUE;
        for (SlotText slot : this.getForgeSlotTexts(screen)) {
            int score = StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"claim item", (boolean)false, (int)2, null) ? 200 : (StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"click to claim", (boolean)false, (int)2, null) ? 180 : (StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"ready to collect", (boolean)false, (int)2, null) ? 170 : (StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"click to collect", (boolean)false, (int)2, null) ? 160 : (StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"item is ready", (boolean)false, (int)2, null) ? 150 : Integer.MIN_VALUE))));
            if (score <= bestScore) continue;
            bestScore = score;
            bestIndex = slot.getIndex();
        }
        return bestScore > 0 ? bestIndex : -1;
    }

    private final int findEmptyForgeSlot(class_465<?> screen) {
        int bestIndex = -1;
        int bestScore = Integer.MIN_VALUE;
        for (SlotText slot : this.getForgeSlotTexts(screen)) {
            if (this.isNavigationSlot(slot.getCombined())) continue;
            int score = StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"empty forge slot", (boolean)false, (int)2, null) ? 220 : (StringsKt.contains$default((CharSequence)slot.getName(), (CharSequence)"empty slot", (boolean)false, (int)2, null) ? 210 : (StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"forge item", (boolean)false, (int)2, null) && StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"slot", (boolean)false, (int)2, null) ? 190 : (StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"click to forge", (boolean)false, (int)2, null) && StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"slot", (boolean)false, (int)2, null) ? 180 : Integer.MIN_VALUE)));
            if (score <= bestScore) continue;
            bestScore = score;
            bestIndex = slot.getIndex();
        }
        return bestScore > 0 ? bestIndex : -1;
    }

    private final int findMaterialRecipeSlot(class_465<?> screen) {
        String query = this.normalizedMaterialQuery();
        List<String> tokens = this.materialTokens();
        if (StringsKt.isBlank((CharSequence)query) || tokens.isEmpty()) {
            return -1;
        }
        int bestIndex = -1;
        int bestScore = Integer.MIN_VALUE;
        for (SlotText slot : this.getForgeSlotTexts(screen)) {
            if (this.isNavigationSlot(slot.getCombined()) || StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"empty forge slot", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"claim item", (boolean)false, (int)2, null)) continue;
            int score = 0;
            if (StringsKt.contains$default((CharSequence)slot.getName(), (CharSequence)query, (boolean)false, (int)2, null)) {
                score += 180;
            }
            if (StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)query, (boolean)false, (int)2, null)) {
                score += 120;
            }
            boolean matchedAll = true;
            for (String token : tokens) {
                if (StringsKt.contains$default((CharSequence)slot.getName(), (CharSequence)token, (boolean)false, (int)2, null)) {
                    score += 45;
                    continue;
                }
                if (StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)token, (boolean)false, (int)2, null)) {
                    score += 20;
                    continue;
                }
                matchedAll = false;
                break;
            }
            if (!matchedAll) continue;
            if (StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"click to forge", (boolean)false, (int)2, null)) {
                score += 20;
            }
            if (StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"not unlocked", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"coming soon", (boolean)false, (int)2, null)) {
                score -= 200;
            }
            if (score <= bestScore) continue;
            bestScore = score;
            bestIndex = slot.getIndex();
        }
        return bestScore > 0 ? bestIndex : -1;
    }

    private final int findStartForgeSlot(class_465<?> screen) {
        String query = this.normalizedMaterialQuery();
        int bestIndex = -1;
        int bestScore = Integer.MIN_VALUE;
        for (SlotText slot : this.getForgeSlotTexts(screen)) {
            if (this.isNavigationSlot(slot.getCombined())) continue;
            int score = StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"click to forge", (boolean)false, (int)2, null) && !StringsKt.isBlank((CharSequence)query) && StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)query, (boolean)false, (int)2, null) ? 180 : (StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"start forge", (boolean)false, (int)2, null) ? 170 : (StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"begin forging", (boolean)false, (int)2, null) ? 160 : (StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"start forging", (boolean)false, (int)2, null) ? 150 : (StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"click to start", (boolean)false, (int)2, null) ? 140 : (StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"confirm forge", (boolean)false, (int)2, null) ? 130 : Integer.MIN_VALUE)))));
            if (score == Integer.MIN_VALUE) continue;
            if (!StringsKt.isBlank((CharSequence)query) && StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)query, (boolean)false, (int)2, null)) {
                score += 20;
            }
            if (StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"not enough", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"insufficient", (boolean)false, (int)2, null)) {
                score -= 160;
            }
            if (score <= bestScore) continue;
            bestScore = score;
            bestIndex = slot.getIndex();
        }
        return bestScore > 0 ? bestIndex : -1;
    }

    private final int findNextPageSlot(class_465<?> screen) {
        Object v0;
        block1: {
            Iterable $this$firstOrNull$iv = this.getForgeSlotTexts(screen);
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                SlotText slot = (SlotText)element$iv;
                boolean bl = false;
                if (!(StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"next page", (boolean)false, (int)2, null) || Intrinsics.areEqual((Object)slot.getName(), (Object)"next"))) continue;
                v0 = element$iv;
                break block1;
            }
            v0 = null;
        }
        SlotText slotText = v0;
        return slotText != null ? slotText.getIndex() : -1;
    }

    private final boolean hasBusyForgeSlot(class_465<?> screen) {
        boolean bl;
        block3: {
            Iterable $this$any$iv = this.getForgeSlotTexts(screen);
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                bl = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    SlotText slot = (SlotText)element$iv;
                    boolean bl2 = false;
                    if (!(StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"time remaining", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"ready to collect", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"collect in", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"ready in", (boolean)false, (int)2, null))) continue;
                    bl = true;
                    break block3;
                }
                bl = false;
            }
        }
        return bl;
    }

    private final boolean hasStartedMaterial(class_465<?> screen) {
        boolean bl;
        block4: {
            String query = this.normalizedMaterialQuery();
            if (StringsKt.isBlank((CharSequence)query)) {
                return false;
            }
            Iterable $this$any$iv = this.getForgeSlotTexts(screen);
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                bl = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    SlotText slot = (SlotText)element$iv;
                    boolean bl2 = false;
                    boolean bl3 = StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)query, (boolean)false, (int)2, null) && (StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"time remaining", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"collect in", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"ready in", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)slot.getCombined(), (CharSequence)"ready to collect", (boolean)false, (int)2, null));
                    if (!bl3) continue;
                    bl = true;
                    break block4;
                }
                bl = false;
            }
        }
        return bl;
    }

    private final boolean isNavigationSlot(String text) {
        return StringsKt.contains$default((CharSequence)text, (CharSequence)"close", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)text, (CharSequence)"go back", (boolean)false, (int)2, null) || Intrinsics.areEqual((Object)text, (Object)"back") || StringsKt.contains$default((CharSequence)text, (CharSequence)"next page", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)text, (CharSequence)"previous page", (boolean)false, (int)2, null);
    }

    private final String normalizedNpcQuery() {
        return this.normalizeText((String)npcNameText.getValue());
    }

    private final String normalizedMaterialQuery() {
        return this.normalizeText((String)materialText.getValue());
    }

    /*
     * WARNING - void declaration
     */
    private final List<String> materialTokens() {
        void $this$filterTo$iv$iv;
        String it;
        Iterable $this$mapTo$iv$iv;
        char[] cArray = new char[]{' '};
        Iterable $this$map$iv = StringsKt.split$default((CharSequence)this.normalizedMaterialQuery(), (char[])cArray, (boolean)false, (int)0, (int)6, null);
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            String string = (String)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(((Object)StringsKt.trim((CharSequence)it)).toString());
        }
        Iterable $this$filter$iv = (List)destination$iv$iv;
        boolean $i$f$filter = false;
        $this$mapTo$iv$iv = $this$filter$iv;
        destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            it = (String)element$iv$iv;
            boolean bl = false;
            if (!(it.length() >= 2)) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        return (List)destination$iv$iv;
    }

    private final String normalizeComponentText(class_2561 component) {
        String string = component.getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        return this.normalizeText(string);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private final String normalizeText(String raw) {
        String string = class_124.method_539((String)raw);
        if (string == null) return "";
        String string2 = string;
        Locale locale = Locale.US;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"US");
        String string3 = string2.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
        String string4 = string3;
        if (string4 == null) return "";
        string2 = StringsKt.replace$default((String)string4, (String)"&", (String)" and ", (boolean)false, (int)4, null);
        if (string2 == null) return "";
        Regex regex = new Regex("[^a-z0-9' ]");
        CharSequence charSequence = string2;
        String string5 = " ";
        String string6 = regex.replace(charSequence, string5);
        if (string6 == null) return "";
        string5 = new Regex("\\s+");
        CharSequence charSequence2 = string6;
        String string7 = " ";
        charSequence = string5.replace(charSequence2, string7);
        if (charSequence == null) return "";
        String string8 = ((Object)StringsKt.trim((CharSequence)charSequence)).toString();
        String string9 = string8;
        if (string8 != null) return string9;
        return "";
    }

    private final boolean nativePathActive() {
        PathStatus it = NativePathfinder.INSTANCE.getStatus();
        boolean bl = false;
        return it != PathStatus.IDLE && it != PathStatus.ARRIVED && it != PathStatus.FAILED;
    }

    private final void stopNpcNavigation() {
        if (npcPathOwned && this.nativePathActive()) {
            PathfindingModule.INSTANCE.stopPath();
        }
        lastNpcPathTarget = null;
        npcPathOwned = false;
    }

    private final void finishSuccess(String message) {
        routeStartedByModule = false;
        this.setStatus(message);
        ChatUtils.sendMessage("Auto Forge: " + message);
        this.transition(State.COMPLETE);
    }

    private final void disable(String reason) {
        this.setStatus(reason);
        ChatUtils.sendMessage("Auto Forge: " + reason + " Disabling.");
        enabled.setValue(false);
    }

    private final void resetRuntime() {
        if (routeStartedByModule && RoutesModule.INSTANCE.isRunning()) {
            RoutesModule.stopForAutomation$default(RoutesModule.INSTANCE, null, 1, null);
        }
        routeStartedByModule = false;
        this.stopNpcNavigation();
        pendingUseRelease = false;
        openAttempts = 0;
        confirmAttempts = 0;
        recipePageTurns = 0;
        claimedReadyThisRun = false;
        class_304 class_3042 = AutoForgeModule.mc.field_1690.field_1904;
        if (class_3042 != null) {
            class_3042.method_23481(false);
        }
        state = State.IDLE;
        stateTick = 0L;
        statusText.setValue("Idle");
    }

    private final void transition(State next) {
        state = next;
        stateTick = 0L;
    }

    private final void setStatus(String message) {
        statusText.setValue(message);
    }

    private static final boolean findForgeInteractionEntity$lambda$0(class_1657 $player, class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return !Intrinsics.areEqual((Object)it, (Object)$player);
    }

    private static final boolean findForgeInteractionEntity$lambda$1(String $targetName, class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        String string = it.method_5477().getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        return StringsKt.contains$default((CharSequence)INSTANCE.normalizeText(string), (CharSequence)$targetName, (boolean)false, (int)2, null);
    }

    private static final Comparable findForgeInteractionEntity$lambda$2(class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it instanceof class_1531 ? (Comparable)Integer.valueOf(1) : (Comparable)Integer.valueOf(0);
    }

    private static final Comparable findForgeInteractionEntity$lambda$3(class_1657 $player, class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return Double.valueOf($player.method_5858(it));
    }

    private static final boolean findForgeInteractionEntity$lambda$4(class_1657 $player, class_1297 $anchor, class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return !Intrinsics.areEqual((Object)it, (Object)$player) && !(it instanceof class_1531) && it.method_5858($anchor) <= 16.0;
    }

    private static final Comparable findForgeInteractionEntity$lambda$5(class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it instanceof class_1657 ? (Comparable)Integer.valueOf(0) : (Comparable)Integer.valueOf(1);
    }

    private static final Comparable findForgeInteractionEntity$lambda$6(class_1297 $anchor, class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return Double.valueOf($anchor.method_5858(it));
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        enabled = new CheckboxSetting("Enabled", "Walk a saved forge route, open a forge NPC, and start the configured material.", false);
        info = new InfoSetting("How It Works", "Starts a saved forge route once, opens the configured NPC, claims one ready slot if allowed, then starts the configured material.", InfoType.INFO);
        statusText = new TextSetting("Status", "Current auto forge state.", "Idle");
        Setting setting = new TextSetting("Forge Route", "Saved route used to walk to the forge NPC. Leave blank to start from your current position.", "");
        TextSetting $this$routeNameText_u24lambda_u240 = setting;
        boolean bl = false;
        $this$routeNameText_u24lambda_u240.setUiGroup("__side__");
        routeNameText = setting;
        setting = new TextSetting("Forge NPC", "Partial NPC name to open once the route finishes.", "forger");
        TextSetting $this$npcNameText_u24lambda_u240 = setting;
        boolean bl2 = false;
        $this$npcNameText_u24lambda_u240.setUiGroup("__side__");
        npcNameText = setting;
        setting = new TextSetting("Material", "Material name or unique keyword to start in the forge.", "");
        TextSetting $this$materialText_u24lambda_u240 = setting;
        boolean bl3 = false;
        $this$materialText_u24lambda_u240.setUiGroup("__side__");
        materialText = setting;
        Setting $this$autoClaimReady_u24lambda_u240 = setting = new CheckboxSetting("Auto Claim Ready", "Claim one ready forge slot before starting the new material.", true);
        boolean bl4 = false;
        $this$autoClaimReady_u24lambda_u240.setUiGroup("__side__");
        autoClaimReady = setting;
        Setting $this$closeOnDone_u24lambda_u240 = setting = new CheckboxSetting("Close On Done", "Close the forge GUI after the material is started.", true);
        boolean bl5 = false;
        $this$closeOnDone_u24lambda_u240.setUiGroup("__side__");
        closeOnDone = setting;
        state = State.IDLE;
        setting = new Setting[]{enabled, info, statusText, routeNameText, npcNameText, materialText, autoClaimReady, closeOnDone};
        INSTANCE.addSetting(setting);
        EventBus.register(INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\n\b\u0082\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\fJ.\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u001b\u0010\u0012\u001a\u00020\u00112\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0011\u0010\u0014\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0014\u0010\nJ\u0011\u0010\u0015\u001a\u00020\u0004H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0015\u0010\fR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0016\u001a\u0004\b\u0017\u0010\nR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0018\u001a\u0004\b\u0019\u0010\fR\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u0018\u001a\u0004\b\u001a\u0010\f\u00a8\u0006\u001b"}, d2={"Lorg/cobalt/internal/mining/AutoForgeModule$SlotText;", "", "", "index", "", "name", "combined", "<init>", "(ILjava/lang/String;Ljava/lang/String;)V", "component1", "()I", "component2", "()Ljava/lang/String;", "component3", "copy", "(ILjava/lang/String;Ljava/lang/String;)Lorg/cobalt/internal/mining/AutoForgeModule$SlotText;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "toString", "I", "getIndex", "Ljava/lang/String;", "getName", "getCombined", "cobalt"})
    private static final class SlotText {
        private final int index;
        @NotNull
        private final String name;
        @NotNull
        private final String combined;

        public SlotText(int index, @NotNull String name, @NotNull String combined) {
            Intrinsics.checkNotNullParameter((Object)name, (String)"name");
            Intrinsics.checkNotNullParameter((Object)combined, (String)"combined");
            this.index = index;
            this.name = name;
            this.combined = combined;
        }

        public final int getIndex() {
            return this.index;
        }

        @NotNull
        public final String getName() {
            return this.name;
        }

        @NotNull
        public final String getCombined() {
            return this.combined;
        }

        public final int component1() {
            return this.index;
        }

        @NotNull
        public final String component2() {
            return this.name;
        }

        @NotNull
        public final String component3() {
            return this.combined;
        }

        @NotNull
        public final SlotText copy(int index, @NotNull String name, @NotNull String combined) {
            Intrinsics.checkNotNullParameter((Object)name, (String)"name");
            Intrinsics.checkNotNullParameter((Object)combined, (String)"combined");
            return new SlotText(index, name, combined);
        }

        public static /* synthetic */ SlotText copy$default(SlotText slotText, int n, String string, String string2, int n2, Object object) {
            if ((n2 & 1) != 0) {
                n = slotText.index;
            }
            if ((n2 & 2) != 0) {
                string = slotText.name;
            }
            if ((n2 & 4) != 0) {
                string2 = slotText.combined;
            }
            return slotText.copy(n, string, string2);
        }

        @NotNull
        public String toString() {
            return "SlotText(index=" + this.index + ", name=" + this.name + ", combined=" + this.combined + ")";
        }

        public int hashCode() {
            int result = Integer.hashCode(this.index);
            result = result * 31 + this.name.hashCode();
            result = result * 31 + this.combined.hashCode();
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof SlotText)) {
                return false;
            }
            SlotText slotText = (SlotText)other;
            if (this.index != slotText.index) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.name, (Object)slotText.name)) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.combined, (Object)slotText.combined);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u000b\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000b\u00a8\u0006\f"}, d2={"Lorg/cobalt/internal/mining/AutoForgeModule$State;", "", "<init>", "(Ljava/lang/String;I)V", "IDLE", "WAIT_ROUTE", "OPEN_NPC", "SELECT_SLOT", "SELECT_RECIPE", "CONFIRM_RECIPE", "VERIFY_STARTED", "COMPLETE", "cobalt"})
    private static final class State
    extends Enum<State> {
        public static final /* enum */ State IDLE = new State();
        public static final /* enum */ State WAIT_ROUTE = new State();
        public static final /* enum */ State OPEN_NPC = new State();
        public static final /* enum */ State SELECT_SLOT = new State();
        public static final /* enum */ State SELECT_RECIPE = new State();
        public static final /* enum */ State CONFIRM_RECIPE = new State();
        public static final /* enum */ State VERIFY_STARTED = new State();
        public static final /* enum */ State COMPLETE = new State();
        private static final /* synthetic */ State[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        public static State[] values() {
            return (State[])$VALUES.clone();
        }

        public static State valueOf(String value) {
            return Enum.valueOf(State.class, value);
        }

        @NotNull
        public static EnumEntries<State> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = stateArray = new State[]{State.IDLE, State.WAIT_ROUTE, State.OPEN_NPC, State.SELECT_SLOT, State.SELECT_RECIPE, State.CONFIRM_RECIPE, State.VERIFY_STARTED, State.COMPLETE};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[State.values().length];
            try {
                nArray[State.IDLE.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.WAIT_ROUTE.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.OPEN_NPC.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.SELECT_SLOT.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.SELECT_RECIPE.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.CONFIRM_RECIPE.ordinal()] = 6;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.VERIFY_STARTED.ordinal()] = 7;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.COMPLETE.ordinal()] = 8;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

