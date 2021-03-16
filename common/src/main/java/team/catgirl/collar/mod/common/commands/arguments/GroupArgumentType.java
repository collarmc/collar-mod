package team.catgirl.collar.mod.common.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import team.catgirl.collar.api.groups.Group;
import team.catgirl.collar.api.groups.GroupType;
import team.catgirl.plastic.brigadier.CommandTargetNotFoundException;
import team.catgirl.collar.mod.service.CollarService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GroupArgumentType implements ArgumentType<Group> {

    private final CollarService collarService;
    private final GroupType type;

    public GroupArgumentType(CollarService collarService, GroupType type) {
        this.collarService = collarService;
        this.type = type;
    }

    public static Group getGroup(CommandContext<?> context, String name) {
        return context.getArgument(name, Group.class);
    }

    @Override
    public Group parse(StringReader reader) throws CommandSyntaxException {
        if (!collarService.getCollar().isPresent()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Collar not connected");
        }
        String input = reader.readUnquotedString();
        return groupList().stream()
                .filter(group -> group.name.equals(input))
                .findFirst().orElseThrow(() -> new CommandTargetNotFoundException("group '" + input +  "' not found"));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        groupList().stream()
                .filter(group -> type == null || group.type.equals(type))
                .filter(group -> group.name.toLowerCase().startsWith(builder.getRemaining().toLowerCase()))
                .forEach(group -> builder.suggest(group.name));
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return groupList().stream()
                .limit(3).map(group -> group.name).collect(Collectors.toList());
    }

    List<Group> groupList() {
        if (!collarService.getCollar().isPresent()) {
            return Collections.emptyList();
        }
        if (type == null) {
            return collarService.getCollar().get().groups().matching(GroupType.GROUP, GroupType.PARTY);
        } else {
            return collarService.getCollar().get().groups().matching(type);
        }
    }
}
