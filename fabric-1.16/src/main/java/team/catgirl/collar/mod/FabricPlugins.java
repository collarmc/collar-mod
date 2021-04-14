package team.catgirl.collar.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import team.catgirl.collar.api.CollarPlugin;
import team.catgirl.collar.mod.common.plugins.Plugins;

import java.util.stream.Stream;

public class FabricPlugins implements Plugins {
    @Override
    public Stream<CollarPlugin> find() {
        return FabricLoader.getInstance().getEntrypoints("client", ClientModInitializer.class).stream()
                .filter(CollarPlugin.class::isInstance)
                .map(CollarPlugin.class::cast);
    }
}
