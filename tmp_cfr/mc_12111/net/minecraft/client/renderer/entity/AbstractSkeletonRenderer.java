/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.monster.skeleton.AbstractSkeleton
 *  net.minecraft.world.item.Items
 */
package net.minecraft.client.renderer.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.monster.skeleton.SkeletonModel;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.item.Items;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractSkeletonRenderer<T extends AbstractSkeleton, S extends SkeletonRenderState>
extends HumanoidMobRenderer<T, S, SkeletonModel<S>> {
    public AbstractSkeletonRenderer(EntityRendererProvider.Context context, ModelLayerLocation modelLayerLocation, ArmorModelSet<ModelLayerLocation> armorModelSet) {
        this(context, armorModelSet, new SkeletonModel(context.bakeLayer(modelLayerLocation)));
    }

    public AbstractSkeletonRenderer(EntityRendererProvider.Context context, ArmorModelSet<ModelLayerLocation> armorModelSet, SkeletonModel<S> skeletonModel) {
        super(context, skeletonModel, 0.5f);
        this.addLayer(new HumanoidArmorLayer(this, ArmorModelSet.bake(armorModelSet, context.getModelSet(), SkeletonModel::new), context.getEquipmentRenderer()));
    }

    @Override
    public void extractRenderState(T abstractSkeleton, S skeletonRenderState, float f) {
        super.extractRenderState(abstractSkeleton, skeletonRenderState, f);
        ((SkeletonRenderState)skeletonRenderState).isAggressive = abstractSkeleton.isAggressive();
        ((SkeletonRenderState)skeletonRenderState).isShaking = abstractSkeleton.isShaking();
        ((SkeletonRenderState)skeletonRenderState).isHoldingBow = abstractSkeleton.getMainHandItem().is(Items.BOW);
    }

    @Override
    protected boolean isShaking(S skeletonRenderState) {
        return ((SkeletonRenderState)skeletonRenderState).isShaking;
    }

    @Override
    protected HumanoidModel.ArmPose getArmPose(T abstractSkeleton, HumanoidArm humanoidArm) {
        if (abstractSkeleton.getMainArm() == humanoidArm && abstractSkeleton.isAggressive() && abstractSkeleton.getMainHandItem().is(Items.BOW)) {
            return HumanoidModel.ArmPose.BOW_AND_ARROW;
        }
        return super.getArmPose(abstractSkeleton, humanoidArm);
    }
}

