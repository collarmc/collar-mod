package team.catgirl.plastic.ui;

import com.mojang.brigadier.CommandDispatcher;

public interface Commands {
    /**
     * Register a command
     * @param name name of command
     * @param source for the dispatcher
     * @param dispatcher for command
     * @param <T> of the source
     */
    <T> void register(String name, T source, CommandDispatcher<T> dispatcher);
}
