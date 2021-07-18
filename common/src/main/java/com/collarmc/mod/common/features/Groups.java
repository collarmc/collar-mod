package com.collarmc.mod.common.features;

import com.collarmc.plastic.player.Player;
import com.collarmc.api.groups.Group;
import com.collarmc.api.groups.GroupType;
import com.collarmc.api.session.Player;
import com.collarmc.client.Collar;
import com.collarmc.client.api.groups.GroupInvitation;
import com.collarmc.client.api.groups.GroupsApi;
import com.collarmc.client.api.groups.GroupsListener;
import com.collarmc.plastic.Plastic;

public class Groups implements GroupsListener {

    private final Plastic plastic;

    public Groups(Plastic plastic) {
        this.plastic = plastic;
    }

    @Override
    public void onGroupCreated(Collar collar, GroupsApi groupsApi, Group group) {
        if (group.type == GroupType.NEARBY) {
            return;
        }
        this.plastic.display.displayMessage(String.format("Created %s %s", group.type.name, group.name));
    }

    @Override
    public void onGroupJoined(Collar collar, GroupsApi groupsApi, Group group, Player player) {
        if (group.type == GroupType.NEARBY) {
            return;
        }
        this.plastic.display.displayMessage(String.format("Joined %s %s", group.type.name, group.name));
    }

    @Override
    public void onGroupLeft(Collar collar, GroupsApi groupsApi, Group group, Player player) {
        if (group.type == GroupType.NEARBY) {
            return;
        }
        this.plastic.display.displayMessage(String.format("Left %s %s", group.type.name, group.name));
    }

    @Override
    public void onGroupInvited(Collar collar, GroupsApi groupsApi, GroupInvitation invitation) {
        // Don't print out in console if the invitation was from a nearby group
        // Or if sender == null, the server is just resending invitiation state
        if (invitation.type == GroupType.NEARBY || invitation.sender == null) {
            return;
        }
        Player player = plastic.world.allPlayers()
                .stream().filter(player1 -> player1.id().equals(invitation.sender.minecraftPlayer.id))
                .findFirst().orElseThrow(() -> new IllegalStateException("cannot find player " + invitation.sender.minecraftPlayer.id));
        String message = String.format("You are invited to %s %s by %s", invitation.type.name, invitation.name, player.name());
        this.plastic.display.displayStatusMessage(message);
        this.plastic.display.displayInfoMessage(message);
    }
}
