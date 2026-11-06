package de.chefexperte.farmersCreate;

import net.minecraft.world.item.crafting.Recipe;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

public final class GeneratedRecipes {

    private static final Map<Recipe<?>, CuttingBoardRecipe> cutting = Collections.synchronizedMap(new IdentityHashMap<>());

    private GeneratedRecipes() {
    }

    public static void clear() {
        cutting.clear();
    }

    public static void addCutting(Recipe<?> recipe, CuttingBoardRecipe source) {
        cutting.put(recipe, source);
    }

    public static CuttingBoardRecipe cuttingSource(Recipe<?> recipe) {
        return recipe == null ? null : cutting.get(recipe);
    }

    public static boolean isCutting(Recipe<?> recipe) {
        return cuttingSource(recipe) != null;
    }
}
