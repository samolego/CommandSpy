package org.samo_lego.commandspy.mixin;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.jetbrains.annotations.Nullable;
import org.samo_lego.commandspy.CommandSpy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashMap;
import java.util.Map;

import static org.samo_lego.commandspy.CommandSpy.MODID;
import static org.samo_lego.commandspy.CommandSpy.config;

@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityMixin {
    @Unique
    private final SignBlockEntity self = (SignBlockEntity) (Object) this;

    @Shadow
    private static CommandSourceStack createCommandSourceStack(@Nullable Player player, Level level, BlockPos blockPos) {
        throw new AssertionError();
    }

    @Inject(
            method = "executeClickCommandsIfPresent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD

    )
    private void catchSignCommand(Player player, Level level, BlockPos blockPos, boolean bl, CallbackInfoReturnable<Boolean> cir, boolean bl2, Component[] var6, int var7, int var8, Component component, Style style, ClickEvent clickEvent) {
        if (config.logging.logSignCommands) {

            // Getting message style from config
            String message = CommandSpy.config.messages.signMessageStyle;

            // Getting other info
            String dimension = self.getLevel().dimension().location().toString();
            int x = self.getBlockPos().getX();
            int y = self.getBlockPos().getY();
            int z = self.getBlockPos().getZ();

            // Saving those to hashmap for fancy printing with logger
            Map<String, String> valuesMap = new HashMap<>();
            valuesMap.put("dimension", dimension);
            valuesMap.put("command", clickEvent.getValue());
            valuesMap.put("x", String.valueOf(x));
            valuesMap.put("y", String.valueOf(y));
            valuesMap.put("z", String.valueOf(z));
            StrSubstitutor sub = new StrSubstitutor(valuesMap);

            // Logging to console
            CommandSpy.logCommand(sub.replace(message), createCommandSourceStack(player, self.getLevel(), self.getBlockPos()), MODID + ".log.signs");
        }
    }
}
