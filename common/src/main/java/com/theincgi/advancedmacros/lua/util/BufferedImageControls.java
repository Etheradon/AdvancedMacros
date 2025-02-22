package com.theincgi.advancedmacros.lua.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.theincgi.advancedmacros.AdvancedMacros;
import com.theincgi.advancedmacros.event.TaskDispatcher;
import com.theincgi.advancedmacros.gui.Color;
import com.theincgi.advancedmacros.lua.LuaValTexture;
import com.theincgi.advancedmacros.lua.functions.HTTP;
import com.theincgi.advancedmacros.misc.Utils;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.luaj.vm2_v3_0_1.LuaError;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.Varargs;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;
import org.luaj.vm2_v3_0_1.lib.TwoArgFunction;
import org.luaj.vm2_v3_0_1.lib.VarArgFunction;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BufferedImageControls extends LuaTable {

    BufferedImage img;
    NativeImageBackedTexture dynamicTexture;
    LuaValTexture tex;
    //buffering means multiple objects arn't created and destroyed, but provide a thread safe way to buffer without syncronization
    ThreadLocal<Color> colorBuffer = new ThreadLocal<Color>() {
        @Override
        protected Color initialValue() {
            return new Color(0);
        }
    };

    public static class CreateImg extends TwoArgFunction {

        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            BufferedImage img = new BufferedImage(arg1.checkint(), arg2.checkint(), BufferedImage.TYPE_INT_ARGB);
            return new BufferedImageControls(img);
        }

    }

    public static class LoadImg extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            if (arg instanceof HTTP.LuaInputStream) {
                try {
                    return new BufferedImageControls(ImageIO.read(((HTTP.LuaInputStream) arg).getInputStream()));
                } catch (IOException e) {
                    throw new LuaError("IOException occurred, " + e.getMessage());
                }
            } else {
                File f = Utils.parseFileLocation(arg);//new File(AdvancedMacros.macrosRootFolder, arg.checkjstring());
                try {
                    return new BufferedImageControls(ImageIO.read(f));
                } catch (IOException e) {
                    throw new LuaError("IOException occurred, " + e.getMessage());
                }
            }
        }

    }

    public static class GetFonts extends ZeroArgFunction {

        @Override
        public LuaValue call() {
            LuaTable fonts = new LuaTable();
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

            for (Font font : ge.getAllFonts()) {
                if (!font.getFontName().toLowerCase().contains("italic") && !font.getFontName().toLowerCase().contains("bold")) {
                    fonts.set(fonts.length() + 1, font.getFontName());
                }
            }
            return fonts;
        }

    }

    public static class GetFormats extends ZeroArgFunction {

        @Override
        public LuaValue call() {
            LuaTable out = new LuaTable();
            LuaTable temp = new LuaTable();
            for (String s : ImageIO.getReaderFormatNames()) {
                temp.set(temp.length() + 1, s);
            }
            out.set("readers", temp);
            temp = new LuaTable();
            for (String s : ImageIO.getWriterFormatNames()) {
                temp.set(temp.length() + 1, s);
            }
            out.set("writers", temp);
            return out;
        }

    }

    public BufferedImageControls(BufferedImage img) {
        this.img = img;
        set("getPixel", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue x, LuaValue y) {
                Color b = colorBuffer.get();
                b.fromHex(img.getRGB(x.checkint() - 1, y.checkint() - 1));
                return b.toLuaValue(AdvancedMacros.COLOR_SPACE_IS_255);
            }
        });
        set("setPixel", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                int x = args.checkint(1) - 1;
                int y = args.checkint(2) - 1;
                args = args.subargs(3);
                img.setRGB(x, y, Utils.parseColor(args, AdvancedMacros.COLOR_SPACE_IS_255).toInt());
                return NONE;
            }
        });
        set("save", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg, LuaValue optFormat) {
                try {
                    File f = Utils.parseFileLocation(arg);//FileSystem.parseFileLocation(arg);
                    ImageIO.write(img, optFormat.optjstring("png"), f);
                } catch (IOException e) {
                    throw new LuaError("IOException occurred, " + e.getMessage());
                }
                return NONE;
            }
        });
        set("convertToTable", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                Color buf = colorBuffer.get();
                LuaTable cols = new LuaTable();
                for (int x = 0; x < img.getWidth(); x++) {
                    LuaTable sCol = new LuaTable();
                    cols.set(x + 1, sCol);
                    for (int y = 0; y < img.getHeight(); y++) {
                        buf.fromHex(img.getRGB(x, y));
                        sCol.set(y + 1, buf.toLuaValue(AdvancedMacros.COLOR_SPACE_IS_255));
                    }
                }
                return cols;
            }
        });
        set("getWidth", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return valueOf(img.getWidth());
            }

            ;
        });
        set("getHeight", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return valueOf(img.getHeight());
            }

            ;
        });
        set("getSize", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs unused) {
                LuaTable temp = new LuaTable();
                temp.set(1, valueOf(img.getWidth()));
                temp.set(2, valueOf(img.getHeight()));
                return temp.unpack();
            }
        });
        set("graphics", new GraphicsContextControls(this));
        LuaTable texture = new LuaTable();
        set("update", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (dynamicTexture != null) {
                    TaskDispatcher.addTask(() -> { //changed to non blocking to prevent possible thread locks
                        RenderSystem.bindTexture(0);
                        dynamicTexture.bindTexture();
                        Utils.updateNativeImage(img, dynamicTexture.getImage()); //TODO see if this can be moved outside the TaskDispatcher's call
                        dynamicTexture.upload();
                    });
                }
                return NONE;
            }
        });

    }

    public void updateTexture() {
        //TaskDispatcher.addTask(()->{
        //tex.bindTexture();

        NativeImage ni = dynamicTexture.getImage();
        Utils.updateNativeImage(img, ni);
        //			for(int y=0; y<img.getHeight(); y++) {
        //				for(int x=0; x<img.getHeight(); x++) {
        //					int c = img.getRGB(x, y);
        //					ni.setPixelRGBA(x, y, c);
        //				}
        //			}
        dynamicTexture.upload();
        //});
    }

    public BufferedImage getImg() {
        return img;
    }

    private void createTexture() {
        TaskDispatcher.waitFor(TaskDispatcher.addTask(new Runnable() {
            @Override
            public void run() {
                synchronized (BufferedImageControls.this) {
                    if (dynamicTexture != null && tex != null) {
                        return;
                    }
                    dynamicTexture = new NativeImageBackedTexture(img.getWidth(), img.getHeight(), true);
                    tex = new LuaValTexture(dynamicTexture);
                    updateTexture();

                }
            }
        }));
    }

    public NativeImageBackedTexture getDynamicTexture() {
        if (dynamicTexture == null) {
            createTexture();
        }
        return dynamicTexture;
    }

    public LuaValTexture getLuaValTexture() {
        if (tex == null) {
            createTexture();
        }
        return tex;
    }

}
