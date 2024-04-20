package com.theincgi.advancedmacros.lua.functions;

import com.theincgi.advancedmacros.misc.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;

public class GetInventory extends OneArgFunction {

    @Override
    public LuaValue call(LuaValue arg) {
        Inventory inventory;
        LuaTable output = new LuaTable();
        if (arg.isnil()) {
            inventory = MinecraftClient.getInstance().player.getInventory();
        } else {
            PlayerEntity player = Utils.findPlayerByName(arg.checkjstring());
            if (player == null) {
                return LuaValue.FALSE;
            }
            inventory = player.getInventory();
        }
        for (int i = 0; i < inventory.size(); i++) {
            output.set(i + 1, Utils.itemStackToLuatable(inventory.getStack(i)));
        }
        return output;
    }

}
