# BrainCraft — Development Roadmap

Step-by-step instructions to build the systems from the [Game Design Document](GDD.md). Work in order; each phase builds on the previous.

**Codebase:** `BrainCraft/` (Fabric 1.21.11, Java 21)
**Design ref:** GDD v0.2 — unbreakable bases, phasable your-blocks, full PvP, weapon tiers, Brainrot Dispenser, flat world.

---

## Phase 0: Current state ✓

- [x] Fabric mod skeleton (BrainCraft)
- [x] Command: `/braincraft spawnzombie` (spawns zombie in front of player)
- [x] Keybind: **B** triggers that command
- [x] GDD and ROADMAP (updated for unbreakable base, blocks, PvP, Brainrots)

**No action.** Use this as the starting point for Phase 1.

---

## Phase 1: Bases — unbreakable zone and “Your base”

**Goal:** Each player has one base: a zone of **unbreakable blocks**, labeled **“Your base,”** and a respawn point.

### 1.1 Base data and registry

1. **Create base data type.**
   - Package `com.braincraft.base` (or `game`).
   - `PlayerBase` (or similar) with: owner UUID, **region** (e.g. center `BlockPos` + radius, or list of blocks), dimension, respawn position (e.g. center + 1 block up).
   - Optional: display name.

2. **Base registry.**
   - `BaseManager`: map `UUID → PlayerBase`, `getBase(UUID)`, `setBase(UUID, PlayerBase)`.
   - In-memory first; persistence in a later phase.

### 1.2 Unbreakable base blocks

1. **Define the base “structure.”**
   - Option A: Base = set of block positions (e.g. circle or square of blocks around a center). When a player gets a base, **set those blocks** in the world to an **unbreakable** block type (custom block or vanilla with unbreakable tag).
   - Option B: Pre-built structure in the world; mark that region as “base” and **cancel block break** for any block in that region (via event or mixin).

2. **Unbreakable behaviour.**
   - Block break event or mixin: if the block is part of a player’s base (lookup by position → BaseManager), **cancel** the break. No drops.
   - Ensure only “base” blocks are unbreakable; **placed blocks** in the world (see Phase 3) are breakable and have ownership.

### 1.3 Respawn at base

1. **Respawn hook.**
   - Use Fabric `ServerPlayerEvents.AFTER_RESPAWN` or mixin so that on death the player respawns at **their base** respawn position (and dimension).
   - If no base, fall back to world spawn.

### 1.4 “Your base” label

1. **When player is inside their base:** Show “Your base” (action bar, subtitle, or title). Detect “inside” by checking if player position is within base region.
   - Tick event or mixin: if `BaseManager.getBase(player.getUUID())` contains `player.blockPosition()`, set action bar text (or send message) “Your base” (use lang key).

**Phase 1 done when:** Each player can have a base (command or auto-assign); base blocks are unbreakable; respawn at base on death; “Your base” appears when standing in base.

---

## Phase 2: Block source and taking blocks

**Goal:** Players can **take blocks** from their base to place in the world (for building).

### 2.1 How players get blocks

1. **Choose one (or combine):**
   - **Block pool at base:** A block or chest at base that gives building blocks (e.g. right-click → add 64 cobblestone to inventory, or open GUI to take blocks). Implement as custom block or use vanilla chest with restock logic.
   - **Infinite block:** One “block source” block at base; right-click gives a stack of a chosen building block (e.g. cobble, planks). Cooldown optional.
2. **Implement the “take blocks” interaction** (block entity, right-click handler, or command for testing).

**Phase 2 done when:** Player can obtain building blocks from their base and carry them to place elsewhere.

---

## Phase 3: Placed blocks — ownership, phasing, mining

**Goal:** Placed blocks have **ownership**. You **phase through your own**; others **cannot** phase through yours (solid to them) and **can mine** them. You **cannot** phase through **others’** blocks.

### 3.1 Block ownership

1. **Track who placed which block.**
   - When a player places a block (block place event), store **owner UUID** for that position. Options:
     - **Block entity:** Custom block that stores owner UUID (every placed “build” block is your custom block).
     - **External map:** Chunk/position → UUID in a world-saved or global structure (e.g. `Map<ChunkPos, Map<BlockPos, UUID>>` or dimension data).
   - Prefer custom “BrainCraft build block” (one or more variants) with block entity storing owner, so placement is explicit and we can control drops.

2. **Which blocks are “owned”?**
   - Only blocks **placed by players** (from base blocks) count. Vanilla blocks in the world (flat world gen) have no owner—normal collision and break for everyone. So: introduce **placeable** “BrainCraft” blocks (e.g. “Your cobble”) that, when placed, store owner and use phasing/mining rules below.

### 3.2 You phase through your blocks

1. **Collision: your blocks.**
   - For blocks owned by the moving player, **disable collision** (player can walk through). Options:
     - **Mixin on collision:** When entity (player) is checking collision with a block, if block at that position is owned by the player, skip adding the collision box (or use empty box).
     - **Custom block:** Your block type’s `getCollisionShape` returns empty for the owner (requires passing player context—e.g. only “no collision” when the world is being queried for that player’s movement; may need mixin on entity collision instead).
   - Practical approach: **mixin on entity/player** so that when checking block collision, if the block is a “BrainCraft build block” and its owner is this player, treat as no collision.

### 3.3 Others’ blocks: solid to you, mineable by you

1. **Others’ blocks:** Normal collision for everyone who is **not** the owner. No special code if “your blocks” are the only exception.
2. **Mining:** Anyone can break any **placed** (owned) block. Drops: either to miner or to owner (GDD open question). Implement normal break + drop; optionally give drop to miner to encourage raiding.

**Phase 3 done when:** Placed “build” blocks have owner; owner phases through their blocks; non-owners hit solid and can mine them.

---

## Phase 4: Combat — 10 hearts, starter weapon, damage

**Goal:** Everyone has **10 hearts**. **Starter weapon** deals **2 hearts** per hit. No tag-respawn; death = respawn at base.

### 4.1 Fixed 10 hearts

1. **On join / respawn:** Set max health to 20 (10 hearts). Use attribute or effect so it sticks (no vanilla items increasing it unless we add later).
   - Mixin or event: when player is created or respawns, set `Attributes.MAX_HEALTH` to 20 and heal to full.

### 4.2 Starter weapon

1. **Define “starter weapon.”** Custom item (e.g. “BrainCraft Starter Sword”) or use vanilla wooden sword with custom damage.
2. **Damage:** On hit (attack entity event or mixin), if the attacker is holding the starter weapon, apply **4 HP (2 hearts)** damage (overriding or adding to vanilla). Ensure only one application per swing (no double application).
   - Fabric `AttackEntityCallback` or mixin on damage logic: if source is player and main hand is starter weapon, set damage to 4 (or add modifier).

### 4.3 Give starter on spawn

1. **First spawn or respawn:** Give the player the starter weapon if they don’t have it (e.g. in inventory or in hotbar). Respawn event: if no starter weapon in inventory, give one.

**Phase 4 done when:** Players have 10 hearts, starter weapon does 2 hearts per hit, and respawn at base on death (from Phase 1).

---

## Phase 5: Brainrots — entity, item, “steal” = deliver to base

**Goal:** **Brainrots** are custom mobs. Killing (or capturing) them gives a **Brainrot item**. **Delivering** that item **to your base** counts as “stolen” and unlocks the next weapon tier.

### 5.1 Brainrot entity

1. **Register Brainrot entity type** (Fabric 1.21). Create `BrainrotEntity` (e.g. extends `Monster` or `PathfinderMob`), register in `BrainCraftMod`, add attributes (health, speed, damage).
2. **Client renderer:** Register renderer in client init (reuse zombie model for first pass or add simple custom model).
3. **AI:** Basic movement and optionally melee attack. No “tag” respawn; Brainrots are PvE only (or add later).

### 5.2 Brainrot item (drop / capture)

1. **On Brainrot death:** Drop a custom item, e.g. “Brainrot” (or “Brainrot in a jar”). That item represents “one Brainrot” for progression.
2. **Alternative:** “Capture” with a net/item that converts entity to item. For first pass, **kill = drop** is enough.

### 5.3 “Steal” = deliver to base

1. **Define “delivered to base.”** When the player is **inside their base** (same region check as “Your base”) and has the **Brainrot item** in inventory, either:
   - **Automatic:** Every few seconds while in base with the item, consume one Brainrot item and increment “Brainrots stolen” for that player.
   - **Manual:** Right-click a “deposit” block at base (or use key) to consume Brainrot item and increment count.
2. **Store “Brainrots stolen” per player.** In-memory map `UUID → int`; persist in Phase 7. This count gates **weapon tiers** (Phase 6).

**Phase 5 done when:** Brainrot mob exists, drops Brainrot item on death; delivering that item at your base increments “stolen” count.

---

## Phase 6: Weapon tiers and progression

**Goal:** **First Brainrot delivered** = unlock **tier 2 weapon** (e.g. 3 hearts/hit). More delivered = tier 3, etc.

### 6.1 Weapon tiers

1. **Define tiers:** e.g. Tier 1 = starter (2 hearts), Tier 2 = 3 hearts, Tier 3 = 4 hearts, Tier 4 = 5 hearts. Create one item per tier (or one item with NBT/damage based on tier).
2. **Unlock rule:** When “Brainrots stolen” (delivered to base) reaches 1, player can use tier 2 weapon; at 2, tier 3; etc. Either give item on first unlock or make tier-2/3/4 weapons **craftable** or **purchasable at base** once unlocked.

### 6.2 Apply tier damage

1. **On attack:** Check attacker’s held item. If it’s a tier-2/3/4 weapon, check if the player has unlocked that tier (stolen count >= required). If yes, apply corresponding damage (6, 8, 10 HP). If not (e.g. picked up someone else’s weapon), treat as starter or deny use.

**Phase 6 done when:** Delivering Brainrots unlocks better weapons; weapon damage matches tier (2 / 3 / 4 / 5 hearts).

---

## Phase 7: Brainrot Dispenser and holding = slow

**Goal:** A **Brainrot Dispenser** block spawns Brainrots (at base or in world). **Holding** a Brainrot (item or “carried” state) makes the player **slightly slower**.

### 7.1 Brainrot Dispenser block

1. **Custom block + block entity.** “Brainrot Dispenser”: right-click or redstone triggers spawn of one Brainrot entity nearby (or gives Brainrot item). Option: different dispenser types for different Brainrot variants (GDD: “different brainrots”).
2. **Placement:** At least one per base (from Phase 1 base setup). Optional: place dispensers in the flat world for contested Brainrot access.
3. **Cooldown:** Per-dispenser or per-player cooldown so Brainrots aren’t infinite (e.g. one use per 60 s per dispenser).

### 7.2 Slowness when holding Brainrot

1. **If player has Brainrot item in hand (main or offhand):** Apply slowness effect (e.g. level I) or reduce movement speed attribute while holding. Use tick event or attribute modifier when item in hand is the Brainrot item.

**Phase 7 done when:** Brainrot Dispenser gives Brainrots; holding Brainrot item = slightly slower movement.

---

## Phase 8: Flat world

**Goal:** The map is **automatically flat**.

### 8.1 World type

1. **Server/world config:** Use **superflat** world type for the BrainCraft dimension/overworld. If the mod always creates or forces the world, set default to superflat in server properties or level dat.
2. **Documentation:** In README or server setup, state that BrainCraft is intended to be played on a flat world.

**Phase 8 done when:** BrainCraft is played on a flat map (config or docs).

---

## Phase 9: Persistence and polish

**Goal:** Bases and progression survive server restart. Messages and balance.

### 9.1 Save data

1. **Base regions and respawn points** — save to world folder (e.g. NBT or JSON per dimension or global).
2. **“Brainrots stolen” per player** — save with player data or in same world file; load on join.
3. **Load on server start / player join;** write on base set, deliver, or shutdown.

### 9.2 Messages and balance

1. **All user-facing text** in lang file (`assets/braincraft/lang/en_us.json`).
2. **Tune:** Weapon damage, dispenser cooldown, slowness level, base size, block give amounts.

---

## Summary table (aligned with GDD v0.2)

| Phase | Deliverable |
|-------|-------------|
| **1** | Unbreakable base zone, “Your base” label, respawn at base on death. |
| **2** | Take blocks from base to place in world. |
| **3** | Placed blocks have owner; you phase through yours; others mine yours; you can’t phase through theirs. |
| **4** | 10 hearts, starter weapon 2 hearts/hit, give starter on spawn. |
| **5** | Brainrot entity, Brainrot item (drop on kill), “deliver to base” = stolen count. |
| **6** | Weapon tiers unlocked by stolen count; tier damage (2/3/4/5 hearts). |
| **7** | Brainrot Dispenser block; slowness when holding Brainrot. |
| **8** | Flat world (config/docs). |
| **9** | Persistence (bases, stolen count); messages and balance. |

---

*Update this roadmap when the GDD or implementation details change.*
