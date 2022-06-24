package com.theincgi.advancedmacros;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

public class CommonPlatform {

    @ExpectPlatform
    public static Path getConfigDirectory() {
        return null;
    }

}
