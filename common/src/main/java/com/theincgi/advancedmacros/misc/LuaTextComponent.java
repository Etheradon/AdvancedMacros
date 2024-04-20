package com.theincgi.advancedmacros.misc;

import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import org.luaj.vm2_v3_0_1.LuaError;
import org.luaj.vm2_v3_0_1.LuaValue;

import java.util.List;

public class LuaTextComponent implements Text {

    private String text;
    private LuaValue action;
    private boolean allowHover;

    private final MutableText internalText;

    public LuaTextComponent(String text, LuaValue action, boolean allowHover) {
        internalText = Text.literal(text);
        this.text = text;
        this.action = action;
        this.allowHover = allowHover;
        if (action.isCallable()) {
            internalText.setStyle(getStyle().withClickEvent(new LuaTextComponentClickEvent(action, this)));
        }
        if ((action.isstring() || action.istable()) && allowHover) {
            if (action.istable() && !action.get("click").isnil()) {
                if (!action.get("click").isCallable()) {
                    throw new LuaError("'click' value of clickable text component table must be callable.");
                }
                internalText.setStyle(getStyle().withClickEvent(new LuaTextComponentClickEvent(action.get("click"), this)));
            }
            if (action.istable() && !action.get("hover").isnil()) {
                internalText.setStyle(getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Utils.toTextComponent(action.get("hover").tojstring(), null, false).a)));
            } else {
                internalText.setStyle(getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Utils.toTextComponent(action.tojstring(), null, false).a)));
            }
        }

    }

    @Override
    public Style getStyle() {
        return internalText.getStyle();
    }

    @Override
    public TextContent getContent() {
        return internalText.getContent();
    }

    @Override
    public List<Text> getSiblings() {
        return internalText.getSiblings();
    }

    @Override
    public OrderedText asOrderedText() {
        return internalText.asOrderedText();
    }

    public String getText() {
        return text;
    }

}
