/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.LayeredRegistryAccess
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.RegistryLayer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.notifications.NotificationService
 *  net.minecraft.server.players.NameAndId
 *  net.minecraft.server.players.PlayerList
 *  net.minecraft.util.ProblemReporter
 *  net.minecraft.util.ProblemReporter$ScopedCollector
 *  net.minecraft.world.level.storage.PlayerDataStorage
 *  net.minecraft.world.level.storage.TagValueOutput
 *  net.minecraft.world.level.storage.ValueOutput
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.server;

import com.mojang.logging.LogUtils;
import java.net.SocketAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.notifications.NotificationService;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class IntegratedPlayerList
extends PlayerList {
    private static final Logger LOGGER = LogUtils.getLogger();
    private @Nullable CompoundTag playerData;

    public IntegratedPlayerList(IntegratedServer integratedServer, LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess, PlayerDataStorage playerDataStorage) {
        super((MinecraftServer)integratedServer, layeredRegistryAccess, playerDataStorage, (NotificationService)integratedServer.notificationManager());
        this.setViewDistance(10);
    }

    protected void save(ServerPlayer serverPlayer) {
        if (this.getServer().isSingleplayerOwner(serverPlayer.nameAndId())) {
            try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(serverPlayer.problemPath(), LOGGER);){
                TagValueOutput tagValueOutput = TagValueOutput.createWithContext((ProblemReporter)scopedCollector, (HolderLookup.Provider)serverPlayer.registryAccess());
                serverPlayer.saveWithoutId((ValueOutput)tagValueOutput);
                this.playerData = tagValueOutput.buildResult();
            }
        }
        super.save(serverPlayer);
    }

    public Component canPlayerLogin(SocketAddress socketAddress, NameAndId nameAndId) {
        if (this.getServer().isSingleplayerOwner(nameAndId) && this.getPlayerByName(nameAndId.name()) != null) {
            return Component.translatable((String)"multiplayer.disconnect.name_taken");
        }
        return super.canPlayerLogin(socketAddress, nameAndId);
    }

    public IntegratedServer getServer() {
        return (IntegratedServer)super.getServer();
    }

    public @Nullable CompoundTag getSingleplayerData() {
        return this.playerData;
    }

    public /* synthetic */ MinecraftServer getServer() {
        return this.getServer();
    }
}

