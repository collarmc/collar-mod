package team.catgirl.plastic;

import team.catgirl.plastic.ui.Display;
import team.catgirl.plastic.world.World;

import java.io.File;

/**
 * Minecraft mod api abstraction
 */
public abstract class Plastic {

    private static Plastic INSTANCE;

    /**
     * UI display
     */
    public final Display display;

    /**
     * The World
     */
    public final World world;

    protected Plastic(Display display, World world) {
        this.display = display;
        this.world = world;
        setPlastic(this);
    }

    /**
     * Minecraft home directory
     * @return home
     */
    public abstract File home();

    /**
     * Server IP
     * @return serverIP or null if not connected
     */
    public abstract String serverIp();

    /**
     * @return plastic instance
     */
    public static Plastic getPlastic() {
        return INSTANCE;
    }

    /**
     * @param plastic instance to set globally
     */
    private static void setPlastic(Plastic plastic) {
        if (INSTANCE != null) {
            throw new IllegalStateException("plastic instance is already set");
        }
        Plastic.INSTANCE = plastic;
    }
}
