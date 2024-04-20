package com.theincgi.advancedmacros.lua.functions;

import com.theincgi.advancedmacros.misc.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.Varargs;
import org.luaj.vm2_v3_0_1.lib.VarArgFunction;

public class GetPlayerBlockPos extends VarArgFunction {

    @Override
    public Varargs invoke(Varargs args) {
        if (args.narg() == 0) {
            PlayerEntity player = MinecraftClient.getInstance().player;
            LuaTable t = new LuaTable();
            t.set(1, LuaValue.valueOf(Math.floor(player.getX())));
            t.set(2, LuaValue.valueOf(Math.floor(player.getY())));
            t.set(3, LuaValue.valueOf(Math.floor(player.getZ())));
            return t.unpack();
        } else {
            String sPlayer = args.checkjstring(1);
            AbstractClientPlayerEntity player = Utils.findPlayerByName(sPlayer);

            //for(EntityPlayer player : MinecraftClient.getInstance().world.playerEntities){
            if (player.getName().equals(sPlayer)) {
                LuaTable t = new LuaTable();
                t.set(1, LuaValue.valueOf(Math.floor(player.getX())));
                t.set(2, LuaValue.valueOf(Math.floor(player.getY())));
                t.set(3, LuaValue.valueOf(Math.floor(player.getZ())));
                return t.unpack();
            }

            return LuaValue.FALSE;
        }
    }

}
