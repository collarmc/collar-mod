package com.collarmc.mod.common.features.events;


import com.collarmc.api.groups.Group;
import com.collarmc.api.waypoints.Waypoint;

public final class WaypointCreatedEvent {
    public final Waypoint waypoint;
    public final Group group;

    public WaypointCreatedEvent(Waypoint waypoint, Group group) {
        this.waypoint = waypoint;
        this.group = group;
    }
}
