package com.braincraft.base;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * In-memory registry of player bases. One base per player.
 */
public final class BaseManager {
	private static final Map<UUID, PlayerBase> BASES = new HashMap<>();

	public static PlayerBase getBase(UUID playerId) {
		return BASES.get(playerId);
	}

	public static void setBase(UUID playerId, PlayerBase base) {
		if (base == null) {
			BASES.remove(playerId);
		} else {
			BASES.put(playerId, base);
		}
	}

	public static void clearBase(UUID playerId) {
		BASES.remove(playerId);
	}

	/** Returns the base that contains this position in this dimension, or null. */
	public static PlayerBase getBaseAt(BlockPos pos, ResourceKey<Level> dimension) {
		for (PlayerBase base : BASES.values()) {
			if (base.contains(pos, dimension)) {
				return base;
			}
		}
		return null;
	}
}
