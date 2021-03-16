package team.catgirl.collar.mod.common.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import team.catgirl.collar.api.friends.Friend;
import team.catgirl.collar.api.friends.Status;
import team.catgirl.collar.api.groups.Group;
import team.catgirl.collar.api.groups.GroupType;
import team.catgirl.collar.api.location.Dimension;
import team.catgirl.collar.api.location.Location;
import team.catgirl.collar.api.waypoints.Waypoint;
import team.catgirl.collar.mod.common.CollarService;
import team.catgirl.collar.mod.common.commands.arguments.*;
import team.catgirl.collar.mod.common.commands.arguments.IdentityArgumentType.IdentityArgument;
import team.catgirl.collar.mod.common.commands.arguments.WaypointArgumentType.WaypointArgument;
import team.catgirl.plastic.Plastic;
import team.catgirl.plastic.player.Player;
import team.catgirl.plastic.ui.TextFormatting;
import team.catgirl.plastic.world.Position;
import team.catgirl.collar.security.mojang.MinecraftPlayer;

import java.util.*;
import java.util.stream.Collectors;

import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.DoubleArgumentType.getDouble;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static team.catgirl.collar.mod.common.commands.arguments.DimensionArgumentType.dimension;
import static team.catgirl.collar.mod.common.commands.arguments.GroupArgumentType.getGroup;
import static team.catgirl.collar.mod.common.commands.arguments.InvitationArgumentType.getInvitation;
import static team.catgirl.collar.mod.common.commands.arguments.PlayerArgumentType.getPlayer;

public class Commands {

    private final CollarService collarService;
    private final Plastic plastic;

    public Commands(CollarService collarService, Plastic plastic) {
        this.collarService = collarService;
        this.plastic = plastic;
    }

    public CommandDispatcher<CollarService> create() {
        CommandDispatcher<CollarService> dispatcher = new CommandDispatcher<>();
        registerServiceCommands(dispatcher);
        registerFriendCommands(dispatcher);
        registerLocationCommands(dispatcher);
        registerWaypointCommands(dispatcher);
        registerGroupCommands(GroupType.PARTY, dispatcher);
        registerGroupCommands(GroupType.GROUP, dispatcher);
        return dispatcher;
    }

    private void registerServiceCommands(CommandDispatcher<CollarService> dispatcher) {
        // collar connect
        dispatcher.register(literal("connect").executes(context -> {
            collarService.connect();
            return 1;
        }));

        // collar disconnect
        dispatcher.register(literal("disconnect").executes(context -> {
            collarService.disconnect();
            return 1;
        }));

        // collar status
        dispatcher.register(literal("status").executes(context -> {
            collarService.with(collar -> {
                plastic.display.displayInfoMessage("Collar is " + collar.getState().name().toLowerCase());
            }, () -> plastic.display.displayMessage("Collar is disconnected"));
            return 1;
        }));
    }

    private void registerFriendCommands(CommandDispatcher<CollarService> dispatcher) {
        // collar friend add [user]
        dispatcher.register(literal("friend")
            .then(literal("add")
                .then(argument("name", identity())
                    .executes(context -> {
                        collarService.with(collar -> {
                            IdentityArgument player = context.getArgument("name", IdentityArgument.class);
                            if (player.player != null) {
                                collar.friends().addFriend(new MinecraftPlayer(player.player.id(), collar.player().minecraftPlayer.server));
                            } else if (player.profile != null) {
                                collar.friends().addFriend(player.profile.id);
                            }
                        });
                        return 1;
                    }))));

        // collar friend remove [user]
        dispatcher.register(literal("friend")
                .then(literal("remove")
                .then(argument("name", identity())
                .executes(context -> {
                    collarService.with(collar -> {
                        IdentityArgument player = context.getArgument("name", IdentityArgument.class);
                        if (player.player != null) {
                            collar.friends().removeFriend(new MinecraftPlayer(player.player.id(), collar.player().minecraftPlayer.server));
                        } else if (player.profile != null) {
                            collar.friends().removeFriend(player.profile.id);
                        } else {
                            throw new IllegalStateException("was not profile or player");
                        }
                    });
                    return 1;
                }))));

        // collar friend list
        dispatcher.register(literal("friend")
                .then(literal("list")
                .executes(context -> {
                    collarService.with(collar -> {
                        Set<Friend> friends = collar.friends().list();
                        if (friends.isEmpty()) {
                            plastic.display.displayInfoMessage("You don't have any friends");
                        } else {
                            friends.stream().sorted(Comparator.comparing(o -> o.status)).forEach(friend -> {
                                TextFormatting color = friend.status.equals(Status.ONLINE) ? TextFormatting.GREEN : TextFormatting.GRAY;
                                plastic.display.displayMessage(plastic.display.newTextBuilder().add(friend.friend.name, color));
                            });
                        }
                    });
                    return 1;
                })));
    }

    private void registerGroupCommands(GroupType type, CommandDispatcher<CollarService> dispatcher) {
        // collar party create [name]
        dispatcher.register(literal(type.name)
                .then(literal("create")
                .then(argument("name", string())
                .executes(context -> {
                    collarService.with(collar -> {
                        collar.groups().create(getString(context, "name"), GroupType.PARTY, ImmutableList.of());
                    });
                    return 1;
                }))));

        // collar party delete [name]
        dispatcher.register(literal(type.name)
                .then(literal("delete")
                .then(argument("name", group(type))
                .executes(context -> {
                    collarService.with(collar -> {
                        collar.groups().delete(getGroup(context, "name"));
                    });
                    return 1;
                }))));

        // collar party leave [name]
        dispatcher.register(literal(type.name)
                .then(literal("leave")
                .then(argument("name", group(type))
                .executes(context -> {
                    collarService.with(collar -> {
                        collar.groups().leave(getGroup(context, "name"));
                    });
                    return 1;
                }))));

        // collar party accept [name]
        dispatcher.register(literal(type.name)
                .then(literal("accept")
                .then(argument("groupName", invitation(type))
                .executes(context -> {
                    collarService.with(collar -> {
                        collar.groups().accept(getInvitation(context, "groupName"));
                    });
                    return 1;
                }))));

        // collar party list
        dispatcher.register(literal(type.name)
                .then(literal("list")
                .executes(context -> {
                    collarService.with(collar -> {
                        List<Group> parties = collar.groups().all().stream()
                                .filter(group -> group.type.equals(type))
                                .collect(Collectors.toList());
                        if (parties.isEmpty()) {
                            plastic.display.displayInfoMessage("You are not a member of any " + type.plural);
                        } else {
                            plastic.display.displayInfoMessage("You belong to the following " + type.plural + ":");
                            parties.forEach(group -> plastic.display.displayInfoMessage(group.name));
                        }
                    });
                    return 1;
                })));

        // collar party [name] add [player]
        dispatcher.register(literal(type.name)
                .then(argument("groupName", group(type))
                .then(literal("add")
                .then(argument("playerName", player())
                .executes(context -> {
                    collarService.with(collar -> {
                        Group group = getGroup(context, "groupName");
                        Player player = getPlayer(context, "playerName");
                        collar.groups().invite(group, ImmutableList.of(player.id()));
                    });
                    return 1;
                })))));

        // collar party [name] remove [player]
        dispatcher.register(literal(type.name)
                .then(argument("groupName", group(type))
                .then(literal("remove")
                .then(argument("playerName", identity())
                .executes(context -> {
                    collarService.with(collar -> {
                        Group group = getGroup(context, "groupName");
                        IdentityArgument identity = context.getArgument("playerName", IdentityArgument.class);
                        group.members.stream().filter(candidate -> candidate.profile.id.equals(identity.profile.id)).findFirst().ifPresent(theMember -> {
                            collar.groups().removeMember(group, theMember);
                        });
                    });
                    return 1;
                })))));
    }

    private void registerLocationCommands(CommandDispatcher<CollarService> dispatcher) {
        // collar location share start [any group name]
        dispatcher.register(literal("location")
                .then(literal("share")
                .then(literal("start")
                .then(argument("groupName", groups())
                .executes(context -> {
                    collarService.with(collar -> {
                        Group group = getGroup(context, "groupName");
                        collar.location().startSharingWith(group);
                    });
                    return 1;
                })))));

        // collar location share stop [any group name]
        dispatcher.register(literal("location")
                .then(literal("share")
                .then(literal("stop")
                .then(argument("groupName", groups())
                .executes(context -> {
                    collarService.with(collar -> {
                        Group group = getGroup(context, "groupName");
                        collar.location().stopSharingWith(group);
                    });
                    return 1;
                })))));

        // collar location share stop [any group name]
        dispatcher.register(literal("location")
                .then(literal("share")
                .then(literal("list")
                .executes(context -> {
                        collarService.with(collar -> {
                            List<Group> active = collar.groups().all().stream()
                                    .filter(group -> collar.location().isSharingWith(group))
                                    .collect(Collectors.toList());
                            if (active.isEmpty()) {
                                plastic.display.displayInfoMessage("You are not sharing your location with any groups");
                            } else {
                                plastic.display.displayInfoMessage("You are sharing your location with groups:");
                                active.forEach(group -> plastic.display.displayInfoMessage(group.name + " (" + group.type.name + ")"));
                            }
                        });
                    return 1;
                }))));
    }

    private void registerWaypointCommands(CommandDispatcher<CollarService> dispatcher) {

        // collar location waypoint add [name]
        dispatcher.register(literal("waypoint")
                .then(literal("add")
                .then(argument("name", string())
                .executes(context -> {
                    collarService.with(collar -> {
                        Position pos = plastic.world.currentPlayer().position();
                        Dimension dimension = mapDimension();
                        Location location = new Location(pos.x, pos.y, pos.z, dimension);
                        collar.location().addWaypoint(getString(context, "name"), location);
                    });
                    return 1;
                }))));

        // collar waypoint remove [name]
        dispatcher.register(literal("waypoint")
                .then(literal("remove")
                .then(argument("name", privateWaypoint())
                .executes(context -> {
                    collarService.with(collar -> {
                        WaypointArgument argument = context.getArgument("name", WaypointArgument.class);
                        collar.location().removeWaypoint(argument.waypoint);
                    });
                    return 1;
                }))));

        // collar location waypoint list
        dispatcher.register(literal("waypoint")
                .then(literal("list")
                .executes(context -> {
                    collarService.with(collar -> {
                        Set<Waypoint> waypoints = collar.location().privateWaypoints();
                        if (waypoints.isEmpty()) {
                            plastic.display.displayInfoMessage("You have no private waypoints");
                        } else {
                            waypoints.forEach(waypoint -> plastic.display.displayInfoMessage(waypoint.displayName()));
                        }
                    });
                    return 1;
                })));

        // collar location waypoint list [any group name]
        dispatcher.register(literal("waypoint")
                .then(literal("list")
                .then(argument("group", groups())
                .executes(context -> {
                    collarService.with(collar -> {
                        Group group = getGroup(context, "group");
                        Set<Waypoint> waypoints = collar.location().groupWaypoints(group);
                        if (waypoints.isEmpty()) {
                            plastic.display.displayInfoMessage("You have no group waypoints");
                        } else {
                            waypoints.forEach(waypoint -> plastic.display.displayInfoMessage(waypoint.displayName()));
                        }
                    });
                    return 1;
                }))));

        // collar location waypoint add [name] [x] [y] [z] to [group]
        dispatcher.register(literal("waypoint")
                .then(literal("add")
                .then(argument("name", string())
                .then(argument("x", doubleArg())
                .then(argument("y", doubleArg())
                .then(argument("z", doubleArg())
                .then(argument("dimension", dimension())
                .then(literal("to")
                .then(argument("group", groups())
                .executes(context -> {
                    collarService.with(collar -> {
                        Group group = getGroup(context, "group");
                        Dimension dimension = context.getArgument("dimension", Dimension.class);
                        Location location = new Location(
                                getDouble(context, "x"),
                                getDouble(context, "y"),
                                getDouble(context, "z"),
                                dimension
                        );
                        collar.location().addWaypoint(group, getString(context, "name"), location);
                    });
                    return 1;
                }))))))))));

        // collar location waypoint remove [name] from [group]
        dispatcher.register(literal("waypoint")
                .then(literal("remove")
                .then(argument("name", groupWaypoint())
                .then(literal("from")
                .then(argument("group", groups())
                .executes(context -> {
                    collarService.with(collar -> {
                        Group group = getGroup(context, "group");
                        WaypointArgument argument = context.getArgument("waypoint", WaypointArgument.class);
                        if (!group.id.equals(argument.group.id)) {
                            collar.location().removeWaypoint(argument.group, argument.waypoint);
                        } else {
                            plastic.display.displayInfoMessage("Waypoint " + argument.waypoint + " does not belong to group " + group.name);
                        }
                    });
                    return 1;
                }))))));

        // collar waypoint add [name] [x] [y] [z]
        dispatcher.register(literal("waypoint")
                .then(literal("add")
                .then(argument("name", string())
                .then(argument("x", doubleArg())
                .then(argument("y", doubleArg())
                .then(argument("z", doubleArg())
                .then(argument("dimension", dimension())
                .executes(context -> {
                    collarService.with(collar -> {
                        Dimension dimension = context.getArgument("dimension", Dimension.class);
                        Location location = new Location(
                                getDouble(context, "x"),
                                getDouble(context, "y"),
                                getDouble(context, "z"),
                                dimension
                        );
                        collar.location().addWaypoint(getString(context, "name"), location);
                    });
                    return 1;
                }))))))));
    }

    private Dimension mapDimension() {
        Dimension dimension;
        switch (plastic.world.currentPlayer().dimension()) {
            case NETHER:
                dimension = Dimension.NETHER;
                break;
            case END:
                dimension = Dimension.END;
                break;
            case OVERWORLD:
                dimension = Dimension.OVERWORLD;
                break;
            default:
                dimension = Dimension.UNKNOWN;
        }
        return dimension;
    }

    public static <T> RequiredArgumentBuilder<CollarService, T> argument(final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public static LiteralArgumentBuilder<CollarService> literal(final String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    private GroupArgumentType group(GroupType type) {
        return new GroupArgumentType(collarService, type);
    }

    private GroupArgumentType groups() {
        return new GroupArgumentType(collarService, null);
    }

    private InvitationArgumentType invitation(GroupType type) {
        return new InvitationArgumentType(collarService, type);
    }

    private WaypointArgumentType privateWaypoint() {
        return new WaypointArgumentType(collarService, true);
    }

    private WaypointArgumentType groupWaypoint() {
        return new WaypointArgumentType(collarService, false);
    }

    private PlayerArgumentType player() {
        return new PlayerArgumentType(plastic);
    }

    private IdentityArgumentType identity() {
        return new IdentityArgumentType(collarService, plastic);
    }

    private GroupMemberArgumentType groupMember() {
        return new GroupMemberArgumentType(collarService, plastic);
    }
}
