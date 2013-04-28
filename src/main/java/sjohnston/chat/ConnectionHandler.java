package sjohnston.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

class ConnectionHandler implements Runnable
{
	private final Socket socket;
	private final ServerState state;
	private final BufferedReader in;
	private final PrintWriter out;
	private String name;

	public ConnectionHandler(Socket s, ServerState state) throws IOException
	{
		this.state = state;
		this.socket = s;
		this.in = new BufferedReader(new InputStreamReader
				(socket.getInputStream()));
		this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter
				(socket.getOutputStream())));
	}

	private void signOut() throws IOException {
		this.broadcast(name + " leaves the chat server...");
		state.removeUser(name, socket);
	}

	private void signIn() throws IOException {
		out.print("Enter a chat handle: ");
		out.flush();
		String nm = in.readLine();
		this.name = nm;
		out.println("Welcome, " + name);
		out.flush();
		this.broadcast(this.name +  " has entered the chat server.");
		state.addUser(name, socket);
	}

	public void run() {
		try {
			signIn();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String msg;
		while(true) {
			try {
				msg = in.readLine();
				if (msg == null || msg.equals("quit")) {
					signOut();
					break;
				} else if (msg.equals("whoison")) {
					synchronized(this.state.getLockObject()) {
						for (String user : this.state.getUsers()) {
							out.println(user);
						}
					}
					out.flush();
				}
				else 
					this.broadcast(name + ": "+msg);
			}
			catch(Exception ex) {
				ex.printStackTrace();
				break;
			}
		}
	}

	private void broadcast(String msg) {
		try {
			synchronized(this.state.getLockObject()) {
				for (Socket c_s : this.state.getClients()) {
					PrintWriter c_out =
							new PrintWriter(new BufferedWriter(new OutputStreamWriter
									(c_s.getOutputStream())));
					System.out.println(msg);
					c_out.println(msg);
					c_out.flush();
				}
			}
		}
		catch(Exception ex) { ex.printStackTrace(); }
	}
}
