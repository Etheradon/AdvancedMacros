package com.theincgi.advancedmacros.lua;

import com.theincgi.advancedmacros.AdvancedMacros;
import com.theincgi.advancedmacros.lua.LuaDebug.LuaThread;
import com.theincgi.advancedmacros.misc.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

import java.io.InputStream;
import java.util.Optional;

public class OpenChangeLog extends ZeroArgFunction {

    @Override
    public LuaValue call() {
        openChangeLog(true);
        return NONE;
    }

    public static void openChangeLog(boolean force) {
        try {
            Optional<Resource> res = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(AdvancedMacros.MOD_ID, "scripts/changelogviewer.lua"));
            if (res.isEmpty()) {
                return;
            }
            InputStream in = res.get().getInputStream();
            LuaValue sFunc = AdvancedMacros.globals.load(in, "changeLog", "t", AdvancedMacros.globals);
            in.close();
            if (force) {
                new LuaThread(sFunc, Utils.varargs(valueOf("force")), "Start up Change Log").start();
            } else {
                new LuaThread(sFunc, "Start up Change Log").start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
