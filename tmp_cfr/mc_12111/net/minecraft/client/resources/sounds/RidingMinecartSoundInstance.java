/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.vehicle.minecart.AbstractMinecart
 *  net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior
 */
package net.minecraft.client.resources.sounds;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.sounds.RidingEntitySoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;

@Environment(value=EnvType.CLIENT)
public class RidingMinecartSoundInstance
extends RidingEntitySoundInstance {
    private final Player player;
    private final AbstractMinecart minecart;
    private final boolean underwaterSound;

    public RidingMinecartSoundInstance(Player player, AbstractMinecart abstractMinecart, boolean bl, SoundEvent soundEvent, float f, float g, float h) {
        super(player, (Entity)abstractMinecart, bl, soundEvent, SoundSource.NEUTRAL, f, g, h);
        this.player = player;
        this.minecart = abstractMinecart;
        this.underwaterSound = bl;
    }

    @Override
    protected boolean shouldNotPlayUnderwaterSound() {
        return this.underwaterSound != this.player.isUnderWater();
    }

    @Override
    protected float getEntitySpeed() {
        return (float)this.minecart.getDeltaMovement().horizontalDistance();
    }

    @Override
    protected boolean shoudlPlaySound() {
        return this.minecart.isOnRails() || !(this.minecart.getBehavior() instanceof NewMinecartBehavior);
    }
}

