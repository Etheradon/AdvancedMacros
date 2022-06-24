package com.theincgi.advancedmacros.fabric;

import com.theincgi.advancedmacros.AdvancedMacros;
import com.theincgi.advancedmacros.event.EventHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;


public class AdvancedMacrosFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AdvancedMacros.init();
        KeyBindingHelper.registerKeyBinding(AdvancedMacros.modKeybind);
        ClientTickEvents.END_CLIENT_TICK.register(EventHandler::onTick);
    }

}
