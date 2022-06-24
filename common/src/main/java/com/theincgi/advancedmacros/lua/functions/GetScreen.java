package com.theincgi.advancedmacros.lua.functions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;

import com.google.common.util.concurrent.ListenableFuture;
import com.theincgi.advancedmacros.event.TaskDispatcher;
import com.theincgi.advancedmacros.lua.util.BufferedImageControls;
import com.theincgi.advancedmacros.misc.Utils;
import org.luaj.vm2_v3_0_1.LuaError;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class GetScreen extends ZeroArgFunction {

    @Override
    public LuaValue call() {
        MinecraftClient mc = MinecraftClient.getInstance();

        ListenableFuture<BufferedImage> futureImage = TaskDispatcher.addTask(new Callable<BufferedImage>() {
            @Override
            public BufferedImage call() throws Exception {
                NativeImage ni = ScreenshotRecorder.takeScreenshot(mc.getFramebuffer());

                return Utils.nativeImageToBufferedImage(ni);
            }
        });

        try {
            return new BufferedImageControls(futureImage.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new LuaError("Error occurred getting the screenshot");
        }
    }
}
