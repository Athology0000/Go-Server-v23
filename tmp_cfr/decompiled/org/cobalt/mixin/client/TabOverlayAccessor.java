/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_355
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package org.cobalt.mixin.client;

import net.minecraft.class_2561;
import net.minecraft.class_355;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={class_355.class})
public interface TabOverlayAccessor {
    @Accessor(value="field_2153")
    public class_2561 getHeader();

    @Accessor(value="field_2154")
    public class_2561 getFooter();
}

