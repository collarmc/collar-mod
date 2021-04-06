package team.catgirl.plastic.player;

import team.catgirl.plastic.world.Entity;
import team.catgirl.plastic.world.Position;
import team.catgirl.plastic.world.Dimension;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public interface Player extends Entity {
    /**
     * @return unique ID of player
     */
    UUID id();

    /**
     * @return network ID of player
     */
    int networkId();

    /**
     * @return name of player
     */
    String name();

    /**
     * @return player's avatar
     */
    Optional<BufferedImage> avatar();

    void onRender();
}
