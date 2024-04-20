package com.theincgi.advancedmacros.mixin;

import com.theincgi.advancedmacros.AdvancedMacros;
import com.theincgi.advancedmacros.access.IMinecraftClient;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient implements IMinecraftClient {

    @Shadow
    protected abstract boolean doAttack();

    @Shadow
    protected abstract void doItemUse();

    @Shadow
    protected abstract void doItemPick();

    @Shadow
    private Thread thread;

    @Inject(method = "Lnet/minecraft/client/MinecraftClient;<init>(Lnet/minecraft/client/RunArgs;)V", at = @At("RETURN"))
    public void am_postInit(CallbackInfo ci) {
        AdvancedMacros.postInit();
    }

    @Override
    public void am_doAttack() {
        doAttack();
    }

    @Override
    public void am_doItemUse() {
        doItemUse();
    }

    @Override
    public void am_doItemPick() {
        doItemPick();
    }

    @Override
    public Thread am_getThread() {
        return thread;
    }

}
