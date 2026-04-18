/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.Identifier
 */
package net.minecraft.client.gui.components;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

@Environment(value=EnvType.CLIENT)
public class CommonButtons {
    public static SpriteIconButton language(int i, Button.OnPress onPress, boolean bl) {
        return SpriteIconButton.builder((Component)Component.translatable((String)"options.language"), onPress, bl).width(i).sprite(Identifier.withDefaultNamespace((String)"icon/language"), 15, 15).build();
    }

    public static SpriteIconButton accessibility(int i, Button.OnPress onPress, boolean bl) {
        MutableComponent component = bl ? Component.translatable((String)"options.accessibility") : Component.translatable((String)"accessibility.onboarding.accessibility.button");
        return SpriteIconButton.builder((Component)component, onPress, bl).width(i).sprite(Identifier.withDefaultNamespace((String)"icon/accessibility"), 15, 15).build();
    }
}

