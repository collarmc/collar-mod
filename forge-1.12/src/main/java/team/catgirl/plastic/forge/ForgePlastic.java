package team.catgirl.plastic.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import team.catgirl.plastic.Plastic;
import team.catgirl.plastic.ui.TextureProvider;
import team.catgirl.pounce.EventBus;

import java.io.File;

public class ForgePlastic extends Plastic {

    public ForgePlastic(TextureProvider textureProvider, EventBus eventBus) {
        super(new ForgeDisplay(), new ForgeWorld(textureProvider, new ForgeChatService(new ForgeDisplay()), eventBus), eventBus);
    }

    @Override
    public File home() {
        return Minecraft.getMinecraft().mcDataDir;
    }

    @Override
    public String serverAddress() {
        ServerData serverData = Minecraft.getMinecraft().getCurrentServerData();
        return serverData == null ? null : serverData.serverIP;
    }

    @Override
    public String sessionId() {
        return Minecraft.getMinecraft().getSession().getSessionID();
    }

    @Override
    public String accessToken() {
        return Minecraft.getMinecraft().getSession().getToken();
    }
}
