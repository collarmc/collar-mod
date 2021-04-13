package team.catgirl.collar.mod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;
import team.catgirl.collar.client.minecraft.Ticks;
import team.catgirl.collar.mod.FabricPlugins;
import team.catgirl.collar.mod.common.CollarService;
import team.catgirl.collar.mod.common.plugins.Plugins;
import team.catgirl.collar.mod.events.ClientDisconnectCallback;
import team.catgirl.collar.mod.events.ClientConnectCallback;
import team.catgirl.collar.mod.events.WorldLoadedCallback;
import team.catgirl.plastic.Plastic;
import team.catgirl.plastic.fabric.FabricPlastic;
import team.catgirl.pounce.EventBus;

@Environment(EnvType.CLIENT)
public class CollarFabricClient implements ClientModInitializer {

    private static final Ticks TICKS = new Ticks();
    private static final Plugins PLUGINS = new FabricPlugins();
    private static final Plastic PLASTIC = new FabricPlastic();
    private static final EventBus EVENT_BUS = new EventBus(Runnable::run);
    private static final CollarService COLLAR_SERVICE = new CollarService(PLASTIC, EVENT_BUS, TICKS, PLUGINS);

    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> TICKS.onTick());
        ClientConnectCallback.EVENT.register(() -> {
            COLLAR_SERVICE.connect();
            return ActionResult.PASS;
        });
        ClientDisconnectCallback.EVENT.register(() -> {
            COLLAR_SERVICE.disconnect();
            return ActionResult.PASS;
        });
        WorldLoadedCallback.EVENT.register(() -> {
            if (MinecraftClient.getInstance().getCurrentServerEntry() != null) {
                COLLAR_SERVICE.connect();
            }
            return ActionResult.PASS;
        });
    }
}
