package team.catgirl.collar.mod.common.features.events;

import team.catgirl.collar.api.waypoints.Waypoint;

public final class WaypointDeletedEvent {
    public final Waypoint waypoint;

    public WaypointDeletedEvent(Waypoint waypoint) {
        this.waypoint = waypoint;
    }
}
