package team.catgirl.collar.mod.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import team.catgirl.collar.api.location.Location;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.mod.common.CollarService;
import team.catgirl.plastic.Plastic;
import team.catgirl.pounce.Preference;
import team.catgirl.pounce.Subscribe;

public final class TracerRenderer {

    private final Plastic plastic;
    private final CollarService service;

    public TracerRenderer(Plastic plastic, CollarService service) {
        this.plastic = plastic;
        this.service = service;
    }

    @Subscribe(Preference.CALLER)
    public void onRender(RenderOverlaysEvent event) {
        service.getCollar().ifPresent(collar -> {
            if (collar.getState() != Collar.State.CONNECTED || !collar.isDebug()) {
                return;
            }
            Window window = MinecraftClient.getInstance().getWindow();

            float r = DyeColor.PINK.getColorComponents()[0];
            float g = DyeColor.PINK.getColorComponents()[1];
            float b = DyeColor.PINK.getColorComponents()[2];
            float a = 0;

            collar.location().playerLocations().forEach((player, location) -> {
                // Don't show where other players are if they are not in the same dimension
                if (!location.dimension.equals(plastic.world.currentPlayer().location().dimension)) {
                    return;
                }
                Vector3f feetPosition = lerpPos(location, event.deltaTime);
                int screenWidth = window.getScaledWidth();
                int screenHeight = window.getHeight();
                GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                RenderSystem.enableBlend();
                RenderSystem.disableTexture();
                RenderSystem.defaultBlendFunc();

                Tessellator tessellator = RenderSystem.renderThreadTesselator();
                BufferBuilder buffer = tessellator.getBuffer();
                buffer.begin(GL11.GL_LINE_STRIP, VertexFormats.POSITION_COLOR);
                Matrix4f model = event.matrixStack.peek().getModel();
                buffer.vertex(model, (screenWidth / 2), screenHeight / 2, 0).color(r, g, b, a).next();
                buffer.vertex(model, feetPosition.x, feetPosition.y, 0).color(r, g, b, a).next();
                buffer.end();
                BufferRenderer.draw(buffer);
                RenderSystem.enableTexture();
                RenderSystem.disableBlend();
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
            });
        });
    }

    private static Vector3f lerpPos(Location location, final float alpha) {
        Location lastRenderLocation = location; // TODO: figuree out how to handle this
        float x = MathHelper.lerp(alpha, lastRenderLocation.x.floatValue(), location.x.floatValue());
        float y = MathHelper.lerp(alpha, lastRenderLocation.y.floatValue(), location.y.floatValue());
        float z = MathHelper.lerp(alpha, lastRenderLocation.z.floatValue(), location.z.floatValue());
        return new Vector3f(x, y, z);
    }
}
