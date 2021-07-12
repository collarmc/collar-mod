package team.catgirl.collar.plastic;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.NotNull;
import team.catgirl.plastic.chat.ChatService;
import team.catgirl.plastic.player.Player;
import team.catgirl.plastic.ui.TextureProvider;
import team.catgirl.plastic.world.World;
import team.catgirl.pounce.EventBus;

import java.util.List;
import java.util.stream.Collectors;

public class FabricWorld extends World {

    public FabricWorld(TextureProvider textureProvider, ChatService chatService, EventBus eventBus) {
        super(textureProvider, chatService, eventBus);
    }

    @Override
    public Player currentPlayer() {
        return new GluePlayer(getPlayer(), textureProvider);
    }

    @Override
    public List<Player> allPlayers() {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) {
            throw new IllegalStateException("no world");
        }
        return world.getPlayers().stream()
                .map(playerEntity -> new GluePlayer(playerEntity, textureProvider))
                .collect(Collectors.toList());
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
