package client;

import java.awt.Color;
import java.util.Map;

/**
 * CheckCirclesAlgorithm is a class that executes the algorithm,
 * which changes the color of the fields to which the given checker can move.
 */
public abstract class CheckCirclesAlgorithm {

    /**
     * A map of circles.
     */
    protected static Map<Integer, Map<Integer, Circle>> circles;
    /**
     * A color of checkers which are checked before running this algorithm.
     */
    protected Color color;
    /**
     * A number of circles adjacent to one side of the star.
     */
    protected static int n;
    /**
     * A boolean value that describes whether jumps are enabled.
     */
    private final boolean jumpsEnabled = true;

    /**
     * Gets the color of checkers which are checked before running this algorithm.
     * @return the color of checkers.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets a map of circles.
     * @param circles the map of circles.
     */
    public static void setCircles(Map<Integer, Map<Integer, Circle>> circles) {
        CheckCirclesAlgorithm.circles = circles;
    }

    /**
     * Sets a color of checkers which are checked before running this algorithm.
     * @param color the color of checkers which are checked before running this algorithm.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Sets a number of circles adjacent to one side of the star.
     * @param n the number of circles adjacent to one side of the star.
     */
    public static void setN(int n) {
        CheckCirclesAlgorithm.n = n;
    }

    /**
     * Changes the color of the fields that the checker can jump onto from the given field
     * and adds them to the tree.
     * @param circleTree the tree node which corresponds to the given field.
     * @param x the value of the first coordinate of the key to which the given field is assigned.
     * @param y the value of the second coordinate of the key to which the given field is assigned.
     * @param a the value which ensures that appropriate circles are checked.
     */
    public void checkCircles(CircleTree circleTree, int x, int y, int a) {
        boolean b = inTargetTriangle(x, y);
        for (int i = -1; i <= 1; i += 1) {
            for (int j = -1; j <= 1; j += 1) {
                if (i != j && isOnTheBoard(x + i * a, y + j * a)) {
                    if (!isWhite(circleTree, x + i, y + j, b)) {
                        if (jumpsEnabled && isOnTheBoard(x + i * 2, y + j * 2)) {
                            CheckCirclesRecursively.getInstance().checkCircleIfWhite(
                            		circleTree, x + i * 2, y + j * 2, b);
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks whether a circle with given coordinates exists.
     * @param x the value of the first coordinate of the circle.
     * @param y the value of the second coordinate of the circle.
     * @return true, if the circle exists, false otherwise.
     */
    public boolean isOnTheBoard(int x, int y) {
        boolean p = Math.abs(x) <= n;
        boolean q = Math.abs(y) <= n;
        boolean r = Math.abs(x + y) <= n;
        return p && q || p && r || q && r;
    }

    /**
     * Checks whether a given circle is white and checks it.
     * @param circleTree the tree node which corresponds to the given field.
     * @param x the value of the first coordinate of the circle.
     * @param y the value of the second coordinate of the circle.
     * @param b true, if the previous circle is in target triangle, false otherwise.
     * @return true, if the given circle is white, false otherwise.
     */
    public boolean checkCircleIfWhite(CircleTree circleTree, int x, int y, boolean b) {
        Circle circle = circles.get(x).get(y);
        if (circle.getColor() == Color.WHITE && (!b || inTargetTriangle(x, y))) {
            circle.setColor(Color.CYAN);
            CircleTree child = new CircleTree(circle, circleTree);
            circleTree.add(child);
            if (this instanceof CheckCirclesRecursively) {
                CheckCirclesRecursively.getInstance().execute(child, x, y);
            }
            return true;
        }
        return false;
    }

    /**
     * Checks whether a given circle is located in the target triangle.
     * @param x the value of the first coordinate of the circle.
     * @param y the value of the second coordinate of the circle.
     * @return true. if the given circle is located in the target triangle, false otherwise.
     */
    public boolean inTargetTriangle(int x, int y) {
        int z;
        if (color == Color.BLUE) {
            z = x;
        } else if (color == Color.YELLOW) {
            z = -x;
        } else if (color == Color.RED) {
            z = -y;
        } else if (color == Color.MAGENTA) {
            z = y;
        } else if (color == Color.GREEN) {
            z = -x - y;
        } else {
            z = x + y;
        }
        return z > n;
    }

    /**
     * Checks whether a given circle.
     * @param circleTree the tree node which corresponds to the given field.
     * @param x the value of the first coordinate of the circle.
     * @param y the value of the second coordinate of the circle.
     * @param b true, if the previous circle is in target triangle, false otherwise.
     * @return false, if the given circle is white, false otherwise.
     */
    public abstract boolean isWhite(CircleTree circleTree, int x, int y, boolean b);

    /**
     * Executes the algorithm, which changes the color of the fields to which the given checker
     * can move, with correct parameters.
     * @param circleTree the tree node which corresponds to the given field.
     * @param x the value of the first coordinate of the key to which the given field is assigned.
     * @param y the value of the second coordinate of the key to which the given field is assigned.
     */
    public abstract void execute(CircleTree circleTree, int x, int y);
}
