package com.theincgi.advancedmacros.mixin;

import net.minecraft.client.gui.widget.TextFieldWidget;

import com.theincgi.advancedmacros.access.ITextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(TextFieldWidget.class)
public class MixinTextFieldWidget implements ITextFieldWidget {
    @Shadow
    private int maxLength;

    @Override
    public int am_getMaxLength() {
        return maxLength;
    }
}
