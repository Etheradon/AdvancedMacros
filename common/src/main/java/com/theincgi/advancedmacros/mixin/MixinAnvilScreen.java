package com.theincgi.advancedmacros.mixin;

import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;

import com.theincgi.advancedmacros.access.IAnvilScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AnvilScreen.class)
public abstract class MixinAnvilScreen implements IAnvilScreen {

    @Shadow
    private TextFieldWidget nameField;

    @Shadow
    protected abstract void onRenamed(String name);

    @Override
    public TextFieldWidget am_getNameField() {
        return nameField;
    }

    @Override
    public void am_rename(String name) {
        onRenamed(name);
    }
}
