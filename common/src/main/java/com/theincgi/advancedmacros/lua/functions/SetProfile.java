package com.theincgi.advancedmacros.lua.functions;

import com.theincgi.advancedmacros.AdvancedMacros;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;

public class SetProfile extends OneArgFunction {

    @Override
    public LuaValue call(LuaValue arg0) {
        return LuaValue.valueOf(AdvancedMacros.macroMenuGui.loadProfile(arg0.checkjstring()));
    }

}
