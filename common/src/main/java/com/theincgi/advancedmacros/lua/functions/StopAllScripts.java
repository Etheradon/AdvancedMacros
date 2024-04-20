package com.theincgi.advancedmacros.lua.functions;

import com.theincgi.advancedmacros.AdvancedMacros;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

public class StopAllScripts extends ZeroArgFunction {

    @Override
    public LuaValue call() {
        AdvancedMacros.stopAll();
        return LuaValue.NONE;
    }

}
