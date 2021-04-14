package team.catgirl.plastic.fabric;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
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
        getPlayer().sendMessage(((FabricTextBuilder)message).text, false);
    }

    @Override
    public void displayMessage(TextBuilder message) {
        getPlayer().sendMessage(((FabricTextBuilder)message).text, false);
    }

    @Override
    public TextBuilder newTextBuilder() {
        return new FabricTextBuilder();
    }

    @Override
    public TextBuilder textBuilderFromJSON(String json) {
        return new FabricTextBuilder(Text.Serializer.fromJson(json));
    }

    @Override
    public TextBuilder textBuilderFromFormattedString(String text) {
        return new FabricTextBuilder(new LiteralText(text));
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
