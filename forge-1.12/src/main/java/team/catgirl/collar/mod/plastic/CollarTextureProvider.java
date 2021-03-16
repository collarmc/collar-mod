package team.catgirl.collar.mod.plastic;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.client.api.textures.Texture;
import team.catgirl.collar.mod.service.events.CollarConnectedEvent;
import team.catgirl.events.Subscribe;
import team.catgirl.plastic.player.Player;
import team.catgirl.plastic.ui.TextureProvider;
import team.catgirl.plastic.ui.TextureType;

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
    public CompletableFuture<Optional<Texture>> getTexture(Player player, TextureType type) {
        if (collar == null || !collar.getState().equals(Collar.State.CONNECTED)) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        team.catgirl.collar.api.textures.TextureType textureType;
        switch (type) {
            case CAPE:
                textureType = team.catgirl.collar.api.textures.TextureType.CAPE;
                break;
            case AVATAR:
                textureType = team.catgirl.collar.api.textures.TextureType.AVATAR;
                break;
            default:
                throw new IllegalStateException("unknown type " + type);
        }
        return collar.identities().resolvePlayer(player.id())
                .thenComposeAsync(thePlayer -> collar.textures().playerTextureFuture(thePlayer, textureType));
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
