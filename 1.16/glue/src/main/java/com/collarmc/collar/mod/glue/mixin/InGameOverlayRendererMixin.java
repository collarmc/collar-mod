<<<<<<< HEAD:1.16/fabric/src/main/java/com/collarmc/mod/fabric/mixin/InGameOverlayRendererMixin.java
package com.collarmc.mod.fabric.mixin;
=======
package com.collarmc.collar.mod.glue.mixin;
>>>>>>> topic/merge:1.16/glue/src/main/java/com/collarmc/collar/mod/glue/mixin/InGameOverlayRendererMixin.java

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.collarmc.plastic.Plastic;
<<<<<<< HEAD:1.16/fabric/src/main/java/com/collarmc/mod/fabric/mixin/InGameOverlayRendererMixin.java
import com.collarmc.mod.fabric.render.RenderOverlaysEvent;
=======
import com.collarmc.collar.mod.glue.render.RenderOverlaysEvent;
>>>>>>> topic/merge:1.16/glue/src/main/java/com/collarmc/collar/mod/glue/mixin/InGameOverlayRendererMixin.java

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {

    @Inject(method = "renderOverlays", at = @At("HEAD"))
    private static void renderOverlays(MinecraftClient minecraftClient, MatrixStack matrixStack, CallbackInfo callbackInfo) {
//        Plastic.getPlastic().onRenderOverlays();
        Plastic.getPlastic().eventBus.dispatch(new RenderOverlaysEvent(matrixStack, minecraftClient.getTickDelta()));
    }
}
