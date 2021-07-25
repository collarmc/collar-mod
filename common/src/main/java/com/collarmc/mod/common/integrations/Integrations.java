package com.collarmc.mod.common.integrations;

import com.collarmc.plastic.Plastic;
import com.collarmc.pounce.EventBus;

public final class Integrations {

    public final RusherHack rusherHack;

    public Integrations(Plastic plastic, EventBus eventBus) {
        if (RusherHack.isLoaded()) {
            rusherHack = new RusherHack(plastic);
            eventBus.subscribe(rusherHack);
        } else {
            rusherHack = null;
        }
    }
}
