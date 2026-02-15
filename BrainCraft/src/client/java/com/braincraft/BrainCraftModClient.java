package com.braincraft;

import net.fabricmc.api.ClientModInitializer;

public class BrainCraftModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BrainCraftKeybinds.register();
	}
}
