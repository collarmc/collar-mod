package com.collarmc.mod.forge.journeymap;

import com.collarmc.api.CollarPlugin;
import com.collarmc.mod.forge.CollarForgeClient;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Forwards JourneyMap client api and events to the event bus
 */
@ParametersAreNonnullByDefault
@journeymap.client.api.ClientPlugin
public class JourneyMap implements IClientPlugin, CollarPlugin {

    @Override
    public void initialize(IClientAPI api) {
        CollarForgeClient.EVENT_BUS.dispatch(api);
    }

    @Override
    public String getModId() {
        return CollarForgeClient.MODID;
    }

    @Override
    public void onEvent(ClientEvent event) {
        CollarForgeClient.EVENT_BUS.dispatch(event);
    }
}
