package team.catgirl.plastic.player;

import team.catgirl.plastic.events.render.PlayerRenderEvent;
import team.catgirl.plastic.world.Entity;

import java.awt.image.BufferedImage;
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

    /**
     * Run when {@link PlayerRenderEvent} fired
     */
    void onRender();
}
