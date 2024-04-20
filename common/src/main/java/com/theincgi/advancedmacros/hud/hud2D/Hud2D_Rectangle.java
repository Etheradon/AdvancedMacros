package com.theincgi.advancedmacros.hud.hud2D;

import com.mojang.blaze3d.systems.RenderSystem;
import com.theincgi.advancedmacros.gui.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;
import org.luaj.vm2_v3_0_1.lib.TwoArgFunction;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

public class Hud2D_Rectangle extends Hud2DItem {

    //Color color = Color.BLACK;
    int colorInt;
    float wid, hei;
    float lastWid, lastHei;

    public Hud2D_Rectangle() {
        getControls().set("setWidth", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                wid = (float) arg.checkdouble();
                return LuaValue.NONE;
            }
        });
        getControls().set("setHeight", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                hei = (float) arg.checkdouble();
                return LuaValue.NONE;
            }
        });
        getControls().set("getWidth", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(wid);
            }
        });
        getControls().set("setSize", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                wid = (float) arg1.checkdouble();
                hei = (float) arg2.checkdouble();
                return NONE;
            }
        });
        getControls().set("getHeight", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(hei);
            }
        });
        super.enableColorControl();
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
        drawRectangle(dx, dy, dw, dh, color, z);
        matrixStack.pop();
    }

    /**
     * @param dx - draw X
     * @param dy - draw Y
     * @param dw - draw Width
     * @param dh - draw Height
     */
    public static void drawRectangle(float dx, float dy, float dw, float dh, Color color, float z) {

        //		//RenderSystem.enableBlend();
        //        RenderSystem.disableTexture2D();
        //        RenderSystem.bindTexture(0);
        //		//RenderSystem.enableAlpha();
        //		//GL11.glEnable(GL11.GL_ALPHA);
        //
        float a = color.getA() / 255f;

        //a = ((float) ((Math.sin(System.currentTimeMillis()%100000/500f)+1 )/2)) * 1f + .0f;
        a *= a;
        //System.out.println(a);
        RenderSystem.setShaderColor(color.getR() / 255f, color.getG() / 255f, color.getB() / 255f, a);//color.getA()/255f);
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        //
        //		RenderSystem.disableBlend();
        //		RenderSystem.enableBlend();
        //
        //		//RenderSystem.disableTexture2D();
        //		//RenderSystem.glBlendEquation(GL11.GL_ADD);
        //		RenderSystem.blendFunc(RenderSystem.SourceFactor.ONE, RenderSystem.DestFactor.ONE_MINUS_DST_ALPHA);
        //RenderSystem.tryBlendFuncSeparate(RenderSystem.SourceFactor.ONE, RenderSystem.DestFactor.ONE_MINUS_SRC_ALPHA, RenderSystem.SourceFactor.ONE, RenderSystem.DestFactor.ZERO);
        //		RenderSystem.enableAlpha();
        //		RenderSystem.disableAlpha();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION); //7 is GL_QUADS btw
        buffer.vertex(dx, dy, z).next(); //bottom left -> bottom right -> top right -> top left
        buffer.vertex(dx, dy + dh, z).next(); //top left
        buffer.vertex(dx + dw, dy + dh, z).next(); //top right
        buffer.vertex(dx + dw, dy, z).next(); //bottom right

        Tessellator.getInstance().draw();
        //RenderSystem.disableBlend();
        // RenderSystem.disableBlend();
        // Gui.drawRect((int)dx, (int)dy, (int)dx+dw, (int)dy+dh, color.toInt());
        //GL11.glPopAttrib();
    }

    @Override
    public void updateLastPos() {
        super.updateLastPos();
        lastWid = wid;
        lastHei = hei;
    }

}
