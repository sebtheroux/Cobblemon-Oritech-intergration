package com.openai.cobblemonoritechpower.energy;

import com.openai.cobblemonoritechpower.config.PowerConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class MachineProfiles {
    private MachineProfiles() {}

    public static PowerConfig.MachineProfile forBlockEntity(BlockEntity blockEntity) {
        ResourceLocation id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType());
        PowerConfig config = PowerConfig.get();
        if (id == null || !"cobblemon".equals(id.getNamespace())) return config.pc;

        return switch (id.getPath()) {
            case "healing_machine" -> config.healingMachine;
            case "pc" -> config.pc;
            case "pasture" -> config.pasture;
            case "tm_machine" -> config.tmMachine;
            case "fossil_multiblock", "restoration_tank", "fossil_analyzer" -> new PowerConfig.MachineProfile(
                Math.max(1, config.fossilMachine.capacity / 4),
                config.fossilMachine.accessCost,
                config.fossilMachine.processingCostPerTick
            );
            default -> config.pc;
        };
    }
}
