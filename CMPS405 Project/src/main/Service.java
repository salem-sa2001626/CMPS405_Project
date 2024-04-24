package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class Service extends Thread{

	Socket nextClient;

	public Service(Socket nextClient) {
		super();
		this.nextClient = nextClient;
	}
	
	public void run() {
		PrintWriter output;
		try {
			output = new PrintWriter(nextClient.getOutputStream(), true);
			output.println(new Date());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if(nextClient != null)
					nextClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
}
