package se.hangman.server;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

	public static Scanner userInput = new Scanner(System.in);
	

	public static int userInput() {
		try {
			int inputInt = userInput.nextInt();
			return inputInt;
		} catch (InputMismatchException e) {
			System.out.println("Input should be an Integer.. Try Again");
			userInput.nextLine();
			int retVal = userInput();
			return retVal;
		}
	}

	public static int getPort() {
		System.out.println("Enter PORT No.");
		int port = userInput();
		if ((port < 0) || (port > 65535)) {
			System.out.println("Port No. is out of range");
			int retPort = getPort();
			return retPort;
		} else {
			return port;
		}
	}


	public static void main(String[] args) {
		System.out.println("Starting Server");
		int port = getPort();
		
		NonblockingServer server = new NonblockingServer(port);
		try {
			server.createServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//servSocket = new ServSocket(port);
		System.out.println("Waiting for a Client to join....");
		//servSocket.createNewListnerThread();

	}

}
