package com.openai.cobblemonoritechpower.mixin;

import com.cobblemon.mod.common.block.entity.TMMachineBlockEntity;
import com.openai.cobblemonoritechpower.config.PowerConfig;
import com.openai.cobblemonoritechpower.energy.PowerUtil;
import com.openai.cobblemonoritechpower.energy.PoweredMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Pauses TM processing when the machine runs out of power. This also covers
 * redstone/batch automation, not only GUI use.
 */
@Mixin(targets = "com.cobblemon.mod.common.block.entity.TMMachineBlockEntity$Companion")
public abstract class TMMachinePowerMixin {
    @Inject(method = "serverTick", at = @At("HEAD"), cancellable = true)
    private void cobblemonOritechPower$gateTmProcessing(
        Level level,
        BlockPos pos,
        BlockState state,
        TMMachineBlockEntity blockEntity,
        CallbackInfo ci
    ) {
        if (level.isClientSide) return;
        if (!blockEntity.getBurnActive()) return;

        long cost = PowerConfig.get().tmMachine.processingCostPerTick;
        if (cost <= 0) return;
        if (!(blockEntity instanceof PoweredMachine powered) || !PowerUtil.consumeExact(powered, cost)) {
            ci.cancel();
        }
    }
}
