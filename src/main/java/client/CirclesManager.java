package client;

import java.awt.Color;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

/**
 * CirclesManager is a class that handles the events of moving checkers.
 */
public class CirclesManager {
    /**
     * A panel in which the star is displayed.
     */
    private final JPanel panel;
    /**
     * A frame in which the game is displayed.
     */
    private final JFrame frame;
    /**
     * A label which shows messages.
     */
    private final JLabel messageLabel;
    /**
     * A button which skips a turn.
     */
    private final JButton button;
    /**
     * A PrintWriter object used for sending messages to server.
     */
    private final PrintWriter out;

    /**
     * A number of circles adjacent to one side of the star.
     */
    private static final int n = 4;
    /**
     * A number of player's checkers which achieved the target triangle.
     */
    private int inTargetNumber = 0;
    /**
     * A map of circles.
     */
    private Map<Integer, Map<Integer, Circle>> circles;
    /**
     * A boolean value which describes whether any circle is checked or not.
     */
    private boolean isChecked = false;
    /**
     * A value of X coordinate of the checked circle, if any circle is checked.
     */
    private int checkedCircleX;
    /**
     * A value of Y coordinate of the checked circle, if any circle is checked.
     */
    private int checkedCircleY;
    /**
     * The Circle Tree object that indicates the achievable fields for the selected checker.
     */
    private CircleTree circleTree;
    /**
     * A color of the player's checkers in a string format.
     */
    private String color;
    
    /**
     * Constructs a new CirclesManager object.
     * @param panel a panel in which the star is displayed.
     * @param frame a frame in which the game is displayed.
     * @param messageLabel a label which shows messages.
     * @param button a button which skips a turn.
     * @param out a PrintWriter object used for sending messages to server.
     */
    public CirclesManager(JPanel panel, JFrame frame, JLabel messageLabel, JButton button, PrintWriter out) {
        this.panel = panel;
        this.frame = frame;
        this.button = button;
        this.messageLabel = messageLabel;
        this.out = out;
        button.addActionListener(e -> {
            button.setEnabled(false);
            if (button.getText().equals("Start a game")) {
                messageLabel.setText("Please wait for opponents to start a game");
                out.println("START");
            } else {
                if(isChecked) {
                    Circle checkedCircle = circles.get(checkedCircleX).get(checkedCircleY);
                    checkedCircle.changeStatus();
                    circleTree.changeChildrenColor();
                    isChecked = false;
                    panel.paintImmediately(0, 0, panel.getWidth(), panel.getHeight());
                }
                messageLabel.setText("Turn skipped, please wait");
                out.println("SKIP");
            }
        });
        createCircles();
        CheckCirclesAlgorithm.setCircles(circles);
        CheckCirclesAlgorithm.setN(n);
    }

    /**
     * Gets the map of circles.
     * @return the map of circles.
     */
    public Map<Integer, Map<Integer, Circle>> getCircles() {
        return circles;
    }

    /**
     * Gets the number of circles adjacent to one side of the star.
     * @return the number of circles adjacent to one side of the star.
     */
    public int getN() {
        return n;
    }

    /**
     * Moves the given checker to the given field (used after receiving message from server).
     * @param firstX the value of the first coordinate of the key to which the first field is assigned.
     * @param firstY the value of the second coordinate of the key to which the first field is assigned.
     * @param secondX the value of the first coordinate of the key to which the second field is assigned.
     * @param secondY the value of the second coordinate of the key to which the second field is assigned.
     */
    public void move(int firstX, int firstY, int secondX, int secondY) {
        Circle firstCircle = circles.get(firstX).get(firstY);
        circleTree = new CircleTree(firstCircle, null);
        Color tmp = CheckFirstCircles.getInstance().getColor();
        Color color = firstCircle.getColor();
        CheckFirstCircles.getInstance().setColor(color);
        CheckCirclesRecursively.getInstance().setColor(color);
        CheckFirstCircles.getInstance().execute(circleTree, firstX, firstY);
        CheckFirstCircles.getInstance().setColor(tmp);
        CheckCirclesRecursively.getInstance().setColor(tmp);
        Circle secondCircle = circles.get(secondX).get(secondY);
        CircleTree node = circleTree.findCircleTree(secondCircle);
        circleTree.changeChildrenColor();
        node.showPath(panel);
    }

    /**
     * Sets color of the circles while starting the game.
     * @param num number of players.
     * @param id identifier of this player.
     */
    public void setColors(int num, int id) {
        int[] arr = switch (num) {
            case 2 -> new int[]{0, 3};
            case 3 -> new int[]{0, 2, 4};
            case 4 -> new int[]{0, 1, 3, 4};
            case 6 -> new int[]{0, 1, 2, 3, 4, 5};
            default -> new int[]{};
        };
        int[][] points = {{-n - 1, 1}, {-n, n + 1}, {n, 1}, {n + 1, -n}, {n, -2 * n}, {-n, -n}};
        Color[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.BLACK};
        for (int k = 0; k < 6; k++) {
            Color color = Arrays.binarySearch(arr, k) >= 0 ? colors[k] : Color.WHITE;
            for (int i = 0; i < n; i++) {
                Map<Integer, Circle> map = circles.get(points[k][0] + ((k % 2) * 2 - 1) * i);
                for (int j = 0; j < n - i; j++) {
                    map.get(points[k][1] + j + (k + 1) % 2 * i).setColor(color);
                }
            }
        }
        for (int i = 1; i <= n; i++) {
            for (int j = -n; j<= n - i; j++) {
                circles.get(i).get(j).setColor(Color.WHITE);
                circles.get(-i).get(-j).setColor(Color.WHITE);
            }
        }
        for (int i = -n; i <= n; i++) {
            circles.get(0).get(i).setColor(Color.WHITE);
        }
        panel.repaint();
        String[] titles = {"Blue", "Red", "Green", "Yellow", "Magenta", "Black"};
        int index;
        index = switch (num) {
            case 2 -> 3 * id;
            case 3 -> 2 * id;
            case 4 -> id % 2 + id / 2 * 3;
            case 6 -> id;
            default -> 0;
        };
        CheckFirstCircles.getInstance().setColor(colors[index]);
        CheckCirclesRecursively.getInstance().setColor(colors[index]);
        color = titles[index];
        frame.setTitle("Chinese checkers: " + color);
    }

    /**
     * Creates circles which represent fields or checker.
     */
    public void createCircles() {
        double x0 = panel.getWidth() / 2.0;
        double y0 = panel.getHeight() / 2.0;
        double r = Math.min(x0, y0);
        double a = r / Math.sqrt(3.0);
        double rad = a / (2 * (n + 1 / Math.sqrt(3.0)));
        double x = x0 - 3 * a / 2 + rad * Math.sqrt(3.0);
        double y = y0 + r / 2 - rad;
        circles = new HashMap<>();
        double help;
        for (int i = 0; i < 3 * n + 1; i++) {
            help = x;
            Map<Integer, Circle> map = new HashMap<>();
            circles.put(i - n, map);
            for (int j = 0; j < 3 * n + 1 - i; j++) {
                Circle circle = new Circle(x - rad * 0.8, y - rad * 0.8, rad * 0.8);
                circle.setColor(Color.WHITE);
                map.put(j - n, circle);
                x += 2 * rad;
            }
            y -= rad * Math.sqrt(3.0);
            x = help + rad;
        }
        for (int i = -n - 1; i >= -2 * n; i--) {
            circles.put(i, new HashMap<>());
        }
        double[] t = { x0 - 3 * a / 2 + rad * Math.sqrt(3.0), y0 - r / 2 + rad,
                x0 - 3 * a / 2 + rad * (4 * n + 2 + Math.sqrt(3.0)), y0 - r / 2 + rad,
                x0 - 3 * a / 2 + rad * (2 * n + 1 + Math.sqrt(3.0)),
                y0 + r / 2 + rad * (Math.sqrt(3.0) - 1) };
        int[] v = { -2 * n, n, 1, n, 1, -n - 1 };
        for (int k = 0; k < 3; k++) {
            x = t[2 * k];
            y = t[2 * k + 1];
            for (int i = 0; i < n; i++) {
                help = x;
                Map<Integer, Circle> map = circles.get(-i + v[2 * k + 1]);
                for (int j = 0; j < n - i; j++) {
                    Circle circle = new Circle(x - rad * 0.8, y - rad * 0.8, rad * 0.8);
                    circle.setColor(Color.WHITE);
                    map.put(i + j + v[2 * k], circle);
                    x += 2 * rad;
                }
                y += rad * Math.sqrt(3.0);
                x = help + rad;
            }
        }
    }

    /**
     * Performs an action which must be done after mouse is pressed on a field or a checker.
     * @param x the X coordinate of the mouse cursor.
     * @param y the Y coordinate of the mouse cursor.
     */
    public void onPress(double x, double y) {
        for (Map.Entry<Integer, Map<Integer, Circle>> outerEntry : circles.entrySet()) {
            for (Map.Entry<Integer, Circle> innerEntry : outerEntry.getValue().entrySet()) {
                Circle circle = innerEntry.getValue();
                if (circle.isHit(x, y)) {
                    if (circle.getColor() == Color.CYAN) {
                        button.setEnabled(false);
                        Circle checkedCircle = circles.get(checkedCircleX).get(checkedCircleY);
                        checkedCircle.changeStatus();
                        circleTree.changeChildrenColor();
                        isChecked = false;
                        int targetCircleX = outerEntry.getKey();
                        int targetCircleY = innerEntry.getKey();
                        if (out != null) {
                            out.println("MOVE "+checkedCircleX+" "+checkedCircleY+" "+outerEntry.getKey()+" "+innerEntry.getKey());
                        }
                        CircleTree node = circleTree.findCircleTree(circle);
                        node.showPath(panel);
                        messageLabel.setText("Valid move, please wait");
                        if (!CheckFirstCircles.getInstance().inTargetTriangle(checkedCircleX, checkedCircleY)
                                && CheckFirstCircles.getInstance().inTargetTriangle(targetCircleX, targetCircleY)) {
                            inTargetNumber++;
                            if (inTargetNumber == n * (n + 1) / 2) {
                                if(out != null) {
                                    out.println("FINISH " + color);
                                }
                            }
                        }
                    } else if (circle.getColor().equals(CheckFirstCircles.getInstance().getColor())) {
                        if (isChecked) {
                            Circle checkedCircle = circles.get(checkedCircleX).get(checkedCircleY);
                            checkedCircle.changeStatus();
                            circleTree.changeChildrenColor();
                            if (checkedCircle.equals(circle)) {
                                isChecked = false;
                                panel.repaint();
                                return;
                            }
                        }
                        circle.changeStatus();
                        checkedCircleX = outerEntry.getKey();
                        checkedCircleY = innerEntry.getKey();
                        isChecked = true;
                        circleTree = new CircleTree(circle, null);
                        CheckFirstCircles.getInstance().execute(circleTree, checkedCircleX, checkedCircleY);
                        panel.repaint();
                    }
                    return;
                }
            }
        }
    }
}
