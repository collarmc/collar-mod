<<<<<<< HEAD:1.16/fabric/src/main/java/com/collarmc/plastic/fabric/FabricChatService.java
package com.collarmc.plastic.fabric;
=======
package com.collarmc.collar.plastic;
>>>>>>> topic/merge:1.16/glue/src/main/java/com/collarmc/collar/plastic/GlueChatService.java

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import com.collarmc.plastic.chat.ChatService;
import com.collarmc.plastic.ui.Display;

public class GlueChatService extends ChatService {

    public GlueChatService(Display display) {
        super(display);
    }

    @Override
    public void sendChatMessage(String recipient, String message) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            display.displayErrorMessage(String.format("No player %s", recipient));
        } else {
            player.sendChatMessage(String.format("/tell %s %s", recipient, message));
        }
    }
}
