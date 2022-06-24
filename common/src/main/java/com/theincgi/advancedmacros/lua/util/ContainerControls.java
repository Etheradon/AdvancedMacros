package com.theincgi.advancedmacros.lua.util;

import net.minecraft.inventory.Inventory;

import org.luaj.vm2_v3_0_1.LuaTable;

public class ContainerControls extends LuaTable {
    Inventory inv;

    public ContainerControls(Inventory inv) {
        this.inv = inv;
    }

}
