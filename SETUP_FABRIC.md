# Setting Up Fabric for Minecraft Modding (Mac)

Step-by-step guide for a programmatic, API-friendly Fabric setup on your MacBook Air.

**BrainCraft** is already set up in the `BrainCraft/` folder. See `BrainCraft/README.md` for build and run commands. You only need **JDK 21** installed, then run `./gradlew runClient` from `BrainCraft/`.

---

## 1. Prerequisites

### Java (JDK 21)

- **Minecraft 1.21.x** needs **JDK 21**.
- Download: [Adoptium Temurin](https://adoptium.net/temurin/releases/) — pick **macOS** and **aarch64** (Apple Silicon) or **x64** (Intel).
- Or with Homebrew:
  ```bash
  brew install openjdk@21
  ```
- Confirm:
  ```bash
  java -version
  ```
  You should see something like `openjdk version "21.x.x"`.

### Git (for manual project creation)

```bash
brew install git
```

### Minecraft: Java Edition

- You need the official **Minecraft: Java Edition** (from [minecraft.net](https://www.minecraft.net)) to run and test your mods.

---

## 2. Create a Fabric Mod Project (MDK)

You can use the **template generator** (easiest) or **clone the example mod**.

### Option A: Template generator (recommended)

1. Open: **[Fabric Template Mod Generator](https://fabricmc.net/develop/template/)**
2. Fill in:
   - **Mod name** (e.g. `My Mod`)
   - **Mod ID** (e.g. `mymod` — lowercase, no spaces; you’ll use this in paths and code)
   - **Package name** (e.g. `com.yourname.mymod` — reverse domain style)
   - **Minecraft version** (e.g. 1.21.1)
3. Optional: under **Advanced Options** you can enable Kotlin, Yarn mappings, or data generators.
4. Click **Generate** and download the ZIP.
5. Unzip into a folder **without** spaces or special characters, e.g.:
   ```text
   /Users/christopherdavid/code/minecraft/my-fabric-mod
   ```

### Option B: Clone the example mod

```bash
cd /Users/christopherdavid/code/minecraft
git clone https://github.com/FabricMC/fabric-example-mod.git my-fabric-mod
cd my-fabric-mod
rm -rf .git
```

Then edit the project to match your mod:

- **`gradle.properties`**: set `archives_base_name`, `maven_group`, and Minecraft/mappings/loader/Loom versions (see [fabricmc.net/develop](https://fabricmc.net/develop/) for versions).
- **`src/main/resources/fabric.mod.json`**: set `id`, `name`, `description`.
- Rename the main package and main class to match your package name (e.g. `com.yourname.mymod`).

---

## 3. IDE Setup

### VS Code (your preference)

1. Install [VS Code](https://code.visualstudio.com/) if you don’t have it.
2. Install **Extension Pack for Java**:
   - Open Extensions (`Cmd+Shift+X`), search for **Extension Pack for Java** (by Microsoft), install.
   - Or: [Marketplace – Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
3. When prompted, point the Java extension to your JDK 21 install.
4. Open your mod folder: **File → Open Folder** → select the folder you created (e.g. `my-fabric-mod`).
5. When VS Code asks to import the Gradle project, click **Import Gradle Project**.

Fabric’s docs recommend IntelliJ for the best Java/Minecraft experience, but VS Code works fine for a programmatic, API-focused workflow.

---

## 4. Build and Run the Mod

In the project root (where `build.gradle` and `gradle.properties` are):

```bash
# Build the mod JAR
./gradlew build

# Run Minecraft with your mod (client)
./gradlew runClient

# Run a dedicated server with your mod
./gradlew runServer
```

First run will download Gradle, Minecraft, and Fabric; it can take a few minutes.

- **Client**: launches the game with your mod loaded.
- **Server**: launches a server with your mod (useful for testing commands/API behavior).

Output JAR is under `build/libs/` (e.g. `my-fabric-mod-1.0.0.jar`).

---

## 5. Playing with Fabric (optional)

To install Fabric for normal play (and to drop your or others’ mods in):

1. **Fabric installer**
   - [fabricmc.net/use/installer](https://fabricmc.net/use/installer/) → download the **Universal/.JAR** installer.
   - Close Minecraft and the launcher, then run the JAR (e.g. double‑click or `java -jar fabric-installer-*.jar`).
   - If macOS blocks it: **System Settings → Privacy & Security → Open Anyway**.
   - Choose Minecraft version, click Install, keep “Create profile” checked.

2. **Mods folder (macOS)**  
   Put mod JARs here:
   ```text
   ~/Library/Application Support/minecraft/mods
   ```

3. **Fabric API**  
   Many mods need [Fabric API](https://modrinth.com/mod/fabric-api). Download the JAR for your Minecraft version and put it in `mods/` as well.

In the launcher, select the **Fabric** profile and play.

---

## 6. Programmatic / API-oriented next steps

For an API-driven, minimal-UI approach:

- **Custom commands**: register commands that trigger your logic (e.g. `/mymod do-something`). See Fabric docs **Commands** section.
- **Events**: use Fabric’s event system (e.g. `ServerLifecycleEvents`, `PlayerBlockBreakEvents`) to hook into game actions and data.
- **Data hooks**: read/write world or player data via Fabric/vanilla APIs.
- **REST or scripting**: run an embedded HTTP server or script engine inside the mod and call into game state from your own endpoints or scripts.

Once the project builds and `runClient` / `runServer` work, you can add these layers on top of the template.

---

## Quick reference

| What              | Where / command |
|-------------------|------------------|
| Create project    | [fabricmc.net/develop/template](https://fabricmc.net/develop/template/) or clone `FabricMC/fabric-example-mod` |
| Docs              | [docs.fabricmc.net](https://docs.fabricmc.net/) |
| Version info      | [fabricmc.net/develop](https://fabricmc.net/develop/) |
| Build             | `./gradlew build` |
| Run client        | `./gradlew runClient` |
| Run server        | `./gradlew runServer` |
| Mod JAR output    | `build/libs/*.jar` |
| Mods folder (Mac) | `~/Library/Application Support/minecraft/mods` |

Replace any `example-mod` or placeholder mod ID in docs with your own mod ID (e.g. in paths like `resources/assets/example-mod` → `resources/assets/mymod`).
