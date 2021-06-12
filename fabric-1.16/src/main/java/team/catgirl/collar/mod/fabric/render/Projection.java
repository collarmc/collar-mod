package team.catgirl.collar.mod.fabric.render;

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
