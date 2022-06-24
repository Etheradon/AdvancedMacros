package com.theincgi.advancedmacros.lua.functions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

import java.util.List;

public class GetLoadedPlayers extends ZeroArgFunction {
    @Override
    public LuaValue call() {
        LuaTable table = new LuaTable();
        int i = 1;
        List<AbstractClientPlayerEntity> players = MinecraftClient.getInstance().world.getPlayers();
        for (int j = 0; j < players.size(); j++) {
            PlayerEntity ep = players.get(j);
            table.set(i++, ep.getName().getString());
        }
        return table;
    }
}
