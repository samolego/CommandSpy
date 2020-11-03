package org.samo_lego.commandspy.mixin;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.samo_lego.commandspy.CommandSpy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class MixinServerPlayNetworkHandler {
	@Shadow
	public ServerPlayerEntity player;

	@Unique
	private final Map<String, String> valuesMap = new HashMap<>();

	// Injection for player chatting
	@Inject(
			method = "onGameMessage(Lnet/minecraft/network/packet/c2s/play/ChatMessageC2SPacket;)V",
			at = @At(
				value = "INVOKE",
				target = "net/minecraft/network/NetworkThreadUtils.forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/server/world/ServerWorld;)V",
				shift = At.Shift.AFTER
			)
	)
	private void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
		boolean enabled = CommandSpy.config.main.logPlayerCommands;
		String command = packet.getChatMessage();

		if(enabled && command.startsWith("/") && CommandSpy.shouldLog(command)) {
			// Message style from config
			String message = CommandSpy.config.main.playerMessageStyle;

			// Other info, later optionally appended to message
			String playername = player.getEntityName();
			String uuid = player.getUuidAsString();

			// Saving those to hashmap for fancy printing with logger
			valuesMap.put("playername", playername);
			valuesMap.put("uuid", uuid);
			valuesMap.put("command", command);

			StrSubstitutor sub = new StrSubstitutor(valuesMap);
			// Logging to console
			CommandSpy.LOGGER.info(sub.replace(message));
		}
	}
}
