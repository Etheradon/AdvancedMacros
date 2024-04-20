package com.theincgi.advancedmacros.hud.hud2D;

import com.mojang.blaze3d.systems.RenderSystem;
import com.theincgi.advancedmacros.AdvancedMacros;
import com.theincgi.advancedmacros.misc.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.Varargs;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;
import org.luaj.vm2_v3_0_1.lib.VarArgFunction;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

import java.util.Scanner;

public class Hud2D_Text extends Hud2DItem {

    String text = "";
    float size = 12;

    boolean monospaced = false;

    public Hud2D_Text() {
        super();
        getControls().set("setText", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String t = arg.tojstring();
                if (!monospaced) {
                    text = Utils.toMinecraftColorCodes(t);
                }
                return LuaValue.NONE;
            }
        });
        getControls().set("getText", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(text);
            }
        });
        getControls().set("setTextSize", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                size = (float) arg.checkdouble();
                return LuaValue.NONE;
            }
        });
        getControls().set("getTextSize", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(size);
            }
        });
        getControls().set("getWidth", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(MinecraftClient.getInstance().textRenderer.getWidth(widestLine(text)) * (size / 7.99f));
            }
        });
        getControls().set("getHeight", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(size * countLines(text));
            }
        });
        getControls().set("getSize", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                LuaTable temp = new LuaTable();
                temp.set(1, LuaValue.valueOf(MinecraftClient.getInstance().textRenderer.getWidth(widestLine(text))));
                temp.set(2, LuaValue.valueOf(size * countLines(text)));
                return temp.unpack();
            }
        });
    }

    public String widestLine(String text) {
        String maxLine = "";
        Scanner temp = new Scanner(text);
        while (temp.hasNextLine()) {
            String m = temp.nextLine().replaceAll(Utils.mcSelectCode + "[0-9a-flkmnor]", "");
            if (m.length() > maxLine.length()) {
                maxLine = m;
            }
        }
        temp.close();

        return maxLine + " ";
    }

    public int countLines(String text) {
        int out = 1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                out++;
            }
        }
        return out;
    }

    @Override
    public void render(DrawContext drawContext, float partialTicks) {
        MatrixStack matrixStack = drawContext.getMatrices();

        matrixStack.push();
        applyTransformation(matrixStack);
        float dx = 0, dy = 0;
        if (allowFrameInterpolation) {
            dx = interpolate(dx, lastX, partialTicks);
            dy = interpolate(dy, lastY, partialTicks);
        }
        if (monospaced) {
            RenderSystem.defaultBlendFunc();
            AdvancedMacros.CUSTOM_FONT_RENDERER.renderText(dx, dy, z, text, getOpacity(), size);
        } else {
            Scanner s = new Scanner(text);
            //RenderSystem.enableBlend();
            //RenderSystem.enableAlpha();
            //TODO 1.19 Update: RenderSystem.disableAlphaTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            TextRenderer fr = MinecraftClient.getInstance().textRenderer;//AdvancedMacros.otherCustomFontRenderer;

            //float old = fr.FONT_HEIGHT;
            //fr.FONT_HEIGHT = (int)size;
            matrixStack.push();
            matrixStack.translate(0, 0, z);    //TESTME hud2d Z translate
            float sc = size / 7.99f;
            matrixStack.scale(sc, sc, 1);
            for (int i = 0; s.hasNextLine(); i += size) {
                drawContext.drawText(fr, s.nextLine(), (int) dx, (int) (dy + i / sc), color.toInt(), false);//(text, (int)x, (int)y, color.toInt());
            }
            matrixStack.pop();
            s.close();
            //RenderSystem.disableAlpha();
        }
        RenderSystem.bindTexture(0);
        //		AdvancedMacros.customFontRenderer.renderText(dx, dy, z, text, getOpacity(), size);
        //	RenderSystem.disableBlend();
        matrixStack.pop();
    }

}
