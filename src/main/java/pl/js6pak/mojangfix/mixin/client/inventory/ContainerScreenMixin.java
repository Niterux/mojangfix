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

package pl.js6pak.mojangfix.mixin.client.inventory;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.menu.InventoryMenuScreen;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(InventoryMenuScreen.class)
public abstract class ContainerScreenMixin extends Screen {
    @Shadow
    protected abstract InventorySlot getHoveredSlot(int x, int y);

    @Shadow
    public InventoryMenu menu;

    @Shadow
    protected abstract boolean isMouseOverSlot(InventorySlot slot, int x, int Y);

    @Unique
    private InventorySlot slot;

    @Unique
    private final Set<InventorySlot> hoveredSlots = new HashSet<>();

    @Inject(method = "mouseReleased", at = @At("RETURN"))
    private void onMouseReleased(int mouseX, int mouseY, int button, CallbackInfo ci) {
        slot = this.getHoveredSlot(mouseX, mouseY);

        if (slot == null)
            return;

        ItemStack cursorStack = minecraft.player.inventory.getCursorStack();
        if (button == -1 && Mouse.isButtonDown(1) && cursorStack != null) {
            if (!hoveredSlots.contains(slot)) {
                if (slot.hasStack() && !slot.getStack().matchesItem(cursorStack)) {
                    return;
                }

                hoveredSlots.add(slot);
                if (hoveredSlots.size() > 1) {
                    this.minecraft.interactionManager.clickSlot(this.menu.networkId, slot.id, 1, false, this.minecraft.player);
                }
            }
        } else {
            hoveredSlots.clear();
        }
    }

    @Unique
    private boolean drawingHoveredSlot;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/inventory/menu/InventoryMenuScreen;isMouseOverSlot(Lnet/minecraft/inventory/slot/InventorySlot;II)Z"))
    private boolean redirectIsPointOverSlot(InventoryMenuScreen guiContainer, InventorySlot slot, int x, int y) {
        return (drawingHoveredSlot = hoveredSlots.contains(slot)) || isMouseOverSlot(slot, x, y);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/inventory/menu/InventoryMenuScreen;fillGradient(IIIIII)V", ordinal = 0))
    private void redirectFillGradient(InventoryMenuScreen instance, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
        if (colorStart != colorEnd) throw new AssertionError();
        int color = drawingHoveredSlot ? 0x20ffffff : colorStart;
        this.fillGradient(startX, startY, endX, endY, color, color);
    }

    @Inject(method = "keyPressed", at = @At("RETURN"))
    private void onKeyPressed(char character, int keyCode, CallbackInfo ci) {
        if (this.slot == null)
            return;

        if (keyCode == this.minecraft.options.dropKey.keyCode) {
            if (this.minecraft.player.inventory.getCursorStack() != null)
                return;

            this.minecraft.interactionManager.clickSlot(this.menu.networkId, slot.id, 0, false, this.minecraft.player);
            this.minecraft.interactionManager.clickSlot(this.menu.networkId, -999, Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) ? 0 : 1, false, this.minecraft.player);
            this.minecraft.interactionManager.clickSlot(this.menu.networkId, slot.id, 0, false, this.minecraft.player);
        }

        if (keyCode >= Keyboard.KEY_1 && keyCode <= Keyboard.KEY_9) {
            if (this.minecraft.player.inventory.getCursorStack() == null)
                this.minecraft.interactionManager.clickSlot(this.menu.networkId, slot.id, 0, false, this.minecraft.player);
            this.minecraft.interactionManager.clickSlot(this.menu.networkId, 35 + keyCode - 1, 0, false, this.minecraft.player);
            this.minecraft.interactionManager.clickSlot(this.menu.networkId, slot.id, 0, false, this.minecraft.player);
        }
    }
}
