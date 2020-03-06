package org.samo_lego.commandspy.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;
import org.samo_lego.commandspy.CommandSpy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(CommandBlock.class)
public abstract class MixinCommandBlock {

    // Injection for command block executing commands
    @Inject(method = "execute(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/CommandBlockExecutor;Z)V", at = @At(value = "INVOKE"))
    private void execute(BlockState state, World world, BlockPos pos, CommandBlockExecutor executor, boolean hasCommand, CallbackInfo ci) {
        boolean enabled = CommandSpy.config.main.logCommandBlockCommands;
        String format = "Command block in %s at X: %s Y: %s Z: %s executed command: %s";

        if(enabled && hasCommand) {
            CommandSpy.LOGGER.info(
                    String.format(
                            format,
                            world.dimension.toString(),
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            executor.getCommand()
                    )
            );
        }
    }
}