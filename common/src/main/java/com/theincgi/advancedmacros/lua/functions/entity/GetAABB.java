package com.theincgi.advancedmacros.lua.functions.entity;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import com.google.common.base.Predicates;
import com.theincgi.advancedmacros.misc.CallableTable;
import com.theincgi.advancedmacros.misc.Pair;
import com.theincgi.advancedmacros.misc.Utils;
import org.luaj.vm2_v3_0_1.LuaError;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.Varargs;
import org.luaj.vm2_v3_0_1.lib.VarArgFunction;

import java.util.List;

public class GetAABB {
    @SuppressWarnings("unchecked")
    //private static final Predicate<Entity> ARROW_TARGETS = Predicates.and(EntityPredicates.NOT_SPECTATING, EntityPredicates.IS_ALIVE); //new Predicate<Entity>()
    //	{
    //		public boolean apply(@Nullable Entity p_apply_1_)
    //		{
    //			return p_apply_1_.canBeCollidedWith();
    //		}
    //	}
    private CallableTable func;
    private static final String[] LOCATION = {"entity", "getAABB"};

    public GetAABB() {
        func = new CallableTable(LOCATION, new Get());
    }

    public CallableTable getFunc() {
        return func;
    }

    private class Get extends VarArgFunction {
        MinecraftClient mc = MinecraftClient.getInstance();
        World world = mc.world;

        /**
         * Given 1 table: Given 3 numbers: used as block pos break; Given 1 number : used as entity
         * id break;
         */
        @Override
        public Varargs invoke(Varargs args) {
            world = mc.world;
            if (args.narg() == 3 || args.arg1().istable()) {
                BlockPos pos;
                if (args.arg1().istable()) {
                    LuaValue t = args.arg1();
                    pos = new BlockPos(t.get(1).checkdouble(), t.get(2).checkdouble(), t.get(3).checkdouble());
                } else {
                    pos = new BlockPos(args.arg(1).checkdouble(), args.arg(2).checkdouble(), args.arg(3).checkdouble());
                }
                BlockState block = world.getBlockState(pos);
                if (block.isAir()) {
                    return FALSE;
                }
                VoxelShape vs = block.getCollisionShape(world, pos);
                if (vs.isEmpty()) {
                    return FALSE;
                }
                Box bb = vs.getBoundingBox();
                if (bb == null) {
                    return FALSE;
                }
                LuaTable out = new LuaTable();
                out.set(1, new AABB(bb));
                out.set(2, !block.getCollisionShape(world, pos).equals(VoxelShapes.empty()));
                return out.unpack();
            } else if (args.narg() == 1) {
                Entity e = world.getEntityById(args.arg1().checkint());
                Box bb = e.getBoundingBox();
                if (bb == null) {
                    return FALSE;
                }
                return new AABB(bb, e);
            } else {
                throw new LuaError("Invalid arguments");
            }
        }
    }

    private static class AABB extends LuaTable {
        Box aabb;
        Entity entity;

        public AABB(Box aabb, Entity e) {
            this(aabb);
            entity = e;
        }

        public AABB(Box aabb) {
            super();
            this.aabb = aabb;
            for (OpCode code : OpCode.values()) {
                this.set(code.name(), new DoOp(code));
            }
            entity = entity == null ? MinecraftClient.getInstance().player : entity;
            this.set("__class", "AxisAlignedBoudingBox");
        }

        private class DoOp extends VarArgFunction {
            OpCode code;

            public DoOp(OpCode code) {
                super();
                this.code = code;
            }

            @Override
            public Varargs invoke(Varargs args) {
                switch (code) {
                    case contains: //check if a point is inside the AABB
                        return valueOf(aabb.contains(new Vec3d(args.arg1().checkdouble(), args.arg(2).checkdouble(), args.arg(3).checkdouble())));
                    case contract: //make it smaller in one dir, negative effects lower end
                        return new AABB(aabb.contract(args.arg1().checkdouble(), args.arg(2).checkdouble(), args.arg(3).checkdouble()));
                    case expand:
                        return new AABB(aabb.expand(args.arg1().checkdouble(), args.arg(2).checkdouble(), args.arg(3).checkdouble()));
                    case getCenter: {
                        LuaTable temp = new LuaTable();
                        Vec3d v = aabb.getCenter();
                        temp.set(1, v.x);
                        temp.set(2, v.y);
                        temp.set(3, v.z);
                        return temp.unpack();
                    }
                    case getPoints: {
                        LuaTable temp = new LuaTable();
                        temp.set(1, aabb.minX);
                        temp.set(2, aabb.minY);
                        temp.set(3, aabb.minZ);
                        temp.set(4, aabb.maxX);
                        temp.set(5, aabb.maxY);
                        temp.set(6, aabb.maxZ);
                        return temp.unpack();
                    }
                    case grow: {
                        double def = args.arg1().checkdouble();
                        return new AABB(aabb.expand(def, args.optdouble(2, def), args.optdouble(3, def)));
                    }
                    case intersects: {
                        if (args.arg1() instanceof AABB) {
                            AABB a = (AABB) args.arg1();
                            return valueOf(aabb.intersects(a.aabb));
                        }
                        throw new LuaError("Not an Axis Aligned Bounding Box");
                    }
                    case intersect:
                        if (args.arg1() instanceof AABB) {
                            AABB a = (AABB) args.arg1();
                            return new AABB(aabb.intersection(a.aabb));
                        }
                        throw new LuaError("Not an Axis Aligned Bounding Box");
                    case offset:
                        return new AABB(aabb.offset(args.arg1().checkdouble(), args.arg(2).checkdouble(), args.arg(3).checkdouble()));
                    case shrink:
                        return new AABB(aabb.shrink(args.arg1().checkdouble(), args.arg(2).checkdouble(), args.arg(3).checkdouble()));
                    case union:
                        if (args.arg1() instanceof AABB) {
                            AABB a = (AABB) args.arg1();
                            return new AABB(aabb.union(a.aabb));
                        }
                        throw new LuaError("Not an Axis Aligned Bounding Box");
                        //				case calculateIntercept:{
                        //					Pair<Vec3d, Varargs> v1 = Utils.consumeVector(args, false, false);
                        //					Pair<Vec3d, Varargs> v2 = Utils.consumeVector(v1.b, false, false);
                        //					return Utils.rayTraceResultToLuaValue(aabb.calculateIntercept(v1.a, v2.a)); //raytrace now?
                        //				}
                    case findEntityOnPath: {
                        World world = MinecraftClient.getInstance().world;
                        Pair<Vec3d, Varargs> v1 = Utils.consumeVector(args, false, false);
                        Entity entity = null;
                        Vec3d start = aabb.getCenter();
                        Vec3d end = start.add(v1.a);
                        List<Entity> list = world.getOtherEntities(entity, aabb.expand(v1.a.x, v1.a.y, v1.a.z).expand(1.0D), Predicates.alwaysTrue());
                        double d0 = 0.0D;

                        for (int i = 0; i < list.size(); ++i) {
                            Entity entity1 = list.get(i);

                            //				            if (entity1 != this.shootingEntity || this.ticksInAir >= 5)
                            //				            {
                            Box Box = entity1.getBoundingBox().expand(0.30000001192092896D);
                            EntityHitResult entityRayTraceResult = ProjectileUtil.raycast(entity, start, end, aabb, Predicates.alwaysTrue(), Double.MAX_VALUE);

                            if (entityRayTraceResult != null) {
                                return Utils.rayTraceResultToLuaValue(entityRayTraceResult);
                            }
                            return NONE;
                        }
                    }
                    default:
                        throw new LuaError("Undefined operation: " + code.name());
                }
            }
        }

        static enum OpCode {
            getPoints,
            contains,
            expand,
            contract,
            getCenter,
            grow,
            shrink,
            intersect,
            intersects,
            offset,
            union,
            //calculateIntercept,
            findEntityOnPath;
        }
    }
}
