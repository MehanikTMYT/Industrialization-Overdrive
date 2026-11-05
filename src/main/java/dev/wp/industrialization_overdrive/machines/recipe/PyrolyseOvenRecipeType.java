package dev.wp.industrialization_overdrive.machines.recipe;

import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.ProxyableMachineRecipeType;
import dev.wp.industrialization_overdrive.IO;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.Comparator;
import java.util.List;

public class PyrolyseOvenRecipeType extends ProxyableMachineRecipeType {
    public PyrolyseOvenRecipeType(Identifier id) {
        super(id);
    }

    @Override
    protected void fillRecipeList(Level world, List<RecipeHolder<MachineRecipe>> recipeList) {
        recipeList.addAll(getManagerRecipes(world));
        for (var cokeOvenRecipe : MIMachineRecipeTypes.COKE_OVEN.getRecipesWithoutCache(world)) {
            Identifier id = IO.id("pyrolyse_oven/generated/%s/%s".formatted(cokeOvenRecipe.id().identifier().getNamespace(), cokeOvenRecipe.id().identifier().getPath()));
            recipeList.add(new RecipeHolder<>(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, id), cokeOvenRecipe.value()));
        }

        recipeList.sort(Comparator.comparing(r -> r.id().identifier().getNamespace().equals(IO.ID) ? 0 : 1));
    }
}
