package main;
import java.io.*;
import java.net.*;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Map<String, String> tickets;
    private List<Game> games;
    private Semaphore gamesSemaphore;
    private String ticket;
    private Game game;
    private Player player;
    private Map<String, Integer> leaderboard;
    public PlayerHandler(Socket socket, Map<String, String> tickets, List<Game> games,
                         Semaphore gamesSemaphore, Map<String, Integer> leaderboard) {
        this.socket = socket;
        this.tickets = tickets;
        this.games = games;
        this.gamesSemaphore = gamesSemaphore;
        this.leaderboard = leaderboard;
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
            out.println("Welcome to the game server! Enter your nickname:");
            String nickname = in.readLine();
            if (nickname == null || nickname.isEmpty()) {
                out.println("Invalid nickname. Closing connection.");
                socket.close();
                return;
            }
            ticket = UUID.randomUUID().toString();
            tickets.put(ticket, nickname);
            out.println("Your ticket: " + ticket);
            this.player = new Player(tickets.get(ticket),ticket, out);
            leaderboard.put(this.player.getNickname(), this.player.getGamesWon());
            showLeaderboard();
            out.println("Available commands: view, join, leave, select, newgame, exit | Usage /<Command>");
            String input;
            while ((input = in.readLine()) != null) {
                String[] tokens = input.split(" ");
                String command = tokens[0];
                switch (command) {
                    case "/join":
                        if (tokens.length != 2) {
                            out.println("Usage: join <gameId>");
                            break;
                        }
                        int gameId = Integer.parseInt(tokens[1]);
                        joinGame(gameId);
                        break;
                    case "/view":
                    	view();
                    	break;
                    case "/select":
                        if (tokens.length != 2) {
                            out.println("Usage: select <number>");
                            break;
                        }
                        int number = Integer.parseInt(tokens[1]);
                        selectNumber(number);
                        break;
                    case "/leave":
                    	if(this.game == null) {
                    		out.println("You are not currently in any game.");
                    	}else {
                    		leaveGame();
                    	}
                    	break;
                    case "/start":
                    	if(this.game == null) {
                    		out.println("You are not currently in any game.");
                    	}else {
                    		this.game.voteStart(player);
                    	}
                    	break;
                    case "/newgame":
                        createNewGame();
                        break;
                    case "/exit":
                        return;
                    default:
                    	if(this.game == null || command.charAt(0) == '/') {
                            out.println("Available commands: view, join, leave, select, newgame, exit | Usage /<Command>");
                    	}else {
                    		String chat = this.player.getNickname()+" > "+input;
                    		game.notifyPlayers(chat);
                    	}
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createNewGame() throws InterruptedException {
        gamesSemaphore.acquire();
        try {
            int gameId = games.size() + 1;
            Game newGame = new Game(gameId,this.leaderboard);
            games.add(newGame);
            
            out.println("New game created with ID " + gameId);
        } finally {
            gamesSemaphore.release();
        }
    }

    private void joinGame(int gameId) throws InterruptedException {
        gamesSemaphore.acquire();
        try {
            for (Game game : games) {
                if (game.getGameId() == gameId) {
                	if(game.isActive()) {
                		this.player.sendMessage("Unable to join game, game is currently active.");
                		return;
                	}else {
                        game.addPlayer(this.player);
                        this.game = game;
                        out.println("Joined game " + gameId);
                        return;
                	}

                }
            }
            out.println("Game not found.");
        } finally {
            gamesSemaphore.release();
        }
    }
    private void view() throws InterruptedException {
    	gamesSemaphore.acquire();
        try {
        	if(games.isEmpty()) {
        		out.println("No game is currently available.");
        	}
        	else {
                for(Game game : games) {
                	out.println("Game ID: "+game.getGameId()+" | Players: "+game.getPlayers()+" | Is Active? "+game.isActive());
                }
        	}

        } finally {
            gamesSemaphore.release();
        }
    }
    private void showLeaderboard() {
    	out.println("Leaderboard:");
        leaderboard.entrySet().stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
        .limit(5)
        .forEach(entry -> out.println(entry.getKey() + ": " + entry.getValue()));
    }
    private void leaveGame() throws InterruptedException {
        gamesSemaphore.acquire();
        try {
            this.game.removePlayer(this.ticket);
            this.game = null;
            out.println("You have left the game.");
        } finally {
            gamesSemaphore.release();
        }
    }
    private void selectNumber(int number) throws InterruptedException {
        if (game == null) {
            out.println("You are not in a game.");
            return;
        }
        if (number > 100 || number < 0) {
            out.println("Please only choose a number between (0 - 100)");
            return;
        }
        gamesSemaphore.acquire();
        try {
            if (!game.isActive()) {
                out.println("Game is not active.");
                return;
            }
            out.println("Number " + number + " selected for round " + game.getCurrentRound());
            game.addSelection(player, number);

        } finally {
            gamesSemaphore.release();
        }
    }
}
