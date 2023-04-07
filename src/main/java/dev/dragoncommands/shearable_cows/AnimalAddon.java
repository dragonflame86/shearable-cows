package dev.dragoncommands.shearable_cows;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public interface AnimalAddon {
    void applyAdditionalNBT(NbtCompound nbt);
    void readAdditionalNBT(NbtCompound nbt);
}
