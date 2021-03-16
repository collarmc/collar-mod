package team.catgirl.plastic.events;

import team.catgirl.events.Event;
import team.catgirl.plastic.player.Player;

/**
 * Fired when {@link Player#onRender()} is called
 */
public final class LoadPlayerTexturesEvent extends Event {
    public final Player player;

    public LoadPlayerTexturesEvent(Player player) {
        this.player = player;
    }
}
