package com.collarmc.mod.common;

import com.collarmc.mod.common.features.Friends;
import com.collarmc.mod.common.features.Groups;
import com.collarmc.mod.common.features.Locations;
import com.collarmc.mod.common.features.Textures;
import com.collarmc.mod.common.integrations.Integrations;
import com.collarmc.mod.common.plugins.Plugins;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.collarmc.api.entities.Entity;
import com.collarmc.api.entities.EntityType;
import com.collarmc.client.*;
import com.collarmc.client.minecraft.Ticks;
import com.collarmc.client.security.ClientIdentityStore;
import com.collarmc.mod.common.features.*;
import com.collarmc.mod.common.events.CollarConnectedEvent;
import com.collarmc.mod.common.events.CollarDisconnectedEvent;
import com.collarmc.mod.common.features.messaging.MessagingListenerImpl;
import com.collarmc.plastic.Plastic;
import com.collarmc.plastic.events.client.ClientConnectedEvent;
import com.collarmc.plastic.events.client.ClientDisconnectedEvent;
import com.collarmc.plastic.events.client.OnTickEvent;
import com.collarmc.plastic.events.world.WorldLoadedEvent;
import com.collarmc.plastic.player.Player;
import com.collarmc.plastic.ui.TextAction.OpenLinkAction;
import com.collarmc.plastic.ui.TextBuilder;
import com.collarmc.plastic.ui.TextColor;
import com.collarmc.security.mojang.MinecraftSession;
import com.collarmc.pounce.EventBus;
import com.collarmc.pounce.Preference;
import com.collarmc.pounce.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.collarmc.client.Collar.State.*;

public class CollarService implements CollarListener {

    private static final Logger LOGGER = LogManager.getLogger(CollarService.class.getName());

    private final Lock connectionLock = new ReentrantLock();
    private final ExecutorService backgroundJobs;
    private final ConnectionState connectionState = new ConnectionState(this);
    private transient Collar collar;
    private final Plastic plastic;
    private final EventBus eventBus;
    private final Ticks ticks;
    private final Plugins plugins;

    public final Locations locations;
    public final Friends friends;
    public final MessagingListenerImpl messagingListenerImpl;
    public final Textures textures;
    public final Groups groups;
    public final Integrations integrations;

    public CollarService(Plastic plastic, EventBus eventBus, Plugins plugins) {
        this.integrations = new Integrations(plastic, eventBus);
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
        with(action, () -> plastic.display.displayInfoMessage("Collar not connected"));
    }

    public void connect() {
        LOGGER.info("Attempting to connect...");
        if (!connectionLock.tryLock()) {
            LOGGER.info("Connection already in progress.");
            return;
        }
        connectionState.setAttempted(true);
        backgroundJobs.submit(() -> {
            try {
                collar = createCollar();
                collar.connect();
                LOGGER.info("Connected to Collar");
            } catch (CollarException|IOException e) {
                String msg = "Connection failed " + e.getMessage();
                plastic.display.displayErrorMessage(msg);
                LOGGER.error(msg, e);
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
                    plastic.display.displayInfoMessage("Collar connecting...");
                    eventBus.dispatch(new CollarConnectedEvent(collar));
                    break;
                case CONNECTED:
                    plastic.display.displayMessage(rainbowText("Collar connected"));
                    collar.location().subscribe(locations);
                    collar.groups().subscribe(groups);
                    collar.friends().subscribe(friends);
                    collar.messaging().subscribe(messagingListenerImpl);
                    collar.textures().subscribe(textures);
                    break;
                case DISCONNECTED:
                    connectionState.setAttempted(false);
                    plastic.display.displayWarningMessage("Collar disconnected");
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
                .add(approvalUrl, TextColor.GOLD, null, new OpenLinkAction(approvalUrl));
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
        plastic.display.displayErrorMessage("Collar failed to verify your Minecraft account");
    }

    @Override
    public void onPrivateIdentityMismatch(Collar collar, String url) {
        plastic.display.displayStatusMessage("Collar encountered a problem");
        TextBuilder builder = plastic.display.newTextBuilder().add("Your private identity did not match. We cannot decrypt your private data. To resolve please visit ")
                .add(url, TextColor.RED, null, new OpenLinkAction(url));
        plastic.display.displayMessage(builder);
    }

    @Override
    public void onError(Collar collar, Throwable throwable) {
        plastic.display.displayErrorMessage(throwable.getMessage());
    }

    @Override
    public void onError(Collar collar, String reason) {
        plastic.display.displayErrorMessage(reason);
    }

    private Collar createCollar() throws IOException {
        CollarConfiguration configuration = new CollarConfiguration.Builder()
                .withCollarServer()
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
        Player player = plastic.world.currentPlayer();
        return MinecraftSession.mojang(
                player.id(),
                player.name(),
                player.networkId(),
                plastic.serverAddress(),
                plastic.accessToken(),
                null
        );
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
        List<TextColor> values = new ArrayList<>(Arrays.asList(TextColor.values()));
        // too dark to display in most contexts
        values.remove(TextColor.BLACK);
        values.remove(TextColor.GRAY);
        values.remove(TextColor.WHITE);
        values.remove(TextColor.DARK_GRAY);
        values.remove(TextColor.DARK_BLUE);
        values.remove(TextColor.DARK_GREEN);
        values.remove(TextColor.DARK_AQUA);
        values.remove(TextColor.DARK_RED);
        values.remove(TextColor.DARK_PURPLE);
        TextColor lastColor = null;
        for (char c : text.toCharArray()) {
            TextColor color = values.get(random.nextInt(values.size()));
            while (color == lastColor) {
                color = values.get(random.nextInt(values.size()));
            }
            lastColor = color;
            builder = builder.add(Character.toString(c), color);
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
