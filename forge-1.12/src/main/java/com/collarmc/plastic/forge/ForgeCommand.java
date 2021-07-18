/*
 * MIT License
 *
 * Copyright (c) 2020 Headpat Services
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.collarmc.plastic.forge;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import com.collarmc.plastic.brigadier.CommandTargetNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("NullableProblems")
public final class ForgeCommand<T> extends CommandBase {
	private final String name;
	private final T source;

	protected final CommandDispatcher<T> commandDispatcher;

	@Override
	public String getName() {
		return name;
	}

	public ForgeCommand(String name, T source, CommandDispatcher<T> dispatcher) {
		this.name = name;
		this.source = source;
		this.commandDispatcher = dispatcher;
	}

	@Override
	public final String getUsage( ICommandSender sender) {
		StringBuilder builder = new StringBuilder();
		builder.append("Usages:");
		for (String s : commandDispatcher.getAllUsage(commandDispatcher.getRoot(), source, true)) {
			builder.append("\n").append("/").append(getName()).append(" ").append(s);
		}
		return builder.toString();
	}

	@Override
	public final void execute( MinecraftServer server, ICommandSender sender, String[] args) {
		try {
			int result = this.commandDispatcher.execute(getCommandString(args), source);
			if (result <= 0) {
				sender.sendMessage(new TextComponentString(getUsage(sender)));
			}
		} catch (CommandTargetNotFoundException e) {
			sender.sendMessage(new TextComponentString(e.getMessage()));
		} catch (CommandSyntaxException e) {
			if (e.getMessage() != null) {
				sender.sendMessage(new TextComponentString(e.getMessage()));
			}
			sender.sendMessage(new TextComponentString(getUsage(sender)));
		}
	}

	@Override
	public final List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		String commandString = getCommandString(args);
		Suggestions suggestions = this.commandDispatcher.getCompletionSuggestions(this.commandDispatcher.parse(commandString, source)).join();
		return suggestions.getList().stream().map(Suggestion::getText).collect(Collectors.toList());
	}

	@Override
	public final List<String> getAliases() {
		return Collections.emptyList();
	}

	public final String getCommandString(String[] args) {
		return String.join(" ", args);
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}
}
