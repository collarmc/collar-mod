package com.collarmc.mod.common.integrations;

import com.collarmc.plastic.Plastic;
import com.collarmc.pounce.EventBus;

/**
 * Client integrations
 */
public final class Integrations {

    public final RusherHack rusherHack;
    public final FutureClient futureClient;

    public Integrations(Plastic plastic, EventBus eventBus) {
        rusherHack = new RusherHack(plastic, eventBus);
        futureClient = new FutureClient(plastic, eventBus);
    }
}
