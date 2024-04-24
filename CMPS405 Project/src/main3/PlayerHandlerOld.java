package main3;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerHandlerOld implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Map<String, String> tickets;
    private List<Game> games;
    private Semaphore gamesSemaphore;
    private Semaphore connectionsSemaphore;
    private AtomicInteger connectionsCount;
    private String ticket;
    private Game game;
    public PlayerHandlerOld(Socket socket, Map<String, String> tickets, List<Game> games,
                         Semaphore gamesSemaphore, Semaphore connectionsSemaphore, AtomicInteger connectionsCount) {
        this.socket = socket;
        this.tickets = tickets;
        this.games = games;
        this.gamesSemaphore = gamesSemaphore;
        this.connectionsSemaphore = connectionsSemaphore;
        this.connectionsCount = connectionsCount;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Acquire permit to handle the connection
            connectionsSemaphore.acquire();
            connectionsCount.incrementAndGet();

            // Initial interaction with the client
            out.println("Welcome to the game server! Enter your nickname:");
            String nickname = in.readLine();
            if (nickname == null || nickname.isEmpty()) {
                out.println("Invalid nickname. Closing connection.");
                socket.close();
                return;
            }

            // Issue a ticket for the player
            ticket = UUID.randomUUID().toString();
            tickets.put(ticket, nickname);
            out.println("Your ticket: " + ticket);

            // Handle player messages
            String input;
            
            while ((input = in.readLine()) != null) {
                String[] tokens = input.split(" ");
                String command = tokens[0];
                switch (command) {
                case "join":
                    if (tokens.length != 2) {
                        out.println("Usage: join <gameId>");
                        break;
                    }
                    int gameId = Integer.parseInt(tokens[1]);
                    joinGame(gameId);
                    break;
                case "select":
                    if (tokens.length != 2) {
                        out.println("Usage: select <number>");
                        break;
                    }
                    int number = Integer.parseInt(tokens[1]);
                    selectNumber(number);
                    break;
                case "newgame":
                    createNewGame();
                    break;
                case "exit":
                    return; // Exit the loop and close the connection
                default:
                    out.println("Unknown command. Available commands: join, select, newgame, exit");
            }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Release permit after handling the connection
            connectionsSemaphore.release();
            connectionsCount.decrementAndGet();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void createNewGame() throws InterruptedException {
        // Acquire permit to access games list
        gamesSemaphore.acquire();
        try {
            int gameId = games.size() + 1;
            Game newGame = new Game(gameId);
            games.add(newGame);
            newGame.addPlayer(new Player(tickets.get(ticket), out));
            out.println("New game created with ID " + gameId);
        } finally {
            // Release permit after accessing games list
            gamesSemaphore.release();
        }
    }

    private void joinGame(int gameId) throws InterruptedException {
        // Acquire permit to access games list
        gamesSemaphore.acquire();
        try {
            // Find the game with the specified ID
            for (Game game : games) {
                if (game.gameId == gameId) {
                    // Add player to the game
                    Player player = new Player(tickets.get(ticket), out);
                    game.addPlayer(player);
                    this.game = game;
                    out.println("Joined game " + gameId);
                    return;
                }
            }
            // Game not found
            out.println("Game not found.");
        } finally {
            // Release permit after accessing games list
            gamesSemaphore.release();
        }
    }

    private void selectNumber(int number) throws InterruptedException {
        // Find the game that the player is currently in
        if (game == null) {
            out.println("You are not in a game.");
            return;
        }

        // Acquire permit to access game data
        gamesSemaphore.acquire();
        try {
            // Check if the game is active
            if (!game.isActive()) {
                out.println("Game is not active.");
                return;
            }

            // Add the player's selection to the game
            game.addSelection(number);
            out.println("Number " + number + " selected for round " + game.getCurrentRound());
        } finally {
            // Release permit after accessing game data
            gamesSemaphore.release();
        }
    }
    // Other methods for handling player actions/messages
}