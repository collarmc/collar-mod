package team.catgirl.collar.mod.fabric.render;

import net.minecraft.client.util.math.MatrixStack;

public final class RenderOverlaysEvent {
    public final MatrixStack matrixStack;
    public final float deltaTime;

    public RenderOverlaysEvent(MatrixStack matrixStack, float deltaTime) {
        this.matrixStack = matrixStack;
        this.deltaTime = deltaTime;
    }
}
