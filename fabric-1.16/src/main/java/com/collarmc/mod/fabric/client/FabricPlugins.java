package com.collarmc.mod.fabric.client;

import com.collarmc.api.CollarPlugin;
import com.collarmc.mod.common.plugins.Plugins;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Objects;
import java.util.stream.Stream;

public class FabricPlugins implements Plugins {
    @Override
    public Stream<CollarPlugin> find() {
        return FabricLoader.getInstance().getEntrypoints("collar", CollarPlugin.class).stream()
                .filter(Objects::nonNull)
                .map(CollarPlugin.class::cast);
    }
}
