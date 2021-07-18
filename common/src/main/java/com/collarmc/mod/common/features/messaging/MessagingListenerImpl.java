package com.collarmc.mod.common.features.messaging;

import com.collarmc.plastic.player.Player;
import com.collarmc.api.groups.Group;
import com.collarmc.api.messaging.Message;
import com.collarmc.api.messaging.TextMessage;
import com.collarmc.api.profiles.PublicProfile;
import com.collarmc.api.session.Player;
import com.collarmc.client.Collar;
import com.collarmc.client.api.messaging.MessagingApi;
import com.collarmc.client.api.messaging.MessagingListener;
import com.collarmc.plastic.Plastic;
import com.collarmc.security.mojang.MinecraftPlayer;
import com.collarmc.plastic.ui.TextColor;
import com.collarmc.plastic.ui.TextStyle;

import java.util.Optional;

public class MessagingListenerImpl implements MessagingListener {

    private final Plastic plastic;

    public MessagingListenerImpl(Plastic plastic) {
        this.plastic = plastic;
    }

    /**
     *  When we know the message was delivered securely we should echo it in the senders chat
     */
    @Override
    public void onPrivateMessageSent(Collar collar, MessagingApi messagingApi, Player player, Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            collar.identities().resolveProfile(player).thenAccept(profileOptional -> {
                if (profileOptional.isPresent()) {
                    PublicProfile profile = profileOptional.get();
                    displaySecurePrivateMessage(profile.name, textMessage.content);
                } else {
                    Optional<Player> collarPlayer = plastic.world.findPlayerById(player.minecraftPlayer.id);
                    if (collarPlayer.isPresent()) {
                        displaySecurePrivateMessage(collarPlayer.get().name(), textMessage.content);
                    } else {
                        displaySecurePrivateMessage(player.profile.toString(), textMessage.content);
                    }
                }
            });
        }
    }

    /**
     * If the message couldn't be sent through collar, then we should just send it directly to the user
     */
    @Override
    public void onPrivateMessageRecipientIsUntrusted(Collar collar, MessagingApi messagingApi, MinecraftPlayer player, Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            Optional<Player> foundPlayer = plastic.world.findPlayerById(player.id);
            if (foundPlayer.isPresent()) {
                displayInsecurePrivateMessage(foundPlayer.get().name(), textMessage.content);
            } else {
                displayInsecurePrivateMessage(player.id.toString(), textMessage.content);
            }
        }
    }

    /**
     * When we receive a private message then we should print it
     */
    @Override
    public void onPrivateMessageReceived(Collar collar, MessagingApi messagingApi, Player sender, Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            plastic.display.displayMessage(plastic.display.newTextBuilder()
                    .add(plastic.world.currentPlayer().name(), TextColor.GRAY)
                    .add(" securely whispers to you: ", TextColor.GRAY)
                    .add(textMessage.content, TextColor.GRAY)
            );
        }
    }

    @Override
    public void onGroupMessageSent(Collar collar, MessagingApi messagingApi, Group group, Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            plastic.display.displayMessage(plastic.display.newTextBuilder()
                    .add("[" + group.name + "] ", TextColor.LIGHT_PURPLE)
                    .add("<" + plastic.world.currentPlayer().name() + "> ", TextColor.GREEN)
                    .add(textMessage.content, TextColor.GREEN)
            );
        }
    }

    @Override
    public void onGroupMessageReceived(Collar collar, MessagingApi messagingApi, Group group, Player sender, Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            collar.identities().resolveProfile(sender).thenAccept(profileOptional -> {
                if (profileOptional.isPresent()) {
                    PublicProfile profile = profileOptional.get();
                    displayReceivedGroupMessage(profile.name, group, textMessage.content);
                } else {
                    Optional<Player> collarPlayer = plastic.world.findPlayerById(sender.minecraftPlayer.id);
                    if (collarPlayer.isPresent()) {
                        displayReceivedGroupMessage(collarPlayer.get().name(), group, textMessage.content);
                    } else {
                        displayReceivedGroupMessage(sender.profile.toString(), group, textMessage.content);
                    }
                }
            });
        }
    }

    private void displayReceivedGroupMessage(String sender, Group group, String content) {
        plastic.display.displayMessage(plastic.display.newTextBuilder()
                .add("[" + group.name + "] ", TextColor.LIGHT_PURPLE)
                .add("<" + sender + "> ", TextColor.WHITE)
                .add(content, TextColor.WHITE)
        );
    }

    private void displaySecurePrivateMessage(String sender, String content) {
        plastic.display.displayMessage(plastic.display.newTextBuilder()
                .add(sender, TextColor.GRAY, TextStyle.ITALIC)
                .add(" securely whispers to you: ", TextColor.GRAY, TextStyle.ITALIC)
                .add(content, TextColor.GRAY, TextStyle.ITALIC)
        );
    }

    private void displayInsecurePrivateMessage(String sender, String content) {
        plastic.display.displayMessage(plastic.display.newTextBuilder()
                .add(sender, TextColor.DARK_RED, TextStyle.ITALIC)
                .add(" insecurely whispers to you: ", TextColor.DARK_RED, TextStyle.ITALIC)
                .add(content, TextColor.DARK_RED, TextStyle.ITALIC)
        );
    }
}
