package com.theincgi.advancedmacros.neoforge;

import com.theincgi.advancedmacros.AdvancedMacros;
import com.theincgi.advancedmacros.event.EventHandler;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;

@Mod(AdvancedMacros.MOD_ID)
public class AdvancedMacrosForge {

    public AdvancedMacrosForge() {
        AdvancedMacros.init();
        NeoForge.EVENT_BUS.addListener(AdvancedMacrosForge::onTick);
        NeoForge.EVENT_BUS.addListener(AdvancedMacrosForge::registerBindings);
    }

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(AdvancedMacros.modKeybind);
    }

    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            EventHandler.onTick(Minecraft.getInstance());
        }
    }

}
