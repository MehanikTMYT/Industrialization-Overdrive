package dev.wp.industrialization_overdrive;

import aztech.modern_industrialization.client.machines.MachineBlockEntityRenderer;
import aztech.modern_industrialization.guidebook.MultiblockShapeCompiler;
import aztech.modern_industrialization.client.machines.multiblocks.MultiblockMachineBER;
import aztech.modern_industrialization.client.machines.multiblocks.MultiblockTankBER;
import aztech.modern_industrialization.machines.MachineBlock;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.blockentities.multiblocks.LargeTankMultiblockBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import dev.wp.industrialization_overdrive.client.MultiblockBuilderRenderer;
import dev.wp.industrialization_overdrive.item.MultiblockBuilder;
import dev.wp.industrialization_overdrive.item.Vajra;
import dev.wp.industrialization_overdrive.network.packet.ModifyMultiblockBuilderModePacket;
import dev.wp.industrialization_overdrive.network.packet.ModifyVajraSpeedPacket;
import guideme.Guide;
import guideme.scene.element.SceneElementTagCompiler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.lwjgl.glfw.GLFW;

@Mod(value = IO.ID, dist = Dist.CLIENT)
public final class IOClient {
    public static final KeyMapping MULTIBLOCK_BUILDER_MODE_SWITCH = new KeyMapping(
            "key.industrialization_overdrive.terminal_mode_switch",
            GLFW.GLFW_KEY_V,
            KeyMapping.Category.register(IO.id("main"))
    );

    public IOClient(IEventBus bus) {
        // Синк 2026-11-06: гайд регистрируется только на клиенте — GuideME GuidebookLevel реализует
        // client-only BlockAndTintGetter и не грузится на dedicated server (как в MI: MIGuide из MIClient).
        setupGuide();
        bus.addListener(RegisterKeyMappingsEvent.class, event -> event.register(MULTIBLOCK_BUILDER_MODE_SWITCH));
        bus.addListener(net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent.class,
                event -> event.register(IO.id("mode"), BuilderModeProperty.CODEC));
        bus.addListener(net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent.class,
                event -> event.register(IO.id("filled"), StackerFilledProperty.CODEC));
        NeoForge.EVENT_BUS.addListener(net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent.class, MultiblockBuilderRenderer::extract);
        NeoForge.EVENT_BUS.addListener(net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent.class, MultiblockBuilderRenderer::submit);

        NeoForge.EVENT_BUS.addListener(InputEvent.Key.class, (event) -> {
            var player = Minecraft.getInstance().player;
            if (player != null && MULTIBLOCK_BUILDER_MODE_SWITCH.consumeClick()) {
                var stack = player.getMainHandItem();
                if (stack.getItem() == IOItems.MULTIBLOCK_BUILDER.get()) {
                    new ModifyMultiblockBuilderModePacket(true).sendToServer();
                }
            }
        });

        NeoForge.EVENT_BUS.addListener(InputEvent.MouseScrollingEvent.class, (event) -> {
            var player = Minecraft.getInstance().player;
            if (player != null && player.isShiftKeyDown()) {
                var stack = player.getMainHandItem();
                if (stack.getItem() == IOItems.VAJRA.get()) {
                    boolean increase = event.getScrollDeltaY() > 0;
                    Vajra.Speed speed = Vajra.getToolSpeed(stack);
                    if (increase ? speed != Vajra.Speed.INSTANT : speed != Vajra.Speed.SLOW) {
                        new ModifyVajraSpeedPacket(increase).sendToServer();
                    }

                    event.setCanceled(true);
                }
            }
        });
    }

    private static void setupGuide() {
        var guide = Guide.builder(IO.id("guide"))
                .extension(SceneElementTagCompiler.EXTENSION_POINT, new MultiblockShapeCompiler());
        // // TesseractGuideMETags.includeIn(guide);
        guide.build();
    }

    public record BuilderModeProperty() implements net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty {
        public static final com.mojang.serialization.MapCodec<BuilderModeProperty> CODEC = com.mojang.serialization.MapCodec.unit(new BuilderModeProperty());
        @Override public float get(net.minecraft.world.item.ItemStack stack, net.minecraft.client.multiplayer.ClientLevel level, net.minecraft.world.entity.ItemOwner owner, int seed) {
            return stack.getOrDefault(IOComponents.MULTI_BUILDER_MODE, MultiblockBuilder.Mode.BUILD).ordinal();
        }
        @Override public com.mojang.serialization.MapCodec<BuilderModeProperty> type() { return CODEC; }
    }
    public record StackerFilledProperty() implements net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty {
        public static final com.mojang.serialization.MapCodec<StackerFilledProperty> CODEC = com.mojang.serialization.MapCodec.unit(new StackerFilledProperty());
        @Override public boolean get(net.minecraft.world.item.ItemStack stack, net.minecraft.client.multiplayer.ClientLevel level, net.minecraft.world.entity.LivingEntity entity, int seed, net.minecraft.world.item.ItemDisplayContext context) {
            var contents=stack.get(IOComponents.UPGRADE_STACKER_CONTENTS.get()); return contents!=null && contents.count()>0;
        }
        @Override public com.mojang.serialization.MapCodec<StackerFilledProperty> type() { return CODEC; }
    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = IO.ID)
    public static final class Renderers {
        @SubscribeEvent
        private static void registerBlockEntityRenderers(FMLClientSetupEvent event) {
            for (DeferredHolder<Block, ? extends Block> blockDef : IOBlocks.Registry.BLOCKS.getEntries()) {
                if (blockDef.get() instanceof MachineBlock machine) {
                    MachineBlockEntity blockEntity = machine.getBlockEntityInstance();
                    BlockEntityType type = blockEntity.getType();

                    BlockEntityRendererProvider provider = switch (blockEntity) {
                        case LargeTankMultiblockBlockEntity ignored -> MultiblockTankBER::new;
                        case MultiblockMachineBlockEntity ignored -> MultiblockMachineBER::new;
                        default -> MachineBlockEntityRenderer::new;
                    };
                    BlockEntityRenderers.register(type, provider);
                }
            }
        }
    }
}
