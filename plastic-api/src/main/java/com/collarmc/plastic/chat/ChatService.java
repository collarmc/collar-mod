package com.collarmc.plastic.chat;

import com.collarmc.plastic.ui.Display;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Chat
 */
public abstract class ChatService {

    private final CopyOnWriteArraySet<ChatInterceptor> interceptors = new CopyOnWriteArraySet<>();
    protected final Display display;

    public ChatService(Display display) {
        this.display = display;
    }

    /**
     * @param message sent
     * @return cancel event or not
     */
    public boolean onChatMessageSent(String message) {
        interceptors.forEach(interceptor -> interceptor.onChatMessageSent(message));
        return !interceptors.isEmpty();
    }

    /**
     * @param interceptor to add
     */
    public void register(ChatInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    /**
     * @param interceptor to remove
     */
    public void remove(ChatInterceptor interceptor) {
        interceptors.remove(interceptor);
    }

    /**
     * Send message to a player
     * @param recipient to send to
     * @param message to send
     */
    public abstract void sendChatMessage(String recipient, String message);

    /**
     * Send a message to yourself
     * @param message to send
     */
    public abstract void sendChatMessageToSelf(String message);
}
