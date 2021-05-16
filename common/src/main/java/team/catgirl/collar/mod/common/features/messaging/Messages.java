package team.catgirl.collar.mod.common.features.messaging;

import team.catgirl.collar.api.groups.Group;
import team.catgirl.collar.api.messaging.TextMessage;
import team.catgirl.collar.mod.common.CollarService;
import team.catgirl.plastic.Plastic;
import team.catgirl.plastic.player.Player;

/**
 * Management of group chat input and redirect
 */
public final class Messages {
    private final Plastic plastic;
    private final CollarService collarService;
    private GroupChatInterceptor currentInterceptor;

    public Messages(Plastic plastic, CollarService collarService) {
        this.plastic = plastic;
        this.collarService = collarService;
    }

    /**
     * Switches conversation to chat with the specified {@link Group}
     * @param group to chat with
     */
    public void switchToGroup(Group group) {
        if (currentInterceptor != null) {
            plastic.world.chatService.remove(currentInterceptor);
        }
        currentInterceptor = new GroupChatInterceptor(collarService, group);
        plastic.world.chatService.register(currentInterceptor);
        plastic.display.displayInfoMessage("Chatting with " + group.type.name + " \"" + group.name + "\"");
    }

    /**
     * Switches conversation to the servers general chat
     */
    public void switchToGeneralChat() {
        if (currentInterceptor != null) {
            plastic.world.chatService.remove(currentInterceptor);
        }
        plastic.display.displayInfoMessage("Chatting with everyone");
    }

    /**
     * Send a private message to another player
     * @param recipient to receive the message
     * @param message to receive
     */
    public void sendMessage(Player recipient, String message) {
        collarService.with(collar -> {
            collar.identities().resolvePlayer(recipient.id()).thenAccept(player -> {
                if (player.isPresent()) {
                    collar.messaging().sendPrivateMessage(player.get(), new TextMessage(message));
                } else {
                    plastic.world.chatService.sendChatMessage(recipient.name(), message);
                }
            });
        }, () -> {
            plastic.world.chatService.sendChatMessage(recipient.name(), message);
        });
    }
}
