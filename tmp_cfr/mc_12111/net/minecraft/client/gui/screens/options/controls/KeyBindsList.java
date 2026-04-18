/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.client.gui.screens.options.controls;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.ArrayUtils;

@Environment(value=EnvType.CLIENT)
public class KeyBindsList
extends ContainerObjectSelectionList<Entry> {
    private static final int ITEM_HEIGHT = 20;
    final KeyBindsScreen keyBindsScreen;
    private int maxNameWidth;

    public KeyBindsList(KeyBindsScreen keyBindsScreen, Minecraft minecraft) {
        super(minecraft, keyBindsScreen.width, keyBindsScreen.layout.getContentHeight(), keyBindsScreen.layout.getHeaderHeight(), 20);
        this.keyBindsScreen = keyBindsScreen;
        Object[] keyMappings = (KeyMapping[])ArrayUtils.clone((Object[])minecraft.options.keyMappings);
        Arrays.sort(keyMappings);
        KeyMapping.Category category = null;
        for (Object keyMapping : keyMappings) {
            MutableComponent component;
            int i;
            KeyMapping.Category category2 = ((KeyMapping)keyMapping).getCategory();
            if (category2 != category) {
                category = category2;
                this.addEntry(new CategoryEntry(category2));
            }
            if ((i = minecraft.font.width((FormattedText)(component = Component.translatable((String)((KeyMapping)keyMapping).getName())))) > this.maxNameWidth) {
                this.maxNameWidth = i;
            }
            this.addEntry(new KeyEntry((KeyMapping)keyMapping, (Component)component));
        }
    }

    public void resetMappingAndUpdateButtons() {
        KeyMapping.resetMapping();
        this.refreshEntries();
    }

    public void refreshEntries() {
        this.children().forEach(Entry::refreshEntry);
    }

    @Override
    public int getRowWidth() {
        return 340;
    }

    @Environment(value=EnvType.CLIENT)
    public class CategoryEntry
    extends Entry {
        private final FocusableTextWidget categoryName;

        public CategoryEntry(KeyMapping.Category category) {
            this.categoryName = FocusableTextWidget.builder(category.label(), ((KeyBindsList)KeyBindsList.this).minecraft.font).alwaysShowBorder(false).backgroundFill(FocusableTextWidget.BackgroundFill.ON_FOCUS).build();
        }

        @Override
        public void renderContent(GuiGraphics guiGraphics, int i, int j, boolean bl, float f) {
            this.categoryName.setPosition(KeyBindsList.this.width / 2 - this.categoryName.getWidth() / 2, this.getContentBottom() - this.categoryName.getHeight());
            this.categoryName.render(guiGraphics, i, j, f);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(this.categoryName);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(this.categoryName);
        }

        @Override
        protected void refreshEntry() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class KeyEntry
    extends Entry {
        private static final Component RESET_BUTTON_TITLE = Component.translatable((String)"controls.reset");
        private static final int PADDING = 10;
        private final KeyMapping key;
        private final Component name;
        private final Button changeButton;
        private final Button resetButton;
        private boolean hasCollision = false;

        KeyEntry(KeyMapping keyMapping, Component component) {
            this.key = keyMapping;
            this.name = component;
            this.changeButton = Button.builder(component, button -> {
                KeyBindsList.this.keyBindsScreen.selectedKey = keyMapping;
                KeyBindsList.this.resetMappingAndUpdateButtons();
            }).bounds(0, 0, 75, 20).createNarration(supplier -> {
                if (keyMapping.isUnbound()) {
                    return Component.translatable((String)"narrator.controls.unbound", (Object[])new Object[]{component});
                }
                return Component.translatable((String)"narrator.controls.bound", (Object[])new Object[]{component, supplier.get()});
            }).build();
            this.resetButton = Button.builder(RESET_BUTTON_TITLE, button -> {
                keyMapping.setKey(keyMapping.getDefaultKey());
                KeyBindsList.this.resetMappingAndUpdateButtons();
            }).bounds(0, 0, 50, 20).createNarration(supplier -> Component.translatable((String)"narrator.controls.reset", (Object[])new Object[]{component})).build();
            this.refreshEntry();
        }

        @Override
        public void renderContent(GuiGraphics guiGraphics, int i, int j, boolean bl, float f) {
            int k = KeyBindsList.this.scrollBarX() - this.resetButton.getWidth() - 10;
            int l = this.getContentY() - 2;
            this.resetButton.setPosition(k, l);
            this.resetButton.render(guiGraphics, i, j, f);
            int m = k - 5 - this.changeButton.getWidth();
            this.changeButton.setPosition(m, l);
            this.changeButton.render(guiGraphics, i, j, f);
            guiGraphics.drawString(((KeyBindsList)KeyBindsList.this).minecraft.font, this.name, this.getContentX(), this.getContentYMiddle() - ((KeyBindsList)KeyBindsList.this).minecraft.font.lineHeight / 2, -1);
            if (this.hasCollision) {
                int n = 3;
                int o = this.changeButton.getX() - 6;
                guiGraphics.fill(o, this.getContentY() - 1, o + 3, this.getContentBottom(), -256);
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of((Object)this.changeButton, (Object)this.resetButton);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of((Object)this.changeButton, (Object)this.resetButton);
        }

        @Override
        protected void refreshEntry() {
            this.changeButton.setMessage(this.key.getTranslatedKeyMessage());
            this.resetButton.active = !this.key.isDefault();
            this.hasCollision = false;
            MutableComponent mutableComponent = Component.empty();
            if (!this.key.isUnbound()) {
                for (KeyMapping keyMapping : ((KeyBindsList)KeyBindsList.this).minecraft.options.keyMappings) {
                    if (keyMapping == this.key || !this.key.same(keyMapping) || keyMapping.isDefault() && this.key.isDefault()) continue;
                    if (this.hasCollision) {
                        mutableComponent.append(", ");
                    }
                    this.hasCollision = true;
                    mutableComponent.append((Component)Component.translatable((String)keyMapping.getName()));
                }
            }
            if (this.hasCollision) {
                this.changeButton.setMessage((Component)Component.literal((String)"[ ").append((Component)this.changeButton.getMessage().copy().withStyle(ChatFormatting.WHITE)).append(" ]").withStyle(ChatFormatting.YELLOW));
                this.changeButton.setTooltip(Tooltip.create((Component)Component.translatable((String)"controls.keybinds.duplicateKeybinds", (Object[])new Object[]{mutableComponent})));
            } else {
                this.changeButton.setTooltip(null);
            }
            if (KeyBindsList.this.keyBindsScreen.selectedKey == this.key) {
                this.changeButton.setMessage((Component)Component.literal((String)"> ").append((Component)this.changeButton.getMessage().copy().withStyle(new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.UNDERLINE})).append(" <").withStyle(ChatFormatting.YELLOW));
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class Entry
    extends ContainerObjectSelectionList.Entry<Entry> {
        abstract void refreshEntry();
    }
}

