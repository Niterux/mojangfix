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

package pl.js6pak.mojangfix.mixin.client.skin;

import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.client.render.texture.HttpImageProcessor;
import net.minecraft.entity.Entity;
import net.minecraft.client.entity.living.player.InputPlayerEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.js6pak.mojangfix.mixinterface.PlayerEntityAccessor;
import pl.js6pak.mojangfix.mixinterface.SkinImageProcessorAccessor;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(method = "onEntityRemoved", at = @At("HEAD"), cancellable = true)
    private void dontUnloadLocalPlayerSkin(Entity entity, CallbackInfo ci) {
        if (entity instanceof InputPlayerEntity) {
            ci.cancel();
        }
    }

    @Unique
    private Entity currentEntity; // I hate this but there is no way to get it from @ModifyArg

    @Inject(method = "onEntityAdded", at = @At("HEAD"))
    private void getEnttity(Entity entity, CallbackInfo ci) {
        currentEntity = entity;
    }

    @ModifyArg(
        method = "onEntityAdded", index = 1,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/texture/TextureManager;getHttpTexture(Ljava/lang/String;Lnet/minecraft/client/render/texture/HttpImageProcessor;)Lnet/minecraft/client/render/texture/HttpTexture;", ordinal = 0)
    )
    private HttpImageProcessor redirectSkinProcessor(HttpImageProcessor def) {
        if (currentEntity instanceof PlayerEntity) {
            PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) currentEntity;
            SkinImageProcessorAccessor skinImageProcessorAccessor = (SkinImageProcessorAccessor) def;
            skinImageProcessorAccessor.setTextureModel(playerEntityAccessor.getTextureModel());
        }
        return def;
    }
}
