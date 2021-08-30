package com.collarmc.mod.common.features;

import com.collarmc.client.api.friends.events.FriendAddedEvent;
import com.collarmc.client.api.friends.events.FriendChangedEvent;
import com.collarmc.client.api.friends.events.FriendRemovedEvent;
import com.collarmc.plastic.Plastic;
import com.collarmc.pounce.EventBus;
import com.collarmc.pounce.Subscribe;

public class Friends {

    private final Plastic plastic;

    public Friends(Plastic plastic, EventBus eventBus) {
        this.plastic = plastic;
        eventBus.subscribe(this);
    }

    @Subscribe
    public void onFriendChanged(FriendChangedEvent event) {
        plastic.display.displayStatusMessage(plastic.display.newTextBuilder().add(String.format("%s is %s", event.friend.profile.name, event.friend.status.name().toLowerCase())));
    }

    @Subscribe
    public void onFriendAdded(FriendAddedEvent event) {
        plastic.display.displayMessage(String.format("Added %s as a friend", event.friend.profile.name));
    }

    @Subscribe
    public void onFriendRemoved(FriendRemovedEvent event) {
        plastic.display.displayMessage(String.format("Removed %s as a friend", event.friend.profile.name));
    }
}
