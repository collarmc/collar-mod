package team.catgirl.collar.mod.fabric.render;

import net.minecraft.client.util.math.MatrixStack;

public final class WorldRenderEvent {
    public final float tickDelta;
    public final MatrixStack matrixStack;

    public WorldRenderEvent(float tickDelta, MatrixStack matrixStack) {
        this.tickDelta = tickDelta;
        this.matrixStack = matrixStack;
    }
}
