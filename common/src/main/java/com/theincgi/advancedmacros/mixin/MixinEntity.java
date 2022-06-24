package com.theincgi.advancedmacros.mixin;

import net.minecraft.entity.Entity;

import com.theincgi.advancedmacros.access.IEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity {

    @Shadow protected abstract void setFlag(int index, boolean value);

    @Override
    public void am_setFlag(int index, boolean value) {
        setFlag(index, value);
    }
}
