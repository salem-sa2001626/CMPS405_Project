package main3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader serverIn;
    private PrintWriter out;
    private BufferedReader userInput;

    public Client(String serverAddress, int serverPort) {
        try {
            socket = new Socket(serverAddress, serverPort);
            serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            userInput = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        // Start a thread to read messages from the server
        new Thread(() -> {
            try {
                String message;
                while ((message = serverIn.readLine()) != null) {
                    System.out.println(message); // Print messages from the server
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Main thread handles user input and sends commands to the server
        try {
            String userInputStr;
            while ((userInputStr = userInput.readLine()) != null) {
                out.println(userInputStr); // Send user input to the server
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 13336);
        client.start();
    }
}

