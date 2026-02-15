package com.braincraft.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Builds the physical base: stone floor, walls (one side open), roof, grass surroundings, and 2 torches inside.
 */
public final class BaseBuilder {

	private static final int WALL_HEIGHT = 3; // air space inside
	private static final int SURROUNDINGS_RADIUS = 25; // grass out to this radius
	public static void build(ServerLevel level, BlockPos center, int radius) {
		int cx = center.getX();
		int cy = center.getY();
		int cz = center.getZ();
		int floorY = cy - 1;
		int roofY = cy + WALL_HEIGHT;

		// 1. Floor (stone) — full rectangle
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dz = -radius; dz <= radius; dz++) {
				level.setBlock(new BlockPos(cx + dx, floorY, cz + dz), Blocks.STONE.defaultBlockState(), 3);
			}
		}

		// 2. Walls (stone) — perimeter except one side (south) so you can build there
		for (int dy = 0; dy < WALL_HEIGHT; dy++) {
			int y = cy + dy;
			for (int dx = -radius; dx <= radius; dx++) {
				for (int dz = -radius; dz <= radius; dz++) {
					boolean edge = (dx == -radius || dx == radius || dz == radius);
					boolean openSide = (dz == -radius);
					if (edge && !openSide) {
						level.setBlock(new BlockPos(cx + dx, y, cz + dz), Blocks.STONE.defaultBlockState(), 3);
					}
				}
			}
		}

		// 3. Roof (stone) — full rectangle
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dz = -radius; dz <= radius; dz++) {
				level.setBlock(new BlockPos(cx + dx, roofY, cz + dz), Blocks.STONE.defaultBlockState(), 3);
			}
		}

		// 4. Two torches on inside walls (west and east) for light
		BlockState torchWest = Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST);
		BlockState torchEast = Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST);
		level.setBlock(new BlockPos(cx - radius + 1, cy, cz), torchWest, 3);
		level.setBlock(new BlockPos(cx + radius - 1, cy, cz), torchEast, 3);

		// 5. Surroundings: grass (and dirt underneath) outside the base footprint
		for (int dx = -SURROUNDINGS_RADIUS; dx <= SURROUNDINGS_RADIUS; dx++) {
			for (int dz = -SURROUNDINGS_RADIUS; dz <= SURROUNDINGS_RADIUS; dz++) {
				if (Math.abs(dx) <= radius && Math.abs(dz) <= radius) continue;
				BlockPos top = new BlockPos(cx + dx, floorY, cz + dz);
				BlockPos below = top.below();
				level.setBlock(top, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
				level.setBlock(below, Blocks.DIRT.defaultBlockState(), 3);
			}
		}
	}
}
