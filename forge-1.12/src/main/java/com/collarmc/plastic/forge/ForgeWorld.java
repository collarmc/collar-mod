package com.collarmc.plastic.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
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
        return findPlayerById(Minecraft.getMinecraft().player.getGameProfile().getId()).orElseThrow(() -> new IllegalStateException("could not find own player"));
    }

    @Override
    public List<Player> allPlayers() {
        WorldClient world = Minecraft.getMinecraft().world;
        if (world == null) {
            return new ArrayList<>();
        }
        NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();
        if (connection == null) {
            return new ArrayList<>();
        }
        return connection.getPlayerInfoMap()
                .stream()
                .map(networkPlayer -> {
                    EntityPlayer entityPlayer = world.getPlayerEntityByUUID(networkPlayer.getGameProfile().getId());
                    return new ForgePlayer(entityPlayer, networkPlayer, textureProvider);
                })
                .collect(Collectors.toList());
    }
}
