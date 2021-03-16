package team.catgirl.collar.mod.plastic;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import team.catgirl.plastic.player.Player;
import team.catgirl.plastic.world.Dimension;
import team.catgirl.plastic.world.Position;

import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.UUID;

import static net.minecraft.world.dimension.DimensionType.*;

public class FabricPlayer implements Player {
    private final AbstractClientPlayerEntity playerEntity;

    public FabricPlayer(AbstractClientPlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
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
    public Optional<BufferedImage> avatar() {
        return Optional.empty();
    }

    @Override
    public void onRender() {
        System.err.println("not implemented");
    }

    @Override
    public int networkId() {
        return playerEntity.getEntityId();
    }

    @Override
    public Position position() {
        BlockPos blockPos = playerEntity.getBlockPos();
        return new Position(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    @Override
    public Dimension dimension() {
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
        return dimension;
    }
}
