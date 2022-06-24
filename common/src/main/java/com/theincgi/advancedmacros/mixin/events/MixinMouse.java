package com.theincgi.advancedmacros.mixin.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;

import com.theincgi.advancedmacros.AdvancedMacros;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Mouse.class)
public class MixinMouse {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void onMouseButton(long window, int key, int action, int mods, final CallbackInfo info) {
        if (window != client.getWindow().getHandle()) {
            return;
        }
        AdvancedMacros.EVENT_HANDLER.onKeyInput(key, -1, action, mods);
    }

}
