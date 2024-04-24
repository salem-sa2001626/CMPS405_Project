package main3;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    static final int PORT = 13336;
    static final int MAX_PLAYERS_PER_GAME = 6;
    static final int MAX_CONCURRENT_CONNECTIONS = 100;

    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private List<Game> games;
    private Map<String, String> tickets;
    private Map<String, Integer> leaderboard;
    private Semaphore gamesSemaphore;
    private Semaphore connectionsSemaphore;
    private AtomicInteger connectionsCount;

    public Server() {
        try {
            serverSocket = new ServerSocket(PORT);
            executorService = Executors.newCachedThreadPool();
            games = new ArrayList<>();
            tickets = new HashMap<>();
            leaderboard = new ConcurrentHashMap<>();
            gamesSemaphore = new Semaphore(1); // Only one thread can access games list at a time
            connectionsSemaphore = new Semaphore(MAX_CONCURRENT_CONNECTIONS);
            connectionsCount = new AtomicInteger(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Server started...");
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                connectionsSemaphore.acquire(); // Acquire a permit to handle the connection
                connectionsCount.incrementAndGet();
                Runnable playerHandler = new PlayerHandler(socket, tickets, games, gamesSemaphore, connectionsSemaphore, connectionsCount);
                executorService.execute(playerHandler);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}