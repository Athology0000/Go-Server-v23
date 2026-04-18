/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.ComponentUtils
 *  net.minecraft.network.chat.Style
 *  net.minecraft.util.Mth
 */
package net.minecraft.client.gui.components;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;

@Environment(value=EnvType.CLIENT)
public class PlainTextButton
extends Button {
    private final Font font;
    private final Component message;
    private final Component underlinedMessage;

    public PlainTextButton(int i, int j, int k, int l, Component component, Button.OnPress onPress, Font font) {
        super(i, j, k, l, component, onPress, DEFAULT_NARRATION);
        this.font = font;
        this.message = component;
        this.underlinedMessage = ComponentUtils.mergeStyles((Component)component, (Style)Style.EMPTY.withUnderlined(Boolean.valueOf(true)));
    }

    @Override
    public void renderContents(GuiGraphics guiGraphics, int i, int j, float f) {
        Component component = this.isHoveredOrFocused() ? this.underlinedMessage : this.message;
        guiGraphics.drawString(this.font, component, this.getX(), this.getY(), 0xFFFFFF | Mth.ceil((float)(this.alpha * 255.0f)) << 24);
    }
}

