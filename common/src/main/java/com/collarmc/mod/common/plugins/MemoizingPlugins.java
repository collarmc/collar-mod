package com.collarmc.mod.common.plugins;

import com.collarmc.api.CollarPlugin;
import com.collarmc.api.CollarPluginLoadedEvent;
import com.collarmc.pounce.EventBus;
import com.google.common.base.Suppliers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.rmi.runtime.Log;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Creates plugin list just once and subscribes it to the {@link EventBus}
 */
public final class MemoizingPlugins implements Plugins {

    private final Logger LOGGER = LogManager.getLogger(MemoizingPlugins.class);
    private final Supplier<List<CollarPlugin>> allPlugins;

    public MemoizingPlugins(EventBus eventBus, Plugins plugins) {
        CollarPluginLoadedEvent event = new CollarPluginLoadedEvent(eventBus);
        allPlugins = Suppliers.memoize(() -> plugins.find()
                .peek(eventBus::subscribe)
                .peek(collarPlugin -> {
                    try {
                        collarPlugin.onLoad(event);
                    } catch (Throwable e) {
                        LOGGER.error("Tried to load " + collarPlugin.getClass() + " but encountered an error", e);
                    }
        }).collect(Collectors.toList()));
    }

    @Override
    public Stream<CollarPlugin> find() {
        return allPlugins.get().stream();
    }
}
