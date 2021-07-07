package team.catgirl.collar.mod.common.features.messaging;

import team.catgirl.collar.api.groups.Group;
import team.catgirl.collar.api.messaging.TextMessage;
import team.catgirl.collar.mod.common.CollarService;
import team.catgirl.plastic.chat.ChatInterceptor;

public class GroupChatInterceptor implements ChatInterceptor {

    private final CollarService collarService;
    private final Group group;

    public GroupChatInterceptor(CollarService collarService, Group group) {
        this.collarService = collarService;
        this.group = group;
    }

    @Override
    public void onChatMessageSent(String message) {
        collarService.with(collar -> {
            collar.messaging().sendGroupMessage(group, new TextMessage(message));
        });
    }
}
