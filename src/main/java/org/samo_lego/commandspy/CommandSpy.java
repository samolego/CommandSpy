package org.samo_lego.commandspy;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.io.File;

public class CommandSpy implements ModInitializer {
	public static final Logger LOGGER = (Logger) LogManager.getLogger();
	public static SpyConfig config;
	private static final File configDirectory = FabricLoader.getInstance().getConfigDirectory();

	@Override
	public void onInitialize() {
		// Info that mod is loading ...
		LOGGER.info("Loading CommandSpy by samo_lego.");
		// Loading config
		config = SpyConfig.loadConfig(new File(configDirectory.toString() + "/CommandSpyConfig.json"));
	}

	public static  boolean canSend(String command) {
		if(command.startsWith("/"))
			command = command.substring(1);

		// If command is on the (config) blacklist, it shouldn't be logged to console
		return !config.blacklistedCommands.contains( command.split(" ")[0] );
	}
}
