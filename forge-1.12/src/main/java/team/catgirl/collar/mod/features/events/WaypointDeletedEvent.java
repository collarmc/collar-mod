package team.catgirl.collar.mod.features.events;

import team.catgirl.event.Event;
import team.catgirl.plastic.world.Dimension;
import team.catgirl.plastic.world.Position;

import java.util.UUID;

public final class WaypointDeletedEvent extends Event {
    public final UUID id;
    public final String name;
    public final Position position;
    public final Dimension dimension;

    public WaypointDeletedEvent(UUID id, String name, Position position, Dimension dimension) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.dimension = dimension;
    }
}
