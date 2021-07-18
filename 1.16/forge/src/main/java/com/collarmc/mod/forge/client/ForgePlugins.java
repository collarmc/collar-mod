package com.collarmc.mod.forge.client;

import com.collarmc.api.CollarPlugin;
import com.collarmc.mod.common.plugins.Plugins;


import java.util.stream.Stream;

public class ForgePlugins implements Plugins {
    @Override
    public Stream<CollarPlugin> find() {
        //TODO IDK how can I collect the mod loaders
        return null;
    }
}
