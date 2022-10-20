package com.collarmc.plastic.fabric;

import com.collarmc.plastic.ui.Display;
import com.collarmc.plastic.ui.TextBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class FabricDisplay implements Display {
    @Override
    public void displayStatusMessage(String message) {
        getPlayer().sendMessage(Text.literal(message), true);
    }

    @Override
    public void displayStatusMessage(TextBuilder message) {
        getPlayer().sendMessage(((FabricTextBuilder)message).text, false);
    }

    @Override
    public void displayMessage(TextBuilder message) {
        getPlayer().sendMessage(((FabricTextBuilder)message).text, false);
    }

    @Override
    public TextBuilder newTextBuilder() {
        return new FabricTextBuilder();
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
