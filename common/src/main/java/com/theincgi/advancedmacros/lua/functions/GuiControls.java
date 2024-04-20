package com.theincgi.advancedmacros.lua.functions;

import com.theincgi.advancedmacros.access.IAnvilScreen;
import com.theincgi.advancedmacros.access.IBookEditScreen;
import com.theincgi.advancedmacros.access.ICommandBlockScreen;
import com.theincgi.advancedmacros.access.ISignBlockEntity;
import com.theincgi.advancedmacros.access.ISignEditScreen;
import com.theincgi.advancedmacros.misc.CallableTable;
import com.theincgi.advancedmacros.misc.Utils;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.CommandBlockExecutor;
import org.luaj.vm2_v3_0_1.LuaError;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.Varargs;
import org.luaj.vm2_v3_0_1.lib.VarArgFunction;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

import java.lang.reflect.InvocationTargetException;

public class GuiControls {

    public static LuaValue load(Screen gCon) {
        LuaTable controls = new LuaTable();
        if (gCon instanceof AnvilScreen gr) {
            for (RepairOp r : RepairOp.values()) {
                controls.set(r.name(), new CallableTable(r.getDocLocation(), new DoRepair(r, gr)));
            }
        } else if (gCon instanceof MerchantScreen gm) {
            for (TradeOp r : TradeOp.values()) {
                controls.set(r.name(), new CallableTable(r.getDocLocation(), new DoTrade(r, gm)));
            }

        } else if (gCon instanceof EnchantmentScreen ge) {
            for (EnchantOp r : EnchantOp.values()) {
                controls.set(r.name(), new CallableTable(r.getDocLocation(), new DoEnchant(r, ge)));
            }
        } else if (gCon instanceof SignEditScreen es) {
            for (SignOp r : SignOp.values()) {
                controls.set(r.name(), new CallableTable(r.getDocLocation(), new DoSign(r, es)));
            }
        } else if (gCon instanceof BookEditScreen bk) {
            for (BookOp r : BookOp.values()) {
                controls.set(r.name(), new CallableTable(r.getDocLocation(), new DoBook(r, bk)));
            }
            //TODO read book screen thing
        } else if (gCon instanceof CommandBlockScreen cb) {
            for (CommandOp r : CommandOp.values()) {
                controls.set(r.name(), new CallableTable(r.getDocLocation(), new DoCommand(r, cb)));
            }
        } else if (gCon instanceof GenericContainerScreen gc) {
            for (ChestOp op : ChestOp.values()) {
                controls.set(op.name(), new CallableTable(op.getDocLocation(), new DoChestOp(op, gc)));
            }
        }//else if(gCon instanceof ReadBookScreen) {
        //			ReadBookScreen rbs = (ReadBookScreen) gCon;
        //			for(ReadBookOp op : ReadBookOp.values()) {
        //				controls.set(op.name(),  new CallableTable(op.getDocLocation(), new DoReadBook(op, rbs)));
        //			}
        //		}
        Screen whenOpened = gCon;
        controls.set("isOpen", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return valueOf(whenOpened == MinecraftClient.getInstance().currentScreen);
            }
        });
        controls.set("close", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                MinecraftClient.getInstance().setScreen(null);
                return NONE;
            }
        });

        return controls;
    }

    private static class DoRepair extends VarArgFunction {

        RepairOp op;
        AnvilScreen gr;

        public DoRepair(RepairOp op, AnvilScreen gr) {
            super();
            this.op = op;
            this.gr = gr;
        }

        @Override
        public Varargs invoke(Varargs args) {
            switch (op) {
                case getCost: {
                    AnvilScreenHandler cr = gr.getScreenHandler();// anvil.get(gr);
                    return valueOf(cr.getLevelCost());
                }
                case getName:
                    return valueOf(((IAnvilScreen) gr).am_getNameField().getText());
                case setName:
                    ((IAnvilScreen) gr).am_getNameField().setText(args.arg1().checkjstring());
                    ((IAnvilScreen) gr).am_rename(args.arg1().checkjstring());
                default:
                    throw new LuaError("Unimplemented function '" + op.name() + "'");
            }
        }

    }

    private static class DoTrade extends VarArgFunction {

        TradeOp op;
        MerchantScreen gm;

        public DoTrade(TradeOp op, MerchantScreen gm) {
            super();
            this.op = op;
            this.gm = gm;
        }

        @Override
        public Varargs invoke(Varargs args) {
            switch (op) {
                case getTrades: {
                    LuaTable trades = new LuaTable();
                    TradeOfferList offers = gm.getScreenHandler().getRecipes();
                    for (int i = 0; i < offers.size(); i++) {
                        TradeOffer mr = offers.get(i);
                        LuaTable t = new LuaTable();
                        LuaTable inputs = new LuaTable();
                        t.set("input", inputs);
                        inputs.set(1, Utils.itemStackToLuatable(mr.getAdjustedFirstBuyItem())); //first stack
                        inputs.set(2, Utils.itemStackToLuatable(mr.getSecondBuyItem())); //second stack
                        t.set("output", Utils.itemStackToLuatable(mr.getSellItem())); //item sold
                        t.set("uses", mr.getUses()); //uses remaining
                        trades.set(i + 1, t);
                    }
                    return trades;
                }
                case getType:
                    return valueOf(gm.getTitle().getString());
                default:
                    throw new LuaError("Unimplemented function '" + op.name() + "'");
            }
        }

    }

    private static class DoEnchant extends VarArgFunction {

        EnchantOp op;
        EnchantmentScreen ge;

        public DoEnchant(EnchantOp op, EnchantmentScreen ge) {
            super();
            this.op = op;
            this.ge = ge;
        }

        @Override
        public Varargs invoke(Varargs args) {
            EnchantmentScreenHandler ce = ge.getScreenHandler();
            switch (op) {
                case getOptions: {
                    LuaTable out = new LuaTable();
                    for (int i = 1; i <= 3; i++) {
                        LuaTable o = new LuaTable();
                        Enchantment e = Enchantment.byRawId(ce.enchantmentId[i - 1]);
                        if (e == null) {
                            continue;
                        }
                        o.set("hint", e.getName(ce.enchantmentPower[i - 1]).getString());
                        o.set("lvl", ce.enchantmentPower[i - 1]);
                        out.set(i, o);
                    }
                    return out;
                }
                case pickOption:
                    MinecraftClient mc = MinecraftClient.getInstance();
                    int arg = args.checkint(1);
                    if (arg < 1 || arg > 3) {
                        throw new LuaError("argument out of range 1-3 (" + arg + ")");
                    }
                    if (ce.onButtonClick(mc.player, arg - 1)) {
                        mc.interactionManager.clickButton(ce.syncId, arg - 1);
                    }
                    return NONE;
                default:
                    throw new LuaError("Unimplemented function '" + op.name() + "'");
            }
        }

    }

    private static class DoSign extends VarArgFunction {

        SignOp op;
        SignEditScreen es;

        public DoSign(SignOp op, SignEditScreen es) {
            super();
            this.op = op;
            this.es = es;
        }

        @Override
        public Varargs invoke(Varargs args) {
            SignBlockEntity ts = ((ISignEditScreen) es).am_getSignBlockEntity();
            Text[] texts = ((ISignBlockEntity) ts).am_getFrontLines();
            // TODO: 1.20.4 add front and back side
            switch (op) {
                case getLines: {
                    LuaTable lines = new LuaTable();
                    for (int i = 0; i < texts.length; i++) {
                        lines.set(i + 1, texts[i].getString());
                    }
                    return lines.unpack();
                }
                case done:
                    //TESTME es.mc is gone! prob ok tho if(es.mc==null) es.mc = MinecraftClient.getInstance();
                    ts.markDirty();
                    MinecraftClient.getInstance().setScreen(null);
                    return NONE;
                case setLine:
                    texts[args.checkint(1) - 1] = Text.literal(args.optjstring(2, ""));
                    return NONE;
                //				case setFormatedLine:
                //					texts[args.checkint(1)] = new TextComponentString(Utils.toMinecraftColorCodes(args.checkjstring(2)));
                //					return NONE; //lost when sent to server
                //case setFormatedLines:
                case setLines: {
                    if (args.arg1().istable()) {
                        LuaTable t = args.checktable(1);
                        for (int i = 1; i <= texts.length; i++) {
                            texts[i - 1] = Text.literal(t.get(i).optjstring(""));
                        }
                    } else {
                        for (int i = 1; i <= texts.length; i++) {
                            texts[i - 1] = Text.literal(args.optjstring(i, ""));
                        }
                    }
                    return NONE;
                }

                default:
                    throw new LuaError("Unimplemented function '" + op.name() + "'");
            }
        }

    }
    //nbt is good enough for now
    //	private static class DoReadBook extends VarArgFunction {
    //		ReadBookOp op;
    //		ReadBookScreen rbs;
    //		public DoReadBook(ReadBookOp op, ReadBookScreen rbs) {
    //			this.op = op;
    //			this.rbs = rbs;
    //		}
    //
    //		@Override
    //		public Varargs invoke() {
    //			switch (op) {
    //			case currentPage:
    //
    //			case getAuthor:
    //			case getText:
    //			case getTitle:
    //			case gotoPage:
    //			case pageCount:
    //			default:
    //				break;
    //			}
    //		}
    //	}

    //TODO readbookscreen
    private static class DoBook extends VarArgFunction {

        BookOp op;
        BookEditScreen book;

        //title
        //sign
        //save
        //setText
        //getText
        //updateButtons
        //addPage
        //currentPage
        //numPages
        //isMod aka is dirty

        //setPages
        //getPages

        public DoBook(BookOp op, BookEditScreen book) {
            super();
            this.op = op;
            this.book = book;
        }

        private IBookEditScreen getInterface() {
            return (IBookEditScreen) book;
        }

        private void markDirty() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
            getInterface().am_markDirty();
            getInterface().am_updateButtons();
        }

        @Override
        public Varargs invoke(Varargs args) {
            try { //WrittenBookItem; ClientPlayerEntity
                switch (op) {
                    case sign:
                        String newTitle = args.checkjstring(1);
                        newTitle = newTitle.substring(0, Math.min(newTitle.length(), 16));
                        getInterface().am_setTitle(newTitle);
                        markDirty();
                        getInterface().am_finalizeBook(true);
                        MinecraftClient.getInstance().setScreen(null);
                        break;
                    case save:
                        getInterface().am_finalizeBook(false);
                        break;
                    case isSigned:
                        return FALSE;
                    case getText:
                        return LuaValue.valueOf(getInterface().am_getPageContent());
                    case setText:
                        getInterface().am_setPageContent(args.checkjstring(1));
                        markDirty();
                        return NONE;
                    case getPages: {
                        int p = getInterface().am_getCurrentPage();
                        LuaTable out = new LuaTable();
                        int pages = getInterface().am_getPageCount();
                        for (int i = 0; i < pages; i++) {
                            getInterface().am_setCurrentPage(i);
                            out.set(i + 1, getInterface().am_getPageContent());
                        }
                        getInterface().am_setCurrentPage(p);
                        return out;
                    }
                    case setPages: {
                        int p = getInterface().am_getCurrentPage();
                        LuaTable in = args.checktable(1);
                        getInterface().am_setCurrentPage(0);
                        for (int i = 0; i < in.length(); i++) {
                            if (i > 0) {
                                int cP = getInterface().am_getCurrentPage();
                                int tP = getInterface().am_getPageCount();
                                if (cP < tP - 1) {
                                    getInterface().am_setCurrentPage(cP + 1);
                                } else {
                                    getInterface().am_appendNewPage();
                                    cP = getInterface().am_getCurrentPage();
                                    tP = getInterface().am_getPageCount();
                                    if (cP < tP - 1) {
                                        getInterface().am_setCurrentPage(cP + 1);
                                    } else {
                                        return FALSE;
                                    }
                                }
                            }
                            getInterface().am_setPageContent(in.get(i + 1).checkjstring());
                        }
                        getInterface().am_setCurrentPage(p);
                        markDirty();
                        return NONE;
                    }
                    case addPage: {
                        int old = getInterface().am_getPageCount();
                        getInterface().am_appendNewPage();
                        return valueOf(getInterface().am_getPageCount() != old);
                    }
                    case nextPage: {
                        int cP = getInterface().am_getCurrentPage();
                        int tP = getInterface().am_getPageCount();
                        if (cP < tP - 1) {
                            getInterface().am_setCurrentPage(cP + 1);
                        } else {
                            getInterface().am_appendNewPage();
                            cP = getInterface().am_getCurrentPage();
                            tP = getInterface().am_getPageCount();
                            if (cP < tP - 1) {
                                getInterface().am_setCurrentPage(cP + 1);
                            } else {
                                return FALSE;
                            }
                        }
                        return TRUE;
                    }
                    case prevPage: {
                        int cP = getInterface().am_getCurrentPage();
                        int tP = getInterface().am_getPageCount();
                        if (cP > 0) {
                            getInterface().am_setCurrentPage(cP - 1);
                        } else {
                            return FALSE;
                        }
                        return TRUE;
                    }
                    case currentPage: {
                        return valueOf(getInterface().am_getCurrentPage() + 1);
                    }
                    case gotoPage: {
                        getInterface().am_setCurrentPage(Math.min(Math.max(0, args.checkint(1) - 1), getInterface().am_getPageCount()));
                        return NONE;
                    }
                    case pageCount: {
                        return valueOf(getInterface().am_getPageCount());
                    }
                    default:
                        throw new LuaError("Unimplemented function '" + op.name() + "'");
                }
            } catch (IllegalAccessException | IllegalArgumentException |
                     InvocationTargetException e) {
                throw new LuaError(e);
            }
            return NONE;
        }

    }

    private static class DoCommand extends VarArgFunction {

        CommandOp op;
        CommandBlockScreen cb;
        //updateGui is public
        //		Method updateMode = ObfuscationReflectionHelper.findMethod(CommandBlockScreen.class, "updateMode", "func_184073_g");
        //		//Method updateGui = ReflectionHelper.findMethod(GuiCommandBlock.class, "updateGui", "a");
        //		Method updateTrack=ObfuscationReflectionHelper.findMethod(CommandBlockScreen.class, "updateCmdOutput", "func_175388_a");
        //		Method updateConditional=
        //				ObfuscationReflectionHelper.findMethod(CommandBlockScreen.class, "updateConditional", "func_184077_i");
        //		Method updateAutomatic = ObfuscationReflectionHelper.findMethod(CommandBlockScreen.class, "updateAutoExec", "func_184076_j");
        TextFieldWidget text;

        ICommandBlockScreen getInterface() {
            return (ICommandBlockScreen) cb;
        }

        public DoCommand(CommandOp op, CommandBlockScreen cb) {
            super();
            this.op = op;
            this.cb = cb;
            //			cb.init(); //TESTME command blocks
            //			cb.updateCommandBlock();
            //			try {
            //				long timeout = System.currentTimeMillis()+400;
            //				while(text==null) {
            //					text = (TextFieldWidget) tf.get(cb);
            //					if(text!=null) break;
            //					try {
            //						Thread.sleep(20);
            //					} catch (InterruptedException e) {break;}
            //					if(timeout < System.currentTimeMillis())
            //						break;
            //				}
            //			} catch (IllegalArgumentException | IllegalAccessException e) {
            //				throw new LuaError( e );
            //			}
            //			cb.updateCommandBlock();
        }

        @Override
        public Varargs invoke(Varargs args) {
            try {
                //TileEntityCommandBlock block = (TileEntityCommandBlock) tecb.get(cb);
                text = getInterface().am_getCommandField();
                switch (op) {
                    case isConditional:
                        return valueOf(getInterface().am_getCommandBlockEntity().isConditionalCommandBlock());
                    case getMode:
                        switch (getInterface().am_getCommandBlockType()) {
                            case AUTO:
                                return valueOf("repeat");
                            case REDSTONE:
                                return valueOf("impulse");
                            case SEQUENCE:
                                return valueOf("chain");
                        }
                        throw new LuaError("Unknown mode...");
                    case getText:
                        return valueOf(text.getText());
                    case setMode:
                        switch (args.checkjstring(1)) {
                            case "repeat":
                                getInterface().am_setCommandBlockType(CommandBlockBlockEntity.Type.AUTO);
                                break;
                            case "impulse":
                                getInterface().am_setCommandBlockType(CommandBlockBlockEntity.Type.REDSTONE);
                                break;
                            case "chain":
                                getInterface().am_setCommandBlockType(CommandBlockBlockEntity.Type.SEQUENCE);
                                break;
                        }
                        cb.updateCommandBlock();
                        return NONE;
                    case setText:
                        text.setText(args.checkjstring(1));
                        return NONE;
                    case done:
                        done();
                        return NONE;
                    case isNeedsRedstone:
                        return valueOf(!getInterface().am_getCommandBlockEntity().isAuto());
                    case isTrackOutput:
                        return valueOf(getInterface().am_getCommandBlockEntity().getCommandExecutor().isTrackingOutput());
                    case setChain:
                        getInterface().am_setCommandBlockType(CommandBlockBlockEntity.Type.SEQUENCE);
                        cb.updateCommandBlock();
                        return NONE;
                    case setConditional:
                        getInterface().am_setConditional(args.optboolean(1, true));
                        cb.updateCommandBlock();
                        return NONE;
                    case setImpulse:
                        getInterface().am_setCommandBlockType(CommandBlockBlockEntity.Type.REDSTONE);
                        cb.updateCommandBlock();
                        break;
                    case setNeedsRedstone:
                        getInterface().am_setAuto(!args.optboolean(1, true));
                        cb.updateCommandBlock();
                        return NONE;
                    case setRepeat:
                        getInterface().am_setCommandBlockType(CommandBlockBlockEntity.Type.AUTO);
                        cb.updateCommandBlock();
                        return NONE;
                    case setTrackOutput:
                        getInterface().am_getCommandBlockEntity().getCommandExecutor().setTrackOutput(args.optboolean(1, true));
                        cb.updateCommandBlock();
                        return NONE;
                    case getOutput:
                        return valueOf(getInterface().am_getCommandField().getText());
                    default:
                        throw new LuaError("Unimplemented function '" + op.name() + "'");
                }
            } catch (IllegalArgumentException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new LuaError(e);
            }
            return NONE;
        }

        private void done() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
            CommandBlockBlockEntity block = getInterface().am_getCommandBlockEntity();
            CommandBlockExecutor commandblockbaselogic = block.getCommandExecutor();
            getInterface().am_syncSettingsToServer();
            if (!commandblockbaselogic.shouldTrackOutput()) {
                commandblockbaselogic.setLastOutput(null);
            }

            MinecraftClient.getInstance().setScreen(null);
        }

    }

    private static class DoChestOp extends VarArgFunction {

        GenericContainerScreen gc;
        ChestOp op;

        public DoChestOp(ChestOp op, GenericContainerScreen gc) {
            this.op = op;
            this.gc = gc;
        }

        @Override
        public Varargs invoke(Varargs args) {
            try {
                switch (op) {
                    case getLowerLabel:
                        return valueOf(Utils.fromMinecraftColorCodes(MinecraftClient.getInstance().player.getInventory().getDisplayName().getString()));
                    case getUpperLabel:
                        return valueOf(Utils.fromMinecraftColorCodes(gc.getTitle().getString()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return NONE;
        }

    }

    private static enum RepairOp {
        setName,
        getName,
        getCost;

        public String[] getDocLocation() {
            return new String[]{"guiEvent#anvil", name()};
        }
    }

    private static enum TradeOp {
        getTrades,
        getType;

        public String[] getDocLocation() {
            return new String[]{"guiEvent#villager", name()};
        }
    }

    private static enum EnchantOp {
        pickOption,
        getOptions;

        public String[] getDocLocation() {
            return new String[]{"guiEvent#enchant", name()};
        }
    }

    private static enum SignOp {
        setLine,
        setLines,
        done,
        getLines;

        public String[] getDocLocation() {
            return new String[]{"guiEvent#sign", name()};
        }
    }

    //	private static enum ReadBookOp{
    //		getTitle,
    //		getAuthor,
    //		getText,
    //		gotoPage,
    //		currentPage,
    //		pageCount;
    //		public String[] getDocLocation() {
    //			return new String[] {"guiEvent#readBook", name()};
    //		}
    //	}
    private static enum BookOp {
        setText,
        getText,
        getPages,
        setPages,
        //		setTitle,
        //getAuthor,
        addPage,
        nextPage,
        prevPage,
        save,
        //getTitle,
        sign,
        currentPage,
        pageCount,
        isSigned, gotoPage;

        public String[] getDocLocation() {
            return new String[]{"guiEvent#book", name()};
        }
    }

    private static enum CommandOp {
        getText,
        setText,
        getMode,
        setImpulse,
        setChain,
        setRepeat,
        setConditional,
        getOutput,
        isConditional,
        setNeedsRedstone,
        isNeedsRedstone,
        setTrackOutput,
        isTrackOutput,
        done,
        setMode;

        public String[] getDocLocation() {
            return new String[]{"guiEvent#commandBlock", name()};
        }
    }

    private static enum ChestOp {
        getUpperLabel,
        getLowerLabel;

        public String[] getDocLocation() {
            return new String[]{"guiEvent#chest", name()};
        }
    }

}
