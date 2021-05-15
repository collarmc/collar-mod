package team.catgirl.plastic.chat;

import java.util.concurrent.CopyOnWriteArraySet;

public final class ChatService {
    private final CopyOnWriteArraySet<ChatInterceptor> interceptors = new CopyOnWriteArraySet<>();

    public boolean onChatMessageSent(String message) {
        interceptors.forEach(interceptor -> interceptor.onChatMessageSent(message));
        return !interceptors.isEmpty();
    }

    public void register(ChatInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    public void remove(ChatInterceptor interceptor) {
        interceptors.remove(interceptor);
    }
}
