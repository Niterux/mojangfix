/*
 * Copyright (C) 2022 js6pak
 *
 * This file is part of MojangFix.
 *
 * MojangFix is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, version 3.
 *
 * MojangFix is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with MojangFix. If not, see <https://www.gnu.org/licenses/>.
 */

package pl.js6pak.mojangfix.client.gui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import com.mojang.blaze3d.vertex.BufferBuilder;
import pl.js6pak.mojangfix.mixinterface.KeyBindingAccessor;

import java.util.HashMap;
import java.util.Map;

public class ControlsListWidget extends ListWidget {
    private final Minecraft minecraft;
    private final GameOptions options;

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class KeyBindingEntry {
        private final ButtonWidget editButton;
        private final ButtonWidget resetButton;
    }

    @Getter
    private final Map<KeyBinding, KeyBindingEntry> buttons = new HashMap<>();

    public ControlsListWidget(ControlsOptionsScreen parent, Minecraft minecraft, GameOptions options) {
        super(minecraft, parent.width, parent.height, 36, parent.height - 36, 20);
        this.minecraft = minecraft;
        this.options = options;
    }

    @Override
    protected int size() {
        return options.keyBindings.length;
    }

    @Override
    protected void entryClicked(int i, boolean bl) {
    }

    @Override
    protected boolean isEntrySelected(int i) {
        return false;
    }

    @Override
    protected void renderBackground() {
    }

    private int mouseX;
    private int mouseY;

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        super.render(mouseX, mouseY, delta);
    }

    @Override
    protected void renderEntry(int index, int x, int y, int l, BufferBuilder tessellator) {
        KeyBinding keyBinding = options.keyBindings[index];
        KeyBindingEntry entry = buttons.get(keyBinding);

        minecraft.textRenderer.drawWithShadow(options.getKeyBindingName(index), x, y + 5, -1);

        ButtonWidget editButton = entry.getEditButton();
        editButton.x = x + 100;
        editButton.y = y;
        editButton.render(minecraft, mouseX, mouseY);

        ButtonWidget resetButton = entry.getResetButton();
        resetButton.x = editButton.x + 75;
        resetButton.y = editButton.y;
        resetButton.active = ((KeyBindingAccessor) keyBinding).getDefaultKeyCode() != keyBinding.keyCode;
        resetButton.render(minecraft, mouseX, mouseY);
    }
}
