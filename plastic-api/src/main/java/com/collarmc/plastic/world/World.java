package com.collarmc.plastic.world;

import com.collarmc.plastic.chat.ChatService;
import com.collarmc.plastic.events.world.WorldLoadedEvent;
import com.collarmc.plastic.player.Player;
import com.collarmc.plastic.ui.TextureProvider;
import team.catgirl.pounce.EventBus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class World {

    public final ChatService chatService;
    protected TextureProvider textureProvider;
    protected final EventBus eventBus;

    public World(TextureProvider textureProvider, ChatService chatService, EventBus eventBus) {
        this.textureProvider = textureProvider;
        this.chatService = chatService;
        this.eventBus = eventBus;
    }

    /**
     * @return the current player
     */
    public abstract Player currentPlayer();

    /**
     * @return all players on the server
     */
    public abstract List<Player> allPlayers();

    /**
     * Find player by their ID
     * @param id of player
     * @return player
     */
    public Optional<Player> findPlayerById(UUID id) {
        return allPlayers().stream().filter(candidate -> candidate.id().equals(id)).findFirst();
    }

    /**
     * Fires the {@link WorldLoadedEvent}
     */
    public final void onWorldLoaded() {
        eventBus.dispatch(new WorldLoadedEvent());
    }

    /**
     * Calls {@link Player#onRender()}
     * @param id player id
     */
    public final void onPlayerRender(UUID id) {
        findPlayerById(id).ifPresent(Player::onRender);
    }
}
