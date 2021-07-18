package com.collarmc.plastic;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.NotNull;
import com.collarmc.plastic.ui.Display;
import com.collarmc.plastic.ui.TextBuilder;

public class GlueDisplay implements Display {
    @Override
    public void displayStatusMessage(String message) {
        getPlayer().sendMessage(new LiteralText(message), true);
    }

    @Override
    public void displayStatusMessage(TextBuilder message) {
        getPlayer().sendMessage(((GlueTextBuilder)message).text, false);
    }

    @Override
    public void displayMessage(TextBuilder message) {
        getPlayer().sendMessage(((GlueTextBuilder)message).text, false);
    }

    @Override
    public TextBuilder newTextBuilder() {
        return new GlueTextBuilder();
    }

    @NotNull
    private ClientPlayerEntity getPlayer() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            throw new IllegalStateException("player not ready");
        }
        return player;
    }
}
