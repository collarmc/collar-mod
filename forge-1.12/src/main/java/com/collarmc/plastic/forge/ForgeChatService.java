package com.collarmc.plastic.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import com.collarmc.plastic.chat.ChatService;
import com.collarmc.plastic.ui.Display;

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
