package chinesecheckers;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.junit.Test;

import chinesecheckers.frontend.CheckersPanel;
import chinesecheckers.frontend.Circle;

/**
 * @author DELL
 *
 */
public class OnPressTest {

    /**
     * 
     */
    @Test
    public void test() {
        JFrame frame = new JFrame();
        frame.setSize(1080, 720);
        CheckersPanel panel = CheckersPanel.getInstance();
        frame.add(panel);
        frame.setVisible(true);
        panel.createCircles();
        frame.setVisible(false);
        Map<Integer, Map<Integer, Circle>> circles = panel.getCircles();
        int n = CheckersPanel.getN();
        Circle circle = circles.get(-1 - n).get(1);
        Color color = circle.getColor();
        Color lightBlue = new Color(51, 204, 255);
        
        panel.onPress(circle.getX() + circle.getWidth() / 2, circle.getY() + circle.getWidth() / 2);
        Circle circle2 = circles.get(-n).get(1);
        Circle circle3 = circles.get(-n).get(0);
        assertTrue(circle2.getColor().equals(lightBlue));
        assertTrue(circle3.getColor().equals(lightBlue));
        panel.onPress(circle.getX() + circle.getWidth() / 2, circle.getY() + circle.getWidth() / 2);
        assertTrue(circle2.getColor().equals(Color.WHITE));
        assertTrue(circle3.getColor().equals(Color.WHITE));

        panel.onPress(circle.getX() + circle.getWidth() / 2, circle.getY() + circle.getWidth() / 2);
        panel.onPress(circle2.getX() + circle2.getWidth() / 2, circle2.getY() + circle2.getWidth() / 2);
        assertTrue(circle2.getColor().equals(color));
        assertTrue(circle.getColor().equals(Color.WHITE));
        assertTrue(circle3.getColor().equals(Color.WHITE));

        if (n > 1) {
            circle = circles.get(-2 - n).get(2);
            panel.onPress(circle.getX() + circle.getWidth() / 2, circle.getY() + circle.getWidth() / 2);
            circle2 = circle3;
            circle3 = circles.get(-1 - n).get(1);
            Circle circle4 = circles.get(-n).get(2);
            assertTrue(circle2.getColor().equals(lightBlue));
            assertTrue(circle3.getColor().equals(lightBlue));
            assertTrue(circle4.getColor().equals(lightBlue));
            panel.onPress(circle2.getX() + circle2.getWidth() / 2, circle2.getY() + circle2.getWidth() / 2);
            assertTrue(circle2.getColor().equals(color));
            assertTrue(circle.getColor().equals(Color.WHITE));
            assertTrue(circle3.getColor().equals(Color.WHITE));
            assertTrue(circle4.getColor().equals(Color.WHITE));
        }
    }

}
