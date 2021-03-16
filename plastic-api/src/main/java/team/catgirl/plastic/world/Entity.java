package team.catgirl.plastic.world;

public interface Entity {
    /**
     * @return network id of player
     */
    int networkId();

    /**
     * @return current position
     */
    Position position();

    /**
     * @return current dimension
     */
    Dimension dimension();
}
