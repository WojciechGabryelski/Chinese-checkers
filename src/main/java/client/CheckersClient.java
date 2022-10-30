package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.List;
import javax.swing.*;

/**
 * CheckersClient is a class that performs tasks on the user's side of the program.
 */
public class CheckersClient implements ActionListener {

    /**
     * A frame in which the game is displayed.
     */
    private final JFrame frame = new JFrame("Chinese checkers");
    /**
     * A label which shows messages.
     */
    private final JLabel messageLabel = new JLabel("...");
    /**
     * A button which skips a turn.
     */
    private final JButton button = new JButton("Start a game");
    private Menu menu;
    private MenuItem menuItem;
    private JDialog load;
    private JDialog user;
    private JButton confirmBtn1;
    private JButton confirmBtn2;
    private JTextField textField;
    private final List<JRadioButton> radioButtonList = new LinkedList<>();
    /**
     * A CirclesManager object.
     */
    private CirclesManager circlesManager;
    /**
     * A boolean value which describes whether the player is still playing the game or is done.
     */
    private boolean isPlaying = false;

    /**
     * A Socket object used to connect with server.
     */
    private Socket socket;
    /**
     * A Scanner object used to get messages from server.
     */
    private Scanner in;
    /**
     * A PrintWriter object used to send messages to client.
     */
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
        user = new JDialog(frame, "Username", true);
        user.setSize(300,150);
        user.setLocationRelativeTo(null);
        user.setLayout(new BorderLayout());
        user.add(new Label("Enter your username"), BorderLayout.NORTH);
        textField = new JTextField(50);
        textField.setFont(new Font(Font.SERIF, Font.PLAIN, 25));
        user.add(textField, BorderLayout.CENTER);
        confirmBtn1 = new JButton("OK");
        confirmBtn1.addActionListener(this);
        user.add(confirmBtn1, BorderLayout.SOUTH);
        user.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        user.setVisible(true);
    }

    /**
     * Constructs a new CheckersClient object.
     * This object does not connect with the server, and it is used for testing.
     */
    public CheckersClient() {
        createPanel();
    }

    /**
     * Creates panel in which the game is displayed.
     */
    public void createPanel() {
        menu = new Menu("Game");
        menuItem = new MenuItem("Load game");
        menu.add(menuItem);
        menuItem.addActionListener(this);
        MenuBar menuBar = new MenuBar();
        menuBar.add(menu);
        frame.setMenuBar(menuBar);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == menuItem) {
            out.println("GAMES");
        } else if (e.getSource() == confirmBtn1) {
            user.setVisible(false);
            out.println("USERNAME " + textField.getText());
        } else if (e.getSource() == confirmBtn2) {
            load.setVisible(false);
            if (radioButtonList.size() > 0) {
                for (JRadioButton radioButton : radioButtonList) {
                    if (radioButton.isSelected()) {
                        isPlaying = true;
                        menu.setEnabled(false);
                        button.setText("Skip turn");
                        button.setEnabled(false);
                        out.println("LOAD " + radioButton.getText());
                        break;
                    }
                }
            }
        }
    }

    /**
     * Gets the frame in which the game is displayed.
     * @return the frame in which the game is displayed.
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * Gets the CirclesManager object.
     * @return the CirclesManager object.
     */
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
                    isPlaying = true;
                    menu.setEnabled(false);
                    button.setText("Skip turn");
                    String[] params = response.substring(6).split(" ");
                    int numberOfPlayers = Integer.parseInt(params[0]);
                    int id = Integer.parseInt(params[1]);
                    circlesManager.setColors(numberOfPlayers, id);
                } else if (response.startsWith("MESSAGE")) {
                    String text = response.substring(8);
                    if (text.equals("Your turn") || text.startsWith("Press")) {
                        button.setEnabled(true);
                    } else if (text.startsWith("Invalid")) {
                        button.setEnabled(false);
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
                    button.setEnabled(false);
                    isPlaying = false;
                    JOptionPane.showMessageDialog(frame, "You lost.");
                    messageLabel.setText("You lost");
                } else if (response.startsWith("GAMES")) {
                    String[] params = response.substring(6).split(" ");
                    load = new JDialog(frame, "Load", true);
                    load.setSize(300,150);
                    load.setLocationRelativeTo(null);
                    load.setLayout(new BorderLayout());
                    confirmBtn2 = new JButton("OK");
                    confirmBtn2.addActionListener(this);
                    load.add(confirmBtn2, BorderLayout.SOUTH);
                    load.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
                    radioButtonList.clear();
                    if (Objects.equals(params[0], "")) {
                        load.add(new Label("You do not have unfinished games."), BorderLayout.CENTER);
                    } else {
                        confirmBtn2.setEnabled(false);
                        load.add(new Label("Choose a game"), BorderLayout.NORTH);
                        JPanel games = new JPanel();
                        ButtonGroup bg = new ButtonGroup();
                        for (String id : params) {
                            JRadioButton radioButton = new JRadioButton(id);
                            radioButtonList.add(radioButton);
                            radioButton.addActionListener(e -> confirmBtn2.setEnabled(true));
                            bg.add(radioButton);
                            games.add(radioButton);
                        }
                        load.add(games, BorderLayout.CENTER);
                    }
                    load.setVisible(true);
                } else if (response.equals("OTHER")) {
                    JOptionPane.showMessageDialog(frame, "MESSAGE Other players have already loaded a game");
                    break;
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

        /**
         * A star which is shown in the CheckersPanel.
         */
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
