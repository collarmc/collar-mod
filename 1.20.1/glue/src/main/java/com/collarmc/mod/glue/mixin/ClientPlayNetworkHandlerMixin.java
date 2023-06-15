package com.collarmc.mod.glue.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.collarmc.plastic.Plastic;
import com.collarmc.plastic.chat.ChatService;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Inject(method = "sendChatMessage", at = @At(value = "HEAD"), cancellable = true)
    public void sendChatMessage(String content, CallbackInfo ci) {
        // if it looks like a command, don't intercept it
        if (content.startsWith("/")) {
            return;
        }
        ChatService chatService = Plastic.getPlastic().world.chatService;
        if (chatService.onChatMessageSent(content)) {
            ci.cancel();
        }
    }
}
