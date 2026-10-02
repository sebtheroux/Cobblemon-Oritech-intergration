package com.openai.cobblemonoritechpower.energy;

import com.cobblemon.mod.common.CobblemonBlockEntities;
import team.reborn.energy.api.EnergyStorage;

public final class EnergyApiRegistrar {
    private EnergyApiRegistrar() {}

    public static void register() {
        // Direct block-entity registrations are intentionally used instead of a
        // global API fallback. Fabric warns that fallbacks can affect every API
        // query; direct registration keeps cable lookups O(1) and local.
        EnergyStorage.SIDED.registerForBlockEntities(
            (blockEntity, side) -> blockEntity instanceof PoweredMachine powered
                ? powered.cop$getEnergyStorage()
                : null,
            CobblemonBlockEntities.HEALING_MACHINE,
            CobblemonBlockEntities.PC,
            CobblemonBlockEntities.PASTURE,
            CobblemonBlockEntities.TM_MACHINE,
            CobblemonBlockEntities.FOSSIL_MULTIBLOCK,
            CobblemonBlockEntities.RESTORATION_TANK,
            CobblemonBlockEntities.FOSSIL_ANALYZER
        );
    }
}
