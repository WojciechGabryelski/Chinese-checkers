package chinesecheckers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * TestingGame is a class similar to Game class which is used for taking tests.
 */
public class TestingGame {

    /**
     * Player is the class representing a player on the server side.
     */
    class Player implements Runnable {
        Socket socket;
        Scanner input;
        PrintWriter output;

        /**
         * Constructs a new Player object with given client socket and id.

         * @param socket client socket.
         * @param id id of the player.
         */
        public Player(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                setup();
                processCommands();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }

        /**
         * Sets up the game.

         * @throws IOException
         */
        private void setup() throws IOException {
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
            output.println("SET_COLOR BLUE");
        }

        /**
         * Receives messages from client and processes given commands.
         */
        private void processCommands() {
            while (input.hasNextLine()) {
                String command = input.nextLine();
                if (command.startsWith("QUIT")) {
                    return;
                } else if (command.startsWith("WHOSE_TURN")) {
                    String params = command.substring(11);
                    output.println("YOUR_TURN "+params);
                } else if (command.startsWith("MOVE")) {
                    output.println(command);
                }
            }
        }
    }
}
