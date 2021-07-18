<<<<<<< HEAD:1.16/fabric/src/main/java/com/collarmc/mod/fabric/mixin/ClientWorldMixin.java
package com.collarmc.mod.fabric.mixin;
=======
package com.collarmc.collar.mod.glue.mixin;
>>>>>>> topic/merge:1.16/glue/src/main/java/com/collarmc/collar/mod/glue/mixin/ClientWorldMixin.java

import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.collarmc.plastic.Plastic;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(CallbackInfo callbackInfo) {
        Plastic.getPlastic().world.onWorldLoaded();
    }
}
