package team.catgirl.collar.mod.common.features.events;

import team.catgirl.collar.api.location.Location;
import team.catgirl.plastic.player.Player;

public class PlayerLocationUpdatedEvent {
    public final Player player;
    public final Location location;

    public PlayerLocationUpdatedEvent(Player player, Location location) {
        this.player = player;
        this.location = location;
    }
}
