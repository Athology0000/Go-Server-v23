/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.CrashReport
 *  net.minecraft.SharedConstants
 *  net.minecraft.SystemReport
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.GlobalPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.LayeredRegistryAccess
 *  net.minecraft.core.Position
 *  net.minecraft.gizmos.GizmoCollector
 *  net.minecraft.gizmos.Gizmos
 *  net.minecraft.gizmos.Gizmos$TemporaryCollection
 *  net.minecraft.gizmos.SimpleGizmoCollector
 *  net.minecraft.gizmos.SimpleGizmoCollector$GizmoInstance
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.RegistryLayer
 *  net.minecraft.server.Services
 *  net.minecraft.server.WorldStem
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.level.ServerPlayer$SavedPosition
 *  net.minecraft.server.level.progress.LevelLoadListener
 *  net.minecraft.server.packs.repository.PackRepository
 *  net.minecraft.server.permissions.LevelBasedPermissionSet
 *  net.minecraft.server.permissions.PermissionSet
 *  net.minecraft.server.players.NameAndId
 *  net.minecraft.stats.Stats
 *  net.minecraft.util.ModCheck
 *  net.minecraft.util.ProblemReporter
 *  net.minecraft.util.ProblemReporter$ScopedCollector
 *  net.minecraft.util.debugchart.LocalSampleLogger
 *  net.minecraft.util.debugchart.SampleLogger
 *  net.minecraft.util.profiling.Profiler
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.chunk.storage.RegionStorageInfo
 *  net.minecraft.world.level.storage.LevelStorageSource$LevelStorageAccess
 *  net.minecraft.world.level.storage.TagValueInput
 *  net.minecraft.world.level.storage.ValueInput
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.server;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.CrashReport;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.server.IntegratedPlayerList;
import net.minecraft.client.server.LanServerPinger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Position;
import net.minecraft.gizmos.GizmoCollector;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.SimpleGizmoCollector;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.server.players.NameAndId;
import net.minecraft.stats.Stats;
import net.minecraft.util.ModCheck;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.debugchart.LocalSampleLogger;
import net.minecraft.util.debugchart.SampleLogger;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class IntegratedServer
extends MinecraftServer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MIN_SIM_DISTANCE = 2;
    public static final int MAX_PLAYERS = 8;
    private final Minecraft minecraft;
    private boolean paused = true;
    private int publishedPort = -1;
    private @Nullable GameType publishedGameType;
    private @Nullable LanServerPinger lanPinger;
    private @Nullable UUID uuid;
    private int previousSimulationDistance = 0;
    private volatile List<SimpleGizmoCollector.GizmoInstance> latestTicksGizmos = new ArrayList<SimpleGizmoCollector.GizmoInstance>();
    private final SimpleGizmoCollector gizmoCollector = new SimpleGizmoCollector();

    public IntegratedServer(Thread thread, Minecraft minecraft, LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, Services services, LevelLoadListener levelLoadListener) {
        super(thread, levelStorageAccess, packRepository, worldStem, minecraft.getProxy(), minecraft.getFixerUpper(), services, levelLoadListener);
        this.setSingleplayerProfile(minecraft.getGameProfile());
        this.setDemo(minecraft.isDemo());
        this.setPlayerList(new IntegratedPlayerList(this, (LayeredRegistryAccess<RegistryLayer>)this.registries(), this.playerDataStorage));
        this.minecraft = minecraft;
    }

    public boolean initServer() {
        LOGGER.info("Starting integrated minecraft server version {}", (Object)SharedConstants.getCurrentVersion().name());
        this.setUsesAuthentication(true);
        this.initializeKeyPair();
        this.loadLevel();
        GameProfile gameProfile = this.getSingleplayerProfile();
        String string = this.getWorldData().getLevelName();
        this.setMotd((String)(gameProfile != null ? gameProfile.name() + " - " + string : string));
        return true;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void processPacketsAndTick(boolean bl) {
        try (Gizmos.TemporaryCollection temporaryCollection = Gizmos.withCollector((GizmoCollector)this.gizmoCollector);){
            super.processPacketsAndTick(bl);
        }
        if (this.tickRateManager().runsNormally()) {
            this.latestTicksGizmos = this.gizmoCollector.drainGizmos();
        }
    }

    public void tickServer(BooleanSupplier booleanSupplier) {
        int j;
        boolean bl = this.paused;
        this.paused = Minecraft.getInstance().isPaused() || this.getPlayerList().getPlayers().isEmpty();
        ProfilerFiller profilerFiller = Profiler.get();
        if (!bl && this.paused) {
            profilerFiller.push("autoSave");
            LOGGER.info("Saving and pausing game...");
            this.saveEverything(false, false, false);
            profilerFiller.pop();
        }
        if (this.paused) {
            this.tickPaused();
            return;
        }
        if (bl) {
            this.forceTimeSynchronization();
        }
        super.tickServer(booleanSupplier);
        int i = Math.max(2, this.minecraft.options.renderDistance().get());
        if (i != this.getPlayerList().getViewDistance()) {
            LOGGER.info("Changing view distance to {}, from {}", (Object)i, (Object)this.getPlayerList().getViewDistance());
            this.getPlayerList().setViewDistance(i);
        }
        if ((j = Math.max(2, this.minecraft.options.simulationDistance().get())) != this.previousSimulationDistance) {
            LOGGER.info("Changing simulation distance to {}, from {}", (Object)j, (Object)this.previousSimulationDistance);
            this.getPlayerList().setSimulationDistance(j);
            this.previousSimulationDistance = j;
        }
    }

    protected LocalSampleLogger getTickTimeLogger() {
        return this.minecraft.getDebugOverlay().getTickTimeLogger();
    }

    public boolean isTickTimeLoggingEnabled() {
        return true;
    }

    private void tickPaused() {
        this.tickConnection();
        for (ServerPlayer serverPlayer : this.getPlayerList().getPlayers()) {
            serverPlayer.awardStat(Stats.TOTAL_WORLD_TIME);
        }
    }

    public boolean shouldRconBroadcast() {
        return true;
    }

    public boolean shouldInformAdmins() {
        return true;
    }

    public Path getServerDirectory() {
        return this.minecraft.gameDirectory.toPath();
    }

    public boolean isDedicatedServer() {
        return false;
    }

    public int getRateLimitPacketsPerSecond() {
        return 0;
    }

    public boolean useNativeTransport() {
        return this.minecraft.options.useNativeTransport();
    }

    public void onServerCrash(CrashReport crashReport) {
        this.minecraft.delayCrashRaw(crashReport);
    }

    public SystemReport fillServerSystemReport(SystemReport systemReport) {
        systemReport.setDetail("Type", "Integrated Server (map_client.txt)");
        systemReport.setDetail("Is Modded", () -> this.getModdedStatus().fullDescription());
        systemReport.setDetail("Launched Version", this.minecraft::getLaunchedVersion);
        return systemReport;
    }

    public ModCheck getModdedStatus() {
        return Minecraft.checkModStatus().merge(super.getModdedStatus());
    }

    public boolean publishServer(@Nullable GameType gameType, boolean bl, int i) {
        try {
            this.minecraft.prepareForMultiplayer();
            this.minecraft.getConnection().prepareKeyPair();
            this.getConnection().startTcpServerListener(null, i);
            LOGGER.info("Started serving on {}", (Object)i);
            this.publishedPort = i;
            this.lanPinger = new LanServerPinger(this.getMotd(), "" + i);
            this.lanPinger.start();
            this.publishedGameType = gameType;
            this.getPlayerList().setAllowCommandsForAllPlayers(bl);
            LevelBasedPermissionSet permissionSet = this.getProfilePermissions(this.minecraft.player.nameAndId());
            this.minecraft.player.setPermissions((PermissionSet)permissionSet);
            for (ServerPlayer serverPlayer : this.getPlayerList().getPlayers()) {
                this.getCommands().sendCommands(serverPlayer);
            }
            return true;
        }
        catch (IOException iOException) {
            return false;
        }
    }

    public void stopServer() {
        super.stopServer();
        if (this.lanPinger != null) {
            this.lanPinger.interrupt();
            this.lanPinger = null;
        }
    }

    public void halt(boolean bl) {
        this.executeBlocking(() -> {
            ArrayList list = Lists.newArrayList((Iterable)this.getPlayerList().getPlayers());
            for (ServerPlayer serverPlayer : list) {
                if (serverPlayer.getUUID().equals(this.uuid)) continue;
                this.getPlayerList().remove(serverPlayer);
            }
        });
        super.halt(bl);
        if (this.lanPinger != null) {
            this.lanPinger.interrupt();
            this.lanPinger = null;
        }
    }

    public boolean isPublished() {
        return this.publishedPort > -1;
    }

    public int getPort() {
        return this.publishedPort;
    }

    public void setDefaultGameType(GameType gameType) {
        super.setDefaultGameType(gameType);
        this.publishedGameType = null;
    }

    public LevelBasedPermissionSet operatorUserPermissions() {
        return LevelBasedPermissionSet.GAMEMASTER;
    }

    public LevelBasedPermissionSet getFunctionCompilationPermissions() {
        return LevelBasedPermissionSet.GAMEMASTER;
    }

    public void setUUID(UUID uUID) {
        this.uuid = uUID;
    }

    public boolean isSingleplayerOwner(NameAndId nameAndId) {
        return this.getSingleplayerProfile() != null && nameAndId.name().equalsIgnoreCase(this.getSingleplayerProfile().name());
    }

    public int getScaledTrackingDistance(int i) {
        return (int)(this.minecraft.options.entityDistanceScaling().get() * (double)i);
    }

    public boolean forceSynchronousWrites() {
        return this.minecraft.options.syncWrites;
    }

    public @Nullable GameType getForcedGameType() {
        if (this.isPublished() && !this.isHardcore()) {
            return (GameType)MoreObjects.firstNonNull((Object)this.publishedGameType, (Object)this.worldData.getGameType());
        }
        return null;
    }

    public GlobalPos selectLevelLoadFocusPos() {
        CompoundTag compoundTag = this.worldData.getLoadedPlayerTag();
        if (compoundTag == null) {
            return super.selectLevelLoadFocusPos();
        }
        try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(LOGGER);){
            ValueInput valueInput = TagValueInput.create((ProblemReporter)scopedCollector, (HolderLookup.Provider)this.registryAccess(), (CompoundTag)compoundTag);
            ServerPlayer.SavedPosition savedPosition = valueInput.read(ServerPlayer.SavedPosition.MAP_CODEC).orElse(ServerPlayer.SavedPosition.EMPTY);
            if (savedPosition.dimension().isPresent() && savedPosition.position().isPresent()) {
                GlobalPos globalPos = new GlobalPos((ResourceKey)savedPosition.dimension().get(), BlockPos.containing((Position)((Position)savedPosition.position().get())));
                return globalPos;
            }
        }
        return super.selectLevelLoadFocusPos();
    }

    public boolean saveEverything(boolean bl, boolean bl2, boolean bl3) {
        boolean bl4 = super.saveEverything(bl, bl2, bl3);
        this.warnOnLowDiskSpace();
        return bl4;
    }

    private void warnOnLowDiskSpace() {
        if (this.storageSource.checkForLowDiskSpace()) {
            this.minecraft.execute(() -> SystemToast.onLowDiskSpace(this.minecraft));
        }
    }

    public void reportChunkLoadFailure(Throwable throwable, RegionStorageInfo regionStorageInfo, ChunkPos chunkPos) {
        super.reportChunkLoadFailure(throwable, regionStorageInfo, chunkPos);
        this.warnOnLowDiskSpace();
        this.minecraft.execute(() -> SystemToast.onChunkLoadFailure(this.minecraft, chunkPos));
    }

    public void reportChunkSaveFailure(Throwable throwable, RegionStorageInfo regionStorageInfo, ChunkPos chunkPos) {
        super.reportChunkSaveFailure(throwable, regionStorageInfo, chunkPos);
        this.warnOnLowDiskSpace();
        this.minecraft.execute(() -> SystemToast.onChunkSaveFailure(this.minecraft, chunkPos));
    }

    public int getMaxPlayers() {
        return 8;
    }

    public Collection<SimpleGizmoCollector.GizmoInstance> getPerTickGizmos() {
        return this.latestTicksGizmos;
    }

    public /* synthetic */ SampleLogger getTickTimeLogger() {
        return this.getTickTimeLogger();
    }

    public /* synthetic */ PermissionSet getFunctionCompilationPermissions() {
        return this.getFunctionCompilationPermissions();
    }
}

