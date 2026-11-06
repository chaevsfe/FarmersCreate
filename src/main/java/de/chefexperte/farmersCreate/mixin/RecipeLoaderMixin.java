package de.chefexperte.farmersCreate.mixin;

import com.zurrtum.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.zurrtum.create.content.kinetics.mixer.MixingRecipe;
import com.zurrtum.create.content.processing.RecipeLoader;
import com.zurrtum.create.content.processing.recipe.HeatCondition;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.content.processing.recipe.SizedIngredient;
import de.chefexperte.farmersCreate.FarmersCreate;
import de.chefexperte.farmersCreate.FarmersCreateConfig;
import de.chefexperte.farmersCreate.GeneratedRecipes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryLoadTask.PendingRegistration;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@Mixin(RecipeLoader.class)
public class RecipeLoaderMixin {

    @Inject(method = "createTask", at = @At("HEAD"))
    private static <V> void farmerscreate$clearGenerated(BiFunction<Identifier, Resource, V> operation, CallbackInfoReturnable<BiFunction<Identifier, Resource, V>> info) {
        GeneratedRecipes.clear();
    }

    @Inject(method = "onLoadRecipe", at = @At("HEAD"))
    private static void farmerscreate$addCreateRecipe(PendingRegistration<Recipe<?>> entry, Recipe<?> recipe, CallbackInfo info) {
        Identifier source = entry.key().identifier();
        if (recipe instanceof CuttingBoardRecipe cuttingBoardRecipe) {
            var processingOutputs = cuttingBoardRecipe.getRollableResults().stream().map(
                    result -> new ProcessingOutput(result.stack().item(), result.stack().count(), result.stack().components(), result.chance())).toList();
            var cuttingRecipe = new DeployerApplicationRecipe(processingOutputs, true, cuttingBoardRecipe.getInput(), cuttingBoardRecipe.getTool());
            GeneratedRecipes.addCutting(cuttingRecipe, cuttingBoardRecipe);
            RecipeLoader.addRecipe(Identifier.fromNamespaceAndPath(FarmersCreate.MOD_ID, "cutting/" + source.getNamespace() + "/" + source.getPath()), cuttingRecipe);
        } else if (recipe instanceof CookingPotRecipe cookingRecipe) {
            var ingredients = new ArrayList<>(SizedIngredient.of(cookingRecipe.input()));
            var result = cookingRecipe.result();
            if (FarmersCreateConfig.get().requireContainer) {
                farmerscreate$servingContainer(cookingRecipe).ifPresent(
                        container -> ingredients.add(new SizedIngredient(Ingredient.of(HolderSet.direct(container)), result.count())));
            }
            var output = List.of(new ProcessingOutput(result.item(), result.count(), result.components(), 1));
            var mixingRecipe = new MixingRecipe(cookingRecipe.getCookTime(), output, List.of(), HeatCondition.HEATED, new ArrayList<>(), ingredients);
            RecipeLoader.addRecipe(Identifier.fromNamespaceAndPath(FarmersCreate.MOD_ID, "cooking/" + source.getNamespace() + "/" + source.getPath()), mixingRecipe);
        }
    }

    @Unique
    private static Optional<Holder<Item>> farmerscreate$servingContainer(CookingPotRecipe recipe) {
        Optional<ItemStackTemplate> override = recipe.containerOverride();
        Holder<Item> container;
        if (override.isPresent()) {
            container = override.get().item();
        } else {
            Item meal = recipe.result().item().value();
            ItemStackTemplate remainder = meal.getCraftingRemainder();
            if (remainder != null) {
                container = remainder.item();
            } else {
                Item fallback = CookingPotBlockEntity.INGREDIENT_REMAINDER_OVERRIDES.get(meal);
                if (fallback == null) {
                    return Optional.empty();
                }
                container = BuiltInRegistries.ITEM.wrapAsHolder(fallback);
            }
        }
        if (container.value() == Items.AIR) {
            return Optional.empty();
        }
        return Optional.of(container);
    }
}
