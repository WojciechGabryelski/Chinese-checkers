package chinesecheckers.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Game is a class that performs server-side game tasks.
 */
public class Game {

    private Player currentPlayer;
    private ArrayList<Player> players = new ArrayList<Player>();
    private int numberOfPlayers = 0;

    /**
     * Player is the class representing a player on the server side.
     */
    class Player implements Runnable {
        private int id;
        private Socket socket;
        private Scanner input;
        private PrintWriter output;

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
                players.remove(this);
                numberOfPlayers--;
                for (int i = 0; i < numberOfPlayers; i++) {
                    players.get(i).output.println("OTHER_PLAYER_LEFT");
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
            players.add(this);
            id = numberOfPlayers;
            numberOfPlayers++;
            if (numberOfPlayers != 1 && numberOfPlayers != 5) {
                currentPlayer = players.get(0);
                for (int i = 0; i < numberOfPlayers; i++) {
                    players.get(i).output.println("START "+numberOfPlayers+" "+i);
                }
                players.get(0).output.println("MESSAGE Your move");
                for (int i = 1; i < numberOfPlayers; i++) {
                    players.get(i).output.println("MESSAGE Opponent's move, please wait");
                }
            } else {
                for (int i = 0; i < numberOfPlayers; i++) {
                    players.get(i).output.println("MESSAGE Invalid number of players: " + numberOfPlayers
                            + ". Number of players must be 2, 3, 4 or 6. Please wait for other player to join");
                }
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
                    if (numberOfPlayers == 1 || numberOfPlayers == 5) {
                        continue;
                    }
                    if (this != currentPlayer) {
                        output.println("MESSAGE Not your turn");
                    } else {
                        String params = command.substring(11);
                        output.println("YOUR_TURN "+params);
                    }
                } else if (command.startsWith("MOVE")) {
                    for (int i = 0; i < numberOfPlayers; i++) {
                        players.get(i).output.println(command);
                    }
                    output.println("MESSAGE Valid move, please wait");
                    currentPlayer = players.get((id + 1) % numberOfPlayers);
                    currentPlayer.output.println("MESSAGE Your turn");                    
                }
            }
        }
    }
}
