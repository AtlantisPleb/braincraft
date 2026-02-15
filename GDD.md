# BrainCraft — Game Design Document

**Version:** 0.2
**Last updated:** 2025-02

---

## 1. Core concept

BrainCraft is a multiplayer PvP/territory game inspired by “steal the brainrot”–style modes. Everyone has a **base made of unbreakable blocks** labeled **“Your base.”** You take blocks from your base to **build** in the world; other players can **mine your placed blocks**, but **you can walk through your own placed blocks** and **cannot walk through other players’ blocks**. Full PvP everywhere: **10 hearts**, different **weapons** with different damage. The **starter weapon** does 2 hearts per hit; **stealing a Brainrot** unlocks the next weapon tier for more damage. The world is **automatically flat**. **Brainrots** are custom mobs you can obtain (e.g. from a dispenser or spawns) and carry—**holding one makes you slightly slower**. No safe zones: someone can still hit you at your base.

**One-line pitch:** Unbreakable base, build with phasable blocks, full PvP with weapon tiers, steal Brainrots to get stronger.

---

## 2. Pillars (locked)

| Pillar | Rule |
|--------|------|
| **Bases** | Every player has a base made of **unbreakable blocks**. UI/text: “Your base.” Respawn point. |
| **No locking** | Bases are always accessible; they’re unbreakable by design, not “locked.” Full PvP at base. |
| **Defense = blocks** | You **take blocks** from your base and **place** them in the world. Others can **mine** your placed blocks. |
| **Your blocks vs others’** | **You phase through your own placed blocks.** You **cannot** phase through other players’ placed blocks. |
| **Combat** | Full PvP. Everyone has **10 hearts**. Weapons have set damage; e.g. starter = 2 hearts/hit. |
| **Progression** | **Steal a Brainrot** → unlock **next weapon** (higher damage). |
| **Brainrots** | Custom mobs. Obtain via **dispenser** (and/or spawns). **Holding a Brainrot** = slightly slower movement. |
| **World** | Map is **automatically flat** (flat world). |

---

## 3. Core loop

- **Spawn** at your base (unbreakable zone, “Your base”).
- **Take blocks** from base, **place** them to create structures. You can walk through **your** blocks; enemies cannot (they’re solid to them) and can **mine** them.
- **Fight** with starter weapon (2 hearts/hit). **Steal Brainrots** to unlock better weapons (more damage per hit).
- **Get Brainrots** from a **Brainrot Dispenser** (at base or in world) and/or from natural spawns; **carry** them for progression but move **slightly slower** while holding one.
- **Raid** others: break their blocks, kill them (they respawn at their base). **Defend** by building and fighting—no invulnerability at base.

---

## 4. Key systems (detailed)

### 4.1 Bases

- **What it is:** A fixed zone made of **unbreakable blocks**. Like the base in “steal a brainrot”: clearly “home,” cannot be broken.
- **Label:** Always shows or says **“Your base”** (e.g. when you enter, or on-screen / action bar).
- **Respawn:** On death, respawn at your base (exact spawn point defined per base).
- **Full PvP:** No safe zone. Other players can hit you and mine your **placed** (non-base) blocks even when you’re at your base.
- **Size/shape:** To be set in implementation (e.g. fixed radius, or a predefined “base platform”). Design: one contiguous unbreakable structure per player.
- **Block source:** Players **take blocks** from this base (or from a “block pool” at base) to place elsewhere. Base blocks themselves are not mineable; only blocks you **place in the world** are.

*Implementation note:* Base blocks must be unbreakable (e.g. block break cancelled + unbreakable tag). Placed blocks in the world need **ownership** (who placed them) so the game knows “yours” (phasable by you, mineable by others) vs “theirs” (solid to you, mineable by you).

---

### 4.2 Blocks: building, phasing, mining

- **Source of blocks:** You get blocks from **your base** (e.g. take from a chest, or from an infinite “block pool” at base, or the base structure itself gives blocks when interacted with). Exact mechanic TBD (e.g. right-click base to get 64 cobble, or a dispenser that gives building blocks).
- **Placement:** You place blocks **in the world** (outside the unbreakable base). When you place a block, it is **yours** (stored: placed-by UUID).
- **Your blocks:**
  - **You can walk through them** (phasable / no collision for you).
  - **Other players cannot** walk through them; they’re solid. Others can **mine** them (normal break time, drops optional—see below).
- **Others’ blocks:**
  - **You cannot** walk through them; they’re solid to you.
  - **You can mine** them (to raid, create paths, etc.).
- **Mining:** No special lock. Anyone can mine any **placed** (non-base) block. Option: mined blocks drop for the miner, or drop for the owner, or despawn—to be decided; recommend drops for miner to encourage raiding.
- **Base blocks:** Part of the “Your base” structure. **Unbreakable.** Not placeable elsewhere; they define the base only.

*Implementation note:* Block ownership = data attached to placed blocks (e.g. block entity, or chunk/block position → UUID). Collision: your blocks = no collision for you; others’ blocks = normal collision. Base blocks = unbreakable (cancel break + possibly a special block type or tag).

---

### 4.3 Combat and health

- **Health:** Everyone has **10 hearts** (20 HP). No variance (no extra hearts from items unless we add later).
- **Weapons:** Different weapons deal different damage. No “tag” respawn—**death** = respawn at base (inventory/XP rules TBD).
- **Starter weapon:** Deals **2 hearts (4 HP)** per hit. Default weapon everyone has (e.g. wooden sword or “BrainCraft starter” item).
- **Weapon progression:** **Steal a Brainrot** → unlock the **next weapon tier** (e.g. tier 2 = 3 hearts/hit, tier 3 = 4 hearts/hit, etc.). Number of tiers TBD (e.g. 3–5).
- **“Steal a Brainrot”:** Defined as obtaining a Brainrot (see 4.5). One stolen Brainrot = one tier unlock? Or N Brainrots per tier? Design: **first Brainrot stolen = tier 2 weapon**, next = tier 3, etc. (concrete numbers in balance pass.)
- **Full PvP:** No safe zones. You can be hit at your base; you can hit others at theirs.

*Implementation note:* Custom weapons or damage modifiers; persistence of “how many Brainrots stolen” per player to gate weapon tiers. Starter weapon given on spawn or in base.

---

### 4.4 Movement: holding a Brainrot

- **While holding a Brainrot** (in hand or in a “carry” slot): player is **slightly slower** (e.g. reduced movement speed).
- **Purpose:** Trade-off: carry Brainrot for progression (weapon unlock) but easier to catch and hit.
- **“Holding”:** Either main hand / offhand, or a dedicated “carried Brainrot” slot/state (e.g. item that represents “captured Brainrot” and applies slowness when in inventory or in hand). Simplest: Brainrot as item in hand = slowness effect.

---

### 4.5 Brainrots: what they are and how you get them

- **What:** Brainrots are **custom Minecraft mobs** (Fabric entities). One or more variants (e.g. weak/fast, tanky, etc.).
- **How you get them (recommended):**
  - **Primary: Brainrot Dispenser.** A block (at each base and/or in the world) that **dispenses Brainrots**. Options:
    - **Use:** Right-click dispenser → spawns one Brainrot entity nearby (or gives “Brainrot in a jar” item that spawns it on use).
    - **Timer:** Dispenser periodically spawns a Brainrot (e.g. every 60 s) until a cap.
  - **Variant:** Different dispenser types or settings give **different Brainrot types** (e.g. dispenser A = type 1, dispenser B = type 2), so players can target which “brainrot” to steal for progression or risk/reward.
  - **Secondary (optional):** **Natural spawns** of Brainrots in the flat world (e.g. at night, or in certain areas) for extra pressure and ways to “steal” without going to a dispenser.
- **“Steal”:** Defined as **bringing a Brainrot to your base** (e.g. kill it and pick up a drop, or “capture” it and deliver), or **capturing** it (e.g. item that captures on use, then you must return to base to “bank” it). Recommendation: **Kill Brainrot → drops “Brainrot” item. Return to your base and deposit/use it → counts as “stolen,” unlocks next weapon tier.** Alternative: “Capture” with a net/item, then deliver to base. Same outcome: one Brainrot delivered = progression step.
- **Dispenser placement:** At least one **Brainrot Dispenser** per base (so everyone can get Brainrots). Optional: neutral dispensers in the world for contested fights over Brainrots.

*Implementation note:* Custom Brainrot entity(ies), custom “Brainrot” item (or entity NBT), Brainrot Dispenser block (block entity + use/timer logic), and a “stolen count” or “delivered to base” check for weapon unlock.

---

### 4.6 World: flat map

- **Map:** **Automatically flat** (Minecraft superflat or custom flat world type).
- **Purpose:** Fair, readable terrain; focus on bases, building, and PvP rather than terrain variation.
- **World gen:** Server/world uses flat world type. No caves or hills unless we add small structures later.

---

## 5. Respawn and death

- **On death (combat, fall, void, etc.):** Respawn at **your base** (fixed spawn point at your base). Inventory/XP: either full keep, full drop, or partial—TBD (recommend: drop inventory on death to encourage risk/reward).
- **No “tag” respawn:** Previous “touch/tag sends you home” is **removed**. Only **death** sends you to base. Combat is lethal (weapons deal damage until 0 hearts).

---

## 6. Progression summary

| Step | What happens |
|------|------------------|
| Start | Spawn at base. Have **starter weapon** (2 hearts/hit). Base has **unbreakable blocks** and a **Brainrot Dispenser**. |
| Build | Take blocks from base, **place** in world. Your blocks = you phase through; others can mine. |
| Get Brainrot | Use **Brainrot Dispenser** (or find spawn). Kill or capture Brainrot, get **Brainrot** item. |
| Steal = deliver | Bring Brainrot to **your base** (deposit / use at base). Counts as “stolen.” |
| Unlock | First Brainrot delivered → **tier 2 weapon** (e.g. 3 hearts/hit). Next → tier 3, etc. |
| Trade-off | **Holding** a Brainrot (in hand or carried) → **slightly slower** movement. |

---

## 7. Technical notes (BrainCraft mod)

- **Engine:** Minecraft Java + Fabric (BrainCraft mod), 1.21.x.
- **Existing:** Spawn-zombie command/keybind (proof-of-concept).
- **To implement:**
  - **Bases:** Unbreakable base zone per player, “Your base” label, respawn at base, block source (take blocks).
  - **Blocks:** Ownership on placed blocks; you phase through yours, others mine yours; you can’t phase through others’; base blocks unbreakable.
  - **Combat:** 10 hearts fixed; starter weapon (2 hearts); weapon tiers gated by “Brainrots stolen” (delivered to base).
  - **Brainrots:** Custom entity(ies); Brainrot Dispenser block (spawns Brainrots, possibly different types); Brainrot item (drop on kill or capture); “delivered to base” = progression.
  - **World:** Flat world type for map.
  - **Movement:** Slowness when holding/carrying a Brainrot.

---

## 8. Open design questions (remaining)

1. **Inventory on death:** Full drop, keep hotbar, or keep all? (Affects risk of carrying Brainrots.)
2. **Number of weapon tiers** and damage per tier (e.g. 2 / 3 / 4 / 5 hearts for tiers 1–4).
3. **Dispenser cooldown:** How often can each player (or each dispenser) give a Brainrot?
4. **Block drops:** When an enemy mines your placed block, who gets the drop—miner or you?
5. **Win condition:** None (sandbox), timed rounds, or first to N kills / N Brainrots delivered?

---

## 9. Document history

| Version | Date | Changes |
|--------|------|--------|
| 0.1 | 2025-02 | Initial GDD: bases, no lock, blocks, mining, touch/tag, respawn at base, Brainrots. |
| 0.2 | 2025-02 | **Locked design:** Base = unbreakable blocks, “Your base”; take blocks to build; you phase through your blocks, others mine them; full PvP, 10 hearts; starter 2 hearts/hit; steal Brainrot → next weapon; flat map; Brainrot Dispenser + optional spawns; holding Brainrot = slower. Removed tag-respawn; combat is lethal only. |

---

*This GDD is the single source of truth for BrainCraft. Update when new decisions are made.*
