package com.collarmc.mod.fabric.client;

import com.collarmc.client.plugin.Plugins;
import com.collarmc.mod.common.CollarService;
import com.collarmc.mod.common.commands.Commands;
import com.collarmc.mod.common.events.CollarModInitializedEvent;
import com.collarmc.mod.common.features.messaging.Messages;
import com.collarmc.mod.common.plastic.*;
import com.collarmc.mod.glue.render.TracerRenderer;
import com.collarmc.mod.glue.render.WaypointRenderer;
import com.collarmc.plastic.Plastic;
import com.collarmc.plastic.FabricPlastic;
import com.collarmc.pounce.EventBus;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class CollarFabricClient implements ClientModInitializer {

    private static final Logger LOGGER = LogManager.getLogger(CollarFabricClient.class.getName());
    private static final EventBus EVENT_BUS = new EventBus(Runnable::run);
    private static final Plugins PLUGINS = new Plugins();
    private  static final CollarTextureProvider COLLAR_TEXTURE_PROVIDER = new CollarTextureProvider(EVENT_BUS);
    private static final Plastic PLASTIC = new FabricPlastic(COLLAR_TEXTURE_PROVIDER, EVENT_BUS);
    private static final CollarService COLLAR_SERVICE = new CollarService(PLASTIC, EVENT_BUS, PLUGINS);
    private static final WaypointRenderer WAYPOINT_RENDERER = new WaypointRenderer(PLASTIC, COLLAR_SERVICE);
    private static final TracerRenderer TRACER_RENDERER = new TracerRenderer(PLASTIC, COLLAR_SERVICE);
    private static final Messages GROUP_CHAT_SERVICE = new Messages(PLASTIC, COLLAR_SERVICE);

    private static MinecraftClient mc;
    @Override
    public void onInitializeClient() {
        mc = MinecraftClient.getInstance();
        LOGGER.info("CollarFabricClient initialization...");
        Commands<FabricClientCommandSource> commands = new Commands<>(COLLAR_SERVICE, GROUP_CHAT_SERVICE, PLASTIC, true);
        LOGGER.info("CollarFabricClient commands initialization...");
        // commands.register(ClientCommandManager.getActiveDispatcher());
        ClientCommandRegistrationCallback.EVENT.register(CollarFabricClient::registerCommands);
        LOGGER.info("CollarFabricClient commands registered");

        if (mc.getCurrentServerEntry()!=null && mc.getCurrentServerEntry().address!=null)
            LOGGER.info("CollarFabricClient current server address: " + mc.getCurrentServerEntry().address);
        else
            LOGGER.info("CollarFabricClient current server address is not provided");

        EVENT_BUS.subscribe(WAYPOINT_RENDERER);
        EVENT_BUS.subscribe(TRACER_RENDERER);
        EVENT_BUS.dispatch(new CollarModInitializedEvent());
        try {
            PLUGINS.loadPlugins(CollarFabricClient.class.getClassLoader(), EVENT_BUS);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        Commands<FabricClientCommandSource> commands = new Commands<>(COLLAR_SERVICE, GROUP_CHAT_SERVICE, PLASTIC, true);
        commands.register(dispatcher);
    }
}
