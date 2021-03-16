package team.catgirl.plastic.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import team.catgirl.plastic.Plastic;
import team.catgirl.plastic.brigadier.CommandTargetNotFoundException;
import team.catgirl.plastic.ui.Commands;

public class FabricCommands implements Commands {
    @Override
    public <T> void register(String name, T source, CommandDispatcher<T> plasticDispatcher) {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal(name).executes(context -> {
                try {
                    int result = plasticDispatcher.execute(new StringReader(context.getInput()), source);
                    if (result <= 0) {
                        Plastic.getPlastic().display.displayMessage(getUsage(name, source, plasticDispatcher));
                    }
                } catch (CommandTargetNotFoundException e) {
                    Plastic.getPlastic().display.displayErrorMessage(e.getMessage());
                } catch (CommandSyntaxException e) {
                    if (e.getMessage() != null) {
                        Plastic.getPlastic().display.displayErrorMessage(e.getMessage());
                    }
                    Plastic.getPlastic().display.displayErrorMessage(getUsage(name, source, plasticDispatcher));
                }
                return 1;
            }));
        });
    }

    public static LiteralArgumentBuilder<ServerCommandSource> literal(final String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public <T> String getUsage(String name, T source, CommandDispatcher<T> dispatcher) {
        StringBuilder builder = new StringBuilder();
        builder.append("Usages:");
        for (String s : dispatcher.getAllUsage(dispatcher.getRoot(), source, true)) {
            builder.append("\n").append("/").append(name).append(" ").append(s);
        }
        return builder.toString();
    }
}
