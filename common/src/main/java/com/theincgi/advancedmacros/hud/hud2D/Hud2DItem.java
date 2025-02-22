package com.theincgi.advancedmacros.hud.hud2D;

import com.theincgi.advancedmacros.AdvancedMacros;
import com.theincgi.advancedmacros.gui.Color;
import com.theincgi.advancedmacros.hud.Destroyable;
import com.theincgi.advancedmacros.misc.Utils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.Varargs;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;
import org.luaj.vm2_v3_0_1.lib.TwoArgFunction;
import org.luaj.vm2_v3_0_1.lib.VarArgFunction;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

public abstract class Hud2DItem implements Destroyable {

    float x, y, z;
    float lastX, lastY;
    float angle;
    //protected float opacity;
    protected boolean allowFrameInterpolation = false;
    LuaValue controls;
    private boolean isDrawing = false;
    Color color = Color.BLACK.copy();

    public Hud2DItem() {
        this.controls = new LuaTable();
        controls.set("setX", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                x = (float) arg.checkdouble();
                return LuaValue.NONE;
            }
        });
        controls.set("getX", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(x);
            }
        });
        controls.set("setY", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                y = (float) arg.checkdouble();
                return LuaValue.NONE;
            }
        });
        controls.set("getY", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(y);
            }
        });
        controls.set("setPos", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                arg2.checkdouble();
                x = (float) arg1.checkdouble();
                y = (float) arg2.checkdouble();
                return LuaValue.NONE;
            }
        });
        controls.set("getPos", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs arg) {
                LuaTable t = new LuaTable();
                t.set(1, LuaValue.valueOf(x));
                t.set(2, LuaValue.valueOf(y));
                return t.unpack();
            }
        });
        controls.set("setRot", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                angle = (float) arg.checkdouble();
                return NONE;
            }
        });
        controls.set("getRot", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return valueOf(angle);
            }
        });
        controls.set("setOpacity", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                color.setA((int) (arg.checkdouble() * 255));
                return LuaValue.NONE;
            }
        });
        controls.set("getOpacity", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(color.getA() / 255f);
            }
        });
        controls.set("enableDraw", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue v) {
                if (v.optboolean(true)) {
                    enableDraw();
                } else {
                    disableDraw();
                }
                return LuaValue.NONE;
            }
        });
        controls.set("disableDraw", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                disableDraw();
                return LuaValue.NONE;
            }
        });
        controls.set("destroy", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                destroy();
                return LuaValue.NONE;
            }
        });
        controls.set("setAllowFrameInterpolation", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                allowFrameInterpolation = arg.checkboolean();
                return LuaValue.NONE;
            }
        });
        controls.set("getAllowFrameInterpolation", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(allowFrameInterpolation);
            }
        });
        controls.set("setZ", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                z = (float) arg.checkdouble();
                return LuaValue.NONE;
            }
        });
        controls.set("getZ", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(z);
            }
        });
        controls.set("isDrawing", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(isDrawing);
            }
        });
    }

    public void enableColorControl() {
        getControls().set("setColor", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                color = Utils.parseColor(args, AdvancedMacros.COLOR_SPACE_IS_255);
                //colorInt = color.toInt();
                return LuaValue.NONE;
            }
        });
        getControls().set("getColor", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return color.toLuaValue(AdvancedMacros.COLOR_SPACE_IS_255);
            }
        });
    }

    public LuaTable getControls() {
        return controls.checktable();
    }

    protected void applyTransformation(MatrixStack matrixStack) {
        matrixStack.translate(x, y, 0);
        matrixStack.multiply(new Quaternionf().rotateLocalY((float) Math.toRadians(angle)));
    }

    @Override
    public void destroy() {
        disableDraw();
    }

    public synchronized void disableDraw() {
        if (!isDrawing) {
            return;
        }
        AdvancedMacros.EVENT_HANDLER.removeHud2DItem(this);
        isDrawing = false;
    }

    public synchronized void enableDraw() {
        if (isDrawing) {
            return;
        }
        isDrawing = true;
        AdvancedMacros.EVENT_HANDLER.addHud2DItem(this);
    }

    abstract public void render(DrawContext drawContext, float partialTicks);

    /**
     * Called by the render loop, not the render method
     */
    public void updateLastPos() {
        lastX = x;
        lastY = y;
    }

    public static float interpolate(float now, float before, float partialTicks) {
        return ((now * (1 - partialTicks)) + (before * partialTicks)) / 2f;
    }

    public float getOpacity() {
        return color.getA();
    }

}
