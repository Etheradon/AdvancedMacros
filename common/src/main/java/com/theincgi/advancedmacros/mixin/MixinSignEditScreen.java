package com.theincgi.advancedmacros.mixin;

import com.theincgi.advancedmacros.access.ISignEditScreen;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractSignEditScreen.class)
public class MixinSignEditScreen implements ISignEditScreen {

    @Shadow
    @Final
    private SignBlockEntity blockEntity;

    @Override
    public SignBlockEntity am_getSignBlockEntity() {
        return blockEntity;
    }

}
