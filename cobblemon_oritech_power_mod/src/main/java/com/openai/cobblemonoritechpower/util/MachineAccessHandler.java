package com.openai.cobblemonoritechpower.util;

import com.openai.cobblemonoritechpower.config.PowerConfig;
import com.openai.cobblemonoritechpower.energy.PowerUtil;
import com.openai.cobblemonoritechpower.energy.PoweredMachine;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;

public final class MachineAccessHandler {
    private MachineAccessHandler() {}

    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (level.isClientSide || !(player instanceof ServerPlayer serverPlayer)) {
                return InteractionResult.PASS;
            }

            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(level.getBlockState(hitResult.getBlockPos()).getBlock());
            if (blockId == null || !"cobblemon".equals(blockId.getNamespace())) return InteractionResult.PASS;

            long cost = switch (blockId.getPath()) {
                case "healing_machine" -> PowerConfig.get().healingMachine.accessCost;
                case "pc" -> PowerConfig.get().pc.accessCost;
                case "pasture" -> PowerConfig.get().pasture.accessCost;
                case "tm_machine" -> player.isShiftKeyDown() ? 0 : PowerConfig.get().tmMachine.accessCost;
                // Fossil machine is loaded while unpowered if desired; actual
                // revival cannot start/progress without continuous power.
                case "fossil_analyzer", "monitor", "restoration_tank" -> 0;
                default -> -1;
            };

            if (cost < 0) return InteractionResult.PASS;
            PoweredMachine machine = PowerUtil.findPoweredMachine(level, hitResult.getBlockPos());
            if (machine == null) return InteractionResult.PASS;

            long stored = machine.cop$getStoredEnergy();
            if (cost > 0 && !PowerUtil.consumeExact(machine, cost)) {
                PowerUtil.noPowerMessage(serverPlayer, cost, stored);
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });
    }
}
