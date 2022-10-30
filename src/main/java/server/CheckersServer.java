package server;

import java.net.ServerSocket;
import java.net.Socket;
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
            ExecutorService pool = Executors.newFixedThreadPool(600);
            Game game = new Game();
            while (true) {
                Socket socket = listener.accept();
                Runnable player = game.addPlayer(socket);
                if (player == null) {
                    game = new Game();
                    pool.execute(game.new Player(socket));
                } else {
                    pool.execute(player);
                }
            }
        }
    }
}
