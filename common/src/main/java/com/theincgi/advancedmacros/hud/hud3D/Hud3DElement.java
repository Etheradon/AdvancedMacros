package com.theincgi.advancedmacros.hud.hud3D;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.theincgi.advancedmacros.lua.LuaValTexture;
import com.theincgi.advancedmacros.misc.Utils;
import org.luaj.vm2_v3_0_1.LuaError;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.Varargs;
import org.luaj.vm2_v3_0_1.lib.VarArgFunction;
import org.lwjgl.opengl.GL11;

public class Hud3DElement extends WorldHudItem {
    //public LuaTable data;
    LuaValTexture texture;// = Utils.checkTexture("resource:holoblock.png");

    public Hud3DElement() {

    }

    @Override
    public void loadControls(LuaValue t) {
        super.loadControls(t);
        enableColorControls(t);

        LuaTable data = new LuaTable();
        data.set("lighting", false);
        //data.set("alphaTest", true);
        //data.set("depthTest", true);
        data.set("cullFace", false);
        data.set("mode", "triangles");
        data.set("verts", new LuaTable());
        data.set("uv", new LuaTable());
        t.set("data", data);

        for (ElementOps op : ElementOps.values()) {
            t.set(op.name(), new DoElementOp(op));
        }
    }

    public class DoElementOp extends VarArgFunction {
        ElementOps op;

        public DoElementOp(ElementOps op) {
            this.op = op;
        }

        @Override
        public Varargs invoke(Varargs args) {
            switch (op) {
                case setTexture:
                    if (args.arg1().isnil()) {
                        texture = null;
                    } else {
                        texture = Utils.parseTexture(args.arg1());
                    }
                    return NONE;
                default:
                    throw new LuaError("Unimplemented function");
            }
        }
    }

    public enum ElementOps {
        setTexture
    }

    @Override
    public void render(MatrixStack ms) {
        try {
            LuaTable data = getControls().get("data").checktable();

            RenderSystem.enableBlend();
            //TODO 1.19 Update: RenderSystem.enableAlphaTest();
            RenderSystem.enableDepthTest();

            LuaValue cullMode = data.get("cullFace");
            if (cullMode.isnil() || cullMode.isboolean() && !cullMode.checkboolean()) {
                RenderSystem.disableCull();
            } else {
                RenderSystem.enableCull();
                //RenderSystem.cullFace( CullFace.valueOf(cullMode.checkjstring().replace(' ', '_').toUpperCase()));
            }

            if (data.get("lighting").checkboolean()) {
                //TODO 1.19 Update: RenderSystem.enableLighting();
            }
            //			RenderSystem.enableCull();
            //			RenderSystem.cullFace(CullFace.BACK);
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);

            render(data, ms.peek().getPositionMatrix());
            RenderSystem.enableCull();
            //TODO 1.19 Update: RenderSystem.disableLighting();
            ms.pop();
        } catch (Throwable e) {
            Utils.logError(e);
            disableDraw();
        }
    }

    /**
     * xyz is players acurate location for partial tick
     */
    private void render(LuaTable t, Matrix4f transform) {
        //		if(t.get("lighting").checkboolean()){
        //			RenderSystem.enableLighting();
        //		}else{
        //			RenderSystem.disableLighting();
        //		}
        //		if(t.get("alphaTest").checkboolean()){
        //			RenderSystem.enableAlpha();
        //		}else{
        //			RenderSystem.disableAlpha();
        //		}
        //		if(t.get("depthTest").checkboolean()){
        //			RenderSystem.enableDepth();
        //		}else{
        //			RenderSystem.disableDepth();
        //		}
        //		String cullSide = t.get("cullFace").checkjstring().toLowerCase();
        //		if(cullSide.equals("back")){
        //			RenderSystem.cullFace(CullFace.BACK);
        //		}else if(cullSide.equals("front")){
        //			RenderSystem.cullFace(CullFace.FRONT);
        //		}else if(cullSide.equals("front and back")){
        //			RenderSystem.cullFace(CullFace.FRONT_AND_BACK);
        //		}else {
        //			RenderSystem.cullFace(CullFace.FRONT);
        //		}
        String mode = t.get("mode").checkjstring();
        //drawSideFace(playerX, playerY, playerZ, 1, 1, 0);
        doGeomotryRender(t, transform, mode);

    }

    private void doGeomotryRender(LuaTable t, Matrix4f transform, String mode) {
        try {
            //drawSideFace(playerX, playerY, playerZ, 1, 1, 0);
            LuaValue verts = t.get("verts").checktable();
            LuaValue uvs = t.get("uv");
            boolean useTexture = !(texture == null) && !texture.isnil() && uvs.istable() &&
                    uvs.length() >= 3;
            VertexFormat type = VertexFormats.POSITION;
            if (useTexture) {
                texture.bindTexture();
                RenderSystem.enableTexture();
                type = VertexFormats.POSITION_TEXTURE;
            } else {
                RenderSystem.disableTexture();
            }

            int gLMode = mode.equals("strip") ? GL11.GL_TRIANGLE_STRIP :
                    mode.equals("fan") ? GL11.GL_TRIANGLE_FAN :
                            mode.equals("quads") ? GL11.GL_QUADS :
                                    mode.equals("triangles") ? GL11.GL_TRIANGLES :
                                            mode.equals("quadStrip") ? GL11.GL_QUAD_STRIP : -1;
            if (gLMode == -1) {
                throw new LuaError("Invalid GL Mode in hud3D object");
            }

            if (verts.length() < 3) {
                throw new LuaError("Hud3D object missing vertex data");
            }
            if (gLMode == GL11.GL_TRIANGLES && verts.length() % 3 != 0) {
                throw new LuaError("Triangles mode requires vertex count % 3 == 0");
            }
            if (gLMode == GL11.GL_QUADS && verts.length() % 4 != 0) {
                throw new LuaError("Quads mode requires vertex count % 4 == 0");
            }
            if (gLMode == GL11.GL_QUAD_STRIP && verts.length() % 2 != 0) {
                throw new LuaError("Quads strip mode requires vertex count % 2 == 0");
            }

            VertexFormat.DrawMode drawMode = VertexFormat.DrawMode.QUADS;
            if (gLMode == GL11.GL_TRIANGLE_FAN) {
                drawMode = VertexFormat.DrawMode.TRIANGLE_FAN;
            } else if (gLMode == GL11.GL_TRIANGLE_STRIP) {
                drawMode = VertexFormat.DrawMode.TRIANGLE_STRIP;
            } else if (gLMode == GL11.GL_TRIANGLES) {
                drawMode = VertexFormat.DrawMode.TRIANGLES;
            } else if (gLMode == GL11.GL_QUADS) {
                drawMode = VertexFormat.DrawMode.QUADS;
            }

            color.apply();
            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            buffer.begin(drawMode, type);
            float x = this.x;
            float y = this.y;
            float z = this.z;

            int vertsLength = verts.length();
            for (int i = 1; i <= vertsLength; i++) {
                float vx, vy, vz;
                LuaValue v = verts.get(i);
                vx = (float) (v.get(1).checkdouble() + x);
                vy = (float) (v.get(2).checkdouble() + y);
                vz = (float) (v.get(3).checkdouble() + z);
                buffer.vertex(transform, vx, vy, vz);
                if (useTexture) {
                    LuaTable uv = uvs.get(i).checktable();
                    double uCoord, vCoord;
                    uCoord = uv.get(1).optdouble(0);
                    vCoord = uv.get(2).optdouble(0);

                    buffer.texture((float) uCoord, (float) vCoord);
                }
                buffer.next();
            }

        } catch (Throwable e) {
            Utils.logError(e);
        } finally {
            Tessellator.getInstance().draw();
        }
    }

}
