package com.collarmc.mod.fabric.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilderStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface MinecraftClientFieldMixin {
    @Accessor(value = "bufferBuilders")
    BufferBuilderStorage bufferBuilders();
}
