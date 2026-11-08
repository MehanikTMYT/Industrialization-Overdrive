package dev.wp.industrialization_overdrive.mixin.client;

import aztech.modern_industrialization.client.machines.gui.MachineMenuClient;
import aztech.modern_industrialization.client.machines.gui.MachineScreen;
import aztech.modern_industrialization.client.screen.MIContainerScreen;
import aztech.modern_industrialization.inventory.BackgroundRenderedSlot;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.wp.industrialization_overdrive.machines.guicomponents.mehatch.MEHatchGuiClient;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * AE2-style rendering of ME hatch slots on MI's machine screen; inert for every other machine.
 *
 * <p>PORT26: {@code renderSlotHighlight(GuiGraphics, Slot, ...)} is gone — vanilla split the hover highlight into the
 * private {@code extractSlotHighlightBack/Front} on {@code AbstractContainerScreen}, which this mixin (targeting
 * {@link MachineScreen}) cannot reach, so the AE2-style hover border is dropped and vanilla's highlight stays. Slot
 * content rendering is unchanged.
 */
@Mixin(MachineScreen.class)
public abstract class MachineScreenMEHatchMixin extends MIContainerScreen<MachineMenuClient> {
    private MachineScreenMEHatchMixin(MachineMenuClient menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Inject(method = "extractSlot", at = @At("HEAD"), cancellable = true, remap = false)
    private void io_renderMEHatchSlot(GuiGraphicsExtractor graphics, Slot slot, int x, int y, CallbackInfo ci) {
        MachineScreen self = (MachineScreen) (Object) this;
        if (!MEHatchGuiClient.isHandledSlot(self.getMenu(), slot)) {
            return;
        }
        ci.cancel();
        MEHatchGuiClient.renderSlot(graphics, self.getMenu(), slot);
    }

    @WrapOperation(method = "renderConfigurableSlotBackgrounds", remap = false,
            at = @At(value = "INVOKE",
                    target = "Laztech/modern_industrialization/inventory/BackgroundRenderedSlot;getBackgroundAtlasLocation()Lnet/minecraft/resources/Identifier;"))
    private Identifier io_meHatchSlotBackground(BackgroundRenderedSlot slot, Operation<Identifier> original) {
        MachineScreen self = (MachineScreen) (Object) this;
        return MEHatchGuiClient.backgroundAtlas(self.getMenu(), slot, original.call(slot));
    }
}
