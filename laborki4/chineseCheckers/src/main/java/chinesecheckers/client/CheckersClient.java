package chinesecheckers.client;

import javax.swing.JFrame;

import chinesecheckers.frontend.CheckersPanel;

public class CheckersClient {
    
    private JFrame frame = new JFrame("Chinese checkers");
    private CheckersPanel panel;
    
    public CheckersClient() {
        frame.setSize(1080, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        panel = CheckersPanel.getInstance();
        frame.add(panel);
        frame.setVisible(true);
        frame.setResizable(false);
        panel.createStar();
        panel.createCircles();
        panel.repaint();
    }
    /**

     * @param args
     */
    public static void main(String[] args) {
        new CheckersClient();
    }
}
