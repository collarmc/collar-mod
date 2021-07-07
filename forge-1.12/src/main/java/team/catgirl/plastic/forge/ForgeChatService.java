package team.catgirl.plastic.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import team.catgirl.plastic.chat.ChatService;
import team.catgirl.plastic.ui.Display;

public final class ForgeChatService extends ChatService {

    public ForgeChatService(Display display) {
        super(display);
    }

    @Override
    public void sendChatMessage(String recipient, String message) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) {
            display.displayErrorMessage(String.format("No player %s", recipient));
        } else {
            player.sendChatMessage(String.format("/tell %s %s", recipient, message));
        }
    }
}
