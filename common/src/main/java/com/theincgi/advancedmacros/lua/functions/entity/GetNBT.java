package com.theincgi.advancedmacros.lua.functions.entity;

import com.google.common.util.concurrent.ListenableFuture;
import com.theincgi.advancedmacros.event.TaskDispatcher;
import com.theincgi.advancedmacros.misc.Utils;
import com.theincgi.advancedmacros.misc.Utils.NBTUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;

import java.util.concurrent.ExecutionException;

public class GetNBT {

    public static class GetPlayerNBT extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            AbstractClientPlayerEntity acpe = null;
            if (arg.isnil()) {
                acpe = MinecraftClient.getInstance().player;
            }
            if (acpe == null) {
                acpe = Utils.findPlayerByName(arg.checkjstring());
            }
            if (acpe != null) {
                return getNbt(acpe);
            }

            return FALSE;
        }

    }

    public static class GetEntityNBT extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            Entity entity = MinecraftClient.getInstance().world.getEntityById(arg.checkint());

            if (entity != null) {
                return getNbt(entity);
            }

            return FALSE;
        }

    }

    public static LuaValue getNbt(AbstractClientPlayerEntity player) {
        ListenableFuture<LuaValue> f = TaskDispatcher.addTask(() -> {
            NbtCompound nbtCompound = new NbtCompound();
            player.writeNbt(nbtCompound);
            return NBTUtils.fromCompound(nbtCompound);
        });
        TaskDispatcher.waitFor(f);
        try {
            return f.get();
        } catch (InterruptedException | ExecutionException e) {
            return LuaValue.FALSE;
        }
    }

    public static LuaValue getNbt(Entity e) {
        ListenableFuture<LuaValue> f = TaskDispatcher.addTask(() -> {
            NbtCompound nbtCompound = new NbtCompound();
            e.writeNbt(nbtCompound);
            return NBTUtils.fromCompound(nbtCompound);
        });
        TaskDispatcher.waitFor(f);
        try {
            return f.get();
        } catch (InterruptedException | ExecutionException e1) {
            return LuaValue.FALSE;
        }
    }

}
