package com.theincgi.advancedmacros.lua.functions;

import com.theincgi.advancedmacros.misc.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

public class GetBlockList extends ZeroArgFunction {

    @Override
    public LuaValue call() {
        LuaTable t = new LuaTable();
        Registries.BLOCK.forEach((b) -> {
            Item item = Item.BLOCK_ITEMS.get(b);//Item.getItemFromBlock(b);

            t.set(Registries.ITEM.getId(item).toString(), Utils.itemStackToLuatable(new ItemStack(item)));

        });
        Registries.ITEM.forEach((b) -> {
            Item item = b;//Item.getItemFromBlock(b);
            if (t.get(Registries.ITEM.getId(item).toString()).isnil()) {
                t.set(Registries.ITEM.getId(item).toString(), Utils.itemStackToLuatable(new ItemStack(item)));
            }

        });

        return t;
    }

}
