package org.samo_lego.commandspy.mixin;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.samo_lego.commandspy.CommandSpy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashMap;
import java.util.Map;

import static org.samo_lego.commandspy.CommandSpy.MODID;
import static org.samo_lego.commandspy.CommandSpy.config;

@Mixin(SignBlockEntity.class)
public class SignBlockEntityMixin {
    @Inject(
            method = "executeClickCommands",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD

    )
    private void catchSignCommand(ServerPlayer player, CallbackInfoReturnable<Boolean> cir, Component[] texts, int i, int j, Component text, Style style,  ClickEvent clickEvent) {
        if(config.logging.logSignCommands) {
            SignBlockEntity sign = (SignBlockEntity) (Object) this;

            // Getting message style from config
            String message = CommandSpy.config.messages.signMessageStyle;

            // Getting other info
            String dimension = sign.getLevel().dimension().location().toString();
            int x = sign.getBlockPos().getX();
            int y = sign.getBlockPos().getY();
            int z = sign.getBlockPos().getZ();

            // Saving those to hashmap for fancy printing with logger
            Map<String, String> valuesMap = new HashMap<>();
            valuesMap.put("dimension", String.valueOf(dimension));
            valuesMap.put("command", clickEvent.getValue());
            valuesMap.put("x", String.valueOf(x));
            valuesMap.put("y", String.valueOf(y));
            valuesMap.put("z", String.valueOf(z));
            StrSubstitutor sub = new StrSubstitutor(valuesMap);

            // Logging to console
            CommandSpy.logCommand(sub.replace(message), sign.createCommandSourceStack((ServerPlayer) player), MODID + ".log.signs");
        }
    }
}
