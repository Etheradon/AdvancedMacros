package com.theincgi.advancedmacros.lua.functions.midi;

import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

import javax.sound.midi.MidiDevice;

public class StopAll extends ZeroArgFunction {

    @Override
    public LuaValue call() {
        synchronized (MidiLib2.devices) {

            for (MidiDevice device : MidiLib2.devices) {
                try {
                    device.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            MidiLib2.devices.clear();
        }
        return null;
    }

}
