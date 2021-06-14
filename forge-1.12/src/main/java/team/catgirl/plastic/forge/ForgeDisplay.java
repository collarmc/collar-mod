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
        Minecraft.getMinecraft().player.sendStatusMessage(((ForgeTextBuilder)message).componentString, true);
    }

    @Override
    public void displayMessage(TextBuilder message) {
        Minecraft.getMinecraft().player.sendMessage(((ForgeTextBuilder)message).componentString);
    }

    @Override
    public TextBuilder newTextBuilder() {
        return new ForgeTextBuilder();
    }
}
