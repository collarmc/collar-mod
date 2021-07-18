package com.collarmc.mod.forge.journeymap;

import com.collarmc.api.location.Location;
import com.collarmc.api.textures.TextureType;
import com.collarmc.mod.common.CollarService;
import com.collarmc.mod.common.events.CollarDisconnectedEvent;
import com.collarmc.mod.common.features.events.PlayerLocationUpdatedEvent;
import com.collarmc.mod.common.features.events.WaypointCreatedEvent;
import com.collarmc.mod.common.features.events.WaypointRemovedEvent;
import com.collarmc.mod.forge.CollarForgeClient;
import com.collarmc.mod.forge.Utils;
import com.collarmc.plastic.Plastic;
import com.collarmc.plastic.player.Player;
import com.collarmc.pounce.Preference;
import com.collarmc.pounce.Subscribe;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.display.Waypoint;
import journeymap.client.api.display.WaypointGroup;
import journeymap.client.api.model.MapImage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class JourneyMapService {

    private static final Logger LOGGER = LogManager.getLogger(JourneyMapService.class);

    private IClientAPI api;
    private final CollarService collarService;
    private final Plastic plastic;
    private final Map<UUID, MarkerOverlay> playerMarkers = new HashMap<>();
    private final Map<UUID, Waypoint> waypoints = new HashMap<>();
    private final Map<UUID, WaypointGroup> groups = new HashMap<>();

    public JourneyMapService(CollarService collarService, Plastic plastic) {
        this.collarService = collarService;
        this.plastic = plastic;
    }

    @Subscribe(Preference.CALLER)
    public void disconnected(CollarDisconnectedEvent e) {
        if (api == null) {
            return;
        }
        reset();
    }

    @Subscribe(Preference.CALLER)
    public void setClientAPI(IClientAPI api) {
        this.api = api;
        reset();
    }

    @Subscribe(Preference.POOL)
    public void waypointCreated(WaypointCreatedEvent event) {
        if (api == null) {
            return;
        }
        int dimensionId = Utils.getDimensionId(event.waypoint.location);
        BlockPos pos = Utils.getBlockPos(event.waypoint.location);
        Waypoint waypoint = waypoints.get(event.waypoint.id);
        if (waypoint == null) {
            waypoint = new Waypoint(CollarForgeClient.MODID, event.waypoint.id.toString(), event.waypoint.name, dimensionId, pos);
        } else {
            waypoint.setName(event.waypoint.name);
            waypoint.setPosition(dimensionId, pos);
        }
        waypoint.setGroup(findOrCreateWaypointGroup(event));
        try {
            api.show(waypoint);
        } catch (Exception e) {
            LOGGER.error("Could not display waypoint " + event.waypoint);
        }
        waypoints.put(event.waypoint.id, waypoint);
    }

    @Subscribe(Preference.POOL)
    public void waypointRemoved(WaypointRemovedEvent event) {
        if (api == null) {
            return;
        }
        Waypoint waypoint = waypoints.remove(event.waypoint.id);
        if (waypoint != null) {
            api.remove(waypoint);
        }
    }

    @Subscribe(Preference.POOL)
    public void playerLocationUpdated(PlayerLocationUpdatedEvent event) {
        if (api == null) {
            return;
        }
        if (event.location.equals(Location.UNKNOWN)) {
            MarkerOverlay marker = playerMarkers.remove(event.player.id());
            if (marker != null) {
                api.remove(marker);
            }
        } else {
            collarService.with(collar -> {
                // Find the player
                collar.identities().resolvePlayer(event.player.id()).thenAccept(player -> {
                    // Resolve the texture
                    player.ifPresent(value -> collar.textures().playerTextureFuture(value, TextureType.AVATAR).thenAccept(optionalTexture -> {
                        optionalTexture.ifPresent(texture -> {
                            // Get the image
                            texture.loadImage(avatar -> {
                                avatar.ifPresent(bufferedImage -> {
                                    displayMarker(event.player, event.location, bufferedImage);
                                });
                            });
                        });
                    }));
                });
            });
        }
    }

    private WaypointGroup findOrCreateWaypointGroup(WaypointCreatedEvent event) {
        UUID id = event.group == null ? plastic.world.currentPlayer().id() : event.group.id;
        String name = event.group == null ? plastic.world.currentPlayer().name() : event.group.name;
        WaypointGroup waypointGroup = groups.get(id);
        if (waypointGroup == null) {
            waypointGroup = new WaypointGroup(CollarForgeClient.MODID, name);
        } else {
            waypointGroup.setName(name);
        }
        groups.put(id, waypointGroup);
        try {
            api.show(waypointGroup);
        } catch (Exception e) {
            LOGGER.error("Could not display waypoint group" + name);
        }
        return waypointGroup;
    }

    private void displayMarker(Player player, Location location, BufferedImage avatar) {
        BlockPos pos = Utils.getBlockPos(location);
        MapImage icon = new MapImage(avatar);
        MarkerOverlay markerOverlay = new MarkerOverlay(CollarForgeClient.MODID, player.id().toString(), pos, icon);
        markerOverlay.setLabel(player.name());
        markerOverlay.setDimension(Utils.getDimensionId(location));
        try {
            api.show(markerOverlay);
        } catch (Exception e) {
            LOGGER.error("Could not display marker for player " + player);
        }
        playerMarkers.put(player.id(), markerOverlay);
    }

    private void reset() {
        playerMarkers.values().forEach(markerOverlay -> api.remove(markerOverlay));
        waypoints.values().forEach(markerOverlay -> api.remove(markerOverlay));
    }
}
