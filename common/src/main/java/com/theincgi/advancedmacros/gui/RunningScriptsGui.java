package com.theincgi.advancedmacros.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import com.theincgi.advancedmacros.gui.elements.Drawable;
import com.theincgi.advancedmacros.gui.elements.GuiButton;
import com.theincgi.advancedmacros.gui.elements.ListManager;
import com.theincgi.advancedmacros.gui.elements.Moveable;
import com.theincgi.advancedmacros.gui.elements.OnClickHandler;
import com.theincgi.advancedmacros.lua.LuaDebug;
import com.theincgi.advancedmacros.lua.LuaDebug.Status;
import com.theincgi.advancedmacros.lua.LuaDebug.StatusListener;
import com.theincgi.advancedmacros.misc.PropertyPalette;
import com.theincgi.advancedmacros.misc.Utils;
import com.theincgi.advancedmacros.misc.Utils.TimeFormat;
import org.luaj.vm2_v3_0_1.LuaValue;

import java.util.concurrent.ConcurrentHashMap;

public class RunningScriptsGui extends Gui {
    ListManager listManager;
    private LuaDebug luaDebug;
    ConcurrentHashMap<Thread, Script> scripts = new ConcurrentHashMap<>(10);

    public RunningScriptsGui(LuaDebug luaDebug) {
        this.luaDebug = luaDebug;
        listManager = new ListManager(5, 17, width - 10, height - 22, /*new WidgetID(700), "colors.runningScripts"*/ new PropertyPalette());
        listManager.setDrawBG(false);
        addInputSubscriber(listManager);
        luaDebug.addStatusListener(new StatusListener() {
            @Override
            public void onStatus(Thread sThread, Status status) {
                switch (status) {
                    case DONE:
                        listManager.remove(scripts.remove(sThread));
                        break;
                    case PAUSED: {
                        Script s = scripts.get(sThread);
                        if (s != null) {
                            s.statusChanged(status);
                        }
                        break;
                    }
                    case RUNNING: {
                        Script s = scripts.get(sThread);
                        if (s == null) {
                            s = new Script(sThread);
                            scripts.put(sThread, s);
                            listManager.add(s);
                        }
                        s.statusChanged(status);
                        break;
                    }
                    case CRASH:
                    case STOPPED: {
                        Script s = scripts.get(sThread);
                        if (s != null) {
                            listManager.remove(scripts.remove(sThread));
                        }
                    }
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public Text getTitle() {
        return Text.literal("Running Scripts");
    }

    @Override
    public void resize(MinecraftClient mc, int width, int height) {
        super.resize(mc, width, height);
        listManager.setWidth(width - 10);
        listManager.setHeight(height - 22);
    }

    final int WHITE = Color.WHITE.toInt();

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.getFontRend().draw(matrixStack, "Running Scripts:", 5, 3, WHITE);
        listManager.onDraw(matrixStack, this, mouseX, mouseY, partialTicks);
    }

    private class Script implements Drawable, Moveable, InputSubscriber {
        Thread thread;
        GuiButton stop;
        int labelColor = Status.NEW.getStatusColor().toInt();
        private int x, y;
        boolean isVisible;

        public Script(final Thread thread) {
            this.thread = thread;

            //stop = new GuiButton(new WidgetID(701), 0, 0, 20, 12, LuaValue.NIL, LuaValue.valueOf("[X]"), "colors.runningScript",
            //		Color.BLACK, Color.WHITE, Color.TEXT_c);
            stop = new GuiButton(0, 0, 20, 12, LuaValue.NIL, LuaValue.valueOf("[X]"), "runningScript", "stopButton");
            stop.setOnClick(new OnClickHandler() {
                @Override
                public void onClick(int button, GuiButton sButton) {
                    luaDebug.stop(thread);
                }
            });
        }

        public void statusChanged(Status status) {
            labelColor = status.getStatusColor().toInt();
        }

        @Override
        public void setPos(int x, int y) {
            this.x = x;
            this.y = y;
            stop.setPos(x, y);
        }

        @Override
        public void setX(int x) {
            setPos(x, y);
        }

        @Override
        public void setY(int y) {
            setPos(x, y);
        }

        @Override
        public void setVisible(boolean b) {
            this.isVisible = b;
        }

        @Override
        public int getItemHeight() {
            return stop.getHei();
        }

        @Override
        public int getItemWidth() {
            return RunningScriptsGui.this.width - 15;
        }

        @Override
        public void setWidth(int i) {
        }

        @Override
        public void setHeight(int i) {
        }

        @Override
        public int getX() {
            return this.x;
        }

        @Override
        public int getY() {
            return this.y;
        }

        @Override
        public void onDraw(MatrixStack matrixStack, Gui g, int mouseX, int mouseY, float partialTicks) {
            stop.onDraw(matrixStack, g, mouseX, mouseY, partialTicks);
            int dY = getY() + 2;
            int m = g.drawMonospaceString(matrixStack, "[", (int) (getX() + stop.getWid() * 1.5), dY, Color.WHITE.toInt());
            double uptime = LuaDebug.getUptime(thread);
            TimeFormat timeFormat = Utils.formatTime(uptime);
            m = g.drawMonospaceString(matrixStack,
                    String.format("%02d:%02d:%02d.%02d",
                            timeFormat.days * 24 + timeFormat.hours,
                            timeFormat.mins, timeFormat.seconds, timeFormat.millis), m, dY, Color.TEXT_7.toInt());
            m = g.drawMonospaceString(matrixStack, "] ", m, dY, Color.WHITE.toInt());
            String label = LuaDebug.getLabel(thread);
            label = label == null ? "?" : label;
            g.drawMonospaceString(matrixStack, label, m, dY, labelColor);
        }

        @Override
        public boolean onScroll(Gui gui, double i) {
            return false;
        }

        @Override
        public boolean onMouseClick(Gui gui, double x, double y, int buttonNum) {
            return stop.onMouseClick(gui, x, y, buttonNum);
        }

        @Override
        public boolean onMouseRelease(Gui gui, double x, double y, int state) {
            return stop.onMouseRelease(gui, x, y, state);
        }

        @Override
        public boolean onMouseClickMove(Gui gui, double x, double y, int buttonNum, double q, double r) {
            return false;
        }

        @Override
        public boolean onCharTyped(Gui gui, char typedChar, int mods) {
            return false;
        }

        @Override
        public boolean onKeyPressed(Gui gui, int keyCode, int scanCode, int modifiers) {
            return false;
        }

        @Override
        public boolean onKeyRelease(Gui gui, int keyCode, int scanCode, int modifiers) {
            return false;
        }

        @Override
        public boolean onKeyRepeat(Gui gui, int keyCode, int scanCode, int modifiers, int n) {
            return false;
        }

    }
}
