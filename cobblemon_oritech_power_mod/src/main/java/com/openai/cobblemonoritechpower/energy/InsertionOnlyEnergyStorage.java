package com.openai.cobblemonoritechpower.energy;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import team.reborn.energy.api.EnergyStorage;

/** Cable-facing view: accepts power, never lets external networks drain it. */
public final class InsertionOnlyEnergyStorage implements EnergyStorage {
    private final MachineEnergyStorage delegate;

    public InsertionOnlyEnergyStorage(MachineEnergyStorage delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean supportsInsertion() {
        return true;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        return delegate.insert(maxAmount, transaction);
    }

    @Override
    public boolean supportsExtraction() {
        return false;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public long getAmount() {
        return delegate.getAmount();
    }

    @Override
    public long getCapacity() {
        return delegate.getCapacity();
    }
}
