<<<<<<< HEAD:1.16/fabric/src/main/java/com/collarmc/mod/fabric/render/WorldRenderEvent.java
package com.collarmc.mod.fabric.render;
=======
package com.collarmc.collar.mod.glue.render;
>>>>>>> topic/merge:1.16/glue/src/main/java/com/collarmc/collar/mod/glue/render/WorldRenderEvent.java

import net.minecraft.client.util.math.MatrixStack;

public final class WorldRenderEvent {
    public final float tickDelta;
    public final MatrixStack matrixStack;

    public WorldRenderEvent(float tickDelta, MatrixStack matrixStack) {
        this.tickDelta = tickDelta;
        this.matrixStack = matrixStack;
    }
}
