package com.braincraft.command;

import com.braincraft.base.BaseBuilder;
import com.braincraft.base.BaseManager;
import com.braincraft.base.PlayerBase;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public final class BrainCraftCommands {

	private static final int BASE_RADIUS = 5;

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(
				Commands.literal("braincraft")
					.then(Commands.literal("spawnzombie")
						.executes(BrainCraftCommands::spawnZombie))
					.then(Commands.literal("base")
						.then(Commands.literal("set")
							.executes(BrainCraftCommands::baseSet))
						.then(Commands.literal("clear")
							.executes(BrainCraftCommands::baseClear))
						.then(Commands.literal("show")
							.executes(BrainCraftCommands::baseShow)))
			);
		});
	}

	private static int baseSet(CommandContext<CommandSourceStack> context) {
		CommandSourceStack source = context.getSource();
		if (!(source.getEntity() instanceof ServerPlayer player)) {
			return 0;
		}
		if (!(player.level() instanceof ServerLevel level)) {
			return 0;
		}
		BlockPos center = player.blockPosition();
		int floorY = center.getY() - 1;
		int roofY = center.getY() + 3; // floor + 3 blocks air + roof
		PlayerBase base = new PlayerBase(
			player.getUUID(),
			center,
			BASE_RADIUS,
			floorY,
			roofY,
			player.level().dimension()
		);
		BaseBuilder.build(level, center, BASE_RADIUS);
		BaseManager.setBase(player.getUUID(), base);
		giveStarterBlocks(player);
		source.sendSuccess(() -> Component.literal("Base built at " + center.getX() + ", " + center.getY() + ", " + center.getZ() + " (stone room + grass surroundings). You have blocks and torches to build with."), false);
		return 1;
	}

	private static void giveStarterBlocks(ServerPlayer player) {
		if (!player.getInventory().add(new ItemStack(Items.COBBLESTONE, 64))) {
			player.drop(new ItemStack(Items.COBBLESTONE, 64), false);
		}
		if (!player.getInventory().add(new ItemStack(Items.DIRT, 32))) {
			player.drop(new ItemStack(Items.DIRT, 32), false);
		}
		if (!player.getInventory().add(new ItemStack(Items.TORCH, 2))) {
			player.drop(new ItemStack(Items.TORCH, 2), false);
		}
	}

	private static int baseClear(CommandContext<CommandSourceStack> context) {
		CommandSourceStack source = context.getSource();
		if (!(source.getEntity() instanceof ServerPlayer player)) {
			return 0;
		}
		BaseManager.clearBase(player.getUUID());
		source.sendSuccess(() -> Component.literal("Base cleared."), false);
		return 1;
	}

	private static int baseShow(CommandContext<CommandSourceStack> context) {
		CommandSourceStack source = context.getSource();
		if (!(source.getEntity() instanceof ServerPlayer player)) {
			return 0;
		}
		PlayerBase base = BaseManager.getBase(player.getUUID());
		if (base == null) {
			source.sendSuccess(() -> Component.literal("No base set. Use /braincraft base set to set your base."), false);
		} else {
			BlockPos c = base.center();
			source.sendSuccess(() -> Component.literal("Your base: " + c.getX() + ", " + c.getY() + ", " + c.getZ() + " (radius " + base.radius() + ")"), false);
		}
		return 1;
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
