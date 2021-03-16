package team.catgirl.collar.mod.common.features;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import team.catgirl.collar.api.groups.Group;
import team.catgirl.collar.api.messaging.Message;
import team.catgirl.collar.api.messaging.TextMessage;
import team.catgirl.collar.api.session.Player;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.client.api.messaging.MessagingApi;
import team.catgirl.collar.client.api.messaging.MessagingListener;
import team.catgirl.plastic.Plastic;
import team.catgirl.collar.security.mojang.MinecraftPlayer;

/**
 * TODO: move all the magic out of here into Plastic
 */
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
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            if (textMessage.consoleMessage == null) {
                throw new IllegalArgumentException("TextMessage not include a console message");
            }
            displayConsoleMessage(textMessage, TextFormatting.LIGHT_PURPLE);
        }
    }

    /**
     * If the message couldn't be sent through collar, then we should just send it directly to the user
     */
    @Override
    public void onPrivateMessageRecipientIsUntrusted(Collar collar, MessagingApi messagingApi, MinecraftPlayer player, Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            Minecraft.getMinecraft().world.playerEntities.stream()
                    .filter(playerEntity -> playerEntity.getGameProfile().getId().equals(player.id))
                    .findFirst().ifPresent(thePlayer -> {
                        TextComponentString textComponentString = new TextComponentString(textMessage.consoleMessage);
                        textComponentString.getStyle().setColor(TextFormatting.GRAY);
                        thePlayer.sendMessage(textComponentString);
            });
            displayConsoleMessage(textMessage, TextFormatting.GRAY);
        }
    }

    /**
     * When we receive a private message then we should print it
     */
    @Override
    public void onPrivateMessageReceived(Collar collar, MessagingApi messagingApi, Player sender, Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            TextComponentString textComponentString = new TextComponentString(textMessage.content);
            textComponentString.getStyle().setColor(TextFormatting.LIGHT_PURPLE);
            plastic.display.displayMessage(plastic.display.textBuilderFromFormattedString(textComponentString.getText()));
        }
    }

    @Override
    public void onGroupMessageSent(Collar collar, MessagingApi messagingApi, Group group, Message message) {

    }

    @Override
    public void onGroupMessageReceived(Collar collar, MessagingApi messagingApi, Group group, Player sender, Message message) {

    }

    private void displayConsoleMessage(TextMessage textMessage, TextFormatting color) {
        TextComponentString textComponentString = new TextComponentString(textMessage.consoleMessage);
        textComponentString.getStyle().setColor(color);
        plastic.display.displayMessage(plastic.display.textBuilderFromJSON(textMessage.consoleMessage));
    }
}
