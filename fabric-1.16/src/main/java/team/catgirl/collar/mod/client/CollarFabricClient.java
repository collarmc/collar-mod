package team.catgirl.collar.mod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import team.catgirl.collar.mod.FabricPlugins;
import team.catgirl.collar.mod.common.CollarService;
import team.catgirl.collar.mod.common.commands.Commands;
import team.catgirl.collar.mod.common.plastic.CollarTextureProvider;
import team.catgirl.collar.mod.common.plugins.Plugins;
import team.catgirl.collar.mod.render.TracerRenderer;
import team.catgirl.collar.mod.render.WaypointRenderer;
import team.catgirl.plastic.Plastic;
import team.catgirl.plastic.fabric.FabricPlastic;
import team.catgirl.pounce.EventBus;

@Environment(EnvType.CLIENT)
public class CollarFabricClient implements ClientModInitializer {

    private static final Plugins PLUGINS = new FabricPlugins();
    private static final EventBus EVENT_BUS = new EventBus(Runnable::run);
    private static final Plastic PLASTIC = new FabricPlastic(new CollarTextureProvider(), EVENT_BUS);
    private static final CollarService COLLAR_SERVICE = new CollarService(PLASTIC, EVENT_BUS, PLUGINS);
    private static final WaypointRenderer WAYPOINT_RENDERER = new WaypointRenderer(PLASTIC, COLLAR_SERVICE);
    private static final TracerRenderer TRACER_RENDERER = new TracerRenderer(PLASTIC, COLLAR_SERVICE);

    @Override
    public void onInitializeClient() {
        Commands<FabricClientCommandSource> commands = new Commands<>(COLLAR_SERVICE, PLASTIC, true);
        commands.register(ClientCommandManager.DISPATCHER);
        EVENT_BUS.subscribe(WAYPOINT_RENDERER);
        EVENT_BUS.subscribe(TRACER_RENDERER);
    }
}
