package team.catgirl.plastic.chat;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Chat
 */
public abstract class ChatService {
    private final CopyOnWriteArraySet<ChatInterceptor> interceptors = new CopyOnWriteArraySet<>();

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

    public abstract void sendChatMessage(String recipient, String message);
}
