package com.theincgi.advancedmacros.lua.functions;

import com.theincgi.advancedmacros.hud.hud3D.HudText;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

public class AddHoloText extends ZeroArgFunction {

    @Override
    public LuaValue call() {
        HudText hudText = new HudText();
        return hudText.getControls();
    }

}
