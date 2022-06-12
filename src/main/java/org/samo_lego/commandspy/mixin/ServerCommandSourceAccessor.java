package org.samo_lego.commandspy.mixin;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CommandSourceStack.class)
public interface ServerCommandSourceAccessor {
    @Invoker("broadcastToAdmins")
    void logCommandToOps(Component command);
}
