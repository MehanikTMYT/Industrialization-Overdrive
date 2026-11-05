package dev.wp.industrialization_overdrive.datagen.server.provider.recipes;

import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import aztech.modern_industrialization.materials.Material;
import aztech.modern_industrialization.materials.part.PartTemplate;
import dev.wp.industrialization_overdrive.IO;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.tesseract.neoforge.compat.mi.recipe.MIMachineRecipeBuilder;
import net.swedz.tesseract.neoforge.helper.RecipeHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static aztech.modern_industrialization.materials.property.MaterialProperty.HARDNESS;

public abstract class RecipesServerDatagenProvider extends RecipeProvider {
    protected RecipesServerDatagenProvider(net.minecraft.core.HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    protected Ingredient ingredient(String maybeTag) {
        return RecipeHelper.ingredient(registries, maybeTag);
    }

    public final void generate() { buildRecipes(); }

    protected static boolean hasPart(Material material, PartTemplate part) {
        return material.getParts().containsKey(part.key());
    }

    protected void addMachineRecipe(String path, String name, MachineRecipeType recipeType, int eu, int duration, Consumer<MIMachineRecipeBuilder> recipeBuilder, RecipeOutput output) {
        MIMachineRecipeBuilder recipe = new MIMachineRecipeBuilder(registries, recipeType, eu, duration);
        recipeBuilder.accept(recipe);
        recipe.offerTo(output, IO.id(path + "/" + name));
    }

    protected void addMaterialMachineRecipe(Material material, String name, MachineRecipeType recipeType, int eu, int duration, Consumer<MIMachineRecipeBuilder> recipeBuilder, RecipeOutput output) {
        addMachineRecipe("materials/%s/%s".formatted(material.name, recipeType.getPath()), name, recipeType, eu, duration, recipeBuilder, output);
    }

    protected void addMaterialMachineRecipe(Material material, String name, MachineRecipeType recipeType, int eu, Consumer<MIMachineRecipeBuilder> recipeBuilder, RecipeOutput output) {
        addMaterialMachineRecipe(material, name, recipeType, eu, (int) (200 * material.get(HARDNESS).timeFactor), recipeBuilder, output);
    }

    @NotNull
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
