package com.collarmc.mod.common.features;

import com.collarmc.api.friends.Friend;
import com.collarmc.client.Collar;
import com.collarmc.client.api.friends.FriendsApi;
import com.collarmc.client.api.friends.FriendsListener;
import com.collarmc.plastic.Plastic;

public class Friends implements FriendsListener {

    private final Plastic plastic;

    public Friends(Plastic plastic) {
        this.plastic = plastic;
    }

    @Override
    public void onFriendChanged(Collar collar, FriendsApi friendsApi, Friend friend) {
        plastic.display.displayStatusMessage(plastic.display.newTextBuilder().add(String.format("%s is %s", friend.friend.name, friend.status.name().toLowerCase())));
    }

    @Override
    public void onFriendAdded(Collar collar, FriendsApi friendsApi, Friend added) {
        plastic.display.displayMessage(String.format("Added %s as a friend", added.friend.name));
    }

    @Override
    public void onFriendRemoved(Collar collar, FriendsApi friendsApi, Friend removed) {
        plastic.display.displayMessage(String.format("Removed %s as a friend", removed.friend.name));
    }
}
