package com.theincgi.advancedmacros.lua.functions.os;

import net.minecraft.client.MinecraftClient;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;
import org.lwjgl.glfw.GLFW;

public class ClipBoard {

    public static class GetClipboard extends ZeroArgFunction {

        @Override
        public LuaValue call() {
            return valueOf(getClipboard());
        }

    }

    public static class SetClipboard extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            setClipboard(arg.tojstring());
            return NONE;
        }

    }

    public static void setClipboard(String toClipboard) {
        GLFW.glfwSetClipboardString(MinecraftClient.getInstance().getWindow().getHandle(), toClipboard);
    }

    public static String getClipboard() {
        try {
            return GLFW.glfwGetClipboardString(MinecraftClient.getInstance().getWindow().getHandle());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
