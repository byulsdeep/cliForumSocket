package controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import backend.Authentication;
import backend.Forum;
import utilities.ProjectUtils;

public class Controller implements Runnable {
	Socket socket;
	BufferedReader requestReader;
	String jobCode;
	String message = "server error";
	ProjectUtils pu;
	DataOutputStream responseSender;
	String clientIP;

	public Controller(Socket socket) {
		this.socket = socket;
		this.clientIP = String.valueOf(socket.getInetAddress());
		this.clientIP = this.clientIP.substring(1, this.clientIP.length());
		this.pu = new ProjectUtils();
	}
	@Override
	public void run() {
		try {
			this.requestReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.responseSender = new DataOutputStream(this.socket.getOutputStream());
			this.entrance();
		} catch (IOException e) {
			System.out.println("Client disconnected: " + this.clientIP);
		}
	}
	void entrance() {
		while (true) {
			String clientData = this.getRequest();
			if (clientData.contains("Connection reset"))
				break;
			if (!clientData.contains("getComments")) {
				System.out.println("Received client data from " + this.clientIP);
				System.out.println(clientData);
			}	
			this.jobCode = this.pu.getJobCode(clientData);
			switch (this.jobCode) {
			case "isIdUsed":
			case "signUp":
			case "logIn":
				this.message = new Authentication().backController(clientData, this.pu);
				break;
			case "getPosts":
			case "addPost":
			case "getNextPostIdx":
			case "deletePost":
			case "getComments":
			case "getNextCommentIdx":
			case "addComment":
				this.message = new Forum().backController(clientData, this.pu);
				break;
			}
			this.sendResponse(this.message);
		}
	}
	void sendResponse(String serverData) {
		try {
			this.responseSender.writeUTF(serverData);
		} catch (Exception e) {
			try { // can't send null with DataOutputStream or else "NullPointerException"
				this.responseSender.writeUTF("Failed to get request");
			} catch (IOException e1) {
			}
			System.out.println("Failed to send response");
		}
		// responseSender.flush();
		// responseSender.println(serverData);
		// print won't work, flush don't help, even without autoflush
	}
	String getRequest() {
		try {
			return this.requestReader.readLine();
		} catch (Exception e) {
			System.out.println("Failed to get request");
			return "Failed to send request";
		}
	}
}
