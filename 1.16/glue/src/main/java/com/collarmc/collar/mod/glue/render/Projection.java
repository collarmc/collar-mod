<<<<<<< HEAD:1.16/fabric/src/main/java/com/collarmc/mod/fabric/render/Projection.java
package com.collarmc.mod.fabric.render;
=======
package com.collarmc.collar.mod.glue.render;
>>>>>>> topic/merge:1.16/glue/src/main/java/com/collarmc/collar/mod/glue/render/Projection.java

public class Projection {
    private final double x;
    private final double y;
    private final Type t;

    public Projection(final double x, final double y, final Type t) {
        this.x = x;
        this.y = y;
        this.t = t;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public Type getType() {
        return this.t;
    }

    public boolean isType(final Type type) {
        return this.t == type;
    }

    public enum Type {
        INSIDE,
        OUTSIDE,
        FAIL;
    }
}
