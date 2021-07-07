package team.catgirl.plastic.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import team.catgirl.plastic.chat.ChatService;
import team.catgirl.plastic.player.Player;
import team.catgirl.plastic.ui.TextureProvider;
import team.catgirl.plastic.world.World;
import team.catgirl.pounce.EventBus;

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
        return Minecraft.getMinecraft().world.playerEntities.stream()
                .map(player -> new ForgePlayer(player, textureProvider))
                .collect(Collectors.toList());
    }
}
