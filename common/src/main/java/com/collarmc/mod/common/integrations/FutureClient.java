package com.collarmc.mod.common.integrations;

import com.collarmc.plastic.Plastic;
import com.collarmc.pounce.EventBus;

public final class FutureClient extends AbstractWaypointCommandIntegration {

    private static final String WAYPOINTS_COMMAND = "waypoints";
    private static final String DEFAULT_PREFIX = ".";
    private static final boolean loaded;

    static {
        boolean found;
        try {
            FutureClient.class.getClassLoader().loadClass("net.futureclient.client.A");
            found = true;
        } catch (ClassNotFoundException ignored) {
            found = false;
        }
        loaded = found;
    }

    public FutureClient(Plastic plastic, EventBus eventBus) {
        super(plastic, eventBus);
    }

    @Override
    protected String waypointsCommand() {
        return WAYPOINTS_COMMAND;
    }

    @Override
    protected String addCommand() {
        return "add";
    }

    @Override
    protected String removeCommand() {
        return "del";
    }

    @Override

    public boolean isLoaded() {
        return loaded;
    }

    @Override
    protected String prefix() {
        // TODO: find the prefix in future config
        return DEFAULT_PREFIX;
    }
}
