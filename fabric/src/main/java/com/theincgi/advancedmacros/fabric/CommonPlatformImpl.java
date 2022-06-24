package com.theincgi.advancedmacros.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;


public class CommonPlatformImpl {
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
