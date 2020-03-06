package org.samo_lego.commandspy.mixin;

import net.minecraft.block.CommandBlock;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.samo_lego.commandspy.CommandSpy;
import org.samo_lego.commandspy.SpyConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
		String message = packet.getChatMessage();
		boolean enabled = CommandSpy.config.main.logPlayerCommands;

		if(enabled && message.startsWith("/")) {
			boolean appendUUID = CommandSpy.config.main.appendUUID;
			CommandSpy.LOGGER.info(player.getEntityName() + " used command: " + message);
		}
	}
}
