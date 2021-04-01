package org.samo_lego.commandspy;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.samo_lego.commandspy.mixin.ServerCommandSourceAccessor;
import org.samo_lego.commandspy.permission.PermissionHelper;

import java.io.File;
import java.nio.file.Path;

public class CommandSpy implements ModInitializer {
	public static final Logger LOGGER = (Logger) LogManager.getLogger("CommandSpy");
	public static final String MODID = "commandspy";

	public static boolean luckpermsLoaded;
	public static SpyConfig config;
	private static final Path configDirectory = FabricLoader.getInstance().getConfigDir();

    @Override
	public void onInitialize() {
		// Info that mod is loading ...
		LOGGER.info("Loading CommandSpy by samo_lego.");
		// Loading config
		config = SpyConfig.loadConfig(new File(configDirectory.toString() + "/CommandSpyConfig.json"));

		luckpermsLoaded = FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v0");
	}

	/**
	 * Determines whether command should be logged, depending on config
	 * @param command command string
	 * @return true if it should be logged, otherwise false
	 */
	public static  boolean shouldLog(String command) {
		if(command.startsWith("/"))
			command = command.substring(1);

		// If command is on the (config) blacklist, it shouldn't be logged to console
		return !config.blacklistedCommands.contains( command.split(" ")[0] );
	}

	/**
	 * Logs command to the console
	 * @param command command to log
	 * @param src the command source
	 * @param permission the permission to use if LuckPerms is loaded
	 */
	public static void logCommand(String command, ServerCommandSource src, String permission) {
		if(config.logging.logToConsole)
			LOGGER.info(command);

		MutableText cmd = new LiteralText(command).formatted(Formatting.GRAY);
		if(luckpermsLoaded)
			// LuckPerms is loaded, so we will make additional permission check
			src.getMinecraftServer().getPlayerManager().getPlayerList().forEach(player -> {
				if(PermissionHelper.checkPermission(player, permission))
					player.sendMessage(cmd, false);
			});
		else if(config.logging.logToOps)
			// Vanilla way - all ops get message
			((ServerCommandSourceAccessor) src).logCommandToOps(cmd);
	}
}
