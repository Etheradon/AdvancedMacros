package com.theincgi.advancedmacros.hud.hud3D;

import com.mojang.blaze3d.systems.RenderSystem;
import com.theincgi.advancedmacros.AdvancedMacros;
import com.theincgi.advancedmacros.gui.Color;
import com.theincgi.advancedmacros.lua.LuaValTexture;
import com.theincgi.advancedmacros.misc.CallableTable;
import com.theincgi.advancedmacros.misc.Settings;
import com.theincgi.advancedmacros.misc.Utils;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.luaj.vm2_v3_0_1.LuaError;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.Varargs;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;
import org.luaj.vm2_v3_0_1.lib.TwoArgFunction;
import org.luaj.vm2_v3_0_1.lib.VarArgFunction;

public class Hud3DPane extends WorldHudItem {

    LuaValTexture texture;
    float uMin = 0, uMax = 1, vMin = 0, vMax = 1, width = 1, length = 1;
    AxisFace axisFace;

    public Hud3DPane(String face) {
        this(face, "resource:holoblock.png");
    }

    public Hud3DPane(String face, String texture) {
        setFace(face);
        this.texture = Utils.checkTexture(Settings.getTextureID(texture));
    }

    private void setFace(String face) {
        face = face.toUpperCase();
        switch (face) {
            case "XZ+":
            case "Y+":
                axisFace = AxisFace.XZP;
                break;
            case "XZ-":
            case "Y-":
                axisFace = AxisFace.XZM;
                break;
            case "XY+":
            case "Z+":
                axisFace = AxisFace.XYP;
                break;
            case "XY-":
            case "Z-":
                axisFace = AxisFace.XYM;
                break;
            case "YZ+":
            case "X+":
                axisFace = AxisFace.YZP;
                break;
            case "YZ-":
            case "X-":
                axisFace = AxisFace.YZM;
                break;
            case "XZ":
            case "Y":
                axisFace = AxisFace.XZ;
                break;
            case "XY":
            case "Z":
                axisFace = AxisFace.XY;
                break;
            case "YZ":
            case "X":
                axisFace = AxisFace.YZ;
                break;
            default:
                throw new LuaError("invalid face");
        }
    }

    @Override
    public void render(MatrixStack ms) {
        if (texture != null) {
            texture.bindTexture();

            Matrix4f t = ms.peek().getPositionMatrix();
            color.apply();

            color.apply();
            switch (axisFace) {
                case XYM:
                    drawSideFace(t, 0, 0, 0, width, length, 0);
                    break;
                case XYP:
                    drawSideFace(t, -width, 0, 0, -width, length, 0);
                    break;
                case XZM:
                    drawBottomFace(t, 0, 0, 0, width, length);
                    break;
                case XZP:
                    drawBottomFace(t, -width, 0, 0, -width, length);
                    break;
                case YZP:
                    drawSideFace(t, 0, 0, 0, 0, length, width);
                    break;
                case YZM:
                    drawSideFace(t, 0, 0, -width, 0, length, -width);
                    break;

                case XY:
                    RenderSystem.disableCull();
                    drawSideFace(t, 0, 0, 0, width, length, 0);
                    RenderSystem.enableCull();
                    break;
                case XZ:
                    RenderSystem.disableCull();
                    drawBottomFace(t, 0, 0, 0, width, length);
                    RenderSystem.enableCull();
                    break;
                case YZ:
                    RenderSystem.disableCull();
                    drawSideFace(t, 0, 0, 0, 0, length, width);
                    RenderSystem.enableCull();
                    break;
                default:
                    break;
            }
        }
    }

    private static enum AxisFace {
        XZP, XZM, XZ,
        XYP, XYM, XY,
        YZP, YZM, YZ;
    }

    private void drawSideFace(Matrix4f transform, float px, float py, float pz, float xWid, float yHei, float zWid) {
        float x = this.x - px;
        float y = this.y - py;
        float z = this.z - pz;

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(transform, x, y, z).texture(uMax, vMax).next();
        buffer.vertex(transform, x, y + yHei, z).texture(uMax, vMin).next();
        buffer.vertex(transform, x + xWid, y + yHei, z + zWid).texture(uMin, vMin).next();
        buffer.vertex(transform, x + xWid, y, z + zWid).texture(uMin, vMax).next();
        Tessellator.getInstance().draw();
    }

    private void drawBottomFace(Matrix4f transform, float px, float py, float pz, float wid, float len) {
        float x = this.x - px;
        float y = this.y - py;
        float z = this.z - pz;

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(transform, x + wid, y, z).texture(uMin, vMin).next();
        buffer.vertex(transform, x + wid, y, z + len).texture(uMin, vMax).next();
        buffer.vertex(transform, x, y, z + len).texture(uMax, vMax).next();
        buffer.vertex(transform, x, y, z).texture(uMax, vMin).next();
        Tessellator.getInstance().draw();
    }

    @Override
    public void loadControls(LuaValue t) {
        super.loadControls(t);
        t.set("setWidth", new CallableTable(new String[]{"hud3D", "newPane()", "setWidth"}, new SetWidth()));
        t.set("setLength", new CallableTable(new String[]{"hud3D", "newPane()", "setWidth"}, new setLength()));
        t.set("changeTexture", new CallableTable(new String[]{"hud3D", "newPane()", "changeTexture"}, new ChangeTexture()));
        t.set("setUV", new CallableTable(new String[]{"hud3D", "newPane()", "setUV"}, new SetUV()));
        t.set("setColor", new CallableTable(new String[]{"hud3D", "newPane()", "setColor"}, new setColor()));
        t.set("getColor", new CallableTable(new String[]{"hud3D", "newPane()", "getColor"}, new GetColor()));
        t.set("setSize", new CallableTable(new String[]{"hud3D", "newPane()", "setSize"}, new setSize()));
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setLength(float length) {
        this.length = length;
    }

    /**
     * flip'd with 1-# for the way I like it, 0,0 in top left
     */
    public void setUV(float uMin, float vMin, float uMax, float vMax) {
        this.uMin = uMin;
        this.uMax = uMax;
        this.vMin = vMin;
        this.vMax = vMax;
    }

    private class SetWidth extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            setWidth((float) arg.checkdouble());
            return LuaValue.NONE;
        }

    }

    private class setLength extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            setLength((float) arg.checkdouble());
            return LuaValue.NONE;
        }

    }

    private class ChangeTexture extends TwoArgFunction {

        @Override
        public LuaValue call(LuaValue arg, LuaValue optSide) {
            texture = Utils.parseTexture(arg);
            if (texture != null) {
                setUV(texture.uMin(), texture.vMin(), texture.uMax(), texture.vMax());
            }
            return LuaValue.NONE;
        }

    }

    private class SetUV extends VarArgFunction {

        @Override
        public Varargs invoke(Varargs args) {
            if (args.narg() < 4) {
                throw new LuaError("Not enough args (uMin, vMin, uMax, vMax)");
            }
            setUV((float) args.arg(1).checkdouble(), (float) args.arg(2).checkdouble(), (float) args.arg(3).checkdouble(), (float) args.arg(4).checkdouble());
            return LuaValue.NONE;
        }

    }

    private class setColor extends TwoArgFunction {

        @Override
        public LuaValue call(LuaValue color, LuaValue optSide) {
            Color c = Utils.parseColor(color, AdvancedMacros.COLOR_SPACE_IS_255);
            Hud3DPane.this.color = c;
            return NONE;
        }

    }

    private class GetColor extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue optside) {
            boolean use = AdvancedMacros.COLOR_SPACE_IS_255;
            return color.toLuaValue(use);
        }

    }

    private class setSize extends TwoArgFunction {

        @Override
        public LuaValue call(LuaValue w, LuaValue l) {
            Hud3DPane.this.width = (float) w.checkdouble();
            Hud3DPane.this.length = (float) l.checkdouble();
            return NONE;
        }

        ;
    }

    public void changeTexture(LuaValue arg) {
        texture = Utils.parseTexture(arg);
    }

}
