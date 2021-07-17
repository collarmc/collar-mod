package com.collarmc.plastic.fabric;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import com.collarmc.plastic.chat.ChatService;
import com.collarmc.plastic.ui.Display;

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
