package tests;

import static org.junit.Assert.*;

import client.CheckersClient;
import client.Circle;
import client.CirclesManager;
import java.awt.Color;
import java.util.Map;
import org.junit.Test;


/**
 * OnPressTest class contains class which tests if onPress method in CheckersClient class works properly.
 */
public class OnPressTest {

    /**
     * Tests if onPress method works properly.
     */
    @Test
    public void test() {
        CheckersClient client = new CheckersClient();
        CirclesManager circlesManager = client.getCirclesManager();
        circlesManager.setColors(2, 0);
        client.getFrame().setVisible(false);
        Map<Integer, Map<Integer, Circle>> circles = circlesManager.getCircles();
        int n = circlesManager.getN();
        Circle circle = circles.get(-1 - n).get(1);
        Color color = circle.getColor();

        circlesManager.onPress(circle.getX() + circle.getWidth() / 2, circle.getY() + circle.getWidth() / 2);
        Circle circle2 = circles.get(-n).get(1);
        Circle circle3 = circles.get(-n).get(0);
        assertEquals(circle2.getColor(), Color.CYAN);
        assertEquals(circle3.getColor(), Color.CYAN);
        circlesManager.onPress(circle.getX() + circle.getWidth() / 2, circle.getY() + circle.getWidth() / 2);
        assertEquals(circle2.getColor(), Color.WHITE);
        assertEquals(circle3.getColor(), Color.WHITE);

        circlesManager.onPress(circle.getX() + circle.getWidth() / 2, circle.getY() + circle.getWidth() / 2);
        circlesManager.onPress(circle2.getX() + circle2.getWidth() / 2, circle2.getY() + circle2.getWidth() / 2);
        assertEquals(circle2.getColor(), color);
        assertEquals(circle.getColor(), Color.WHITE);
        assertEquals(circle3.getColor(), Color.WHITE);

        if (n > 1) {
            circle = circles.get(-2 - n).get(2);
            circlesManager.onPress(circle.getX() + circle.getWidth() / 2, circle.getY() + circle.getWidth() / 2);
            circle2 = circle3;
            circle3 = circles.get(-1 - n).get(1);
            Circle circle4 = circles.get(-n).get(2);
            assertEquals(circle2.getColor(), Color.CYAN);
            assertEquals(circle3.getColor(), Color.CYAN);
            assertEquals(circle4.getColor(), Color.CYAN);
            circlesManager.onPress(circle2.getX() + circle2.getWidth() / 2, circle2.getY() + circle2.getWidth() / 2);
            assertEquals(circle2.getColor(), color);
            assertEquals(circle.getColor(), Color.WHITE);
            assertEquals(circle3.getColor(), Color.WHITE);
            assertEquals(circle4.getColor(), Color.WHITE);
        }
    }
}
