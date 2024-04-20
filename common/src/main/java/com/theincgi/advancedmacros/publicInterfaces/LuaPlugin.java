package com.theincgi.advancedmacros.publicInterfaces;

/**
 * Object must be of type LuaFunction<br> A no arg constructor is also required (reflection used)
 */
public interface LuaPlugin {

    /**
     * Returns the name of the library used in require "yourLibraryName"
     */
    String getLibraryName();

}
