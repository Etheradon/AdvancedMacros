package com.theincgi.advancedmacros.hud.hud3D;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import com.theincgi.advancedmacros.AdvancedMacros;
import com.theincgi.advancedmacros.misc.CallableTable;
import com.theincgi.advancedmacros.misc.Utils;
import org.luaj.vm2_v3_0_1.LuaError;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.Varargs;
import org.luaj.vm2_v3_0_1.lib.VarArgFunction;

public class Hud3D extends LuaTable {
    public Hud3D() {
        for (Hud3DOpCode op : Hud3DOpCode.values()) {
            this.set(op.toString(), new CallableTable(op.getDocLocation(), new DoOp(op)));
        }

    }

    private class DoOp extends VarArgFunction {
        Hud3DOpCode op;

        public DoOp(Hud3DOpCode op) {
            super();
            this.op = op;
        }

        @Override
        public Varargs invoke(Varargs args) {
            ClientPlayerEntity p = MinecraftClient.getInstance().player;
            switch (op) {
                case clearAll: {
                    AdvancedMacros.EVENT_HANDLER.clearWorldHud();
                    return LuaValue.NONE;
                }
                case newBlock: {
                    HoloBlock hb = new HoloBlock();
                    hb.setPos((float) args.optdouble(1, (int) Math.floor(p.getX())),
                            (float) args.optdouble(2, (int) Math.floor(p.getY())),
                            (float) args.optdouble(3, (int) Math.floor(p.getZ())));
                    hb.setTexture(Utils.parseTexture(args.arg(4)));
                    return hb.getControls();
                }
                case newText: {
                    HudText text = new HudText();
                    text.setText(args.arg1().optjstring(""));
                    text.setPos((float) args.optdouble(2, (int) Math.floor(p.getX())),
                            (float) args.optdouble(3, (int) Math.floor(p.getY())),
                            (float) args.optdouble(4, (int) Math.floor(p.getZ())));
                    return text.getControls();
                }
                case newPane: {
                    Hud3DPane pane = new Hud3DPane(args.arg(1).checkjstring());
                    pane.setPos((float) args.optdouble(2, (int) Math.floor(p.getX())),
                            (float) args.optdouble(3, (int) Math.floor(p.getY())),
                            (float) args.optdouble(4, (int) Math.floor(p.getZ())));
                    if (!args.arg(5).isnil()) {
                        pane.changeTexture(args.arg(5));
                    }
                    return pane.getControls();
                }
                case newMesh: {
                    Hud3DElement element = new Hud3DElement();
                    element.setPos((float) args.optdouble(1, (int) Math.floor(p.getX())),
                            (float) args.optdouble(2, (int) Math.floor(p.getY())),
                            (float) args.optdouble(3, (int) Math.floor(p.getZ())));
                    return element.getControls();
                }
                default:
                    throw new LuaError("Unimplemented function " + op);
            }
        }
    }

    private static enum Hud3DOpCode {
        newBlock,
        newText,
        newPane,
        newMesh,
        clearAll;

        public String[] getDocLocation() {
            String[] loc = new String[2];
            loc[0] = "hud3D";
            switch (this) {
                case clearAll:
                case newBlock:
                case newText:
                case newPane:
                    loc[1] = this.toString();
                    return loc;
                default:
                    return null;
            }
        }
    }
}
