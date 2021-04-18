package team.catgirl.collar.mod.common.plugins;

import team.catgirl.collar.api.CollarPlugin;

import java.util.stream.Stream;

public interface Plugins {
    /**
     * @return all available collar plugins
     */
    Stream<CollarPlugin> find();
}