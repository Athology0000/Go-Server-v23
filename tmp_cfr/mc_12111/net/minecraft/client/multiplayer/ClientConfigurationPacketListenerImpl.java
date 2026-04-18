/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.RegistryAccess$Frozen
 *  net.minecraft.network.Connection
 *  net.minecraft.network.DisconnectionDetails
 *  net.minecraft.network.PacketListener
 *  net.minecraft.network.PacketProcessor
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.TickablePacketListener
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.PacketUtils
 *  net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.configuration.ClientConfigurationPacketListener
 *  net.minecraft.network.protocol.configuration.ClientboundCodeOfConductPacket
 *  net.minecraft.network.protocol.configuration.ClientboundFinishConfigurationPacket
 *  net.minecraft.network.protocol.configuration.ClientboundRegistryDataPacket
 *  net.minecraft.network.protocol.configuration.ClientboundResetChatPacket
 *  net.minecraft.network.protocol.configuration.ClientboundSelectKnownPacks
 *  net.minecraft.network.protocol.configuration.ClientboundUpdateEnabledFeaturesPacket
 *  net.minecraft.network.protocol.configuration.ServerboundAcceptCodeOfConductPacket
 *  net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket
 *  net.minecraft.network.protocol.configuration.ServerboundSelectKnownPacks
 *  net.minecraft.network.protocol.game.GameProtocols
 *  net.minecraft.network.protocol.game.GameProtocols$Context
 *  net.minecraft.server.packs.repository.KnownPack
 *  net.minecraft.server.packs.resources.CloseableResourceManager
 *  net.minecraft.server.packs.resources.ResourceProvider
 *  net.minecraft.world.flag.FeatureFlagSet
 *  net.minecraft.world.flag.FeatureFlags
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.multiplayer;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.dialog.DialogConnectionAccess;
import net.minecraft.client.gui.screens.multiplayer.CodeOfConductScreen;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.client.multiplayer.KnownPacksManager;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.client.multiplayer.RegistryDataCollector;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.PacketListener;
import net.minecraft.network.PacketProcessor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ClientConfigurationPacketListener;
import net.minecraft.network.protocol.configuration.ClientboundCodeOfConductPacket;
import net.minecraft.network.protocol.configuration.ClientboundFinishConfigurationPacket;
import net.minecraft.network.protocol.configuration.ClientboundRegistryDataPacket;
import net.minecraft.network.protocol.configuration.ClientboundResetChatPacket;
import net.minecraft.network.protocol.configuration.ClientboundSelectKnownPacks;
import net.minecraft.network.protocol.configuration.ClientboundUpdateEnabledFeaturesPacket;
import net.minecraft.network.protocol.configuration.ServerboundAcceptCodeOfConductPacket;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;
import net.minecraft.network.protocol.configuration.ServerboundSelectKnownPacks;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientConfigurationPacketListenerImpl
extends ClientCommonPacketListenerImpl
implements ClientConfigurationPacketListener,
TickablePacketListener {
    static final Logger LOGGER = LogUtils.getLogger();
    public static final Component DISCONNECTED_MESSAGE = Component.translatable((String)"multiplayer.disconnect.code_of_conduct");
    private final LevelLoadTracker levelLoadTracker;
    private final GameProfile localGameProfile;
    private FeatureFlagSet enabledFeatures;
    private final RegistryAccess.Frozen receivedRegistries;
    private final RegistryDataCollector registryDataCollector = new RegistryDataCollector();
    private @Nullable KnownPacksManager knownPacks;
    protected @Nullable ChatComponent.State chatState;
    private boolean seenCodeOfConduct;

    public ClientConfigurationPacketListenerImpl(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
        this.levelLoadTracker = commonListenerCookie.levelLoadTracker();
        this.localGameProfile = commonListenerCookie.localGameProfile();
        this.receivedRegistries = commonListenerCookie.receivedRegistries();
        this.enabledFeatures = commonListenerCookie.enabledFeatures();
        this.chatState = commonListenerCookie.chatState();
    }

    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }

    @Override
    protected void handleCustomPayload(CustomPacketPayload customPacketPayload) {
        this.handleUnknownCustomPayload(customPacketPayload);
    }

    private void handleUnknownCustomPayload(CustomPacketPayload customPacketPayload) {
        LOGGER.warn("Unknown custom packet payload: {}", (Object)customPacketPayload.type().id());
    }

    public void handleRegistryData(ClientboundRegistryDataPacket clientboundRegistryDataPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundRegistryDataPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.registryDataCollector.appendContents(clientboundRegistryDataPacket.registry(), clientboundRegistryDataPacket.entries());
    }

    public void handleUpdateTags(ClientboundUpdateTagsPacket clientboundUpdateTagsPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundUpdateTagsPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        this.registryDataCollector.appendTags(clientboundUpdateTagsPacket.getTags());
    }

    public void handleEnabledFeatures(ClientboundUpdateEnabledFeaturesPacket clientboundUpdateEnabledFeaturesPacket) {
        this.enabledFeatures = FeatureFlags.REGISTRY.fromNames((Iterable)clientboundUpdateEnabledFeaturesPacket.features());
    }

    public void handleSelectKnownPacks(ClientboundSelectKnownPacks clientboundSelectKnownPacks) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundSelectKnownPacks, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        if (this.knownPacks == null) {
            this.knownPacks = new KnownPacksManager();
        }
        List<KnownPack> list = this.knownPacks.trySelectingPacks(clientboundSelectKnownPacks.knownPacks());
        this.send((Packet<?>)new ServerboundSelectKnownPacks(list));
    }

    public void handleResetChat(ClientboundResetChatPacket clientboundResetChatPacket) {
        this.chatState = null;
    }

    private <T> T runWithResources(Function<ResourceProvider, T> function) {
        if (this.knownPacks == null) {
            return function.apply(ResourceProvider.EMPTY);
        }
        try (CloseableResourceManager closeableResourceManager = this.knownPacks.createResourceManager();){
            T t = function.apply((ResourceProvider)closeableResourceManager);
            return t;
        }
    }

    public void handleCodeOfConduct(ClientboundCodeOfConductPacket clientboundCodeOfConductPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundCodeOfConductPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        if (this.seenCodeOfConduct) {
            throw new IllegalStateException("Server sent duplicate Code of Conduct");
        }
        this.seenCodeOfConduct = true;
        String string = clientboundCodeOfConductPacket.codeOfConduct();
        if (this.serverData != null && this.serverData.hasAcceptedCodeOfConduct(string)) {
            this.send((Packet<?>)ServerboundAcceptCodeOfConductPacket.INSTANCE);
        } else {
            Screen screen = this.minecraft.screen;
            this.minecraft.setScreen(new CodeOfConductScreen(this.serverData, screen, string, bl -> {
                if (bl) {
                    this.send((Packet<?>)ServerboundAcceptCodeOfConductPacket.INSTANCE);
                    this.minecraft.setScreen(screen);
                } else {
                    this.createDialogAccess().disconnect(DISCONNECTED_MESSAGE);
                }
            }));
        }
    }

    public void handleConfigurationFinished(ClientboundFinishConfigurationPacket clientboundFinishConfigurationPacket) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundFinishConfigurationPacket, (PacketListener)this, (PacketProcessor)this.minecraft.packetProcessor());
        RegistryAccess.Frozen frozen = this.runWithResources(resourceProvider -> this.registryDataCollector.collectGameRegistries((ResourceProvider)resourceProvider, this.receivedRegistries, this.connection.isMemoryConnection()));
        this.connection.setupInboundProtocol(GameProtocols.CLIENTBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator((RegistryAccess)frozen)), (PacketListener)new ClientPacketListener(this.minecraft, this.connection, new CommonListenerCookie(this.levelLoadTracker, this.localGameProfile, this.telemetryManager, frozen, this.enabledFeatures, this.serverBrand, this.serverData, this.postDisconnectScreen, this.serverCookies, this.chatState, this.customReportDetails, this.serverLinks(), this.seenPlayers, this.seenInsecureChatWarning)));
        this.connection.send((Packet)ServerboundFinishConfigurationPacket.INSTANCE);
        this.connection.setupOutboundProtocol(GameProtocols.SERVERBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator((RegistryAccess)frozen), (Object)new GameProtocols.Context(this){

            public boolean hasInfiniteMaterials() {
                return true;
            }
        }));
    }

    public void tick() {
        this.sendDeferredPackets();
    }

    @Override
    public void onDisconnect(DisconnectionDetails disconnectionDetails) {
        super.onDisconnect(disconnectionDetails);
        this.minecraft.clearDownloadedResourcePacks();
    }

    @Override
    protected DialogConnectionAccess createDialogAccess() {
        return new ClientCommonPacketListenerImpl.CommonDialogAccess(this){

            @Override
            public void runCommand(String string, @Nullable Screen screen) {
                LOGGER.warn("Commands are not supported in configuration phase, trying to run '{}'", (Object)string);
            }
        };
    }
}

