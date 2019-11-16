package se.hangman.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;



public class NonblockingServer {

	private Selector selector;
	private ServerSocketChannel listeningSocketChannel;

	private static final int LINGER_TIME = 5000;
	private int PORT = 0;
	NonblockingServer (int port){
		PORT = port;
	}
	void createServer() throws IOException {
		try {
            initSelector();
            initListeningSocketChannel();
            while (true) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        startHandler(key);
                    } else if (key.isReadable()) {
                        recvFromClient(key);
                    } else if (key.isWritable()) {
                        sendToClient(key);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Server failure.");
        }
	}
	 private void initSelector() throws IOException {
	        selector = Selector.open();
	    }
	 private void initListeningSocketChannel() throws IOException {
	        listeningSocketChannel = ServerSocketChannel.open();
	        listeningSocketChannel.configureBlocking(false);
	        listeningSocketChannel.bind(new InetSocketAddress(PORT));
	        listeningSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
	    }
	 private void startHandler(SelectionKey key) throws IOException {
	        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
	        SocketChannel clientChannel = serverSocketChannel.accept();
	        clientChannel.configureBlocking(false);
	        ClientHandler handler = new ClientHandler(this, clientChannel);
	        clientChannel.register(selector, SelectionKey.OP_WRITE, new Client(handler));
	        clientChannel.setOption(StandardSocketOptions.SO_LINGER, LINGER_TIME); 
	        //Close will probably
	        //block on some JVMs.
	        // clientChannel.socket().setSoTimeout(TIMEOUT_HALF_HOUR); Timeout is not supported on 
	        // socket channels. Could be implemented using a separate timer that is checked whenever the
	        // select() method in the main loop returns.
	    }

	    private void recvFromClient(SelectionKey key) throws IOException {
	        Client client = (Client) key.attachment();
	        try {
	            client.handler.readData();
	        } catch (IOException clientHasClosedConnection) {
	            removeClient(key);
	        }
	    }

	    private void sendToClient(SelectionKey key) throws IOException {
	        Client client = (Client) key.attachment();
	        try {
	            client.sendAll();
	            key.interestOps(SelectionKey.OP_READ);
	        }  catch (IOException clientHasClosedConnection) {
	            removeClient(key);
	        }
	    }
	    private void removeClient(SelectionKey clientKey) throws IOException {
	        Client client = (Client) clientKey.attachment();
	        client.handler.disconnectClient();
	        clientKey.cancel();
	    }
	    // client Handle
	    private class Client {
	        private final ClientHandler handler;
	        private final String messagesToSend = "";

	        private Client(ClientHandler handler) {
	            this.handler = handler;
	            
	        }

	        private void sendAll() throws IOException { 
	          // handler.writeData(messagesToSend, "Connected");
	            
	        }
	    }
}
