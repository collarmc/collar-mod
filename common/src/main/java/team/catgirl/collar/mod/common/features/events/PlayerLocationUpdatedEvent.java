package team.catgirl.collar.mod.common.features.events;

import team.catgirl.plastic.player.Player;
import team.catgirl.plastic.world.Dimension;
import team.catgirl.plastic.world.Position;

public class PlayerLocationUpdatedEvent {
    public final Player player;
    public final Position position;
    public final Dimension dimension;

    public PlayerLocationUpdatedEvent(Player player, Position position, Dimension dimension) {
        this.player = player;
        this.position = position;
        this.dimension = dimension;
    }
}
