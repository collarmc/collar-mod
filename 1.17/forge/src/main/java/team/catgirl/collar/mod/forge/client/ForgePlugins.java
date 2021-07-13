package team.catgirl.collar.mod.forge.client;

import team.catgirl.collar.api.CollarPlugin;
import team.catgirl.collar.mod.common.plugins.Plugins;


import java.util.stream.Stream;

public class ForgePlugins implements Plugins {
    @Override
    public Stream<CollarPlugin> find() {
        //TODO IDK how can I collect the mod loaders
        return null;
    }
}
