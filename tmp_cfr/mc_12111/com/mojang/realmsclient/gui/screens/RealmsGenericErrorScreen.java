/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.ComponentUtils
 *  net.minecraft.network.chat.Style
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsError;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.realms.RealmsScreen;

@Environment(value=EnvType.CLIENT)
public class RealmsGenericErrorScreen
extends RealmsScreen {
    private static final Component GENERIC_TITLE = Component.translatable((String)"mco.errorMessage.generic");
    private final Screen nextScreen;
    private final Component detail;
    private MultiLineLabel splitDetail = MultiLineLabel.EMPTY;

    public RealmsGenericErrorScreen(RealmsServiceException realmsServiceException, Screen screen) {
        this(ErrorMessage.forServiceError(realmsServiceException), screen);
    }

    public RealmsGenericErrorScreen(Component component, Screen screen) {
        this(new ErrorMessage(GENERIC_TITLE, component), screen);
    }

    public RealmsGenericErrorScreen(Component component, Component component2, Screen screen) {
        this(new ErrorMessage(component, component2), screen);
    }

    private RealmsGenericErrorScreen(ErrorMessage errorMessage, Screen screen) {
        super(errorMessage.title);
        this.nextScreen = screen;
        this.detail = ComponentUtils.mergeStyles((Component)errorMessage.detail, (Style)Style.EMPTY.withColor(-2142128));
    }

    @Override
    public void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_OK, button -> this.onClose()).bounds(this.width / 2 - 100, this.height - 52, 200, 20).build());
        this.splitDetail = MultiLineLabel.create(this.font, this.detail, this.width * 3 / 4);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.nextScreen);
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration((Component[])new Component[]{super.getNarrationMessage(), this.detail});
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 80, -1);
        ActiveTextCollector activeTextCollector = guiGraphics.textRenderer();
        this.splitDetail.visitLines(TextAlignment.CENTER, this.width / 2, 100, this.minecraft.font.lineHeight, activeTextCollector);
    }

    @Environment(value=EnvType.CLIENT)
    static final class ErrorMessage
    extends Record {
        final Component title;
        final Component detail;

        ErrorMessage(Component component, Component component2) {
            this.title = component;
            this.detail = component2;
        }

        static ErrorMessage forServiceError(RealmsServiceException realmsServiceException) {
            RealmsError realmsError = realmsServiceException.realmsError;
            return new ErrorMessage((Component)Component.translatable((String)"mco.errorMessage.realmsService.realmsError", (Object[])new Object[]{realmsError.errorCode()}), realmsError.errorMessage());
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ErrorMessage.class, "title;detail", "title", "detail"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ErrorMessage.class, "title;detail", "title", "detail"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ErrorMessage.class, "title;detail", "title", "detail"}, this, object);
        }

        public Component title() {
            return this.title;
        }

        public Component detail() {
            return this.detail;
        }
    }
}

