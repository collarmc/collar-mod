package com.collarmc.mod.common.features;

import com.collarmc.client.api.location.events.LocationSharingStartedEvent;
import com.collarmc.client.api.location.events.LocationSharingStoppedEvent;
import com.collarmc.client.api.location.events.WaypointCreatedEvent;
import com.collarmc.client.api.location.events.WaypointRemovedEvent;
import com.collarmc.plastic.Plastic;
import com.collarmc.pounce.EventBus;
import com.collarmc.pounce.Subscribe;

public class Locations {

    private final Plastic plastic;

    public Locations(Plastic plastic, EventBus events) {
        this.plastic = plastic;
        events.subscribe(this);
    }

    @Subscribe
    public void onWaypointCreated(WaypointCreatedEvent event) {
        String message;
        if (event.group == null) {
            message = String.format("Waypoint %s created", event.waypoint.name);
        } else {
            message = String.format("Waypoint %s created in %s %s", event.waypoint.name, event.group.type.name, event.group.name);
        }
        plastic.display.displayStatusMessage(message);
        plastic.display.displayInfoMessage(message);
    }

    @Subscribe
    public void onWaypointRemoved(WaypointRemovedEvent event) {
        String message;
        if (event.group == null) {
            message = String.format("Waypoint %s removed", event.waypoint.name);
        } else {
            message = String.format("Waypoint %s removed from %s %s", event.waypoint.name, event.group.type.name, event.group.name);
        }
        plastic.display.displayStatusMessage(message);
        plastic.display.displayInfoMessage(message);
    }

    @Subscribe
    public void onStartedSharingLocation(LocationSharingStartedEvent event) {
        plastic.display.displayInfoMessage(String.format("Started sharing location with %s", event.group.name));
    }

    @Subscribe
    public void onStoppedSharingLocation(LocationSharingStoppedEvent event) {
        plastic.display.displayInfoMessage(String.format("Stopped sharing location with %s", event.group.name));
    }
}
