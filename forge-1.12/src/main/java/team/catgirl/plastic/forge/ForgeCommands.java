package team.catgirl.plastic.forge;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraftforge.client.ClientCommandHandler;
import team.catgirl.plastic.ui.Commands;

public class ForgeCommands implements Commands {

    @Override
    public <T> void register(String name, T source, CommandDispatcher<T> dispatcher) {
        ClientCommandHandler.instance.registerCommand(new ForgeCommand<T>(name, source, dispatcher));
    }
}
