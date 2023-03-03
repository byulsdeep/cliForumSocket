package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public Server() {
		runServer();
	}
	public void runServer() {
		try {
			ServerSocket serverSocket = new ServerSocket(2400);
			System.out.println("Waiting for clients to connect...");

			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("Client connected: " + socket.getInetAddress());

				new Thread(new Controller(socket)).start();
			}
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

}