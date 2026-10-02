# Build the runnable Fabric jar

This project targets:
- Minecraft 1.21.1
- Fabric Loader 0.17.3
- Fabric API 0.116.17+1.21.1
- Cobblemon 1.8.1 Fabric
- Oritech 1.2.12 runtime energy compatibility
- Java 21

## GitHub (easiest)
1. Put this project in a GitHub repository.
2. Open **Actions** -> **Build Fabric Mod** -> **Run workflow**.
3. Download the `cobblemon-oritech-power-0.1.0` artifact.
4. Inside is `cobblemon-oritech-power-0.1.0.jar` for the mods folder.

The workflow performs the required Fabric Loom remapping; do not use a plain javac-created jar.
