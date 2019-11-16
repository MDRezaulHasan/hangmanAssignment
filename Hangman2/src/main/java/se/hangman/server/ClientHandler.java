package se.hangman.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.Key;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;



public class ClientHandler implements Runnable {
	
	
	public static class ClientMessage {
		String type;
		String Key;
		String player;
		String guess;

		@JsonCreator
		public ClientMessage(@JsonProperty("type") String type, @JsonProperty("Key") String Key,
				@JsonProperty("player") String player, @JsonProperty("guess") String guess) {

			this.type = type;
			this.Key = Key;
			this.player = player;
			this.guess = guess;
		}
	}

	public static class ClientCredentials {
		String username;
		String password;
		@JsonCreator
		public ClientCredentials(@JsonProperty("username") String username, @JsonProperty("password") String password) {

			this.username = username;
			this.password = password;
		}
	}

	private class Message {
		int NoOfLetters;
		String correctGuessedLetters;
		int NoOfAttemptsLeft;
		int Score;
	}

	private String username = "r";
	private String password = "r";
	private Key JWTKey = null;
	
	private ObjectMapper Obj = null;
	private HangmanGame game = null;
	private Message msg = null;
	private final SocketChannel clientChannel;
    private final ByteBuffer msgFromClient = ByteBuffer.allocateDirect(2064);
    NonblockingServer server;  // will have to remove
    
    ClientHandler(NonblockingServer server, SocketChannel clientChannel) {
        this.server = server;
        this.clientChannel = clientChannel;
        this.Obj = new ObjectMapper();
		this.Obj.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		Thread t = new Thread(this);
		t.start();
    }
    
    /**
     * Sends the specified message to the connected client.
     *
     * @param msg The message to send.
     * @throws IOException If failed to send message.
     */
    void writeData(String data, String request) throws IOException {
    	String totalRrequest = request + "-".concat(data); 
		String writeData = Integer.toString(totalRrequest.length()).concat( totalRrequest);
    	ByteBuffer msg = ByteBuffer.wrap(writeData.getBytes());
        clientChannel.write(msg);
        if (msg.hasRemaining()) {
            System.out.println("Could not send message");
        }
    }
    
    /**
     * Reads a message from the connected client, then submits a task to the default
     *
     * @throws IOException If failed to read message
     */
    void readData() throws IOException {
        msgFromClient.clear();
        int numOfReadBytes ;
        numOfReadBytes = clientChannel.read(msgFromClient);
        if (numOfReadBytes == -1) {
            throw new IOException("Client has closed connection.");
        }
        
        String recvdString = extractMessageFromBuffer();
        System.out.println(recvdString);
        
        int len = retrieveLengthFromString(recvdString);
		int expectedLength = Integer.parseInt(recvdString.substring(0, len));
		int currentLength = recvdString.substring(len, recvdString.length()).length();
		if (expectedLength == currentLength) {
			String request = recvdString.substring(len,recvdString.length());
			String methodName =  request.split("-")[0]; 
			String data =  request.split("-")[1];
			
			if (methodName.equalsIgnoreCase("login")) {
				checkLogin(data);
			}
			else if (methodName.equalsIgnoreCase("start")) {
				guessWord(data,methodName);
			}
			else if (methodName.equalsIgnoreCase("guess")) {
				guessWord(data,methodName);
				
			}
			
		} 
    }

    private String extractMessageFromBuffer() {
        msgFromClient.flip();
        byte[] bytes = new byte[msgFromClient.remaining()];
        msgFromClient.get(bytes);
        return new String(bytes);
    }

    /**
     * Closes this instance's client connection.
     *
     * @throws IOException If failed to close connection.
     */
    void disconnectClient() throws IOException {
        clientChannel.close();
    }

	@Override
	public void run() {
		
		
	}
	private void checkLogin(String str) {
		boolean status = false;
		try {
			status = validateUser(str);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		if (status) {
			try {
				game = new HangmanGame();
			} catch (IOException e) {
				e.printStackTrace();
			}
			msg = new Message();
		}
	}
	
	private void guessWord (String data, String method) {
		System.out.println("Waiting to read data");
		ClientMessage cmsg = null;
		cmsg = JSONStringToObject(data);
		//if (validateJWTKey(cmsg.Key)) { // need to check valid key
			System.out.println("Key Succesfully Validated");
			ActionOnCmd(cmsg.type, cmsg.guess);
			if (cmsg.type.compareTo("Stop") != 0) {
				try {
					writeData(objToJSONString(msg),method);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
//		} else {
//			System.out.println("Key Validation Failed");
//			try {
//				writeData("failed",method);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
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

	public void close() {
		try {
			disconnectClient();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ClientMessage JSONStringToObject(String jsonString) {

		try {
			return Obj.readValue(jsonString, ClientMessage.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ClientCredentials JSONStringToLoginObject(String jsonString) {

		try {
			return Obj.readValue(jsonString, ClientCredentials.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String objToJSONString(Message msg) {
		try {
			return Obj.writeValueAsString(msg);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void ActionOnCmd(String cmd, String str) {
		if (cmd.compareTo("Start") == 0) {
			game.selectWord();
			updateMessage();
		} else if (cmd.compareTo("Stop") == 0) {
		} else if (cmd.compareTo("Guess") == 0) {
			if (str.length() == 1) {
				game.matchLetter(str);
				updateMessage();
			} else {
				game.matchWord(str);
				updateMessage();
			}
		}
	}

	public void updateMessage() {
		msg.correctGuessedLetters = game.msg.correctGuessedLetters;
		msg.NoOfAttemptsLeft = game.msg.NoOfAttemptsLeft;
		msg.NoOfLetters = game.msg.NoOfLetters;
		msg.Score = game.msg.Score;
	}

	public boolean validateUser(String jsonString) throws IOException {
		ClientCredentials cred = JSONStringToLoginObject(jsonString);
		if ((cred.username.compareTo(username) == 0) && (cred.password.compareTo(password) == 0)) {
			String JWTKey = generateJWTKey(jsonString);
			writeData(JWTKey, "login");
			return true;
		} else {
			writeData("201", "login");
			return false;
		}
	}

	public String generateJWTKey(String jsonString) {
		JWTKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
		return Jwts.builder().setSubject(jsonString).signWith(JWTKey).compact();
	}

	public boolean validateJWTKey(String key) {
		try {
			Jwts.parser().setSigningKey(JWTKey).parseClaimsJws(key);
			return true;

		} catch (Exception e) {
			return false;
		}

	}

}
