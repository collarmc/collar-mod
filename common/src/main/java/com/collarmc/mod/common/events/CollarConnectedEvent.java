package com.collarmc.mod.common.events;

import com.collarmc.client.Collar;

public class CollarConnectedEvent {
    /**
     * The Collar client
     */
    public final Collar collar;

    public CollarConnectedEvent(Collar collar) {
        this.collar = collar;
    }
}
