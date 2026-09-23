package de.chefexperte.farmersCreate.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.create.content.kinetics.deployer.BeltDeployerCallbacks;
import com.zurrtum.create.content.kinetics.deployer.DeployerBlockEntity;
import com.zurrtum.create.content.kinetics.deployer.ItemApplicationRecipe;
import de.chefexperte.farmersCreate.FarmersCreateConfig;
import de.chefexperte.farmersCreate.GeneratedRecipes;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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
}
