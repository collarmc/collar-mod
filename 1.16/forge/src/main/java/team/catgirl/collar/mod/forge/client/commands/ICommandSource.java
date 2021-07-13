package team.catgirl.collar.mod.forge.client.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public interface ICommandSource extends CommandSource {
    void sendFeedback(Text msg);
    void sendError(Text msg);
}
