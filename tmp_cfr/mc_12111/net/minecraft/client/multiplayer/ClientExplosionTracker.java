/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.particles.ExplosionParticleInfo
 *  net.minecraft.server.level.ParticleStatus
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.random.WeightedList
 *  net.minecraft.util.random.WeightedRandom
 *  net.minecraft.world.phys.Vec3
 */
package net.minecraft.client.multiplayer;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.phys.Vec3;

@Environment(value=EnvType.CLIENT)
public class ClientExplosionTracker {
    private static final int MAX_PARTICLES_PER_TICK = 512;
    private final List<ExplosionInfo> explosions = new ArrayList<ExplosionInfo>();

    public void track(Vec3 vec3, float f, int i, WeightedList<ExplosionParticleInfo> weightedList) {
        if (!weightedList.isEmpty()) {
            this.explosions.add(new ExplosionInfo(vec3, f, i, weightedList));
        }
    }

    public void tick(ClientLevel clientLevel) {
        if (Minecraft.getInstance().options.particles().get() != ParticleStatus.ALL) {
            this.explosions.clear();
            return;
        }
        int i = WeightedRandom.getTotalWeight(this.explosions, ExplosionInfo::blockCount);
        int j = Math.min(i, 512);
        for (int k = 0; k < j; ++k) {
            WeightedRandom.getRandomItem((RandomSource)clientLevel.getRandom(), this.explosions, (int)i, ExplosionInfo::blockCount).ifPresent(explosionInfo -> this.addParticle(clientLevel, (ExplosionInfo)explosionInfo));
        }
        this.explosions.clear();
    }

    private void addParticle(ClientLevel clientLevel, ExplosionInfo explosionInfo) {
        float f;
        Vec3 vec32;
        Vec3 vec33;
        RandomSource randomSource = clientLevel.getRandom();
        Vec3 vec3 = explosionInfo.center();
        Vec3 vec34 = vec3.add(vec33 = (vec32 = new Vec3((double)(randomSource.nextFloat() * 2.0f - 1.0f), (double)(randomSource.nextFloat() * 2.0f - 1.0f), (double)(randomSource.nextFloat() * 2.0f - 1.0f)).normalize()).scale((double)(f = (float)Math.cbrt(randomSource.nextFloat()) * explosionInfo.radius())));
        if (!clientLevel.getBlockState(BlockPos.containing((Position)vec34)).isAir()) {
            return;
        }
        float g = 0.5f / (f / explosionInfo.radius() + 0.1f) * randomSource.nextFloat() * randomSource.nextFloat() + 0.3f;
        ExplosionParticleInfo explosionParticleInfo = (ExplosionParticleInfo)explosionInfo.blockParticles.getRandomOrThrow(randomSource);
        Vec3 vec35 = vec3.add(vec33.scale((double)explosionParticleInfo.scaling()));
        Vec3 vec36 = vec32.scale((double)(g * explosionParticleInfo.speed()));
        clientLevel.addParticle(explosionParticleInfo.particle(), vec35.x(), vec35.y(), vec35.z(), vec36.x(), vec36.y(), vec36.z());
    }

    @Environment(value=EnvType.CLIENT)
    static final class ExplosionInfo
    extends Record {
        private final Vec3 center;
        private final float radius;
        private final int blockCount;
        final WeightedList<ExplosionParticleInfo> blockParticles;

        ExplosionInfo(Vec3 vec3, float f, int i, WeightedList<ExplosionParticleInfo> weightedList) {
            this.center = vec3;
            this.radius = f;
            this.blockCount = i;
            this.blockParticles = weightedList;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ExplosionInfo.class, "center;radius;blockCount;blockParticles", "center", "radius", "blockCount", "blockParticles"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ExplosionInfo.class, "center;radius;blockCount;blockParticles", "center", "radius", "blockCount", "blockParticles"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ExplosionInfo.class, "center;radius;blockCount;blockParticles", "center", "radius", "blockCount", "blockParticles"}, this, object);
        }

        public Vec3 center() {
            return this.center;
        }

        public float radius() {
            return this.radius;
        }

        public int blockCount() {
            return this.blockCount;
        }

        public WeightedList<ExplosionParticleInfo> blockParticles() {
            return this.blockParticles;
        }
    }
}

