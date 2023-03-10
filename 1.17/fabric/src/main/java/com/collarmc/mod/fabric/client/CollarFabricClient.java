package com.collarmc.mod.fabric.client;

import com.collarmc.client.plugin.Plugins;
import com.collarmc.mod.common.events.CollarModInitializedEvent;
import com.collarmc.mod.glue.render.TracerRenderer;
import com.collarmc.mod.glue.render.WaypointRenderer;
import com.collarmc.plastic.GluePlastic;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import com.collarmc.mod.common.CollarService;
import com.collarmc.mod.common.features.messaging.Messages;
import com.collarmc.mod.common.commands.Commands;
import com.collarmc.mod.common.plastic.*;
import com.collarmc.plastic.Plastic;
import com.collarmc.pounce.EventBus;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class CollarFabricClient implements ClientModInitializer {

    private static final EventBus EVENT_BUS = new EventBus(Runnable::run);
    private static final Plugins PLUGINS = new Plugins();
    private static final Plastic PLASTIC = new GluePlastic(new CollarTextureProvider(EVENT_BUS), EVENT_BUS);
    private static final CollarService COLLAR_SERVICE = new CollarService(PLASTIC, EVENT_BUS, PLUGINS);
    private static final WaypointRenderer WAYPOINT_RENDERER = new WaypointRenderer(PLASTIC, COLLAR_SERVICE);
    private static final TracerRenderer TRACER_RENDERER = new TracerRenderer(PLASTIC, COLLAR_SERVICE);
    private static final Messages GROUP_CHAT_SERVICE = new Messages(PLASTIC, COLLAR_SERVICE);

    @Override
    public void onInitializeClient() {
        Commands<FabricClientCommandSource> commands = new Commands<>(COLLAR_SERVICE, GROUP_CHAT_SERVICE, PLASTIC, true);
        commands.register(ClientCommandManager.DISPATCHER);
        EVENT_BUS.subscribe(WAYPOINT_RENDERER);
        EVENT_BUS.subscribe(TRACER_RENDERER);
        EVENT_BUS.dispatch(new CollarModInitializedEvent());
        try {
            PLUGINS.loadPlugins(CollarFabricClient.class.getClassLoader(), EVENT_BUS);
        }catch (IOException e){
            throw new IllegalStateException(e);
        }
    }
}
