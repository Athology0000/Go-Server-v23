/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.fabricmc.api.ClientModInitializer
 *  net.minecraft.class_2724
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.class_2724;
import org.cobalt.api.addon.Addon;
import org.cobalt.api.command.CommandManager;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.PacketEvent;
import org.cobalt.api.hud.HudModuleManager;
import org.cobalt.api.hud.modules.CommissionMacroModule;
import org.cobalt.api.hud.modules.InventoryHudModule;
import org.cobalt.api.hud.modules.MiningHudModule;
import org.cobalt.api.hud.modules.WatermarkModule;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.ModuleManager;
import org.cobalt.api.notification.NotificationManager;
import org.cobalt.api.pathfinder.jni.NativePathfinder;
import org.cobalt.api.rotation.RotationExecutor;
import org.cobalt.api.util.TickScheduler;
import org.cobalt.internal.chat.ChatFilterModule;
import org.cobalt.internal.combat.CombatHudModule;
import org.cobalt.internal.combat.CombatMacroModule;
import org.cobalt.internal.combat.CombatPatrolModule;
import org.cobalt.internal.command.MainCommand;
import org.cobalt.internal.diana.DianaHelperModule;
import org.cobalt.internal.diana.DianaMacroModule;
import org.cobalt.internal.dungeons.BloodCampHelperModule;
import org.cobalt.internal.dungeons.DungeonMapModule;
import org.cobalt.internal.dungeons.DungeonRoutesModule;
import org.cobalt.internal.dungeons.DungeonsModule;
import org.cobalt.internal.etherwarp.EtherwarpHelperModule;
import org.cobalt.internal.etherwarp.LeftClickEtherwarpModule;
import org.cobalt.internal.etherwarp.SmoothAotvModule;
import org.cobalt.internal.farming.FarmingMacroModule;
import org.cobalt.internal.garden.GardenAnalyzerModule;
import org.cobalt.internal.garden.GardenHudModule;
import org.cobalt.internal.garden.GardenMacroModule;
import org.cobalt.internal.helper.Config;
import org.cobalt.internal.loader.AddonLoader;
import org.cobalt.internal.mining.AutoForgeModule;
import org.cobalt.internal.mining.AutoLanternModule;
import org.cobalt.internal.mining.FairyModule;
import org.cobalt.internal.mining.MiningCoinPopupModule;
import org.cobalt.internal.mining.MiningMacroModule;
import org.cobalt.internal.mining.MiningModule;
import org.cobalt.internal.mining.NoFrillsMiningModule;
import org.cobalt.internal.mining.RoutesModule;
import org.cobalt.internal.mining.VeinDirectionModule;
import org.cobalt.internal.pathfinding.PathfindingModule;
import org.cobalt.internal.pig.PigMacroModule;
import org.cobalt.internal.qol.AutoStashModule;
import org.cobalt.internal.qol.ItemLockingModule;
import org.cobalt.internal.qol.PriceTooltipModule;
import org.cobalt.internal.qol.QolModule;
import org.cobalt.internal.rotation.RotationsModule;
import org.cobalt.internal.routes.RouteEditMode;
import org.cobalt.internal.routes.RouteStore;
import org.cobalt.internal.spotify.SpotifyModule;
import org.cobalt.internal.stats.MacroTimeTracker;
import org.cobalt.internal.visual.BlockOutlineModule;
import org.cobalt.internal.visual.BlockOverlayModule;
import org.cobalt.internal.visual.DarkModeModule;
import org.cobalt.internal.visual.DeployableHudModule;
import org.cobalt.internal.visual.FreecamModule;
import org.cobalt.internal.visual.FullBrightModule;
import org.cobalt.internal.visual.HotbarOverlayModule;
import org.cobalt.internal.visual.MobEspModule;
import org.cobalt.internal.visual.OrbitFreecamModule;
import org.cobalt.internal.visual.PetDisplayModule;
import org.cobalt.internal.visual.SkyboxChangerModule;
import org.cobalt.internal.visual.TitleScreenRenderer;
import org.cobalt.internal.visual.WitherImpactOverlayModule;
import org.cobalt.internal.wardrobe.WardrobeModule;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u0017\u0010\b\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006H\u0007\u00a2\u0006\u0004\b\b\u0010\t\u00a8\u0006\n"}, d2={"Lorg/cobalt/Cobalt;", "Lnet/fabricmc/api/ClientModInitializer;", "<init>", "()V", "", "onInitializeClient", "Lorg/cobalt/api/event/impl/client/PacketEvent$Incoming;", "event", "onRespawn", "(Lorg/cobalt/api/event/impl/client/PacketEvent$Incoming;)V", "cobalt"})
@SourceDebugExtension(value={"SMAP\nCobalt.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Cobalt.kt\norg/cobalt/Cobalt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,165:1\n1586#2:166\n1661#2,3:167\n1915#2,2:170\n1915#2,2:172\n*S KotlinDebug\n*F\n+ 1 Cobalt.kt\norg/cobalt/Cobalt\n*L\n135#1:166\n135#1:167,3\n135#1:170,2\n148#1:172,2\n*E\n"})
public final class Cobalt
implements ClientModInitializer {
    @NotNull
    public static final Cobalt INSTANCE = new Cobalt();

    private Cobalt() {
    }

    /*
     * WARNING - void declaration
     */
    public void onInitializeClient() {
        Object element$iv;
        void $this$mapTo$iv$iv;
        Object[] objectArray = new Module[]{new WatermarkModule(), new InventoryHudModule(), MiningModule.INSTANCE, MiningHudModule.INSTANCE, MiningCoinPopupModule.INSTANCE, FairyModule.INSTANCE, RoutesModule.INSTANCE, MiningMacroModule.INSTANCE, CommissionMacroModule.INSTANCE, AutoForgeModule.INSTANCE, VeinDirectionModule.INSTANCE, AutoLanternModule.INSTANCE, NoFrillsMiningModule.INSTANCE, CombatMacroModule.INSTANCE, CombatPatrolModule.INSTANCE, CombatHudModule.INSTANCE, DungeonsModule.INSTANCE, BloodCampHelperModule.INSTANCE, DungeonMapModule.INSTANCE, DungeonRoutesModule.INSTANCE, EtherwarpHelperModule.INSTANCE, LeftClickEtherwarpModule.INSTANCE, SmoothAotvModule.INSTANCE, PathfindingModule.INSTANCE, FullBrightModule.INSTANCE, SkyboxChangerModule.INSTANCE, DarkModeModule.INSTANCE, FreecamModule.INSTANCE, DeployableHudModule.INSTANCE, OrbitFreecamModule.INSTANCE, BlockOverlayModule.INSTANCE, BlockOutlineModule.INSTANCE, MobEspModule.INSTANCE, WitherImpactOverlayModule.INSTANCE, QolModule.INSTANCE, ItemLockingModule.INSTANCE, PriceTooltipModule.INSTANCE, AutoStashModule.INSTANCE, RotationsModule.INSTANCE, HotbarOverlayModule.INSTANCE, PetDisplayModule.INSTANCE, GardenAnalyzerModule.INSTANCE, GardenMacroModule.INSTANCE, FarmingMacroModule.INSTANCE, PigMacroModule.INSTANCE, SpotifyModule.INSTANCE, ChatFilterModule.INSTANCE, DianaMacroModule.INSTANCE, DianaHelperModule.INSTANCE, WardrobeModule.INSTANCE, GardenHudModule.INSTANCE};
        ModuleManager.INSTANCE.addModules$cobalt(CollectionsKt.listOf((Object[])objectArray));
        Iterable $this$map$iv = AddonLoader.INSTANCE.getAddons();
        boolean $i$f$map = false;
        Iterator iterator = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            Pair pair = (Pair)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add((Addon)it.getSecond());
        }
        Object $this$forEach$iv = (Object[])((List)destination$iv$iv);
        boolean $i$f$forEach = false;
        iterator = $this$forEach$iv.iterator();
        while (iterator.hasNext()) {
            element$iv = iterator.next();
            Addon it = (Addon)element$iv;
            boolean bl = false;
            it.onLoad();
            ModuleManager.INSTANCE.addModules$cobalt(it.getModules());
        }
        CommandManager.register(MainCommand.INSTANCE);
        CommandManager.INSTANCE.dispatchAll$cobalt();
        MacroTimeTracker.INSTANCE.load();
        $this$forEach$iv = new Object[]{TickScheduler.INSTANCE, MainCommand.INSTANCE, NotificationManager.INSTANCE, RotationExecutor.INSTANCE, HudModuleManager.INSTANCE, TitleScreenRenderer.INSTANCE, MacroTimeTracker.INSTANCE, RouteEditMode.INSTANCE};
        $this$forEach$iv = CollectionsKt.listOf((Object[])$this$forEach$iv);
        $i$f$forEach = false;
        iterator = $this$forEach$iv.iterator();
        while (iterator.hasNext()) {
            Object it = element$iv = iterator.next();
            boolean bl = false;
            EventBus.register(it);
        }
        NativePathfinder.INSTANCE.init();
        Config.INSTANCE.loadModulesConfig();
        RouteStore.INSTANCE.migrate();
        RouteStore.INSTANCE.loadAssignments();
        ItemLockingModule.INSTANCE.loadPersistedState();
        WardrobeModule.INSTANCE.loadFavorites();
        EventBus.register(this);
        System.out.println((Object)"Dutt Client Initialized");
    }

    @SubscribeEvent
    public final void onRespawn(@NotNull PacketEvent.Incoming event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (event.getPacket() instanceof class_2724) {
            NativePathfinder.INSTANCE.onLevelChange();
        }
    }
}

