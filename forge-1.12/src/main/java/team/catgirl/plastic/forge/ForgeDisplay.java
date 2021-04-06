package team.catgirl.plastic.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import team.catgirl.plastic.forge.ForgeTextBuilder;
import team.catgirl.plastic.ui.Display;
import team.catgirl.plastic.ui.TextBuilder;

public class ForgeDisplay implements Display {

    @Override
    public void displayStatusMessage(String message) {
        Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString(message), true);
    }

    @Override
    public void displayStatusMessage(TextBuilder message) {
        Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString(message.formattedString()), true);
    }

    @Override
    public void displayMessage(TextBuilder message) {
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(message.formattedString()));
    }

    @Override
    public TextBuilder newTextBuilder() {
        return new ForgeTextBuilder();
    }

    @Override
    public TextBuilder textBuilderFromJSON(String json) {
        return null;
    }

    @Override
    public TextBuilder textBuilderFromFormattedString(String text) {
        return null;
    }
}
