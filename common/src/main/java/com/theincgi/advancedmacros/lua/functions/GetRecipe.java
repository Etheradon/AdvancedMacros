package com.theincgi.advancedmacros.lua.functions;

import com.theincgi.advancedmacros.misc.CallableTable;
import com.theincgi.advancedmacros.misc.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.Registries;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;

public class GetRecipe extends CallableTable {

    public static final String GUI_NAME = "guiName";

    public GetRecipe() {
        super(new String[]{"getRecipe"}, new OneArgFunction() {

            @Override
            public LuaValue call(LuaValue arg) {
                LuaTable types = new LuaTable();
                MinecraftClient.getInstance().world.getRecipeManager().values().forEach(r -> {
                    LuaValue v = types.get(r.value().getType().toString()); //holds recipes of some type, like furnace recpies
                    if (v.isnil()) {
                        types.set(r.value().getType().toString(), v = new LuaTable());
                    }
                    if (arg.isnil() || Registries.ITEM.getId(r.value().getResult(null).getItem()).toString().contains(arg.checkjstring())) { //no search item
                        ItemStack output = r.value().getResult(null);
                        LuaTable pair = new LuaTable();
                        pair.set("in", recipeInputs(r.value()));
                        pair.set("out", Utils.itemStackToLuatable(output));
                        v.set(v.length() + 1, pair);
                    } else {

                    }

                });

                return types;
            }

            private LuaValue recipeInputs(Recipe<?> r) {
                LuaTable inputs;
                if (r instanceof ShapedRecipe) {
                    ShapedRecipe sr = (ShapedRecipe) r;
                    inputs = dimTable(sr.getWidth(), sr.getHeight());
                    for (int y = 0; y < sr.getHeight(); y++) {
                        for (int x = 0; x < sr.getWidth(); x++) {
                            Ingredient i = r.getIngredients().get(y * sr.getWidth() + x);
                            setIngredient(inputs, x + 1, y + 1, i);
                        }
                    }
                } else {
                    inputs = new LuaTable();
                    for (int i = 0; i < r.getIngredients().size(); i++) {
                        inputs.set(i + 1, optionsToTable(r.getIngredients().get(i).getMatchingStacks()));
                    }
                }
                return inputs;
            }
        });

    }

    private static void setIngredient(LuaTable t, int x, int y, Ingredient i) {
        LuaTable temp = t.get(x).checktable();
        temp.set(y, optionsToTable(i.getMatchingStacks()));
    }

    private static LuaTable optionsToTable(ItemStack[] opts) {
        LuaTable out = new LuaTable();
        int i = 1;
        for (ItemStack itemStack : opts) {
            out.set(i++, Utils.itemStackToLuatable(itemStack));
        }
        return out;
    }

    //indexed x,y, itemOption
    private static LuaTable dimTable(int width, int height) {
        LuaTable out = new LuaTable();
        for (int x = 1; x <= width; x++) {
            LuaTable tmp = new LuaTable();
            for (int y = 1; y <= height; y++) {
                tmp.set(y, new LuaTable());
            }
            out.set(x, tmp);
        }
        return out;
    }

}
