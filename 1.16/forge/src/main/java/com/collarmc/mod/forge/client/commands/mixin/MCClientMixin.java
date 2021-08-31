package com.collarmc.mod.forge.client.commands.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.collarmc.mod.forge.client.commands.ClientCommands;

@Mixin(MinecraftClient.class)
public class MCClientMixin {
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void mcClientMixin(RunArgs args, CallbackInfo ci){
        ClientCommands.finalizeInit();
    }
}
