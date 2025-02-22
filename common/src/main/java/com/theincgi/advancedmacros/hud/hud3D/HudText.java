package com.theincgi.advancedmacros.hud.hud3D;

import com.mojang.blaze3d.systems.RenderSystem;
import com.theincgi.advancedmacros.AdvancedMacros;
import com.theincgi.advancedmacros.misc.CallableTable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

public class HudText extends WorldHudItem {

    MinecraftClient mc = MinecraftClient.getInstance();
    String text = "";

    //private boolean is3D;
    //Color color;
    public HudText() {
        //this.is3D = is3D;
    }

    @Override
    public void render(MatrixStack ms) {
        RenderSystem.setShaderColor(1, 1, 1, color.getAFloat());
        AdvancedMacros.CUSTOM_FONT_RENDERER.renderText(ms.peek().getPositionMatrix(), x, y, z, yaw, pitch, roll, text, color.getAFloat());
    }

    @Override
    public void destroy() {
        disableDraw();
    }

    public void setText(String checkjstring) {
        text = checkjstring;
    }

    @Override
    public void setRotation(float yaw, float pitch, float roll) {
        setYaw(yaw);
        setPitch(pitch);
        setRoll(roll);
    }

    public String getText() {
        return text;
    }

    @Override
    public void loadControls(LuaValue t) {
        super.loadControls(t);
        t.set("setText", new CallableTable(new String[]{"hud3D", "newText()", "setText"}, new SetText()));
        t.set("getText", new CallableTable(new String[]{"hud3D", "newText()", "getText"}, new GetText()));
    }

    //Definitly stolen from the FontRenderer class
    private class SetText extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg0) {
            setText(arg0.checkjstring());
            return LuaValue.NONE;
        }

    }

    private class GetText extends ZeroArgFunction {

        @Override
        public LuaValue call() {
            return LuaValue.valueOf(getText());
        }

    }

}
