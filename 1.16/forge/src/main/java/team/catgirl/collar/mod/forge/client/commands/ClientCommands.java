package team.catgirl.collar.mod.forge.client.commands;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import team.catgirl.collar.mod.forge.client.CollarForge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ClientCommands {
    private static final char PREFIX = '/';
    public static final CommandDispatcher<ICommandSource> DISPATCHER = new CommandDispatcher<>();
    private static final String API_COMMAND_NAME = "collar-forgehax-command";
    private static final String SHORT_API_COMMAND_NAME = "cfc";

    /**
     *
     * @param someString command candidate message
     * @return true, if don't send the message to the server.
     */
    public static boolean executeCommand(String someString){
        if(someString.isEmpty()){
            return false;
        }
        if(someString.charAt(0) != PREFIX){
            return false;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ICommandSource commandSource = (ICommandSource) Objects.requireNonNull(client.getNetworkHandler()).getCommandSource();

        client.getProfiler().push(someString);

        try {
            DISPATCHER.execute(someString.substring(1), commandSource);
            return true;
        } catch (CommandSyntaxException e){
            boolean ignored = isIgnoredException(e.getType());
            if(ignored) return false;

            commandSource.sendError(getErrorMessage(e));
            return true;
        } catch (CommandException ce){
            commandSource.sendError(ce.getTextMessage());
            return true;
        } catch (RuntimeException e){
            commandSource.sendError(Text.of(e.getMessage()));
            return true;
        } finally {
            client.getProfiler().pop();
        }
    }

    private static boolean isIgnoredException(CommandExceptionType type) {
        BuiltInExceptionProvider builtins = CommandSyntaxException.BUILT_IN_EXCEPTIONS;
        return type == builtins.dispatcherUnknownCommand() || type == builtins.dispatcherParseException();
    }

    private static Text getErrorMessage(CommandSyntaxException exception){
        Text msg = Texts.toText(exception.getRawMessage());
        String ctx = exception.getContext();

        return ctx != null ? new TranslatableText("command.context.parse_error", msg, ctx) : msg;
    }

    private static int executeRootHelp(CommandContext<ICommandSource> context) {
        return executeHelp(DISPATCHER.getRoot(), context);
    }
    private static int executeArgumentHelp(CommandContext<ICommandSource> context) throws CommandSyntaxException {
        ParseResults<ICommandSource> parseResults = DISPATCHER.parse(StringArgumentType.getString(context, "command"), context.getSource());
        List<ParsedCommandNode<ICommandSource>> nodes = parseResults.getContext().getNodes();

        if (nodes.isEmpty()) {
            //TODO idk??? 			throw HelpCommandAccessor.getFailedException().create();
        }

        return executeHelp(Iterables.getLast(nodes).getNode(), context);
    }
    private static int executeHelp(CommandNode<ICommandSource> startNode, CommandContext<ICommandSource> context) {
        Map<CommandNode<ICommandSource>, String> commands = DISPATCHER.getSmartUsage(startNode, context.getSource());

        for (String command : commands.values()) {
            context.getSource().sendFeedback(new LiteralText("/" + command));
        }

        return commands.size();
    }
    public static void finalizeInit() {
        if (!DISPATCHER.getRoot().getChildren().isEmpty()) {
            // Register an API command if there are other commands;
            // these helpers are not needed if there are no client commands
            LiteralArgumentBuilder<ICommandSource> help = literal("help");
            help.executes(ClientCommands::executeRootHelp);
            help.then(argument("command", StringArgumentType.greedyString()).executes(ClientCommands::executeArgumentHelp));

            CommandNode<ICommandSource> mainNode = DISPATCHER.register(literal(API_COMMAND_NAME).then(help));
            DISPATCHER.register(literal(SHORT_API_COMMAND_NAME).redirect(mainNode));
        }

        // noinspection CodeBlock2Expr
        DISPATCHER.findAmbiguities((parent, child, sibling, inputs) -> {
            CollarForge.LOGGER.warning("Ambiguity between arguments " + DISPATCHER.getPath(child) + " and " + DISPATCHER.getPath(sibling) +" with inputs: " + inputs);
        });
    }

    private static LiteralArgumentBuilder<ICommandSource> literal(String s){
        return LiteralArgumentBuilder.literal(s);
    }
    private static <T> RequiredArgumentBuilder<ICommandSource, T> argument(String command, ArgumentType<T> a) {
        return RequiredArgumentBuilder.argument(command, a);
    }

    public static void addCommands(CommandDispatcher<ICommandSource> target, ICommandSource source) {
        Map<CommandNode<ICommandSource>, CommandNode<ICommandSource>> originalToCopy = new HashMap<>();
        originalToCopy.put(DISPATCHER.getRoot(), target.getRoot());
        copyChildren(DISPATCHER.getRoot(), target.getRoot(), source, originalToCopy);
    }
    private static void copyChildren(
            CommandNode<ICommandSource> origin,
            CommandNode<ICommandSource> target,
            ICommandSource source,
            Map<CommandNode<ICommandSource>, CommandNode<ICommandSource>> originalToCopy
    ) {
        for (CommandNode<ICommandSource> child : origin.getChildren()) {
            if (!child.canUse(source)) continue;

            ArgumentBuilder<ICommandSource, ?> builder = child.createBuilder();

            // Reset the unnecessary non-completion stuff from the builder
            builder.requires(s -> true); // This is checked with the if check above.

            if (builder.getCommand() != null) {
                builder.executes(context -> 0);
            }

            // Set up redirects
            if (builder.getRedirect() != null) {
                builder.redirect(originalToCopy.get(builder.getRedirect()));
            }

            CommandNode<ICommandSource> result = builder.build();
            originalToCopy.put(child, result);
            target.addChild(result);

            if (!child.getChildren().isEmpty()) {
                copyChildren(child, result, source, originalToCopy);
            }
        }
    }
}
