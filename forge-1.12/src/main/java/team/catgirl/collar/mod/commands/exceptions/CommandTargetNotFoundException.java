package team.catgirl.collar.mod.commands.exceptions;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public final class CommandTargetNotFoundException extends CommandSyntaxException {
    public CommandTargetNotFoundException(String message) {
        super(new SimpleCommandExceptionType(new LiteralMessage(message)), new LiteralMessage(message));
    }
}
