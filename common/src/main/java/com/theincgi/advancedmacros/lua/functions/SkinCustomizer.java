package com.theincgi.advancedmacros.lua.functions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.entity.PlayerModelPart;

import org.luaj.vm2_v3_0_1.LuaError;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.TwoArgFunction;

public class SkinCustomizer extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue arg0, LuaValue arg1) {
        GameOptions set = MinecraftClient.getInstance().options;
        switch (arg0.checkjstring()) {
            case "hat":
            case "helmet":
                set.togglePlayerModelPart(PlayerModelPart.HAT, arg1.checkboolean());
                break;
            case "jacket":
            case "chest":
                set.togglePlayerModelPart(PlayerModelPart.JACKET, arg1.checkboolean());
                break;
            case "left leg":
                set.togglePlayerModelPart(PlayerModelPart.LEFT_PANTS_LEG, arg1.checkboolean());
                break;
            case "right leg":
                set.togglePlayerModelPart(PlayerModelPart.RIGHT_PANTS_LEG, arg1.checkboolean());
                break;
            case "left arm":
                set.togglePlayerModelPart(PlayerModelPart.LEFT_SLEEVE, arg1.checkboolean());
                break;
            case "right arm":
                set.togglePlayerModelPart(PlayerModelPart.RIGHT_SLEEVE, arg1.checkboolean());
                break;
            case "cape":
                set.togglePlayerModelPart(PlayerModelPart.CAPE, arg1.checkboolean());
                break;

            default:
                throw new LuaError("Unknown part");
        }
        return LuaValue.NONE;
    }

    @Override
    public LuaValue tostring() {
        return LuaValue.valueOf("function: customizeSkin(part, enable)\n"
                + "parts:{cape, hat/helmet, jacket/chest, \n"
                + "left/right leg, left/right arm}");
    }
}
