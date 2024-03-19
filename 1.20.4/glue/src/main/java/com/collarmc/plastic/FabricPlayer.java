package com.collarmc.plastic;

import com.collarmc.api.location.Dimension;
import com.collarmc.api.location.Location;
import com.collarmc.mod.glue.IPlayerListEntryTextures;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.UUID;

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
    public void onRender() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.player.getName().getString().equals(this.name())) {
            LOGGER.info("FabricPlayer onRender SELF RENDER of " + this.name());
        } else
            LOGGER.info("FabricPlayer onRender method for player named " + this.name() + " {" + this.id() + "}");
        Identifier capeTexture = playerEntity.getSkinTextures().capeTexture();
            if (minecraftClient == null) {
                throw new IllegalStateException("minecraftClient");
            }
            ClientPlayNetworkHandler networkHandler = minecraftClient.getNetworkHandler();
            if (networkHandler == null) {
                LOGGER.info("Collar FabricPlayer onRender networkHandler is null");
                throw new IllegalStateException("networkHandler");
            }
            PlayerListEntry entry = networkHandler.getPlayerListEntry(playerEntity.getGameProfile().getId());
            IPlayerListEntryTextures entryMixin = (IPlayerListEntryTextures)entry;
            if (entryMixin == null) {
                //throw new IllegalStateException("entryMixin");
                //it shouldn't crash, there are dummy entities on multiple servers. like Hypixel
                LOGGER.info("Collar FabricPlayer onRender entryMixin is null");
                return;
            }


            Map<MinecraftProfileTexture.Type, Identifier> textures = entryMixin.getTextures();
            String textureName = String.format("plastic-capes/%s.png", playerEntity.getGameProfile().getId());
            //LOGGER.info("Collar FabricPlayer onRender textureName: " + textureName);
            textureProvider.getTexture(this, TextureType.CAPE, null).thenAccept(textureOptional -> {
                LOGGER.info("onRender getting the Cape Texture for player " + this.name());
                textureOptional.ifPresent(texture -> {
                    NativeImage image = nativeImageFrom(texture);
                    NativeImageBackedTexture nativeImageTexture = new NativeImageBackedTexture(image);

                    Identifier identifier = minecraftClient.getTextureManager().registerDynamicTexture(textureName, nativeImageTexture);

                    textures.put(MinecraftProfileTexture.Type.CAPE, identifier);
                    textures.put(MinecraftProfileTexture.Type.ELYTRA, identifier);
                });
            });
            textureProvider.getTexture(this, TextureType.AVATAR, null).thenAccept(textureOptional -> {
                LOGGER.info("onRender getting the Avatar Texture for player " + this.name());
                textureOptional.ifPresent(texture -> {
                    NativeImage image = nativeImageFrom(texture);
                    NativeImageBackedTexture nativeImageTexture = new NativeImageBackedTexture(image);
                    Identifier identifier = minecraftClient.getTextureManager().registerDynamicTexture(textureName, nativeImageTexture);

                    textures.put(MinecraftProfileTexture.Type.SKIN, identifier);
                });
            });
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

    private static NativeImage nativeImageFrom(BufferedImage img) {
        NativeImage nativeImage = new NativeImage(img.getWidth(), img.getHeight(), true);
        for (int width = 0; width < img.getWidth(); width++) {
            for (int height = 0; height < img.getHeight(); height++) {
                int color = img.getRGB(width, height);
                int a = (color >> 24) & 0xff;
                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;
                int rgba = (a << 24) | (b << 16) | (g << 8) | r;
                nativeImage.setColor(width, height, rgba);
            }
        }
        return nativeImage;
    }

    @Override
    public String toString() {
        return playerEntity.getUuid() + " " + playerEntity.getName();
    }
}