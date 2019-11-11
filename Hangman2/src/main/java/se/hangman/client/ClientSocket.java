package se.hangman.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientSocket {

	private Socket cSocket = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;

	public ClientSocket() {

	}

	public void connectToServer(String ip, int port) throws Exception {
		cSocket = new Socket(ip, port);
		dis = new DataInputStream(cSocket.getInputStream());
		dos = new DataOutputStream(cSocket.getOutputStream());
	}

	public String readDataFromServer() {
		try {
			String readData = dis.readUTF();
			int len = retrieveLengthFromString(readData);
			int expectedLength = Integer.parseInt(readData.substring(0, len));
			int currentLength = readData.substring(len, readData.length()).length();
			if (expectedLength == currentLength) {
				return readData.substring(len, readData.length());
			} else {
				return "";
			}
			// return dis.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void writeDataToServer(String data) {
		try {
			String writeData = Integer.toString(data.length()).concat(data);
			dos.writeUTF(writeData);
			// dos.writeUTF(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			dis.close();
			dos.close();
			cSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int retrieveLengthFromString(String str) {
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.substring(i, i + 1).matches("[0-9]"))
				count += 1;
			else
				break;
		}
		return count;
	}

}
