package client;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

/**
 * This is a circle which represents a field.
 */
public class Circle extends Ellipse2D.Double {

    /**
     * A color of the circle.
     */
    private Color color;
    /**
     * A boolean value which describes whether the circle is checked or not.
     */
    private boolean isChecked = false;
    /**
     * The circle used to show border when the smaller circle is checked.
     */
    private final Ellipse2D.Double biggerCircle;

    /**
     * Constructs a new Circle object.
     * @param x the X coordinate of the center of the circle.
     * @param y the Y coordinate of the center of the circle.
     * @param r the radius of the circle.
     */
    public Circle(double x, double y, double r) {
        super(x, y, 2 * r, 2 * r);
        biggerCircle = new Ellipse2D.Double(x - r * 0.25, y - r * 0.25, r * 2.5, r * 2.5);
    }

    /**
     * Sets the color of the circle.
     * @param color the color to be set. 
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Gets the color of the circle.
     * @return the color of the circle.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Checks whether a given point belongs to the circle.
     * @param x the X coordinate of the given point.
     * @param y the Y coordinate of the given point.
     * @return true, if the given point belongs to the circle, false otherwise.
     */
    public boolean isHit(double x, double y) {
        return Math.pow(2 * this.x + width - 2 * x, 2) + Math.pow(2 * this.y + width - 2 * y, 2) <= Math.pow(width, 2);
    }

    /**
     * Changes status of the circle.
     */
    public void changeStatus() {
        isChecked = !isChecked;
    }

    /**
     * Draws the circle. If the circle is checked, it also draws a border.
     * @param g2d Graphics2D object.
     */
    public void draw(Graphics2D g2d) {
        if (isChecked) {
            g2d.setPaint(Color.CYAN);
            g2d.fill(biggerCircle);
        }
        g2d.setPaint(color);
        g2d.fill(this);
    }
}
