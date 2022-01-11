package server;

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
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
        try (ServerSocket listener = new ServerSocket(58901)) {
            System.out.println("Chinese Checkers Server is Running...");
            ExecutorService pool = Executors.newFixedThreadPool(6);
            Game game = new Game();
            while(true) {
                pool.execute(game.new Player(listener.accept()));
            }
        }
    }
}
