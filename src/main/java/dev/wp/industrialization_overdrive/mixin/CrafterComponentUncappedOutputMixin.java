package dev.wp.industrialization_overdrive.mixin;

import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.machines.components.CrafterComponent;
import net.neoforged.neoforge.transfer.item.ItemResource;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.wp.industrialization_overdrive.machines.blockentities.hatches.MEHatchInventoryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * MI's second {@code putItemOutputs} pass sizes room as {@code maxStackSize - amount}, which goes negative on an
 * uncapped ME hatch slot holding over a stack and over-delivers to later slots; use the slot's own capacity instead.
 */
@Mixin(value = CrafterComponent.class, remap = false)
public abstract class CrafterComponentUncappedOutputMixin {
    // PORT26 QA: bare-имя давало "Scanned 0 target(s)" — указываем полный дескриптор (как в соседнем Helper-миксине).
    @WrapOperation(method = "putItemOutputs(Laztech/modern_industrialization/machines/recipe/MachineRecipe;ZZ)Z",
            at = @At(value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/transfer/item/ItemResource;getMaxStackSize()I"))
    private int io_uncappedMaxStackSize(ItemResource variant, Operation<Integer> original,
            @Local ConfigurableItemStack stack) {
        return MEHatchInventoryAccess.uncappedMaxStackSize(stack, original.call(variant));
    }
}
