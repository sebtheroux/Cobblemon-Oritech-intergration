# Cobblemon Oritech Power

Fabric 1.21.1 compatibility mod for this modpack baseline:

- Minecraft 1.21.1
- Cobblemon 1.8.1
- Oritech 1.2.12
- Fabric API 0.116.17+1.21.1
- TechReborn Energy API 4.1.0 (the energy API used by Oritech Fabric)

## Design

Cobblemon machines expose a standard `team.reborn.energy.api.EnergyStorage` capability. Oritech energy cables/generators can therefore push energy directly into them without this mod scanning the world or pulling energy every tick.

Covered functional machines:

- Healing Machine
- PC
- Pasture
- TM Machine
- Fossil Analyzer
- Fossil Monitor
- Restoration Tank

`cobblemon:damaged_monitor` is deliberately not given a power buffer: it is the broken Porygon interaction block rather than an operational machine/block entity.

## Behaviour

### Healing Machine
Requires an external-energy access charge before use. Cobblemon's own healing animation/charge logic remains intact, so this integration does not rewrite party healing internals.

### PC
Requires a small energy charge each time it is accessed.

### Pasture
Requires a small energy charge when accessed. Existing tethered Pokémon are not despawned during a temporary blackout; this avoids destructive state changes and unnecessary entity churn.

### TM Machine
Requires energy to access and consumes energy every server tick while processing. If power runs out, progression pauses. This also covers redstone/batch automation because the processing ticker itself is gated.

### Fossil Machine
The Analyzer, Monitor and Restoration Tank form one energy pool for processing purposes. The configured fossil capacity is divided across the four powered block entities, so the assembled multiblock holds about the configured total rather than four times that amount. Oritech power may be connected to any component. Fossil processing consumes energy continuously and pauses safely when power is insufficient. Loading fossils/materials is allowed while unpowered.

## Oritech pipes/cables

Energy integration is automatic. Oritech uses the TechReborn Energy API on Fabric, which is push-based; generators/cables push power into machines. This mod intentionally does **not** poll adjacent blocks for power.

Cobblemon's TM Machine and fossil inventories already expose vanilla/container automation paths; this mod does not replace or bypass their slot-validation rules. That keeps Oritech item-pipe compatibility dependent on standard inventory interoperability rather than unsafe direct inventory mutation.

## Default power balance

Generated config: `config/cobblemon_oritech_power.json`

```json
{
  "maxInputPerTick": 512,
  "healingMachine": { "capacity": 16000, "accessCost": 1200, "processingCostPerTick": 0 },
  "pc":             { "capacity": 8000,  "accessCost": 80,   "processingCostPerTick": 0 },
  "pasture":        { "capacity": 8000,  "accessCost": 60,   "processingCostPerTick": 0 },
  "tmMachine":      { "capacity": 20000, "accessCost": 40,   "processingCostPerTick": 24 },
  "fossilMachine":  { "capacity": 48000, "accessCost": 0,    "processingCostPerTick": 32 }
}
```

All values are editable without rebuilding the mod.

## Performance choices

- No global server-tick world scan.
- No searching for nearby generators.
- No energy pulling; Oritech/TechReborn convention is push-based.
- Direct Fabric Block API registration for Cobblemon block-entity types rather than a global fallback provider.
- Fossil processing accesses only the four known multiblock positions while the machine is actually running.
- Power shortage pauses processing instead of repeatedly restarting recipes or consuming ingredients.
- A startup audit checks `#cobblemon:machines` and warns once if a future Cobblemon update adds a new functional machine not covered by this mod.

## Building

Java 21 is required.

The project intentionally targets the exact Cobblemon 1.8.1 Fabric Maven artifact (`maven.modrinth:MdwFAVRL:gBW3vLC7`) and Energy API 4.1.0.

Oritech 1.2.12 itself uses Gradle 9.5.1; use Gradle 9.5.1 with Java 21 for the closest matching build environment.

With Gradle available:

```bash
gradle build
```

The remapped Fabric jar will be under `build/libs/`.

## Update policy

This mod intentionally has exact runtime dependencies on Cobblemon 1.8.1 and Oritech 1.2.12. When either mod is upgraded, review the machine tick methods and bump the dependency only after testing. This is safer than silently running mixins against changed machine internals.
