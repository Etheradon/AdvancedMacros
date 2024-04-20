package com.theincgi.advancedmacros.lua.functions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.luaj.vm2_v3_0_1.LuaError;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.Varargs;
import org.luaj.vm2_v3_0_1.lib.VarArgFunction;

public class GetBiome extends VarArgFunction {

    @Override
    public Varargs invoke(Varargs args) {
        BlockPos pos = null;

        if (args.narg() == 0) {
            pos = MinecraftClient.getInstance().player.getBlockPos();
        } else if (args.narg() == 2) {
            pos = new BlockPos(args.arg(1).checkint(), 64, args.arg(2).checkint());
        } else {
            throw new LuaError("Invalid args: NONE or X, Z");
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        LuaTable out = new LuaTable();
        Biome b = MinecraftClient.getInstance().world.getBiome(MinecraftClient.getInstance().player.getBlockPos()).value();
        out.set(1, mc.world.getRegistryManager().get(RegistryKeys.BIOME).getId(b).toString());
        LuaTable details = new LuaTable();
        // TODO: 1.20.4 - add block pos as argument
        //details.set("canSnow", LuaValue.valueOf(b.getPrecipitation().equals(Biome.Precipitation.SNOW)));
        //details.set("canRain", LuaValue.valueOf(b.getPrecipitation().equals(Biome.Precipitation.RAIN)));
        details.set("temp", b.getTemperature()); //cold medium warm ocean
        out.set(2, details);
        return out.unpack();
    }

}
