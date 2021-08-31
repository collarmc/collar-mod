package com.collarmc.mod.glue.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.collarmc.plastic.Plastic;
import com.collarmc.plastic.chat.ChatService;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Inject(method = "sendChatMessage", at = @At(value = "HEAD"), cancellable = true)
    public void sendChatMessage(String message, CallbackInfo cb) {
        // if it looks like a command, don't intercept it
        if (message.startsWith("/")) {
            return;
        }
        ChatService chatService = Plastic.getPlastic().world.chatService;
        if (chatService.onChatMessageSent(message)) {
            cb.cancel();
        }
    }
}
