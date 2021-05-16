package team.catgirl.plastic.fabric;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import team.catgirl.plastic.chat.ChatService;

public class FabricChatService extends ChatService {
    @Override
    public void sendChatMessage(String recipient, String message) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            throw new IllegalStateException("no player");
        }
        player.sendChatMessage(String.format("/msg %s %s", recipient, message));
    }
}
