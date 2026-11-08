package dev.wp.industrialization_overdrive.mixin;

import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.wp.industrialization_overdrive.machines.blockentities.hatches.MEHatchInventoryAccess;
import net.swedz.tesseract.neoforge.compat.mi.helper.CrafterComponentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Tesseract's item-output placement sizes a slot as maxStackSize - amount; use the slot's own capacity on uncapped ME hatch slots. */
@Mixin(value = CrafterComponentHelper.class, remap = false)
public abstract class CrafterComponentHelperUncappedOutputMixin {
    // The private overload that does the placement.
    @WrapOperation(method = "putItemOutputs(Laztech/modern_industrialization/machines/recipe/MachineRecipe;ZZLnet/swedz/tesseract/neoforge/compat/mi/helper/CrafterComponentHelper$CommonBehavior;Laztech/modern_industrialization/machines/components/CrafterComponent$Inventory;I)Z",
            at = @At(value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/transfer/item/ItemResource;getMaxStackSize()I"))
    private static int io_uncappedMaxStackSize(ItemResource variant, Operation<Integer> original,
            @Local ConfigurableItemStack stack) {
        return MEHatchInventoryAccess.uncappedMaxStackSize(stack, original.call(variant));
    }
}
