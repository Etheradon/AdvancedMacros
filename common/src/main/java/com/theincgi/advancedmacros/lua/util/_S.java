package com.theincgi.advancedmacros.lua.util;

import com.theincgi.advancedmacros.AdvancedMacros;
import com.theincgi.advancedmacros.lua.LuaDebug;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaThread;
import org.luaj.vm2_v3_0_1.LuaValue;

import java.util.HashMap;

/**
 * Script local environment<br> All instances of a script will access the same table from this.<br>
 * Automatically synchronized
 */
public class _S extends LuaTable {
    private static HashMap<String, LuaTable> map = new HashMap<>();

    public _S() {

    }

    @Override
    public void hashset(LuaValue key, LuaValue value) {
        LuaThread lt = AdvancedMacros.globals.getCurrentLuaThread();
        String src = LuaDebug.getSourceName(lt);

        synchronized (src) {
            map.computeIfAbsent(src, (k) -> {
                return new LuaTable();
            }).hashset(key, value);
        }
    }

    @Override
    public LuaValue get(int key) {
        if (AdvancedMacros.globals == null) {
            return NIL;
        }
        if (AdvancedMacros.globals.getCurrentLuaThread() == null) {
            return NIL;
        }
        LuaThread lt = AdvancedMacros.globals.getCurrentLuaThread();
        String src = LuaDebug.getSourceName(lt);

        synchronized (src) {
            return map.computeIfAbsent(src, (k) -> {
                return new LuaTable();
            }).get(key);
        }
    }

    @Override
    public LuaValue get(LuaValue key) {
        if (AdvancedMacros.globals == null) {
            return NIL;
        }
        if (AdvancedMacros.globals.getCurrentLuaThread() == null) {
            return NIL;
        }
        LuaThread lt = AdvancedMacros.globals.getCurrentLuaThread();
        String src = LuaDebug.getSourceName(lt);

        synchronized (src) {
            return map.computeIfAbsent(src, (k) -> {
                return new LuaTable();
            }).get(key);
        }
    }

    @Override
    public LuaValue get(String key) {
        if (AdvancedMacros.globals == null) {
            return NIL;
        }
        if (AdvancedMacros.globals.getCurrentLuaThread() == null) {
            return NIL;
        }
        LuaThread lt = AdvancedMacros.globals.getCurrentLuaThread();
        String src = LuaDebug.getSourceName(lt);

        synchronized (src) {
            return map.computeIfAbsent(src, (k) -> {
                return new LuaTable();
            }).get(key);
        }
    }

    @Override
    public LuaValue setmetatable(LuaValue metatable) {
        LuaThread lt = AdvancedMacros.globals.getCurrentLuaThread();
        String src = LuaDebug.getSourceName(lt);

        synchronized (src) {
            return map.computeIfAbsent(src, (k) -> {
                return new LuaTable();
            }).setmetatable(metatable);
        }
    }

    @Override
    public LuaValue getmetatable() {
        LuaThread lt = AdvancedMacros.globals.getCurrentLuaThread();
        String src = LuaDebug.getSourceName(lt);

        synchronized (src) {
            return map.computeIfAbsent(src, (k) -> {
                return new LuaTable();
            }).getmetatable();
        }
    }
}
