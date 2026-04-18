/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ItemParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.particles.PowerParticleOption
 *  net.minecraft.core.particles.SculkChargeParticleOptions
 *  net.minecraft.core.particles.ShriekParticleOption
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.core.particles.SpellParticleOption
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.util.ParticleUtils
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.valueproviders.IntProvider
 *  net.minecraft.util.valueproviders.UniformInt
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.BoneMealItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.JukeboxSong
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.BrushableBlock
 *  net.minecraft.world.level.block.ComposterBlock
 *  net.minecraft.world.level.block.MultifaceBlock
 *  net.minecraft.world.level.block.PointedDripstoneBlock
 *  net.minecraft.world.level.block.SculkShriekerBlock
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.trialspawner.TrialSpawner
 *  net.minecraft.world.level.block.entity.trialspawner.TrialSpawner$FlameParticle
 *  net.minecraft.world.level.block.entity.vault.VaultBlockEntity
 *  net.minecraft.world.level.block.entity.vault.VaultBlockEntity$Client
 *  net.minecraft.world.level.block.entity.vault.VaultSharedData
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package net.minecraft.client.renderer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.PowerParticleOption;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.SpellParticleOption;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultSharedData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Environment(value=EnvType.CLIENT)
public class LevelEventHandler {
    private final Minecraft minecraft;
    private final ClientLevel level;
    private final Map<BlockPos, SoundInstance> playingJukeboxSongs = new HashMap<BlockPos, SoundInstance>();

    public LevelEventHandler(Minecraft minecraft, ClientLevel clientLevel) {
        this.minecraft = minecraft;
        this.level = clientLevel;
    }

    public void globalLevelEvent(int i, BlockPos blockPos, int j) {
        switch (i) {
            case 1023: 
            case 1028: 
            case 1038: {
                Camera camera = this.minecraft.gameRenderer.getMainCamera();
                if (!camera.isInitialized()) break;
                Vec3 vec3 = Vec3.atCenterOf((Vec3i)blockPos).subtract(camera.position()).normalize();
                Vec3 vec32 = camera.position().add(vec3.scale(2.0));
                if (i == 1023) {
                    this.level.playLocalSound(vec32.x, vec32.y, vec32.z, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                if (i == 1038) {
                    this.level.playLocalSound(vec32.x, vec32.y, vec32.z, SoundEvents.END_PORTAL_SPAWN, SoundSource.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                this.level.playLocalSound(vec32.x, vec32.y, vec32.z, SoundEvents.ENDER_DRAGON_DEATH, SoundSource.HOSTILE, 5.0f, 1.0f, false);
            }
        }
    }

    public void levelEvent(int i, BlockPos blockPos, int j) {
        RandomSource randomSource = this.level.random;
        switch (i) {
            case 1035: {
                this.level.playLocalSound(blockPos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1033: {
                this.level.playLocalSound(blockPos, SoundEvents.CHORUS_FLOWER_GROW, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1034: {
                this.level.playLocalSound(blockPos, SoundEvents.CHORUS_FLOWER_DEATH, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1032: {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(SoundEvents.PORTAL_TRAVEL, randomSource.nextFloat() * 0.4f + 0.8f, 0.25f));
                break;
            }
            case 1001: {
                this.level.playLocalSound(blockPos, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 1000: {
                this.level.playLocalSound(blockPos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1049: {
                this.level.playLocalSound(blockPos, SoundEvents.CRAFTER_CRAFT, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1050: {
                this.level.playLocalSound(blockPos, SoundEvents.CRAFTER_FAIL, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1004: {
                this.level.playLocalSound(blockPos, SoundEvents.FIREWORK_ROCKET_SHOOT, SoundSource.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1002: {
                this.level.playLocalSound(blockPos, SoundEvents.DISPENSER_LAUNCH, SoundSource.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 1051: {
                this.level.playLocalSound(blockPos, SoundEvents.WIND_CHARGE_THROW, SoundSource.BLOCKS, 0.5f, 0.4f / (this.level.getRandom().nextFloat() * 0.4f + 0.8f), false);
                break;
            }
            case 2010: {
                this.shootParticles(j, blockPos, randomSource, ParticleTypes.WHITE_SMOKE);
                break;
            }
            case 2000: {
                this.shootParticles(j, blockPos, randomSource, ParticleTypes.SMOKE);
                break;
            }
            case 2003: {
                double d = (double)blockPos.getX() + 0.5;
                double e = blockPos.getY();
                double f = (double)blockPos.getZ() + 0.5;
                for (int k = 0; k < 8; ++k) {
                    this.level.addParticle((ParticleOptions)new ItemParticleOption(ParticleTypes.ITEM, new ItemStack((ItemLike)Items.ENDER_EYE)), d, e, f, randomSource.nextGaussian() * 0.15, randomSource.nextDouble() * 0.2, randomSource.nextGaussian() * 0.15);
                }
                for (double g = 0.0; g < Math.PI * 2; g += 0.15707963267948966) {
                    this.level.addParticle((ParticleOptions)ParticleTypes.PORTAL, d + Math.cos(g) * 5.0, e - 0.4, f + Math.sin(g) * 5.0, Math.cos(g) * -5.0, 0.0, Math.sin(g) * -5.0);
                    this.level.addParticle((ParticleOptions)ParticleTypes.PORTAL, d + Math.cos(g) * 5.0, e - 0.4, f + Math.sin(g) * 5.0, Math.cos(g) * -7.0, 0.0, Math.sin(g) * -7.0);
                }
                break;
            }
            case 2002: 
            case 2007: {
                Vec3 vec3 = Vec3.atBottomCenterOf((Vec3i)blockPos);
                for (int l = 0; l < 8; ++l) {
                    this.level.addParticle((ParticleOptions)new ItemParticleOption(ParticleTypes.ITEM, new ItemStack((ItemLike)Items.SPLASH_POTION)), vec3.x, vec3.y, vec3.z, randomSource.nextGaussian() * 0.15, randomSource.nextDouble() * 0.2, randomSource.nextGaussian() * 0.15);
                }
                float h = (float)(j >> 16 & 0xFF) / 255.0f;
                float m = (float)(j >> 8 & 0xFF) / 255.0f;
                float n = (float)(j >> 0 & 0xFF) / 255.0f;
                ParticleType particleType = i == 2007 ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;
                for (int o = 0; o < 100; ++o) {
                    double g = randomSource.nextDouble() * 4.0;
                    double p = randomSource.nextDouble() * Math.PI * 2.0;
                    double q = Math.cos(p) * g;
                    double r = 0.01 + randomSource.nextDouble() * 0.5;
                    double s = Math.sin(p) * g;
                    float t = 0.75f + randomSource.nextFloat() * 0.25f;
                    SpellParticleOption spellParticleOption = SpellParticleOption.create((ParticleType)particleType, (float)(h * t), (float)(m * t), (float)(n * t), (float)((float)g));
                    this.level.addParticle((ParticleOptions)spellParticleOption, vec3.x + q * 0.1, vec3.y + 0.3, vec3.z + s * 0.1, q, r, s);
                }
                this.level.playLocalSound(blockPos, SoundEvents.SPLASH_POTION_BREAK, SoundSource.NEUTRAL, 1.0f, randomSource.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2001: {
                BlockState blockState = Block.stateById((int)j);
                if (!blockState.isAir()) {
                    SoundType soundType = blockState.getSoundType();
                    this.level.playLocalSound(blockPos, soundType.getBreakSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0f) / 2.0f, soundType.getPitch() * 0.8f, false);
                }
                this.level.addDestroyBlockEffect(blockPos, blockState);
                break;
            }
            case 3008: {
                BlockState blockState2 = Block.stateById((int)j);
                Block n = blockState2.getBlock();
                if (n instanceof BrushableBlock) {
                    BrushableBlock brushableBlock = (BrushableBlock)n;
                    this.level.playLocalSound(blockPos, brushableBlock.getBrushCompletedSound(), SoundSource.PLAYERS, 1.0f, 1.0f, false);
                }
                this.level.addDestroyBlockEffect(blockPos, blockState2);
                break;
            }
            case 2004: {
                for (int u = 0; u < 20; ++u) {
                    double v = (double)blockPos.getX() + 0.5 + (randomSource.nextDouble() - 0.5) * 2.0;
                    double w = (double)blockPos.getY() + 0.5 + (randomSource.nextDouble() - 0.5) * 2.0;
                    double x = (double)blockPos.getZ() + 0.5 + (randomSource.nextDouble() - 0.5) * 2.0;
                    this.level.addParticle((ParticleOptions)ParticleTypes.SMOKE, v, w, x, 0.0, 0.0, 0.0);
                    this.level.addParticle((ParticleOptions)ParticleTypes.FLAME, v, w, x, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 3011: {
                TrialSpawner.addSpawnParticles((Level)this.level, (BlockPos)blockPos, (RandomSource)randomSource, (SimpleParticleType)TrialSpawner.FlameParticle.decode((int)j).particleType);
                break;
            }
            case 3012: {
                this.level.playLocalSound(blockPos, SoundEvents.TRIAL_SPAWNER_SPAWN_MOB, SoundSource.BLOCKS, 1.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, true);
                TrialSpawner.addSpawnParticles((Level)this.level, (BlockPos)blockPos, (RandomSource)randomSource, (SimpleParticleType)TrialSpawner.FlameParticle.decode((int)j).particleType);
                break;
            }
            case 3021: {
                this.level.playLocalSound(blockPos, SoundEvents.TRIAL_SPAWNER_SPAWN_ITEM, SoundSource.BLOCKS, 1.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, true);
                TrialSpawner.addSpawnParticles((Level)this.level, (BlockPos)blockPos, (RandomSource)randomSource, (SimpleParticleType)TrialSpawner.FlameParticle.decode((int)j).particleType);
                break;
            }
            case 3013: {
                this.level.playLocalSound(blockPos, SoundEvents.TRIAL_SPAWNER_DETECT_PLAYER, SoundSource.BLOCKS, 1.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, true);
                TrialSpawner.addDetectPlayerParticles((Level)this.level, (BlockPos)blockPos, (RandomSource)randomSource, (int)j, (ParticleOptions)ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER);
                break;
            }
            case 3019: {
                this.level.playLocalSound(blockPos, SoundEvents.TRIAL_SPAWNER_DETECT_PLAYER, SoundSource.BLOCKS, 1.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, true);
                TrialSpawner.addDetectPlayerParticles((Level)this.level, (BlockPos)blockPos, (RandomSource)randomSource, (int)j, (ParticleOptions)ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS);
                break;
            }
            case 3020: {
                this.level.playLocalSound(blockPos, SoundEvents.TRIAL_SPAWNER_OMINOUS_ACTIVATE, SoundSource.BLOCKS, j == 0 ? 0.3f : 1.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, true);
                TrialSpawner.addDetectPlayerParticles((Level)this.level, (BlockPos)blockPos, (RandomSource)randomSource, (int)0, (ParticleOptions)ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS);
                TrialSpawner.addBecomeOminousParticles((Level)this.level, (BlockPos)blockPos, (RandomSource)randomSource);
                break;
            }
            case 3014: {
                this.level.playLocalSound(blockPos, SoundEvents.TRIAL_SPAWNER_EJECT_ITEM, SoundSource.BLOCKS, 1.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, true);
                TrialSpawner.addEjectItemParticles((Level)this.level, (BlockPos)blockPos, (RandomSource)randomSource);
                break;
            }
            case 3017: {
                TrialSpawner.addEjectItemParticles((Level)this.level, (BlockPos)blockPos, (RandomSource)randomSource);
                break;
            }
            case 3015: {
                BlockEntity v = this.level.getBlockEntity(blockPos);
                if (!(v instanceof VaultBlockEntity)) break;
                VaultBlockEntity vaultBlockEntity = (VaultBlockEntity)v;
                VaultBlockEntity.Client.emitActivationParticles((Level)this.level, (BlockPos)vaultBlockEntity.getBlockPos(), (BlockState)vaultBlockEntity.getBlockState(), (VaultSharedData)vaultBlockEntity.getSharedData(), (ParticleOptions)(j == 0 ? ParticleTypes.SMALL_FLAME : ParticleTypes.SOUL_FIRE_FLAME));
                this.level.playLocalSound(blockPos, SoundEvents.VAULT_ACTIVATE, SoundSource.BLOCKS, 1.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, true);
                break;
            }
            case 3016: {
                VaultBlockEntity.Client.emitDeactivationParticles((Level)this.level, (BlockPos)blockPos, (ParticleOptions)(j == 0 ? ParticleTypes.SMALL_FLAME : ParticleTypes.SOUL_FIRE_FLAME));
                this.level.playLocalSound(blockPos, SoundEvents.VAULT_DEACTIVATE, SoundSource.BLOCKS, 1.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, true);
                break;
            }
            case 3018: {
                for (int u = 0; u < 10; ++u) {
                    double v = randomSource.nextGaussian() * 0.02;
                    double w = randomSource.nextGaussian() * 0.02;
                    double x = randomSource.nextGaussian() * 0.02;
                    this.level.addParticle((ParticleOptions)ParticleTypes.POOF, (double)blockPos.getX() + randomSource.nextDouble(), (double)blockPos.getY() + randomSource.nextDouble(), (double)blockPos.getZ() + randomSource.nextDouble(), v, w, x);
                }
                this.level.playLocalSound(blockPos, SoundEvents.COBWEB_PLACE, SoundSource.BLOCKS, 1.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, true);
                break;
            }
            case 1505: {
                BoneMealItem.addGrowthParticles((LevelAccessor)this.level, (BlockPos)blockPos, (int)j);
                this.level.playLocalSound(blockPos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 2011: {
                ParticleUtils.spawnParticleInBlock((LevelAccessor)this.level, (BlockPos)blockPos, (int)j, (ParticleOptions)ParticleTypes.HAPPY_VILLAGER);
                break;
            }
            case 2012: {
                ParticleUtils.spawnParticleInBlock((LevelAccessor)this.level, (BlockPos)blockPos, (int)j, (ParticleOptions)ParticleTypes.HAPPY_VILLAGER);
                break;
            }
            case 3009: {
                ParticleUtils.spawnParticlesOnBlockFaces((Level)this.level, (BlockPos)blockPos, (ParticleOptions)ParticleTypes.EGG_CRACK, (IntProvider)UniformInt.of((int)3, (int)6));
                break;
            }
            case 3002: {
                if (j >= 0 && j < Direction.Axis.VALUES.length) {
                    ParticleUtils.spawnParticlesAlongAxis((Direction.Axis)Direction.Axis.VALUES[j], (Level)this.level, (BlockPos)blockPos, (double)0.125, (ParticleOptions)ParticleTypes.ELECTRIC_SPARK, (UniformInt)UniformInt.of((int)10, (int)19));
                    break;
                }
                ParticleUtils.spawnParticlesOnBlockFaces((Level)this.level, (BlockPos)blockPos, (ParticleOptions)ParticleTypes.ELECTRIC_SPARK, (IntProvider)UniformInt.of((int)3, (int)5));
                break;
            }
            case 2013: {
                ParticleUtils.spawnSmashAttackParticles((LevelAccessor)this.level, (BlockPos)blockPos, (int)j);
                break;
            }
            case 3006: {
                int u = j >> 6;
                if (u > 0) {
                    if (randomSource.nextFloat() < 0.3f + (float)u * 0.1f) {
                        float n = 0.15f + 0.02f * (float)u * (float)u * randomSource.nextFloat();
                        float y = 0.4f + 0.3f * (float)u * randomSource.nextFloat();
                        this.level.playLocalSound(blockPos, SoundEvents.SCULK_BLOCK_CHARGE, SoundSource.BLOCKS, n, y, false);
                    }
                    byte b = (byte)(j & 0x3F);
                    UniformInt intProvider = UniformInt.of((int)0, (int)u);
                    float z = 0.005f;
                    Supplier<Vec3> supplier = () -> new Vec3(Mth.nextDouble((RandomSource)randomSource, (double)-0.005f, (double)0.005f), Mth.nextDouble((RandomSource)randomSource, (double)-0.005f, (double)0.005f), Mth.nextDouble((RandomSource)randomSource, (double)-0.005f, (double)0.005f));
                    if (b == 0) {
                        for (Direction direction : Direction.values()) {
                            float aa = direction == Direction.DOWN ? (float)Math.PI : 0.0f;
                            double r = direction.getAxis() == Direction.Axis.Y ? 0.65 : 0.57;
                            ParticleUtils.spawnParticlesOnBlockFace((Level)this.level, (BlockPos)blockPos, (ParticleOptions)new SculkChargeParticleOptions(aa), (IntProvider)intProvider, (Direction)direction, supplier, (double)r);
                        }
                    } else {
                        for (Direction direction2 : MultifaceBlock.unpack((byte)b)) {
                            float ab = direction2 == Direction.UP ? (float)Math.PI : 0.0f;
                            double q = 0.35;
                            ParticleUtils.spawnParticlesOnBlockFace((Level)this.level, (BlockPos)blockPos, (ParticleOptions)new SculkChargeParticleOptions(ab), (IntProvider)intProvider, (Direction)direction2, supplier, (double)0.35);
                        }
                    }
                } else {
                    this.level.playLocalSound(blockPos, SoundEvents.SCULK_BLOCK_CHARGE, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                    boolean bl = this.level.getBlockState(blockPos).isCollisionShapeFullBlock((BlockGetter)this.level, blockPos);
                    int ac = bl ? 40 : 20;
                    float z = bl ? 0.45f : 0.25f;
                    float ad = 0.07f;
                    for (int ae = 0; ae < ac; ++ae) {
                        float af = 2.0f * randomSource.nextFloat() - 1.0f;
                        float ab = 2.0f * randomSource.nextFloat() - 1.0f;
                        float ag = 2.0f * randomSource.nextFloat() - 1.0f;
                        this.level.addParticle((ParticleOptions)ParticleTypes.SCULK_CHARGE_POP, (double)blockPos.getX() + 0.5 + (double)(af * z), (double)blockPos.getY() + 0.5 + (double)(ab * z), (double)blockPos.getZ() + 0.5 + (double)(ag * z), af * 0.07f, ab * 0.07f, ag * 0.07f);
                    }
                }
                break;
            }
            case 3007: {
                boolean bl2;
                for (int ah = 0; ah < 10; ++ah) {
                    this.level.addParticle((ParticleOptions)new ShriekParticleOption(ah * 5), (double)blockPos.getX() + 0.5, (double)blockPos.getY() + SculkShriekerBlock.TOP_Y, (double)blockPos.getZ() + 0.5, 0.0, 0.0, 0.0);
                }
                BlockState blockState3 = this.level.getBlockState(blockPos);
                boolean bl = bl2 = blockState3.hasProperty((Property)BlockStateProperties.WATERLOGGED) && (Boolean)blockState3.getValue((Property)BlockStateProperties.WATERLOGGED) != false;
                if (bl2) break;
                this.level.playLocalSound((double)blockPos.getX() + 0.5, (double)blockPos.getY() + SculkShriekerBlock.TOP_Y, (double)blockPos.getZ() + 0.5, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.BLOCKS, 2.0f, 0.6f + this.level.random.nextFloat() * 0.4f, false);
                break;
            }
            case 3003: {
                ParticleUtils.spawnParticlesOnBlockFaces((Level)this.level, (BlockPos)blockPos, (ParticleOptions)ParticleTypes.WAX_ON, (IntProvider)UniformInt.of((int)3, (int)5));
                this.level.playLocalSound(blockPos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 3004: {
                ParticleUtils.spawnParticlesOnBlockFaces((Level)this.level, (BlockPos)blockPos, (ParticleOptions)ParticleTypes.WAX_OFF, (IntProvider)UniformInt.of((int)3, (int)5));
                break;
            }
            case 3005: {
                ParticleUtils.spawnParticlesOnBlockFaces((Level)this.level, (BlockPos)blockPos, (ParticleOptions)ParticleTypes.SCRAPE, (IntProvider)UniformInt.of((int)3, (int)5));
                break;
            }
            case 2008: {
                this.level.addParticle((ParticleOptions)ParticleTypes.EXPLOSION, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 0.0, 0.0, 0.0);
                break;
            }
            case 1500: {
                ComposterBlock.handleFill((Level)this.level, (BlockPos)blockPos, (j > 0 ? 1 : 0) != 0);
                break;
            }
            case 1504: {
                PointedDripstoneBlock.spawnDripParticle((Level)this.level, (BlockPos)blockPos, (BlockState)this.level.getBlockState(blockPos));
                break;
            }
            case 1501: {
                this.level.playLocalSound(blockPos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (randomSource.nextFloat() - randomSource.nextFloat()) * 0.8f, false);
                for (int o = 0; o < 8; ++o) {
                    this.level.addParticle((ParticleOptions)ParticleTypes.LARGE_SMOKE, (double)blockPos.getX() + randomSource.nextDouble(), (double)blockPos.getY() + 1.2, (double)blockPos.getZ() + randomSource.nextDouble(), 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1502: {
                this.level.playLocalSound(blockPos, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.5f, 2.6f + (randomSource.nextFloat() - randomSource.nextFloat()) * 0.8f, false);
                for (int o = 0; o < 5; ++o) {
                    double g = (double)blockPos.getX() + randomSource.nextDouble() * 0.6 + 0.2;
                    double p = (double)blockPos.getY() + randomSource.nextDouble() * 0.6 + 0.2;
                    double q = (double)blockPos.getZ() + randomSource.nextDouble() * 0.6 + 0.2;
                    this.level.addParticle((ParticleOptions)ParticleTypes.SMOKE, g, p, q, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1503: {
                this.level.playLocalSound(blockPos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                for (int o = 0; o < 16; ++o) {
                    double g = (double)blockPos.getX() + (5.0 + randomSource.nextDouble() * 6.0) / 16.0;
                    double p = (double)blockPos.getY() + 0.8125;
                    double q = (double)blockPos.getZ() + (5.0 + randomSource.nextDouble() * 6.0) / 16.0;
                    this.level.addParticle((ParticleOptions)ParticleTypes.SMOKE, g, p, q, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 2006: {
                for (int o = 0; o < 200; ++o) {
                    float ad = randomSource.nextFloat() * 4.0f;
                    float ai = randomSource.nextFloat() * ((float)Math.PI * 2);
                    double p = Mth.cos((double)ai) * ad;
                    double q = 0.01 + randomSource.nextDouble() * 0.5;
                    double r = Mth.sin((double)ai) * ad;
                    this.level.addParticle((ParticleOptions)PowerParticleOption.create((ParticleType)ParticleTypes.DRAGON_BREATH, (float)ad), (double)blockPos.getX() + p * 0.1, (double)blockPos.getY() + 0.3, (double)blockPos.getZ() + r * 0.1, p, q, r);
                }
                if (j != 1) break;
                this.level.playLocalSound(blockPos, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 1.0f, randomSource.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2009: {
                for (int o = 0; o < 8; ++o) {
                    this.level.addParticle((ParticleOptions)ParticleTypes.CLOUD, (double)blockPos.getX() + randomSource.nextDouble(), (double)blockPos.getY() + 1.2, (double)blockPos.getZ() + randomSource.nextDouble(), 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1009: {
                if (j == 0) {
                    this.level.playLocalSound(blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (randomSource.nextFloat() - randomSource.nextFloat()) * 0.8f, false);
                    break;
                }
                if (j != 1) break;
                this.level.playLocalSound(blockPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.7f, 1.6f + (randomSource.nextFloat() - randomSource.nextFloat()) * 0.4f, false);
                break;
            }
            case 1029: {
                this.level.playLocalSound(blockPos, SoundEvents.ANVIL_DESTROY, SoundSource.BLOCKS, 1.0f, randomSource.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1030: {
                this.level.playLocalSound(blockPos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, randomSource.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1044: {
                this.level.playLocalSound(blockPos, SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1031: {
                this.level.playLocalSound(blockPos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.3f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1039: {
                this.level.playLocalSound(blockPos, SoundEvents.PHANTOM_BITE, SoundSource.HOSTILE, 0.3f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1010: {
                this.level.registryAccess().lookupOrThrow(Registries.JUKEBOX_SONG).get(j).ifPresent(reference -> this.playJukeboxSong((Holder<JukeboxSong>)reference, blockPos));
                break;
            }
            case 1011: {
                this.stopJukeboxSongAndNotifyNearby(blockPos);
                break;
            }
            case 1015: {
                this.level.playLocalSound(blockPos, SoundEvents.GHAST_WARN, SoundSource.HOSTILE, 10.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1017: {
                this.level.playLocalSound(blockPos, SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.HOSTILE, 10.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1016: {
                this.level.playLocalSound(blockPos, SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 10.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1019: {
                this.level.playLocalSound(blockPos, SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1022: {
                this.level.playLocalSound(blockPos, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.HOSTILE, 2.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1021: {
                this.level.playLocalSound(blockPos, SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1020: {
                this.level.playLocalSound(blockPos, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.HOSTILE, 2.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1018: {
                this.level.playLocalSound(blockPos, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 2.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1024: {
                this.level.playLocalSound(blockPos, SoundEvents.WITHER_SHOOT, SoundSource.HOSTILE, 2.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1026: {
                this.level.playLocalSound(blockPos, SoundEvents.ZOMBIE_INFECT, SoundSource.HOSTILE, 2.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1027: {
                this.level.playLocalSound(blockPos, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.HOSTILE, 2.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1040: {
                this.level.playLocalSound(blockPos, SoundEvents.ZOMBIE_CONVERTED_TO_DROWNED, SoundSource.HOSTILE, 2.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1041: {
                this.level.playLocalSound(blockPos, SoundEvents.HUSK_CONVERTED_TO_ZOMBIE, SoundSource.HOSTILE, 2.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1025: {
                this.level.playLocalSound(blockPos, SoundEvents.BAT_TAKEOFF, SoundSource.NEUTRAL, 0.05f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1042: {
                this.level.playLocalSound(blockPos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1043: {
                this.level.playLocalSound(blockPos, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 3000: {
                this.level.addAlwaysVisibleParticle((ParticleOptions)ParticleTypes.EXPLOSION_EMITTER, true, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 0.0, 0.0, 0.0);
                this.level.playLocalSound(blockPos, SoundEvents.END_GATEWAY_SPAWN, SoundSource.BLOCKS, 10.0f, (1.0f + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2f) * 0.7f, false);
                break;
            }
            case 3001: {
                this.level.playLocalSound(blockPos, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 64.0f, 0.8f + this.level.random.nextFloat() * 0.3f, false);
                break;
            }
            case 1045: {
                this.level.playLocalSound(blockPos, SoundEvents.POINTED_DRIPSTONE_LAND, SoundSource.BLOCKS, 2.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1046: {
                this.level.playLocalSound(blockPos, SoundEvents.POINTED_DRIPSTONE_DRIP_LAVA_INTO_CAULDRON, SoundSource.BLOCKS, 2.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1047: {
                this.level.playLocalSound(blockPos, SoundEvents.POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON, SoundSource.BLOCKS, 2.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1048: {
                this.level.playLocalSound(blockPos, SoundEvents.SKELETON_CONVERTED_TO_STRAY, SoundSource.HOSTILE, 2.0f, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2f + 1.0f, false);
            }
        }
    }

    private void shootParticles(int i, BlockPos blockPos, RandomSource randomSource, SimpleParticleType simpleParticleType) {
        Direction direction = Direction.from3DDataValue((int)i);
        int j = direction.getStepX();
        int k = direction.getStepY();
        int l = direction.getStepZ();
        for (int m = 0; m < 10; ++m) {
            double d = randomSource.nextDouble() * 0.2 + 0.01;
            double e = (double)blockPos.getX() + (double)j * 0.6 + 0.5 + (double)j * 0.01 + (randomSource.nextDouble() - 0.5) * (double)l * 0.5;
            double f = (double)blockPos.getY() + (double)k * 0.6 + 0.5 + (double)k * 0.01 + (randomSource.nextDouble() - 0.5) * (double)k * 0.5;
            double g = (double)blockPos.getZ() + (double)l * 0.6 + 0.5 + (double)l * 0.01 + (randomSource.nextDouble() - 0.5) * (double)j * 0.5;
            double h = (double)j * d + randomSource.nextGaussian() * 0.01;
            double n = (double)k * d + randomSource.nextGaussian() * 0.01;
            double o = (double)l * d + randomSource.nextGaussian() * 0.01;
            this.level.addParticle((ParticleOptions)simpleParticleType, e, f, g, h, n, o);
        }
    }

    private void playJukeboxSong(Holder<JukeboxSong> holder, BlockPos blockPos) {
        this.stopJukeboxSong(blockPos);
        JukeboxSong jukeboxSong = (JukeboxSong)holder.value();
        SoundEvent soundEvent = (SoundEvent)jukeboxSong.soundEvent().value();
        SimpleSoundInstance soundInstance = SimpleSoundInstance.forJukeboxSong(soundEvent, Vec3.atCenterOf((Vec3i)blockPos));
        this.playingJukeboxSongs.put(blockPos, soundInstance);
        this.minecraft.getSoundManager().play(soundInstance);
        this.minecraft.gui.setNowPlaying(jukeboxSong.description());
        this.notifyNearbyEntities(this.level, blockPos, true);
    }

    private void stopJukeboxSong(BlockPos blockPos) {
        SoundInstance soundInstance = this.playingJukeboxSongs.remove(blockPos);
        if (soundInstance != null) {
            this.minecraft.getSoundManager().stop(soundInstance);
        }
    }

    private void stopJukeboxSongAndNotifyNearby(BlockPos blockPos) {
        this.stopJukeboxSong(blockPos);
        this.notifyNearbyEntities(this.level, blockPos, false);
    }

    private void notifyNearbyEntities(Level level, BlockPos blockPos, boolean bl) {
        List list = level.getEntitiesOfClass(LivingEntity.class, new AABB(blockPos).inflate(3.0));
        for (LivingEntity livingEntity : list) {
            livingEntity.setRecordPlayingNearby(blockPos, bl);
        }
    }
}

