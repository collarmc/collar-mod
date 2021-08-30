package com.collarmc.mod.common.features.messaging;

import com.collarmc.api.groups.Group;
import com.collarmc.api.messaging.TextMessage;
import com.collarmc.api.profiles.PublicProfile;
import com.collarmc.client.api.messaging.events.*;
import com.collarmc.plastic.Plastic;
import com.collarmc.plastic.ui.TextColor;
import com.collarmc.plastic.ui.TextStyle;
import com.collarmc.pounce.EventBus;
import com.collarmc.pounce.Subscribe;

import java.util.Optional;

public class Messaging {

    private final Plastic plastic;

    public Messaging(Plastic plastic, EventBus eventBus) {
        this.plastic = plastic;
        eventBus.subscribe(this);
    }

    /**
     *  When we know the message was delivered securely we should echo it in the senders chat
     */
    @Subscribe
    public void onPrivateMessageSent(PrivateMessageSentEvent event) {
        if (event.message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) event.message;
            event.collar.identities().resolveProfile(event.player).thenAccept(profileOptional -> {
                if (profileOptional.isPresent()) {
                    PublicProfile profile = profileOptional.get();
                    displaySecurePrivateMessage(profile.name, textMessage.content);
                } else {
                    Optional<com.collarmc.plastic.player.Player> collarPlayer = plastic.world.findPlayerById(event.player.minecraftPlayer.id);
                    if (collarPlayer.isPresent()) {
                        displaySecurePrivateMessage(collarPlayer.get().name(), textMessage.content);
                    } else {
                        displaySecurePrivateMessage(event.player.identity.id().toString(), textMessage.content);
                    }
                }
            });
        }
    }

    /**
     * If the message couldn't be sent through collar, then we should just send it directly to the user
     */
    @Subscribe
    public void onPrivateMessageRecipientIsUntrusted(UntrustedPrivateMessageReceivedEvent event) {
        if (event.message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) event.message;
            Optional<com.collarmc.plastic.player.Player> foundPlayer = plastic.world.findPlayerById(event.player.id);
            if (foundPlayer.isPresent()) {
                displayInsecurePrivateMessage(foundPlayer.get().name(), textMessage.content);
            } else {
                displayInsecurePrivateMessage(event.player.id.toString(), textMessage.content);
            }
        }
    }

    /**
     * When we receive a private message then we should print it
     */
    @Subscribe
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) event.message;
            plastic.display.displayMessage(plastic.display.newTextBuilder()
                    .add(plastic.world.currentPlayer().name(), TextColor.GRAY)
                    .add(" securely whispers to you: ", TextColor.GRAY)
                    .add(textMessage.content, TextColor.GRAY)
            );
        }
    }

    @Subscribe
    public void onGroupMessageSent(GroupMessageSentEvent event) {
        if (event.message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) event.message;
            plastic.display.displayMessage(plastic.display.newTextBuilder()
                    .add("[" + event.group.name + "] ", TextColor.LIGHT_PURPLE)
                    .add("<" + plastic.world.currentPlayer().name() + "> ", TextColor.GREEN)
                    .add(textMessage.content, TextColor.GREEN)
            );
        }
    }

    @Subscribe
    public void onGroupMessageReceived(GroupMessageReceivedEvent event) {
        if (event.message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) event.message;
            event.collar.identities().resolveProfile(event.sender).thenAccept(profileOptional -> {
                if (profileOptional.isPresent()) {
                    PublicProfile profile = profileOptional.get();
                    displayReceivedGroupMessage(profile.name, event.group, textMessage.content);
                } else {
                    Optional<com.collarmc.plastic.player.Player> collarPlayer = plastic.world.findPlayerById(event.sender.minecraftPlayer.id);
                    if (collarPlayer.isPresent()) {
                        displayReceivedGroupMessage(collarPlayer.get().name(), event.group, textMessage.content);
                    } else {
                        displayReceivedGroupMessage(event.sender.identity.id().toString(), event.group, textMessage.content);
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
