/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.resources.Identifier
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.system.MemoryUtil
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.util;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsTextureManager {
    private static final Map<String, RealmsTexture> TEXTURES = Maps.newHashMap();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Identifier TEMPLATE_ICON_LOCATION = Identifier.withDefaultNamespace((String)"textures/gui/presets/isles.png");

    public static Identifier worldTemplate(String string, @Nullable String string2) {
        if (string2 == null) {
            return TEMPLATE_ICON_LOCATION;
        }
        return RealmsTextureManager.getTexture(string, string2);
    }

    private static Identifier getTexture(String string, String string2) {
        RealmsTexture realmsTexture = TEXTURES.get(string);
        if (realmsTexture != null && realmsTexture.image().equals(string2)) {
            return realmsTexture.textureId;
        }
        NativeImage nativeImage = RealmsTextureManager.loadImage(string2);
        if (nativeImage == null) {
            Identifier identifier = MissingTextureAtlasSprite.getLocation();
            TEXTURES.put(string, new RealmsTexture(string2, identifier));
            return identifier;
        }
        Identifier identifier = Identifier.fromNamespaceAndPath((String)"realms", (String)("dynamic/" + string));
        Minecraft.getInstance().getTextureManager().register(identifier, new DynamicTexture(() -> ((Identifier)identifier).toString(), nativeImage));
        TEXTURES.put(string, new RealmsTexture(string2, identifier));
        return identifier;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static @Nullable NativeImage loadImage(String string) {
        byte[] bs = Base64.getDecoder().decode(string);
        ByteBuffer byteBuffer = MemoryUtil.memAlloc((int)bs.length);
        try {
            NativeImage nativeImage = NativeImage.read(byteBuffer.put(bs).flip());
            return nativeImage;
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to load world image: {}", (Object)string, (Object)iOException);
        }
        finally {
            MemoryUtil.memFree((Buffer)byteBuffer);
        }
        return null;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class RealmsTexture
    extends Record {
        private final String image;
        final Identifier textureId;

        public RealmsTexture(String string, Identifier identifier) {
            this.image = string;
            this.textureId = identifier;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{RealmsTexture.class, "image;textureId", "image", "textureId"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RealmsTexture.class, "image;textureId", "image", "textureId"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RealmsTexture.class, "image;textureId", "image", "textureId"}, this, object);
        }

        public String image() {
            return this.image;
        }

        public Identifier textureId() {
            return this.textureId;
        }
    }
}

