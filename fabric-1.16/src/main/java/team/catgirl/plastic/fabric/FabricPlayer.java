package team.catgirl.plastic.fabric;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import team.catgirl.collar.api.location.Dimension;
import team.catgirl.collar.api.location.Location;
import team.catgirl.collar.mod.mixin.PlayerListEntryMixin;
import team.catgirl.plastic.player.Player;
import team.catgirl.plastic.ui.TextureProvider;
import team.catgirl.plastic.ui.TextureType;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static net.minecraft.world.dimension.DimensionType.*;

public class FabricPlayer implements Player {
    private final AbstractClientPlayerEntity playerEntity;
    private final TextureProvider textureProvider;

    public FabricPlayer(AbstractClientPlayerEntity playerEntity, TextureProvider textureProvider) {
        this.playerEntity = playerEntity;
        this.textureProvider = textureProvider;
    }

    @Override
    public UUID id() {
        return playerEntity.getUuid();
    }

    @Override
    public String name() {
        return playerEntity.getName().asString();
    }

    @Override
    public float yaw() {
        return playerEntity.yaw;
    }

    @Override
    public Optional<BufferedImage> avatar() {
        return Optional.empty();
    }

    @Override
    public void onRender() {
        if (playerEntity.getCapeTexture() == null) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient == null) {
                throw new IllegalStateException("minecraftClient");
            }
            ClientPlayNetworkHandler networkHandler = minecraftClient.getNetworkHandler();
            if (networkHandler == null) {
                throw new IllegalStateException("networkHandler");
            }
            PlayerListEntry entry = networkHandler.getPlayerListEntry(playerEntity.getGameProfile().getId());
            PlayerListEntryMixin entryMixin = (PlayerListEntryMixin)entry;
            if (entryMixin == null) {
                throw new IllegalStateException("entryMixin");
            }
            Map<MinecraftProfileTexture.Type, Identifier> textures = entryMixin.textures();
            String textureName = String.format("plastic-capes/%s.png", playerEntity.getGameProfile().getId());
            textureProvider.getTexture(this, TextureType.CAPE).thenAccept(textureOptional -> {
                textureOptional.ifPresent(texture -> {
                    NativeImage image = nativeImageFrom(texture);
                    NativeImageBackedTexture nativeImageTexture = new NativeImageBackedTexture(image);
                    Identifier identifier = minecraftClient.getTextureManager().registerDynamicTexture(textureName, nativeImageTexture);
                    textures.put(MinecraftProfileTexture.Type.CAPE, identifier);
                    textures.put(MinecraftProfileTexture.Type.ELYTRA, identifier);
                });
            });
        }
    }

    @Override
    public int networkId() {
        return playerEntity.getEntityId();
    }

    @Override
    public Location location() {
        Dimension dimension;
        Identifier skyProperties = playerEntity.getEntityWorld().getDimension().getSkyProperties();
        if (OVERWORLD_ID.equals(skyProperties)) {
            dimension = Dimension.OVERWORLD;
        } else if (THE_END_ID.equals(skyProperties)) {
            dimension = Dimension.END;
        } else if (THE_NETHER_ID.equals(skyProperties)) {
            dimension = Dimension.NETHER;
        } else {
            dimension = Dimension.UNKNOWN;
        }
        BlockPos blockPos = playerEntity.getBlockPos();
        return new Location((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), dimension);
    }

    private static NativeImage nativeImageFrom(BufferedImage img) {
        NativeImage nativeImage = new NativeImage(img.getWidth(), img.getHeight(), true);
        for (int width = 0; width < img.getWidth(); width++) {
            for (int height = 0; height < img.getHeight(); height++) {
                nativeImage.setPixelColor(width, height, img.getRGB(width, height));
            }
        }
        return nativeImage;
    }
}
