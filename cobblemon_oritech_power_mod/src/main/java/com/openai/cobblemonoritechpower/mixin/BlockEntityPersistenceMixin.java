package com.openai.cobblemonoritechpower.mixin;

import com.openai.cobblemonoritechpower.energy.PoweredMachine;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityPersistenceMixin {
    private static final String POWER_KEY = "CobblemonOritechPowerEnergy";

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void cobblemonOritechPower$saveEnergy(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if ((Object) this instanceof PoweredMachine powered) {
            tag.putLong(POWER_KEY, powered.cop$getStoredEnergy());
        }
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void cobblemonOritechPower$loadEnergy(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if ((Object) this instanceof PoweredMachine powered && tag.contains(POWER_KEY)) {
            powered.cop$setStoredEnergy(tag.getLong(POWER_KEY));
        }
    }
}
