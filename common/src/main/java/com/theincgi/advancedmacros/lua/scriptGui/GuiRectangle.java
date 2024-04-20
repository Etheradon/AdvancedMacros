package com.theincgi.advancedmacros.lua.scriptGui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.theincgi.advancedmacros.gui.Color;
import com.theincgi.advancedmacros.gui.Gui;
import com.theincgi.advancedmacros.gui.elements.GuiRect;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

public class GuiRectangle extends ScriptGuiElement {

    public GuiRectangle(Gui gui, Group parent) {
        super(gui, parent);
        enableColorControl();
        enableSizeControl();
        set("__class", "advancedMacros.GuiRectangle");
    }

    @Override
    public void onDraw(DrawContext drawContext, Gui g, int mouseX, int mouseY, float partialTicks) {
        super.onDraw(drawContext, g, mouseX, mouseY, partialTicks);
        if (!visible) {
            return;
        }

        drawRectangle(x, y, wid, hei, color, z);
        if (getHoverTint() != null && GuiRect.isInBounds(mouseX, mouseY, (int) x, (int) y, (int) wid, (int) hei)) {
            drawRectangle(x, y, wid, hei, getHoverTint(), z);
        }
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

    public static void drawRectangle(float dx, float dy, float dw, float dh, Color color, float z) {
        //RenderSystem.enableBlend();
        //RenderSystem.enableAlpha();
        //		RenderSystem.enableBlend();
        RenderSystem.setShaderColor(color.getR() / 255f, color.getG() / 255f, color.getB() / 255f, color.getA() / 255f);
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        RenderSystem.disableBlend();
        RenderSystem.enableBlend();
        //RenderSystem.disableTexture2D();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        //TODO 1.19 Update: RenderSystem.disableAlphaTest();
        //TODO 1.19 Update: RenderSystem.enableAlphaTest();
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

}
