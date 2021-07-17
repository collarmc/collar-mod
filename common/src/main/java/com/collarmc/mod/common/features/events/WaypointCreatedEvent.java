package com.collarmc.mod.common.features.events;


import com.collarmc.api.waypoints.Waypoint;

import java.util.UUID;

public final class WaypointCreatedEvent {
    public final Waypoint waypoint;

    public WaypointCreatedEvent(Waypoint waypoint) {
        this.waypoint = waypoint;
    }
}
