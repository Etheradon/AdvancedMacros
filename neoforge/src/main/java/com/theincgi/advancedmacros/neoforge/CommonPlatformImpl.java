package com.theincgi.advancedmacros.neoforge;

import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class CommonPlatformImpl {

    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

}
