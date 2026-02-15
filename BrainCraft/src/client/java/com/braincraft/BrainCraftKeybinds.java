package com.braincraft;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class BrainCraftKeybinds {

	private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
		Identifier.fromNamespaceAndPath(BrainCraftMod.MOD_ID, "braincraft")
	);
	private static KeyMapping spawnZombieKey;

	public static void register() {
		spawnZombieKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.braincraft.spawn_zombie",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_B,
			CATEGORY
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (spawnZombieKey.consumeClick()) {
				if (client.player != null && client.getConnection() != null) {
					client.getConnection().sendCommand("braincraft spawnzombie");
				}
			}
		});
	}
}
