package chinesecheckers.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * CheckersClient is a class that performs tasks on the user's side of the program.
 */
public class CheckersClient {

    private JFrame frame = new JFrame("Chinese checkers");
    private CheckersPanel panel;
    private JLabel messageLabel = new JLabel("...");

    private static int n = 4;
    private Map<Integer, Map<Integer, Circle>> circles;
    private boolean isChecked = false;
    private int checkedCircleX;
    private int checkedCircleY;
    private CircleTree circleTree;
    private Color color;

    private Socket socket;
    private Scanner in;
    private PrintWriter out;

    /**
     * Constructs a new CheckersClient object.

     * @param serverAddress address of server.
     * @throws Exception
     */
    public CheckersClient(String serverAddress) throws Exception {

        socket = new Socket(serverAddress, 58901);
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream(), true);

        messageLabel.setBackground(Color.lightGray);
        frame.getContentPane().add(messageLabel, BorderLayout.SOUTH);

        frame.setSize(1080, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        panel = new CheckersPanel();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setResizable(false);
        panel.createStar();
        createCircles();
        panel.repaint();
    }

    /**
     * Gets the frame in which the game is displayed.

     * @return the frame in which the game is displayed.
     */
    public JFrame getFrame() {
        return frame;
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
     * Checks whether client sent a command and calls processCommands method.

     * @throws Exception
     */
    public void play() throws Exception {
        try {
            while (in.hasNextLine()) {
                processCommands();
            }
            out.println("QUIT");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socket.close();
            frame.dispose();
        }
    }

    /**
     * Receives messages from the server and performs appropriate tasks.

     * @throws Exception
     */
    public void processCommands() throws Exception {
        String response = in.nextLine();
        if (response.startsWith("YOUR_TURN")) {
            String[] params = response.substring(10).split(" ");
            onPress(Double.parseDouble(params[0]), Double.parseDouble(params[1]));
        } else if (response.startsWith("MOVE")) {
            String[] params = response.substring(5).split(" ");
            int firstX = Integer.parseInt(params[0]);
            int firstY = Integer.parseInt(params[1]);
            int secondX = Integer.parseInt(params[2]);
            int secondY = Integer.parseInt(params[3]);
            Circle firstCircle = circles.get(firstX).get(firstY);
            circleTree = new CircleTree(firstCircle, null);
            checkCircles(firstX, firstY);
            Circle secondCircle = circles.get(secondX).get(secondY);
            CircleTree node = circleTree.findCircleTree(secondCircle);
            circleTree.changeChildrenColor();
            node.showPath(firstCircle, panel);
        } else if (response.startsWith("SET_COLOR")) {
            if (response.equals("SET_COLOR BLUE")) {
                color = Color.BLUE;
            } else {
                color = Color.RED;
            }
        } else if (response.startsWith("MESSAGE")) {
            messageLabel.setText(response.substring(8));
        }
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
        circles = new HashMap<Integer, Map<Integer, Circle>>();
        double help;
        for (int i = 0; i < 3 * n + 1; i++) {
            help = x;
            Map<Integer, Circle> map = new HashMap<Integer, Circle>();
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
            circles.put(i, new HashMap<Integer, Circle>());
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
        for (int i = 0; i < n; i++) {
            Map<Integer, Circle> map = circles.get(-n - 1 - i);
            Map<Integer, Circle> map2 = circles.get(n + 1 + i);
            for (int j = 0; j < n - i; j++) {
                map.get(1 + j + i).setColor(Color.BLUE);
                map2.get(-n + j).setColor(Color.RED);
            }
        }
    }

    /**
     * Performs an action which must be done after mouse is pressed on a field or a checker.

     * @param x the X coordinate of the mouse cursor.
     * @param y the Y coordinate of the mouse cursor.
     */
    public void onPress(double x, double y) {
        Color lightBlue = new Color(51, 204, 255);
        for (Map.Entry<Integer, Map<Integer, Circle>> outerEntry : circles.entrySet()) {
            for (Map.Entry<Integer, Circle> innerEntry : outerEntry.getValue().entrySet()) {
                Circle circle = innerEntry.getValue();
                if (circle.isHit(x, y)) {
                    if (circle.getColor().equals(lightBlue)) {
                        Circle checkedCircle = circles.get(checkedCircleX).get(checkedCircleY);
                        checkedCircle.changeStatus();
                        circleTree.changeChildrenColor();
                        isChecked = false;
                        out.println("MOVE "+checkedCircleX+" "+checkedCircleY+" "+outerEntry.getKey()+" "+innerEntry.getKey());
                    } else if (circle.getColor().equals(color)) {
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
                        checkCircles(checkedCircleX, checkedCircleY);
                        panel.repaint();
                    }
                    return;
                }
            }
        }
    }

    /**
     * Changes the color of the fields to which the given checker can move and creates the tree of these fields.

     * @param x the value of the first coordinate of the key to which the selected circle is assigned.
     * @param y the value of the second coordinate of the key to which the selected circle is assigned.
     */
    public void checkCircles(int x, int y) {
        Color lightBlue = new Color(51, 204, 255);
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                boolean p = Math.abs(x + i) <= n;
                boolean q = Math.abs(y + j) <= n;
                boolean r = Math.abs(x + i + y + j) <= n;
                if (i != j && (p && q || p && r || q && r)) {
                    Circle circle = circles.get(x + i).get(y + j);
                    if (circle.getColor() == Color.WHITE) {
                        circleTree.add(new CircleTree(circle, circleTree));
                        circle.setColor(lightBlue);
                    } else {
                        p = Math.abs(x + 2 * i) <= n;
                        q = Math.abs(y + 2 * j) <= n;
                        r = Math.abs(x + 2 * i + y + 2 * j) <= n;
                        if (p && q || p && r || q && r) {
                            circle = circles.get(x + 2 * i).get(y + 2 * j);
                            if (circle.getColor() == Color.WHITE) {
                                CircleTree child = new CircleTree(circle, circleTree);
                                circleTree.add(child);
                                recCheckCircles(child, x + 2 * i, y + 2 * j);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Changes the color of the fields that the checker can jump onto from the given field
     * as soon as it reaches this field and adds them to the tree.

     * @param circleTree the tree node which corresponds to the given field.
     * @param x the value of the first coordinate of the key to which the given field is assigned.
     * @param y the value of the second coordinate of the key to which the given field is assigned.
     */
    public void recCheckCircles(CircleTree circleTree, int x, int y) {
        circles.get(x).get(y).setColor(new Color(51, 204, 255));
        for (int i = -2; i <= 2; i += 2) {
            for (int j = -2; j <= 2; j += 2) {
                boolean p = Math.abs(x + i) <= n;
                boolean q = Math.abs(y + j) <= n;
                boolean r = Math.abs(x + i + y + j) <= n;
                if (i != j && (p && q || p && r || q && r)) {
                    Circle between = circles.get(x + i / 2).get(y + j / 2);
                    Circle circle = circles.get(x + i).get(y + j);
                    if (between.getColor() != Color.WHITE && circle.getColor() == Color.WHITE) {
                        CircleTree child = new CircleTree(circle, circleTree);
                        circleTree.add(child);
                        recCheckCircles(child, x + i, y + j);
                    }
                }
            }
        }
    }

    /**
     * CheckersPanel is a panel in which the game is displayed.
     */
    class CheckersPanel extends JPanel {

        private Star star;

        /**
         * Constructs a new CheckersPanel object.
         */
        public CheckersPanel() {

            addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    double x = e.getX();
                    double y = e.getY();
                    out.println("WHOSE_TURN " + x + " " + y);
                }
            });
        }

        /**
         * Creates a star which is shown in the CheckersPanel.
         */
        public void createStar() {
            star = new Star(getWidth(), getHeight());
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(star.getColor());
            g2d.fill(star);
            for (Map<Integer, Circle> map : circles.values()) {
                for (Circle circle : map.values()) {
                    circle.draw(g2d);
                }
            }
        }
    }

    /**
     * Main method.

     * @param args arguments passed to the program.
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Pass the server IP as the sole command line argument");
            return;
        }
        CheckersClient client = new CheckersClient(args[0]);
        client.play();
    }
}
