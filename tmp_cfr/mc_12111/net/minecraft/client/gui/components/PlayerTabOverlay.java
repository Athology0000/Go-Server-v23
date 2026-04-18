/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.ChatFormatting
 *  net.minecraft.Optionull
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.ComponentUtils
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.numbers.NumberFormat
 *  net.minecraft.network.chat.numbers.StyledFormat
 *  net.minecraft.resources.Identifier
 *  net.minecraft.util.ARGB
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.scores.Objective
 *  net.minecraft.world.scores.PlayerTeam
 *  net.minecraft.world.scores.ReadOnlyScoreInfo
 *  net.minecraft.world.scores.ScoreHolder
 *  net.minecraft.world.scores.Scoreboard
 *  net.minecraft.world.scores.Team
 *  net.minecraft.world.scores.criteria.ObjectiveCriteria$RenderType
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.components;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerTabOverlay {
    private static final Identifier PING_UNKNOWN_SPRITE = Identifier.withDefaultNamespace((String)"icon/ping_unknown");
    private static final Identifier PING_1_SPRITE = Identifier.withDefaultNamespace((String)"icon/ping_1");
    private static final Identifier PING_2_SPRITE = Identifier.withDefaultNamespace((String)"icon/ping_2");
    private static final Identifier PING_3_SPRITE = Identifier.withDefaultNamespace((String)"icon/ping_3");
    private static final Identifier PING_4_SPRITE = Identifier.withDefaultNamespace((String)"icon/ping_4");
    private static final Identifier PING_5_SPRITE = Identifier.withDefaultNamespace((String)"icon/ping_5");
    private static final Identifier HEART_CONTAINER_BLINKING_SPRITE = Identifier.withDefaultNamespace((String)"hud/heart/container_blinking");
    private static final Identifier HEART_CONTAINER_SPRITE = Identifier.withDefaultNamespace((String)"hud/heart/container");
    private static final Identifier HEART_FULL_BLINKING_SPRITE = Identifier.withDefaultNamespace((String)"hud/heart/full_blinking");
    private static final Identifier HEART_HALF_BLINKING_SPRITE = Identifier.withDefaultNamespace((String)"hud/heart/half_blinking");
    private static final Identifier HEART_ABSORBING_FULL_BLINKING_SPRITE = Identifier.withDefaultNamespace((String)"hud/heart/absorbing_full_blinking");
    private static final Identifier HEART_FULL_SPRITE = Identifier.withDefaultNamespace((String)"hud/heart/full");
    private static final Identifier HEART_ABSORBING_HALF_BLINKING_SPRITE = Identifier.withDefaultNamespace((String)"hud/heart/absorbing_half_blinking");
    private static final Identifier HEART_HALF_SPRITE = Identifier.withDefaultNamespace((String)"hud/heart/half");
    private static final Comparator<PlayerInfo> PLAYER_COMPARATOR = Comparator.comparingInt(playerInfo -> -playerInfo.getTabListOrder()).thenComparingInt(playerInfo -> playerInfo.getGameMode() == GameType.SPECTATOR ? 1 : 0).thenComparing(playerInfo -> (String)Optionull.mapOrDefault((Object)playerInfo.getTeam(), PlayerTeam::getName, (Object)"")).thenComparing(playerInfo -> playerInfo.getProfile().name(), String::compareToIgnoreCase);
    public static final int MAX_ROWS_PER_COL = 20;
    private final Minecraft minecraft;
    private final Gui gui;
    private @Nullable Component footer;
    private @Nullable Component header;
    private boolean visible;
    private final Map<UUID, HealthState> healthStates = new Object2ObjectOpenHashMap();

    public PlayerTabOverlay(Minecraft minecraft, Gui gui) {
        this.minecraft = minecraft;
        this.gui = gui;
    }

    public Component getNameForDisplay(PlayerInfo playerInfo) {
        if (playerInfo.getTabListDisplayName() != null) {
            return this.decorateName(playerInfo, playerInfo.getTabListDisplayName().copy());
        }
        return this.decorateName(playerInfo, PlayerTeam.formatNameForTeam((Team)playerInfo.getTeam(), (Component)Component.literal((String)playerInfo.getProfile().name())));
    }

    private Component decorateName(PlayerInfo playerInfo, MutableComponent mutableComponent) {
        return playerInfo.getGameMode() == GameType.SPECTATOR ? mutableComponent.withStyle(ChatFormatting.ITALIC) : mutableComponent;
    }

    public void setVisible(boolean bl) {
        if (this.visible != bl) {
            this.healthStates.clear();
            this.visible = bl;
            if (bl) {
                MutableComponent component = ComponentUtils.formatList(this.getPlayerInfos(), (Component)Component.literal((String)", "), this::getNameForDisplay);
                this.minecraft.getNarrator().saySystemNow((Component)Component.translatable((String)"multiplayer.player.list.narration", (Object[])new Object[]{component}));
            }
        }
    }

    private List<PlayerInfo> getPlayerInfos() {
        return this.minecraft.player.connection.getListedOnlinePlayers().stream().sorted(PLAYER_COMPARATOR).limit(80L).toList();
    }

    public void render(GuiGraphics guiGraphics, int i, Scoreboard scoreboard, @Nullable Objective objective) {
        int y;
        int v;
        boolean bl;
        int o;
        int n;
        List<PlayerInfo> list = this.getPlayerInfos();
        ArrayList<ScoreDisplayEntry> list2 = new ArrayList<ScoreDisplayEntry>(list.size());
        int j = this.minecraft.font.width(" ");
        int k = 0;
        int l = 0;
        for (PlayerInfo playerInfo2 : list) {
            Component component = this.getNameForDisplay(playerInfo2);
            k = Math.max(k, this.minecraft.font.width((FormattedText)component));
            int m = 0;
            MutableComponent component2 = null;
            n = 0;
            if (objective != null) {
                ScoreHolder scoreHolder = ScoreHolder.fromGameProfile((GameProfile)playerInfo2.getProfile());
                ReadOnlyScoreInfo readOnlyScoreInfo = scoreboard.getPlayerScoreInfo(scoreHolder, objective);
                if (readOnlyScoreInfo != null) {
                    m = readOnlyScoreInfo.value();
                }
                if (objective.getRenderType() != ObjectiveCriteria.RenderType.HEARTS) {
                    NumberFormat numberFormat = objective.numberFormatOrDefault((NumberFormat)StyledFormat.PLAYER_LIST_DEFAULT);
                    component2 = ReadOnlyScoreInfo.safeFormatValue((ReadOnlyScoreInfo)readOnlyScoreInfo, (NumberFormat)numberFormat);
                    n = this.minecraft.font.width((FormattedText)component2);
                    l = Math.max(l, n > 0 ? j + n : 0);
                }
            }
            list2.add(new ScoreDisplayEntry(component, m, (Component)component2, n));
        }
        if (!this.healthStates.isEmpty()) {
            Set set = list.stream().map(playerInfo -> playerInfo.getProfile().id()).collect(Collectors.toSet());
            this.healthStates.keySet().removeIf(uUID -> !set.contains(uUID));
        }
        int p = o = list.size();
        int q = 1;
        while (p > 20) {
            p = (o + ++q - 1) / q;
        }
        boolean bl2 = bl = this.minecraft.isLocalServer() || this.minecraft.getConnection().getConnection().isEncrypted();
        int r = objective != null ? (objective.getRenderType() == ObjectiveCriteria.RenderType.HEARTS ? 90 : l) : 0;
        n = Math.min(q * ((bl ? 9 : 0) + k + r + 13), i - 50) / q;
        int s = i / 2 - (n * q + (q - 1) * 5) / 2;
        int t = 10;
        int u = n * q + (q - 1) * 5;
        List<FormattedCharSequence> list3 = null;
        if (this.header != null) {
            list3 = this.minecraft.font.split((FormattedText)this.header, i - 50);
            for (FormattedCharSequence formattedCharSequence : list3) {
                u = Math.max(u, this.minecraft.font.width(formattedCharSequence));
            }
        }
        List<FormattedCharSequence> list4 = null;
        if (this.footer != null) {
            list4 = this.minecraft.font.split((FormattedText)this.footer, i - 50);
            for (FormattedCharSequence formattedCharSequence2 : list4) {
                u = Math.max(u, this.minecraft.font.width(formattedCharSequence2));
            }
        }
        if (list3 != null) {
            guiGraphics.fill(i / 2 - u / 2 - 1, t - 1, i / 2 + u / 2 + 1, t + list3.size() * this.minecraft.font.lineHeight, Integer.MIN_VALUE);
            for (FormattedCharSequence formattedCharSequence2 : list3) {
                v = this.minecraft.font.width(formattedCharSequence2);
                guiGraphics.drawString(this.minecraft.font, formattedCharSequence2, i / 2 - v / 2, t, -1);
                t += this.minecraft.font.lineHeight;
            }
            ++t;
        }
        guiGraphics.fill(i / 2 - u / 2 - 1, t - 1, i / 2 + u / 2 + 1, t + p * 9, Integer.MIN_VALUE);
        int n2 = this.minecraft.options.getBackgroundColor(0x20FFFFFF);
        for (int x = 0; x < o; ++x) {
            int ab;
            int ac;
            v = x / p;
            y = x % p;
            int z = s + v * n + v * 5;
            int aa = t + y * 9;
            guiGraphics.fill(z, aa, z + n, aa + 8, n2);
            if (x >= list.size()) continue;
            PlayerInfo playerInfo2 = list.get(x);
            ScoreDisplayEntry scoreDisplayEntry = (ScoreDisplayEntry)list2.get(x);
            GameProfile gameProfile = playerInfo2.getProfile();
            if (bl) {
                Player player = this.minecraft.level.getPlayerByUUID(gameProfile.id());
                boolean bl22 = player != null && AvatarRenderer.isPlayerUpsideDown(player);
                PlayerFaceRenderer.draw(guiGraphics, playerInfo2.getSkin().body().texturePath(), z, aa, 8, playerInfo2.showHat(), bl22, -1);
                z += 9;
            }
            guiGraphics.drawString(this.minecraft.font, scoreDisplayEntry.name, z, aa, playerInfo2.getGameMode() == GameType.SPECTATOR ? -1862270977 : -1);
            if (objective != null && playerInfo2.getGameMode() != GameType.SPECTATOR && (ac = (ab = z + k + 1) + r) - ab > 5) {
                this.renderTablistScore(objective, aa, scoreDisplayEntry, ab, ac, gameProfile.id(), guiGraphics);
            }
            this.renderPingIcon(guiGraphics, n, z - (bl ? 9 : 0), aa, playerInfo2);
        }
        if (list4 != null) {
            guiGraphics.fill(i / 2 - u / 2 - 1, (t += p * 9 + 1) - 1, i / 2 + u / 2 + 1, t + list4.size() * this.minecraft.font.lineHeight, Integer.MIN_VALUE);
            for (FormattedCharSequence formattedCharSequence3 : list4) {
                y = this.minecraft.font.width(formattedCharSequence3);
                guiGraphics.drawString(this.minecraft.font, formattedCharSequence3, i / 2 - y / 2, t, -1);
                t += this.minecraft.font.lineHeight;
            }
        }
    }

    protected void renderPingIcon(GuiGraphics guiGraphics, int i, int j, int k, PlayerInfo playerInfo) {
        Identifier identifier = playerInfo.getLatency() < 0 ? PING_UNKNOWN_SPRITE : (playerInfo.getLatency() < 150 ? PING_5_SPRITE : (playerInfo.getLatency() < 300 ? PING_4_SPRITE : (playerInfo.getLatency() < 600 ? PING_3_SPRITE : (playerInfo.getLatency() < 1000 ? PING_2_SPRITE : PING_1_SPRITE))));
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, identifier, j + i - 11, k, 10, 8);
    }

    private void renderTablistScore(Objective objective, int i, ScoreDisplayEntry scoreDisplayEntry, int j, int k, UUID uUID, GuiGraphics guiGraphics) {
        if (objective.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
            this.renderTablistHearts(i, j, k, uUID, guiGraphics, scoreDisplayEntry.score);
        } else if (scoreDisplayEntry.formattedScore != null) {
            guiGraphics.drawString(this.minecraft.font, scoreDisplayEntry.formattedScore, k - scoreDisplayEntry.scoreWidth, i, -1);
        }
    }

    private void renderTablistHearts(int i, int j, int k, UUID uUID2, GuiGraphics guiGraphics, int l) {
        int p;
        HealthState healthState = this.healthStates.computeIfAbsent(uUID2, uUID -> new HealthState(l));
        healthState.update(l, this.gui.getGuiTicks());
        int m = Mth.positiveCeilDiv((int)Math.max(l, healthState.displayedValue()), (int)2);
        int n = Math.max(l, Math.max(healthState.displayedValue(), 20)) / 2;
        boolean bl = healthState.isBlinking(this.gui.getGuiTicks());
        if (m <= 0) {
            return;
        }
        int o = Mth.floor((float)Math.min((float)(k - j - 4) / (float)n, 9.0f));
        if (o <= 3) {
            float f = Mth.clamp((float)((float)l / 20.0f), (float)0.0f, (float)1.0f);
            int p2 = (int)((1.0f - f) * 255.0f) << 16 | (int)(f * 255.0f) << 8;
            float g = (float)l / 2.0f;
            MutableComponent component = Component.translatable((String)"multiplayer.player.list.hp", (Object[])new Object[]{Float.valueOf(g)});
            MutableComponent component2 = k - this.minecraft.font.width((FormattedText)component) >= j ? component : Component.literal((String)Float.toString(g));
            guiGraphics.drawString(this.minecraft.font, (Component)component2, (k + j - this.minecraft.font.width((FormattedText)component2)) / 2, i, ARGB.opaque((int)p2));
            return;
        }
        Identifier identifier = bl ? HEART_CONTAINER_BLINKING_SPRITE : HEART_CONTAINER_SPRITE;
        for (p = m; p < n; ++p) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, identifier, j + p * o, i, 9, 9);
        }
        for (p = 0; p < m; ++p) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, identifier, j + p * o, i, 9, 9);
            if (bl) {
                if (p * 2 + 1 < healthState.displayedValue()) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, HEART_FULL_BLINKING_SPRITE, j + p * o, i, 9, 9);
                }
                if (p * 2 + 1 == healthState.displayedValue()) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, HEART_HALF_BLINKING_SPRITE, j + p * o, i, 9, 9);
                }
            }
            if (p * 2 + 1 < l) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, p >= 10 ? HEART_ABSORBING_FULL_BLINKING_SPRITE : HEART_FULL_SPRITE, j + p * o, i, 9, 9);
            }
            if (p * 2 + 1 != l) continue;
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, p >= 10 ? HEART_ABSORBING_HALF_BLINKING_SPRITE : HEART_HALF_SPRITE, j + p * o, i, 9, 9);
        }
    }

    public void setFooter(@Nullable Component component) {
        this.footer = component;
    }

    public void setHeader(@Nullable Component component) {
        this.header = component;
    }

    public void reset() {
        this.header = null;
        this.footer = null;
    }

    @Environment(value=EnvType.CLIENT)
    static final class ScoreDisplayEntry
    extends Record {
        final Component name;
        final int score;
        final @Nullable Component formattedScore;
        final int scoreWidth;

        ScoreDisplayEntry(Component component, int i, @Nullable Component component2, int j) {
            this.name = component;
            this.score = i;
            this.formattedScore = component2;
            this.scoreWidth = j;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ScoreDisplayEntry.class, "name;score;formattedScore;scoreWidth", "name", "score", "formattedScore", "scoreWidth"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ScoreDisplayEntry.class, "name;score;formattedScore;scoreWidth", "name", "score", "formattedScore", "scoreWidth"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ScoreDisplayEntry.class, "name;score;formattedScore;scoreWidth", "name", "score", "formattedScore", "scoreWidth"}, this, object);
        }

        public Component name() {
            return this.name;
        }

        public int score() {
            return this.score;
        }

        public @Nullable Component formattedScore() {
            return this.formattedScore;
        }

        public int scoreWidth() {
            return this.scoreWidth;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class HealthState {
        private static final long DISPLAY_UPDATE_DELAY = 20L;
        private static final long DECREASE_BLINK_DURATION = 20L;
        private static final long INCREASE_BLINK_DURATION = 10L;
        private int lastValue;
        private int displayedValue;
        private long lastUpdateTick;
        private long blinkUntilTick;

        public HealthState(int i) {
            this.displayedValue = i;
            this.lastValue = i;
        }

        public void update(int i, long l) {
            if (i != this.lastValue) {
                long m = i < this.lastValue ? 20L : 10L;
                this.blinkUntilTick = l + m;
                this.lastValue = i;
                this.lastUpdateTick = l;
            }
            if (l - this.lastUpdateTick > 20L) {
                this.displayedValue = i;
            }
        }

        public int displayedValue() {
            return this.displayedValue;
        }

        public boolean isBlinking(long l) {
            return this.blinkUntilTick > l && (this.blinkUntilTick - l) % 6L >= 3L;
        }
    }
}

