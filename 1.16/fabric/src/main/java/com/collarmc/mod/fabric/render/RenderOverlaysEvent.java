<<<<<<< HEAD:1.16/fabric/src/main/java/com/collarmc/mod/fabric/render/RenderOverlaysEvent.java
package com.collarmc.mod.fabric.render;
=======
package com.collarmc.collar.mod.glue.render;
>>>>>>> topic/merge:1.16/glue/src/main/java/com/collarmc/collar/mod/glue/render/RenderOverlaysEvent.java

import net.minecraft.client.util.math.MatrixStack;

public final class RenderOverlaysEvent {
    public final MatrixStack matrixStack;
    public final float deltaTime;

    public RenderOverlaysEvent(MatrixStack matrixStack, float deltaTime) {
        this.matrixStack = matrixStack;
        this.deltaTime = deltaTime;
    }
}
