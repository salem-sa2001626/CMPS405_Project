package main3;

import java.io.PrintWriter;

class Player {
    String nickname;
    int points;
    private PrintWriter out;
    String ticket;
    boolean voted;
    int gamesWon;
    public Player(String nickname, String ticket,PrintWriter out) {
        this.nickname = nickname;
        this.points = 0; // Initial points
        this.out = out;
        this.ticket = ticket;
        this.voted = false;
    }
    public void sendMessage(String message) {
        out.println(message);
    }
	public String getNickname() {
		return nickname;
	}
	
	public boolean isVoted() {
		return voted;
	}
	public void setVoted(boolean voted) {
		this.voted = voted;
	}
	public int getGamesWon() {
		return gamesWon;
	}
	public void setGamesWon(int gamesWon) {
		this.gamesWon = gamesWon;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	public PrintWriter getOut() {
		return out;
	}
	public void setOut(PrintWriter out) {
		this.out = out;
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public void addPoint() {
		this.points++;
	}
	@Override
	public String toString() {
		return this.getNickname();
	}
    
}