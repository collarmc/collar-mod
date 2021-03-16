package team.catgirl.collar.mod.integrations.journeymap;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.Waypoint;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.model.MapImage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import team.catgirl.collar.mod.features.events.PlayerLocationUpdatedEvent;
import team.catgirl.collar.mod.features.events.WaypointCreatedEvent;
import team.catgirl.collar.mod.features.events.WaypointDeletedEvent;
import team.catgirl.collar.mod.forge.CollarMod;
import team.catgirl.collar.mod.service.events.CollarConnectedEvent;
import team.catgirl.collar.mod.service.events.CollarDisconnectedEvent;
import team.catgirl.events.Subscribe;
import team.catgirl.plastic.Plastic;
import team.catgirl.plastic.player.Player;
import team.catgirl.plastic.world.Dimension;
import team.catgirl.plastic.world.Position;

import java.awt.image.BufferedImage;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@journeymap.client.api.ClientPlugin
public class JourneyMapMod implements IClientPlugin {

    private final Plastic plastic = Plastic.getPlastic();
    private IClientAPI journeyMap;

    private final ConcurrentHashMap<UUID, Waypoint> privateWaypoints = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Player, Waypoint> playerWaypoints = new ConcurrentHashMap<>();

    @Override
    public void initialize(IClientAPI journeyMap) {
        this.journeyMap = journeyMap;
        CollarMod.EVENT_BUS.subscribe(this);
    }

    @Override
    public String getModId() {
        return CollarMod.MODID;
    }

    @Override
    public void onEvent(ClientEvent event) {}

    @Subscribe
    public void onPlayerLocationUpdated(PlayerLocationUpdatedEvent event) {
        if (!event.player.id().equals(plastic.world.currentPlayer().id())) {
            Waypoint removed = playerWaypoints.remove(event.player);
            if (removed != null) {
                journeyMap.remove(removed);
            }
            if (!event.position.equals(Position.UNKNOWN)) {
                BufferedImage icon = event.player.avatar().orElse(null);
                Waypoint playerWaypoint = waypointFrom(event.player.name(), icon, event.position, event.dimension);
                playerWaypoints.put(event.player, playerWaypoint);
                show(playerWaypoint);
            }
        }
    }

    @Subscribe
    public void onConnected(CollarConnectedEvent event) {
        journeyMap.removeAll(CollarMod.MODID);
    }

    @Subscribe
    public void onDisconnected(CollarDisconnectedEvent event) {
        journeyMap.removeAll(CollarMod.MODID);
    }

    @Subscribe
    public void onWaypointCreated(WaypointCreatedEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append(event.name);
        if (event.groupName != null) {
            sb.append(" (");
            sb.append(event.groupName);
            sb.append(")");
        }
        Waypoint playerWaypoint = waypointFrom(sb.toString(), null, event.position, event.dimension);
        privateWaypoints.put(event.id, playerWaypoint);
        show(playerWaypoint);
    }

    @Subscribe
    public void onWaypointDeleted(WaypointDeletedEvent event) {
        Waypoint removed = privateWaypoints.remove(event.id);
        if (removed != null) {
            journeyMap.remove(removed);
        }
    }

    private void show(journeymap.client.api.display.Waypoint playerWaypoint) {
        try {
            journeyMap.show(playerWaypoint);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Waypoint waypointFrom(String name, BufferedImage icon, Position position, Dimension dimension) {
        int dimensionId;
        switch (dimension) {
            case OVERWORLD:
                dimensionId = DimensionType.OVERWORLD.getId();
                break;
            case END:
                dimensionId = DimensionType.THE_END.getId();
                break;
            case NETHER:
                dimensionId = DimensionType.NETHER.getId();
                break;
            default:
                throw new IllegalStateException("could not get dimension id of " + dimension);
        }
        Waypoint waypoint = new Waypoint(
                CollarMod.MODID,
                name,
                dimensionId,
                new BlockPos(position.x, position.y, position.z)
        );
        if (icon != null) {
            waypoint.setIcon(new MapImage(icon));
        }
        return waypoint;
    }
}
