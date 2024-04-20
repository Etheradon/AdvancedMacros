package com.theincgi.advancedmacros.gui.elements;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

//TODO check usage, LuaTexVal tends to get used, dont recall this as much
public class CustomTexture {

    BufferedImage bufferedImage;
    ByteBuffer buffer;
    private int textureID;

    public CustomTexture(BufferedImage buffImg) {
        this.bufferedImage = buffImg;
        buffer = ByteBuffer.allocateDirect(buffImg.getWidth() * buffImg.getHeight() * 4);
        //textureID = GL11.glGenTextures();
    }

    public void update() {
        buffer.clear();
        int[] pixels = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                int pixel = pixels[y * bufferedImage.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));               // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }
        buffer.flip();

    }

    private void bindTex() {
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, bufferedImage.getWidth(), bufferedImage.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
    }

    public void draw(int x, int y, int wid, int hei) {
        //TODO 1.19 Update: RenderSystem.pushTextureAttributes();
        //		RenderSystem.enableLighting();
        //		//RenderSystem.disableDepth();
        bindTex();
        int z = 0;
        int uMin, uMax, vMin, vMax;
        uMin = vMin = 1;
        uMax = vMax = 0;
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(x, y, z).texture(uMin, vMin).next();
        buffer.vertex(x, y + hei, z).texture(uMin, vMax).next();
        buffer.vertex(x + hei, y + hei, z).texture(uMax, vMax).next();
        buffer.vertex(x + hei, y, z).texture(uMax, vMin).next();
        Tessellator.getInstance().draw();
        unbindTex();
    }

    private void unbindTex() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

}
