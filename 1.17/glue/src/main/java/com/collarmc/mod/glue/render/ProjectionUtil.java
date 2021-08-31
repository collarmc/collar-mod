package com.collarmc.mod.glue.render;

import net.minecraft.util.math.Vec3d;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

// Created by green lizard
public enum ProjectionUtil {

    INSTANCE;

    private IntBuffer viewport;
    private final int[] viewPortArray = new int[16];

    private FloatBuffer modelView;
    private FloatBuffer projection;
    private int windowWidth, windowHeight;
    private double windowScale = 1.0;

    private Matrix4f viewProjectionMatrix;

    private boolean hasBeenInitialized, hasBeenUpdated;

    private void initBuffers() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            viewport = stack.mallocInt(16);
            modelView = stack.mallocFloat(16);
            projection = stack.mallocFloat(16);
        }
    }

    public void updateBuffers() {
        // malloc to the temp use 64KB (by default) memory stack
        initBuffers();

        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelView);
        GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projection);

        windowWidth = viewport.get(2);
        windowHeight = viewport.get(3);

        viewPortArray[2] = windowWidth;
        viewPortArray[3] = windowHeight;

        Matrix4f projectionMatrix = new Matrix4f(projection);
        Matrix4f modelViewMatrix = new Matrix4f(modelView);

        viewProjectionMatrix = projectionMatrix.mul(modelViewMatrix);

        hasBeenUpdated = true;
    }

    public Projection projection(double x, double y, double z) {
        return this.projection(x, y, z, false);
    }

    public Projection projection(double x, double y, double z, boolean projectOutside) {
        if (hasBeenUpdated) {
            Vector3f screenCoordsJOML = viewProjectionMatrix.project((float) x, (float) y, (float) z, viewPortArray, new Vector3f());

            float sX = screenCoordsJOML.x();
            float sY = screenCoordsJOML.y();
            float sZ = screenCoordsJOML.z();

            double scaledScreenX = sX * this.windowScale;
            double scaledScreenY = (this.windowHeight - sY) * this.windowScale;

            if (sZ >= 0.0 && sZ <= 1.0) {
                return new Projection(scaledScreenX, scaledScreenY, Projection.Type.INSIDE);
            }
            else if(projectOutside) {
                Vec3d pos = new Vec3d(x, y, z);
                Vec3d c = new Vec3d(0, 0, 0);
                Vec3d flipped = pos.add(c.subtract(pos).multiply(2));

                screenCoordsJOML = viewProjectionMatrix.project((float) flipped.getX(), (float) flipped.getY(), (float) flipped.getZ(), viewPortArray, new Vector3f());

                if (screenCoordsJOML.z() >= 0.0 && screenCoordsJOML.z() <= 1.0) {
                    scaledScreenX = sX * this.windowScale;
                    scaledScreenY = (this.windowHeight - sY) * this.windowScale;

                    scaledScreenX = this.windowWidth * this.windowScale - scaledScreenX;
                    scaledScreenY = this.windowHeight * this.windowScale - scaledScreenY;

                    Vec3d expand = new Vec3d(scaledScreenX, scaledScreenY, 0);
                    Vec3d center = new Vec3d(this.windowWidth * this.windowScale / 2.0, this.windowHeight * this.windowScale / 2.0, 0);
                    expand = expand.subtract(center).normalize().multiply(this.windowWidth * 2).add(center);

                    return new Projection(expand.getX(), expand.getY(), Projection.Type.OUTSIDE);
                }

                // This is an invalid point caused by projecting too close to the near clipping plane.
                return new Projection(sX * windowScale, (this.windowHeight - sY) * windowScale, Projection.Type.FAIL);
            }
        }

        return new Projection(0.0, 0.0, Projection.Type.FAIL);
    }

    public Vec3d doUnproject(int x, int y) {
        if (hasBeenUpdated) {
            Vector3f worldCoordsJOML = viewProjectionMatrix.unproject(x, y, 0.0F, viewPortArray, new Vector3f());
            return new Vec3d(worldCoordsJOML.x(), worldCoordsJOML.y(), worldCoordsJOML.z());
        }

        return null;
    }

}

