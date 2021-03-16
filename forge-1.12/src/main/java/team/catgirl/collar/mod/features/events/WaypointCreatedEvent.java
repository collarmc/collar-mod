package team.catgirl.collar.mod.features.events;

import team.catgirl.events.Event;
import team.catgirl.plastic.world.Dimension;
import team.catgirl.plastic.world.Position;

import java.util.UUID;

public final class WaypointCreatedEvent extends Event {
    public final UUID id;
    public final String name;
    public final String groupName;
    public final Position position;
    public final Dimension dimension;

    public WaypointCreatedEvent(UUID id, String name, String groupName, Position position, Dimension dimension) {
        this.id = id;
        this.name = name;
        this.groupName = groupName;
        this.position = position;
        this.dimension = dimension;
    }
}
