package com.collarmc.plastic.world;

import com.collarmc.api.location.Location;

/**
 * TODO: replace with {@link com.collarmc.api.entities.Entity}
 */
@Deprecated
public interface Entity {
    /**
     * @return network id of entity
     */
    int networkId();

    /**
     * @return current position
     */
    Location location();
}
