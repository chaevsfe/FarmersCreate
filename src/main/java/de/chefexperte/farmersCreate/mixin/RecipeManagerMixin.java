package de.chefexperte.farmersCreate.mixin;

import com.zurrtum.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.zurrtum.create.content.kinetics.mixer.MixingRecipe;
import com.zurrtum.create.content.processing.recipe.HeatCondition;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.content.processing.recipe.SizedIngredient;
import de.chefexperte.farmersCreate.FarmersCreate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Shadow
    private RecipeMap recipes;

    @Inject(at = @At("RETURN"), method = "apply(Lnet/minecraft/world/item/crafting/RecipeMap;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V")
    private void farmerscreate$addCreateRecipes(RecipeMap recipeMap, ResourceManager resourceManager, ProfilerFiller profilerFiller, CallbackInfo ci) {
        var allRecipes = new ArrayList<>(recipeMap.values());
        // Get Farmer's Delight Cutting Board Recipes
        Collection<RecipeHolder<CuttingBoardRecipe>> cuttingBoardRecipes = recipeMap.byType(ModRecipeTypes.CUTTING.get()).stream().toList();
        FarmersCreate.LOGGER.debug("Found {} Farmer's Delight cutting board recipes", cuttingBoardRecipes.size());
        for (var cuttingBoardRecipeHolder : cuttingBoardRecipes) {
            var cuttingBoardRecipe = cuttingBoardRecipeHolder.value();
            var processingOutputs = cuttingBoardRecipe.getRollableResults().stream().map(
                    result -> new ProcessingOutput(result.stack().item(), result.stack().count(), result.stack().components(), result.chance())).toList();
            var cuttingRecipe = new DeployerApplicationRecipe(processingOutputs, true, cuttingBoardRecipe.getInput(), cuttingBoardRecipe.getTool());
            var namespace = cuttingBoardRecipeHolder.id().identifier().getNamespace();
            var path = cuttingBoardRecipeHolder.id().identifier().getPath();
            var newId = Identifier.fromNamespaceAndPath(FarmersCreate.MOD_ID, "cutting/" + namespace + "/" + path);
            var key = ResourceKey.create(Registries.RECIPE, newId);
            var recipeHolder = new RecipeHolder<>(key, cuttingRecipe);
            allRecipes.add(recipeHolder);
        }
        // Get Farmer's Delight Cooking Recipes
        Collection<RecipeHolder<CookingPotRecipe>> cookingRecipes = recipeMap.byType(ModRecipeTypes.COOKING.get()).stream().toList();
        FarmersCreate.LOGGER.debug("Found {} Farmer's Delight cooking pot recipes", cookingRecipes.size());
        for (var cookingRecipeHolder : cookingRecipes) {
            var cookingRecipe = cookingRecipeHolder.value();
            var ingredients = SizedIngredient.of(cookingRecipe.input());
            var time = cookingRecipe.getCookTime();
            var result = cookingRecipe.result();
            var output = List.of(new ProcessingOutput(result.item(), result.count(), result.components(), 1));
            var mixingRecipe = new MixingRecipe(time, output, List.of(), HeatCondition.HEATED, new ArrayList<>(), ingredients);
            var namespace = cookingRecipeHolder.id().identifier().getNamespace();
            var path = cookingRecipeHolder.id().identifier().getPath();
            var newId = Identifier.fromNamespaceAndPath(FarmersCreate.MOD_ID, "cooking/" + namespace + "/" + path);
            var key = ResourceKey.create(Registries.RECIPE, newId);
            var recipeHolder = new RecipeHolder<>(key, mixingRecipe);
            allRecipes.add(recipeHolder);
        }
        this.recipes = RecipeMap.create(allRecipes);
    }
}
