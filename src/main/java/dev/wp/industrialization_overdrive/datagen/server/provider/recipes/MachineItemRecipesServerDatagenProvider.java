package dev.wp.industrialization_overdrive.datagen.server.provider.recipes;

import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.MIItem;
import aztech.modern_industrialization.materials.MIMaterials;
import aztech.modern_industrialization.materials.part.MIParts;
import dev.wp.industrialization_overdrive.IO;
import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.tesseract.neoforge.compat.mi.recipe.MIMachineRecipeBuilder;
import net.swedz.tesseract.neoforge.compat.vanilla.recipe.ShapedRecipeBuilder;
import net.swedz.tesseract.neoforge.compat.vanilla.recipe.ShapelessRecipeBuilder;

import java.util.function.Consumer;

public final class MachineItemRecipesServerDatagenProvider extends RecipesServerDatagenProvider {
    public MachineItemRecipesServerDatagenProvider(net.minecraft.core.HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    private static String machine(String machine, String tier) {
        return "%s:%s".formatted(IO.ID, tier == null ? machine : "%s_%s".formatted(tier, machine));
    }

    private static String machineBronze(String machine) {
        return machine(machine, "bronze");
    }

    private static String machineSteel(String machine) {
        return machine(machine, "steel");
    }

    private void addBasicCraftingMachineRecipes(String machineName, String machineTier, Consumer<ShapedRecipeBuilder> crafting, RecipeOutput output) {
        String recipeId = machineTier == null ? "" : "/%s".formatted(machineTier);

        ShapedRecipeBuilder shapedRecipeBuilder = new ShapedRecipeBuilder(registries);
        crafting.accept(shapedRecipeBuilder);
        shapedRecipeBuilder.output(machine(machineName, machineTier), 1);
        shapedRecipeBuilder.offerTo(output, IO.id("machines/%s/craft%s".formatted(machineName, recipeId)));

        MIMachineRecipeBuilder.fromShapedToAssembler(shapedRecipeBuilder).offerTo(output, IO.id("machines/%s/assembler%s".formatted(machineName, recipeId)));
    }

    private void addBasicCraftingMachineRecipes(String machineName, Consumer<ShapedRecipeBuilder> crafting, RecipeOutput output) {
        addBasicCraftingMachineRecipes(machineName, null, crafting, output);
    }

    private void addBronzeMachineRecipes(String machine, Consumer<ShapedRecipeBuilder> crafting, RecipeOutput output) {
        addBasicCraftingMachineRecipes(machine, "bronze", crafting, output);
    }

    private void addSteelMachineRecipes(String machine, Consumer<ShapedRecipeBuilder> crafting, RecipeOutput output) {
        addBasicCraftingMachineRecipes(machine, "steel", crafting, output);
    }

    private void addElectricMachineRecipes(String machine, Consumer<ShapedRecipeBuilder> crafting, RecipeOutput output) {
        addBasicCraftingMachineRecipes(machine, "electric", crafting, output);
    }

    private void addSteelUpgradeMachineRecipes(String machine, RecipeOutput output) {
        ShapelessRecipeBuilder shapelessRecipeBuilder = new ShapelessRecipeBuilder(registries)
                .with(machineBronze(machine))
                .with(MIItem.STEEL_UPGRADE)
                .output(machineSteel(machine), 1);
        shapelessRecipeBuilder.offerTo(output, IO.id("machines/%s/craft/upgrade_steel".formatted(machine)));

        MIMachineRecipeBuilder.fromShapelessToPacker(shapelessRecipeBuilder).offerTo(output, IO.id("machines/%s/packer/upgrade_steel".formatted(machine)));

        MIMachineRecipeBuilder.fromShapelessToUnpackerAndFlip(shapelessRecipeBuilder).offerTo(output, IO.id("machines/%s/unpacker/downgrade_steel".formatted(machine)));
    }

    private void addBronzeAndSteelMachineRecipes(String machine, Consumer<ShapedRecipeBuilder> crafting, RecipeOutput output) {
        addBronzeMachineRecipes(machine, crafting, output);
        addSteelUpgradeMachineRecipes(machine, output);
    }

    private void multiProcessingArray(RecipeOutput output) {
        addBasicCraftingMachineRecipes(
                "multi_processing_array",
                (builder) -> builder
                        .define('A', MIItem.LARGE_ADVANCED_MOTOR)
                        .define('D', MIItem.PROCESSING_UNIT)
                        .define('C', MIMaterials.TITANIUM.getPart(MIParts.MACHINE_CASING_SPECIAL))
                        .define('S', "modern_industrialization:assembler")
                        .pattern("ADA")
                        .pattern("CSC")
                        .pattern("ADA"),
                output
        );
    }

    private void pyrolyseOven(RecipeOutput output) {
        addBasicCraftingMachineRecipes(
                "pyrolyse_oven",
                (builder) -> builder
                        .define('H', MIBlock.ADVANCED_MACHINE_HULL)
                        .define('C', MIItem.ELECTRONIC_CIRCUIT)
                        .define('B', MIMaterials.BRONZE.getPart(MIParts.MACHINE_CASING_SPECIAL))
                        .define('O', "modern_industrialization:coke_oven")
                        .define('M', MIMaterials.CUPRONICKEL.getPart(MIParts.WIRE_MAGNETIC))
                        .pattern("MCM")
                        .pattern("OHO")
                        .pattern("BCB"),
                output
        );
    }

    @Override
    protected void buildRecipes() {
        multiProcessingArray(output);
        pyrolyseOven(output);
    }
}
