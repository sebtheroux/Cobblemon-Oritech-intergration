package com.openai.cobblemonoritechpower.mixin;

import com.openai.cobblemonoritechpower.config.PowerConfig;
import com.openai.cobblemonoritechpower.energy.MachineEnergyStorage;
import com.openai.cobblemonoritechpower.energy.MachineProfiles;
import com.openai.cobblemonoritechpower.energy.InsertionOnlyEnergyStorage;
import com.openai.cobblemonoritechpower.energy.PoweredMachine;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import team.reborn.energy.api.EnergyStorage;

@Mixin(targets = {
    "com.cobblemon.mod.common.block.entity.HealingMachineBlockEntity",
    "com.cobblemon.mod.common.block.entity.PCBlockEntity",
    "com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity",
    "com.cobblemon.mod.common.block.entity.TMMachineBlockEntity",
    "com.cobblemon.mod.common.block.entity.FossilMultiblockEntity",
    "com.cobblemon.mod.common.block.entity.RestorationTankBlockEntity",
    "com.cobblemon.mod.common.block.entity.FossilAnalyzerBlockEntity"
})
public abstract class PoweredMachineBlockEntityMixin implements PoweredMachine {
    @Unique
    private MachineEnergyStorage cobblemonOritechPower$energy;
    @Unique
    private EnergyStorage cobblemonOritechPower$externalEnergy;

    @Unique
    private MachineEnergyStorage cobblemonOritechPower$getOrCreateStorage() {
        if (cobblemonOritechPower$energy == null) {
            BlockEntity self = (BlockEntity) (Object) this;
            PowerConfig.MachineProfile profile = MachineProfiles.forBlockEntity(self);
            cobblemonOritechPower$energy = new MachineEnergyStorage(
                profile.capacity,
                Math.min(profile.capacity, PowerConfig.get().maxInputPerTick),
                self::setChanged
            );
        }
        return cobblemonOritechPower$energy;
    }

    @Override
    public EnergyStorage cop$getEnergyStorage() {
        MachineEnergyStorage internal = cobblemonOritechPower$getOrCreateStorage();
        if (cobblemonOritechPower$externalEnergy == null) {
            cobblemonOritechPower$externalEnergy = new InsertionOnlyEnergyStorage(internal);
        }
        return cobblemonOritechPower$externalEnergy;
    }

    @Override
    public MachineEnergyStorage cop$getInternalEnergyStorage() {
        return cobblemonOritechPower$getOrCreateStorage();
    }

    @Override
    public long cop$getStoredEnergy() {
        return cobblemonOritechPower$getOrCreateStorage().getAmount();
    }

    @Override
    public void cop$setStoredEnergy(long amount) {
        cobblemonOritechPower$getOrCreateStorage().setStored(amount);
    }
}
