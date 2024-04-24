package main2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Game {
    int gameId;
    List<Player> players;
    boolean active;
    int currentRound;
    List<Integer> selections;
    Semaphore gameSemaphore;
    int MIN_PLAYERS_TO_START = 2;

    public Game(int gameId) {
        this.gameId = gameId;
        this.players = new ArrayList<>();
        this.active = false;
        this.currentRound = 0;
        this.selections = new ArrayList<>();
        this.gameSemaphore = new Semaphore(1); // Only one thread can access game data at a time
    }

    public void addPlayer(Player player) throws InterruptedException {
        gameSemaphore.acquire(); // Acquire permit to access game data
        try {
            players.add(player);
            if(players.size() == 2) {
            	startGame();
            }
        } finally {
            gameSemaphore.release(); // Release permit after modifying game data
        }
    }

    public void start() throws InterruptedException {
        gameSemaphore.acquire(); // Acquire permit to access game data
        try {
            active = true;
            currentRound = 1;
        } finally {
            gameSemaphore.release(); // Release permit after modifying game data
        }
    }

    public void end() throws InterruptedException {
        gameSemaphore.acquire(); // Acquire permit to access game data
        try {
            active = false;
        } finally {
            gameSemaphore.release(); // Release permit after modifying game data
        }
    }

    public boolean isActive() throws InterruptedException {
        gameSemaphore.acquire(); // Acquire permit to access game data
        try {
            return active;
        } finally {
            gameSemaphore.release(); // Release permit after accessing game data
        }
    }

    public void addSelection(int selection) throws InterruptedException {
        gameSemaphore.acquire(); // Acquire permit to access game data
        try {
            selections.add(selection);
            if(selections.size() == players.size()) {
            	int sum = 0;
            	for(int i : selections) {
            		sum += i;
            	}
            	float avg = (float) sum / selections.size();
            	
            	notifyPlayers(""+avg);
            }
        } finally {
            gameSemaphore.release(); // Release permit after modifying game data
        }
    }
    public int getCurrentRound() throws InterruptedException {
        gameSemaphore.acquire(); // Acquire permit to access game data
        try {
            return currentRound;
        } finally {
            gameSemaphore.release(); // Release permit after accessing game data
        }
    }
    
    public void startGame() {
        if (players.size() >= MIN_PLAYERS_TO_START) { // MIN_PLAYERS_TO_START is a constant indicating the minimum players required to start
            // Start the game
            active = true;
            currentRound = 1;
            notifyPlayers("The game has started! Round 1 begins now.");
        }
    }

    private void notifyPlayers(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }
    // Other game-related methods
}