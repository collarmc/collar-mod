package team.catgirl.plastic.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import team.catgirl.plastic.forge.ForgeTextBuilder;
import team.catgirl.plastic.ui.Display;
import team.catgirl.plastic.ui.TextBuilder;

public class ForgeDisplay implements Display {
    
    @Override
    public void displayStatus(String message) {
        Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString(message), true);
    }

    @Override
    public void sendMessage(String message) {
        Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString(message), false);
    }

    @Override
    public TextBuilder newTextBuilder() {
        return new ForgeTextBuilder();
    }
}
