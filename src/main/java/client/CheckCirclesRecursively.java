package client;

import java.awt.Color;

/**
 * CheckCirclesAlgorithm is a class that executes the algorithm,
 * which changes the color of the fields to which the given checker can move,
 * excluding circles adjacent to the initial position of the checker.
 */
public class CheckCirclesRecursively extends CheckCirclesAlgorithm {

    /**
     * The CheckCirclesRecursively object.
     */
    private static final CheckCirclesRecursively checkCirclesRecursively = new CheckCirclesRecursively();

    /**
     * Constructs a CheckCirclesRecursively object.
     */
    private CheckCirclesRecursively(){}

    /**
     * Gets the instance of CheckCirclesRecursively class.
     * @return the instance of CheckCirclesRecursively class.
     */
    public static CheckCirclesRecursively getInstance() {
        return checkCirclesRecursively;
    }

    @Override
    public void execute(CircleTree circleTree, int x, int y) {
        checkCircles(circleTree, x, y, 2);
    }

    @Override
    public boolean isWhite(CircleTree circleTree, int x, int y, boolean b) {
        Circle between = circles.get(x).get(y);
        return between.getColor() == Color.WHITE;
    }
}
