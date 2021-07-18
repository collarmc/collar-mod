package com.collarmc.mod.common.features.messaging;

import com.collarmc.api.groups.Group;
import com.collarmc.api.messaging.TextMessage;
import com.collarmc.mod.common.CollarService;
import com.collarmc.plastic.chat.ChatInterceptor;

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
