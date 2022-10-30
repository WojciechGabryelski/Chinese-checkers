package client;

/**
 * CheckCirclesAlgorithm is a class that executes the algorithm,
 * which changes the color of the fields to which the given checker can move,
 * including circles adjacent to the initial position of the checker.
 */
public class CheckFirstCircles extends CheckCirclesAlgorithm{

    /**
     * The CheckFirstCircles object.
     */
    private static final CheckFirstCircles checkFirstCircles = new CheckFirstCircles();

    /**
     * Constructs a CheckCirclesRecursively object.
     */
    private CheckFirstCircles(){}

    /**
     * Gets the instance of CheckFirstCircles class.
     * @return the instance of CheckFirstCircles class.
     */
    public static CheckFirstCircles getInstance() {
        return checkFirstCircles;
    }

    @Override
    public void execute(CircleTree circleTree, int x, int y) {
        checkCircles(circleTree, x, y, 1);
    }

    @Override
    public boolean isWhite(CircleTree circleTree, int x, int y, boolean b) {
        return checkCircleIfWhite(circleTree, x, y, b);
    }
}
