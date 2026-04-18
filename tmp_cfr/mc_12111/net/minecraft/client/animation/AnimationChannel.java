/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.util.Mth
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public record AnimationChannel(Target target, Keyframe[] keyframes) {

    @Environment(value=EnvType.CLIENT)
    public static interface Target {
        public void apply(ModelPart var1, Vector3f var2);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Interpolations {
        public static final Interpolation LINEAR = (vector3f, f, keyframes, i, j, g) -> {
            Vector3fc vector3fc = keyframes[i].postTarget();
            Vector3fc vector3fc2 = keyframes[j].preTarget();
            return vector3fc.lerp(vector3fc2, f, vector3f).mul(g);
        };
        public static final Interpolation CATMULLROM = (vector3f, f, keyframes, i, j, g) -> {
            Vector3fc vector3fc = keyframes[Math.max(0, i - 1)].postTarget();
            Vector3fc vector3fc2 = keyframes[i].postTarget();
            Vector3fc vector3fc3 = keyframes[j].postTarget();
            Vector3fc vector3fc4 = keyframes[Math.min(keyframes.length - 1, j + 1)].postTarget();
            vector3f.set(Mth.catmullrom((float)f, (float)vector3fc.x(), (float)vector3fc2.x(), (float)vector3fc3.x(), (float)vector3fc4.x()) * g, Mth.catmullrom((float)f, (float)vector3fc.y(), (float)vector3fc2.y(), (float)vector3fc3.y(), (float)vector3fc4.y()) * g, Mth.catmullrom((float)f, (float)vector3fc.z(), (float)vector3fc2.z(), (float)vector3fc3.z(), (float)vector3fc4.z()) * g);
            return vector3f;
        };
    }

    @Environment(value=EnvType.CLIENT)
    public static class Targets {
        public static final Target POSITION = ModelPart::offsetPos;
        public static final Target ROTATION = ModelPart::offsetRotation;
        public static final Target SCALE = ModelPart::offsetScale;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Interpolation {
        public Vector3f apply(Vector3f var1, float var2, Keyframe[] var3, int var4, int var5, float var6);
    }
}

