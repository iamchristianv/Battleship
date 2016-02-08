import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MenuWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private GameServer server;
	private GameClient client;
	
	private JLabel addressLabel;
	
	private JTextField nameTextField;
	
	private JTextField addressTextField;
	private JTextField portTextField;
	private JTextField mapTextField;

	private JCheckBox hostGameCheckBox;
	private JCheckBox customPortCheckBox;
	private JCheckBox mapCheckBox;

	private JButton refreshButton;
	private JButton connectButton;
	
	WaitingWindow waitingWindow;
	GameWindow gameWindow;
	
	private int number;

	// Constructor
	MenuWindow() {
		super("Battleship - Menu");
		setSize(400, 400);
		setLocation(0, 0);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		initializeComponents();
		createWindow();
		createActions();
	}


	// initializeComponents()
	private void initializeComponents() {
		this.server = null;
		this.client = null;
				
		try {
			InetAddress address = InetAddress.getLocalHost();
			
			if(address.getHostAddress().equals("127.0.0.1")) {
				this.addressLabel = new JLabel("No Internet Connection!");
			} else {
				this.addressLabel = new JLabel("IP Address: " + address.getHostAddress());
			}
		} catch(UnknownHostException uhe) {
			System.out.println("UnknownHostException in initializeComponents() in MenuWindow");
		}
		
		this.nameTextField = new JTextField(15);

		this.addressTextField = new JTextField(7);
		this.portTextField = new JTextField(7);
		this.mapTextField = new JTextField(7);

		this.hostGameCheckBox = new JCheckBox("Host Game");
		this.customPortCheckBox = new JCheckBox("Custom Port");
		this.mapCheckBox = new JCheckBox("201 Map");

		this.refreshButton = new JButton("Refresh");
		this.connectButton = new JButton("Connect");
		
		this.waitingWindow = null;
		this.gameWindow = null;
		
		this.number = 0;

		this.addressLabel.setHorizontalAlignment(JLabel.CENTER);
		this.addressTextField.setText("127.0.0.1");
		this.portTextField.setText("1024");
		this.portTextField.setEnabled(false);
		this.mapTextField.setEnabled(false);
	}

	// createMenu()
	private void createWindow() {
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(6, 0));

		JPanel namePanel = new JPanel();
		namePanel.add(new JLabel("Name:"));
		namePanel.add(this.nameTextField);

		JPanel hostPanel = new JPanel();
		hostPanel.add(this.hostGameCheckBox);
		hostPanel.add(new JLabel("                     "));
		hostPanel.add(new JLabel("IP Address:"));
		hostPanel.add(this.addressTextField);

		JPanel customPanel = new JPanel();
		customPanel.add(this.customPortCheckBox);
		customPanel.add(new JLabel("                "));
		customPanel.add(new JLabel("Port Number:"));
		customPanel.add(this.portTextField);

		JPanel mapsPanel = new JPanel();
		mapsPanel.add(this.mapCheckBox);
		mapsPanel.add(new JLabel("                       "));
		mapsPanel.add(new JLabel("Map Name:"));
		mapsPanel.add(this.mapTextField);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(this.refreshButton);
		buttonPanel.add(new JLabel("  "));
		buttonPanel.add(this.connectButton);

		centerPanel.add(this.addressLabel);
		centerPanel.add(namePanel);
		centerPanel.add(hostPanel);
		centerPanel.add(customPanel);
		centerPanel.add(mapsPanel);
		centerPanel.add(buttonPanel);

		add(centerPanel, BorderLayout.CENTER);
	}

	// createActions()
	private void createActions() {
		this.hostGameCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				MenuWindow.this.addressTextField.setEnabled(!MenuWindow.this.addressTextField.isEnabled());
				
				if(MenuWindow.this.hostGameCheckBox.isSelected() || MenuWindow.this.customPortCheckBox.isSelected()) {
					MenuWindow.this.mapCheckBox.setEnabled(false);
				} else {
					MenuWindow.this.mapCheckBox.setEnabled(true);
				}
			}
		});

		this.customPortCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				MenuWindow.this.portTextField.setEnabled(!MenuWindow.this.portTextField.isEnabled());
				
				if(MenuWindow.this.hostGameCheckBox.isSelected() || MenuWindow.this.customPortCheckBox.isSelected()) {
					MenuWindow.this.mapCheckBox.setEnabled(false);
				} else {
					MenuWindow.this.mapCheckBox.setEnabled(true);
				}			}
		});

		this.mapCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {		
				MenuWindow.this.hostGameCheckBox.setEnabled(!MenuWindow.this.hostGameCheckBox.isEnabled());
				MenuWindow.this.addressTextField.setEnabled(!MenuWindow.this.addressTextField.isEnabled());

				MenuWindow.this.customPortCheckBox.setEnabled(!MenuWindow.this.customPortCheckBox.isEnabled());
				
				MenuWindow.this.mapTextField.setEnabled(!MenuWindow.this.mapTextField.isEnabled());
			}
		});

		this.refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					InetAddress address = InetAddress.getLocalHost();

					if(address.getHostAddress().equals("127.0.0.1")) {
						MenuWindow.this.addressLabel.setText("No Internet Connection!");
					} else {
						MenuWindow.this.addressLabel.setText("IP Address: " + address.getHostAddress());
					}
				} catch(UnknownHostException uhe) {
					System.out.println("UnknownHostException in refreshButton in MenuWindow");
				}
			}
		});

		this.connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {								
				if(MenuWindow.this.nameTextField.getText().equals("")) {
					JOptionPane.showMessageDialog(MenuWindow.this, "Please enter your name in the text field.", "Error", JOptionPane.PLAIN_MESSAGE, null);
					return;
				}
																
				if(!MenuWindow.this.hostGameCheckBox.isSelected() && MenuWindow.this.addressTextField.getText().equals("")) {
					JOptionPane.showMessageDialog(MenuWindow.this, "Please enter an address in the text field.", "Error", JOptionPane.PLAIN_MESSAGE, null);
					return;
				}
								
				if(!MenuWindow.this.mapCheckBox.isSelected() && MenuWindow.this.portTextField.getText().equals("")) {
					JOptionPane.showMessageDialog(MenuWindow.this, "Please enter a port in the text field.", "Error", JOptionPane.PLAIN_MESSAGE, null);
					return;
				}
								
				if(MenuWindow.this.mapCheckBox.isSelected() && MenuWindow.this.mapTextField.getText().equals("")) {
					JOptionPane.showMessageDialog(MenuWindow.this, "Please enter a map in the text field.", "Error", JOptionPane.PLAIN_MESSAGE, null);
					return;
				}
				
				String name = MenuWindow.this.nameTextField.getText();
				String address = MenuWindow.this.addressTextField.getText();
				String port = MenuWindow.this.portTextField.getText();
				String map = MenuWindow.this.mapTextField.getText();
				
				if(MenuWindow.this.mapCheckBox.isSelected()) {
					String mapString = "http://www-scf.usc.edu/~csci201/assignments/" + map + ".battle";
					
					try {
						URL mapURL = new URL(mapString);
						Scanner mapScanner = new Scanner(mapURL.openStream());
						String[] mapLines = new String[10];
						
						for(int i = 0; i < 10; i++) {
							mapLines[i] = mapScanner.nextLine();
						}
						
						MenuWindow.this.setVisible(false);
						MenuWindow.this.gameWindow = new GameWindow(name, "Computer", mapLines, null, MenuWindow.this);
						MenuWindow.this.gameWindow.setVisible(true);

						mapScanner.close();
					} catch (MalformedURLException mue) {
						System.out.println("MalformedURLException in connectButton in MenuWindow");
					} catch (IOException ioe) {
						System.out.println("IOException in connectButton in MenuWindow");
					}
					
					return;
				}
				
				MenuWindow.this.number = (MenuWindow.this.number == 0) ? Integer.parseInt(port) : MenuWindow.this.number + 1;
				
				if(MenuWindow.this.hostGameCheckBox.isSelected()) {
					MenuWindow.this.setVisible(false);
					MenuWindow.this.waitingWindow = new WaitingWindow();
					MenuWindow.this.waitingWindow.setVisible(true);
					MenuWindow.this.server = new GameServer(MenuWindow.this, MenuWindow.this.number);
					MenuWindow.this.server.start();
					MenuWindow.this.client = new GameClient(MenuWindow.this, address, MenuWindow.this.number, name);
					MenuWindow.this.client.start();
				} else {
					MenuWindow.this.client = new GameClient(MenuWindow.this, address, MenuWindow.this.number, name);
					MenuWindow.this.client.start();
				}
			}
		});
	}
	
	// lostConnection()
	boolean lostConnection() {
		try {
			InetAddress address = InetAddress.getLocalHost();

			if(address.getHostAddress().equals("127.0.0.1")) {
				return true;
			}			
		} catch(UnknownHostException uhe) {
			System.out.println("UnknownHostException in refreshButton in MenuWindow");
		}
		
		return false;
	}
	
	//// cannotConnect()
	void cannotConnect() {
		if(this.waitingWindow != null) {
			this.waitingWindow.timer.interrupt();
			this.waitingWindow.setVisible(false);
		}
				
		this.setVisible(true);
		JOptionPane.showMessageDialog(MenuWindow.this, "Unable to connect to Server.", "Connection Error", JOptionPane.PLAIN_MESSAGE, null);
	}

	class WaitingWindow extends JFrame {
		private static final long serialVersionUID = 1L;
		
		JLabel waitingLabel;
		JLabel countingLabel;
		
		Timer timer;

		//// WaitingWindow()
		WaitingWindow() {
			super("Battleship - Connecting");
			setSize(400, 400);
			setLocation(0, 0);

			JPanel centerPanel = new JPanel();
			centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
			this.waitingLabel = new JLabel("Waiting for another client to connect...");
			this.waitingLabel.setAlignmentX(CENTER_ALIGNMENT);
			this.countingLabel = new JLabel("30 seconds until this server times out");
			this.countingLabel.setAlignmentX(CENTER_ALIGNMENT);
			centerPanel.add(new JLabel(" "));
			centerPanel.add(new JLabel(" "));
			centerPanel.add(new JLabel(" "));
			centerPanel.add(this.waitingLabel);
			centerPanel.add(new JLabel(" "));
			centerPanel.add(this.countingLabel);		
			add(centerPanel, BorderLayout.CENTER);

			this.timer = new Timer();
			this.timer.start();	
		}
		
		//// Timer
		class Timer extends Thread {
			private int seconds;

			//// run()
			public void run() {			
				for(this.seconds = 30; this.seconds > 0; this.seconds--) {
					WaitingWindow.this.countingLabel.setText(this.seconds + " seconds until server times out");

					try {
						Thread.sleep(1000);
					} catch(InterruptedException ie) {
						System.out.println("InterruptedException in run() in Timer in WaitingWindow in MenuWindow");
						return;
					}
				}
				
				MenuWindow.this.setVisible(true);
				MenuWindow.this.cannotConnect();
			}
		}
	}
}
