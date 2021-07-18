package com.collarmc.mod.forge.journeymap;

import com.collarmc.api.CollarPlugin;
import com.collarmc.mod.common.events.CollarModInitializedEvent;
import com.collarmc.mod.forge.CollarForgeClient;
import com.collarmc.pounce.Preference;
import com.collarmc.pounce.Subscribe;
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

    public JourneyMap() {
        CollarForgeClient.EVENT_BUS.subscribe(this);
    }

    private IClientAPI api;

    @Override
    public void initialize(IClientAPI api) {
        this.api = api;
        CollarForgeClient.EVENT_BUS.dispatch(new JourneyMapEvent(api));
    }

    @Override
    public String getModId() {
        return CollarForgeClient.MODID;
    }

    @Override
    public void onEvent(ClientEvent event) {
        CollarForgeClient.EVENT_BUS.dispatch(event);
    }

    @Subscribe(Preference.CALLER)
    public void onReady(CollarModInitializedEvent ignored) {
        if (api == null) return;
        CollarForgeClient.EVENT_BUS.dispatch(new JourneyMapEvent(api));
    }
}
