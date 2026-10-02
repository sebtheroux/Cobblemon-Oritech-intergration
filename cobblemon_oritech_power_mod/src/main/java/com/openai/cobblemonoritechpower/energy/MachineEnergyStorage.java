package com.openai.cobblemonoritechpower.energy;

import team.reborn.energy.api.base.SimpleEnergyStorage;

public final class MachineEnergyStorage extends SimpleEnergyStorage {
    private final Runnable dirtyCallback;

    public MachineEnergyStorage(long capacity, long maxInsert, Runnable dirtyCallback) {
        // Internal storage supports extraction so the machine can consume power.
        // External callers only receive InsertionOnlyEnergyStorage.
        super(capacity, maxInsert, capacity);
        this.dirtyCallback = dirtyCallback;
    }

    @Override
    protected void onFinalCommit() {
        dirtyCallback.run();
    }

    public void setStored(long value) {
        amount = Math.max(0, Math.min(value, capacity));
    }
}
