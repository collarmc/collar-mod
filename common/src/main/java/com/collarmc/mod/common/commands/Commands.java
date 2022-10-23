package com.collarmc.mod.common.commands;

import com.collarmc.api.friends.Friend;
import com.collarmc.api.friends.Status;
import com.collarmc.api.groups.Group;
import com.collarmc.api.groups.GroupType;
import com.collarmc.api.location.Dimension;
import com.collarmc.api.location.Location;
import com.collarmc.api.minecraft.MinecraftPlayer;
import com.collarmc.api.waypoints.Waypoint;
import com.collarmc.client.api.groups.GroupInvitation;
import com.collarmc.mod.common.CollarService;
import com.collarmc.mod.common.commands.arguments.*;
import com.collarmc.mod.common.commands.arguments.IdentityArgumentType.IdentityArgument;
import com.collarmc.mod.common.commands.arguments.WaypointArgumentType.WaypointArgument;
import com.collarmc.mod.common.features.messaging.Messages;
import com.collarmc.plastic.Plastic;
import com.collarmc.plastic.player.Player;
import com.collarmc.plastic.ui.TextColor;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.collarmc.mod.common.commands.arguments.DimensionArgumentType.dimension;
import static com.collarmc.mod.common.commands.arguments.GroupArgumentType.getGroup;
import static com.collarmc.mod.common.commands.arguments.InvitationArgumentType.getInvitation;
import static com.collarmc.mod.common.commands.arguments.PlayerArgumentType.getPlayer;
import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.DoubleArgumentType.getDouble;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

public final class Commands<S> {

    private final CollarService collarService;
    private final Messages messages;
    private final Plastic plastic;
    private final boolean prefixed;

    public Commands(CollarService collarService, Messages messages, Plastic plastic, boolean prefixed) {
        this.collarService = collarService;
        this.messages = messages;
        this.plastic = plastic;
        this.prefixed = prefixed;
    }

    public void register(CommandDispatcher<S> dispatcher) {
        registerServiceCommands(dispatcher);
        registerFriendCommands(dispatcher);
        registerLocationCommands(dispatcher);
        registerWaypointCommands(dispatcher);
        registerGroupCommands(GroupType.PARTY, dispatcher);
        registerGroupCommands(GroupType.GROUP, dispatcher);
        registerChatCommands(dispatcher);
    }

    private LiteralArgumentBuilder<S> prefixed(String name, LiteralArgumentBuilder<S> argumentBuilder) {
        return this.prefixed ? literal("collar").then(literal(name).then(argumentBuilder)) : literal(name).then(argumentBuilder);
    }

    private LiteralArgumentBuilder<S> prefixed(String name, ArgumentBuilder<S, ?> argument) {
        return this.prefixed ? literal("collar").then(literal(name).then(argument)) : literal(name).then(argument);
    }

    private LiteralArgumentBuilder<S> prefixed(String name, Command<S> command) {
        return this.prefixed ? literal("collar").then(literal(name).executes(command)) : literal(name).executes(command);
    }

    private void registerServiceCommands(CommandDispatcher<S> dispatcher) {
        // collar connect
        dispatcher.register(prefixed("connect", context -> {
            collarService.connect();
            return 1;
        }));

        // collar disconnect
        dispatcher.register(prefixed("disconnect", context -> {
            collarService.disconnect();
            return 1;
        }));

        // collar status
        dispatcher.register(prefixed("status", context -> {
            collarService.with(collar -> {
                plastic.display.displayInfoMessage("Collar is " + collar.getState().name().toLowerCase());
            }, () -> plastic.display.displayMessage("Collar is disconnected"));
            return 1;
        }));

        // collar me
        dispatcher.register(prefixed("me", context -> {
            collarService.with(collar -> {
                collar.identities().resolveProfile(collar.player()).thenAccept(publicProfile -> {
                    Player player = plastic.world.findPlayerById(collar.player().minecraftPlayer.id).orElseThrow(() -> new IllegalStateException("should have been able to find self"));
                    publicProfile.ifPresent(profile -> plastic.display.displayInfoMessage("You are connected as " + profile.name + " on minecraft account " + player.name()));
                });
            });
            return 1;
        }));
    }

    private void registerFriendCommands(CommandDispatcher<S> dispatcher) {
        // collar friend add [user]
        dispatcher.register(prefixed("friend", literal("add")
                .then(argument("name", identity())
                        .executes(context -> {
                            collarService.with(collar -> {
                                IdentityArgument player = context.getArgument("name", IdentityArgument.class);
                                if (player.player != null) {
                                    collar.friends().addFriend(new MinecraftPlayer(player.player.id(), collar.player().minecraftPlayer.server, collar.player().minecraftPlayer.networkId));
                                } else if (player.profile != null) {
                                    collar.friends().addFriend(player.profile.id);
                                }
                            });
                            return 1;
                        }))));

        // collar friend remove [user]
        dispatcher.register(prefixed("friend", literal("remove")
                .then(argument("name", identity())
                        .executes(context -> {
                            collarService.with(collar -> {
                                IdentityArgument player = context.getArgument("name", IdentityArgument.class);
                                if (player.player != null) {
                                    collar.friends().removeFriend(new MinecraftPlayer(player.player.id(), collar.player().minecraftPlayer.server, collar.player().minecraftPlayer.networkId));
                                } else if (player.profile != null) {
                                    collar.friends().removeFriend(player.profile.id);
                                } else {
                                    throw new IllegalStateException("was not profile or player");
                                }
                            });
                            return 1;
                        }))));

        // collar friend list
        dispatcher.register(prefixed("friend", literal("list")
                .executes(context -> {
                    collarService.with(collar -> {
                        Set<Friend> friends = collar.friends().list();
                        if (friends.isEmpty()) {
                            plastic.display.displayInfoMessage("You don't have any friends");
                        } else {
                            friends.stream().sorted(Comparator.comparing(o -> o.status)).forEach(friend -> {
                                TextColor color = friend.status.equals(Status.ONLINE) ? TextColor.GREEN : TextColor.GRAY;
                                plastic.display.displayMessage(plastic.display.newTextBuilder().add(friend.profile.name, color));
                            });
                        }
                    });
                    return 1;
                })));
    }

    private void registerGroupCommands(GroupType type, CommandDispatcher<S> dispatcher) {
        // collar party create [name]
        dispatcher.register(prefixed(type.name, literal("create")
                .then(argument("name", string())
                        .executes(context -> {
                            collarService.with(collar -> {
                                collar.groups().create(getString(context, "name"), type, ImmutableList.of());
                            });
                            return 1;
                        }))));

        // collar party delete [name]
        dispatcher.register(prefixed(type.name, literal("delete")
                .then(argument("name", group(type))
                        .executes(context -> {
                            collarService.with(collar -> {
                                collar.groups().delete(getGroup(context, "name"));
                            });
                            return 1;
                        }))));

        // collar party leave [name]
        dispatcher.register(prefixed(type.name, literal("leave")
                .then(argument("name", group(type))
                        .executes(context -> {
                            collarService.with(collar -> {
                                collar.groups().leave(getGroup(context, "name"));
                            });
                            return 1;
                        }))));

        // collar par/**/ty invites
        dispatcher.register(prefixed(type.name, literal("invites")
                .executes(context -> {
                    collarService.with(collar -> {
                        List<GroupInvitation> invitations = collar.groups().invitations().stream()
                                .filter(invitation -> invitation.type == type)
                                .collect(Collectors.toList());
                        if (invitations.isEmpty()) {
                            plastic.display.displayInfoMessage("You have no invites to any " + type.plural);
                        } else {
                            plastic.display.displayInfoMessage("You have invites to:");
                            invitations.forEach(invitation -> plastic.display.displayInfoMessage(invitation.name));
                            plastic.display.displayInfoMessage("To accept type '/collar " + type.name  + " accept [name]");
                        }
                    });
                    return 1;
                })));

        // collar party accept [name]
        dispatcher.register(prefixed(type.name, literal("accept")
                .then(argument("groupName", invitation(type))
                        .executes(context -> {
                            collarService.with(collar -> {
                                collar.groups().accept(getInvitation(context, "groupName"));
                            });
                            return 1;
                        }))));

        // collar party list
        dispatcher.register(prefixed(type.name, literal("list")
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

        // collar party add [name] [player]
        dispatcher.register(prefixed(type.name, literal("add")
                .then(argument("groupName", group(type))
                        .then(argument("playerName", player())
                                .executes(context -> {
                                    collarService.with(collar -> {
                                        Group group = getGroup(context, "groupName");
                                        Player player = getPlayer(context, "playerName");
                                        collar.groups().invite(group, ImmutableList.of(player.id()));
                                    });
                                    return 1;
                                })))));

        // collar party remove [name] [player]
        dispatcher.register(prefixed(type.name, literal("remove")
                .then(argument("groupName", group(type))
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

        // collar party members [name]
        dispatcher.register(prefixed(type.name, literal("members")
                .then(argument("groupName", group(type)))
                .executes(context -> {
                    collarService.with(collar -> {
                        Group group = getGroup(context, "groupName");
                        plastic.display.displayMessage("Members:");
                        group.members.forEach(member -> {
                            Optional<Player> thePlayer = plastic.world.allPlayers().stream().filter(player -> member.player.minecraftPlayer.id.equals(player.id())).findFirst();
                            String message;
                            if (thePlayer.isPresent()) {
                                message = member.profile.name + " playing as " + member.player.minecraftPlayer.id + " (" + member.membershipRole.name() + ")";
                            } else {
                                message = member.profile.name;
                            }
                            plastic.display.displayMessage(message + "(" + member.membershipRole.name() + ")");
                        });
                    });
                    return 1;
                })));
    }

    private void registerLocationCommands(CommandDispatcher<S> dispatcher) {
        // collar location share start [any group name]
        dispatcher.register(prefixed("location", literal("share")
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
        dispatcher.register(prefixed("location", literal("share")
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
        dispatcher.register(prefixed("location", literal("share")
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

    private void registerWaypointCommands(CommandDispatcher<S> dispatcher) {

        // collar location waypoint add [name]
        dispatcher.register(prefixed("waypoint", literal("add")
                .then(argument("name", string())
                        .executes(context -> {
                            collarService.with(collar -> {
                                Location location = plastic.world.currentPlayer().location();
                                collar.location().addWaypoint(getString(context, "name"), location);
                            });
                            return 1;
                        }))));

        // collar waypoint remove [name]
        dispatcher.register(prefixed("waypoint", literal("remove")
                .then(argument("name", privateWaypoint())
                        .executes(context -> {
                            collarService.with(collar -> {
                                WaypointArgument argument = context.getArgument("name", WaypointArgument.class);
                                collar.location().removeWaypoint(argument.waypoint);
                            });
                            return 1;
                        }))));

        // collar location waypoint list
        dispatcher.register(prefixed("waypoint", literal("list")
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
        dispatcher.register(prefixed("waypoint", literal("list")
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
        dispatcher.register(prefixed("waypoint", literal("add")
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
        dispatcher.register(prefixed("waypoint", literal("remove")
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
        dispatcher.register(prefixed("waypoint", literal("add")
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

    private void registerChatCommands(CommandDispatcher<S> dispatcher) {
        // /msg player2 OwO
        dispatcher.register(literal("msg")
                .then(argument("recipient", player())
                        .then(argument("rawMessage", string())
                                .executes(context -> {
                                    Player recipient = getPlayer(context, "recipient");
                                    String message = getString(context, "rawMessage");
                                    messages.sendMessage(recipient, message);
                                    return 1;
                                }))));

        // collar chat with coolkids
        dispatcher.register(prefixed("chat", literal("with")
                .then(argument("group", groups())
                        .executes(context -> {
                            Group group = getGroup(context, "group");
                            messages.switchToGroup(group);
                            return 1;
                        }))));

        // collar chat off
        dispatcher.register(prefixed("chat", literal("off").executes(context -> {
            messages.switchToGeneralChat();
            return 1;
        })));
    }

    public <T> RequiredArgumentBuilder<S, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    private LiteralArgumentBuilder<S> literal(final String name) {
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