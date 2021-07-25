package com.collarmc.plastic.player;

import com.collarmc.plastic.events.render.PlayerRenderEvent;
import com.collarmc.plastic.ui.TextBuilder;
import com.collarmc.plastic.world.Entity;

import java.awt.image.BufferedImage;
import java.util.UUID;
import java.util.function.Consumer;

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

    float yaw();

    void avatar(Consumer<BufferedImage> consumer);

    /**
     * Run when {@link PlayerRenderEvent} fired
     */
    void onRender();

    void send(TextBuilder message);

    void send(String message);
}
