/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.resources.Identifier
 *  net.minecraft.world.entity.animal.equine.Horse
 *  net.minecraft.world.entity.animal.equine.Variant
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.animal.equine.EquineSaddleModel;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HorseMarkingLayer;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Variant;

@Environment(value=EnvType.CLIENT)
public final class HorseRenderer
extends AbstractHorseRenderer<Horse, HorseRenderState, HorseModel> {
    private static final Map<Variant, Identifier> LOCATION_BY_VARIANT = Maps.newEnumMap(Map.of(Variant.WHITE, Identifier.withDefaultNamespace((String)"textures/entity/horse/horse_white.png"), Variant.CREAMY, Identifier.withDefaultNamespace((String)"textures/entity/horse/horse_creamy.png"), Variant.CHESTNUT, Identifier.withDefaultNamespace((String)"textures/entity/horse/horse_chestnut.png"), Variant.BROWN, Identifier.withDefaultNamespace((String)"textures/entity/horse/horse_brown.png"), Variant.BLACK, Identifier.withDefaultNamespace((String)"textures/entity/horse/horse_black.png"), Variant.GRAY, Identifier.withDefaultNamespace((String)"textures/entity/horse/horse_gray.png"), Variant.DARK_BROWN, Identifier.withDefaultNamespace((String)"textures/entity/horse/horse_darkbrown.png")));

    public HorseRenderer(EntityRendererProvider.Context context) {
        super(context, new HorseModel(context.bakeLayer(ModelLayers.HORSE)), new HorseModel(context.bakeLayer(ModelLayers.HORSE_BABY)));
        this.addLayer(new HorseMarkingLayer(this));
        this.addLayer(new SimpleEquipmentLayer<HorseRenderState, HorseModel, HorseModel>(this, context.getEquipmentRenderer(), EquipmentClientInfo.LayerType.HORSE_BODY, horseRenderState -> horseRenderState.bodyArmorItem, new HorseModel(context.bakeLayer(ModelLayers.HORSE_ARMOR)), new HorseModel(context.bakeLayer(ModelLayers.HORSE_BABY_ARMOR)), 2));
        this.addLayer(new SimpleEquipmentLayer<HorseRenderState, HorseModel, EquineSaddleModel>(this, context.getEquipmentRenderer(), EquipmentClientInfo.LayerType.HORSE_SADDLE, horseRenderState -> horseRenderState.saddle, new EquineSaddleModel(context.bakeLayer(ModelLayers.HORSE_SADDLE)), new EquineSaddleModel(context.bakeLayer(ModelLayers.HORSE_BABY_SADDLE)), 2));
    }

    @Override
    public Identifier getTextureLocation(HorseRenderState horseRenderState) {
        return LOCATION_BY_VARIANT.get(horseRenderState.variant);
    }

    @Override
    public HorseRenderState createRenderState() {
        return new HorseRenderState();
    }

    @Override
    public void extractRenderState(Horse horse, HorseRenderState horseRenderState, float f) {
        super.extractRenderState(horse, horseRenderState, f);
        horseRenderState.variant = horse.getVariant();
        horseRenderState.markings = horse.getMarkings();
        horseRenderState.bodyArmorItem = horse.getBodyArmorItem().copy();
    }

    @Override
    public /* synthetic */ Identifier getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((HorseRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

