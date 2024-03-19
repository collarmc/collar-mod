package com.collarmc.plastic;

import com.collarmc.plastic.chat.ChatService;
import com.collarmc.plastic.ui.Display;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

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
            String messageText = String.format("/tell %s %s", recipient, message);
            player.sendChatMessage(messageText, null);
        }
    }

    @Override
    public void sendChatMessageToSelf(String message) {
        MinecraftClient.getInstance().player.sendChatMessage(message, null);
    }
}
