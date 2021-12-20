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
      //MESSAGE Invalid number of players
        client.processCommands();
        CheckersClient secClient = new CheckersClient("localhost");
      //START
        client.processCommands();
      //YOUR_MOVE
        client.processCommands();
        client.getFrame().setVisible(false);
      //START
        secClient.processCommands();
      //MESSAGE Opponent's turn
        secClient.processCommands();
        secClient.getFrame().setVisible(false);
        Map<Integer, Map<Integer, Circle>> circles = client.getCircles();
        Map<Integer, Map<Integer, Circle>> secCircles = secClient.getCircles();
        int n = client.getN();
        Circle circle = circles.get(-1 - n).get(1);
        Color color = circle.getColor();
        Circle secCircle = secCircles.get(1 + n).get(-n);
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
        secClient.processCommands();
        assertTrue(circle2.getColor().equals(color));
        assertTrue(circle.getColor().equals(Color.WHITE));
        assertTrue(circle3.getColor().equals(Color.WHITE));
      //MESSAGE Valid move
        client.processCommands();
      //MESSAGE Your turn
        secClient.processCommands();
      
        secClient.onPress(secCircle.getX() + secCircle.getWidth() / 2, secCircle.getY() + secCircle.getWidth() / 2);
        Circle secCircle2 = secCircles.get(n).get(-n);
        secClient.onPress(secCircle2.getX() + secCircle2.getWidth() / 2, secCircle2.getY() + secCircle2.getWidth() / 2);
        secClient.processCommands();
        client.processCommands();
      //MESSAGE Valid move
        secClient.processCommands();
      //MESSAGE Your turn
        client.processCommands();

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
