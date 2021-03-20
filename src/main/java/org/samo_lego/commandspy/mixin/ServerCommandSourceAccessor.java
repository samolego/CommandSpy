package org.samo_lego.commandspy.mixin;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerCommandSource.class)
public interface ServerCommandSourceAccessor {
    @Invoker("sendToOps")
    void logCommandToOps(Text command);
}
