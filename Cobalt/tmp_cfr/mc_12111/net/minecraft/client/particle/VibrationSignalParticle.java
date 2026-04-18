/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.core.particles.VibrationParticleOption
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.gameevent.PositionSource
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaternionf
 */
package net.minecraft.client.particle;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

@Environment(value=EnvType.CLIENT)
public class VibrationSignalParticle
extends SingleQuadParticle {
    private final PositionSource target;
    private float rot;
    private float rotO;
    private float pitch;
    private float pitchO;

    VibrationSignalParticle(ClientLevel clientLevel, double d, double e, double f, PositionSource positionSource, int i, TextureAtlasSprite textureAtlasSprite) {
        super(clientLevel, d, e, f, 0.0, 0.0, 0.0, textureAtlasSprite);
        this.quadSize = 0.3f;
        this.target = positionSource;
        this.lifetime = i;
        Optional optional = positionSource.getPosition((Level)clientLevel);
        if (optional.isPresent()) {
            Vec3 vec3 = (Vec3)optional.get();
            double g = d - vec3.x();
            double h = e - vec3.y();
            double j = f - vec3.z();
            this.rotO = this.rot = (float)Mth.atan2((double)g, (double)j);
            this.pitchO = this.pitch = (float)Mth.atan2((double)h, (double)Math.sqrt(g * g + j * j));
        }
    }

    @Override
    public void extract(QuadParticleRenderState quadParticleRenderState, Camera camera, float f) {
        float g = Mth.sin((double)(((float)this.age + f - (float)Math.PI * 2) * 0.05f)) * 2.0f;
        float h = Mth.lerp((float)f, (float)this.rotO, (float)this.rot);
        float i = Mth.lerp((float)f, (float)this.pitchO, (float)this.pitch) + 1.5707964f;
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotationY(h).rotateX(-i).rotateY(g);
        this.extractRotatedQuad(quadParticleRenderState, camera, quaternionf, f);
        quaternionf.rotationY((float)(-Math.PI) + h).rotateX(i).rotateY(g);
        this.extractRotatedQuad(quadParticleRenderState, camera, quaternionf, f);
    }

    @Override
    public int getLightColor(float f) {
        return 240;
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        Optional optional = this.target.getPosition((Level)this.level);
        if (optional.isEmpty()) {
            this.remove();
            return;
        }
        int i = this.lifetime - this.age;
        double d = 1.0 / (double)i;
        Vec3 vec3 = (Vec3)optional.get();
        this.x = Mth.lerp((double)d, (double)this.x, (double)vec3.x());
        this.y = Mth.lerp((double)d, (double)this.y, (double)vec3.y());
        this.z = Mth.lerp((double)d, (double)this.z, (double)vec3.z());
        double e = this.x - vec3.x();
        double f = this.y - vec3.y();
        double g = this.z - vec3.z();
        this.rotO = this.rot;
        this.rot = (float)Mth.atan2((double)e, (double)g);
        this.pitchO = this.pitch;
        this.pitch = (float)Mth.atan2((double)f, (double)Math.sqrt(e * e + g * g));
    }

    @Environment(value=EnvType.CLIENT)
    public static class Provider
    implements ParticleProvider<VibrationParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(VibrationParticleOption vibrationParticleOption, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, RandomSource randomSource) {
            VibrationSignalParticle vibrationSignalParticle = new VibrationSignalParticle(clientLevel, d, e, f, vibrationParticleOption.getDestination(), vibrationParticleOption.getArrivalInTicks(), this.sprite.get(randomSource));
            vibrationSignalParticle.setAlpha(1.0f);
            return vibrationSignalParticle;
        }
    }
}

