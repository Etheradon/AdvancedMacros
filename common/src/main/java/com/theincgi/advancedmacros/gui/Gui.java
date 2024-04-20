package com.theincgi.advancedmacros.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.theincgi.advancedmacros.gui.elements.Drawable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Gui extends Screen implements ParentElement {

    TextRenderer fontRend = MinecraftClient.getInstance().textRenderer;
    //private LinkedList<KeyTime> heldKeys = new LinkedList<>();
    private HashMap<Integer, Long> keyRepeatDelay = new HashMap<>();
    private LinkedList<InputSubscriber> inputSubscribers = new LinkedList<>();
    /**
     * The next key or mouse event will be sent to this before anything else
     */
    public InputSubscriber nextKeyListen = null;
    private LinkedList<Drawable> drawables = new LinkedList<>();
    public volatile Drawable drawLast = null;
    private Object focusItem = null;
    private int lastResWidth, lastResHeight;

    /**
     * Strictly key typed and mouse clicked events atm
     */
    public InputSubscriber firstSubsciber;

    private Queue<InputSubscriber> inputSubscriberToAdd = new LinkedList<>(), inputSubscribersToRemove = new LinkedList<>();
    private Queue<Drawable> drawableToAdd = new LinkedList<>(), drawableToRemove = new LinkedList<>();

    private int repeatMod = 0;
    private boolean drawDefaultBackground = true;

    public Gui() {
        super(null); //text component title
        super.client = MinecraftClient.getInstance();
    }

    public void drawHorizontalLine(DrawContext drawContext, int startX, int endX, int y, int color) {
        drawContext.drawHorizontalLine(startX, endX, y, color);
    }

    public void drawVerticalLine(DrawContext drawContext, int x, int startY, int endY, int color) {
        drawContext.drawVerticalLine(x, startY, endY, color);
    }

    public static void drawBoxedRectangle(DrawContext drawContext, int x, int y, int w, int h, int boarderW, int frame, int fill) {
        drawContext.fill(x, y, x + w + 1, y + h + 1, frame);
        drawContext.fill(x + boarderW, y + boarderW, x + w - boarderW + 1, y + h - boarderW + 1, fill);
    }

    public void drawBoxedRectangle(DrawContext drawContext, int x, int y, int w, int h, int frame, int fill) {
        drawContext.fill(x, y, x + w + 1, y + h + 1, fill);
        drawHollowRect(drawContext, x, y, w, h, frame);
    }

    private void drawHollowRect(DrawContext drawContext, int x, int y, int w, int h, int col) {
        drawHorizontalLine(drawContext, x, x + w, y, col);
        drawHorizontalLine(drawContext, x, x + w, y + h, col);
        drawVerticalLine(drawContext, x, y, y + h, col);
        drawVerticalLine(drawContext, x + w, y, y + h, col);
    }

    /**
     * returns next x to use in this for multiColoring
     */
    public int drawMonospaceString(DrawContext drawContext, String str, int x, int y, int color) {
        TextRenderer fr = getFontRend();
        int cWid = (int) ((8f / 12) * fr.fontHeight);
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            float offset = cWid / 2 - fr.getWidth(String.valueOf(c)) / 2;
            drawContext.drawText(fr, c + "", (int) (x + offset + cWid * i), y, color, false);
        }
        return cWid * str.length() + x;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        keyRepeatDelay.put(scanCode, System.currentTimeMillis());
        if (nextKeyListen != null && nextKeyListen.onKeyPressed(this, keyCode, scanCode, modifiers)) {
            nextKeyListen = null;
            return true;
        }
        if (firstSubsciber != null && firstSubsciber.onKeyPressed(this, keyCode, scanCode, modifiers)) {
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc()) {
            this.close();
            return true;
        }
        //heldKeys.add(new KeyTime(keyCode, typedChar));
        synchronized (inputSubscribers) {
            for (InputSubscriber inputSubscriber : inputSubscribers) {
                if (inputSubscriber.onKeyPressed(this, keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char typedChar, int modifiers) {
        if (nextKeyListen != null && nextKeyListen.onCharTyped(this, typedChar, modifiers)) {
            nextKeyListen = null;
            return true;
        }
        if (firstSubsciber != null && firstSubsciber.onCharTyped(this, typedChar, modifiers)) {
            return true;
        }
        if (super.charTyped(typedChar, modifiers)) {
            return true;
        }
        //heldKeys.add(new KeyTime(keyCode, typedChar));
        synchronized (inputSubscribers) {
            for (InputSubscriber inputSubscriber : inputSubscribers) {
                if (inputSubscriber.onCharTyped(this, typedChar, modifiers)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * fires after key has been held in for a time mod will always be positive
     * <br><b>Tip</b>: Use mod to reduce key repeat speed.
     * <blockquote><br> if(mod%5==0){...} </code></blockquote>>
     */
    public boolean onKeyRepeated(Gui gui, int keyCode, int scanCode, int mods, int n) {
        if (!keyRepeatDelay.containsKey(scanCode)) {
            return false; //safety check
        }
        if (keyRepeatDelay.get(scanCode) + 300 > System.currentTimeMillis()) {
            return false; //wait a little while before key spaming
        }
        //TODO customize key repeat delay
        if (firstSubsciber != null && firstSubsciber.onKeyRepeat(gui, keyCode, scanCode, mods, n)) {
            return true;
        }
        synchronized (inputSubscribers) {
            for (InputSubscriber inputSubscriber : inputSubscribers) {
                if (inputSubscriber.onKeyRepeat(gui, keyCode, scanCode, mods, n)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean keyReleased(int p_223281_1_, int p_223281_2_, int p_223281_3_) {
        return onKeyRelease(this, p_223281_1_, p_223281_2_, p_223281_3_);
    }

    /**
     * very overridable, this is called after input subscribers have not claimed this event
     */
    public boolean onKeyRelease(Gui gui, int keyCode, int scanCode, int modifiers) {
        if (firstSubsciber != null && firstSubsciber.onKeyRelease(this, keyCode, scanCode, modifiers)) {
            return true;
        }
        synchronized (inputSubscribers) {
            for (InputSubscriber inputSubscriber : inputSubscribers) {
                if (inputSubscriber.onKeyRelease(this, keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Window mainWindow;

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
        if (mainWindow == null) {
            mainWindow = MinecraftClient.getInstance().getWindow();
        }
        if (mainWindow.getScaledWidth() != lastResWidth || mainWindow.getScaledHeight() != lastResHeight) {
            lastResWidth = mainWindow.getScaledWidth();
            lastResHeight = mainWindow.getScaledHeight();
            resize(MinecraftClient.getInstance(), lastResWidth, lastResHeight);
        }

        synchronized (inputSubscribers) {
            synchronized (inputSubscriberToAdd) {
                while (!inputSubscriberToAdd.isEmpty()) {
                    inputSubscribers.add(inputSubscriberToAdd.poll());
                }
            }
            synchronized (inputSubscribersToRemove) {
                while (!inputSubscribersToRemove.isEmpty()) {
                    inputSubscribers.remove(inputSubscribersToRemove.poll());
                }
            }

        }
        synchronized (drawables) {
            synchronized (drawableToAdd) {
                while (!drawableToAdd.isEmpty()) {
                    drawables.add(drawableToAdd.poll());
                }
            }
            synchronized (drawableToRemove) {
                while (!drawableToRemove.isEmpty()) {
                    drawables.remove(drawableToRemove.poll());
                }
            }

        }

        if (drawDefaultBackground) {
            renderBackground(drawContext, mouseX, mouseY, partialTicks); //prev default bg
        }

        if (MinecraftClient.getInstance().currentScreen == this) { //do not steal the child gui's events!
            //			int i = Mouse.getDWheel();
            //			if(i!=0)
            //				mouseScroll((int) Math.signum(i));
            //			Stack<KeyTime> killList = new Stack<>();
            //			for(KeyTime k:heldKeys){
            //				k.fireKeyRepeat();
            //				if(k.dead){
            //					killList.push(k);
            //				}
            //			}
            //			for (KeyTime keyTime : killList) {
            //				boolean flag = false;
            //				synchronized (inputSubscribers) {
            //					for (InputSubscriber inputSubscriber : inputSubscribers) {
            //						if(inputSubscriber.onKeyRelease(this, keyTime.key, keyTime.keyCode)) {
            //							flag = true;
            //							break;
            //						}
            //					}
            //				}
            //				if(!flag)
            //					onKeyRelease(this, keyTime.key, keyTime.keyCode);
            //				heldKeys.remove(keyTime);
            //			}
        }

        synchronized (drawables) {
            for (Drawable drawable : drawables) {
                //TODO 1.19 Update: RenderSystem.pushTextureAttributes();
                //RenderSystem.enableAlpha();
                //RenderSystem.disableBlend();
                //RenderSystem.enableColorMaterial();

                drawable.onDraw(drawContext, this, mouseX, mouseY, partialTicks);
            }
        }
        if (drawLast != null) {
            drawLast.onDraw(drawContext, this, mouseX, mouseY, partialTicks);
        }
    }

    public void drawImage(DrawContext drawContext, Identifier texture, int x, int y, int wid, int hei, float uMin, float vMin, float uMax, float vMax) {
        MatrixStack matrixStack = drawContext.getMatrices();

        matrixStack.push();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, texture);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();

        buf.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_TEXTURE_COLOR);
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();

        buf.vertex(matrix, x, y + hei, 0).texture(uMin, vMax).color(0xFFFFFFFF).next();
        buf.vertex(matrix, x + wid, y + hei, 0).texture(uMax, vMax).color(0xFFFFFFFF).next();
        buf.vertex(matrix, x, y, 0).texture(uMin, vMin).color(0xFFFFFFFF).next();
        buf.vertex(matrix, x + wid, y, 0).texture(uMax, vMin).color(0xFFFFFFFF).next();
        tessellator.draw();

        matrixStack.pop();
        RenderSystem.disableBlend();
    }

    public static void drawPixel(DrawContext drawContext, int x, int y, int color) {
        drawContext.fill(x, y, x + 1, y + 1, color);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (firstSubsciber != null && firstSubsciber.onScroll(this, verticalAmount)) {
            return true;
        }
        synchronized (inputSubscribers) {
            for (InputSubscriber inputSubscriber : inputSubscribers) {
                if (inputSubscriber.onScroll(this, verticalAmount)) {
                    return true;
                }
            }

        }
        return false;
    }

    //	private class KeyTime{
    //		int keyCode;
    //		char key;
    //		long timeSig;
    //		static final int validationTime = 500;
    //		static final int repeatDelay = 10;
    //		boolean dead = false;
    //		public KeyTime(int keyCode, char key) {
    //			super();
    //			this.keyCode = keyCode;
    //			this.key = key;
    //			this.timeSig = System.currentTimeMillis();
    //		}
    //
    //		public boolean isValid(){
    //			if(!Keyboard.isDown(keyCode)){
    //				dead=true;
    //				return false;}
    //			return System.currentTimeMillis()-timeSig>validationTime;
    //		}
    //
    //		public void fireKeyRepeat(){
    //			if(isValid()){
    //				timeSig=System.currentTimeMillis()-validationTime+repeatDelay;
    //				keyRepeated(key, keyCode,repeatMod);
    //				repeatMod = Math.max(0, repeatMod+1);//must always be positve that all
    //			}
    //		}
    //	}

    public TextRenderer getFontRend() {
        return fontRend;
    }

    public void drawCenteredString(DrawContext drawContext, TextRenderer TextRendererIn, String text, int x, int y, int color) {
        int wid = fontRend.getWidth(text);
        drawContext.drawText(TextRendererIn, text, x - wid / 2, y - fontRend.fontHeight / 2, color, false);//, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (nextKeyListen != null && nextKeyListen.onMouseClick(this, mouseX, mouseY, mouseButton)) {
            nextKeyListen = null;
            return true;
        }
        if (firstSubsciber != null && firstSubsciber.onMouseClick(this, mouseX, mouseY, mouseButton)) {
            return true;
        }

        //System.out.println("CLICK 1");
        synchronized (inputSubscribers) {
            for (InputSubscriber inputSubscriber : inputSubscribers) {
                if (inputSubscriber.onMouseClick(this, mouseX, mouseY, mouseButton)) {
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        if (firstSubsciber != null && firstSubsciber.onMouseRelease(this, mouseX, mouseY, state)) {
            return true;
        }
        synchronized (inputSubscribers) {
            for (InputSubscriber inputSubscriber : inputSubscribers) {
                if (inputSubscriber.onMouseRelease(this, mouseX, mouseY, state)) {
                    return true;
                }
            }
        }
        return super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double q, double r) {

        if (firstSubsciber != null && firstSubsciber.onMouseClickMove(this, mouseX, mouseY, clickedMouseButton, q, r)) {
            return true;
        }
        synchronized (inputSubscribers) {
            for (InputSubscriber inputSubscriber : inputSubscribers) {
                if (inputSubscriber.onMouseClickMove(this, mouseX, mouseY, clickedMouseButton, q, r)) {
                    return true;
                }
            }
        }
        return super.mouseDragged(mouseX, mouseY, clickedMouseButton, q, r);
    }

    public static interface Focusable {

        public boolean isFocused();

        public void setFocused(boolean f);

    }

    public static interface InputSubscriber {

        /**
         * @param i scroll amount
         */
        public boolean onScroll(Gui gui, double i);

        public boolean onMouseClick(Gui gui, double x, double y, int buttonNum);

        public boolean onMouseRelease(Gui gui, double x, double y, int state);

        public boolean onMouseClickMove(Gui gui, double x, double y, int buttonNum, double q, double r);

        public boolean onCharTyped(Gui gui, char typedChar, int mods);

        /**
         * aka keyTyped
         */
        public boolean onKeyPressed(Gui gui, int keyCode, int scanCode, int modifiers);

        public boolean onKeyRepeat(Gui gui, int keyCode, int scanCode, int modifiers, int n);

        public boolean onKeyRelease(Gui gui, int keyCode, int scanCode, int modifiers);

    }

    public void showGui() {
        //AdvancedMacros.lastGui = this;
        MinecraftClient.getInstance().setScreen(this);
    }

    //something to call each time you switch back
    public void onOpen() {

    }

    public Object getFocusItem() {
        //System.out.println("Foooocas "+focusItem);
        return focusItem;
    }

    public void setFocusItem(Object focusItem) {
        this.focusItem = focusItem;
        //System.out.println("FOCUS: >> "+focusItem);
    }

    public int getUnscaledWindowWidth() {
        return MinecraftClient.getInstance().getWindow().getWidth();
    }

    public int getUnscaledWindowHeight() {
        return MinecraftClient.getInstance().getWindow().getHeight();
    }

    public void setDrawDefaultBackground(boolean drawDefaultBackground) {
        this.drawDefaultBackground = drawDefaultBackground;
    }

    public boolean getDrawDefaultBackground() {
        return drawDefaultBackground;
    }

    public void addDrawable(Drawable d) {
        synchronized (drawableToAdd) {
            drawableToAdd.add(d);
        }
    }

    public void addInputSubscriber(InputSubscriber i) {
        synchronized (inputSubscriberToAdd) {
            inputSubscriberToAdd.add(i);
        }
    }

    public void removeDrawables(Drawable d) {
        synchronized (drawableToRemove) {
            drawableToRemove.add(d);
        }
    }

    public void removeInputSubscriber(InputSubscriber i) {
        synchronized (inputSubscribersToRemove) {
            inputSubscribersToRemove.add(i);
        }
    }

    public void clearDrawables() {
        synchronized (drawableToRemove) {
            drawableToRemove.addAll(drawables);
        }
    }

    public void clearInputSubscribers() {
        synchronized (inputSubscribersToRemove) {
            inputSubscribersToRemove.addAll(inputSubscribers);
        }
    }

    /**
     * Synchronize usage on linked list! do not use to add or remove elements directly
     */
    protected LinkedList<InputSubscriber> getInputSubscribers() {
        return inputSubscribers;
    }

    public boolean onScroll(Gui gui, double i) {
        return false;
    }

}
