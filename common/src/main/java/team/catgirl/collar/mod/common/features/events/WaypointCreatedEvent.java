package team.catgirl.collar.mod.common.features.events;


import team.catgirl.collar.api.waypoints.Waypoint;

import java.util.UUID;

public final class WaypointCreatedEvent {
    public final Waypoint waypoint;

    public WaypointCreatedEvent(Waypoint waypoint) {
        this.waypoint = waypoint;
    }
}
