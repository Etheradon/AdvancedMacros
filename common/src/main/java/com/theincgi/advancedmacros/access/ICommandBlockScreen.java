package com.theincgi.advancedmacros.access;

import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.gui.widget.TextFieldWidget;

public interface ICommandBlockScreen {

    void am_syncSettingsToServer();

    CommandBlockBlockEntity am_getCommandBlockEntity();

    CommandBlockBlockEntity.Type am_getCommandBlockType();

    TextFieldWidget am_getCommandField();

    void am_setCommandBlockType(CommandBlockBlockEntity.Type type);

    void am_setConditional(boolean conditional);

    void am_setAuto(boolean autoActivate);

}
