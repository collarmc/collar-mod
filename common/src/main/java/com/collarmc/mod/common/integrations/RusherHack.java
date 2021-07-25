package com.collarmc.mod.common.integrations;

import com.collarmc.api.groups.Group;
import com.collarmc.api.waypoints.Waypoint;
import com.collarmc.libs.org.fasterxml.jackson.core.type.TypeReference;
import com.collarmc.mod.common.features.events.WaypointCreatedEvent;
import com.collarmc.mod.common.features.events.WaypointRemovedEvent;
import com.collarmc.plastic.Plastic;
import com.collarmc.pounce.Preference;
import com.collarmc.pounce.Subscribe;
import com.collarmc.utils.Utils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class RusherHack {

    private static final String WAYPOINTS_COMMAND = "waypoints";
    private static final String DEFAULT_PREFIX = "*";
    private static final boolean loaded;

    static {
        boolean found;
        try {
            RusherHack.class.getClassLoader().loadClass("org.rusherhack.client.RusherHack");
            found = true;
        } catch (ClassNotFoundException ignored) {
            found = false;
        }
        loaded = found;
    }

    private final Plastic plastic;

    public RusherHack(Plastic plastic) {
        this.plastic = plastic;
    }

    @Subscribe(Preference.CALLER)
    public void onWaypointCreated(WaypointCreatedEvent e) {
        if (!loaded) {
            return;
        }
        plastic.world.currentPlayer().send(String.format(
                "%s%s add \"%s\" %s %s %s",
                prefix(),
                "waypoints",
                name(e.waypoint, e.group),
                e.waypoint.location.x,
                e.waypoint.location.y,
                e.waypoint.location.z)
        );
    }

    @Subscribe(Preference.CALLER)
    public void onWaypointRemoved(WaypointRemovedEvent e) {
        if (!loaded) {
            return;
        }
        plastic.world.currentPlayer().send(String.format("%s%s remove \"%s\"", prefix(), WAYPOINTS_COMMAND, name(e.waypoint, e.group)));
    }

    public static boolean isLoaded() {
        return loaded;
    }

    private static String name(Waypoint waypoint, Group group) {
        return group == null ? waypoint.name : group.name + " - " + waypoint.name;
    }

    private String prefix() {
        File file = new File(plastic.home(), "rusherhack/prefix.json");
        if (file.exists()) {
            List<PrefixFile> prefixFile;
            try {
                prefixFile = Utils.jsonMapper().readValue(file, new TypeReference<List<PrefixFile>>() { });
            } catch (IOException e) {
                return DEFAULT_PREFIX;
            }
            return prefixFile.stream().findFirst().map(config -> config.prefix).orElse(DEFAULT_PREFIX);
        }
        return DEFAULT_PREFIX;
    }

    private static final class PrefixFile {
        public final String prefix;

        public PrefixFile(@JsonProperty("prefix") String prefix) {
            this.prefix = prefix;
        }
    }
}
