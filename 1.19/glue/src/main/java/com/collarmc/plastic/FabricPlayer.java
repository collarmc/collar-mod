package com.collarmc.plastic.fabric;

import com.collarmc.api.location.Dimension;
import com.collarmc.api.location.Location;
import com.collarmc.mod.fabric.mixin.PlayerListEntryMixin;
import com.collarmc.plastic.player.Player;
import com.collarmc.plastic.ui.TextureProvider;
import com.collarmc.plastic.ui.TextureType;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static net.minecraft.world.dimension.DimensionTypes.*;

public class FabricPlayer implements Player {

    private static final Logger LOGGER = LogManager.getLogger(FabricPlayer.class);

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
        return playerEntity.getName().getString();
    }

    @Override
    public float yaw() {
        return playerEntity.getYaw();
    }

    @Override
    public void avatar(Consumer<BufferedImage> consumer) {
        textureProvider.getTexture(this, TextureType.AVATAR, defaultAvatar()).thenAccept(bufferedImageOptional -> {
            bufferedImageOptional.ifPresent(consumer);
            if (!bufferedImageOptional.isPresent()) {
                LOGGER.error("Avatar for " + this + " is missing");
            }
        });
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
            textureProvider.getTexture(this, TextureType.CAPE, null).thenAccept(textureOptional -> {
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
        return playerEntity.getId();
    }

    @Override
    public Location location() {
        Dimension dimension;
        Identifier effects = playerEntity.getEntityWorld().getDimension().effects();
        if (OVERWORLD_ID.equals(effects)) {
            dimension = Dimension.OVERWORLD;
        } else if (THE_END_ID.equals(effects)) {
            dimension = Dimension.END;
        } else if (THE_NETHER_ID.equals(effects)) {
            dimension = Dimension.NETHER;
        } else {
            dimension = Dimension.UNKNOWN;
        }
        BlockPos blockPos = playerEntity.getBlockPos();
        return new Location((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), dimension);
    }

    private BufferedImage defaultAvatar() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null) {
            throw new IllegalStateException("minecraftClient");
        }
        Identifier skinTexture = this.playerEntity.getSkinTexture();
        try {
            Resource resource = minecraftClient.getResourceManager().getResource(skinTexture).orElse(null);
            BufferedImage skin = ImageIO.read(resource.getInputStream());
            return skin.getSubimage(8, 8, 15, 15);
        } catch (IOException e) {
            throw new IllegalStateException("could not load skin for " + this);
        }
    }

    private static NativeImage nativeImageFrom(BufferedImage img) {
        NativeImage nativeImage = new NativeImage(img.getWidth(), img.getHeight(), true);
        for (int width = 0; width < img.getWidth(); width++) {
            for (int height = 0; height < img.getHeight(); height++) {
                nativeImage.setColor(width, height, img.getRGB(width, height));
            }
        }
        return nativeImage;
    }

    @Override
    public String toString() {
        return playerEntity.getUuid() + " " + playerEntity.getName();
    }
}
