package com.collarmc.mod.glue;

import com.google.common.base.MoreObjects;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface IPlayerListEntryTextures {
    public Map<MinecraftProfileTexture.Type, Identifier> getTextures();

}
