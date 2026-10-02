package com.openai.cobblemonoritechpower;

import com.openai.cobblemonoritechpower.config.PowerConfig;
import com.openai.cobblemonoritechpower.energy.EnergyApiRegistrar;
import com.openai.cobblemonoritechpower.util.MachineAccessHandler;
import com.openai.cobblemonoritechpower.util.MachineCoverageAudit;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CobblemonOritechPower implements ModInitializer {
    public static final String MOD_ID = "cobblemon_oritech_power";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        PowerConfig.load();
        EnergyApiRegistrar.register();
        MachineAccessHandler.register();
        MachineCoverageAudit.register();

        LOGGER.info("Cobblemon Oritech Power loaded: Cobblemon machines now accept TechReborn/Oritech energy.");
    }
}
