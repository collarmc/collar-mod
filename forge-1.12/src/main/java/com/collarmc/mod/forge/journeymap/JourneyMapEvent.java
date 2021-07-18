package com.collarmc.mod.forge.journeymap;

import journeymap.client.api.IClientAPI;

public final class JourneyMapEvent {
    public final IClientAPI api;

    public JourneyMapEvent(IClientAPI api) {
        this.api = api;
    }
}
