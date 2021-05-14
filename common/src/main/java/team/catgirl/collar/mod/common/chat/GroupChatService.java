package team.catgirl.collar.mod.common.chat;

import team.catgirl.collar.api.groups.Group;
import team.catgirl.collar.mod.common.CollarService;
import team.catgirl.plastic.Plastic;

public final class GroupChatService {
    private final Plastic plastic;
    private final CollarService collarService;
    private GroupChatInterceptor currentInterceptor;

    public GroupChatService(Plastic plastic, CollarService collarService) {
        this.plastic = plastic;
        this.collarService = collarService;
    }

    public void switchToGroup(Group group) {
        if (currentInterceptor != null) {
            plastic.world.chatService.remove(currentInterceptor);
        }
        currentInterceptor = new GroupChatInterceptor(collarService, group);
        plastic.world.chatService.register(currentInterceptor);
        plastic.display.displayInfoMessage("Chatting with " + group.type.name + " \"" + group.name + "\"");
    }

    public void switchToGeneralChat() {
        if (currentInterceptor != null) {
            plastic.world.chatService.remove(currentInterceptor);
        }
        plastic.display.displayInfoMessage("Chatting with everyone");
    }
}
