package se.hangman.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {

	private static ServSocket servSocket = null;
	public static Scanner userInput = new Scanner(System.in);
	public static List<String> ListOfWords = new ArrayList<String>();

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

	public static void loadFile(String file) {
		try {
			InputStream inputStream = Main.class.getResourceAsStream(file);
			InputStreamReader in = new InputStreamReader(inputStream);
			BufferedReader br = new BufferedReader(in);
			String line;
			while ((line = br.readLine()) != null) {
				ListOfWords.add(line);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		loadFile("/words.txt");
		System.out.println("Starting Server");
		int port = getPort();
		servSocket = new ServSocket(port);
		System.out.println("Waiting for a Client to join....");
		servSocket.createNewListnerThread();
		while (true) {

		}

	}

}
