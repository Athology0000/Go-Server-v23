/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Axis
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.resources.Identifier
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.animal.squid.Squid
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.animal.squid.SquidModel;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.SquidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.squid.Squid;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class SquidRenderer<T extends Squid>
extends AgeableMobRenderer<T, SquidRenderState, SquidModel> {
    private static final Identifier SQUID_LOCATION = Identifier.withDefaultNamespace((String)"textures/entity/squid/squid.png");

    public SquidRenderer(EntityRendererProvider.Context context, SquidModel squidModel, SquidModel squidModel2) {
        super(context, squidModel, squidModel2, 0.7f);
    }

    @Override
    public Identifier getTextureLocation(SquidRenderState squidRenderState) {
        return SQUID_LOCATION;
    }

    @Override
    public SquidRenderState createRenderState() {
        return new SquidRenderState();
    }

    @Override
    public void extractRenderState(T squid, SquidRenderState squidRenderState, float f) {
        super.extractRenderState(squid, squidRenderState, f);
        squidRenderState.tentacleAngle = Mth.lerp((float)f, (float)((Squid)squid).oldTentacleAngle, (float)((Squid)squid).tentacleAngle);
        squidRenderState.xBodyRot = Mth.lerp((float)f, (float)((Squid)squid).xBodyRotO, (float)((Squid)squid).xBodyRot);
        squidRenderState.zBodyRot = Mth.lerp((float)f, (float)((Squid)squid).zBodyRotO, (float)((Squid)squid).zBodyRot);
    }

    @Override
    protected void setupRotations(SquidRenderState squidRenderState, PoseStack poseStack, float f, float g) {
        poseStack.translate(0.0f, squidRenderState.isBaby ? 0.25f : 0.5f, 0.0f);
        poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0f - f));
        poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(squidRenderState.xBodyRot));
        poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(squidRenderState.zBodyRot));
        poseStack.translate(0.0f, squidRenderState.isBaby ? -0.6f : -1.2f, 0.0f);
    }

    @Override
    public /* synthetic */ Identifier getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((SquidRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

