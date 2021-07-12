package team.catgirl.plastic.forge;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import team.catgirl.collar.api.location.Dimension;
import team.catgirl.collar.api.location.Location;
import team.catgirl.plastic.ui.TextureProvider;
import team.catgirl.plastic.ui.TextureType;
import team.catgirl.plastic.player.Player;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class ForgePlayer implements Player {

    private final static Cache<String, Optional<BufferedImage>> AVATAR_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .initialCapacity(50)
            .build();

    public final UUID id;
    public final EntityPlayer player;
    private final TextureProvider textureProvider;
    private final Minecraft minecraft = Minecraft.getMinecraft();

    public ForgePlayer(EntityPlayer player, TextureProvider textureProvider) {
        this.id = player.getUniqueID();
        this.player = player;
        this.textureProvider = textureProvider;
    }

    @Override
    public UUID id() {
        return player.getGameProfile().getId();
    }

    @Override
    public int networkId() {
        return player.getEntityId();
    }

    @Override
    public String name() {
        return player.getName();
    }

    @Override
    public float yaw() {
        return player.cameraYaw;
    }

    @Override
    public Location location() {
        Dimension dimension;
        switch (DimensionType.getById(player.dimension)) {
            case NETHER:
                dimension = Dimension.NETHER;
                break;
            case OVERWORLD:
                dimension = Dimension.OVERWORLD;
                break;
            case THE_END:
                dimension = Dimension.END;
                break;
            default:
                dimension = Dimension.UNKNOWN;
        }
        BlockPos pos = player.getPosition();
        return new Location((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), dimension);
    }

    @Override
    public Optional<BufferedImage> avatar() {
        try {
            return AVATAR_CACHE.get(name(), () -> {
                AtomicReference<BufferedImage> avatarImage = new AtomicReference<>();
                textureProvider.getTexture(this, TextureType.AVATAR);
                return avatarImage.get() == null ? Optional.empty() : Optional.of(avatarImage.get());
            });
        } catch (ExecutionException e) {
            return Optional.empty();
        }
    }

    @Override
    public void onRender() {
        if (!(player instanceof AbstractClientPlayer)) {
            return;
        }
        boolean hasCape = ((AbstractClientPlayer) player).hasPlayerInfo() && ((AbstractClientPlayer) player).getLocationCape() != null;
        if (!hasCape) {
            AbstractClientPlayer acp = (AbstractClientPlayer) player;
            NetworkPlayerInfo playerInfo = ObfuscationReflectionHelper.getPrivateValue(AbstractClientPlayer.class, acp, "field_175157_a");
            if(playerInfo == null) return;
            Map<MinecraftProfileTexture.Type, ResourceLocation> textures = ObfuscationReflectionHelper.getPrivateValue(NetworkPlayerInfo.class, playerInfo, "field_187107_a");
            String textureName = String.format("plastic-capes/%s.png", playerInfo.getGameProfile().getId());
            textureProvider.getTexture(this, TextureType.CAPE).thenAccept(textureOptional -> {
                textureOptional.ifPresent(texture -> {
                    ResourceLocation resourceLocation = minecraft.getTextureManager().getDynamicTextureLocation(textureName, new DynamicTexture(texture));
//                            minecraft.getTextureManager().bindTexture(resourceLocation);
                    textures.put(MinecraftProfileTexture.Type.CAPE, resourceLocation);
                    textures.put(MinecraftProfileTexture.Type.ELYTRA, resourceLocation);
                });
            });
        }
    }

    private Optional<BufferedImage> defaultAvatar() {
        EntityPlayer playerEntityByName = minecraft.world.getPlayerEntityByName(player.getName());
        if (playerEntityByName == null) {
            return Optional.empty();
        }
        EntityOtherPlayerMP playerMP = (EntityOtherPlayerMP) playerEntityByName;
        ResourceLocation locationSkin = playerMP.getLocationSkin();
        try {
            IResource resource = minecraft.getResourceManager().getResource(locationSkin);
            try (InputStream stream = resource.getInputStream()) {
                BufferedImage bufferedImage = TextureUtil.readBufferedImage(stream);
                return Optional.of(bufferedImage.getSubimage(8, 8, 15, 15));
            }
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForgePlayer that = (ForgePlayer) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
