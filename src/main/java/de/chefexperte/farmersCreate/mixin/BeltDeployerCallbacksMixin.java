package de.chefexperte.farmersCreate.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.create.content.kinetics.deployer.BeltDeployerCallbacks;
import com.zurrtum.create.content.kinetics.deployer.DeployerBlockEntity;
import com.zurrtum.create.content.kinetics.deployer.ItemApplicationInput;
import com.zurrtum.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.zurrtum.create.foundation.recipe.CreateRecipe;
import com.zurrtum.create.foundation.recipe.CreateRollableRecipe;
import de.chefexperte.farmersCreate.FarmersCreateConfig;
import de.chefexperte.farmersCreate.GeneratedRecipes;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import java.util.ArrayList;
import java.util.List;

@Mixin(BeltDeployerCallbacks.class)
public class BeltDeployerCallbacksMixin {

    @WrapOperation(
            method = "activate(Lcom/zurrtum/create/content/kinetics/belt/transport/TransportedItemStack;Lcom/zurrtum/create/content/kinetics/belt/behaviour/TransportedItemStackHandlerBehaviour;Lcom/zurrtum/create/content/kinetics/deployer/DeployerBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;)V",
            at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/content/kinetics/deployer/ItemApplicationRecipe;keepHeldItem()Z"))
    private static boolean farmerscreate$damageCuttingTool(ItemApplicationRecipe recipe, Operation<Boolean> original,
                                                          @Local(argsOnly = true) DeployerBlockEntity deployer) {
        boolean keepHeldItem = original.call(recipe);
        if (keepHeldItem && FarmersCreateConfig.get().damageTools && GeneratedRecipes.isCutting(recipe)) {
            ItemStack held = deployer.getPlayer().cast().getMainHandItem();
            if (held.isDamageableItem()) {
                return false;
            }
        }
        return keepHeldItem;
    }

    @WrapOperation(
            method = "activate(Lcom/zurrtum/create/content/kinetics/belt/transport/TransportedItemStack;Lcom/zurrtum/create/content/kinetics/belt/behaviour/TransportedItemStackHandlerBehaviour;Lcom/zurrtum/create/content/kinetics/deployer/DeployerBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;)V",
            at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/foundation/recipe/RecipeApplier;applyRecipeOn(Lnet/minecraft/util/RandomSource;ILnet/minecraft/world/item/crafting/RecipeInput;Lcom/zurrtum/create/foundation/recipe/CreateRollableRecipe;)Ljava/util/List;"))
    private static List<ItemStack> farmerscreate$rollLikeCuttingBoard(RandomSource random, int count, RecipeInput input, CreateRollableRecipe<?> recipe,
                                                                     Operation<List<ItemStack>> original,
                                                                     @Local(argsOnly = true) DeployerBlockEntity deployer) {
        CuttingBoardRecipe source = GeneratedRecipes.cuttingSource(recipe);
        if (source == null || !FarmersCreateConfig.get().farmersDelightRolls
                || !(input instanceof ItemApplicationInput application)
                || CreateRecipe.getJunk(application.target()) != null) {
            return original.call(random, count, input, recipe);
        }
        int fortune = EnchantmentHelper.getItemEnchantmentLevel(
                deployer.getLevel().holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE), application.ingredient());
        List<ItemStack> results = new ArrayList<>();
        for (int roll = 0; roll < count; roll++) {
            results.addAll(source.rollResults(random, fortune));
            for (int slot = 0; slot < input.size(); slot++) {
                ItemStackTemplate remainder = input.getItem(slot).getItem().getCraftingRemainder();
                if (remainder != null) {
                    ItemStack stack = remainder.create();
                    if (!stack.isEmpty()) {
                        results.add(stack);
                    }
                }
            }
        }
        return results;
    }
}
