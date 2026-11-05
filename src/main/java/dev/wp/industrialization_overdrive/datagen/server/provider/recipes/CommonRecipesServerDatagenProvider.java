package dev.wp.industrialization_overdrive.datagen.server.provider.recipes;

import aztech.modern_industrialization.MIFluids;
import aztech.modern_industrialization.MIItem;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import dev.wp.industrialization_overdrive.IO;
import dev.wp.industrialization_overdrive.IOItems;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.tesseract.neoforge.compat.mi.recipe.MIMachineRecipeBuilder;
import net.swedz.tesseract.neoforge.compat.vanilla.recipe.ShapedRecipeBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class CommonRecipesServerDatagenProvider extends RecipesServerDatagenProvider {
    public CommonRecipesServerDatagenProvider(net.minecraft.core.HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    private void addBasicCraftingRecipes(String path, String name, boolean assembler, ItemLike result, int resultCount, Consumer<ShapedRecipeBuilder> crafting, RecipeOutput output) {
        ShapedRecipeBuilder shapedRecipeBuilder = new ShapedRecipeBuilder(registries);
        crafting.accept(shapedRecipeBuilder);
        shapedRecipeBuilder.output(result, resultCount);
        shapedRecipeBuilder.offerTo(output, IO.id(path + "/craft/" + name));

        if (assembler) {
            MIMachineRecipeBuilder.fromShapedToAssembler(shapedRecipeBuilder).offerTo(output, IO.id(path + "/craft/" + name + "/assembler"));
        }
    }


    @Override
    protected void buildRecipes() {
        addBasicCraftingRecipes(
                "shaped", "terminal", false,
                IOItems.MULTIBLOCK_BUILDER, 1,
                (r) -> r
                        .pattern("SGS")
                        .pattern("SES")
                        .define('S', "#c:plates/steel")
                        .define('G', Tags.Items.GLASS_PANES)
                        .define('E', MIItem.ELECTRONIC_CIRCUIT),
                output
        );

        addBasicCraftingRecipes(
                "shaped", "upgrade_stacker", false,
                IOItems.UPGRADE_STACKER, 1,
                (r) -> r
                        .pattern("GGG")
                        .pattern("ECA")
                        .pattern("AAA")
                        .define('G', Tags.Items.GLASS_PANES)
                        .define('E', MIItem.ELECTRONIC_CIRCUIT)
                        .define('A', "#c:plates/aluminum")
                        .define('C', Tags.Items.CHESTS_WOODEN),
                output
        );

        addMachineRecipe(
                "assembler", "vajra", MIMachineRecipeTypes.ASSEMBLER,
                32, 20 * 10,
                (r) -> r
                        .itemIn(MIItem.DIGITAL_CIRCUIT, 8)
                        .itemIn(MIItem.TURBO_UPGRADE, 8)
                        .itemIn(MIItem.ADVANCED_MOTOR, 8)
                        .itemIn("modern_industrialization:sodium_battery", 1)
                        .itemIn(MIItem.DIESEL_MINING_DRILL, 1)
                        .itemIn(MIItem.DIESEL_CHAINSAW, 1)
                        .itemIn(MIItem.OVERDRIVE_MODULE, 1)
                        .fluidIn(MIFluids.CRYOFLUID, 1000)
                        .itemOut(IOItems.VAJRA, 1),
                output
        );
    }
}
