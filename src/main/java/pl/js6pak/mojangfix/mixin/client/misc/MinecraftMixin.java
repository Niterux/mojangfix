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

package pl.js6pak.mojangfix.mixin.client.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.options.GameOptions;
import org.lwjgl.input.Keyboard;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.js6pak.mojangfix.mixinterface.GameSettingsAccessor;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    public GameOptions options;

	@Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;debugProfilerEnabled:Z", ordinal = 0, shift = At.Shift.BEFORE))
    private void onF3(CallbackInfo ci) {
        GameSettingsAccessor gameSettings = (GameSettingsAccessor) this.options;
        gameSettings.setShowDebugInfoGraph(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL));
    }

    @Redirect(method = "run", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;debugProfilerEnabled:Z", opcode = Opcodes.GETFIELD))
    private boolean getShowDebugInfo(GameOptions gameSettings) {
        return gameSettings.debugProfilerEnabled && ((GameSettingsAccessor) this.options).isShowDebugInfoGraph();
    }

    @Inject(method = "initTimerHackThread", at = @At("HEAD"), cancellable = true)
    private void disableSessionCheck(CallbackInfo ci) {
        ci.cancel();
    }
}
