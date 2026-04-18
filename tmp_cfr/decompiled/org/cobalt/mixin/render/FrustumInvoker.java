/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_4604
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package org.cobalt.mixin.render;

import net.minecraft.class_4604;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={class_4604.class})
public interface FrustumInvoker {
    @Invoker(value="method_23089")
    public int invokeCubeInFrustum(double var1, double var3, double var5, double var7, double var9, double var11);
}

