/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.util.Mth
 */
package net.minecraft.client.model.object.boat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.util.Mth;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractBoatModel
extends EntityModel<BoatRenderState> {
    private final ModelPart leftPaddle;
    private final ModelPart rightPaddle;

    public AbstractBoatModel(ModelPart modelPart) {
        super(modelPart);
        this.leftPaddle = modelPart.getChild("left_paddle");
        this.rightPaddle = modelPart.getChild("right_paddle");
    }

    @Override
    public void setupAnim(BoatRenderState boatRenderState) {
        super.setupAnim(boatRenderState);
        AbstractBoatModel.animatePaddle(boatRenderState.rowingTimeLeft, 0, this.leftPaddle);
        AbstractBoatModel.animatePaddle(boatRenderState.rowingTimeRight, 1, this.rightPaddle);
    }

    private static void animatePaddle(float f, int i, ModelPart modelPart) {
        modelPart.xRot = Mth.clampedLerp((float)((Mth.sin((double)(-f)) + 1.0f) / 2.0f), (float)-1.0471976f, (float)-0.2617994f);
        modelPart.yRot = Mth.clampedLerp((float)((Mth.sin((double)(-f + 1.0f)) + 1.0f) / 2.0f), (float)-0.7853982f, (float)0.7853982f);
        if (i == 1) {
            modelPart.yRot = (float)Math.PI - modelPart.yRot;
        }
    }
}

