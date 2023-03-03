package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Server {
	ServerSocket serverSocket;
	Socket socket;
	//static List<PrintWriter> writers;
	
	public Server() {
		runServer();
	}
	public void runServer() {
		try {
			serverSocket = new ServerSocket(2400);
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
class Handler implements Runnable {
	Socket s;
	Handler(Socket s) {
		this.s = s;
	}
	
	@Override
	public void run() {
		
	}
}