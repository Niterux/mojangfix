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

import com.github.steveice10.mc.auth.data.GameProfile;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import lombok.Getter;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import pl.js6pak.mojangfix.client.skinfix.SkinService;
import pl.js6pak.mojangfix.mixinterface.PlayerEntityAccessor;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityAccessor {
    @Unique
    @Getter
    private GameProfile.TextureModel textureModel;

    public PlayerEntityMixin(World world) {
        super(world);
    }

    @ModifyExpressionValue(method = "registerCloak", at = @At(value = "CONSTANT", args = "stringValue=http://s3.amazonaws.com/MinecraftCloaks/"))
    private String retromcCapeUrl(String original) {
		return "https://assets.retromc.org/capes/";
	}

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/living/player/PlayerEntity;texture:Ljava/lang/String;"))
    private void redirectTexture(PlayerEntity instance, String value) {
        this.setTextureModel(GameProfile.TextureModel.NORMAL);
    }

    @Unique
    public void setTextureModel(GameProfile.TextureModel textureModel) {
        this.textureModel = textureModel;
        this.texture = textureModel == GameProfile.TextureModel.NORMAL ? SkinService.STEVE_TEXTURE : SkinService.ALEX_TEXTURE;
    }
}
