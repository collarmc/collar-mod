package com.collarmc.mod.common.plastic;

import com.collarmc.client.events.CollarStateChangedEvent;
import com.collarmc.pounce.EventBus;
import com.collarmc.pounce.Preference;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.collarmc.client.Collar;
import com.collarmc.plastic.player.Player;
import com.collarmc.plastic.ui.TextureProvider;
import com.collarmc.plastic.ui.TextureType;
import com.collarmc.pounce.Subscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class CollarTextureProvider implements TextureProvider {

    private static final Logger LOGGER = LogManager.getLogger(CollarTextureProvider.class);

    private final EventBus eventBus;

    private static final Cache<TextureKey, Optional<BufferedImage>> TEXTURE_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .initialCapacity(100)
            .recordStats()
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

            Optional<BufferedImage> cachedImage = TEXTURE_CACHE.getIfPresent(textureKey);

            if (cachedImage != null) {
                if (cachedImage.isPresent()) {
                    LOGGER.info("Getting texture image from cache { playerName: " + player.name() + ", playerId: " + player.id() + ", textureType: " + type + " }");
                } else
                    LOGGER.info("Getting NULL for texture image from cache { playerName: " + player.name() + ", playerId: " + player.id() + ", textureType: " + type + " }");
                return CompletableFuture.completedFuture(cachedImage);
            } else {
                CompletableFuture<Optional<BufferedImage>> theImage = this.getTextureFromApi(player, type, defaultTexture).thenApply(value->{
                    //Optional<BufferedImage> retVal = Optional.empty();
                    if (value.isPresent()){
                        LOGGER.info("Getting texture image from api { playerName: " + player.name() + ", playerId: " + player.id() + ", textureType: " + type + " }");
                        //retVal = value;
                    } else {
                        LOGGER.info("Getting NULL for texture image from api { playerName: " + player.name() + ", playerId: "  + player.id() + ", textureType: " + type + " }");
                    }
                    TEXTURE_CACHE.put(textureKey, value);
                    return value;
                });
                return theImage;
            }
        } else {
            LOGGER.info("Cannot get texture image because of collar state { playerName: " + player.name() + ", playerId: " + player.id() + ", textureType: " + type + " }");
            return CompletableFuture.completedFuture(Optional.empty());
        }
    }

    private  CompletableFuture<Optional<BufferedImage>> getTextureFromApi(Player player, TextureType type, BufferedImage defaultTexture) {
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
