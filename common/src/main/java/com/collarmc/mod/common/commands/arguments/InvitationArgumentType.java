package com.collarmc.mod.common.commands.arguments;

import com.collarmc.mod.common.CollarService;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.collarmc.api.groups.GroupType;
import com.collarmc.client.Collar;
import com.collarmc.client.api.groups.GroupInvitation;
import com.collarmc.plastic.brigadier.CommandTargetNotFoundException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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
        Collar collar = collarService.getCollar().get();
        if (collar.getState() != Collar.State.CONNECTED) {
            return builder.buildFuture();
        }
        collar.groups().invitations().stream().filter(invitation -> invitation.type.equals(type))
                .filter(invitation -> invitation.name.toLowerCase().startsWith(builder.getRemaining().toLowerCase()))
                .forEach(group -> builder.suggest(group.name));
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        if (!collarService.getCollar().isPresent()) {
            return Collections.emptyList();
        }
        Collar collar = collarService.getCollar().get();
        if (collar.getState() != Collar.State.CONNECTED) {
            return new HashSet<>();
        }
        return collar.groups().invitations().stream()
                .filter(invitation -> invitation.type.equals(type))
                .limit(3)
                .map(invitation -> invitation.name)
                .collect(Collectors.toList());
    }
}
