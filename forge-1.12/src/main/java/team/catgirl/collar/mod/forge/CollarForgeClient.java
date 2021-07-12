package team.catgirl.collar.mod.forge;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import team.catgirl.collar.client.CollarListener;
import team.catgirl.collar.mod.common.CollarService;
import team.catgirl.collar.mod.common.features.messaging.Messages;
import team.catgirl.collar.mod.common.commands.Commands;
import team.catgirl.collar.mod.common.plastic.CollarTextureProvider;
import team.catgirl.collar.mod.common.plugins.Plugins;
import team.catgirl.collar.mod.forge.client.ForgePlugins;
import team.catgirl.plastic.Plastic;
import team.catgirl.plastic.chat.ChatService;
import team.catgirl.plastic.forge.ForgeCommand;
import team.catgirl.plastic.forge.ForgePlastic;
import team.catgirl.pounce.EventBus;

@SideOnly(Side.CLIENT)
@Mod(modid = CollarForgeClient.MODID, name = CollarForgeClient.NAME, version = CollarForgeClient.VERSION)
public class CollarForgeClient implements CollarListener
{
    public static final String MODID = "team.catgirl.collar";
    public static final String NAME = "Collar";
    public static final String VERSION = "0.1";

    private static final Plugins PLUGINS = new ForgePlugins();
    private static Plastic PLASTIC;
    public static final EventBus EVENT_BUS = new EventBus(Runnable::run);

    private CollarService collarService;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        CollarTextureProvider textureProvider = new CollarTextureProvider();
        EVENT_BUS.subscribe(textureProvider);
        PLASTIC = new ForgePlastic(textureProvider, EVENT_BUS);
        collarService = new CollarService(PLASTIC, EVENT_BUS, PLUGINS);
        // Setup the command system
        CommandDispatcher<CollarService> dispatcher = new CommandDispatcher<>();
        Messages messages = new Messages(Plastic.getPlastic(), collarService);
        Commands<CollarService> commands = new Commands<>(collarService, messages, PLASTIC, false);
        commands.register(dispatcher);
        ClientCommandHandler.instance.registerCommand(new ForgeCommand<>("collar", collarService, dispatcher));
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        PLASTIC.onTick();
    }

    @SubscribeEvent
    public void onWorldLoaded(WorldEvent.Load load) {
        PLASTIC.world.onWorldLoaded();
    }

    @SubscribeEvent
    public void connected(ClientConnectedToServerEvent connected) {
        PLASTIC.onClientConnected();
    }

    @SubscribeEvent
    public void disconnected(ClientDisconnectionFromServerEvent disconnection) {
        PLASTIC.onClientDisconnected();
    }

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.getEntityPlayer();
        if(player.getGameProfile() != null) PLASTIC.world.onPlayerRender(player.getGameProfile().getId());
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatEvent event) {
        // if it looks like a command, don't intercept it
        if (event.getMessage().startsWith("/")) {
            return;
        }
        ChatService chatService = Plastic.getPlastic().world.chatService;
        if (chatService.onChatMessageSent(event.getMessage())) {
            event.setCanceled(true);
        }
    }
}
