package com.collarmc.mod.common.features.events;

import com.collarmc.api.location.Location;
import com.collarmc.plastic.player.Player;

public class PlayerLocationUpdatedEvent {
    public final Player player;
    public final Location location;

    public PlayerLocationUpdatedEvent(Player player, Location location) {
        this.player = player;
        this.location = location;
    }
}
