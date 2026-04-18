/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.common.hash.HashCode
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.ChatFormatting
 *  net.minecraft.advancements.AdvancementHolder
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.ArgumentSignatures
 *  net.minecraft.commands.synchronization.SuggestionProviders
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Registry
 *  net.minecraft.core.Registry$PendingTags
 *  net.minecraft.core.RegistryAccess$Frozen
 *  net.minecraft.core.RegistrySynchronization
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.particles.ExplosionParticleInfo
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.Connection
 *  net.minecraft.network.HashedPatchMap$HashGenerator
 *  net.minecraft.network.PacketListener
 *  net.minecraft.network.PacketProcessor
 *  net.minecraft.network.TickablePacketListener
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.LastSeenMessagesTracker
 *  net.minecraft.network.chat.LastSeenMessagesTracker$Update
 *  net.minecraft.network.chat.LocalChatSession
 *  net.minecraft.network.chat.MessageSignature
 *  net.minecraft.network.chat.MessageSignatureCache
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.PlayerChatMessage
 *  net.minecraft.network.chat.RemoteChatSession
 *  net.minecraft.network.chat.RemoteChatSession$Data
 *  net.minecraft.network.chat.SignableCommand
 *  net.minecraft.network.chat.SignedMessageBody
 *  net.minecraft.network.chat.SignedMessageChain$Encoder
 *  net.minecraft.network.chat.SignedMessageLink
 *  net.minecraft.network.chat.numbers.NumberFormat
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.PacketUtils
 *  net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket
 *  net.minecraft.network.protocol.common.ServerboundClientInformationPacket
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.configuration.ConfigurationProtocols
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.protocol.game.ClientboundAddEntityPacket
 *  net.minecraft.network.protocol.game.ClientboundAnimatePacket
 *  net.minecraft.network.protocol.game.ClientboundAwardStatsPacket
 *  net.minecraft.network.protocol.game.ClientboundBlockChangedAckPacket
 *  net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket
 *  net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
 *  net.minecraft.network.protocol.game.ClientboundBlockEventPacket
 *  net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
 *  net.minecraft.network.protocol.game.ClientboundBossEventPacket
 *  net.minecraft.network.protocol.game.ClientboundBundlePacket
 *  net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket
 *  net.minecraft.network.protocol.game.ClientboundChunkBatchFinishedPacket
 *  net.minecraft.network.protocol.game.ClientboundChunkBatchStartPacket
 *  net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket
 *  net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket$ChunkBiomeData
 *  net.minecraft.network.protocol.game.ClientboundClearTitlesPacket
 *  net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket
 *  net.minecraft.network.protocol.game.ClientboundCommandsPacket
 *  net.minecraft.network.protocol.game.ClientboundCommandsPacket$NodeBuilder
 *  net.minecraft.network.protocol.game.ClientboundContainerClosePacket
 *  net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket
 *  net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket
 *  net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket
 *  net.minecraft.network.protocol.game.ClientboundCooldownPacket
 *  net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket
 *  net.minecraft.network.protocol.game.ClientboundDamageEventPacket
 *  net.minecraft.network.protocol.game.ClientboundDebugBlockValuePacket
 *  net.minecraft.network.protocol.game.ClientboundDebugChunkValuePacket
 *  net.minecraft.network.protocol.game.ClientboundDebugEntityValuePacket
 *  net.minecraft.network.protocol.game.ClientboundDebugEventPacket
 *  net.minecraft.network.protocol.game.ClientboundDebugSamplePacket
 *  net.minecraft.network.protocol.game.ClientboundDeleteChatPacket
 *  net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket
 *  net.minecraft.network.protocol.game.ClientboundEntityEventPacket
 *  net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket
 *  net.minecraft.network.protocol.game.ClientboundExplodePacket
 *  net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket
 *  net.minecraft.network.protocol.game.ClientboundGameEventPacket
 *  net.minecraft.network.protocol.game.ClientboundGameEventPacket$Type
 *  net.minecraft.network.protocol.game.ClientboundGameTestHighlightPosPacket
 *  net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket
 *  net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket
 *  net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData
 *  net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket
 *  net.minecraft.network.protocol.game.ClientboundLevelEventPacket
 *  net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket
 *  net.minecraft.network.protocol.game.ClientboundLightUpdatePacket
 *  net.minecraft.network.protocol.game.ClientboundLightUpdatePacketData
 *  net.minecraft.network.protocol.game.ClientboundLoginPacket
 *  net.minecraft.network.protocol.game.ClientboundMapItemDataPacket
 *  net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket
 *  net.minecraft.network.protocol.game.ClientboundMountScreenOpenPacket
 *  net.minecraft.network.protocol.game.ClientboundMoveEntityPacket
 *  net.minecraft.network.protocol.game.ClientboundMoveMinecartPacket
 *  net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket
 *  net.minecraft.network.protocol.game.ClientboundOpenBookPacket
 *  net.minecraft.network.protocol.game.ClientboundOpenScreenPacket
 *  net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket
 *  net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket
 *  net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket
 *  net.minecraft.network.protocol.game.ClientboundPlayerChatPacket
 *  net.minecraft.network.protocol.game.ClientboundPlayerCombatEndPacket
 *  net.minecraft.network.protocol.game.ClientboundPlayerCombatEnterPacket
 *  net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket
 *  net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket
 *  net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
 *  net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket$Action
 *  net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket$Entry
 *  net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket
 *  net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket
 *  net.minecraft.network.protocol.game.ClientboundPlayerRotationPacket
 *  net.minecraft.network.protocol.game.ClientboundProjectilePowerPacket
 *  net.minecraft.network.protocol.game.ClientboundRecipeBookAddPacket
 *  net.minecraft.network.protocol.game.ClientboundRecipeBookAddPacket$Entry
 *  net.minecraft.network.protocol.game.ClientboundRecipeBookRemovePacket
 *  net.minecraft.network.protocol.game.ClientboundRecipeBookSettingsPacket
 *  net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
 *  net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket
 *  net.minecraft.network.protocol.game.ClientboundResetScorePacket
 *  net.minecraft.network.protocol.game.ClientboundRespawnPacket
 *  net.minecraft.network.protocol.game.ClientboundRotateHeadPacket
 *  net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket
 *  net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket
 *  net.minecraft.network.protocol.game.ClientboundServerDataPacket
 *  net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket
 *  net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket
 *  net.minecraft.network.protocol.game.ClientboundSetBorderLerpSizePacket
 *  net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket
 *  net.minecraft.network.protocol.game.ClientboundSetBorderWarningDelayPacket
 *  net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket
 *  net.minecraft.network.protocol.game.ClientboundSetCameraPacket
 *  net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket
 *  net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket
 *  net.minecraft.network.protocol.game.ClientboundSetCursorItemPacket
 *  net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket
 *  net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket
 *  net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
 *  net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket
 *  net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket
 *  net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket
 *  net.minecraft.network.protocol.game.ClientboundSetExperiencePacket
 *  net.minecraft.network.protocol.game.ClientboundSetHealthPacket
 *  net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket
 *  net.minecraft.network.protocol.game.ClientboundSetObjectivePacket
 *  net.minecraft.network.protocol.game.ClientboundSetPassengersPacket
 *  net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket
 *  net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket
 *  net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket$Action
 *  net.minecraft.network.protocol.game.ClientboundSetScorePacket
 *  net.minecraft.network.protocol.game.ClientboundSetSimulationDistancePacket
 *  net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket
 *  net.minecraft.network.protocol.game.ClientboundSetTimePacket
 *  net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket
 *  net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket
 *  net.minecraft.network.protocol.game.ClientboundSoundEntityPacket
 *  net.minecraft.network.protocol.game.ClientboundSoundPacket
 *  net.minecraft.network.protocol.game.ClientboundStartConfigurationPacket
 *  net.minecraft.network.protocol.game.ClientboundStopSoundPacket
 *  net.minecraft.network.protocol.game.ClientboundSystemChatPacket
 *  net.minecraft.network.protocol.game.ClientboundTabListPacket
 *  net.minecraft.network.protocol.game.ClientboundTagQueryPacket
 *  net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket
 *  net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
 *  net.minecraft.network.protocol.game.ClientboundTestInstanceBlockStatus
 *  net.minecraft.network.protocol.game.ClientboundTickingStatePacket
 *  net.minecraft.network.protocol.game.ClientboundTickingStepPacket
 *  net.minecraft.network.protocol.game.ClientboundTrackedWaypointPacket
 *  net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket
 *  net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket
 *  net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket$AttributeSnapshot
 *  net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket
 *  net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket
 *  net.minecraft.network.protocol.game.CommonPlayerSpawnInfo
 *  net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket
 *  net.minecraft.network.protocol.game.ServerboundChatAckPacket
 *  net.minecraft.network.protocol.game.ServerboundChatCommandPacket
 *  net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket
 *  net.minecraft.network.protocol.game.ServerboundChatPacket
 *  net.minecraft.network.protocol.game.ServerboundChatSessionUpdatePacket
 *  net.minecraft.network.protocol.game.ServerboundChunkBatchReceivedPacket
 *  net.minecraft.network.protocol.game.ServerboundClientCommandPacket
 *  net.minecraft.network.protocol.game.ServerboundClientCommandPacket$Action
 *  net.minecraft.network.protocol.game.ServerboundConfigurationAcknowledgedPacket
 *  net.minecraft.network.protocol.game.ServerboundMovePlayerPacket$PosRot
 *  net.minecraft.network.protocol.game.ServerboundMovePlayerPacket$Rot
 *  net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket
 *  net.minecraft.network.protocol.game.ServerboundPlayerLoadedPacket
 *  net.minecraft.network.protocol.game.VecDeltaCodec
 *  net.minecraft.network.protocol.ping.ClientboundPongResponsePacket
 *  net.minecraft.resources.Identifier
 *  net.minecraft.resources.RegistryOps
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ClientInformation
 *  net.minecraft.server.permissions.Permission
 *  net.minecraft.server.permissions.Permission$Atom
 *  net.minecraft.server.permissions.PermissionCheck
 *  net.minecraft.server.permissions.PermissionCheck$Require
 *  net.minecraft.server.permissions.PermissionSet
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.stats.Stat
 *  net.minecraft.stats.StatsCounter
 *  net.minecraft.tags.TagNetworkSerialization$NetworkPayload
 *  net.minecraft.util.Crypt$SaltSupplier
 *  net.minecraft.util.HashOps
 *  net.minecraft.util.Mth
 *  net.minecraft.util.ProblemReporter
 *  net.minecraft.util.ProblemReporter$ScopedCollector
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.SignatureValidator
 *  net.minecraft.util.debug.DebugValueAccess
 *  net.minecraft.util.random.WeightedList
 *  net.minecraft.world.Container
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.SimpleContainer
 *  net.minecraft.world.TickRateManager
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntitySpawnReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.ExperienceOrb
 *  net.minecraft.world.entity.Leashable
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.PositionMoveRotation
 *  net.minecraft.world.entity.Relative
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeMap
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.animal.bee.Bee
 *  net.minecraft.world.entity.animal.equine.AbstractHorse
 *  net.minecraft.world.entity.animal.nautilus.AbstractNautilus
 *  net.minecraft.world.entity.animal.sniffer.Sniffer
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.monster.Guardian
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.player.ProfileKeyPair
 *  net.minecraft.world.entity.player.ProfilePublicKey$ValidationException
 *  net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile
 *  net.minecraft.world.entity.vehicle.boat.AbstractBoat
 *  net.minecraft.world.entity.vehicle.minecart.AbstractMinecart
 *  net.minecraft.world.entity.vehicle.minecart.MinecartBehavior
 *  net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior
 *  net.minecraft.world.flag.FeatureFlagSet
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.AbstractMountInventoryMenu
 *  net.minecraft.world.inventory.HorseInventoryMenu
 *  net.minecraft.world.inventory.InventoryMenu
 *  net.minecraft.world.inventory.MerchantMenu
 *  net.minecraft.world.inventory.NautilusInventoryMenu
 *  net.minecraft.world.item.CreativeModeTabs
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.alchemy.PotionBrewing
 *  net.minecraft.world.item.crafting.RecipeAccess
 *  net.minecraft.world.item.crafting.SelectableRecipe$SingleInputSet
 *  net.minecraft.world.item.crafting.StonecutterRecipe
 *  net.minecraft.world.item.crafting.display.RecipeDisplayId
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.CommandBlockEntity
 *  net.minecraft.world.level.block.entity.FuelValues
 *  net.minecraft.world.level.block.entity.SignBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.border.WorldBorder
 *  net.minecraft.world.level.chunk.DataLayer
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.dimension.DimensionType
 *  net.minecraft.world.level.lighting.LevelLightEngine
 *  net.minecraft.world.level.saveddata.maps.MapId
 *  net.minecraft.world.level.saveddata.maps.MapItemSavedData
 *  net.minecraft.world.level.storage.TagValueInput
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.scores.Objective
 *  net.minecraft.world.scores.PlayerTeam
 *  net.minecraft.world.scores.ScoreAccess
 *  net.minecraft.world.scores.ScoreHolder
 *  net.minecraft.world.scores.Scoreboard
 *  net.minecraft.world.scores.criteria.ObjectiveCriteria
 *  net.minecraft.world.waypoints.TrackedWaypointManager
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.hash.HashCode;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.lang.ref.WeakReference;
import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.DebugQueryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.DemoIntroScreen;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.dialog.DialogConnectionAccess;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.HorseInventoryScreen;
import net.minecraft.client.gui.screens.inventory.NautilusInventoryScreen;
import net.minecraft.client.gui.screens.inventory.TestInstanceBlockEditScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerReconfigScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.multiplayer.CacheSlot;
import net.minecraft.client.multiplayer.ChunkBatchSizeCalculator;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientDebugSubscriber;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientRecipeContainer;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.PingDebugMonitor;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.resources.sounds.BeeAggressiveSoundInstance;
import net.minecraft.client.resources.sounds.BeeFlyingSoundInstance;
import net.minecraft.client.resources.sounds.BeeSoundInstance;
import net.minecraft.client.resources.sounds.GuardianAttackSoundInstance;
import net.minecraft.client.resources.sounds.MinecartSoundInstance;
import net.minecraft.client.resources.sounds.SnifferSoundInstance;
import net.minecraft.client.waypoints.ClientWaypointManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.HashedPatchMap;
import net.minecraft.network.PacketListener;
import net.minecraft.network.PacketProcessor;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.LastSeenMessagesTracker;
import net.minecraft.network.chat.LocalChatSession;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MessageSignatureCache;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.chat.SignableCommand;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.chat.SignedMessageChain;
import net.minecraft.network.chat.SignedMessageLink;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ConfigurationProtocols;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import net.minecraft.network.protocol.game.ClientboundBlockChangedAckPacket;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundChunkBatchFinishedPacket;
import net.minecraft.network.protocol.game.ClientboundChunkBatchStartPacket;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.network.protocol.game.ClientboundDebugBlockValuePacket;
import net.minecraft.network.protocol.game.ClientboundDebugChunkValuePacket;
import net.minecraft.network.protocol.game.ClientboundDebugEntityValuePacket;
import net.minecraft.network.protocol.game.ClientboundDebugEventPacket;
import net.minecraft.network.protocol.game.ClientboundDebugSamplePacket;
import net.minecraft.network.protocol.game.ClientboundDeleteChatPacket;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameTestHighlightPosPacket;
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacketData;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.network.protocol.game.ClientboundMountScreenOpenPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveMinecartPacket;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEndPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEnterPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerRotationPacket;
import net.minecraft.network.protocol.game.ClientboundProjectilePowerPacket;
import net.minecraft.network.protocol.game.ClientboundRecipeBookAddPacket;
import net.minecraft.network.protocol.game.ClientboundRecipeBookRemovePacket;
import net.minecraft.network.protocol.game.ClientboundRecipeBookSettingsPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundResetScorePacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket;
import net.minecraft.network.protocol.game.ClientboundServerDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderLerpSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDelayPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundSetCursorItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetSimulationDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStartConfigurationPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTestInstanceBlockStatus;
import net.minecraft.network.protocol.game.ClientboundTickingStatePacket;
import net.minecraft.network.protocol.game.ClientboundTickingStepPacket;
import net.minecraft.network.protocol.game.ClientboundTrackedWaypointPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundChatAckPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatSessionUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundChunkBatchReceivedPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundConfigurationAcknowledgedPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerLoadedPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatsCounter;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.util.Crypt;
import net.minecraft.util.HashOps;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfileKeyPair;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractMountInventoryMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.NautilusInventoryMenu;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.waypoints.TrackedWaypointManager;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientPacketListener
extends ClientCommonPacketListenerImpl
implements ClientGamePacketListener,
TickablePacketListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component UNSECURE_SERVER_TOAST_TITLE = Component.translatable((String)"multiplayer.unsecureserver.toast.title");
    private static final Component UNSERURE_SERVER_TOAST = Component.translatable((String)"multiplayer.unsecureserver.toast");
    private static final Component INVALID_PACKET = Component.translatable((String)"multiplayer.disconnect.invalid_packet");
    private static final Component RECONFIGURE_SCREEN_MESSAGE = Component.translatable((String)"connect.reconfiguring");
    private static final Component BAD_CHAT_INDEX = Component.translatable((String)"multiplayer.disconnect.bad_chat_index");
    private static final Component COMMAND_SEND_CONFIRM_TITLE = Component.translatable((String)"multiplayer.confirm_command.title");
    private static final Component BUTTON_RUN_COMMAND = Component.translatable((String)"multiplayer.confirm_command.run_command");
    private static final Component BUTTON_SUGGEST_COMMAND = Component.translatable((String)"multiplayer.confirm_command.suggest_command");
    private static final int PENDING_OFFSET_THRESHOLD = 64;
    public static final int TELEPORT_INTERPOLATION_THRESHOLD = 64;
    private static final Permission RESTRICTED_COMMAND = Permission.Atom.create((String)"client/commands/restricted");
    static final PermissionCheck RESTRICTED_COMMAND_CHECK = new PermissionCheck.Require(RESTRICTED_COMMAND);
    private static final PermissionSet ALLOW_RESTRICTED_COMMANDS = permission -> permission.equals((Object)RESTRICTED_COMMAND);
    private static final ClientboundCommandsPacket.NodeBuilder<ClientSuggestionProvider> COMMAND_NODE_BUILDER = new ClientboundCommandsPacket.NodeBuilder<ClientSuggestionProvider>(){

        public ArgumentBuilder<ClientSuggestionProvider, ?> createLiteral(String string) {
            return LiteralArgumentBuilder.literal((String)string);
        }

        public ArgumentBuilder<ClientSuggestionProvider, ?> createArgument(String string, ArgumentType<?> argumentType, @Nullable Identifier identifier) {
            RequiredArgumentBuilder requiredArgumentBuilder = RequiredArgumentBuilder.argument((String)string, argumentType);
            if (identifier != null) {
                requiredArgumentBuilder.suggests(SuggestionProviders.getProvider((Identifier)identifier));
            }
            return requiredArgumentBuilder;
        }

        public ArgumentBuilder<ClientSuggestionProvider, ?> configure(ArgumentBuilder<ClientSuggestionProvider, ?> argumentBuilder, boolean bl, boolean bl2) {
            if (bl) {
                argumentBuilder.executes(commandContext -> 0);
            }
            if (bl2) {
                argumentBuilder.requires((Predicate)Commands.hasPermission((PermissionCheck)RESTRICTED_COMMAND_CHECK));
            }
            return argumentBuilder;
        }
    };
    private final GameProfile localGameProfile;
    private ClientLevel level;
    private ClientLevel.ClientLevelData levelData;
    private final Map<UUID, PlayerInfo> playerInfoMap = Maps.newHashMap();
    private final Set<PlayerInfo> listedPlayers = new ReferenceOpenHashSet();
    private final ClientAdvancements advancements;
    private final ClientSuggestionProvider suggestionsProvider;
    private final ClientSuggestionProvider restrictedSuggestionsProvider;
    private final DebugQueryHandler debugQueryHandler = new DebugQueryHandler(this);
    private int serverChunkRadius = 3;
    private int serverSimulationDistance = 3;
    private final RandomSource random = RandomSource.createThreadSafe();
    private CommandDispatcher<ClientSuggestionProvider> commands = new CommandDispatcher();
    private ClientRecipeContainer recipes = new ClientRecipeContainer(Map.of(), (SelectableRecipe.SingleInputSet<StonecutterRecipe>)SelectableRecipe.SingleInputSet.empty());
    private final UUID id = UUID.randomUUID();
    private Set<ResourceKey<Level>> levels;
    private final RegistryAccess.Frozen registryAccess;
    private final FeatureFlagSet enabledFeatures;
    private final PotionBrewing potionBrewing;
    private FuelValues fuelValues;
    private final HashedPatchMap.HashGenerator decoratedHashOpsGenerator;
    private OptionalInt removedPlayerVehicleId = OptionalInt.empty();
    private @Nullable LocalChatSession chatSession;
    private SignedMessageChain.Encoder signedMessageEncoder = SignedMessageChain.Encoder.UNSIGNED;
    private int nextChatIndex;
    private LastSeenMessagesTracker lastSeenMessages = new LastSeenMessagesTracker(20);
    private MessageSignatureCache messageSignatureCache = MessageSignatureCache.createDefault();
    private @Nullable CompletableFuture<Optional<ProfileKeyPair>> keyPairFuture;
    private @Nullable ClientInformation remoteClientInformation;
    private final ChunkBatchSizeCalculator chunkBatchSizeCalculator = new ChunkBatchSizeCalculator();
    private final PingDebugMonitor pingDebugMonitor;
    private final ClientDebugSubscriber debugSubscriber;
    private @Nullable LevelLoadTracker levelLoadTracker;
    private boolean serverEnforcesSecureChat;
    private volatile boolean closed;
    private final Scoreboard scoreboard = new Scoreboard();
    private final ClientWaypointManager waypointManager = new ClientWaypointManager();
    private final SessionSearchTrees searchTrees = new SessionSearchTrees();
    private final List<WeakReference<CacheSlot<?, ?>>> cacheSlots = new ArrayList();
    private boolean clientLoaded;

    public ClientPacketListener(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
        this.localGameProfile = commonListenerCookie.localGameProfile();
        this.registryAccess = commonListenerCookie.receivedRegistries();
        RegistryOps registryOps = this.registryAccess.createSerializationContext((DynamicOps)HashOps.CRC32C_INSTANCE);
        this.decoratedHashOpsGenerator = typedDataComponent -> ((HashCode)typedDataComponent.encodeValue((DynamicOps)registryOps).getOrThrow(string -> new IllegalArgumentException("Failed to hash " + String.valueOf(typedDataComponent) + ": " + string))).asInt();
        this.enabledFeatures = commonListenerCookie.enabledFeatures();
        this.advancements = new ClientAdvancements(minecraft, this.telemetryManager);
        PermissionSet permissionSet = permission -> {
            LocalPlayer localPlayer = minecraft.player;
            return localPlayer != null && localPlayer.permissions().hasPermission(permission);
        };
        this.suggestionsProvider = new ClientSuggestionProvider(this, minecraft, permissionSet.union(ALLOW_RESTRICTED_COMMANDS));
        this.restrictedSuggestionsProvider = new ClientSuggestionProvider(this, minecraft, PermissionSet.NO_PERMISSIONS);
        this.pingDebugMonitor = new PingDebugMonitor(this, minecraft.getDebugOverlay().getPingLogger());
        this.debugSubscriber = new ClientDebugSubscriber(this, minecraft.getDebugOverlay());
        if (commonListenerCookie.chatState() != null) {
            minecraft.gui.getChat().restoreState(commonListenerCookie.chatState());
        }
        this.potionBrewing = PotionBrewing.bootstrap((FeatureFlagSet)this.enabledFeatures);
        this.fuelValues = FuelValues.vanillaBurnTimes((HolderLookup.Provider)commonListenerCookie.receivedRegistries(), (FeatureFlagSet)this.enabledFeatures);
        this.levelLoadTracker = commonListenerCookie.levelLoadTracker();
    }

    public ClientSuggestionProvider getSuggestionsProvider() {
        return this.suggestionsProvider;
    }

    public void close() {
        this.closed = true;
        this.clearLevel();
        this.telemetryManager.onDisconnect();
    }

    public void clearLevel() {
        this.clearCacheSlots();
        this.level = null;
        this.levelLoadTracker = null;
    }

    private void clearCacheSlots() {
        for (WeakReference<CacheSlot<?, ?>> weakReference : this.cacheSlots) {
            CacheSlot cacheSlot = (CacheSlot)weakReference.get();
            if (cacheSlot == null) continue;
            cacheSlot.clear();
        }
        this.cacheSlots.clear();
    }

    public RecipeAccess recipes() {
        return this.recipes;
    }

    public void handleLogin(ClientboundLoginPacket clientboundLoginPacket) {
        ClientLevel.ClientLevelData clientLevelData;
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundLoginPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.gameMode = new MultiPlayerGameMode(this.minecraft, this);
        CommonPlayerSpawnInfo commonPlayerSpawnInfo = clientboundLoginPacket.commonPlayerSpawnInfo();
        ArrayList list = Lists.newArrayList((Iterable)clientboundLoginPacket.levels());
        Collections.shuffle(list);
        this.levels = Sets.newLinkedHashSet((Iterable)list);
        ResourceKey resourceKey = commonPlayerSpawnInfo.dimension();
        Holder holder = commonPlayerSpawnInfo.dimensionType();
        this.serverChunkRadius = clientboundLoginPacket.chunkRadius();
        this.serverSimulationDistance = clientboundLoginPacket.simulationDistance();
        boolean bl = commonPlayerSpawnInfo.isDebug();
        boolean bl2 = commonPlayerSpawnInfo.isFlat();
        int i = commonPlayerSpawnInfo.seaLevel();
        this.levelData = clientLevelData = new ClientLevel.ClientLevelData(Difficulty.NORMAL, clientboundLoginPacket.hardcore(), bl2);
        this.level = new ClientLevel(this, clientLevelData, (ResourceKey<Level>)resourceKey, (Holder<DimensionType>)holder, this.serverChunkRadius, this.serverSimulationDistance, this.minecraft.levelRenderer, bl, commonPlayerSpawnInfo.seed(), i);
        this.minecraft.setLevel(this.level);
        if (this.minecraft.player == null) {
            this.minecraft.player = this.minecraft.gameMode.createPlayer(this.level, new StatsCounter(), new ClientRecipeBook());
            this.minecraft.player.setYRot(-180.0f);
            if (this.minecraft.getSingleplayerServer() != null) {
                this.minecraft.getSingleplayerServer().setUUID(this.minecraft.player.getUUID());
            }
        }
        this.setClientLoaded(false);
        this.debugSubscriber.clear();
        this.minecraft.levelRenderer.debugRenderer.refreshRendererList();
        this.minecraft.player.resetPos();
        this.minecraft.player.setId(clientboundLoginPacket.playerId());
        this.level.addEntity((Entity)this.minecraft.player);
        this.minecraft.player.input = new KeyboardInput(this.minecraft.options);
        this.minecraft.gameMode.adjustPlayer(this.minecraft.player);
        this.minecraft.setCameraEntity((Entity)this.minecraft.player);
        this.startWaitingForNewLevel(this.minecraft.player, this.level, LevelLoadingScreen.Reason.OTHER);
        this.minecraft.player.setReducedDebugInfo(clientboundLoginPacket.reducedDebugInfo());
        this.minecraft.player.setShowDeathScreen(clientboundLoginPacket.showDeathScreen());
        this.minecraft.player.setDoLimitedCrafting(clientboundLoginPacket.doLimitedCrafting());
        this.minecraft.player.setLastDeathLocation(commonPlayerSpawnInfo.lastDeathLocation());
        this.minecraft.player.setPortalCooldown(commonPlayerSpawnInfo.portalCooldown());
        this.minecraft.gameMode.setLocalMode(commonPlayerSpawnInfo.gameType(), commonPlayerSpawnInfo.previousGameType());
        this.minecraft.options.setServerRenderDistance(clientboundLoginPacket.chunkRadius());
        this.chatSession = null;
        this.signedMessageEncoder = SignedMessageChain.Encoder.UNSIGNED;
        this.nextChatIndex = 0;
        this.lastSeenMessages = new LastSeenMessagesTracker(20);
        this.messageSignatureCache = MessageSignatureCache.createDefault();
        if (this.connection.isEncrypted()) {
            this.prepareKeyPair();
        }
        this.telemetryManager.onPlayerInfoReceived(commonPlayerSpawnInfo.gameType(), clientboundLoginPacket.hardcore());
        this.minecraft.quickPlayLog().log(this.minecraft);
        this.serverEnforcesSecureChat = clientboundLoginPacket.enforcesSecureChat();
        if (this.serverData != null && !this.seenInsecureChatWarning && !this.enforcesSecureChat()) {
            SystemToast systemToast = SystemToast.multiline(this.minecraft, SystemToast.SystemToastId.UNSECURE_SERVER_WARNING, UNSECURE_SERVER_TOAST_TITLE, UNSERURE_SERVER_TOAST);
            this.minecraft.getToastManager().addToast(systemToast);
            this.seenInsecureChatWarning = true;
        }
    }

    public void handleAddEntity(ClientboundAddEntityPacket clientboundAddEntityPacket) {
        Player player;
        UUID uUID;
        PlayerInfo playerInfo;
        Entity entity;
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundAddEntityPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        if (this.removedPlayerVehicleId.isPresent() && this.removedPlayerVehicleId.getAsInt() == clientboundAddEntityPacket.getId()) {
            this.removedPlayerVehicleId = OptionalInt.empty();
        }
        if ((entity = this.createEntityFromPacket(clientboundAddEntityPacket)) != null) {
            entity.recreateFromPacket(clientboundAddEntityPacket);
            this.level.addEntity(entity);
            this.postAddEntitySoundInstance(entity);
        } else {
            LOGGER.warn("Skipping Entity with id {}", (Object)clientboundAddEntityPacket.getType());
        }
        if (entity instanceof Player && (playerInfo = this.playerInfoMap.get(uUID = (player = (Player)entity).getUUID())) != null) {
            this.seenPlayers.put(uUID, playerInfo);
        }
    }

    private @Nullable Entity createEntityFromPacket(ClientboundAddEntityPacket clientboundAddEntityPacket) {
        EntityType entityType = clientboundAddEntityPacket.getType();
        if (entityType == EntityType.PLAYER) {
            PlayerInfo playerInfo = this.getPlayerInfo(clientboundAddEntityPacket.getUUID());
            if (playerInfo == null) {
                LOGGER.warn("Server attempted to add player prior to sending player info (Player id {})", (Object)clientboundAddEntityPacket.getUUID());
                return null;
            }
            return new RemotePlayer(this.level, playerInfo.getProfile());
        }
        return entityType.create((Level)this.level, EntitySpawnReason.LOAD);
    }

    private void postAddEntitySoundInstance(Entity entity) {
        if (entity instanceof AbstractMinecart) {
            AbstractMinecart abstractMinecart = (AbstractMinecart)entity;
            this.minecraft.getSoundManager().play(new MinecartSoundInstance(abstractMinecart));
        } else if (entity instanceof Bee) {
            Bee bee = (Bee)entity;
            boolean bl = bee.isAngry();
            BeeSoundInstance beeSoundInstance = bl ? new BeeAggressiveSoundInstance(bee) : new BeeFlyingSoundInstance(bee);
            this.minecraft.getSoundManager().queueTickingSound(beeSoundInstance);
        }
    }

    public void handleSetEntityMotion(ClientboundSetEntityMotionPacket clientboundSetEntityMotionPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetEntityMotionPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(clientboundSetEntityMotionPacket.getId());
        if (entity == null) {
            return;
        }
        entity.lerpMotion(clientboundSetEntityMotionPacket.getMovement());
    }

    public void handleSetEntityData(ClientboundSetEntityDataPacket clientboundSetEntityDataPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetEntityDataPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(clientboundSetEntityDataPacket.id());
        if (entity != null) {
            entity.getEntityData().assignValues(clientboundSetEntityDataPacket.packedItems());
        }
    }

    public void handleEntityPositionSync(ClientboundEntityPositionSyncPacket clientboundEntityPositionSyncPacket) {
        boolean bl;
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundEntityPositionSyncPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(clientboundEntityPositionSyncPacket.id());
        if (entity == null) {
            return;
        }
        Vec3 vec3 = clientboundEntityPositionSyncPacket.values().position();
        entity.getPositionCodec().setBase(vec3);
        if (entity.isLocalInstanceAuthoritative()) {
            return;
        }
        float f = clientboundEntityPositionSyncPacket.values().yRot();
        float g = clientboundEntityPositionSyncPacket.values().xRot();
        boolean bl2 = bl = entity.position().distanceToSqr(vec3) > 4096.0;
        if (this.level.isTickingEntity(entity) && !bl) {
            entity.moveOrInterpolateTo(vec3, f, g);
        } else {
            entity.snapTo(vec3, f, g);
        }
        if (!entity.isInterpolating() && entity.hasIndirectPassenger((Entity)this.minecraft.player)) {
            entity.positionRider((Entity)this.minecraft.player);
            this.minecraft.player.setOldPosAndRot();
        }
        entity.setOnGround(clientboundEntityPositionSyncPacket.onGround());
    }

    public void handleTeleportEntity(ClientboundTeleportEntityPacket clientboundTeleportEntityPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundTeleportEntityPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(clientboundTeleportEntityPacket.id());
        if (entity == null) {
            if (this.removedPlayerVehicleId.isPresent() && this.removedPlayerVehicleId.getAsInt() == clientboundTeleportEntityPacket.id()) {
                LOGGER.debug("Trying to teleport entity with id {}, that was formerly player vehicle, applying teleport to player instead", (Object)clientboundTeleportEntityPacket.id());
                ClientPacketListener.setValuesFromPositionPacket(clientboundTeleportEntityPacket.change(), clientboundTeleportEntityPacket.relatives(), (Entity)this.minecraft.player, false);
                this.connection.send((Packet)new ServerboundMovePlayerPacket.PosRot(this.minecraft.player.getX(), this.minecraft.player.getY(), this.minecraft.player.getZ(), this.minecraft.player.getYRot(), this.minecraft.player.getXRot(), false, false));
            }
            return;
        }
        boolean bl = clientboundTeleportEntityPacket.relatives().contains(Relative.X) || clientboundTeleportEntityPacket.relatives().contains(Relative.Y) || clientboundTeleportEntityPacket.relatives().contains(Relative.Z);
        boolean bl2 = this.level.isTickingEntity(entity) || !entity.isLocalInstanceAuthoritative() || bl;
        boolean bl3 = ClientPacketListener.setValuesFromPositionPacket(clientboundTeleportEntityPacket.change(), clientboundTeleportEntityPacket.relatives(), entity, bl2);
        entity.setOnGround(clientboundTeleportEntityPacket.onGround());
        if (!bl3 && entity.hasIndirectPassenger((Entity)this.minecraft.player)) {
            entity.positionRider((Entity)this.minecraft.player);
            this.minecraft.player.setOldPosAndRot();
            if (entity.isLocalInstanceAuthoritative()) {
                this.connection.send((Packet)ServerboundMoveVehiclePacket.fromEntity((Entity)entity));
            }
        }
    }

    public void handleTickingState(ClientboundTickingStatePacket clientboundTickingStatePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundTickingStatePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        if (this.minecraft.level == null) {
            return;
        }
        TickRateManager tickRateManager = this.minecraft.level.tickRateManager();
        tickRateManager.setTickRate(clientboundTickingStatePacket.tickRate());
        tickRateManager.setFrozen(clientboundTickingStatePacket.isFrozen());
    }

    public void handleTickingStep(ClientboundTickingStepPacket clientboundTickingStepPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundTickingStepPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        if (this.minecraft.level == null) {
            return;
        }
        TickRateManager tickRateManager = this.minecraft.level.tickRateManager();
        tickRateManager.setFrozenTicksToRun(clientboundTickingStepPacket.tickSteps());
    }

    public void handleSetHeldSlot(ClientboundSetHeldSlotPacket clientboundSetHeldSlotPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetHeldSlotPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        if (Inventory.isHotbarSlot((int)clientboundSetHeldSlotPacket.slot())) {
            this.minecraft.player.getInventory().setSelectedSlot(clientboundSetHeldSlotPacket.slot());
        }
    }

    public void handleMoveEntity(ClientboundMoveEntityPacket clientboundMoveEntityPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundMoveEntityPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = clientboundMoveEntityPacket.getEntity((Level)this.level);
        if (entity == null) {
            return;
        }
        if (entity.isLocalInstanceAuthoritative()) {
            VecDeltaCodec vecDeltaCodec = entity.getPositionCodec();
            Vec3 vec3 = vecDeltaCodec.decode((long)clientboundMoveEntityPacket.getXa(), (long)clientboundMoveEntityPacket.getYa(), (long)clientboundMoveEntityPacket.getZa());
            vecDeltaCodec.setBase(vec3);
            return;
        }
        if (clientboundMoveEntityPacket.hasPosition()) {
            VecDeltaCodec vecDeltaCodec = entity.getPositionCodec();
            Vec3 vec3 = vecDeltaCodec.decode((long)clientboundMoveEntityPacket.getXa(), (long)clientboundMoveEntityPacket.getYa(), (long)clientboundMoveEntityPacket.getZa());
            vecDeltaCodec.setBase(vec3);
            if (clientboundMoveEntityPacket.hasRotation()) {
                entity.moveOrInterpolateTo(vec3, clientboundMoveEntityPacket.getYRot(), clientboundMoveEntityPacket.getXRot());
            } else {
                entity.moveOrInterpolateTo(vec3);
            }
        } else if (clientboundMoveEntityPacket.hasRotation()) {
            entity.moveOrInterpolateTo(clientboundMoveEntityPacket.getYRot(), clientboundMoveEntityPacket.getXRot());
        }
        entity.setOnGround(clientboundMoveEntityPacket.isOnGround());
    }

    public void handleMinecartAlongTrack(ClientboundMoveMinecartPacket clientboundMoveMinecartPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundMoveMinecartPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = clientboundMoveMinecartPacket.getEntity((Level)this.level);
        if (!(entity instanceof AbstractMinecart)) {
            return;
        }
        AbstractMinecart abstractMinecart = (AbstractMinecart)entity;
        MinecartBehavior minecartBehavior = abstractMinecart.getBehavior();
        if (minecartBehavior instanceof NewMinecartBehavior) {
            NewMinecartBehavior newMinecartBehavior = (NewMinecartBehavior)minecartBehavior;
            newMinecartBehavior.lerpSteps.addAll(clientboundMoveMinecartPacket.lerpSteps());
        }
    }

    public void handleRotateMob(ClientboundRotateHeadPacket clientboundRotateHeadPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundRotateHeadPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = clientboundRotateHeadPacket.getEntity((Level)this.level);
        if (entity == null) {
            return;
        }
        entity.lerpHeadTo(clientboundRotateHeadPacket.getYHeadRot(), 3);
    }

    public void handleRemoveEntities(ClientboundRemoveEntitiesPacket clientboundRemoveEntitiesPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundRemoveEntitiesPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        clientboundRemoveEntitiesPacket.getEntityIds().forEach(i -> {
            Entity entity = this.level.getEntity(i);
            if (entity == null) {
                return;
            }
            if (entity.hasIndirectPassenger((Entity)this.minecraft.player)) {
                LOGGER.debug("Remove entity {}:{} that has player as passenger", (Object)entity.getType(), (Object)i);
                this.removedPlayerVehicleId = OptionalInt.of(i);
            }
            this.level.removeEntity(i, Entity.RemovalReason.DISCARDED);
            this.debugSubscriber.dropEntity(entity);
        });
    }

    public void handleMovePlayer(ClientboundPlayerPositionPacket clientboundPlayerPositionPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundPlayerPositionPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        LocalPlayer player = this.minecraft.player;
        if (!player.isPassenger()) {
            ClientPacketListener.setValuesFromPositionPacket(clientboundPlayerPositionPacket.change(), clientboundPlayerPositionPacket.relatives(), (Entity)player, false);
        }
        this.connection.send((Packet)new ServerboundAcceptTeleportationPacket(clientboundPlayerPositionPacket.id()));
        this.connection.send((Packet)new ServerboundMovePlayerPacket.PosRot(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), false, false));
    }

    private static boolean setValuesFromPositionPacket(PositionMoveRotation positionMoveRotation, Set<Relative> set, Entity entity, boolean bl) {
        boolean bl2;
        PositionMoveRotation positionMoveRotation2 = PositionMoveRotation.of((Entity)entity);
        PositionMoveRotation positionMoveRotation3 = PositionMoveRotation.calculateAbsolute((PositionMoveRotation)positionMoveRotation2, (PositionMoveRotation)positionMoveRotation, set);
        boolean bl3 = bl2 = positionMoveRotation2.position().distanceToSqr(positionMoveRotation3.position()) > 4096.0;
        if (bl && !bl2) {
            entity.moveOrInterpolateTo(positionMoveRotation3.position(), positionMoveRotation3.yRot(), positionMoveRotation3.xRot());
            entity.setDeltaMovement(positionMoveRotation3.deltaMovement());
            return true;
        }
        entity.setPos(positionMoveRotation3.position());
        entity.setDeltaMovement(positionMoveRotation3.deltaMovement());
        entity.setYRot(positionMoveRotation3.yRot());
        entity.setXRot(positionMoveRotation3.xRot());
        PositionMoveRotation positionMoveRotation4 = new PositionMoveRotation(entity.oldPosition(), Vec3.ZERO, entity.yRotO, entity.xRotO);
        PositionMoveRotation positionMoveRotation5 = PositionMoveRotation.calculateAbsolute((PositionMoveRotation)positionMoveRotation4, (PositionMoveRotation)positionMoveRotation, set);
        entity.setOldPosAndRot(positionMoveRotation5.position(), positionMoveRotation5.yRot(), positionMoveRotation5.xRot());
        return false;
    }

    public void handleRotatePlayer(ClientboundPlayerRotationPacket clientboundPlayerRotationPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundPlayerRotationPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        LocalPlayer player = this.minecraft.player;
        Set set = Relative.rotation((boolean)clientboundPlayerRotationPacket.relativeY(), (boolean)clientboundPlayerRotationPacket.relativeX());
        PositionMoveRotation positionMoveRotation = PositionMoveRotation.of((Entity)player);
        PositionMoveRotation positionMoveRotation2 = PositionMoveRotation.calculateAbsolute((PositionMoveRotation)positionMoveRotation, (PositionMoveRotation)positionMoveRotation.withRotation(clientboundPlayerRotationPacket.yRot(), clientboundPlayerRotationPacket.xRot()), (Set)set);
        player.setYRot(positionMoveRotation2.yRot());
        player.setXRot(positionMoveRotation2.xRot());
        player.setOldRot();
        this.connection.send((Packet)new ServerboundMovePlayerPacket.Rot(player.getYRot(), player.getXRot(), false, false));
    }

    public void handleChunkBlocksUpdate(ClientboundSectionBlocksUpdatePacket clientboundSectionBlocksUpdatePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSectionBlocksUpdatePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        clientboundSectionBlocksUpdatePacket.runUpdates((blockPos, blockState) -> this.level.setServerVerifiedBlockState((BlockPos)blockPos, (BlockState)blockState, 19));
    }

    public void handleLevelChunkWithLight(ClientboundLevelChunkWithLightPacket clientboundLevelChunkWithLightPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundLevelChunkWithLightPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        int i = clientboundLevelChunkWithLightPacket.getX();
        int j = clientboundLevelChunkWithLightPacket.getZ();
        this.updateLevelChunk(i, j, clientboundLevelChunkWithLightPacket.getChunkData());
        ClientboundLightUpdatePacketData clientboundLightUpdatePacketData = clientboundLevelChunkWithLightPacket.getLightData();
        this.level.queueLightUpdate(() -> {
            this.applyLightData(i, j, clientboundLightUpdatePacketData, false);
            LevelChunk levelChunk = this.level.getChunkSource().getChunk(i, j, false);
            if (levelChunk != null) {
                this.enableChunkLight(levelChunk, i, j);
                this.minecraft.levelRenderer.onChunkReadyToRender(levelChunk.getPos());
            }
        });
    }

    public void handleChunksBiomes(ClientboundChunksBiomesPacket clientboundChunksBiomesPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundChunksBiomesPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        for (ClientboundChunksBiomesPacket.ChunkBiomeData chunkBiomeData : clientboundChunksBiomesPacket.chunkBiomeData()) {
            this.level.getChunkSource().replaceBiomes(chunkBiomeData.pos().x, chunkBiomeData.pos().z, chunkBiomeData.getReadBuffer());
        }
        for (ClientboundChunksBiomesPacket.ChunkBiomeData chunkBiomeData : clientboundChunksBiomesPacket.chunkBiomeData()) {
            this.level.onChunkLoaded(new ChunkPos(chunkBiomeData.pos().x, chunkBiomeData.pos().z));
        }
        for (ClientboundChunksBiomesPacket.ChunkBiomeData chunkBiomeData : clientboundChunksBiomesPacket.chunkBiomeData()) {
            for (int i = -1; i <= 1; ++i) {
                for (int j = -1; j <= 1; ++j) {
                    for (int k = this.level.getMinSectionY(); k <= this.level.getMaxSectionY(); ++k) {
                        this.minecraft.levelRenderer.setSectionDirty(chunkBiomeData.pos().x + i, k, chunkBiomeData.pos().z + j);
                    }
                }
            }
        }
    }

    private void updateLevelChunk(int i, int j, ClientboundLevelChunkPacketData clientboundLevelChunkPacketData) {
        this.level.getChunkSource().replaceWithPacketData(i, j, clientboundLevelChunkPacketData.getReadBuffer(), clientboundLevelChunkPacketData.getHeightmaps(), clientboundLevelChunkPacketData.getBlockEntitiesTagsConsumer(i, j));
    }

    private void enableChunkLight(LevelChunk levelChunk, int i, int j) {
        LevelLightEngine levelLightEngine = this.level.getChunkSource().getLightEngine();
        LevelChunkSection[] levelChunkSections = levelChunk.getSections();
        ChunkPos chunkPos = levelChunk.getPos();
        for (int k = 0; k < levelChunkSections.length; ++k) {
            LevelChunkSection levelChunkSection = levelChunkSections[k];
            int l = this.level.getSectionYFromSectionIndex(k);
            levelLightEngine.updateSectionStatus(SectionPos.of((ChunkPos)chunkPos, (int)l), levelChunkSection.hasOnlyAir());
        }
        this.level.setSectionRangeDirty(i - 1, this.level.getMinSectionY(), j - 1, i + 1, this.level.getMaxSectionY(), j + 1);
    }

    public void handleForgetLevelChunk(ClientboundForgetLevelChunkPacket clientboundForgetLevelChunkPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundForgetLevelChunkPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.level.getChunkSource().drop(clientboundForgetLevelChunkPacket.pos());
        this.debugSubscriber.dropChunk(clientboundForgetLevelChunkPacket.pos());
        this.queueLightRemoval(clientboundForgetLevelChunkPacket);
    }

    private void queueLightRemoval(ClientboundForgetLevelChunkPacket clientboundForgetLevelChunkPacket) {
        ChunkPos chunkPos = clientboundForgetLevelChunkPacket.pos();
        this.level.queueLightUpdate(() -> {
            int i;
            LevelLightEngine levelLightEngine = this.level.getLightEngine();
            levelLightEngine.setLightEnabled(chunkPos, false);
            for (i = levelLightEngine.getMinLightSection(); i < levelLightEngine.getMaxLightSection(); ++i) {
                SectionPos sectionPos = SectionPos.of((ChunkPos)chunkPos, (int)i);
                levelLightEngine.queueSectionData(LightLayer.BLOCK, sectionPos, null);
                levelLightEngine.queueSectionData(LightLayer.SKY, sectionPos, null);
            }
            for (i = this.level.getMinSectionY(); i <= this.level.getMaxSectionY(); ++i) {
                levelLightEngine.updateSectionStatus(SectionPos.of((ChunkPos)chunkPos, (int)i), true);
            }
        });
    }

    public void handleBlockUpdate(ClientboundBlockUpdatePacket clientboundBlockUpdatePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundBlockUpdatePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.level.setServerVerifiedBlockState(clientboundBlockUpdatePacket.getPos(), clientboundBlockUpdatePacket.getBlockState(), 19);
    }

    public void handleConfigurationStart(ClientboundStartConfigurationPacket clientboundStartConfigurationPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundStartConfigurationPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.getChatListener().flushQueue();
        this.sendChatAcknowledgement();
        ChatComponent.State state = this.minecraft.gui.getChat().storeState();
        this.minecraft.clearClientLevel(new ServerReconfigScreen(RECONFIGURE_SCREEN_MESSAGE, this.connection));
        this.connection.setupInboundProtocol(ConfigurationProtocols.CLIENTBOUND, (PacketListener)new ClientConfigurationPacketListenerImpl(this.minecraft, this.connection, new CommonListenerCookie(new LevelLoadTracker(), this.localGameProfile, this.telemetryManager, this.registryAccess, this.enabledFeatures, this.serverBrand, this.serverData, this.postDisconnectScreen, this.serverCookies, state, this.customReportDetails, this.serverLinks(), this.seenPlayers, this.seenInsecureChatWarning)));
        this.send((Packet<?>)ServerboundConfigurationAcknowledgedPacket.INSTANCE);
        this.connection.setupOutboundProtocol(ConfigurationProtocols.SERVERBOUND);
    }

    public void handleTakeItemEntity(ClientboundTakeItemEntityPacket clientboundTakeItemEntityPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundTakeItemEntityPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(clientboundTakeItemEntityPacket.getItemId());
        Object livingEntity = (LivingEntity)this.level.getEntity(clientboundTakeItemEntityPacket.getPlayerId());
        if (livingEntity == null) {
            livingEntity = this.minecraft.player;
        }
        if (entity != null) {
            if (entity instanceof ExperienceOrb) {
                this.level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1f, (this.random.nextFloat() - this.random.nextFloat()) * 0.35f + 0.9f, false);
            } else {
                this.level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, (this.random.nextFloat() - this.random.nextFloat()) * 1.4f + 2.0f, false);
            }
            EntityRenderState entityRenderState = this.minecraft.getEntityRenderDispatcher().extractEntity(entity, 1.0f);
            this.minecraft.particleEngine.add(new ItemPickupParticle(this.level, entityRenderState, (Entity)livingEntity, entity.getDeltaMovement()));
            if (entity instanceof ItemEntity) {
                ItemEntity itemEntity = (ItemEntity)entity;
                ItemStack itemStack = itemEntity.getItem();
                if (!itemStack.isEmpty()) {
                    itemStack.shrink(clientboundTakeItemEntityPacket.getAmount());
                }
                if (itemStack.isEmpty()) {
                    this.level.removeEntity(clientboundTakeItemEntityPacket.getItemId(), Entity.RemovalReason.DISCARDED);
                }
            } else if (!(entity instanceof ExperienceOrb)) {
                this.level.removeEntity(clientboundTakeItemEntityPacket.getItemId(), Entity.RemovalReason.DISCARDED);
            }
        }
    }

    public void handleSystemChat(ClientboundSystemChatPacket clientboundSystemChatPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSystemChatPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.getChatListener().handleSystemMessage(clientboundSystemChatPacket.content(), clientboundSystemChatPacket.overlay());
    }

    public void handlePlayerChat(ClientboundPlayerChatPacket clientboundPlayerChatPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundPlayerChatPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        int i = this.nextChatIndex++;
        if (clientboundPlayerChatPacket.globalIndex() != i) {
            LOGGER.error("Missing or out-of-order chat message from server, expected index {} but got {}", (Object)i, (Object)clientboundPlayerChatPacket.globalIndex());
            this.connection.disconnect(BAD_CHAT_INDEX);
            return;
        }
        Optional optional = clientboundPlayerChatPacket.body().unpack(this.messageSignatureCache);
        if (optional.isEmpty()) {
            LOGGER.error("Message from player with ID {} referenced unrecognized signature id", (Object)clientboundPlayerChatPacket.sender());
            this.connection.disconnect(INVALID_PACKET);
            return;
        }
        this.messageSignatureCache.push((SignedMessageBody)optional.get(), clientboundPlayerChatPacket.signature());
        UUID uUID = clientboundPlayerChatPacket.sender();
        PlayerInfo playerInfo = this.getPlayerInfo(uUID);
        if (playerInfo == null) {
            LOGGER.error("Received player chat packet for unknown player with ID: {}", (Object)uUID);
            this.minecraft.getChatListener().handleChatMessageError(uUID, clientboundPlayerChatPacket.signature(), clientboundPlayerChatPacket.chatType());
            return;
        }
        RemoteChatSession remoteChatSession = playerInfo.getChatSession();
        SignedMessageLink signedMessageLink = remoteChatSession != null ? new SignedMessageLink(clientboundPlayerChatPacket.index(), uUID, remoteChatSession.sessionId()) : SignedMessageLink.unsigned((UUID)uUID);
        PlayerChatMessage playerChatMessage = new PlayerChatMessage(signedMessageLink, clientboundPlayerChatPacket.signature(), (SignedMessageBody)optional.get(), clientboundPlayerChatPacket.unsignedContent(), clientboundPlayerChatPacket.filterMask());
        playerChatMessage = playerInfo.getMessageValidator().updateAndValidate(playerChatMessage);
        if (playerChatMessage != null) {
            this.minecraft.getChatListener().handlePlayerChatMessage(playerChatMessage, playerInfo.getProfile(), clientboundPlayerChatPacket.chatType());
        } else {
            this.minecraft.getChatListener().handleChatMessageError(uUID, clientboundPlayerChatPacket.signature(), clientboundPlayerChatPacket.chatType());
        }
    }

    public void handleDisguisedChat(ClientboundDisguisedChatPacket clientboundDisguisedChatPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundDisguisedChatPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.getChatListener().handleDisguisedChatMessage(clientboundDisguisedChatPacket.message(), clientboundDisguisedChatPacket.chatType());
    }

    public void handleDeleteChat(ClientboundDeleteChatPacket clientboundDeleteChatPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundDeleteChatPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Optional optional = clientboundDeleteChatPacket.messageSignature().unpack(this.messageSignatureCache);
        if (optional.isEmpty()) {
            this.connection.disconnect(INVALID_PACKET);
            return;
        }
        this.lastSeenMessages.ignorePending((MessageSignature)optional.get());
        if (!this.minecraft.getChatListener().removeFromDelayedMessageQueue((MessageSignature)optional.get())) {
            this.minecraft.gui.getChat().deleteMessage((MessageSignature)optional.get());
        }
    }

    public void handleAnimate(ClientboundAnimatePacket clientboundAnimatePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundAnimatePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(clientboundAnimatePacket.getId());
        if (entity == null) {
            return;
        }
        if (clientboundAnimatePacket.getAction() == 0) {
            LivingEntity livingEntity = (LivingEntity)entity;
            livingEntity.swing(InteractionHand.MAIN_HAND);
        } else if (clientboundAnimatePacket.getAction() == 3) {
            LivingEntity livingEntity = (LivingEntity)entity;
            livingEntity.swing(InteractionHand.OFF_HAND);
        } else if (clientboundAnimatePacket.getAction() == 2) {
            Player player = (Player)entity;
            player.stopSleepInBed(false, false);
        } else if (clientboundAnimatePacket.getAction() == 4) {
            this.minecraft.particleEngine.createTrackingEmitter(entity, (ParticleOptions)ParticleTypes.CRIT);
        } else if (clientboundAnimatePacket.getAction() == 5) {
            this.minecraft.particleEngine.createTrackingEmitter(entity, (ParticleOptions)ParticleTypes.ENCHANTED_HIT);
        }
    }

    public void handleHurtAnimation(ClientboundHurtAnimationPacket clientboundHurtAnimationPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundHurtAnimationPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(clientboundHurtAnimationPacket.id());
        if (entity == null) {
            return;
        }
        entity.animateHurt(clientboundHurtAnimationPacket.yaw());
    }

    public void handleSetTime(ClientboundSetTimePacket clientboundSetTimePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetTimePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.level.setTimeFromServer(clientboundSetTimePacket.gameTime(), clientboundSetTimePacket.dayTime(), clientboundSetTimePacket.tickDayTime());
        this.telemetryManager.setTime(clientboundSetTimePacket.gameTime());
    }

    public void handleSetSpawn(ClientboundSetDefaultSpawnPositionPacket clientboundSetDefaultSpawnPositionPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetDefaultSpawnPositionPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.level.setRespawnData(clientboundSetDefaultSpawnPositionPacket.respawnData());
    }

    public void handleSetEntityPassengersPacket(ClientboundSetPassengersPacket clientboundSetPassengersPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetPassengersPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(clientboundSetPassengersPacket.getVehicle());
        if (entity == null) {
            LOGGER.warn("Received passengers for unknown entity");
            return;
        }
        boolean bl = entity.hasIndirectPassenger((Entity)this.minecraft.player);
        entity.ejectPassengers();
        for (int i : clientboundSetPassengersPacket.getPassengers()) {
            Entity entity2 = this.level.getEntity(i);
            if (entity2 == null) continue;
            entity2.startRiding(entity, true, false);
            if (entity2 != this.minecraft.player) continue;
            this.removedPlayerVehicleId = OptionalInt.empty();
            if (bl) continue;
            if (entity instanceof AbstractBoat) {
                this.minecraft.player.yRotO = entity.getYRot();
                this.minecraft.player.setYRot(entity.getYRot());
                this.minecraft.player.setYHeadRot(entity.getYRot());
            }
            MutableComponent component = Component.translatable((String)"mount.onboard", (Object[])new Object[]{this.minecraft.options.keyShift.getTranslatedKeyMessage()});
            this.minecraft.gui.setOverlayMessage((Component)component, false);
            this.minecraft.getNarrator().saySystemNow((Component)component);
        }
    }

    public void handleEntityLinkPacket(ClientboundSetEntityLinkPacket clientboundSetEntityLinkPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetEntityLinkPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(clientboundSetEntityLinkPacket.getSourceId());
        if (entity instanceof Leashable) {
            Leashable leashable = (Leashable)entity;
            leashable.setDelayedLeashHolderId(clientboundSetEntityLinkPacket.getDestId());
        }
    }

    private static ItemStack findTotem(Player player) {
        for (InteractionHand interactionHand : InteractionHand.values()) {
            ItemStack itemStack = player.getItemInHand(interactionHand);
            if (!itemStack.has(DataComponents.DEATH_PROTECTION)) continue;
            return itemStack;
        }
        return new ItemStack((ItemLike)Items.TOTEM_OF_UNDYING);
    }

    public void handleEntityEvent(ClientboundEntityEventPacket clientboundEntityEventPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundEntityEventPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = clientboundEntityEventPacket.getEntity((Level)this.level);
        if (entity != null) {
            switch (clientboundEntityEventPacket.getEventId()) {
                case 63: {
                    this.minecraft.getSoundManager().play(new SnifferSoundInstance((Sniffer)entity));
                    break;
                }
                case 21: {
                    this.minecraft.getSoundManager().play(new GuardianAttackSoundInstance((Guardian)entity));
                    break;
                }
                case 35: {
                    int i = 40;
                    this.minecraft.particleEngine.createTrackingEmitter(entity, (ParticleOptions)ParticleTypes.TOTEM_OF_UNDYING, 30);
                    this.level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, entity.getSoundSource(), 1.0f, 1.0f, false);
                    if (entity != this.minecraft.player) break;
                    this.minecraft.gameRenderer.displayItemActivation(ClientPacketListener.findTotem(this.minecraft.player));
                    break;
                }
                default: {
                    entity.handleEntityEvent(clientboundEntityEventPacket.getEventId());
                }
            }
        }
    }

    public void handleDamageEvent(ClientboundDamageEventPacket clientboundDamageEventPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundDamageEventPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(clientboundDamageEventPacket.entityId());
        if (entity == null) {
            return;
        }
        entity.handleDamageEvent(clientboundDamageEventPacket.getSource((Level)this.level));
    }

    public void handleSetHealth(ClientboundSetHealthPacket clientboundSetHealthPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetHealthPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.player.hurtTo(clientboundSetHealthPacket.getHealth());
        this.minecraft.player.getFoodData().setFoodLevel(clientboundSetHealthPacket.getFood());
        this.minecraft.player.getFoodData().setSaturation(clientboundSetHealthPacket.getSaturation());
    }

    public void handleSetExperience(ClientboundSetExperiencePacket clientboundSetExperiencePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetExperiencePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.player.setExperienceValues(clientboundSetExperiencePacket.getExperienceProgress(), clientboundSetExperiencePacket.getTotalExperience(), clientboundSetExperiencePacket.getExperienceLevel());
    }

    public void handleRespawn(ClientboundRespawnPacket clientboundRespawnPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundRespawnPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        CommonPlayerSpawnInfo commonPlayerSpawnInfo = clientboundRespawnPacket.commonPlayerSpawnInfo();
        ResourceKey resourceKey = commonPlayerSpawnInfo.dimension();
        Holder holder = commonPlayerSpawnInfo.dimensionType();
        LocalPlayer localPlayer = this.minecraft.player;
        ResourceKey resourceKey2 = localPlayer.level().dimension();
        boolean bl = resourceKey != resourceKey2;
        LevelLoadingScreen.Reason reason = this.determineLevelLoadingReason(localPlayer.isDeadOrDying(), (ResourceKey<Level>)resourceKey, (ResourceKey<Level>)resourceKey2);
        if (bl) {
            ClientLevel.ClientLevelData clientLevelData;
            Map<MapId, MapItemSavedData> map = this.level.getAllMapData();
            boolean bl2 = commonPlayerSpawnInfo.isDebug();
            boolean bl3 = commonPlayerSpawnInfo.isFlat();
            int i = commonPlayerSpawnInfo.seaLevel();
            this.levelData = clientLevelData = new ClientLevel.ClientLevelData(this.levelData.getDifficulty(), this.levelData.isHardcore(), bl3);
            this.level = new ClientLevel(this, clientLevelData, (ResourceKey<Level>)resourceKey, (Holder<DimensionType>)holder, this.serverChunkRadius, this.serverSimulationDistance, this.minecraft.levelRenderer, bl2, commonPlayerSpawnInfo.seed(), i);
            this.level.addMapData(map);
            this.minecraft.setLevel(this.level);
            this.debugSubscriber.dropLevel();
        }
        this.minecraft.setCameraEntity(null);
        if (localPlayer.hasContainerOpen()) {
            localPlayer.closeContainer();
        }
        LocalPlayer localPlayer2 = clientboundRespawnPacket.shouldKeep((byte)2) ? this.minecraft.gameMode.createPlayer(this.level, localPlayer.getStats(), localPlayer.getRecipeBook(), localPlayer.getLastSentInput(), localPlayer.isSprinting()) : this.minecraft.gameMode.createPlayer(this.level, localPlayer.getStats(), localPlayer.getRecipeBook());
        this.setClientLoaded(false);
        this.startWaitingForNewLevel(localPlayer2, this.level, reason);
        localPlayer2.setId(localPlayer.getId());
        this.minecraft.player = localPlayer2;
        if (bl) {
            this.minecraft.getMusicManager().stopPlaying();
        }
        this.minecraft.setCameraEntity((Entity)localPlayer2);
        if (clientboundRespawnPacket.shouldKeep((byte)2)) {
            List list = localPlayer.getEntityData().getNonDefaultValues();
            if (list != null) {
                localPlayer2.getEntityData().assignValues(list);
            }
            localPlayer2.setDeltaMovement(localPlayer.getDeltaMovement());
            localPlayer2.setYRot(localPlayer.getYRot());
            localPlayer2.setXRot(localPlayer.getXRot());
        } else {
            localPlayer2.resetPos();
            localPlayer2.setYRot(-180.0f);
        }
        if (clientboundRespawnPacket.shouldKeep((byte)1)) {
            localPlayer2.getAttributes().assignAllValues(localPlayer.getAttributes());
        } else {
            localPlayer2.getAttributes().assignBaseValues(localPlayer.getAttributes());
        }
        this.level.addEntity((Entity)localPlayer2);
        localPlayer2.input = new KeyboardInput(this.minecraft.options);
        this.minecraft.gameMode.adjustPlayer(localPlayer2);
        localPlayer2.setReducedDebugInfo(localPlayer.isReducedDebugInfo());
        localPlayer2.setShowDeathScreen(localPlayer.shouldShowDeathScreen());
        localPlayer2.setLastDeathLocation(commonPlayerSpawnInfo.lastDeathLocation());
        localPlayer2.setPortalCooldown(commonPlayerSpawnInfo.portalCooldown());
        localPlayer2.portalEffectIntensity = localPlayer.portalEffectIntensity;
        localPlayer2.oPortalEffectIntensity = localPlayer.oPortalEffectIntensity;
        if (this.minecraft.screen instanceof DeathScreen || this.minecraft.screen instanceof DeathScreen.TitleConfirmScreen) {
            this.minecraft.setScreen(null);
        }
        this.minecraft.gameMode.setLocalMode(commonPlayerSpawnInfo.gameType(), commonPlayerSpawnInfo.previousGameType());
    }

    private LevelLoadingScreen.Reason determineLevelLoadingReason(boolean bl, ResourceKey<Level> resourceKey, ResourceKey<Level> resourceKey2) {
        LevelLoadingScreen.Reason reason = LevelLoadingScreen.Reason.OTHER;
        if (!bl) {
            if (resourceKey == Level.NETHER || resourceKey2 == Level.NETHER) {
                reason = LevelLoadingScreen.Reason.NETHER_PORTAL;
            } else if (resourceKey == Level.END || resourceKey2 == Level.END) {
                reason = LevelLoadingScreen.Reason.END_PORTAL;
            }
        }
        return reason;
    }

    public void handleExplosion(ClientboundExplodePacket clientboundExplodePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundExplodePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Vec3 vec3 = clientboundExplodePacket.center();
        this.minecraft.level.playLocalSound(vec3.x(), vec3.y(), vec3.z(), (SoundEvent)clientboundExplodePacket.explosionSound().value(), SoundSource.BLOCKS, 4.0f, (1.0f + (this.minecraft.level.random.nextFloat() - this.minecraft.level.random.nextFloat()) * 0.2f) * 0.7f, false);
        this.minecraft.level.addParticle(clientboundExplodePacket.explosionParticle(), vec3.x(), vec3.y(), vec3.z(), 1.0, 0.0, 0.0);
        this.minecraft.level.trackExplosionEffects(vec3, clientboundExplodePacket.radius(), clientboundExplodePacket.blockCount(), (WeightedList<ExplosionParticleInfo>)clientboundExplodePacket.blockParticles());
        clientboundExplodePacket.playerKnockback().ifPresent(arg_0 -> ((LocalPlayer)this.minecraft.player).addDeltaMovement(arg_0));
    }

    public void handleMountScreenOpen(ClientboundMountScreenOpenPacket clientboundMountScreenOpenPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundMountScreenOpenPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(clientboundMountScreenOpenPacket.getEntityId());
        LocalPlayer localPlayer = this.minecraft.player;
        int i = clientboundMountScreenOpenPacket.getInventoryColumns();
        SimpleContainer simpleContainer = new SimpleContainer(AbstractMountInventoryMenu.getInventorySize((int)i));
        if (entity instanceof AbstractHorse) {
            AbstractHorse abstractHorse = (AbstractHorse)entity;
            HorseInventoryMenu horseInventoryMenu = new HorseInventoryMenu(clientboundMountScreenOpenPacket.getContainerId(), localPlayer.getInventory(), (Container)simpleContainer, abstractHorse, i);
            localPlayer.containerMenu = horseInventoryMenu;
            this.minecraft.setScreen(new HorseInventoryScreen(horseInventoryMenu, localPlayer.getInventory(), abstractHorse, i));
        } else if (entity instanceof AbstractNautilus) {
            AbstractNautilus abstractNautilus = (AbstractNautilus)entity;
            NautilusInventoryMenu nautilusInventoryMenu = new NautilusInventoryMenu(clientboundMountScreenOpenPacket.getContainerId(), localPlayer.getInventory(), (Container)simpleContainer, abstractNautilus, i);
            localPlayer.containerMenu = nautilusInventoryMenu;
            this.minecraft.setScreen(new NautilusInventoryScreen(nautilusInventoryMenu, localPlayer.getInventory(), abstractNautilus, i));
        }
    }

    public void handleOpenScreen(ClientboundOpenScreenPacket clientboundOpenScreenPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundOpenScreenPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        MenuScreens.create(clientboundOpenScreenPacket.getType(), this.minecraft, clientboundOpenScreenPacket.getContainerId(), clientboundOpenScreenPacket.getTitle());
    }

    public void handleContainerSetSlot(ClientboundContainerSetSlotPacket clientboundContainerSetSlotPacket) {
        CreativeModeInventoryScreen creativeModeInventoryScreen;
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundContainerSetSlotPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        LocalPlayer player = this.minecraft.player;
        ItemStack itemStack = clientboundContainerSetSlotPacket.getItem();
        int i = clientboundContainerSetSlotPacket.getSlot();
        this.minecraft.getTutorial().onGetItem(itemStack);
        Screen screen = this.minecraft.screen;
        boolean bl = screen instanceof CreativeModeInventoryScreen ? !(creativeModeInventoryScreen = (CreativeModeInventoryScreen)screen).isInventoryOpen() : false;
        if (clientboundContainerSetSlotPacket.getContainerId() == 0) {
            ItemStack itemStack2;
            if (InventoryMenu.isHotbarSlot((int)i) && !itemStack.isEmpty() && ((itemStack2 = player.inventoryMenu.getSlot(i).getItem()).isEmpty() || itemStack2.getCount() < itemStack.getCount())) {
                itemStack.setPopTime(5);
            }
            player.inventoryMenu.setItem(i, clientboundContainerSetSlotPacket.getStateId(), itemStack);
        } else if (!(clientboundContainerSetSlotPacket.getContainerId() != player.containerMenu.containerId || clientboundContainerSetSlotPacket.getContainerId() == 0 && bl)) {
            player.containerMenu.setItem(i, clientboundContainerSetSlotPacket.getStateId(), itemStack);
        }
        if (this.minecraft.screen instanceof CreativeModeInventoryScreen) {
            player.inventoryMenu.setRemoteSlot(i, itemStack);
            player.inventoryMenu.broadcastChanges();
        }
    }

    public void handleSetCursorItem(ClientboundSetCursorItemPacket clientboundSetCursorItemPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetCursorItemPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.getTutorial().onGetItem(clientboundSetCursorItemPacket.contents());
        if (!(this.minecraft.screen instanceof CreativeModeInventoryScreen)) {
            this.minecraft.player.containerMenu.setCarried(clientboundSetCursorItemPacket.contents());
        }
    }

    public void handleSetPlayerInventory(ClientboundSetPlayerInventoryPacket clientboundSetPlayerInventoryPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetPlayerInventoryPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.getTutorial().onGetItem(clientboundSetPlayerInventoryPacket.contents());
        this.minecraft.player.getInventory().setItem(clientboundSetPlayerInventoryPacket.slot(), clientboundSetPlayerInventoryPacket.contents());
    }

    public void handleContainerContent(ClientboundContainerSetContentPacket clientboundContainerSetContentPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundContainerSetContentPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        LocalPlayer player = this.minecraft.player;
        if (clientboundContainerSetContentPacket.containerId() == 0) {
            player.inventoryMenu.initializeContents(clientboundContainerSetContentPacket.stateId(), clientboundContainerSetContentPacket.items(), clientboundContainerSetContentPacket.carriedItem());
        } else if (clientboundContainerSetContentPacket.containerId() == player.containerMenu.containerId) {
            player.containerMenu.initializeContents(clientboundContainerSetContentPacket.stateId(), clientboundContainerSetContentPacket.items(), clientboundContainerSetContentPacket.carriedItem());
        }
    }

    public void handleOpenSignEditor(ClientboundOpenSignEditorPacket clientboundOpenSignEditorPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundOpenSignEditorPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        BlockPos blockPos = clientboundOpenSignEditorPacket.getPos();
        BlockEntity blockEntity = this.level.getBlockEntity(blockPos);
        if (blockEntity instanceof SignBlockEntity) {
            SignBlockEntity signBlockEntity = (SignBlockEntity)blockEntity;
            this.minecraft.player.openTextEdit(signBlockEntity, clientboundOpenSignEditorPacket.isFrontText());
        } else {
            LOGGER.warn("Ignoring openTextEdit on an invalid entity: {} at pos {}", (Object)this.level.getBlockEntity(blockPos), (Object)blockPos);
        }
    }

    public void handleBlockEntityData(ClientboundBlockEntityDataPacket clientboundBlockEntityDataPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundBlockEntityDataPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        BlockPos blockPos = clientboundBlockEntityDataPacket.getPos();
        this.minecraft.level.getBlockEntity(blockPos, clientboundBlockEntityDataPacket.getType()).ifPresent(blockEntity -> {
            try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(blockEntity.problemPath(), LOGGER);){
                blockEntity.loadWithComponents(TagValueInput.create((ProblemReporter)scopedCollector, (HolderLookup.Provider)this.registryAccess, (CompoundTag)clientboundBlockEntityDataPacket.getTag()));
            }
            if (blockEntity instanceof CommandBlockEntity && this.minecraft.screen instanceof CommandBlockEditScreen) {
                ((CommandBlockEditScreen)this.minecraft.screen).updateGui();
            }
        });
    }

    public void handleContainerSetData(ClientboundContainerSetDataPacket clientboundContainerSetDataPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundContainerSetDataPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        LocalPlayer player = this.minecraft.player;
        if (player.containerMenu.containerId == clientboundContainerSetDataPacket.getContainerId()) {
            player.containerMenu.setData(clientboundContainerSetDataPacket.getId(), clientboundContainerSetDataPacket.getValue());
        }
    }

    public void handleSetEquipment(ClientboundSetEquipmentPacket clientboundSetEquipmentPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetEquipmentPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(clientboundSetEquipmentPacket.getEntity());
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            clientboundSetEquipmentPacket.getSlots().forEach(pair -> livingEntity.setItemSlot((EquipmentSlot)pair.getFirst(), (ItemStack)pair.getSecond()));
        }
    }

    public void handleContainerClose(ClientboundContainerClosePacket clientboundContainerClosePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundContainerClosePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.player.clientSideCloseContainer();
    }

    public void handleBlockEvent(ClientboundBlockEventPacket clientboundBlockEventPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundBlockEventPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.level.blockEvent(clientboundBlockEventPacket.getPos(), clientboundBlockEventPacket.getBlock(), clientboundBlockEventPacket.getB0(), clientboundBlockEventPacket.getB1());
    }

    public void handleBlockDestruction(ClientboundBlockDestructionPacket clientboundBlockDestructionPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundBlockDestructionPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.level.destroyBlockProgress(clientboundBlockDestructionPacket.getId(), clientboundBlockDestructionPacket.getPos(), clientboundBlockDestructionPacket.getProgress());
    }

    public void handleGameEvent(ClientboundGameEventPacket clientboundGameEventPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundGameEventPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        LocalPlayer player = this.minecraft.player;
        ClientboundGameEventPacket.Type type = clientboundGameEventPacket.getEvent();
        float f = clientboundGameEventPacket.getParam();
        int i = Mth.floor((float)(f + 0.5f));
        if (type == ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE) {
            player.displayClientMessage((Component)Component.translatable((String)"block.minecraft.spawn.not_valid"), false);
        } else if (type == ClientboundGameEventPacket.START_RAINING) {
            this.level.getLevelData().setRaining(true);
            this.level.setRainLevel(0.0f);
        } else if (type == ClientboundGameEventPacket.STOP_RAINING) {
            this.level.getLevelData().setRaining(false);
            this.level.setRainLevel(1.0f);
        } else if (type == ClientboundGameEventPacket.CHANGE_GAME_MODE) {
            this.minecraft.gameMode.setLocalMode(GameType.byId((int)i));
        } else if (type == ClientboundGameEventPacket.WIN_GAME) {
            this.minecraft.setScreen(new WinScreen(true, () -> {
                this.minecraft.player.connection.send((Packet<?>)new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
                this.minecraft.setScreen(null);
            }));
        } else if (type == ClientboundGameEventPacket.DEMO_EVENT) {
            Options options = this.minecraft.options;
            MutableComponent component = null;
            if (f == 0.0f) {
                this.minecraft.setScreen(new DemoIntroScreen());
            } else if (f == 101.0f) {
                component = Component.translatable((String)"demo.help.movement", (Object[])new Object[]{options.keyUp.getTranslatedKeyMessage(), options.keyLeft.getTranslatedKeyMessage(), options.keyDown.getTranslatedKeyMessage(), options.keyRight.getTranslatedKeyMessage()});
            } else if (f == 102.0f) {
                component = Component.translatable((String)"demo.help.jump", (Object[])new Object[]{options.keyJump.getTranslatedKeyMessage()});
            } else if (f == 103.0f) {
                component = Component.translatable((String)"demo.help.inventory", (Object[])new Object[]{options.keyInventory.getTranslatedKeyMessage()});
            } else if (f == 104.0f) {
                component = Component.translatable((String)"demo.day.6", (Object[])new Object[]{options.keyScreenshot.getTranslatedKeyMessage()});
            }
            if (component != null) {
                this.minecraft.gui.getChat().addMessage((Component)component);
                this.minecraft.getNarrator().saySystemQueued((Component)component);
            }
        } else if (type == ClientboundGameEventPacket.PLAY_ARROW_HIT_SOUND) {
            this.level.playSound((Entity)player, player.getX(), player.getEyeY(), player.getZ(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 0.18f, 0.45f);
        } else if (type == ClientboundGameEventPacket.RAIN_LEVEL_CHANGE) {
            this.level.setRainLevel(f);
        } else if (type == ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE) {
            this.level.setThunderLevel(f);
        } else if (type == ClientboundGameEventPacket.PUFFER_FISH_STING) {
            this.level.playSound((Entity)player, player.getX(), player.getY(), player.getZ(), SoundEvents.PUFFER_FISH_STING, SoundSource.NEUTRAL, 1.0f, 1.0f);
        } else if (type == ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT) {
            this.level.addParticle((ParticleOptions)ParticleTypes.ELDER_GUARDIAN, player.getX(), player.getY(), player.getZ(), 0.0, 0.0, 0.0);
            if (i == 1) {
                this.level.playSound((Entity)player, player.getX(), player.getY(), player.getZ(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1.0f, 1.0f);
            }
        } else if (type == ClientboundGameEventPacket.IMMEDIATE_RESPAWN) {
            this.minecraft.player.setShowDeathScreen(f == 0.0f);
        } else if (type == ClientboundGameEventPacket.LIMITED_CRAFTING) {
            this.minecraft.player.setDoLimitedCrafting(f == 1.0f);
        } else if (type == ClientboundGameEventPacket.LEVEL_CHUNKS_LOAD_START && this.levelLoadTracker != null) {
            this.levelLoadTracker.loadingPacketsReceived();
        }
    }

    private void startWaitingForNewLevel(LocalPlayer localPlayer, ClientLevel clientLevel, LevelLoadingScreen.Reason reason) {
        if (this.levelLoadTracker == null) {
            this.levelLoadTracker = new LevelLoadTracker();
        }
        this.levelLoadTracker.startClientLoad(localPlayer, clientLevel, this.minecraft.levelRenderer);
        Screen screen = this.minecraft.screen;
        if (screen instanceof LevelLoadingScreen) {
            LevelLoadingScreen levelLoadingScreen = (LevelLoadingScreen)screen;
            levelLoadingScreen.update(this.levelLoadTracker, reason);
        } else {
            this.minecraft.gui.getChat().preserveCurrentChatScreen();
            this.minecraft.setScreenAndShow(new LevelLoadingScreen(this.levelLoadTracker, reason));
        }
    }

    public void handleMapItemData(ClientboundMapItemDataPacket clientboundMapItemDataPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundMapItemDataPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        MapId mapId = clientboundMapItemDataPacket.mapId();
        MapItemSavedData mapItemSavedData = this.minecraft.level.getMapData(mapId);
        if (mapItemSavedData == null) {
            mapItemSavedData = MapItemSavedData.createForClient((byte)clientboundMapItemDataPacket.scale(), (boolean)clientboundMapItemDataPacket.locked(), (ResourceKey)this.minecraft.level.dimension());
            this.minecraft.level.overrideMapData(mapId, mapItemSavedData);
        }
        clientboundMapItemDataPacket.applyToMap(mapItemSavedData);
        this.minecraft.getMapTextureManager().update(mapId, mapItemSavedData);
    }

    public void handleLevelEvent(ClientboundLevelEventPacket clientboundLevelEventPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundLevelEventPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        if (clientboundLevelEventPacket.isGlobalEvent()) {
            this.minecraft.level.globalLevelEvent(clientboundLevelEventPacket.getType(), clientboundLevelEventPacket.getPos(), clientboundLevelEventPacket.getData());
        } else {
            this.minecraft.level.levelEvent(clientboundLevelEventPacket.getType(), clientboundLevelEventPacket.getPos(), clientboundLevelEventPacket.getData());
        }
    }

    public void handleUpdateAdvancementsPacket(ClientboundUpdateAdvancementsPacket clientboundUpdateAdvancementsPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundUpdateAdvancementsPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.advancements.update(clientboundUpdateAdvancementsPacket);
    }

    public void handleSelectAdvancementsTab(ClientboundSelectAdvancementsTabPacket clientboundSelectAdvancementsTabPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSelectAdvancementsTabPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Identifier identifier = clientboundSelectAdvancementsTabPacket.getTab();
        if (identifier == null) {
            this.advancements.setSelectedTab(null, false);
        } else {
            AdvancementHolder advancementHolder = this.advancements.get(identifier);
            this.advancements.setSelectedTab(advancementHolder, false);
        }
    }

    public void handleCommands(ClientboundCommandsPacket clientboundCommandsPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundCommandsPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.commands = new CommandDispatcher(clientboundCommandsPacket.getRoot(CommandBuildContext.simple((HolderLookup.Provider)this.registryAccess, (FeatureFlagSet)this.enabledFeatures), COMMAND_NODE_BUILDER));
    }

    public void handleStopSoundEvent(ClientboundStopSoundPacket clientboundStopSoundPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundStopSoundPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.getSoundManager().stop(clientboundStopSoundPacket.getName(), clientboundStopSoundPacket.getSource());
    }

    public void handleCommandSuggestions(ClientboundCommandSuggestionsPacket clientboundCommandSuggestionsPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundCommandSuggestionsPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.suggestionsProvider.completeCustomSuggestions(clientboundCommandSuggestionsPacket.id(), clientboundCommandSuggestionsPacket.toSuggestions());
    }

    public void handleUpdateRecipes(ClientboundUpdateRecipesPacket clientboundUpdateRecipesPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundUpdateRecipesPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.recipes = new ClientRecipeContainer(clientboundUpdateRecipesPacket.itemSets(), (SelectableRecipe.SingleInputSet<StonecutterRecipe>)clientboundUpdateRecipesPacket.stonecutterRecipes());
    }

    public void handleLookAt(ClientboundPlayerLookAtPacket clientboundPlayerLookAtPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundPlayerLookAtPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Vec3 vec3 = clientboundPlayerLookAtPacket.getPosition((Level)this.level);
        if (vec3 != null) {
            this.minecraft.player.lookAt(clientboundPlayerLookAtPacket.getFromAnchor(), vec3);
        }
    }

    public void handleTagQueryPacket(ClientboundTagQueryPacket clientboundTagQueryPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundTagQueryPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        if (!this.debugQueryHandler.handleResponse(clientboundTagQueryPacket.getTransactionId(), clientboundTagQueryPacket.getTag())) {
            LOGGER.debug("Got unhandled response to tag query {}", (Object)clientboundTagQueryPacket.getTransactionId());
        }
    }

    public void handleAwardStats(ClientboundAwardStatsPacket clientboundAwardStatsPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundAwardStatsPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        for (Object2IntMap.Entry entry : clientboundAwardStatsPacket.stats().object2IntEntrySet()) {
            Stat stat = (Stat)entry.getKey();
            int i = entry.getIntValue();
            this.minecraft.player.getStats().setValue((Player)this.minecraft.player, stat, i);
        }
        Screen screen = this.minecraft.screen;
        if (screen instanceof StatsScreen) {
            StatsScreen statsScreen = (StatsScreen)screen;
            statsScreen.onStatsUpdated();
        }
    }

    public void handleRecipeBookAdd(ClientboundRecipeBookAddPacket clientboundRecipeBookAddPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundRecipeBookAddPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        ClientRecipeBook clientRecipeBook = this.minecraft.player.getRecipeBook();
        if (clientboundRecipeBookAddPacket.replace()) {
            clientRecipeBook.clear();
        }
        for (ClientboundRecipeBookAddPacket.Entry entry : clientboundRecipeBookAddPacket.entries()) {
            clientRecipeBook.add(entry.contents());
            if (entry.highlight()) {
                clientRecipeBook.addHighlight(entry.contents().id());
            }
            if (!entry.notification()) continue;
            RecipeToast.addOrUpdate(this.minecraft.getToastManager(), entry.contents().display());
        }
        this.refreshRecipeBook(clientRecipeBook);
    }

    public void handleRecipeBookRemove(ClientboundRecipeBookRemovePacket clientboundRecipeBookRemovePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundRecipeBookRemovePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        ClientRecipeBook clientRecipeBook = this.minecraft.player.getRecipeBook();
        for (RecipeDisplayId recipeDisplayId : clientboundRecipeBookRemovePacket.recipes()) {
            clientRecipeBook.remove(recipeDisplayId);
        }
        this.refreshRecipeBook(clientRecipeBook);
    }

    public void handleRecipeBookSettings(ClientboundRecipeBookSettingsPacket clientboundRecipeBookSettingsPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundRecipeBookSettingsPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        ClientRecipeBook clientRecipeBook = this.minecraft.player.getRecipeBook();
        clientRecipeBook.setBookSettings(clientboundRecipeBookSettingsPacket.bookSettings());
        this.refreshRecipeBook(clientRecipeBook);
    }

    private void refreshRecipeBook(ClientRecipeBook clientRecipeBook) {
        clientRecipeBook.rebuildCollections();
        this.searchTrees.updateRecipes(clientRecipeBook, this.level);
        Screen screen = this.minecraft.screen;
        if (screen instanceof RecipeUpdateListener) {
            RecipeUpdateListener recipeUpdateListener = (RecipeUpdateListener)((Object)screen);
            recipeUpdateListener.recipesUpdated();
        }
    }

    public void handleUpdateMobEffect(ClientboundUpdateMobEffectPacket clientboundUpdateMobEffectPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundUpdateMobEffectPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(clientboundUpdateMobEffectPacket.getEntityId());
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        Holder holder = clientboundUpdateMobEffectPacket.getEffect();
        MobEffectInstance mobEffectInstance = new MobEffectInstance(holder, clientboundUpdateMobEffectPacket.getEffectDurationTicks(), clientboundUpdateMobEffectPacket.getEffectAmplifier(), clientboundUpdateMobEffectPacket.isEffectAmbient(), clientboundUpdateMobEffectPacket.isEffectVisible(), clientboundUpdateMobEffectPacket.effectShowsIcon(), null);
        if (!clientboundUpdateMobEffectPacket.shouldBlend()) {
            mobEffectInstance.skipBlending();
        }
        ((LivingEntity)entity).forceAddEffect(mobEffectInstance, null);
    }

    private <T> Registry.PendingTags<T> updateTags(ResourceKey<? extends Registry<? extends T>> resourceKey, TagNetworkSerialization.NetworkPayload networkPayload) {
        Registry registry = this.registryAccess.lookupOrThrow(resourceKey);
        return registry.prepareTagReload(networkPayload.resolve(registry));
    }

    public void handleUpdateTags(ClientboundUpdateTagsPacket clientboundUpdateTagsPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundUpdateTagsPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        ArrayList list = new ArrayList(clientboundUpdateTagsPacket.getTags().size());
        boolean bl = this.connection.isMemoryConnection();
        clientboundUpdateTagsPacket.getTags().forEach((resourceKey, networkPayload) -> {
            if (!bl || RegistrySynchronization.isNetworkable((ResourceKey)resourceKey)) {
                list.add(this.updateTags((ResourceKey)resourceKey, (TagNetworkSerialization.NetworkPayload)networkPayload));
            }
        });
        list.forEach(Registry.PendingTags::apply);
        this.fuelValues = FuelValues.vanillaBurnTimes((HolderLookup.Provider)this.registryAccess, (FeatureFlagSet)this.enabledFeatures);
        List<ItemStack> list2 = List.copyOf(CreativeModeTabs.searchTab().getDisplayItems());
        this.searchTrees.updateCreativeTags(list2);
    }

    public void handlePlayerCombatEnd(ClientboundPlayerCombatEndPacket clientboundPlayerCombatEndPacket) {
    }

    public void handlePlayerCombatEnter(ClientboundPlayerCombatEnterPacket clientboundPlayerCombatEnterPacket) {
    }

    public void handlePlayerCombatKill(ClientboundPlayerCombatKillPacket clientboundPlayerCombatKillPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundPlayerCombatKillPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(clientboundPlayerCombatKillPacket.playerId());
        if (entity == this.minecraft.player) {
            if (this.minecraft.player.shouldShowDeathScreen()) {
                this.minecraft.setScreen(new DeathScreen(clientboundPlayerCombatKillPacket.message(), this.level.getLevelData().isHardcore(), this.minecraft.player));
            } else {
                this.minecraft.player.respawn();
            }
        }
    }

    public void handleChangeDifficulty(ClientboundChangeDifficultyPacket clientboundChangeDifficultyPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundChangeDifficultyPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.levelData.setDifficulty(clientboundChangeDifficultyPacket.difficulty());
        this.levelData.setDifficultyLocked(clientboundChangeDifficultyPacket.locked());
    }

    public void handleSetCamera(ClientboundSetCameraPacket clientboundSetCameraPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetCameraPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = clientboundSetCameraPacket.getEntity((Level)this.level);
        if (entity != null) {
            this.minecraft.setCameraEntity(entity);
        }
    }

    public void handleInitializeBorder(ClientboundInitializeBorderPacket clientboundInitializeBorderPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundInitializeBorderPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        WorldBorder worldBorder = this.level.getWorldBorder();
        worldBorder.setCenter(clientboundInitializeBorderPacket.getNewCenterX(), clientboundInitializeBorderPacket.getNewCenterZ());
        long l = clientboundInitializeBorderPacket.getLerpTime();
        if (l > 0L) {
            worldBorder.lerpSizeBetween(clientboundInitializeBorderPacket.getOldSize(), clientboundInitializeBorderPacket.getNewSize(), l, this.level.getGameTime());
        } else {
            worldBorder.setSize(clientboundInitializeBorderPacket.getNewSize());
        }
        worldBorder.setAbsoluteMaxSize(clientboundInitializeBorderPacket.getNewAbsoluteMaxSize());
        worldBorder.setWarningBlocks(clientboundInitializeBorderPacket.getWarningBlocks());
        worldBorder.setWarningTime(clientboundInitializeBorderPacket.getWarningTime());
    }

    public void handleSetBorderCenter(ClientboundSetBorderCenterPacket clientboundSetBorderCenterPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetBorderCenterPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.level.getWorldBorder().setCenter(clientboundSetBorderCenterPacket.getNewCenterX(), clientboundSetBorderCenterPacket.getNewCenterZ());
    }

    public void handleSetBorderLerpSize(ClientboundSetBorderLerpSizePacket clientboundSetBorderLerpSizePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetBorderLerpSizePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.level.getWorldBorder().lerpSizeBetween(clientboundSetBorderLerpSizePacket.getOldSize(), clientboundSetBorderLerpSizePacket.getNewSize(), clientboundSetBorderLerpSizePacket.getLerpTime(), this.level.getGameTime());
    }

    public void handleSetBorderSize(ClientboundSetBorderSizePacket clientboundSetBorderSizePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetBorderSizePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.level.getWorldBorder().setSize(clientboundSetBorderSizePacket.getSize());
    }

    public void handleSetBorderWarningDistance(ClientboundSetBorderWarningDistancePacket clientboundSetBorderWarningDistancePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetBorderWarningDistancePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.level.getWorldBorder().setWarningBlocks(clientboundSetBorderWarningDistancePacket.getWarningBlocks());
    }

    public void handleSetBorderWarningDelay(ClientboundSetBorderWarningDelayPacket clientboundSetBorderWarningDelayPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetBorderWarningDelayPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.level.getWorldBorder().setWarningTime(clientboundSetBorderWarningDelayPacket.getWarningDelay());
    }

    public void handleTitlesClear(ClientboundClearTitlesPacket clientboundClearTitlesPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundClearTitlesPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.gui.clearTitles();
        if (clientboundClearTitlesPacket.shouldResetTimes()) {
            this.minecraft.gui.resetTitleTimes();
        }
    }

    public void handleServerData(ClientboundServerDataPacket clientboundServerDataPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundServerDataPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        if (this.serverData == null) {
            return;
        }
        this.serverData.motd = clientboundServerDataPacket.motd();
        clientboundServerDataPacket.iconBytes().map(ServerData::validateIcon).ifPresent(this.serverData::setIconBytes);
        ServerList.saveSingleServer(this.serverData);
    }

    public void handleCustomChatCompletions(ClientboundCustomChatCompletionsPacket clientboundCustomChatCompletionsPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundCustomChatCompletionsPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.suggestionsProvider.modifyCustomCompletions(clientboundCustomChatCompletionsPacket.action(), clientboundCustomChatCompletionsPacket.entries());
    }

    public void setActionBarText(ClientboundSetActionBarTextPacket clientboundSetActionBarTextPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetActionBarTextPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.gui.setOverlayMessage(clientboundSetActionBarTextPacket.text(), false);
    }

    public void setTitleText(ClientboundSetTitleTextPacket clientboundSetTitleTextPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetTitleTextPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.gui.setTitle(clientboundSetTitleTextPacket.text());
    }

    public void setSubtitleText(ClientboundSetSubtitleTextPacket clientboundSetSubtitleTextPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetSubtitleTextPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.gui.setSubtitle(clientboundSetSubtitleTextPacket.text());
    }

    public void setTitlesAnimation(ClientboundSetTitlesAnimationPacket clientboundSetTitlesAnimationPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetTitlesAnimationPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.gui.setTimes(clientboundSetTitlesAnimationPacket.getFadeIn(), clientboundSetTitlesAnimationPacket.getStay(), clientboundSetTitlesAnimationPacket.getFadeOut());
    }

    public void handleTabListCustomisation(ClientboundTabListPacket clientboundTabListPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundTabListPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.gui.getTabList().setHeader(clientboundTabListPacket.header().getString().isEmpty() ? null : clientboundTabListPacket.header());
        this.minecraft.gui.getTabList().setFooter(clientboundTabListPacket.footer().getString().isEmpty() ? null : clientboundTabListPacket.footer());
    }

    public void handleRemoveMobEffect(ClientboundRemoveMobEffectPacket clientboundRemoveMobEffectPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundRemoveMobEffectPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = clientboundRemoveMobEffectPacket.getEntity((Level)this.level);
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            livingEntity.removeEffectNoUpdate(clientboundRemoveMobEffectPacket.effect());
        }
    }

    public void handlePlayerInfoRemove(ClientboundPlayerInfoRemovePacket clientboundPlayerInfoRemovePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundPlayerInfoRemovePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        for (UUID uUID : clientboundPlayerInfoRemovePacket.profileIds()) {
            this.minecraft.getPlayerSocialManager().removePlayer(uUID);
            PlayerInfo playerInfo = this.playerInfoMap.remove(uUID);
            if (playerInfo == null) continue;
            this.listedPlayers.remove(playerInfo);
        }
    }

    public void handlePlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket clientboundPlayerInfoUpdatePacket) {
        PlayerInfo playerInfo;
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundPlayerInfoUpdatePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        for (ClientboundPlayerInfoUpdatePacket.Entry entry : clientboundPlayerInfoUpdatePacket.newEntries()) {
            playerInfo = new PlayerInfo(Objects.requireNonNull(entry.profile()), this.enforcesSecureChat());
            if (this.playerInfoMap.putIfAbsent(entry.profileId(), playerInfo) != null) continue;
            this.minecraft.getPlayerSocialManager().addPlayer(playerInfo);
        }
        for (ClientboundPlayerInfoUpdatePacket.Entry entry : clientboundPlayerInfoUpdatePacket.entries()) {
            playerInfo = this.playerInfoMap.get(entry.profileId());
            if (playerInfo == null) {
                LOGGER.warn("Ignoring player info update for unknown player {} ({})", (Object)entry.profileId(), (Object)clientboundPlayerInfoUpdatePacket.actions());
                continue;
            }
            for (ClientboundPlayerInfoUpdatePacket.Action action : clientboundPlayerInfoUpdatePacket.actions()) {
                this.applyPlayerInfoUpdate(action, entry, playerInfo);
            }
        }
    }

    private void applyPlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket.Action action, ClientboundPlayerInfoUpdatePacket.Entry entry, PlayerInfo playerInfo) {
        switch (action) {
            case INITIALIZE_CHAT: {
                this.initializeChatSession(entry, playerInfo);
                break;
            }
            case UPDATE_GAME_MODE: {
                if (playerInfo.getGameMode() != entry.gameMode() && this.minecraft.player != null && this.minecraft.player.getUUID().equals(entry.profileId())) {
                    this.minecraft.player.onGameModeChanged(entry.gameMode());
                }
                playerInfo.setGameMode(entry.gameMode());
                break;
            }
            case UPDATE_LISTED: {
                if (entry.listed()) {
                    this.listedPlayers.add(playerInfo);
                    break;
                }
                this.listedPlayers.remove(playerInfo);
                break;
            }
            case UPDATE_LATENCY: {
                playerInfo.setLatency(entry.latency());
                break;
            }
            case UPDATE_DISPLAY_NAME: {
                playerInfo.setTabListDisplayName(entry.displayName());
                break;
            }
            case UPDATE_HAT: {
                playerInfo.setShowHat(entry.showHat());
                break;
            }
            case UPDATE_LIST_ORDER: {
                playerInfo.setTabListOrder(entry.listOrder());
            }
        }
    }

    private void initializeChatSession(ClientboundPlayerInfoUpdatePacket.Entry entry, PlayerInfo playerInfo) {
        GameProfile gameProfile = playerInfo.getProfile();
        SignatureValidator signatureValidator = this.minecraft.services().profileKeySignatureValidator();
        if (signatureValidator == null) {
            LOGGER.warn("Ignoring chat session from {} due to missing Services public key", (Object)gameProfile.name());
            playerInfo.clearChatSession(this.enforcesSecureChat());
            return;
        }
        RemoteChatSession.Data data = entry.chatSession();
        if (data != null) {
            try {
                RemoteChatSession remoteChatSession = data.validate(gameProfile, signatureValidator);
                playerInfo.setChatSession(remoteChatSession);
            }
            catch (ProfilePublicKey.ValidationException validationException) {
                LOGGER.error("Failed to validate profile key for player: '{}'", (Object)gameProfile.name(), (Object)validationException);
                playerInfo.clearChatSession(this.enforcesSecureChat());
            }
        } else {
            playerInfo.clearChatSession(this.enforcesSecureChat());
        }
    }

    private boolean enforcesSecureChat() {
        return this.minecraft.services().canValidateProfileKeys() && this.serverEnforcesSecureChat;
    }

    public void handlePlayerAbilities(ClientboundPlayerAbilitiesPacket clientboundPlayerAbilitiesPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundPlayerAbilitiesPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        LocalPlayer player = this.minecraft.player;
        player.getAbilities().flying = clientboundPlayerAbilitiesPacket.isFlying();
        player.getAbilities().instabuild = clientboundPlayerAbilitiesPacket.canInstabuild();
        player.getAbilities().invulnerable = clientboundPlayerAbilitiesPacket.isInvulnerable();
        player.getAbilities().mayfly = clientboundPlayerAbilitiesPacket.canFly();
        player.getAbilities().setFlyingSpeed(clientboundPlayerAbilitiesPacket.getFlyingSpeed());
        player.getAbilities().setWalkingSpeed(clientboundPlayerAbilitiesPacket.getWalkingSpeed());
    }

    public void handleSoundEvent(ClientboundSoundPacket clientboundSoundPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSoundPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.level.playSeededSound((Entity)this.minecraft.player, clientboundSoundPacket.getX(), clientboundSoundPacket.getY(), clientboundSoundPacket.getZ(), (Holder<SoundEvent>)clientboundSoundPacket.getSound(), clientboundSoundPacket.getSource(), clientboundSoundPacket.getVolume(), clientboundSoundPacket.getPitch(), clientboundSoundPacket.getSeed());
    }

    public void handleSoundEntityEvent(ClientboundSoundEntityPacket clientboundSoundEntityPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSoundEntityPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(clientboundSoundEntityPacket.getId());
        if (entity == null) {
            return;
        }
        this.minecraft.level.playSeededSound((Entity)this.minecraft.player, entity, (Holder<SoundEvent>)clientboundSoundEntityPacket.getSound(), clientboundSoundEntityPacket.getSource(), clientboundSoundEntityPacket.getVolume(), clientboundSoundEntityPacket.getPitch(), clientboundSoundEntityPacket.getSeed());
    }

    public void handleBossUpdate(ClientboundBossEventPacket clientboundBossEventPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundBossEventPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.gui.getBossOverlay().update(clientboundBossEventPacket);
    }

    public void handleItemCooldown(ClientboundCooldownPacket clientboundCooldownPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundCooldownPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        if (clientboundCooldownPacket.duration() == 0) {
            this.minecraft.player.getCooldowns().removeCooldown(clientboundCooldownPacket.cooldownGroup());
        } else {
            this.minecraft.player.getCooldowns().addCooldown(clientboundCooldownPacket.cooldownGroup(), clientboundCooldownPacket.duration());
        }
    }

    public void handleMoveVehicle(ClientboundMoveVehiclePacket clientboundMoveVehiclePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundMoveVehiclePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = this.minecraft.player.getRootVehicle();
        if (entity != this.minecraft.player && entity.isLocalInstanceAuthoritative()) {
            Vec3 vec32;
            Vec3 vec3 = clientboundMoveVehiclePacket.position();
            if (vec3.distanceTo(vec32 = entity.isInterpolating() ? entity.getInterpolation().position() : entity.position()) > (double)1.0E-5f) {
                if (entity.isInterpolating()) {
                    entity.getInterpolation().cancel();
                }
                entity.absSnapTo(vec3.x(), vec3.y(), vec3.z(), clientboundMoveVehiclePacket.yRot(), clientboundMoveVehiclePacket.xRot());
            }
            this.connection.send((Packet)ServerboundMoveVehiclePacket.fromEntity((Entity)entity));
        }
    }

    public void handleOpenBook(ClientboundOpenBookPacket clientboundOpenBookPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundOpenBookPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        ItemStack itemStack = this.minecraft.player.getItemInHand(clientboundOpenBookPacket.getHand());
        BookViewScreen.BookAccess bookAccess = BookViewScreen.BookAccess.fromItem(itemStack);
        if (bookAccess != null) {
            this.minecraft.setScreen(new BookViewScreen(bookAccess));
        }
    }

    @Override
    public void handleCustomPayload(CustomPacketPayload customPacketPayload) {
        this.handleUnknownCustomPayload(customPacketPayload);
    }

    private void handleUnknownCustomPayload(CustomPacketPayload customPacketPayload) {
        LOGGER.warn("Unknown custom packet payload: {}", (Object)customPacketPayload.type().id());
    }

    public void handleAddObjective(ClientboundSetObjectivePacket clientboundSetObjectivePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetObjectivePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        String string = clientboundSetObjectivePacket.getObjectiveName();
        if (clientboundSetObjectivePacket.getMethod() == 0) {
            this.scoreboard.addObjective(string, ObjectiveCriteria.DUMMY, clientboundSetObjectivePacket.getDisplayName(), clientboundSetObjectivePacket.getRenderType(), false, (NumberFormat)clientboundSetObjectivePacket.getNumberFormat().orElse(null));
        } else {
            Objective objective = this.scoreboard.getObjective(string);
            if (objective != null) {
                if (clientboundSetObjectivePacket.getMethod() == 1) {
                    this.scoreboard.removeObjective(objective);
                } else if (clientboundSetObjectivePacket.getMethod() == 2) {
                    objective.setRenderType(clientboundSetObjectivePacket.getRenderType());
                    objective.setDisplayName(clientboundSetObjectivePacket.getDisplayName());
                    objective.setNumberFormat((NumberFormat)clientboundSetObjectivePacket.getNumberFormat().orElse(null));
                }
            }
        }
    }

    public void handleSetScore(ClientboundSetScorePacket clientboundSetScorePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetScorePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        String string = clientboundSetScorePacket.objectiveName();
        ScoreHolder scoreHolder = ScoreHolder.forNameOnly((String)clientboundSetScorePacket.owner());
        Objective objective = this.scoreboard.getObjective(string);
        if (objective != null) {
            ScoreAccess scoreAccess = this.scoreboard.getOrCreatePlayerScore(scoreHolder, objective, true);
            scoreAccess.set(clientboundSetScorePacket.score());
            scoreAccess.display((Component)clientboundSetScorePacket.display().orElse(null));
            scoreAccess.numberFormatOverride((NumberFormat)clientboundSetScorePacket.numberFormat().orElse(null));
        } else {
            LOGGER.warn("Received packet for unknown scoreboard objective: {}", (Object)string);
        }
    }

    public void handleResetScore(ClientboundResetScorePacket clientboundResetScorePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundResetScorePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        String string = clientboundResetScorePacket.objectiveName();
        ScoreHolder scoreHolder = ScoreHolder.forNameOnly((String)clientboundResetScorePacket.owner());
        if (string == null) {
            this.scoreboard.resetAllPlayerScores(scoreHolder);
        } else {
            Objective objective = this.scoreboard.getObjective(string);
            if (objective != null) {
                this.scoreboard.resetSinglePlayerScore(scoreHolder, objective);
            } else {
                LOGGER.warn("Received packet for unknown scoreboard objective: {}", (Object)string);
            }
        }
    }

    public void handleSetDisplayObjective(ClientboundSetDisplayObjectivePacket clientboundSetDisplayObjectivePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetDisplayObjectivePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        String string = clientboundSetDisplayObjectivePacket.getObjectiveName();
        Objective objective = string == null ? null : this.scoreboard.getObjective(string);
        this.scoreboard.setDisplayObjective(clientboundSetDisplayObjectivePacket.getSlot(), objective);
    }

    public void handleSetPlayerTeamPacket(ClientboundSetPlayerTeamPacket clientboundSetPlayerTeamPacket) {
        PlayerTeam playerTeam;
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetPlayerTeamPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        ClientboundSetPlayerTeamPacket.Action action = clientboundSetPlayerTeamPacket.getTeamAction();
        if (action == ClientboundSetPlayerTeamPacket.Action.ADD) {
            playerTeam = this.scoreboard.addPlayerTeam(clientboundSetPlayerTeamPacket.getName());
        } else {
            playerTeam = this.scoreboard.getPlayerTeam(clientboundSetPlayerTeamPacket.getName());
            if (playerTeam == null) {
                LOGGER.warn("Received packet for unknown team {}: team action: {}, player action: {}", new Object[]{clientboundSetPlayerTeamPacket.getName(), clientboundSetPlayerTeamPacket.getTeamAction(), clientboundSetPlayerTeamPacket.getPlayerAction()});
                return;
            }
        }
        Optional optional = clientboundSetPlayerTeamPacket.getParameters();
        optional.ifPresent(parameters -> {
            playerTeam.setDisplayName(parameters.getDisplayName());
            playerTeam.setColor(parameters.getColor());
            playerTeam.unpackOptions(parameters.getOptions());
            playerTeam.setNameTagVisibility(parameters.getNametagVisibility());
            playerTeam.setCollisionRule(parameters.getCollisionRule());
            playerTeam.setPlayerPrefix(parameters.getPlayerPrefix());
            playerTeam.setPlayerSuffix(parameters.getPlayerSuffix());
        });
        ClientboundSetPlayerTeamPacket.Action action2 = clientboundSetPlayerTeamPacket.getPlayerAction();
        if (action2 == ClientboundSetPlayerTeamPacket.Action.ADD) {
            for (String string : clientboundSetPlayerTeamPacket.getPlayers()) {
                this.scoreboard.addPlayerToTeam(string, playerTeam);
            }
        } else if (action2 == ClientboundSetPlayerTeamPacket.Action.REMOVE) {
            for (String string : clientboundSetPlayerTeamPacket.getPlayers()) {
                this.scoreboard.removePlayerFromTeam(string, playerTeam);
            }
        }
        if (action == ClientboundSetPlayerTeamPacket.Action.REMOVE) {
            this.scoreboard.removePlayerTeam(playerTeam);
        }
    }

    public void handleParticleEvent(ClientboundLevelParticlesPacket clientboundLevelParticlesPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundLevelParticlesPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        if (clientboundLevelParticlesPacket.getCount() == 0) {
            double d = clientboundLevelParticlesPacket.getMaxSpeed() * clientboundLevelParticlesPacket.getXDist();
            double e = clientboundLevelParticlesPacket.getMaxSpeed() * clientboundLevelParticlesPacket.getYDist();
            double f = clientboundLevelParticlesPacket.getMaxSpeed() * clientboundLevelParticlesPacket.getZDist();
            try {
                this.level.addParticle(clientboundLevelParticlesPacket.getParticle(), clientboundLevelParticlesPacket.isOverrideLimiter(), clientboundLevelParticlesPacket.alwaysShow(), clientboundLevelParticlesPacket.getX(), clientboundLevelParticlesPacket.getY(), clientboundLevelParticlesPacket.getZ(), d, e, f);
            }
            catch (Throwable throwable) {
                LOGGER.warn("Could not spawn particle effect {}", (Object)clientboundLevelParticlesPacket.getParticle());
            }
        } else {
            for (int i = 0; i < clientboundLevelParticlesPacket.getCount(); ++i) {
                double g = this.random.nextGaussian() * (double)clientboundLevelParticlesPacket.getXDist();
                double h = this.random.nextGaussian() * (double)clientboundLevelParticlesPacket.getYDist();
                double j = this.random.nextGaussian() * (double)clientboundLevelParticlesPacket.getZDist();
                double k = this.random.nextGaussian() * (double)clientboundLevelParticlesPacket.getMaxSpeed();
                double l = this.random.nextGaussian() * (double)clientboundLevelParticlesPacket.getMaxSpeed();
                double m = this.random.nextGaussian() * (double)clientboundLevelParticlesPacket.getMaxSpeed();
                try {
                    this.level.addParticle(clientboundLevelParticlesPacket.getParticle(), clientboundLevelParticlesPacket.isOverrideLimiter(), clientboundLevelParticlesPacket.alwaysShow(), clientboundLevelParticlesPacket.getX() + g, clientboundLevelParticlesPacket.getY() + h, clientboundLevelParticlesPacket.getZ() + j, k, l, m);
                    continue;
                }
                catch (Throwable throwable2) {
                    LOGGER.warn("Could not spawn particle effect {}", (Object)clientboundLevelParticlesPacket.getParticle());
                    return;
                }
            }
        }
    }

    public void handleUpdateAttributes(ClientboundUpdateAttributesPacket clientboundUpdateAttributesPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundUpdateAttributesPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(clientboundUpdateAttributesPacket.getEntityId());
        if (entity == null) {
            return;
        }
        if (!(entity instanceof LivingEntity)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + String.valueOf(entity) + ")");
        }
        AttributeMap attributeMap = ((LivingEntity)entity).getAttributes();
        for (ClientboundUpdateAttributesPacket.AttributeSnapshot attributeSnapshot : clientboundUpdateAttributesPacket.getValues()) {
            AttributeInstance attributeInstance = attributeMap.getInstance(attributeSnapshot.attribute());
            if (attributeInstance == null) {
                LOGGER.warn("Entity {} does not have attribute {}", (Object)entity, (Object)attributeSnapshot.attribute().getRegisteredName());
                continue;
            }
            attributeInstance.setBaseValue(attributeSnapshot.base());
            attributeInstance.removeModifiers();
            for (AttributeModifier attributeModifier : attributeSnapshot.modifiers()) {
                attributeInstance.addTransientModifier(attributeModifier);
            }
        }
    }

    public void handlePlaceRecipe(ClientboundPlaceGhostRecipePacket clientboundPlaceGhostRecipePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundPlaceGhostRecipePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        AbstractContainerMenu abstractContainerMenu = this.minecraft.player.containerMenu;
        if (abstractContainerMenu.containerId != clientboundPlaceGhostRecipePacket.containerId()) {
            return;
        }
        Screen screen = this.minecraft.screen;
        if (screen instanceof RecipeUpdateListener) {
            RecipeUpdateListener recipeUpdateListener = (RecipeUpdateListener)((Object)screen);
            recipeUpdateListener.fillGhostRecipe(clientboundPlaceGhostRecipePacket.recipeDisplay());
        }
    }

    public void handleLightUpdatePacket(ClientboundLightUpdatePacket clientboundLightUpdatePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundLightUpdatePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        int i = clientboundLightUpdatePacket.getX();
        int j = clientboundLightUpdatePacket.getZ();
        ClientboundLightUpdatePacketData clientboundLightUpdatePacketData = clientboundLightUpdatePacket.getLightData();
        this.level.queueLightUpdate(() -> this.applyLightData(i, j, clientboundLightUpdatePacketData, true));
    }

    private void applyLightData(int i, int j, ClientboundLightUpdatePacketData clientboundLightUpdatePacketData, boolean bl) {
        LevelLightEngine levelLightEngine = this.level.getChunkSource().getLightEngine();
        BitSet bitSet = clientboundLightUpdatePacketData.getSkyYMask();
        BitSet bitSet2 = clientboundLightUpdatePacketData.getEmptySkyYMask();
        Iterator<byte[]> iterator = clientboundLightUpdatePacketData.getSkyUpdates().iterator();
        this.readSectionList(i, j, levelLightEngine, LightLayer.SKY, bitSet, bitSet2, iterator, bl);
        BitSet bitSet3 = clientboundLightUpdatePacketData.getBlockYMask();
        BitSet bitSet4 = clientboundLightUpdatePacketData.getEmptyBlockYMask();
        Iterator<byte[]> iterator2 = clientboundLightUpdatePacketData.getBlockUpdates().iterator();
        this.readSectionList(i, j, levelLightEngine, LightLayer.BLOCK, bitSet3, bitSet4, iterator2, bl);
        levelLightEngine.setLightEnabled(new ChunkPos(i, j), true);
    }

    public void handleMerchantOffers(ClientboundMerchantOffersPacket clientboundMerchantOffersPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundMerchantOffersPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        AbstractContainerMenu abstractContainerMenu = this.minecraft.player.containerMenu;
        if (clientboundMerchantOffersPacket.getContainerId() == abstractContainerMenu.containerId && abstractContainerMenu instanceof MerchantMenu) {
            MerchantMenu merchantMenu = (MerchantMenu)abstractContainerMenu;
            merchantMenu.setOffers(clientboundMerchantOffersPacket.getOffers());
            merchantMenu.setXp(clientboundMerchantOffersPacket.getVillagerXp());
            merchantMenu.setMerchantLevel(clientboundMerchantOffersPacket.getVillagerLevel());
            merchantMenu.setShowProgressBar(clientboundMerchantOffersPacket.showProgress());
            merchantMenu.setCanRestock(clientboundMerchantOffersPacket.canRestock());
        }
    }

    public void handleSetChunkCacheRadius(ClientboundSetChunkCacheRadiusPacket clientboundSetChunkCacheRadiusPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetChunkCacheRadiusPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.serverChunkRadius = clientboundSetChunkCacheRadiusPacket.getRadius();
        this.minecraft.options.setServerRenderDistance(this.serverChunkRadius);
        this.level.getChunkSource().updateViewRadius(clientboundSetChunkCacheRadiusPacket.getRadius());
    }

    public void handleSetSimulationDistance(ClientboundSetSimulationDistancePacket clientboundSetSimulationDistancePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetSimulationDistancePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.serverSimulationDistance = clientboundSetSimulationDistancePacket.simulationDistance();
        this.level.setServerSimulationDistance(this.serverSimulationDistance);
    }

    public void handleSetChunkCacheCenter(ClientboundSetChunkCacheCenterPacket clientboundSetChunkCacheCenterPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSetChunkCacheCenterPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.level.getChunkSource().updateViewCenter(clientboundSetChunkCacheCenterPacket.getX(), clientboundSetChunkCacheCenterPacket.getZ());
    }

    public void handleBlockChangedAck(ClientboundBlockChangedAckPacket clientboundBlockChangedAckPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundBlockChangedAckPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.level.handleBlockChangedAck(clientboundBlockChangedAckPacket.sequence());
    }

    public void handleBundlePacket(ClientboundBundlePacket clientboundBundlePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundBundlePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        for (Packet packet : clientboundBundlePacket.subPackets()) {
            packet.handle((PacketListener)this);
        }
    }

    public void handleProjectilePowerPacket(ClientboundProjectilePowerPacket clientboundProjectilePowerPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundProjectilePowerPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(clientboundProjectilePowerPacket.getId());
        if (entity instanceof AbstractHurtingProjectile) {
            AbstractHurtingProjectile abstractHurtingProjectile = (AbstractHurtingProjectile)entity;
            abstractHurtingProjectile.accelerationPower = clientboundProjectilePowerPacket.getAccelerationPower();
        }
    }

    public void handleChunkBatchStart(ClientboundChunkBatchStartPacket clientboundChunkBatchStartPacket) {
        this.chunkBatchSizeCalculator.onBatchStart();
    }

    public void handleChunkBatchFinished(ClientboundChunkBatchFinishedPacket clientboundChunkBatchFinishedPacket) {
        this.chunkBatchSizeCalculator.onBatchFinished(clientboundChunkBatchFinishedPacket.batchSize());
        this.send((Packet<?>)new ServerboundChunkBatchReceivedPacket(this.chunkBatchSizeCalculator.getDesiredChunksPerTick()));
    }

    public void handleDebugSample(ClientboundDebugSamplePacket clientboundDebugSamplePacket) {
        this.minecraft.getDebugOverlay().logRemoteSample(clientboundDebugSamplePacket.sample(), clientboundDebugSamplePacket.debugSampleType());
    }

    public void handlePongResponse(ClientboundPongResponsePacket clientboundPongResponsePacket) {
        this.pingDebugMonitor.onPongReceived(clientboundPongResponsePacket);
    }

    public void handleTestInstanceBlockStatus(ClientboundTestInstanceBlockStatus clientboundTestInstanceBlockStatus) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundTestInstanceBlockStatus, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Screen screen = this.minecraft.screen;
        if (screen instanceof TestInstanceBlockEditScreen) {
            TestInstanceBlockEditScreen testInstanceBlockEditScreen = (TestInstanceBlockEditScreen)screen;
            testInstanceBlockEditScreen.setStatus(clientboundTestInstanceBlockStatus.status(), clientboundTestInstanceBlockStatus.size());
        }
    }

    public void handleWaypoint(ClientboundTrackedWaypointPacket clientboundTrackedWaypointPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundTrackedWaypointPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        clientboundTrackedWaypointPacket.apply((TrackedWaypointManager)this.waypointManager);
    }

    public void handleDebugChunkValue(ClientboundDebugChunkValuePacket clientboundDebugChunkValuePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundDebugChunkValuePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.debugSubscriber.updateChunk(this.level.getGameTime(), clientboundDebugChunkValuePacket.chunkPos(), clientboundDebugChunkValuePacket.update());
    }

    public void handleDebugBlockValue(ClientboundDebugBlockValuePacket clientboundDebugBlockValuePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundDebugBlockValuePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.debugSubscriber.updateBlock(this.level.getGameTime(), clientboundDebugBlockValuePacket.blockPos(), clientboundDebugBlockValuePacket.update());
    }

    public void handleDebugEntityValue(ClientboundDebugEntityValuePacket clientboundDebugEntityValuePacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundDebugEntityValuePacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(clientboundDebugEntityValuePacket.entityId());
        if (entity != null) {
            this.debugSubscriber.updateEntity(this.level.getGameTime(), entity, clientboundDebugEntityValuePacket.update());
        }
    }

    public void handleDebugEvent(ClientboundDebugEventPacket clientboundDebugEventPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundDebugEventPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.debugSubscriber.pushEvent(this.level.getGameTime(), clientboundDebugEventPacket.event());
    }

    public void handleGameTestHighlightPos(ClientboundGameTestHighlightPosPacket clientboundGameTestHighlightPosPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundGameTestHighlightPosPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.minecraft.levelRenderer.gameTestBlockHighlightRenderer.highlightPos(clientboundGameTestHighlightPosPacket.absolutePos(), clientboundGameTestHighlightPosPacket.relativePos());
    }

    private void readSectionList(int i, int j, LevelLightEngine levelLightEngine, LightLayer lightLayer, BitSet bitSet, BitSet bitSet2, Iterator<byte[]> iterator, boolean bl) {
        for (int k = 0; k < levelLightEngine.getLightSectionCount(); ++k) {
            int l = levelLightEngine.getMinLightSection() + k;
            boolean bl2 = bitSet.get(k);
            boolean bl3 = bitSet2.get(k);
            if (!bl2 && !bl3) continue;
            levelLightEngine.queueSectionData(lightLayer, SectionPos.of((int)i, (int)l, (int)j), bl2 ? new DataLayer((byte[])iterator.next().clone()) : new DataLayer());
            if (!bl) continue;
            this.level.setSectionDirtyWithNeighbors(i, l, j);
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    public boolean isAcceptingMessages() {
        return this.connection.isConnected() && !this.closed;
    }

    public Collection<PlayerInfo> getListedOnlinePlayers() {
        return this.listedPlayers;
    }

    public Collection<PlayerInfo> getOnlinePlayers() {
        return this.playerInfoMap.values();
    }

    public Collection<UUID> getOnlinePlayerIds() {
        return this.playerInfoMap.keySet();
    }

    public @Nullable PlayerInfo getPlayerInfo(UUID uUID) {
        return this.playerInfoMap.get(uUID);
    }

    public @Nullable PlayerInfo getPlayerInfo(String string) {
        for (PlayerInfo playerInfo : this.playerInfoMap.values()) {
            if (!playerInfo.getProfile().name().equals(string)) continue;
            return playerInfo;
        }
        return null;
    }

    public Map<UUID, PlayerInfo> getSeenPlayers() {
        return this.seenPlayers;
    }

    public @Nullable PlayerInfo getPlayerInfoIgnoreCase(String string) {
        for (PlayerInfo playerInfo : this.playerInfoMap.values()) {
            if (!playerInfo.getProfile().name().equalsIgnoreCase(string)) continue;
            return playerInfo;
        }
        return null;
    }

    public GameProfile getLocalGameProfile() {
        return this.localGameProfile;
    }

    public ClientAdvancements getAdvancements() {
        return this.advancements;
    }

    public CommandDispatcher<ClientSuggestionProvider> getCommands() {
        return this.commands;
    }

    public ClientLevel getLevel() {
        return this.level;
    }

    public DebugQueryHandler getDebugQueryHandler() {
        return this.debugQueryHandler;
    }

    public UUID getId() {
        return this.id;
    }

    public Set<ResourceKey<Level>> levels() {
        return this.levels;
    }

    public RegistryAccess.Frozen registryAccess() {
        return this.registryAccess;
    }

    public void markMessageAsProcessed(MessageSignature messageSignature, boolean bl) {
        if (this.lastSeenMessages.addPending(messageSignature, bl) && this.lastSeenMessages.offset() > 64) {
            this.sendChatAcknowledgement();
        }
    }

    private void sendChatAcknowledgement() {
        int i = this.lastSeenMessages.getAndClearOffset();
        if (i > 0) {
            this.send((Packet<?>)new ServerboundChatAckPacket(i));
        }
    }

    public void sendChat(String string) {
        Instant instant = Instant.now();
        long l = Crypt.SaltSupplier.getLong();
        LastSeenMessagesTracker.Update update = this.lastSeenMessages.generateAndApplyUpdate();
        MessageSignature messageSignature = this.signedMessageEncoder.pack(new SignedMessageBody(string, instant, l, update.lastSeen()));
        this.send((Packet<?>)new ServerboundChatPacket(string, instant, l, messageSignature, update.update()));
    }

    public void sendCommand(String string2) {
        SignableCommand signableCommand = SignableCommand.of((ParseResults)this.commands.parse(string2, (Object)this.suggestionsProvider));
        if (signableCommand.arguments().isEmpty()) {
            this.send((Packet<?>)new ServerboundChatCommandPacket(string2));
            return;
        }
        Instant instant = Instant.now();
        long l = Crypt.SaltSupplier.getLong();
        LastSeenMessagesTracker.Update update = this.lastSeenMessages.generateAndApplyUpdate();
        ArgumentSignatures argumentSignatures = ArgumentSignatures.signCommand((SignableCommand)signableCommand, string -> {
            SignedMessageBody signedMessageBody = new SignedMessageBody(string, instant, l, update.lastSeen());
            return this.signedMessageEncoder.pack(signedMessageBody);
        });
        this.send((Packet<?>)new ServerboundChatCommandSignedPacket(string2, instant, l, argumentSignatures, update.update()));
    }

    public void sendUnattendedCommand(String string, @Nullable Screen screen) {
        switch (this.verifyCommand(string).ordinal()) {
            case 0: {
                this.send((Packet<?>)new ServerboundChatCommandPacket(string));
                this.minecraft.setScreen(screen);
                break;
            }
            case 1: {
                this.openCommandSendConfirmationWindow(string, "multiplayer.confirm_command.parse_errors", screen);
                break;
            }
            case 3: {
                this.openCommandSendConfirmationWindow(string, "multiplayer.confirm_command.permissions_required", screen);
                break;
            }
            case 2: {
                this.openSignedCommandSendConfirmationWindow(string, "multiplayer.confirm_command.signature_required", screen);
            }
        }
    }

    private CommandCheckResult verifyCommand(String string) {
        ParseResults parseResults = this.commands.parse(string, (Object)this.suggestionsProvider);
        if (!ClientPacketListener.isValidCommand(parseResults)) {
            return CommandCheckResult.PARSE_ERRORS;
        }
        if (SignableCommand.hasSignableArguments((ParseResults)parseResults)) {
            return CommandCheckResult.SIGNATURE_REQUIRED;
        }
        ParseResults parseResults2 = this.commands.parse(string, (Object)this.restrictedSuggestionsProvider);
        if (!ClientPacketListener.isValidCommand(parseResults2)) {
            return CommandCheckResult.PERMISSIONS_REQUIRED;
        }
        return CommandCheckResult.NO_ISSUES;
    }

    private static boolean isValidCommand(ParseResults<?> parseResults) {
        return !parseResults.getReader().canRead() && parseResults.getExceptions().isEmpty() && parseResults.getContext().getLastChild().getCommand() != null;
    }

    private void openSendConfirmationWindow(String string, String string2, Component component, Runnable runnable) {
        Screen screen = this.minecraft.screen;
        this.minecraft.setScreen(new ConfirmScreen(bl -> {
            if (bl) {
                runnable.run();
            } else {
                this.minecraft.setScreen(screen);
            }
        }, COMMAND_SEND_CONFIRM_TITLE, (Component)Component.translatable((String)string2, (Object[])new Object[]{Component.literal((String)string).withStyle(ChatFormatting.YELLOW)}), component, screen != null ? CommonComponents.GUI_BACK : CommonComponents.GUI_CANCEL));
    }

    private void openCommandSendConfirmationWindow(String string, String string2, @Nullable Screen screen) {
        this.openSendConfirmationWindow(string, string2, BUTTON_RUN_COMMAND, () -> {
            this.send((Packet<?>)new ServerboundChatCommandPacket(string));
            this.minecraft.setScreen(screen);
        });
    }

    private void openSignedCommandSendConfirmationWindow(String string, String string2, @Nullable Screen screen) {
        boolean bl = screen == null && this.minecraft.getChatStatus().isChatAllowed(this.minecraft.isLocalServer());
        this.openSendConfirmationWindow(string, string2, bl ? BUTTON_SUGGEST_COMMAND : CommonComponents.GUI_COPY_TO_CLIPBOARD, () -> {
            if (bl) {
                this.minecraft.openChatScreen(ChatComponent.ChatMethod.COMMAND);
                Screen screen2 = this.minecraft.screen;
                if (screen2 instanceof ChatScreen) {
                    ChatScreen chatScreen = (ChatScreen)screen2;
                    chatScreen.insertText(string, false);
                }
            } else {
                this.minecraft.keyboardHandler.setClipboard("/" + string);
                this.minecraft.setScreen(screen);
            }
        });
    }

    public void broadcastClientInformation(ClientInformation clientInformation) {
        if (!clientInformation.equals((Object)this.remoteClientInformation)) {
            this.send((Packet<?>)new ServerboundClientInformationPacket(clientInformation));
            this.remoteClientInformation = clientInformation;
        }
    }

    public void tick() {
        if (this.chatSession != null && this.minecraft.getProfileKeyPairManager().shouldRefreshKeyPair()) {
            this.prepareKeyPair();
        }
        if (this.keyPairFuture != null && this.keyPairFuture.isDone()) {
            this.keyPairFuture.join().ifPresent(this::setKeyPair);
            this.keyPairFuture = null;
        }
        this.sendDeferredPackets();
        if (this.minecraft.getDebugOverlay().showNetworkCharts()) {
            this.pingDebugMonitor.tick();
        }
        if (this.level != null) {
            this.debugSubscriber.tick(this.level.getGameTime());
        }
        this.telemetryManager.tick();
        if (this.levelLoadTracker != null) {
            this.levelLoadTracker.tickClientLoad();
            if (this.levelLoadTracker.isLevelReady()) {
                this.notifyPlayerLoaded();
                this.levelLoadTracker = null;
            }
        }
    }

    private void notifyPlayerLoaded() {
        if (!this.hasClientLoaded()) {
            this.connection.send((Packet)new ServerboundPlayerLoadedPacket());
            this.setClientLoaded(true);
        }
    }

    public void prepareKeyPair() {
        this.keyPairFuture = this.minecraft.getProfileKeyPairManager().prepareKeyPair();
    }

    private void setKeyPair(ProfileKeyPair profileKeyPair) {
        if (!this.minecraft.isLocalPlayer(this.localGameProfile.id())) {
            return;
        }
        if (this.chatSession != null && this.chatSession.keyPair().equals((Object)profileKeyPair)) {
            return;
        }
        this.chatSession = LocalChatSession.create((ProfileKeyPair)profileKeyPair);
        this.signedMessageEncoder = this.chatSession.createMessageEncoder(this.localGameProfile.id());
        this.send((Packet<?>)new ServerboundChatSessionUpdatePacket(this.chatSession.asRemote().asData()));
    }

    @Override
    protected DialogConnectionAccess createDialogAccess() {
        return new ClientCommonPacketListenerImpl.CommonDialogAccess(){

            @Override
            public void runCommand(String string, @Nullable Screen screen) {
                ClientPacketListener.this.sendUnattendedCommand(string, screen);
            }
        };
    }

    public @Nullable ServerData getServerData() {
        return this.serverData;
    }

    public FeatureFlagSet enabledFeatures() {
        return this.enabledFeatures;
    }

    public boolean isFeatureEnabled(FeatureFlagSet featureFlagSet) {
        return featureFlagSet.isSubsetOf(this.enabledFeatures());
    }

    public Scoreboard scoreboard() {
        return this.scoreboard;
    }

    public PotionBrewing potionBrewing() {
        return this.potionBrewing;
    }

    public FuelValues fuelValues() {
        return this.fuelValues;
    }

    public void updateSearchTrees() {
        this.searchTrees.rebuildAfterLanguageChange();
    }

    public SessionSearchTrees searchTrees() {
        return this.searchTrees;
    }

    public void registerForCleaning(CacheSlot<?, ?> cacheSlot) {
        this.cacheSlots.add(new WeakReference(cacheSlot));
    }

    public HashedPatchMap.HashGenerator decoratedHashOpsGenenerator() {
        return this.decoratedHashOpsGenerator;
    }

    public ClientWaypointManager getWaypointManager() {
        return this.waypointManager;
    }

    public DebugValueAccess createDebugValueAccess() {
        return this.debugSubscriber.createDebugValueAccess(this.level);
    }

    public boolean hasClientLoaded() {
        return this.clientLoaded;
    }

    private void setClientLoaded(boolean bl) {
        this.clientLoaded = bl;
    }

    @Environment(value=EnvType.CLIENT)
    static enum CommandCheckResult {
        NO_ISSUES,
        PARSE_ERRORS,
        SIGNATURE_REQUIRED,
        PERMISSIONS_REQUIRED;

    }
}

