package team.catgirl.collar.mod.common.events;

import team.catgirl.collar.client.Collar;

public class CollarConnectedEvent {
    /**
     * The Collar client
     */
    public final Collar collar;

    public CollarConnectedEvent(Collar collar) {
        this.collar = collar;
    }
}
