package com.openai.cobblemonoritechpower.energy;

import team.reborn.energy.api.EnergyStorage;

public interface PoweredMachine {
    /** Safe capability exposed to Oritech/other energy networks. */
    EnergyStorage cop$getEnergyStorage();
    /** Internal transactional storage used only by this integration's machine logic. */
    MachineEnergyStorage cop$getInternalEnergyStorage();
    long cop$getStoredEnergy();
    void cop$setStoredEnergy(long amount);
}
