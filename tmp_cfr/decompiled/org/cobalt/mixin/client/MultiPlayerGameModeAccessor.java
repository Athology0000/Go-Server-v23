/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1799
 *  net.minecraft.class_636
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package org.cobalt.mixin.client;

import net.minecraft.class_1799;
import net.minecraft.class_636;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={class_636.class})
public interface MultiPlayerGameModeAccessor {
    @Accessor(value="field_3718")
    public void setDestroyingItemCobalt(class_1799 var1);
}

