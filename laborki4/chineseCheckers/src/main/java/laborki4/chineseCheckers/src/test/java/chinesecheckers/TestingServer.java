package chinesecheckers;

import java.net.ServerSocket;
import java.util.concurrent.Executors;

/**
 * TestingServer is a server used to taking tests.
 */
public class TestingServer {
    
    /**
     * Main method.

     * @param args arguments passed to the program.
     */
    public static void main(String[] args) throws Exception {
        try (var listener = new ServerSocket(58901)) {
            System.out.println("Testing Server is Running...");
            var pool = Executors.newFixedThreadPool(2);
            while (true) {
                TestingGame game = new TestingGame();
                pool.execute(game.new Player(listener.accept()));
            }
        }
    }
}
