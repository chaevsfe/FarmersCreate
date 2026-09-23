package de.chefexperte.farmersCreate;

import net.minecraft.world.item.crafting.Recipe;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

public final class GeneratedRecipes {

    private static volatile Map<Recipe<?>, CuttingBoardRecipe> cutting = Map.of();

    private GeneratedRecipes() {
    }

    public static void setCutting(IdentityHashMap<Recipe<?>, CuttingBoardRecipe> recipes) {
        cutting = Collections.unmodifiableMap(recipes);
    }

    public static CuttingBoardRecipe cuttingSource(Recipe<?> recipe) {
        return recipe == null ? null : cutting.get(recipe);
    }

    public static boolean isCutting(Recipe<?> recipe) {
        return cuttingSource(recipe) != null;
    }
}
