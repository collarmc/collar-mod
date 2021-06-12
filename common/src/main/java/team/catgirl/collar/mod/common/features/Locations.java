package team.catgirl.collar.mod.common.features;

import team.catgirl.collar.api.groups.Group;
import team.catgirl.collar.api.location.Location;
import team.catgirl.collar.api.session.Player;
import team.catgirl.collar.api.waypoints.Waypoint;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.client.api.location.LocationApi;
import team.catgirl.collar.client.api.location.LocationListener;
import team.catgirl.collar.mod.common.features.events.PlayerLocationUpdatedEvent;
import team.catgirl.collar.mod.common.features.events.WaypointCreatedEvent;
import team.catgirl.collar.mod.common.features.events.WaypointDeletedEvent;
import team.catgirl.plastic.Plastic;
import team.catgirl.pounce.EventBus;

import java.util.Set;

public class Locations implements LocationListener {

    private final Plastic plastic;
    private final EventBus events;

    public Locations(Plastic plastic, EventBus events) {
        this.plastic = plastic;
        this.events = events;
    }

    @Override
    public void onLocationUpdated(Collar collar, LocationApi locationApi, Player player, Location location) {
        plastic.world.allPlayers().stream()
                .filter(candidate -> candidate.id().equals(player.minecraftPlayer.id))
                .findFirst()
                .ifPresent(thePlayer -> {
                    events.dispatch(new PlayerLocationUpdatedEvent(thePlayer, thePlayer.location()));
                });
    }

    @Override
    public void onWaypointCreated(Collar collar, LocationApi locationApi, Group group, Waypoint waypoint) {
        String message;
        if (group == null) {
            message = String.format("Waypoint %s created", waypoint.name);
        } else {
            message = String.format("Waypoint %s created in %s %s", waypoint.name, group.type.name, group.name);
        }
        plastic.display.displayStatusMessage(message);
        plastic.display.displayInfoMessage(message);
        events.dispatch(new WaypointCreatedEvent(waypoint));
    }

    @Override
    public void onWaypointRemoved(Collar collar, LocationApi locationApi, Group group, Waypoint waypoint) {
        String message;
        if (group == null) {
            message = String.format("Waypoint %s removed", waypoint.name);
        } else {
            message = String.format("Waypoint %s removed from %s %s", waypoint.name, group.type.name, group.name);
        }
        plastic.display.displayStatusMessage(message);
        plastic.display.displayInfoMessage(message);
        events.dispatch(new WaypointDeletedEvent(waypoint));
    }

    @Override
    public void onPrivateWaypointsReceived(Collar collar, LocationApi locationApi, Set<Waypoint> privateWaypoints) {
        privateWaypoints.forEach(waypoint -> events.dispatch(new WaypointCreatedEvent(waypoint)));
    }

    @Override
    public void onStartedSharingLocation(Collar collar, LocationApi locationApi, Group group) {
        plastic.display.displayInfoMessage(String.format("Started sharing location with %s", group.name));
    }

    @Override
    public void onStoppedSharingLocation(Collar collar, LocationApi locationApi, Group group) {
        plastic.display.displayInfoMessage(String.format("Stopped sharing location with %s", group.name));
    }
}
