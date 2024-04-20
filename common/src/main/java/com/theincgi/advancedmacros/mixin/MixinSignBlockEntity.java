package com.theincgi.advancedmacros.mixin;

import com.theincgi.advancedmacros.access.ISignBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SignBlockEntity.class)
public class MixinSignBlockEntity implements ISignBlockEntity {

    @Shadow
    private SignText frontText;

    @Shadow
    private SignText backText;

    @Override
    public Text[] am_getFrontLines() {
        return frontText.getMessages(false);
    }

    @Override
    public Text[] am_getBackLines() {
        return backText.getMessages(false);
    }

}
