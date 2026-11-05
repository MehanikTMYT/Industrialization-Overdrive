package dev.wp.industrialization_overdrive.datagen.client;

import dev.wp.industrialization_overdrive.IO;
import dev.wp.industrialization_overdrive.datagen.client.provider.LanguageDatagenProvider;
import dev.wp.industrialization_overdrive.datagen.client.provider.models.BlockModelsDatagenProvider;
import dev.wp.industrialization_overdrive.datagen.client.provider.models.ItemModelsDatagenProvider;
import net.minecraft.data.DataProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.tesseract.neoforge.datagen.mi.MIDatagenHooks;

import java.util.function.Function;

public final class DatagenDelegatorClient {
    public static void configure(GatherDataEvent event) {
        MIDatagenHooks.Client.includeMISprites(event);

        // BlockModelsDatagenProvider delegates casing and machine definitions to the native MI hook.

        add(event, BlockModelsDatagenProvider::new);
        add(event, ItemModelsDatagenProvider::new);
        add(event, LanguageDatagenProvider::new);
    }

    private static void add(GatherDataEvent event, Function<GatherDataEvent, DataProvider> providerCreator) {
        event.getGenerator().addProvider(true, providerCreator.apply(event));
    }
}
