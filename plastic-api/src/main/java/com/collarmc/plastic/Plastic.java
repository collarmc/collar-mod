package com.collarmc.plastic;

import com.collarmc.client.Collar;
import com.collarmc.plastic.events.client.ClientConnectedEvent;
import com.collarmc.plastic.events.client.ClientDisconnectedEvent;
import com.collarmc.plastic.events.client.OnTickEvent;
import com.collarmc.plastic.ui.Display;
import com.collarmc.plastic.world.World;
import com.collarmc.pounce.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Minecraft mod api abstraction
 */
public abstract class Plastic {
    protected abstract Logger getLogger();
    private static Plastic INSTANCE;

    /**
     * UI display
     */
    public final Display display;

    /**
     * The World
     */
    public final World world;

    /**
     * The event bus
     */
    public final EventBus eventBus;

    protected Plastic(Display display, World world, EventBus eventBus) {
        this.display = display;
        this.world = world;
        this.eventBus = eventBus;
        setPlastic(this);
    }

    /**
     * Minecraft home directory
     * @return home
     */
    public abstract File home();

    /**
     * Server address
     * @return serverIP or null if not connected
     */
    public abstract String serverAddress();

    /**
     * @return session id
     */
    public abstract String sessionId();

    /**
     * @return access token
     */
    public abstract String accessToken();

    /**
     * @return plastic instance
     */
    public static Plastic getPlastic() {
        return INSTANCE;
    }

    /**
     * @param plastic instance to set globally
     */
    private static void setPlastic(Plastic plastic) {
        if (INSTANCE != null) {
            throw new IllegalStateException("plastic instance is already set");
        }
        Plastic.INSTANCE = plastic;
    }

    /**
     * Fires {@link ClientConnectedEvent}
     */
    public final void onClientConnected() {
        eventBus.dispatch(new ClientConnectedEvent());
    }

    /**
     * Fires {@link ClientDisconnectedEvent}
     */
    public final void onClientDisconnected() {
        eventBus.dispatch(new ClientDisconnectedEvent());
    }

    /**
     * Fires {@link OnTickEvent}
     */
    public final void onTick() {
        eventBus.dispatch(new OnTickEvent());
    }

//    /**
//     * Fires {@link RenderOverlaysEvent}
//     */
//    public final void onRenderOverlays() {
//        eventBus.dispatch(new RenderOverlaysEvent());
//    }
}
