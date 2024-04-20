package com.theincgi.advancedmacros.lua.functions;

import com.theincgi.advancedmacros.event.TaskDispatcher;
import com.theincgi.advancedmacros.misc.CallableTable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;

public class Connect extends CallableTable {

    public Connect() {
        super(new String[]{"connect"}, new Op());
    }

    private static class Op extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {

            TaskDispatcher.addTask(() -> {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.world != null) {
                    Disconnect.disconnect();
                }
                ServerInfo sDat = new ServerInfo(I18n.translate("selectServer.defaultName"), "", ServerInfo.ServerType.OTHER);
                sDat.address = arg.checkjstring();

                MultiplayerScreen mp = new MultiplayerScreen(null);

                mc.setScreen(new DirectConnectScreen(mp, callback -> {
                }, sDat)); //TESTME direct connect

            });
            return NONE;
        }

    }

}
