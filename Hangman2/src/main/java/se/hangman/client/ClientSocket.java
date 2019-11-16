package se.hangman.client;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;




public class ClientSocket {

	private static final String FATAL_COMMUNICATION_MSG = "Lost connection.";
    private static final String FATAL_DISCONNECT_MSG = "Could not disconnect, will leave ungracefully.";

    private final ByteBuffer msgFromServer = ByteBuffer.allocateDirect(2048);
    private InetSocketAddress serverAddress;
    private SocketChannel socketChannel;
    private Selector selector;
    public boolean connected;
    private volatile boolean timeToSend = false;
    private String messagesToSend = null;

    
    LoginController loginController = null;
    GameController gameController = null;
	public ClientSocket() {
	}

	public void connectToServer(String ip, int port) throws Exception {
		try {
		     serverAddress = new InetSocketAddress(ip, port);
		       
            initConnection();
            initSelector();

            while (connected) {
                if (timeToSend) {
                    socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    timeToSend = false;
                }

                selector.select();
                for (SelectionKey key : selector.selectedKeys()) {
                    selector.selectedKeys().remove(key);
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isConnectable()) {
                        completeConnection(key);
                    } else if (key.isReadable()) {
                    	recvFromServer(key);
                    } else if (key.isWritable()) {
                        sendToServer(key);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(FATAL_COMMUNICATION_MSG);
        }
        try {
            doDisconnect();
        } catch (IOException ex) {
            System.err.println(FATAL_DISCONNECT_MSG);
        }
	}
	private void initSelector() throws IOException {
	        selector = Selector.open();
	        socketChannel.register(selector, SelectionKey.OP_CONNECT);
	    }

    private void initConnection() throws IOException {
	        socketChannel = SocketChannel.open();
	        socketChannel.configureBlocking(false);
	        socketChannel.connect(serverAddress);
	        connected = true;
	    }
    private void completeConnection(SelectionKey key) throws IOException {
        socketChannel.finishConnect();
        key.interestOps(SelectionKey.OP_READ);
    }

    private void sendMsg(String parts) throws IOException {
        this.messagesToSend = parts;
//        ByteBuffer msg = ByteBuffer.wrap(messagesToSend.getBytes());
//        socketChannel.write(msg);
        timeToSend = true;
        selector.wakeup();
    }
    private void sendToServer(SelectionKey key) throws IOException {
        ByteBuffer msg = ByteBuffer.wrap(messagesToSend.getBytes());
        socketChannel.write(msg);
        messagesToSend = null;
        key.interestOps(SelectionKey.OP_READ);
        
    }

    private void recvFromServer(SelectionKey key) throws IOException {
        msgFromServer.clear();
        int numOfReadBytes = socketChannel.read(msgFromServer);
        if (numOfReadBytes == -1) {
            throw new IOException(FATAL_COMMUNICATION_MSG);
        }
        readDataFromServer();
    }
    public void readDataFromServer() {
        
        String recvdString = extractMessageFromBuffer();
        int len = retrieveLengthFromString(recvdString);
		int expectedLength = Integer.parseInt(recvdString.substring(0, len));
		int currentLength = recvdString.substring(len, recvdString.length()).length();
		if (expectedLength == currentLength) {
			String request = recvdString.substring(len,recvdString.length());
			String methodName =  request.split("-")[0]; 
			String data =  request.split("-")[1];
			if (methodName.equalsIgnoreCase("login")) {
				loginController.response(data);
			}
			else if (methodName.equalsIgnoreCase("start")) {
				try {
					gameController.responseOfStart(data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (methodName.equalsIgnoreCase("guess")) {
				try {
					gameController.responseForGuess(data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
    }
    private String extractMessageFromBuffer() {
        msgFromServer.flip();
        byte[] bytes = new byte[msgFromServer.remaining()];
        msgFromServer.get(bytes);
        return new String(bytes);
    }
    /**
     * Stops the communicating thread and closes the connection with the server.
     *
     * @throws IOException If failed to close connection.
     */
    public void disconnect() throws IOException {
        connected = false;
        sendMsg("Disconnected");
    }

    private void doDisconnect() throws IOException {
        socketChannel.close();
        socketChannel.keyFor(selector).cancel();
    }
	


	public void writeDataToServerFromLoginController(String data, String request,LoginController loginController) throws IOException {
		this.loginController = loginController;
		String totalRrequest = request + "-".concat(data); 
		String writeData = Integer.toString(totalRrequest.length()).concat( totalRrequest);
		sendMsg(writeData);
	}
	public void writeDataToServerFromGameController(String data, String request,GameController gameController) throws IOException {
		this.gameController = gameController;
		String totalRrequest = request + "-".concat(data); 
		String writeData = Integer.toString(totalRrequest.length()).concat( totalRrequest);
		sendMsg(writeData);
	}

	public void close() {
		try {
			doDisconnect();
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
