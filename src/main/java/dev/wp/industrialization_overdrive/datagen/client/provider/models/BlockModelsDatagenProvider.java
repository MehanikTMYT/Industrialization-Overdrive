package dev.wp.industrialization_overdrive.datagen.client.provider.models;
import dev.wp.industrialization_overdrive.IO;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.tesseract.neoforge.datagen.mi.client.MachineCasingModelsMIHookDatagenProvider;
import java.util.concurrent.CompletableFuture;
/** Native MI casing, machine blockstate and machine item definitions. */
public final class BlockModelsDatagenProvider implements DataProvider {
 private final MachineCasingModelsMIHookDatagenProvider delegate;
 public BlockModelsDatagenProvider(GatherDataEvent event) { delegate=new MachineCasingModelsMIHookDatagenProvider(event,IO.ID); }
 @Override public CompletableFuture<?> run(CachedOutput output) { return delegate.run(output); }
 @Override public String getName() { return "Overdrive machine models"; }
}
