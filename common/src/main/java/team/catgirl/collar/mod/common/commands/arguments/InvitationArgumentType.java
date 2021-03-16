package team.catgirl.collar.mod.common.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import team.catgirl.collar.api.groups.GroupType;
import team.catgirl.collar.client.api.groups.GroupInvitation;
import team.catgirl.plastic.brigadier.CommandTargetNotFoundException;
import team.catgirl.collar.mod.service.CollarService;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class InvitationArgumentType implements ArgumentType<GroupInvitation> {

    private final CollarService collarService;
    private final GroupType type;

    public InvitationArgumentType(CollarService collarService, GroupType type) {
        this.collarService = collarService;
        this.type = type;
    }

    public static GroupInvitation getInvitation(CommandContext<?> context, String name) {
        return context.getArgument(name, GroupInvitation.class);
    }

    @Override
    public GroupInvitation parse(StringReader reader) throws CommandSyntaxException {
        if (!collarService.getCollar().isPresent()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Collar not connected");
        }
        String input = reader.readUnquotedString();
        return collarService.getCollar().get().groups().invitations().stream()
                .filter(invitation -> invitation.type.equals(type))
                .filter(invitation -> invitation.name.equals(input))
                .findFirst().orElseThrow(() -> new CommandTargetNotFoundException("invitation to group '" + input +  "' not found"));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (!collarService.getCollar().isPresent()) {
            return builder.buildFuture();
        }
        collarService.getCollar().get().groups().invitations().stream().filter(invitation -> invitation.type.equals(type))
                .filter(invitation -> invitation.name.toLowerCase().startsWith(builder.getRemaining().toLowerCase()))
                .forEach(group -> builder.suggest(group.name));
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        if (!collarService.getCollar().isPresent()) {
            return Collections.emptyList();
        }
        return collarService.getCollar().get().groups().invitations().stream()
                .filter(invitation -> invitation.type.equals(type))
                .limit(3)
                .map(invitation -> invitation.name)
                .collect(Collectors.toList());
    }
}
