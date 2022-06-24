package com.theincgi.advancedmacros.lua.functions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import com.theincgi.advancedmacros.misc.CallableTable;
import com.theincgi.advancedmacros.misc.Pair;
import com.theincgi.advancedmacros.misc.Utils;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.Varargs;
import org.luaj.vm2_v3_0_1.lib.VarArgFunction;

public class RayTrace {

    private static CallableTable func;

    private RayTrace() {
    }

    public static CallableTable getFunc() {
        if (func == null) {
            genFunc();
        }
        return func;
    }

    private static void genFunc() {
        func = new CallableTable(new String[]{"rayTrace"}, new RayTraceFunc());
    }

    public static class RayTraceFunc extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            //args: {vector}, <{from}>, <maxDist/REACH_LIMIT>, stopOnLiquid
            //args: {vector}, <maxDist/REACH_LIMIT>, stopOnLiquid
            MinecraftClient mc = MinecraftClient.getInstance();
            PlayerEntity p = mc.player;

            Pair<Vec3d, Varargs> vec = Utils.consumeVector(args, true, true); //look angle
            if (vec.a == null) {
                vec.a = p.getRotationVector();
            }
            Pair<Vec3d, Varargs> optVec = Utils.consumeVector(vec.b, true, false); //from pos
            if (optVec.a == null) {
                optVec.a = p.getEyePos();
            }
            double distance = mc.interactionManager.getReachDistance();
            if (optVec.b.arg1().isnumber()) {
                distance = optVec.b.arg1().checkdouble();
                optVec.b = optVec.b.subargs(2);
            }
            //System.out.println(distance);
            //System.out.println(optVec.b);

            boolean stopOnLiquid = optVec.b.optboolean(1, false);

            Vec3d end = optVec.a.add(vec.a.multiply(distance));
            RaycastContext rtc = new RaycastContext(optVec.a, end, RaycastContext.ShapeType.OUTLINE, stopOnLiquid ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, p);
            HitResult rtr = MinecraftClient.getInstance().world.raycast(rtc);
            //MinecraftClient.getInstance().objectMouseOver
            LuaValue result = Utils.rayTraceResultToLuaValue(rtr);
            return result;
        }
    }
}
