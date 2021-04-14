package team.catgirl.collar.mod.mixin;

import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.catgirl.collar.mod.events.WorldLoadedCallback;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(CallbackInfo callbackInfo) {
        WorldLoadedCallback.EVENT.invoker().onLoaded();
    }
}
