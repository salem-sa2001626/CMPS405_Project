package main;

import java.net.*;
import java.io.*;
import java.util.*;

public class DaytimeServer {
	public static void main(String args[]) {
		try {
			// Bind to service port, so clients access to daytime service
			ServerSocket server = new ServerSocket(1300);
			System.out.println("Server waiting for client on port " + server.getLocalPort());
			System.out.println("Day Time Service Started");
			for (;;) {
				// Get the next TCP Client
				Socket nextClient = server.accept();
				// Display connection details
				System.out
						.println("Receiving Request From " + nextClient.getInetAddress() + ":" + nextClient.getPort());
				// Write the current time to the client socket
				Service servThread = new Service(nextClient);
				servThread.start();
			}
		} catch (IOException ioe) {
			System.out.println("Error" + ioe);
		}
	}


}

