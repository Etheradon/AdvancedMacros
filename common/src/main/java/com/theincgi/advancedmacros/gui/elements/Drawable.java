package com.theincgi.advancedmacros.gui.elements;

import net.minecraft.client.util.math.MatrixStack;

import com.theincgi.advancedmacros.gui.Gui;

public interface Drawable {
    void onDraw(MatrixStack matrixStack, Gui g, int mouseX, int mouseY, float partialTicks);
}
