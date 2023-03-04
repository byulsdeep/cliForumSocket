package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	ServerSocket serverSocket;
	Socket socket;
	//static List<PrintWriter> writers;
	
	public Server() {
		runServer();
	}
	public void runServer() {
		try {
			serverSocket = new ServerSocket(9999);
			//writers = new LinkedList<>();
			
			System.out.println("Waiting for clients to connect...");

			while (true) {
				socket = serverSocket.accept();
				System.out.println("Client connected: " + socket.getInetAddress());
				//writers.add(new PrintWriter(socket.getOutputStream(), true));
				new Thread(new Controller(socket)).start();
			}
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
}