package client;

import java.awt.Color;
import java.awt.geom.Path2D;

/**
 * This is the star displayed during the game.
 */
public class Star extends Path2D.Double {

    /**
     * A color of the star.
     */
    private final Color color = new Color(252, 200, 163);

    /**
     * Constructs a new Star object.
     * @param width the width of the panel which contains the star.
     * @param height the height of the panel which contains the star.
     */
    public Star(int width, int height) {
        double x = width / 2.0;
        double y = height / 2.0;
        double r = Math.min(x, y);
        double a = r / Math.sqrt(3.0);
        moveTo(x, y + r);
        lineTo(x - a / 2, y + r / 2);
        lineTo(x - 3 * a / 2, y + r / 2);
        lineTo(x - a, y);
        lineTo(x - 3 * a / 2, y - r / 2);
        lineTo(x - a / 2, y - r / 2);
        lineTo(x, y - r);
        lineTo(x + a / 2, y - r / 2);
        lineTo(x + 3 * a / 2, y - r / 2);
        lineTo(x + a, y);
        lineTo(x + 3 * a / 2, y + r / 2);
        lineTo(x + a / 2, y + r / 2);
        closePath();
    }

    /**
     * Returns a color of the star.
     * @return the color of the star.
     */
    public Color getColor() {
        return color;
    }
}
