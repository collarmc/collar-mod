package com.collarmc.mod.glue.mixin;

import com.collarmc.mod.glue.IPlayerListEntryTextures;
import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import java.util.Map;
import java.util.function.Supplier;


@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin implements IPlayerListEntryTextures {

    private final Map<MinecraftProfileTexture.Type, Identifier> textures = Maps.newEnumMap(MinecraftProfileTexture.Type.class);
    public Map<MinecraftProfileTexture.Type, Identifier> getTextures() {
        return this.textures;
    }
    @Accessor(value = "texturesSupplier")
    public abstract Supplier<SkinTextures> getSkinTexturesSuplier();

    @Inject(method = "getSkinTextures", at = @At("RETURN"), cancellable = true)
    public void modifyReturnValue(CallbackInfoReturnable<SkinTextures> cir) {
        cir.cancel();
        SkinTextures skinTextures = getSkinTexturesSuplier().get();
        Identifier capeTexture = textures.getOrDefault(MinecraftProfileTexture.Type.CAPE, skinTextures.capeTexture());
        Identifier elytraTexture = textures.getOrDefault(MinecraftProfileTexture.Type.CAPE, skinTextures.elytraTexture());
        Identifier texture = textures.getOrDefault(MinecraftProfileTexture.Type.SKIN, skinTextures.texture());
        cir.setReturnValue(new SkinTextures(texture, skinTextures.textureUrl(), capeTexture, elytraTexture, skinTextures.model(), skinTextures.secure()));
    }
}


