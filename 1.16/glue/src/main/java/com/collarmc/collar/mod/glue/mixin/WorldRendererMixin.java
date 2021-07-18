<<<<<<< HEAD:1.16/fabric/src/main/java/com/collarmc/mod/fabric/mixin/WorldRendererMixin.java
package com.collarmc.mod.fabric.mixin;
=======
package com.collarmc.collar.mod.glue.mixin;
>>>>>>> topic/merge:1.16/glue/src/main/java/com/collarmc/collar/mod/glue/mixin/WorldRendererMixin.java

import com.collarmc.collar.mod.glue.render.WorldRenderEvent;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
<<<<<<< HEAD:1.16/fabric/src/main/java/com/collarmc/mod/fabric/mixin/WorldRendererMixin.java
import com.collarmc.mod.fabric.render.WorldRenderEvent;
=======
>>>>>>> topic/merge:1.16/glue/src/main/java/com/collarmc/collar/mod/glue/mixin/WorldRendererMixin.java
import com.collarmc.plastic.Plastic;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Inject(method = "render", at = @At("TAIL"))
    public void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
        Plastic.getPlastic().eventBus.dispatch(new WorldRenderEvent(tickDelta, matrices));
    }
}
