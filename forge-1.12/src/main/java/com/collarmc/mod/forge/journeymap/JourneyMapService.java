package com.collarmc.mod.forge.journeymap;

import com.collarmc.api.location.Location;
import com.collarmc.client.Collar;
import com.collarmc.client.api.location.events.LocationUpdatedEvent;
import com.collarmc.client.api.location.events.WaypointCreatedEvent;
import com.collarmc.client.api.location.events.WaypointRemovedEvent;
import com.collarmc.client.events.CollarStateChangedEvent;
import com.collarmc.mod.common.CollarService;
import com.collarmc.mod.forge.CollarForgeClient;
import com.collarmc.mod.forge.Utils;
import com.collarmc.plastic.Plastic;
import com.collarmc.plastic.player.Player;
import com.collarmc.pounce.Preference;
import com.collarmc.pounce.Subscribe;
import com.mojang.authlib.GameProfile;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.display.Waypoint;
import journeymap.client.api.display.WaypointGroup;
import journeymap.client.api.model.MapImage;
import journeymap.common.feature.PlayerRadarManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class JourneyMapService {

    private static final Logger LOGGER = LogManager.getLogger(JourneyMapService.class);

    private IClientAPI api;
    private final CollarService collarService;
    private final Plastic plastic;
    private final ConcurrentMap<UUID, MarkerOverlay> playerMarkers = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, Waypoint> waypoints = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, WaypointGroup> groups = new ConcurrentHashMap<>();

    public JourneyMapService(CollarService collarService, Plastic plastic) {
        this.collarService = collarService;
        this.plastic = plastic;
    }

    @Subscribe(Preference.CALLER)
    public void disconnected(CollarStateChangedEvent event) {
        if (event.state != Collar.State.DISCONNECTED || api == null) {
            return;
        }
        reset();
    }

    @Subscribe(Preference.CALLER)
    public void setClientAPI(JourneyMapEvent event) {
        this.api = event.api;
        reset();
    }

    private BufferedImage createMarker() {
        BufferedImage bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        g.setColor(Color.red);
        g.fillRect(0, 0, 16, 16);
        return bi;
    }

    @Subscribe(Preference.POOL)
    public void waypointCreated(WaypointCreatedEvent event) {
        if (api == null) {
            return;
        }
        int dimensionId = Utils.getDimensionId(event.waypoint.location);
        BlockPos pos = Utils.getBlockPos(event.waypoint.location);
        Waypoint waypoint = waypoints.compute(event.waypoint.id, (uuid, mapWaypoint) -> {
            if (mapWaypoint == null) {
                mapWaypoint = new Waypoint(CollarForgeClient.MODID, event.waypoint.id.toString(), event.waypoint.name, dimensionId, pos);
            }
            mapWaypoint.setName(event.waypoint.name);
            mapWaypoint.setPosition(dimensionId, pos);
            mapWaypoint.setGroup(findOrCreateWaypointGroup(event));
            return mapWaypoint;
        });
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
    public void locationUpdated(LocationUpdatedEvent event) {
        if (api == null) {
            return;
        }

        UUID playerId = event.player.minecraftPlayer.id;
        Player player = plastic.world.findPlayerById(playerId).orElseThrow(() -> new IllegalStateException("cannot find player " + playerId));
        PlayerRadarManager.getInstance().addPlayer(createEntityPlayer(player, event.location));

//        if (event.location.equals(Location.UNKNOWN)) {
//            MarkerOverlay marker = playerMarkers.remove(event.player.id());
//            if (marker != null) {
//                api.remove(marker);
//            }
//        } else {
//            displayMarker(event.player, event.location, createMarker());
////            event.player.avatar(bufferedImage -> displayMarker(event.player, event.location, bufferedImage));
//        }
    }

    /**
     * Try not to leave journey map in a dirty state when client quits
     */
    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::reset));
    }

    private WaypointGroup findOrCreateWaypointGroup(WaypointCreatedEvent event) {
        UUID id = event.group == null ? plastic.world.currentPlayer().id() : event.group.id;
        String name = event.group == null ? plastic.world.currentPlayer().name() : event.group.name;
        WaypointGroup waypointGroup = groups.compute(id, (uuid, group) -> {
            if (group == null) {
                group = new WaypointGroup(CollarForgeClient.MODID, name);
            }
            group.setName(name);
            return group;
        });
        try {
            api.show(waypointGroup);
        } catch (Exception e) {
            LOGGER.error("Could not display waypoint group" + name);
        }
        return waypointGroup;
    }

    private void displayMarker(Player player, Location location, BufferedImage avatar) {
        BlockPos pos = Utils.getBlockPos(location);
        MarkerOverlay markerOverlay = playerMarkers.compute(player.id(), (uuid, overlay) -> {
            if (overlay == null) {
                MapImage icon = new MapImage(avatar);
                overlay = new MarkerOverlay(CollarForgeClient.MODID, player.id().toString(), pos, icon);
            }
            overlay.setTitle(player.name());
            overlay.setPoint(pos);
            overlay.setDimension(Utils.getDimensionId(location));
            return overlay;
        });
        try {
            api.show(markerOverlay);
        } catch (Exception e) {
            LOGGER.error("Could not display marker for player " + player);
        }
    }

    public void reset() {
        if (api != null) {
            playerMarkers.values().forEach(markerOverlay -> api.remove(markerOverlay));
            waypoints.values().forEach(markerOverlay -> api.remove(markerOverlay));
            api.removeAll(CollarForgeClient.MODID);
        }
        playerMarkers.clear();
        waypoints.clear();
    }

    private static EntityOtherPlayerMP createEntityPlayer(Player player, Location location) {
        EntityOtherPlayerMP entityPlayer = new EntityOtherPlayerMP(Minecraft.getMinecraft().world, new GameProfile(player.id(), player.name()));
        entityPlayer.setPositionAndRotation(location.x, location.y, location.z, 0, 0);
        entityPlayer.setEntityId(player.networkId());
        entityPlayer.dimension = Utils.getDimensionId(location);
        entityPlayer.setUniqueId(player.id());
        entityPlayer.chunkCoordX = location.x.intValue() << 4;
        entityPlayer.chunkCoordY = location.y.intValue() << 4;
        entityPlayer.chunkCoordZ = location.z.intValue() << 4;
        entityPlayer.addedToChunk = true;
        entityPlayer.rotationYawHead = 0; // TODO: pass around player rotation
        entityPlayer.setSneaking(false);
        return entityPlayer;
    }
}
