package se.hangman.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;

public class ClientModel implements Runnable {

	private String PlayerName;
	private static Map<String, ClientSocket> PlayersMap;
	private List<String> PlayersList;
	public ClientSocket cSocket = null;
	private String ServerIp;
	private int ServerPort;

	ClientModel(String player, String ip, int port) {
		PlayerName = "";
		ServerIp = "";
		ServerPort = 0;
		PlayersMap = new HashMap<String, ClientSocket>();
		PlayersList = new ArrayList<String>();
		PlayerName = player;
		ServerIp = ip;
		ServerPort = port;
		Thread t = new Thread(this, PlayerName);
		t.start();
		}

	
	public boolean createNewPlayerThread() {
		
		if (!PlayersMap.containsKey(PlayerName)) {
			if(socketConnection()) {
				addToPlayersList(PlayerName);
//				Thread t = new Thread(this, PlayerName);
//				t.start();
				Platform.runLater(() -> {
					// Main.startGameUI(PlayerName, getSocketObject( PlayerName));
					Main.startLoginUI(PlayerName, getSocketObject(PlayerName));
				});
				return cSocket.connected;
			}
			else {
				return false;
			}
			
		} else {
			return false;
		}
	}

	public void run() {
		cSocket = new ClientSocket();
		try {
			cSocket.connectToServer(ServerIp, ServerPort);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean socketConnection() {
		return cSocket.connected;
		
	}

	public void addToPlayersList(String player) {
		PlayersMap.put(player, cSocket);
	}

	public static void removeFromPlayersList(String player) {
		if (PlayersMap.containsKey(player)) {
			PlayersMap.remove(player);
		}
	}

	public List<String> getList() {
		PlayersList.clear();
		PlayersList.addAll(PlayersMap.keySet());
		return PlayersList;
	}

	public ClientSocket getSocketObject(String player) {
		return PlayersMap.get(player);
	}
}
