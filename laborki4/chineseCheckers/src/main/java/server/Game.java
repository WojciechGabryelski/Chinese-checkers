package server;

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
    private final ArrayList<Player> players = new ArrayList<>();
    private final ArrayList<Player> allPlayers = new ArrayList<>();


    /**
     * Player is the class representing a player on the server side.
     */
    class Player implements Runnable {
        private final Socket socket;
        private Scanner input;
        private PrintWriter output;

        /**
         * Constructs a new Player object with given client socket and id.
         * @param socket client socket.
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
                allPlayers.remove(this);
                if (players.contains(this)) {
                    players.remove(this);
                    for (Player player : allPlayers) {
                        player.output.println("OTHER_PLAYER_LEFT");
                    }
                }
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }

        /**
         * Sets up the game.
         * @throws IOException throws exception when problem with connection with client occurs.
         */
        private void setup() throws IOException {
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
            players.add(this);
            allPlayers.add(this);
            int numberOfPlayers = players.size();
            if (numberOfPlayers != 1 && numberOfPlayers != 5) {
                int j = (int)(Math.random()*numberOfPlayers);
                currentPlayer = players.get(j);
                for (int i = 0; i < numberOfPlayers; i++) {
                    players.get(i).output.println("START "+numberOfPlayers+" "+i);
                }
                currentPlayer.output.println("MESSAGE Your turn");
                for (int i = 0; i < numberOfPlayers; i++) {
                    if (i != j) {
                        players.get(i).output.println("MESSAGE Opponent's move, please wait");
                    }
                }
            } else {
                for (Player player : players) {
                    player.output.println("MESSAGE Invalid number of players: " + numberOfPlayers
                            + ". Number of players must be 2, 3, 4 or 6. Please wait for other player to join");
                }
            }
        }

        /**
         * Receives messages from client and processes given commands.
         */
        private void processCommands() {
            while (input.hasNextLine()) {
                int numberOfPlayers = players.size();
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
                } else if (command.startsWith("MOVE") || command.equals("SKIP")) {
                    if (command.startsWith("MOVE")) {
                        for (Player player : allPlayers) {
                            if (player != this) {
                                player.output.println(command);
                            }
                        }
                    }
                    currentPlayer = players.get((players.indexOf(this) + 1) % numberOfPlayers);
                    currentPlayer.output.println("MESSAGE Your turn");
                } else if (command.startsWith("FINISH")) {
                    String color = command.substring(7);
                    int place = allPlayers.size() - numberOfPlayers + 1;
                    output.println("FINISH " + place);
                    for (Player player : allPlayers) {
                        if (player != this) {
                            player.output.println("WINNER " + color + " " + place);
                        }
                    }
                    players.remove(this);
                    if (players.size() == 1) {
                        players.get(0).output.println("DEFEAT");
                        players.remove(0);
                    }
                }
            }
        }
    }
}
