package com.theincgi.advancedmacros.mixin.events;

import com.theincgi.advancedmacros.AdvancedMacros;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "renderWorld", at = @At("RETURN"))
    public void am_onRenderWorldLast(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        AdvancedMacros.EVENT_HANDLER.onLastWorldRender(matrices);
    }

}
