package com.theincgi.advancedmacros.mixin;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;

import com.theincgi.advancedmacros.access.ISignEditScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(SignEditScreen.class)
public class MixinSignEditScreen implements ISignEditScreen {
    @Shadow @Final private SignBlockEntity sign;

    @Override
    public SignBlockEntity am_getSignBlockEntity() {
        return sign;
    }
}
