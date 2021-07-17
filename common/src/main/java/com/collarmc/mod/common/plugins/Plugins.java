package com.collarmc.mod.common.plugins;

import com.collarmc.api.CollarPlugin;

import java.util.stream.Stream;

public interface Plugins {
    /**
     * @return all available collar plugins
     */
    Stream<CollarPlugin> find();
}
