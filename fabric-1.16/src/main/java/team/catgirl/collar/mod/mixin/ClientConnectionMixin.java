package team.catgirl.collar.mod.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.catgirl.collar.mod.events.ClientLoginCallback;
import team.catgirl.collar.mod.events.ClientDisconnectCallback;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "disconnect", at = @At("HEAD"))
    private void onDisconnect(CallbackInfo info) {
        ClientDisconnectCallback.EVENT.invoker().onDisconnected();
    }

    @Inject(at = {@At(value = "HEAD",
            target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;)V")},
            method = {
                    "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V"})
    private void onChannelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo info) {
        if (packet instanceof LoginSuccessS2CPacket) {
            ClientLoginCallback.EVENT.invoker().onLogin();
        }
    }
}
