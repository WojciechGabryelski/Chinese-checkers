package chinesecheckers.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Game is a class that performs server-side game tasks.
 */
public class Game {

    Player currentPlayer;

    /**
     * Player is the class representing a player on the server side.
     */
    class Player implements Runnable {
        int id;
        Player opponent;
        Socket socket;
        Scanner input;
        PrintWriter output;

        /**
         * Constructs a new Player object with given client socket and id.

         * @param socket client socket.
         * @param id id of the player.
         */
        public Player(Socket socket, int id) {
            this.socket = socket;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                setup();
                processCommands();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (opponent != null && opponent.output != null) {
                    opponent.output.println("OTHER_PLAYER_LEFT");
                }
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
            if (id == 1) {
                currentPlayer = this;
                output.println("SET_COLOR BLUE");
                output.println("MESSAGE Waiting for opponent to connect");
            } else {
                opponent = currentPlayer;
                opponent.opponent = this;
                output.println("SET_COLOR RED");
                opponent.output.println("MESSAGE Your move");
            }
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
                    if (this != currentPlayer) {
                        output.println("MESSAGE Not your turn");
                    } else if (opponent == null) {
                        output.println("You don't have an opponent yet");
                    } else {
                        String params = command.substring(11);
                        output.println("YOUR_TURN "+params);
                    }
                } else if (command.startsWith("MOVE")) {
                    output.println(command);
                    output.println("MESSAGE Valid move, please wait");
                    opponent.output.println(command);
                    opponent.output.println("MESSAGE Opponent moved, your turn");
                    currentPlayer = opponent;
                }
            }
        }
    }
}
