package team.catgirl.collar.mod.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.catgirl.plastic.Plastic;
import team.catgirl.plastic.chat.ChatService;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Inject(method = "sendChatMessage", at = @At(value = "HEAD"), cancellable = true)
    public void onDisconnect(String message, CallbackInfo cb) {
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
