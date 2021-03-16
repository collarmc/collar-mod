package team.catgirl.collar.mod.common.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import team.catgirl.collar.api.location.Dimension;
import team.catgirl.plastic.brigadier.CommandTargetNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class DimensionArgumentType implements ArgumentType<Dimension> {

    public static DimensionArgumentType dimension() {
        return new DimensionArgumentType();
    }

    @Override
    public Dimension parse(StringReader reader) throws CommandSyntaxException {
        String input = reader.readUnquotedString();
        return dimensions().stream().filter(dimension -> input.toLowerCase().equals(dimension.toLowerCase(Locale.ROOT)))
                .findFirst().map(s -> Dimension.valueOf(s.toUpperCase()))
                .orElseThrow(() -> new CommandTargetNotFoundException("dimension '" + reader.readUnquotedString() +  "' not found"));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        dimensions().stream()
                .filter(dimension -> dimension.toLowerCase().startsWith(builder.getRemaining().toLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return dimensions();
    }

    private List<String> dimensions() {
        return stream(Dimension.values()).filter(dimension -> dimension != Dimension.UNKNOWN)
                .map(dimension -> dimension.name().toLowerCase()).collect(Collectors.toList());
    }
}
