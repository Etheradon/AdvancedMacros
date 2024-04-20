package com.theincgi.advancedmacros.lua.functions.entity;

import com.theincgi.advancedmacros.misc.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;

public class GetEntityData extends OneArgFunction {

    @Override
    public LuaValue call(LuaValue arg0) {
        Entity e = MinecraftClient.getInstance().world.getEntityById(arg0.checkint());
        if (e == null) {
            return FALSE;
        }
        return Utils.entityToTable(e);
    }

}
