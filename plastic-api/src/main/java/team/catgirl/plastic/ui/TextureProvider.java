package team.catgirl.plastic.ui;

import team.catgirl.plastic.player.Player;

import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TextureProvider {
    CompletableFuture<Optional<BufferedImage>> getTexture(Player player, TextureType type);
}
