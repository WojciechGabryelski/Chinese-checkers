package tests;

import static org.junit.Assert.assertTrue;

import client.CheckersClient;
import client.Circle;
import client.CirclesManager;
import java.util.Map;
import org.junit.Test;

/**
 * OnPressTest class contains class which tests whether the adjacent circles are equidistant from each other.
 */
public class DistancesEqualTest {

    /**
     * Tests whether adjacent circles are equidistant from each other.
     */
    @Test
    public void shouldAnswerWithTrue(){
        CheckersClient client = new CheckersClient();
        CirclesManager circlesManager = client.getCirclesManager();
        client.getFrame().setVisible(false);
        Map<Integer, Map<Integer, Circle>> circles =circlesManager.getCircles();
        int n = circlesManager.getN();
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