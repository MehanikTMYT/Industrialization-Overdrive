package dev.wp.industrialization_overdrive.datagen.server.provider.datamaps;

import aztech.modern_industrialization.MI;
import dev.wp.industrialization_overdrive.IODataMaps;
import dev.wp.industrialization_overdrive.datamap.PyrolyseOvenTier;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

public final class DataMapDatagenProvider extends DataMapProvider {

    public DataMapDatagenProvider(GatherDataEvent event) {
        super(event.getGenerator().getPackOutput(), event.getLookupProvider());
    }

    @Override
    protected void gather(HolderLookup.@NotNull Provider provider) {
        this.addPyrolyseOvenTier(MI.id("cupronickel_coil"), 8, 1.0F);
        this.addPyrolyseOvenTier(MI.id("kanthal_coil"), 32, 0.75F);
    }

    private void addPyrolyseOvenTier(Identifier block, int batchSize, float euCostMultiplier) {
        this.builder(IODataMaps.PYROLYSE_OVEN_TIER).add(block, new PyrolyseOvenTier(batchSize, euCostMultiplier), false);
    }

    @NotNull
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
