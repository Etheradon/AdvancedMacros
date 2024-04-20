package com.theincgi.advancedmacros.hud.hud2D;

import com.mojang.blaze3d.systems.RenderSystem;
import com.theincgi.advancedmacros.lua.LuaValTexture;
import com.theincgi.advancedmacros.misc.Settings;
import com.theincgi.advancedmacros.misc.Utils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.luaj.vm2_v3_0_1.LuaError;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.Varargs;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;
import org.luaj.vm2_v3_0_1.lib.VarArgFunction;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

public class Hud2D_Image extends Hud2D_Rectangle {

    LuaValTexture lvt = Utils.checkTexture(Settings.getTextureID("resource:holoblock.png"));
    float uMin, vMin, uMax = 1, vMax = 1;

    public Hud2D_Image() {
        super();

        lvt = Utils.checkTexture(Settings.getTextureID("resource:holoblock.png"));

        getControls().set("setImage", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue v) {
                setTexture(v);
                return LuaValue.NONE;
            }
        });
        getControls().set("getImage", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return lvt;
            }
        });
        getControls().set("setUV", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if (args.narg() < 4) {
                    throw new LuaError("Not enough args (uMin, vMin, uMax, vMax)");
                }
                setUV((float) args.arg(1).checkdouble(), (float) args.arg(2).checkdouble(), (float) args.arg(3).checkdouble(), (float) args.arg(4).checkdouble());
                return LuaValue.NONE;
            }
        });
        super.enableColorControl();
    }

    public void setTexture(LuaValue v) {
        lvt = Utils.parseTexture(v);
    }

    public void setUV(float uMin, float vMin, float uMax, float vMax) {
        this.uMin = uMin;
        this.vMin = vMin;
        this.uMax = uMax;
        this.vMax = vMax;
    }

    @Override
    public void render(DrawContext drawContext, float partialTicks) {
        MatrixStack matrixStack = drawContext.getMatrices();

        matrixStack.push();
        applyTransformation(matrixStack);
        float dx = 0, dy = 0, dw = wid, dh = hei;
        if (allowFrameInterpolation) {
            dx = interpolate(dx, lastX, partialTicks);
            dy = interpolate(dy, lastY, partialTicks);
            dw = interpolate(dw, lastWid, partialTicks);
            dh = interpolate(dh, lastHei, partialTicks);
        }

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
        lvt.bindTexture();

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(dx, y, z).texture(uMin, vMin).next();
        buffer.vertex(dx, y + dh, z).texture(uMin, vMax).next();
        buffer.vertex(dx + dw, y + dh, z).texture(uMax, vMax).next();
        buffer.vertex(dx + dw, y, z).texture(uMax, vMin).next();
        Tessellator.getInstance().draw();
        RenderSystem.disableBlend();

        matrixStack.pop();
    }

}
