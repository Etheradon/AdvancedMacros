package com.theincgi.advancedmacros.lua.functions;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

import com.theincgi.advancedmacros.misc.Utils;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

public class GetBlockList extends ZeroArgFunction {
    @Override
    public LuaValue call() {
        LuaTable t = new LuaTable();
        Registry.BLOCK.forEach((b) -> {
            Item item = Item.BLOCK_ITEMS.get(b);//Item.getItemFromBlock(b);

            t.set(Registry.ITEM.getId(item).toString(), Utils.itemStackToLuatable(new ItemStack(item)));

        });
        Registry.ITEM.forEach((b) -> {
            Item item = b;//Item.getItemFromBlock(b);
            if (t.get(Registry.ITEM.getId(item).toString()).isnil()) {
                t.set(Registry.ITEM.getId(item).toString(), Utils.itemStackToLuatable(new ItemStack(item)));
            }

        });

        return t;
    }

}
