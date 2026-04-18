/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_2371
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package org.cobalt.mixin.client;

import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_2371;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={class_1661.class})
public interface GardenInventoryAccessor {
    @Accessor(value="field_7547")
    public class_2371<class_1799> getItems();
}

