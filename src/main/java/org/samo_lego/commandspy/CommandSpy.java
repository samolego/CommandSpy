package org.samo_lego.commandspy;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.io.File;

public class CommandSpy implements ModInitializer {
	public static final Logger LOGGER = (Logger) LogManager.getLogger();
	public static SpyConfig config;

	@Override
	public void onInitialize() {
		// Info that mod is loading ...
		LOGGER.info("Loading CommandSpy by samo_lego.");
		// Loading config
		config = SpyConfig.loadConfig(new File("./config/CommandSpyConfig.json"));
	}
}
