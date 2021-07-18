package com.collarmc.mod.forge.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import com.collarmc.mod.common.CollarService;
import com.collarmc.mod.common.commands.Commands;
import com.collarmc.mod.common.features.messaging.Messages;
import com.collarmc.mod.common.plastic.CollarTextureProvider;
import com.collarmc.mod.common.plugins.Plugins;
import com.collarmc.mod.forge.client.commands.ClientCommands;
import com.collarmc.mod.forge.client.commands.ICommandSource;
import com.collarmc.collar.mod.glue.render.TracerRenderer;
import com.collarmc.collar.mod.glue.render.WaypointRenderer;
import com.collarmc.collar.plastic.GluePlastic;
import com.collarmc.plastic.Plastic;
import com.collarmc.pounce.EventBus;
import java.util.logging.Logger;

@OnlyIn(Dist.CLIENT)
@Mod("collar-forge")
public class CollarForge {

    public static final Logger LOGGER = Logger.getLogger("Collar");

    private static final Plugins PLUGINS = new ForgePlugins();
    private static final EventBus EVENT_BUS = new EventBus(Runnable::run);
    private static final Plastic PLASTIC = new GluePlastic(new CollarTextureProvider(), EVENT_BUS);
    private static final CollarService COLLAR_SERVICE = new CollarService(PLASTIC, EVENT_BUS, PLUGINS);
    private static final WaypointRenderer WAYPOINT_RENDERER = new WaypointRenderer(PLASTIC, COLLAR_SERVICE);
    private static final TracerRenderer TRACER_RENDERER = new TracerRenderer(PLASTIC, COLLAR_SERVICE);
    private static final Messages GROUP_CHAT_SERVICE = new Messages(PLASTIC, COLLAR_SERVICE);

    public CollarForge(){
        init();
    }


    public void init() {
        //Forge does not support client-side commands...
        //https://github.com/MinecraftForge/MinecraftForge/pull/7754 PR to support client commands

        //We still CAN use mixins...
        Commands<ICommandSource> commands = new Commands<>(COLLAR_SERVICE, GROUP_CHAT_SERVICE, PLASTIC, true);
        commands.register(ClientCommands.DISPATCHER);

        //TODO Commands idk, somehow
        EVENT_BUS.subscribe(WAYPOINT_RENDERER);
        EVENT_BUS.subscribe(TRACER_RENDERER);
    }


    //WE DON'T subscribe to events like tick, these are already applied from mixins...
    //glue mixins will apply for both Forge and Fabric. And yes, Forge 1.15.2+ supports mixins, they just not documented it.
}
