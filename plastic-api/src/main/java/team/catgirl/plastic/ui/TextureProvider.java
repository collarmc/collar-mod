package team.catgirl.plastic.ui;

import team.catgirl.collar.client.api.textures.Texture;
import team.catgirl.plastic.player.Player;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TextureProvider {
    CompletableFuture<Optional<Texture>> getTexture(Player player, TextureType type);
}
