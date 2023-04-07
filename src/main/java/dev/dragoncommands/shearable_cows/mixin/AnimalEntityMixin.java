package dev.dragoncommands.shearable_cows.mixin;

import dev.dragoncommands.shearable_cows.AnimalAddon;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin {

    @Inject(at=@At("HEAD"), method = "readCustomDataFromNbt")
    void readNBT(NbtCompound nbt, CallbackInfo ci) {
        if(this instanceof AnimalAddon addon) {
            addon.readAdditionalNBT(nbt);
        }
    }

    @Inject(at=@At("HEAD"), method = "writeCustomDataToNbt")
    void writeNBT(NbtCompound nbt, CallbackInfo ci) {
        if(this instanceof AnimalAddon addon) {
            addon.applyAdditionalNBT(nbt);
        }
    }

}
