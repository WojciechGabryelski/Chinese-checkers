package chinesecheckers;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.Map;

import org.junit.Test;

import chinesecheckers.client.CheckersClient;
import chinesecheckers.client.Circle;

/**
 * OnPressTest class contains class which tests if onPress method in CheckersClient class works properly.
 */
public class OnPressTest {

    /**
     * Tests if onPress method works properly.
     */
    @Test
    public void test() throws Exception {
        CheckersClient client = new CheckersClient("localhost");
        client.processCommands();
        client.getFrame().setVisible(false);
        Map<Integer, Map<Integer, Circle>> circles = client.getCircles();
        int n = client.getN();
        Circle circle = circles.get(-1 - n).get(1);
        Color color = circle.getColor();
        Color lightBlue = new Color(51, 204, 255);
        
        client.onPress(circle.getX() + circle.getWidth() / 2, circle.getY() + circle.getWidth() / 2);
        Circle circle2 = circles.get(-n).get(1);
        Circle circle3 = circles.get(-n).get(0);
        assertTrue(circle2.getColor().equals(lightBlue));
        assertTrue(circle3.getColor().equals(lightBlue));
        client.onPress(circle.getX() + circle.getWidth() / 2, circle.getY() + circle.getWidth() / 2);
        assertTrue(circle2.getColor().equals(Color.WHITE));
        assertTrue(circle3.getColor().equals(Color.WHITE));

        client.onPress(circle.getX() + circle.getWidth() / 2, circle.getY() + circle.getWidth() / 2);
        client.onPress(circle2.getX() + circle2.getWidth() / 2, circle2.getY() + circle2.getWidth() / 2);
        client.processCommands();
        assertTrue(circle2.getColor().equals(color));
        assertTrue(circle.getColor().equals(Color.WHITE));
        assertTrue(circle3.getColor().equals(Color.WHITE));

        if (n > 1) {
            circle = circles.get(-2 - n).get(2);
            client.onPress(circle.getX() + circle.getWidth() / 2, circle.getY() + circle.getWidth() / 2);
            circle2 = circle3;
            circle3 = circles.get(-1 - n).get(1);
            Circle circle4 = circles.get(-n).get(2);
            assertTrue(circle2.getColor().equals(lightBlue));
            assertTrue(circle3.getColor().equals(lightBlue));
            assertTrue(circle4.getColor().equals(lightBlue));
            client.onPress(circle2.getX() + circle2.getWidth() / 2, circle2.getY() + circle2.getWidth() / 2);
            client.processCommands();
            assertTrue(circle2.getColor().equals(color));
            assertTrue(circle.getColor().equals(Color.WHITE));
            assertTrue(circle3.getColor().equals(Color.WHITE));
            assertTrue(circle4.getColor().equals(Color.WHITE));
        }
    }

}
