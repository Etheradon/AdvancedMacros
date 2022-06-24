package com.theincgi.advancedmacros.lua.scriptGui;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

import com.mojang.blaze3d.systems.RenderSystem;
import com.theincgi.advancedmacros.gui.Color;
import com.theincgi.advancedmacros.gui.Gui;
import com.theincgi.advancedmacros.gui.elements.GuiRect;
import com.theincgi.advancedmacros.lua.LuaValTexture;
import com.theincgi.advancedmacros.misc.Settings;
import com.theincgi.advancedmacros.misc.Utils;
import org.luaj.vm2_v3_0_1.LuaError;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.Varargs;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;
import org.luaj.vm2_v3_0_1.lib.VarArgFunction;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

public class GuiImage extends ScriptGuiElement {
    LuaValTexture lvt = Utils.checkTexture(Settings.getTextureID("resource:holoblock.png"));
    float uMin = 0, uMax = 1, vMin = 0, vMax = 1;

    public GuiImage(Gui gui, Group parent) {
        super(gui, parent);
        enableColorControl();
        enableSizeControl();
        color = Color.WHITE;
        set("setImage", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue v) {
                setTexture(v);
                return LuaValue.NONE;
            }
        });
        set("getImage", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return lvt;
            }
        });
        set("setUV", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if (args.narg() < 4) {
                    throw new LuaError("Not enough args (uMin, vMin, uMax, vMax)");
                }
                setUV((float) args.arg(1).checkdouble(), (float) args.arg(2).checkdouble(), (float) args.arg(3).checkdouble(), (float) args.arg(4).checkdouble());
                return LuaValue.NONE;
            }
        });
        set("__class", "advancedMacros.GuiImage");
    }

    @Override
    public void onDraw(MatrixStack matrixStack, Gui g, int mouseX, int mouseY, float partialTicks) {
        super.onDraw(matrixStack, g, mouseX, mouseY, partialTicks);
        if (!visible) {
            return;
        }

        matrixStack.push();
        //TODO 1.19 Update:  RenderSystem.pushTextureAttributes();
        float dx = x, dy = y, dw = wid, dh = hei;

        RenderSystem.setShaderColor(color.getR() / 255f, color.getG() / 255f, color.getB() / 255f, color.getA() / 255f);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
        if (lvt != null) {
            lvt.bindTexture();
        }

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(dx, dy, z).texture(uMin, vMin).next();
        buffer.vertex(dx, dy + dh, z).texture(uMin, vMax).next();
        buffer.vertex(dx + dw, dy + dh, z).texture(uMax, vMax).next();
        buffer.vertex(dx + dw, dy, z).texture(uMax, vMin).next();
        Tessellator.getInstance().draw();
        RenderSystem.disableBlend();

        if (getHoverTint() != null && GuiRect.isInBounds(mouseX, mouseY, (int) x, (int) y, (int) wid, (int) hei)) {
            GuiRectangle.drawRectangle(x, y, wid, hei, getHoverTint(), z);
        }
        matrixStack.pop();
    }

    public void setTexture(LuaValue v) {
        lvt = Utils.parseTexture(v);
    }

    @Override
    public int getItemHeight() {
        return (int) hei;
    }

    @Override
    public int getItemWidth() {
        return (int) wid;
    }

    @Override
    public void setWidth(int i) {
        this.wid = i;
    }

    @Override
    public void setHeight(int i) {
        this.hei = i;
    }

    public void setUV(float uMin, float vMin, float uMax, float vMax) {
        this.uMin = uMin;
        this.vMin = vMin;
        this.uMax = uMax;
        this.vMax = vMax;
    }
}
