package main;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    static final int PORT = 13336;
    static final int MAX_PLAYERS_PER_GAME = 6;

    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private List<Game> games;
    private Map<String, String> tickets;
    private Map<String, Integer> leaderboard;
    private Semaphore gamesSemaphore;

    public Server() {
        try {
            serverSocket = new ServerSocket(PORT);
            executorService = Executors.newCachedThreadPool();
            games = new ArrayList<>();
            tickets = new HashMap<>();
            leaderboard = new HashMap<>();
            gamesSemaphore = new Semaphore(1); // Only one thread can access games list at a time
            Game game = new Game(1,leaderboard);
            games.add(game);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Server started...");
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                Runnable playerHandler = new PlayerHandler(socket, tickets, games, gamesSemaphore, leaderboard);
                executorService.execute(playerHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}