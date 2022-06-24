package com.theincgi.advancedmacros.lua.functions;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import com.google.common.util.concurrent.ListenableFuture;
import com.theincgi.advancedmacros.AdvancedMacros;
import com.theincgi.advancedmacros.event.TaskDispatcher;
import com.theincgi.advancedmacros.misc.Utils;
import org.luaj.vm2_v3_0_1.LuaError;
import org.luaj.vm2_v3_0_1.LuaFunction;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.Varargs;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;
import org.luaj.vm2_v3_0_1.lib.ThreeArgFunction;
import org.luaj.vm2_v3_0_1.lib.VarArgFunction;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@SuppressWarnings("resource")
public class GetPlayer extends OneArgFunction {

    public static final LuaTable playerFunctions = new LuaTable();

    static {
        playerFunctions.set("getInventory", new PlayerValueFunction("getInventory", player -> {
            return Utils.inventoryToTable(player.getInventory(), !(player instanceof ClientPlayerEntity));
        }).threadSensitive());

        playerFunctions.set("getMainHand", new PlayerValueFunction("getMainHand", player -> {
            return Utils.itemStackToLuatable(player.getMainHandStack());
        }));
        playerFunctions.set("getOffHand", new PlayerValueFunction("getOffHand", player -> {
            return Utils.itemStackToLuatable(player.getOffHandStack());
        }));
        playerFunctions.set("getDimension", new PlayerValueFunction("getDimension", player -> {
            return Utils.toTable(player.world.getDimension());
        }));
        playerFunctions.set("getPitch", new PlayerValueFunction("getPitch", player -> {
            return valueOf(MathHelper.wrapDegrees(player.getPitch()));
        }));
        playerFunctions.set("getYaw", new PlayerValueFunction("getYaw", player -> {
            return valueOf(MathHelper.wrapDegrees(player.getYaw()));
        }));
/*        playerFunctions.set("getExp", new PlayerValueFunction("getExp", player -> {
            return valueOf(player.experienceProgress);
        }));*/
        playerFunctions.set("getExpLevel", new PlayerValueFunction("getExpLevel", player -> {
            return valueOf(player.experienceLevel);
        }));
        playerFunctions.set("getExpTotal", new PlayerValueFunction("getExpTotal", player -> {
            return valueOf(player.totalExperience);
        }));
        playerFunctions.set("getEyeHeight", new PlayerValueFunction("getEyeHeight", player -> {
            return valueOf(player.getEyeY());
        }));
        playerFunctions.set("getFallDist", new PlayerValueFunction("getFallDist", player -> {
            return valueOf(player.fallDistance);
        }));
        playerFunctions.set("getHeight", new PlayerValueFunction("getHeight", player -> {
            return valueOf(player.getHeight());
        }));
        playerFunctions.set("getWidth", new PlayerValueFunction("getWidth", player -> {
            return valueOf(player.getWidth());
        }));
        playerFunctions.set("getHurtResTime", new PlayerValueFunction("getHurtResTime", player -> {
            return valueOf(player.hurtTime);
        }));
        playerFunctions.set("isCollidedHorz", new PlayerValueFunction("isCollidedHorz", player -> {
            return valueOf(player.horizontalCollision);
        }));
        playerFunctions.set("isCollidedVert", new PlayerValueFunction("isCollidedVert", player -> {
            return valueOf(player.verticalCollision);
        }));
        playerFunctions.set("getSwingProgress", new PlayerValueFunction("getSwingProgress", player -> {
            return valueOf(player.handSwingProgress);
        }));
        playerFunctions.set("isSwingInProgress", new PlayerValueFunction("isSwingInProgress", player -> {
            return valueOf(player.handSwinging);
        }));
        playerFunctions.set("getMaxHurtResTime", new PlayerValueFunction("getMaxHurtResTime", player -> {
            return valueOf(player.maxHurtTime);
        }));
        playerFunctions.set("isNoClip", new PlayerValueFunction("isNoClip", player -> {
            return valueOf(player.noClip);
        }));
        playerFunctions.set("isOnGround", new PlayerValueFunction("isOnGround", player -> {
            return valueOf(player.isOnGround());
        }));
        playerFunctions.set("isInvulnerable", new PlayerValueFunction("isInvulnerable", player -> {
            return valueOf(player.isInvulnerable());
        }));
/*        playerFunctions.set("getBedLocation", new PlayerValueFunction("getBedLocation", player -> {
            LuaTable pos = new LuaTable();
            BlockPos p = player.getBedLocation(DimensionType.OVERWORLD);
            if (p != null) {
                pos.set(1, LuaValue.valueOf(p.getX()));
                pos.set(2, LuaValue.valueOf(p.getY()));
                pos.set(3, LuaValue.valueOf(p.getZ()));
                return pos.unpack();
            }
            return FALSE;
        }));*/
        playerFunctions.set("getTeam", new PlayerValueFunction("getTeam", player -> {
            return player.getScoreboardTeam() == null ? FALSE : valueOf(player.getScoreboardTeam().getName());
        }));
        playerFunctions.set("getLuck", new PlayerValueFunction("getLuck", player -> {
            return valueOf(player.getLuck());
        }));
        playerFunctions.set("getHealth", new PlayerValueFunction("getHealth", player -> {
            return valueOf(player.getHealth());
        }));
        playerFunctions.set("getHunger", new PlayerValueFunction("getHunger", player -> {
            return valueOf(MathHelper.ceil(player.getHungerManager().getFoodLevel()));
        }));
        playerFunctions.set("getHungerExact", new PlayerValueFunction("getHungerExact", player -> {
            return valueOf(player.getHungerManager().getFoodLevel());
        }));
        playerFunctions.set("getAir", new PlayerValueFunction("getAir", player -> {
            return valueOf(player.getAir());
        }));
        playerFunctions.set("hasNoGravity", new PlayerValueFunction("hasNoGravity", player -> {
            return valueOf(player.hasNoGravity());
        }));
        playerFunctions.set("getVelocity", new PlayerValueFunction("getVelocity", player -> {
            return Utils.toTable(player.getVelocity()).unpack();
        }));
        playerFunctions.set("isSneaking", new PlayerValueFunction("isSneaking", player -> {
            return valueOf(player.isSneaking());
        }));
        playerFunctions.set("isOnLadder", new PlayerValueFunction("isOnLadder", player -> {
            return valueOf(player.isHoldingOntoLadder());
        }));
        playerFunctions.set("isInWater", new PlayerValueFunction("isInWater", player -> {
            return valueOf(player.isTouchingWater());
        }));
        playerFunctions.set("isInLava", new PlayerValueFunction("isInLava", player -> {
            return valueOf(player.isInLava());
        }));
        playerFunctions.set("isImmuneToFire", new PlayerValueFunction("isImmuneToFire", player -> {
            return valueOf(player.isFireImmune());
        }));
        playerFunctions.set("isImmuneToExplosion", new PlayerValueFunction("isImmuneToExplosion", player -> {
            return valueOf(player.isImmuneToExplosion());
        }));
        playerFunctions.set("isEyltraFlying", new PlayerValueFunction("isEyltraFlying", player -> {
            return valueOf(player.isFallFlying());
        }));
        playerFunctions.set("isOnFire", new PlayerValueFunction("isOnFire", player -> {
            return valueOf(player.isOnFire());
        }));
        playerFunctions.set("isSprinting", new PlayerValueFunction("isSprinting", player -> {
            return valueOf(player.isSprinting());
        }));
        playerFunctions.set("getPotionEffects", new PlayerValueFunction("getPotionEffects", player -> {
            LuaTable effects = new LuaTable();
            int i = 1;
            for (StatusEffectInstance pe : player.getActiveStatusEffects().values()) {
                effects.set(i++, Utils.effectToTable(pe));
            }
            return effects;
        }).threadSensitive());
        playerFunctions.set("getRidingEntity", new PlayerValueFunction("getRidingEntity", player -> {
            return Utils.entityToTable(player.getVehicle());
        }));
        playerFunctions.set("isSleeping", new PlayerValueFunction("isSleeping", player -> {
            return valueOf(player.isSleeping());
        }));
        playerFunctions.set("isInvisible", new PlayerValueFunction("isInvisible", player -> {
            return valueOf(player.isInvisible());
        }));
        playerFunctions.set("getUUID", new PlayerValueFunction("getUUID", player -> {
            return valueOf(player.getUuid().toString());
        }));
        playerFunctions.set("lookingAt", new PlayerValueFunction("lookingAt", player -> {
            HitResult rtr = player.raycast(8, 0, false); //CHECKME
            if (rtr != null) {
                BlockPos lookingAt = ((BlockHitResult) rtr).getBlockPos();
                if (lookingAt != null) {
                    LuaTable look = new LuaTable();
                    look.set(1, LuaValue.valueOf(lookingAt.getX()));
                    look.set(2, LuaValue.valueOf(lookingAt.getY()));
                    look.set(3, LuaValue.valueOf(lookingAt.getZ()));
                    return look.unpack();
                }
            }
            return FALSE;
        }));
        playerFunctions.set("getEntityID", new PlayerValueFunction("getEntityID", player -> {
            return valueOf(player.getId());
        }));
        playerFunctions.set("getGamemode", new PlayerValueFunction("getGamemode", player -> {
            return valueOf(player.isSpectator() ? "spectator" : player.isCreative() ? "creative" : "survival");
        })); //TODO adventure?
        playerFunctions.set("getTarget", new PlayerValueFunction("getTarget", player -> {
            if (player.equals(MinecraftClient.getInstance().player)) {
                return Utils.rayTraceResultToLuaValue(MinecraftClient.getInstance().crosshairTarget);
            }
            return FALSE;
        }));

        playerFunctions.set("isBlocking", new PlayerValueFunction("isBlocking", player -> {
            return valueOf(player.isBlocking());
        }));
        playerFunctions.set("isActualySwimming", new PlayerValueFunction("isActualySwimming", player -> {
            return valueOf(player.isSwimming());
        }));
        playerFunctions.set("getPose", new PlayerValueFunction("getPose", player -> {
            return valueOf(player.getPose().name());
        }));
        playerFunctions.set("isGlowing", new PlayerValueFunction("isGlowing", player -> {
            return valueOf(player.isGlowing());
        }));
        playerFunctions.set("isInBubbleColumn", new PlayerValueFunction("isInBubbleColumn", player -> {
            return valueOf(player.world.getBlockState(player.getBlockPos()).getBlock() == Blocks.BUBBLE_COLUMN);
        }));
        playerFunctions.set("isPassenger", new PlayerValueFunction("isPassenger", player -> {
            return valueOf(player.hasVehicle());
        }));
        playerFunctions.set("isFullyAsleep", new PlayerValueFunction("isFullyAsleep", player -> {
            return valueOf(player.isSleeping());
        }));
        playerFunctions.set("canBePushedByWater", new PlayerValueFunction("canBePushedByWater", player -> {
            return valueOf(player.isPushedByFluids());
        }));
/*        playerFunctions.set("isSpinAttacking", new PlayerValueFunction("isSpinAttacking", player -> {
            return valueOf(player.isSpinAttacking());
        }));*/
        playerFunctions.set("isWet", new PlayerValueFunction("isWet", player -> {
            return valueOf(player.isWet());
        }));
        //		if(player instanceof ClientPlayerEntity) getHotbar
        //			t.set("invSlot", ((ClientPlayerEntity)player).inventory.currentItem+1);
    }

    @Override
    public LuaValue call(LuaValue playerName) {
        ListenableFuture<LuaValue> future = TaskDispatcher.addTask(() -> {
            if (playerName.isnil()) {
                return entityPlayerToTable(MinecraftClient.getInstance().player);
            } else {
                try {
                    String toFind = playerName.checkjstring();
                    AbstractClientPlayerEntity acpe = Utils.findPlayerByName(toFind);
                    if (acpe != null) {
                        return entityPlayerToTable(acpe);
                    }
                    return FALSE;
                } catch (NullPointerException npe) {
                    return LuaValue.FALSE;
                }
            }
        });
        try {
            return future.get();
        } catch (Exception ex) {
            Utils.logError(ex);
            return FALSE;
        }
    }

    public static LuaValue entityPlayerToTable(PlayerEntity player) {
        if (player == null) {
            return NIL;
        }
        try {
            LuaTable t = new LuaTable() {
                LuaFunction func = new ThreeArgFunction() {
                    @Override
                    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
                        player.setVelocity(arg1.checkdouble(), arg2.checkdouble(), arg3.checkdouble());
                        return NONE;
                    }
                };

                @Override
                public LuaValue rawget(LuaValue key) { //secret function
                    if (key.checkjstring().equals("setVelocity") && player instanceof ClientPlayerEntity) {
                        return func;
                    }
                    return super.rawget(key);
                }
            };
            t.set("name", player.getName().getString());
            t.set("inventory", Utils.inventoryToTable(player.getInventory(), !(player instanceof ClientPlayerEntity)));
            {
                LuaTable pos = new LuaTable();
                pos.set(1, LuaValue.valueOf(player.getX()));
                pos.set(2, LuaValue.valueOf(player.getY()));
                pos.set(3, LuaValue.valueOf(player.getZ()));
                t.set("pos", pos);
            }
            t.set("mainHand", Utils.itemStackToLuatable(player.getMainHandStack()));
            t.set("offHand", Utils.itemStackToLuatable(player.getOffHandStack()));
            if (player instanceof ClientPlayerEntity) {
                t.set("invSlot", ((ClientPlayerEntity) player).getInventory().selectedSlot + 1);
            }

            t.set("dimension", Utils.toTable(player.world.getDimension()));
            t.set("pitch", MathHelper.wrapDegrees(player.getPitch()));//player.rotationPitch);
            t.set("yaw", MathHelper.wrapDegrees(player.getYaw()));//player.rotationYawHead);
            //t.set("exp", player.experience);
            t.set("expLevel", player.experienceLevel);
            t.set("expTotal", player.totalExperience);
            t.set("eyeHeight", player.getEyeY());
            t.set("fallDist", player.fallDistance);
            t.set("height", player.getHeight());
            t.set("width", player.getWidth());
            t.set("hurtResTime", player.hurtTime);
            //t.set("isAirborne", LuaValue.valueOf(player.isAirBorne));
            t.set("isCollidedHorz", LuaValue.valueOf(player.horizontalCollision));
            t.set("isCollidedVert", LuaValue.valueOf(player.verticalCollision));
            t.set("swingProgress", LuaValue.valueOf(player.handSwingProgress));
            t.set("maxHurtResTime", LuaValue.valueOf(player.maxHurtTime));
            t.set("isNoClip", LuaValue.valueOf(player.noClip));
            t.set("onGround", LuaValue.valueOf(player.isOnGround()));
            t.set("isInvulnerable", LuaValue.valueOf(player.isInvulnerable()));
/*                LuaTable pos = new LuaTable();
                BlockPos p = player.getBedLocation(DimensionType.OVERWORLD);
                if (p != null) {
                    pos.set(1, LuaValue.valueOf(p.getX()));
                    pos.set(2, LuaValue.valueOf(p.getY()));
                    pos.set(3, LuaValue.valueOf(p.getZ()));
                    t.set("bedLocation", pos);
                }*/
            t.set("team", player.getScoreboardTeam() == null ? FALSE : valueOf(player.getScoreboardTeam().getName()));
            t.set("luck", player.getLuck());
            t.set("health", MathHelper.ceil(player.getHealth()));
            t.set("hunger", MathHelper.ceil(player.getHungerManager().getFoodLevel()));
            t.set("air", player.getAir());
            t.set("hasNoGravity", LuaValue.valueOf(player.hasNoGravity()));
            {
                LuaTable velocity = Utils.toTable(player.getVelocity());

                t.set("velocity", velocity);
            }
            t.set("isSneaking", LuaValue.valueOf(player.isSneaking()));
            t.set("isOnLadder", LuaValue.valueOf(player.isHoldingOntoLadder()));
            t.set("isInWater", LuaValue.valueOf(player.isTouchingWater()));
            t.set("isInLava", LuaValue.valueOf(player.isInLava()));
            t.set("immuneToFire", LuaValue.valueOf(player.isOnFire()));
            t.set("isImmuneToExplosion", LuaValue.valueOf(player.isImmuneToExplosion()));
            t.set("isEyltraFlying", LuaValue.valueOf(player.isFallFlying()));
            t.set("isOnFire", LuaValue.valueOf(player.isOnFire()));
            t.set("isSprinting", LuaValue.valueOf(player.isSprinting()));
            {
                LuaTable effects = new LuaTable();
                int i = 1;
                for (StatusEffectInstance pe : player.getActiveStatusEffects().values()) {
                    effects.set(i++, Utils.effectToTable(pe));
                }
                t.set("potionEffects", effects);
            }
            t.set("entityRiding", Utils.entityToTable(player.getVehicle()));
            t.set("isSleeping", LuaValue.valueOf(player.isSleeping()));
            t.set("isInvisible", LuaValue.valueOf(player.isInvisible()));
            t.set("uuid", LuaValue.valueOf(player.getUuid().toString()));
            {
                HitResult rtr = player.raycast(8, 0, false); //CHECKME
                if (rtr != null) {
                    BlockPos lookingAt = ((BlockHitResult) rtr).getBlockPos();
                    if (lookingAt != null) {
                        LuaTable look = new LuaTable();
                        look.set(1, LuaValue.valueOf(lookingAt.getX()));
                        look.set(2, LuaValue.valueOf(lookingAt.getY()));
                        look.set(3, LuaValue.valueOf(lookingAt.getZ()));
                        t.set("lookingAt", look);
                    }
                }
            }
            t.set("entityID", valueOf(player.getId()));
            t.set("gamemode", player.isSpectator() ? "spectator" : player.isCreative() ? "creative" : "survival"); //FIXME ... adventure?

            if (player.equals(MinecraftClient.getInstance().player)) {
                t.set("target", Utils.rayTraceResultToLuaValue(MinecraftClient.getInstance().crosshairTarget));
            }

            return t;
        } catch (Exception e) {
            e.printStackTrace();
            return NIL;
        }
    }

    public static Optional<PlayerEntity> getPlayerFromLuaValue(LuaValue value) {
        if (value.isnil()) {
            return Optional.ofNullable(AdvancedMacros.getMinecraft().player);
        } else {
            try {
                String toFind = value.checkjstring();
                AbstractClientPlayerEntity acpe = Utils.findPlayerByName(toFind);
                if (acpe != null) {
                    return Optional.of(acpe);
                }
                return Optional.empty();
            } catch (NullPointerException npe) {
                return Optional.empty();
            }
        }
    }

    private static class PlayerValueFunction extends VarArgFunction {
        private boolean threadSensitive = false;
        private final Function<PlayerEntity, Varargs> get;
        private final String fName;

        public PlayerValueFunction(String fName, Function<PlayerEntity, Varargs> get) {
            this.fName = fName;
            this.get = get;
        }

        public PlayerValueFunction threadSensitive() {
            threadSensitive = true;
            return this;
        }

        @Override
        public Varargs invoke(Varargs args) {
            final Optional<PlayerEntity> player = getPlayerFromLuaValue(args.arg1());
            if (!player.isPresent()) {
                return NIL;
            }
            if (threadSensitive) {
                ListenableFuture<Varargs> x = TaskDispatcher.addTask(() -> {
                    return get.apply(player.get());
                });
                try {
                    return x.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new LuaError(e);
                }
            } else {
                return get.apply(player.get());
            }
        }

        @Override
        public LuaValue tostring() {
            return valueOf(toString());
        }

        @Override
        public String toString() {
            return "function " + fName + "([String: playerName])";
        }
    }

}
