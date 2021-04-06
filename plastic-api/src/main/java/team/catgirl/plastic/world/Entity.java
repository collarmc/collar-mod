package team.catgirl.plastic.world;

public interface Entity {
    /**
     * @return network id of entity
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
