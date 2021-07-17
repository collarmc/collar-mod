package com.collarmc.plastic.fabric;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.NotNull;
import com.collarmc.plastic.chat.ChatService;
import com.collarmc.plastic.player.Player;
import com.collarmc.plastic.ui.TextureProvider;
import com.collarmc.plastic.world.World;
import team.catgirl.pounce.EventBus;

import java.util.List;
import java.util.stream.Collectors;

public class FabricWorld extends World {

    public FabricWorld(TextureProvider textureProvider, ChatService chatService, EventBus eventBus) {
        super(textureProvider, chatService, eventBus);
    }

    @Override
    public Player currentPlayer() {
        return new FabricPlayer(getPlayer(), textureProvider);
    }

    @Override
    public List<Player> allPlayers() {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) {
            throw new IllegalStateException("no world");
        }
        return world.getPlayers().stream()
                .map(playerEntity -> new FabricPlayer(playerEntity, textureProvider))
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
