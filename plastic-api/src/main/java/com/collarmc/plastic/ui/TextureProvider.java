package com.collarmc.plastic.ui;

import com.collarmc.plastic.player.Player;

import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TextureProvider {
    CompletableFuture<Optional<BufferedImage>> getTexture(Player player, TextureType type, BufferedImage defaultTexture);
}
