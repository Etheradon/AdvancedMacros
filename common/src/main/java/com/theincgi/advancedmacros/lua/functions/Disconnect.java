package com.theincgi.advancedmacros.lua.functions;

import com.theincgi.advancedmacros.event.TaskDispatcher;
import com.theincgi.advancedmacros.misc.CallableTable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

public class Disconnect extends CallableTable {

    public Disconnect() {
        super(new String[]{"disconnect"}, new Op());
    }

    private static class Op extends ZeroArgFunction {

        @Override
        public LuaValue call() {
            disconnect();
            return NONE;
        }

    }

    public static void disconnect() { //FIXME? //TESTME
        MinecraftClient mc = MinecraftClient.getInstance();
        TaskDispatcher.addTask(() -> {
            if (mc.world != null) {
                mc.world.disconnect();
            }
            mc.joinWorld(null);
            mc.setScreen(new TitleScreen());
        });
    }

}
