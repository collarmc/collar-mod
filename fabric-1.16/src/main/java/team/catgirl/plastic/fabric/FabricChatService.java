package team.catgirl.plastic.fabric;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import team.catgirl.plastic.chat.ChatService;
import team.catgirl.plastic.ui.Display;

public class FabricChatService extends ChatService {

    public FabricChatService(Display display) {
        super(display);
    }

    @Override
    public void sendChatMessage(String recipient, String message) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            display.displayErrorMessage(String.format("No player %s", recipient));
        } else {
            player.sendChatMessage(String.format("/tell %s %s", recipient, message));
        }
    }
}
