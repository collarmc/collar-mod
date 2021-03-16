package team.catgirl.plastic.world;

public final class Position {

    public static final Position UNKNOWN = new Position(4.9E-324D, 4.9E-324D, 4.9E-324D);

    public final double x;
    public final double y;
    public final double z;

    public Position(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
