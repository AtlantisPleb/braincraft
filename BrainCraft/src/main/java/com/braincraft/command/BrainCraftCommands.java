package com.braincraft.command;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.phys.Vec3;

public final class BrainCraftCommands {

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(
				Commands.literal("braincraft")
					.then(Commands.literal("spawnzombie")
						.executes(BrainCraftCommands::spawnZombie))
			);
		});
	}

	private static int spawnZombie(CommandContext<CommandSourceStack> context) {
		CommandSourceStack source = context.getSource();
		if (!(source.getEntity() instanceof ServerPlayer player)) {
			return 0;
		}
		if (!(player.level() instanceof ServerLevel level)) {
			return 0;
		}
		Vec3 pos = player.position();
		Vec3 look = player.getLookAngle();
		double x = pos.x + look.x * 2;
		double y = pos.y;
		double z = pos.z + look.z * 2;
		BlockPos blockPos = BlockPos.containing(x, y, z);
		Entity zombie = EntityType.ZOMBIE.spawn(level, blockPos, EntitySpawnReason.COMMAND);
		if (zombie != null) {
			source.sendSuccess(() -> net.minecraft.network.chat.Component.literal("Spawned a zombie!"), false);
			return 1;
		}
		return 0;
	}
}
