package team.catgirl.collar.mod.forge.client.commands.mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.catgirl.collar.mod.forge.client.commands.ClientCommands;
import team.catgirl.collar.mod.forge.client.commands.ICommandSource;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkMixin {

    @Shadow private CommandDispatcher<CommandSource> commandDispatcher;

    @Shadow @Final private ClientCommandSource commandSource;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "onCommandTree", at = @At("RETURN"))
    private void onCommandTree(CommandTreeS2CPacket p_195511_1_, CallbackInfo ci){
        ClientCommands.addCommands((CommandDispatcher) commandDispatcher, (ICommandSource) commandSource);
    }
}
