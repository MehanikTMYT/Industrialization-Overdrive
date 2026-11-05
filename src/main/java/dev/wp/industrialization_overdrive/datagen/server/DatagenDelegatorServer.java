package dev.wp.industrialization_overdrive.datagen.server;

import dev.wp.industrialization_overdrive.datagen.server.provider.datamaps.DataMapDatagenProvider;
import dev.wp.industrialization_overdrive.datagen.server.provider.loottable.BlockLootTableDatagenProvider;
import dev.wp.industrialization_overdrive.datagen.server.provider.recipes.CommonRecipesServerDatagenProvider;
import dev.wp.industrialization_overdrive.datagen.server.provider.recipes.MachineItemRecipesServerDatagenProvider;
import dev.wp.industrialization_overdrive.datagen.server.provider.recipes.PyrolyseOvenRecipes;
import dev.wp.industrialization_overdrive.datagen.server.provider.tags.BlockTagDatagenProvider;
import dev.wp.industrialization_overdrive.datagen.server.provider.tags.ItemTagDatagenProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class DatagenDelegatorServer {
    public static void configure(GatherDataEvent event) {
        add(event, DataMapDatagenProvider::new);

        addLootTable(event, BlockLootTableDatagenProvider::new);

        event.getGenerator().addProvider(true, new net.minecraft.data.recipes.RecipeProvider.Runner(event.getGenerator().getPackOutput(), event.getLookupProvider()) {
            @Override protected net.minecraft.data.recipes.RecipeProvider createRecipeProvider(HolderLookup.Provider registries, net.minecraft.data.recipes.RecipeOutput output) {
                return new net.minecraft.data.recipes.RecipeProvider(registries, output) {
                    @Override protected void buildRecipes() {
                        new PyrolyseOvenRecipes(registries, output).generate();
                        new CommonRecipesServerDatagenProvider(registries, output).generate();
                        new MachineItemRecipesServerDatagenProvider(registries, output).generate();
                    }
                };
            }
            @Override public String getName() { return "Overdrive recipes"; }
        });

        add(event, BlockTagDatagenProvider::new);
        add(event, ItemTagDatagenProvider::new);
    }

    private static void add(GatherDataEvent event, Function<GatherDataEvent, DataProvider> providerCreator) {
        event.getGenerator().addProvider(true, providerCreator.apply(event));
    }

    private static void addLootTable(GatherDataEvent event, Function<HolderLookup.Provider, LootTableSubProvider> providerCreator) {
        event.getGenerator().addProvider(
                true,
                new LootTableProvider(
                        event.getGenerator().getPackOutput(),
                        Set.of(),
                        List.of(new LootTableProvider.SubProviderEntry(providerCreator, LootContextParamSets.BLOCK)),
                        event.getLookupProvider()
                )
        );
    }
}
