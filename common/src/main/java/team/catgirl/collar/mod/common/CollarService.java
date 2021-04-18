package team.catgirl.collar.mod.common;

import team.catgirl.collar.api.entities.Entity;
import team.catgirl.collar.api.entities.EntityType;
import team.catgirl.collar.api.location.Dimension;
import team.catgirl.collar.api.location.Location;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.client.CollarConfiguration;
import team.catgirl.collar.client.CollarException;
import team.catgirl.collar.client.CollarListener;
import team.catgirl.collar.client.minecraft.Ticks;
import team.catgirl.collar.client.security.ClientIdentityStore;
import team.catgirl.collar.mod.common.features.*;
import team.catgirl.collar.mod.common.events.CollarConnectedEvent;
import team.catgirl.collar.mod.common.events.CollarDisconnectedEvent;
import team.catgirl.plastic.Plastic;
import team.catgirl.plastic.events.client.ClientConnectedEvent;
import team.catgirl.plastic.events.client.ClientDisconnectedEvent;
import team.catgirl.plastic.events.client.OnTickEvent;
import team.catgirl.plastic.events.world.WorldLoadedEvent;
import team.catgirl.plastic.player.Player;
import team.catgirl.plastic.ui.TextAction;
import team.catgirl.plastic.ui.TextBuilder;
import team.catgirl.plastic.ui.TextFormatting;
import team.catgirl.collar.mod.common.plugins.Plugins;
import team.catgirl.collar.security.mojang.MinecraftSession;
import team.catgirl.pounce.EventBus;
import team.catgirl.pounce.Preference;
import team.catgirl.pounce.Subscribe;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static team.catgirl.collar.client.Collar.State.*;

public class CollarService implements CollarListener {

    private static final Logger LOGGER = Logger.getLogger(CollarService.class.getName());

    private final ExecutorService backgroundJobs;
    private final ConnectionState connectionState = new ConnectionState(this);
    private Collar collar;
    private final Plastic plastic;
    private final EventBus eventBus;
    private final Ticks ticks;
    private final Plugins plugins;

    public final Locations locations;
    public final Friends friends;
    public final Messaging messaging;
    public final Textures textures;
    public final Groups groups;

    public CollarService(Plastic plastic, EventBus eventBus, Plugins plugins) {
        this.plastic = plastic;
        this.eventBus = eventBus;
        this.ticks = new Ticks();
        this.plugins = plugins;
        this.locations = new Locations(plastic, eventBus);
        this.friends = new Friends(plastic);
        this.messaging = new Messaging(plastic);
        this.textures = new Textures(plastic);
        this.groups = new Groups(plastic);
        this.backgroundJobs = Executors.newCachedThreadPool(r -> {
            Thread thread = new Thread(r);
            thread.setName("Collar Worker");
            return thread;
        });
        eventBus.subscribe(this);
        eventBus.subscribe(connectionState);
    }

    public Optional<Collar> getCollar() {
        return collar == null ? Optional.empty() : Optional.of(collar);
    }

    public void with(Consumer<Collar> action, Runnable emptyAction) {
        if (collar == null || !collar.getState().equals(CONNECTED)) {
            emptyAction.run();
        } else {
            action.accept(collar);
        }
    }

    public void with(Consumer<Collar> action) {
        with(action, () -> plastic.display.displayMessage(plastic.display.newTextBuilder().add("Collar not connected", TextFormatting.YELLOW)));
    }

    public void connect() {
        backgroundJobs.submit(() -> {
            try {
                collar = createCollar();
                collar.connect();
            } catch (CollarException e) {
                plastic.display.displayMessage(plastic.display.newTextBuilder().add(e.getMessage(), TextFormatting.RED));
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            } catch (Throwable e) {
                plastic.display.displayMessage(plastic.display.newTextBuilder().add("Failed to connect to Collar", TextFormatting.RED));
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        });
    }

    public void disconnect() {
        backgroundJobs.submit(() -> {
            if (collar != null) {
                collar.disconnect();
            }
        });
    }

    @Override
    public void onStateChanged(Collar collar, Collar.State state) {
        backgroundJobs.submit(() -> {
            String formatted;
            switch (state) {
                case CONNECTING:
                    plastic.display.displayMessage(this.plastic.display.newTextBuilder().add("Collar connecting...", TextFormatting.GREEN));
                    eventBus.dispatch(new CollarConnectedEvent(collar));
                    break;
                case CONNECTED:
                    plastic.display.displayMessage(this.plastic.display.newTextBuilder().add("Collar connected", TextFormatting.GREEN));
                    collar.location().subscribe(locations);
                    collar.groups().subscribe(groups);
                    collar.friends().subscribe(friends);
                    collar.messaging().subscribe(messaging);
                    collar.textures().subscribe(textures);
                    break;
                case DISCONNECTED:
                    plastic.display.displayMessage(this.plastic.display.newTextBuilder().add("Collar disconnected", TextFormatting.GREEN));
                    eventBus.dispatch(new CollarDisconnectedEvent());
                    break;
            }
            plugins.find().forEach(plugin -> {
                switch (state) {
                    case CONNECTING:
                        plugin.onConnecting(collar);
                        break;
                    case CONNECTED:
                        plugin.onConnected(collar);
                        break;
                    case DISCONNECTED:
                        plugin.onDisconnected(collar);
                        break;
                }
            });
        });
    }

    @Override
    public void onConfirmDeviceRegistration(Collar collar, String token, String approvalUrl) {
        plastic.display.displayStatusMessage("Collar registration required");
        plastic.display.displayMessage(rainbowText("Welcome to Collar!"));
        TextBuilder text = plastic.display.newTextBuilder()
                .add("You'll need to associate this computer with your Collar account at ")
                .add(approvalUrl, TextFormatting.GOLD, new TextAction.OpenLinkAction(approvalUrl));
        plastic.display.displayMessage(text);
    }

    @Override
    public void onClientUntrusted(Collar collar, ClientIdentityStore store) {
        try {
            store.reset();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void onMinecraftAccountVerificationFailed(Collar collar, MinecraftSession session) {
        plastic.display.displayStatusMessage("Account verification failed");
        plastic.display.displayMessage(plastic.display.newTextBuilder().add("Collar failed to verify your Minecraft account", TextFormatting.RED));
    }

    @Override
    public void onPrivateIdentityMismatch(Collar collar, String url) {
        plastic.display.displayStatusMessage("Collar encountered a problem");
        TextBuilder builder = plastic.display.newTextBuilder().add("Your private identity did not match. We cannot decrypt your private data. To resolve please visit ")
                .add(url, TextFormatting.RED, new TextAction.OpenLinkAction(url));
        plastic.display.displayMessage(builder);
    }

    private Collar createCollar() throws IOException {
        CollarConfiguration configuration = new CollarConfiguration.Builder()
                .withCollarDevelopmentServer()
                .withListener(this)
                .withTicks(ticks)
                .withHomeDirectory(plastic.home())
                .withPlayerLocation(() -> plastic.world.currentPlayer().location())
                .withEntitiesSupplier(this::nearbyPlayerEntities)
                .withSession(this::getMinecraftSession).build();
        return Collar.create(configuration);
    }

    private MinecraftSession getMinecraftSession() {
        String serverIP = plastic.serverIp();
        Player player = plastic.world.currentPlayer();
        UUID playerId = player.id();
        String playerName = player.name();
        return MinecraftSession.noJang(playerId, playerName, player.networkId(), serverIP);
    }

    private Set<Entity> nearbyPlayerEntities() {
        return plastic.world.allPlayers().stream()
                .map(entityPlayer -> new Entity(entityPlayer.networkId(), EntityType.PLAYER))
                .collect(Collectors.toSet());
    }

    @Subscribe(Preference.POOL)
    private void onTick(OnTickEvent event) {
        ticks.onTick();
    }

    private TextBuilder rainbowText(String text) {
        TextBuilder builder = plastic.display.newTextBuilder();
        List<TextFormatting> colors = TextFormatting.colors();
        Random random = new Random();
        for (char c : text.toCharArray()) {
            TextFormatting value = colors.get(random.nextInt(colors.size()));
            builder = builder.add(Character.toString(c), value);
        }
        return builder;
    }

    /**
     * Manages connection state of Collar client
     */
    public static final class ConnectionState {
        private final CollarService service;

        private boolean connected = false;
        private boolean loaded = false;

        public ConnectionState(CollarService service) {
            this.service = service;
        }

        @Subscribe(Preference.CALLER)
        public void connected(ClientConnectedEvent e) {
            this.connected = true;
            attemptToConnect();
        }

        @Subscribe(Preference.CALLER)
        public void disconnected(ClientDisconnectedEvent e) {
            this.connected = false;
            this.loaded = false;
        }

        @Subscribe(Preference.CALLER)
        public void worldLoaded(WorldLoadedEvent event) {
            this.loaded = true;
            attemptToConnect();
        }

        private void attemptToConnect() {
            if (connected && loaded) {
                service.connect();
            }
        }
    }
}
