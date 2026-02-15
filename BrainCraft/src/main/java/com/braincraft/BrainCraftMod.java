package com.braincraft;

import com.braincraft.base.BaseGameplay;
import com.braincraft.command.BrainCraftCommands;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrainCraftMod implements ModInitializer {
	public static final String MOD_ID = "braincraft";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("BrainCraft initialized!");
		BrainCraftCommands.register();
		BaseGameplay.register();
	}
}
