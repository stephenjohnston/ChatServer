package sjohnston.chat;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerState {
	private final Object lockObject = new Object();
    private final ArrayList<Socket> clients;
    private final ArrayList<String> names;
    
    public ServerState() {
    	this.clients = new ArrayList<Socket>();
    	this.names = new ArrayList<String>();
    }
    
    public synchronized void addUser(String name, Socket client) {
    	synchronized(lockObject) {
    		this.clients.add(client);
    		this.names.add(name);
    	}
    }
    
    public void removeUser(String name, Socket client) throws IOException {
    	synchronized(lockObject) {
    		this.clients.remove(client);
    		this.names.remove(name);	
    		client.close();
    	}
    }
    
    public List<String> getUsers() {
    	return this.names;
    }
    
    public List<Socket> getClients() {
    	return this.clients;
    }
    
    public Object getLockObject() { 
    	return this.lockObject;
    }
}
