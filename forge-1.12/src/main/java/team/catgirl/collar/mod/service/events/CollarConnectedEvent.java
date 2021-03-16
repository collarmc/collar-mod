package team.catgirl.collar.mod.service.events;

import team.catgirl.collar.client.Collar;
import team.catgirl.event.Event;

public class CollarConnectedEvent extends Event {
    /**
     * The Collar client
     */
    public final Collar collar;

    public CollarConnectedEvent(Collar collar) {
        this.collar = collar;
    }
}
