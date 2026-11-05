package dev.wp.industrialization_overdrive.client;

import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.MachineBlock;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.wp.industrialization_overdrive.IOComponents;
import dev.wp.industrialization_overdrive.IOItems;
import dev.wp.industrialization_overdrive.item.MultiblockBuilder;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;


import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MultiblockBuilderRenderer {
    private static final int GREEN = 0x55CC55;
    private static final int RED = 0xDD5555;

    private record Preview(BlockPos pos, BlockState state, int tint, CompoundTag settings) {}

    private MultiblockBuilderRenderer() {}

    private record ExtractedPreview(BlockPos pos, int tint, List<BakedQuad> quads) {}
    private static final net.minecraft.util.context.ContextKey<List<ExtractedPreview>> PREVIEWS = new net.minecraft.util.context.ContextKey<>(dev.wp.industrialization_overdrive.IO.id("builder_previews"));

    public static void extract(ExtractLevelRenderStateEvent event) {
        var minecraft=Minecraft.getInstance();var level=event.getLevel();var player=minecraft.player;
        List<ExtractedPreview> extracted=new ArrayList<>();
        if(player!=null && minecraft.hitResult instanceof BlockHitResult hit && hit.getType()!=HitResult.Type.MISS) {
            var stack=heldBuilder(player);
            if(!stack.isEmpty()) for(var preview:previews(level,player,hit,stack)) {
                var parts=new ArrayList<BlockStateModelPart>();
                var model=minecraft.getModelManager().getBlockStateModelSet().get(preview.state());
                var data=modelData(level,preview);
                net.minecraft.client.renderer.block.BlockAndTintGetter view=new net.minecraft.client.renderer.block.BlockAndTintGetter() {
                    @Override public net.minecraft.world.level.CardinalLighting cardinalLighting(){return level.cardinalLighting();}
                    @Override public net.minecraft.world.level.lighting.LevelLightEngine getLightEngine(){return level.getLightEngine();}
                    @Override public int getBlockTint(BlockPos pos,net.minecraft.world.level.ColorResolver resolver){return level.getBlockTint(pos,resolver);}
                    @Override public net.minecraft.world.level.block.entity.BlockEntity getBlockEntity(BlockPos pos){return level.getBlockEntity(pos);}
                    @Override public BlockState getBlockState(BlockPos pos){return pos.equals(preview.pos())?preview.state():level.getBlockState(pos);}
                    @Override public net.minecraft.world.level.material.FluidState getFluidState(BlockPos pos){return getBlockState(pos).getFluidState();}
                    @Override public int getHeight(){return level.getHeight();}
                    @Override public int getMinY(){return level.getMinY();}
                    @Override public ModelData getModelData(BlockPos pos){return pos.equals(preview.pos()) && data!=null?data:level.getModelData(pos);}
                };
                model.collectParts(view,preview.pos(),preview.state(),RandomSource.create(42),parts);
                var quads=new ArrayList<BakedQuad>();
                for(var part:parts){quads.addAll(part.getQuads(null));for(var direction:net.minecraft.core.Direction.values())quads.addAll(part.getQuads(direction));}
                extracted.add(new ExtractedPreview(preview.pos().immutable(),preview.tint(),List.copyOf(quads)));
            }
        }
        event.getRenderState().setRenderData(PREVIEWS,List.copyOf(extracted));
    }

    public static void submit(SubmitCustomGeometryEvent event) {
        var previews=event.getLevelRenderState().getRenderData(PREVIEWS);if(previews==null)return;
        var camera=event.getLevelRenderState().cameraRenderState.pos;var pose=event.getPoseStack();
        for(var preview:previews){
            pose.pushPose();
            pose.translate(preview.pos().getX()-camera.x,preview.pos().getY()-camera.y,preview.pos().getZ()-camera.z);
            float scale=preview.tint()==GREEN?0.999F:1.001F;pose.translate(0.5,0.5,0.5);pose.scale(scale,scale,scale);pose.translate(-0.5,-0.5,-0.5);
            event.getSubmitNodeCollector().submitCustomGeometry(pose,net.minecraft.client.renderer.Sheets.translucentBlockSheet(),(matrix,buffer)->{
                var instance=new com.mojang.blaze3d.vertex.QuadInstance();instance.setColor(0x7D000000|preview.tint());instance.setLightCoords(0xF000F0);instance.setOverlayCoords(0);
                for(var quad:preview.quads())buffer.putBakedQuad(matrix,quad,instance);
            });
            pose.popPose();
        }
    }

    private static ItemStack heldBuilder(Player player) {
        for (var hand : List.of(player.getMainHandItem(),player.getOffhandItem())) {
            if (hand.is(IOItems.MULTIBLOCK_BUILDER.get())) return hand;
        }
        return ItemStack.EMPTY;
    }

    private static List<Preview> previews(ClientLevel level, Player player, BlockHitResult hit, ItemStack stack) {
        MultiblockBuilder.Mode mode = stack.getOrDefault(IOComponents.MULTI_BUILDER_MODE, MultiblockBuilder.Mode.BUILD);
        if (mode == MultiblockBuilder.Mode.BUILD) return buildPreviews(level, hit.getBlockPos());
        if (mode != MultiblockBuilder.Mode.COPY_PASTE) return List.of();

        IOComponents.Template storedTemplate = stack.get(IOComponents.MULTI_BUILDER_TEMPLATE);
        if (storedTemplate == null || storedTemplate.blocks().isEmpty()) return List.of();
        Map<BlockPos, IOComponents.BlockData> template = storedTemplate.expand();
        for (var entry : template.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) return List.of();
        }
        if (template.get(BlockPos.ZERO) == null) return List.of();

        Rotation rotation = MultiblockBuilder.getPasteRotation(template.get(BlockPos.ZERO).settings(), player.getDirection().getOpposite());
        BlockPos pasteTarget = player.isShiftKeyDown() ? hit.getBlockPos() : hit.getBlockPos().relative(hit.getDirection());
        BlockPos anchor = MultiblockBuilder.pastePosition(pasteTarget, template, rotation);
        List<Preview> result = new ArrayList<>();
        for (var entry : template.entrySet()) {
            Block block = Block.byItem(entry.getValue().item());
            if (block == Blocks.AIR) return List.of();
            addPreview(level, result, anchor.offset(MultiblockBuilder.rotateOffset(entry.getKey(), rotation)),
                    block.defaultBlockState(), MultiblockBuilder.rotateSettings(entry.getValue().settings(), rotation));
        }
        return result;
    }

    private static List<Preview> buildPreviews(ClientLevel level, BlockPos controllerPos) {
        if (!(level.getBlockEntity(controllerPos) instanceof MultiblockMachineBlockEntity machine)) return List.of();
        ShapeMatcher matcher = machine.createShapeMatcher();
        List<Preview> result = new ArrayList<>();
        for (BlockPos pos : matcher.getPositions()) {
            if (!level.hasChunkAt(pos) || alreadyValid(matcher, level, pos)) continue;
            var member = matcher.getSimpleMember(pos);
            addPreview(level, result, pos, ShapeMatcher.toWorldState(level, pos,
                    member.getPreviewState(), matcher.controllerDirection), null);
        }
        return result;
    }

    private static void addPreview(ClientLevel level, List<Preview> result, BlockPos pos, BlockState state, CompoundTag settings) {
        if (!level.hasChunkAt(pos) || state.isAir()) return;
        BlockState current = level.getBlockState(pos);
        if (current.equals(state)) return;
        result.add(new Preview(pos, state, current.canBeReplaced() ? GREEN : RED, settings));
    }

    private static ModelData modelData(ClientLevel level, Preview preview) {
        if (preview.settings == null || !(preview.state.getBlock() instanceof MachineBlock machineBlock)) return null;
        try {
            MachineBlockEntity machine = machineBlock.newBlockEntity(preview.pos, preview.state);
            machine.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,level.registryAccess(),preview.settings), false);
            return machine.getModelData();
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static boolean alreadyValid(ShapeMatcher matcher, ClientLevel level, BlockPos pos) {
        var previewState = matcher.getSimpleMember(pos).getPreviewState();
        if (level.getBlockState(pos).getBlock().asItem().equals(previewState.getBlock().asItem())) return true;
        if (!(level.getBlockEntity(pos) instanceof HatchBlockEntity hatch)) return false;
        var flags = matcher.getHatchFlags(pos);
        return flags != null && flags.allows(hatch.getHatchType());
    }

}
