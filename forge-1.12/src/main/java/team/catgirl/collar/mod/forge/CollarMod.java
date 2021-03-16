package team.catgirl.collar.mod.forge;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;
import team.catgirl.collar.client.CollarListener;
import team.catgirl.collar.client.minecraft.Ticks;
import team.catgirl.collar.mod.commands.Commands;
import team.catgirl.collar.mod.plastic.CollarTextureProvider;
import team.catgirl.collar.mod.plugins.Plugins;
import team.catgirl.collar.mod.service.CollarService;
import team.catgirl.events.EventBus;
import team.catgirl.events.Subscribe;
import team.catgirl.plastic.Plastic;
import team.catgirl.plastic.events.LoadPlayerTexturesEvent;
import team.catgirl.plastic.forge.ForgePlastic;
import team.catgirl.plastic.ui.TextureProvider;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
@Mod(modid = CollarMod.MODID, name = CollarMod.NAME, version = CollarMod.VERSION)
public class CollarMod implements CollarListener
{
    public static final String MODID = "team.catgirl.collar";
    public static final String NAME = "Collar";
    public static final String VERSION = "0.1";

    private static Logger logger;
    private static final Ticks TICKS = new Ticks();
    private static boolean isWorldLoaded = true;
    private static boolean isConnectedToServer = false;
    private static final Plugins PLUGINS = new ForgePlugins();
    private static Plastic PLASTIC;
    public static final EventBus EVENT_BUS = new EventBus();

    private CollarService collarService;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        CollarTextureProvider textureProvider = new CollarTextureProvider();
        EVENT_BUS.subscribe(textureProvider);
        PLASTIC = new ForgePlastic(textureProvider);
        collarService = new CollarService(PLASTIC, EVENT_BUS, TICKS, PLUGINS, logger);
        PLASTIC.commands.register("collar", collarService, new Commands(collarService, PLASTIC).create());
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        TICKS.onTick();
    }

    @SubscribeEvent
    public void onWorldLoaded(WorldEvent.Load load) {
        // Only start collar when the world is loaded and the server is connected
        if (!isWorldLoaded && isConnectedToServer) {
            collarService.connect();
        }
        isWorldLoaded = true;
    }

    @SubscribeEvent
    public void connected(ClientConnectedToServerEvent connected) {
        isConnectedToServer = true;
    }

    @SubscribeEvent
    public void disconnected(ClientDisconnectionFromServerEvent disconnection) {
        isConnectedToServer = false;
        isWorldLoaded = false;
        // Stop collar and reset state
        if (collarService != null) {
            collarService.disconnect();
        }
    }

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.getEntityPlayer();
        UUID uuid = player.getUniqueID();
        PLASTIC.world.allPlayers().stream()
                .filter(candidate -> candidate.id().equals(uuid)).findFirst()
                .ifPresent(team.catgirl.plastic.player.Player::onRender);
    }
}
