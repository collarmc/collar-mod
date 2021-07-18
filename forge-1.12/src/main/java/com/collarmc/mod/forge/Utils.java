package com.collarmc.mod.forge;

import com.collarmc.api.location.Location;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;

public final class Utils {

    public static BlockPos getBlockPos(Location location) {
        return new BlockPos(location.x, location.y, location.z);
    }

    public static int getDimensionId(Location location) {
        DimensionType dimension;
        switch (location.dimension) {
            case OVERWORLD:
                dimension = DimensionType.OVERWORLD;
                break;
            case END:
                dimension = DimensionType.THE_END;
                break;
            case NETHER:
                dimension = DimensionType.NETHER;
                break;
            default:
                throw new IllegalStateException("unknown dimension " + location.dimension);
        }
        return dimension.getId();
    }

    private Utils() {}
}
