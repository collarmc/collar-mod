package team.catgirl.collar.mod.common.features;

import team.catgirl.collar.api.groups.Group;
import team.catgirl.collar.api.messaging.Message;
import team.catgirl.collar.api.session.Player;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.client.api.messaging.MessagingApi;
import team.catgirl.collar.client.api.messaging.MessagingListener;
import team.catgirl.plastic.Plastic;
import team.catgirl.collar.security.mojang.MinecraftPlayer;

public class Messaging implements MessagingListener {

    private final Plastic plastic;

    public Messaging(Plastic plastic) {
        this.plastic = plastic;
    }

    /**
     *  When we know the message was delivered securely we should echo it in the senders chat
     */
    @Override
    public void onPrivateMessageSent(Collar collar, MessagingApi messagingApi, Player player, Message message) {
    }

    /**
     * If the message couldn't be sent through collar, then we should just send it directly to the user
     */
    @Override
    public void onPrivateMessageRecipientIsUntrusted(Collar collar, MessagingApi messagingApi, MinecraftPlayer player, Message message) {

    }

    /**
     * When we receive a private message then we should print it
     */
    @Override
    public void onPrivateMessageReceived(Collar collar, MessagingApi messagingApi, Player sender, Message message) {

    }

    @Override
    public void onGroupMessageSent(Collar collar, MessagingApi messagingApi, Group group, Message message) {

    }

    @Override
    public void onGroupMessageReceived(Collar collar, MessagingApi messagingApi, Group group, Player sender, Message message) {

    }
}
