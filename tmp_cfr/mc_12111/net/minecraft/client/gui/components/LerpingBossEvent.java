/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.util.Util
 *  net.minecraft.world.BossEvent
 *  net.minecraft.world.BossEvent$BossBarColor
 *  net.minecraft.world.BossEvent$BossBarOverlay
 */
package net.minecraft.client.gui.components;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.BossEvent;

@Environment(value=EnvType.CLIENT)
public class LerpingBossEvent
extends BossEvent {
    private static final long LERP_MILLISECONDS = 100L;
    protected float targetPercent;
    protected long setTime;

    public LerpingBossEvent(UUID uUID, Component component, float f, BossEvent.BossBarColor bossBarColor, BossEvent.BossBarOverlay bossBarOverlay, boolean bl, boolean bl2, boolean bl3) {
        super(uUID, component, bossBarColor, bossBarOverlay);
        this.targetPercent = f;
        this.progress = f;
        this.setTime = Util.getMillis();
        this.setDarkenScreen(bl);
        this.setPlayBossMusic(bl2);
        this.setCreateWorldFog(bl3);
    }

    public void setProgress(float f) {
        this.progress = this.getProgress();
        this.targetPercent = f;
        this.setTime = Util.getMillis();
    }

    public float getProgress() {
        long l = Util.getMillis() - this.setTime;
        float f = Mth.clamp((float)((float)l / 100.0f), (float)0.0f, (float)1.0f);
        return Mth.lerp((float)f, (float)this.progress, (float)this.targetPercent);
    }
}

