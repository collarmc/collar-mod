package com.collarmc.mod.forge.client;

import net.minecraftforge.fml.common.Loader;
import com.collarmc.api.CollarPlugin;
import com.collarmc.mod.common.plugins.Plugins;

import java.util.stream.Stream;

public class ForgePlugins implements Plugins {
    @Override
    public Stream<CollarPlugin> find() {
        return Loader.instance().getActiveModList().stream()
                .filter(modContainer -> modContainer.getMod() instanceof CollarPlugin)
                .map(modContainer -> (CollarPlugin) modContainer.getMod());
    }
}
