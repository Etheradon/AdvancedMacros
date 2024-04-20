package com.theincgi.advancedmacros.lua.functions;

import com.theincgi.advancedmacros.AdvancedMacros;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

public class ClearWorldHud extends ZeroArgFunction {

    @Override
    public LuaValue call() {
        AdvancedMacros.EVENT_HANDLER.clearWorldHud();
        return LuaValue.NONE;
    }

}
