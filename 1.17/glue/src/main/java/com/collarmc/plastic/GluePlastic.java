package com.collarmc.plastic;

import com.collarmc.client.Collar;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import com.collarmc.plastic.ui.TextureProvider;
import com.collarmc.pounce.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public final class GluePlastic extends Plastic {
    private static final Logger LOGGER = LogManager.getLogger(Collar.class.getName());
    public GluePlastic(TextureProvider textureProvider, EventBus eventBus) {
        super(new GlueDisplay(), new GlueWorld(textureProvider, new GlueChatService(new GlueDisplay()), eventBus), eventBus);
        LOGGER.info("GluePlastic 1.17 constructor");
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
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
