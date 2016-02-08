import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

public class GameClient extends Thread {
	// Parent Window
	MenuWindow menuWindow;
	
	// Game Window
	GameWindow gameWindow;
	
	// Network Data
	private Socket socket;
	private ObjectOutputStream sender;
	private ObjectInputStream receiver;
	private String userName;
	private int index;
	private String otherName;
	
	// Constructor
	GameClient(MenuWindow menuWindow, String address, int port, String name) {
		this.menuWindow = menuWindow;
		this.gameWindow = null;
		
		try {
			this.socket = new Socket(address, port);
			this.sender = new ObjectOutputStream(this.socket.getOutputStream());
			this.receiver = new ObjectInputStream(this.socket.getInputStream());
		} catch(IOException ioe) {
			System.out.println("IOException in Client() in Client");
		}
		
		this.userName = name;
		this.otherName = "other name not assigned";
	}
	
	// sendShips()
	void sendShips(Ship[][] ships) throws IOException {
		if(this.menuWindow.lostConnection() && this.otherName != "Computer") {
			this.gameWindow.dispose();
			this.menuWindow.setVisible(true);
			JOptionPane.showMessageDialog(this.menuWindow, "You have disconnected!", "Connection Error", JOptionPane.PLAIN_MESSAGE, null);
			this.interrupt();
			return;
		}
		
		this.sender.writeObject(this.index + ":Ships");
		this.sender.writeObject(ships);
	}
	
	// sendCoordinates()
	void sendCoordinates(int row, int column) throws IOException {
		if(this.menuWindow.lostConnection() && this.otherName != "Computer") {
			this.gameWindow.dispose();
			this.menuWindow.setVisible(true);
			JOptionPane.showMessageDialog(this.menuWindow, "You have disconnected!", "Connection Error", JOptionPane.PLAIN_MESSAGE, null);
			this.interrupt();
			return;
		}
		
		this.sender.writeObject(this.index + ":Coordinates:" + row + column);
	}
	
	// sendMessage()
	void sendMessage(String message) throws IOException {
		if(this.menuWindow.lostConnection() && this.otherName != "Computer") {
			this.gameWindow.dispose();
			this.menuWindow.setVisible(true);
			JOptionPane.showMessageDialog(this.menuWindow, "You have disconnected!", "Connection Error", JOptionPane.PLAIN_MESSAGE, null);
			this.interrupt();
			return;
		}
		
		this.sender.writeObject(this.index + ":Message:" + message);
	}
	
	// restartGame()
	void restartGame() throws IOException {
		if(this.menuWindow.lostConnection() && this.otherName != "Computer") {
			this.gameWindow.dispose();
			this.menuWindow.setVisible(true);
			JOptionPane.showMessageDialog(this.menuWindow, "You have disconnected!", "Connection Error", JOptionPane.PLAIN_MESSAGE, null);
			this.interrupt();
			return;
		}
		
		this.sender.writeObject(this.index + ":Restart Game");
	}
	
	// doNotRestartGame()
	void doNotRestartGame() throws IOException {
		if(this.menuWindow.lostConnection() && this.otherName != "Computer") {
			this.gameWindow.dispose();
			this.menuWindow.setVisible(true);
			JOptionPane.showMessageDialog(this.menuWindow, "You have disconnected!", "Connection Error", JOptionPane.PLAIN_MESSAGE, null);
			this.interrupt();
			return;
		}
		
		this.sender.writeObject(this.index + ":Do Not Restart Game");
	}
	
	// disconnectFromGame()
	void disconnectFromGame() throws IOException {
		if(this.menuWindow.lostConnection() && this.otherName != "Computer") {
			this.gameWindow.dispose();
			this.menuWindow.setVisible(true);
			JOptionPane.showMessageDialog(this.menuWindow, "You have disconnected!", "Connection Error", JOptionPane.PLAIN_MESSAGE, null);
			this.interrupt();
			return;
		}
		
		this.sender.writeObject(this.index + ":Disconnect");
	}
	
	// run()
	public void run() {
		try {
			this.index = (int)this.receiver.readObject();
			
			// if there are already two players connected to the server
			if(this.index == -1) {
				this.menuWindow.cannotConnect();
				return;
			}
			
			this.sender.writeObject(this.index + ":Name:" + this.userName);
			
			while(true) {
				String message = (String)this.receiver.readObject();
				String[] components = message.split(":");	
				
				if(this.menuWindow.lostConnection() && this.otherName != "Computer") {
					this.gameWindow.dispose();
					this.menuWindow.setVisible(true);
					JOptionPane.showMessageDialog(this.menuWindow, "You are not connected to the internet!", "Connection Error", JOptionPane.PLAIN_MESSAGE, null);
					this.interrupt();
					return;
				}
				
				if(components[0].equals("Other Name")) {
					this.otherName = components[1];
				}
				
				if(components[0].equals("Show Game Window")) {
					if(this.menuWindow.waitingWindow != null) {
						this.menuWindow.waitingWindow.timer.interrupt();
						this.menuWindow.waitingWindow.setVisible(false);
					}
					
					this.menuWindow.setVisible(false);
					
					if(this.gameWindow != null) {
						this.gameWindow.dispose();
					}
					
					this.gameWindow = new GameWindow(this.userName, this.otherName, null, this, null);
					this.gameWindow.setVisible(true);
				}
				
				if(components[0].equals("Ships")) {
					Ship[][] ships = (Ship[][])this.receiver.readObject();
					this.gameWindow.setShips(ships);
				}
				
				if(components[0].equals("Start Game")) {
					this.gameWindow.startGame();
				}
				
				if(components[0].equals("Coordinates")) {
					int row = Integer.parseInt(components[1].charAt(0) + "");
					int column = -1;
					
					if(components[1].length() == 2) {
						column = Integer.parseInt(components[1].charAt(1) + "");
					} else if(components[1].length() == 3) {
						column = Integer.parseInt(components[1].charAt(1) + "" + components[1].charAt(2) + "");
					}
					
					this.gameWindow.computerAttack(row, column);
				}
				
				if(components[0].equals("Other Disconnected")) {
					this.gameWindow.dispose();
					this.menuWindow.setVisible(true);
					JOptionPane.showMessageDialog(this.menuWindow, "The other player has quit!", "Game Over", JOptionPane.PLAIN_MESSAGE, null);
					return;
				}
				
				if(components[0].equals("Message")) {
					this.gameWindow.receiveMessage(components[1]);
				}
				
				if(components[0].equals("Close Game")) {
					this.gameWindow.dispose();
					this.menuWindow.setVisible(true);
					JOptionPane.showMessageDialog(this.menuWindow, "Both players did not want to have a rematch!", "Game Over", JOptionPane.PLAIN_MESSAGE, null);
				}
			}
		} catch(IOException ioe) {
			System.out.println("IOException in run() in GameClient");
		} catch(ClassNotFoundException cnfe) {
			System.out.println("ClassNotFoundException in run() in GameClient");
		} catch(NullPointerException npe) {
			System.out.println("NullPointerException in run() in GameClient" + npe.getMessage());
		}
	}
}
