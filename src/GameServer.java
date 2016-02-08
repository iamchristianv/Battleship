import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer extends Thread {
	// Parent Window
	MenuWindow menuWindow;
	
	// Network Data
	private ServerSocket ss;
	private Socket[] sockets;
	private ObjectOutputStream[] senders;
	private ObjectInputStream[] receivers;
	private Messenger[] messengers;
	private String[] users;
	private boolean[] ready;
	private boolean[] restart;
	private int count;
	
	// Constructor
	GameServer(MenuWindow menuWindow, int port) { 
		this.menuWindow = menuWindow;
		
		try {
			this.ss = new ServerSocket(port);
		} catch(IOException ioe) {
			System.out.println("IOException in Server() in Server");
		}
		
		this.sockets = new Socket[3];
		this.senders = new ObjectOutputStream[3];
		this.receivers = new ObjectInputStream[3];
		this.messengers = new Messenger[3];
		this.users = new String[3];
		this.ready = new boolean[3];
		this.ready[0] = false;
		this.ready[1] = false;
		this.ready[2] = false;
		this.restart = new boolean[3];
		this.restart[0] = false;
		this.restart[1] = false;
		this.restart[2] = false;
		this.count = 0;
	}
	
	// run()
	public void run() {
		try {		
			while(true) {
				this.count++;
				
				if(this.count > 2) {
					this.sockets[0] = this.ss.accept();
					this.senders[0] = new ObjectOutputStream(this.sockets[0].getOutputStream());
					this.senders[0].writeObject(-1);
					return;
				}

				this.sockets[this.count] = this.ss.accept();	
				this.senders[this.count] = new ObjectOutputStream(this.sockets[this.count].getOutputStream());
				this.receivers[this.count] = new ObjectInputStream(this.sockets[this.count].getInputStream());
				this.senders[this.count].writeObject(this.count);
				this.messengers[this.count] = new Messenger(this.count);
				this.messengers[this.count].start();
			}
		} catch(IOException ioe) {
			System.out.println("IOException in run() in Server");
		} catch(NullPointerException npe) {
			System.out.println("NullPointerException in run() in Server");
		}
	}
	
	// Messenger
	class Messenger extends Thread {
		private int index;
		
		// Constructor
		Messenger(int index) {
			this.index = index;
		}
		
		// run()
		public void run() {			
			try {
				while(true) {
					String message = (String)GameServer.this.receivers[this.index].readObject();
					String[] components = message.split(":");
					
					int index = Integer.parseInt(components[0]);
										
					if(components[1].equals("Name")) {						
						GameServer.this.users[index] = components[2];
						
						if(index == 2) {
							GameServer.this.senders[1].writeObject("Other Name:" + GameServer.this.users[2]);
							GameServer.this.senders[1].writeObject("Show Game Window");
							GameServer.this.senders[2].writeObject("Other Name:" + GameServer.this.users[1]);
							GameServer.this.senders[2].writeObject("Show Game Window");
						}
					}
					
					if(components[1].equals("Disconnect")) {
						if(index == 1) {
							GameServer.this.senders[2].writeObject("Other Disconnected");
						} else if(index == 2) {
							GameServer.this.senders[1].writeObject("Other Disconnected");
						}
					}
					
					if(components[1].equals("Ships")) {
						GameServer.this.ready[index] = true;
						
						Ship[][] ships = (Ship[][])GameServer.this.receivers[this.index].readObject();

						if(index == 1) {
							GameServer.this.senders[2].writeObject("Ships");
							GameServer.this.senders[2].writeObject(ships);
						} else if(index == 2) {
							GameServer.this.senders[1].writeObject("Ships");
							GameServer.this.senders[1].writeObject(ships);
						}
						
						if(GameServer.this.ready[1] == true && GameServer.this.ready[2] == true) {
							GameServer.this.senders[1].writeObject("Start Game");
							GameServer.this.senders[2].writeObject("Start Game");
						}
					}
					
					if(components[1].equals("Coordinates")) {
						if(index == 1) {
							GameServer.this.senders[2].writeObject("Coordinates:" + components[2]);
						} else if(index == 2) {
							GameServer.this.senders[1].writeObject("Coordinates:" + components[2]);
						}
					}
					
					if(components[1].equals("Message")) {
						if(index == 1) {
							GameServer.this.senders[2].writeObject("Message:" + components[2]);
						} else if(index == 2) {
							GameServer.this.senders[1].writeObject("Message:" + components[2]);
						}
					}
					
					if(components[1].equals("Restart Game")) {
						GameServer.this.restart[index] = true;
						
						if(GameServer.this.restart[1] == true && GameServer.this.restart[2] == true) {
							GameServer.this.ready[1] = false;
							GameServer.this.ready[2] = false;
							GameServer.this.restart[1] = false;
							GameServer.this.restart[2] = false;
							GameServer.this.senders[1].writeObject("Show Game Window");
							GameServer.this.senders[2].writeObject("Show Game Window");
						}
					}
					
					if(components[1].equals("Do Not Restart Game")) {
						GameServer.this.senders[1].writeObject("Close Game");
					}
				}
			} catch (IOException ioe) {
				System.out.println("IOException in Messenger in GameServer for Player " + this.index);
				
				try {
					if(this.index == 2) {
						GameServer.this.senders[1].writeObject("Friend Disconnected");
					} 
				} catch(IOException e) {
					System.out.println("IOException in run() in Messenger in GameServer");
				}
			} catch (ClassNotFoundException cnfe) {
				System.out.println("ClassNotFoundException in Messenger in GameServer for Player " + this.index);
			}
		}
	}
}