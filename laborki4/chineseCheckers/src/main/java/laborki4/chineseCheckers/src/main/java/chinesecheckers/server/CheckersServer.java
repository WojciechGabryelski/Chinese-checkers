package chinesecheckers.server;

import java.net.ServerSocket;
import java.util.concurrent.Executors;

/**
 * CheckersServer is a class that performs tasks on the server side of the program.
 */
public class CheckersServer {

    /**
     * Main method.

     * @param args arguments passed to the program.
     */
    public static void main(String[] args) throws Exception {
        try (var listener = new ServerSocket(58901)) {
            System.out.println("Chinese Checkers Server is Running...");
            var pool = Executors.newFixedThreadPool(6);
            while (true) {
                Game game = new Game();
                for (int i = 0; i < 6; i++) {
                    pool.execute(game.new Player(listener.accept(), i));
                }
            }
        }
    }
}
