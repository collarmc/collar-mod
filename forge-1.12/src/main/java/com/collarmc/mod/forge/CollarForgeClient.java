package com.collarmc.mod.forge;

import com.collarmc.client.CollarListener;
import com.collarmc.mod.forge.journeymap.JourneyMapService;
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
import com.collarmc.mod.common.CollarService;
import com.collarmc.mod.common.features.messaging.Messages;
import com.collarmc.mod.common.commands.Commands;
import com.collarmc.mod.common.plastic.CollarTextureProvider;
import com.collarmc.mod.common.plugins.Plugins;
import com.collarmc.mod.forge.client.ForgePlugins;
import com.collarmc.plastic.Plastic;
import com.collarmc.plastic.chat.ChatService;
import com.collarmc.plastic.forge.ForgeCommand;
import com.collarmc.plastic.forge.ForgePlastic;
import com.collarmc.pounce.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
@Mod(modid = CollarForgeClient.MODID, name = CollarForgeClient.NAME, version = CollarForgeClient.VERSION)
public class CollarForgeClient implements CollarListener
{
    private static final Logger LOGGER = LogManager.getLogger(CollarForgeClient.class);

    public static final String MODID = "com.collarmc";
    public static final String NAME = "Collar";
    public static final String VERSION = "0.1";

    private static final Plugins PLUGINS = new ForgePlugins();
    private static Plastic PLASTIC;
    public static final EventBus EVENT_BUS = new EventBus(Runnable::run);

    private CollarService collarService;
    private JourneyMapService journeyMapService;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        // Setup collar
        CollarTextureProvider textureProvider = new CollarTextureProvider();
        EVENT_BUS.subscribe(textureProvider);
        PLASTIC = new ForgePlastic(textureProvider, EVENT_BUS);
        collarService = new CollarService(PLASTIC, EVENT_BUS, PLUGINS);
        // Journey Map integration
        initJourneyMap();
        // Setup command system
        CommandDispatcher<CollarService> dispatcher = new CommandDispatcher<>();
        Messages messages = new Messages(Plastic.getPlastic(), collarService);
        Commands<CollarService> commands = new Commands<>(collarService, messages, PLASTIC, false);
        commands.register(dispatcher);
        ClientCommandHandler.instance.registerCommand(new ForgeCommand<>("collar", collarService, dispatcher));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {}

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
        PLASTIC.world.onPlayerRender(player.getGameProfile().getId());
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

    private void initJourneyMap() {
        try {
            journeyMapService = new JourneyMapService(collarService, PLASTIC);
            EVENT_BUS.subscribe(journeyMapService);
            LOGGER.info("JourneyMap available.");
        } catch (NoClassDefFoundError e) {
            LOGGER.warn("JourneyMap not available.");
        }
    }
}
