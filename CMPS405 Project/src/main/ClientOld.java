package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientOld {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 13336;
    public static boolean inGame = false;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            // Read welcome message from the server
            String serverResponse = in.readLine();
            System.out.println(serverResponse);

            // Prompt the user to enter a nickname
            System.out.print("Enter your nickname: ");
            String nickname = userInput.readLine();
            out.println(nickname);

            // Read the ticket issued by the server
            String ticket = in.readLine();
            System.out.println("Your ticket: " + ticket);

            while (true) {
                // Prompt the user for input
            	if(!inGame) {
            		System.out.print("Enter command (join <gameId>, select <number>, newgame, exit): ");
                    String userInputStr = userInput.readLine();

                    // Send user input to the server
                    out.println(userInputStr);

                    // Check if user wants to exit
                    if (userInputStr.equalsIgnoreCase("exit")) {
                        break;
                    }

                    // Read and display server response
                    String serverResponseStr = in.readLine();
                    System.out.println(serverResponseStr);
            	}else {
            		System.out.println("Hello My Friend You are in game");
            	}
                
            }

            // Close resources
            userInput.close();
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}