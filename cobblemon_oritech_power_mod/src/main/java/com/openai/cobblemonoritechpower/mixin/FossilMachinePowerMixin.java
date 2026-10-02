package com.openai.cobblemonoritechpower.mixin;

import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure;
import com.openai.cobblemonoritechpower.config.PowerConfig;
import com.openai.cobblemonoritechpower.energy.PowerUtil;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The fossil analyzer + monitor + restoration tank act as one powered machine.
 * Energy may be pushed into any component; processing consumes from their
 * combined buffers atomically.
 */
@Mixin(FossilMultiblockStructure.class)
public abstract class FossilMachinePowerMixin {
    @Inject(method = "startMachine", at = @At("HEAD"), cancellable = true)
    private void cobblemonOritechPower$requireStartupPower(Level level, CallbackInfo ci) {
        if (level.isClientSide) return;
        FossilMultiblockStructure self = (FossilMultiblockStructure) (Object) this;
        long tickCost = PowerConfig.get().fossilMachine.processingCostPerTick;
        if (tickCost > 0 && PowerUtil.fossilStored(level, self) < tickCost) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void cobblemonOritechPower$gateFossilProcessing(Level level, CallbackInfo ci) {
        if (level.isClientSide) return;
        FossilMultiblockStructure self = (FossilMultiblockStructure) (Object) this;
        if (!self.isRunning()) return;

        long tickCost = PowerConfig.get().fossilMachine.processingCostPerTick;
        if (tickCost > 0 && !PowerUtil.consumeFossil(level, self, tickCost)) {
            ci.cancel();
        }
    }
}
