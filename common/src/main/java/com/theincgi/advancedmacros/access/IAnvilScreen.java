package com.theincgi.advancedmacros.access;

import net.minecraft.client.gui.widget.TextFieldWidget;

public interface IAnvilScreen {

    TextFieldWidget am_getNameField();

    void am_rename(String name);

}
