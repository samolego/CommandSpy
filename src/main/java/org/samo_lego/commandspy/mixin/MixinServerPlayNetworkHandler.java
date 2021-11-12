package org.samo_lego.commandspy.mixin;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
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

@Mixin(ServerPlayNetworkHandler.class)
public abstract class MixinServerPlayNetworkHandler {
	@Shadow
	public ServerPlayerEntity player;

	// Injection for player chatting
	@Inject(
			method = "onChatMessage(Lnet/minecraft/network/packet/c2s/play/ChatMessageC2SPacket;)V",
			at = @At(
				value = "INVOKE",
				target = "net/minecraft/network/NetworkThreadUtils.forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/server/world/ServerWorld;)V",
				shift = At.Shift.AFTER
			)
	)
	private void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
		boolean enabled = CommandSpy.config.logging.logPlayerCommands;
		String command = packet.getChatMessage();

		if(enabled && command.startsWith("/") && CommandSpy.shouldLog(command)) {
			// Message style from config
			String message = CommandSpy.config.messages.playerMessageStyle;

			// Other info, later optionally appended to message
			String playername = player.getEntityName();
			String uuid = player.getUuidAsString();

			// Saving those to hashmap for fancy printing with logger
			Map<String, String> valuesMap = new HashMap<>();
			valuesMap.put("playername", playername);
			valuesMap.put("uuid", uuid);
			valuesMap.put("command", command);
			valuesMap.put("dimension", this.player.getWorld().getRegistryKey().getValue().toString());

			StrSubstitutor sub = new StrSubstitutor(valuesMap);
			// Logging to console
			CommandSpy.logCommand(sub.replace(message), player.getCommandSource(), MODID + ".log.players");
		}
	}
}
