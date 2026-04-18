/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.UserApiService
 *  com.mojang.authlib.minecraft.UserApiService$UserProperties
 *  com.mojang.authlib.yggdrasil.ProfileResult
 *  net.minecraft.class_310
 *  net.minecraft.class_320
 *  net.minecraft.class_7497
 *  net.minecraft.class_7574
 *  net.minecraft.class_7853
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package org.cobalt.mixin.client;

import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_310;
import net.minecraft.class_320;
import net.minecraft.class_7497;
import net.minecraft.class_7574;
import net.minecraft.class_7853;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={class_310.class})
public interface MinecraftAccessor {
    @Invoker(value="method_1536")
    public boolean leftClick();

    @Invoker(value="method_1583")
    public void rightClick();

    @Mutable
    @Accessor(value="field_1726")
    public void setUser(class_320 var1);

    @Mutable
    @Accessor(value="field_45899")
    public void setProfileFuture(CompletableFuture<ProfileResult> var1);

    @Mutable
    @Accessor(value="field_26902")
    public void setUserApiService(UserApiService var1);

    @Mutable
    @Accessor(value="field_47680")
    public void setUserPropertiesFuture(CompletableFuture<UserApiService.UserProperties> var1);

    @Mutable
    @Accessor(value="field_39068")
    public void setProfileKeyPairManager(class_7853 var1);

    @Mutable
    @Accessor(value="field_62106")
    public void setServices(class_7497 var1);

    @Accessor(value="field_39492")
    public void setReportingContext(class_7574 var1);
}

