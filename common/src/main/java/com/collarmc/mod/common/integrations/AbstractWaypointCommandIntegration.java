package com.collarmc.mod.common.integrations;

import com.collarmc.api.groups.Group;
import com.collarmc.api.waypoints.Waypoint;
import com.collarmc.client.Collar;
import com.collarmc.client.api.location.events.WaypointCreatedEvent;
import com.collarmc.client.api.location.events.WaypointRemovedEvent;
import com.collarmc.client.events.CollarStateChangedEvent;
import com.collarmc.plastic.Plastic;
import com.collarmc.pounce.EventBus;
import com.collarmc.pounce.Preference;
import com.collarmc.pounce.Subscribe;

public abstract class AbstractWaypointCommandIntegration {

    protected final Plastic plastic;

    public AbstractWaypointCommandIntegration(Plastic plastic, EventBus eventBus) {
        this.plastic = plastic;
        eventBus.subscribe(this);
    }

    @Subscribe(Preference.CALLER)
    public void onStateChanged(CollarStateChangedEvent event) {
        if (event.state == Collar.State.DISCONNECTING) {
            // Remove private waypoints
            event.collar.location().privateWaypoints().forEach(waypoint -> removeWaypoint(waypoint, null));
            // Remove group waypoints
            event.collar.groups().groups()
                    .forEach(group -> event.collar.location().groupWaypoints(group).forEach(waypoint -> removeWaypoint(waypoint, group)));
        }
    }

    @Subscribe(Preference.CALLER)
    public void onWaypointCreated(WaypointCreatedEvent event) {
        if (!isLoaded()) {
            return;
        }
        plastic.world.chatService.sendChatMessageToSelf(String.format(
                "%s%s %s \"%s\" %s %s %s %s",
                prefix(),
                waypointsCommand(),
                addCommand(),
                name(event.waypoint, event.group),
                event.waypoint.location.x,
                event.waypoint.location.y,
                event.waypoint.location.z,
                event.waypoint.location.dimension.name().toLowerCase()
        ));
    }

    @Subscribe(Preference.CALLER)
    public void onWaypointRemoved(WaypointRemovedEvent event) {
        if (!isLoaded()) {
            return;
        }
        removeWaypoint(event.waypoint, event.group);
    }

    private void removeWaypoint(Waypoint waypoint, Group group) {
        plastic.world.chatService.sendChatMessageToSelf(String.format(
                "%s%s %s \"%s\"",
                prefix(),
                waypointsCommand(),
                removeCommand(),
                name(waypoint, group)
        ));
    }

    protected abstract String prefix();

    protected abstract String waypointsCommand();

    protected abstract String addCommand();

    protected abstract String removeCommand();

    public abstract boolean isLoaded();

    private static String name(Waypoint waypoint, Group group) {
        return group == null ? waypoint.name : group.name + " - " + waypoint.name;
    }
}
