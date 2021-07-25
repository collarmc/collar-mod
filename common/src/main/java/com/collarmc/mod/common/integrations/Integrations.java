package com.collarmc.mod.common.integrations;

import com.collarmc.plastic.Plastic;
import com.collarmc.pounce.EventBus;

public final class Integrations {

    public final RusherHack rusherHack;
    public final FutureClient futureClient;

    public Integrations(Plastic plastic, EventBus eventBus) {
        if (RusherHack.isLoaded()) {
            rusherHack = new RusherHack(plastic);
            eventBus.subscribe(rusherHack);
        } else {
            rusherHack = null;
        }
        if (FutureClient.isLoaded()) {
            futureClient = new FutureClient(plastic);
            eventBus.subscribe(futureClient);
        } else {
            futureClient = null;
        }
    }
}
