package com.theincgi.advancedmacros.lua.scriptGui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.theincgi.advancedmacros.gui.Gui;
import com.theincgi.advancedmacros.gui.elements.GuiRect;
import com.theincgi.advancedmacros.misc.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.luaj.vm2_v3_0_1.LuaError;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;
import org.luaj.vm2_v3_0_1.lib.TwoArgFunction;

public class GuiItemIcon extends ScriptGuiElement {

    static ItemRenderer itemRender = MinecraftClient.getInstance().getItemRenderer();
    static TextRenderer fontRenderer = MinecraftClient.getInstance().textRenderer;

    ItemStack itemStack;

    public GuiItemIcon(Gui gui, Group parent) {
        super(gui, parent);
        itemStack = ItemStack.EMPTY;
        this.set("setItem", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue item, LuaValue quantity) {
                if (item.isnil()) {
                    throw new LuaError("Item cannot be nil");
                }
                setStack(item.checkjstring());
                if (!quantity.isnil()) {
                    itemStack.setCount(quantity.checkint());
                }
                return NONE;
            }
        });
        this.set("setCount", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                itemStack.setCount(arg.checkint());
                return NONE;
            }
        });
        set("__class", "advancedMacros.GuiItem");
        wid = hei = 16;
    }

    public void setStack(String text) {
        //Item i = Item.getByNameOrId(text);
        if (text == null) {
            itemStack = ItemStack.EMPTY;
            return;
        }
        if (!text.contains(":")) {
            text = "minecraft:" + text;
        }
        int indx = text.lastIndexOf(":");
        String end = text.substring(indx + 1);
        try {
            int dmg = Integer.parseInt(end);
            itemStack = Utils.itemStackFromName(text.substring(0, indx));
            itemStack.setDamage(dmg);
        } catch (NumberFormatException e) {
            itemStack = Utils.itemStackFromName(text);
        }

    }

    @Override
    public void onDraw(DrawContext drawContext, Gui g, int mouseX, int mouseY, float partialTicks) {
        super.onDraw(drawContext, g, mouseX, mouseY, partialTicks);
        DiffuseLighting.disableGuiDepthLighting();
        DiffuseLighting.enableGuiDepthLighting();

        drawContext.drawItemInSlot(MinecraftClient.getInstance().textRenderer, itemStack, (int) x, (int) y);
        DiffuseLighting.disableGuiDepthLighting();
        RenderSystem.bindTexture(0);

        if (GuiRect.isInBounds(mouseX, mouseY, (int) x, (int) y, (int) wid, (int) hei)) {
            GuiRectangle.drawRectangle(x, y, wid, hei, getHoverTint(), z + 150);
        }
    }

    @Override
    public int getItemHeight() {
        // TODO Auto-generated method stub
        return (int) wid;
    }

    @Override
    public int getItemWidth() {
        // TODO Auto-generated method stub
        return (int) hei;
    }

    @Override
    public void setWidth(int i) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setHeight(int i) {
        // TODO Auto-generated method stub

    }

    public void setCount(int optint) {
        itemStack.setCount(1);
    }

}
