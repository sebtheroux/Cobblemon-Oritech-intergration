package com.openai.cobblemonoritechpower.energy;

import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class PowerUtil {
    private PowerUtil() {}

    public static PoweredMachine findPoweredMachine(Level level, BlockPos clickedPos) {
        PoweredMachine direct = asPowered(level.getBlockEntity(clickedPos));
        if (direct != null) return direct;

        // PC and Pasture are two blocks high and only their base owns the block entity.
        PoweredMachine below = asPowered(level.getBlockEntity(clickedPos.below()));
        if (below != null) return below;
        return asPowered(level.getBlockEntity(clickedPos.above()));
    }

    private static PoweredMachine asPowered(BlockEntity be) {
        return be instanceof PoweredMachine powered ? powered : null;
    }

    public static boolean consumeExact(PoweredMachine machine, long amount) {
        if (amount <= 0) return true;
        EnergyStorage storage = machine.cop$getInternalEnergyStorage();
        try (Transaction tx = Transaction.openOuter()) {
            long extracted = storage.extract(amount, tx);
            if (extracted != amount) return false;
            tx.commit();
            return true;
        }
    }

    public static boolean hasEnergy(PoweredMachine machine, long amount) {
        return amount <= 0 || machine.cop$getStoredEnergy() >= amount;
    }

    public static void noPowerMessage(ServerPlayer player, long needed, long stored) {
        player.displayClientMessage(
            Component.translatable("message.cobblemon_oritech_power.no_power", needed, stored),
            true
        );
    }

    public static boolean consumeFossil(Level level, FossilMultiblockStructure structure, long amount) {
        if (amount <= 0) return true;
        List<PoweredMachine> storages = fossilStorages(level, structure);
        long available = 0;
        for (PoweredMachine storage : storages) available += storage.cop$getStoredEnergy();
        if (available < amount) return false;

        try (Transaction tx = Transaction.openOuter()) {
            long remaining = amount;
            for (PoweredMachine machine : storages) {
                if (remaining <= 0) break;
                long extracted = machine.cop$getInternalEnergyStorage().extract(remaining, tx);
                remaining -= extracted;
            }
            if (remaining > 0) return false;
            tx.commit();
            return true;
        }
    }

    public static long fossilStored(Level level, FossilMultiblockStructure structure) {
        long total = 0;
        for (PoweredMachine machine : fossilStorages(level, structure)) total += machine.cop$getStoredEnergy();
        return total;
    }

    private static List<PoweredMachine> fossilStorages(Level level, FossilMultiblockStructure structure) {
        Set<BlockPos> positions = new LinkedHashSet<>();
        positions.add(structure.getAnalyzerPos());
        positions.add(structure.getMonitorPos());
        positions.add(structure.getTankBasePos());
        positions.add(structure.getTankBasePos().above());

        List<PoweredMachine> result = new ArrayList<>(4);
        for (BlockPos pos : positions) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PoweredMachine powered) result.add(powered);
        }
        return result;
    }

}
