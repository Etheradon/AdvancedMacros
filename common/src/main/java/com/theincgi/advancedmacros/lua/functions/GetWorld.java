package com.theincgi.advancedmacros.lua.functions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;

import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

public class GetWorld extends ZeroArgFunction {

    @Override
    public LuaValue call() {
        try {
            World world = MinecraftClient.getInstance().player.getEntityWorld();
            return worldToTable(world);
        } catch (NullPointerException npe) {
            return LuaValue.FALSE;
        }

    }

    private static void set(LuaTable t, String string, WorldBorder worldBorder) {
        if (worldBorder == null) {
            t.set(string, LuaValue.FALSE);
            return;
        }
        LuaTable b = new LuaTable();
        b.set("centerX", worldBorder.getCenterX());
        b.set("centerZ", worldBorder.getCenterZ());
        b.set("warningDist", worldBorder.getWarningBlocks());
        b.set("dmgPerBlock", worldBorder.getDamagePerBlock());
        b.set("dmgBuffer", worldBorder.getDamagePerBlock());
        b.set("radius", worldBorder.getMaxRadius() / 2);
        b.set("size", worldBorder.getSize());
        t.set(string, b);
    }

    private static void set(LuaTable t, String string, long seed) {
        t.set(string, seed);
    }

    private static void set(LuaTable t, String string, BlockPos spawnPoint) {
        LuaTable p = new LuaTable();
        p.set(1, LuaValue.valueOf(spawnPoint.getX()));
        p.set(2, LuaValue.valueOf(spawnPoint.getY()));
        p.set(3, LuaValue.valueOf(spawnPoint.getZ()));
        t.set(string, p);
    }

    private static void set(LuaTable t, String string, boolean difficultyLocked) {
        t.set(string, LuaValue.valueOf(difficultyLocked));
    }

    private static void set(LuaTable t, String string, String name) {
        t.set(string, name);
    }

    private static void set(LuaTable t, String string, int arg) {
        t.set(string, LuaValue.valueOf(arg));
    }

    public static LuaTable worldToTable(World world) {
        LuaTable t = new LuaTable();
        set(t, "isRemote", !world.isClient());
        set(t, "isDaytime", world.isDay());
        set(t, "worldTime", world.getTimeOfDay()); //TESTME
        {
            String stat;
            if (world.isRaining() && world.isThundering()) {
                stat = "thunder";
            } else if (world.isRaining() && !world.isThundering()) {
                stat = "rain";
            } else if (!world.isRaining() && world.isThundering()) {
                stat = "only thunder";
            } else {
                stat = "clear";
            }
            set(t, "weather", stat);
        }
        set(t, "moonPhase", world.getMoonPhase());
        //set(t, "clearWeatherTime", world.getLevelProperties().getClearWeatherTime());
        //set(t, "rainTime", world.getLevelProperties().getRainTime());
        set(t, "gameType", world.getLevelProperties().getDifficulty().getName());
        set(t, "seed", world.getLevelProperties().isRaining());
        set(t, "difficulty", world.getDifficulty().name().toLowerCase());

        //set(t, "name", world.getLevelProperties().getWorldName());
        set(t, "spawn", world.getSpawnPos());
        set(t, "border", world.getWorldBorder());
        set(t, "isHardcore", world.getLevelProperties().isHardcore());
        set(t, "isDifficultyLocked", world.getLevelProperties().isDifficultyLocked());
        set(t, "isSinglePlayer", MinecraftClient.getInstance().isInSingleplayer());
        IntegratedServer is = MinecraftClient.getInstance().getServer();
        set(t, "isLanHost", is != null && is.isRemote());
        //set(t, "connectionType", NetworkHooks.getConnectionType(() -> MinecraftClient.getInstance().getNetworkHandler()).name()); //yeilded modded
        if (MinecraftClient.getInstance().getCurrentServerEntry() != null) {
            ServerInfo sd = MinecraftClient.getInstance().getCurrentServerEntry();
            if (sd != null) {
                t.set("serverName", sd.name == null ? LuaValue.FALSE : LuaValue.valueOf(sd.name));
                t.set("messageOfTheDay", sd.label == null ? LuaValue.FALSE : LuaValue.valueOf(sd.label.getString()));
                t.set("ip", sd.address == null ? LuaValue.FALSE : LuaValue.valueOf(sd.address));
            }
        }
        return t;
    }
}
