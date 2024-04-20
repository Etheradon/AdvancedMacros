package com.theincgi.advancedmacros.misc;

import com.theincgi.advancedmacros.lua.LuaDebug.LuaThread;
import net.minecraft.text.ClickEvent;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;

public class LuaTextComponentClickEvent extends ClickEvent {

    LuaValue onClick;
    private final LuaTextComponent ltc;

    public LuaTextComponentClickEvent(LuaValue onClick, LuaTextComponent ltc) {
        super(null, ltc.getString());
        this.ltc = ltc;
        this.onClick = onClick;
    }

    public void click() {
        LuaTable args = new LuaTable();
        args.set(1, ltc.getText());
        args.set(2, ltc.getString());
        LuaThread t;
        if (onClick.istable() && !onClick.get("click").isnil()) {
            t = new LuaThread(onClick.get("click"), args.unpack(), "LuaTextComponentClick");
        } else {
            t = new LuaThread(onClick, args.unpack(), "LuaTextComponentClick");
        }
        t.start();
    }

}
