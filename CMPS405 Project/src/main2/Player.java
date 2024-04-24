package main2;

import java.io.PrintWriter;

class Player {
    String nickname;
    int points;
    private PrintWriter out;
    public Player(String nickname, PrintWriter out) {
        this.nickname = nickname;
        this.points = 0; // Initial points
        this.out = out;
    }
    public void sendMessage(String message) {
        out.println(message);
    }
}