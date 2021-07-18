<<<<<<< HEAD:1.16/fabric/src/main/java/com/collarmc/plastic/fabric/FabricDisplay.java
package com.collarmc.plastic.fabric;
=======
package com.collarmc.collar.plastic;
>>>>>>> topic/merge:1.16/glue/src/main/java/com/collarmc/collar/plastic/GlueDisplay.java

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.NotNull;
import com.collarmc.plastic.ui.Display;
import com.collarmc.plastic.ui.TextBuilder;

public class GlueDisplay implements Display {
    @Override
    public void displayStatusMessage(String message) {
        getPlayer().sendMessage(new LiteralText(message), true);
    }

    @Override
    public void displayStatusMessage(TextBuilder message) {
        getPlayer().sendMessage(((GlueTextBuilder)message).text, false);
    }

    @Override
    public void displayMessage(TextBuilder message) {
        getPlayer().sendMessage(((GlueTextBuilder)message).text, false);
    }

    @Override
    public TextBuilder newTextBuilder() {
        return new GlueTextBuilder();
    }

    @NotNull
    private ClientPlayerEntity getPlayer() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            throw new IllegalStateException("player not ready");
        }
        return player;
    }
}
