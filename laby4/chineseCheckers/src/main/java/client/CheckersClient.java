package client;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import javax.swing.*;

/**
 * CheckersClient is a class that performs tasks on the user's side of the program.
 */
public class CheckersClient {

    private final JFrame frame = new JFrame("Chinese checkers");
    private final JLabel messageLabel = new JLabel("...");
    private final JButton button = new JButton("Skip turn");
    private CirclesManager circlesManager;
    private boolean isPlaying = true;

    private Socket socket;
    private Scanner in;
    private PrintWriter out;

    /**
     * Constructs a new CheckersClient object with given server address.
     * @param serverAddress address of server.
     * @throws Exception throws exception when problem with connection with server occurs.
     */
    public CheckersClient(String serverAddress) throws Exception {
        socket = new Socket(serverAddress, 58901);
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream(), true);
        createPanel();
    }

    /**
     * Constructs a new CheckersClient object.
     * This object does not connect with the server, and it is used for testing.
     *
     */
    public CheckersClient() {
        createPanel();
    }

    /**
     * Creates panel in which the game is displayed.
     */
    public void createPanel() {
        messageLabel.setBackground(Color.lightGray);
        frame.getContentPane().add(messageLabel, BorderLayout.SOUTH);
        frame.getContentPane().add(button, BorderLayout.NORTH);
        button.setEnabled(false);
        frame.setSize(1080, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        CheckersPanel panel = new CheckersPanel();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setResizable(false);
        panel.createStar();
        circlesManager = new CirclesManager(panel, frame, messageLabel, button, out);
        panel.repaint();
    }

    /**
     * Gets the frame in which the game is displayed.
     * @return the frame in which the game is displayed.
     */
    public JFrame getFrame() {
        return frame;
    }

    public CirclesManager getCirclesManager() {
        return circlesManager;
    }

    /**
     * Receives messages from the server and performs appropriate tasks.
     * @throws Exception throws exception when there is unsuccessful attempt to close the socket.
     */
    public void play() throws Exception {
        try {
            while (in.hasNextLine()) {
                String response = in.nextLine();
                if (response.startsWith("YOUR_TURN")) {
                    String[] params = response.substring(10).split(" ");
                    circlesManager.onPress(Double.parseDouble(params[0]), Double.parseDouble(params[1]));
                } else if (response.startsWith("MOVE")) {
                    String[] params = response.substring(5).split(" ");
                    int firstX = Integer.parseInt(params[0]);
                    int firstY = Integer.parseInt(params[1]);
                    int secondX = Integer.parseInt(params[2]);
                    int secondY = Integer.parseInt(params[3]);
                    circlesManager.move(firstX, firstY, secondX, secondY);
                } else if (response.startsWith("START")) {
                    String[] params = response.substring(6).split(" ");
                    int numberOfPlayers = Integer.parseInt(params[0]);
                    int id = Integer.parseInt(params[1]);
                    circlesManager.setColors(numberOfPlayers, id);
                } else if (response.startsWith("MESSAGE")) {
                    String text = response.substring(8);
                    if (text.equals("Your turn")) {
                        button.setEnabled(true);
                    }
                    messageLabel.setText(text);
                } else if (response.startsWith("OTHER_PLAYER_LEFT")) {
                    JOptionPane.showMessageDialog(frame, "Other player left");
                    break;
                } else if (response.startsWith("FINISH")) {
                    int place = Integer.parseInt(response.substring(7));
                    if (place == 1) {
                        JOptionPane.showMessageDialog(frame, "Congratulations, you won!");
                        messageLabel.setText("You won!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "You took " + place + ". place.");
                        messageLabel.setText("You took " + place + ". place.");
                    }
                    isPlaying = false;
                } else if (response.startsWith("WINNER")) {
                    String[] params = response.substring(7).split(" ");
                    String player = params[0];
                    int place = Integer.parseInt(params[1]);
                    if (place == 1) {
                        JOptionPane.showMessageDialog(frame, "Player " + player + " won.");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Player " + player + " took " + place + ". place.");
                    }
                } else if (response.startsWith("DEFEAT")) {
                    JOptionPane.showMessageDialog(frame, "You lost.");
                    messageLabel.setText("You lost");
                }
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
                    if (isPlaying) {
                        double x = e.getX();
                        double y = e.getY();
                        out.println("WHOSE_TURN " + x + " " + y);
                    }
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
            for (Map<Integer, Circle> map : circlesManager.getCircles().values()) {
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
