package com.theincgi.advancedmacros.lua.functions;

import net.minecraft.client.texture.Sprite;

import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

import java.util.HashMap;
import java.util.Map;

public class GetTextureList extends ZeroArgFunction {
    private final Map<String, Sprite> mapRegisteredSprites;

    public GetTextureList() throws NoSuchFieldException, RuntimeException, IllegalAccessException {
        this.mapRegisteredSprites = new HashMap<String, Sprite>();
        //		AtlasTexture map = MinecraftClient.getInstance().textureManager.getTexture(textureLocation)getTextureMap();
        //		Field f = ObfuscationReflectionHelper.findField(AtlasTexture.class, "field_94252_e"); //TESTME getTextureList
        //		//Field f = TextureMap.class.getDeclaredField(isObf?"j":"mapRegisteredSprites");
        //		f.setAccessible(true);
        //		mapRegisteredSprites = (Map<String, TextureAtlasSprite>) f.get(map);
    }

    @Override
    public LuaValue call() {
        LuaTable t = new LuaTable();
        int i = 0;
        for (Sprite o : mapRegisteredSprites.values()) {
            t.set(++i, o.getId().toString());
        }
        return t;
    }
}
