package controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import backend.Authentication;
import backend.Forum;
import utilities.ProjectUtils;

public class Controller implements Runnable {
	private Socket socket;
	BufferedReader requestReader;
	//PrintWriter responseSender;
	String jobCode;
	String message;
	ProjectUtils pu;
	DataOutputStream responseSender;

	public Controller(Socket socket) {
		this.socket = socket;
		this.pu = new ProjectUtils();
	}

	public void run() {
		try {
			requestReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			responseSender = new DataOutputStream(socket.getOutputStream());
			entrance();
		} catch (IOException e) {
			System.out.println("Client disconnected: " + socket.getInetAddress());
		}
	}
	void entrance() {
		while (true) {
			String clientData = getRequest();
			System.out.println("Received client data from " + socket.getInetAddress());
			System.out.println(clientData);

			jobCode = pu.getJobCode(clientData);
			switch (jobCode) {
			case "isIdUsed":
			case "signUp":
			case "logIn":
				message = new Authentication().backController(clientData, pu);
				break;
			case "getPosts":
			case "addPost":
			case "getMaxPostIdx":
			case "deletePost":
				message = new Forum().backController(clientData, pu);
				break;
			}
			sendResponse(message);
		}
	}
	void sendResponse(String serverData) {
		try {
			responseSender.writeUTF(serverData);
		} catch (Exception e) {
			// TODO: handle exception
		}
		//responseSender.flush();
		// responseSender.println(serverData);
		// print won't work, flush don't help, even without autoflush
	}
	String getRequest() {
		try {
			return requestReader.readLine();
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
