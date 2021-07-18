package com.collarmc.mod.common.plastic;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.collarmc.client.Collar;
import com.collarmc.client.api.textures.Texture;
import com.collarmc.mod.common.events.CollarConnectedEvent;
import com.collarmc.plastic.player.Player;
import com.collarmc.plastic.ui.TextureProvider;
import com.collarmc.plastic.ui.TextureType;
import com.collarmc.pounce.Subscribe;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CollarTextureProvider implements TextureProvider {

    private static final Cache<TextureKey, CompletableFuture<Texture>> TEXTURE_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.SECONDS)
            .initialCapacity(100)
            .build();

    private Collar collar;

    @Override
    public CompletableFuture<Optional<BufferedImage>> getTexture(Player player, TextureType type, BufferedImage defaultTexture) {
        if (collar == null || !collar.getState().equals(Collar.State.CONNECTED)) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        com.collarmc.api.textures.TextureType textureType;
        switch (type) {
            case CAPE:
                textureType = com.collarmc.api.textures.TextureType.CAPE;
                break;
            case AVATAR:
                textureType = com.collarmc.api.textures.TextureType.AVATAR;
                break;
            default:
                throw new IllegalStateException("unknown type " + type);
        }

        return collar.identities().resolvePlayer(player.id())
                .thenComposeAsync(thePlayer -> {
                    if (thePlayer.isPresent()) {
                        return collar.textures().playerTextureFuture(thePlayer.get(), textureType)
                                .thenComposeAsync(textureOptional -> {
                                    if (textureOptional.isPresent()) {
                                        CompletableFuture<Optional<BufferedImage>> result = new CompletableFuture<>();
                                        textureOptional.ifPresent(texture -> {
                                            texture.loadImage(bufferedImageOptional -> {
                                                if (bufferedImageOptional.isPresent()) {
                                                    result.complete(bufferedImageOptional);
                                                } else {
                                                    result.complete(Optional.ofNullable(defaultTexture));
                                                }
                                            });
                                        });
                                        return result;
                                    } else {
                                        return CompletableFuture.completedFuture(Optional.ofNullable(defaultTexture));
                                    }
                                });
                    } else {
                        return CompletableFuture.completedFuture(Optional.ofNullable(defaultTexture));
                    }
                });
    }

    @Subscribe
    public void onConnected(CollarConnectedEvent event) {
        collar = event.collar;
        TEXTURE_CACHE.invalidateAll();
    }

    private static final class TextureKey {
        public final UUID id;
        public final TextureType type;

        public TextureKey(UUID id, TextureType type) {
            this.id = id;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TextureKey that = (TextureKey) o;
            return id.equals(that.id) && type == that.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, type);
        }
    }
}
