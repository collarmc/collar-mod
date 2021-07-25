package com.collarmc.mod.common.integrations;

import com.collarmc.api.groups.Group;
import com.collarmc.api.waypoints.Waypoint;
import com.collarmc.mod.common.features.events.WaypointCreatedEvent;
import com.collarmc.mod.common.features.events.WaypointRemovedEvent;
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
    public void onWaypointCreated(WaypointCreatedEvent e) {
        if (!isLoaded()) {
            return;
        }
        plastic.world.chatService.sendChatMessageToSelf(String.format(
                "%s%s %s \"%s\" %s %s %s %s",
                prefix(),
                waypointsCommand(),
                addCommand(),
                name(e.waypoint, e.group),
                e.waypoint.location.x,
                e.waypoint.location.y,
                e.waypoint.location.z,
                e.waypoint.location.dimension.name().toLowerCase()
        ));
    }

    @Subscribe(Preference.CALLER)
    public void onWaypointRemoved(WaypointRemovedEvent e) {
        if (!isLoaded()) {
            return;
        }
        plastic.world.chatService.sendChatMessageToSelf(String.format(
                "%s%s %s \"%s\"",
                prefix(),
                waypointsCommand(),
                removeCommand(),
                name(e.waypoint, e.group)
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
