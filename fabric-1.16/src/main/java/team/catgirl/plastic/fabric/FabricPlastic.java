package team.catgirl.plastic.fabric;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import team.catgirl.plastic.Plastic;
import team.catgirl.plastic.ui.TextureProvider;
import team.catgirl.pounce.EventBus;

import java.io.File;

public final class FabricPlastic extends Plastic {

    public FabricPlastic(TextureProvider textureProvider, EventBus eventBus) {
        super(new FabricDisplay(), new FabricWorld(textureProvider, new FabricChatService(new FabricDisplay()), eventBus), eventBus);
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
}
