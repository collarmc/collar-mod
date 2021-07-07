package team.catgirl.collar.mod.common.features;

import team.catgirl.collar.api.groups.Group;
import team.catgirl.collar.api.groups.GroupType;
import team.catgirl.collar.api.session.Player;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.client.api.groups.GroupInvitation;
import team.catgirl.collar.client.api.groups.GroupsApi;
import team.catgirl.collar.client.api.groups.GroupsListener;
import team.catgirl.plastic.Plastic;

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
        team.catgirl.plastic.player.Player player = plastic.world.allPlayers()
                .stream().filter(player1 -> player1.id().equals(invitation.sender.minecraftPlayer.id))
                .findFirst().orElseThrow(() -> new IllegalStateException("cannot find player " + invitation.sender.minecraftPlayer.id));
        String message = String.format("You are invited to %s %s by %s", invitation.type.name, invitation.name, player.name());
        this.plastic.display.displayStatusMessage(message);
        this.plastic.display.displayInfoMessage(message);
    }
}
