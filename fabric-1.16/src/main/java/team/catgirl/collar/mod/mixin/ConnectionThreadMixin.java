package team.catgirl.collar.mod.mixin;

import net.minecraft.client.gui.screen.ConnectScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.catgirl.collar.mod.events.ClientConnectCallback;

@Mixin(ConnectScreen.class)
public abstract class ConnectionThreadMixin {
	
	@Inject(method = "connect", at = @At("TAIL"))
	public void connect(CallbackInfo callbackInfo) {
		ClientConnectCallback.EVENT.invoker().onConnected();
	}
}
