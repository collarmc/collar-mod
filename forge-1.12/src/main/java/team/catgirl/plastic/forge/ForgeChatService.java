package team.catgirl.plastic.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import team.catgirl.plastic.chat.ChatService;

public final class ForgeChatService extends ChatService {
    @Override
    public void sendChatMessage(String recipient, String message) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) {
            throw new IllegalStateException("no player");
        }
        player.sendChatMessage(String.format("/msg %s %s", recipient, message));
    }
}
