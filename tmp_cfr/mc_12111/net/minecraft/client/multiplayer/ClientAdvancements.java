/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.advancements.AdvancementHolder
 *  net.minecraft.advancements.AdvancementNode
 *  net.minecraft.advancements.AdvancementProgress
 *  net.minecraft.advancements.AdvancementTree
 *  net.minecraft.advancements.AdvancementTree$Listener
 *  net.minecraft.advancements.DisplayInfo
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket
 *  net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket
 *  net.minecraft.resources.Identifier
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.multiplayer;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientAdvancements {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Minecraft minecraft;
    private final WorldSessionTelemetryManager telemetryManager;
    private final AdvancementTree tree = new AdvancementTree();
    private final Map<AdvancementHolder, AdvancementProgress> progress = new Object2ObjectOpenHashMap();
    private @Nullable Listener listener;
    private @Nullable AdvancementHolder selectedTab;

    public ClientAdvancements(Minecraft minecraft, WorldSessionTelemetryManager worldSessionTelemetryManager) {
        this.minecraft = minecraft;
        this.telemetryManager = worldSessionTelemetryManager;
    }

    public void update(ClientboundUpdateAdvancementsPacket clientboundUpdateAdvancementsPacket) {
        if (clientboundUpdateAdvancementsPacket.shouldReset()) {
            this.tree.clear();
            this.progress.clear();
        }
        this.tree.remove(clientboundUpdateAdvancementsPacket.getRemoved());
        this.tree.addAll((Collection)clientboundUpdateAdvancementsPacket.getAdded());
        for (Map.Entry entry : clientboundUpdateAdvancementsPacket.getProgress().entrySet()) {
            AdvancementNode advancementNode = this.tree.get((Identifier)entry.getKey());
            if (advancementNode != null) {
                AdvancementProgress advancementProgress = (AdvancementProgress)entry.getValue();
                advancementProgress.update(advancementNode.advancement().requirements());
                this.progress.put(advancementNode.holder(), advancementProgress);
                if (this.listener != null) {
                    this.listener.onUpdateAdvancementProgress(advancementNode, advancementProgress);
                }
                if (clientboundUpdateAdvancementsPacket.shouldReset() || !advancementProgress.isDone()) continue;
                if (this.minecraft.level != null) {
                    this.telemetryManager.onAdvancementDone(this.minecraft.level, advancementNode.holder());
                }
                Optional optional = advancementNode.advancement().display();
                if (!clientboundUpdateAdvancementsPacket.shouldShowAdvancements() || !optional.isPresent() || !((DisplayInfo)optional.get()).shouldShowToast()) continue;
                this.minecraft.getToastManager().addToast(new AdvancementToast(advancementNode.holder()));
                continue;
            }
            LOGGER.warn("Server informed client about progress for unknown advancement {}", entry.getKey());
        }
    }

    public AdvancementTree getTree() {
        return this.tree;
    }

    public void setSelectedTab(@Nullable AdvancementHolder advancementHolder, boolean bl) {
        ClientPacketListener clientPacketListener = this.minecraft.getConnection();
        if (clientPacketListener != null && advancementHolder != null && bl) {
            clientPacketListener.send((Packet<?>)ServerboundSeenAdvancementsPacket.openedTab((AdvancementHolder)advancementHolder));
        }
        if (this.selectedTab != advancementHolder) {
            this.selectedTab = advancementHolder;
            if (this.listener != null) {
                this.listener.onSelectedTabChanged(advancementHolder);
            }
        }
    }

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
        this.tree.setListener((AdvancementTree.Listener)listener);
        if (listener != null) {
            this.progress.forEach((advancementHolder, advancementProgress) -> {
                AdvancementNode advancementNode = this.tree.get(advancementHolder);
                if (advancementNode != null) {
                    listener.onUpdateAdvancementProgress(advancementNode, (AdvancementProgress)advancementProgress);
                }
            });
            listener.onSelectedTabChanged(this.selectedTab);
        }
    }

    public @Nullable AdvancementHolder get(Identifier identifier) {
        AdvancementNode advancementNode = this.tree.get(identifier);
        return advancementNode != null ? advancementNode.holder() : null;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Listener
    extends AdvancementTree.Listener {
        public void onUpdateAdvancementProgress(AdvancementNode var1, AdvancementProgress var2);

        public void onSelectedTabChanged(@Nullable AdvancementHolder var1);
    }
}

