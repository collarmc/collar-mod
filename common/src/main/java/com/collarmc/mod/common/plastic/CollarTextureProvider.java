package com.collarmc.mod.common.plastic;

import com.collarmc.client.events.CollarStateChangedEvent;
import com.collarmc.pounce.EventBus;
import com.collarmc.pounce.Preference;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.collarmc.client.Collar;
import com.collarmc.plastic.player.Player;
import com.collarmc.plastic.ui.TextureProvider;
import com.collarmc.plastic.ui.TextureType;
import com.collarmc.pounce.Subscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CollarTextureProvider implements TextureProvider {

    private static final Logger LOGGER = LogManager.getLogger(CollarTextureProvider.class);

    private final EventBus eventBus;

    private static final Cache<TextureKey, CompletableFuture<Optional<BufferedImage>>> TEXTURE_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .initialCapacity(100)
            .build();

    private Collar collar;

    public CollarTextureProvider(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.subscribe(this);
    }

    @Override
    public CompletableFuture<Optional<BufferedImage>> getTexture(Player player, TextureType type, BufferedImage defaultTexture) {
        if (this.collar != null && this.collar.getState().equals(Collar.State.CONNECTED)) {
            TextureKey textureKey = new TextureKey(player.id(), type);
            CompletableFuture<Optional<BufferedImage>> theImage = (CompletableFuture)TEXTURE_CACHE.getIfPresent(textureKey);
            if (theImage == null) {
                theImage = this.getTextureFromApi(player, type, defaultTexture);
                TEXTURE_CACHE.put(textureKey, theImage);
            }
            return theImage;
        } else {
            return CompletableFuture.completedFuture(Optional.empty());
        }
    }

    private CompletableFuture<Optional<BufferedImage>> getTextureFromApi(Player player, TextureType type, BufferedImage defaultTexture) {
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

        return this.collar.identities().resolvePlayer(player.id()).thenComposeAsync((thePlayer) -> {
            return thePlayer.isPresent() ? this.collar.textures().playerTextureFuture((com.collarmc.api.session.Player)thePlayer.get(), textureType).thenComposeAsync((textureOptional) -> {
                if (textureOptional.isPresent()) {
                    CompletableFuture<Optional<BufferedImage>> result = new CompletableFuture();
                    textureOptional.ifPresent((texture) -> {
                        texture.loadImage((bufferedImageOptional) -> {
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
            }) : CompletableFuture.completedFuture(Optional.ofNullable(defaultTexture));
        });
    }

    @Subscribe(Preference.CALLER)
    public void onConnected(CollarStateChangedEvent event) {
        this.collar = event.collar;
        if (event.state == Collar.State.CONNECTED) {
            TEXTURE_CACHE.invalidateAll();
        }

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
