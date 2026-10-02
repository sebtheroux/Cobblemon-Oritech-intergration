package com.openai.cobblemonoritechpower.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PowerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("cobblemon_oritech_power.json");
    private static PowerConfig INSTANCE;

    public long maxInputPerTick = 512;

    public MachineProfile healingMachine = new MachineProfile(16_000, 1_200, 0);
    public MachineProfile pc = new MachineProfile(8_000, 80, 0);
    public MachineProfile pasture = new MachineProfile(8_000, 60, 0);
    public MachineProfile tmMachine = new MachineProfile(20_000, 40, 24);
    public MachineProfile fossilMachine = new MachineProfile(48_000, 0, 32);

    /** Broken/damaged monitor has no BlockEntity and no powered operation. */
    public boolean warnAboutUnpoweredFutureMachines = true;

    public static PowerConfig get() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    public static PowerConfig load() {
        try {
            Files.createDirectories(PATH.getParent());
            if (Files.exists(PATH)) {
                try (Reader reader = Files.newBufferedReader(PATH)) {
                    PowerConfig parsed = GSON.fromJson(reader, PowerConfig.class);
                    if (parsed != null) {
                        parsed.sanitize();
                        INSTANCE = parsed;
                        parsed.save();
                        return parsed;
                    }
                }
            }
        } catch (Exception ignored) {
            // Fall through to defaults. Loader log is handled by the mod initializer.
        }
        PowerConfig defaults = new PowerConfig();
        defaults.sanitize();
        INSTANCE = defaults;
        defaults.save();
        return defaults;
    }

    public void save() {
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(PATH)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException ignored) {
        }
    }

    private void sanitize() {
        maxInputPerTick = Math.max(1, maxInputPerTick);
        healingMachine = MachineProfile.sanitize(healingMachine, 16_000, 1_200, 0);
        pc = MachineProfile.sanitize(pc, 8_000, 80, 0);
        pasture = MachineProfile.sanitize(pasture, 8_000, 60, 0);
        tmMachine = MachineProfile.sanitize(tmMachine, 20_000, 40, 24);
        fossilMachine = MachineProfile.sanitize(fossilMachine, 48_000, 0, 32);
    }

    public static final class MachineProfile {
        public long capacity;
        /** Cost charged when a player opens/uses the machine. */
        public long accessCost;
        /** Cost per server processing tick; 0 for non-processing machines. */
        public long processingCostPerTick;

        public MachineProfile() {
        }

        public MachineProfile(long capacity, long accessCost, long processingCostPerTick) {
            this.capacity = capacity;
            this.accessCost = accessCost;
            this.processingCostPerTick = processingCostPerTick;
        }

        private static MachineProfile sanitize(MachineProfile p, long cap, long access, long tick) {
            if (p == null) p = new MachineProfile(cap, access, tick);
            p.capacity = Math.max(1, p.capacity);
            p.accessCost = Math.max(0, Math.min(p.accessCost, p.capacity));
            p.processingCostPerTick = Math.max(0, Math.min(p.processingCostPerTick, p.capacity));
            return p;
        }
    }
}
