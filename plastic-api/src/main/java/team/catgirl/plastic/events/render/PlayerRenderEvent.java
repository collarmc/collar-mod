package team.catgirl.plastic.events.render;

import team.catgirl.plastic.player.Player;

/**
 * Fired when the player is rendered
 */
public final class PlayerRenderEvent {
    public final Player player;

    public PlayerRenderEvent(Player player) {
        this.player = player;
    }
}
