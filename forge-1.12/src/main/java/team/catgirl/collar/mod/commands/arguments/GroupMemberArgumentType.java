package team.catgirl.collar.mod.commands.arguments;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import team.catgirl.collar.api.groups.Member;
import team.catgirl.collar.mod.commands.exceptions.CommandTargetNotFoundException;
import team.catgirl.plastic.Plastic;
import team.catgirl.collar.mod.service.CollarService;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class GroupMemberArgumentType implements ArgumentType<Member> {

    private final CollarService collarService;
    private final Plastic plastic;

    public GroupMemberArgumentType(CollarService collarService, Plastic plastic) {
        this.collarService = collarService;
        this.plastic = plastic;
    }

    @Override
    public Member parse(StringReader reader) throws CommandSyntaxException {
        String input = reader.readUnquotedString();
        return members().stream()
                .filter(thePlayer -> thePlayer.profile.name.equals(input))
                .findFirst().orElseThrow(() -> new CommandTargetNotFoundException("group member '" + reader.readUnquotedString() +  "' not found"));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        members().forEach(member -> {
            // Allow searching by the players minecraft name
            if (member.player.minecraftPlayer != null) {
                plastic.world.allPlayers().stream().filter(player -> player.id().equals(member.player.minecraftPlayer.id)).findFirst().ifPresent(player -> {
                    if (player.name().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                        builder.suggest(player.name());
                    }
                });
            }
            if (member.profile.name.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                builder.suggest(member.profile.name);
            }
        });
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return members().stream().limit(5).map(GroupMemberArgumentType::name).collect(Collectors.toList());
    }

    private static String name(Member member) {
        return member.profile.name;
    }

    private List<Member> members() {
        if (!collarService.getCollar().isPresent()) {
            return ImmutableList.of();
        }
        Collection<Member> values = collarService.getCollar().get().groups().all().stream()
                .flatMap(group -> group.members.stream())
                .collect(Collectors.toMap(member -> member.player.profile, o -> o, (member, member2) -> member)).values();
        return ImmutableList.copyOf(values);
    }
}
