# Java inventory: master 8a97e1d

Все 52 исходника сохранены. Это инвентарь миграции, не список завершённых портов.

| Файл | Обнаруженные области миграции |
|---|---|
| `dev/wp/industrialization_overdrive/IO.java` | Tesseract MI bridge отсутствует; ResourceLocation → Identifier |
| `dev/wp/industrialization_overdrive/IOBlocks.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/IOClient.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/IOComponents.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/IOConfig.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/IODataMaps.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/IOItems.java` | Tesseract MI capabilities отсутствует |
| `dev/wp/industrialization_overdrive/IOMachines.java` | Tesseract MI bridge отсутствует; ResourceLocation → Identifier |
| `dev/wp/industrialization_overdrive/IOOtherRegistries.java` | GrandPower API требует замены |
| `dev/wp/industrialization_overdrive/IOSortOrder.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/IOTags.java` | ResourceLocation → Identifier |
| `dev/wp/industrialization_overdrive/IOText.java` | Tesseract MI bridge отсутствует |
| `dev/wp/industrialization_overdrive/IOTooltips.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/IOUtil.java` | EI-интеграция 1.21.1 |
| `dev/wp/industrialization_overdrive/client/MultiblockBuilderRenderer.java` | новый rendering API |
| `dev/wp/industrialization_overdrive/compat/AE2Integration.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/compat/EIIntegration.java` | EI-интеграция 1.21.1; ValueInput/ValueOutput; ValueInput/ValueOutput |
| `dev/wp/industrialization_overdrive/compat/IOAUPlugin.java` | AlmostUnified API; ResourceLocation → Identifier |
| `dev/wp/industrialization_overdrive/compat/mi/IOMIHookListener.java` | Tesseract MI bridge отсутствует; ResourceLocation → Identifier |
| `dev/wp/industrialization_overdrive/compat/mi/IOMIHookRegistry.java` | Tesseract MI bridge отсутствует |
| `dev/wp/industrialization_overdrive/datagen/DatagenDelegator.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/datagen/client/DatagenDelegatorClient.java` | Tesseract MI datagen отсутствует |
| `dev/wp/industrialization_overdrive/datagen/client/provider/LanguageDatagenProvider.java` | Tesseract MI datagen отсутствует |
| `dev/wp/industrialization_overdrive/datagen/client/provider/models/BlockModelsDatagenProvider.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/datagen/client/provider/models/ItemModelsDatagenProvider.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/datagen/server/DatagenDelegatorServer.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/datagen/server/provider/datamaps/DataMapDatagenProvider.java` | ResourceLocation → Identifier |
| `dev/wp/industrialization_overdrive/datagen/server/provider/loottable/BlockLootTableDatagenProvider.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/datagen/server/provider/recipes/CommonRecipesServerDatagenProvider.java` | Tesseract MI bridge отсутствует |
| `dev/wp/industrialization_overdrive/datagen/server/provider/recipes/MachineItemRecipesServerDatagenProvider.java` | Tesseract MI bridge отсутствует |
| `dev/wp/industrialization_overdrive/datagen/server/provider/recipes/PyrolyseOvenRecipes.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/datagen/server/provider/recipes/RecipesServerDatagenProvider.java` | Tesseract MI bridge отсутствует |
| `dev/wp/industrialization_overdrive/datagen/server/provider/tags/BlockTagDatagenProvider.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/datagen/server/provider/tags/ItemTagDatagenProvider.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/datamap/PyrolyseOvenTier.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/item/MultiblockBuilder.java` | ValueInput/ValueOutput; ValueInput/ValueOutput |
| `dev/wp/industrialization_overdrive/item/UpgradeStacker.java` | InteractionResult API |
| `dev/wp/industrialization_overdrive/item/Vajra.java` | GrandPower API требует замены; InteractionResult API |
| `dev/wp/industrialization_overdrive/machines/blockentities/multiblock/MultiProcessingArrayBlockEntity.java` | Tesseract MI bridge отсутствует |
| `dev/wp/industrialization_overdrive/machines/blockentities/multiblock/PyrolyseOvenBlockEntity.java` | Tesseract MI bridge отсутствует; ResourceLocation → Identifier |
| `dev/wp/industrialization_overdrive/machines/components/craft/MultiProcessingArrayMachineComponent.java` | Tesseract MI bridge отсутствует; ResourceLocation → Identifier; ValueInput/ValueOutput; ValueInput/ValueOutput |
| `dev/wp/industrialization_overdrive/machines/guicomponents/multiprocessingarraymachineslot/MultiProcessingArrayMachineSlot.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/machines/guicomponents/multiprocessingarraymachineslot/MultiProcessingArrayMachineSlotClient.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/machines/recipe/PyrolyseOvenRecipeType.java` | ResourceLocation → Identifier |
| `dev/wp/industrialization_overdrive/mixin/AbstractElectricMultipliedCraftingMultiblockMixin.java` | Tesseract MI bridge отсутствует; InteractionResult API |
| `dev/wp/industrialization_overdrive/mixin/AbstractMultipliedCraftingMultiblockAccessor.java` | Tesseract MI bridge отсутствует |
| `dev/wp/industrialization_overdrive/mixin/SlotPanelAccessor.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/mixin/SlotPanelUpgradeStackerMixin.java` | Tesseract MI bridge отсутствует |
| `dev/wp/industrialization_overdrive/network/IOCustomPacket.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/network/IOPackets.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/network/packet/ModifyMultiblockBuilderModePacket.java` | требует проверки компиляцией и runtime |
| `dev/wp/industrialization_overdrive/network/packet/ModifyVajraSpeedPacket.java` | требует проверки компиляцией и runtime |
