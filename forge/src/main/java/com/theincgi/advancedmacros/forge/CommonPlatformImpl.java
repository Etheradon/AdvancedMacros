package com.theincgi.advancedmacros.forge;

import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;


public class CommonPlatformImpl {
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
