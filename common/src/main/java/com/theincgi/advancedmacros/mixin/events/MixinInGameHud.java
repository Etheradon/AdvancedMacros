package com.theincgi.advancedmacros.mixin.events;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;

import com.theincgi.advancedmacros.AdvancedMacros;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(method = "render", at = @At("RETURN"))
    public void am_onRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        AdvancedMacros.EVENT_HANDLER.afterOverlay(matrices);
    }

}
