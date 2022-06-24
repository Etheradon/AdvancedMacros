package com.theincgi.advancedmacros.forge;

import net.minecraft.client.MinecraftClient;

import com.theincgi.advancedmacros.AdvancedMacros;
import com.theincgi.advancedmacros.event.EventHandler;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(AdvancedMacros.MODID)
public class AdvancedMacrosForge {

    public AdvancedMacrosForge() {
        AdvancedMacros.init();
        ClientRegistry.registerKeyBinding(AdvancedMacros.modKeybind);
        MinecraftForge.EVENT_BUS.addListener(AdvancedMacrosForge::onTick);
    }

    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            EventHandler.onTick(MinecraftClient.getInstance());
        }
    }

}
