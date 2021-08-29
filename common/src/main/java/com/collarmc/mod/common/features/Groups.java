package com.collarmc.mod.common.features;

import com.collarmc.api.groups.GroupType;
import com.collarmc.client.api.groups.events.GroupCreatedEvent;
import com.collarmc.client.api.groups.events.GroupInvitationEvent;
import com.collarmc.client.api.groups.events.GroupJoinedEvent;
import com.collarmc.client.api.groups.events.GroupLeftEvent;
import com.collarmc.plastic.Plastic;
import com.collarmc.pounce.EventBus;
import com.collarmc.pounce.Subscribe;

public class Groups {

    private final Plastic plastic;

    public Groups(Plastic plastic, EventBus eventBus) {
        this.plastic = plastic;
        eventBus.subscribe(this);
    }

    @Subscribe
    public void onGroupCreated(GroupCreatedEvent event) {
        if (event.group.type == GroupType.NEARBY) {
            return;
        }
        this.plastic.display.displayMessage(String.format("Created %s %s", event.group.type.name, event.group.name));
    }

    @Subscribe
    public void onGroupJoined(GroupJoinedEvent event) {
        if (event.group.type == GroupType.NEARBY) {
            return;
        }
        this.plastic.display.displayMessage(String.format("Joined %s %s", event.group.type.name, event.group.name));
    }

    @Subscribe
    public void onGroupLeft(GroupLeftEvent event) {
        if (event.group.type == GroupType.NEARBY) {
            return;
        }
        this.plastic.display.displayMessage(String.format("Left %s %s", event.group.type.name, event.group.name));
    }

    @Subscribe
    public void onGroupInvited(GroupInvitationEvent event) {
        // Don't print out in console if the invitation was from a nearby group
        // Or if sender == null, the server is just resending invitiation state
        if (event.invitation.type == GroupType.NEARBY || event.invitation.sender == null) {
            return;
        }
        com.collarmc.plastic.player.Player player = plastic.world.allPlayers()
                .stream().filter(player1 -> player1.id().equals(event.invitation.sender.minecraftPlayer.id))
                .findFirst().orElseThrow(() -> new IllegalStateException("cannot find player " + event.invitation.sender.minecraftPlayer.id));
        String message = String.format("You are invited to %s %s by %s", event.invitation.type.name, event.invitation.name, player.name());
        this.plastic.display.displayStatusMessage(message);
        this.plastic.display.displayInfoMessage(message);
    }
}
