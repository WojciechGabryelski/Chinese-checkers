package server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

/**
 * Game is a class that performs server-side game tasks.
 */
public class Game {

    /**
     * The player taking the turn.
     */
    private Player currentPlayer;
    /**
     * Players who have not finished the game yet.
     */
    private final ArrayList<Player> players = new ArrayList<>();
    /**
     * All the players including these who finished the game.
     */
    private final ArrayList<Player> allPlayers = new ArrayList<>();

    private boolean isStarted = false;

    private int readyPlayers;

    private Integer id;

    private int moveNum;

    private final Map<Integer, List<String>> games = new HashMap<>();

    private final GameJDBCTemplate gameJDBCTemplate;

    public Game() {
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
        gameJDBCTemplate = (GameJDBCTemplate)context.getBean("gameJDBCTemplate");
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer gameId) {
        this.id = gameId;
    }

    public Player addPlayer(Socket socket) {
        if (allPlayers.size() < 6 && !isStarted) {
            return new Player(socket);
        }
        return null;
    }

    /**
     * Player is the class representing a player on the server side.
     */
    class Player implements Runnable {
        /**
         * A Socket object used to connect with client.
         */
        private final Socket socket;
        /**
         * A Scanner object used to get messages from client.
         */
        private Scanner input;
        /**
         * A PrintWriter object used to send messages to client.
         */
        private PrintWriter output;

        private String username;

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
                    games.clear();
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
            readyPlayers = 0;
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
            players.add(this);
            allPlayers.add(this);
            int numberOfPlayers = players.size();
            if (numberOfPlayers != 1 && numberOfPlayers != 5) {
                for (Player player : players) {
                    player.output.println("MESSAGE Press a button to start a game");
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
                if (command.equals("START")) {
                    readyPlayers++;
                    if (readyPlayers == players.size()) {
                        isStarted = true;
                        moveNum = 1;
                        int j = (int)(Math.random()*numberOfPlayers);
                        currentPlayer = players.get(j);
                        id = gameJDBCTemplate.create();
                        for (int i = 0; i < numberOfPlayers; i++) {
                            players.get(i).output.println("START " + numberOfPlayers + " " + i);
                            gameJDBCTemplate.insertPlayer(id, players.get(i).username, i + 1);
                        }
                        currentPlayer.output.println("MESSAGE Your turn");
                        for (int i = 0; i < numberOfPlayers; i++) {
                            if (i != j) {
                                players.get(i).output.println("MESSAGE Opponent's move, please wait");
                            }
                        }
                    }
                } else if (command.startsWith("QUIT")) {
                    return;
                } else if (command.startsWith("WHOSE_TURN")) {
                    if (numberOfPlayers == 1 || numberOfPlayers == 5) {
                        continue;
                    }
                    if (this != currentPlayer) {
                        output.println("MESSAGE Not your turn");
                    } else {
                        String params = command.substring(11);
                        output.println("YOUR_TURN " + params);
                    }
                } else if (command.startsWith("MOVE") || command.equals("SKIP")) {
                    if (command.startsWith("MOVE")) {
                        for (Player player : allPlayers) {
                            if (player != this) {
                                player.output.println(command);
                            }
                        }
                        String[] params = command.substring(5).split(" ");
                        gameJDBCTemplate.insertMove(id, moveNum, username, Integer.parseInt(params[0]), Integer.parseInt(params[1]),
                                Integer.parseInt(params[2]), Integer.parseInt(params[3]));
                        moveNum++;
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
                        gameJDBCTemplate.delete(id);
                    }
                } else if (command.startsWith("USERNAME")) {
                    username = command.substring(9);
                } else if (command.startsWith("GAMES")) {
                    StringBuilder params = new StringBuilder();
                    for (Integer id : gameJDBCTemplate.listGames(username)) {
                        params.append(id.toString()).append(" ");
                    }
                    output.println("GAMES " + params);
                } else if (command.startsWith("LOAD")) {
                    int gameId = Integer.parseInt(command.substring(5));
                    games.computeIfAbsent(gameId, k -> new LinkedList<>());
                    if (!games.get(gameId).contains(username)) {
                        games.get(gameId).add(username);
                        if (games.get(gameId).size() == gameJDBCTemplate.getPlayersNumber(gameId)) {
                            id = gameId;
                            moveNum = 1;
                            for (int i = 1; i <= games.get(id).size(); i++) {
                                String user = gameJDBCTemplate.getPlayerByPosition(id, i);
                                for (Player player : allPlayers) {
                                    if (player.username.equals(user)) {
                                        players.set(i - 1, player);
                                        break;
                                    }
                                }
                            }
                            while (players.size() > games.get(id).size()) {
                                players.remove(games.get(id).size());
                            }
                            games.clear();
                            for (Player player : allPlayers) {
                                if (!players.contains(player)) {
                                    player.output.println("OTHER");
                                }
                            }
                            for (int i = 0; i < players.size(); i++) {
                                allPlayers.set(i, players.get(i));
                            }
                            while (players.size() < allPlayers.size()) {
                                allPlayers.remove(players.size());
                            }
                            for (int i = 0; i < players.size(); i++) {
                                players.get(i).output.println("START " + players.size() + " " + i);
                            }
                            String user = players.get(0).username;
                            Move move = gameJDBCTemplate.getMove(id, 1);
                            for (int i = 2; move != null; i++) {
                                for (Player player : allPlayers) {
                                    player.output.println("MOVE " + move.getX1() + " " + move.getY1() + " "
                                    + move.getX2() + " " + move.getY2());
                                }
                                moveNum++;
                                user = move.getPlayer();
                                move = gameJDBCTemplate.getMove(id, i);
                            }
                            int j = 0;
                            for (int i = 0; i < players.size(); i++) {
                                if (players.get(i).username.equals(user)) {
                                    j = (i+1)%players.size();
                                    currentPlayer = players.get(j);
                                    break;
                                }
                            }
                            currentPlayer.output.println("MESSAGE Your turn");
                            for (int i = 0; i < players.size(); i++) {
                                if (i != j) {
                                    players.get(i).output.println("MESSAGE Opponent's move, please wait");
                                }
                            }
                        } else {
                            output.println("MESSAGE Please wait for other players to load the game");
                        }
                    }
                }
            }
        }
    }
}
