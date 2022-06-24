package com.theincgi.advancedmacros.lua.functions.minecraft;

import com.theincgi.advancedmacros.misc.CallableTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

public class GetFPS extends CallableTable {

    public GetFPS() {
        super(new String[]{"getFps"}, new Op());
    }

    private static class Op extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return valueOf(-1);//MinecraftClient.getInstance().getDebugFPS());
        }
    }
}
