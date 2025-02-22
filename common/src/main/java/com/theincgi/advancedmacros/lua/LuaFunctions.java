package com.theincgi.advancedmacros.lua;

import com.theincgi.advancedmacros.misc.CallableTable;
import com.theincgi.advancedmacros.misc.Pair;
import com.theincgi.advancedmacros.misc.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.luaj.vm2_v3_0_1.LuaError;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.Varargs;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;
import org.luaj.vm2_v3_0_1.lib.VarArgFunction;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;

public class LuaFunctions {

    public static Dictionary<Character, String> chatColors = new Hashtable<>();

    static {
        //static init for chatColors
        chatColors.put('0', "black");
        chatColors.put('1', "dark_blue");
        chatColors.put('2', "dark_green");
        chatColors.put('3', "dark_aqua");
        chatColors.put('4', "dark_red");
        chatColors.put('5', "dark_purple");
        chatColors.put('6', "gold");
        chatColors.put('7', "gray");
        chatColors.put('8', "dark_gray");
        chatColors.put('9', "blue");
        chatColors.put('a', "green");
        chatColors.put('b', "aqua");
        chatColors.put('c', "red");
        chatColors.put('d', "light_purple");
        chatColors.put('e', "yellow");
        chatColors.put('f', "white");
    }

    public static class Sleep extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue time) {
            try {
                Thread.sleep(time.checklong());
            } catch (InterruptedException e) {
            }
            return LuaValue.NONE;
        }

    }

    public static class Say extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            MinecraftClient.getInstance().getNetworkHandler().sendChatMessage(arg.tojstring());
            MinecraftClient.getInstance().inGameHud.getChatHud().addToMessageHistory(arg.tojstring());
            return LuaValue.NONE;
        }

    }

    public static class Log extends VarArgFunction {

        final char OBFUSCATE = 'O',   //&_ escape char definitions
                ITALICS = 'I',
                UNDERLINE = 'U',
                BOLD = 'B',
                STRIKETHRU = 'S';
        boolean obfuscate = false,   //text modes
                italics = false,
                underline = false,
                bold = false,
                strikethru = false;

        final String textHeader = "{\"text\":\"";                //looks like ---> {"text":"
        final String singalQuote = "\"";                        //looks like ---> "    //used to close segements in an easier way to read then "\""
        final String BOLD_SEGMENT = ",\"bold\":true";            //looks like ---> ,"bold":true
        final String ITALIC_SEGMENT = ",\"italic\":true";        //looks like ---> ,"italic":true
        final String UNDERLINE_SEGMENT = ",\"underlined\":true";    //looks like ---> ,"underlined":true
        final String STRIKETHRU_SEGMENT = ",\"strikethrough\":true";    //looks like ---> ,"strkethrough":true
        final String OBFUSCATE_SEGMENT = ",\"obfuscated\":true";    //looks like ---> ,"obfuscated":true
        final String colorHeader = ",\"color\":\"";                //looks like ---> ,"color":"        //closes text section and starts color
        String fragment;
        String activeColor = "white";
        String parsed;                                        //looks like ---> [""

        private void appendSegment() {
            parsed += "," + textHeader + fragment + singalQuote;       //text
            if (bold) {
                parsed += BOLD_SEGMENT;
            }   //bold
            if (italics) {
                parsed += ITALIC_SEGMENT;
            }   //italics
            if (underline) {
                parsed += UNDERLINE_SEGMENT;
            }   //underline
            if (strikethru) {
                parsed += STRIKETHRU_SEGMENT;
            }   //strikethru
            if (obfuscate) {
                parsed += OBFUSCATE_SEGMENT;
            }   //obfuscate
            parsed += colorHeader + activeColor + singalQuote + "}";   //color
            fragment = "";
        }

        private void resetFormat() {
            //reset values
            bold = false;
            italics = false;
            underline = false;
            strikethru = false;
            obfuscate = false;
        }

        @Override
        public Varargs invoke(Varargs arg0) {
            try {
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(formatString(arg0));
            } catch (LuaError err) {
                throw err;
            } catch (Throwable e) {
                //prob tried to log without chat
                e.printStackTrace();
            }
            return LuaValue.NONE;
        }

        public synchronized Text formatString(Varargs arg0) { //TODO make it so hovering a callable table shows the tooltip
            String toParse;
            MutableText out = null;
            for (int i = 1; arg0.narg() > 0; i++) {
                //				if(i!=1) {
                //					out.appendText(" ");
                //				}
                LuaValue arg = arg0.arg1();
                Pair<MutableText, Varargs> pair;

                if (arg.istable()) {
                    toParse = formatTableForLog(arg.checktable());
                } else {
                    toParse = arg.tojstring();
                }

                pair = Utils.toTextComponent(toParse, arg0.subargs(2), true);
                arg0 = pair.b;
                if (out == null) {
                    out = pair.a;
                } else {
                    out.append(pair.a);
                }

            }
            return out;

        }

    }

    public static String formatTableForLog(LuaTable t) {
        LinkedList<LuaTable> l = new LinkedList<>();
        String f = "&e" + t.tojstring() + " &f{\n";
        f += formatTableForLog(t, l, 2);
        f += "&f}";
        return f;
    }

    public static boolean tableContainsKeys(LuaTable table) {
        return !table.next(LuaValue.NIL).arg1().isnil();
    }

    private static String formatTableForLog(LuaTable t, LinkedList<LuaTable> antiR, int indent) { //TODO optamize with StringBuilder
        StringBuilder s = new StringBuilder();
        antiR.add(t);
        for (LuaValue k : t.keys()) {
            LuaValue v = t.get(k);
            String keyText = escAND(k.tojstring());
            if (k.type() == LuaValue.TSTRING) {
                keyText = "\"" + keyText + "\"";
            }
            if (v.istable()) {
                if (antiR.indexOf(v) >= 0) {
                    //repeat subTable
                    s.append(rep(" ", indent)).append("&f[&c").append(keyText).append("&f] = <&4RECURSIVE&f> &e").append(escAND(v.tojstring())).append("&f{&4...&f}\n");
                } else {
                    LuaTable vTab = v.checktable();
                    if (vTab.getmetatable() != null && vTab.getmetatable().istable() && vTab.getmetatable().get(CallableTable.LUA_FUNCTION_KEY).optboolean(false)) {
                        s.append(rep(" ", indent)).append("&f[&c").append(keyText).append("&f] = &b").append(escAND((v.tojstring())));
                        if (tableContainsKeys(vTab)) {
                            s.append(" &f{\n");
                            s.append(formatTableForLog(vTab, antiR, indent + 2));
                            s.append(rep(" ", indent)).append("&f}\n");
                        } else {
                            s.append("&f\n");
                        }
                    } else {
                        //antiR.add(t);
                        s.append(rep(" ", indent)).append("&f[&c").append(keyText).append("&f] = &e").append(escAND(v.tojstring())); //TODO remove \n if no keys of any type
                        if (tableContainsKeys(vTab)) {
                            s.append(" &f{\n");
                            s.append(formatTableForLog(vTab, antiR, indent + 2));
                            s.append(rep(" ", indent)).append("&f}\n");
                        } else {
                            s.append(" &f{}\n");
                        }
                    }

                }
            } else {
                s.append(rep(" ", indent)).append("&f[&c").append(keyText).append("&f] = &b");
                if (v.typename().equals("string")) {
                    s.append("&f\"&b").append(escAND(v.tojstring())).append("&f\""); //added &b to fix color formating in these
                    //added .replaceAll so that way color formating doesnt trigger inside the table print
                } else {
                    s.append(v.isuserdata() ? "&d" + escAND(v.tojstring()) : escAND(v.tojstring()));
                }
                s.append("\n");
            }
        }
        if (t.getmetatable() != null && t.getmetatable().istable()) {
            antiR.add(t.getmetatable().checktable());
            s.append(rep(" ", indent)).append("&f[&dmetatable&f] = &d").append(t.getmetatable().tojstring()).append(" &f{\n");
            s.append(formatTableForLog(t.getmetatable().checktable(), antiR, indent + 4));
            s.append(rep(" ", indent)).append("&f}\n");
        }
        return s.toString();
    }

    private static String escAND(String s) {
        return s.replace("&", "&&");
    }

    public static String rep(String s, int t) {
        return String.valueOf(s).repeat(Math.max(0, t));
    }

    public static class Debug extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg0) {
            System.out.println("LUA DEBUG: " + arg0.tojstring());
            return LuaValue.NONE;
        }

    }

}
