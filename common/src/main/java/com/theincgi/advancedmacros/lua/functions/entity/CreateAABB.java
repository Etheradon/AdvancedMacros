package com.theincgi.advancedmacros.lua.functions.entity;

import com.theincgi.advancedmacros.misc.CallableTable;
import org.luaj.vm2_v3_0_1.lib.VarArgFunction;

public class CreateAABB extends CallableTable {

    static final String[] docName = {"createAABB"};

    public CreateAABB() {
        super(docName, new CreateGenerator());
        // TODO Auto-generated constructor stub
    }

    private static class CreateGenerator extends VarArgFunction {

    }

}
