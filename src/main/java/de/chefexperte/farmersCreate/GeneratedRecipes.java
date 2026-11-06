package de.chefexperte.farmersCreate;

import com.google.common.collect.MapMaker;
import net.minecraft.world.item.crafting.Recipe;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import java.util.Map;

public final class GeneratedRecipes {

    private static final Map<Recipe<?>, CuttingBoardRecipe> cutting = new MapMaker().weakKeys().makeMap();

    private GeneratedRecipes() {
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
