package team.catgirl.plastic.world;

import team.catgirl.plastic.player.Player;

import java.util.List;

public interface World {
    /**
     * @return the current player
     */
    Player currentPlayer();

    /**
     * @return all players on the server
     */
    List<Player> allPlayers();
}
