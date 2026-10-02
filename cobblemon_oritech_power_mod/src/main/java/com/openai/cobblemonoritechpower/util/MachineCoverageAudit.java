package com.openai.cobblemonoritechpower.util;

import com.openai.cobblemonoritechpower.CobblemonOritechPower;
import com.openai.cobblemonoritechpower.config.PowerConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public final class MachineCoverageAudit {
    private MachineCoverageAudit() {}

    private static final Set<String> POWERED = Set.of(
        "cobblemon:healing_machine",
        "cobblemon:pc",
        "cobblemon:pasture",
        "cobblemon:tm_machine",
        "cobblemon:fossil_analyzer",
        "cobblemon:monitor",
        "cobblemon:restoration_tank"
    );

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            if (!PowerConfig.get().warnAboutUnpoweredFutureMachines) return;
            TagKey<Block> tag = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("cobblemon", "machines"));
            BuiltInRegistries.BLOCK.getTag(tag).ifPresent(holders -> holders.forEach(holder -> {
                ResourceLocation id = BuiltInRegistries.BLOCK.getKey(holder.value());
                if (id == null) return;
                String value = id.toString();
                if (!POWERED.contains(value) && !"cobblemon:damaged_monitor".equals(value)) {
                    CobblemonOritechPower.LOGGER.warn(
                        "Cobblemon machine {} is not covered by Cobblemon Oritech Power. This may be a new machine added by an update.", value
                    );
                }
            }));
        });
    }
}
