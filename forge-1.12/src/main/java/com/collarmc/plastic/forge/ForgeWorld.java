package com.collarmc.plastic.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import com.collarmc.plastic.chat.ChatService;
import com.collarmc.plastic.player.Player;
import com.collarmc.plastic.ui.TextureProvider;
import com.collarmc.plastic.world.World;
import com.collarmc.pounce.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ForgeWorld extends World {

    public ForgeWorld(TextureProvider textureProvider, ChatService chatService, EventBus eventBus) {
        super(textureProvider, chatService, eventBus);
    }

    @Override
    public Player currentPlayer() {
        EntityPlayer entityPlayer = Minecraft.getMinecraft().world.playerEntities.stream()
                .filter(player -> player.getEntityId() == Minecraft.getMinecraft().player.getEntityId())
                .findFirst().orElseThrow(() -> new IllegalStateException("could not find current player"));
        return new ForgePlayer(entityPlayer, textureProvider);
    }

    @Override
    public List<Player> allPlayers() {
        WorldClient world = Minecraft.getMinecraft().world;
        if (world == null) {
            return new ArrayList<>();
        }
        return world.playerEntities.stream()
                .map(player -> new ForgePlayer(player, textureProvider))
                .collect(Collectors.toList());
    }
}
