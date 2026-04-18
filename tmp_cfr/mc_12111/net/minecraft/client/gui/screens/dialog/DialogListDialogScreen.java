/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.core.Holder
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$ShowDialog
 *  net.minecraft.server.dialog.ActionButton
 *  net.minecraft.server.dialog.CommonButtonData
 *  net.minecraft.server.dialog.Dialog
 *  net.minecraft.server.dialog.DialogListDialog
 *  net.minecraft.server.dialog.action.StaticAction
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screens.dialog;

import java.util.Optional;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.dialog.ButtonListDialogScreen;
import net.minecraft.client.gui.screens.dialog.DialogConnectionAccess;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.DialogListDialog;
import net.minecraft.server.dialog.action.StaticAction;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DialogListDialogScreen
extends ButtonListDialogScreen<DialogListDialog> {
    public DialogListDialogScreen(@Nullable Screen screen, DialogListDialog dialogListDialog, DialogConnectionAccess dialogConnectionAccess) {
        super(screen, dialogListDialog, dialogConnectionAccess);
    }

    @Override
    protected Stream<ActionButton> createListActions(DialogListDialog dialogListDialog, DialogConnectionAccess dialogConnectionAccess) {
        return dialogListDialog.dialogs().stream().map(holder -> DialogListDialogScreen.createDialogClickAction(dialogListDialog, (Holder<Dialog>)holder));
    }

    private static ActionButton createDialogClickAction(DialogListDialog dialogListDialog, Holder<Dialog> holder) {
        return new ActionButton(new CommonButtonData(((Dialog)holder.value()).common().computeExternalTitle(), dialogListDialog.buttonWidth()), Optional.of(new StaticAction((ClickEvent)new ClickEvent.ShowDialog(holder))));
    }
}

