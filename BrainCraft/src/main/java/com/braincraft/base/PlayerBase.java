package com.braincraft.base;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.UUID;

/**
 * A player's base: a 3D stone structure (floor, walls, roof) in a dimension.
 * Blocks inside are unbreakable; player respawns here on death.
 */
public record PlayerBase(
	UUID owner,
	BlockPos center,
	int radius,
	int floorY,
	int roofY,
	ResourceKey<Level> dimension
) {
	/** Respawn position: inside the room, one block above floor (center level). */
	public BlockPos respawnBlockPos() {
		return new BlockPos(center.getX(), center.getY(), center.getZ());
	}

	/** True if this block is inside the base structure (3D box). */
	public boolean contains(BlockPos pos, ResourceKey<Level> dim) {
		if (!dimension.equals(dim)) return false;
		int dx = Math.abs(pos.getX() - center.getX());
		int dz = Math.abs(pos.getZ() - center.getZ());
		return dx <= radius && dz <= radius && pos.getY() >= floorY && pos.getY() <= roofY;
	}
}
