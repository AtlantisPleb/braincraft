package com.braincraft.base;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Registers base-related gameplay: unbreakable base region, respawn at base, "Your base" action bar.
 */
public final class BaseGameplay {

	private static final int ACTION_BAR_INTERVAL_TICKS = 40; // every 2 seconds

	public static void register() {
		// Cancel block break inside any base region
		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
			if (world.isClientSide()) return true;
			if (!(world instanceof ServerLevel level)) return true;
			PlayerBase base = BaseManager.getBaseAt(pos, level.dimension());
			if (base != null) {
				player.displayClientMessage(Component.literal("You cannot break blocks inside a base!"), true);
				return false; // cancel
			}
			return true;
		});

		// Respawn at own base on death
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			PlayerBase base = BaseManager.getBase(newPlayer.getUUID());
			if (base == null) return;
			ServerLevel currentLevel = (ServerLevel) newPlayer.level();
			MinecraftServer server = currentLevel.getServer();
			ServerLevel level = server.getLevel(base.dimension());
			if (level == null) return;
			BlockPos respawnPos = base.respawnBlockPos();
			double x = respawnPos.getX() + 0.5;
			double y = respawnPos.getY();
			double z = respawnPos.getZ() + 0.5;
			newPlayer.teleportTo(level, x, y, z, java.util.Set.of(), newPlayer.getYRot(), newPlayer.getXRot(), false);
		});

		// "Your base" action bar when standing in your base (throttled)
		ServerTickEvents.END_SERVER_TICK.register(BaseGameplay::tickActionBar);
	}

	private static void tickActionBar(MinecraftServer server) {
		if (server.getTickCount() % ACTION_BAR_INTERVAL_TICKS != 0) return;
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			PlayerBase base = BaseManager.getBase(player.getUUID());
			if (base == null) continue;
			if (base.contains(player.blockPosition(), player.level().dimension())) {
				player.displayClientMessage(Component.literal("Your base"), true); // true = action bar
			}
		}
	}
}
