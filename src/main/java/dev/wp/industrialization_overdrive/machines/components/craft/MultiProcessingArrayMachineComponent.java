package dev.wp.industrialization_overdrive.machines.components.craft;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.MachineComponent;
import aztech.modern_industrialization.machines.components.DropableComponent;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import dev.wp.industrialization_overdrive.IO;
import dev.wp.industrialization_overdrive.machines.guicomponents.multiprocessingarraymachineslot.MultiProcessingArrayMachineSlot;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.swedz.tesseract.neoforge.compat.mi.api.ComponentStackHolder;

public final class MultiProcessingArrayMachineComponent implements MachineComponent.ServerOnly, DropableComponent, ComponentStackHolder {
    public static final Identifier ID = IO.id("multi_processing_array_machine");

    private ItemStack machines = ItemStack.EMPTY;
    private MachineRecipeType machineRecipeType;

    public ItemStack getMachines() {
        return machines;
    }

    public boolean hasMachines() {
        return !machines.isEmpty() && machineRecipeType != null;
    }

    public int getMachineCount() {
        return machines.getCount();
    }

    public MachineRecipeType getMachineRecipeType() {
        return machineRecipeType;
    }

    public void setMachines(MachineBlockEntity be, ItemStack machines) {
        this.setStack(machines);
        be.setChanged();
        be.sync();
    }

    @Override
    public void writeNbt(ValueOutput tag) {
        tag.store("machinesStack", ItemStack.OPTIONAL_CODEC, machines);
    }

    @Override
    public void readNbt(ValueInput tag, boolean isUpgradingMachine) {
        setStack(tag.read("machinesStack", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY));
    }

    @Override
    public ItemStack getDrop() {
        return machines;
    }

    @Override
    public ItemStack getStack() {
        return machines;
    }

    @Override
    public void setStack(ItemStack stack) {
        machines = stack;
        machineRecipeType = machines.isEmpty() ? null : MultiProcessingArrayMachineSlot.getMachine(machines).recipeType();
    }
}
