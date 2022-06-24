package com.theincgi.advancedmacros.gui.elements;

@FunctionalInterface
public interface OnClickHandler {
    public static final int LMB = 0, RMB = 1, MMB = 2;

    public void onClick(int button, GuiButton sButton);

}
