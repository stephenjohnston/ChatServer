package sjohnston.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private final ServerSocket listener;
    private final int port;
    private final ServerState state;

    public ChatServer(int port) throws IOException
    {
    	this.port = port;
        this.listener = new ServerSocket(port);
        this.state = new ServerState();
    }

    public static void main(String args[]) throws IOException {
    	final int port = 2000;
        ChatServer server = new ChatServer(port);
        server.run();
    }

    public void run()
    {
        System.out.println("Server is now running on port " + port);
        while (true) {
            try {
                Socket connection = listener.accept();
                ConnectionHandler handler = new ConnectionHandler(connection, state);
                Thread t = new Thread(handler);
                t.start();
            }
            catch(Exception e) {
            	System.err.println(e); 
            }
        }
    }
}
