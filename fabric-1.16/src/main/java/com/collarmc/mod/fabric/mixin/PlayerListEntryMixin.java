package com.collarmc.mod.fabric.mixin;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(PlayerListEntry.class)
public interface PlayerListEntryMixin {
    @Accessor(value = "textures")
    Map<Type, Identifier> textures();
}
