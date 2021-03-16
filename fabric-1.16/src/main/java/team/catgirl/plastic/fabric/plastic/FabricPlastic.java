package team.catgirl.plastic.fabric.plastic;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import team.catgirl.plastic.Plastic;

import java.io.File;

public final class FabricPlastic extends Plastic {

    public FabricPlastic() {
        super(new FabricDisplay(), new FabricWorld(), new FabricCommands());
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
