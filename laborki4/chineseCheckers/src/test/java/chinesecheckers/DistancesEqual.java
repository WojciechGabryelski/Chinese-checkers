package chinesecheckers;

import static org.junit.Assert.assertTrue;

import java.util.Map;
import javax.swing.JFrame;

import org.junit.Test;

import chinesecheckers.frontend.CheckersPanel;
import chinesecheckers.frontend.Circle;

/**
 * @author DELL
 *
 */
public class DistancesEqual{

    /**
     * 
     */
    @Test
    public void shouldAnswerWithTrue() {
        JFrame frame = new JFrame();
        frame.setSize(1080, 720);
        CheckersPanel panel = CheckersPanel.getInstance();
        frame.add(panel);
        frame.setVisible(true);
        panel.createCircles();
        frame.setVisible(false);
        Map<Integer, Map<Integer, Circle>> circles = panel.getCircles();
        int n = CheckersPanel.getN();
        Circle circle1 = circles.get(0).get(0);
        Circle circle2 = circles.get(1).get(0);
        double dist = Math.pow(circle1.getX() - circle2.getX(), 2.0) + Math.pow(circle1.getY() - circle2.getY(), 2.0);
        for (Map.Entry<Integer, Map<Integer, Circle>> outerEntry : circles.entrySet()) {
            for (Map.Entry<Integer, Circle> innerEntry : outerEntry.getValue().entrySet()) {
                Circle circle = innerEntry.getValue();
                int x = outerEntry.getKey();
                int y = innerEntry.getKey();
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        boolean p = Math.abs(x + i) <= n;
                        boolean q = Math.abs(y + j) <= n;
                        boolean r = Math.abs(x + i + y + j) <= n;
                        if (i != j && (p && q || p && r || q && r)) {
                            Circle neighbour = circles.get(x + i).get(y + j);
                            assertTrue(Math.abs(Math.pow(circle.getX() - neighbour.getX(), 2.0)
                                    + Math.pow(circle.getY() - neighbour.getY(), 2.0) - dist) < 0.0001);
                        }
                    }
                }
            }
        }
    }
}
