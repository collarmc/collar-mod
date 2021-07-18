package com.collarmc.mod.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import com.collarmc.api.CollarPlugin;
import com.collarmc.mod.common.plugins.Plugins;

import java.util.stream.Stream;

public class FabricPlugins implements Plugins {
    @Override
    public Stream<CollarPlugin> find() {
        return FabricLoader.getInstance().getEntrypoints("client", ClientModInitializer.class).stream()
                .filter(CollarPlugin.class::isInstance)
                .map(CollarPlugin.class::cast);
    }
}
