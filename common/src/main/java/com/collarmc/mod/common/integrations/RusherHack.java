package com.collarmc.mod.common.integrations;

import com.collarmc.libs.org.fasterxml.jackson.core.type.TypeReference;
import com.collarmc.plastic.Plastic;
import com.collarmc.pounce.EventBus;
import com.collarmc.utils.Utils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class RusherHack extends AbstractWaypointCommandIntegration {

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

    public RusherHack(Plastic plastic, EventBus eventBus) {
        super(plastic, eventBus);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    protected String prefix() {
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

    @Override
    protected String waypointsCommand() {
        return WAYPOINTS_COMMAND;
    }

    private static final class PrefixFile {
        public final String prefix;

        public PrefixFile(@JsonProperty("prefix") String prefix) {
            this.prefix = prefix;
        }
    }
}
