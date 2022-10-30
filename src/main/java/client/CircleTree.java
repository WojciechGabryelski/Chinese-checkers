package client;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

/**
 * CircleTree is a tree whose values are circles to which the concrete checker can jump.
 * The fields which must be reached in order to reach another field are higher in the tree.
 */
public class CircleTree {

    /**
     * The circle which is the value of CircleTree node.
     */
    private final Circle circle;
    /**
     * A parent of this node.
     */
    private final CircleTree parent;
    /**
     * Children of this node.
     */
    private final List<CircleTree> children = new LinkedList<>();

    /**
     * Constructs a new CircleTree object.
     * @param circle the circle assigned to the tree node.
     * @param parent the parent of the tree node.
     */
    public CircleTree(Circle circle, CircleTree parent) {
        this.circle = circle;
        this.parent = parent;
    }

    /**
     * Sets the color of circles, which are values of the node's children, to white.
     */
    public void changeChildrenColor() {
        for (CircleTree child : children) {
            child.circle.setColor(Color.WHITE);
            child.changeChildrenColor();
        }
    }

    /**
     * Finds the tree node whose value is the given circle.
     * @param circle the circle assigned to a searched tree node.
     * @return the tree node whose value is the given circle. 
     */
    public CircleTree findCircleTree(Circle circle) {
        if (circle.equals(this.circle)) {
            return this;
        }
        for (CircleTree child : children) {
            CircleTree helper = child.findCircleTree(circle);
            if (helper != null) {
                return helper;
            }
        }
        return null;
    }

    /**
     * Adds a CircleTree object to the list of the current node's children.
     * @param circleTree a CircleTree object
     */
    public void add(CircleTree circleTree) {
        children.add(circleTree);
    }

    /**
     * Displays an animation of a checker reaching a given field.
     * @param panel the panel in which the game is displayed.
     */
    public void showPath(JPanel panel) {
        if (parent == null) {
            return;
        }
        parent.showPath(panel);
        Circle parentCircle = parent.circle;
        Color color = parentCircle.getColor();
        parentCircle.setColor(Color.WHITE);
        this.circle.setColor(color);
        SwingUtilities.invokeLater(() -> panel.paintImmediately(0, 0, panel.getWidth(), panel.getHeight()));
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (Exception ignored) {
        }
    }
}
