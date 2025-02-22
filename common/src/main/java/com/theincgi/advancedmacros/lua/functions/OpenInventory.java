package com.theincgi.advancedmacros.lua.functions;

import com.google.common.util.concurrent.ListenableFuture;
import com.theincgi.advancedmacros.event.TaskDispatcher;
import com.theincgi.advancedmacros.misc.CallableTable;
import com.theincgi.advancedmacros.misc.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.BrewingStandScreen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.client.gui.screen.ingame.Generic3x3ContainerScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.luaj.vm2_v3_0_1.LuaError;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.Varargs;
import org.luaj.vm2_v3_0_1.lib.VarArgFunction;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

public class OpenInventory extends ZeroArgFunction {

    private static LuaValue mapping = LuaValue.FALSE;

    @Override
    public LuaValue call() {
        LuaTable controls = new LuaTable();

        for (OpCode op : OpCode.values()) {
            controls.set(op.name(), new CallableTable(op.getDocLocation(), new DoOp(op)));
        }
        controls.set("mapping", getMapping());
        controls.set("LMB", 0);
        controls.set("RMB", 1);
        controls.set("MMB", 2);
        return controls;
    }

    private class DoOp extends VarArgFunction {

        OpCode code;

        public DoOp(OpCode code) {
            super();
            this.code = code;

        }

        @Override
        public Varargs invoke(Varargs args) {
            MinecraftClient mc = MinecraftClient.getInstance();
            HandledScreen<?> container;
            if (mc.currentScreen instanceof HandledScreen) {
                container = (HandledScreen<?>) mc.currentScreen;
            } else {
                container = new InventoryScreen(mc.player);
            }

            //ItemStack held = mc.player.inventory.getItemStack();

            ClientPlayerInteractionManager ctrl = mc.interactionManager;
            int wID = container.getScreenHandler().syncId;

            switch (code) {
                case click: {
                    TaskDispatcher.addTask(() -> {
                        int slotA = args.arg1().checkint();
                        int mouseButton = args.optint(2, 0);
                        SlotActionType type = SlotActionType.PICKUP;
                        if (mouseButton == 2) {
                            type = SlotActionType.CLONE;
                        }
                        ctrl.clickSlot(wID, slotA - 1, mouseButton, type, mc.player);
                    });
                    return NONE;
                }
                case dragClick: {
                    TaskDispatcher.addTask(() -> {
                        LuaValue slots = args.arg1().checktable();
                        int mouseButton = args.optint(2, 0) == 0 ? 1 : 5;
                        ctrl.clickSlot(wID, -999, mouseButton - 1, SlotActionType.QUICK_CRAFT, mc.player);
                        for (int i = 1; i <= slots.length(); i++) {
                            int slot = slots.get(i).checkint();
                            ctrl.clickSlot(wID, slot - 1, mouseButton, SlotActionType.QUICK_CRAFT, mc.player);
                        }
                        ctrl.clickSlot(wID, -999, mouseButton + 1, SlotActionType.QUICK_CRAFT, mc.player);
                    });
                    return NONE;
                }
                case closeAndDrop:
                    TaskDispatcher.addTask(() -> {
                        ItemStack held = mc.player.currentScreenHandler.getCursorStack();
                        if (!held.isEmpty()) {
                            ctrl.clickSlot(wID, -999, 0, SlotActionType.PICKUP, mc.player);
                        }
                    });
                    return NONE;
                case close:
                    mc.player.closeScreen();
                    return NONE;
                case quick: {
                    TaskDispatcher.addTask(() -> {
                        int slotA = args.arg1().checkint();
                        SlotActionType type = SlotActionType.QUICK_MOVE;
                        ctrl.clickSlot(wID, slotA - 1, 0, type, mc.player);
                    });
                    return NONE;
                }
                case split: {
                    ListenableFuture<Void> x = TaskDispatcher.addTask(() -> {
                        int slotA = args.arg1().checkint();
                        SlotActionType type = SlotActionType.PICKUP;
                        int slotB = args.checkint(2);
                        ItemStack is1 = container.getScreenHandler().getSlot(slotA - 1).getStack();
                        ItemStack is2 = container.getScreenHandler().getSlot(slotB - 1).getStack();
                        if (!is2.isEmpty() && !Utils.itemsEqual(is1, is2)) {
                            throw new LuaError("Destination slot is occupied by a different item");
                        }
                        ctrl.clickSlot(wID, slotA - 1, 1, type, mc.player);
                        ctrl.clickSlot(wID, slotB - 1, 0, type, mc.player);
                    });
                    TaskDispatcher.waitFor(x);
                    try {
                        x.get();
                    } catch (Exception e) {
                        return FALSE;
                    }
                    return TRUE;
                }
                case getHeld: {
                    ItemStack held = mc.player.currentScreenHandler.getCursorStack();
                    return Utils.itemStackToLuatable(held);
                }
                case getSlot: {
                    int slotA = args.arg1().checkint();
                    return Utils.itemStackToLuatable(container.getScreenHandler().getSlot(slotA - 1).getStack());
                }
                case swap: {
                    TaskDispatcher.addTask((Runnable) () -> {
                        ItemStack held;
                        int slotA = args.checkint(1);
                        int slotB = args.checkint(2);
                        ItemStack is1 = container.getScreenHandler().getSlot(slotA - 1).getStack();
                        ItemStack is2 = container.getScreenHandler().getSlot(slotB - 1).getStack();
                        if (is1.isEmpty() && is2.isEmpty()) {
                            return;
                        }

                        SlotActionType type = SlotActionType.PICKUP;
                        if (!is1.isEmpty()) {
                            ctrl.clickSlot(wID, slotA - 1, 0, type, mc.player);
                        }
                        held = mc.player.currentScreenHandler.getCursorStack();
                        if ((!is2.isEmpty()) || (!held.isEmpty())) {
                            ctrl.clickSlot(wID, slotB - 1, 0, type, mc.player);
                        }
                        held = mc.player.currentScreenHandler.getCursorStack();
                        if (held.isEmpty()) {
                            return;
                        }
                        ctrl.clickSlot(wID, slotA - 1, 0, type, mc.player);
                    });
                    return NONE;
                }
                case grabAll: {
                    TaskDispatcher.addTask(() -> {
                        int slotA = args.checkint(1);
                        ctrl.clickSlot(wID, slotA - 1, 0, SlotActionType.PICKUP, mc.player);
                        ctrl.clickSlot(wID, slotA - 1, 0, SlotActionType.PICKUP_ALL, mc.player);
                    });
                    return NONE;
                }
                case getType:
                    return getType(container);
                case getTotalSlots: { //as suggested by swadicalrag
                    return valueOf(container.getScreenHandler().slots.size());
                }
                case getMap: {
                    return getMapping().get(getType(container));
                }
                default:
                    break;
            }
            return NONE;
        }

    }

    public LuaValue getType(HandledScreen<?> container) {
        if (container instanceof InventoryScreen) {
            return valueOf("inventory");
        }
        if (container instanceof EnchantmentScreen) {
            return valueOf("enchantment table");
        }
        if (container instanceof MerchantScreen) {
            return valueOf("villager");
        }
        if (container instanceof AnvilScreen) {
            return valueOf("anvil");
        }
        if (container instanceof BeaconScreen) {
            return valueOf("beacon");
        }
        if (container instanceof BrewingStandScreen) {
            return valueOf("brewing stand");
        }
        if (container instanceof GenericContainerScreen) {
            if (container.getScreenHandler().slots.size() == 90) {
                return valueOf("double chest");
            }
            return valueOf("chest");
        }
        if (container instanceof CraftingScreen) {
            return valueOf("crafting table");
        }
        if (container instanceof Generic3x3ContainerScreen) {
            return valueOf("dispenser");
        }
        if (container instanceof FurnaceScreen) {
            return valueOf("furnace");
        }
        if (container instanceof HopperScreen) {
            return valueOf("hopper");
        }
        if (container instanceof HorseScreen) {
            return valueOf("horse inventory");
        }
        if (container instanceof ShulkerBoxScreen) {
            return valueOf("shulker box");
        }
        return valueOf(container.getClass().toString());
    }

    private static enum OpCode {
        close,
        closeAndDrop,
        swap,
        split,
        getHeld,
        getSlot,
        quick,
        grabAll,
        getType,
        getMap,
        getTotalSlots,
        click,
        dragClick;

        public String[] getDocLocation() {
            return new String[]{"openInventory()", name()};
        }
    }

    private LuaValue getMapping() {
        if (mapping.istable()) {
            return mapping;
        }
        mapping = new LuaTable();

        LuaTable inv = new LuaTable();
        mapping.set("inventory", inv);

        inv.set("hotbar", quickTable(37, 45));
        inv.set("main", quickTable(10, 36));
        inv.set("boots", 9);
        inv.set("leggings", 8);
        inv.set("chestplate", 7);
        inv.set("helmet", 6);
        inv.set("offHand", 46);
        inv.set("craftingIn", quickTable(2, 5));
        inv.set("craftingOut", 1);

        LuaTable beacon = new LuaTable();
        mapping.set("beacon", beacon);
        beacon.set("slot", 1);
        beacon.set("main", quickTable(2, 28));
        beacon.set("hotbar", quickTable(29, 37));

        LuaTable brew = new LuaTable();
        mapping.set("brewing stand", brew);

        brew.set("fuel", 5);
        brew.set("input", 4);
        brew.set("output", quickTable(1, 3));
        brew.set("main", quickTable(6, 32));
        brew.set("hotbar", quickTable(33, 41));

        LuaTable chest = new LuaTable();
        mapping.set("chest", chest);
        mapping.set("shulker box", chest);

        chest.set("contents", quickTable(1, 27));
        chest.set("main", quickTable(28, 54));
        chest.set("hotbar", quickTable(55, 63));

        LuaTable doubleChest = new LuaTable();
        mapping.set("double chest", doubleChest);
        doubleChest.set("contents", quickTable(1, 54));
        doubleChest.set("main", quickTable(55, 81));
        doubleChest.set("hotbar", quickTable(82, 90));

        LuaTable craft = new LuaTable();
        mapping.set("crafting table", craft);
        craft.set("craftingIn", quickTable(2, 10));
        craft.set("craftOut", 1);
        craft.set("main", quickTable(11, 37));
        craft.set("hotbar", quickTable(38, 46));

        LuaTable disp = new LuaTable();
        mapping.set("dispenser", disp);
        disp.set("contents", quickTable(1, 9));
        disp.set("main", quickTable(10, 36));
        disp.set("hotbar", quickTable(37, 45));

        LuaTable furn = new LuaTable();
        mapping.set("furnace", furn);
        furn.set("input", 1);
        furn.set("fuel", 2);
        furn.set("output", 3);
        furn.set("main", quickTable(4, 30));
        furn.set("hotbar", quickTable(31, 39));

        LuaTable hopper = new LuaTable();
        mapping.set("hopper", hopper);
        hopper.set("contents", quickTable(1, 5));
        hopper.set("main", quickTable(6, 32));
        hopper.set("hotbar", quickTable(33, 41));

        LuaTable anv = new LuaTable();
        mapping.set("anvil", anv);
        anv.set("item", 1);
        anv.set("material", 2);
        anv.set("input", quickTable(1, 2));
        anv.set("output", 3);
        anv.set("main", quickTable(4, 30));
        anv.set("hotbar", quickTable(31, 39));

        LuaTable enc = new LuaTable();
        mapping.set("enchantment table", enc);
        enc.set("tool", 1);
        enc.set("lapis", 2);
        enc.set("main", quickTable(3, 29));
        enc.set("hotbar", quickTable(30, 38));

        LuaTable vil = new LuaTable();
        mapping.set("villager", vil);
        vil.set("input", quickTable(1, 2));
        vil.set("output", 3);
        vil.set("main", quickTable(4, 30));
        vil.set("hotbar", quickTable(31, 39));

        return mapping;
    }

    private LuaValue quickTable(int i, int j) {
        LuaTable t = new LuaTable();
        for (int n = 1; i <= j; i++, n++) {
            t.set(n, LuaValue.valueOf(i));
        }
        return t;
    }

    //	private static class CloseInventory extends ZeroArgFunction{
    //		@Override
    //		public LuaValue call() {
    //			MinecraftClient.getInstance().player.closeScreenAndDropStack();
    //			return LuaValue.NONE;
    //		}
    //	}
    //	private static class SwapStack extends TwoArgFunction{
    //		@Override
    //		public LuaValue call(LuaValue arg1, LuaValue arg2) {
    //			InventoryPlayer inv = MinecraftClient.getInstance().player.inventory;
    //			ContainerPlayer  invContainer =(ContainerPlayer) MinecraftClient.getInstance().player.inventoryContainer;
    //			NetHandlerPlayClient nhpc = MinecraftClient.getInstance().getConnection();
    //
    //			int sourceSlot = arg1.checkint()-1;
    //			int sinkSlot = arg2.checkint()-1;
    //
    //			int sourceIndex = getSlotNum(sourceSlot);
    //			int sinkIndex = getSlotNum(sinkSlot);
    //
    //			System.out.printf("Source: %d -> %d\n",sourceSlot, sourceIndex);
    //			System.out.printf("Sink: %d -> %d\n",sinkSlot, sinkIndex);
    //			if(sourceIndex==-1 || sinkIndex==-1){return LuaValue.NONE;}
    //
    //
    //			doClick(sourceIndex, 0); //should be left
    //			doClick(sinkIndex, 0);
    //			doClick(sourceIndex,  0);
    //			//invContainer.inventorySlots.get(sourceIndex).putStack(stackSink);
    //			//invContainer.inventorySlots.get(sinkIndex).putStack(stackSource);
    //
    //			//BOOKMARK slots outside hotbar cause click on first slot for an unknown reason
    //			//pls use debug mode later
    //			return LuaValue.NONE;
    //		}
    //	}
    //	private static class SplitStack extends ThreeArgFunction{
    //		@Override
    //		public LuaValue call(LuaValue arg0, LuaValue arg1, LuaValue arg2) {
    //			InventoryPlayer inv = MinecraftClient.getInstance().player.inventory;
    //			ContainerPlayer  invContainer =(ContainerPlayer) MinecraftClient.getInstance().player.inventoryContainer;
    //			NetHandlerPlayClient nhpc = MinecraftClient.getInstance().getConnection();
    //			ItemStack sourceStack = inv.getStackInSlot(arg1.checkint()-1);
    //			ItemStack sinkStack = inv.getStackInSlot(arg1.checkint()-1);
    //			if(!sourceStack.isEmpty() &&
    //					!Utils.itemsEqual(sourceStack, sinkStack))
    //				return LuaValue.FALSE;
    //			int amount = arg2.optint((int) (inv.getStackInSlot(arg0.checkint()-1).getCount()/2f+.5));
    //			amount = Math.min(inv.getStackInSlot(arg0.checkint()-1).getCount(), amount);
    //			int sourceSlot = arg0.checkint()-1;
    //			int sinkSlot = arg1.checkint()-1;
    //
    //			int sourceIndex = getSlotNum(sourceSlot);
    //			int sinkIndex = getSlotNum(sinkSlot);
    //
    //			System.out.printf("Source: %d -> %d\n",sourceSlot, sourceIndex);
    //			System.out.printf("Sink: %d -> %d\n",sinkSlot, sinkIndex);
    //			if(sourceIndex==-1 || sinkIndex==-1){return LuaValue.NONE;}
    //			//System.out.println("Debugging!");
    //
    //			doClick(sourceIndex, 0); //should be left
    //			for(int i = 0; i<amount; i++)
    //				doClick(sinkIndex, 1);
    //			doClick(sourceIndex, 0); //should be left
    //			//doClick(sourceIndex,  0);
    //			//invContainer.inventorySlots.get(sourceIndex).putStack(stackSink);
    //			//invContainer.inventorySlots.get(sinkIndex).putStack(stackSource);
    //
    //			//BOOKMARK slots outside hotbar cause click on first slot for an unknown reason
    //			//pls use debug mode later
    //			return LuaValue.NONE;
    //		}
    //	}
    //	private static void doClick(int index, int buttonNum){
    //		EntityPlayer player = MinecraftClient.getInstance().player;
    //		SlotActionType SlotActionType = SlotActionType.PICKUP;
    //		hndlMsClick(index, buttonNum, SlotActionType);
    //		try {
    //			Thread.sleep(50);
    //		} catch (InterruptedException e) {
    //			e.printStackTrace();
    //		}
    //	}
    //	private static void hndlMsClick(int indx, int dragType, SlotActionType cType){
    //		EntityPlayer player = MinecraftClient.getInstance().player;
    //
    //		player.inventoryContainer.slotClick(indx, dragType, cType, player);
    //
    //		NetHandlerPlayClient nhpc = MinecraftClient.getInstance().getConnection();
    //		nhpc.sendPacket(new CPacketClickWindow(player.inventoryContainer.windowId,
    //				indx, //ID of slot clicked TODO check me, idk if ID is slot ID or index
    //				dragType,//button num used
    //				cType,
    //				player.inventory.getItemStack(),
    //				player.inventoryContainer.getNextTransactionID(player.inventory)));
    //	}
    //
    private SlotActionType getCType() {
        if (MinecraftClient.getInstance().player.currentScreenHandler.getCursorStack().isEmpty()) {
            return SlotActionType.PICKUP;
        }
        return SlotActionType.SWAP;
    }
    //	private void doClick(int index, SlotActionType type, int buttonNum){
    //		System.out.println("Clicked on index "+index);
    //		InventoryPlayer inv = MinecraftClient.getInstance().player.inventory;
    //
    //		ContainerPlayer  invContainer =(ContainerPlayer) MinecraftClient.getInstance().player.inventoryContainer;
    //		NetHandlerPlayClient nhpc = MinecraftClient.getInstance().getConnection();
    //
    //		invContainer.slotClick(index, 0, type, MinecraftClient.getInstance().player);
    //		nhpc.sendPacket(new CPacketClickWindow(invContainer.windowId,
    //				index, //ID of slot clicked TODO check me, idk if ID is slot ID or index
    //				buttonNum,//button num used
    //				type,
    //				inv.getStackInSlot(index),
    //				invContainer.getNextTransactionID(inv)));
    //
    //		//BOOKMARK investigating slotClick() method
    //	}

    private static int getSlotNum(int invIndx) {
        PlayerInventory inv = MinecraftClient.getInstance().player.getInventory();
        ScreenHandler handler = MinecraftClient.getInstance().player.currentScreenHandler;

        if (inRange(invIndx, 0, 8)) {
            return invIndx + 36;
        } else if (inRange(invIndx, 9, 35)) {
            return invIndx;
        } else if (inRange(invIndx, 36, 39)) {
            return invIndx - 31;
        } else if (invIndx == 40) {
            return 45;
        } else if (inRange(invIndx, 41, 44)) {
            return invIndx - 40;
        } else if (invIndx == 45) {
            return 0;
        }
        for (int i = 0; i < handler.slots.size(); i++) {
            if (invIndx == handler.slots.get(i).getIndex()) {
                System.out.println("INDEX " + invIndx + " = " + i);
                return i;
            }
        }
        return -1;
    }

    public static boolean inRange(int x, int a, int b) {
        return a <= x && x <= b;
    }

}
