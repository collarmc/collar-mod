package team.catgirl.collar.mod.commands.arguments;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import team.catgirl.collar.api.groups.Group;
import team.catgirl.collar.api.waypoints.Waypoint;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.mod.commands.exceptions.CommandTargetNotFoundException;
import team.catgirl.collar.mod.service.CollarService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class WaypointArgumentType implements ArgumentType<WaypointArgumentType.WaypointArgument> {

    private final CollarService collarService;
    private final boolean privateWaypoints;

    public WaypointArgumentType(CollarService collarService, boolean privateWaypoints) {
        this.collarService = collarService;
        this.privateWaypoints = privateWaypoints;
    }

    @Override
    public WaypointArgument parse(StringReader reader) throws CommandSyntaxException {
        String input = reader.readUnquotedString();
        return waypoints().stream().filter(waypoint -> input.equals(waypoint.waypoint.name))
                .findFirst()
                .orElseThrow(() -> new CommandTargetNotFoundException("waypoint '" + input +  "' not found"));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        waypoints().stream()
                .filter(waypoint -> waypoint.waypoint.name.toLowerCase().startsWith(builder.getRemaining().toLowerCase()))
                .forEach(waypoint -> builder.suggest(waypoint.waypoint.name));
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return waypoints().stream().limit(3).map(waypoint -> waypoint.waypoint.name).collect(Collectors.toList());
    }

    private List<WaypointArgument> waypoints() {
        if (!collarService.getCollar().isPresent()) {
            return ImmutableList.of();
        }
        Collar collar = collarService.getCollar().get();
        if (privateWaypoints) {
            return collar.location().privateWaypoints().stream()
                    .map(waypoint -> new WaypointArgument(waypoint, null))
                    .collect(Collectors.toList());
        } else {
            List<WaypointArgument> waypointArguments = new ArrayList<>();
            for (Group group : collar.groups().all()) {
                waypointArguments.addAll(collar.location().groupWaypoints(group).stream()
                        .map(waypoint -> new WaypointArgument(waypoint, group))
                        .collect(Collectors.toList()));
            }
            return waypointArguments;
        }
    }

    public static class WaypointArgument {
        public final Waypoint waypoint;
        public final Group group;

        public WaypointArgument(Waypoint waypoint, Group group) {
            this.waypoint = waypoint;
            this.group = group;
        }
    }
}
