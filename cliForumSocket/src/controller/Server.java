package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public Server() {
		this.runServer();
	}
	public void runServer() {
		try {
			ServerSocket serverSocket = new ServerSocket(2400);			
			System.out.println("Waiting for clients to connect...");
			while (true) {
				Socket socket = serverSocket.accept();
				String clientIP = String.valueOf(socket.getInetAddress());
				clientIP = clientIP.substring(1, clientIP.length());
				System.out.println("Client connected: " + clientIP);
				new Thread(new Controller(socket)).start();
			}
		} catch (IOException e) {
			System.out.println("Error starting server.");
		}
	}
}