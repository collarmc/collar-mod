package com.collarmc.mod.forge.client.commands.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.collarmc.mod.forge.client.commands.ClientCommands;

@Mixin(ClientPlayerEntity.class)
public class ClientCommandDispatcher {
    @Inject(method = "sendChatMessage", at = @At(value = "HEAD"), cancellable = true)
    private void dispatch(String playerMessage, CallbackInfo ci){
        if(ClientCommands.executeCommand(playerMessage)){
            ci.cancel();
        }
    }
}
