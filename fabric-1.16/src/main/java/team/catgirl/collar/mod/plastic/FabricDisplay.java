package team.catgirl.collar.mod.plastic;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.NotNull;
import team.catgirl.plastic.ui.Display;
import team.catgirl.plastic.ui.TextBuilder;

public class FabricDisplay implements Display {
    @Override
    public void displayStatusMessage(String message) {
        getPlayer().sendMessage(new LiteralText(message), true);
    }

    @Override
    public void displayStatusMessage(TextBuilder message) {

    }

    @Override
    public void displayMessage(TextBuilder message) {

    }

    @Override
    public TextBuilder newTextBuilder() {
        return null;
    }

    @Override
    public TextBuilder textBuilderFromJSON(String json) {
        return null;
    }

    @Override
    public TextBuilder textBuilderFromFormattedString(String text) {
        return null;
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
