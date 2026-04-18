/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.RegistryAccess$Frozen
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.WorldStem
 *  net.minecraft.server.packs.repository.PackRepository
 *  net.minecraft.server.packs.repository.ServerPacksSource
 *  net.minecraft.util.Mth
 *  net.minecraft.util.Util
 *  net.minecraft.util.worldupdate.WorldUpgrader
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.storage.LevelStorageSource$LevelStorageAccess
 *  net.minecraft.world.level.storage.WorldData
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screens.worldselection;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.function.ToIntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class OptimizeWorldScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ToIntFunction<ResourceKey<Level>> DIMENSION_COLORS = (ToIntFunction)Util.make((Object)new Reference2IntOpenHashMap(), reference2IntOpenHashMap -> {
        reference2IntOpenHashMap.put((Object)Level.OVERWORLD, -13408734);
        reference2IntOpenHashMap.put((Object)Level.NETHER, -10075085);
        reference2IntOpenHashMap.put((Object)Level.END, -8943531);
        reference2IntOpenHashMap.defaultReturnValue(-2236963);
    });
    private final BooleanConsumer callback;
    private final WorldUpgrader upgrader;

    public static @Nullable OptimizeWorldScreen create(Minecraft minecraft, BooleanConsumer booleanConsumer, DataFixer dataFixer, LevelStorageSource.LevelStorageAccess levelStorageAccess, boolean bl) {
        OptimizeWorldScreen optimizeWorldScreen;
        block8: {
            WorldOpenFlows worldOpenFlows = minecraft.createWorldOpenFlows();
            PackRepository packRepository = ServerPacksSource.createPackRepository((LevelStorageSource.LevelStorageAccess)levelStorageAccess);
            WorldStem worldStem = worldOpenFlows.loadWorldStem(levelStorageAccess.getDataTag(), false, packRepository);
            try {
                WorldData worldData = worldStem.worldData();
                RegistryAccess.Frozen frozen = worldStem.registries().compositeAccess();
                levelStorageAccess.saveDataTag((RegistryAccess)frozen, worldData);
                optimizeWorldScreen = new OptimizeWorldScreen(booleanConsumer, dataFixer, levelStorageAccess, worldData, bl, (RegistryAccess)frozen);
                if (worldStem == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (worldStem != null) {
                        try {
                            worldStem.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception exception) {
                    LOGGER.warn("Failed to load datapacks, can't optimize world", (Throwable)exception);
                    return null;
                }
            }
            worldStem.close();
        }
        return optimizeWorldScreen;
    }

    private OptimizeWorldScreen(BooleanConsumer booleanConsumer, DataFixer dataFixer, LevelStorageSource.LevelStorageAccess levelStorageAccess, WorldData worldData, boolean bl, RegistryAccess registryAccess) {
        super((Component)Component.translatable((String)"optimizeWorld.title", (Object[])new Object[]{worldData.getLevelSettings().levelName()}));
        this.callback = booleanConsumer;
        this.upgrader = new WorldUpgrader(levelStorageAccess, dataFixer, worldData, registryAccess, bl, false);
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> {
            this.upgrader.cancel();
            this.callback.accept(false);
        }).bounds(this.width / 2 - 100, this.height / 4 + 150, 200, 20).build());
    }

    @Override
    public void tick() {
        if (this.upgrader.isFinished()) {
            this.callback.accept(true);
        }
    }

    @Override
    public void onClose() {
        this.callback.accept(false);
    }

    @Override
    public void removed() {
        this.upgrader.cancel();
        this.upgrader.close();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, -1);
        int k = this.width / 2 - 150;
        int l = this.width / 2 + 150;
        int m = this.height / 4 + 100;
        int n = m + 10;
        guiGraphics.drawCenteredString(this.font, this.upgrader.getStatus(), this.width / 2, m - this.font.lineHeight - 2, -6250336);
        if (this.upgrader.getTotalChunks() > 0) {
            guiGraphics.fill(k - 1, m - 1, l + 1, n + 1, -16777216);
            guiGraphics.drawString(this.font, (Component)Component.translatable((String)"optimizeWorld.info.converted", (Object[])new Object[]{this.upgrader.getConverted()}), k, 40, -6250336);
            guiGraphics.drawString(this.font, (Component)Component.translatable((String)"optimizeWorld.info.skipped", (Object[])new Object[]{this.upgrader.getSkipped()}), k, 40 + this.font.lineHeight + 3, -6250336);
            guiGraphics.drawString(this.font, (Component)Component.translatable((String)"optimizeWorld.info.total", (Object[])new Object[]{this.upgrader.getTotalChunks()}), k, 40 + (this.font.lineHeight + 3) * 2, -6250336);
            int o = 0;
            for (ResourceKey resourceKey : this.upgrader.levels()) {
                int p = Mth.floor((float)(this.upgrader.dimensionProgress(resourceKey) * (float)(l - k)));
                guiGraphics.fill(k + o, m, k + o + p, n, DIMENSION_COLORS.applyAsInt((ResourceKey<Level>)resourceKey));
                o += p;
            }
            int q = this.upgrader.getConverted() + this.upgrader.getSkipped();
            MutableComponent component = Component.translatable((String)"optimizeWorld.progress.counter", (Object[])new Object[]{q, this.upgrader.getTotalChunks()});
            MutableComponent component2 = Component.translatable((String)"optimizeWorld.progress.percentage", (Object[])new Object[]{Mth.floor((float)(this.upgrader.getProgress() * 100.0f))});
            guiGraphics.drawCenteredString(this.font, (Component)component, this.width / 2, m + 2 * this.font.lineHeight + 2, -6250336);
            guiGraphics.drawCenteredString(this.font, (Component)component2, this.width / 2, m + (n - m) / 2 - this.font.lineHeight / 2, -6250336);
        }
    }
}

