/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Queues
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.CrashReport
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.ReportedException
 *  net.minecraft.SharedConstants
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Cursor3D
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Holder
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ExplosionParticleInfo
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.NbtOps
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ParticleStatus
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.ARGB
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.Util
 *  net.minecraft.util.profiling.Profiler
 *  net.minecraft.util.profiling.Zone
 *  net.minecraft.util.profiling.jfr.JvmProfiler
 *  net.minecraft.util.random.WeightedList
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.TickRateManager
 *  net.minecraft.world.attribute.AmbientParticle
 *  net.minecraft.world.attribute.EnvironmentAttributeReader
 *  net.minecraft.world.attribute.EnvironmentAttributeSystem
 *  net.minecraft.world.attribute.EnvironmentAttributeSystem$Builder
 *  net.minecraft.world.attribute.EnvironmentAttributes
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntitySelector
 *  net.minecraft.world.entity.boss.enderdragon.EnderDragon
 *  net.minecraft.world.entity.boss.enderdragon.EnderDragonPart
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.flag.FeatureFlagSet
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.alchemy.PotionBrewing
 *  net.minecraft.world.item.component.FireworkExplosion
 *  net.minecraft.world.item.crafting.RecipeAccess
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.ColorResolver
 *  net.minecraft.world.level.ExplosionDamageCalculator
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.Level$ExplosionInteraction
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.Biomes
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Block$UpdateFlags
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.FuelValues
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.border.WorldBorder
 *  net.minecraft.world.level.chunk.ChunkSource
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.dimension.DimensionType
 *  net.minecraft.world.level.dimension.DimensionType$CardinalLightType
 *  net.minecraft.world.level.entity.EntityAccess
 *  net.minecraft.world.level.entity.EntityTickList
 *  net.minecraft.world.level.entity.LevelCallback
 *  net.minecraft.world.level.entity.LevelEntityGetter
 *  net.minecraft.world.level.entity.TransientEntitySectionManager
 *  net.minecraft.world.level.gameevent.GameEvent
 *  net.minecraft.world.level.gameevent.GameEvent$Context
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.saveddata.maps.MapId
 *  net.minecraft.world.level.saveddata.maps.MapItemSavedData
 *  net.minecraft.world.level.storage.LevelData
 *  net.minecraft.world.level.storage.LevelData$RespawnData
 *  net.minecraft.world.level.storage.WritableLevelData
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.minecraft.world.scores.Scoreboard
 *  net.minecraft.world.ticks.BlackholeTickAccess
 *  net.minecraft.world.ticks.LevelTickAccess
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.multiplayer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.lang.runtime.SwitchBootstraps;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.multiplayer.CacheSlot;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientExplosionTracker;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.EndFlashState;
import net.minecraft.client.renderer.LevelEventHandler;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.sounds.DirectionalSoundInstance;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.Difficulty;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.attribute.AmbientParticle;
import net.minecraft.world.attribute.EnvironmentAttributeReader;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.BlackholeTickAccess;
import net.minecraft.world.ticks.LevelTickAccess;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientLevel
extends Level
implements CacheSlot.Cleaner<ClientLevel> {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Component DEFAULT_QUIT_MESSAGE = Component.translatable((String)"multiplayer.status.quitting");
    private static final double FLUID_PARTICLE_SPAWN_OFFSET = 0.05;
    private static final int NORMAL_LIGHT_UPDATES_PER_FRAME = 10;
    private static final int LIGHT_UPDATE_QUEUE_SIZE_THRESHOLD = 1000;
    final EntityTickList tickingEntities = new EntityTickList();
    private final TransientEntitySectionManager<Entity> entityStorage = new TransientEntitySectionManager(Entity.class, (LevelCallback)new EntityCallbacks());
    private final ClientPacketListener connection;
    private final LevelRenderer levelRenderer;
    private final LevelEventHandler levelEventHandler;
    private final ClientLevelData clientLevelData;
    private final TickRateManager tickRateManager;
    private final @Nullable EndFlashState endFlashState;
    private final Minecraft minecraft = Minecraft.getInstance();
    final List<AbstractClientPlayer> players = Lists.newArrayList();
    final List<EnderDragonPart> dragonParts = Lists.newArrayList();
    private final Map<MapId, MapItemSavedData> mapData = Maps.newHashMap();
    private int skyFlashTime;
    private final Object2ObjectArrayMap<ColorResolver, BlockTintCache> tintCaches = (Object2ObjectArrayMap)Util.make((Object)new Object2ObjectArrayMap(3), object2ObjectArrayMap -> {
        object2ObjectArrayMap.put((Object)BiomeColors.GRASS_COLOR_RESOLVER, (Object)new BlockTintCache(blockPos -> this.calculateBlockTint((BlockPos)blockPos, BiomeColors.GRASS_COLOR_RESOLVER)));
        object2ObjectArrayMap.put((Object)BiomeColors.FOLIAGE_COLOR_RESOLVER, (Object)new BlockTintCache(blockPos -> this.calculateBlockTint((BlockPos)blockPos, BiomeColors.FOLIAGE_COLOR_RESOLVER)));
        object2ObjectArrayMap.put((Object)BiomeColors.DRY_FOLIAGE_COLOR_RESOLVER, (Object)new BlockTintCache(blockPos -> this.calculateBlockTint((BlockPos)blockPos, BiomeColors.DRY_FOLIAGE_COLOR_RESOLVER)));
        object2ObjectArrayMap.put((Object)BiomeColors.WATER_COLOR_RESOLVER, (Object)new BlockTintCache(blockPos -> this.calculateBlockTint((BlockPos)blockPos, BiomeColors.WATER_COLOR_RESOLVER)));
    });
    private final ClientChunkCache chunkSource;
    private final Deque<Runnable> lightUpdateQueue = Queues.newArrayDeque();
    private int serverSimulationDistance;
    private final BlockStatePredictionHandler blockStatePredictionHandler = new BlockStatePredictionHandler();
    private final Set<BlockEntity> globallyRenderedBlockEntities = new ReferenceOpenHashSet();
    private final ClientExplosionTracker explosionTracker = new ClientExplosionTracker();
    private final WorldBorder worldBorder = new WorldBorder();
    private final EnvironmentAttributeSystem environmentAttributes;
    private final int seaLevel;
    private boolean tickDayTime;
    private static final Set<Item> MARKER_PARTICLE_ITEMS = Set.of(Items.BARRIER, Items.LIGHT);

    public void handleBlockChangedAck(int i) {
        if (SharedConstants.DEBUG_BLOCK_BREAK) {
            LOGGER.debug("ACK {}", (Object)i);
        }
        this.blockStatePredictionHandler.endPredictionsUpTo(i, this);
    }

    public void onBlockEntityAdded(BlockEntity blockEntity) {
        BlockEntityRenderer blockEntityRenderer = this.minecraft.getBlockEntityRenderDispatcher().getRenderer(blockEntity);
        if (blockEntityRenderer != null && blockEntityRenderer.shouldRenderOffScreen()) {
            this.globallyRenderedBlockEntities.add(blockEntity);
        }
    }

    public Set<BlockEntity> getGloballyRenderedBlockEntities() {
        return this.globallyRenderedBlockEntities;
    }

    public void setServerVerifiedBlockState(BlockPos blockPos, BlockState blockState, @Block.UpdateFlags int i) {
        if (!this.blockStatePredictionHandler.updateKnownServerState(blockPos, blockState)) {
            super.setBlock(blockPos, blockState, i, 512);
        }
    }

    public void syncBlockState(BlockPos blockPos, BlockState blockState, Vec3 vec3) {
        BlockState blockState2 = this.getBlockState(blockPos);
        if (blockState2 != blockState) {
            this.setBlock(blockPos, blockState, 19);
            LocalPlayer player = this.minecraft.player;
            if (this == player.level() && player.isColliding(blockPos, blockState)) {
                player.absSnapTo(vec3.x, vec3.y, vec3.z);
            }
        }
    }

    BlockStatePredictionHandler getBlockStatePredictionHandler() {
        return this.blockStatePredictionHandler;
    }

    public boolean setBlock(BlockPos blockPos, BlockState blockState, @Block.UpdateFlags int i, int j) {
        if (this.blockStatePredictionHandler.isPredicting()) {
            BlockState blockState2 = this.getBlockState(blockPos);
            boolean bl = super.setBlock(blockPos, blockState, i, j);
            if (bl) {
                this.blockStatePredictionHandler.retainKnownServerState(blockPos, blockState2, this.minecraft.player);
            }
            return bl;
        }
        return super.setBlock(blockPos, blockState, i, j);
    }

    public ClientLevel(ClientPacketListener clientPacketListener, ClientLevelData clientLevelData, ResourceKey<Level> resourceKey, Holder<DimensionType> holder, int i, int j, LevelRenderer levelRenderer, boolean bl, long l, int k) {
        super((WritableLevelData)clientLevelData, resourceKey, (RegistryAccess)clientPacketListener.registryAccess(), holder, true, bl, l, 1000000);
        this.connection = clientPacketListener;
        this.chunkSource = new ClientChunkCache(this, i);
        this.tickRateManager = new TickRateManager();
        this.clientLevelData = clientLevelData;
        this.levelRenderer = levelRenderer;
        this.seaLevel = k;
        this.levelEventHandler = new LevelEventHandler(this.minecraft, this);
        this.endFlashState = ((DimensionType)holder.value()).hasEndFlashes() ? new EndFlashState() : null;
        this.setRespawnData(LevelData.RespawnData.of(resourceKey, (BlockPos)new BlockPos(8, 64, 8), (float)0.0f, (float)0.0f));
        this.serverSimulationDistance = j;
        this.environmentAttributes = this.addEnvironmentAttributeLayers(EnvironmentAttributeSystem.builder()).build();
        this.updateSkyBrightness();
        if (this.canHaveWeather()) {
            this.prepareWeather();
        }
    }

    private EnvironmentAttributeSystem.Builder addEnvironmentAttributeLayers(EnvironmentAttributeSystem.Builder builder) {
        builder.addDefaultLayers((Level)this);
        int i2 = ARGB.color((int)204, (int)204, (int)255);
        builder.addTimeBasedLayer(EnvironmentAttributes.SKY_COLOR, (integer, j) -> {
            if (this.getSkyFlashTime() > 0) {
                return ARGB.srgbLerp((float)0.22f, (int)integer, (int)i2);
            }
            return integer;
        });
        builder.addTimeBasedLayer(EnvironmentAttributes.SKY_LIGHT_FACTOR, (float_, i) -> Float.valueOf(this.getSkyFlashTime() > 0 ? 1.0f : float_.floatValue()));
        return builder;
    }

    public void queueLightUpdate(Runnable runnable) {
        this.lightUpdateQueue.add(runnable);
    }

    public void pollLightUpdates() {
        Runnable runnable;
        int i = this.lightUpdateQueue.size();
        int j = i < 1000 ? Math.max(10, i / 10) : i;
        for (int k = 0; k < j && (runnable = this.lightUpdateQueue.poll()) != null; ++k) {
            runnable.run();
        }
    }

    public @Nullable EndFlashState endFlashState() {
        return this.endFlashState;
    }

    public void tick(BooleanSupplier booleanSupplier) {
        this.updateSkyBrightness();
        if (this.tickRateManager().runsNormally()) {
            this.getWorldBorder().tick();
            this.tickTime();
        }
        if (this.skyFlashTime > 0) {
            this.setSkyFlashTime(this.skyFlashTime - 1);
        }
        if (this.endFlashState != null) {
            this.endFlashState.tick(this.getGameTime());
            if (this.endFlashState.flashStartedThisTick() && !(this.minecraft.screen instanceof WinScreen)) {
                this.minecraft.getSoundManager().playDelayed(new DirectionalSoundInstance(SoundEvents.WEATHER_END_FLASH, SoundSource.WEATHER, this.random, this.minecraft.gameRenderer.getMainCamera(), this.endFlashState.getXAngle(), this.endFlashState.getYAngle()), 30);
            }
        }
        this.explosionTracker.tick(this);
        try (Zone zone = Profiler.get().zone("blocks");){
            this.chunkSource.tick(booleanSupplier, true);
        }
        JvmProfiler.INSTANCE.onClientTick(this.minecraft.getFps());
        this.environmentAttributes().invalidateTickCache();
    }

    private void tickTime() {
        this.clientLevelData.setGameTime(this.clientLevelData.getGameTime() + 1L);
        if (this.tickDayTime) {
            this.clientLevelData.setDayTime(this.clientLevelData.getDayTime() + 1L);
        }
    }

    public void setTimeFromServer(long l, long m, boolean bl) {
        this.clientLevelData.setGameTime(l);
        this.clientLevelData.setDayTime(m);
        this.tickDayTime = bl;
    }

    public Iterable<Entity> entitiesForRendering() {
        return this.getEntities().getAll();
    }

    public void tickEntities() {
        this.tickingEntities.forEach(entity -> {
            if (entity.isRemoved() || entity.isPassenger() || this.tickRateManager.isEntityFrozen(entity)) {
                return;
            }
            this.guardEntityTick(this::tickNonPassenger, (Entity)entity);
        });
    }

    public boolean isTickingEntity(Entity entity) {
        return this.tickingEntities.contains(entity);
    }

    public boolean shouldTickDeath(Entity entity) {
        return entity.chunkPosition().getChessboardDistance(this.minecraft.player.chunkPosition()) <= this.serverSimulationDistance;
    }

    public void tickNonPassenger(Entity entity) {
        entity.setOldPosAndRot();
        ++entity.tickCount;
        Profiler.get().push(() -> BuiltInRegistries.ENTITY_TYPE.getKey((Object)entity.getType()).toString());
        entity.tick();
        Profiler.get().pop();
        for (Entity entity2 : entity.getPassengers()) {
            this.tickPassenger(entity, entity2);
        }
    }

    private void tickPassenger(Entity entity, Entity entity2) {
        if (entity2.isRemoved() || entity2.getVehicle() != entity) {
            entity2.stopRiding();
            return;
        }
        if (!(entity2 instanceof Player) && !this.tickingEntities.contains(entity2)) {
            return;
        }
        entity2.setOldPosAndRot();
        ++entity2.tickCount;
        entity2.rideTick();
        for (Entity entity3 : entity2.getPassengers()) {
            this.tickPassenger(entity2, entity3);
        }
    }

    public void unload(LevelChunk levelChunk) {
        levelChunk.clearAllBlockEntities();
        this.chunkSource.getLightEngine().setLightEnabled(levelChunk.getPos(), false);
        this.entityStorage.stopTicking(levelChunk.getPos());
    }

    public void onChunkLoaded(ChunkPos chunkPos) {
        this.tintCaches.forEach((colorResolver, blockTintCache) -> blockTintCache.invalidateForChunk(chunkPos.x, chunkPos.z));
        this.entityStorage.startTicking(chunkPos);
    }

    public void onSectionBecomingNonEmpty(long l) {
        this.levelRenderer.onSectionBecomingNonEmpty(l);
    }

    public void clearTintCaches() {
        this.tintCaches.forEach((colorResolver, blockTintCache) -> blockTintCache.invalidateAll());
    }

    public boolean hasChunk(int i, int j) {
        return true;
    }

    public int getEntityCount() {
        return this.entityStorage.count();
    }

    public void addEntity(Entity entity) {
        this.removeEntity(entity.getId(), Entity.RemovalReason.DISCARDED);
        this.entityStorage.addEntity((EntityAccess)entity);
    }

    public void removeEntity(int i, Entity.RemovalReason removalReason) {
        Entity entity = (Entity)this.getEntities().get(i);
        if (entity != null) {
            entity.setRemoved(removalReason);
            entity.onClientRemoval();
        }
    }

    public List<Entity> getPushableEntities(Entity entity, AABB aABB) {
        LocalPlayer localPlayer = this.minecraft.player;
        if (localPlayer != null && localPlayer != entity && localPlayer.getBoundingBox().intersects(aABB) && EntitySelector.pushableBy((Entity)entity).test(localPlayer)) {
            return List.of(localPlayer);
        }
        return List.of();
    }

    public @Nullable Entity getEntity(int i) {
        return (Entity)this.getEntities().get(i);
    }

    public void disconnect(Component component) {
        this.connection.getConnection().disconnect(component);
    }

    public void animateTick(int i, int j, int k) {
        int l = 32;
        RandomSource randomSource = RandomSource.create();
        Block block = this.getMarkerParticleTarget();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int m = 0; m < 667; ++m) {
            this.doAnimateTick(i, j, k, 16, randomSource, block, mutableBlockPos);
            this.doAnimateTick(i, j, k, 32, randomSource, block, mutableBlockPos);
        }
    }

    private @Nullable Block getMarkerParticleTarget() {
        ItemStack itemStack;
        Item item;
        if (this.minecraft.gameMode.getPlayerMode() == GameType.CREATIVE && MARKER_PARTICLE_ITEMS.contains(item = (itemStack = this.minecraft.player.getMainHandItem()).getItem()) && item instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)item;
            return blockItem.getBlock();
        }
        return null;
    }

    public void doAnimateTick(int i, int j, int k, int l, RandomSource randomSource, @Nullable Block block, BlockPos.MutableBlockPos mutableBlockPos) {
        int m = i + this.random.nextInt(l) - this.random.nextInt(l);
        int n = j + this.random.nextInt(l) - this.random.nextInt(l);
        int o = k + this.random.nextInt(l) - this.random.nextInt(l);
        mutableBlockPos.set(m, n, o);
        BlockState blockState = this.getBlockState((BlockPos)mutableBlockPos);
        blockState.getBlock().animateTick(blockState, (Level)this, (BlockPos)mutableBlockPos, randomSource);
        FluidState fluidState = this.getFluidState((BlockPos)mutableBlockPos);
        if (!fluidState.isEmpty()) {
            fluidState.animateTick((Level)this, (BlockPos)mutableBlockPos, randomSource);
            ParticleOptions particleOptions = fluidState.getDripParticle();
            if (particleOptions != null && this.random.nextInt(10) == 0) {
                boolean bl = blockState.isFaceSturdy((BlockGetter)this, (BlockPos)mutableBlockPos, Direction.DOWN);
                BlockPos blockPos = mutableBlockPos.below();
                this.trySpawnDripParticles(blockPos, this.getBlockState(blockPos), particleOptions, bl);
            }
        }
        if (block == blockState.getBlock()) {
            this.addParticle((ParticleOptions)new BlockParticleOption(ParticleTypes.BLOCK_MARKER, blockState), (double)m + 0.5, (double)n + 0.5, (double)o + 0.5, 0.0, 0.0, 0.0);
        }
        if (!blockState.isCollisionShapeFullBlock((BlockGetter)this, (BlockPos)mutableBlockPos)) {
            for (AmbientParticle ambientParticle : (List)this.environmentAttributes().getValue(EnvironmentAttributes.AMBIENT_PARTICLES, (BlockPos)mutableBlockPos)) {
                if (!ambientParticle.canSpawn(this.random)) continue;
                this.addParticle(ambientParticle.particle(), (double)mutableBlockPos.getX() + this.random.nextDouble(), (double)mutableBlockPos.getY() + this.random.nextDouble(), (double)mutableBlockPos.getZ() + this.random.nextDouble(), 0.0, 0.0, 0.0);
            }
        }
    }

    private void trySpawnDripParticles(BlockPos blockPos, BlockState blockState, ParticleOptions particleOptions, boolean bl) {
        if (!blockState.getFluidState().isEmpty()) {
            return;
        }
        VoxelShape voxelShape = blockState.getCollisionShape((BlockGetter)this, blockPos);
        double d = voxelShape.max(Direction.Axis.Y);
        if (d < 1.0) {
            if (bl) {
                this.spawnFluidParticle(blockPos.getX(), blockPos.getX() + 1, blockPos.getZ(), blockPos.getZ() + 1, (double)(blockPos.getY() + 1) - 0.05, particleOptions);
            }
        } else if (!blockState.is(BlockTags.IMPERMEABLE)) {
            double e = voxelShape.min(Direction.Axis.Y);
            if (e > 0.0) {
                this.spawnParticle(blockPos, particleOptions, voxelShape, (double)blockPos.getY() + e - 0.05);
            } else {
                BlockPos blockPos2 = blockPos.below();
                BlockState blockState2 = this.getBlockState(blockPos2);
                VoxelShape voxelShape2 = blockState2.getCollisionShape((BlockGetter)this, blockPos2);
                double f = voxelShape2.max(Direction.Axis.Y);
                if (f < 1.0 && blockState2.getFluidState().isEmpty()) {
                    this.spawnParticle(blockPos, particleOptions, voxelShape, (double)blockPos.getY() - 0.05);
                }
            }
        }
    }

    private void spawnParticle(BlockPos blockPos, ParticleOptions particleOptions, VoxelShape voxelShape, double d) {
        this.spawnFluidParticle((double)blockPos.getX() + voxelShape.min(Direction.Axis.X), (double)blockPos.getX() + voxelShape.max(Direction.Axis.X), (double)blockPos.getZ() + voxelShape.min(Direction.Axis.Z), (double)blockPos.getZ() + voxelShape.max(Direction.Axis.Z), d, particleOptions);
    }

    private void spawnFluidParticle(double d, double e, double f, double g, double h, ParticleOptions particleOptions) {
        this.addParticle(particleOptions, Mth.lerp((double)this.random.nextDouble(), (double)d, (double)e), h, Mth.lerp((double)this.random.nextDouble(), (double)f, (double)g), 0.0, 0.0, 0.0);
    }

    public CrashReportCategory fillReportDetails(CrashReport crashReport) {
        CrashReportCategory crashReportCategory = super.fillReportDetails(crashReport);
        crashReportCategory.setDetail("Server brand", () -> this.minecraft.player.connection.serverBrand());
        crashReportCategory.setDetail("Server type", () -> this.minecraft.getSingleplayerServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server");
        crashReportCategory.setDetail("Tracked entity count", () -> String.valueOf(this.getEntityCount()));
        return crashReportCategory;
    }

    public void playSeededSound(@Nullable Entity entity, double d, double e, double f, Holder<SoundEvent> holder, SoundSource soundSource, float g, float h, long l) {
        if (entity == this.minecraft.player) {
            this.playSound(d, e, f, (SoundEvent)holder.value(), soundSource, g, h, false, l);
        }
    }

    public void playSeededSound(@Nullable Entity entity, Entity entity2, Holder<SoundEvent> holder, SoundSource soundSource, float f, float g, long l) {
        if (entity == this.minecraft.player) {
            this.minecraft.getSoundManager().play(new EntityBoundSoundInstance((SoundEvent)holder.value(), soundSource, f, g, entity2, l));
        }
    }

    public void playLocalSound(Entity entity, SoundEvent soundEvent, SoundSource soundSource, float f, float g) {
        this.minecraft.getSoundManager().play(new EntityBoundSoundInstance(soundEvent, soundSource, f, g, entity, this.random.nextLong()));
    }

    public void playPlayerSound(SoundEvent soundEvent, SoundSource soundSource, float f, float g) {
        if (this.minecraft.player != null) {
            this.minecraft.getSoundManager().play(new EntityBoundSoundInstance(soundEvent, soundSource, f, g, (Entity)this.minecraft.player, this.random.nextLong()));
        }
    }

    public void playLocalSound(double d, double e, double f, SoundEvent soundEvent, SoundSource soundSource, float g, float h, boolean bl) {
        this.playSound(d, e, f, soundEvent, soundSource, g, h, bl, this.random.nextLong());
    }

    private void playSound(double d, double e, double f, SoundEvent soundEvent, SoundSource soundSource, float g, float h, boolean bl, long l) {
        double i = this.minecraft.gameRenderer.getMainCamera().position().distanceToSqr(d, e, f);
        SimpleSoundInstance simpleSoundInstance = new SimpleSoundInstance(soundEvent, soundSource, g, h, RandomSource.create((long)l), d, e, f);
        if (bl && i > 100.0) {
            double j = Math.sqrt(i) / 40.0;
            this.minecraft.getSoundManager().playDelayed(simpleSoundInstance, (int)(j * 20.0));
        } else {
            this.minecraft.getSoundManager().play(simpleSoundInstance);
        }
    }

    public void createFireworks(double d, double e, double f, double g, double h, double i, List<FireworkExplosion> list) {
        if (list.isEmpty()) {
            for (int j = 0; j < this.random.nextInt(3) + 2; ++j) {
                this.addParticle((ParticleOptions)ParticleTypes.POOF, d, e, f, this.random.nextGaussian() * 0.05, 0.005, this.random.nextGaussian() * 0.05);
            }
        } else {
            this.minecraft.particleEngine.add(new FireworkParticles.Starter(this, d, e, f, g, h, i, this.minecraft.particleEngine, list));
        }
    }

    public void sendPacketToServer(Packet<?> packet) {
        this.connection.send(packet);
    }

    public WorldBorder getWorldBorder() {
        return this.worldBorder;
    }

    public RecipeAccess recipeAccess() {
        return this.connection.recipes();
    }

    public TickRateManager tickRateManager() {
        return this.tickRateManager;
    }

    public EnvironmentAttributeSystem environmentAttributes() {
        return this.environmentAttributes;
    }

    public LevelTickAccess<Block> getBlockTicks() {
        return BlackholeTickAccess.emptyLevelList();
    }

    public LevelTickAccess<Fluid> getFluidTicks() {
        return BlackholeTickAccess.emptyLevelList();
    }

    public ClientChunkCache getChunkSource() {
        return this.chunkSource;
    }

    public @Nullable MapItemSavedData getMapData(MapId mapId) {
        return this.mapData.get(mapId);
    }

    public void overrideMapData(MapId mapId, MapItemSavedData mapItemSavedData) {
        this.mapData.put(mapId, mapItemSavedData);
    }

    public Scoreboard getScoreboard() {
        return this.connection.scoreboard();
    }

    public void sendBlockUpdated(BlockPos blockPos, BlockState blockState, BlockState blockState2, @Block.UpdateFlags int i) {
        this.levelRenderer.blockChanged((BlockGetter)this, blockPos, blockState, blockState2, i);
    }

    public void setBlocksDirty(BlockPos blockPos, BlockState blockState, BlockState blockState2) {
        this.levelRenderer.setBlockDirty(blockPos, blockState, blockState2);
    }

    public void setSectionDirtyWithNeighbors(int i, int j, int k) {
        this.levelRenderer.setSectionDirtyWithNeighbors(i, j, k);
    }

    public void setSectionRangeDirty(int i, int j, int k, int l, int m, int n) {
        this.levelRenderer.setSectionRangeDirty(i, j, k, l, m, n);
    }

    public void destroyBlockProgress(int i, BlockPos blockPos, int j) {
        this.levelRenderer.destroyBlockProgress(i, blockPos, j);
    }

    public void globalLevelEvent(int i, BlockPos blockPos, int j) {
        this.levelEventHandler.globalLevelEvent(i, blockPos, j);
    }

    public void levelEvent(@Nullable Entity entity, int i, BlockPos blockPos, int j) {
        try {
            this.levelEventHandler.levelEvent(i, blockPos, j);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable((Throwable)throwable, (String)"Playing level event");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Level event being played");
            crashReportCategory.setDetail("Block coordinates", (Object)CrashReportCategory.formatLocation((LevelHeightAccessor)this, (BlockPos)blockPos));
            crashReportCategory.setDetail("Event source", (Object)entity);
            crashReportCategory.setDetail("Event type", (Object)i);
            crashReportCategory.setDetail("Event data", (Object)j);
            throw new ReportedException(crashReport);
        }
    }

    public void addParticle(ParticleOptions particleOptions, double d, double e, double f, double g, double h, double i) {
        this.doAddParticle(particleOptions, particleOptions.getType().getOverrideLimiter(), false, d, e, f, g, h, i);
    }

    public void addParticle(ParticleOptions particleOptions, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i) {
        this.doAddParticle(particleOptions, particleOptions.getType().getOverrideLimiter() || bl, bl2, d, e, f, g, h, i);
    }

    public void addAlwaysVisibleParticle(ParticleOptions particleOptions, double d, double e, double f, double g, double h, double i) {
        this.doAddParticle(particleOptions, false, true, d, e, f, g, h, i);
    }

    public void addAlwaysVisibleParticle(ParticleOptions particleOptions, boolean bl, double d, double e, double f, double g, double h, double i) {
        this.doAddParticle(particleOptions, particleOptions.getType().getOverrideLimiter() || bl, true, d, e, f, g, h, i);
    }

    private void doAddParticle(ParticleOptions particleOptions, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i) {
        try {
            Camera camera = this.minecraft.gameRenderer.getMainCamera();
            ParticleStatus particleStatus = this.calculateParticleLevel(bl2);
            if (bl) {
                this.minecraft.particleEngine.createParticle(particleOptions, d, e, f, g, h, i);
                return;
            }
            if (camera.position().distanceToSqr(d, e, f) > 1024.0) {
                return;
            }
            if (particleStatus == ParticleStatus.MINIMAL) {
                return;
            }
            this.minecraft.particleEngine.createParticle(particleOptions, d, e, f, g, h, i);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable((Throwable)throwable, (String)"Exception while adding particle");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Particle being added");
            crashReportCategory.setDetail("ID", (Object)BuiltInRegistries.PARTICLE_TYPE.getKey((Object)particleOptions.getType()));
            crashReportCategory.setDetail("Parameters", () -> ParticleTypes.CODEC.encodeStart((DynamicOps)this.registryAccess().createSerializationContext((DynamicOps)NbtOps.INSTANCE), (Object)particleOptions).toString());
            crashReportCategory.setDetail("Position", () -> CrashReportCategory.formatLocation((LevelHeightAccessor)this, (double)d, (double)e, (double)f));
            throw new ReportedException(crashReport);
        }
    }

    private ParticleStatus calculateParticleLevel(boolean bl) {
        ParticleStatus particleStatus = this.minecraft.options.particles().get();
        if (bl && particleStatus == ParticleStatus.MINIMAL && this.random.nextInt(10) == 0) {
            particleStatus = ParticleStatus.DECREASED;
        }
        if (particleStatus == ParticleStatus.DECREASED && this.random.nextInt(3) == 0) {
            particleStatus = ParticleStatus.MINIMAL;
        }
        return particleStatus;
    }

    public List<AbstractClientPlayer> players() {
        return this.players;
    }

    public List<EnderDragonPart> dragonParts() {
        return this.dragonParts;
    }

    public Holder<Biome> getUncachedNoiseBiome(int i, int j, int k) {
        return this.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS);
    }

    private int getSkyFlashTime() {
        return this.minecraft.options.hideLightningFlash().get() != false ? 0 : this.skyFlashTime;
    }

    public void setSkyFlashTime(int i) {
        this.skyFlashTime = i;
    }

    public float getShade(Direction direction, boolean bl) {
        DimensionType.CardinalLightType cardinalLightType = this.dimensionType().cardinalLightType();
        if (!bl) {
            return cardinalLightType == DimensionType.CardinalLightType.NETHER ? 0.9f : 1.0f;
        }
        return switch (direction) {
            default -> throw new MatchException(null, null);
            case Direction.DOWN -> {
                if (cardinalLightType == DimensionType.CardinalLightType.NETHER) {
                    yield 0.9f;
                }
                yield 0.5f;
            }
            case Direction.UP -> {
                if (cardinalLightType == DimensionType.CardinalLightType.NETHER) {
                    yield 0.9f;
                }
                yield 1.0f;
            }
            case Direction.NORTH, Direction.SOUTH -> 0.8f;
            case Direction.WEST, Direction.EAST -> 0.6f;
        };
    }

    public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        BlockTintCache blockTintCache = (BlockTintCache)this.tintCaches.get((Object)colorResolver);
        return blockTintCache.getColor(blockPos);
    }

    public int calculateBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        int i = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (i == 0) {
            return colorResolver.getColor((Biome)this.getBiome(blockPos).value(), (double)blockPos.getX(), (double)blockPos.getZ());
        }
        int j = (i * 2 + 1) * (i * 2 + 1);
        int k = 0;
        int l = 0;
        int m = 0;
        Cursor3D cursor3D = new Cursor3D(blockPos.getX() - i, blockPos.getY(), blockPos.getZ() - i, blockPos.getX() + i, blockPos.getY(), blockPos.getZ() + i);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        while (cursor3D.advance()) {
            mutableBlockPos.set(cursor3D.nextX(), cursor3D.nextY(), cursor3D.nextZ());
            int n = colorResolver.getColor((Biome)this.getBiome((BlockPos)mutableBlockPos).value(), (double)mutableBlockPos.getX(), (double)mutableBlockPos.getZ());
            k += (n & 0xFF0000) >> 16;
            l += (n & 0xFF00) >> 8;
            m += n & 0xFF;
        }
        return (k / j & 0xFF) << 16 | (l / j & 0xFF) << 8 | m / j & 0xFF;
    }

    public void setRespawnData(LevelData.RespawnData respawnData) {
        this.levelData.setSpawn(this.getWorldBorderAdjustedRespawnData(respawnData));
    }

    public LevelData.RespawnData getRespawnData() {
        return this.levelData.getRespawnData();
    }

    public String toString() {
        return "ClientLevel";
    }

    public ClientLevelData getLevelData() {
        return this.clientLevelData;
    }

    public void gameEvent(Holder<GameEvent> holder, Vec3 vec3, GameEvent.Context context) {
    }

    protected Map<MapId, MapItemSavedData> getAllMapData() {
        return ImmutableMap.copyOf(this.mapData);
    }

    protected void addMapData(Map<MapId, MapItemSavedData> map) {
        this.mapData.putAll(map);
    }

    protected LevelEntityGetter<Entity> getEntities() {
        return this.entityStorage.getEntityGetter();
    }

    public String gatherChunkSourceStats() {
        return "Chunks[C] W: " + this.chunkSource.gatherStats() + " E: " + this.entityStorage.gatherStats();
    }

    public void addDestroyBlockEffect(BlockPos blockPos, BlockState blockState) {
        if (blockState.isAir() || !blockState.shouldSpawnTerrainParticles()) {
            return;
        }
        VoxelShape voxelShape = blockState.getShape((BlockGetter)this, blockPos);
        double d2 = 0.25;
        voxelShape.forAllBoxes((d, e, f, g, h, i) -> {
            double j = Math.min(1.0, g - d);
            double k = Math.min(1.0, h - e);
            double l = Math.min(1.0, i - f);
            int m = Math.max(2, Mth.ceil((double)(j / 0.25)));
            int n = Math.max(2, Mth.ceil((double)(k / 0.25)));
            int o = Math.max(2, Mth.ceil((double)(l / 0.25)));
            for (int p = 0; p < m; ++p) {
                for (int q = 0; q < n; ++q) {
                    for (int r = 0; r < o; ++r) {
                        double s = ((double)p + 0.5) / (double)m;
                        double t = ((double)q + 0.5) / (double)n;
                        double u = ((double)r + 0.5) / (double)o;
                        double v = s * j + d;
                        double w = t * k + e;
                        double x = u * l + f;
                        this.minecraft.particleEngine.add(new TerrainParticle(this, (double)blockPos.getX() + v, (double)blockPos.getY() + w, (double)blockPos.getZ() + x, s - 0.5, t - 0.5, u - 0.5, blockState, blockPos));
                    }
                }
            }
        });
    }

    public void addBreakingBlockEffect(BlockPos blockPos, Direction direction) {
        BlockState blockState = this.getBlockState(blockPos);
        if (blockState.getRenderShape() == RenderShape.INVISIBLE || !blockState.shouldSpawnTerrainParticles()) {
            return;
        }
        int i = blockPos.getX();
        int j = blockPos.getY();
        int k = blockPos.getZ();
        float f = 0.1f;
        AABB aABB = blockState.getShape((BlockGetter)this, blockPos).bounds();
        double d = (double)i + this.random.nextDouble() * (aABB.maxX - aABB.minX - (double)0.2f) + (double)0.1f + aABB.minX;
        double e = (double)j + this.random.nextDouble() * (aABB.maxY - aABB.minY - (double)0.2f) + (double)0.1f + aABB.minY;
        double g = (double)k + this.random.nextDouble() * (aABB.maxZ - aABB.minZ - (double)0.2f) + (double)0.1f + aABB.minZ;
        if (direction == Direction.DOWN) {
            e = (double)j + aABB.minY - (double)0.1f;
        }
        if (direction == Direction.UP) {
            e = (double)j + aABB.maxY + (double)0.1f;
        }
        if (direction == Direction.NORTH) {
            g = (double)k + aABB.minZ - (double)0.1f;
        }
        if (direction == Direction.SOUTH) {
            g = (double)k + aABB.maxZ + (double)0.1f;
        }
        if (direction == Direction.WEST) {
            d = (double)i + aABB.minX - (double)0.1f;
        }
        if (direction == Direction.EAST) {
            d = (double)i + aABB.maxX + (double)0.1f;
        }
        this.minecraft.particleEngine.add(new TerrainParticle(this, d, e, g, 0.0, 0.0, 0.0, blockState, blockPos).setPower(0.2f).scale(0.6f));
    }

    public void setServerSimulationDistance(int i) {
        this.serverSimulationDistance = i;
    }

    public int getServerSimulationDistance() {
        return this.serverSimulationDistance;
    }

    public FeatureFlagSet enabledFeatures() {
        return this.connection.enabledFeatures();
    }

    public PotionBrewing potionBrewing() {
        return this.connection.potionBrewing();
    }

    public FuelValues fuelValues() {
        return this.connection.fuelValues();
    }

    public void explode(@Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator explosionDamageCalculator, double d, double e, double f, float g, boolean bl, Level.ExplosionInteraction explosionInteraction, ParticleOptions particleOptions, ParticleOptions particleOptions2, WeightedList<ExplosionParticleInfo> weightedList, Holder<SoundEvent> holder) {
    }

    public int getSeaLevel() {
        return this.seaLevel;
    }

    public int getClientLeafTintColor(BlockPos blockPos) {
        return Minecraft.getInstance().getBlockColors().getColor(this.getBlockState(blockPos), (BlockAndTintGetter)this, blockPos, 0);
    }

    @Override
    public void registerForCleaning(CacheSlot<ClientLevel, ?> cacheSlot) {
        this.connection.registerForCleaning(cacheSlot);
    }

    public void trackExplosionEffects(Vec3 vec3, float f, int i, WeightedList<ExplosionParticleInfo> weightedList) {
        this.explosionTracker.track(vec3, f, i, weightedList);
    }

    public /* synthetic */ LevelData getLevelData() {
        return this.getLevelData();
    }

    public /* synthetic */ Collection dragonParts() {
        return this.dragonParts();
    }

    public /* synthetic */ ChunkSource getChunkSource() {
        return this.getChunkSource();
    }

    public /* synthetic */ EnvironmentAttributeReader environmentAttributes() {
        return this.environmentAttributes();
    }

    @Environment(value=EnvType.CLIENT)
    final class EntityCallbacks
    implements LevelCallback<Entity> {
        EntityCallbacks() {
        }

        public void onCreated(Entity entity) {
        }

        public void onDestroyed(Entity entity) {
        }

        public void onTickingStart(Entity entity) {
            ClientLevel.this.tickingEntities.add(entity);
        }

        public void onTickingEnd(Entity entity) {
            ClientLevel.this.tickingEntities.remove(entity);
        }

        public void onTrackingStart(Entity entity) {
            Entity entity2 = entity;
            Objects.requireNonNull(entity2);
            Entity entity3 = entity2;
            int n = 0;
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{AbstractClientPlayer.class, EnderDragon.class}, (Object)entity3, n)) {
                case 0: {
                    AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)entity3;
                    ClientLevel.this.players.add(abstractClientPlayer);
                    break;
                }
                case 1: {
                    EnderDragon enderDragon = (EnderDragon)entity3;
                    ClientLevel.this.dragonParts.addAll(Arrays.asList(enderDragon.getSubEntities()));
                    break;
                }
            }
        }

        public void onTrackingEnd(Entity entity) {
            entity.unRide();
            Entity entity2 = entity;
            Objects.requireNonNull(entity2);
            Entity entity3 = entity2;
            int n = 0;
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{AbstractClientPlayer.class, EnderDragon.class}, (Object)entity3, n)) {
                case 0: {
                    AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)entity3;
                    ClientLevel.this.players.remove(abstractClientPlayer);
                    break;
                }
                case 1: {
                    EnderDragon enderDragon = (EnderDragon)entity3;
                    ClientLevel.this.dragonParts.removeAll(Arrays.asList(enderDragon.getSubEntities()));
                    break;
                }
            }
        }

        public void onSectionChange(Entity entity) {
        }

        public /* synthetic */ void onSectionChange(Object object) {
            this.onSectionChange((Entity)object);
        }

        public /* synthetic */ void onTrackingEnd(Object object) {
            this.onTrackingEnd((Entity)object);
        }

        public /* synthetic */ void onTrackingStart(Object object) {
            this.onTrackingStart((Entity)object);
        }

        public /* synthetic */ void onTickingStart(Object object) {
            this.onTickingStart((Entity)object);
        }

        public /* synthetic */ void onDestroyed(Object object) {
            this.onDestroyed((Entity)object);
        }

        public /* synthetic */ void onCreated(Object object) {
            this.onCreated((Entity)object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class ClientLevelData
    implements WritableLevelData {
        private final boolean hardcore;
        private final boolean isFlat;
        private LevelData.RespawnData respawnData;
        private long gameTime;
        private long dayTime;
        private boolean raining;
        private Difficulty difficulty;
        private boolean difficultyLocked;

        public ClientLevelData(Difficulty difficulty, boolean bl, boolean bl2) {
            this.difficulty = difficulty;
            this.hardcore = bl;
            this.isFlat = bl2;
        }

        public LevelData.RespawnData getRespawnData() {
            return this.respawnData;
        }

        public long getGameTime() {
            return this.gameTime;
        }

        public long getDayTime() {
            return this.dayTime;
        }

        public void setGameTime(long l) {
            this.gameTime = l;
        }

        public void setDayTime(long l) {
            this.dayTime = l;
        }

        public void setSpawn(LevelData.RespawnData respawnData) {
            this.respawnData = respawnData;
        }

        public boolean isThundering() {
            return false;
        }

        public boolean isRaining() {
            return this.raining;
        }

        public void setRaining(boolean bl) {
            this.raining = bl;
        }

        public boolean isHardcore() {
            return this.hardcore;
        }

        public Difficulty getDifficulty() {
            return this.difficulty;
        }

        public boolean isDifficultyLocked() {
            return this.difficultyLocked;
        }

        public void fillCrashReportCategory(CrashReportCategory crashReportCategory, LevelHeightAccessor levelHeightAccessor) {
            super.fillCrashReportCategory(crashReportCategory, levelHeightAccessor);
        }

        public void setDifficulty(Difficulty difficulty) {
            this.difficulty = difficulty;
        }

        public void setDifficultyLocked(boolean bl) {
            this.difficultyLocked = bl;
        }

        public double getHorizonHeight(LevelHeightAccessor levelHeightAccessor) {
            if (this.isFlat) {
                return levelHeightAccessor.getMinY();
            }
            return 63.0;
        }

        public float voidDarknessOnsetRange() {
            if (this.isFlat) {
                return 1.0f;
            }
            return 32.0f;
        }
    }
}

