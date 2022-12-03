package com.collarmc.plastic;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import com.collarmc.plastic.ui.TextureProvider;
import com.collarmc.pounce.EventBus;
import org.apache.logging.log4j.Logger;

import java.io.File;

public final class GluePlastic extends Plastic {

    public GluePlastic(TextureProvider textureProvider, EventBus eventBus) {
        super(new GlueDisplay(), new GlueWorld(textureProvider, new GlueChatService(new GlueDisplay()), eventBus), eventBus);
    }

    @Override
    protected Logger getLogger() {
        return null;
    }

    @Override
    public File home() {
        return MinecraftClient.getInstance().runDirectory;
    }

    @Override
    public String serverAddress() {
        ServerInfo currentServerEntry = MinecraftClient.getInstance().getCurrentServerEntry();
        if (currentServerEntry == null) {
            throw new IllegalStateException("not connected to a server");
        }
        return currentServerEntry.address;
    }

    @Override
    public String sessionId() {
        return MinecraftClient.getInstance().getSession().getSessionId();
    }

    @Override
    public String accessToken() {
        return MinecraftClient.getInstance().getSession().getAccessToken();
    }
}
