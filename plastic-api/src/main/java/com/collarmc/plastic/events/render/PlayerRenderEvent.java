package com.collarmc.plastic.events.render;

import com.collarmc.plastic.player.Player;

/**
 * Fired when the player is rendered
 */
public final class PlayerRenderEvent {
    public final Player player;

    public PlayerRenderEvent(Player player) {
        this.player = player;
    }
}
