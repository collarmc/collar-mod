package com.collarmc.plastic;

import com.collarmc.plastic.chat.ChatService;
import com.collarmc.plastic.ui.Display;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;

public class FabricChatService extends ChatService {

    public FabricChatService(Display display) {
        super(display);
    }

    @Override
    public void sendChatMessage(String recipient, String message) {
        ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
        if (handler == null) {
            display.displayErrorMessage(String.format("No clientPlayNetworkHandler %s", recipient));
        } else {
            String messageText = String.format("/tell %s %s", recipient, message);
            handler.sendChatMessage(messageText);
        }
    }

    @Override
    public void sendChatMessageToSelf(String message) {
        MinecraftClient.getInstance().getNetworkHandler().sendChatMessage(message);
    }
}
