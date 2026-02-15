# BrainCraft

A Fabric mod for Minecraft (Java Edition), set up for a programmatic, API-driven workflow.

## Requirements

- **JDK 21** (required for Minecraft 1.21.x / Fabric Loom)
- Minecraft: Java Edition
- [Fabric Loader](https://fabricmc.net/use/installer/) (for playing with the mod)

### Install JDK 21 on Mac

```bash
# Homebrew
brew install openjdk@21

# Then use it for this project (add to your shell profile to make permanent):
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

Or download [Adoptium Temurin 21](https://adoptium.net/temurin/releases/) for macOS.

## Build & run

```bash
# Build the mod JAR
./gradlew build

# Run Minecraft with BrainCraft loaded (client)
./gradlew runClient

# Run dedicated server with BrainCraft loaded
./gradlew runServer
```

Output JAR: `build/libs/braincraft-1.0.0.jar`

## Flat world

To play on a **completely flat** grass world:

1. **Create New World** → **More World Options** → **World Type: Superflat**.
2. Click **Customize** → under **Preset**, choose **BrainCraft Flat (Grass)** (or type `braincraft:grass_flat`).
3. Create the world. The ground will be grass with dirt and stone below.

## Base (Phase 1)

- **`/braincraft base set`** — Builds your base where you stand: a **stone room** (floor, walls, roof) and **grass** in the surrounding area. Blocks inside the base are unbreakable.
- **`/braincraft base show`** — Show your base position.
- **`/braincraft base clear`** — Remove your base (in-memory; blocks stay).
- When you stand inside your base, the action bar shows **Your base**. On death you respawn inside your base.

## Project layout

| Path | Purpose |
|------|--------|
| `src/main/java/com/braincraft/` | Main mod logic (server & shared) |
| `src/main/java/com/braincraft/mixin/` | Server-side mixins |
| `src/client/java/com/braincraft/` | Client-only logic |
| `src/client/java/com/braincraft/mixin/client/` | Client-only mixins |
| `src/main/resources/fabric.mod.json` | Mod metadata |
| `src/main/resources/assets/braincraft/` | Mod assets (e.g. icon) |

## Next steps

- Register [custom commands](https://docs.fabricmc.net/) for in-game or script-driven actions.
- Use Fabric [events](https://docs.fabricmc.net/) (e.g. `ServerLifecycleEvents`, block/entity events) for hooks.
- Add an embedded HTTP server or scripting layer for REST/API-style automation.

## Links

- [Fabric docs](https://docs.fabricmc.net/)
- [Fabric versions](https://fabricmc.net/develop/)
