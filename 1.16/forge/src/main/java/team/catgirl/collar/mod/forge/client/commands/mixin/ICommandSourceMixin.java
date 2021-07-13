package team.catgirl.collar.mod.forge.client.commands.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import team.catgirl.collar.mod.forge.client.commands.ICommandSource;


@Mixin(ClientCommandSource.class)
public abstract class ICommandSourceMixin implements ICommandSource {
    @Shadow @Final private MinecraftClient client;

    @Override
    public void sendFeedback(Text msg) {
        client.inGameHud.addChatMessage(MessageType.SYSTEM, msg, Util.NIL_UUID);
    }

    @Override
    public void sendError(Text msg) {
        client.inGameHud.addChatMessage(MessageType.SYSTEM, new LiteralText("").append(msg).formatted(Formatting.RED), Util.NIL_UUID);
    }

}
