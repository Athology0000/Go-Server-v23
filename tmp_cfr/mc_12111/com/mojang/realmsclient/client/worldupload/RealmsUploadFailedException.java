/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.network.chat.Component
 */
package com.mojang.realmsclient.client.worldupload;

import com.mojang.realmsclient.client.worldupload.RealmsUploadException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;

@Environment(value=EnvType.CLIENT)
public class RealmsUploadFailedException
extends RealmsUploadException {
    private final Component errorMessage;

    public RealmsUploadFailedException(Component component) {
        this.errorMessage = component;
    }

    public RealmsUploadFailedException(String string) {
        this((Component)Component.literal((String)string));
    }

    @Override
    public Component getStatusMessage() {
        return Component.translatable((String)"mco.upload.failed", (Object[])new Object[]{this.errorMessage});
    }
}

