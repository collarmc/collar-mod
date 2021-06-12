package team.catgirl.collar.mod.common;

import team.catgirl.collar.api.entities.Entity;
import team.catgirl.collar.api.entities.EntityType;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.client.CollarConfiguration;
import team.catgirl.collar.client.CollarException;
import team.catgirl.collar.client.CollarListener;
import team.catgirl.collar.client.minecraft.Ticks;
import team.catgirl.collar.client.security.ClientIdentityStore;
import team.catgirl.collar.mod.common.features.*;
import team.catgirl.collar.mod.common.events.CollarConnectedEvent;
import team.catgirl.collar.mod.common.events.CollarDisconnectedEvent;
import team.catgirl.collar.mod.common.features.messaging.MessagingListenerImpl;
import team.catgirl.plastic.Plastic;
import team.catgirl.plastic.events.client.ClientConnectedEvent;
import team.catgirl.plastic.events.client.ClientDisconnectedEvent;
import team.catgirl.plastic.events.client.OnTickEvent;
import team.catgirl.plastic.events.world.WorldLoadedEvent;
import team.catgirl.plastic.player.Player;
import team.catgirl.plastic.ui.TextAction;
import team.catgirl.plastic.ui.TextBuilder;
import team.catgirl.plastic.ui.TextColor;
import team.catgirl.collar.mod.common.plugins.Plugins;
import team.catgirl.collar.security.mojang.MinecraftSession;
import team.catgirl.pounce.EventBus;
import team.catgirl.pounce.Preference;
import team.catgirl.pounce.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static team.catgirl.collar.client.Collar.State.*;

public class CollarService implements CollarListener {

    private static final Logger LOGGER = Logger.getLogger(CollarService.class.getName());

    private final Lock connectionLock = new ReentrantLock();
    private final ExecutorService backgroundJobs;
    private final ConnectionState connectionState = new ConnectionState(this);
    private Collar collar;
    private final Plastic plastic;
    private final EventBus eventBus;
    private final Ticks ticks;
    private final Plugins plugins;

    public final Locations locations;
    public final Friends friends;
    public final MessagingListenerImpl messagingListenerImpl;
    public final Textures textures;
    public final Groups groups;

    public CollarService(Plastic plastic, EventBus eventBus, Plugins plugins) {
        this.plastic = plastic;
        this.eventBus = eventBus;
        this.ticks = new Ticks();
        this.plugins = plugins;
        this.locations = new Locations(plastic, eventBus);
        this.friends = new Friends(plastic);
        this.messagingListenerImpl = new MessagingListenerImpl(plastic);
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
            if (emptyAction != null) {
                emptyAction.run();
            }
        } else {
            if (action != null) {
                action.accept(collar);
            }
        }
    }

    public void with(Consumer<Collar> action) {
        with(action, () -> plastic.display.displayMessage(plastic.display.newTextBuilder().add("Collar not connected", TextColor.YELLOW)));
    }

    public void connect() {
        if (!connectionLock.tryLock()) {
            return;
        }
        connectionState.setAttempted(true);
        backgroundJobs.submit(() -> {
            try {
                collar = createCollar();
                collar.connect();
            } catch (CollarException e) {
                plastic.display.displayMessage(plastic.display.newTextBuilder().add(e.getMessage(), TextColor.RED));
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            } catch (Throwable e) {
                plastic.display.displayMessage(plastic.display.newTextBuilder().add("Failed to connect to Collar", TextColor.RED));
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            } finally {
                connectionLock.unlock();
            }
        });
    }

    public void disconnect() {
        if (!connectionLock.tryLock()) {
            return;
        }
        connectionState.setAttempted(false);
        backgroundJobs.submit(() -> {
            try {
                if (collar != null) {
                    collar.disconnect();
                    collar = null;
                }
            } finally {
                connectionLock.unlock();
            }
        });
    }

    @Override
    public void onStateChanged(Collar collar, Collar.State state) {
        backgroundJobs.submit(() -> {
            switch (state) {
                case CONNECTING:
                    plastic.display.displayMessage(this.plastic.display.newTextBuilder().add("Collar connecting...", TextColor.GREEN));
                    eventBus.dispatch(new CollarConnectedEvent(collar));
                    break;
                case CONNECTED:
                    plastic.display.displayMessage(this.plastic.display.newTextBuilder().add("Collar connected", TextColor.GREEN));
                    collar.location().subscribe(locations);
                    collar.groups().subscribe(groups);
                    collar.friends().subscribe(friends);
                    collar.messaging().subscribe(messagingListenerImpl);
                    collar.textures().subscribe(textures);
                    break;
                case DISCONNECTED:
                    connectionState.setAttempted(false);
                    plastic.display.displayMessage(this.plastic.display.newTextBuilder().add("Collar disconnected", TextColor.GREEN));
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
                .add(approvalUrl, TextColor.GOLD, null, new TextAction.OpenLinkAction(approvalUrl));
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
        plastic.display.displayMessage(plastic.display.newTextBuilder().add("Collar failed to verify your Minecraft account", TextColor.RED));
    }

    @Override
    public void onPrivateIdentityMismatch(Collar collar, String url) {
        plastic.display.displayStatusMessage("Collar encountered a problem");
        TextBuilder builder = plastic.display.newTextBuilder().add("Your private identity did not match. We cannot decrypt your private data. To resolve please visit ")
                .add(url, TextColor.RED, null, new TextAction.OpenLinkAction(url));
        plastic.display.displayMessage(builder);
    }

    private Collar createCollar() throws IOException {
        CollarConfiguration configuration = new CollarConfiguration.Builder()
                .withCollarServer("http://localhost:4000")
                .withListener(this)
                .withTicks(ticks)
                .withHomeDirectory(collarHome())
                .withPlayerLocation(() -> plastic.world.currentPlayer().location())
                .withEntitiesSupplier(this::nearbyPlayerEntities)
                .withSession(this::getMinecraftSession).build();
        return Collar.create(configuration);
    }

    /**
     * Override the collar home directory with COLLAR_PLAYER so that you can test multiple players in the same IDE session
     * @return home
     */
    private File collarHome() {
        String player = System.getenv("COLLAR_PLAYER");
        return player != null ? new File(plastic.home(), "collar-" + player) : plastic.home();
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
        with(i -> ticks.onTick(), null);
    }

    private TextBuilder rainbowText(String text) {
        TextBuilder builder = plastic.display.newTextBuilder();
        Random random = new Random();
        for (char c : text.toCharArray()) {
            TextColor value = TextColor.values()[random.nextInt(TextColor.values().length)];
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
        private boolean attempted = false;

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
            this.attempted = false;
            service.disconnect();
        }

        @Subscribe(Preference.CALLER)
        public void worldLoaded(WorldLoadedEvent event) {
            this.loaded = true;
            attemptToConnect();
        }

        private void attemptToConnect() {
            if (!attempted && connected && loaded) {
                service.connect();
            }
        }

        public void setAttempted(boolean collarConnectionAttempted) {
            this.attempted = collarConnectionAttempted;
        }
    }
}
