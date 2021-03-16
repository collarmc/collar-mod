package team.catgirl.collar.mod.forge;

import net.minecraftforge.fml.common.Loader;
import team.catgirl.collar.api.CollarPlugin;
import team.catgirl.collar.mod.common.plugins.Plugins;

import java.util.stream.Stream;

public class ForgePlugins implements Plugins {
    @Override
    public Stream<CollarPlugin> find() {
        return Loader.instance().getActiveModList().stream()
                .filter(modContainer -> modContainer.getMod() instanceof CollarPlugin)
                .map(modContainer -> (CollarPlugin) modContainer.getMod());
    }
}
