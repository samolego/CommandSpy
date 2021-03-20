package org.samo_lego.commandspy.mixin;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.samo_lego.commandspy.CommandSpy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashMap;
import java.util.Map;

import static org.samo_lego.commandspy.CommandSpy.config;

@Mixin(SignBlockEntity.class)
public class SignBlockEntityMixin {
    @Inject(
            method = "onActivate(Lnet/minecraft/entity/player/PlayerEntity;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/command/CommandManager;execute(Lnet/minecraft/server/command/ServerCommandSource;Ljava/lang/String;)I"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void catchSignCommand(PlayerEntity player, CallbackInfoReturnable<Boolean> cir, Text[] texts, int i, Text currentText, Style style, ClickEvent clickEvent) {
        if(config.logging.logSignCommands) {
            SignBlockEntity sign = (SignBlockEntity) (Object) this;

            // Getting message style from config
            String message = CommandSpy.config.messages.commandBlockMessageStyle;

            // Getting other info
            String dimension = sign.getWorld().getRegistryKey().getValue().toString();
            int x = sign.getPos().getX();
            int y = sign.getPos().getY();
            int z = sign.getPos().getZ();

            // Saving those to hashmap for fancy printing with logger
            Map<String, String> valuesMap = new HashMap<>();
            valuesMap.put("dimension", String.valueOf(dimension));
            valuesMap.put("command", clickEvent.getValue());
            valuesMap.put("x", String.valueOf(x));
            valuesMap.put("y", String.valueOf(y));
            valuesMap.put("z", String.valueOf(z));
            StrSubstitutor sub = new StrSubstitutor(valuesMap);

            // Logging to console
            CommandSpy.logCommand(sub.replace(message), sign.getCommandSource((ServerPlayerEntity) player));
        }
    }
}
