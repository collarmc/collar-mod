package com.collarmc.plastic;

import com.collarmc.client.Collar;
import com.collarmc.plastic.ui.TextureProvider;
import com.collarmc.pounce.EventBus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.server.integrated.IntegratedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public final class FabricPlastic extends Plastic {

    private static final Logger LOGGER = LogManager.getLogger(Collar.class.getName());
    public static MinecraftClient mc;

    public FabricPlastic(TextureProvider textureProvider, EventBus eventBus) {
        super(new FabricDisplay(), new com.collarmc.plastic.FabricWorld(textureProvider, new FabricChatService(new FabricDisplay()), eventBus), eventBus);
        LOGGER.info("FabricPlastic 1.19 constructor");
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
        IntegratedServer currentIntegratedServerEntry = MinecraftClient.getInstance().getServer();
        if (currentServerEntry == null) {
            if (currentIntegratedServerEntry != null) {
                return "localhost";
            } else {
                throw new IllegalStateException("not connected to a server");
            }
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
