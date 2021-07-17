package team.catgirl.collar.mod.fabric.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;
import team.catgirl.collar.api.waypoints.Waypoint;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.mod.common.CollarService;
import team.catgirl.collar.mod.fabric.mixin.MinecraftClientFieldMixin;
import team.catgirl.plastic.Plastic;
import team.catgirl.plastic.player.Player;
import team.catgirl.pounce.Preference;
import team.catgirl.pounce.Subscribe;

import static net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer.BEAM_TEXTURE;

public class WaypointRenderer {

    private final Plastic plastic;
    private final CollarService collarService;

    public WaypointRenderer(Plastic plastic, CollarService collarService) {
        this.plastic = plastic;
        this.collarService = collarService;
    }

    @Subscribe(Preference.CALLER)
    public void renderWaypointLabel(WorldRenderEvent event) {
//
//        ProjectionUtil.INSTANCE.
//
//        MatrixStack matrix = event.matrixStack;
//        collarService.getCollar().ifPresent(collar -> {
//            if (collar.getState() != Collar.State.CONNECTED) {
//                return;
//            }
//            collar.location().privateWaypoints().forEach(waypoint -> renderWaypointLabel(event.matrixStack, waypoint, plastic.world.currentPlayer()));
//        });
    }

    @Subscribe(Preference.CALLER)
    public void renderWaypointBeacon(WorldRenderEvent event) {
        collarService.getCollar().ifPresent(collar -> {
            if (collar.getState() != Collar.State.CONNECTED && !collar.configuration.debugConfiguration.waypoints) {
                return;
            }
            collar.location().privateWaypoints().stream()
                    .filter(this::currentDimension)
                    .forEach(waypoint -> renderWaypointBeacon(event.matrixStack, waypoint, plastic.world.currentPlayer()));
            collar.groups().groups().forEach(group -> {
                collar.location().groupWaypoints(group).stream()
                        .filter(this::currentDimension)
                        .forEach(waypoint -> renderWaypointBeacon(event.matrixStack, waypoint, plastic.world.currentPlayer()));
            });
        });
    }

    boolean currentDimension(Waypoint waypoint) {
        return plastic.world.currentPlayer().location().dimension.equals(waypoint.location.dimension);
    }

    private void renderWaypointBeacon(MatrixStack matrix, Waypoint waypoint, Player player) {
        MinecraftClientFieldMixin minecraftClientMixin = (MinecraftClientFieldMixin)MinecraftClient.getInstance();
        matrix.push();
        long time = MinecraftClient.getInstance().world.getTime();
        Vec3d pos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
        matrix.translate(-pos.getX() + waypoint.location.x, -pos.getY() + waypoint.location.y, -pos.getZ() + waypoint.location.z);
        renderBeacon(matrix, minecraftClientMixin.bufferBuilders().getEntityVertexConsumers(), player.yaw(), time, 0, 1024, DyeColor.PINK.getColorComponents());
        matrix.pop();
    }

    private static void renderBeacon(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, float f, long l, int i, int j, float[] color) {
        BeaconBlockEntityRenderer.renderLightBeam(matrixStack, vertexConsumerProvider, BEAM_TEXTURE, f, 1.0F, l, i, j, color, 0.2F, 0.25F);
    }
}
