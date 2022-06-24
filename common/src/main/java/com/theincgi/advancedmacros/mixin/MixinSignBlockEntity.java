package com.theincgi.advancedmacros.mixin;



import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.text.Text;

import com.theincgi.advancedmacros.access.ISignBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SignBlockEntity.class)
public class MixinSignBlockEntity implements ISignBlockEntity {

    @Shadow @Final private Text[] texts;

    @Override
    public Text[] am_getLines() {
        return texts;
    }
}
