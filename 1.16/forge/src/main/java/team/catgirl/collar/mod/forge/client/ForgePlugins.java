package team.catgirl.collar.mod.forge.client;

import net.minecraftforge.fml.loading.FMLCommonLaunchHandler;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import team.catgirl.collar.api.CollarPlugin;
import team.catgirl.collar.mod.common.plugins.Plugins;

import net.minecraftforge.fml.common.Loader;

import java.util.stream.Stream;

public class ForgePlugins implements Plugins {
    @Override
    public Stream<CollarPlugin> find() {
        //TODO IDK how can I collect the mod loaders
        return null;
    }
}
