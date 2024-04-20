package com.theincgi.advancedmacros.gui.elements;

import com.theincgi.advancedmacros.gui.Gui;
import net.minecraft.client.gui.DrawContext;

public interface Drawable {

    void onDraw(DrawContext drawContext, Gui g, int mouseX, int mouseY, float partialTicks);

}
