package com.theincgi.advancedmacros.lua.functions;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import com.theincgi.advancedmacros.misc.Utils;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.ThreeArgFunction;

public class GetBlock extends ThreeArgFunction {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        int x = (arg1.checkint()), y = arg2.checkint(), z = (arg3.checkint());
        BlockPos pos = new BlockPos(x, y, z);
        Chunk chunk = MinecraftClient.getInstance().world.getChunk(pos);

        BlockEntity te = MinecraftClient.getInstance().world.getBlockEntity(pos);
        //		if(MinecraftClient.getInstance().world.getChunkProvider().isChunkLoaded(chunk.getPos())){
        //			return LuaValue.FALSE;
        //		}

        BlockState block = chunk.getBlockState(new BlockPos(x, y, z));
        if (block.getBlock().equals(Blocks.VOID_AIR)) {
            return FALSE;
        }
        LuaTable result = Utils.blockToTable(block, te);
        BlockState s;
        result.set("mapColor", Utils.parseColor(block.getMapColor(MinecraftClient.getInstance().player.world, pos)));
        return result;
    }
}
