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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class Messaging {

    private final Plastic plastic;
    private static final Logger LOGGER = LogManager.getLogger(Messaging.class.getName());

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
                    //LOGGER.info("[MSG SENDER] Resolved profile is present, player identity id: " + event.player.identity.id() + ", profile name: " + profile.name);
                    displaySecurePrivateMessage(profile.name, textMessage.content);
                } else {
                    Optional<com.collarmc.plastic.player.Player> collarPlayer = plastic.world.findPlayerById(event.player.minecraftPlayer.id);
                    if (collarPlayer.isPresent()) {
                        //LOGGER.info("[MSG SENDER] Resolved profile is not present, player identity id: " + event.player.identity.id() + "minecraftPlayer id: " + event.player.minecraftPlayer.id + ", collar player name: " + collarPlayer.get().name());
                        displaySecurePrivateMessage(collarPlayer.get().name(), textMessage.content);
                    } else {
                        //LOGGER.info("[MSG SENDER] Resolved profile is not present, player identity id: " + event.player.identity.id() + "minecraftPlayer id: " + event.player.minecraftPlayer.id + ", collar player is not present" );
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
            event.collar.identities().resolveProfile(event.player).thenAccept(profileOptional -> {
                if (profileOptional.isPresent()) {
                    PublicProfile profile = profileOptional.get();
                    //LOGGER.info("[MSG RECEIVER] Resolved profile is present, player identity id: " + event.player.identity.id() + ", profile name: " + profile.name);
                    displaySecurePrivateMessageReceived(profile.name, textMessage.content);
                } else {
                    Optional<com.collarmc.plastic.player.Player> collarPlayer = plastic.world.findPlayerById(event.player.minecraftPlayer.id);
                    if (collarPlayer.isPresent()) {
                       //LOGGER.info("[MSG RECEIVER] Resolved profile is not present, player identity id: " + event.player.identity.id() + "minecraftPlayer id: " + event.player.minecraftPlayer.id + ", collar player name: " + collarPlayer.get().name());
                        displaySecurePrivateMessageReceived(collarPlayer.get().name(), textMessage.content);
                    } else {
                        //LOGGER.info("[MSG RECEIVER] Resolved profile is not present, player identity id: " + event.player.identity.id() + "minecraftPlayer id: " + event.player.minecraftPlayer.id + ", collar player is not present" );
                        displaySecurePrivateMessageReceived(event.player.identity.id().toString(), textMessage.content);
                    }
                }
            });
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
                    //LOGGER.info("[GROUP MSG RECEIVER] Resolved profile is present, player identity id: " + event.sender.identity.id() + ", profile name: " + profile.name);
                    displayReceivedGroupMessage(profile.name, event.group, textMessage.content);
                } else {
                    Optional<com.collarmc.plastic.player.Player> collarPlayer = plastic.world.findPlayerById(event.sender.minecraftPlayer.id);
                    if (collarPlayer.isPresent()) {
                        //LOGGER.info("[GROUP MSG RECEIVER] Resolved profile is not present, player identity id: " + event.sender.identity.id() + "minecraftPlayer id: " + event.sender.minecraftPlayer.id + ", collar player name: " + collarPlayer.get().name());
                        displayReceivedGroupMessage(collarPlayer.get().name(), event.group, textMessage.content);
                    } else {
                        //LOGGER.info("[GROUP MSG RECEIVER] Resolved profile is not present, player identity id: " + event.sender.identity.id() + "minecraftPlayer id: " + event.sender.minecraftPlayer.id + ", collar player is not present" );
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

    private void displaySecurePrivateMessage(String recipient, String content) {
        plastic.display.displayMessage(plastic.display.newTextBuilder()
                .add("You securely whisper to ", TextColor.GRAY, TextStyle.ITALIC)
                .add(recipient, TextColor.GRAY, TextStyle.ITALIC)
                .add(": ", TextColor.GRAY, TextStyle.ITALIC)
                .add(content, TextColor.GRAY, TextStyle.ITALIC)
        );
    }

    private void displayInsecurePrivateMessage(String recipient, String content) {
        plastic.display.displayMessage(plastic.display.newTextBuilder()
                .add("You insecurely whisper to ", TextColor.GRAY, TextStyle.ITALIC)
                .add(recipient, TextColor.DARK_RED, TextStyle.ITALIC)
                .add(": ", TextColor.GRAY, TextStyle.ITALIC)
                .add(content, TextColor.DARK_RED, TextStyle.ITALIC)
        );
    }

    private void displaySecurePrivateMessageReceived(String sender, String content) {
        plastic.display.displayMessage(plastic.display.newTextBuilder()
                .add(sender, TextColor.GRAY, TextStyle.ITALIC)
                .add(" securely whispers to you: ", TextColor.GRAY, TextStyle.ITALIC)
                .add(content, TextColor.GRAY, TextStyle.ITALIC)
        );
    }

}
