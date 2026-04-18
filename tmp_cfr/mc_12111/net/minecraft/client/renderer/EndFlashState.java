/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 */
package net.minecraft.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

@Environment(value=EnvType.CLIENT)
public class EndFlashState {
    public static final int SOUND_DELAY_IN_TICKS = 30;
    private static final int FLASH_INTERVAL_IN_TICKS = 600;
    private static final int MAX_FLASH_OFFSET_IN_TICKS = 200;
    private static final int MIN_FLASH_DURATION_IN_TICKS = 100;
    private static final int MAX_FLASH_DURATION_IN_TICKS = 380;
    private long flashSeed;
    private int offset;
    private int duration;
    private float intensity;
    private float oldIntensity;
    private float xAngle;
    private float yAngle;

    public void tick(long l) {
        this.calculateFlashParameters(l);
        this.oldIntensity = this.intensity;
        this.intensity = this.calculateIntensity(l);
    }

    private void calculateFlashParameters(long l) {
        long m = l / 600L;
        if (m != this.flashSeed) {
            RandomSource randomSource = RandomSource.create((long)m);
            randomSource.nextFloat();
            this.offset = Mth.randomBetweenInclusive((RandomSource)randomSource, (int)0, (int)200);
            this.duration = Mth.randomBetweenInclusive((RandomSource)randomSource, (int)100, (int)Math.min(380, 600 - this.offset));
            this.xAngle = Mth.randomBetween((RandomSource)randomSource, (float)-60.0f, (float)10.0f);
            this.yAngle = Mth.randomBetween((RandomSource)randomSource, (float)-180.0f, (float)180.0f);
            this.flashSeed = m;
        }
    }

    private float calculateIntensity(long l) {
        long m = l % 600L;
        if (m < (long)this.offset || m > (long)(this.offset + this.duration)) {
            return 0.0f;
        }
        return Mth.sin((double)((float)(m - (long)this.offset) * (float)Math.PI / (float)this.duration));
    }

    public float getXAngle() {
        return this.xAngle;
    }

    public float getYAngle() {
        return this.yAngle;
    }

    public float getIntensity(float f) {
        return Mth.lerp((float)f, (float)this.oldIntensity, (float)this.intensity);
    }

    public boolean flashStartedThisTick() {
        return this.intensity > 0.0f && this.oldIntensity <= 0.0f;
    }
}

