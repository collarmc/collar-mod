package team.catgirl.plastic.fabric;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import team.catgirl.plastic.Plastic;
import team.catgirl.plastic.ui.TextureProvider;

import java.io.File;

public final class FabricPlastic extends Plastic {

    public FabricPlastic(TextureProvider textureProvider) {
        super(new FabricDisplay(), new FabricWorld(textureProvider));
    }

    @Override
    public File home() {
        return MinecraftClient.getInstance().runDirectory;
    }

    @Override
    public String serverIp() {
        ServerInfo currentServerEntry = MinecraftClient.getInstance().getCurrentServerEntry();
        if (currentServerEntry == null) {
            throw new IllegalStateException("not connected to a server");
        }
        return currentServerEntry.address;
    }
}
