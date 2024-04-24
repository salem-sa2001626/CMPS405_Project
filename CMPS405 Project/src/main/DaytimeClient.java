package main;
import java.net.*;
import java.io.*;

public class DaytimeClient {
	public static void main(String args[]) {
		try {
			// Get a Socket to the daytime service,
			Socket daytime = new Socket("localhost", 1300);
			System.out.println("Connected with server " + daytime.getInetAddress() + ":" + daytime.getPort());
			// Set the socket option just in case server stalls
			daytime.setSoTimeout(2000);
			// Read from the server
			BufferedReader reader = new BufferedReader(new InputStreamReader(daytime.getInputStream()));
			// Display result on the screen
			System.out.println("Result = " + reader.readLine());
			// Close the connection
			daytime.close();
		} catch (IOException ioe) {
			System.out.println("Error" + ioe);
		}
	}
}