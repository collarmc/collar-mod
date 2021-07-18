package com.collarmc.mod.common.features.events;

import com.collarmc.api.waypoints.Waypoint;

public final class WaypointDeletedEvent {
    public final Waypoint waypoint;

    public WaypointDeletedEvent(Waypoint waypoint) {
        this.waypoint = waypoint;
    }
}
