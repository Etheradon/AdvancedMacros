package com.theincgi.advancedmacros.lua.functions;

import net.minecraft.client.MinecraftClient;

import com.theincgi.advancedmacros.misc.Utils;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;

@Deprecated
public class AdvLog extends OneArgFunction {
    @Override
    public LuaValue call(LuaValue arg0) {
        if (arg0.istable()) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Utils.luaTableToComponentJson(arg0.checktable()));
        }
        return LuaValue.NONE;
    }
}
