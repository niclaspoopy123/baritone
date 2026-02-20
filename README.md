# Baritone Plus

A Fabric client-side mod for Minecraft 1.15 that extends [Baritone](https://github.com/cabaletta/baritone) with **Fight Bot**, **Crystal Bot**, an improved **Mine Bot**, and a unified **GUI**.

## Features

### Fight Bot
- Auto-targets and attacks the nearest hostile mob
- Waits for the attack cool-down so every hit deals full damage
- Optional player targeting for PvP
- Configurable reach distance (1–6 blocks)

### Crystal Bot (End Crystal PvP)
- Automatically places End Crystals on obsidian/bedrock near enemy players
- Automatically breaks nearby crystals to deal damage
- Configurable place range, break range, and target range

### Mine Bot (Improved)
- **Ore priority system** — mines the most valuable ore first  
  Diamond → Emerald → Gold → Iron → Lapis → Redstone → Coal
- **Vein mining** — follows connected ore blocks so the whole vein is mined
- **Lava avoidance** — skips ore blocks that border lava
- Configurable search radius (1–32 blocks)

### GUI (Right Shift)
Press **Right Shift** at any time to open the Baritone Plus settings screen.  
Toggle each module and its options with a single click:

| Button | Description |
|--------|-------------|
| Fight Bot | Enable / disable auto-combat |
| Target Players | Toggle PvP targeting |
| Crystal Bot | Enable / disable crystal automation |
| Mine Bot | Enable / disable smart mining |
| Vein Mine | Toggle vein mining |
| Avoid Lava | Toggle lava avoidance |

## Building

Requires Java 8+ and the Fabric toolchain.

```bash
./gradlew build
```

The compiled mod JAR will be in `build/libs/`.

## Installation

1. Install [Fabric Loader](https://fabricmc.net/) for Minecraft 1.15.2.
2. Install [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api).
3. Drop `baritone-api-fabric-1.15.0.jar` and the built mod JAR into your `.minecraft/mods` folder.
4. Launch Minecraft — press **Right Shift** in-game to open the GUI.

## License

MIT
