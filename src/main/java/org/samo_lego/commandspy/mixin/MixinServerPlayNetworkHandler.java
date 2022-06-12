package org.samo_lego.commandspy.mixin;

import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.samo_lego.commandspy.CommandSpy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

import static org.samo_lego.commandspy.CommandSpy.MODID;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinServerPlayNetworkHandler {
	@Shadow
	public ServerPlayer player;

	// Injection for player chatting
	@Inject(
			method = "handleChatCommand",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/server/level/ServerPlayer;createCommandSourceStack()Lnet/minecraft/commands/CommandSourceStack;"
			)
	)
	private void onChatMessage(ServerboundChatCommandPacket packet, CallbackInfo ci) {
		boolean enabled = CommandSpy.config.logging.logPlayerCommands;
		String command = packet.command();

		if(enabled && CommandSpy.shouldLog(command)) {
			// Message style from config
			String message = CommandSpy.config.messages.playerMessageStyle;

			// Other info, later optionally appended to message
			String playername = player.getScoreboardName();
			String uuid = player.getStringUUID();

			// Saving those to hashmap for fancy printing with logger
			Map<String, String> valuesMap = new HashMap<>();
			valuesMap.put("playername", playername);
			valuesMap.put("uuid", uuid);
			valuesMap.put("command", command);
			valuesMap.put("dimension", this.player.getLevel().dimension().location().toString());

			StrSubstitutor sub = new StrSubstitutor(valuesMap);
			// Logging to console
			CommandSpy.logCommand(sub.replace(message), player.createCommandSourceStack(), MODID + ".log.players");
		}
	}
}
