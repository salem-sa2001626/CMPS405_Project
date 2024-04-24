package main3;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class Game {
    private int gameId;
    private List<Player> players;
    private boolean active;
    private int currentRound;
    private Map<String, Integer> selections;
    private Semaphore gameSemaphore;
    private final int MIN_PLAYERS_TO_START = 2;

    public Game(int gameId) {
        this.gameId = gameId;
        this.players = new ArrayList<>();
        this.active = false;
        this.currentRound = 0;
        this.selections = new HashMap<>();
        this.gameSemaphore = new Semaphore(1); // Only one thread can access game data at a time
    }

    public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getCurrentRound() {
		return currentRound;
	}

	public void setCurrentRound(int currentRound) {
		this.currentRound = currentRound;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public Map<String, Integer> getSelections() {
		return selections;
	}

	public void setSelections(Map<String, Integer> selections) {
		this.selections = selections;
	}
	
	public void removePlayer(String ticket) {
		for(Player player : players) {
			if(player.getTicket().equals(ticket)) {
				players.remove(player);
				break;
			}
		}
	}

	public void addPlayer(Player player) throws InterruptedException {
        gameSemaphore.acquire(); // Acquire permit to access game data
        try {
            players.add(player);
            if (players.size() >= MIN_PLAYERS_TO_START) {
                startGame(); // Start the game when minimum players join
            }
        } finally {
            gameSemaphore.release(); // Release permit after modifying game data
        }
    }

    public void startGame() {
        if (!active) {
            active = true;
            currentRound = 1;
            StringBuilder startMessage = new StringBuilder("The game has started! Round 1 begins now.\n");
            startMessage.append("Current players: ");
            for (Player player : players) {
                startMessage.append(player.getNickname()).append(", ");
            }
            startMessage.deleteCharAt(startMessage.length() - 1); // Remove the last comma
            startMessage.deleteCharAt(startMessage.length() - 1); // Remove the space after the last name
            notifyPlayers(startMessage.toString());
        }
    }

    public void addSelection(Player player, int selection) throws InterruptedException {
        gameSemaphore.acquire(); // Acquire permit to access game data
        try {
            String ticket = player.getTicket();
            if (active && ticket != null && !selections.containsKey(ticket)) {
                selections.put(ticket, selection);
                if (selections.size() == players.size()) {
                    int sum = 0;
                    for (int value : selections.values()) {
                        sum += value;
                    }
                    float avg = (float) sum / selections.size();
                    String playerNames = "";
                    String values = "";
                    String status = "";
                    String scores = "";
                    float closestValue = 99999999;
                    notifyPlayers("Average of selections for round " + currentRound + ": " + avg);
                    for (Map.Entry<String, Integer> entry : selections.entrySet()) {
                    	int value = entry.getValue();
                    		if((value-(avg*(2/3))) <= closestValue) {
                    			closestValue = value-(avg*(2/3));
                    			
                    		}
                    	}
                    for (Map.Entry<String, Integer> entry : selections.entrySet()) {
                    	int value = entry.getValue();
                    		if((value-(avg*(2/3))) == closestValue) {
                    			for(Player pl : players) {
                                	if(pl.getTicket().equals(entry.getKey())) {
                                		playerNames = playerNames +","+pl.getNickname();
                                		pl.addPoint();
                                		scores = scores +","+pl.getPoints();
                                		values = values+","+value;
                                		status = status+","+"win";
                                		
                                	}
                    			}
                    		}else {
                    			for(Player pl : players) {
                                	if(pl.getTicket().equals(entry.getKey())) {
                                		playerNames = playerNames +","+pl.getNickname();
                                		scores = scores +","+pl.getPoints();
                                		values = values+","+value;
                                		status = status+","+"lose";
                                	}
                    			}
                    			
                    		}
                    	}
       
                    notifyPlayers("game round "+this.currentRound+" "+playerNames.substring(1)+" "+values.substring(1)+" "+scores.substring(1)+" "+status.substring(1));
/*                    for(Player pl : players) {
                    	if(pl.getTicket().equals(closestPlayer)) {
                    		notifyPlayers("The winner of the round is: "+pl.getNickname()+", Selected Value: "+selections.get(closestPlayer));
                    	}
                    	
                    }*/
                    selections.clear(); // Clear selections for the next round
                    currentRound++; // Move to the next round
                }
 
            } else {
                player.sendMessage("The game is not active or you have already submitted a selection.");
            }
        } finally {
            gameSemaphore.release(); // Release permit after modifying game data
        }
    }


    public void endGame() {
        active = false;
        currentRound = 0;
        selections.clear();
        notifyPlayers("The game has ended.");
    }

    private void notifyPlayers(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    // Other game-related methods
}
