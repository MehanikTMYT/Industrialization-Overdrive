package dev.wp.industrialization_overdrive.datagen.client.provider.models;
import com.google.gson.*;
import dev.wp.industrialization_overdrive.IO;
import net.minecraft.data.*;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import java.util.*;
import java.util.concurrent.CompletableFuture;
/** Item definitions replace the removed model overrides API. */
public final class ItemModelsDatagenProvider implements DataProvider {
 private final PackOutput output;
 public ItemModelsDatagenProvider(GatherDataEvent event) { output=event.getGenerator().getPackOutput(); }
 private static JsonObject model(String name) { var json=new JsonObject();json.addProperty("type","minecraft:model");json.addProperty("model",IO.ID+":item/"+name);return json; }
 @Override public CompletableFuture<?> run(CachedOutput cache) {
  var definitions=new LinkedHashMap<String,JsonObject>();
  var terminal=new JsonObject();terminal.addProperty("type","minecraft:range_dispatch");terminal.addProperty("property",IO.ID+":mode");terminal.add("fallback",model("terminal"));
  var entries=new JsonArray();String[] modes={"terminal_copy_paste","terminal_tear_down"};
  for(int i=0;i<modes.length;i++){var entry=new JsonObject();entry.addProperty("threshold",i+1);entry.add("model",model(modes[i]));entries.add(entry);}terminal.add("entries",entries);definitions.put("terminal",terminal);
  var stacker=new JsonObject();stacker.addProperty("type","minecraft:condition");stacker.addProperty("property",IO.ID+":filled");stacker.add("on_true",model("upgrade_stacker_filled"));stacker.add("on_false",model("upgrade_stacker"));definitions.put("upgrade_stacker",stacker);definitions.put("vajra",model("vajra"));
  var paths=output.createPathProvider(PackOutput.Target.RESOURCE_PACK,"items");var futures=new ArrayList<CompletableFuture<?>>();
  definitions.forEach((name,model)->{var json=new JsonObject();json.add("model",model);futures.add(DataProvider.saveStable(cache,json,paths.json(IO.id(name))));});
  var vajra=new JsonObject();vajra.addProperty("parent","minecraft:item/handheld");var textures=new JsonObject();textures.addProperty("layer0",IO.ID+":item/vajra");vajra.add("textures",textures);
  futures.add(DataProvider.saveStable(cache,vajra,output.createPathProvider(PackOutput.Target.RESOURCE_PACK,"models/item").json(IO.id("vajra"))));
  return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
 }
 @Override public String getName() { return "Overdrive item definitions"; }
}
